package be.noki_senpai.NKeconomy.managers;

import be.noki_senpai.NKeconomy.NKeconomy;
import be.noki_senpai.NKeconomy.utils.SQLConnect;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class DatabaseManager
{
	private static Connection bdd = null;
	public static Map<String, String> table = new HashMap<>();

	private ConsoleCommandSender console = null;
	private ConfigManager configManager = null;

	public DatabaseManager(ConfigManager configManager)
	{
		this.console = Bukkit.getConsoleSender();
		this.configManager = configManager;
	}

	public boolean loadDatabase()
	{
		// Save table name
		table.put("accounts", ConfigManager.PREFIX + "accounts");
		table.put("cross_server", "NK_cross_server");

		// Setting database informations
		SQLConnect.setInfo(configManager.getDbHost(), configManager.getDbPort(), configManager.getDbName(), configManager.getDbUser(), configManager.getDbPassword());

		// Try to connect to database
		try
		{
			bdd = SQLConnect.getHikariDS().getConnection();
		}
		catch(SQLException e)
		{
			bdd = null;
			console.sendMessage(ChatColor.DARK_RED + NKeconomy.PNAME
					+ " Error while attempting database connexion. Verify your access informations in config.yml");
			e.printStackTrace();
			return false;
		}

		try
		{
			// Check if tables already exist on database
			if(!existTables())
			{
				// Create database structure if not exist
				createTable();
			}

		}
		catch(SQLException e)
		{
			console.sendMessage(ChatColor.DARK_RED + NKeconomy.PNAME + " Error while creating database structure. (Error#A.2.002)");
			return false;
		}

		return true;
	}

	public void unloadDatabase()
	{
		if(bdd != null)
		{
			try
			{
				bdd.close();
			}
			catch(SQLException e)
			{
				e.printStackTrace();
			}
		}
	}

	private boolean existTables() throws SQLException
	{
		// Select all tables beginning with the prefix
		String req = "SHOW TABLES FROM " + configManager.getDbName() + " LIKE '" + ConfigManager.PREFIX + "%'";
		ResultSet resultat = null;
		PreparedStatement ps = null;

		try
		{
			ps = bdd.prepareStatement(req);
			resultat = ps.executeQuery();
			int count = 0;
			while (resultat.next())
			{
				count++;
			}

			// if all tables are missing
			if (count == 0)
			{
				console.sendMessage(ChatColor.GREEN + NKeconomy.PNAME + " Missing table(s). First start.");
				return false;
			}
			resultat.close();
			ps.close();

			req = "SHOW TABLES FROM " + configManager.getDbName() + " LIKE 'NK_cross_server'";
			ps = bdd.prepareStatement(req);
			resultat = ps.executeQuery();
			if (resultat.next())
			{
				count++;
			}

			// if 1 or more tables are missing
			if (count < table.size())
			{
				console.sendMessage(ChatColor.DARK_RED + NKeconomy.PNAME + " Missing table(s). Please don't alter tables name or structure in database. (Error#main.Storage.002)");
				return false;
			}
		}
		catch (SQLException e1)
		{
			console.sendMessage(ChatColor.DARK_RED + NKeconomy.PNAME + " Error while testing existance of tables. (Error#main.Storage.003)");
			NKeconomy.getPlugin().disablePlugin();
		}
		finally
		{
			if (ps != null)
			{
				ps.close();
			}
			if (resultat != null)
			{
				resultat.close();
			}
		}

		return true;
	}

	private void createTable() throws SQLException
	{
		try
		{
			bdd = getConnection();

			String req = null;
			Statement s = null;

			console.sendMessage(ChatColor.GREEN + NKeconomy.PNAME + " Creating Database structure ...");

			try
			{
				// Creating accounts table
				req = "CREATE TABLE IF NOT EXISTS `" + table.get("accounts") + "` (" + "`id` int(11) NOT NULL AUTO_INCREMENT," + "`uuid` varchar(40) NOT NULL," + "`name` varchar(40) NOT NULL," + "`amount` double NOT NULL," + "PRIMARY KEY (`id`)," + "UNIQUE KEY `uuid_unique` (`uuid`) USING BTREE" + ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
				s = bdd.createStatement();
				s.execute(req);
				s.close();

				// Creating cross_server table
				req = "CREATE TABLE IF NOT EXISTS `" + table.get("cross_server") + "` (" + "`id` int(11) NOT NULL AUTO_INCREMENT," + "`name` varchar(40) NOT NULL," + "`server` varchar(120) NOT NULL," + "PRIMARY KEY (`id`)" + ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
				s = bdd.createStatement();
				s.execute(req);
				s.close();

				console.sendMessage(ChatColor.GREEN + NKeconomy.PNAME + " Database structure created.");
			}
			catch(SQLException e)
			{
				console.sendMessage(ChatColor.DARK_RED + NKeconomy.PNAME + " Error while creating database structure. (Error#main.Storage.000)");
				e.printStackTrace();
			}
			finally
			{
				if(s != null)
				{
					s.close();
				}
			}
		}
		catch(SQLException e)
		{
			console.sendMessage(ChatColor.DARK_RED + NKeconomy.PNAME + " Error while creating database structure. (Error#main.Storage.001)");
		}
	}

	// Getter 'bdd'
	public static Connection getConnection()
	{
		try
		{
			if(!bdd.isValid(1))
			{
				if(!bdd.isClosed())
				{
					bdd.close();
				}
				bdd = SQLConnect.getHikariDS().getConnection();
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		return bdd;
	}
}

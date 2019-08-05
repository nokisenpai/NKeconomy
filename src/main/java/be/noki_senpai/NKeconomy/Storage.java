package be.noki_senpai.NKeconomy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;

public class Storage 
{
	static Connection bdd = null;
	private static Map<String, String> table = new HashMap<>();
	public static ConsoleCommandSender console;
	static String PName;
	static String dbName;
	static String prefix;
	
	static void createTable(String dbName_, String prefix_, Map<String, String> table_) throws SQLException
	{
		table = table_;
		console = NKeconomy.getInstance().getConsole();
		PName = NKeconomy.PName;
		dbName = dbName_;
		prefix = prefix_;
		
		try 
		{
			bdd = NKeconomy.getInstance().getConnection();
			
			if(!existTable())
			{
				String req;
				Statement s = null;
				
				console.sendMessage(ChatColor.GREEN + PName + " Creating Database structure ...");
								
				try
				{
			        // Creating accounts table
			        req = "CREATE TABLE IF NOT EXISTS `" + table.get("accounts") + "` (" +
						 "`id` int(11) NOT NULL AUTO_INCREMENT," +
						 "`uuid` varchar(40) NOT NULL," +
						 "`name` varchar(40) NOT NULL," +
						 "`amount` double NOT NULL," +
						 "PRIMARY KEY (`id`)," +
						 "UNIQUE KEY `uuid_unique` (`uuid`) USING BTREE" +
						") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
					s = bdd.createStatement();
			        s.execute(req);
			        s.close();
			        
			        
			        // Creating cross_server table
			        req = "CREATE TABLE IF NOT EXISTS `" + table.get("cross_server") + "` (" +
						 "`id` int(11) NOT NULL AUTO_INCREMENT," +
						 "`name` varchar(40) NOT NULL," +
						 "`server` varchar(120) NOT NULL," +
						 "PRIMARY KEY (`id`)" +
						") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
					s = bdd.createStatement();
			        s.execute(req);
			        s.close();
					
					console.sendMessage(ChatColor.GREEN + PName + " Database structure created.");
				}
				catch (SQLException e)
				{
					console.sendMessage(ChatColor.DARK_RED + PName + " Error while creating database structure. (Error#main.Storage.000)");
					NKeconomy.getInstance().disablePlugin();
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
		}
		catch (SQLException e)
		{
			console.sendMessage(ChatColor.DARK_RED + PName + " Error while creating database structure. (Error#main.Storage.001)");
			NKeconomy.getInstance().disablePlugin();
		}

		table = null;
		console = null;
		PName = null;
		dbName = null;
		prefix = null;
	}
	
	private static boolean existTable() throws SQLException
	{
		// Select all tables beginning with the prefix
		String req = "SHOW TABLES FROM " + dbName + " LIKE '"+ prefix + "%'";
		ResultSet resultat = null;
		PreparedStatement ps = null;
		
		try 
		{
			ps = bdd.prepareStatement(req);
	        resultat = ps.executeQuery();
	        int count = 0;
	        while(resultat.next()) 
	        {
	        	count++;
	        }
	        
	        //if all tables are missing
	        if(count == 0)
        	{
        		console.sendMessage(ChatColor.GREEN + PName + " Missing table(s). First start.");
        		return false;
        	}
	        resultat.close();
	        ps.close();
	        
	        req = "SHOW TABLES FROM " + dbName + " LIKE 'NK_cross_server'";
	        ps = bdd.prepareStatement(req);
	        resultat = ps.executeQuery();
	        if(resultat.next())
	        {
	        	count++;
	        }
	        
	        //if 1 or more tables are missing
	        if(count < table.size())
        	{
        		console.sendMessage(ChatColor.DARK_RED + PName + " Missing table(s). Please don't alter tables name or structure in database. (Error#main.Storage.002)");
        		return false;
        	}
		} 
		catch (SQLException e1) 
		{
			console.sendMessage(ChatColor.DARK_RED + PName + " Error while testing existance of tables. (Error#main.Storage.003)");
			NKeconomy.getInstance().disablePlugin();
		}
		finally
		{
		    if(ps != null)
		    {
		    	ps.close();
		    }
		    if(resultat != null)
		    {
		    	resultat.close();
		    }
		}
		
		return true;
	}
}

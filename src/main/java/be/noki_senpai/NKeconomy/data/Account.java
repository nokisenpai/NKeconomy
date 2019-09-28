package be.noki_senpai.NKeconomy.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
import java.util.function.Function;

import be.noki_senpai.NKeconomy.managers.ConfigManager;
import be.noki_senpai.NKeconomy.managers.DatabaseManager;
import be.noki_senpai.NKeconomy.managers.QueueManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import be.noki_senpai.NKeconomy.NKeconomy;

public class Account
{
	private int id;
	private UUID playerUUID;
	private String playerName;
	private int accountId;
	private Double amount = 0.0;

	public Account(UUID UUID)
	{
		setPlayerUUID(UUID);
		setPlayerName(Bukkit.getOfflinePlayer(playerUUID).getName());

		Connection bdd = null;
		ResultSet resultat = null;
		PreparedStatement ps = null;
		String req = null;

		try
		{
			bdd = DatabaseManager.getConnection();

			// Get 'id', 'uuid', 'name' from database
			req = "SELECT p.id, uuid, name, a.id AS account_id, amount FROM " + DatabaseManager.table.PLAYERS + " p LEFT JOIN " + DatabaseManager.table.ACCOUNTS + " a ON p.id = a.player_id WHERE uuid = ?";
			ps = bdd.prepareStatement(req);
			ps.setString(1, getPlayerUUID().toString());

			resultat = ps.executeQuery();

			// If there is a result account exist
			if(resultat.next())
			{
				setId(resultat.getInt("id"));
				String tmpName = resultat.getString("name");
				if(resultat.getDouble("account_id") == 0)
				{
					ps.close();
					resultat.close();

					//Add new account on database
					req = "INSERT INTO " + DatabaseManager.table.ACCOUNTS + " ( player_id, amount ) VALUES ( ? , ? )";
					ps = bdd.prepareStatement(req, Statement.RETURN_GENERATED_KEYS);
					ps.setDouble(1, getId());
					ps.setDouble(2, 10000);

					ps.executeUpdate();
					resultat = ps.getGeneratedKeys();

					resultat.next();
					setAccountId(resultat.getInt(1));

					setAmount((double) 10000);
				}
				else
				{
					setAccountId(resultat.getInt("account_id"));
					setAmount(resultat.getDouble("amount"));
				}

				ps.close();
				resultat.close();

				if(NKeconomy.managePlayerDb)
				{
					// If names are differents, update in database
					if(!tmpName.equals(getPlayerName()))
					{
						req = "UPDATE " + DatabaseManager.table.ACCOUNTS + " SET name = ? WHERE id = ?";
						ps = bdd.prepareStatement(req);
						ps.setString(1, getPlayerName());
						ps.setInt(2, getId());

						ps.executeUpdate();

						ps.close();
						resultat.close();
					}
				}
			}
			else
			{
				//Add new player on database
				req = "INSERT INTO " + DatabaseManager.table.PLAYERS + " ( uuid, name) VALUES ( ? , ? ) ON DUPLICATE KEY UPDATE id = id";
				ps = bdd.prepareStatement(req, Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, getPlayerUUID().toString());
				ps.setString(2, getPlayerName());
				ps.executeUpdate();
				resultat = ps.getGeneratedKeys();

				resultat.next();
				setId(resultat.getInt(1));

				ps.close();
				resultat.close();

				//Add new account on database
				//Add new account on database
				req = "INSERT INTO " + DatabaseManager.table.ACCOUNTS + " ( player_id, amount ) VALUES ( ? , ? )";
				ps = bdd.prepareStatement(req, Statement.RETURN_GENERATED_KEYS);
				ps.setDouble(1, getId());
				ps.setDouble(2, ConfigManager.STARTAMOUNT);

				ps.executeUpdate();
				resultat = ps.getGeneratedKeys();

				resultat.next();
				setAccountId(resultat.getInt(1));

				setAmount(ConfigManager.STARTAMOUNT);

				ps.close();
				resultat.close();
			}
		}
		catch(SQLException e)
		{
			Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + NKeconomy.PNAME + " Error while setting a player. (Error#data.Players.000)");
			e.printStackTrace();
		}
	}

	// ######################################
	// Getters & Setters
	// ######################################

	// Getter & Setter 'id'
	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	// Getter & Setter 'playerUUID'
	public UUID getPlayerUUID()
	{
		return playerUUID;
	}

	public void setPlayerUUID(UUID playerUUID)
	{
		this.playerUUID = playerUUID;
	}

	// Getter & Setter 'playerName'
	public String getPlayerName()
	{
		return playerName;
	}

	public void setPlayerName(String playerName)
	{
		this.playerName = playerName;
	}

	// Getter & Setter 'accountId'
	public int getAccountId()
	{
		return accountId;
	}

	public void setAccountId(int accountId)
	{
		this.accountId = accountId;
	}

	// Getter & Setter 'amount'
	public double getAmount()
	{
		return amount;
	}

	public void setAmount(Double amount)
	{
		this.amount = amount;
	}

	// ######################################
	// Add & Remove amount
	// ######################################

	public void addAmount(Double amount)
	{
		this.amount += amount;
	}

	public void removeAmount(Double amount)
	{
		this.amount -= amount;
	}

	// ######################################
	// Save amount
	// ######################################

	public void save(QueueManager queueManager)
	{
		Connection bdd = null;
		PreparedStatement ps = null;
		String req = null;

		try
		{
			bdd = DatabaseManager.getConnection();

			req = "UPDATE " + DatabaseManager.table.ACCOUNTS + " SET amount = ? WHERE id = ?";
			ps = bdd.prepareStatement(req);
			ps.setDouble(1, getAmount());
			ps.setInt(2, accountId);

			ps.executeUpdate();
			ps.close();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}
}

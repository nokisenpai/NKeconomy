package be.noki_senpai.NKeconomy.managers;

import be.noki_senpai.NKeconomy.NKeconomy;
import be.noki_senpai.NKeconomy.data.Account;
import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;

public class AccountManager
{
	// Players datas
	public static Map<String, Account> accounts = null;
	private ConsoleCommandSender console = null;
	private QueueManager queueManager = null;
	String tmp1 = "";
	String tmp2 = "";

	public AccountManager(QueueManager queueManager)
	{
		this.accounts = new TreeMap<String, Account>(String.CASE_INSENSITIVE_ORDER);
		this.console = Bukkit.getConsoleSender();
		this.queueManager = queueManager;

		new BukkitRunnable()
		{
			@Override public void run()
			{
				saveAll();
			}
		}.runTaskTimerAsynchronously(NKeconomy.getPlugin(), 0, 300 * 20);

		// Purge cross_server for this server
		this.queueManager.addToQueue(new Function()
		{
			@Override public Object apply(Object o)
			{
				purgeCrossServer();
				return null;
			}
		});

	}

	public void loadAccount()
	{
		// Get all connected players
		Bukkit.getOnlinePlayers().forEach(player -> accounts.put(player.getDisplayName(), new Account(player.getUniqueId())));
	}

	public void unloadAccout()
	{
		saveAll();
		accounts.clear();
	}

	// ######################################
	// Getters & Setters
	// ######################################

	// getPlayer
	public Account getAccount(String playerName)
	{
		return accounts.get(playerName);
	}

	public void addAccount(Player player)
	{
		accounts.put(player.getName(), new Account(player.getUniqueId()));
	}

	public void delAccount(String playerName)
	{
		accounts.remove(playerName);
	}

	// **************************************
	// **************************************
	// Accounts functions
	// **************************************
	// **************************************

	// ######################################
	// Save all players amounts
	// ######################################

	public void saveAll()
	{
		if(accounts.size() > 0)
		{
			Connection bdd = null;
			PreparedStatement ps = null;
			String req = null;
			tmp1 = "";
			tmp2 = "(";

			accounts.forEach((key, value) -> {
				tmp1 += "WHEN " + value.getId() + " THEN " + value.getAmount() + " ";
				tmp2 += value.getId() + ",";
			});

			tmp2 += "-1)";

			try
			{
				bdd = DatabaseManager.getConnection();
				req = "UPDATE " + DatabaseManager.table.get("accounts") + " SET amount = CASE id " + tmp1 + "ELSE amount END WHERE id IN" + tmp2;
				ps = bdd.prepareStatement(req);
				ps.executeUpdate();
				ps.close();
			}
			catch(SQLException e)
			{
				e.printStackTrace();
			}
		}
	}

	// ######################################
	// Get amount
	// ######################################
	private double getOnlineBalance(String playerName)
	{
		return accounts.get(playerName).getAmount();
	}

	private double getOfflineBalance(String playerName)
	{
		Connection bdd = null;
		PreparedStatement ps = null;
		ResultSet resultat = null;
		String req = null;

		try
		{
			bdd = DatabaseManager.getConnection();

			req = "SELECT amount FROM " + DatabaseManager.table.get("accounts") + " WHERE name = ?";
			ps = bdd.prepareStatement(req);
			ps.setString(1, playerName);

			resultat = ps.executeQuery();
			if(resultat.next())
			{
				double amount = resultat.getDouble("amount");
				ps.close();
				resultat.close();
				return amount;
			}
			ps.close();
			resultat.close();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}

		return 0;
	}

	public double getBalance(String playerName)
	{
		if(accounts.containsKey(playerName))
		{
			return getOnlineBalance(playerName);
		}
		else
		{
			return getOfflineBalance(playerName);
		}
	}

	// ######################################
	// Has amount
	// ######################################
	private boolean hasOfflineAccount(String playerName)
	{
		Connection bdd = null;
		PreparedStatement ps = null;
		ResultSet resultat = null;
		String req = null;

		try
		{
			bdd = DatabaseManager.getConnection();

			req = "SELECT COUNT(id) FROM " + DatabaseManager.table.get("accounts") + " WHERE name = ?";
			ps = bdd.prepareStatement(req);
			ps.setString(1, playerName);

			resultat = ps.executeQuery();
			resultat.next();

			if(resultat.getInt(1) == 1)
			{
				ps.close();
				resultat.close();
				return true;
			}
			ps.close();
			resultat.close();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		return false;
	}

	public boolean hasAccount(String playerName)
	{
		if(accounts.containsKey(playerName))
		{
			return true;
		}
		else
		{
			return hasOfflineAccount(playerName);
		}
	}

	// ######################################
	// Pay amount
	// ######################################
	private void payOnlineAmount(String playerName, double amount, final String sender)
	{
		accounts.get(playerName).addAmount(amount);
		if(Bukkit.getPlayer(playerName) != null)
		{
			Bukkit.getPlayer(playerName).sendMessage(
					ChatColor.AQUA + sender + ChatColor.GREEN + " vous a donné " + format(amount) + " " + ConfigManager.CURRENCY);
		}
	}

	private void payOfflineAmount(String playerName, double amount)
	{
		Connection bdd = null;
		PreparedStatement ps = null;
		String req = null;

		try
		{
			bdd = DatabaseManager.getConnection();

			req = "UPDATE " + DatabaseManager.table.get("accounts") + " SET amount = amount + ? WHERE name = ?";
			ps = bdd.prepareStatement(req);
			ps.setDouble(1, amount);
			ps.setString(2, playerName);

			ps.executeUpdate();
			ps.close();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}

	private void payCrossServer(String playerName, double amount_, final String senderName)
	{
		final double amount = round(amount_);
		String server = getOtherServer(playerName);
		if(server != null)
		{
			ByteArrayDataOutput out = ByteStreams.newDataOutput();
			out.writeUTF("Forward"); // So BungeeCord knows to forward it
			out.writeUTF(server.toUpperCase());
			out.writeUTF("NKeconomy");

			ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
			DataOutputStream msgout = new DataOutputStream(msgbytes);
			try
			{
				msgout.writeUTF("pay|" + playerName + "|" + amount + "|" + senderName);
			}
			catch(IOException exception)
			{
				exception.printStackTrace();
			}

			out.writeShort(msgbytes.toByteArray().length);
			out.write(msgbytes.toByteArray());

			Player player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);

			player.sendPluginMessage(NKeconomy.getPlugin(), "BungeeCord", out.toByteArray());
		}
	}

	public void payAmount(String playerName, double amount_, final String senderName, boolean crossServer)
	{
		final double amount = round(amount_);
		if(accounts.containsKey(playerName))
		{
			payOnlineAmount(playerName, amount, senderName);
		}
		else if(!crossServer)
		{
			payCrossServer(playerName, amount_, senderName);
		}
		if(!crossServer)
		{
			payOfflineAmount(playerName, amount);
		}
	}

	// ######################################
	// Give amount
	// ######################################
	private void giveOnlineAmount(final String playerName, double amount, boolean crossServer)
	{
		accounts.get(playerName).addAmount(amount);
		if(crossServer && Bukkit.getPlayer(playerName) != null)
		{
			Bukkit.getPlayer(playerName).sendMessage(ChatColor.GREEN + "Vous avez reçu " + format(amount) + " " + ConfigManager.CURRENCY);
		}
	}

	private void giveOfflineAmount(final String playerName, double amount)
	{
		Connection bdd = null;
		PreparedStatement ps = null;
		String req = null;

		try
		{
			bdd = DatabaseManager.getConnection();

			req = "UPDATE " + DatabaseManager.table.get("accounts") + " SET amount = amount + ? WHERE name = ?";
			ps = bdd.prepareStatement(req);
			ps.setDouble(1, amount);
			ps.setString(2, playerName);

			ps.executeUpdate();
			ps.close();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}

	private void giveCrossServer(final String playerName, double amount)
	{
		String server = getOtherServer(playerName);
		if(server != null)
		{
			if(accounts.size() != 0)
			{
				ByteArrayDataOutput out = ByteStreams.newDataOutput();
				out.writeUTF("Forward"); // So BungeeCord knows to forward it
				out.writeUTF(server.toUpperCase());
				out.writeUTF("NKeconomy");

				ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
				DataOutputStream msgout = new DataOutputStream(msgbytes);
				try
				{
					msgout.writeUTF("give|" + playerName + "|" + amount + "|null");
				}
				catch(IOException exception)
				{
					exception.printStackTrace();
				}

				out.writeShort(msgbytes.toByteArray().length);
				out.write(msgbytes.toByteArray());

				Player player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);

				player.sendPluginMessage(NKeconomy.getPlugin(), "BungeeCord", out.toByteArray());
			}
			else
			{
				Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + NKeconomy.PNAME + " " + playerName
						+ " est connecté(e) sur un autre serveur. Utilisez la console de ce serveur pour lui donner des " + ConfigManager.CURRENCY);
			}
		}
	}

	public void giveAmount(final String playerName, double amount_, boolean crossServer)
	{
		final double amount = round(amount_);
		if(accounts.containsKey(playerName))
		{
			giveOnlineAmount(playerName, amount, crossServer);
		}
		else if(!crossServer)
		{
			giveCrossServer(playerName, amount);
		}
		if(!crossServer)
		{
			giveOfflineAmount(playerName, amount);
		}
	}

	// ######################################
	// Take amount
	// ######################################
	private void takeOnlineAmount(final String playerName, double amount, boolean crossServer)
	{
		accounts.get(playerName).removeAmount(amount);
		if(crossServer && Bukkit.getPlayer(playerName) != null)
		{
			Bukkit.getPlayer(playerName).sendMessage(ChatColor.GREEN + "Vous avez perdu " + format(amount) + " " + ConfigManager.CURRENCY);
		}
	}

	private void takeOfflineAmount(final String playerName, double amount)
	{
		Connection bdd = null;
		PreparedStatement ps = null;
		String req = null;

		try
		{
			bdd = DatabaseManager.getConnection();

			req = "UPDATE " + DatabaseManager.table.get("accounts") + " SET amount = amount - ? WHERE name = ?";
			ps = bdd.prepareStatement(req);
			ps.setDouble(1, amount);
			ps.setString(2, playerName);

			ps.executeUpdate();
			ps.close();

		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}

	private boolean takeCrossServer(final String playerName, double amount)
	{
		String server = getOtherServer(playerName);
		if(server != null)
		{
			if(accounts.size() != 0)
			{
				ByteArrayDataOutput out = ByteStreams.newDataOutput();
				out.writeUTF("Forward"); // So BungeeCord knows to forward it
				out.writeUTF(server.toUpperCase());
				out.writeUTF("NKeconomy");

				ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
				DataOutputStream msgout = new DataOutputStream(msgbytes);
				try
				{
					msgout.writeUTF("take|" + playerName + "|" + amount + "|null"); // You can do
				}
				catch(IOException exception)
				{
					exception.printStackTrace();
				}

				out.writeShort(msgbytes.toByteArray().length);
				out.write(msgbytes.toByteArray());

				Player player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);

				player.sendPluginMessage(NKeconomy.getPlugin(), "BungeeCord", out.toByteArray());

				return true;
			}
			else
			{
				Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + NKeconomy.PNAME + " " + playerName
						+ " est connecté(e) sur un autre serveur. Utilisez la console de ce serveur pour lui retirer des " + ConfigManager.CURRENCY);
				return false;
			}
		}
		return true;
	}

	public boolean takeAmount(final String playerName, double amount_, boolean crossServer)
	{
		final double amount = round(amount_);
		if(getBalance(playerName) >= amount)
		{
			if(accounts.containsKey(playerName))
			{
				takeOnlineAmount(playerName, amount, crossServer);
			}
			else if(!crossServer)
			{
				if(!takeCrossServer(playerName, amount))
				{
					return false;
				}
			}
			if(!crossServer)
			{
				takeOfflineAmount(playerName, amount);
			}

			return true;
		}
		return false;
	}

	// ######################################
	// Set amount
	// ######################################
	private void setOnlineAmount(final String playerName, double amount, boolean crossServer)
	{
		accounts.get(playerName).setAmount(amount);
		if(crossServer && Bukkit.getPlayer(playerName) != null)
		{
			Bukkit.getPlayer(playerName).sendMessage(ChatColor.GREEN + "Vous avez maintenant " + format(amount) + " " + ConfigManager.CURRENCY);
		}
	}

	private void setOfflineAmount(final String playerName, double amount)
	{
		Connection bdd = null;
		PreparedStatement ps = null;
		String req = null;

		try
		{
			bdd = DatabaseManager.getConnection();

			req = "UPDATE " + DatabaseManager.table.get("accounts") + " SET amount = ? WHERE name = ?";
			ps = bdd.prepareStatement(req);
			ps.setDouble(1, amount);
			ps.setString(2, playerName);

			ps.executeUpdate();
			ps.close();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}

	private void setCrossServer(final String playerName, double amount)
	{
		String server = getOtherServer(playerName);
		if(server != null)
		{
			if(accounts.size() != 0)
			{
				ByteArrayDataOutput out = ByteStreams.newDataOutput();
				out.writeUTF("Forward"); // So BungeeCord knows to forward it
				out.writeUTF(server.toUpperCase());
				out.writeUTF("NKeconomy");

				ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
				DataOutputStream msgout = new DataOutputStream(msgbytes);
				try
				{
					msgout.writeUTF("set|" + playerName + "|" + amount + "|null"); // You can do anything
					// you want with msgout
				}
				catch(IOException exception)
				{
					exception.printStackTrace();
				}

				out.writeShort(msgbytes.toByteArray().length);
				out.write(msgbytes.toByteArray());

				Player player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);

				player.sendPluginMessage(NKeconomy.getPlugin(), "BungeeCord", out.toByteArray());
			}
			else
			{
				Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + NKeconomy.PNAME + " " + playerName
						+ " est connecté(e) sur un autre serveur. Utilisez la console de ce serveur pour lui définir son nombre de "
						+ ConfigManager.CURRENCY);
			}
		}
	}

	public void setAmount(final String playerName, double amount_, boolean crossServer)
	{
		final double amount = round(amount_);
		if(accounts.containsKey(playerName))
		{
			setOnlineAmount(playerName, amount, crossServer);
		}
		else if(!crossServer)
		{
			setCrossServer(playerName, amount);
		}
		if(!crossServer)
		{
			setOfflineAmount(playerName, amount);
		}
	}

	// ######################################
	// Get top amount
	// ######################################

	public Map<String, Double> topAmount(int page)
	{
		if(page >= 1)
		{
			Map<String, Double> topAmount = new LinkedHashMap<String, Double>();

			Connection bdd = null;
			PreparedStatement ps = null;
			ResultSet resultat = null;
			String req = null;

			try
			{
				bdd = DatabaseManager.getConnection();

				req = "SELECT name, amount FROM " + DatabaseManager.table.get("accounts") + " ORDER BY amount DESC LIMIT ?, 10";
				ps = bdd.prepareStatement(req);
				ps.setInt(1, (page - 1) * 10);
				resultat = ps.executeQuery();

				while(resultat.next())
				{
					topAmount.put(resultat.getString("name"), resultat.getDouble("amount"));
				}

				ps.close();
				resultat.close();
				return topAmount;
			}
			catch(SQLException e)
			{
				e.printStackTrace();
			}
		}
		return null;
	}

	// ######################################
	// Utils functions
	// ######################################

	// Format amount
	public String format(double amount)
	{
		String pattern = "";
		if(amount % 1 == 0)
		{
			pattern = "###,###,###,##0";
		}
		else
		{
			pattern = "###,###,###,##0.00";
		}

		DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols(Locale.FRENCH);
		formatSymbols.setDecimalSeparator(',');
		formatSymbols.setGroupingSeparator(' ');

		DecimalFormat decimalFormat = new DecimalFormat(pattern, formatSymbols);

		return decimalFormat.format(amount);
	}

	// Round amount
	public double round(double value)
	{
		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(5, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}

	// Purge cross server
	public void purgeCrossServer()
	{
		Connection bdd = null;
		PreparedStatement ps = null;
		String req = null;

		try
		{
			bdd = DatabaseManager.getConnection();

			req = "DELETE FROM " + DatabaseManager.table.get("cross_server") + " WHERE server = ?";
			ps = bdd.prepareStatement(req);
			ps.setString(1, ConfigManager.SERVERNAME);

			ps.execute();
			ps.close();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}

	// Check if a player is connected in other server
	public String getOtherServer(String playername)
	{
		Connection bdd = null;
		PreparedStatement ps = null;
		ResultSet resultat = null;
		String req = null;

		try
		{
			bdd = DatabaseManager.getConnection();

			req = "SELECT server FROM " + DatabaseManager.table.get("cross_server") + " WHERE name = ?";
			ps = bdd.prepareStatement(req);
			ps.setString(1, playername);

			resultat = ps.executeQuery();

			if(resultat.next())
			{
				String server = resultat.getString("server");

				resultat.close();
				ps.close();

				return server;
			}
			resultat.close();
			ps.close();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}

		return null;
	}

	// Add a player entry on sql DB with his server
	public void addOtherServer(String playername)
	{
		Connection bdd = null;
		PreparedStatement ps = null;
		String req = null;

		try
		{
			bdd = DatabaseManager.getConnection();

			req = "INSERT INTO " + DatabaseManager.table.get("cross_server") + " ( name, server ) VALUES ( ? , ? )";
			ps = bdd.prepareStatement(req);
			ps.setString(1, playername);
			ps.setString(2, ConfigManager.SERVERNAME);

			ps.execute();
			ps.close();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}

	// Remove a player entry on sql DB with his server
	public void removeOtherServer(String playername)
	{
		Connection bdd = null;
		PreparedStatement ps = null;
		String req = null;

		try
		{
			bdd = DatabaseManager.getConnection();

			req = "DELETE FROM " + DatabaseManager.table.get("cross_server") + " WHERE name = ? AND server = ?";
			ps = bdd.prepareStatement(req);
			ps.setString(1, playername);
			ps.setString(2, ConfigManager.SERVERNAME);

			ps.execute();
			ps.close();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}
}

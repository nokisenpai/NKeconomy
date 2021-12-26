package ovh.lumen.NKeconomy.managers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import ovh.lumen.NKeconomy.data.Account;
import ovh.lumen.NKeconomy.data.NKData;
import ovh.lumen.NKeconomy.enums.Messages;
import ovh.lumen.NKeconomy.utils.MessageParser;

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

public final class AccountManager
{
	private static int saveDataTaskId1 = -1;

	private AccountManager() {}

	public static void load()
	{
		Bukkit.getOnlinePlayers().forEach(player -> NKData.ACCOUNTS.put(player.getName(), new Account(player.getUniqueId())));

		saveDataTaskId1 = new BukkitRunnable()
		{
			@Override
			public void run()
			{
				saveAll();
			}
		}.runTaskTimerAsynchronously((Plugin) NKData.PLUGIN, 0, 300 * 20).getTaskId();
	}

	public static void unload()
	{
		Bukkit.getScheduler().cancelTask(saveDataTaskId1);
		saveAll();
		NKData.ACCOUNTS.clear();
	}

	public static Account getAccount(String playerName)
	{
		return NKData.ACCOUNTS.get(playerName);
	}

	public static void addAccount(Player player)
	{
		NKData.ACCOUNTS.put(player.getName(), new Account(player.getUniqueId()));
	}

	public static void removeAccount(String playerName)
	{
		NKData.ACCOUNTS.remove(playerName);
	}

	public static void saveAll()
	{
		if(NKData.ACCOUNTS.size() > 0)
		{
			try
			{
				Connection bdd = DatabaseManager.getConnection();
				String req = "UPDATE " + DatabaseManager.Tables.ACCOUNTS + " SET amount = ? WHERE player_uuid = ?";
				PreparedStatement ps = bdd.prepareStatement(req);

				for(Map.Entry<String, Account> account : NKData.ACCOUNTS.entrySet())
				{
					ps.setDouble(1, account.getValue().getAmount());
					ps.setString(2, account.getValue().getUuid().toString());
					ps.addBatch();
				}

				ps.executeBatch();
				ps.close();
			}
			catch(SQLException e)
			{
				e.printStackTrace();
			}
		}
	}

	public static double getBalance(String playerName)
	{
		if(NKData.ACCOUNTS.containsKey(playerName))
		{
			return getOnlineBalance(playerName);
		}
		else
		{
			return getOfflineBalance(playerName);
		}
	}

	public static double getOnlineBalance(String playerName)
	{
		return NKData.ACCOUNTS.get(playerName).getAmount();
	}

	public static double getOfflineBalance(String playerName)
	{
		try
		{
			Connection bdd = DatabaseManager.getConnection();

			String req = "SELECT amount FROM " + DatabaseManager.Tables.ACCOUNTS + " WHERE player_uuid = ?";
			PreparedStatement ps = bdd.prepareStatement(req);
			ps.setString(1, Bukkit.getOfflinePlayer(playerName).getUniqueId().toString());

			ResultSet result = ps.executeQuery();

			if(result.next())
			{
				double amount = result.getDouble("amount");
				ps.close();
				result.close();
				return amount;
			}
			ps.close();
			result.close();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}

		return -1;
	}

	public static boolean hasAccount(String playerName)
	{
		if(NKData.ACCOUNTS.containsKey(playerName))
		{
			return true;
		}
		else
		{
			return hasOfflineAccount(playerName);
		}
	}

	public static boolean hasOfflineAccount(String playerName)
	{
		try
		{
			Connection bdd = DatabaseManager.getConnection();

			String req = "SELECT COUNT(player_uuid) FROM " + DatabaseManager.Tables.ACCOUNTS + " WHERE player_uuid = ?";
			PreparedStatement ps = bdd.prepareStatement(req);
			ps.setString(1, Bukkit.getOfflinePlayer(playerName).getUniqueId().toString());

			ResultSet result = ps.executeQuery();
			result.next();

			if(result.getInt(1) == 1)
			{
				ps.close();
				result.close();

				return true;
			}
			ps.close();
			result.close();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}

		return false;
	}

	public static void payAmount(String playerName, double amount, final String senderName)
	{
		amount = round(amount);
		if(NKData.ACCOUNTS.containsKey(playerName))
		{
			payOnlineAmount(playerName, amount, senderName);
			return;
		}
		if(!NKData.ENABLE_CROSS_SERVER)
		{
			payOfflineAmount(playerName, amount);
			return;
		}
		payCrossServer(playerName, amount, senderName);
	}

	public static void payOnlineAmount(String playerName, double amount, final String sender)
	{
		NKData.ACCOUNTS.get(playerName).addAmount(amount);

		Player player = Bukkit.getPlayer(playerName);
		if(player != null)
		{
			MessageParser messageParser = new MessageParser(Messages.ECO_PAY_TARGET_NOTIFY.toString());
			messageParser.addArg(sender);
			messageParser.addArg(format(amount));
			messageParser.addArg(NKData.CURRENCY);

			player.sendMessage(messageParser.parse());
		}
	}

	public static void payOfflineAmount(String playerName, double amount)
	{
		try
		{
			Connection bdd = DatabaseManager.getConnection();
			String req = "UPDATE " + DatabaseManager.Tables.ACCOUNTS + " SET amount = amount + ? WHERE player_uuid = ?";
			PreparedStatement ps = bdd.prepareStatement(req);
			ps.setDouble(1, amount);
			ps.setString(2, Bukkit.getOfflinePlayer(playerName).getUniqueId().toString());

			ps.executeUpdate();
			ps.close();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}

	public static void payCrossServer(String playerName, double amount, final String senderName)
	{
		String data = "pay|" + playerName + "|" + amount + "|" + senderName; //TODO perform error
		NKcoreAPIManager.nKcoreAPI.sendNetworkDataForPlayer(playerName, NKData.PLUGIN_NAME, data, NKData.PLUGIN_NAME);
	}

	public static void giveAmount(final String playerName, double amount)
	{
		amount = round(amount);
		if(NKData.ACCOUNTS.containsKey(playerName))
		{
			giveOnlineAmount(playerName, amount);
			return;
		}
		if(!NKData.ENABLE_CROSS_SERVER)
		{
			giveOfflineAmount(playerName, amount);
			return;
		}
		giveCrossServer(playerName, amount);
	}

	public static void giveOnlineAmount(final String playerName, double amount)
	{
		NKData.ACCOUNTS.get(playerName).addAmount(amount);

		Player player = Bukkit.getPlayer(playerName);
		if(player != null)
		{
			MessageParser messageParser = new MessageParser(Messages.ECO_GIVE_TARGET_NOTIFY.toString());
			messageParser.addArg(format(amount));
			messageParser.addArg(NKData.CURRENCY);

			player.sendMessage(messageParser.parse());
		}
	}

	public static void giveOfflineAmount(final String playerName, double amount)
	{
		try
		{
			Connection bdd = DatabaseManager.getConnection();
			String req = "UPDATE " + DatabaseManager.Tables.ACCOUNTS + " SET amount = amount + ? WHERE player_uuid = ?";
			PreparedStatement ps = bdd.prepareStatement(req);
			ps.setDouble(1, amount);
			ps.setString(2, Bukkit.getOfflinePlayer(playerName).getUniqueId().toString());

			ps.executeUpdate();
			ps.close();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}

	public static void giveCrossServer(final String playerName, double amount)
	{
		String data = "give|" + playerName + "|" + amount + "|null"; //TODO perform error
		NKcoreAPIManager.nKcoreAPI.sendNetworkDataForPlayer(playerName, NKData.PLUGIN_NAME, data, NKData.PLUGIN_NAME);
	}

	public static boolean takeAmount(final String playerName, double amount)
	{
		amount = round(amount);
		if(round(getBalance(playerName)) >= amount)
		{
			if(NKData.ACCOUNTS.containsKey(playerName))
			{
				takeOnlineAmount(playerName, amount);
			}
			if(!NKData.ENABLE_CROSS_SERVER)
			{
				takeOfflineAmount(playerName, amount);
			}
			takeCrossServer(playerName, amount);

			return true;
		}
		return false;
	}

	public static void takeOnlineAmount(final String playerName, double amount)
	{
		NKData.ACCOUNTS.get(playerName).removeAmount(amount);
		Player player = Bukkit.getPlayer(playerName);
		if(player != null)
		{
			MessageParser messageParser = new MessageParser(Messages.ECO_TAKE_TARGET_NOTIFY.toString());
			messageParser.addArg(format(amount));
			messageParser.addArg(NKData.CURRENCY);

			player.sendMessage(messageParser.parse());
		}
	}

	public static void takeOfflineAmount(final String playerName, double amount)
	{
		try
		{
			Connection bdd = DatabaseManager.getConnection();
			String req = "UPDATE " + DatabaseManager.Tables.ACCOUNTS + " SET amount = amount - ? WHERE player_uuid = ?";
			PreparedStatement ps = bdd.prepareStatement(req);
			ps.setDouble(1, amount);
			ps.setString(2, Bukkit.getOfflinePlayer(playerName).getUniqueId().toString());

			ps.executeUpdate();
			ps.close();

		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}

	public static void takeCrossServer(final String playerName, double amount)
	{
		String data = "take|" + playerName + "|" + amount + "|null"; //TODO perform error
		NKcoreAPIManager.nKcoreAPI.sendNetworkDataForPlayer(playerName, NKData.PLUGIN_NAME, data, NKData.PLUGIN_NAME);
	}

	public static void setAmount(final String playerName, double amount)
	{
		amount = round(amount);
		if(NKData.ACCOUNTS.containsKey(playerName))
		{
			setOnlineAmount(playerName, amount);
		}
		if(!NKData.ENABLE_CROSS_SERVER)
		{
			setOfflineAmount(playerName, amount);
		}
		setCrossServer(playerName, amount);
	}

	public static void setOnlineAmount(final String playerName, double amount)
	{
		NKData.ACCOUNTS.get(playerName).setAmount(amount);
		Player player = Bukkit.getPlayer(playerName);
		if(player != null)
		{
			MessageParser messageParser = new MessageParser(Messages.ECO_SET_TARGET_NOTIFY.toString());
			messageParser.addArg(format(amount));
			messageParser.addArg(NKData.CURRENCY);

			player.sendMessage(messageParser.parse());
		}
	}

	public static void setOfflineAmount(final String playerName, double amount)
	{
		try
		{
			Connection bdd = DatabaseManager.getConnection();
			String req = "UPDATE " + DatabaseManager.Tables.ACCOUNTS + " SET amount = ? WHERE player_uuid = ?";
			PreparedStatement ps = bdd.prepareStatement(req);
			ps.setDouble(1, amount);
			ps.setString(2, Bukkit.getOfflinePlayer(playerName).getUniqueId().toString());

			ps.executeUpdate();
			ps.close();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}

	public static void setCrossServer(final String playerName, double amount)
	{
		String data = "set|" + playerName + "|" + amount + "|null"; //TODO perform error
		NKcoreAPIManager.nKcoreAPI.sendNetworkDataForPlayer(playerName, NKData.PLUGIN_NAME, data, NKData.PLUGIN_NAME);
	}

	public static @NotNull Map<String, Double> topAmount(int page)
	{
		Map<String, Double> topAmount = new LinkedHashMap<>();

		if(page >= 1)
		{
			try
			{
				Connection bdd = DatabaseManager.getConnection();
				String req = "SELECT name, amount FROM " + DatabaseManager.Tables.ACCOUNTS + " ORDER BY amount DESC LIMIT ?, 10";
				PreparedStatement ps = bdd.prepareStatement(req);
				ps.setInt(1, (page - 1) * 10);
				ResultSet result = ps.executeQuery();

				while(result.next())
				{
					topAmount.put(result.getString("name"), result.getDouble("amount"));
				}

				ps.close();
				result.close();
			}
			catch(SQLException e)
			{
				e.printStackTrace();
			}
		}

		return topAmount;
	}

	public static String format(double amount)
	{
		String pattern;
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

	public static double round(double value)
	{
		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(5, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}

	public static void onDataReceived(String data)
	{
		String[] args = data.split("\\|");

		if(args.length >= 4)
		{
			switch(args[0])
			{
				case "give" -> AccountManager.giveAmount(args[1], Double.parseDouble(args[2]));
				case "pay" -> AccountManager.payAmount(args[1], Double.parseDouble(args[2]), args[3]);
				case "take" -> AccountManager.takeAmount(args[1], Double.parseDouble(args[2]));
				case "set" -> AccountManager.setAmount(args[1], Double.parseDouble(args[2]));
				default -> {
				}
			}
		}
	}

	public static void onErrorReceived(String data)
	{
		String[] args = data.split("\\|");

		if(args.length >= 4)
		{
			switch(args[0])
			{
				case "give" -> AccountManager.giveOfflineAmount(args[1], Double.parseDouble(args[2]));
				case "pay" -> AccountManager.payOfflineAmount(args[1], Double.parseDouble(args[2]));
				case "take" -> AccountManager.takeOfflineAmount(args[1], Double.parseDouble(args[2]));
				case "set" -> AccountManager.setOfflineAmount(args[1], Double.parseDouble(args[2]));
				default -> {
				}
			}
		}
	}
}

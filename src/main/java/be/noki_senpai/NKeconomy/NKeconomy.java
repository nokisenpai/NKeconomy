package be.noki_senpai.NKeconomy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import be.noki_senpai.NKeconomy.data.Players;
import be.noki_senpai.NKeconomy.listeners.EcoCompleter;
import be.noki_senpai.NKeconomy.listeners.PlayerConnectionListener;
import be.noki_senpai.NKeconomy.utils.Economy_NKeconomy;
import be.noki_senpai.NKeconomy.utils.SQLConnect;
import net.milkbowl.vault.economy.Economy;

public class NKeconomy extends JavaPlugin 
{
	public final static String PName = "[NKeconomy]";
	public static String prefix;
	public static Map<String, String> table = new HashMap<>();
	
	// Options
	public static double startAmount = 100;
	public static String currency = "Lumi";
	
	
	
	private static NKeconomy instance;
	public ConsoleCommandSender console = getServer().getConsoleSender();
	private static Connection bdd = null;
			
	public static Map<String, Players> players = new TreeMap<String, Players>(String.CASE_INSENSITIVE_ORDER);
	
	private static Economy econ = null;
		
	
	
	
	
	String tmp1 = "";
	String tmp2 = "";
	
	
	
	
	// Fired when plugin is first enabled
	@Override
    public void onEnable() 
	{
		instance = this;
		
		//Say to Vault "Hey ! I'm an economy plugin !"
		Bukkit.getServicesManager().register(Economy.class, new Economy_NKeconomy(this), getServer().getPluginManager().getPlugin("Vault"), ServicePriority.Normal);
		
		if (!setupEconomy() ) 
		{
            console.sendMessage(ChatColor.DARK_RED + PName + " Disabled due to no Vault dependency found ! (Error#A.2.000)");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
				
		this.saveDefaultConfig();
		
		if(this.getConfig().getBoolean("use-mysql"))
		{

			prefix = this.getConfig().getString("table-prefix");
			startAmount = this.getConfig().getDouble("start-amount");
			currency = this.getConfig().getString("currency").replace("&", "§");
			table.put("players", prefix + "players");
			table.put("accounts", prefix + "accounts");

			//Setting database informations		
			SQLConnect.setInfo(this.getConfig().getString("host"), this.getConfig().getInt("port"), this.getConfig().getString("dbName"), this.getConfig().getString("user"), this.getConfig().getString("password"));
			try
			{
				bdd = SQLConnect.getHikariDS().getConnection();
			} 
			catch (SQLException e1)
			{
				bdd = null;
				console.sendMessage(ChatColor.DARK_RED + PName + " Error while attempting database connexion. Verify your access informations in config.yml");
				getServer().getPluginManager().disablePlugin(this);
				e1.printStackTrace();
			}
			
			
			
			try 
			{
				//Creating database structure if not exist
				Storage.createTable(this.getConfig().getString("dbName"), prefix, table);
			} 
			catch (SQLException e) 
			{
				console.sendMessage(ChatColor.DARK_RED + PName + " Error while creating database structure. (Error#A.2.002)");
				getServer().getPluginManager().disablePlugin(this);
			}
			
			//On command
			getServer().getPluginManager().registerEvents(new PlayerConnectionListener(), this);
			this.getCommand("eco").setTabCompleter(new EcoCompleter());
			getCommand("eco").setExecutor(new EcoCmd());
			
			Bukkit.getOnlinePlayers().forEach(player -> players.putIfAbsent(player.getDisplayName(), new Players(player.getUniqueId())));
			
			new BukkitRunnable() 
			{
			    @Override
			    public void run() 
			    {
			        saveAll();
			    }
			}.runTaskTimerAsynchronously(this, 0, 300*20);
			
		}
		else
		{
			console.sendMessage(ChatColor.DARK_RED + PName + " Disabled because this plugin only use MySQL database. Please set to true the 'use-mysql' field in config.yml");
			getServer().getPluginManager().disablePlugin(this);
		}
		
    }
	
	
    // Fired when plugin is disabled
    @Override
    public void onDisable() 
    {
    	if(bdd != null)
    	{
    		saveAll();
        	players.clear();
        	try
    		{
    			bdd.close();
    		} 
        	catch (SQLException e)
    		{
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    	}
    	kickAllPlayers();
    	console.sendMessage(ChatColor.GREEN + PName + " has been disable.");
    }
    

	
	public static NKeconomy getInstance()
	{
		return instance;
	}
	public ConsoleCommandSender getConsole()
	{
		return console;
	}
	
	private boolean setupEconomy() 
	{
        if (getServer().getPluginManager().getPlugin("Vault") == null) 
        {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) 
        {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
	
	public void disablePlugin()
	{
		getServer().getPluginManager().disablePlugin(this);
	}
	
	public Connection getConnection()
	{
		return bdd;
	}
	
	public void saveAll()
	{
		if(players.size() > 0)
		{
			PreparedStatement ps = null;
			String req = null;
			tmp1 = "";
			tmp2 = "(";
			
			players.forEach((key, value) -> 
			{
				tmp1 += "WHEN " + value.getPlayerID() + " THEN " + value.getPlayerAccount().getAmount() + " ";
				tmp2 += value.getPlayerID() + ",";
			});
			

			tmp2 += "-1)";
	
			req = "UPDATE " + table.get("accounts") + " SET amount = CASE player_id " + tmp1 + "ELSE amount END WHERE player_id IN" + tmp2;  
	        try
			{
				ps = bdd.prepareStatement(req);
				ps.executeUpdate();
				ps.close();
			} 
	        catch (SQLException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	// transactions	
	public static void addAmount(String playerName, Double amount)
	{
		amount = round(amount);
		if(players.containsKey(playerName))
    	{
			players.get(playerName).getPlayerAccount().addAmount(amount);
    	}
		else
		{
			Connection bdd = null;
			PreparedStatement ps = null;
			String req = null;

			req = "UPDATE " + table.get("accounts") + " ac LEFT JOIN " + table.get("players") + " pl ON ac.player_id = pl.id SET ac.amount=ac.amount+" + amount + " WHERE name='" + playerName + "'";

	        try
			{
	        	bdd = getInstance().getConnection();
				ps = bdd.prepareStatement(req);
				ps.executeUpdate();
				ps.close();
			} 
	        catch (SQLException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	public static boolean removeAmount(String playerName, Double amount)
	{
		amount = round(amount);
		if(Players.getBalance(playerName) >= amount)
    	{
			if(players.containsKey(playerName))
	    	{
				players.get(playerName).getPlayerAccount().removeAmount(amount);
				return true;
	    	}
			else
			{
				Connection bdd = null;
				PreparedStatement ps = null;
				String req = null;
	
		        try
				{
	        		bdd = getInstance().getConnection();
		        	
		        	req = "UPDATE " + table.get("accounts") + " ac LEFT JOIN " + table.get("players") + " pl ON ac.player_id = pl.id SET ac.amount=ac.amount-? WHERE name=?";
					ps = bdd.prepareStatement(req);
					ps.setDouble(1, amount);
					ps.setString(2, playerName);
					
					ps.executeUpdate();
					ps.close();
					return true;
				} 
		        catch (SQLException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
    	}
		return false;
	}
	
	public static void setAmount(String playerName, Double amount)
	{
		amount = round(amount);
		if(players.containsKey(playerName))
    	{
			players.get(playerName).getPlayerAccount().setAmount(amount);
    	}
		else
		{
			Connection bdd = null;
			PreparedStatement ps = null;
			String req = null;

			
	        try
			{
	        	bdd = getInstance().getConnection();
	        	
	        	req = "UPDATE " + table.get("accounts") + " ac LEFT JOIN " + table.get("players") + " pl ON ac.player_id = pl.id SET ac.amount=? WHERE name=?";
				ps = bdd.prepareStatement(req);
				ps.setDouble(1, amount);
				ps.setString(2, playerName);
				
				ps.executeUpdate();
				ps.close();
			} 
	        catch (SQLException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static LinkedHashMap<String, Double> topAmount(int page)
	{
		if(page >= 1)
		{
			LinkedHashMap<String, Double> topAmount = new LinkedHashMap<String, Double>();
			
			Connection bdd = null;
			PreparedStatement ps = null;
			ResultSet resultat = null;
			String req = null;
	
			
	        try
			{
	        	bdd = getInstance().getConnection();
	        	
	        	req = "SELECT pl.name, ac.amount FROM " + table.get("accounts") + " ac LEFT JOIN " + table.get("players") + " pl ON pl.id=ac.player_id ORDER BY ac.amount DESC LIMIT ?, 10";
				ps = bdd.prepareStatement(req);
				ps.setInt(1, (page-1)*10);
				resultat = ps.executeQuery();
				
				while (resultat.next()) 
				{
					topAmount.put(resultat.getString("name"), resultat.getDouble("amount"));
				}
				
				ps.close();
				resultat.close();
				return topAmount;
			} 
	        catch (SQLException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;

	}
	
	public static String format(double amount) 
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
    	
    	String format = decimalFormat.format(amount);
  
        return format;
	}
	
	
	
	public static double round(double value) 
	{
	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(5, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
	
	public static void kickAllPlayers()
	{
		Bukkit.getOnlinePlayers().forEach((player) -> 
		{
			player.kickPlayer("Le serveur redémarre.");
		});
	}
}

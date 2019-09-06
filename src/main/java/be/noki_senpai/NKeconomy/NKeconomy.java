package be.noki_senpai.NKeconomy;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import be.noki_senpai.NKeconomy.managers.ConfigManager;
import be.noki_senpai.NKeconomy.managers.Manager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import be.noki_senpai.NKeconomy.cmd.EcoCmd;
import be.noki_senpai.NKeconomy.data.Account;
import be.noki_senpai.NKeconomy.listeners.EcoCompleter;
import be.noki_senpai.NKeconomy.listeners.PlayerConnectionListener;
import be.noki_senpai.NKeconomy.utils.Economy_NKeconomy;
import be.noki_senpai.NKeconomy.utils.SQLConnect;
import net.milkbowl.vault.economy.Economy;

public class NKeconomy extends JavaPlugin implements PluginMessageListener
{
	public final static String PNAME = "[NKeconomy]";
	private Manager manager = null;
	private ConsoleCommandSender console = null;
	private static NKeconomy plugin;
	private static Economy economy = null;

	// Fired when plugin is first enabled
	@Override public void onEnable()
	{
		plugin = this;
		System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "WARN");
		this.saveDefaultConfig();

		console = Bukkit.getConsoleSender();
		manager = new Manager(this);

		// Link with Vault
		if(!setupEconomy())
		{
			console.sendMessage(ChatColor.DARK_RED + PNAME + " Disabled due to no Vault dependency found ! (Error#A.2.000)");
			disablePlugin();
			return;
		}

		// Load configuration
		if(!manager.getConfigManager().loadConfig())
		{
			disablePlugin();
			return;
		}

		// Load database connection (with check)
		if(!manager.getDatabaseManager().loadDatabase())
		{
			disablePlugin();
			return;
		}

		// On command
		getServer().getPluginManager().registerEvents(new PlayerConnectionListener(manager.getQueueManager(), manager.getAccountManager()), this);
		this.getCommand("eco").setTabCompleter(new EcoCompleter());
		getCommand("eco").setExecutor(new EcoCmd(manager.getQueueManager(), manager.getAccountManager()));

		// Data exchange between servers
		this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);

		console.sendMessage(ChatColor.WHITE + "     .--. ");
		console.sendMessage(ChatColor.WHITE + "     |    '.   " + ChatColor.GREEN + PNAME + " by NoKi_senpai - successfully enabled !");
		console.sendMessage(ChatColor.WHITE + "'-..____.-'");
	}

	// Fired when plugin is disabled
	@Override public void onDisable()
	{
		manager.getDatabaseManager().unloadDatabase();
		manager.getAccountManager().unloadAccout();
		kickAllPlayers();
		console.sendMessage(ChatColor.GREEN + PNAME + " has been disable.");
	}

	// ######################################
	// Getters & Setters
	// ######################################

	// Getter 'instance'
	public static NKeconomy getPlugin()
	{
		return plugin;
	}

	// Getter 'bdd'
	public Economy getEconomy()
	{
		return economy;
	}

	// ######################################
	// Setup Vault economy
	// ######################################

	private boolean setupEconomy()
	{
		if(getServer().getPluginManager().getPlugin("Vault") == null)
		{
			return false;
		}
		// Say to Vault "Hey ! I'm an economy plugin !"
		Bukkit.getServicesManager().register(Economy.class, new Economy_NKeconomy(this, manager.getAccountManager()), getServer().getPluginManager().getPlugin("Vault"), ServicePriority.Normal);

		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if(rsp == null)
		{
			return false;
		}
		economy = rsp.getProvider();
		return economy != null;
	}

	// ######################################
	// Disable this plugin
	// ######################################

	public void disablePlugin()
	{
		getServer().getPluginManager().disablePlugin(this);
	}

	// ######################################
	// Data exchange between servers
	// ######################################

	@Override public void onPluginMessageReceived(String channel, Player player, byte[] message)
	{

		if(!channel.equals("BungeeCord"))
		{
			return;
		}
		ByteArrayDataInput in = ByteStreams.newDataInput(message);
		String subchannel = in.readUTF();

		if(subchannel.equals("NKeconomy"))
		{
			String tmp = in.readUTF();
			tmp = tmp.substring(2, tmp.length());
			String[] args = tmp.split("\\|");

			if(args.length >= 4)
			{

				switch(args[0])
				{
					case "give":
						manager.getAccountManager().giveAmount(args[1], Double.parseDouble(args[2]), true);
						break;
					case "pay":
						manager.getAccountManager().payAmount(args[1], Double.parseDouble(args[2]), args[3], true);
						break;
					case "take":
						manager.getAccountManager().takeAmount(args[1], Double.parseDouble(args[2]), true);
						break;
					case "set":
						manager.getAccountManager().setAmount(args[1], Double.parseDouble(args[2]), true);
						break;
					default:
				}

			}
		}
	}

	// ######################################
	// External feature
	// ######################################

	public static void kickAllPlayers()
	{
		Bukkit.getOnlinePlayers().forEach((player) -> {
			player.kickPlayer("Le serveur redémarre.");
		});
	}
}

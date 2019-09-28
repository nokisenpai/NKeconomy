package be.noki_senpai.NKeconomy.managers;

import be.noki_senpai.NKeconomy.NKeconomy;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;

public class Manager
{
	private ConsoleCommandSender console = null;
	private QueueManager queueManager = null;
	private ConfigManager configManager = null;
	private DatabaseManager databaseManager = null;
	private AccountManager accountManager = null;


	public Manager(NKeconomy instance)
	{
		console = Bukkit.getConsoleSender();
		queueManager = new QueueManager();
		configManager = new ConfigManager(instance.getConfig());
		databaseManager = new DatabaseManager(configManager);
		accountManager = new AccountManager(queueManager);
	}

	// ######################################
	// Getters & Setters
	// ######################################

	// Console
	public ConsoleCommandSender getConsole()
	{
		return console;
	}

	// QueueManager
	public QueueManager getQueueManager()
	{
		return queueManager;
	}

	// PluginManager
	public ConfigManager getConfigManager()
	{
		return configManager;
	}

	// DatabaseManager
	public DatabaseManager getDatabaseManager()
	{
		return databaseManager;
	}

	// PlayerManager
	public AccountManager getAccountManager()
	{
		return accountManager;
	}
}

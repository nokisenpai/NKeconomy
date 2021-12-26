package ovh.lumen.NKeconomy;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import ovh.lumen.NKeconomy.data.NKData;
import ovh.lumen.NKeconomy.enums.InternalMessages;
import ovh.lumen.NKeconomy.exceptions.SetupException;
import ovh.lumen.NKeconomy.interfaces.NKplugin;
import ovh.lumen.NKeconomy.managers.AccountManager;
import ovh.lumen.NKeconomy.managers.ConfigManager;
import ovh.lumen.NKeconomy.managers.DatabaseManager;
import ovh.lumen.NKeconomy.managers.NKcoreAPIManager;
import ovh.lumen.NKeconomy.registers.CommandRegister;
import ovh.lumen.NKeconomy.registers.CompleterRegister;
import ovh.lumen.NKeconomy.registers.ListenerRegister;
import ovh.lumen.NKeconomy.utils.Economy_NKeconomy;
import ovh.lumen.NKeconomy.utils.NKLogger;

public class Main extends JavaPlugin implements NKplugin
{
	@Override
	public void onEnable()
	{
		setup();
	}

	@Override
	public void onDisable()
	{
		clean();
	}

	@Override
	public void setup()
	{
		NKData.PLUGIN = this;
		NKData.PLUGIN_NAME = this.getName();
		NKData.PLUGIN_VERSION = this.getDescription().getVersion();
		NKData.PLUGIN_AUTHOR = this.getDescription().getAuthors().get(0);

		this.saveDefaultConfig();

		// Init
		NKLogger.init(Bukkit.getConsoleSender());
		ConfigManager.init(this.getConfig());

		// Load
		try
		{
			NKcoreAPIManager.load(this);
			ConfigManager.load();
			setupEconomy();
			DatabaseManager.load();
			AccountManager.load();
		}
		catch(SetupException e)
		{
			NKLogger.error(e.getMessage());
			disablePlugin();

			return;
		}

		//Register
		ListenerRegister.registerAllListeners(this);
		CommandRegister.registerAllCommands(this);
		CompleterRegister.registerAllCompleters(this);

		displayNKSuccess();
	}

	@Override
	public void clean()
	{
		AccountManager.unload();
		DatabaseManager.unload();
	}

	@Override
	public void reload()
	{
		NKLogger.send(InternalMessages.RELOAD_ANNOUNCE.toString());
		clean();
		setup();
	}

	private void setupEconomy() throws SetupException
	{
		if(getServer().getPluginManager().getPlugin("Vault") == null)
		{
			throw new SetupException(InternalMessages.VAULT_MISSING.toString());
		}

		Bukkit.getServicesManager().register(Economy.class, new Economy_NKeconomy(this), Bukkit.getServer().getPluginManager().getPlugin("Vault"), ServicePriority.Normal);
	}

	public void disablePlugin()
	{
		getServer().getPluginManager().disablePlugin(this);
	}

	private void displayNKSuccess()
	{
		NKLogger.show("\n"
				+ ChatColor.WHITE + "\u00A0 \u00A0 \u00A0.--.\n"
				+ "\u00A0 \u00A0 \u00A0| \u00A0 '. \u00A0" + ChatColor.GREEN + NKData.PLUGIN_NAME + " v" + NKData.PLUGIN_VERSION + " by " + NKData.PLUGIN_AUTHOR
				+ " - successfully enabled !\n"
				+ ChatColor.WHITE + "'-..___.-'");
	}
}

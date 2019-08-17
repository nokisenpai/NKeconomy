package be.noki_senpai.NKeconomy.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

import be.noki_senpai.NKeconomy.NKeconomy;
import net.milkbowl.vault.economy.AbstractEconomy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;

public class Economy_NKeconomy extends AbstractEconomy 
{
	private static final Logger log = Logger.getLogger("Minecraft");

	private final String name = "NKeconomy";
	private Plugin plugin = null;
	private NKeconomy economy = null;

	public Economy_NKeconomy(Plugin plugin)
	{
		this.plugin = plugin;
		Bukkit.getServer().getPluginManager().registerEvents(new EconomyServerListener(this), plugin);

		if (economy == null) 
		{
			Plugin nkeconomy = plugin.getServer().getPluginManager().getPlugin(name);

			if (nkeconomy != null && nkeconomy.isEnabled()) 
			{
				economy = (NKeconomy) nkeconomy;
				log.info(String.format("[%s] hooked as economy plugin.", plugin.getDescription().getName(), name));
			}
		}
	}

	public class EconomyServerListener implements Listener 
	{
		Economy_NKeconomy economy = null;

		public EconomyServerListener(Economy_NKeconomy economy) 
		{
			this.economy = economy;
		}

		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginEnable(PluginEnableEvent event) 
		{
			if (economy.economy == null) 
			{
				Plugin nkeconomy = event.getPlugin();

				if (nkeconomy.getDescription().getName().equals(economy.name)) 
				{
					economy.economy = (NKeconomy) nkeconomy;
					log.info(String.format("[%s] hooked as economy plugin.", plugin.getDescription().getName(), economy.name));
				}
			}
		}

		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginDisable(PluginDisableEvent event) 
		{
			if (economy.economy != null) 
			{
				if (event.getPlugin().getDescription().getName().equals(economy.name)) 
				{
					economy.economy = null;
					log.info(String.format("[%s] unhooked as economy plugin.", plugin.getDescription().getName(), economy.name));
				}
			}
		}
	}
	
	
	
	

	@Override
	public boolean isEnabled() 
	{
		if (plugin == null) 
		{
			return false;
		} 
		else 
		{
			return plugin.isEnabled();
		}
	}

	@Override
	public String getName() 
	{
		return name;
	}

	
	
	//####################################
	// Simple account
	//####################################
	
	@Override
	public double getBalance(String playerName) 
	{
		double balance;

		try 
		{
			balance = NKeconomy.getBalance(playerName);
		} 
		catch (Exception e) 
		{
			balance = 0;
		}

		return balance;
	}

	public boolean createPlayerAccount(String playerName) 
	{
		/*if (hasAccount(playerName)) 
		{
			return false;
		}
		//return com.earth2me.essentials.api.Economy.createNPC(playerName);*/
		return true;
	}

	@Override
	public EconomyResponse withdrawPlayer(String playerName, double amount) 
	{
		amount = NKeconomy.round(amount);
		if (amount < 0) 
		{
			return new EconomyResponse(0, 0, ResponseType.FAILURE, "Cannot withdraw negative funds");
		}
		
		double balance;
		EconomyResponse.ResponseType type;
		String errorMessage = null;

		if(NKeconomy.takeAmount(playerName, amount, false))
		{
			balance = getBalance(playerName);
			type = EconomyResponse.ResponseType.SUCCESS;
		}
		else
		{
			balance = 0;
			amount = 0;
			type = EconomyResponse.ResponseType.FAILURE;
		}
		
		return new EconomyResponse(amount, balance, type, errorMessage);
	}

	@Override
	public EconomyResponse depositPlayer(String playerName, double amount) 
	{	
		amount = NKeconomy.round(amount);
		if (amount < 0) 
		{
			return new EconomyResponse(0, 0, ResponseType.FAILURE, "Cannot desposit negative funds");
		}
		
		double balance;
		EconomyResponse.ResponseType type;
		String errorMessage = null;
		
		
		NKeconomy.giveAmount(playerName, amount, false);
		balance = getBalance(playerName);
		type = EconomyResponse.ResponseType.SUCCESS;

		return new EconomyResponse(amount, balance, type, errorMessage);
	}
	
	@Override
	public boolean hasAccount(String playerName) 
	{
		return NKeconomy.hasAccount(playerName);
	}
	
	@Override
	public boolean has(String playerName, double amount) 
	{
		amount = NKeconomy.round(amount);
		if(getBalance(playerName)>=amount)
		{
			return true;
		}
		return false;
	}
	
	
	
	//####################################
	// Currency & format
	//####################################
	
	@Override
	public String format(double amount) 
	{
		return NKeconomy.format(amount)+" "+NKeconomy.currency+ChatColor.RESET;
	}

	@Override
	public String currencyNameSingular() 
	{
		return "Lumi";
	}

	@Override
	public String currencyNamePlural() 
	{
		return "Lumi";
	}
	
	@Override
	public int fractionalDigits() 
	{
		return 5;
	}

	//####################################
	// Bank (not implemented)
	//####################################
	@Override
	public EconomyResponse createBank(String name, String player) 
	{
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "NKeconomy does not support bank accounts!");
	}

	@Override
	public EconomyResponse deleteBank(String name) 
	{
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "NKeconomy does not support bank accounts!");
	}

	@Override
	public EconomyResponse bankHas(String name, double amount) 
	{
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "NKeconomy does not support bank accounts!");
	}

	@Override
	public EconomyResponse bankWithdraw(String name, double amount) 
	{
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "NKeconomy does not support bank accounts!");
	}

	@Override
	public EconomyResponse bankDeposit(String name, double amount) 
	{
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "NKeconomy does not support bank accounts!");
	}

	@Override
	public EconomyResponse isBankOwner(String name, String playerName) 
	{
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "NKeconomy does not support bank accounts!");
	}

	@Override
	public EconomyResponse isBankMember(String name, String playerName) 
	{
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "NKeconomy does not support bank accounts!");
	}

	@Override
	public EconomyResponse bankBalance(String name) 
	{
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "NKeconomy does not support bank accounts!");
	}

	@Override
	public List<String> getBanks() 
	{
		return new ArrayList<String>();
	}

	@Override
	public boolean hasBankSupport() 
	{
		return false;
	}
	
	//####################################
	// World support account (not implemented)
	//####################################
	@Override
	public boolean hasAccount(String playerName, String worldName) 
	{
		return hasAccount(playerName);
	}

	@Override
	public double getBalance(String playerName, String world) 
	{
		return getBalance(playerName);
	}

	@Override
	public boolean has(String playerName, String worldName, double amount) 
	{
		return has(playerName, amount);
	}

	@Override
	public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) 
	{
		return withdrawPlayer(playerName, amount);
	}

	@Override
	public EconomyResponse depositPlayer(String playerName, String worldName, double amount) 
	{
		return depositPlayer(playerName, amount);
	}

	@Override
	public boolean createPlayerAccount(String playerName, String worldName) 
	{
		return createPlayerAccount(playerName);
	}
}

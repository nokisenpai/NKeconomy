package ovh.lumen.NKeconomy.utils;

import net.milkbowl.vault.economy.AbstractEconomy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import ovh.lumen.NKeconomy.Main;
import ovh.lumen.NKeconomy.data.NKData;
import ovh.lumen.NKeconomy.managers.AccountManager;

import java.util.ArrayList;
import java.util.List;

public class Economy_NKeconomy extends AbstractEconomy
{
	private final Plugin plugin;
	private Main economy;

	public Economy_NKeconomy(Plugin plugin)
	{
		this.plugin = plugin;

		Bukkit.getServer().getPluginManager().registerEvents(new EconomyServerListener(this), plugin);
		economy = (Main) plugin;

		if(economy.isEnabled())
		{
			NKLogger.log(String.format("[%s] hooked as economy plugin.", economy.getDescription().getName()));
		}
	}

	public static class EconomyServerListener implements Listener
	{
		Economy_NKeconomy economyImpl;

		public EconomyServerListener(Economy_NKeconomy economyImpl)
		{
			this.economyImpl = economyImpl;
		}

		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginEnable(PluginEnableEvent event)
		{
			if(economyImpl.economy == null)
			{
				Plugin nkeconomy = event.getPlugin();

				if(nkeconomy.getName().equals(NKData.PLUGIN_NAME))
				{
					economyImpl.economy = (Main) nkeconomy;
					NKLogger.log(String.format("[%s] hooked as economy plugin.", economyImpl.economy.getName()));
				}
			}
		}

		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginDisable(PluginDisableEvent event)
		{
			if(economyImpl.economy != null)
			{
				if(event.getPlugin().getName().equals(NKData.PLUGIN_NAME))
				{
					NKLogger.log(String.format("[%s] unhooked as economy plugin.", economyImpl.economy.getName()));
					economyImpl.economy = null;
				}
			}
		}
	}

	@Override
	public boolean isEnabled()
	{
		if(plugin == null)
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
		return economy.getName();
	}

	// ####################################
	// Simple account
	// ####################################

	@Override
	public double getBalance(String playerName)
	{
		double balance;
		try
		{
			balance = AccountManager.getBalance(playerName);
		}
		catch(Exception e)
		{
			balance = 0;
		}
		return balance;
	}

	public boolean createPlayerAccount(String playerName)
	{
		NKLogger.debug("Account creation is not allowed.");
		return true;
	}

	@Override
	public EconomyResponse withdrawPlayer(String playerName, double amount)
	{
		amount = AccountManager.round(amount);
		if(amount < 0)
		{
			return new EconomyResponse(0, 0, ResponseType.FAILURE, "Cannot withdraw negative funds");
		}

		double balance;
		EconomyResponse.ResponseType type;

		if(AccountManager.takeAmount(playerName, amount))
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

		return new EconomyResponse(amount, balance, type, null);
	}

	@Override
	public EconomyResponse depositPlayer(String playerName, double amount)
	{
		amount = AccountManager.round(amount);
		if(amount < 0)
		{
			return new EconomyResponse(0, 0, ResponseType.FAILURE, "Cannot deposit negative funds");
		}

		double balance;
		EconomyResponse.ResponseType type;

		AccountManager.giveAmount(playerName, amount);
		balance = getBalance(playerName);
		type = EconomyResponse.ResponseType.SUCCESS;

		return new EconomyResponse(amount, balance, type, null);
	}

	@Override
	public boolean hasAccount(String playerName)
	{
		return AccountManager.hasAccount(playerName);
	}

	@Override
	public boolean has(String playerName, double amount)
	{
		amount = AccountManager.round(amount);

		return getBalance(playerName) >= amount;
	}

	// ####################################
	// Currency & format
	// ####################################

	@Override
	public String format(double amount)
	{
		return AccountManager.format(amount) + " " + NKData.CURRENCY + ChatColor.RESET;
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

	// ####################################
	// Bank (not implemented)
	// ####################################
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
		return new ArrayList<>();
	}

	@Override
	public boolean hasBankSupport()
	{
		return false;
	}

	// ####################################
	// World support account (not implemented)
	// ####################################
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

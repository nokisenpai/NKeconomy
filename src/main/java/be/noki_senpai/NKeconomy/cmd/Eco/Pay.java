package be.noki_senpai.NKeconomy.cmd;

import be.noki_senpai.NKeconomy.managers.AccountManager;
import be.noki_senpai.NKeconomy.managers.ConfigManager;
import be.noki_senpai.NKeconomy.managers.QueueManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import be.noki_senpai.NKeconomy.NKeconomy;
import be.noki_senpai.NKeconomy.utils.CheckType;

import java.util.function.Function;

public class Pay
{
	private AccountManager accountManager = null;
	private QueueManager queueManager = null;

	public Pay(QueueManager queueManager, AccountManager accountManager)
	{
		this.queueManager = queueManager;
		this.accountManager = accountManager;
	}

	public boolean pay(CommandSender sender, String[] args)
	{

		// Command called by a player
		if (sender instanceof Player)
		{
			// If no more argument
			if (args.length == 1 || args.length == 2)
			{
				sender.sendMessage(ChatColor.GREEN + EcoCmd.usageCmdPay);
				return true;
			}
			if (args.length == 3)
			{
				// Display money amount of player
				// Check permission to display money amount of a player
				if (!(sender.hasPermission("*") || sender.hasPermission("nkeco.*") || sender.hasPermission("nkeco.pay") || sender.hasPermission("nkeco.user") || sender.hasPermission("nkeco.admin")))
				{
					// Send that the player does not have the permission
					sender.sendMessage(ChatColor.RED + NKeconomy.PNAME + " Vous n'avez pas la permission !");
					return true;
				}
				else
				{
					if (CheckType.isNumber(args[2]))
					{
						queueManager.addToQueue(new Function()
						{
							@Override public Object apply(Object o)
							{
								if (accountManager.hasAccount(args[1]))
								{
									if (accountManager.takeAmount(sender.getName(), Double.parseDouble(args[2]), false))
									{
										accountManager.payAmount(args[1], Double.parseDouble(args[2]), sender.getName(), false);
										sender.sendMessage(ChatColor.GREEN + "Vous avez donné " + accountManager.format(Double.parseDouble(args[2])) + " " + ConfigManager.CURRENCY + ChatColor.GREEN + " � " + ChatColor.AQUA + args[1]);
									}
									else
									{
										sender.sendMessage(ChatColor.RED + "Vous n'avez pas assez de " + ConfigManager.CURRENCY);
									}
								}
								else
								{
									sender.sendMessage(ChatColor.RED + "Joueur introuvable");
								}
								return null;
							}
						});
					}
					else
					{
						sender.sendMessage(ChatColor.RED + "Le montant doit être un nombre");
					}
				}
			}
			return true;
		}

		// Command called by Console
		if (sender instanceof ConsoleCommandSender)
		{
			sender.sendMessage(ChatColor.RED + "Vous ne pouvez pas utiliser cette commande dans la console.");
			return true;
		}
		// Command does not called by player or Console
		sender.sendMessage(ChatColor.DARK_RED + NKeconomy.PNAME + " An error has occured");
		Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + NKeconomy.PNAME + " An error has occured (Error#3.004)");
		return true;
	}
}

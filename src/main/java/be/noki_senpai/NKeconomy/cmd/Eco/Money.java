package be.noki_senpai.NKeconomy.cmd.Eco;

import be.noki_senpai.NKeconomy.managers.AccountManager;
import be.noki_senpai.NKeconomy.managers.ConfigManager;
import be.noki_senpai.NKeconomy.managers.QueueManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import be.noki_senpai.NKeconomy.NKeconomy;

import java.util.function.Function;

public class Money
{
	private QueueManager queueManager = null;
	private AccountManager accountManager = null;

	public Money (QueueManager queueManager, AccountManager accountManager)
	{
		this.queueManager = queueManager;
		this.accountManager = accountManager;
	}

	public boolean money(CommandSender sender, String[] args)
	{

		// Command called by a player
		if (sender instanceof Player)
		{
			// If no more argument
			if (args.length == 1)
			{
				if (!(sender.hasPermission("*") || sender.hasPermission("nkeco.*") || sender.hasPermission("nkeco.money") || sender.hasPermission("nkeco.user") || sender.hasPermission("nkeco.admin")))
				{
					// Send that the player does not have the permission
					sender.sendMessage(ChatColor.RED + " Vous n'avez pas la permission !");
					return true;
				}
				else
				{
					// Display amount money of sender
					sender.sendMessage(ChatColor.GREEN + "Vous avez " + accountManager.format(accountManager.getAccount(sender.getName()).getAmount()) + " " + ConfigManager.CURRENCY);
				}

				return true;
			}
			if (args.length == 2)
			{
				// Display money amount of player
				// Check permission to display money amount of a player
				if (!(sender.hasPermission("*") || sender.hasPermission("nkeco.*") || sender.hasPermission("nkeco.money.other") || sender.hasPermission("nkeco.money.*") || sender.hasPermission("nkeco.admin")))
				{
					// Send that the player does not have the permission
					sender.sendMessage(ChatColor.RED + NKeconomy.PNAME + " Vous n'avez pas la permission !");
					return true;
				}
				else
				{
					queueManager.addToQueue(new Function()
					{
						@Override public Object apply(Object o)
						{
							if (accountManager.hasAccount(args[1]))
							{
								sender.sendMessage(ChatColor.AQUA + args[1] + ChatColor.GREEN + " a " + accountManager.format(accountManager.getBalance(args[1])) + " " + ConfigManager.CURRENCY);
							}
							else
							{
								sender.sendMessage(ChatColor.RED + "Joueur introuvable");
							}
							return null;
						}
					});

				}
				return true;
			}
			return true;
		}

		// Command called by Console
		if (sender instanceof ConsoleCommandSender)
		{
			if (args.length == 1)
			{
				sender.sendMessage(ChatColor.RED + "Veuillez spécifier un joueur.");
				return true;
			}
			if (args.length == 2)
			{
				queueManager.addToQueue(new Function()
				{
					@Override public Object apply(Object o)
					{
						if (accountManager.hasAccount(args[1]))
						{
							sender.sendMessage(ChatColor.AQUA + args[1] + ChatColor.GREEN + " a " + accountManager.format(accountManager.getBalance(args[1])) + " " + ConfigManager.CURRENCY);
						}
						else
						{
							sender.sendMessage(ChatColor.RED + "Joueur introuvable");
						}
						return null;
					}
				});

				return true;
			}
			return true;
		}
		// Command does not called by player or Console
		sender.sendMessage(ChatColor.DARK_RED + NKeconomy.PNAME + " An error has occured");
		Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + NKeconomy.PNAME + " An error has occured (Error#3.004)");
		return true;
	}
}

package be.noki_senpai.NKeconomy.cmd.Eco;

import be.noki_senpai.NKeconomy.cmd.EcoCmd;
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

public class Give
{
	private QueueManager queueManager = null;
	private AccountManager accountManager = null;

	public Give(QueueManager queueManager, AccountManager accountManager)
	{
		this.queueManager = queueManager;
		this.accountManager = accountManager;
	}

	public boolean give(CommandSender sender, String[] args)
	{
		// Command called by a player
		if(sender instanceof Player)
		{
			// If no more argument
			if(args.length == 1 || args.length == 2)
			{
				sender.sendMessage(ChatColor.GREEN + EcoCmd.usageCmdGive);
				return true;
			}
			if(args.length == 3)
			{
				// Check permission to display money amount of a player
				if(!(sender.hasPermission("*") || sender.hasPermission("nkeco.*") || sender.hasPermission("nkeco.give")
						|| sender.hasPermission("nkeco.admin")))
				{
					// Send that the player does not have the permission
					sender.sendMessage(ChatColor.RED + NKeconomy.PNAME + " Vous n'avez pas la permission !");
					return true;
				}
				else
				{
					if(CheckType.isNumber(args[2]))
					{
						queueManager.addToQueue(new Function()
						{
							@Override public Object apply(Object o)
							{
								if(accountManager.hasAccount(args[1]))
								{
									accountManager.giveAmount(args[1], Double.parseDouble(args[2]), false);
									if(Bukkit.getPlayer(args[1]) != null)
									{
										Bukkit.getPlayer(args[1]).sendMessage(
												ChatColor.GREEN + " Vous avez reçu " + accountManager.format(Double.parseDouble(args[2])) + " "
														+ ConfigManager.CURRENCY);
									}
									sender.sendMessage(
											ChatColor.AQUA + args[1] + ChatColor.GREEN + " a reçu " + args[2] + " " + ConfigManager.CURRENCY);
								}
								else
								{
									if(args[1].equals("*"))
									{
										accountManager.accounts.forEach((key, value) -> {
											value.addAmount(Double.parseDouble(args[2]));
											Bukkit.getPlayer(value.getPlayerUUID()).sendMessage(
													ChatColor.GREEN + " Vous avez reçu " + accountManager.format(Double.parseDouble(args[2])) + " "
															+ ConfigManager.CURRENCY);
											sender.sendMessage(ChatColor.AQUA + value.getPlayerName() + ChatColor.GREEN + " a reçu "
													+ accountManager.format(Double.parseDouble(args[2])) + " " + ConfigManager.CURRENCY);
										});
									}
									else
									{
										sender.sendMessage(ChatColor.RED + "Joueur introuvable");
									}
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
		if(sender instanceof ConsoleCommandSender)
		{
			// If no more argument
			if(args.length == 1 || args.length == 2)
			{
				sender.sendMessage(ChatColor.GREEN + EcoCmd.usageCmdGive);
				return true;
			}
			if(args.length == 3)
			{
				if(CheckType.isNumber(args[2]))
				{
					queueManager.addToQueue(new Function()
					{
						@Override public Object apply(Object o)
						{
							if(accountManager.hasAccount(args[1]))
							{
								accountManager.giveAmount(args[1], Double.parseDouble(args[2]), false);
								if(Bukkit.getPlayer(args[1]) != null)
								{
									Bukkit.getPlayer(args[1]).sendMessage(
											ChatColor.GREEN + " Vous avez reçu " + accountManager.format(Double.parseDouble(args[2])) + " "
													+ ConfigManager.CURRENCY);
								}
								sender.sendMessage(ChatColor.AQUA + args[1] + ChatColor.GREEN + " a reçu " + args[2] + " " + ConfigManager.CURRENCY);
							}
							else
							{
								if(args[1].equals("*"))
								{
									accountManager.accounts.forEach((key, value) -> {
										value.addAmount(Double.parseDouble(args[2]));
										Bukkit.getPlayer(value.getPlayerUUID()).sendMessage(
												ChatColor.GREEN + " Vous avez reçu " + accountManager.format(Double.parseDouble(args[2])) + " "
														+ ConfigManager.CURRENCY);
										sender.sendMessage(ChatColor.AQUA + value.getPlayerName() + ChatColor.GREEN + " a reçu "
												+ accountManager.format(Double.parseDouble(args[2])) + " " + ConfigManager.CURRENCY);
									});
								}
								else
								{
									sender.sendMessage(ChatColor.RED + "Joueur introuvable");
								}
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
			return true;
		}
		// Command does not called by player or Console
		sender.sendMessage(ChatColor.DARK_RED + NKeconomy.PNAME + " An error has occured");
		Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + NKeconomy.PNAME + " An error has occured (Error#3.004)");
		return true;
	}
}

package be.noki_senpai.NKeconomy.cmd;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import be.noki_senpai.NKeconomy.NKeconomy;
import be.noki_senpai.NKeconomy.utils.CheckType;

public class Take
{
	public static boolean take(CommandSender sender, String[] args)
	{
		// Command called by a player
		if (sender instanceof Player)
		{
			// If no more argument
			if (args.length == 1 || args.length == 2)
			{
				sender.sendMessage(ChatColor.GREEN + EcoCmd.usageCmdTake);
				return true;
			}
			if (args.length == 3)
			{
				// Check permission to display money amount of a player
				if (!(sender.hasPermission("*") || sender.hasPermission("nkeco.*") || sender.hasPermission("nkeco.take") || sender.hasPermission("nkeco.admin")))
				{
					// Send that the player does not have the permission
					sender.sendMessage(ChatColor.RED + NKeconomy.PNAME + " Vous n'avez pas la permission !");
					return true;
				}
				else
				{
					if (CheckType.isNumber(args[2]))
					{
						if (NKeconomy.hasAccount(args[1]))
						{
							if (NKeconomy.takeAmount(args[1], Double.parseDouble(args[2]), false))
							{
								if (Bukkit.getPlayer(args[1]) != null)
								{
									Bukkit.getPlayer(args[1]).sendMessage(ChatColor.GREEN + " Vous avez �t� prelev� de " + NKeconomy.format(Double.parseDouble(args[2])) + " " + NKeconomy.currency);
								}
								sender.sendMessage(ChatColor.AQUA + args[1] + ChatColor.GREEN + " a perdu " + NKeconomy.format(Double.parseDouble(args[2])) + " " + NKeconomy.currency);
							}
							else
							{
								sender.sendMessage(ChatColor.RED + "Le joueur n'a pas assez de " + NKeconomy.currency);
							}

						}
						else
						{
							sender.sendMessage(ChatColor.RED + "Joueur introuvable");
						}
					}
					else
					{
						sender.sendMessage(ChatColor.RED + "Le montant doit �tre un nombre");
					}
				}
			}
			return true;
		}

		// Command called by Console
		if (sender instanceof ConsoleCommandSender)
		{
			// If no more argument
			if (args.length == 1 || args.length == 2)
			{
				sender.sendMessage(ChatColor.GREEN + EcoCmd.usageCmdTake);
				return true;
			}
			if (args.length == 3)
			{
				if (CheckType.isNumber(args[2]))
				{
					if (NKeconomy.hasAccount(args[1]))
					{
						if (NKeconomy.takeAmount(args[1], Double.parseDouble(args[2]), false))
						{
							sender.sendMessage(ChatColor.AQUA + args[1] + ChatColor.GREEN + " a perdu " + NKeconomy.format(Double.parseDouble(args[2])) + " " + NKeconomy.currency);
						}
						else
						{
							if (NKeconomy.getBalance(args[1]) >= Double.parseDouble(args[2]))
							{
								sender.sendMessage(ChatColor.RED + "Le joueur n'a pas assez de " + NKeconomy.currency);
							}
							else
							{
								sender.sendMessage(ChatColor.DARK_RED + " " + args[1] + " est connect�(e) sur un autre serveur. Utilisez la console de ce serveur pour lui retirer des " + NKeconomy.currency);
							}

						}
					}
					else
					{
						sender.sendMessage(ChatColor.RED + "Joueur introuvable");
					}
				}
				else
				{
					sender.sendMessage(ChatColor.RED + "Le montant doit �tre un nombre");
				}
			}
			return true;
		}
		// Command does not called by player or Console
		sender.sendMessage(ChatColor.DARK_RED + NKeconomy.PNAME + " An error has occured");
		NKeconomy.getPlugin().getConsole().sendMessage(ChatColor.DARK_RED + NKeconomy.PNAME + " An error has occured (Error#3.004)");
		return true;
	}
}

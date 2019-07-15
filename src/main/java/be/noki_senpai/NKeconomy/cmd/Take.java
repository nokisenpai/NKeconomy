package be.noki_senpai.NKeconomy.cmd;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import be.noki_senpai.NKeconomy.EcoCmd;
import be.noki_senpai.NKeconomy.NKeconomy;
import be.noki_senpai.NKeconomy.data.Players;
import be.noki_senpai.NKeconomy.utils.CheckType;

public class Take
{
	public static boolean take(CommandSender sender, String[] args)
	{
		// Command called by a player
		if (sender instanceof Player) 
		{
			// If no more argument
			if(args.length == 1 || args.length == 2)
			{
				sender.sendMessage(ChatColor.GREEN + EcoCmd.usageCmdTake);
				return true;
			}
			if(args.length == 3)
			{
				// Check permission to display money amount of a player
				if(!(sender.hasPermission("*") || sender.hasPermission("nkeco.*") || sender.hasPermission("nkeco.take") || sender.hasPermission("nkeco.admin")))
				{
					// Send that the player does not have the permission
					sender.sendMessage(ChatColor.RED + NKeconomy.PName + " Vous n'avez pas la permission !");
					return true;
				}
				else
				{
					if(CheckType.isNumber(args[2]))
					{
						if(Players.hasAccount(args[1]))
						{
							if(NKeconomy.removeAmount(args[1], Double.parseDouble(args[2])))
							{
								if(Bukkit.getPlayer(args[1])!=null)
								{
									Bukkit.getPlayer(args[1]).sendMessage(ChatColor.GREEN + " Vous avez été prelevé de " + NKeconomy.format(Double.parseDouble(args[2])) + " " + NKeconomy.currency);
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
						sender.sendMessage(ChatColor.RED + "Le montant doit être un nombre");
					}
				}
			}
			return true;
		}
		
		
		// Command called by Console
		if (sender instanceof ConsoleCommandSender)
		{
			// If no more argument
			if(args.length == 1 || args.length == 2)
			{
				sender.sendMessage(ChatColor.GREEN + EcoCmd.usageCmdTake);
				return true;
			}
			if(args.length == 3)
			{
				if(CheckType.isNumber(args[2]))
				{
					if(Players.hasAccount(args[1]))
					{
						if(NKeconomy.removeAmount(args[1], Double.parseDouble(args[2])))
						{
							if(Bukkit.getPlayer(args[1])!=null)
							{
								Bukkit.getPlayer(args[1]).sendMessage(ChatColor.GREEN + " Vous avez été prelevé de " + NKeconomy.format(Double.parseDouble(args[2])) + " " + NKeconomy.currency);
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
					sender.sendMessage(ChatColor.RED + "Le montant doit être un nombre");
				}
			}
			return true;
		}
		// Command does not called by player or Console
		sender.sendMessage(ChatColor.DARK_RED + NKeconomy.PName + " An error has occured");
		NKeconomy.getInstance().getConsole().sendMessage(ChatColor.DARK_RED + NKeconomy.PName + " An error has occured (Error#3.004)");
		return true;
	}
}

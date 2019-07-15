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

public class Pay
{
	public static boolean pay(CommandSender sender, String[] args)
	{

		// Command called by a player
		if (sender instanceof Player) 
		{
			// If no more argument
			if(args.length == 1 || args.length == 2)
			{
				sender.sendMessage(ChatColor.GREEN + EcoCmd.usageCmdPay);
				return true;
			}
			if(args.length == 3)
			{
				// Display money amount of player
				// Check permission to display money amount of a player
				if(!(sender.hasPermission("*") || sender.hasPermission("nkeco.*") || sender.hasPermission("nkeco.pay") || sender.hasPermission("nkeco.user") || sender.hasPermission("nkeco.admin")))
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
							if(NKeconomy.removeAmount(sender.getName(), Double.parseDouble(args[2])))
							{
								NKeconomy.addAmount(args[1], Double.parseDouble(args[2]));
								if(Bukkit.getPlayer(args[1])!=null)
								{
									Bukkit.getPlayer(args[1]).sendMessage(ChatColor.AQUA + sender.getName() + ChatColor.GREEN + " vous avez donné " + NKeconomy.format(Double.parseDouble(args[2])) + " " + NKeconomy.currency);
								}
								sender.sendMessage(ChatColor.GREEN + "Vous avez donné " + NKeconomy.format(Double.parseDouble(args[2])) + " " + NKeconomy.currency + ChatColor.GREEN + " à " + ChatColor.AQUA + args[1]);
							}
							else
							{
								sender.sendMessage(ChatColor.RED + "Vous n'avez pas assez de " + NKeconomy.currency);
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
			sender.sendMessage(ChatColor.RED + "Dans la console, vous ne pouvez pas utiliser cette commande.");
			return true;
		}
		// Command does not called by player or Console
		sender.sendMessage(ChatColor.DARK_RED + NKeconomy.PName + " An error has occured");
		NKeconomy.getInstance().getConsole().sendMessage(ChatColor.DARK_RED + NKeconomy.PName + " An error has occured (Error#3.004)");
		return true;
	}
}

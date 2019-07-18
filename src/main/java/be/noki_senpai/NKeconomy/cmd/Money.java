package be.noki_senpai.NKeconomy.cmd;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import be.noki_senpai.NKeconomy.NKeconomy;

public class Money 
{	
	public static boolean money(CommandSender sender, String[] args)
	{

		// Command called by a player
		if (sender instanceof Player) 
		{
			// If no more argument
			if(args.length == 1)
			{
				if(!(sender.hasPermission("*") || sender.hasPermission("nkeco.*") || sender.hasPermission("nkeco.money") || sender.hasPermission("nkeco.user") || sender.hasPermission("nkeco.admin")))
				{
					// Send that the player does not have the permission
					sender.sendMessage(ChatColor.RED + " Vous n'avez pas la permission !");
					return true;
				}
				else
				{
					// Display amount money of sender
					sender.sendMessage(ChatColor.GREEN + "Vous avez " + NKeconomy.format(NKeconomy.accounts.get(sender.getName()).getAmount()) + " " + NKeconomy.currency);
				}
				
				return true;
			}
			if(args.length == 2)
			{
				// Display money amount of player
				// Check permission to display money amount of a player
				if(!(sender.hasPermission("*") || sender.hasPermission("nkeco.*") || sender.hasPermission("nkeco.money.other") || sender.hasPermission("nkeco.money.*") || sender.hasPermission("nkeco.admin")))
				{
					// Send that the player does not have the permission
					sender.sendMessage(ChatColor.RED + NKeconomy.PName + " Vous n'avez pas la permission !");
					return true;
				}
				else
				{
					if(NKeconomy.hasAccount(args[1]))
					{
						sender.sendMessage(ChatColor.AQUA + args[1] + ChatColor.GREEN + " a " + NKeconomy.format(NKeconomy.getBalance(args[1])) + " " + NKeconomy.currency);
					}
					else
					{
						sender.sendMessage(ChatColor.RED + "Joueur introuvable");
					}
				}
				return true;
			}
			return true;
		}
		
		
		// Command called by Console
		if (sender instanceof ConsoleCommandSender)
		{
			if(args.length == 1)
			{
				sender.sendMessage(ChatColor.RED + "Dans la console, veuillez specifier un joueur.");
				return true;
			}
			if(args.length == 2)
			{
				if(NKeconomy.hasAccount(args[1]))
				{
					sender.sendMessage(ChatColor.AQUA + args[1] + ChatColor.GREEN + " a " + NKeconomy.format(NKeconomy.getBalance(args[1])) + " " + NKeconomy.currency);
				}
				else
				{
					sender.sendMessage(ChatColor.RED + "Joueur introuvable");
				}

				return true;
			}
			return true;
		}
		// Command does not called by player or Console
		sender.sendMessage(ChatColor.DARK_RED + NKeconomy.PName + " An error has occured");
		NKeconomy.getInstance().getConsole().sendMessage(ChatColor.DARK_RED + NKeconomy.PName + " An error has occured (Error#3.004)");
		return true;
	}
}

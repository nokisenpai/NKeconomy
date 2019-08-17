package be.noki_senpai.NKeconomy.cmd;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import be.noki_senpai.NKeconomy.NKeconomy;
import be.noki_senpai.NKeconomy.utils.CheckType;

public class Top
{
	public static boolean top(CommandSender sender, String[] args)
	{
		int page = 1;
		String topList = null;
		// Command called by a player
		if (sender instanceof Player)
		{
			if (!(sender.hasPermission("*") || sender.hasPermission("nkeco.*") || sender.hasPermission("nkeco.top") || sender.hasPermission("nkeco.user") || sender.hasPermission("nkeco.admin")))
			{
				// Send that the player does not have the permission
				sender.sendMessage(ChatColor.RED + NKeconomy.PName + " Vous n'avez pas la permission !");
				return true;
			}
			else
			{
				// If no more argument
				if (args.length == 2)
				{
					// Display money amount of player
					// Check permission to display money amount of a player
					if (CheckType.isNumber(args[1]))
					{
						page = Integer.parseInt(args[1]);
						if (page == 0)
						{
							page = 1;
						}
					}
					else
					{
						sender.sendMessage(ChatColor.RED + "La page doit être un nombre");
						return true;
					}
				}

				LinkedHashMap<String, Double> topAmount = NKeconomy.topAmount(page);

				if (topAmount.size() == 0)
				{
					sender.sendMessage(ChatColor.RED + "Il n'y a personne à cette page du classement");
				}
				else
				{
					topList = ChatColor.GREEN + "---- Top " + NKeconomy.currency + ChatColor.GREEN + " ---- " + ChatColor.AQUA + ((page - 1) * 10 + 1) + ChatColor.GREEN + " à " + ChatColor.AQUA + (page * 10) + ChatColor.GREEN + " -----------------------------";

					int i = ((page - 1) * 10 + 1);
					String itsMe = "";
					for (Entry<String, Double> entry : NKeconomy.topAmount(page).entrySet())
					{
						itsMe = "";
						if (entry.getKey().equals(sender.getName()))
						{
							itsMe = ChatColor.GOLD + "" + ChatColor.BOLD + "> " + ChatColor.RESET;
						}
						if (i == 1)
						{
							topList += "\n" + ChatColor.GOLD + i + ". " + itsMe + ChatColor.GOLD + ChatColor.BOLD + entry.getKey() + "   " + NKeconomy.format(entry.getValue()) + " " + NKeconomy.currency;
						}
						else
						{
							topList += "\n" + ChatColor.GREEN + i + ". " + itsMe + ChatColor.GREEN + ChatColor.AQUA + entry.getKey() + "   " + NKeconomy.format(entry.getValue()) + " " + NKeconomy.currency;
						}
						i = i + 1;
					}

					sender.sendMessage(topList);
				}
			}

			return true;
		}

		// Command called by Console
		if (sender instanceof ConsoleCommandSender)
		{
			// If no more argument
			if (args.length == 2)
			{
				if (CheckType.isNumber(args[1]))
				{
					page = Integer.parseInt(args[1]);
					if (page == 0)
					{
						page = 1;
					}
				}
				else
				{
					sender.sendMessage(ChatColor.RED + "La page doit être un nombre");
					return true;
				}
			}
			topList = "\n" + ChatColor.GREEN + "Top " + NKeconomy.currency + " " + ChatColor.AQUA + ((page - 1) * 10 + 1) + ChatColor.GREEN + " => " + ChatColor.AQUA + (page * 10) + ChatColor.GREEN + "\n----------------------------------------------------";

			int i = ((page - 1) * 10 + 1);
			String itsMe = "";
			for (Entry<String, Double> entry : NKeconomy.topAmount(page).entrySet())
			{
				if (entry.getKey().equals(sender.getName()))
				{
					itsMe = "=> ";
				}
				if (i == 1)
				{
					topList += "\n" + ChatColor.GOLD + i + ". " + itsMe + ChatColor.BOLD + entry.getKey() + "   " + NKeconomy.format(entry.getValue()) + " " + NKeconomy.currency;
				}
				else
				{
					topList += "\n" + ChatColor.GREEN + i + ". " + itsMe + ChatColor.AQUA + entry.getKey() + "   " + NKeconomy.format(entry.getValue()) + " " + NKeconomy.currency;
				}
				i = i + 1;
			}
			topList += ChatColor.GREEN + "\n----------------------------------------------------";

			sender.sendMessage(topList);

			return true;
		}
		// Command does not called by player or Console
		sender.sendMessage(ChatColor.DARK_RED + NKeconomy.PName + " An error has occured");
		NKeconomy.getInstance().getConsole().sendMessage(ChatColor.DARK_RED + NKeconomy.PName + " An error has occured (Error#3.004)");
		return true;
	}
}

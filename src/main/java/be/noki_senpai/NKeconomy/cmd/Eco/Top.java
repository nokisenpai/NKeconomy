package be.noki_senpai.NKeconomy.cmd.Eco;

import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

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

public class Top
{
	private QueueManager queueManager = null;
	private AccountManager accountManager = null;

	public Top(QueueManager queueManager, AccountManager accountManager)
	{
		this.queueManager = queueManager;
		this.accountManager = accountManager;
	}

	public boolean top(CommandSender sender, String[] args)
	{
		int page = 1;

		if(!(sender.hasPermission("*") || sender.hasPermission("nkeco.*") || sender.hasPermission("nkeco.top") || sender.hasPermission("nkeco.user")
				|| sender.hasPermission("nkeco.admin")))
		{
			// Send that the player does not have the permission
			sender.sendMessage(ChatColor.RED + NKeconomy.PNAME + " Vous n'avez pas la permission !");
			return true;
		}
		else
		{
			// If no more argument
			if(args.length == 2)
			{
				// Display money amount of player
				// Check permission to display money amount of a player
				if(CheckType.isNumber(args[1]))
				{
					page = Integer.parseInt(args[1]);
					if(page == 0)
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

			int finalPage = page;
			queueManager.addToQueue(new Function()
			{
				@Override public Object apply(Object o)
				{
					String topList = null;
					Map<String, Double> topAmount = accountManager.topAmount(finalPage);

					if(topAmount.size() == 0)
					{
						sender.sendMessage(ChatColor.RED + "Il n'y a personne à cette page du classement");
					}
					else
					{

						topList = ChatColor.GREEN + "---- Top " + ConfigManager.CURRENCY + ChatColor.GREEN + " ---- " + ChatColor.AQUA + (
								(finalPage - 1) * 10 + 1) + ChatColor.GREEN + " à " + ChatColor.AQUA + (finalPage * 10) + ChatColor.GREEN
								+ " -----------------------------";

						int i = ((finalPage - 1) * 10 + 1);
						String itsMe = "";
						for(Entry<String, Double> entry : accountManager.topAmount(finalPage).entrySet())
						{
							itsMe = "";
							if(entry.getKey().equals(sender.getName()))
							{
								itsMe = ChatColor.GOLD + "" + ChatColor.BOLD + "> " + ChatColor.RESET;
							}
							if(i == 1)
							{
								topList += "\n" + ChatColor.GOLD + i + ". " + itsMe + ChatColor.GOLD + ChatColor.BOLD + entry.getKey() + "   "
										+ accountManager.format(entry.getValue()) + " " + ConfigManager.CURRENCY;
							}
							else
							{
								topList += "\n" + ChatColor.GREEN + i + ". " + itsMe + ChatColor.GREEN + ChatColor.AQUA + entry.getKey() + "   "
										+ accountManager.format(entry.getValue()) + " " + ConfigManager.CURRENCY;
							}
							i = i + 1;
						}

						sender.sendMessage(topList);
					}
					return null;
				}
			});
			return true;
		}
	}
}

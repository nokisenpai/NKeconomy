package be.noki_senpai.NKeconomy.cmd;

import be.noki_senpai.NKeconomy.cmd.Eco.*;
import be.noki_senpai.NKeconomy.managers.AccountManager;
import be.noki_senpai.NKeconomy.managers.QueueManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import be.noki_senpai.NKeconomy.NKeconomy;

public class EcoCmd implements CommandExecutor
{
	private QueueManager queueManager = null;
	private AccountManager accountManager = null;

	public final static String usageCmdMoney = "> /eco money";
	public final static String usageAdminCmdMoney = " " + ChatColor.BLUE + "[joueur]" + ChatColor.GREEN;

	public final static String usageCmdPay = "> /eco pay " + ChatColor.RED + "<joueur> " + ChatColor.RED + "<montant>" + ChatColor.GREEN;
	public final static String usageCmdGive = "> /eco give " + ChatColor.RED + "<joueur> " + ChatColor.RED + "<montant>" + ChatColor.GREEN;
	public final static String usageCmdTake = "> /eco take " + ChatColor.RED + "<joueur> " + ChatColor.RED + "<montant>" + ChatColor.GREEN;
	public final static String usageCmdSet = "> /eco set " + ChatColor.RED + "<joueur> " + ChatColor.RED + "<montant>" + ChatColor.GREEN;
	public final static String usageCmdTop = "> /eco top " + ChatColor.BLUE + "[page]" + ChatColor.GREEN;

	public EcoCmd(QueueManager queueManager, AccountManager accountManager)
	{
		this.queueManager = queueManager;
		this.accountManager = accountManager;
	}

	@Override
	public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args)
	{

		// if no argument
		if (args.length == 0)
		{
			sender.sendMessage(usageCmd(sender));
			return true;
		}

		args[0] = args[0].toLowerCase();
		switch (args[0])
		{
			case "money":
				return new Money(queueManager, accountManager).money(sender, args);
			case "pay":
				return new Pay(queueManager, accountManager).pay(sender, args);
			case "give":
				return new Give(queueManager, accountManager).give(sender, args);
			case "take":
				return new Take(queueManager, accountManager).take(sender, args);
			case "set":
				return new Set(queueManager, accountManager).set(sender, args);
			case "top":
				return new Top(queueManager, accountManager).top(sender, args);
			default:
				sender.sendMessage(usageCmd(sender));
				return true;
		}
	}

	public String usageCmd(CommandSender sender)
	{
		String usageCmd = "\n" + ChatColor.GREEN + "Liste des commandes pour " + ChatColor.RED + NKeconomy.PNAME + ChatColor.GREEN + "\n----------------------------------------------------";
		String cmdMoney = usageCmdMoney;

		if (sender.hasPermission("*") || sender.hasPermission("nkeco.*") || sender.hasPermission("nkeco.money.other") || sender.hasPermission("nkeco.admin"))
		{
			cmdMoney += usageAdminCmdMoney;
		}
		if (sender.hasPermission("*") || sender.hasPermission("nkeco.*") || sender.hasPermission("nkeco.user") || sender.hasPermission("nkeco.admin"))
		{
			usageCmd += "\n" + cmdMoney + "\n" + usageCmdPay + "\n" + usageCmdTop;
		}
		else
		{
			if (sender.hasPermission("nkeco.money"))
			{
				usageCmd += "\n" + cmdMoney;
			}
			if (sender.hasPermission("nkeco.pay"))
			{
				usageCmd += "\n" + usageCmdPay;
			}
			if (sender.hasPermission("nkeco.top"))
			{
				usageCmd += "\n" + usageCmdTop;
			}
		}

		if (sender.hasPermission("*") || sender.hasPermission("nkeco.*") || sender.hasPermission("nkeco.admin"))
		{
			usageCmd += "\n" + usageCmdGive + "\n" + usageCmdTake + "\n" + usageCmdSet;
		}
		else
		{
			if (sender.hasPermission("nkeco.give"))
			{
				usageCmd += "\n" + usageCmdGive;
			}
			if (sender.hasPermission("nkeco.take"))
			{
				usageCmd += "\n" + usageCmdTake;
			}
			if (sender.hasPermission("nkeco.set"))
			{
				usageCmd += "\n" + usageCmdSet;
			}
		}

		usageCmd += "\n----------------------------------------------------";

		return usageCmd;
	}
}

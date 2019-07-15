package be.noki_senpai.NKeconomy;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import be.noki_senpai.NKeconomy.cmd.Give;
import be.noki_senpai.NKeconomy.cmd.Money;
import be.noki_senpai.NKeconomy.cmd.Pay;
import be.noki_senpai.NKeconomy.cmd.Set;
import be.noki_senpai.NKeconomy.cmd.Take;
import be.noki_senpai.NKeconomy.cmd.Top;

public class EcoCmd implements CommandExecutor
{
	public final static String usageCmdMoney = "> /eco money";
	public final static String usageAdminCmdMoney = " " + ChatColor.BLUE+"[joueur]"+ ChatColor.GREEN;
	
	public final static String usageCmdPay = "> /eco pay "+ChatColor.RED+"<joueur> "+ChatColor.RED+"<montant>"+ ChatColor.GREEN;
	public final static String usageCmdGive = "> /eco give "+ChatColor.RED+"<joueur> "+ChatColor.RED+"<montant>"+ ChatColor.GREEN;
	public final static String usageCmdTake = "> /eco take "+ChatColor.RED+"<joueur> "+ChatColor.RED+"<montant>"+ ChatColor.GREEN;
	public final static String usageCmdSet = "> /eco set "+ChatColor.RED+"<joueur> "+ChatColor.RED+"<montant>"+ ChatColor.GREEN;
	public final static String usageCmdTop = "> /eco top "+ChatColor.BLUE+"[page]"+ ChatColor.GREEN;
	
	
	
	@Override
	public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) 
	{
				
		//if no argument
		if(args.length == 0)
		{
			sender.sendMessage(usageCmd(sender));
			return true;
		}
		
		args[0] = args[0].toLowerCase();
		switch(args[0])
		{
			case "money" : return Money.money(sender, args);
			case "pay" : return Pay.pay(sender, args);
			case "give" : return Give.give(sender, args);
			case "take" : return Take.take(sender, args);
			case "set" : return Set.set(sender, args);
			case "top" : return Top.top(sender, args);
			default : sender.sendMessage(usageCmd(sender));
			return true;
		}
	}
	
	public String usageCmd(CommandSender sender)
	{
		String usageCmd = "\n" + ChatColor.GREEN + "Liste des commandes pour " + ChatColor.RED + NKeconomy.PName + ChatColor.GREEN
				+ "\n----------------------------------------------------";
		String cmdMoney = usageCmdMoney;

		if(sender.hasPermission("*") || sender.hasPermission("nkeco.*") || sender.hasPermission("nkeco.money.other") || sender.hasPermission("nkeco.admin"))
		{
			cmdMoney += usageAdminCmdMoney;
		}
		if(sender.hasPermission("*") || sender.hasPermission("nkeco.*") || sender.hasPermission("nkeco.user") || sender.hasPermission("nkeco.admin"))
		{
			usageCmd += "\n" + cmdMoney
					+ "\n" + usageCmdPay
					+ "\n" + usageCmdTop;
		}
		else
		{
			if(sender.hasPermission("nkeco.money"))
			{
				usageCmd += "\n" + cmdMoney;
			}
			if(sender.hasPermission("nkeco.pay"))
			{
				usageCmd += "\n" + usageCmdPay;
			}
			if(sender.hasPermission("nkeco.top"))
			{
				usageCmd += "\n" + usageCmdTop;
			}
		}
		
		if(sender.hasPermission("*") || sender.hasPermission("nkeco.*") || sender.hasPermission("nkeco.admin"))
		{
			usageCmd += "\n" + usageCmdGive
					+ "\n" + usageCmdTake
					+ "\n" + usageCmdSet;
		}
		else
		{
			if(sender.hasPermission("nkeco.give"))
			{
				usageCmd += "\n" + usageCmdGive;
			}
			if(sender.hasPermission("nkeco.take"))
			{
				usageCmd += "\n" + usageCmdTake;
			}
			if(sender.hasPermission("nkeco.set"))
			{
				usageCmd += "\n" + usageCmdSet;
			}
		}

		usageCmd += "\n----------------------------------------------------";
		
		return usageCmd;
	}
}

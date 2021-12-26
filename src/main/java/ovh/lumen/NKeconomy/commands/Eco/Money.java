package ovh.lumen.NKeconomy.commands.Eco;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import ovh.lumen.NKeconomy.data.NKData;
import ovh.lumen.NKeconomy.enums.Messages;
import ovh.lumen.NKeconomy.enums.Permissions;
import ovh.lumen.NKeconomy.interfaces.SubCommand;
import ovh.lumen.NKeconomy.managers.AccountManager;
import ovh.lumen.NKeconomy.managers.AsyncQueueManager;
import ovh.lumen.NKeconomy.utils.MessageParser;

public class Money implements SubCommand
{
	public Money() {}

	@Override
	public boolean execute(CommandSender sender, String[] args)
	{
		if(sender instanceof Player)
		{
			if(args.length == 1)
			{
				if(!hasMoneyPermissions(sender))
				{
					sender.sendMessage(Messages.PERMISSION_MISSING.toString());
					return true;
				}

				MessageParser messageParser = new MessageParser(Messages.ECO_MONEY_SELF_MSG.toString());
				messageParser.addArg(AccountManager.format(AccountManager.getAccount(sender.getName()).getAmount()));
				messageParser.addArg(NKData.CURRENCY);

				sender.sendMessage(messageParser.parse());
				return true;
			}

			if(!hasMoneyOtherPermissions(sender))
			{
				sender.sendMessage(Messages.PERMISSION_MISSING.toString());
				return true;
			}

			AsyncQueueManager.addToQueue(o ->
			{
				if(AccountManager.hasAccount(args[1]))
				{
					MessageParser messageParser = new MessageParser(Messages.ECO_MONEY_OTHER_MSG.toString());
					messageParser.addArg(args[1]);
					messageParser.addArg(AccountManager.format(AccountManager.getAccount(sender.getName()).getAmount()));
					messageParser.addArg(NKData.CURRENCY);

					sender.sendMessage(messageParser.parse());
				}
				else
				{
					sender.sendMessage(Messages.UNKNOWN_PLAYER.toString());
				}

				return null;
			});

			return true;
		}

		if(sender instanceof ConsoleCommandSender)
		{
			if(args.length == 1)
			{
				sender.sendMessage(Messages.ECO_MONEY_CONSOLE_NO_ARG_MSG.toString());
				return true;
			}

			AsyncQueueManager.addToQueue(o ->
			{
				if(AccountManager.hasAccount(args[1]))
				{
					MessageParser messageParser = new MessageParser(Messages.ECO_MONEY_OTHER_MSG.toString());
					messageParser.addArg(args[1]);
					messageParser.addArg(AccountManager.format(AccountManager.getAccount(sender.getName()).getAmount()));
					messageParser.addArg(NKData.CURRENCY);

					sender.sendMessage(messageParser.parse());
				}
				else
				{
					sender.sendMessage(Messages.UNKNOWN_PLAYER.toString());
				}
				return null;
			});

			return true;
		}

		return true;
	}

	private boolean hasMoneyPermissions(CommandSender sender)
	{
		return sender.hasPermission(Permissions.ECO_MONEY_CMD.toString()) ||
				sender.hasPermission(Permissions.USER.toString()) ||
				sender.hasPermission(Permissions.ADMIN.toString());
	}

	private boolean hasMoneyOtherPermissions(CommandSender sender)
	{
		return sender.hasPermission(Permissions.ECO_MONEY_OTHER_CMD.toString()) ||
				sender.hasPermission(Permissions.ADMIN.toString());
	}
}

package ovh.lumen.NKeconomy.commands.Eco;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import ovh.lumen.NKeconomy.data.NKData;
import ovh.lumen.NKeconomy.enums.Messages;
import ovh.lumen.NKeconomy.enums.Permissions;
import ovh.lumen.NKeconomy.enums.Usages;
import ovh.lumen.NKeconomy.interfaces.SubCommand;
import ovh.lumen.NKeconomy.managers.AccountManager;
import ovh.lumen.NKeconomy.managers.AsyncQueueManager;
import ovh.lumen.NKeconomy.utils.CheckType;
import ovh.lumen.NKeconomy.utils.MessageParser;

public class Pay implements SubCommand
{
	public Pay() {}

	@Override
	public boolean execute(CommandSender sender, String[] args)
	{
		if(sender instanceof Player)
		{
			if(!hasPayPermissions(sender))
			{
				sender.sendMessage(Messages.PERMISSION_MISSING.toString());
				return true;
			}

			if(args.length < 3)
			{
				sender.sendMessage(Usages.ECO_PAY_CMD.toString());
				return true;
			}

			if(!CheckType.isNumber(args[2]))
			{
				sender.sendMessage(Messages.ARG_MUST_BE_NUMBER_MSG.toString());
				return true;
			}

			double amount = Double.parseDouble(args[2]);

			if(amount < 0)
			{
				sender.sendMessage(Messages.ARG_MUST_BE_POSITIVE_NUMBER_MSG.toString());
				return true;
			}

			AsyncQueueManager.addToQueue(o ->
			{
				if(AccountManager.hasAccount(args[1]))
				{
					if(AccountManager.takeAmount(sender.getName(), amount))
					{
						AccountManager.payAmount(args[1], amount, sender.getName());

						MessageParser messageParser = new MessageParser(Messages.ECO_PAY_SENDER_NOTIFY.toString());
						messageParser.addArg(AccountManager.format(amount));
						messageParser.addArg(NKData.CURRENCY);
						messageParser.addArg(args[1]);

						sender.sendMessage(messageParser.parse());
					}
					else
					{
						MessageParser messageParser = new MessageParser(Messages.ECO_NOT_ENOUGH_MONEY.toString());
						messageParser.addArg(NKData.CURRENCY);

						sender.sendMessage(messageParser.parse());
					}
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
			sender.sendMessage(Messages.NO_CONSOLE_USE.toString());
			return true;
		}

		return true;
	}

	private boolean hasPayPermissions(CommandSender sender)
	{
		return sender.hasPermission(Permissions.ECO_PAY_CMD.toString()) ||
				sender.hasPermission(Permissions.USER.toString()) ||
				sender.hasPermission(Permissions.ADMIN.toString());
	}
}

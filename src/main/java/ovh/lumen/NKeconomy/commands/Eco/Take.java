package ovh.lumen.NKeconomy.commands.Eco;

import org.bukkit.command.CommandSender;
import ovh.lumen.NKeconomy.data.NKData;
import ovh.lumen.NKeconomy.enums.Messages;
import ovh.lumen.NKeconomy.enums.Permissions;
import ovh.lumen.NKeconomy.enums.Usages;
import ovh.lumen.NKeconomy.interfaces.SubCommand;
import ovh.lumen.NKeconomy.managers.AccountManager;
import ovh.lumen.NKeconomy.managers.AsyncQueueManager;
import ovh.lumen.NKeconomy.utils.CheckType;
import ovh.lumen.NKeconomy.utils.MessageParser;

public class Take implements SubCommand
{
	public Take() {}

	@Override
	public boolean execute(CommandSender sender, String[] args)
	{
		if(!hasTakePermissions(sender))
		{
			sender.sendMessage(Messages.PERMISSION_MISSING.toString());
			return true;
		}

		if(args.length < 3)
		{
			sender.sendMessage(Usages.ECO_SET_CMD.toString());
			return true;
		}

		if(!CheckType.isNumber(args[2]))
		{
			sender.sendMessage(Messages.ARG_MUST_BE_NUMBER_MSG.toString());
			return true;
		}

		double amount = Double.parseDouble(args[2]);

		AsyncQueueManager.addToQueue(o ->
		{
			if(AccountManager.hasAccount(args[1]))
			{
				if(AccountManager.takeAmount(args[1], Double.parseDouble(args[2])))
				{
					MessageParser messageParser = new MessageParser(Messages.ECO_TAKE_SENDER_NOTIFY.toString());
					messageParser.addArg(args[1]);
					messageParser.addArg(AccountManager.format(amount));
					messageParser.addArg(NKData.CURRENCY);

					sender.sendMessage(messageParser.parse());
				}
				else
				{
					MessageParser messageParser = new MessageParser(Messages.ECO_TARGET_NOT_ENOUGH_MONEY.toString());
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

	private boolean hasTakePermissions(CommandSender sender)
	{
		return sender.hasPermission(Permissions.ECO_TAKE_CMD.toString()) ||
				sender.hasPermission(Permissions.ADMIN.toString());
	}
}

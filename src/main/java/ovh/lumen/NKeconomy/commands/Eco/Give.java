package ovh.lumen.NKeconomy.commands.Eco;

import org.bukkit.command.CommandSender;
import ovh.lumen.NKeconomy.data.NKData;
import ovh.lumen.NKeconomy.enums.Messages;
import ovh.lumen.NKeconomy.enums.Permissions;
import ovh.lumen.NKeconomy.enums.Usages;
import ovh.lumen.NKeconomy.interfaces.SubCommand;
import ovh.lumen.NKeconomy.managers.AccountManager;
import ovh.lumen.NKeconomy.managers.AsyncQueueManager;
import ovh.lumen.NKeconomy.managers.NKcoreAPIManager;
import ovh.lumen.NKeconomy.utils.CheckType;
import ovh.lumen.NKeconomy.utils.MessageParser;

public class Give implements SubCommand
{
	public Give() {}

	@Override
	public boolean execute(CommandSender sender, String[] args)
	{
		if(!hasGivePermissions(sender))
		{
			sender.sendMessage(Messages.PERMISSION_MISSING.toString());
			return true;
		}

		if(args.length < 3)
		{
			sender.sendMessage(Usages.ECO_GIVE_CMD.toString());
			return true;
		}

		if(!CheckType.isNumber(args[2]))
		{
			sender.sendMessage(Messages.ARG_MUST_BE_NUMBER_MSG.toString());
			return true;
		}

		AsyncQueueManager.addToQueue(o ->
		{
			double amount = Double.parseDouble(args[2]);
			if(args[1].equals("*"))
			{
				if(NKData.ENABLE_CROSS_SERVER)
				{
					String data = "give|*|" + amount + "|null";
					NKcoreAPIManager.nKcoreAPI.broadcastNetworkData(NKData.PLUGIN_NAME, data ,NKData.PLUGIN_NAME);
				}
				else
				{
					NKData.ACCOUNTS.keySet().forEach(playerName -> AccountManager.giveAmount(playerName, amount));
				}

				sendSenderNotify(sender, amount);

				return null;
			}
			if(AccountManager.hasAccount(args[1]))
			{
				AccountManager.giveAmount(args[1], amount);
				sendSenderNotify(sender, amount);
			}
			else
			{
				sender.sendMessage(Messages.UNKNOWN_PLAYER.toString());
			}

			return null;
		});

		return true;
	}

	private boolean hasGivePermissions(CommandSender sender)
	{
		return sender.hasPermission(Permissions.ECO_GIVE_CMD.toString()) ||
				sender.hasPermission(Permissions.ADMIN.toString());
	}

	private void sendSenderNotify(CommandSender sender, double amount)
	{
		MessageParser messageParser = new MessageParser(Messages.ECO_GIVE_SENDER_NOTIFY.toString());
		messageParser.addArg(AccountManager.format(amount));
		messageParser.addArg(NKData.CURRENCY);

		sender.sendMessage(messageParser.parse());
	}
}

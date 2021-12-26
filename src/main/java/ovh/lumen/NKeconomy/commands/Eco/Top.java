package ovh.lumen.NKeconomy.commands.Eco;

import org.bukkit.command.CommandSender;
import ovh.lumen.NKeconomy.data.NKData;
import ovh.lumen.NKeconomy.enums.Messages;
import ovh.lumen.NKeconomy.enums.Permissions;
import ovh.lumen.NKeconomy.interfaces.SubCommand;
import ovh.lumen.NKeconomy.managers.AccountManager;
import ovh.lumen.NKeconomy.managers.AsyncQueueManager;
import ovh.lumen.NKeconomy.utils.CheckType;
import ovh.lumen.NKeconomy.utils.MessageParser;

import java.util.Map;
import java.util.Map.Entry;

public class Top implements SubCommand
{
	public Top() {}

	@Override
	public boolean execute(CommandSender sender, String[] args)
	{
		int page = 1;

		if(!hasTopPermissions(sender))
		{
			sender.sendMessage(Messages.PERMISSION_MISSING.toString());
			return true;
		}

		if(args.length >= 2)
		{
			if(!CheckType.isNumber(args[1]))
			{
				sender.sendMessage(Messages.ARG_MUST_BE_NUMBER_MSG.toString());
				return true;
			}

			page = Integer.parseInt(args[1]);
			if(page == 0)
			{
				page = 1;
			}
		}

		int finalPage = page;
		AsyncQueueManager.addToQueue(o ->
		{
			Map<String, Double> topAmount = AccountManager.topAmount(finalPage);

			if(topAmount.size() == 0)
			{
				sender.sendMessage(Messages.ECO_TOP_NOBODY.toString());
				return null;
			}

			String topList = getTopTitle(finalPage);

			int i = ((finalPage - 1) * 10 + 1);
			String itsMe;
			for(Entry<String, Double> entry : topAmount.entrySet())
			{
				itsMe = "";
				if(entry.getKey().equals(sender.getName()))
				{
					itsMe = Messages.ECO_TOP_ITS_ME.toString();
				}
				if(i == 1)
				{
					topList += getTop1(entry, i, itsMe);
				}
				else
				{
					topList += getLine(entry, i, itsMe);
				}
				i = i + 1;
			}

			sender.sendMessage(topList);
			return null;
		});

		return true;
	}

	private boolean hasTopPermissions(CommandSender sender)
	{
		return sender.hasPermission(Permissions.ECO_TOP_CMD.toString()) ||
				sender.hasPermission(Permissions.USER.toString()) ||
				sender.hasPermission(Permissions.ADMIN.toString());
	}

	private String getTopTitle(int finalPage)
	{
		MessageParser messageParser = new MessageParser(Messages.ECO_TOP_TITLE.toString());
		messageParser.addArg(NKData.CURRENCY);
		messageParser.addArg(String.valueOf((finalPage - 1) * 10 + 1));
		messageParser.addArg(String.valueOf(finalPage * 10));

		return messageParser.parse();
	}

	private String getTop1(Entry<String, Double> entry, int i, String itsMe)
	{
		MessageParser messageParser = new MessageParser(Messages.ECO_TOP_1.toString());
		messageParser.addArg(String.valueOf(i));
		messageParser.addArg(itsMe);
		messageParser.addArg(entry.getKey());
		messageParser.addArg(AccountManager.format(entry.getValue()));
		messageParser.addArg(NKData.CURRENCY);

		return messageParser.parse();
	}

	private String getLine(Entry<String, Double> entry, int i, String itsMe)
	{
		MessageParser messageParser = new MessageParser(Messages.ECO_TOP_LINE.toString());
		messageParser.addArg(String.valueOf(i));
		messageParser.addArg(itsMe);
		messageParser.addArg(entry.getKey());
		messageParser.addArg(AccountManager.format(entry.getValue()));
		messageParser.addArg(NKData.CURRENCY);

		return messageParser.parse();
	}
}

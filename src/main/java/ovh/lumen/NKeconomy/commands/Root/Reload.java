package ovh.lumen.NKeconomy.commands.Root;

import org.bukkit.command.CommandSender;
import ovh.lumen.NKeconomy.data.NKData;
import ovh.lumen.NKeconomy.enums.Messages;
import ovh.lumen.NKeconomy.enums.Permissions;
import ovh.lumen.NKeconomy.interfaces.SubCommand;
import ovh.lumen.NKeconomy.utils.MessageParser;

public class Reload implements SubCommand
{
	public Reload() {}

	public boolean execute(CommandSender sender, String[] args)
	{
		if(!hasReloadPermissions(sender))
		{
			sender.sendMessage(Messages.PERMISSION_MISSING.toString());

			return true;
		}

		NKData.PLUGIN.reload();

		MessageParser messageParser = new MessageParser(Messages.ROOT_RELOAD_MSG.toString());
		messageParser.addArg(NKData.PLUGIN_NAME);

		sender.sendMessage(messageParser.parse());

		return true;
	}

	private boolean hasReloadPermissions(CommandSender sender)
	{
		return sender.hasPermission(Permissions.ROOT_RELOAD_CMD.toString()) ||
				sender.hasPermission(Permissions.ADMIN.toString());
	}
}

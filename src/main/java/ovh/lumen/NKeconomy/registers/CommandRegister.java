package ovh.lumen.NKeconomy.registers;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import ovh.lumen.NKeconomy.commands.EcoCmd;
import ovh.lumen.NKeconomy.commands.RootCmd;
import ovh.lumen.NKeconomy.data.NKData;
import ovh.lumen.NKeconomy.enums.InternalMessages;
import ovh.lumen.NKeconomy.utils.MessageParser;
import ovh.lumen.NKeconomy.utils.NKLogger;

import java.util.HashMap;
import java.util.Map;

public class CommandRegister
{
	private static final Map<String, CommandExecutor> commands = setCommands();

	public static void registerAllCommands(JavaPlugin plugin)
	{
		commands.forEach((commandName, commandExecutor) ->
		{
			PluginCommand command = plugin.getCommand(commandName);
			if(command != null)
			{
				command.setExecutor(commandExecutor);
			}
			else
			{
				MessageParser messageParser = new MessageParser(InternalMessages.REGISTER_COMMAND_FAIL.toString());
				messageParser.addArg(commandName);

				NKLogger.warn(messageParser.parse());
			}
		});
	}

	private static Map<String, CommandExecutor> setCommands()
	{
		Map<String, CommandExecutor> commands = new HashMap<>();
		commands.put(NKData.PLUGIN_NAME, new RootCmd());
		commands.put("eco", new EcoCmd());

		return commands;
	}
}

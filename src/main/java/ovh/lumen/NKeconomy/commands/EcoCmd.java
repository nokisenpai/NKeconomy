package ovh.lumen.NKeconomy.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import ovh.lumen.NKeconomy.commands.Eco.*;
import ovh.lumen.NKeconomy.enums.Usages;

public class EcoCmd implements CommandExecutor
{
	public EcoCmd() {}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args)
	{
		if (args.length == 0)
		{
			sender.sendMessage(Usages.ECO_CMD.toString());
			return true;
		}

		args[0] = args[0].toLowerCase();
		switch (args[0])
		{
			case "money":
				return new Money().execute(sender, args);
			case "pay":
				return new Pay().execute(sender, args);
			case "give":
				return new Give().execute(sender, args);
			case "take":
				return new Take().execute(sender, args);
			case "set":
				return new Set().execute(sender, args);
			case "top":
				return new Top().execute(sender, args);
			default:
				sender.sendMessage(Usages.ECO_CMD.toString());
				return true;
		}
	}
}

package ovh.lumen.NKeconomy.completers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

public class EcoCompleter implements TabCompleter
{
	List<String> COMMANDS = Arrays.asList("money", "pay", "top", "give", "take", "set");

	@Override
	public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args)
	{
		if (args.length == 1)
		{
			final List<String> completions = new ArrayList<>();

			org.bukkit.util.StringUtil.copyPartialMatches(args[0], COMMANDS, completions);
			Collections.sort(completions);

			return completions;
		}
		return null;
	}
}

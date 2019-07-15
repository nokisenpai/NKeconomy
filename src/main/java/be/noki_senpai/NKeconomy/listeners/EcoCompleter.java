package be.noki_senpai.NKeconomy.listeners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class EcoCompleter implements TabCompleter 
{
    List<String> COMMANDS = Arrays.asList("money", "pay", "top", "give", "take", "set");

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) 
    {
    	if(args.length==1)
    	{
	        final List<String> completions = new ArrayList<>();
	        //copy matches of first argument from list (ex: if first arg is 'm' will return just 'minecraft')
	        org.bukkit.util.StringUtil.copyPartialMatches(args[0], COMMANDS, completions);
	        //sort the list
	        Collections.sort(completions);
	        return completions;
    	}
    	return null;
    }
}

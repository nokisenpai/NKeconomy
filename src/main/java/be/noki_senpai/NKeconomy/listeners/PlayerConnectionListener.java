package be.noki_senpai.NKeconomy.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import be.noki_senpai.NKeconomy.NKeconomy;
import be.noki_senpai.NKeconomy.data.Accounts;;



public class PlayerConnectionListener implements Listener 
{
    @EventHandler
    public void PlayerJoinEvent(final PlayerJoinEvent event) 
    {
    	NKeconomy.accounts.putIfAbsent(event.getPlayer().getName(),new Accounts(event.getPlayer().getUniqueId()));
    	NKeconomy.addOtherServer(event.getPlayer().getName());
    }

    @EventHandler
    public void onPlayerQuitEvent(final PlayerQuitEvent event) 
    {	
    	NKeconomy.accounts.get(Bukkit.getOfflinePlayer(event.getPlayer().getUniqueId()).getName()).save();
    	NKeconomy.accounts.remove(Bukkit.getOfflinePlayer(event.getPlayer().getUniqueId()).getName());
    	NKeconomy.removeOtherServer(event.getPlayer().getName());
    }
}

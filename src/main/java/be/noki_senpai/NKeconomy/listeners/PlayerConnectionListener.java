package be.noki_senpai.NKeconomy.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import be.noki_senpai.NKeconomy.NKeconomy;
import be.noki_senpai.NKeconomy.data.Players;;



public class PlayerConnectionListener implements Listener 
{
    @EventHandler
    public void PlayerJoinEvent(final PlayerJoinEvent event) 
    {
    	NKeconomy.players.putIfAbsent(event.getPlayer().getDisplayName(),new Players(event.getPlayer().getUniqueId()));
    }

    @EventHandler
    public void onPlayerQuitEvent(final PlayerQuitEvent event) 
    {	
    	NKeconomy.players.get(Bukkit.getOfflinePlayer(event.getPlayer().getUniqueId()).getName()).save();
    	NKeconomy.players.remove(Bukkit.getOfflinePlayer(event.getPlayer().getUniqueId()).getName());
    }

    /*@EventHandler
    public void onCooldownEnd(CooldownEndEvent event) 
    {
        if (!event.getKey().equals(JobsMain.getInstance().getName() + "-updateJobs")) return;

        JobPlayer jobPlayer = JobsMain.getInstance().players.get(event.getPlayer().getUniqueId());
        if (jobPlayer != null) jobPlayer.save();
    }*/
}

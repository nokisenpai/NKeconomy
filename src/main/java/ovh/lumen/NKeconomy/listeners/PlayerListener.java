package ovh.lumen.NKeconomy.listeners;

import ovh.lumen.NKeconomy.managers.AccountManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import ovh.lumen.NKeconomy.managers.AsyncQueueManager;

public class PlayerListener implements Listener
{
	public PlayerListener() {}

	@EventHandler
	public void PlayerJoinEvent(final PlayerJoinEvent event)
	{
		AsyncQueueManager.addToQueue(o ->
		{
			AccountManager.addAccount(event.getPlayer());
			return null;
		});
	}

	@EventHandler
	public void onPlayerQuitEvent(final PlayerQuitEvent event)
	{
		String playerName = event.getPlayer().getName();
		AsyncQueueManager.addToQueue(o ->
		{
			AccountManager.getAccount(playerName).save();
			AccountManager.removeAccount(playerName);
			return null;
		});
	}
}

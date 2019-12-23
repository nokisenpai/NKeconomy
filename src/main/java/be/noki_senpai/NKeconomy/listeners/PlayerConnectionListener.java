package be.noki_senpai.NKeconomy.listeners;

import be.noki_senpai.NKeconomy.NKeconomy;
import be.noki_senpai.NKeconomy.managers.AccountManager;
import be.noki_senpai.NKeconomy.managers.QueueManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.function.Function;

;

public class PlayerConnectionListener implements Listener
{
	private AccountManager accountManager = null;
	private QueueManager queueManager = null;

	public PlayerConnectionListener(QueueManager queueManager, AccountManager accountManager)
	{
		this.accountManager = accountManager;
		this.queueManager = queueManager;
	}

	@EventHandler
	public void PlayerJoinEvent(final PlayerJoinEvent event)
	{
		new BukkitRunnable()
		{
			@Override public void run()
			{
				accountManager.addAccount(event.getPlayer());
			}
		}.runTaskLaterAsynchronously(NKeconomy.getPlugin(), 20);
	}

	@EventHandler
	public void onPlayerQuitEvent(final PlayerQuitEvent event)
	{
		String playerName = event.getPlayer().getName();
		queueManager.addToQueue(new Function()
		{
			@Override public Object apply(Object o)
			{
				accountManager.getAccount(playerName).save(queueManager);
				accountManager.delAccount(playerName);
				return null;
			}
		});
	}
}

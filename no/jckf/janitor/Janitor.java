package no.jckf.janitor;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Janitor extends JavaPlugin implements Listener {
	private boolean enabled;

	public void onEnable() {
		enabled = getConfig().getBoolean("enabled",false);

		getServer().getPluginManager().registerEvents(this,this);

		getServer().getScheduler().scheduleSyncRepeatingTask(this,new Runnable() {
			public void run() {
				if (enabled) {
					for (Player p : getServer().getOnlinePlayers()) {
						p.sendMessage(ChatColor.RED + "Reminder: Maintenance mode is enabled.");
					}
				}
			}
		},0,1200);
	}

	public void onDisable() {

	}

	public boolean onCommand(CommandSender sender,Command command,String label,String[] args) {
		if (!sender.isOp()) {
			return true;
		}

		if (args.length == 0 || args.length > 1) {
			sender.sendMessage("Maintenance mode is " + (enabled ? "enabled" : "disabled") + ".");
			return false;
		}

		if (args[0].equalsIgnoreCase("on")) {
			enabled = true;

			for (Player p : getServer().getOnlinePlayers()) {
				if (!p.isOp()) {
					p.kickPlayer("The server is going to maintenance mode.");
				}
			}

			sender.sendMessage("Maintenance mode is now enabled.");
		} else if (args[0].equalsIgnoreCase("off")) {
			enabled = false;
			sender.sendMessage("Maintenance mode is now disabled.");
		}

		getConfig().set("enabled",enabled);
		saveConfig();

		return true;
	}

	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent event) {
		if (enabled && !event.getPlayer().isOp()) {
			event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
			event.setKickMessage("The server is in maintenance mode.");
		}
	}
}

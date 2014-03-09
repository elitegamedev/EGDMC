package dev.elitegame.egdmc;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Main extends JavaPlugin implements Listener {

	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		saveDefaultConfig();
		getLogger().info("Welcome to EliteGameDevMC!");
		getLogger().info("This plugin handles everything in this server.");
		getLogger().info("If removed, the server will not work.");
		List<String> help = getConfig().getStringList("help");
		help.removeAll(help);
		getConfig().set("help", help);
		saveConfig();
		// "/command: Description"
		help.add("/help: Displays this message");
		help.add("/sversion - /sv: Tells the version of the server");
		help.add("/admin: WIP! Admin commands");
		getConfig().set("help", help);
		saveConfig();
		getLogger().info("The plugin has started!");
	}

	@Override
	public void onDisable() {
		saveDefaultConfig();
		getLogger().info("Server now shutting down.");
	}

	@EventHandler
	public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
		Player player = event.getPlayer();
		if (player.getGameMode() == GameMode.CREATIVE) {
			return;
		}
		event.setCancelled(true);
		player.setAllowFlight(false);
		player.setFlying(false);
		player.setVelocity(player.getLocation().getDirection().multiply(1.5).setY(1));
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if ((player.getGameMode()!=GameMode.CREATIVE)&&(player.getLocation().subtract(0, 1, 0).getBlock().getType()!=Material.AIR)&&(!player.isFlying())) {
			player.setAllowFlight(true);
		}
			
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		Player player = (Player) sender;
		if (sender instanceof Player) {
			if (label.equalsIgnoreCase("help")) {
				sendHelp(player);
			} else if (label.equalsIgnoreCase("sversion") || label.equalsIgnoreCase("sv")) {
				PluginDescriptionFile pdfFile = this.getDescription();
				String version = pdfFile.getVersion();
				player.sendMessage(ChatColor.RED + "This server is currently at v" + version + "!");
			} else if (label.equalsIgnoreCase("admin")) {
				if (player.hasPermission("egdmc.admin") || player.isOp()) {
					if (args.length == 1) {
						if (args[0].equalsIgnoreCase("time")) {
							player.sendMessage("Usage: /admin time <time>");
						}
					} else if (args.length == 2) {
						if (args[0].equalsIgnoreCase("time")) {
							if (args[1].isEmpty()) {
								player.sendMessage("You must enter a time! (day, night, mid)");
							} else {
								if (args[1].equalsIgnoreCase("day")) {
									List<World> worlds = getServer().getWorlds();
									long day = 0;
									for (World w : worlds) {
										w.setTime(day);
									}
								} else if (args[1].equalsIgnoreCase("night")) {
									List<World> worlds = getServer().getWorlds();
									long night = 12500;
									for (World w : worlds) {
										w.setTime(night);
									}
								} else if (args[1].equalsIgnoreCase("mid")) {
									List<World> worlds = getServer().getWorlds();
									long mid = 6250;
									for (World w : worlds) {
										w.setTime(mid);
									}
								}
							}
						}
					}
				} else {
					player.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
				}
			} else if (label.equalsIgnoreCase("kickall")) {
				Player[] players = getServer().getOnlinePlayers();
				player.sendMessage(ChatColor.RED + "Kicking all players...");
				for (Player p : players) {
					p.kickPlayer(ChatColor.BLUE + "An Admin just kicked everyone off the server!");
				}
			} else if (label.equalsIgnoreCase("pickup")) {
				if (args.length == 0) {
					player.sendMessage(ChatColor.RED + "Usage: /pickup on/off");
				} else if (args.length == 1) {
					if (args[0].equalsIgnoreCase("on")) {
						player.setCanPickupItems(true);
						player.sendMessage(ChatColor.GREEN + "Pickups Enabled!");
					} else if (args[0].equalsIgnoreCase("off")) {
						player.setCanPickupItems(false);
						player.sendMessage(ChatColor.GREEN + "Pickups Disabled!");
					} else {
						player.sendMessage(ChatColor.RED + "Usage: /pickup on/off");
					}
				}
			}
		} else {
			sender.sendMessage("You must be a player to use this command!");
		}
		return true;
	}

	public void sendHelp(Player plr) {
		List<String> help = getConfig().getStringList("help");
		for (String s : help) {
			plr.sendMessage(ChatColor.GRAY + s);
		}
	}

}

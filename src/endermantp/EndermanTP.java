package endermantp;

import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class EndermanTP extends JavaPlugin implements Listener, CommandExecutor{
	HashMap<UUID, Integer> playerThresholds = new HashMap<UUID, Integer>();
	Logger logger = getLogger();
	PluginManager pm = getServer().getPluginManager();
	BukkitScheduler scheduler = getServer().getScheduler();


	//Configurable
	int threshold = 5;
	int delay = 80;
	boolean debug = false;
	double multiply = 3;
	double maxYDistance = 5;
	//

	@Override
	public void onEnable(){
		threshold = getConfig().getInt("threshold", 5);
		delay = getConfig().getInt("delay", 80);
		debug = getConfig().getBoolean("debug", false);
		multiply = getConfig().getDouble("multiply", 3);
		maxYDistance = getConfig().getDouble("maxYDistance", 5);
		if(threshold <= 0){
			logger.warning("Threshold was less than or equal to 0! Setting to 5(default)");
			threshold = 5;
		}
		if(delay <= 0){
			logger.warning("Delay was less than or equal to 0! Setting to 80(default)");
			delay = 80;
		}
		if(multiply <= 0){
			logger.warning("Multiply was less than or equal to 0! Setting to 3(default)");
			multiply = 3;
		}
		if(maxYDistance <= 0){
			logger.warning("Max Y distance was less than or equal to 0! Setting to 5(default)");
			maxYDistance = 5;
		}
		getConfig().set("threshold", threshold);
		getConfig().set("delay", delay);
		getConfig().set("debug", debug);
		getConfig().set("multiply", multiply);
		getConfig().set("maxYDistance", maxYDistance);
		saveConfig();
		pm.registerEvents(this, this);
		getCommand("endermantp").setExecutor(this);
	}

	@Override
	public void onDisable(){
		
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(label.equalsIgnoreCase("endermantp")){
			if(!sender.hasPermission("endermantp.command")){
				sender.sendMessage("You need " + ChatColor.GREEN + "endermantp.command" + ChatColor.WHITE + " permission.");
				return true;
			}
			if(args.length == 1){
				if(args[0].equalsIgnoreCase("delay")){
					sender.sendMessage("Current delay: " + delay);
				} else if(args[0].equalsIgnoreCase("threshold")){
					sender.sendMessage("Current threshold: " + threshold);
				} else if(args[0].equalsIgnoreCase("debug")) {
					debug = !debug;
					getConfig().set("debug", debug);
					saveConfig();
					sender.sendMessage("Debug " + (debug ? ChatColor.GREEN + "ON" : ChatColor.RED + "OFF"));
				} else if(args[0].equalsIgnoreCase("multiply")){
					sender.sendMessage("Current multiply: " + multiply);
				} else if(args[0].equalsIgnoreCase("maxy")){
					sender.sendMessage("Current max Y: " + maxYDistance);
				} else {
					sender.sendMessage("Usage: /endermantp <delay/threshold/multiply/maxY> [newValue]");
					sender.sendMessage("Example: to see current delay type /endermantp delay");
					sender.sendMessage("Example: to set delay to 80 type /endermantp delay 80");
				}
			} else if(args.length == 2){
				if(args[0].equalsIgnoreCase("delay")){
					int newDelay = 0;
					try{
						newDelay = Integer.parseInt(args[1]);
					} catch (NumberFormatException ex){
						sender.sendMessage(args[1] + " is not a number!");
						return true;
					}
					if(newDelay < 1){
						sender.sendMessage("Delay can't be less than 1!");
						return true;
					}
					delay = newDelay;
					getConfig().set("delay", delay);
					saveConfig();
					sender.sendMessage("Delay set to: " + delay);
				} else if(args[0].equalsIgnoreCase("threshold")){
					int newThreshold = 0;
					try{
						newThreshold = Integer.parseInt(args[1]);
					} catch (NumberFormatException ex){
						sender.sendMessage(args[1] + " is not a number!");
						return true;
					}
					if(newThreshold < 1){
						sender.sendMessage("Threshold can't be less than 1!");
						return true;
					}
					threshold = newThreshold;
					getConfig().set("threshold", threshold);
					saveConfig();
					sender.sendMessage("Threshold set to: " + threshold);
				} else if(args[0].equalsIgnoreCase("multiply")){
					double newMultiply = 0;
					try{
						newMultiply = Double.parseDouble(args[1]);
					} catch (NumberFormatException ex){
						sender.sendMessage(args[1] + " is not a number!");
						return true;
					}
					if(newMultiply <= 0){
						sender.sendMessage("Multiply can't be equal or less than 0!");
						return true;
					}
					multiply = newMultiply;
					getConfig().set("multiply", multiply);
					saveConfig();
					sender.sendMessage("Multiply set to: " + multiply);
				} else if(args[0].equalsIgnoreCase("maxy")){
					double newMaxYDistance = 0;
					try{
						newMaxYDistance = Double.parseDouble(args[1]);
					} catch (NumberFormatException ex){
						sender.sendMessage(args[1] + " is not a number!");
						return true;
					}
					if(newMaxYDistance <= 0){
						sender.sendMessage("Max Y distance can't be equal or less than 0!");
						return true;
					}
					maxYDistance = newMaxYDistance;
					getConfig().set("maxYDistance", maxYDistance);
					saveConfig();
					sender.sendMessage("Max Y distance set to: " + maxYDistance);
				} else {
					sender.sendMessage("Usage: /endermantp <delay/threshold/multiply/maxY> [newValue]");
					sender.sendMessage("Example: to see current delay type /endermantp delay");
					sender.sendMessage("Example: to set delay to 80 type /endermantp delay 80");
				}
			} else {
				sender.sendMessage("Usage: /endermantp <delay/threshold/multiply/maxY> [newValue]");
				sender.sendMessage("Example: to see current delay type /endermantp delay");
				sender.sendMessage("Example: to set delay to 80 type /endermantp delay 80");
			}
			return true;
		}
		return false;
	}

	@EventHandler
	public void onHit(EntityDamageByEntityEvent e){
		if(e.getDamager() instanceof Player){
			if(e.getEntityType() == EntityType.ENDERMAN){
				Player player = (Player) e.getDamager();
				if(player.hasPermission("endermantp.teleport")){
					if(playerThresholds.containsKey(player.getUniqueId())){
						playerThresholds.put(player.getUniqueId(), playerThresholds.get(player.getUniqueId()) + 1);
						if(debug){
							player.sendMessage("[EndermanTP] " + ChatColor.GREEN + "+1" + ChatColor.WHITE + ", current: " + playerThresholds.get(player.getUniqueId()));
						}
						scheduler.scheduleSyncDelayedTask(this, new Runnable() {
							UUID uuid = player.getUniqueId();
							@Override
							public void run() {
								playerThresholds.put(uuid, playerThresholds.get(uuid) - 1);
								if(debug){
									player.sendMessage("[EndermanTP] " + ChatColor.RED + "-1" + ChatColor.WHITE + ", current: " + playerThresholds.get(player.getUniqueId()));
								}
							}
						}, delay);
					} else {
						playerThresholds.put(player.getUniqueId(), 1);
						if(debug){
							player.sendMessage("[EndermanTP] " + ChatColor.GREEN + "(Player added) +1" + ChatColor.WHITE + ", current: " + playerThresholds.get(player.getUniqueId()));
						}
						scheduler.scheduleSyncDelayedTask(this, new Runnable() {
							UUID uuid = player.getUniqueId();
							@Override
							public void run() {
								playerThresholds.put(uuid, playerThresholds.get(uuid) - 1);
								if(debug){
									player.sendMessage("[EndermanTP] " + ChatColor.RED + "(Player added) -1" + ChatColor.WHITE + ", current: " + playerThresholds.get(player.getUniqueId()));
								}
							}
						}, delay);
					}
					if(playerThresholds.get(player.getUniqueId()) > threshold){
						if(player.getLocation().add(0, 2, 0).getBlock().getType() == Material.AIR){
							e.getEntity().teleport(player);
							if(debug){
								player.sendMessage("[EndermanTP] Over threshold, teleporting enderman to you(no block on top of you detected)");
							}
						} else {
							teleportPlayerInFrontOfEnderman(player, e.getEntity());
							if(debug){
								player.sendMessage("[EndermanTP] Over threshold, teleporting you to enderman(block on top of you detected)");
							}
						}
					}
				}
			}
		}
	}
	
	private void teleportPlayerInFrontOfEnderman(Player player, Entity enderman){
		Location originalPlayerlocation = player.getLocation();
		Location playerLookingAt = originalPlayerlocation.clone();
		playerLookingAt.add(playerLookingAt.getDirection().multiply(multiply).toLocation(playerLookingAt.getWorld()));
		int highestY = 0;
		int m = 1;
		while(true){
			if(m < 0.2){
				player.teleport(enderman);
				return;
			}
			highestY = playerLookingAt.getWorld().getHighestBlockYAt(playerLookingAt);
			if(Math.abs(highestY - originalPlayerlocation.getY()) > maxYDistance){
				m += -0.1;
				playerLookingAt = originalPlayerlocation.clone();
				playerLookingAt.add(playerLookingAt.getDirection().multiply(multiply * m).toLocation(playerLookingAt.getWorld()));
			} else {
				playerLookingAt.setY(highestY);
				player.teleport(lookTowardsLocation(playerLookingAt, enderman.getLocation()));
				break;
			}
		}
	}
	
	private Location lookTowardsLocation(Location from, Location to){
		if(from != null && to != null){
			Location loc = from.clone();
			double x = to.getX() - from.getX();
			double y = to.getY() - from.getY();
			double z = to.getZ() - from.getZ();
			if(x == 0 && z == 0){
				loc.setPitch(y > 0 ? -90 : 90);
				return loc;
			}
			loc.setYaw((float) Math.toDegrees((Math.atan2(-x, z) + (Math.PI * 2)) % (Math.PI * 2)));
			loc.setPitch((float) Math.toDegrees(Math.atan(-y / Math.sqrt((x * x) + (z * z)))));
			return loc;
		}
		return null;
	}
}

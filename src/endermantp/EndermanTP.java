package endermantp;

import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class EndermanTP extends JavaPlugin implements Listener{
	HashMap<UUID, Integer> playerThresholds = new HashMap<UUID, Integer>();
	Logger logger = getLogger();
	PluginManager pm = getServer().getPluginManager();
	BukkitScheduler scheduler = getServer().getScheduler();
	
	
	//Configurable
	int threshold = 5;
	int delay = 80;
	boolean debug = false;
	//
	
	@Override
	public void onEnable(){
		threshold = getConfig().getInt("threshold", 5);
		delay = getConfig().getInt("delay", 80);
		debug = getConfig().getBoolean("debug", false);
		if(threshold <= 0){
			logger.warning("Threshold was less than or equal to 0! Setting to 5(default)");
			threshold = 5;
		}
		if(delay <= 0){
			logger.warning("Delay was less than or equal to 0! Setting to 80(default)");
			delay = 80;
		}
		getConfig().set("threshold", threshold);
		getConfig().set("delay", delay);
		getConfig().set("debug", debug);
		saveConfig();
		pm.registerEvents(this, this);
	}
	
	@Override
	public void onDisable(){
		
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
							player.teleport(e.getEntity());
							if(debug){
								player.sendMessage("[EndermanTP] Over threshold, teleporting you to enderman(block on top of you detected)");
							}
						}
					}
				}
			}
		}
	}
}

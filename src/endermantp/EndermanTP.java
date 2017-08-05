package endermantp;

import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class EndermanTP extends JavaPlugin implements Listener{
	HashMap<UUID, Long> cooldown = new HashMap<UUID, Long>();
	Logger logger = getLogger();
	PluginManager pm = getServer().getPluginManager();
	BukkitScheduler scheduler = getServer().getScheduler();
	
	
	//Configurable
	double rangeX = 30;
	double rangeY = 30;
	double rangeZ = 30;
	long cooldownMs = 30000;
	double multiply = 3;
	boolean cancelEndermanTeleport = false;
	//
	
	@Override
	public void onEnable(){
		rangeX = getConfig().getDouble("rangeX", 30);
		rangeY = getConfig().getDouble("rangeY", 30);
		rangeZ = getConfig().getDouble("rangeZ", 30);
		cooldownMs = getConfig().getLong("cooldownMs", 30000);
		multiply = getConfig().getDouble("multiply", 3);
		cancelEndermanTeleport = getConfig().getBoolean("cancelEndermanTeleport", false);
		if(rangeX <= 0){
			logger.warning("Range X was less than or equal to 0! Setting to 30(default)");
			rangeX = 30;
		}
		if(rangeY <= 0){
			logger.warning("Range Y was less than or equal to 0! Setting to 30(default)");
			rangeY = 30;
		}
		if(rangeZ <= 0){
			logger.warning("Range Z was less than or equal to 0! Setting to 30(default)");
			rangeZ = 30;
		}
		if(cooldownMs <= 0){
			logger.warning("Cooldown miliseconds was less than or equal to 0! Setting to 30 000(default) = 30 seconds");
			cooldownMs = 30000;
		}
		if(multiply <= 0){
			logger.warning("Multiply was less than or equal to 0! Setting to 3(default)");
			multiply = 3;
		}
		getConfig().set("rangeX", rangeX);
		getConfig().set("rangeY", rangeY);
		getConfig().set("rangeZ", rangeZ);
		getConfig().set("cooldownMs", cooldownMs);
		getConfig().set("multiply", multiply);
		getConfig().set("cancelEndermanTeleport", cancelEndermanTeleport);
		saveConfig();
		pm.registerEvents(this, this);
	}
	
	@Override
	public void onDisable(){
		
	}
	
	@EventHandler
	public void onTeleport(EntityTeleportEvent e){
		if(e.getEntityType() == EntityType.ENDERMAN){
			if(teleportNearestPlayersToEnderman(e.getEntity())){
				if(cancelEndermanTeleport){
					e.setCancelled(true);
				}
			}
		}
	}
	
	public boolean teleportNearestPlayersToEnderman(Entity e){
		int found = 0;
		if(e != null){
			for(Entity en : e.getNearbyEntities(rangeX, rangeY, rangeZ)){
				if(en instanceof Player){
					Player player = (Player) en;
					if(!player.hasPermission("endermantp.teleport")){
						continue;
					}
					if(cooldown.containsKey(player.getUniqueId())){
						if(System.currentTimeMillis() - cooldown.get(player.getUniqueId()) > cooldownMs){
							cooldown.put(player.getUniqueId(), System.currentTimeMillis());
							found++;
							scheduler.scheduleSyncDelayedTask(this, new Runnable() {
								Player pla = player;
								Entity ent = e;
								@Override
								public void run() {
									if(ent != null && !ent.isDead()){
										if(pla != null && !pla.isDead()){
											Location loc = ent.getLocation();
											loc.add(loc.getDirection().multiply(multiply).toLocation(loc.getWorld()));
											loc.setY(loc.getWorld().getHighestBlockYAt(loc));
											pla.teleport(lookTowardsLocation(loc, en.getLocation()));
										}
									}
								}
							});
						}
					} else {
						cooldown.put(player.getUniqueId(), System.currentTimeMillis());
						found++;
						scheduler.scheduleSyncDelayedTask(this, new Runnable() {
							Player pla = player;
							Entity ent = e;
							@Override
							public void run() {
								if(ent != null && !ent.isDead()){
									if(pla != null && !pla.isDead()){
										Location loc = ent.getLocation();
										loc.add(loc.getDirection().multiply(multiply).toLocation(loc.getWorld()));
										loc.setY(loc.getWorld().getHighestBlockYAt(loc));
										pla.teleport(lookTowardsLocation(loc, en.getLocation()));
									}
								}
							}
						});
					}
				}
			}
		}
		return !(found < 1);
	}
	
	public Location lookTowardsLocation(Location from, Location to){
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

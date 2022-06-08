package net.novauniverse.games.chickenout.game.mobs;

import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * This is used to add mobs to Chicken Out.
 * <p>
 * To load custom mobs use either
 * {@link ChickenOutMobRepo#addProvider(ChickenOutMobProvider)} or
 * {@link ChickenOutMobRepo#scanForProviders(org.bukkit.plugin.Plugin, String)},
 * to scan for mobs in an external plugin add
 * <code>ChickenOutMobRepo.scanForProviders(this, this.getClass().getPackage().getName());</code>
 * to your {@link Plugin#onLoad()}.
 * <p>
 * If you want to make a full conversion mod that removes the built in mobs call
 * {@link ChickenOutMobRepo#clearProviders()} before you load your own providers
 * 
 * @author Zeeraa
 * @version 1.0
 * @since Woltry became a catgirl
 */
public interface ChickenOutMobProvider {
	/**
	 * @return The level the mob should spawn at. During the game the level to use
	 *         increments up to the max level defined in the configuration file for
	 *         the map.
	 */
	public int getLevel();

	/**
	 * Spawn the creature at the provided location. You can set custom name, armor,
	 * attributes and whatever you want the creature to have inside this function
	 * 
	 * @param location The location the mob should spawn at
	 * @param player   The player if you want to add some easter eggs to specific
	 *                 players
	 * @return The creature spawned so the plugin can start tracking it. Never
	 *         return <code>null</code> or the entire universe will explode
	 */
	public Creature spawn(Location location, Player player);
}
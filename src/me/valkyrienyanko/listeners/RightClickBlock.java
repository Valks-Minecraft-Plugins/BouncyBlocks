package me.valkyrienyanko.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.java.JavaPlugin;

import me.valkyrienyanko.BouncyBlocks;

public class RightClickBlock implements Listener {
	@EventHandler
	private void rightClickBlock(BlockPlaceEvent e) {
		if (e.getBlock().getType() == Material.SPONGE) {
			e.setCancelled(true);
			BouncyBlock bb = new BouncyBlock(Material.SPONGE, 2, JavaPlugin.getPlugin(BouncyBlocks.class));
			bb.spawn(e.getBlock().getLocation());
			bb.setEffect(true);
		}
	}
}

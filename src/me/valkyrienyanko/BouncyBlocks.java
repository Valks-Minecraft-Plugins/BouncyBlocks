package me.valkyrienyanko;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import me.valkyrienyanko.listeners.BouncyBlock;
import me.valkyrienyanko.listeners.RightClickBlock;

public class BouncyBlocks extends JavaPlugin {
	@Override
	public void onEnable() {
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new BouncyBlock(), this);
		pm.registerEvents(new RightClickBlock(), this);
	}
}

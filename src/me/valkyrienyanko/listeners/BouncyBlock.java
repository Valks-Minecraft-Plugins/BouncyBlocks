package me.valkyrienyanko.listeners;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

public class BouncyBlock implements Listener {

	private Material mat;
	private int bounces, removeAfter;
	private FallingBlock fb;
	private Plugin p;
	private boolean dead = false;
	private boolean effect = false;
	private byte data = 0;
	
	public BouncyBlock() {
		
	}

	/**
	 * @param m           - Block material
	 * @param removeAfter - How many times it can hit the ground before it will be
	 *                    removed.
	 */
	public BouncyBlock(Material m, int removeAfter, Plugin p) {
		this.mat = m;
		this.removeAfter = removeAfter;
		this.p = p;
		Bukkit.getPluginManager().registerEvents(this, p);
	}

	@SuppressWarnings("deprecation")
	public void spawn(Location l) {
		if (fb == null) {
			fb = l.getWorld().spawnFallingBlock(l, getMaterial(), getData());
		}
		fb.setMetadata("BB", new FixedMetadataValue(p, "A bouncy block"));
		fb.setDropItem(false);
		fb.setVelocity(getRandomVelocity());
	}

	public void spawn(Location l, FallingBlock fb) {
		this.fb = fb;
		fb.setMetadata("BB", new FixedMetadataValue(p, "A bouncy block"));
		fb.setDropItem(false);
		fb.setVelocity(getRandomVelocity());
	}

	public boolean doEffect() {
		return effect;
	}

	public void setEffect(boolean effect) {
		this.effect = effect;
	}

	public boolean isDead() {
		return dead;
	}

	public void setDead(boolean dead) {
		if (dead) {
			if (fb != null)
				fb.remove();
		}
		this.dead = dead;
	}

	public int getBouncesToRemove() {
		return removeAfter;
	}

	public int getBounces() {
		return bounces;
	}

	public byte getData() {
		return data;
	}

	public void setData(byte data) {
		this.data = data;
	}

	public Material getMaterial() {
		return mat;
	}

	public FallingBlock getFallingBlock() {
		return fb;
	}

	public Plugin getPlugin() {
		return p;
	}

	public void setBounces(int bounces) {
		this.bounces = bounces;
	}

	public void setMaterial(Material mat) {
		this.mat = mat;
	}

	private Vector getRandomVelocity() {
		Random random = new Random();
		final double power = 0.5D;
		double rix = random.nextBoolean() ? -power : power;
		double riz = random.nextBoolean() ? -power : power;
		double x = random.nextBoolean() ? (rix * (0.25D + (random.nextInt(3) / 5))) : 0.0D;
		double y = 0.6D + (random.nextInt(2) / 4.5D);
		double z = random.nextBoolean() ? (riz * (0.25D + (random.nextInt(3) / 5))) : 0.0D;
		Vector velocity = new Vector(x, y, z);

		return velocity;
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void entityChangeBlock(EntityChangeBlockEvent e) {
		if (e.getEntity() instanceof FallingBlock) {
			FallingBlock fb = (FallingBlock) e.getEntity();
			if (fb.hasMetadata("BB")) {
				//if (fb.getUniqueId().compareTo(getFallingBlock().getUniqueId()) == 0) {
					BouncyBlockHitGroundEvent event = new BouncyBlockHitGroundEvent(this, fb,
							e.getBlock().getLocation());
					Bukkit.getPluginManager().callEvent(event);

					if (event.isCancelled()) {
						setDead(true);

						return;
					}

					if (doEffect()) {
						fb.getWorld().playEffect(fb.getLocation(), Effect.STEP_SOUND, getMaterial());
					}
					if (getBounces() >= getBouncesToRemove()) {
						setDead(true);
						return;
					}
					setBounces(getBounces() + 1);
					e.setCancelled(true);
					fb.remove();
					fb = e.getEntity().getLocation().getWorld().spawnFallingBlock(e.getEntity().getLocation(),
							getMaterial(), getData());
					spawn(fb.getLocation(), fb);
				//}
			}
		}
	}

	public static class BouncyBlockHitGroundEvent extends Event implements Cancellable {
		private static final HandlerList handlers = new HandlerList();
		private FallingBlock fb;
		private BouncyBlock bouncyBlock;
		private Location hit;
		private boolean cancelled = false;

		public BouncyBlockHitGroundEvent(BouncyBlock bouncyBlock, FallingBlock fb, Location hit) {
			this.hit = hit;
			this.bouncyBlock = bouncyBlock;
			this.fb = fb;
		}

		public FallingBlock getFallingBlock() {
			return fb;
		}

		public BouncyBlock getBouncyBlock() {
			return bouncyBlock;
		}

		public Location getHit() {
			return hit;
		}

		public int getBouncesLeftUntilDeath() {
			return bouncyBlock.getBouncesToRemove() - bouncyBlock.getBounces();
		}

		public HandlerList getHandlers() {
			return handlers;
		}

		public static HandlerList getHandlerList() {
			return handlers;
		}

		/**
		 * Cancelling the event will just make the falling block act as a normal one.
		 */
		public void setCancelled(boolean cancelled) {
			this.cancelled = cancelled;
		}

		public boolean isCancelled() {
			return cancelled;
		}

	}

}
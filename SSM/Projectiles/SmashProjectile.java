package SSM.Projectiles;

import SSM.SSM;
import SSM.Utilities.DamageUtil;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public abstract class SmashProjectile extends BukkitRunnable implements Listener {

    protected static JavaPlugin plugin = SSM.getInstance();
    protected Player firer;
    protected String name;
    protected Entity projectile;
    protected double damage;
    protected double hitbox_size;
    protected double knockback_mult;
    protected long expiration_ticks = 300;

    public SmashProjectile(Player firer, String name) {
        this.firer = firer;
        this.name = name;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void launchProjectile() {
        setProjectileEntity(createProjectileEntity());
        this.doVelocity();
        this.runTaskTimer(plugin, 0L, 0L);
    }

    @Override
    public void run() {
        if(projectile == null || !projectile.isValid()) {
            destroy();
            return;
        }
        if(projectile.getTicksLived() > getExpirationTicks()) {
            if(onExpire()) {
                destroy();
                return;
            }
        }
        // Check if we hit an entity first
        LivingEntity target = checkClosestTarget();
        if(target != null) {
            if(onHitLivingEntity(target)) {
                firer.playSound(firer.getLocation(), Sound.ORB_PICKUP, 1.0f, 1.25f);
                destroy();
                return;
            }
        }
        // Check if we hit a block next
        Block block = checkHitBlock();
        if(block != null) {
            if(onHitBlock(block)) {
                destroy();
                return;
            }
        }
        // Check if we're idle next
        if(checkIdle()) {
            if(onIdle()) {
                destroy();
                return;
            }
        }
        doEffect();
    }

    public void destroy() {
        if(projectile != null) {
            projectile.remove();
        }
        this.cancel();
    }

    protected LivingEntity checkClosestTarget() {
        // Realistically the fastest both a player and entity will move is about
        // 80 blocks per second or 4 blocks per tick, (hue fast blocking with autoclicker got 3.5)
        // If we do 15 iterations this should be more than enough for most projectiles leeway
        // If you want to optimize this probably start with checking the magnitude of velocity
        double max_realistic_velocity = 4;
        int max_iterations = 15;
        net.minecraft.server.v1_8_R3.Entity entity = ((CraftEntity) projectile).getHandle();
        // Get possible projectiles that could be hit on this tick
        List<LivingEntity> possible = new ArrayList<LivingEntity>();
        for(Entity check : projectile.getWorld().getNearbyEntities(projectile.getLocation(),
                entity.motX + hitbox_size + max_realistic_velocity,
                entity.motY + hitbox_size + max_realistic_velocity,
                entity.motZ + hitbox_size + max_realistic_velocity)) {
            if(!(check instanceof LivingEntity)) {
                continue;
            }
            if (check.equals(firer) || check.equals(projectile)) {
                continue;
            }
            possible.add((LivingEntity) check);
        }
        for(int i = 0; i < max_iterations; i++) {
            double percent = i / (max_iterations - 1);
            // Linearly interpolate the player and entity hitbox and see if they overlap
            for(LivingEntity check : possible) {
                net.minecraft.server.v1_8_R3.EntityLiving living = (EntityLiving) ((CraftEntity) check).getHandle();
                AxisAlignedBB bb = living.getBoundingBox();
                double l_x = living.motX * percent;
                double l_y = living.motY * percent;
                double l_z = living.motZ * percent;
                AxisAlignedBB livingBB = new AxisAlignedBB(bb.a + l_x, bb.b + l_y, bb.c + l_z,
                        bb.d + l_x, bb.e + l_y, bb.f + l_z);
                double p_x = entity.locX + entity.motX * percent;
                double p_y = entity.locY + entity.motY * percent;
                double p_z = entity.locZ + entity.motZ * percent;
                AxisAlignedBB projectileBB = new AxisAlignedBB(p_x, p_y, p_z, p_x, p_y, p_z);
                projectileBB = projectileBB.grow(hitbox_size, hitbox_size, hitbox_size);
                if(projectileBB.b(livingBB)) {
                    return check;
                }
            }
        }
        return null;
    }

    protected Block checkHitBlock() {
        net.minecraft.server.v1_8_R3.World world = ((CraftWorld) projectile.getWorld()).getHandle();
        net.minecraft.server.v1_8_R3.Entity entity = ((CraftEntity) projectile).getHandle();
        // Do a raytrace to see what our real position is going to be
        Vec3D vec_old = new Vec3D(entity.locX, entity.locY, entity.locZ);
        Vec3D vec_new = new Vec3D(entity.locX + entity.motX, entity.locY + entity.motY, entity.locZ + entity.motZ);
        MovingObjectPosition final_position = entity.world.rayTrace(vec_old, vec_new, false, true, false);
        if(final_position == null) {
            return null;
        }
        Block block = projectile.getWorld().getBlockAt(final_position.a().getX(),
                final_position.a().getY(), final_position.a().getZ());
        if(block.isLiquid() || !block.getType().isSolid()) {
            return null;
        }
        // Set our motion to stop on the block we are hitting
        entity.motX = ((float) (final_position.pos.a - entity.locX));
        entity.motY = ((float) (final_position.pos.b - entity.locY));
        entity.motZ = ((float) (final_position.pos.c - entity.locZ));
        // Get the magnitude of the motion vector
        float f2 = MathHelper.sqrt(entity.motX * entity.motX + entity.motY * entity.motY + entity.motZ * entity.motZ);
        entity.locX -= entity.motX / f2 * 0.0500000007450581D;
        entity.locY -= entity.motY / f2 * 0.0500000007450581D;
        entity.locZ -= entity.motZ / f2 * 0.0500000007450581D;
        return block;
    }

    protected boolean checkIdle() {
        if(projectile.isDead() || !projectile.isValid()) {
            return true;
        }
        Block check_block = projectile.getLocation().getBlock().getRelative(BlockFace.DOWN);
        if(projectile.getVelocity().length() < 0.2 && (projectile.isOnGround() || check_block.getType().isSolid())) {
            return true;
        }
        return false;
    }

    public void setProjectileEntity(Entity projectile) {
        if(projectile == null) {
            return;
        }
        this.projectile = projectile;
        if (projectile instanceof Item) {
            Item item = (Item) projectile;
            item.setPickupDelay(1000000);
        }
    }

    protected abstract Entity createProjectileEntity();

    // Called once when the projectile is fired
    protected abstract void doVelocity();

    // Visual effects that apply every tick
    protected abstract void doEffect();

    // Returns true to call destroy after
    protected abstract boolean onExpire();

    // Returns true to call destroy after
    protected abstract boolean onHitLivingEntity(LivingEntity hit);

    // Returns true to call destroy after
    protected abstract boolean onHitBlock(Block hit);

    // Returns true to call destroy after
    protected abstract boolean onIdle();

    public long getExpirationTicks() {
        return expiration_ticks;
    }

    public String getName() {
        return name;
    }

}

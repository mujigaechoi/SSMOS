package SSM.Projectiles;

import SSM.Utilities.DamageUtil;
import SSM.Utilities.VelocityUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class WebProjectile extends SmashProjectile {

    public WebProjectile(Player firer, String name) {
        super(firer, name);
        this.damage = 6;
        this.hitbox_mult = 0.5;
        this.knockback_mult = 0;
    }

    @Override
    protected Entity getProjectileEntity() {
        ItemStack cobweb = new ItemStack(Material.WEB, 1);
        return firer.getWorld().dropItem(firer.getLocation().add(0, 0.5, 0), cobweb);
    }

    @Override
    protected void doVelocity() {
        Vector spread = new Vector(Math.random(), Math.random(), Math.random()).subtract(new Vector(0.5, 0.5, 0.5));
        spread.normalize();
        spread.multiply(0.2);
        Vector final_velocity = firer.getLocation().getDirection().multiply(-1).add(spread);
        VelocityUtil.setVelocity(projectile, final_velocity,
                Math.random() * 0.4 + 1, false, 0, 0.2, 10, false);
    }

    @Override
    protected void doEffect() {
        return;
    }

    @Override
    protected boolean onExpire() {
        createWeb(projectile.getLocation(), 40, 3);
        return true;
    }

    @Override
    protected boolean onHitLivingEntity(LivingEntity hit) {
        createWeb(hit.getLocation(), 60, 0);
        DamageUtil.damage(hit, firer, damage, knockback_mult,
                false, EntityDamageEvent.DamageCause.CUSTOM,
                projectile.getLocation(), name);
        VelocityUtil.setVelocity(hit, new Vector(0, 0, 0));
        return true;
    }

    @Override
    protected boolean onHitBlock(Block hit) {
        createWeb(projectile.getLocation(), 40, 3);
        return true;
    }

    @Override
    protected boolean onIdle() {
        createWeb(projectile.getLocation(), 40, 3);
        return true;
    }

    public void createWeb(Location location, long ticks_stay, long need_ticks_lived) {
        if(projectile.getTicksLived() > need_ticks_lived) {
            Block replace = location.getBlock();
            Material replacedType = replace.getType();
            if (replacedType == Material.WEB) {
                return;
            }
            replace.setType(Material.WEB);
            Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                @Override
                public void run() {
                    replace.setType(replacedType);
                }
            }, ticks_stay);
        }
    }

}
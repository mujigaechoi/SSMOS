package SSM.Abilities;

import SSM.GameManagers.OwnerEvents.OwnerRightClickEvent;
import SSM.Leap;
import SSM.Utilities.DamageUtil;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

public class WolfStrike extends Leap implements OwnerRightClickEvent {

    double damage = 7.0;

    public WolfStrike() {
        this.name = "Wolf Strike";
        this.cooldownTime = 8;
        this.power = 1.5;
        this.timed = true;
        this.activeTime = 2.5;
        this.endOnLand = true;
        this.hitbox = 1.0;
    }

    public void onOwnerRightClick(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        checkAndActivate(player);
    }

    public void activate() {
        owner.setVelocity(owner.getLocation().getDirection().multiply(power));
        activity.put(owner.getUniqueId(), true);
        timerList.put(owner.getUniqueId(), System.currentTimeMillis() + (long) activeTime * 1000);
    }

    @Override
    public void onHit(LivingEntity target) {
        DamageUtil.dealDamage(owner, target, damage, true, false);
        Vector enemy = target.getLocation().toVector();
        Vector player = owner.getLocation().toVector();
        Vector pre = enemy.subtract(player);
        Vector velocity = pre.normalize().multiply(1.35);
        target.setVelocity(new Vector(velocity.getX(), 0.5, velocity.getZ()));

    }


}

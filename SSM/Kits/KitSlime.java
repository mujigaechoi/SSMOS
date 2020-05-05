package SSM.Kits;

import SSM.*;
import SSM.Abilities.*;
import SSM.Attributes.DoubleJumps.GenericDoubleJump;
import SSM.Attributes.Regeneration;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class KitSlime extends Kit {

    public KitSlime() {
        super();

        this.damage = 6;
        this.armor = 4;
        this.speed = 0.21f;
        this.regeneration = 0.35;
        this.knockbackTaken = 0;
        this.disguise = DisguiseType.SLIME;
        this.name = "Slime";
        this.menuItem = new ItemStack(Material.SLIME_BALL);
    }

    public void equipKit(Player player) {
        super.equipKit(player);

        setArmor(Material.CHAINMAIL_BOOTS, 0);
        setArmor(Material.CHAINMAIL_CHESTPLATE, 2);
        setArmor(Material.CHAINMAIL_HELMET, 3);

        setItem(Material.IRON_SWORD, 0, null);
        setItem(Material.IRON_AXE, 1, new SlimeSlam());

        addAttribute(new Regeneration(regeneration, 1));
        addAttribute(new GenericDoubleJump(0.61, 0.9, 1, Sound.ENTITY_GHAST_SHOOT));

    }

}

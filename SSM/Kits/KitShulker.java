package SSM.Kits;

import SSM.*;
import SSM.Abilities.*;
import SSM.Attributes.DoubleJumps.GenericDoubleJump;
import SSM.Attributes.Regeneration;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class KitShulker extends Kit {

    public KitShulker() {
        super();

        this.damage = 5;
        this.speed = 0.15f;
        this.regeneration = 0.2;
        this.knockbackTaken = 0;
        this.name = "Shulker";
        this.menuItem = new ItemStack(Material.SHULKER_SHELL);
    }

    public void equipKit(Player player) {
        super.equipKit(player);

        setArmor(Material.IRON_BOOTS, 0);
        setArmor(Material.IRON_LEGGINGS, 1);
        setArmor(Material.IRON_CHESTPLATE, 2);
        setArmor(Material.IRON_HELMET, 3);

        setItem(Material.IRON_AXE, 0, new Shulker());

        addAttribute(new Regeneration(regeneration, 1));
        addAttribute(new GenericDoubleJump(0.61, 0.8, 1, Sound.ENTITY_GHAST_SHOOT));

    }
}

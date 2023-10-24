package SSM.Kits;

import SSM.Attributes.DoubleJumps.GenericDoubleJump;
import SSM.Attributes.Regeneration;
import SSM.GameManagers.DisguiseManager;
import SSM.GameManagers.Disguises.GuardianDisguise;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class KitGuardian extends Kit {

    public KitGuardian() {
        super();
        this.damage = 5;
        this.armor = 4.5;
        this.regeneration = 0.25;
        this.knockback = 1.25;
        this.name = "Guardian";
        this.menuItem = Material.PRISMARINE_SHARD;
    }

    @Override
    public void initializeKit() {
        setArmor(Material.LEATHER_BOOTS, 0);
        setArmor(Material.DIAMOND_CHESTPLATE, 2);

        addAttribute(new Regeneration(regeneration, 20));
        addAttribute(new GenericDoubleJump(0.9, 0.9, 1, Sound.GHAST_FIREBALL));

        DisguiseManager.addDisguise(owner, new GuardianDisguise(owner));
    }

    @Override
    public void setPreviewHotbar() {
        setItem(new ItemStack(Material.IRON_SWORD, 1), 0);
        setItem(new ItemStack(Material.IRON_AXE, 1), 1);
    }

    @Override
    public void setGameHotbar() {
        setItem(new ItemStack(Material.IRON_SWORD, 1), 0);
        setItem(new ItemStack(Material.IRON_AXE, 1), 1);
    }

}
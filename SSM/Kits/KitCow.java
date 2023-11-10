package SSM.Kits;

import SSM.Abilities.AngryHerd;
import SSM.Abilities.InkShotgun;
import SSM.Abilities.MilkSpiral;
import SSM.Attributes.Compass;
import SSM.Attributes.DoubleJumps.GenericDoubleJump;
import SSM.Attributes.Hunger;
import SSM.Attributes.Regeneration;
import SSM.Attributes.Stampede;
import SSM.GameManagers.DisguiseManager;
import SSM.GameManagers.Disguises.CowDisguise;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

public class KitCow extends Kit {

    public KitCow() {
        super();
        this.damage = 6;
        this.armor = 7;
        this.regeneration = 0.25;
        this.knockback = 1.1;
        this.name = "Cow";
        this.menuItem = Material.RAW_BEEF;
    }

    @Override
    public void initializeKit() {
        setArmorSlot(Material.IRON_BOOTS, 0);
        setArmorSlot(Material.IRON_LEGGINGS, 1);
        setArmorSlot(Material.CHAINMAIL_CHESTPLATE, 2);
        setArmorSlot(Material.IRON_HELMET, 3);

        setAbility(new AngryHerd(), 0);
        setAbility(new MilkSpiral(), 1);

        addAttribute(new Regeneration(regeneration));
        addAttribute(new Hunger());
        addAttribute(new Compass());
        addAttribute(new GenericDoubleJump(0.9, 0.9, 1, Sound.GHAST_FIREBALL));
        addAttribute(new Stampede());

        DisguiseManager.addDisguise(owner, new CowDisguise(owner));
    }

    @Override
    public void setPreviewHotbar() {
        setItem(new ItemStack(Material.IRON_AXE), 0);
        setItem(new ItemStack(Material.IRON_SPADE), 1);
        setItem(new ItemStack(Material.LEATHER), 2, getAttributeByClass(Stampede.class));
        setItem(new ItemStack(Material.NETHER_STAR), 3);
    }

    @Override
    public void setGameHotbar() {
        setItem(new ItemStack(Material.IRON_AXE), 0);
        setItem(new ItemStack(Material.IRON_SPADE), 1);
        setItem(Compass.COMPASS_ITEM, 2);
    }

}

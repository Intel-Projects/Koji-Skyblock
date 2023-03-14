package koji.skyblock.item.enchants.enchants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import koji.developerkit.utils.xseries.XMaterial;
import koji.skyblock.item.CustomItem;
import koji.skyblock.item.ItemType;
import koji.skyblock.item.enchants.Enchant;
import koji.skyblock.player.PClass;
import koji.skyblock.player.events.SkyblockDamageEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.entity.Horse.Variant;
import org.bukkit.event.EventHandler;

public class Smite extends Enchant {
   private static final List undead;

   public boolean isUltimate() {
      return false;
   }

   public int getMaxLevel() {
      return 5;
   }

   public String getName() {
      return "Smite";
   }

   public String getDisplayName() {
      return "Smite";
   }

   public ArrayList getTargets() {
      return this.targets(new ItemType[]{ItemType.SWORD, ItemType.FISHING_WEAPON});
   }

   private static boolean isUndeadHorse(Entity type) {
      if (XMaterial.supports(11)) {
         return type.getType() == EntityType.valueOf("ZOMBIE_HORSE") || type.getType() == EntityType.valueOf("SKELETON_HORSE");
      } else if (!(type instanceof Horse)) {
         return false;
      } else {
         return ((Horse)type).getVariant() == Variant.UNDEAD_HORSE || ((Horse)type).getVariant() == Variant.SKELETON_HORSE;
      }
   }

   @EventHandler
   public void onSkyblockDamage(SkyblockDamageEvent e) {
      if (e.getDamager() instanceof Player) {
         PClass pS = PClass.getPlayer((Player)e.getDamager());
         if (isUndeadHorse(e.getEntity()) || undead.contains(e.getEntity().getType())) {
            e.addToAdditiveMultiplier(0.08D * (double)(Integer)(new CustomItem(e.parseItem(pS.player()))).getEnchants().get(this));
         }
      }

   }

   public double getVar(int level) {
      return (double)(8 * level);
   }

   public boolean canAppearInEnchantTable() {
      return true;
   }

   public int getBaseExperienceCost() {
      return 10;
   }

   public int getExperienceAddedPerLevel(int level) {
      return 5;
   }

   public int getBookshelfPowerRequirement() {
      return 0;
   }

   public ArrayList getConflicts() {
      return arrayList(new Class[]{BaneOfArthropods.class, Sharpness.class});
   }

   static {
      undead = new ArrayList(Arrays.asList(EntityType.ZOMBIE, EntityType.SKELETON, EntityType.GIANT, EntityType.WITHER));
      if (XMaterial.supports(11)) {
         undead.add(EntityType.valueOf("HUSK"));
         undead.add(EntityType.valueOf("STRAY"));
         undead.add(EntityType.valueOf("ZOMBIE_VILLAGER"));
         if (XMaterial.supports(13)) {
            undead.add(EntityType.valueOf("DROWNED"));
            undead.add(EntityType.valueOf("PHANTOM"));
            if (XMaterial.supports(16)) {
               undead.add(EntityType.valueOf("ZOGLIN"));
               undead.add(EntityType.valueOf("PIGLIN"));
               undead.add(EntityType.valueOf("ZOMBIFIED_PIGLIN"));
            }
         }
      }

      if (!XMaterial.supports(16)) {
         undead.add(EntityType.valueOf("PIG_ZOMBIE"));
      }

   }
}

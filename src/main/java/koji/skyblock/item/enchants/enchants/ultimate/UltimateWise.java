package koji.skyblock.item.enchants.enchants.ultimate;

import java.util.ArrayList;
import koji.skyblock.item.CustomItem;
import koji.skyblock.item.ItemType;
import koji.skyblock.item.enchants.Enchant;
import koji.skyblock.item.enchants.events.EnchantAddEvent;
import koji.skyblock.item.enchants.events.EnchantRemoveEvent;
import koji.skyblock.player.PClass;
import koji.skyblock.player.api.ManaUseEvent;
import org.bukkit.event.EventHandler;

public class UltimateWise extends Enchant {
   public boolean isUltimate() {
      return true;
   }

   public int getMaxLevel() {
      return 5;
   }

   public String getName() {
      return "Ultimate_Wise";
   }

   public String getDisplayName() {
      return "Ultimate Wise";
   }

   public ArrayList getTargets() {
      return this.targets(new ItemType[]{ItemType.FISHING_ROD, ItemType.SHOVEL, ItemType.PICKAXE, ItemType.HOE, ItemType.SWORD, ItemType.FISHING_WEAPON, ItemType.AXE, ItemType.BOW});
   }

   @EventHandler
   public void manaUse(ManaUseEvent e) {
      e.setManaCost(e.getManaCost() - e.getManaCost() * (double)(Integer)e.getItem().getEnchants().get(this) / 10.0D);
   }

   @EventHandler
   public void onEnchantRemove(EnchantRemoveEvent e) {
      CustomItem ci = e.getItem();
      ci.getAbilities().forEach((a) -> {
         double cost;
         try {
            cost = Double.parseDouble((String)a.getPlaceholderDefaults().getOrDefault("mana_cost", "0"));
         } catch (NumberFormatException var5) {
            cost = 0.0D;
         }

         e.getItem().changePlaceholder(a.getIdentifier(), "mana_cost", num(PClass.format(cost)));
      });
      e.setItem(ci);
   }

   @EventHandler
   public void onEnchantAdd(EnchantAddEvent e) {
      CustomItem ci = e.getItem();
      ci.getAbilities().forEach((a) -> {
         double cost;
         try {
            cost = Double.parseDouble((String)a.getPlaceholderDefaults().getOrDefault("mana_cost", "0"));
         } catch (NumberFormatException var6) {
            cost = 0.0D;
         }

         e.getItem().changePlaceholder(a.getIdentifier(), "mana_cost", num(PClass.format(cost - cost * (double)e.getItem().getEnchantLevel(this) / 10.0D)));
      });
      e.setItem(ci);
   }

   public double getVar(int level) {
      return (double)(10 * level);
   }

   public boolean canAppearInEnchantTable() {
      return false;
   }

   public int getBaseExperienceCost() {
      return 0;
   }

   public int getExperienceAddedPerLevel(int level) {
      return 0;
   }

   public int getBookshelfPowerRequirement() {
      return 0;
   }

   public ArrayList getConflicts() {
      return null;
   }
}

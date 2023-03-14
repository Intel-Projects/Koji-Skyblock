package koji.skyblock.item.enchants.enchants;

import java.util.ArrayList;
import koji.skyblock.item.CustomItem;
import koji.skyblock.item.ItemType;
import koji.skyblock.item.enchants.Enchant;
import koji.skyblock.player.PClass;
import koji.skyblock.player.Stats;
import koji.skyblock.player.events.SkyblockDamageEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class LifeSteal extends Enchant {
   public boolean isUltimate() {
      return false;
   }

   public int getMaxLevel() {
      return 3;
   }

   public String getName() {
      return "Life_Steal";
   }

   public String getDisplayName() {
      return "Life Steal";
   }

   public ArrayList getTargets() {
      return this.targets(new ItemType[]{ItemType.SWORD, ItemType.FISHING_WEAPON});
   }

   @EventHandler
   public void onSkyblockDamage(SkyblockDamageEvent e) {
      if (e.getDamager() instanceof Player) {
         PClass pS = PClass.getPlayer((Player)e.getDamager());
         double percentage = 0.005D * (double)(Integer)(new CustomItem(e.parseItem(pS.player()))).getEnchants().get(this);
         pS.addStat(Stats.HEALTH, percentage * pS.getStat(Stats.MAX_HEALTH));
      }

   }

   public double getVar(int level) {
      return 0.1D * (double)level;
   }

   public boolean canAppearInEnchantTable() {
      return true;
   }

   public int getBaseExperienceCost() {
      return 20;
   }

   public int getExperienceAddedPerLevel(int level) {
      return 5;
   }

   public int getBookshelfPowerRequirement() {
      return 3;
   }

   public ArrayList getConflicts() {
      return arrayList(new Class[]{Syphon.class});
   }
}

package koji.skyblock.item.enchants.enchants;

import java.util.ArrayList;
import koji.skyblock.item.CustomItem;
import koji.skyblock.item.ItemType;
import koji.skyblock.item.enchants.Enchant;
import koji.skyblock.player.PClass;
import koji.skyblock.player.Stats;
import koji.skyblock.player.events.SkyblockDamageEvent;
import koji.skyblock.utils.StatMap;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class Syphon extends Enchant {
   public boolean isUltimate() {
      return false;
   }

   public int getMaxLevel() {
      return 3;
   }

   public String getName() {
      return "Syphon";
   }

   public String getDisplayName() {
      return "Syphon";
   }

   public ArrayList getTargets() {
      return arrayList(new ItemType[]{ItemType.SWORD, ItemType.FISHING_WEAPON});
   }

   @EventHandler
   public void onSkyblockDamage(SkyblockDamageEvent e) {
      if (e.getDamager() instanceof Player) {
         PClass pS = PClass.getPlayer((Player)e.getDamager());
         CustomItem item = new CustomItem(e.parseItem(pS.player()));
         StatMap stats = pS.getStatDifference(item);
         int min = Math.min((int)(stats.get(Stats.CRIT_DAMAGE) / 100.0D), 10);
         double percentage = this.getVar((Integer)item.getEnchants().get(this)) / 100.0D;
         pS.addStat(Stats.HEALTH, percentage * pS.getStat(Stats.MAX_HEALTH) * (double)min);
      }

   }

   public double getVar(int level) {
      return 0.1D + 0.1D * (double)level;
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
      return 5;
   }

   public ArrayList getConflicts() {
      return arrayList(new Class[]{LifeSteal.class});
   }
}

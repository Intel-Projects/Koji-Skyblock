package koji.skyblock.item.enchants.enchants;

import java.util.ArrayList;
import java.util.List;
import koji.skyblock.item.CustomItem;
import koji.skyblock.item.ItemType;
import koji.skyblock.item.enchants.Enchant;
import koji.skyblock.player.events.SkyblockDamageEvent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class GiantKiller extends Enchant {
   public boolean isUltimate() {
      return false;
   }

   public int getMaxLevel() {
      return 5;
   }

   public String getName() {
      return "Giant_Killer";
   }

   public String getDisplayName() {
      return "Giant Killer";
   }

   public ArrayList getTargets() {
      return this.targets(new ItemType[]{ItemType.SWORD, ItemType.FISHING_WEAPON});
   }

   @EventHandler
   public void onSkyblockDamage(SkyblockDamageEvent e) {
      if (e.getDamager() instanceof Player && e.getType() == SkyblockDamageEvent.Type.SWORD) {
         Player p = (Player)e.getDamager();
         LivingEntity damagee = (LivingEntity)e.getEntity();
         int level = (Integer)(new CustomItem(e.parseItem(p))).getEnchants().get(this);
         if (damagee.getHealth() > p.getHealth()) {
            e.addToAdditiveMultiplier(Math.min((damagee.getHealth() - p.getHealth()) / p.getHealth() * this.getVar(level) / 100.0D, (double)(Integer)this.getExtraVars(level).get(0) / 100.0D));
         }
      }

   }

   public double getVar(int level) {
      if (level == 5) {
         return 0.6D;
      } else {
         return level >= 6 ? (double)(level - 5) * 0.3D + 0.6D : (double)level / 10.0D;
      }
   }

   public List getExtraVars(int level) {
      if (level == 5) {
         return arrayList(new Integer[]{30});
      } else if (level == 6) {
         return arrayList(new Integer[]{45});
      } else {
         return level >= 7 ? arrayList(new Integer[]{45 + (level - 6) * 20}) : arrayList(new Integer[]{5 * level});
      }
   }

   public boolean canAppearInEnchantTable() {
      return true;
   }

   public int getBaseExperienceCost() {
      return 10;
   }

   public int getExperienceAddedPerLevel(int level) {
      return 10;
   }

   public int getBookshelfPowerRequirement() {
      return 4;
   }

   public ArrayList getConflicts() {
      return null;
   }
}

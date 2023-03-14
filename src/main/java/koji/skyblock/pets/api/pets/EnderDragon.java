package koji.skyblock.pets.api.pets;

import java.util.ArrayList;
import java.util.Iterator;
import koji.skyblock.pets.OverwritePet;
import koji.skyblock.pets.Pet;
import koji.skyblock.pets.api.OverwritePetAbility;
import koji.skyblock.pets.api.PetAbility;
import koji.skyblock.pets.api.PetDequipEvent;
import koji.skyblock.pets.api.PetEquipEvent;
import koji.skyblock.player.PClass;
import koji.skyblock.player.Stats;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class EnderDragon extends OverwritePet {
   private final ArrayList increasedStats;

   public EnderDragon() {
      this.increasedStats = arrayList(new Stats[]{Stats.MAX_HEALTH, Stats.DEFENSE, Stats.TRUE_DEFENSE, Stats.STRENGTH, Stats.SPEED, Stats.CRIT_CHANCE, Stats.CRIT_DAMAGE, Stats.MAX_MANA, Stats.SEA_CREATURE_CHANCE, Stats.MAGIC_FIND, Stats.PET_LUCK, Stats.ABILITY_DAMAGE, Stats.FEROCITY});
   }

   public PetAbility getFirstAbility() {
      return new OverwritePetAbility() {
         public PetAbility getOverride() {
            return EnderDragon.this.override.getFirstAbility();
         }

         @EventHandler
         public double onDealDamage(EntityDamageByEntityEvent e) {
            if (e.getEntity() instanceof Enderman) {
               Player p = (Player)e.getDamager();
               return e.getDamage() * (double)PClass.getPlayer(p).getPetInstance().getLevel() * 0.02D;
            } else {
               return 0.0D;
            }
         }
      };
   }

   public PetAbility getSecondAbility() {
      return new OverwritePetAbility() {
         public PetAbility getOverride() {
            return EnderDragon.this.override.getSecondAbility();
         }

         @EventHandler
         public void onEquip(PetEquipEvent e) {
            PClass pS = PClass.getPlayer(e.getPlayer());
            String reason = "EnderDragon_Pet";
            String aspect = "ASPECT_OF_THE_DRAGONS";
            pS.addItemStatGain(reason, aspect, Stats.DAMAGE, 0.5D * (double)e.getLevel());
            pS.addItemStatGain(reason, aspect, Stats.STRENGTH, 0.3D * (double)e.getLevel());
         }

         @EventHandler
         public void onUnequip(PetDequipEvent e) {
            PClass pS = PClass.getPlayer(e.getPlayer());
            pS.resetItemStatGain("EnderDragon_Pet");
         }
      };
   }

   public PetAbility getThirdAbility() {
      return new OverwritePetAbility() {
         public PetAbility getOverride() {
            return EnderDragon.this.override.getThirdAbility();
         }

         @EventHandler
         public void onEquip(PetEquipEvent e) {
            PClass pS = PClass.getPlayer(e.getPlayer());
            Iterator var3 = EnderDragon.this.increasedStats.iterator();

            while(var3.hasNext()) {
               Stats stat = (Stats)var3.next();
               pS.addMultiplyStat("EnderDragon_Pet", stat, 0.001D * (double)e.getLevel());
            }

         }

         @EventHandler
         public void onUnequip(PetDequipEvent e) {
            PClass.getPlayer(e.getPlayer()).resetMultiplyingStats("EnderDragon_Pet");
         }
      };
   }

   public Pet getPetOverride() {
      return Pet.matchFromType("EnderDragon");
   }
}

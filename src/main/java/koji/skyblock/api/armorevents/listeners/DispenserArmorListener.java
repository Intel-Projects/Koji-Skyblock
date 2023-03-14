package koji.skyblock.api.armorevents.listeners;

import koji.skyblock.api.armorevents.enums.ArmorType;
import koji.skyblock.api.armorevents.event.ArmorEquipEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseArmorEvent;
import org.bukkit.inventory.ItemStack;

public class DispenserArmorListener implements Listener {
   @EventHandler
   public void dispenseArmorEvent(BlockDispenseArmorEvent event) {
      ArmorType type = ArmorType.matchType(event.getItem());
      if (type != null && event.getTargetEntity() instanceof Player) {
         Player p = (Player)event.getTargetEntity();
         ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent(p, ArmorEquipEvent.EquipMethod.DISPENSER, type, (ItemStack)null, event.getItem());
         Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
         if (armorEquipEvent.isCancelled()) {
            event.setCancelled(true);
         }
      }

   }
}

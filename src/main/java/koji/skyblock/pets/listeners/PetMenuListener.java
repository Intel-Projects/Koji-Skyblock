package koji.skyblock.pets.listeners;

import koji.developerkit.listener.KListener;
import koji.skyblock.pets.commands.OpenPetsMenuCMD;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;

public class PetMenuListener extends KListener {
   @EventHandler
   public void onInventoryClick(InventoryClickEvent e) {
      if (OpenPetsMenuCMD.getPlayersWithPetMenuOpen().contains((Player)e.getWhoClicked())) {
         e.setCancelled(!e.getClickedInventory().equals(e.getView().getTopInventory()));
      }

   }
}

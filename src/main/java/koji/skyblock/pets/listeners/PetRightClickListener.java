package koji.skyblock.pets.listeners;

import koji.developerkit.listener.KListener;
import koji.developerkit.utils.NBTItem;
import koji.developerkit.utils.xseries.XSound;
import koji.skyblock.files.pets.PetData;
import koji.skyblock.pets.PetInstance;
import koji.skyblock.player.PClass;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PetRightClickListener extends KListener {
   @EventHandler
   public void onRightClick(PlayerInteractEvent e) {
      if (isValidItem(e.getItem())) {
         Player p = e.getPlayer();
         NBTItem item = new NBTItem(e.getItem());
         if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            PetInstance petInstance = PClass.getPlayer(p).getPetInstance();
            if (item.hasKey("petType")) {
               e.setCancelled(true);
               playSound(p, XSound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F);
               petInstance.saveItemToFile(p.getItemInHand());
               p.setItemInHand((ItemStack)null);
            } else if (item.hasKey("petSkin")) {
               e.setCancelled(true);
               String skullId = item.getString("petSkin");
               String petSkinType = item.getString("petSkinType");
               if (petInstance.isActive() && petInstance.getPet().getName().equalsIgnoreCase(petSkinType)) {
                  PetData.getPetData().setSkin(p, petInstance.getUuid(), skullId);
                  petInstance.setSkin(skullId);
                  petInstance.setPetVisually();
                  p.playSound(p.getLocation(), XSound.ENTITY_ARROW_HIT_PLAYER.parseSound(), 1000.0F, 1.0F);
                  p.sendMessage(ChatColor.GREEN + "Your " + item.getItem().getItemMeta().getDisplayName() + ChatColor.GREEN + " has been applied!");
                  p.setItemInHand((ItemStack)null);
               }
            }
         }
      }

   }
}

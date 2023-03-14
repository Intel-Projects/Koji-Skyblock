package koji.skyblock.api.armorevents.event;

import koji.skyblock.api.armorevents.enums.ArmorType;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;

public final class ArmorEquipEvent extends PlayerEvent implements Cancellable {
   private static final HandlerList handlers = new HandlerList();
   private boolean cancel = false;
   private final ArmorEquipEvent.EquipMethod equipType;
   private final ArmorType type;
   private ItemStack oldArmorPiece;
   private ItemStack newArmorPiece;

   public ArmorEquipEvent(Player p, ArmorEquipEvent.EquipMethod equipType, ArmorType type, ItemStack oldArmorPiece, ItemStack newArmorPiece) {
      super(p);
      this.equipType = equipType;
      this.type = type;
      this.oldArmorPiece = oldArmorPiece;
      this.newArmorPiece = newArmorPiece;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }

   public HandlerList getHandlers() {
      return handlers;
   }

   public void setCancelled(boolean cancel) {
      this.cancel = cancel;
   }

   public boolean isCancelled() {
      return this.cancel;
   }

   public ArmorType getType() {
      return this.type;
   }

   public ItemStack getOldArmorPiece() {
      return this.oldArmorPiece;
   }

   public void setOldArmorPiece(ItemStack oldArmorPiece) {
      this.oldArmorPiece = oldArmorPiece;
   }

   public ItemStack getNewArmorPiece() {
      return this.newArmorPiece;
   }

   public void setNewArmorPiece(ItemStack newArmorPiece) {
      this.newArmorPiece = newArmorPiece;
   }

   public ItemStack[] getNewArmorContents() {
      ItemStack[] armor = this.player.getInventory().getArmorContents();
      switch(this.type) {
      case HELMET:
         armor[0] = this.getNewArmorPiece();
         break;
      case CHESTPLATE:
         armor[1] = this.getNewArmorPiece();
         break;
      case LEGGINGS:
         armor[2] = this.getNewArmorPiece();
         break;
      case BOOTS:
         armor[3] = this.getNewArmorPiece();
      }

      return armor;
   }

   public ItemStack[] getOldArmorContents() {
      ItemStack[] armor = this.player.getInventory().getArmorContents();
      switch(this.type) {
      case HELMET:
         armor[0] = this.getOldArmorPiece();
         break;
      case CHESTPLATE:
         armor[1] = this.getOldArmorPiece();
         break;
      case LEGGINGS:
         armor[2] = this.getOldArmorPiece();
         break;
      case BOOTS:
         armor[3] = this.getOldArmorPiece();
      }

      return armor;
   }

   public ArmorEquipEvent.EquipMethod getMethod() {
      return this.equipType;
   }

   public static enum EquipMethod {
      SHIFT_CLICK,
      DRAG,
      PICK_DROP,
      HOTBAR,
      HOTBAR_SWAP,
      DISPENSER,
      BROKE,
      DEATH;
   }
}

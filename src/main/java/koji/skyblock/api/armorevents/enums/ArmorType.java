package koji.skyblock.api.armorevents.enums;

import koji.skyblock.api.armorevents.listeners.ArmorListener;
import org.bukkit.inventory.ItemStack;

public enum ArmorType {
   HELMET(5),
   CHESTPLATE(6),
   LEGGINGS(7),
   BOOTS(8);

   private final int slot;

   private ArmorType(int slot) {
      this.slot = slot;
   }

   public static ArmorType matchType(ItemStack itemStack) {
      if (ArmorListener.isAirOrNull(itemStack)) {
         return null;
      } else {
         String type = itemStack.getType().name();
         if (!type.endsWith("_HELMET") && !type.endsWith("_SKULL") && !type.endsWith("_HEAD") && !type.equals("SKULL_ITEM")) {
            if (!type.endsWith("_CHESTPLATE") && !type.equals("ELYTRA")) {
               if (type.endsWith("_LEGGINGS")) {
                  return LEGGINGS;
               } else {
                  return type.endsWith("_BOOTS") ? BOOTS : null;
               }
            } else {
               return CHESTPLATE;
            }
         } else {
            return HELMET;
         }
      }
   }

   public int getSlot() {
      return this.slot;
   }
}

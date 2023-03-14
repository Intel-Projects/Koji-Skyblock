package koji.skyblock.item.creation;

import koji.developerkit.listener.KListener;
import koji.skyblock.commands.Messages;
import koji.skyblock.item.CustomItem;
import koji.skyblock.item.utils.SignMenuFactory;
import koji.skyblock.player.Stats;
import org.bukkit.event.EventHandler;

public class MidasSword extends KListener {
   @EventHandler
   public void onMidasGrab(GrabCustomItemEvent e) {
      if (e.getItem().getID().equals("MIDAS_SWORD")) {
         e.setCancelled(true);
         SignMenuFactory.Menu menu = SignMenuFactory.getFactory().newMenu(arrayList(new String[]{"", "^^^^^^^^^^^^^^^", "Enter Amount", "Midas Bid"})).reopenIfFail(false).response((player, strings) -> {
            String digits = strings[0].replaceAll("[^0-9.]", "");
            int paid = digits.equals("") ? 0 : (int)Double.parseDouble(digits);
            int bonusStats = 120;
            if (paid < 1000000) {
               bonusStats = paid / '썐';
            } else if (paid < 2500000) {
               bonusStats = (paid - 1000000) / 100000;
            } else if (paid < 7500000) {
               bonusStats = (paid - 2500000) / 200000;
            } else if (paid < 25000000) {
               bonusStats = (paid - 7500000) / 500000;
            } else if (paid < 50000000) {
               bonusStats = (paid - 25000000) / 1000000;
            }

            CustomItem item = e.getItem();
            item.changePlaceholder("swordgreed", "paid", commaify(paid));
            item.changePlaceholder("swordgreed", "damage", String.valueOf(bonusStats));
            item.changePlaceholder("swordgreed", "strength", String.valueOf(bonusStats));
            item.addStat(Stats.DAMAGE, (double)bonusStats, false);
            item.addStat(Stats.STRENGTH, (double)bonusStats, false);
            if (!addItemUnlessFull(player.getInventory(), item.buildWithAbilities())) {
               player.sendMessage(Messages.FULL_INVENTORY.getMessage());
            }

            return true;
         });
         menu.open(e.getPlayer());
      }

   }
}

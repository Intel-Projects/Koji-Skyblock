package koji.skyblock.player.events;

import koji.skyblock.files.Config;
import org.bukkit.event.entity.PlayerDeathEvent;

public class SkyblockDeathEvent extends PlayerDeathEvent {
   double initialCoins;
   private double amountOfCoinsBeingLost;
   private boolean loseCoins = Config.willLoseCoinsOnDeath();

   public SkyblockDeathEvent(PlayerDeathEvent e, double coins) {
      super(e.getEntity(), e.getDrops(), e.getDroppedExp(), e.getNewExp(), e.getNewTotalExp(), e.getNewLevel(), e.getDeathMessage());
      this.amountOfCoinsBeingLost = Config.getMultipliedCoinAmount(coins);
      this.initialCoins = coins;
   }

   public boolean willLoseCoins() {
      return this.loseCoins;
   }

   public void setWillLoseCoins(boolean value) {
      this.loseCoins = value;
   }

   public double getInitialCoins() {
      return this.initialCoins;
   }

   public double getAmountOfCoinsBeingLost() {
      return this.amountOfCoinsBeingLost;
   }

   public void setAmountOfCoinsBeingLost(double amountOfCoinsBeingLost) {
      this.amountOfCoinsBeingLost = amountOfCoinsBeingLost;
   }
}

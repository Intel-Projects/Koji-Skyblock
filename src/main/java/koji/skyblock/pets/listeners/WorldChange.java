package koji.skyblock.pets.listeners;

import koji.skyblock.player.PClass;
import koji.skyblock.player.scoreboard.ScoreboardState;
import koji.skyblock.utils.SkyblockWorld;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class WorldChange implements Listener {
   @EventHandler
   public void onWorldChange(PlayerChangedWorldEvent e) {
      Player p = e.getPlayer();
      World from = e.getFrom();
      PClass pS = PClass.getPlayer(p);
      SkyblockWorld sbWorldFrom = SkyblockWorld.getWorld(from);
      SkyblockWorld sbWorldTo = SkyblockWorld.getWorld(p.getWorld());
      if (sbWorldFrom.isInDragonFight()) {
         pS.setState(sbWorldTo.isInDragonFight() ? ScoreboardState.DRAGON_BATTLE : ScoreboardState.DEFAULT);
      }

      if (sbWorldTo.isInDragonFight()) {
         pS.setState(ScoreboardState.DRAGON_BATTLE);
      }

      pS.getPetInstance().teleportStands();
      pS.getPetInstance().refreshVisibility();
   }
}

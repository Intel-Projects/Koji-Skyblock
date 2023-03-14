package koji.skyblock.player.damage;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import koji.developerkit.listener.KListener;
import koji.developerkit.runnable.KRunnable;
import koji.developerkit.utils.duplet.Triplet;
import koji.developerkit.utils.xseries.XMaterial;
import koji.developerkit.utils.xseries.XSound;
import koji.skyblock.Skyblock;
import koji.skyblock.item.CustomItem;
import koji.skyblock.player.MiscListeners;
import koji.skyblock.player.PClass;
import koji.skyblock.player.Stats;
import koji.skyblock.player.events.SkyblockDamageEvent;
import koji.skyblock.player.events.SkyblockDeathEvent;
import koji.skyblock.utils.StatMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.util.Vector;

public class DamageListener extends KListener {
   private static final List CRIT_SPECTRUM;

   @EventHandler(
      priority = EventPriority.HIGHEST
   )
   public void entityHit(EntityDamageByEntityEvent e) {
      Entity damager = e.getDamager();
      SkyblockDamageEvent.Type type = SkyblockDamageEvent.Type.SWORD;
      CustomItem itemUsed = null;
      if (!e.isCancelled()) {
         if (e.getEntity().getLastDamageCause() instanceof SkyblockDamageEvent) {
            SkyblockDamageEvent event = (SkyblockDamageEvent)e.getEntity().getLastDamageCause();
            if (event.getType() == SkyblockDamageEvent.Type.MAGIC) {
               return;
            }

            if (event.getType() == SkyblockDamageEvent.Type.UNSPECIFIED && event.getItem() != null) {
               itemUsed = event.getItem();
            }
         }

         if (damager instanceof Arrow && ((Arrow)damager).getShooter() instanceof Player) {
            Arrow arrow = (Arrow)damager;
            if (MiscListeners.getArrows().containsKey(arrow)) {
               itemUsed = (CustomItem)((Triplet)MiscListeners.getArrows().get(arrow)).getSecond();
            }

            Entity damager = (Player)arrow.getShooter();
            type = SkyblockDamageEvent.Type.BOW;
            PClass pS = PClass.getPlayer((Player)damager);
            StatMap stats = pS.getStatDifference(itemUsed);
            SkyblockDamageEvent damageEvent = new SkyblockDamageEvent(damager, e.getDamager(), e.getEntity(), e.getEntity() instanceof Player, type, (5.0D + stats.get(Stats.DAMAGE)) * (1.0D + stats.get(Stats.STRENGTH) / 100.0D), itemUsed);
            Bukkit.getPluginManager().callEvent(damageEvent);
            e.setCancelled(damageEvent.isCancelled());
            e.setDamage(damageEvent.getDamage() * (!MiscListeners.getArrows().containsKey(arrow) ? 1.0D : (Double)((Triplet)MiscListeners.getArrows().get(arrow)).getThird()));
            return;
         }

         if (damager instanceof Player || e.getEntity() instanceof Player) {
            PClass pS = PClass.getPlayer((Player)((Player)(damager instanceof Player ? damager : e.getEntity())));
            StatMap stats = pS.getStatDifference(itemUsed);
            SkyblockDamageEvent damageEvent = new SkyblockDamageEvent(damager, e.getEntity(), e.getEntity() instanceof Player && !e.getEntity().hasMetadata("NPC"), type, (5.0D + stats.get(Stats.DAMAGE)) * (1.0D + stats.get(Stats.STRENGTH) / 100.0D), itemUsed);
            Bukkit.getPluginManager().callEvent(damageEvent);
            e.setCancelled(damageEvent.isCancelled());
            e.setDamage(damageEvent.getDamage());
         }
      }

   }

   @EventHandler
   public void onEntityDamage(EntityDamageEvent e) {
      if (e.getEntity() instanceof Player) {
         if (e.getCause() == DamageCause.ENTITY_ATTACK) {
            return;
         }

         SkyblockDamageEvent.Type type;
         switch(e.getCause()) {
         case FALL:
            type = SkyblockDamageEvent.Type.FALL;
            break;
         case LAVA:
         case FIRE:
         case FIRE_TICK:
            type = SkyblockDamageEvent.Type.FIRE;
            break;
         case VOID:
            type = SkyblockDamageEvent.Type.VOID;
            break;
         case DROWNING:
            type = SkyblockDamageEvent.Type.DROWNING;
            break;
         default:
            type = SkyblockDamageEvent.Type.UNSPECIFIED;
         }

         SkyblockDamageEvent damageEvent = new SkyblockDamageEvent(e.getEntity(), true, type, e.getDamage());
         Bukkit.getPluginManager().callEvent(damageEvent);
         e.setCancelled(damageEvent.isCancelled());
         e.setDamage(damageEvent.getDamage());
      }

   }

   @EventHandler(
      priority = EventPriority.HIGHEST
   )
   public void onSkyblockDamage(SkyblockDamageEvent e) {
      double finalDamage;
      Player p;
      PClass pS;
      if (!e.isPlayerReceivingDamage() && e.getType() != SkyblockDamageEvent.Type.FIRE) {
         p = (Player)e.getDamager();
         pS = PClass.getPlayer(p);
         StatMap stats = e.getItem() != null ? pS.getStatDifference(e.getItem()) : pS.getStats();
         finalDamage = e.getDamage();
         boolean crit = (double)((int)random(101.0F)) <= stats.get(Stats.CRIT_CHANCE);
         if (crit) {
            finalDamage *= 1.0D + stats.get(Stats.CRIT_DAMAGE) / 100.0D;
         }

         double additive = e.getAdditiveMultiplier();
         finalDamage *= 1.0D + additive;
         double multiplicative = e.getMultiplicativeMultiplier();
         finalDamage *= 1.0D + multiplicative;
         finalDamage *= 1.0D - e.getDamageReduction() / 100.0D;
         if (e.getEntity().hasMetadata("NPC")) {
            try {
               Method npcRegistryClazz = Class.forName("net.citizensnpcs.api.CitizensAPI").getMethod("getNPCRegistry");
               Object npcRegistry = npcRegistryClazz.invoke((Object)null);
               Method getNpc = npcRegistry.getClass().getMethod("getNPC", Entity.class);
               Object npc = getNpc.invoke(npcRegistry, e.getEntity());
               boolean isProtected = (Boolean)npc.getClass().getMethod("isProtected").invoke(npc);
               if (isProtected) {
                  e.setCancelled(true);
                  return;
               }
            } catch (Exception var17) {
               var17.printStackTrace();
            }
         }

         e.setDamage(finalDamage * e.getFinalDamageMultiplier());
         setDamageIndicator(e.getEntity().getLocation(), crit ? DamageListener.DamageType.CRIT : DamageListener.DamageType.NORMAL, finalDamage);
         if (!pS.isOnFerocityCooldown()) {
            for(double ferocity = stats.get(Stats.FEROCITY); ferocity > 0.0D; ferocity -= 100.0D) {
               if ((double)((int)random(100.0F) + 1) <= ferocity) {
                  pS.setOnFerocityCooldown(true);
                  (new KRunnable((task) -> {
                     if (!e.getEntity().isDead()) {
                        p.playSound(p.getLocation(), XSound.ITEM_FLINTANDSTEEL_USE.parseSound(), 100.0F, 0.0F);
                        ((LivingEntity)e.getEntity()).damage(finalDamage, p);
                     }

                  })).runTaskLater(Skyblock.getPlugin(), 1L);
                  (new KRunnable((task) -> {
                     pS.setOnFerocityCooldown(false);
                  })).runTaskLater(Skyblock.getPlugin(), 5L);
               }
            }
         }
      } else if (!e.isPlayerReceivingDamage()) {
         setDamageIndicator(e.getEntity().getLocation(), DamageListener.DamageType.FIRE, e.getDamage());

         for(int i = 0; i < 4; ++i) {
            double randomX = Math.random();
            finalDamage = Math.random();
            double randomZ = Math.random();
            randomX -= 0.5D;
            finalDamage += 0.25D;
            randomZ -= 0.5D;
            Skyblock.getAbilityParticleManager().damage(e.getEntity().getLocation().add(randomX, finalDamage, randomZ));
         }
      } else if (e.getType().equals(SkyblockDamageEvent.Type.VOID)) {
         p = (Player)e.getEntity();
         p.setLastDamageCause(new EntityDamageEvent(p, DamageCause.VOID, new HashMap(), new HashMap()));
         this.onPlayerDeath(new SkyblockDeathEvent(new PlayerDeathEvent(p, fromArray(p.getInventory().getContents()), p.getTotalExperience(), p.getDisplayName() + " died"), Skyblock.getPlugin().getEconomy().getBalance(p)));
      } else if (!e.getEntity().hasMetadata("NPC")) {
         p = (Player)e.getEntity();
         pS = PClass.getPlayer(p);
         double finalDamage = e.getDamage();
         double defense = e.isTrueDamage() ? pS.getStat(Stats.TRUE_DEFENSE) : pS.getStat(Stats.DEFENSE);
         finalDamage *= 1.0D - (defense / (defense + 100.0D) + pS.getTotalDamageReducAsPercent() + e.getDamageReduction() / 100.0D);
         finalDamage = pS.subtractAbsorption(finalDamage);
         setDamageIndicator(e.getEntity().getLocation(), DamageListener.DamageType.NORMAL, finalDamage);
         pS.removeStat(Stats.HEALTH, finalDamage);
         e.setDamage(finalDamage * e.getFinalDamageMultiplier());
      }

   }

   @EventHandler(
      priority = EventPriority.HIGHEST
   )
   public void onPlayerDeath(PlayerDeathEvent e) {
      double balance = Skyblock.getPlugin().getEconomy().getBalance(e.getEntity());
      SkyblockDeathEvent sDE = new SkyblockDeathEvent(e, balance);
      this.onPlayerDeath(sDE);
      e.setDeathMessage(sDE.getDeathMessage());
      e.setKeepInventory(sDE.getKeepInventory());
      e.setKeepLevel(sDE.getKeepLevel());
   }

   @EventHandler(
      priority = EventPriority.HIGHEST
   )
   public void onPlayerDeath(SkyblockDeathEvent e) {
      e.setKeepInventory(true);
      e.setKeepLevel(true);
      e.setDeathMessage((String)null);
      e.getDrops().clear();
      Player p = e.getEntity();
      EntityDamageEvent event = p.getLastDamageCause();
      String personal = "You died";
      String message = " died.";
      switch(this.getDamageCause(p)) {
      case FALL:
         message = " fell to their death.";
         personal = "You fell to your death.";
         break;
      case LAVA:
      case FIRE:
      case FIRE_TICK:
         message = " burned to death.";
         personal = "You burned to death.";
         break;
      case VOID:
         message = " fell into the void.";
         personal = "You fell into the void.";
         break;
      case DROWNING:
         message = " drowned.";
         personal = "You drowned.";
         break;
      case ENTITY_ATTACK:
         EntityDamageByEntityEvent damageEvent = (EntityDamageByEntityEvent)event;
         message = " was slain by " + damageEvent.getDamager().getName() + ChatColor.GRAY + ".";
         personal = "You were slain by " + damageEvent.getDamager().getName() + ChatColor.GRAY + ".";
         break;
      case SUFFOCATION:
         message = " suffocated.";
         personal = "You suffocated.";
         break;
      case CONTACT:
         EntityDamageByBlockEvent blockEvent = (EntityDamageByBlockEvent)event;
         if (blockEvent.getDamager().getType() == XMaterial.CACTUS.parseMaterial()) {
            message = " was pricked to death by a cactus.";
            personal = "You were pricked to death by a cactus.";
         }
      }

      Iterator var8 = p.getWorld().getPlayers().iterator();

      while(var8.hasNext()) {
         Player player = (Player)var8.next();
         if (!player.equals(p)) {
            player.sendMessage(ChatColor.RED + " ☠ " + ChatColor.GRAY + p.getDisplayName() + ChatColor.GRAY + message);
         }
      }

      (new KRunnable((task) -> {
         p.spigot().respawn();
         p.setVelocity(new Vector());
         p.setFallDistance(0.0F);
         Location teleport = p.getWorld().getSpawnLocation();
         if (p.getBedSpawnLocation() != null) {
            teleport = p.getBedSpawnLocation();
         }

         p.teleport(teleport);
         if (e.getInitialCoins() != 0.0D && e.willLoseCoins()) {
            playSound(p, XSound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, 2.0F);
            double withdraw = e.getAmountOfCoinsBeingLost();
            Skyblock.getPlugin().getEconomy().withdrawPlayer(p, withdraw);
            p.sendMessage(ChatColor.RED + "You died and lost " + PClass.format(withdraw) + " coins!");
         } else {
            p.sendMessage(ChatColor.RED + " ☠ " + ChatColor.GRAY + personal);
         }

         PClass pS = PClass.getPlayer(p);
         p.setMaxHealth(pS.getStat(Stats.MAX_HEALTH));
         p.setHealth(pS.getStat(Stats.MAX_HEALTH));
         PClass.getPlayer(p).setStat(Stats.HEALTH, PClass.getPlayer(p).getStat(Stats.MAX_HEALTH));
      })).runTaskLater(Skyblock.getPlugin(), 1L);
   }

   public DamageCause getDamageCause(Entity e) {
      return e.getLastDamageCause().getCause();
   }

   public static void setDamageIndicator(Location loc, DamageListener.DamageType type, double damage) {
      double randomX = Math.random();
      double randomY = Math.random();
      double randomZ = Math.random();
      randomX -= 0.5D;
      randomY += 0.25D;
      randomZ -= 0.5D;
      ArmorStand stand = (ArmorStand)Skyblock.getArmorStand(loc.add(randomX, randomY, randomZ));
      stand.setCustomName(color(type, (int)damage));
      stand.setCustomNameVisible(true);
      stand.setGravity(false);
      (new KRunnable((task) -> {
         stand.remove();
      })).runTaskLater(Skyblock.getPlugin(), 30L);
   }

   public static String color(DamageListener.DamageType type, int text) {
      switch(type) {
      case FIRE:
         return ChatColor.GOLD + "" + text;
      case CRIT:
         return rainbowize("✧" + text + "✧");
      default:
         return ChatColor.GRAY + "" + text;
      }
   }

   public static String rainbowize(String string) {
      StringBuilder builder = new StringBuilder();
      int i = 0;
      String[] var3 = string.split("");
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         String c = var3[var5];
         if (i > CRIT_SPECTRUM.size() - 1) {
            i = 0;
         }

         builder.append(CRIT_SPECTRUM.get(i)).append(c);
         ++i;
      }

      return builder.toString();
   }

   static {
      CRIT_SPECTRUM = arrayList(new ChatColor[]{ChatColor.WHITE, ChatColor.WHITE, ChatColor.YELLOW, ChatColor.GOLD, ChatColor.RED, ChatColor.RED});
   }

   public static enum DamageType {
      CRIT,
      FIRE,
      NORMAL;
   }
}

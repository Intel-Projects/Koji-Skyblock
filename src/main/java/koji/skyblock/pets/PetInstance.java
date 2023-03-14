package koji.skyblock.pets;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;
import koji.developerkit.utils.ItemBuilder;
import koji.developerkit.utils.xseries.XMaterial;
import koji.skyblock.Skyblock;
import koji.skyblock.files.data.PlayerData;
import koji.skyblock.files.pets.PetData;
import koji.skyblock.item.Rarity;
import koji.skyblock.pets.api.PetDequipEvent;
import koji.skyblock.pets.api.PetEquipEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;

public class PetInstance extends Levelable {
   private final Player player;
   private String uuid;
   private Pet pet;
   private int level;
   private double currentXp;
   private String skin;
   private Rarity rarity;
   private static final ArrayList petArmorStands = new ArrayList();
   private ArmorStand nameTagStand;
   private ArmorStand armorStand;
   private final BukkitRunnable runnable;
   private boolean running = false;

   public PetInstance(Player p) {
      super(0, 0.0D, new double[0]);
      this.player = p;
      this.uuid = null;
      this.pet = null;
      this.level = 0;
      this.currentXp = 0.0D;
      this.skin = null;
      this.rarity = null;
      this.runnable = this.getRunnable();
      this.createArmorStands();
      this.teleportStands();
      this.start();
   }

   public ItemStack getItemFromUUID(String uuid) {
      PetData petData = PetData.getPetData();
      int level = petData.getLevel(this.player, uuid);
      double currentXp = petData.getCurrentExp(this.player, uuid);
      String type = petData.getType(this.player, uuid);
      Rarity rarity = petData.getRarity(this.player, uuid);
      String skin = petData.getSkin(this.player, uuid);
      petData.erasePetData(this.player, uuid);
      Pet pet = Pet.matchFromType(type);
      return pet != null ? pet.getItem(rarity, level, currentXp, skin) : (new ItemBuilder(XMaterial.STICK)).setName(ChatColor.RED + "Report me to admins!").setLore(new String[]{ChatColor.GRAY + "This item should not be", ChatColor.GRAY + "normally obtainable, please", ChatColor.GRAY + "contact an admin for support."}).build();
   }

   public PetInstance saveItemToFile(ItemStack item) {
      ItemBuilder ib = new ItemBuilder(item);
      String petType = ib.getString("petType");
      if (Pet.matchFromType(petType) == null) {
         this.player.sendMessage("§Couldn not find pet \"" + petType + "\"");
         return this;
      } else {
         Rarity rarity = Rarity.valueOf(ib.getString("petRarity"));
         int currentLevel = ib.getInt("petLevel");
         double currentXp = ib.getDouble("petCurrentXP");
         String skin = ib.getString("petSkin");
         PetData petData = PetData.getPetData();
         if (!petData.getPlayerExists(this.player)) {
            petData.createPlayer(this.player);
         }

         String id = UUID.randomUUID().toString();
         petData.createPet(this.player, id, petType, currentLevel, currentXp, skin, rarity);
         petData.addPet(this.player, this.uuid);
         this.player.sendMessage(ChatColor.GREEN + "Added Pet to your Pet Inventory!");
         return this;
      }
   }

   public ItemStack getItem() {
      return this.rarity == null ? null : this.pet.getItem(this.rarity, this.level, this.currentXp, this.skin);
   }

   public void set(String uuid) {
      PetData petData = PetData.getPetData();
      Pet pet = Pet.matchFromType(petData.getType(this.player, uuid));
      if (pet != null) {
         this.set(uuid, pet, petData.getLevel(this.player, uuid), petData.getCurrentExp(this.player, uuid), petData.getSkin(this.player, uuid), petData.getRarity(this.player, uuid));
      }
   }

   public void set(String uuid, Pet pet, int level, double currentXp, String skin, Rarity rarity) {
      PetEquipEvent event = new PetEquipEvent(this.player, pet, rarity, level);
      Bukkit.getPluginManager().callEvent(event);
      if (!event.isCancelled()) {
         if (this.running) {
            this.clear();
         }

         this.uuid = uuid;
         this.pet = pet;
         this.level = level;
         this.currentXp = currentXp;
         this.skin = skin;
         this.rarity = rarity;
         this.setPetVisually();
         this.teleportStands();
         PlayerData.getPlayerData().setActivePet(this.player, uuid);
      }
   }

   public void reset() {
      this.reset(false);
   }

   public void reset(boolean logOff) {
      this.uuid = null;
      this.pet = null;
      this.level = 0;
      this.currentXp = 0.0D;
      this.skin = null;
      this.rarity = null;
      if (!logOff) {
         PlayerData.getPlayerData().setActivePet(this.player, (String)null);
      }

   }

   public PetInstance addXP(double amount) {
      super.addXP(amount, this.player);
      return this;
   }

   public void start() {
      this.runnable.runTaskTimer(Skyblock.getPlugin(), 1L, 1L);
      this.running = true;
   }

   private void stop() {
      try {
         this.runnable.cancel();
         this.running = false;
      } catch (Exception var2) {
      }

   }

   public String getName() {
      return this.pet == null ? ChatColor.RED + "None" : this.rarity.getColor() + this.pet.getNameSpace();
   }

   public boolean isActive() {
      return this.uuid != null;
   }

   public void setPetVisually() {
      if (this.getItem() != null) {
         this.armorStand.setItemInHand(this.getItem());
         String skin = this.skin != null && !this.skin.isEmpty() ? " ✦" : "";
         this.nameTagStand.setCustomName("§8[§7Lv" + this.getLevel() + "§8] " + this.rarity.getColor() + ChatColor.stripColor(this.player.getDisplayName()) + "'s " + this.rarity.getColor() + this.pet.getNameSpace() + skin);
         this.nameTagStand.setCustomNameVisible(true);
      }

   }

   public void teleportStands() {
      Location cloned = this.player.getLocation().add(0.0D, 1.25D, 0.0D);
      cloned.setYaw(this.getYaw(this.armorStand));
      this.nameTagStand.teleport(cloned);
      Location samePlace = this.nameTagStand.getLocation().clone();
      samePlace.setYaw(this.getYaw(this.armorStand));
      this.armorStand.teleport(samePlace);
   }

   public void createArmorStands() {
      this.nameTagStand = (ArmorStand)Skyblock.getArmorStand(this.player.getLocation());
      this.armorStand = (ArmorStand)Skyblock.getArmorStand(this.player.getLocation());
      this.armorStand.setMarker(true);
      this.armorStand.setCustomNameVisible(false);
      this.armorStand.setArms(true);
      EulerAngle ang = XMaterial.supports(14) ? new EulerAngle(Math.toRadians(315.0D), Math.toRadians(225.0D), 0.0D) : new EulerAngle(0.0D, Math.toRadians(40.0D), 0.0D);
      this.armorStand.setRightArmPose(ang);
      this.armorStand.setGravity(false);
      this.nameTagStand.setGravity(false);
      this.nameTagStand.setMarker(true);
      this.nameTagStand.setCustomNameVisible(false);
      if (!petArmorStands.contains(this.nameTagStand)) {
         petArmorStands.add(this.nameTagStand);
      }

      if (!petArmorStands.contains(this.armorStand)) {
         petArmorStands.add(this.armorStand);
      }

      this.setVisible(PlayerData.getPlayerData().canSeePets(this.player));
      this.setPetVisually();
   }

   private void deadCheck() {
      if (!this.nameTagStand.isDead() && !this.armorStand.isDead()) {
         if (this.player.getWorld() != this.nameTagStand.getWorld()) {
            this.armorStand.teleport(this.player.getLocation());
            this.nameTagStand.teleport(this.armorStand.getLocation());
         }
      } else {
         if (!this.nameTagStand.isDead()) {
            this.nameTagStand.remove();
         }

         if (!this.armorStand.isDead()) {
            this.armorStand.remove();
         }

         this.createArmorStands();
         this.teleportStands();
      }

   }

   private float getYaw(ArmorStand stand) {
      return (float)Math.toDegrees(Math.atan2(this.player.getLocation().getZ() - stand.getLocation().getZ(), this.player.getLocation().getX() - stand.getLocation().getX())) - 90.0F;
   }

   private BukkitRunnable getRunnable() {
      return new BukkitRunnable() {
         boolean up = false;
         double bobbing = 0.0D;

         public void run() {
            PetInstance.this.deadCheck();
            if (PetInstance.this.uuid == null) {
               this.up = false;
            } else {
               Location playerLoc = PetInstance.this.player.getLocation().clone().add(0.0D, 1.0D, 0.0D);
               double distance = Math.sqrt(playerLoc.distanceSquared(PetInstance.this.nameTagStand.getLocation()));
               Location test = PetInstance.this.nameTagStand.getLocation().clone().add(0.0D, -0.5D, 0.0D);
               if (distance > 2.0D) {
                  test.add(playerLoc.toVector().subtract(test.toVector()).normalize().multiply(0.3D));
                  float yaw = PetInstance.this.getYaw(PetInstance.this.nameTagStand);
                  test.setYaw(yaw);
                  Location loc = PetInstance.this.armorStand.getLocation();
                  loc.setYaw(yaw);
                  PetInstance.this.armorStand.teleport(loc);
               }

               test.add(0.0D, 0.5D + this.bobbing, 0.0D);
               if (!this.up) {
                  this.bobbing += 0.0075D;
               } else {
                  this.bobbing -= 0.0075D;
               }

               if (this.bobbing >= 0.08D || this.bobbing <= -0.08D) {
                  this.up = !this.up;
               }

               PetInstance.this.nameTagStand.teleport(test);
               if (distance > 30.0D) {
                  PetInstance.this.armorStand.teleport(PetInstance.this.player.getLocation());
                  PetInstance.this.nameTagStand.teleport(PetInstance.this.armorStand.getLocation());
               }

               PetInstance.this.updateName();
            }
         }
      };
   }

   public void updateName() {
      this.deadCheck();
      double x = Math.cos(Math.toRadians((double)this.nameTagStand.getLocation().getYaw())) * 0.5D;
      double z = Math.sin(Math.toRadians((double)this.nameTagStand.getLocation().getYaw())) * 0.5D;
      this.armorStand.teleport(this.nameTagStand.getLocation().clone().add(x, -0.8D, z));
   }

   public void setVisible(boolean isVisible) {
      Iterator var2 = petArmorStands.iterator();

      while(var2.hasNext()) {
         ArmorStand armorStand = (ArmorStand)var2.next();
         PlayerData.getPlayerData().setCanSeePets(this.player, isVisible);
         Skyblock.getEntityHider().setHide(this.player, armorStand, isVisible);
      }

      this.refreshVisibility();
   }

   public void refreshVisibility() {
      Iterator var1 = this.player.getWorld().getPlayers().iterator();

      while(var1.hasNext()) {
         Player p = (Player)var1.next();
         boolean canSeePets = PlayerData.getPlayerData().canSeePets(p);
         Skyblock.getEntityHider().setHide(p, this.armorStand, canSeePets);
         Skyblock.getEntityHider().setHide(p, this.nameTagStand, canSeePets);
      }

   }

   public void save() {
      if (this.uuid != null) {
         PetData petData = PetData.getPetData();
         if (!petData.getPetExists(this.player, this.uuid)) {
            petData.createPet(this.player, this.uuid, this.pet.getName(), this.level, this.currentXp, this.skin, this.rarity);
         }

         petData.setType(this.player, this.uuid, this.pet.getName());
         petData.setLevel(this.player, this.uuid, this.level);
         petData.setCurrentExp(this.player, this.uuid, this.currentXp);
         petData.setRarity(this.player, this.uuid, this.rarity);
         petData.setSkin(this.player, this.uuid, this.skin);
      }
   }

   public void clear() {
      this.clear(false);
   }

   public void clear(boolean logOff) {
      PetDequipEvent event = new PetDequipEvent(this.player, this.pet, this.rarity, this.level);
      Bukkit.getPluginManager().callEvent(event);
      if (!event.isCancelled()) {
         this.save();
         this.reset(logOff);
         this.nameTagStand.setCustomNameVisible(false);
         this.armorStand.setItemInHand((ItemStack)null);
      }
   }

   public void delete() {
      this.clear(true);
      this.stop();
      this.armorStand.remove();
      this.nameTagStand.remove();
   }

   public Player getPlayer() {
      return this.player;
   }

   public String getUuid() {
      return this.uuid;
   }

   public Pet getPet() {
      return this.pet;
   }

   public int getLevel() {
      return this.level;
   }

   public double getCurrentXp() {
      return this.currentXp;
   }

   public String getSkin() {
      return this.skin;
   }

   public void setSkin(String skin) {
      this.skin = skin;
   }

   public Rarity getRarity() {
      return this.rarity;
   }

   public static ArrayList getPetArmorStands() {
      return petArmorStands;
   }

   public ArmorStand getNameTagStand() {
      return this.nameTagStand;
   }

   public ArmorStand getArmorStand() {
      return this.armorStand;
   }
}

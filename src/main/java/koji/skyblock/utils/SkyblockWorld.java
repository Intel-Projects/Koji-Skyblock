package koji.skyblock.utils;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.bukkit.BlockChangeDelegate;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Difficulty;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.WorldType;
import org.bukkit.World.Environment;
import org.bukkit.World.Spigot;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Item;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.LivingEntity;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

public class SkyblockWorld implements World {
   private static final HashMap worlds = new HashMap();
   World world;
   private boolean inDragonFight = false;
   private EnderDragon dragon = null;

   public static SkyblockWorld getWorld(World world) {
      return (SkyblockWorld)worlds.getOrDefault(world, new SkyblockWorld(world));
   }

   public SkyblockWorld(World world) {
      this.world = world;
      worlds.put(world, this);
   }

   public Block getBlockAt(int x, int y, int z) {
      return this.world.getBlockAt(x, y, z);
   }

   public Block getBlockAt(Location location) {
      return this.world.getBlockAt(location);
   }

   /** @deprecated */
   @Deprecated
   public int getBlockTypeIdAt(int x, int y, int z) {
      return this.world.getBlockTypeIdAt(x, y, z);
   }

   /** @deprecated */
   @Deprecated
   public int getBlockTypeIdAt(Location location) {
      return this.world.getBlockTypeIdAt(location);
   }

   public int getHighestBlockYAt(int x, int z) {
      return this.world.getHighestBlockYAt(x, z);
   }

   public int getHighestBlockYAt(Location location) {
      return this.world.getHighestBlockYAt(location);
   }

   public Block getHighestBlockAt(int x, int z) {
      return this.world.getHighestBlockAt(x, z);
   }

   public Block getHighestBlockAt(Location location) {
      return this.world.getHighestBlockAt(location);
   }

   public Chunk getChunkAt(int x, int z) {
      return this.world.getChunkAt(x, z);
   }

   public Chunk getChunkAt(Location location) {
      return this.world.getChunkAt(location);
   }

   public Chunk getChunkAt(Block block) {
      return this.world.getChunkAt(block);
   }

   public boolean isChunkLoaded(Chunk chunk) {
      return this.world.isChunkLoaded(chunk);
   }

   public Chunk[] getLoadedChunks() {
      return this.world.getLoadedChunks();
   }

   public void loadChunk(Chunk chunk) {
      this.world.loadChunk(chunk);
   }

   public boolean isChunkLoaded(int x, int z) {
      return this.world.isChunkLoaded(x, z);
   }

   public boolean isChunkInUse(int x, int z) {
      return this.world.isChunkInUse(x, z);
   }

   public void loadChunk(int x, int z) {
      this.world.loadChunk(x, z);
   }

   public boolean loadChunk(int x, int z, boolean generate) {
      return this.world.loadChunk(x, z, generate);
   }

   public boolean unloadChunk(Chunk chunk) {
      return this.world.unloadChunk(chunk);
   }

   public boolean unloadChunk(int x, int z) {
      return this.world.unloadChunk(x, z);
   }

   public boolean unloadChunk(int x, int z, boolean save) {
      return this.world.unloadChunk(x, z, save);
   }

   public boolean unloadChunk(int x, int z, boolean save, boolean safe) {
      return this.world.unloadChunk(x, z, save, safe);
   }

   public boolean unloadChunkRequest(int x, int z) {
      return this.world.unloadChunkRequest(x, z);
   }

   public boolean unloadChunkRequest(int x, int z, boolean safe) {
      return this.world.unloadChunkRequest(x, z, safe);
   }

   public boolean regenerateChunk(int x, int z) {
      return this.world.regenerateChunk(x, z);
   }

   /** @deprecated */
   @Deprecated
   public boolean refreshChunk(int x, int z) {
      return this.world.refreshChunk(x, z);
   }

   public Item dropItem(Location location, ItemStack item) {
      return this.world.dropItem(location, item);
   }

   public Item dropItemNaturally(Location location, ItemStack item) {
      return this.world.dropItemNaturally(location, item);
   }

   public Arrow spawnArrow(Location location, Vector direction, float speed, float spread) {
      return this.world.spawnArrow(location, direction, speed, spread);
   }

   public boolean generateTree(Location location, TreeType type) {
      return this.world.generateTree(location, type);
   }

   public boolean generateTree(Location loc, TreeType type, BlockChangeDelegate delegate) {
      return this.world.generateTree(loc, type, delegate);
   }

   public Entity spawnEntity(Location loc, EntityType type) {
      return this.world.spawnEntity(loc, type);
   }

   /** @deprecated */
   @Deprecated
   public LivingEntity spawnCreature(Location loc, EntityType type) {
      return this.world.spawnCreature(loc, type);
   }

   /** @deprecated */
   @Deprecated
   public LivingEntity spawnCreature(Location loc, CreatureType type) {
      return this.world.spawnCreature(loc, type);
   }

   public LightningStrike strikeLightning(Location loc) {
      return this.world.strikeLightning(loc);
   }

   public LightningStrike strikeLightningEffect(Location loc) {
      return this.world.strikeLightningEffect(loc);
   }

   public List getEntities() {
      return this.world.getEntities();
   }

   public List getLivingEntities() {
      return this.world.getLivingEntities();
   }

   /** @deprecated */
   @SafeVarargs
   @Deprecated
   public final Collection getEntitiesByClass(Class... classes) {
      return this.world.getEntitiesByClass(classes);
   }

   public Collection getEntitiesByClass(Class cls) {
      return this.world.getEntitiesByClass(cls);
   }

   public Collection getEntitiesByClasses(Class... classes) {
      return this.world.getEntitiesByClasses(classes);
   }

   public List getPlayers() {
      return this.world.getPlayers();
   }

   public Collection getNearbyEntities(Location location, double x, double y, double z) {
      return this.world.getNearbyEntities(location, x, y, z);
   }

   public String getName() {
      return this.world.getName();
   }

   public UUID getUID() {
      return this.world.getUID();
   }

   public Location getSpawnLocation() {
      return this.world.getSpawnLocation();
   }

   public boolean setSpawnLocation(int x, int y, int z) {
      return this.world.setSpawnLocation(x, y, z);
   }

   public long getTime() {
      return this.world.getTime();
   }

   public void setTime(long time) {
      this.world.setTime(time);
   }

   public long getFullTime() {
      return this.world.getFullTime();
   }

   public void setFullTime(long time) {
      this.world.setFullTime(time);
   }

   public boolean hasStorm() {
      return this.world.hasStorm();
   }

   public void setStorm(boolean hasStorm) {
      this.world.setStorm(hasStorm);
   }

   public int getWeatherDuration() {
      return this.world.getWeatherDuration();
   }

   public void setWeatherDuration(int duration) {
      this.world.setWeatherDuration(duration);
   }

   public boolean isThundering() {
      return this.world.isThundering();
   }

   public void setThundering(boolean thundering) {
      this.world.setThundering(thundering);
   }

   public int getThunderDuration() {
      return this.world.getThunderDuration();
   }

   public void setThunderDuration(int duration) {
      this.world.setThunderDuration(duration);
   }

   public boolean createExplosion(double x, double y, double z, float power) {
      return this.world.createExplosion(x, y, z, power);
   }

   public boolean createExplosion(double x, double y, double z, float power, boolean setFire) {
      return this.world.createExplosion(x, y, z, power, setFire);
   }

   public boolean createExplosion(double x, double y, double z, float power, boolean setFire, boolean breakBlocks) {
      return this.world.createExplosion(x, y, z, power, setFire, breakBlocks);
   }

   public boolean createExplosion(Location loc, float power) {
      return this.world.createExplosion(loc, power);
   }

   public boolean createExplosion(Location loc, float power, boolean setFire) {
      return this.world.createExplosion(loc, power, setFire);
   }

   public Environment getEnvironment() {
      return this.world.getEnvironment();
   }

   public long getSeed() {
      return this.world.getSeed();
   }

   public boolean getPVP() {
      return this.world.getPVP();
   }

   public void setPVP(boolean pvp) {
      this.world.setPVP(pvp);
   }

   public ChunkGenerator getGenerator() {
      return this.world.getGenerator();
   }

   public void save() {
      this.world.save();
   }

   public List getPopulators() {
      return this.world.getPopulators();
   }

   public Entity spawn(Location location, Class clazz) throws IllegalArgumentException {
      return this.world.spawn(location, clazz);
   }

   /** @deprecated */
   @Deprecated
   public FallingBlock spawnFallingBlock(Location location, Material material, byte data) throws IllegalArgumentException {
      return this.world.spawnFallingBlock(location, material, data);
   }

   /** @deprecated */
   @Deprecated
   public FallingBlock spawnFallingBlock(Location location, int blockId, byte blockData) throws IllegalArgumentException {
      return this.world.spawnFallingBlock(location, blockId, blockData);
   }

   public void playEffect(Location location, Effect effect, int data) {
      this.world.playEffect(location, effect, data);
   }

   public void playEffect(Location location, Effect effect, int data, int radius) {
      this.world.playEffect(location, effect, data, radius);
   }

   public void playEffect(Location location, Effect effect, Object data) {
      this.world.playEffect(location, effect, data);
   }

   public void playEffect(Location location, Effect effect, Object data, int radius) {
      this.world.playEffect(location, effect, data, radius);
   }

   public ChunkSnapshot getEmptyChunkSnapshot(int x, int z, boolean includeBiome, boolean includeBiomeTempRain) {
      return this.world.getEmptyChunkSnapshot(x, z, includeBiome, includeBiomeTempRain);
   }

   public void setSpawnFlags(boolean allowMonsters, boolean allowAnimals) {
      this.world.setSpawnFlags(allowMonsters, allowAnimals);
   }

   public boolean getAllowAnimals() {
      return this.world.getAllowAnimals();
   }

   public boolean getAllowMonsters() {
      return this.world.getAllowMonsters();
   }

   public Biome getBiome(int x, int z) {
      return this.world.getBiome(x, z);
   }

   public void setBiome(int x, int z, Biome bio) {
      this.world.setBiome(x, z, bio);
   }

   public double getTemperature(int x, int z) {
      return this.world.getTemperature(x, z);
   }

   public double getHumidity(int x, int z) {
      return this.world.getHumidity(x, z);
   }

   public int getMaxHeight() {
      return this.world.getMaxHeight();
   }

   public int getSeaLevel() {
      return this.world.getSeaLevel();
   }

   public boolean getKeepSpawnInMemory() {
      return this.world.getKeepSpawnInMemory();
   }

   public void setKeepSpawnInMemory(boolean keepLoaded) {
      this.world.setKeepSpawnInMemory(keepLoaded);
   }

   public boolean isAutoSave() {
      return this.world.isAutoSave();
   }

   public void setAutoSave(boolean value) {
      this.world.setAutoSave(value);
   }

   public void setDifficulty(Difficulty difficulty) {
      this.world.setDifficulty(difficulty);
   }

   public Difficulty getDifficulty() {
      return this.world.getDifficulty();
   }

   public File getWorldFolder() {
      return this.world.getWorldFolder();
   }

   public WorldType getWorldType() {
      return this.world.getWorldType();
   }

   public boolean canGenerateStructures() {
      return this.world.canGenerateStructures();
   }

   public long getTicksPerAnimalSpawns() {
      return this.world.getTicksPerAnimalSpawns();
   }

   public void setTicksPerAnimalSpawns(int ticksPerAnimalSpawns) {
      this.world.setTicksPerAnimalSpawns(ticksPerAnimalSpawns);
   }

   public long getTicksPerMonsterSpawns() {
      return this.world.getTicksPerMonsterSpawns();
   }

   public void setTicksPerMonsterSpawns(int ticksPerMonsterSpawns) {
      this.world.setTicksPerMonsterSpawns(ticksPerMonsterSpawns);
   }

   public int getMonsterSpawnLimit() {
      return this.world.getMonsterSpawnLimit();
   }

   public void setMonsterSpawnLimit(int limit) {
      this.world.setMonsterSpawnLimit(limit);
   }

   public int getAnimalSpawnLimit() {
      return this.world.getAnimalSpawnLimit();
   }

   public void setAnimalSpawnLimit(int limit) {
      this.world.setAnimalSpawnLimit(limit);
   }

   public int getWaterAnimalSpawnLimit() {
      return this.world.getWaterAnimalSpawnLimit();
   }

   public void setWaterAnimalSpawnLimit(int limit) {
      this.world.setWaterAnimalSpawnLimit(limit);
   }

   public int getAmbientSpawnLimit() {
      return this.world.getAmbientSpawnLimit();
   }

   public void setAmbientSpawnLimit(int limit) {
      this.world.setAmbientSpawnLimit(limit);
   }

   public void playSound(Location location, Sound sound, float volume, float pitch) {
      this.world.playSound(location, sound, volume, pitch);
   }

   public String[] getGameRules() {
      return this.world.getGameRules();
   }

   public String getGameRuleValue(String rule) {
      return this.world.getGameRuleValue(rule);
   }

   public boolean setGameRuleValue(String rule, String value) {
      return this.world.setGameRuleValue(rule, value);
   }

   public boolean isGameRule(String rule) {
      return this.world.isGameRule(rule);
   }

   public Spigot spigot() {
      return this.world.spigot();
   }

   public WorldBorder getWorldBorder() {
      return this.world.getWorldBorder();
   }

   public void setMetadata(String metadataKey, MetadataValue newMetadataValue) {
      this.world.setMetadata(metadataKey, newMetadataValue);
   }

   public List getMetadata(String metadataKey) {
      return this.world.getMetadata(metadataKey);
   }

   public boolean hasMetadata(String metadataKey) {
      return this.world.hasMetadata(metadataKey);
   }

   public void removeMetadata(String metadataKey, Plugin owningPlugin) {
      this.world.removeMetadata(metadataKey, owningPlugin);
   }

   public void sendPluginMessage(Plugin source, String channel, byte[] message) {
      this.world.sendPluginMessage(source, channel, message);
   }

   public Set getListeningPluginChannels() {
      return this.world.getListeningPluginChannels();
   }

   public World getWorld() {
      return this.world;
   }

   public boolean isInDragonFight() {
      return this.inDragonFight;
   }

   public void setInDragonFight(boolean inDragonFight) {
      this.inDragonFight = inDragonFight;
   }

   public EnderDragon getDragon() {
      return this.dragon;
   }

   public void setDragon(EnderDragon dragon) {
      this.dragon = dragon;
   }
}

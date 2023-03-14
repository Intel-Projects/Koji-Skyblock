package koji.skyblock.player.scoreboard;

public enum ScoreboardState {
   DEFAULT("normal"),
   DRAGON_BATTLE("dragon"),
   SLAYER_QUEST("slayer"),
   DUNGEON_START,
   DUNGEON_IN_PROGRESS,
   DUNGEON_BOSS;

   String state;

   private ScoreboardState(String state) {
      this.state = state;
   }

   private ScoreboardState() {
   }

   public String getState() {
      return this.state;
   }
}

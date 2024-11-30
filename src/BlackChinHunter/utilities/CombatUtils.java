package BlackChinHunter.utilities;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.Player;

import static org.dreambot.api.utilities.Logger.log;

public class CombatUtils {

    private static final Tile WILDERNESS_SAFE_TILE = new Tile(3105, 3523); // Wilderness Level 30 Tile
    private static final int LOW_HEALTH_THRESHOLD = 3; // Health threshold for fleeing
    private static final int MIN_FOOD_COUNT = 5; // Minimum food required to stay in combat
    private static long lastCombatTime = 0; // Timestamp for last combat exit
    private static final int HEALTH_STABLE_THRESHOLD = 80; // Health percentage to consider stable
    private static long healthStableStartTime = 0; // Timestamp for when health became stable
    private static final Area RESET_AREA = new Area(3142, 3773, 3145, 3770); // Replace with actual reset area

    /**
     * Determines if a player is attackable.
     * @param player The player to evaluate.
     * @return True if the player is attackable, false otherwise.
     */
    public static boolean isAttackable(Player player) {
        return player != null && !player.isInCombat() && player.canAttack() && !isThreat(player);
    }

    /**
     * Finds the nearest attackable player.
     * @return The closest attackable player, or null if none are found.
     */
    public static Player getAttackablePlayer() {
        return Players.closest(player ->
                player != null &&
                        !player.getName().equals(Players.getLocal().getName()) && // Exclude local player
                        player.exists() &&
                        !player.isInCombat() &&
                        player.canAttack() &&
                        isValidTarget(player) // Optional: Use additional filtering logic
        );
    }

    /**
     * Updates the last combat timestamp when the player exits combat.
     */
    public static void updateLastCombatTime() {
        if (!Players.getLocal().isInCombat()) {
            lastCombatTime = System.currentTimeMillis();
        }
    }

    /**
     * Determines if combat ended recently within the specified duration.
     * @param duration The duration in milliseconds to check for recent combat.
     * @return True if combat ended within the given duration, false otherwise.
     */
    public static boolean recentlyExitedCombat(long duration) {
        return (System.currentTimeMillis() - lastCombatTime) <= duration;
    }

    /**
     * Checks if the player is currently under attack.
     * @return True if the player is under attack, false otherwise.
     */
    public static boolean isUnderAttack() {
        return Players.getLocal().isInCombat() && Players.getLocal().getHealthPercent() < 50;
    }

    /**
     * Checks if the player's health is critically low.
     * @return True if health is below the low threshold, false otherwise.
     */
    public static boolean isHealthLow() {
        return Players.getLocal().getHealthPercent() < LOW_HEALTH_THRESHOLD;
    }

    /**
     * Determines if a player is a threat based on their combat level.
     * @param player The player to evaluate.
     * @return True if the player is a threat, false otherwise.
     */
    public static boolean isThreat(Player player) {
        if (player == null) return false;
        int levelDifference = player.getLevel() - Players.getLocal().getLevel();
        return levelDifference > 10 && levelDifference <= 20;
    }

    /**
     * Determines if the player should flee based on current conditions.
     * @return True if fleeing is necessary, false otherwise.
     */
    public static boolean shouldFlee() {
        long foodCount = FoodUtils.countFoodInInventory();
        log("Checking if we should flee. Food count: " + foodCount);
        return foodCount < 5; // Example: Flee if fewer than 5 pieces of food remain
    }

    /**
     * Checks if the player's health is stable.
     * @return True if health has remained above the stable threshold for 2 seconds, false otherwise.
     */
    public static boolean isHealthStable() {
        if (Players.getLocal().getHealthPercent() >= HEALTH_STABLE_THRESHOLD) {
            if (healthStableStartTime == 0) {
                healthStableStartTime = System.currentTimeMillis();
            }
            return System.currentTimeMillis() - healthStableStartTime >= 2000;
        } else {
            healthStableStartTime = 0; // Reset timer if health drops below the threshold
            return false;
        }
    }
    public static boolean isValidTarget(Player target) {
        return target != null && target.exists() && !target.isInCombat();
    }
    public static boolean isInCombat() {
        return Players.getLocal().isInCombat()
                || Players.getLocal().getAnimation() != -1;
    }
    public static void optimizePlayerMovement(Player target) {
        if (target != null && target.exists()) {
            double distance = Players.getLocal().distance(target);
            if (distance > 5) { // Example range threshold
                log("Moving closer to target...");
                Walking.walk(target.getTile());
                Sleep.sleepUntil(() -> Players.getLocal().distance(target) <= 5, 2000);
            }
        }
    }
    /**
     * Executes reset logic after combat.
     */
    public static void resetAfterCombat() {
        if (!Players.getLocal().isInCombat()) {
            if (Inventory.contains("Food") && Players.getLocal().getHealthPercent() < 80) {
                Inventory.interact(item -> item.getName().contains("Food"), "Eat");
                Sleep.sleepUntil(() -> Players.getLocal().getHealthPercent() > 80, 2000);
            }

            if (!RESET_AREA.contains(Players.getLocal())) {
                log("Walking to reset area...");
                Walking.walk(RESET_AREA.getRandomTile());
                Sleep.sleepUntil(() -> RESET_AREA.contains(Players.getLocal()), 5000);
            }
        }
    }

    /**
     * Executes flee logic to escape combat safely.
     */
    public static void flee() {
        log("Fleeing toward wilderness safe zone...");
        Walking.walk(WILDERNESS_SAFE_TILE);

        while (Players.getLocal().distance(WILDERNESS_SAFE_TILE) > 1) {
            if (Players.getLocal().isInCombat() && FoodUtils.countFoodInInventory() > 0) {
                log("Eating food while fleeing...");
                Inventory.interact(item -> FoodUtils.isFood(item.getName()), "Eat");
                Sleep.sleepUntil(() -> Players.getLocal().getHealthPercent() > 50, 5000);
            }
            Sleep.sleep(200); // Smooth execution delay
        }

        if (Equipment.contains("Amulet of glory")) {
            log("Using Amulet of Glory to teleport to Edgeville...");
            Equipment.interact(EquipmentSlot.valueOf("Amulet of glory"), "Rub");
            Sleep.sleepUntil(() -> !Players.getLocal().isInCombat(), 5000);
        } else {
            log("No Amulet of Glory found! Continuing to safe zone...");
        }
    }
}

package BlackChinHunter.utilities;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.input.Camera;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;

import java.util.Arrays;
import java.util.List;

import static org.dreambot.api.utilities.Logger.log;

public class NavigationUtils {

    private static final Tile LAVA_MAZE_TILE = new Tile(3028, 3840, 0);
    /**
     * Walks to a specific tile.
     * @param targetTile The tile to navigate to.
     * @return True if the player is at or near the target tile, false otherwise.
     */
    public static boolean walkToTile(Tile targetTile) {
        if (Walking.walk(targetTile)) {
            log("Walking to tile: " + targetTile);
            Sleep.sleepUntil(() -> Players.getLocal().distance(targetTile) <= 1, Calculations.random(4000, 6000));
            return true;
        }
        log("Failed to walk to tile: " + targetTile);
        return false;
    }

    /**
     * Walks to a random tile within an area.
     * @param area The area to navigate to.
     * @return True if the player is within the area, false otherwise.
     */
    public static boolean walkToArea(Area area) {
        Tile randomTile = area.getRandomTile();
        log("Walking to random tile within area: " + randomTile);
        return walkToTile(randomTile);
    }

    /**
     * Validates if the player is already within a specific area.
     * @param area The area to check.
     * @return True if the player is within the area, false otherwise.
     */
    public static boolean isInArea(Area area) {
        return !area.contains(Players.getLocal());
    }

    public static boolean teleportToLavaMaze() {
        if (Inventory.contains(item -> item.getName().startsWith("Burning amulet"))) {
            log("Using Burning Amulet to teleport to Lava Maze...");

            if (Inventory.interact(item -> item.getName().startsWith("Burning amulet"), "Rub")) {
                Sleep.sleepUntil(Dialogues::inDialogue, 3000);

                // Ensure dialogue is active and select "Lava Maze" (option 3)
                if (Dialogues.inDialogue()) {
                    log("Burning Amulet dialogue detected...");
                    if (Dialogues.clickOption(3)) { // Lava Maze is option 3
                        log("Selected Lava Maze teleport option...");
                        Sleep.sleepUntil(Dialogues::canContinue, 3000);

                        if (Dialogues.canContinue()) {
                            Dialogues.continueDialogue(); // Confirm teleport
                            log("Confirmed teleport to Lava Maze.");
                        }

                        // Wait for teleportation to complete
                        Sleep.sleepUntil(() -> Players.getLocal().distance(LAVA_MAZE_TILE) < 10, 5000);
                        return true;
                    } else {
                        log("Failed to select Lava Maze option.");
                    }
                }
            } else {
                log("Failed to interact with Burning Amulet.");
            }
        } else {
            log("No Burning Amulet found in inventory.");
        }
        return false;
    }

    /**
     * Handles obstacles like doors or gates blocking the path.
     */


    public static void handleObstacle() {
        GameObject obstacle = GameObjects.closest(obj -> obj != null && obj.hasAction("Open", "Climb", "Enter"));
        if (obstacle != null) {
            log("Obstacle found: " + obstacle.getName() + " at " + obstacle.getTile());

            List<String> actions = Arrays.asList("Open", "Climb", "Enter");
            for (String action : actions) {
                if (obstacle.hasAction(action)) {
                    if (obstacle.interact(action)) {
                        log("Interacting with obstacle using action: " + action);
                        Sleep.sleepUntil(() -> Players.getLocal().isMoving() || !obstacle.exists(), Calculations.random(3000, 5000));
                        return;
                    } else {
                        log("Failed to interact with obstacle using action: " + action);
                    }
                }
            }
        } else {
            log("No obstacle found to handle.");
        }
    }


    /**
     * Randomizes player movement for anti-detection purposes.
     */
    public static void randomizeMovement() {
        log("Randomizing player movement...");
        if (Calculations.random(0, 100) < 50) {
            Walking.walk(Players.getLocal().getTile().translate(Calculations.random(-3, 3), Calculations.random(-3, 3)));
            Sleep.sleep(Calculations.random(1000, 2000));
        } else {
            Camera.rotateTo(Calculations.random(0, 360), Calculations.random(0, 100));
            Sleep.sleep(Calculations.random(500, 1500));
        }
    }

    /**
     * Ensures the player reaches a destination safely.
     * @param area The target area.
     * @return True if the player reached the destination, false otherwise.
     */
    public static boolean ensureArrival(Area area) {
        while (isInArea(area)) {
            if (!walkToArea(area)) {
                log("Failed to walk to area: " + area);
                return false;
            }
            handleObstacle();
        }
        log("Arrived at destination: " + area);
        return true;
    }
}

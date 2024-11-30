package BlackChinHunter.modules.banking;

import BlackChinHunter.Framework.Branch;
import BlackChinHunter.utilities.NavigationUtils;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;

import static org.dreambot.api.utilities.Logger.log;

public class NavigateToBlackChinchompasLeaf extends Branch {

    private static final Area BLACK_CHINS_AREA = new Tile(3143, 3772, 0).getArea(5); // Black Chinchompa area
    private static final Tile CHINCHOMPA_TILE = new Tile(3150, 3771, 0);

    @Override
    public boolean validate() {
        boolean notAtBlackChins = !NavigationUtils.isInArea(new Tile(3143, 3772, 0).getArea(5));
        log("Validating NavigateToBlackChinchompasLeaf: Not at Black Chins: " + notAtBlackChins);
        return notAtBlackChins; // Trigger navigation if not at Black Chins
    }

    @Override
    public int execute() {
        log("Navigating to Black Chinchompa hunting area...");
        if (NavigationUtils.teleportToLavaMaze()) {
            log("Teleport to Lava Maze successful. Walking to Black Chinchompa area...");
        } else {
            log("Teleport failed or not available. Attempting to walk directly...");
        }

        // Walk to the Black Chinchompa area
        if (NavigationUtils.walkToTile(CHINCHOMPA_TILE)) {
            log("Successfully navigated to the Black Chinchompa area.");
        } else {
            log("Failed to navigate to the Black Chinchompa area.");
        }

        return 300;
    }
}
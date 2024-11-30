package BlackChinHunter.utilities;

import org.dreambot.api.methods.tabs.Tabs;
import org.dreambot.api.methods.world.World;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.api.utilities.Sleep;

import java.util.*;
import java.util.stream.Collectors;

import static org.dreambot.api.utilities.Logger.log;

public class WorldHoppingUtils {

    private static final List<Integer> P2P_WORLDS = new ArrayList<>();
    private static final Map<Integer, Long> RECENT_WORLDS = new HashMap<>();
    private static final long RECENT_WORLD_TIMEOUT = 120_000; // 2 minutes in milliseconds

    static {
        // Pre-fetch all P2P worlds at startup
        P2P_WORLDS.addAll(Worlds.all().stream()
                .filter(world -> world.isMembers() && !world.isDeadmanMode() && !world.isTournamentWorld())
                .map(World::getWorld)
                .collect(Collectors.toList()));
    }

    /**
     * Hops to a random P2P world, avoiding recently used worlds.
     */
    public static void hopToRandomP2PWorld() {
        int currentWorld = Worlds.getCurrentWorld();
        List<Integer> eligibleWorlds = P2P_WORLDS.stream()
                .filter(world -> world != currentWorld && !isWorldRecent(world))
                .collect(Collectors.toList());

        if (eligibleWorlds.isEmpty()) {
            log("No eligible P2P worlds found!");
            return;
        }

        // Sort worlds by proximity to minimize scrolling
        eligibleWorlds.sort(Comparator.comparingInt(world -> Math.abs(world - currentWorld)));

        // Pick a random world from the top 3 closest worlds
        int targetWorld = eligibleWorlds.get(new Random().nextInt(Math.min(6, eligibleWorlds.size())));

        log("Hopping to world: " + targetWorld);
        if (WorldHopper.hopWorld(targetWorld)) {
            RECENT_WORLDS.put(targetWorld, System.currentTimeMillis());
            Sleep.sleepUntil(() -> Worlds.getCurrentWorld() == targetWorld, 5000);
        } else {
            log("Failed to hop to world: " + targetWorld);
        }
    }

    /**
     * Logs out rapidly by interacting with the logout tab.
     */
    public static void fastLogout() {
        log("Logging out immediately...");
        Tabs.logout();
        {
        }
    }
    /**
     * Checks if a world is considered "recent" based on the timeout period.
     *
     * @param world The world to check.
     * @return True if the world was used recently, false otherwise.
     */
    private static boolean isWorldRecent(int world) {
        return RECENT_WORLDS.containsKey(world) &&
                System.currentTimeMillis() - RECENT_WORLDS.get(world) < RECENT_WORLD_TIMEOUT;
    }

    /**
     * Clears old worlds from the recent world cache to avoid memory buildup.
     */
    private static void cleanupRecentWorlds() {
        long now = System.currentTimeMillis();
        RECENT_WORLDS.entrySet().removeIf(entry -> now - entry.getValue() > RECENT_WORLD_TIMEOUT);
    }
}

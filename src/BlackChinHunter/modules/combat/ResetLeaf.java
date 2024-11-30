package BlackChinHunter.modules.combat;

import BlackChinHunter.Framework.Branch;
import BlackChinHunter.utilities.CombatUtils;
import BlackChinHunter.utilities.LootUtils;
import BlackChinHunter.utilities.NavigationUtils;
import BlackChinHunter.utilities.WorldHoppingUtils;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.wrappers.items.GroundItem;

import static org.dreambot.api.utilities.Logger.log;

public class ResetLeaf extends Branch {

    private static final long RECENT_COMBAT_DURATION = 10_000; // 10 seconds

    @Override
    public boolean validate() {
        GroundItem valuableLoot = LootUtils.findValuableLoot();
        return CombatUtils.recentlyExitedCombat(RECENT_COMBAT_DURATION)
                && CombatUtils.getAttackablePlayer() == null
                && valuableLoot == null // Ensure no valuable loot is nearby
                && CombatUtils.isHealthStable();
    }

    @Override
    public int execute() {
        log("Resetting after combat...");
        CombatUtils.resetAfterCombat();

        // Define the reset area using the coordinates
        Area resetArea = new Area(3142, 3773, 3145, 3770);
        if (NavigationUtils.isInArea(resetArea)) {
            log("Player is not in the reset area. Navigating there...");
            NavigationUtils.ensureArrival(resetArea);
        }

        log("Reset complete; preparing to hop worlds...");
        WorldHoppingUtils.hopToRandomP2PWorld();

        return 300; // Delay before next validation
    }
}
package BlackChinHunter.modules.worldhopping;

import BlackChinHunter.Framework.Branch;
import BlackChinHunter.utilities.WorldHoppingUtils;
import BlackChinHunter.utilities.CombatUtils;

import static org.dreambot.api.utilities.Logger.log;

public class HopRandomWorldLeaf extends Branch {

    @Override
    public boolean validate() {
        // Validate world hop conditions:
        // - Not in combat
        // - No immediate need to flee
        // - Safe to hop
        return !CombatUtils.isUnderAttack() && !CombatUtils.shouldFlee();
    }

    @Override
    public int execute() {
        log("Preparing to hop to a random P2P world...");
        WorldHoppingUtils.hopToRandomP2PWorld();
        return 1000; // Short delay after hopping
    }
}

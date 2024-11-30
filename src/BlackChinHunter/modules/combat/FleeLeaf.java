package BlackChinHunter.modules.combat;

import BlackChinHunter.Framework.Branch;
import BlackChinHunter.utilities.CombatUtils;

import static org.dreambot.api.utilities.Logger.log;

public class FleeLeaf extends Branch {

    @Override
    public boolean validate() {
        return CombatUtils.shouldFlee(); // Low food, low health, or threats
    }

    @Override
    public int execute() {
        log("Executing flee logic...");
        CombatUtils.flee();// Calls the enhanced flee method
        return 500; // Delay before next action
    }
}

package BlackChinHunter.modules.combat;

import BlackChinHunter.Framework.Branch;
import BlackChinHunter.utilities.CombatUtils;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.Player;

import static org.dreambot.api.utilities.Logger.log;

public class AttackPlayerLeaf extends Branch {

    @Override
    public boolean validate() {
        return CombatUtils.getAttackablePlayer() != null;
    }

    @Override
    public int execute() {
        Player target = CombatUtils.getAttackablePlayer();
        if (target != null) {
            log("Attacking player: " + target.getName());
            if (target.interact("Attack")) {
                Sleep.sleepUntil(() -> Players.getLocal().isInCombat(), 3000);
                return 200;
            } else {
                log("Failed to attack: " + target.getName());
            }
        } else {
            log("No valid targets found.");
        }
        return 300; // Delay if no action is taken
    }
}

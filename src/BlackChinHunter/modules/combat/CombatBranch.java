package BlackChinHunter.modules.combat;

import BlackChinHunter.Framework.Branch;
import BlackChinHunter.utilities.CombatUtils;
import BlackChinHunter.utilities.FoodUtils;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.utilities.Sleep;

import static org.dreambot.api.utilities.Logger.log;

public class CombatBranch extends Branch {

    private Player currentTarget; // To store the current target during combat
    private long lastPotionTime = 0; // Timestamp for when the last potion was used
    private static final long POTION_COOLDOWN = 300_000; // 5 minutes cooldown for using potions
    private long attackTimeoutStart = 0; // Timer to avoid deadlocks
    private static final long ATTACK_TIMEOUT_DURATION = 5000; // 5 seconds timeout

    public CombatBranch() {
        // Add child leaves
        addChild(new AttackPlayerLeaf());
        addChild(new FleeLeaf());
        addChild(new ResetLeaf());
    }

    @Override
    public boolean validate() {
        // Validate if combat actions are needed
        boolean shouldFlee = CombatUtils.shouldFlee(); // Check if fleeing is necessary
        boolean hasAttackablePlayer = CombatUtils.getAttackablePlayer() != null; // Check for valid targets

        // Combat actions are valid if not fleeing and there is a target
        return !shouldFlee && hasAttackablePlayer;
    }

    @Override
    public int execute() {
        log("Executing combat-related actions...");

        // Priority check for fleeing
        if (CombatUtils.shouldFlee()) {
            log("Conditions met for fleeing; delegating to FleeLeaf.");
            return super.execute(); // FleeLeaf will handle the logic
        }

        // Use Ranging Potion if available and cooldown has passed
        if (System.currentTimeMillis() - lastPotionTime > POTION_COOLDOWN
                && Inventory.contains(item -> item.getName().startsWith("Ranging potion"))) {
            log("Drinking a dose of Ranging Potion...");
            Inventory.interact(item -> item.getName().startsWith("Ranging potion"), "Drink");
            lastPotionTime = System.currentTimeMillis();
            Sleep.sleep(800, 1200); // Simulate human-like delay
        }

        // Check for attackable player
        Player target = CombatUtils.getAttackablePlayer();

        // Re-Attack Logic
        if (currentTarget != null && !CombatUtils.isValidTarget(currentTarget)) {
            log("Current target is no longer valid; resetting target.");
            currentTarget = null;
        }

        if (target != null) {
            // Update current target and reset attack timeout timer
            if (currentTarget == null) {
                currentTarget = target;
                attackTimeoutStart = System.currentTimeMillis();
            }

            log("Attacking player: " + currentTarget.getName());

            // Combat-Only Resource Consumption (Eat Food if Health is Low)
            if (CombatUtils.isHealthLow()) {
                log("Health is low; eating food...");
                if (Inventory.interact(item -> FoodUtils.isFood(item.getName()), "Eat")) {
                    Sleep.sleepUntil(() -> !CombatUtils.isHealthLow(), 2000);
                    log("Recovered health; re-engaging with target.");
                }
            }

            // Avoid Deadlocks
            if (System.currentTimeMillis() - attackTimeoutStart > ATTACK_TIMEOUT_DURATION) {
                log("Attack timeout reached; re-evaluating target.");
                currentTarget = null; // Reset target
                return 200; // Delay to allow re-evaluation
            }

            // Attack the current target
            if (currentTarget.interact("Attack")) {
                Sleep.sleepUntil(CombatUtils::isInCombat, 3000);
            } else {
                log("Failed to attack; re-evaluating target.");
                currentTarget = null; // Reset target on failure
            }
        } else {
            log("No valid targets found.");
        }

        return super.execute(); // Continue execution for any lower-priority leaves
    }
}

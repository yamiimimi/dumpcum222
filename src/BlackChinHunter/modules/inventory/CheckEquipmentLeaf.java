package BlackChinHunter.modules.inventory;

import BlackChinHunter.Framework.Branch;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import static org.dreambot.api.utilities.Logger.log;

public class CheckEquipmentLeaf extends Branch {

    private static final int MINIMUM_RUNE_DART_COUNT = 50;

    @Override
    public boolean validate() {
        int equippedRuneDarts = Equipment.count("Rune dart");
        log("Rune Darts equipped: " + equippedRuneDarts);
        return equippedRuneDarts < MINIMUM_RUNE_DART_COUNT; // Validate if Rune Darts are below threshold
    }

    @Override
    public int execute() {
        log("Rune Darts are below the threshold! Transitioning to banking...");
        return new BlackChinHunter.modules.banking.BankingBranch().execute(); // Transition to banking branch
    }
}

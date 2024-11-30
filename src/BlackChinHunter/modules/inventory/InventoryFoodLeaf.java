package BlackChinHunter.modules.inventory;

import BlackChinHunter.Framework.Branch;
import BlackChinHunter.utilities.FoodUtils;
import static org.dreambot.api.utilities.Logger.log;

public class InventoryFoodLeaf extends Branch {

    private static final int MINIMUM_FOOD_COUNT = 5;

    @Override
    public boolean validate() {
        int foodCount = (int) FoodUtils.countFoodInInventory();
        log("Current food count: " + foodCount);
        return foodCount < MINIMUM_FOOD_COUNT; // Validate if food is below threshold
    }

    @Override
    public int execute() {
        log("Food count is below the threshold! Transitioning to banking...");
        return new BlackChinHunter.modules.banking.BankingBranch().execute(); // Transition to banking branch
    }
}

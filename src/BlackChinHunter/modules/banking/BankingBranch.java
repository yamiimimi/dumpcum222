package BlackChinHunter.modules.banking;

import BlackChinHunter.Framework.Branch;
import BlackChinHunter.utilities.InventoryUtils;
import org.dreambot.api.methods.interactive.Players;

import static org.dreambot.api.utilities.Logger.log;

public class BankingBranch extends Branch {

    private boolean completedBanking = false;

    public BankingBranch() {
        // Add leaves in order of execution
        addChild(new NavigateToBankLeaf());
        addChild(new DepositLootLeaf());
        addChild(new WithdrawSuppliesLeaf());
        addChild(new NavigateToBlackChinchompasLeaf()); // Add navigation leaf here
    }

    @Override
    public boolean validate() {
        boolean needsBanking = InventoryUtils.hasNonEssentialItems() || InventoryUtils.needsRestocking();
        boolean notAtBlackChins = !new NavigateToBlackChinchompasLeaf().validate(); // Validate navigation
        boolean completed = completedBanking && notAtBlackChins;

        log("Validating BankingBranch... Needs banking: " + needsBanking + ", Completed banking: " + completed + ", Not at Black Chins: " + notAtBlackChins);
        return (needsBanking || !completed) && !completedBanking;
    }

    @Override
    public int execute() {
        log("Executing BankingBranch...");
        super.execute();

        boolean allTasksComplete = !InventoryUtils.hasNonEssentialItems()
                && !InventoryUtils.needsRestocking()
                && new NavigateToBlackChinchompasLeaf().validate();

        if (allTasksComplete) {
            log("Banking tasks and navigation complete. Exiting BankingBranch...");
            completedBanking = true;
        }

        return 300; // Delay between executions
    }

    public void resetBankingState() {
        completedBanking = false;
        log("BankingBranch state reset.");
    }
}

package BlackChinHunter.modules.worldhopping;

import BlackChinHunter.Framework.Branch;


import static org.dreambot.api.utilities.Logger.log;

public class WorldHoppingBranch extends Branch {

    public WorldHoppingBranch() {
        addChild(new HopRandomWorldLeaf());
    }

    @Override
    public boolean validate() {
        log("Validating World Hopping Branch...");
        // Example: Always valid to keep the branch minimal
        return true;
    }
}

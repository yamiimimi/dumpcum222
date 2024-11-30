package BlackChinHunter.Framework;

import java.util.ArrayList;
import java.util.List;

public abstract class Branch {

    private final List<Branch> children = new ArrayList<>();

    public void addChild(Branch child) {
        children.add(child);
    }

    public int execute() {
        for (Branch child : children) {
            if (child.validate()) {
                return child.execute();
            }
        }
        return 300;
    }

    public abstract boolean validate();
}
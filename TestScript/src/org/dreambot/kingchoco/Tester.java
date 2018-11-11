package org.dreambot.kingchoco;

import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;

@ScriptManifest(author = "kingchoco", category = Category.MISC, description = "SCRIPT TESTER", name = "SCRIPT TESTER", version = 1.0)
public class Tester extends AbstractScript {

    private final int FISHING_ANIMATION = 623;

    @Override
    public int onLoop() {
        if (getInventory().isFull()) {

        }
        return 0;
    }

    @Override
    public void stop() {
        super.stop();
    }
}

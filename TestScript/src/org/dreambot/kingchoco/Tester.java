package org.dreambot.kingchoco;

import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.wrappers.interactive.Entity;
import org.dreambot.api.wrappers.interactive.NPC;

import java.util.concurrent.TimeUnit;

@ScriptManifest(author = "kingchoco", category = Category.MISC, description = "SCRIPT TESTER", name = "SCRIPT TESTER", version = 1.0)
public class Tester extends AbstractScript
{

    private final int FISHING_ANIMATION = 623;

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public int onLoop()
    {
        getCamera().rotateTo(383,1366);
        NPC fishingSpot = getNpcs().closest(1526);
        fishingSpot.interact("Lure");
        return 10000;
    }

    @Override
    public void stop() {
        super.stop();
    }
}

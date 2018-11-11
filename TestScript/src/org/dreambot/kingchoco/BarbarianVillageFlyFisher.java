package org.dreambot.kingchoco;

import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.wrappers.interactive.Player;

@ScriptManifest(version = 1.0, name = "Barbarian Village FlyFishing", description = "Fly fishes trout and salmon" +
        "and banks them", category = Category.FISHING, author = "KingChoco")
public class BarbarianVillageFlyFisher extends AbstractScript
{
    private final Area BARBARIAN_FISHING_AREA = new Area(new Tile(), new Tile());
    private final Area EDGEVILLE_BANKING_AREA = new Area(new Tile(), new Tile());
    private final int FLY_FISHING_ROD_ID = 309;
    private final int FEATHERS_ID = 814;
    private final int FISHING_ANIMATION_ID = 623;



    private enum PlayerState
    {
        WALK_TO_BANK, WALKING_TO_FISHING_SPOT, FISHING, BANKING;
    }
    private PlayerState currentState;
    private Walking walkingObject;

    private Player localPlayer;

    private Bank edgevilleBank;

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public int onLoop() {
        
        return 0;
    }
}

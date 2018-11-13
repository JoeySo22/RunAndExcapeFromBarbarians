package org.dreambot.kingchoco;

import org.dreambot.api.methods.container.impl.Inventory;
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
    private final Area BARBARIAN_FISHING_AREA = new Area(new Tile(3109, 3439), new Tile(3100, 3422));
    private final Area EDGEVILLE_BANKING_AREA = new Area(new Tile(3094, 3493), new Tile(3091, 3488));
    private final int FLY_FISHING_ROD_ID = 309;
    private final int FEATHERS_ID = 814;
    private final int FISHING_ANIMATION_ID = 623;
    private final int RAW_TROUT_ID = 335;
    private final int RAW_SALMON_ID = 331;

    private enum PlayerState
    {
        WALK_TO_BANK, WALKING_TO_FISHING_SPOT, FISHING, BANKING;
    }

    private PlayerState currentState;

    private Walking walkingObject;

    private Player localPlayer;

    private Bank edgevilleBank;

    @Override
    public void onStart()
    {
        log("onStart begun.");
        super.onStart();
        this.walkingObject = getWalking();
        this.localPlayer = getLocalPlayer();
        this.
    }

    @Override
    public int onLoop()
    {
        log("onLoop begins.");
        currentState = checkPlayerState();
        switch (currentState)
        {
            case BANKING:
                banking();
                break;
            case FISHING:
                fishing();
                break;
            case WALK_TO_BANK:
                walkingToBank();
                break;
            case WALKING_TO_FISHING_SPOT:
                walkingToFishingSpot();
                break;
        }
        log("onLoop ends.");
        return 0;
    }

    private PlayerState checkPlayerState()
    {
        log("checkPlayerState open.");
        Inventory inventory = getInventory();
        Tile currentPlayerTile = getLocalPlayer().getTile();
        boolean playerInFishingTile = BARBARIAN_FISHING_AREA.contains(currentPlayerTile);
        boolean playerInBankingTile = EDGEVILLE_BANKING_AREA.contains(currentPlayerTile);
        boolean playerHasFlyFishingRod = inventory.contains(FLY_FISHING_ROD_ID);
        boolean playerHasFeathers = inventory.contains(FEATHERS_ID);
        boolean playerHasFullInventory = inventory.isFull();

        if (!playerHasFeathers || !playerHasFlyFishingRod)
        {
            return PlayerState.WALK_TO_BANK;
        }
        if (playerHasFeathers && playerHasFlyFishingRod && playerHasFullInventory)
        {
            return PlayerState.WALK_TO_BANK;
        }
        if (playerHasFeathers && playerHasFlyFishingRod && !playerHasFullInventory)
        {
            return PlayerState.WALKING_TO_FISHING_SPOT;
        }
        if (playerInFishingTile && !playerHasFullInventory)
            return PlayerState.WALK_TO_BANK;
        log("checkPlayerState exit.");
    }
}

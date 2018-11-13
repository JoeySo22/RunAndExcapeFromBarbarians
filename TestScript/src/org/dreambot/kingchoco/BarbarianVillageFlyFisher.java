package org.dreambot.kingchoco;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.interactive.Player;

import java.util.concurrent.TimeUnit;

@ScriptManifest(version = 1.0, name = "Barbarian Village FlyFishing", description = "Fly fishes trout and salmon" +
        "and banks them", category = Category.FISHING, author = "KingChoco")
public class BarbarianVillageFlyFisher extends AbstractScript
{
    private final Area BARBARIAN_FISHING_AREA = new Area(new Tile(3109, 3439), new Tile(3100, 3422));
    private final Area EDGEVILLE_BANKING_AREA = new Area(new Tile(3094, 3493), new Tile(3091, 3488));
    private final int FLY_FISHING_ROD_ID = 309;
    private final int FEATHERS_ID = 814;
    private final int FISHING_ANIMATION_ID = 623;
    private final int ROD_FISHING_SPOT_ID = 1526;

    private boolean playerInFishingTile;
    private boolean playerInBankingTile;
    private boolean playerHasFlyFishingRod;
    private boolean playerHasFeathers;
    private boolean playerHasFullInventory;

    private enum PlayerState
    {
        WALK_TO_BANK, WALKING_TO_FISHING_SPOT, FISHING, BANKING;
    }

    private Inventory inventory;

    private Tile currentPlayerTile;

    private PlayerState currentState;

    private Walking walkingObject;

    private Player localPlayer;

    private BankLocation edgevilleBank;

    @Override
    public void onStart()
    {
        log("onStart begun.");
        super.onStart();
        this.walkingObject = getWalking();
        this.localPlayer = getLocalPlayer();
        this.edgevilleBank = BankLocation.EDGEVILLE;
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
            case WALKING_TO_FISHING_SPOT:
                walkingToFishingSpot();
                break;
            case FISHING:
            fishing();
            break;
            case WALK_TO_BANK:
            walkingToBank();
            break;
        }
        log("onLoop ends.");
        return 0;
    }

    private PlayerState checkPlayerState()
    {
        log("checkPlayerState open.");
        updateStatus();
        if (!playerHasFeathers || !playerHasFlyFishingRod)
        {
            if (playerInBankingTile)
            {
                return PlayerState.BANKING;
            }
            else
            {
                return PlayerState.WALK_TO_BANK;
            }
        }
        if (playerHasFeathers && playerHasFlyFishingRod)
        {
            if (playerHasFullInventory)
            {
                if (playerInBankingTile)
                {
                    return PlayerState.BANKING;
                }
                else
                {
                    return PlayerState.WALK_TO_BANK;
                }
            }
            if (!playerHasFullInventory)
            {
                if (playerInFishingTile)
                {
                    return PlayerState.FISHING;
                }
                else
                {
                    return PlayerState.WALKING_TO_FISHING_SPOT;
                }
            }
        }
        else {
            return PlayerState.WALK_TO_BANK;
        }
        return PlayerState.WALK_TO_BANK;
    }

    private void updateStatus()
    {
        this.inventory = getInventory();
        this.currentPlayerTile = getLocalPlayer().getTile();
        this.playerInFishingTile = BARBARIAN_FISHING_AREA.contains(currentPlayerTile);
        this.playerInBankingTile = EDGEVILLE_BANKING_AREA.contains(currentPlayerTile);
        this.playerHasFlyFishingRod = inventory.contains(FLY_FISHING_ROD_ID);
        this.playerHasFeathers = inventory.contains(FEATHERS_ID);
        this.playerHasFullInventory = inventory.isFull();
    }

    private void banking()
    {
        log("banking activated.");
        updateStatus();
        Bank bank = getBank();
        bank.depositAllExcept(FLY_FISHING_ROD_ID, FEATHERS_ID);
        if (!playerHasFeathers)
        {
            if (!bank.withdraw(FEATHERS_ID, 3000))
            {
                stop();
            }
        }
        if (!playerHasFlyFishingRod)
        {
            bank.withdraw(FLY_FISHING_ROD_ID);
        }
        bank.close();
        currentState = PlayerState.WALKING_TO_FISHING_SPOT;
    }

    private void walkingToFishingSpot()
    {
        walkTo(BARBARIAN_FISHING_AREA.getRandomTile(), true);
        currentState = PlayerState.FISHING;
    }

    private void walkingToBank()
    {
        walkTo(EDGEVILLE_BANKING_AREA.getRandomTile(), true);
        currentState = PlayerState.BANKING;
    }

    private void fishing()
    {
        NPC rodFishingSpot = getNpcs().closest(ROD_FISHING_SPOT_ID);
        rodFishingSpot.interact("Lure");
    }

    private void walkTo(Tile tile, boolean walking)
    {
        log("walkTo: " + tile.toString());

        int steps = 1;
        while (!localPlayer.getTile().equals(tile))
        {
            log("Step..." + String.valueOf(steps));
            steps++;
            walkingObject.walk(tile);
            try {
                TimeUnit.MILLISECONDS.sleep(randomNumberGenerator());
            }
            catch (InterruptedException e)
            {
                log("Couldn't sleep.");
            }
        }
        log("Stopped walkTo.");
    }

    @Override
    public void stop()
    {
        super.stop();
    }

    private long randomNumberGenerator() { return Math.round(Math.random() * 2000) + 5000; }
}

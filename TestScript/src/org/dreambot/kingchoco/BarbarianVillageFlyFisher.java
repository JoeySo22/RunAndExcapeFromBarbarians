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

    private Bank edgevilleBank;

    private Inventory inventory;

    private Tile currentPlayerTile;

    private PlayerState currentState;

    private Walking walkingObject;

    private Player localPlayer;

    @Override
    public void onStart()
    {
        log("onStart begun.");
        super.onStart();
        this.walkingObject = getWalking();
        this.localPlayer = getLocalPlayer();
        this.edgevilleBank = getBank();
        log("onStart finished.");
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
                log("checkPlayerState - BANKING.");
                return PlayerState.BANKING;
            }
            else
            {
                log("checkPlayerState - WALK_TO_BANK");
                return PlayerState.WALK_TO_BANK;
            }
        }
        if (playerHasFeathers && playerHasFlyFishingRod)
        {
            if (playerHasFullInventory)
            {
                if (playerInBankingTile)
                {
                    log("checkPlayerState - BANKING");
                    return PlayerState.BANKING;
                }
                else
                {
                    log("checkPlayerState - WALK_TO_BANK");
                    return PlayerState.WALK_TO_BANK;
                }
            }
            if (!playerHasFullInventory)
            {
                if (playerInFishingTile)
                {
                    log("checkPlayerState - FISHING");
                    return PlayerState.FISHING;
                }
                else
                {
                    log("checkPlayerState - WALKING_TO_FISHING_SPOT");
                    return PlayerState.WALKING_TO_FISHING_SPOT;
                }
            }
        }
        else {
            log(("checkPlayerState - WALK_TO_BANK"));
            return PlayerState.WALK_TO_BANK;
        }
        log("checkPlayerState - WALK_TO_BANK");
        return PlayerState.WALK_TO_BANK;
    }

    private void updateStatus()
    {
        log("updateStatus called.");
        this.inventory = getInventory();
        this.currentPlayerTile = getLocalPlayer().getTile();
        this.playerInFishingTile = BARBARIAN_FISHING_AREA.contains(currentPlayerTile);
        this.playerInBankingTile = EDGEVILLE_BANKING_AREA.contains(currentPlayerTile);
        this.playerHasFlyFishingRod = inventory.contains(FLY_FISHING_ROD_ID);
        this.playerHasFeathers = inventory.contains(FEATHERS_ID);
        this.playerHasFullInventory = inventory.isFull();
        log("updateStatus exited.");
    }

    private void banking()
    {
        log("banking activated.");
        this.edgevilleBank.openClosest();
        this.edgevilleBank.depositAllExcept(FLY_FISHING_ROD_ID, FEATHERS_ID);
        if (!playerHasFeathers)
        {
            if (!this.edgevilleBank.withdraw(FEATHERS_ID, 3000))
            {
                log("stop called.");
                stop();
            }
        }
        if (!playerHasFlyFishingRod)
        {
            this.edgevilleBank.withdraw(FLY_FISHING_ROD_ID);
        }
        this.edgevilleBank.close();
        log("banking de-activated.");
    }

    private void walkingToFishingSpot()
    {
        log("walkingToFishingSpot called.");
        walkTo(BARBARIAN_FISHING_AREA.getRandomTile(), true);
        log("walkingToFishingSpot exited.");
    }

    private void walkingToBank()
    {
        log("walkingToBank called.");
        walkTo(EDGEVILLE_BANKING_AREA.getRandomTile(), true);
        log("walkingToBank exited");
    }

    private void fishing()
    {
        log("fishing called.");
        while (!this.playerHasFullInventory)
        {
            NPC rodFishingSpot = getNpcs().closest(ROD_FISHING_SPOT_ID);
            rodFishingSpot.interact("Lure");
            currentlyFishingDelay();
            this.playerHasFullInventory = getInventory().isFull();
        }
        log("fishing exited.");
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

    private void currentlyFishingDelay()
    {
        log("currentlyFishingDelay called.");
        while (!localPlayer.isStandingStill())
        {
            try
            {
                TimeUnit.MILLISECONDS.sleep(randomNumberGenerator());
            }
            catch (InterruptedException e)
            {
                log("Did not sleep.");
            }
        }
        log("currentlyFishingDelay exited");
    }

    @Override
    public void stop()
    {
        super.stop();
    }

    private long randomNumberGenerator() { return Math.round(Math.random() * 2000) + 5000; }
}

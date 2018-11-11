package org.dreambot.kingchoco;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.Player;

import java.util.concurrent.TimeUnit;

@ScriptManifest(version = 1.95, name = "Barbarian Village Coal", description = "Mines & banks coal at Barbarian Village",
                category = Category.MINING, author = "KingChoco")

public class BarbarianVillageCoalMiner extends AbstractScript
{
    private final Tile BARB_MINING_TILE = new Tile(3083,3423);

    private final Tile EDGE_BANK_TILE = BankLocation.EDGEVILLE.getCenter();
    private final int RUNE_PICKAXE_ID = 1275;

    private final int COAL_ORE_COLOR_ID = 7456;
    private final int CAMERA_PITCH = 318;

    private final int CAMERA_YAW = 772;
    private final int MINING_ANIMATION_ID = 624;

    private enum PlayerState
    {
        WALK_TO_BANK, WALK_TO_MINING_SPOT, MINING, BANKING
    }

    private Walking walkingObject;

    private Player localPlayer;

    private Bank edgevilleBank;

    private PlayerState currentState;



    @Override
    public void onStart()
    {
        log("OnStart begun.");
        super.onStart();
        this.walkingObject = getWalking();
        if (walkingObject.isRunEnabled())
        {
            try {
                // We need to delay the time to act on toggling the run otherwise it won't do it.
                TimeUnit.MILLISECONDS.sleep(1000);
                walkingObject.toggleRun();
            }
            catch (InterruptedException e)
            {
                log("didn't sleep.");
            }
        }
        this.localPlayer = getLocalPlayer();
        this.edgevilleBank = getBank();
        log("OnStart exit.");
    }

    @Override
    public int onLoop()
    {
        log("Onloop begins.");
        currentState = checkPlayerState();
        switch (currentState)
        {
            case WALK_TO_BANK:              walkingToBank();
                                            break;
            case WALK_TO_MINING_SPOT:       walkingToMiningSpot();
                                            break;
            case BANKING:                   banking();
                                            break;
            case MINING:                    mining();
                                            break;
        }
        log("onLoop exits.");
        enforceWalking();
        return 0;
    }

    private PlayerState checkPlayerState()
    {
        log("checkPlayerState open.");
        Inventory inventory = getInventory();
        Tile currentPlayerTile = getLocalPlayer().getTile();
        boolean playerInMiningTile = BARB_MINING_TILE.equals(currentPlayerTile);
        boolean playerInEdgevilleBankTile = EDGE_BANK_TILE.equals(currentPlayerTile);
        boolean playerHasRunePickaxe = inventory.contains(RUNE_PICKAXE_ID);
        boolean playerHasFullInventory = inventory.isFull();

        if (playerHasRunePickaxe & playerHasFullInventory)
        {
            if (playerInEdgevilleBankTile)
            {
                log("has pickaxe and has full inventory and is in bank tile. Banking...");
                return PlayerState.BANKING;
            }
            else
            {
                log("has pickaxe and has full inventory and is not in bank area. Walking to Edgeville bank...");
                return PlayerState.WALK_TO_BANK;
            }
        }

        if (playerHasRunePickaxe & !playerHasFullInventory)
        {
            if (playerInMiningTile)
            {
                log("has pickaxe and does not have full inventory and is in mining area. Mining...");
                return PlayerState.MINING;
            }
            else
            {
                log("has pickaxe and does not have full inventory and is not in mining area. Walking to mine...");
                return PlayerState.WALK_TO_MINING_SPOT;
            }
        }
        if (!playerHasRunePickaxe)
        {
            if (!playerInEdgevilleBankTile)
            {
                log("does not have pickaxe and is not in bank. Walking to bank...");
                return PlayerState.WALK_TO_BANK;
            }
            else
            {
                log("does not have pickaxe and is in bank. Banking...");
                return PlayerState.BANKING;
            }
        }
        return PlayerState.WALK_TO_BANK;
    }

    public void enforceWalking()
    {
        if (walkingObject.isRunEnabled()){
            walkingObject.toggleRun();
        }
    }

    private void walkingToBank()
    {
        log("Case: WALK_TO_BANK...");
        walkTo(EDGE_BANK_TILE, true);
        currentState = checkPlayerState();
        log("Case:WALK_TO_BANK close.");
    }
    private void walkingToMiningSpot()
    {
        log("Case: WALK_TO_MINING_SPOT...");
        walkTo(BARB_MINING_TILE, true);
        currentState = checkPlayerState();
        log("Case: WALK_TO_MINING_SPOT close.");
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
        } log("Stopped walkTo.");
    }

    private long randomNumberGenerator() {
        return Math.round(Math.random() * 2000) + 5000;
    }

    private void banking()
    {
        log("Case: BANKING...opening closest.");
        edgevilleBank.openClosest();
        log("Depositing all except pickaxe");
        edgevilleBank.depositAllExcept(RUNE_PICKAXE_ID);
        if (!getInventory().contains(RUNE_PICKAXE_ID))
        {
            edgevilleBank.withdraw(RUNE_PICKAXE_ID);
        }
        log("Closing bank...");
        edgevilleBank.close();
        currentState = checkPlayerState();
        log("Case: BANKING close.");
    }
    private void mining()
    {
        log("Case: MINING...looking for rocks.");
        getCamera().mouseRotateTo(CAMERA_YAW,CAMERA_PITCH);
        while (!getInventory().isFull())
        {
            currentlyMiningDelay();
            GameObject coal_rocks = getGameObjects().closest(COAL_ORE_COLOR_ID);
            coal_rocks.interactForceRight("Mine");
        }

        if (getInventory().isFull())
        {
            currentState = checkPlayerState();
        }
        log("Case: MINING close.");
    }

    private void currentlyMiningDelay()
    {
        while (getLocalPlayer().isAnimating()) {
            continue;
        }
    }

    @Override
    public void stop()
    {
        log("Stopped Script.");
        super.stop();
    }
}

/*
    Problems I can see:

    1. The ensure walk is always ensure running.
    2. The path the walk does is not my usual path.
    3. When the player arrives to the destination, it always goes to the same tile instead of just checking if it is
    in the right area.
    4. It clicks on mine very fast and repeatedly.
    5. Runs into problems when there are no rocks to mine. changing something


 */
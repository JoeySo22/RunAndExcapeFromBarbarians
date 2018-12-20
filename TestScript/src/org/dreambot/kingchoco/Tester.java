package org.dreambot.kingchoco;

import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.wrappers.interactive.Entity;
import org.dreambot.api.wrappers.interactive.NPC;

import java.awt.*;
import java.util.concurrent.TimeUnit;

@ScriptManifest(author = "kingchoco", category = Category.MISC, description = "SCRIPT TESTER", name = "SCRIPT TESTER", version = 1.0)
public class Tester extends AbstractScript
{

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public int onLoop()
    {
<<<<<<< Updated upstream
        Dialogues dialogues = getDialogues();
        log(String.valueOf(dialogues.inDialogue()));
        stop();
=======
        //Need to find a way to notice the notification and to close the dialog and end the fishing() method.
>>>>>>> Stashed changes
        return 10000;
    }

    @Override
    public void stop() {
        super.stop();
    }
}

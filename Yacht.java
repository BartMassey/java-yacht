/*
 * Copyright © 1997 Bart Massey
 * [This program is licensed under the "MIT License"]
 * Please see the file COPYING in the source
 * distribution of this software for license terms.
 */

//
// Game of Yacht
// From:
//  Play According To Hoyle: Hoyle's Rules Of Games
//  Philip D. and Andrew T. Morehead
//  Copyright 1946 New American Library
//  LOC 63-81703
//  pp. 241-242
// Software Copyright 1997 Bart Massey
// All Rights Reserved
//

import aux.*;
import java.awt.*;
import java.net.*;
import java.applet.*;

public class Yacht extends Applet {
    private YachtDice dice;
    private Button rollem;
    private Digits rolls;
    private YachtHuman human;
    private YachtMach machine;
    private volatile int player;
    private static final int HUMAN_PLAYER = 1;
    private static final int MACHINE_PLAYER = 2;
    private int cur_roll[];
    private boolean done;
    private static final int postdelay = 2000;

    public void init () {
	setLayout(new BorderLayout());
	dice = new YachtDice();
	rollem = new Button("Roll");
	rolls = new Digits(1);
	rolls.setValue(3);
	Panel dicecontrol = new Panel();
	dicecontrol.add(rollem);
	dicecontrol.add(rolls);
	Panel dicedisplay = new Panel();
	dicedisplay.setLayout(new BorderLayout());
	dicedisplay.add("Center", dice);
	dicedisplay.add("South", dicecontrol);
	human = new YachtHuman();
	machine = new MachHeur();
	add("Center", dicedisplay);
	Panel bogus = new Panel();
	bogus.setLayout(new BoxLayout(BoxLayout.VERTICAL,
				      BoxLayout.CENTER,
				      BoxLayout.CENTER));
	try {
	    URL base = getDocumentBase();
	    URL image_url = new URL(base, "yacht.gif");
	    Image title_image = getImage(image_url);
	    MediaTracker mt = new MediaTracker(this);
	    mt.addImage(title_image, 0);
	    mt.waitForID(0);
	    Canvas title_canvas = new ImageCanvas(title_image);
	    bogus.add(title_canvas);
	} catch (MalformedURLException e) {
	    showStatus("Can't find title image starter.gif");
	} catch (InterruptedException e) {
	    showStatus("Interrupted load of title image starter.gif");
	}
	Panel realbogus = new Panel();
	realbogus.add(human);
	realbogus.add(machine);
	bogus.add(realbogus);
	add("North", bogus);
	validate();
	done = false;
	rollem.disable();
	human.initialize();
	machine.initialize();
	player_start(HUMAN_PLAYER);
	cur_roll = dice.roll(0);
    }

    private synchronized void player_start(int player) {
	this.player = player;
	rolls.setValue(2);
        dice.initialize();
	rollem.disable();
    }
    
    public synchronized boolean action(Event e, Object o) {
	if (e.target == rollem) {
	    int nleft = rolls.getValue();
	    if (nleft <= 0)
		return true;
	    --nleft;
	    rolls.setValue(nleft);
	    rollem.disable();
	    dice.disable();
	    cur_roll = dice.roll(0);
	    return true;
	} else if (e.target == human && e.arg.equals("Scored")) {
	    player_start(MACHINE_PLAYER);
	    cur_roll = dice.roll(postdelay);
	    return true;
	} else if (e.target == human && e.arg.equals("Done")) {
	    player_start(MACHINE_PLAYER);
	    cur_roll = dice.roll(postdelay);
	    done = true;
	    return true;
	} else if (e.target == dice && e.arg.equals("Settled")) {
	    if (player == MACHINE_PLAYER) {
		int nleft = rolls.getValue();
		boolean result[] = machine.handleRoll(cur_roll, nleft);
		if (result == null) {
		    if (!done) {
			player_start(HUMAN_PLAYER);
			cur_roll = dice.roll(0);
		    }
		    return true;
		}
		if (nleft <= 0)
		    throw new Error("unhandled machine roll");
		rolls.setValue(--nleft);
		cur_roll = dice.reroll(result, postdelay);
		dice.setDiceLive(true);
	    } else {
		human.currentRoll(cur_roll);
		int nleft = rolls.getValue();
		if (nleft > 0) {
		    rollem.enable();
		    dice.enable();
		}
	    }
	    return true;
	}
	return super.action(e, o);
    }
}

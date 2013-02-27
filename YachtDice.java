/*
 * Copyright © 1997 Bart Massey
 * [This program is licensed under the "MIT License"]
 * Please see the file COPYING in the source
 * distribution of this software for license terms.
 */

import aux.*;
import java.awt.*;

public class YachtDice extends Panel {
    private static final int ndice = 5;
    private Die dice[];
    private int unsettled = 0;
    
    public YachtDice(int size) {
	dice = new Die[ndice];
	for (int i = 0; i < ndice; i++) {
	    dice[i] = new Die(size);
	    add(dice[i]);
	}
    }
    
    public YachtDice() {
	dice = new Die[ndice];
	setLayout(new GridLayout(1, ndice, 5, 0));
	for (int i = 0; i < ndice; i++) {
	    dice[i] = new Die();
	    add(dice[i]);
	}
    }
    
    public Insets insets() {
	return new Insets(5, 15, 5, 15);
    }
    
    public void initialize() {
	for (int i = 0; i < ndice; i++)
	    dice[i].value = 1;
	setDiceLive(false);
	setDiceEnabled(false);
    }
    
    public void setDiceLive(boolean live) {
        for (int i = 0; i < ndice; i++)
	    dice[i].setDieLive(live);
    }

    public void enable() {
	super.enable();
	for (int i = 0; i < ndice; i++)
	    dice[i].enable();
    }

    public void disable() {
	for (int i = 0; i < ndice; i++)
	    dice[i].disable();
	super.disable();
    }

    public void setDiceEnabled(boolean enabled) {
	if (enabled)
	    enable();
	else
	    disable();
    }
    
    public synchronized int[] roll(int postdelay) {
	for (int i = 0; i < ndice; i++)
	    if (!dice[i].getLive()) {
		dice[i].setDieLive(true);
		dice[i].roll(postdelay);
		unsettled++;
	    }
	int result[] = new int[ndice];
	for(int i = 0; i < ndice; i++)
	    result[i] = dice[i].value;
	return result;
    }

    public synchronized int[] reroll(boolean todo[], int postdelay) {
	for (int i = 0; i < ndice; i++)
	    if (todo[i]) {
		dice[i].setDieLive(false);
		dice[i].roll(postdelay);
		unsettled++;
	    }
	int result[] = new int[ndice];
	for(int i = 0; i < ndice; i++)
	    result[i] = dice[i].value;
	return result;
    }
    
    public synchronized boolean action(Event e, Object o) {
	if (e.arg != null && e.arg.equals("Dieclick")) {
	    for (int i = 0; i < dice.length; i++)
		if (e.target == dice[i]) {
		  dice[i].setDieLive(!dice[i].getLive());
		  return true;
		}
	} else if (e.arg != null && e.arg.equals("Settled")) {
	    if (unsettled > 0) {
		--unsettled;
		if (unsettled > 0)
		    return false;
		Event settled = new Event(this, Event.ACTION_EVENT, "Settled");
		settled.when = e.when;
		boolean bogus = postEvent(settled);
	    }
	}
	return super.action(e, o);
    }
}

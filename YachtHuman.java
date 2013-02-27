/*
 * Copyright © 1997 Bart Massey
 * [This program is licensed under the "MIT License"]
 * Please see the file COPYING in the source
 * distribution of this software for license terms.
 */

import aux.*;
import java.awt.*;

public class YachtHuman extends Panel implements YachtCategories {
    private Button buttons[] = new Button[CATEGORIES];
    private Digits values[] = new Digits[CATEGORIES];
    private int current_roll[] = null;
    private int categories = CATEGORIES;
    private Table display;
    private Digits total_score;

    private void add_score_item(String name, int what) {
	buttons[what] = new Button(name);
	values[what] = new Digits(2);
	display.add(buttons[what]);
	display.add(values[what]);
    }
    
    YachtHuman() {
	setLayout(new BorderLayout());
	display = new Table("f|r||f|r");
	total_score = new Digits(3);
	display.setBox(true);
	display.setBoxRows(true);
	add_score_item("Ones", 0);
	add_score_item("Yacht", YACHT);
	add_score_item("Twos", 1);
	add_score_item("High Straight", HIGH_STRAIGHT);
	add_score_item("Threes", 2);
	add_score_item("Low Straight", LOW_STRAIGHT);
	add_score_item("Fours", 3);
	add_score_item("Four Of A Kind", FOUR_OF_A_KIND);
	add_score_item("Fives", 4);
	add_score_item("Full House", FULL_HOUSE);
	add_score_item("Sixes", 5);
	add_score_item("Choice", CHOICE);
	Table bogus = new Table("c");
	bogus.setBox(true);
	bogus.add(total_score);
	add("Center", display);
	add("South", bogus);
    }

    public Insets insets() {
	return new Insets(5, 25, 5, 25);
    }
    
    public void initialize() {
	categories = CATEGORIES;
	current_roll = null;
	for (int i = 0; i < CATEGORIES; i++) {
	    buttons[i].enable();
	    values[i].clearValue();
	    values[i].setDimmed(true);
	}
	total_score.setValue(0);
    }

    public void currentRoll(int roll[]) {
	if (roll.length != 5)
	    throw new IllegalArgumentException("Can only score 5-die rolls");
	current_roll = roll;
	for (int i = 0; i < CATEGORIES; i++)
	    if (buttons[i].isEnabled())
		values[i].setValue(compute_score(i));
    }

    private int compute_score(int b) {
	return YachtScore.score(current_roll, b);
    }
    
    public boolean action(Event e, Object o) {
	// XXX throughought this function, I reuse i as the loop
	// control variable, because of the numerous post-loop tests
	int b;
	for (b = 0; b < CATEGORIES; b++)
	    if (e.target == buttons[b])
		break;
	if (b >= CATEGORIES)
	    return super.action(e, o);
	if (current_roll == null)
	    return true;
	int score = compute_score(b);
	buttons[b].disable();
	values[b].setDimmed(false);
	for (int i = 0; i < CATEGORIES; i++)
	    if (buttons[i].isEnabled())
		values[i].clearValue();
	total_score.setValue(total_score.getValue() + score);
	if (--categories == 0) {
	    Event endit = new Event(this, Event.ACTION_EVENT, "Done");
	    endit.when = e.when;
	    endit.x = e.x;
	    endit.y = e.y;
	    endit.modifiers = e.modifiers;
	    boolean bogus = postEvent(endit);
	    return true;
	}
	Event scoreit = new Event(this, Event.ACTION_EVENT, "Scored");
	scoreit.when = e.when;
	scoreit.x = e.x;
	scoreit.y = e.y;
	scoreit.modifiers = e.modifiers;
	boolean bogus = postEvent(scoreit);
	return true;
    }

    public int currentScore() {
	return total_score.getValue();
    }
}

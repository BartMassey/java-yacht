/*
 * Copyright © 1997 Bart Massey
 * [This program is licensed under the "MIT License"]
 * Please see the file COPYING in the source
 * distribution of this software for license terms.
 */

import aux.*;
import java.awt.*;

abstract public class YachtMach extends Panel implements YachtCategories {
    protected boolean used[] = new boolean[CATEGORIES];
    protected int scores[] = new int[CATEGORIES];
    protected int roll[];
    protected int remaining;
    protected int hist[];
    protected boolean reroll[] = new boolean[5];
    private Digits values[] = new Digits[CATEGORIES];
    private int categories = CATEGORIES;
    private Table display;
    private Digits total_score;

    protected void status(String msg) {
	System.out.println(msg);
    }

    private void show_roll() {
	System.out.print(roll[0]);
	for (int i = 1; i < 5; i++)
	    System.out.print(" " + roll[i]);
	System.out.println("");
    }

    private String reroll_string(boolean val) {
	if (val)
	    return "x";
	return "_";
    }
    
    private void show_rerolls() {
	System.out.print(reroll_string(reroll[0]));
	for (int i = 1; i < 5; i++)
	    System.out.print(" " + reroll_string(reroll[i]));
	System.out.println("");
    }
    
    private void add_score_item(String name, int what) {
	display.add(new Label(name, Label.CENTER));
	values[what] = new Digits(2);
	display.add(values[what]);
    }
    
    YachtMach() {
	setLayout(new BorderLayout());
	display = new Table("c|r||c|r");
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
	total_score = new Digits(3);
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
	for (int i = 0; i < CATEGORIES; i++) {
	    values[i].clearValue();
	    used[i] = false;
	}
	total_score.setValue(0);
    }
    
    abstract protected int choose();

    public boolean[] handleRoll(int roll[], int remaining) {
	if (roll.length != 5)
	    throw new IllegalArgumentException("Can only score 5-die rolls");
	this.roll = roll;
	this.remaining = remaining;
	if (remaining == 2)
	    status("---");
	status("processing roll: " + remaining + " remaining");
	show_roll();
	hist = YachtScore.histogram(roll);
	for (int i = 0; i < CATEGORIES; i++)
	    if (used[i])
		scores[i] = 0;
	    else
		scores[i] = YachtScore.score(roll, i);
	int choice = choose();
	if (choice == NONE) {
	    show_rerolls();
	    return reroll;
	}
	status("accept roll");
	values[choice].setValue(scores[choice]);
	used[choice] = true;
	total_score.setValue(total_score.getValue() + scores[choice]);
	return null;
    }

    public int currentScore() {
	return total_score.getValue();
    }
}

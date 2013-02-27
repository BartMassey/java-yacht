/*
 * Copyright © 1997 Bart Massey
 * [This program is licensed under the "MIT License"]
 * Please see the file COPYING in the source
 * distribution of this software for license terms.
 */

import java.awt.*;

interface Patterns {
    static final int XX = 7;
    static final int XA = 8;
    static final int XB = 9;
    static final int XC = 10;
    static final int XD = 11;
    static final int XE = 12;
    static final int Xa = 13;
    static final int Xb = 14;
    static final int Xc = 15;
    static final int Xd = 16;
    static final int Xe = 17;
}

class RBSRule implements Patterns {
    private int pat[];
    private boolean reroll[][];

    RBSRule(int pat[], boolean reroll[][]) {
	if (pat.length != 5)
	    throw new IllegalArgumentException("bad pattern length");
        if (reroll.length != 3)
        	throw new IllegalArgumentException("bad reroll length");
	for (int i = 0; i < 2; i++)
	    if (reroll[i].length != 5)
		throw new IllegalArgumentException("bad reroll length " + i);
	this.pat = pat;
    }

    final boolean match(int roll[]) {
	if (roll.length != 5)
	    throw new IllegalArgumentException("bad pattern length");
	for (int i = 0; i < 5; i++) {
	    switch(pat[i]) {
	    case 1: case 2: case 3: case 4: case 5: case 6:
		if (roll[i] != pat[i])
		    return false;
		break;
	    case XA: case XB: case XC: case XD: case XE:
		for (int j = i + 1; j < 5; j++)
		    if (pat[j] == pat[i] && roll[j] != roll[i])
			return false;
		break;
	    case Xa: case Xb: case Xc: case Xd: case Xe:
		for (int j = i + 1; j < 5; j++) {
		    if (pat[j] == pat[i]) {
			if (roll[j] != roll[i])
			    return false;
		        continue;
		    }
		    switch (pat[i]) {
		    case Xa: case Xb: case Xc: case Xd: case Xe:
			if (roll[j] == roll[i])
			    return false;
		    }
		}
		break;
	    case XX:
		break;
	    default:
		throw new Error("bad pattern");
	    }
	}
	return true;
    }

    final boolean[] action(int remaining) {
	return reroll[remaining];
    }
}

public class MachRBS extends YachtMach implements YachtCategories, Patterns {

    protected int choose() {
	return 0;
    }

}

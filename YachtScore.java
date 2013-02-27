/*
 * Copyright © 1997 Bart Massey
 * [This program is licensed under the "MIT License"]
 * Please see the file COPYING in the source
 * distribution of this software for license terms.
 */

import java.awt.*;

public class YachtScore implements YachtCategories {

    public static int[] histogram(int roll[]) {
    	int hist[] = new int[6];
	for (int i = 0; i < 6; i++)
	    hist[i] = 0;
	for (int i = 0; i < 5; i++)
	    hist[roll[i] - 1]++;
	return hist;
    }

    public static int score(int roll[], int b) {
	int hist[] = histogram(roll);
	int total = 0;
	for (int i = 0; i < 5; i++)
	    total += roll[i];
	int score = 0;
	if (b < 6) {
	    score = hist[b] * (b + 1);
	} else {
	    int i;
	    switch (b) {
		case YACHT:
		    for (i = 0; i < 6; i++)
			if (hist[i] == 5) {
			    score = 50;
			    break;
			}
		    break;
		case HIGH_STRAIGHT:
		    for (i = 1; i < 6; i++)
			if (hist[i] != 1)
			    break;
		    if (i >= 6)
			score = 30;
		    break;
		case LOW_STRAIGHT:
		    for (i = 0; i < 5; i++)
			if (hist[i] != 1)
			    break;
		    if (i >= 5)
			score = 30;
		    break;
		case FOUR_OF_A_KIND:
		    for (i = 0; i < 6; i++)
			if (hist[i] >= 4) {
			    score = total;
			    break;
			}
		    break;
		case FULL_HOUSE:
		    for (i = 0; i < 6; i++)
			if (hist[i] == 3)
			    break;
		    if (i < 6)
			for (i = 0; i < 6; i++)
			    if (hist[i] == 2) {
				score = total;
				break;
			    }
		    break;
		case CHOICE:
		    score = total;
		    break;
	    }
	}
	return score;
    }
}

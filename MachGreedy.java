import aux.*;
import java.awt.*;

public class MachGreedy extends YachtMach implements YachtCategories {
    private double this_score;
    private int score_cache[][][][][] = new int[6][6][6][6][6];
    private int choice_cache[][][][][] = new int[6][6][6][6][6];
    static private final int pref[] = {
	YACHT, HIGH_STRAIGHT, LOW_STRAIGHT, FOUR_OF_A_KIND,
	FULL_HOUSE, 5, 4, 3, 2, 1, 0, CHOICE
    };
    static private final int seed_roll[] = { 6, 6, 6, 6, 6 };

    // XXX All the hijinx here are due to a JDK 1.0 compiler bug.
    static private int[] clone_roll(int roll[]) {
	int nroll[] = null;
	try {
	    nroll = (int [])roll.clone();
	    if (false)
		throw new CloneNotSupportedException();
	} catch (CloneNotSupportedException e) {
	    // do nothing.
	}
	return nroll;
    }
    
    private int max_score(int roll[]) {
	int which = NONE;
	int score = -1;
	for (int i = 0; i < pref.length; i++)
	    if (!used[pref[i]]) {
		int nscore = YachtScore.score(roll, pref[i]);
		if (nscore > score) {
		    score = nscore;
		    which = pref[i];
		}
	    }
	this_score = score;
	return which;
    }

    private void cache_scores(int roll[]) {
	int i;
	for (i = 0; i < 5; i++)
	    if (roll[i] > 1)
		break;
	if (i >= 5)
	    return;
	int choice = max_score(roll);
	score_cache[roll[0] - 1][roll[1] - 1][roll[2] - 1][roll[3] - 1][roll[4] - 1] = (int) this_score;
	choice_cache[roll[0] - 1][roll[1] - 1][roll[2] - 1][roll[3] - 1][roll[4] - 1] = choice;
	--roll[i];
	for (int j = 0; j < i; j++)
	    roll[j] = roll[i];
	cache_scores(roll);
    }

    private static void sort_roll(int roll[]) {
	for (int i = 0; i < 5; i++)
	    for (int j = i; j < 5; j++)
		if (roll[i] > roll[j]) {
		    int tmp = roll[j];
		    roll[j] = roll[i];
		    roll[i] = tmp;
		}
    }

    private int max_cached_score(int oroll[]) {
	int roll[] = clone_roll(oroll);
	sort_roll(roll);
	this_score = score_cache[roll[0] - 1][roll[1] - 1][roll[2] - 1][roll[3] - 1][roll[4] - 1];
	return choice_cache[roll[0] - 1][roll[1] - 1][roll[2] - 1][roll[3] - 1][roll[4] - 1];
    }

    private double try_all(int roll[], boolean roll_mask[], int remaining) {
	int i;
	for (i = 0; i < 5; i++)
	    if (roll_mask[i])
		break;
	if (i >= 5) {
	    int bogus = find_best(roll, remaining);
	    return this_score;
	}
	roll_mask[i] = false;
	double mean = 0;
	for (int j = 1; j <= 6; j++) {
	    roll[i] = j;
	    mean += try_all(roll, roll_mask, remaining);
	}
	return mean / 6;
    }

    private int find_best(int roll[], int remaining) {
	int default_best = max_cached_score(roll);
	double score = this_score;
	if (remaining > 0) {
	    boolean roll_mask[] = new boolean[5];
	    boolean best_mask[] = null;
	    for (int i = 1; i < (1 << 5); i++) {
		if (remaining > 1)
		    status("  " + i);
		for (int j = 0; j < 5; j++)
		    roll_mask[j] = ((i & (1 << j)) > 0);
		double mean = -1;
		int nroll[] = clone_roll(roll);
		mean = try_all(nroll, roll_mask, remaining - 1);
		if (mean > score) {
		    if (remaining == 2)
			status("new best score with " + remaining + " remaining is " + mean);
		    score = mean;
		    if (best_mask == null)
			best_mask = new boolean[5];
		    for (int j = 0; j < 5; j++)
			best_mask[j] = ((i & (1 << j)) > 0);
		}
	    }
	    if (best_mask != null) {
		this_score = score;
		reroll = best_mask;
		return NONE;
	    }
	}
	return default_best;
    }
    
    protected int choose() {
	status("caching scores");
	cache_scores(clone_roll(seed_roll));
	status("searching");
	return find_best(roll, remaining);
    }

}

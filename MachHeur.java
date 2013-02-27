import aux.*;
import java.awt.*;

public class MachHeur extends YachtMach implements YachtCategories {
    
    private int handle_four_of_a_kind() {
	status("4 Of A Kind try"); 
	if (remaining == 0)
	    return FOUR_OF_A_KIND;
	for (int i = 0; i < 5; i++)
	    if (hist[roll[i] - 1] == 1 && (!used[YACHT] || roll[i] <= 3)) {
		reroll[i] = true;
		return NONE;
	    }
	if (used[YACHT] && roll[0] <= 3) {
	    reroll[0] = true;
	    return NONE;
	}
	return FOUR_OF_A_KIND;
    }

    private int handle_pips(int pip) {
	status("Numbered dice try: " + pip + "s"); 
	if (remaining == 0)
	    return (pip - 1);
	for (int i = 0; i < 5; i++)
	    if (roll[i] != pip)
	        reroll[i] = true;
	return NONE;
    }
    
    private int straight_count(int start, int end) {
	int count = 0;
	for (int i = start; i <= end; i++)
	    if (hist[i - 1] >= 1)
		count++;
	return count;
    }

    private void straight_reroll(int start, int end) {
	for (int i = 0; i < 5; i++)
	    reroll[i] = true;
	for (int i = start; i <= end; i++)
	    for (int j = 0; j < 5; j++)
		if (roll[j] == i) {
		    reroll[j] = false;
		    break;
		}
    }
    
    private boolean straight_try() {
	if (remaining == 0)
	    return false;
	if (!used[HIGH_STRAIGHT] &&
	  straight_count(2,6) >= 4 && remaining == 2) {
	    status("high straight try");
	    straight_reroll(2, 6);
	    return true;
	}
	if (!used[LOW_STRAIGHT] &&
	  straight_count(1,5) >= 4 && remaining == 2) {
	    status("low straight try");
	    straight_reroll(1, 5);
	    return true;
	}
	if (!used[LOW_STRAIGHT] && !used[HIGH_STRAIGHT] &&
	  straight_count(2,5) >= 4 && remaining >= 1) {
	    status("open straight try");
	    straight_reroll(2, 5);
	    return true;
	}
	return false;
    }


    private int punt() {
	status("punt");
	if (scores[CHOICE] > 10)
	    return CHOICE;
	for (int i = 0; i < 2; i++)
	    if (!used[i])
		return i;
	if (!used[CHOICE])
	    return CHOICE;
	if (!used[YACHT])
	    return YACHT;
	if (!used[LOW_STRAIGHT])
	    return LOW_STRAIGHT;
	if (!used[HIGH_STRAIGHT])
	    return HIGH_STRAIGHT;
	if (!used[FOUR_OF_A_KIND])
	    return FOUR_OF_A_KIND;
	if (!used[3])
	    return 3;
	if (!used[FULL_HOUSE])
	    return FULL_HOUSE;
	for (int i = 3; i < 6; i++)
	    if (!used[i])
		return i;
	throw new Error("bad punt");
    }
    
    protected int choose() {
	status("selecting strategy");
	if (scores[HIGH_STRAIGHT] > 0)
	    return HIGH_STRAIGHT;
	if (scores[LOW_STRAIGHT] > 0)
	    return LOW_STRAIGHT;
	if (scores[YACHT] > 0)
	    return YACHT;
	for (int i = 0; i < 5; i++)
	    reroll[i] = false;
	if (scores[FOUR_OF_A_KIND] > 10)
	    return handle_four_of_a_kind();
	if (scores[FULL_HOUSE] > 8)
	    return FULL_HOUSE;
	for (int i = 6; i >= 1; --i) {
	    if (!used[i - 1] && (hist[i - 1] >= 3))
		return handle_pips(i);
	    if (remaining <= 1 && !used[i - 1] && (hist[i - 1] >= 2))
		return handle_pips(i);
	    boolean flail = false;
	    flail |= !used[FOUR_OF_A_KIND];
	    flail |= !used[FULL_HOUSE];
	    if (flail && remaining == 1 && hist[i - 1] == 2)
		return handle_pips(i);
	    flail |= !used[YACHT];
	    if (flail && remaining == 2 && hist[i - 1] >= 3)
		return handle_pips(i);
	}
	if (scores[FOUR_OF_A_KIND] > 0)
	    return handle_four_of_a_kind();
	if (remaining == 0 && scores[FULL_HOUSE] > 0 )
	    return FULL_HOUSE;
	if (straight_try())
	    return NONE;
	status("inferior options now...");
	for (int i = 1; i <= 6; i++)
	    if (!used[i - 1] && hist[i - 1] >= 2)
		return handle_pips(i);
	if (remaining == 0)
	    return punt();
	status("reroll all");
	for (int i = 0; i < 5; i++)
	    reroll[i] = true;
	return NONE;
    }

}

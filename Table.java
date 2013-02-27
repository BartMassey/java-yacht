/*
 * Copyright © 1996-2009 Bart Massey
 * ALL RIGHTS RESERVED
 * [This program is licensed under the "MIT License"]
 * Please see the file COPYING in the source
 * distribution of this software for license terms.
 */

import java.awt.*;

public class Table extends Panel implements LayoutManager {
    private static final int COL_LEFT = 1;
    private static final int COL_CENTER = 2;
    private static final int COL_RIGHT = 3;
    private static final int COL_FILL = 4;
    private boolean box = false;
    private boolean box_rows = false;
    private int cols[];
    private int ncols;
    private int separators[];
    private int hsizes[];
    private int vsizes[];
    private boolean stretches[];
    private int nrows;
    private Component comp[] = null;
    
    public Table(String spec) {
	super();
	table_layout(spec);
	setLayout(this);
    }

    private void table_layout(String spec) {
	ncols = 0;
	for (int i = 0; i < spec.length(); i++)
	    switch(spec.charAt(i)) {
	    case 'l':
	    case 'r':
	    case 'c':
	    case 'f':
		ncols++;
		break;
	    case '|':
	    case '*':
		break;
	    default:
		throw new IllegalArgumentException("Bad table specification char");
	    }
	cols = new int[ncols];
	separators = new int[ncols - 1];
	for (int i = 0; i < ncols - 1; i++)
	    separators[i] = 0;
	stretches = new boolean[ncols];
	for (int i = 0; i < ncols; i++)
	    stretches[i] = false;
	boolean stretchable = false;
	int curcol = 0;
	for (int i = 0; i < spec.length(); i++)
	    switch (spec.charAt(i)) {
	    case 'l':
		cols[curcol++] = COL_LEFT;
		stretchable = true;
		break;
	    case 'r':
		cols[curcol++] = COL_RIGHT;
		stretchable = true;
		break;
	    case 'c':
		cols[curcol++] = COL_CENTER;
		stretchable = true;
		break;
	    case 'f':
		cols[curcol++] = COL_FILL;
		stretchable = true;
		break;
	    case '*':
		if (!stretchable)
		    throw new IllegalArgumentException("Unexpected table stretch");
		stretches[curcol - 1] = true;
		stretchable = false;
		break;
	    case '|':
		if (curcol < 1 || curcol >= ncols ||
		    separators[curcol - 1] >= 2)
		    throw new IllegalArgumentException("Bad table separator specification");
		separators[curcol - 1]++;
		stretchable = false;
		break;
	    default:
		throw new IllegalArgumentException("Unusual table specification error");
	    }
	if (ncols == 0)
	    throw new IllegalArgumentException("Empty table specification");
    }

    public void setBox(boolean box) {
	this.box = box;
    }

    public void setBoxRows(boolean box_rows) {
	this.box_rows = box_rows;
    }

    private void set_minimums(Component comp[]) {
	for (int i = 0; i < ncols; i++) {
	    hsizes[i] = 0;
	    vsizes[i] = 0;
	}
	for (int i = 0; i < comp.length; i++) {
	    int x = i % ncols;
	    int y = i / ncols;
	    Dimension dm = comp[i].minimumSize();
	    if (dm.width > hsizes[x])
		hsizes[x] = dm.width;
	    if (dm.height > vsizes[y])
		vsizes[y] = dm.height;
	}
    }

    private void set_preferreds(Component comp[]) {
	for (int i = 0; i < ncols; i++)
	    hsizes[i] = 0;
	for (int i = 0; i < nrows; i++)
	    vsizes[i] = 0;
	for (int i = 0; i < comp.length; i++) {
	    int x = i % ncols;
	    int y = i / ncols;
	    Dimension dm = comp[i].preferredSize();
	    if (dm.width > hsizes[x])
		hsizes[x] = dm.width;
	    if (dm.height > vsizes[y])
		vsizes[y] = dm.height;
	}
    }

    private Dimension csizes() {
    	Dimension result = new Dimension(0, 0);
	for (int i = 0; i < ncols; i++)
	    result.width += hsizes[i];
	for (int i = 0; i < nrows; i++)
	    result.height += vsizes[i];
	for (int i = 0; i < ncols - 1; i++)
	    if (separators[i] > 0)
		result.width += 1 + 2 * separators[i];
	if (box) {
	    result.height += 4;
	    result.width += 4;
	}
	if (box_rows)
	    result.height += 3 * (nrows - 1);
	return result;
    }

    private void set_slack(Dimension current, Dimension target) {
    	Dimension raw = new Dimension(0, 0);
	for (int i = 0; i < ncols; i++)
	    if (stretches[i])
		raw.width += hsizes[i];
	for (int i = 0; i < nrows; i++)
	    raw.height += vsizes[i];
	if (current.width < target.width) {
	    int hslack = target.width - current.width;
	    for (int i = 0; i < ncols; i++)
		if (stretches[i])
		    hsizes[i] += hslack * hsizes[i] / raw.width;
	}
	if (current.height < target.height) {
	    int vslack = target.height - current.height;
	    for (int i = 0; i < nrows; i++)
		vsizes[i] += vslack * vsizes[i] / raw.height;
	}
    }

    private void setup_measure(Container c) {
	comp = c.getComponents();
	nrows = 1 + (comp.length - 1) / ncols;
	hsizes = new int[ncols];
	vsizes = new int[nrows];
    }
    
    private void measure(Container c) {
	setup_measure(c);
	set_preferreds(comp);
	Dimension dc = c.size();
	Insets insets = c.insets();
	dc.width -= insets.left + insets.right;
	dc.height -= insets.top + insets.bottom;
	Dimension ds = csizes();
	if (ds.width < dc.width && ds.height < dc.height) {
	    set_slack(ds, dc);
	    return;
	}
	set_minimums(comp);
	Dimension dsx = csizes();
	set_slack(dsx, dc);
    }
    
    public void paint(Graphics g) {
	super.paint(g);
	measure(this);
	Dimension ls = csizes();
	Dimension cs = size();
	Insets insets = insets();
	cs.width -= insets.left + insets.right;
	cs.height -= insets.top + insets.bottom;
	int xpos = insets.left;
	if (cs.width > ls.width)
	    xpos += (cs.width - ls.width) / 2;
	int ypos = insets.top;
	if (cs.height > ls.height)
	    ypos += (cs.height - ls.height) / 2;
	if (box)
	    g.drawRect(xpos, ypos, ls.width - 1, ls.height - 1);
	if (box_rows) {
	    int ybase = ypos;
	    if (box)
		ybase += 2;
	    for (int i = 0; i < nrows; i++) {
		ybase += vsizes[i];
		g.drawLine(xpos, ybase + 1, xpos + ls.width - 1, ybase + 1);
		ybase += 3;
	    }
	}
	int xbase = xpos;
	if (box)
	    xbase += 2;
	for (int i = 0; i < ncols - 1; i++) {
	    xbase += hsizes[i];
	    switch (separators[i]) {
	    case 2:
		g.drawLine(xbase + 1, ypos, xbase + 1, ypos + ls.height - 1);
		xbase += 2;
	    case 1:
		g.drawLine(xbase + 1, ypos, xbase + 1, ypos + ls.height - 1);
		xbase += 3;
	    }
	}
    }

    public Dimension minimumLayoutSize(Container c) {
	setup_measure(c);
	set_minimums(comp);
	Dimension dc = csizes();
	Insets insets = c.insets();
	dc.width += insets.left + insets.right;
	dc.height += insets.top + insets.bottom;
	return dc;
    }
    
    public Dimension preferredLayoutSize(Container c) {
	setup_measure(c);
	set_preferreds(comp);
	Dimension dc = csizes();
	Insets insets = c.insets();
	dc.width += insets.left + insets.right;
	dc.height += insets.top + insets.bottom;
	return dc;
    }
    
    public void layoutContainer(Container c) {
	measure(c);
	Dimension ls = csizes();
	Dimension cs = c.size();
	Insets insets = c.insets();
	cs.width -= insets.left + insets.right;
	cs.height -= insets.top + insets.bottom;
	int ypos = insets.top;
	if (cs.height > ls.height)
	    ypos += (cs.height - ls.height) / 2;
	if (box)
	    ypos += 2;
	int voffsets[] = new int[nrows];
	for (int i = 0; i < nrows; i++) {
	    voffsets[i] = ypos;
	    ypos += vsizes[i];
	    if (box_rows)
		ypos += 3;
	}
	int xpos = insets.left;
	if (cs.width > ls.width)
	    xpos += (cs.width - ls.width) / 2;
	if (box)
	    xpos += 2;
	int hoffsets[] = new int[ncols];
	for (int i = 0; i < ncols; i++) {
	    hoffsets[i] = xpos;
	    xpos += hsizes[i];
	    if (i < ncols - 1)
		switch (separators[i]) {
		case 2:
		    xpos += 2;
		case 1:
		    xpos += 3;
		}
	}
	for (int i = 0; i < comp.length; i++)
	    if (comp[i].isVisible()) {
		Dimension ps = comp[i].preferredSize();
		int x = i % ncols;
		int y = i / ncols;
		int ho = hoffsets[x];
		int hs = hsizes[x];
		switch(cols[x]) {
		case COL_FILL:
		    break;
		case COL_CENTER:
		    if (hs > ps.width) {
			ho += (hs - ps.width) / 2;
			hs = ps.width;
		    }
		    break;
		case COL_RIGHT:
		    if (hs > ps.width) {
			ho += hs - ps.width;
			hs = ps.width;
		    }
		    break;
		case COL_LEFT:
		    if (hs > ps.width)
			hs = ps.width;
		    break;
		}
		comp[i].reshape(ho, voffsets[y], hs, vsizes[y]);
	    }
    }

    public void addLayoutComponent(String s, Component c) {
    }
    
    public void removeLayoutComponent(Component c) {
    }
}

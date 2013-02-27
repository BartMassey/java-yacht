/*
 * Copyright © 1996-2009 Bart Massey
 * ALL RIGHTS RESERVED
 * [This program is licensed under the "MIT License"]
 * Please see the file COPYING in the source
 * distribution of this software for license terms.
 */

import java.awt.*;

public class BoxLayout implements LayoutManager {
    public static final int HORIZONTAL = 1;
    public static final int VERTICAL = 2;
    public static final int LEFT = 1;
    public static final int TOP = 1;
    public static final int CENTER = 2;
    public static final int RIGHT = 3;
    public static final int BOTTOM = 3;
    public static final int FILL = 4;
    private boolean box = false;
    private int orientation = VERTICAL;
    private int alignment = LEFT;
    private int centering = TOP;
    
    public BoxLayout() {
    }
    
    public BoxLayout(int orientation) {
	this();
	this.orientation = orientation;
    }

    public BoxLayout(int orientation, int alignment) {
	this(orientation);
	this.alignment = alignment;
    }

    public BoxLayout(int orientation, int alignment, int centering) {
	this(orientation, alignment);
	this.centering = centering;
    }

    private static Dimension total_preferred_size(Component comp[]) {
	Dimension total = new Dimension(0,0);
	for (int i = 0; i < comp.length; i++) {
	    if (!comp[i].isVisible())
		continue;
	    Dimension s = comp[i].preferredSize();
	    total.width += s.width;
	    total.height += s.height;
	}
	return total;
    }
    
    private static Dimension maximum_preferred_size(Component comp[]) {
	Dimension max = new Dimension(0,0);
	for (int i = 0; i < comp.length; i++) {
	    if (!comp[i].isVisible())
		continue;
	    Dimension s = comp[i].preferredSize();
	    if (s.width > max.width)
		max.width = s.width;
	    if (s.height > max.height)
		max.height = s.height;
	}
	return max;
    }
    
    public Dimension preferredLayoutSize(Container c) {
	Component comp[] = c.getComponents();
	Dimension total = total_preferred_size(comp);
	Dimension max = maximum_preferred_size(comp);
	Insets insets = c.insets();
	total.width += insets.left + insets.right;
	max.width += insets.left + insets.right;
	total.height += insets.top + insets.bottom;
	max.height += insets.top + insets.bottom;
	if (orientation == HORIZONTAL)
	    return new Dimension(total.width, max.height);
	else
	    return new Dimension(max.width, total.height);
    }
    
    private static Dimension total_minimum_size(Component comp[]) {
	Dimension total = new Dimension(0,0);
	for (int i = 0; i < comp.length; i++) {
	    if (!comp[i].isVisible())
		continue;
	    Dimension s = comp[i].minimumSize();
	    total.width += s.width;
	    total.height += s.height;
	}
	return total;
    }
    
    private static Dimension maximum_minimum_size(Component comp[]) {
	Dimension max = new Dimension(0,0);
	for (int i = 0; i < comp.length; i++) {
	    if (!comp[i].isVisible())
		continue;
	    Dimension s = comp[i].minimumSize();
	    if (s.width > max.width)
		max.width = s.width;
	    if (s.height > max.height)
		max.height = s.height;
	}
	return max;
    }
    
    public Dimension minimumLayoutSize(Container c) {
	Component comp[] = c.getComponents();
	Dimension total = total_minimum_size(comp);
	Dimension max = maximum_minimum_size(comp);
	Insets insets = c.insets();
	total.width += insets.left + insets.right;
	max.width += insets.left + insets.right;
	total.height += insets.top + insets.bottom;
	max.height += insets.top + insets.bottom;
	if (orientation == HORIZONTAL)
	    return new Dimension(total.width, max.height);
	else
	    return new Dimension(max.width, total.height);
    }
    
    public void layoutContainer(Container c) {
	Component comp[] = c.getComponents();
	Dimension s = c.size();
	Dimension as = preferredLayoutSize(c);
	boolean use_preferred = true;
	if (as.width > s.width || as.height > s.height) {
	    as = minimumLayoutSize(c);
	    use_preferred = false;
	}
	Insets insets = c.insets();
	Dimension offset = new Dimension(insets.left,insets.top);
	Dimension total;
	Dimension max;
	if (use_preferred) {
	    total = total_preferred_size(comp);
	    max = new Dimension(s.width - insets.left - insets.right,
				s.height - insets.top - insets.bottom);
	} else {
	    total = total_minimum_size(comp);
	    max = maximum_minimum_size(comp);
	}
	int total_pad = 0;
	int pad = 0;
	if (orientation == HORIZONTAL) {
	    switch (centering) {
	    case CENTER:
		if (total.width < s.width)
		    offset.width += (s.width - total.width) / 2;
		break;
	    case RIGHT:
		if (total.width < s.width)
		    offset.width += s.width - total.width;
		break;
	    case FILL:
		if (total.width < s.width) {
		    total_pad = s.width - total.width;
		    pad = total_pad / comp.length;
		}
		break;
	    }
	} else {
	    switch (centering) {
	    case CENTER:
		if (total.height < s.height)
		    offset.height += (s.height - total.height) / 2;
		break;
	    case BOTTOM:
		if (total.height < s.height)
		    offset.height += s.height - total.height;
		break;
	    case FILL:
		if (total.height < s.height) {
		    total_pad = s.height - total.height;
		    pad = total_pad / comp.length;
		}
		break;
	    }
	}
	for (int i = 0; i < comp.length; i++) {
	    Dimension cs;
	    if (use_preferred)
		cs = comp[i].preferredSize();
	    else
		cs = comp[i].minimumSize();
	    if (orientation == HORIZONTAL) {
		int align = 0;
		int height = cs.height;
		switch (alignment) {
		case CENTER:
		    align = (max.height - cs.height) / 2;
		    break;
		case BOTTOM:
		    align = max.height - cs.height;
		    break;
		case FILL:
		    height = max.height;
		    break;
		}
		if (centering == FILL && pad > 0) {
		    int npad = pad;
		    if (total_pad > (npad - i) * comp.length)
			npad++;
		    cs.width += npad;
		    total_pad -= npad;
		}
		comp[i].reshape(offset.width, offset.height + align,
				cs.width, height);
		offset.width += cs.width;
	    } else {
		int align = 0;
		int width = cs.width;
		switch (alignment) {
		case CENTER:
		    align = (max.width - cs.width) / 2;
		    break;
		case BOTTOM:
		    align = max.width - cs.width;
		    break;
		case FILL:
		    width = max.width;
		    break;
		}
		if (centering == FILL && pad > 0) {
		    int npad = pad;
		    if (total_pad > (npad - i) * comp.length)
			npad++;
		    cs.height += npad;
		    total_pad -= npad;
		}
		comp[i].reshape(offset.width + align, offset.height,
				width, cs.height);
		offset.height += cs.height;
	    }
	}
    }

    public void addLayoutComponent(String s, Component c) {
    }
    
    public void removeLayoutComponent(Component c) {
    }
}

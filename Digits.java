/*
 * Copyright © 1996-2009 Bart Massey
 * ALL RIGHTS RESERVED
 * [This program is licensed under the "MIT License"]
 * Please see the file COPYING in the source
 * distribution of this software for license terms.
 */

import java.awt.*;

public class Digits extends Canvas {
    private int ndigits;
    private int value = 0;
    private boolean has_value = false;
    private Font digit_font;
    private static Font default_digit_font = null;
    private static int default_pointsize = -1;
    private boolean greyed = false;

    public static int defaultPointsize() {
	if (default_pointsize < 0) {
	    Font f = (new Label("x")).getFont();
	    if (f == null)
		throw new AWTError("No label font");
	    else
		default_pointsize = f.getSize();
	}
	return default_pointsize;
    }
    
    private static Font find_font() {
	if (default_digit_font == null) {
	    int size = 14;
	    try {
		size = defaultPointsize();
	    } catch (AWTError e) {
		// do nothing
	    }
	    default_digit_font = new Font("Courier", Font.PLAIN, size);
	}
	return default_digit_font;
    }

    public Digits(int n, Font f) {
	if (n <= 0)
	    throw new IllegalArgumentException("Too few digits");
	ndigits = n;
	digit_font = f;
    }

    public Digits(int n) {
	this(n, find_font());
    }

    public Dimension minimumSize() {
	FontMetrics fm = getFontMetrics(digit_font);
	int w = ndigits * fm.getMaxAdvance();
	return new Dimension(w, fm.getHeight());
    }
    
    public Dimension preferredSize() {
	return minimumSize();
    }

    public void paint(Graphics g) {
	if (!has_value)
	    return;
	Dimension s = size();
	Dimension m = minimumSize();
	if (greyed) {
	    Color old = g.getColor();
	    g.setColor(Color.gray);
	    g.fillRect(0, 0, s.width, s.height);
	    g.setColor(old);
	}
	if (s.width < m.width || s.height < m.height) {
	    if (s.width > 5)
		g.drawLine(5, s.height / 2, s.width - 5, s.height / 2);
	    return;
	}
	g.setFont(digit_font);
	String vs = Integer.toString(value);
	char ch[] = new char[ndigits];
	for (int i = 0; i < ndigits; i++)
	    ch[i] = 0;
	if (vs.length() > ndigits)
	    vs = vs.substring(vs.length() - ndigits);
	for (int i = 0; i < vs.length(); i++)
	    ch[i + ndigits - vs.length()] = vs.charAt(i);
	FontMetrics fm = g.getFontMetrics();
	int y = s.height - fm.getDescent() - fm.getLeading();
	int x = s.width;
	for (int i = 0; i < ndigits; i++) {
	    int j = ndigits - i - 1;
	    x -= fm.charWidth(ch[j]);
	    g.drawChars(ch, j, 1, x, y);
	}
    }

    public Font getFont() {
	return digit_font;
    }

    public void setFont(Font f) {
	digit_font = f;
	repaint();
    }

    public void setValue(int v) {
	has_value = true;
	value = v;
	repaint();
    }

    // Should throw a subclass of RuntimeException
    public int getValue() {
	if (!has_value)
	    throw new AWTError("Attempted to get non-existent digits");
	return value;
    }

    public boolean hasValue() {
	return has_value;
    }

    public void clearValue() {
	has_value = false;
	repaint();
    }

    public void setDimmed(boolean greyed) {
	if (this.greyed != greyed) {
	    this.greyed = greyed;
	    repaint();
	}
    }
}

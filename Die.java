/*
 * Copyright © 1997 Bart Massey
 * [This program is licensed under the "MIT License"]
 * Please see the file COPYING in the source
 * distribution of this software for license terms.
 */

import aux.*;
import java.awt.*;
import java.util.*;

class Die extends Canvas implements Runnable {
    static private volatile Random prng = null;
    private Dimension dotsize;
    private int size;
    private boolean tracking = false;
    private boolean clickmode = false;
    private volatile byte visible_value = 1;
    private boolean live = false;
    private volatile int post_delay;
    public Color dotColor = Color.black;
    public Color dieColor = Color.white;
    public Color deadDieColor = Color.gray;
    public volatile byte value = 1;

    static byte roll_value(int modulus) {
	if (prng == null) {
	    long seed = (new Date()).getTime();
	    prng = new Random(seed);
	}
	return (byte) ((prng.nextInt() >>> 1) % modulus + 1);
    }

    public Die() {
    }

    public Die(int size) {
	resize(size, size);
    }

    private void draw_dot(Graphics g, int nx, int dx, int ny, int dy) {
	int x0 = size * nx / dx;
	int y0 = size * ny / dy;
	int x = x0 - dotsize.width / 2;
	int y = y0 - dotsize.height / 2;
	if (live)
	    g.fillOval(x, y, dotsize.width, dotsize.height);
	else
	    g.drawOval(x, y, dotsize.width, dotsize.height);
    }
    
    public void paint(Graphics g) {
	dotsize = new Dimension(size / 8, size / 8);

	if (!live)
	    g.setColor(deadDieColor);
	else if (tracking)
	    g.setColor(dotColor);
	else
	    g.setColor(dieColor);
	int cs = size / 6;
	g.fillRoundRect(0, 0, size - 1, size - 1, cs, cs);
	if (tracking)
	    g.setColor(dieColor);
	else
	    g.setColor(dotColor);
	g.drawRoundRect(0, 0, size - 1, size - 1, cs, cs);
	switch (visible_value) {
	case 1:
	    draw_dot(g, 1, 2, 1, 2);
	    break;
	case 2:
	    draw_dot(g, 1, 3, 1, 3);
	    draw_dot(g, 2, 3, 2, 3);
	    break;
	case 3:
	    draw_dot(g, 1, 4, 1, 4);
	    draw_dot(g, 2, 4, 2, 4);
	    draw_dot(g, 3, 4, 3, 4);
	    break;
	case 4:
	    draw_dot(g, 1, 3, 1, 3);
	    draw_dot(g, 1, 3, 2, 3);
	    draw_dot(g, 2, 3, 1, 3);
	    draw_dot(g, 2, 3, 2, 3);
	    break;
	case 5:
	    draw_dot(g, 1, 4, 1, 4);
	    draw_dot(g, 1, 4, 3, 4);
	    draw_dot(g, 3, 4, 1, 4);
	    draw_dot(g, 3, 4, 3, 4);
	    draw_dot(g, 2, 4, 2, 4);
	    break;
	case 6:
	    draw_dot(g, 1, 3, 1, 4);
	    draw_dot(g, 1, 3, 2, 4);
	    draw_dot(g, 1, 3, 3, 4);
	    draw_dot(g, 2, 3, 1, 4);
	    draw_dot(g, 2, 3, 2, 4);
	    draw_dot(g, 2, 3, 3, 4);
	    break;
	default:
	    throw new Error("die value " + visible_value + " out of range");
	}
    }

    public void reshape(int x, int y, int width, int height) {
	size = width;
	if (size > height)
	    size = height;
	x += (width - size) / 2;
	y += (height - size) / 2;
	super.reshape(x, y, size, size);
    }

    private synchronized void next_frame(long ms, boolean last) {
	int modulus = 5;
	if (last)
	    modulus = 4;
	byte nv = roll_value(modulus);
	if (nv >= visible_value)
	    nv++;
	if (last && nv >= value)
	    nv++;
	visible_value = nv;
	repaint();
	try {
	    wait(ms);
	} catch (InterruptedException e) {
	    // do nothing
	}
    }
    
    // do die roll animation
    public synchronized void run() {
	try {
	    wait(roll_value(100) + 100);
	} catch (InterruptedException e) {
	    // do nothing
	}
	next_frame(100, false);
	next_frame(100, false);
	next_frame(150, false);
	next_frame(150, false);
	next_frame(200, false);
	next_frame(250, true);
	visible_value = value;
	repaint();
	if (post_delay >= 100) {
	    try {
		wait(post_delay);
	    } catch (InterruptedException e) {
		// do nothing
	    }
	}
	Event settled = new Event(this, Event.ACTION_EVENT, "Settled");
	settled.when = System.currentTimeMillis();
	boolean bogus = postEvent(settled);
    }

    public synchronized void roll(int postdelay) {
	value = roll_value(6);
	this.post_delay = postdelay;
        Thread animator = new Thread(this);
	animator.start();
    }

    private void set_tracking(boolean nt) {
	if (nt != tracking) {
	    tracking = nt;
	    repaint();
	}
    }
    
    public boolean mouseEnter(Event e, int x, int y) {
	if (clickmode)
	    set_tracking(true);
	else
	    set_tracking(false);
	return super.mouseEnter(e, x, y);
    }

    public boolean mouseExit(Event e, int x, int y) {
	set_tracking(false);
	return super.mouseExit(e, x, y);
    }

    public boolean mouseDown(Event e, int x, int y) {
	clickmode = true;
	set_tracking(true);
	return true;
    }

    public boolean mouseUp(Event e, int x, int y) {
	set_tracking(false);
	if (!clickmode || !inside(x, y)) {
	    clickmode = false;
	    return super.mouseUp(e, x, y);
	}
	clickmode = false;
	Event dieclick = new Event(this, Event.ACTION_EVENT, "Dieclick");
	dieclick.when = e.when;
	dieclick.x = x;
	dieclick.y = y;
	dieclick.modifiers = e.modifiers;
	boolean bogus = postEvent(dieclick);
	return true;
    }

    public void setDieLive(boolean be_live) {
	if (live != be_live) {
	    live = be_live;
	    repaint();
	}
    }

    public boolean getLive() {
	return live;
    }

    public Dimension minimumSize() {
	return new Dimension(10, 10);
    }

    public Dimension preferredSize() {
	if (size < 10)
	    return new Dimension(10, 10);
	return new Dimension(size, size);
    }
}

/*
 * Copyright © 1996-2009 Bart Massey
 * ALL RIGHTS RESERVED
 * [This program is licensed under the "MIT License"]
 * Please see the file COPYING in the source
 * distribution of this software for license terms.
 */

import java.awt.*;

public class ImageCanvas extends Canvas {
    private Image image;
    
    public ImageCanvas(Image image) {
	this.image = image;
    }

    public void paint(Graphics graphics) {
	graphics.drawImage(image, 0, 0, this);
    }

    public void update(Graphics graphics) {
	paint(graphics);
    }

    public Dimension minimumSize() {
	int w = image.getWidth(null);
	int h = image.getHeight(null);
	if (w == -1 || h == -1)
	    return new Dimension(0,0);
	return new Dimension(w, h);
    }

    public Dimension preferredSize() {
	return minimumSize();
    }
}

# Java Yacht
Copyright © 2013 Bart Massey

This is an implementation of a classic "poker dice" game
called "Yacht" in Java. Yacht is taken from a game rulebook
(*Hoyle's Rules of Games,* ed. Alfred Morehead and Geofferey
Mott-Smith, New American Library 1948) that well predates
the introduction of the similar (and similarly-named) game
introduced in 1956.

I wrote this code in 1997, and haven't done much of anything
since. Still, it seems to work OK.

Some of the source files in this program, specifically the
files `BoxLayout.java`, `Digits.java`, `Table.java` and
`ImageCanvas.java`, are copied from my Java aux
library. This was done for convenience, to make the program
standalone, but if you make improvements to these files, it
would be nice to submit those there.  The [Java aux
library](http://github.com/BartMassey/java-aux) is available
on [my GitHub](http://github.com/BartMassey).

To build the program, just say "javac *.java". Ignore the many
deprecation warnings, which are mostly due to this being an
AWT rather than Swing program.

To run the program, load the `yacht.html` webpage into your
favorite Java-applet-supporting web browser.

This program is licensed under the "MIT License". Please see
the file `COPYING` in this distribution for license terms.

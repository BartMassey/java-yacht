# Copyright © 1997 Bart Massey
# [This program is licensed under the "MIT License"]
# Please see the file COPYING in the source
# distribution of this software for license terms.

.SUFFIXES: .java .class

.java.class:
	javac $*.java

Yacht.class: YachtDice.class YachtMach.class YachtHuman.class Digits.class

YachtDice.class: Die.class

YachtHuman.class: Table.class Digits.class YachtCategories.class YachtScore.class

YachtMach.class: Table.class Digits.class YachtCategories.class YachtScore.class

YachtScore.class: YachtCategories.class

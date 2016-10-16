package net.game;

public enum Race {

HUMAN((char)0),
DWARF((char)1),
NIGHTELF((char)2),
GNOME((char)3),
DRAENEI((char)4),
ORC((char)5),
UNDEAD((char)6),
TAUREN((char)7),
TROLL((char)8),
BLOODELF((char)9);

    private final char value;
    Race(char value) {
    	this.value = value;
    }
    
    public char getValue() {
    	return this.value;
    }
}

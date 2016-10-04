package net.game;

public enum Race {

HUMAN("Human", new Classe[]{Classe.WARRIOR, Classe.PALADIN, Classe.ROGUE, Classe.PRIEST, Classe.MAGE, Classe.WARLOCK}),
DWARF("Dward", new Classe[]{Classe.WARRIOR, Classe.PALADIN, Classe.HUNTER, Classe.ROGUE, Classe.PRIEST}),
NIGHTELF("Night elf", new Classe[]{Classe.WARRIOR, Classe.HUNTER, Classe.ROGUE, Classe.PRIEST, Classe.DRUID}),
GNOME("Gnome", new Classe[]{Classe.WARRIOR, Classe.ROGUE, Classe.MAGE, Classe.WARLOCK}),
DRAENEI("Draenei", new Classe[]{Classe.WARRIOR, Classe.PALADIN, Classe.HUNTER, Classe.PRIEST, Classe.SHAMAN, Classe.MAGE}),
ORC("Orc", new Classe[]{Classe.WARRIOR, Classe.HUNTER, Classe.ROGUE, Classe.SHAMAN, Classe.WARLOCK}),
UNDEAD("Undead", new Classe[]{Classe.WARRIOR, Classe.ROGUE, Classe.PRIEST, Classe.MAGE, Classe.WARLOCK}),
TAUREN("Tauren", new Classe[]{Classe.WARRIOR, Classe.HUNTER, Classe.SHAMAN, Classe.DRUID}),
TROLL("Troll", new Classe[]{Classe.WARRIOR, Classe.HUNTER, Classe.ROGUE, Classe.PRIEST, Classe.SHAMAN, Classe.MAGE}),
BLOODELF("Blood elf", new Classe[]{Classe.PALADIN, Classe.HUNTER, Classe.ROGUE, Classe.PRIEST, Classe.MAGE, Classe.WARLOCK});

    private final Classe[] classe;
    private final String name;
    
    Race(String name, Classe[] classe) {
        this.classe = classe;
        this.name = name;
    }
    
    public Classe[] getClasse() {
    	return this.classe;
    }
    
    public String getName() {
    	return this.name;
    }
}

package model;

/**
 * Created by time on 3/22/17.
 */
public enum TILE_TYPE {
    ROAD('R'), DESART('D'), FOREST('F'), LAKE('L'), TOWN('T'), GRAVEYARD('g'), GRASS('G');

    char value;

    TILE_TYPE(char value) {
        this.value = value;
    }

    @Override
    public String toString() {
        if (this != GRASS)
            return ((char)27+"[31m"+this.value+(char)27+"[39;49m");
        else
            return ""+this.value;
    }
}

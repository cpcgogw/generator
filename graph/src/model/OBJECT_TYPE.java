package model;

/**
 * Created by vilddjur on 3/23/17.
 */
public enum OBJECT_TYPE {
    START, END, LOCK, KEY, ROOM, ANY, MONSTER, TRAP, DANGER;

    public TILE_TYPE toTile() {
        switch (this){
            case MONSTER:
                return TILE_TYPE.MONSTER;
            case TRAP:
                return TILE_TYPE.TRAP;
            case DANGER:
                return TILE_TYPE.DANGER;
            default:
                return TILE_TYPE.UNKOWN;
        }
    }
}

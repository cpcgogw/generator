package model;

/**
 * Created by vilddjur on 3/23/17.
 */
public enum OBJECT_TYPE implements TYPE {
    INIT, START, QUEST, KEY, ANY, MONSTER, TRAP, DANGER, REWARD;

    public TILE_TYPE toTile() {
        switch (this){
            case MONSTER:
                return TILE_TYPE.MONSTER;
            case TRAP:
                return TILE_TYPE.TRAP;
            case DANGER:
                return TILE_TYPE.DANGER;
            case REWARD:
                return TILE_TYPE.CHEST;
            default:
                return TILE_TYPE.UNKOWN;
        }
    }
}

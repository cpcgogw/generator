package model.enums;

import model.interfaces.TYPE;

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

    @Override
    public String toString() {
        return super.toString().substring(0,1) +
                super.toString().toLowerCase().substring(1, super.toString().toCharArray().length);
    }
}

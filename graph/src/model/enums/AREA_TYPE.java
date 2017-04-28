package model.enums;

import javafx.scene.paint.Color;
import model.interfaces.TYPE;

/**
 * Created by vilddjur on 3/23/17.
 */
public enum AREA_TYPE implements TYPE {
    TOWN, GRASSFIELD, DESERT, GRAVEYARD, CAVE, ISLAND;

    public Color toColor(){
        switch (this) {
            case CAVE:
                return Color.ROSYBROWN;
            case TOWN:
                return Color.GRAY;
            case DESERT:
                return Color.DARKKHAKI;
            case ISLAND:
                return Color.AQUA;
            case GRAVEYARD:
                return Color.LIGHTGREEN;
            case GRASSFIELD:
                return Color.FORESTGREEN;
        }
        return Color.BLACK;
    }

    @Override
    public String toString() {
        return super.toString().substring(0,1) +
                super.toString().toLowerCase().substring(1, super.toString().toCharArray().length);
    }
}

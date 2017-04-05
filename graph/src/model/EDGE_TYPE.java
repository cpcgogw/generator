package model;

/**
 * Created by vilddjur on 4/5/17.
 */
public enum EDGE_TYPE {
    LOCKED('L'), NORMAL('N'), ONE_DIRECTIONAL('O');
    char value;
    EDGE_TYPE(char value){
        this.value = value;
    }
    @Override
    public String toString(){
        return ""+this.value;
    }
}

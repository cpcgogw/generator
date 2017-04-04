package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by vilddjur on 1/28/17.
 */
public class Rule {

    public DrawablePattern matchingDrawablePattern;
    public ArrayList<DrawablePattern> possibleTranslations;
    private Random rand;

    public Rule(DrawablePattern matchingDrawablePattern){
        rand = new Random();
        this.matchingDrawablePattern = matchingDrawablePattern;
        possibleTranslations = new ArrayList<DrawablePattern>();
    }

    public Rule(DrawablePattern match, List<DrawablePattern> translations) {
        this(match);
        for (DrawablePattern pattern : translations) {
            possibleTranslations.add(pattern);
        }
    }

    @Override
    public boolean equals(Object o) {
        if(o.hashCode() == this.hashCode()){
            if(o instanceof Rule){
                Rule tmp = (Rule) o;
                return super.equals(tmp)
                        && tmp.matchingDrawablePattern.equals(this.matchingDrawablePattern)
                        && tmp.possibleTranslations.equals(this.possibleTranslations); // maybe sort the lists.
            }else{
                return false;
            }

        }else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int code = 0;
        code += matchingDrawablePattern.hashCode()*3;
        code += possibleTranslations.hashCode()*5;
        return super.hashCode() + code;
    }

    public boolean matches(DrawablePattern p){
        return p.equals(matchingDrawablePattern);
    }

    public DrawablePattern randomPossiblePattern() {
        System.out.println(possibleTranslations.size());
        return possibleTranslations.get(rand.nextInt(possibleTranslations.size()));
    }

    @Override
    public String toString() {
        String s = "Matching "+ matchingDrawablePattern +"\n";
        for (DrawablePattern p : possibleTranslations)
            s += "Translation "+p+"\n";
        return s;
    }
}

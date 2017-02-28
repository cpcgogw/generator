package model;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by vilddjur on 1/28/17.
 */
public class Rule {

    public Pattern matchingPattern;
    public ArrayList<Pattern> possibleTranslations;
    private Random rand;
    public Rule(Pattern matchingPattern){
        rand = new Random();
        this.matchingPattern = matchingPattern;
        possibleTranslations = new ArrayList<Pattern>();
    }

    public Rule(Pattern match, ArrayList<Pair<ArrayList<Node>, ArrayList<Edge>>> translations) {
        this.matchingPattern = match;
        possibleTranslations = new ArrayList<Pattern>();
        rand = new Random();
        for (Pair<ArrayList<Node>, ArrayList<Edge>> pair : translations) {
            Pattern p = new Pattern(pair);
            possibleTranslations.add(p);
        }
    }

    @Override
    public boolean equals(Object o) {
        if(o.hashCode() == this.hashCode()){
            if(o instanceof Rule){
                Rule tmp = (Rule) o;
                return super.equals(tmp)
                        && tmp.matchingPattern.equals(this.matchingPattern)
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
        code += matchingPattern.hashCode()*3;
        code += possibleTranslations.hashCode()*5;
        return super.hashCode() + code;
    }

    public boolean matches(Pattern p){
        return p.equals(matchingPattern);
    }

    public Pattern randomPossiblePattern() {
        return possibleTranslations.get(rand.nextInt(possibleTranslations.size()));
    }
}

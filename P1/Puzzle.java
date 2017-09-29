/**
 * Puzzle
 *
 * @author   Kevin Nash (kjn33)
 * @version  2017.9.27
 */
package P1;

import java.lang.StringBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.TreeSet;
import java.util.Random;

public class Puzzle {
    
    private PuzzleState state;
    
    private int maxNodes;
    
    public Puzzle() {
        this("b12 345 678");
    }
    
    public Puzzle(String state) {
        this.state = new PuzzleState(state);
    }
    
    public PuzzleState getState() {
        return this.state;
    }
    
    public void scrambleGoal(int maxSteps) {
        Random random = new Random();
        this.state = new PuzzleState("b12 345 678");
        for (int i = 0; i < maxSteps; i++) {
            TreeSet<PuzzleState> children = this.state.generateChildren();
            int polls = random.nextInt(children.size());
            for (int j = 0; j < polls; j++) {
                children.pollLast();
            }
            this.state = children.last();
            System.out.println(this.state);
        }
    }
    
    public static String generateRandomTileString() {
        ArrayList<String> tiles = new ArrayList<String>(Arrays.asList(
            "b","1","2","3","4","5","6","7","8"
        ));
        Collections.shuffle(tiles);
        tiles.add(3, " ");
        tiles.add(7, " ");
        StringBuilder sb = new StringBuilder();
        for (String s : tiles) {
            sb.append(s);
        }
        if (!isSolvable(sb.toString())) {
            return generateRandomTileString();
        }
        return sb.toString();
    }
    
    public static boolean isSolvable(String tiles) {
        try {
            PuzzleState.validateTileString(tiles);
        } catch (Exception e) {
            return false;
        }
        tiles = tiles.replaceAll("b|\\s", "");
        int inversions = 0;
        for (int i = 0; i < tiles.length() - 1; i++) {
            for (int j = i + 1; j < tiles.length(); j++) {
                if (Character.getNumericValue(tiles.charAt(j)) <
                    Character.getNumericValue(tiles.charAt(i))) {
                    inversions++;
                }
            }
        }
        return (inversions % 2) == 0;
    }
    
    /**
     * Returns a String representation of the puzzle
     * @return  puzzle String
     */
    @Override
    public String toString() {
        return this.state.toString();
    }
}

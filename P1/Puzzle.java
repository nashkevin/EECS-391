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
    
    private int maxNodes = 99999;
    
    public Puzzle() {
        this("b12 345 678");
    }
    
    public Puzzle(String state) {
        this.state = new PuzzleState(state);
    }
    
    public PuzzleState getState() {
        return this.state;
    }

    public setMaxNodes(int n) {
        this.maxNodes = n;
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
    
    public void moveUp() {
        int emptyIndex = this.state.findEmptyTile();
        if (!this.state.isTopRow(emptyIndex)) {
            String tiles = this.state.swapTiles(this.state.getTiles(),
                                                emptyIndex, emptyIndex - 4);
            this.state = new PuzzleState(tiles, this.state.getHeuristic());
        }
        else {
            throw new IndexOutOfBoundsException();
        }
    }
    
    public void moveDown() {
        int emptyIndex = this.state.findEmptyTile();
        if (!this.state.isBottomRow(emptyIndex)) {
            String tiles = this.state.swapTiles(this.state.getTiles(),
                                                emptyIndex, emptyIndex + 4);
            this.state = new PuzzleState(tiles, this.state.getHeuristic());
        }
        else {
            throw new IndexOutOfBoundsException();
        }
    }
    
    public void moveLeft() {
        int emptyIndex = this.state.findEmptyTile();
        if (!this.state.isLeftColumn(emptyIndex)) {
            String tiles = this.state.swapTiles(this.state.getTiles(),
                                                emptyIndex, emptyIndex - 1);
            this.state = new PuzzleState(tiles, this.state.getHeuristic());
        }
        else {
            throw new IndexOutOfBoundsException();
        }
    }
    
    public void moveRight() {
        int emptyIndex = this.state.findEmptyTile();
        if (!this.state.isRightColumn(emptyIndex)) {
            String tiles = this.state.swapTiles(this.state.getTiles(),
                                                emptyIndex, emptyIndex + 1);
            this.state = new PuzzleState(tiles, this.state.getHeuristic());
        }
        else {
            throw new IndexOutOfBoundsException();
        }
    }
    
    public void beamSearch(int k) {
        int counter = 1;
        TreeSet<PuzzleState> beam = new TreeSet<PuzzleState>();
        beam.add(this.state);
        TreeSet<PuzzleState> nextPly = new TreeSet<PuzzleState>();
        
        while (0 < this.state.getCost() && counter < this.maxNodes) {
            for (PuzzleState s1 : beam) {
                TreeSet<PuzzleState> children = s1.generateChildren();
                for (PuzzleState s2 : children) {
                    nextPly.add(s2);
                    counter++;
                }
            }
            beam.clear();
            for (int i = 0; i < k && !nextPly.isEmpty(); i++) {
                beam.add(nextPly.pollFirst());
            }
            this.state = beam.first();
        }
        if (0 == this.state.getCost()) {
            System.out.println("Solved with " + counter + " node(s)");
        }
        else {
            System.err.println("Failed to solve after " + counter + " node(s)");
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

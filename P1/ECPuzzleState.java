/**
 * PuzzleState
 *
 * @author   Kevin Nash (kjn33)
 * @version  2017.9.26
 */
package P1;

import java.lang.Math;
import java.util.Arrays;
import java.util.HashSet;
import java.util.PriorityQueue;

public class PuzzleState implements Comparable<PuzzleState> {

    public enum Heuristic { COUNT_MISPLACED, SUM_DISTANCES };

    /** The arrangement of the puzzle tiles **/
    private String tiles;

    private byte hVal = Byte.MAX_VALUE;
    private int gVal = 0;

    private Heuristic heuristic;

    /**
     * Constructs a PuzzleState given an arrangement of tiles
     * @param  tiles      the tile arrangement in form "b12 345 678"
     */
    public PuzzleState(String tiles) {
        this(tiles, Heuristic.SUM_DISTANCES);
    }
    
    /**
     * Constructs a PuzzleState given an arrangement of tiles
     * @param  tiles      the tile arrangement in form "b12 345 678"
     * @param  heuristic  TODO: describe
     */
    public PuzzleState(String tiles, Heuristic heuristic) {
        validateTileString(tiles);
        this.tiles = tiles;
        this.heuristic = heuristic;
        calculateHval();
    }
    
    /**
     * Constructs a PuzzleState given an arrangement of tiles
     * @param  tiles      the tile arrangement in form "b12 345 678"
     * @param  heuristic  TODO: describe
     */
    public PuzzleState(String tiles, Heuristic heuristic, int gVal) {
        validateTileString(tiles);
        this.tiles = tiles;
        this.heuristic = heuristic;
        this.gVal = gVal;
        calculateHval();
    }
    
    /**
     * Returns f(n), the total cost of this state
     * @return  fVal
     */
    public int getFval() {
        return this.gVal + this.hVal;
    }
    
    /**
     * Returns h(n), the predicted cost of this state
     * @return  hVal
     */
    public byte getHval() {
        return this.hVal;
    }
    
    /**
     * Returns g(n), the existing cost of this state
     * @return  gVal
     */
    public int getGval() {
        return this.gVal;
    }
    
    public void setGval(int val) {
        this.gVal = val;
    }

    public String getTiles() {
        return this.tiles;
    }
    
    public Heuristic getHeuristic() {
        return this.heuristic;
    }

    /**
     * Returns a String representation of the puzzle state
     * @return  game String
     */
    @Override
    public String toString() {
        return this.tiles;
    }

    @Override
    public int compareTo(PuzzleState that) {
        return (this.getGval() - that.getGval()) + (this.getHval() - that.getHval());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        try {
            final PuzzleState that = (PuzzleState) obj;
            return this.getTiles().equals(that.getTiles());
        }
        catch (Exception e) {
            return false;
        }
    }

    /**
     * Returns a set of PuzzleStates that can be reached from this state
     * @return  children of the state
     */
    public PriorityQueue<PuzzleState> generateChildren() {
        PriorityQueue<PuzzleState> children = new PriorityQueue<PuzzleState>();
        int emptyIndex = findEmptyTile();

        // swap the empty tile with tiles orthoganal to it
        if (!isLeftColumn(emptyIndex)) {
            children.add(new PuzzleState(swapTiles(
                tiles, emptyIndex, emptyIndex - 1),
                this.heuristic, this.gVal + 1));
        }
        if (!isRightColumn(emptyIndex)) {
            children.add(new PuzzleState(swapTiles(
                tiles, emptyIndex, emptyIndex + 1),
                this.heuristic, this.gVal + 1));
        }
        if (!isTopRow(emptyIndex)) {
            children.add(new PuzzleState(swapTiles(
                tiles, emptyIndex, emptyIndex - 4),
                this.heuristic, this.gVal + 1));
        }
        if (!isBottomRow(emptyIndex)) {
            children.add(new PuzzleState(swapTiles(
                tiles, emptyIndex, emptyIndex + 4),
                this.heuristic, this.gVal + 1));
        }
        return children;
    }
    
    public int findEmptyTile() {
        int emptyIndex = -1;
        for (int i = 0; i < this.tiles.length(); i++) {
            if ('b' == this.tiles.charAt(i)) {
                emptyIndex = i;
            }
        }
        return emptyIndex;
    }
    
    /**
     * Throws an exception if the given tile string is not valid
     */
    public static void validateTileString(String tiles) {
        class TileStringException extends RuntimeException {
            TileStringException() {
                super("The tile string is not in the correct format");
            }
        }
        Character[] validTiles = { 'b','1','2','3','4','5','6','7','8' };
        HashSet<Character> remainingTiles =
            new HashSet<Character>(Arrays.asList(validTiles));

        if (11 < tiles.length()) {
            throw new TileStringException();
        }

        for (int i = 0; i < tiles.length(); i++) {
            if (0 == (i + 1) % 4) {
                if (' ' != tiles.charAt(i)) {
                    throw new TileStringException();
                }
            }
            else if (!remainingTiles.remove(tiles.charAt(i))) {
                throw new TileStringException();
            }
        }
        if (!remainingTiles.isEmpty()) {
            throw new TileStringException();
        }
    }
    
    public String swapTiles(String s, int i, int j) {
        char[] c = s.toCharArray();

        char temp = c[i];
        c[i] = c[j];
        c[j] = temp;

        return new String(c);
    }
    
    public boolean isLeftColumn(int i) {
        return (0 == i % 4);
    }

    public boolean isRightColumn(int i) {
        return (0 == (i - 2) % 4);
    }

    public boolean isTopRow(int i) {
        return (0 <= i && i <= 2);
    }

    public boolean isBottomRow(int i) {
        return (8 <= i && i <= 10);
    }

    private void calculateHval() {
        switch (this.heuristic) {
            case COUNT_MISPLACED:
                calculateMisplacedHval();
                break;
            case SUM_DISTANCES:
                calculateDistanceHval();
                break;
        }
    }

    public void calculateMisplacedHval() {
        this.hVal = 0;
        String goal = "b12 345 678";
        for (int i = 0; i < goal.length(); i++) {
            if (this.tiles.charAt(i) != goal.charAt(i) && goal.charAt(i) != ' ') {
                this.hVal++;
            }
        }
    }

    public void calculateDistanceHval() {
        this.hVal = 0;
        char[] tiles = this.tiles.replace(" ", "").replace("b", "0").toCharArray();
        int xDist = 0;
        int yDist = 0;
        for (int i = 0; i < tiles.length; i++) {
            int tileVal = Character.getNumericValue(tiles[i]);
            if (0 < tileVal) {
                xDist = Math.abs(tileVal % 3 - i % 3);
                yDist = Math.abs(tileVal / 3 - i / 3);
                this.hVal += (xDist + yDist);
            }
        }
    }
}

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
import java.util.TreeSet;

public class PuzzleState implements Comparable<PuzzleState> {

    public enum Heuristic { COUNT_MISPLACED, SUM_DISTANCES };

    /** The arrangement of the puzzle tiles **/
    private String tiles;

    /** g **/
    private byte cost = Byte.MAX_VALUE;

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
        calculateCost();
    }

    /**
     * Returns the cost associated with this state
     * @return  cost
     */
    public byte getCost() {
        return this.cost;
    }

    public String getTiles() {
        return this.tiles;
    }

    /**
     * Returns a String representation of the puzzle state
     * @return  game String
     */
    @Override
    public String toString() {
        return this.tiles/*.replace(' ', '\n')*/;
    }

    @Override
    public int compareTo(PuzzleState that) {
        // offset by 1 so that TreeSet does not falsely abandon "duplicates"
        return (this.cost - that.cost) + 1;
    }

    public boolean equals(PuzzleState that) {
        return this.tiles.equals(that.getTiles());
    }

    /**
     * Returns a set of PuzzleStates that can be reached from this state
     * @return  children of the state
     */
    public TreeSet<PuzzleState> generateChildren() {
        TreeSet<PuzzleState> children = new TreeSet<PuzzleState>();

        // find the empty tile
        int emptyIndex = -1;
        for (int i = 0; i < tiles.length(); i++) {
            if ('b' == tiles.charAt(i)) {
                emptyIndex = i;
            }
        }

        // swap the empty tile with tiles orthoganal to it
        if (!isLeftColumn(emptyIndex)) {
            children.add(new PuzzleState(swapTiles(
                tiles, emptyIndex, emptyIndex - 1), this.heuristic));
        }
        if (!isRightColumn(emptyIndex)) {
            children.add(new PuzzleState(swapTiles(
                tiles, emptyIndex, emptyIndex + 1), this.heuristic));
        }
        if (!isTopRow(emptyIndex)) {
            children.add(new PuzzleState(swapTiles(
                tiles, emptyIndex, emptyIndex - 4), this.heuristic));
        }
        if (!isBottomRow(emptyIndex)) {
            children.add(new PuzzleState(swapTiles(
                tiles, emptyIndex, emptyIndex + 4), this.heuristic));
        }
        return children;
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

    private void calculateCost() {
        switch (this.heuristic) {
            case COUNT_MISPLACED:
                calculateMisplacedCost();
            case SUM_DISTANCES:
                calculateDistanceCost();
        }
    }

    private void calculateMisplacedCost() {
        this.cost = 0;
        String goal = "b12 345 678";
        for (int i = 0; i < goal.length(); i++) {
            if (this.tiles.charAt(i) != goal.charAt(i) && goal.charAt(i) != ' ') {
                this.cost++;
            }
        }
    }

    private void calculateDistanceCost() {
        this.cost = 0;
        char[] tiles = this.tiles.replace(" ", "").replace("b", "0").toCharArray();
        int xDist = 0;
        int yDist = 0;
        for (int i = 0; i < tiles.length; i++) {
            int tileVal = Character.getNumericValue(tiles[i]);
            if (0 < tileVal) {
                xDist = Math.abs(tileVal % 3 - i % 3);
                yDist = Math.abs(tileVal / 3 - i / 3);
                this.cost += (xDist + yDist);
            }
        }
    }

    private boolean isLeftColumn(int i) {
        return (0 == i % 4);
    }

    private boolean isRightColumn(int i) {
        return (0 == (i - 2) % 4);
    }

    private boolean isTopRow(int i) {
        return (0 <= i && i <= 2);
    }

    private boolean isBottomRow(int i) {
        return (8 <= i && i <= 10);
    }

    private String swapTiles(String s, int i, int j) {
        char[] c = s.toCharArray();

        char temp = c[i];
        c[i] = c[j];
        c[j] = temp;

        return new String(c);
    }
}

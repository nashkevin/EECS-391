/**
 * PuzzleState
 *
 * @author   Kevin Nash (kjn33)
 * @version  2017.9.26
 */
import java.util.Arrays;
import java.util.HashSet;
import java.util.TreeSet;

public class PuzzleState {
    
    /** The arrangement of the puzzle tiles **/
    private byte[] tiles = new byte[9];
    
    /** Whether or not this state is the goal **/
    private boolean isGoalState = false;
    
    /**
     * Constructs the goal PuzzleState
     */
    public PuzzleState() {
        this("b12 345 678");
    }
    
    /**
     * Constructs a PuzzleState given an arrangement of tiles
     * @param  tiles  the tile arrangement in form "b12 345 678"
     */
    public PuzzleState(String tiles) {
        validateTileString(tiles);
        this.isGoalState = ("b12 345 678" == tiles);
        int i = 0;
        int j = 0;
        while (i < tiles.length()) {
            byte tile = (byte) Character.getNumericValue(tiles.charAt(i));
            if (11 == tile) {
                this.tiles[j++] = 0;
            }
            else if (0 < (i + 1) % 4) {
                this.tiles[j++] = tile;
            }
            i++;
        }
    }

    /**
     * Returns true if the state is the goal state
     * @return  logical value of the goal state
     */
    public boolean isGoalState() {
        return this.isGoalState;
    }

    /**
     * Returns a set of PuzzleStates that can be reached from this state
     * @return  children of the state
     */
    public TreeSet<PuzzleState> generateChildren() {
         TreeSet<PuzzleState> children = new TreeSet<PuzzleState>();

        // find the empty tile
        int emptyIndex;
        for (int i = 0; i < tiles.length; i++) {
            if (0 == tiles[i]) {
                emptyIndex = i;
            }
        }

        // swap the empty tile with tiles orthoganal to it
        if (!isLeftColumn(emptyIndex)) {
            children.add(swapChars(tiles, emptyIndex, emptyIndex - 1));
        }
        if (!isRightColumn(emptyIndex)) {
            children.add(swapChars(tiles, emptyIndex, emptyIndex + 1));
        }
        if (!isTopRow(emptyIndex)) {
            children.add(swapChars(tiles, emptyIndex, emptyIndex - 3))
        }
        if (!isBottomRow(emptyIndex)) {
            children.add(swapChars(tiles, emptyIndex, emptyIndex + 3))
        }

        return children;
    }

    /**
     * Returns a String representation of the puzzle state
     * @return  game String
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\u250C---\u252C---\u252C---\u2510\n\u2502 ");
        sb.append((0 < tiles[0]) ? tiles[0] : " ");
        sb.append(" \u2502 ");
        sb.append((0 < tiles[1]) ? tiles[1] : " ");
        sb.append(" \u2502 ");
        sb.append((0 < tiles[2]) ? tiles[2] : " ");
        sb.append(" \u2502");
        sb.append("\n\u251C---\u253C---\u253C---\u2524");
        sb.append("\n\u2502 ");
        sb.append((0 < tiles[3]) ? tiles[3] : " ");
        sb.append(" \u2502 ");
        sb.append((0 < tiles[4]) ? tiles[4] : " ");
        sb.append(" \u2502 ");
        sb.append((0 < tiles[5]) ? tiles[5] : " ");
        sb.append(" \u2502");
        sb.append("\n\u251C---\u253C---\u253C---\u2524");
        sb.append("\n\u2502 ");
        sb.append((0 < tiles[6]) ? tiles[6] : " ");
        sb.append(" \u2502 ");
        sb.append((0 < tiles[7]) ? tiles[7] : " ");
        sb.append(" \u2502 ");
        sb.append((0 < tiles[8]) ? tiles[8] : " ");
        sb.append(" \u2502\n\u2514---\u2534---\u2534---\u2518");
        return sb.toString();
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

    private String swapChars(String s, int i, int j) {
        char[] c = s.toCharArray();

        char temp = c[i];
        c[i] = c[j];
        c[j] = temp;

        return new String(c);
    }

    /**
     * Throws an exception if the given tile string is not valid
     */
    private void validateTileString(String tiles) {
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
}

/**
 * PuzzleState
 *
 * @author   Kevin Nash (kjn33)
 * @version  2017.9.25
 */
import java.util.Arrays;
import java.util.HashSet;

public class PuzzleState {
    
    /** The arrangement of the puzzle tiles **/
    public byte[] tiles = new byte[9];
    
    /** Whether or not this state is the goal **/
    public boolean isGoalState = false;
    
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
        byte i = 0;
        byte j = 0;
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
    
    /**
     * Returns a String representation of the Solitaire game
     * @return  game String
     */
    public static void validateTileString(String tiles) {
        class TileStringException extends RuntimeException {
            TileStringException() {
                super("The tile string is not in the correct format");
            }
        }
        Character[] tileChars = { 'b','1','2','3','4','5','6','7','8' };
        HashSet<Character> validTiles =
            new HashSet<Character>(Arrays.asList(tileChars));
        
        if (11 < tiles.length()) {
            throw new TileStringException();
        }
        
        for (int i = 0; i < tiles.length(); i++) {
            if (0 == (i + 1) % 4) {
                if (' ' != tiles.charAt(i)) {
                    throw new TileStringException();
                }
            }
            else if (!validTiles.remove(tiles.charAt(i))) {
                throw new TileStringException();
            }
        }
        if (!validTiles.isEmpty()) {
            throw new TileStringException();
        }
    }
}

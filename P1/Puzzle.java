/**
 * Puzzle
 *
 * @author   Kevin Nash (kjn33)
 * @version  2017.9.27
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Puzzle {
    
    private PuzzleState state;
    
    private int maxNodes;
    
    public Puzzle() {
//        this(  );
    }
    
    public Puzzle(String state) {
        this.state = new PuzzleState(state);
    }
    
    private static String getRandomTileString() {
        ArrayList<String> tiles = new ArrayList<String>(Arrays.asList(
            "b","1","2","3","4","5","6","7","8"
        ));
        Collections.shuffle(tiles);
        tiles.add(3, " ");
        tiles.add(6, " ");
        return new String(tiles.toArray(new String[tiles.size()]));
    }
}

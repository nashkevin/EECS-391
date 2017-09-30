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
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.TreeSet;
import java.util.Random;

public class Puzzle {
    
    private PuzzleState state;
    
    private int maxNodes = 999999;
    
    public Puzzle() {
        this("b12 345 678");
    }
    
    public Puzzle(String state) {
        this.state = new PuzzleState(state);
    }
    
    public PuzzleState getState() {
        return this.state;
    }

    public void setMaxNodes(int n) {
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
        }
        this.state = new PuzzleState(this.state.getTiles());
        System.out.println(this.state);
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
        int nodeCounter = 1;
        int plyCounter = 0;
        TreeSet<PuzzleState> beam = new TreeSet<PuzzleState>();
        beam.add(this.state);
        TreeSet<PuzzleState> nextPly = new TreeSet<PuzzleState>();
        long startTime = System.nanoTime();
        
        while (0 < this.state.getHval() && nodeCounter < this.maxNodes) {
            for (PuzzleState s1 : beam) {
                TreeSet<PuzzleState> children = s1.generateChildren();
                for (PuzzleState s2 : children) {
                    nextPly.add(s2);
                    nodeCounter++;
                }
            }
            beam.clear();
            for (int i = 0; i < k && !nextPly.isEmpty(); i++) {
                beam.add(nextPly.pollFirst());
            }
            this.state = beam.first();
            System.out.println(this.state);
            plyCounter++;
        }
        
        long endTime = System.nanoTime();
        String message;
        if (0 == this.state.getHval()) {
            message = "Solved with ";
        }
        else {
            message = "Failed to solve after ";
        }
        long elapsedTime = (endTime - startTime) / 1000000;
        message += nodeCounter + " node(s)";
        message += ", " + plyCounter + " ply(s)";
        message += ", " + this.state.getGval() + " step(s)";
        message += ", " + elapsedTime + " ms elapsed";
        System.out.println(message);
    }
    
    public void aStarSearch(String heuristic) {
        if (heuristic.equals("h1")) {
            this.state = new PuzzleState(this.state.getTiles(),
                                         PuzzleState.Heuristic.COUNT_MISPLACED, 0);
        }
        
        int nodeCounter = 1;

        Comparator<PuzzleState> cmp = new Comparator<PuzzleState>() {
            @Override
            public int compare(final PuzzleState ps1, final PuzzleState ps2) {
                int f1 = ps1.getGval() + ps1.getHval();
                int f2 = ps2.getGval() + ps2.getHval();
                if (f1 < f2) {
                    return -1;
                }
                else if (f2 < f1) {
                    return 1;
                }
                return 0;
            }
        };
        
        this.state = new PuzzleState(this.state.getTiles(),
                                     this.state.getHeuristic(), 0);
        PriorityQueue<PuzzleState> frontier =
            new PriorityQueue<PuzzleState>(127, cmp);
        frontier.offer(new PuzzleState(this.state.getTiles(),
                                       this.state.getHeuristic(),
                                       this.state.getGval()));
        TreeSet<PuzzleState> explored = new TreeSet<PuzzleState>();
        
        String message;
        long startTime = System.nanoTime();
        while (true) { // the textbook uses a true loop
            if (0 == frontier.size()) {
                message = "Failed to solve after ";
                break;
            }
            this.state = frontier.poll();
            if (0 == this.state.getHval()) {
                message = "Solved with ";
                break;
            }
            explored.add(new PuzzleState(this.state.getTiles(),
                                         this.state.getHeuristic(),
                                         this.state.getGval()));
            TreeSet<PuzzleState> children = this.state.generateChildren();
            for (PuzzleState child : children) {
                if (!explored.contains(child) && !frontier.contains(child)) {
                    frontier.offer(new PuzzleState(this.state.getTiles(),
                                                   this.state.getHeuristic(),
                                                   this.state.getGval()));
                }
                else if (frontier.contains(child)) {
                    for (PuzzleState ps : frontier) {
                        if (child.equals(ps) && child.getGval() < ps.getGval()) {
                            ps.setGval(child.getGval());
                        }
                    }
                }
                nodeCounter++;
            }
        }
        long endTime = System.nanoTime();
        long elapsedTime = (endTime - startTime) / 1000000;
        message += nodeCounter + " node(s)";
        message += ", " + this.state.getGval() + " step(s)";
        message += ", " + elapsedTime + " ms elapsed";
        System.out.println(message);
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

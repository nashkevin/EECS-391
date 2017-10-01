/**
 * Puzzle
 *
 * @author   Kevin Nash (kjn33)
 * @version  2017.9.27
 */
package P1;

import java.lang.Math;
import java.lang.StringBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;
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
        this.state = new PuzzleState("b12 345 678");
        Random rand = new Random(maxSteps);
        PriorityQueue<PuzzleState> children;
        
        for (int i = 0; i < maxSteps; i++) {
            children = this.state.generateChildren();
            int size = children.size();
            for (int j = 0; j < (Math.abs(rand.nextInt()) % size) + 1; j++) {
                this.state = children.poll();
            }
        }
        this.state = new PuzzleState(this.state.getTiles(),
                                     this.state.getHeuristic(), 0);
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
        PriorityQueue<PuzzleState> beam =
            new PriorityQueue<PuzzleState>(k, new Comparator<PuzzleState>() {
            @Override
            public int compare(PuzzleState ps1, PuzzleState ps2) {
                return ps1.getHval() - ps2.getHval();
            }
        });
        beam.add(this.state);
        PriorityQueue<PuzzleState> nextPly = new PriorityQueue<PuzzleState>();
        long startTime = System.nanoTime();
        
        while (0 < this.state.getHval() && nodeCounter < this.maxNodes) {
            for (PuzzleState s1 : beam) {
                PriorityQueue<PuzzleState> children = s1.generateChildren();
                for (PuzzleState s2 : children) {
                    nextPly.add(s2);
                    nodeCounter++;
                }
            }
            beam.clear();
            for (int i = 0; i < k && !nextPly.isEmpty(); i++) {
                beam.add(nextPly.poll());
            }
            this.state = beam.peek();
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
        } else if (heuristic.equals("h2")) {
            this.state = new PuzzleState(this.state.getTiles(),
                                         PuzzleState.Heuristic.SUM_DISTANCES, 0);
        } else {
            System.err.println("Invalid input");
            return;
        }
        int nodeCounter = 1;
        
        PriorityQueue<PuzzleState> frontier = new PriorityQueue<PuzzleState>();
//        System.out.println("offered " + this.state + " to frontier");
        frontier.offer(new PuzzleState(this.state.getTiles(),
                                       this.state.getHeuristic(),
                                       this.state.getGval()));
//        System.out.println("frontier:\n" + frontier);
        PriorityQueue<PuzzleState> explored = new PriorityQueue<PuzzleState>();
        
        String message;
        long startTime = System.nanoTime();
        while (true) { // the textbook uses a true loop
            if (0 == frontier.size() || maxNodes < nodeCounter) {
                message = "Failed to solve after ";
                break;
            }
            this.state = frontier.poll();
            System.out.println(this.state);
//            System.out.println("popped " + this.state + " from frontier");
//            System.out.println("frontier:\n" + frontier);
            if (0 == this.state.getHval()) {
                message = "Solved with ";
                break;
            }
//            System.out.println("added " + this.state + " to explored");
            explored.add(new PuzzleState(this.state.getTiles(),
                                         this.state.getHeuristic(),
                                         this.state.getGval()));
//            System.out.println("explored:\n" + explored);
            PriorityQueue<PuzzleState> children = this.state.generateChildren();
//            System.out.println("children:\n" + children);
            for (PuzzleState child : children) {
//                System.out.println("explored contains " + child + "? " + explored.contains(child));
//                System.out.println("frontier contains " + child + "? " + frontier.contains(child));
                if (!explored.contains(child) && !frontier.contains(child)) {
//                    System.out.println("offered " + child + " to frontier");
                    frontier.offer(new PuzzleState(child.getTiles(),
                                                   child.getHeuristic(),
                                                   child.getGval()));
                    
                }
                else if (frontier.contains(child)) {
                    PuzzleState[] temp = new PuzzleState[frontier.size()];
                    temp = frontier.toArray(temp);
                    for (PuzzleState ps : temp) {
                        if (child.equals(ps) && child.getGval() < ps.getGval()) {
//                            System.out.println("updated frontier from " + ps + " gVal " + ps.getGval() + " to " + child + " gval " + child.getGval());
                            frontier.remove(ps);
                            frontier.offer(child);
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
        this.state = new PuzzleState(this.state.getTiles(), this.state.getHeuristic(), 0);
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

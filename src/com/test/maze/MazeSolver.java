package com.test.maze;
import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
import java.util.Stack;
/**
    This class go through the maze node graph and find the path from start to end.
    Also it traces the path from end to start and mark the trace on the maze.
    This uses Depth first search technique, But it can use any algorithm technique as the
    graph is already build in the Maze class.
 */
public class MazeSolver {

    public static final char Filling_CHAR = '.';
    /*
       read the graph and trace the path from start to end.
    */
    static void solveMaze(Maze maze) {
        Stack<Maze.Node> stack = new Stack<Maze.Node>();
        // keep track of previous point visited on the node point. This needs to track the path.
        Point[] [] prevPoints = new Point[maze.getHeight()][maze.getWidth()];
        // start the graph from maze start point.
        stack.add(maze.getStart());
        Maze.Node end = maze.getEnd();
        int count = 0;
        //boolean finished = false;

        while (!stack.isEmpty())
        {
            count++;
            Maze.Node item = stack.pop();
            // if reached to end point stop.
            if (item.point.equals(end.point)) {
                // finished = true;
                break;
            }
            // mark it the node visited.
            item.setVisited(true);

            // process all the neighbours of the node.
            List<Maze.Node> neighbours = item.getNeighbours();
            for (Maze.Node n : neighbours) {
                if(n!=null && !n.isVisited())
                {
                    stack.add(n);
                    prevPoints[n.point.getY()][n.point.getX()] = item.point;
                }
            }
        }
        tracePath(maze, prevPoints);
    }

    /*
       trace the path of the node
     */
    private static void tracePath(Maze maze, Point[][] prevPoints) {
        // start tracing from the end of the graph backwards.
        int column = maze.getEnd().point.getX();
        int rows = maze.getEnd().point.getY();
        // find previous point at the end node.
        Point currentPoint = prevPoints[rows][column];
        // There is no
        if(!Optional.ofNullable(currentPoint).isPresent())
            throw new IllegalArgumentException("Not a valid maze, there is no route between start to end");
        populatePassThrough(maze, column, rows, currentPoint);

        // trace till the start point for the route, from end point.
        // still needs more refactoring.
        while (!currentPoint.equals(maze.getStart().point)) {
            maze.getMazeData()[currentPoint.getY()][currentPoint.getX()] = Filling_CHAR;
            column = currentPoint.getX();
            rows = currentPoint.getY();
            currentPoint = prevPoints[rows][column];
            populatePassThrough(maze, column, rows, currentPoint);
        }
    }

    /*
     populate all the cells path in the straight line or vertical line,
     which was skipped while avoiding many unnecessary nodes in the graph building
     because it was pass through in the same direction.
     but because we have to populate each cell for output, we have to find the cell and populate with '.'.
    */
    private static void populatePassThrough(Maze maze, int column, int rows, Point currentPoint) {

        populateUpDownCells(maze, column, rows, currentPoint);
        populateLeftRightCells(maze, column, rows, currentPoint);
    }

    /*
        populate left to right path on the maze which was not part of the graph node.
     */
    private static void populateLeftRightCells(Maze maze, int column, int rows, Point currentPoint) {
        // this still needs more refactoring.
        if (rows == currentPoint.getY()) {
            int cntCellPath = column - currentPoint.getX();
            boolean left = cntCellPath > 0;
            for (int i = 1; i < Math.abs(cntCellPath); i++) {
                maze.getMazeData()[rows][left ? column - i : column + i] = Filling_CHAR;
            }
        }
    }

    /*
        populate up down path on the maze which was not part of the graph node.
     */
    private static void populateUpDownCells(Maze maze, int column, int rows, Point currentPoint) {
        // still needs more refactoring.
        if (column == currentPoint.getX()) {
            int cntCellPath = rows - currentPoint.getY();
            boolean up = cntCellPath > 0;
            for (int i = 1; i < Math.abs(cntCellPath); i++) {
                maze.getMazeData()[up ? rows - i : rows + i][column] = Filling_CHAR;
            }
        }
    }
}

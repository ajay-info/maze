package com.test.maze;

import javax.swing.text.html.Option;
import java.io.IOException;
import java.util.*;

/**
    Maze class gets the data and builds a graph of nodes in one pass and also find the start and end node.
    This graph nodes can be searched and traced with different algorithm.
    It is assumed that the maze first sets of rows is always wall, Maze first and last columns are always a wall.
 */
public class Maze {

    private int width, height, cnt;
    private char [][] mazeData;
    private Maze.Node  start, end;
    private Maze.Node [] topNodes;

    public Maze(char [][] mazeData) throws IOException {
        this.mazeData = mazeData;
        width = mazeData[0].length;
        height = mazeData.length;
        initMazeLinks();

        if (!Optional.ofNullable(start).isPresent())
            throw new IllegalArgumentException("Could not find starting point (S), aborting.");
        if ((!Optional.ofNullable(end).isPresent()))
            throw new IllegalArgumentException("Could not find ending point (E), aborting.");

        // just for demo purpose. should be removed and logged as logger, if needed
        System.out.println("Start " + start.point.toString());
        System.out.println("End " + end.point.toString());
        System.out.println("Total no of nodes build in the graph for maze " + cnt);
    }

    public char[][] getMazeData() {
        return mazeData;
    }

    /*
      This initialize the maze and build the connected graph of the nodes.
      It does not build the node, if it is just a pass through in left or right
      and up and down direction.
    */
    private void initMazeLinks() {
        topNodes = new Maze.Node[width];
        boolean curr = false;
        boolean prev = false;
        boolean next = false;
        NodeType currNodeType = NodeType.WALL;
        NodeType prevNodeType = NodeType.WALL;
        NodeType nextNodeType = NodeType.WALL;

        NodeType type;
        // it is assumed first row is always a wall, so start from 1, validation can be added if needed
        for(int rows = 1; rows < height-1; rows++) {
            curr = false;
            currNodeType = NodeType.WALL;
            nextNodeType = getNodeType(rows, 1);
            next = isPath(nextNodeType);
            Maze.Node leftNode = null;
            // it is assumed first column is always a wall, so start from 1
            // validation can be added for this.
            for (int column =1; column < width -1; column++) {
                Maze.Node xNode = null;
                prev = curr;
                prevNodeType = currNodeType;
                curr = next;
                currNodeType = nextNodeType;
                nextNodeType = getNodeType(rows, column +1);
                next = isPath(nextNodeType);

                if (!curr) {
                    // it is a wall do not create a node
                    continue;
                }
                if (prev) {
                    if(next) {
                        // it is a Path Path Path
                        // if above or below is path create node or ignore
                        if (isPath(getNodeType(rows -1, column))
                                || isPath(getNodeType(rows + 1, column))
                                || currNodeType == NodeType.START
                                || currNodeType == NodeType.END
                        ) {
                            xNode = new Maze.Node(new Point(rows, column));
                            if (leftNode != null) {
                                leftNode.addNeighbours(xNode);
                            }
                            xNode.addNeighbours(leftNode);
                            leftNode = xNode;

                            // check if it start or end Node
                            setStartEndNode(currNodeType, xNode);
                        }
                    } else {
                        // it is a Path Path Wall, always create a node.
                        xNode = new Maze.Node(new Point(rows,column));
                        if (leftNode != null) {
                            leftNode.addNeighbours(xNode);
                        }
                        xNode.addNeighbours(leftNode);
                        leftNode = null;
                        // check if it start or end Node
                        setStartEndNode(currNodeType, xNode);
                    }
                } else {
                    if(next) {
                        // it is a Wall Path Path, always create a node.
                        xNode = new Maze.Node(new Point(rows, column));
                        leftNode = xNode;
                        // check if it start or end Node
                        setStartEndNode(currNodeType, xNode);
                    } else {
                        // it is a Wall Path Wall
                        // create if dead end or if it is start or end
                        if (!isPath(getNodeType(rows - 1, column))
                                || !isPath(getNodeType(rows + 1, column))
                                || currNodeType == NodeType.START
                                || currNodeType == NodeType.END
                        ) {
                            xNode = new Maze.Node(new Point(rows,column));
                            setStartEndNode(currNodeType, xNode);
                        }
                    }
                }
                // if path above connect to topnode, this allows to connect the nodes in vertical direction.
                // if it is a pass through.
                if (xNode != null) {
                    if (isPath(getNodeType(rows -1, column))) {
                        Maze.Node topNode = topNodes[column];
                        topNode.addNeighbours(xNode);
                        xNode.addNeighbours(topNode);
                    }

                    // if there is a path down, put this on topnode, to connect later.
                    if(isPath(getNodeType(rows + 1, column))) {
                        topNodes[column] = xNode;
                    } else {
                        topNodes[column] = null;
                    }
                    cnt++;
                }
            }
        }
    }
    /*
        mark the node as start or end if it is.
     */
    private void setStartEndNode(NodeType currNodeType, Node xNode) {
        if (currNodeType== NodeType.START) {
            if (Optional.ofNullable(start).isPresent())
                throw new IllegalArgumentException("More than one starting (S) point in the maze");
            start = xNode;
        }
        if (currNodeType == NodeType.END) {
            if (Optional.ofNullable(end).isPresent())
                throw new IllegalArgumentException("More than one End (E) point in the maze");

            end = xNode;
        }
    }

    /*
        EveryNode is path except WALL.
     */
    private boolean isPath(NodeType type) {
        return type == NodeType.START
                || type == NodeType.END
                || type == NodeType.PATH;
    }

    // returns the node type.
    private NodeType getNodeType  (int rows, int column) {
        return NodeType.valueOf(mazeData[rows][column]);
    }

    /*
        print the maze. This is just printing on Sys.out. which needs to be refactored.
     */
    public void print() {
        Arrays.stream(mazeData).forEach(System.out::println);
    }

    /*
        Node class, which represents a place on the maze and used to build the graph.
        This has all the direction, which stored the linking between nodes.
        There will be four max connection depending on the Node.
     */
    static class Node {
        // represents the co-ordinate of this Node on the maze.
        Point point;
        // list of connection of the node on the maze.
        private List<Maze.Node> neighbours;

        boolean isVisited() {
            return visited;
        }

        public void setVisited(boolean visited) {
            this.visited = visited;
        }

        // if the node is visited, set to true, so that it does not go in loop.
        private boolean visited = false;

        private Node(Point point) {
            this.point = point;
            neighbours = new ArrayList<>();
        }
        void addNeighbours(int position, Maze.Node node)
        {
            this.neighbours.add(position, node);
        }
        void addNeighbours(Maze.Node node)
        {
            this.neighbours.add(node);
        }
        List<Maze.Node> getNeighbours() {
            return neighbours;
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Node getStart() {
        return start;
    }

    public Node getEnd() {
        return end;
    }
}

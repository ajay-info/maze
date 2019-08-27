package com.test.maze;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.CharBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * File reader and driver to read the maze file.
 * It initialize the Maze object.
 * Calls MazeSolver to solve the maze.
 * prints the Maze before and after.
 */
public class Reader {
    public static void main(String[] args) throws IOException {
        Optional<String> fileName = Optional.ofNullable(PropertyFileReader.INSTANCE.getMazeFileName());
        if (!fileName.isPresent())
            throw new IllegalArgumentException("Could not find the file name in property file, aborting.");
        Reader reader = new Reader();
        char [][] mazeData = reader.read(fileName.get());
        if (mazeData.length == 0)
            throw new IllegalArgumentException("Maze File is empty, aborting.");
        int width = mazeData[0].length;
        int column = mazeData.length;

        // check the size of each row is same in the maze, it can be either rectangle or square.
        if (Arrays.stream(mazeData).anyMatch(item -> item.length != width))
            throw new IllegalArgumentException("Maze in the file should be rectangular or square");

        // check the walls exist on corners.
        if (!Arrays.stream(mazeData).allMatch((item -> NodeType.valueOf(item[0]) == NodeType.WALL)))
            throw new IllegalArgumentException("First Column in maze is not a wall");
        if (!Arrays.stream(mazeData).allMatch((item -> NodeType.valueOf(item[width - 1]) == NodeType.WALL)))
            throw new IllegalArgumentException("Last Column in maze is not a wall");
        // There is no stream of char
        if(!CharBuffer.wrap(mazeData[0]).chars().allMatch((item -> NodeType.valueOf((char)item) == NodeType.WALL)))
            throw new IllegalArgumentException("First row in maze is not a wall");
        if(!CharBuffer.wrap(mazeData[column - 1]).chars().allMatch((item -> NodeType.valueOf((char)item) == NodeType.WALL)))
            throw new IllegalArgumentException("last row in maze is not a wall");
        Maze maze = new Maze(mazeData);


        System.out.println("********* before solving the Maze ***********");
        System.out.println();

        // print the maze
        maze.print();
        MazeSolver.solveMaze(maze);
        // print after the path is found
        System.out.println();
        System.out.println("********* after solving the Maze *************");
        maze.print();
    }

    /*
      reads the given maze file name and returns the maze data as a array.
     */
    private char [][]  read(String mazeFileName) throws IOException {
        // this needs to be read from classpath..
        Path path = Paths.get(".");
        try {
            path = Paths.get(getClass().getClassLoader().getResource(mazeFileName).toURI());
        } catch (URISyntaxException e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
        }

        if (!Files.isRegularFile(path) || !Files.isReadable(path)) {
            throw new IllegalArgumentException("Cannot locate readable file " + mazeFileName);
        }
        try (Stream<String> stream = Files.lines(path)) {
            return stream.map(line -> line.toCharArray()).toArray(size -> new char [size][1]);
        }
    }
}

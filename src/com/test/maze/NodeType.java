package com.test.maze;

/**
 * This enum class hold all the different valid blocks on the Maze.
 * If there is a block which does not belongs to this, then it is a invalid maze.
 */
public enum NodeType {
    WALL('#'), PATH(' '), START('S'), END('E');
    private char type;
    private NodeType(char c) {
        this.type = c;
    }

    /*
        returns all the valid block based on characters.
     */
    public static NodeType valueOf(char c) {
        NodeType type;
        switch (c) {
            case 'S':
                type = NodeType.START;
                break;
            case 'E':
                type = NodeType.END;
                break;
            case ' ':
                type = NodeType.PATH;
                break;
            case '#':
                type = NodeType.WALL;
                break;
            default:
                throw new IllegalArgumentException("Invalid character found, Maze is not a valid maze" + c);
        }
        return type;
    }
}

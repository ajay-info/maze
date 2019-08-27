package com.test.maze;

import java.io.IOException;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Config file reader to read the config file.
 */
public enum PropertyFileReader {
    INSTANCE;

    private final Properties properties;

    PropertyFileReader() {
        properties = new Properties();
        try {
            properties.load(getClass().getClassLoader().getResourceAsStream("config.properties"));
            // should be removed just for demo purpose
            Logger.getLogger(getClass().getName()).log(Level.INFO, "if source maze file is not found, please check the file name '" + properties.getProperty("maze.file.name") + "' exist in the folder, from where java command is running for demo");
        } catch (IOException e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public String getMazeFileName() {
        return properties.getProperty("maze.file.name");
    }
}

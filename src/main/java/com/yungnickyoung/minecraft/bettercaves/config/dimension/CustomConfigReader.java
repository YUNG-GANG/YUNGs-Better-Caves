package com.yungnickyoung.minecraft.bettercaves.config.dimension;

import com.yungnickyoung.minecraft.bettercaves.config.Settings;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class CustomConfigReader {
    // Load config with default values based on user global config
    public ConfigHolder config = new ConfigHolder();

    public static void loadConfig(String path) {
        File f = new File(path);
        if (!f.exists()) {
            Settings.LOGGER.warn("File " + path + " does not exist");
        }
        if (f.isDirectory()) {
            Settings.LOGGER.warn(path + " is a directory, not a file");
        }

        try {
            FileReader reader = new FileReader(f);
            loadFile(reader);
        } catch (FileNotFoundException e) {
            Settings.LOGGER.warn("File " + path + " not found");
        }

    }

    private static void loadFile(FileReader reader) {

    }
}

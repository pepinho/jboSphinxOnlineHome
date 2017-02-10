//
// Copyright (c) 2017 by jbo - Josef Baro
//
// Project: jbo-soo-home-graphics
// File: FileBackup.java
// Created: 08.02.2017 - 18:47:52
//
package de.jbo.soo.home.graphics.converter.colors.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Josef Baro (jbo) <br>
 * @version 08.02.2017 jbo - created <br>
 */
public class FileBackup {

    private static final String BACKUP_SUFFIX = ".backup";

    private static final Logger LOG = LoggerFactory.getLogger(FileBackup.class);

    public boolean backup(String filepath) {
        boolean state = false;
        FileReader reader = null;
        FileWriter writer = null;

        LOG.info("Backup start for: " + filepath);

        File fileToBackup = new File(filepath);

        if (!fileToBackup.exists()) {
            LOG.error("The file to be backupped doesn't exist. We abort...");
            return state;
        }

        File fileBackupDirectory = fileToBackup.getParentFile();

        File fileBackup = new File(fileBackupDirectory, fileToBackup.getName() + BACKUP_SUFFIX);

        try {
            reader = new FileReader(fileToBackup);
        } catch (FileNotFoundException e) {
            LOG.error("The file to be backuped could not be accessed for reading. We abort...", e);
            return state;
        }
        try {
            writer = new FileWriter(fileBackup);
        } catch (IOException e) {
            IOUtils.closeQuietly(reader);
            LOG.error("The backup-file could not be created. We abort...", e);
            return state;
        }

        try {
            int copied = IOUtils.copy(reader, writer);
            LOG.info("File successfully backupped. " + copied + " bytes transfered.");
            state = true;
        } catch (IOException e) {
            LOG.error("The backup process failed. We abort...", e);
        } finally {
            IOUtils.closeQuietly(reader);
            IOUtils.closeQuietly(writer);
        }

        return state;
    }
}

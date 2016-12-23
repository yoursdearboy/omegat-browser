package com.yoursdearboy.omegat.plugins.browser;

import org.omegat.util.DirectoryMonitor;

import java.io.File;
import java.io.FilenameFilter;

/**
 *
 */
class ScriptsMonitor implements DirectoryMonitor.Callback, DirectoryMonitor.DirectoryCallback {
    private final DirectoryMonitor directoryMonitor;
    private final FilenameFilter filenameFilter;
    private final ScriptsRunner scriptsRunner;

    ScriptsMonitor(ScriptsRunner scriptsRunner, FilenameFilter filenameFilter) {
        this.scriptsRunner = scriptsRunner;
        this.directoryMonitor = new DirectoryMonitor(scriptsRunner.getScriptsDirectory(), this, this);
        this.filenameFilter = filenameFilter;
    }

    void start() {
        directoryMonitor.start();
    }

    @Override
    public void fileChanged(File file) {
        if (!filenameFilter.accept(file.getParentFile(), file.getName())) return;
        if (file.exists()) {
            scriptsRunner.disable(file);
            scriptsRunner.enable(file);
        } else {
            scriptsRunner.disable(file);
        }
    }

    @Override
    public void directoryChanged(File file) {

    }
}

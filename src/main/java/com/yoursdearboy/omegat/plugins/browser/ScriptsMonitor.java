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
    private boolean skipOnStartFlag = false;

    ScriptsMonitor(ScriptsRunner scriptsRunner, File scriptsDirectory, FilenameFilter filenameFilter) {
        this.scriptsRunner = scriptsRunner;
        this.directoryMonitor = new DirectoryMonitor(scriptsDirectory, this, this);
        this.filenameFilter = filenameFilter;
    }

    void start(boolean skipOnStart) {
        skipOnStartFlag = skipOnStart;
        directoryMonitor.checkChanges();
        skipOnStartFlag = false;
        directoryMonitor.start();
    }

    @Override
    public void fileChanged(File file) {
        if (skipOnStartFlag) return;
        if (!filenameFilter.accept(file.getParentFile(), file.getName())) return;
        if (file.exists()) {
            scriptsRunner.remove(file);
            scriptsRunner.add(file);
            scriptsRunner.enable(file);
        } else {
            scriptsRunner.remove(file);
        }
    }

    @Override
    public void directoryChanged(File file) {

    }
}

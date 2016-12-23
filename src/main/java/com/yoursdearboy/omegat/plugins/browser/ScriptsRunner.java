package com.yoursdearboy.omegat.plugins.browser;

import javax.script.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;

/**
 *
 */
class ScriptsRunner {
    private final ScriptEngine scriptEngine;
    private final ScriptsMonitor scriptsMonitor;
    private final File scriptsDirectory;

    ScriptsRunner(File scriptsDirectory) {
        this.scriptsDirectory = scriptsDirectory;
        this.scriptsMonitor = new ScriptsMonitor(this, new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith("groovy");
            }
        });

        Bindings bindings = new SimpleBindings();
        bindings.put("BrowserPane", BrowserPane.class);
        ScriptEngineManager scriptEngineManager = new ScriptEngineManager(BrowserPlugin.class.getClassLoader());
        scriptEngineManager.setBindings(bindings);
        this.scriptEngine = scriptEngineManager.getEngineByName("groovy");
    }

    void start() {
        scriptsMonitor.start();
    }

    File getScriptsDirectory() {
        return scriptsDirectory;
    }

    void enable(File file) {
        try {
            scriptEngine.eval(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ScriptException e) {
            e.printStackTrace();
        }
    }

    void disable(File file) {

    }
}

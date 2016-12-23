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
    private final File scriptsDir;
    private final ScriptEngine scriptEngine;

    ScriptsRunner(File scriptsDir) {
        this.scriptsDir = scriptsDir;
        Bindings bindings = new SimpleBindings();
        bindings.put("BrowserPane", BrowserPane.class);
        ScriptEngineManager scriptEngineManager = new ScriptEngineManager(BrowserPlugin.class.getClassLoader());
        scriptEngineManager.setBindings(bindings);
        this.scriptEngine = scriptEngineManager.getEngineByName("groovy");
    }

    File[] getScripts() {
        return scriptsDir.listFiles(new FilenameFilter(){
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".groovy");
            }
        });
    }

    Object eval(File script) {
        try {
            return scriptEngine.eval(new FileReader(script));
        } catch (ScriptException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}

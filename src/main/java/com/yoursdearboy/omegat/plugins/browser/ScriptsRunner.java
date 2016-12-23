package com.yoursdearboy.omegat.plugins.browser;

import org.omegat.util.Log;

import javax.script.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
class ScriptsRunner {
    private final ScriptEngine scriptEngine;
    private List<ScriptsEventListener> listeners;

    ScriptsRunner() {
        this.listeners = new ArrayList<ScriptsEventListener>();
        Bindings bindings = new SimpleBindings();
        bindings.put("BrowserPane", BrowserPane.class);
        bindings.put("ScriptsEventListener", ScriptsEventListener.class);
        bindings.put("scriptsRunner", this);
        ScriptEngineManager scriptEngineManager = new ScriptEngineManager(BrowserPlugin.class.getClassLoader());
        scriptEngineManager.setBindings(bindings);
        this.scriptEngine = scriptEngineManager.getEngineByName("groovy");
    }

    void registerEventListener(ScriptsEventListener listener) {
        listeners.add(listener);
    }

    void unregisterEventListener(ScriptsEventListener listener) {
        listeners.remove(listener);
    }

    void add(File file) {
        Log.log(String.format("Adding browser script: %s", file.getName()));
        for (Object listener : listeners.toArray()) ((ScriptsEventListener) listener).onAdd(file);
    }

    void enable(File file) {
        Log.log(String.format("Enabling browser script: %s", file.getName()));
        try {
            scriptEngine.eval(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ScriptException e) {
            e.printStackTrace();
        }
        for (Object listener : listeners.toArray()) ((ScriptsEventListener) listener).onEnable(file);
    }

    void disable(File file) {
        Log.log(String.format("Disabling browser script: %s", file.getName()));
        for (Object listener : listeners.toArray()) ((ScriptsEventListener) listener).onDisable(file);
    }

    void remove(File file) {
        disable(file);
        Log.log(String.format("Removing browser script: %s", file.getName()));
        for (Object listener : listeners.toArray()) ((ScriptsEventListener) listener).onRemove(file);
    }
}

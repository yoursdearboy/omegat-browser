package com.yoursdearboy.omegat.plugins.browser;

import javafx.application.Platform;
import org.omegat.core.Core;
import org.omegat.core.CoreEvents;
import org.omegat.core.events.IApplicationEventListener;
import org.omegat.util.StaticUtils;

import javax.script.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/*
 * @author Kirill Voronin (yoursdearboy@gmail.com)
 */
public class BrowserPlugin {
    private static String BROWSER_SCRIPTS_DIR_NAME = "browser-scripts";
    private static HashMap<String,BrowserPane> panes =
            new HashMap<String, BrowserPane>();
    private static ScriptEngine scriptEngine;

    public static void loadPlugins() {
        setupBrowserScriptsDir();
        setupScriptEngine();
        CoreEvents.registerApplicationEventListener(new IApplicationEventListener() {
            public void onApplicationStartup() {
                Platform.setImplicitExit(false);
                JMenu toolsMenu = Core.getMainWindow().getMainMenu().getToolsMenu();
                toolsMenu.addSeparator();
                toolsMenu.add(new AbstractAction("Open browser scripts") {
                    @Override
                    public void actionPerformed(ActionEvent event) {
                        try {
                            Desktop.getDesktop().open(getBrowserScriptsDir());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                for (File script : getScripts()) evalScript(script);
            }

            public void onApplicationShutdown() {

            }
        });
    }

    public static void unloadPlugins() {
    }

    public static BrowserPane getPane(String key, String title, String domain) {
        if (panes.containsKey(key)) {
            return panes.get(key);
        } else {
            BrowserPane pane = new BrowserPane(Core.getMainWindow(), key, title, domain);
            panes.put(key, pane);
            return pane;
        }
    }

    private static void setupScriptEngine() {
        Bindings bindings = new SimpleBindings();
        bindings.put("BrowserPlugin", BrowserPlugin.class);
        bindings.put("BrowserPane", BrowserPane.class);
        ScriptEngineManager scriptEngineManager = new ScriptEngineManager(BrowserPlugin.class.getClassLoader());
        scriptEngineManager.setBindings(bindings);
        scriptEngine = scriptEngineManager.getEngineByName("groovy");
    }

    private static Object evalScript(File script) {
        try {
            return scriptEngine.eval(new FileReader(script));
        } catch (ScriptException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static File getBrowserScriptsDir() {
        return new File(StaticUtils.getConfigDir(), BROWSER_SCRIPTS_DIR_NAME);
    }

    private static void setupBrowserScriptsDir() {
        File browserScriptsDir = getBrowserScriptsDir();
        if (!browserScriptsDir.exists()) browserScriptsDir.mkdir();
    }

    private static File[] getScripts() {
        return getBrowserScriptsDir().listFiles();
    }
}

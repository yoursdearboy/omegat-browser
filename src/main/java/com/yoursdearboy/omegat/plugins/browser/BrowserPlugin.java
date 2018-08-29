package com.yoursdearboy.omegat.plugins.browser;

import javafx.application.Platform;
import org.omegat.core.Core;
import org.omegat.core.CoreEvents;
import org.omegat.core.events.IApplicationEventListener;
import org.omegat.util.StaticUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

/*
 * @author Kirill Voronin (yoursdearboy@gmail.com)
 */
public class BrowserPlugin {
    private static String SCRIPTS_DIRNAME = "browser-scripts";
    private static String SCRIPTS_PREFS_FILENAME = "browser-scripts.prefs";
    private static ScriptsRunner scriptsRunner;
    private static ScriptsMonitor scriptsMonitor;
    private static ScriptsPrefs scriptsPrefs;
    private static FilenameFilter filenameFilter = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {
            return name.endsWith("groovy");
        }
    };

    public static void loadPlugins() {
        setupSystemProperties();
        setupScriptsDir();
        setupScriptsRunner();
        setupScriptsPrefs();
        CoreEvents.registerApplicationEventListener(new IApplicationEventListener() {
            @Override
            public void onApplicationStartup() {
                Platform.setImplicitExit(false);
                setupMenu();
                startScriptsRunner();
            }

            @Override
            public void onApplicationShutdown() {
            }
        });
    }

    public static void unloadPlugins() {
    }

    private static void setupSystemProperties() {
        System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
    }

    private static void setupScriptsDir() {
        File ScriptsDir = getScriptsDir();
        if (!ScriptsDir.exists()) ScriptsDir.mkdir();
    }

    private static void setupScriptsRunner() {
        scriptsRunner = new ScriptsRunner();
        scriptsMonitor = new ScriptsMonitor(scriptsRunner, getScriptsDir(), filenameFilter);
    }

    private static void startScriptsRunner() {
        java.util.List<File> scripts = scriptsPrefs.getEnabledScripts();
        for (File file : getScriptsDir().listFiles(filenameFilter)) {
            scriptsRunner.add(file);
            if (scripts.contains(file)) {
                scriptsRunner.enable(file);
            }
        }
        scriptsMonitor.start(true);
    }

    private static void setupScriptsPrefs() {
        scriptsPrefs = new ScriptsPrefs(getScriptsPrefsFile(), getScriptsDir(), scriptsRunner);
        scriptsPrefs.load();
        CoreEvents.registerApplicationEventListener(new IApplicationEventListener() {
            @Override
            public void onApplicationStartup() {

            }

            @Override
            public void onApplicationShutdown() {
                scriptsPrefs.save();
            }
        });
    }

    private static File getScriptsDir() {
        return new File(StaticUtils.getConfigDir(), SCRIPTS_DIRNAME);
    }

    private static File getScriptsPrefsFile() {
        return new File(StaticUtils.getConfigDir(), SCRIPTS_PREFS_FILENAME);
    }

    private static void setupMenu() {
        JMenu toolsMenu = Core.getMainWindow().getMainMenu().getToolsMenu();
        toolsMenu.addSeparator();
        toolsMenu.add(new AbstractAction("Open browser scripts") {
            @Override
            public void actionPerformed(ActionEvent event) {
                try {
                    Desktop.getDesktop().open(getScriptsDir());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        toolsMenu.add(new ScriptsMenu("Browser scripts", scriptsRunner));
    }
}

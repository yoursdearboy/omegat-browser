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
    private static String SCRIPTS_DIR_NAME = "browser-scripts";
    private static ScriptsRunner scriptsRunner;
    private static ScriptsMonitor scriptsMonitor;
    private static FilenameFilter filenameFilter = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {
            return name.endsWith("groovy");
        }
    };

    public static void loadPlugins() {
        setupScriptsDir();
        setupScriptsRunner();
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

    private static void setupScriptsDir() {
        File browserScriptsDir = getBrowserScriptsDir();
        if (!browserScriptsDir.exists()) browserScriptsDir.mkdir();
    }

    private static void setupScriptsRunner() {
        scriptsRunner = new ScriptsRunner();
        scriptsMonitor = new ScriptsMonitor(scriptsRunner, getBrowserScriptsDir(), filenameFilter);
    }

    private static void startScriptsRunner() {
        for (File file : getBrowserScriptsDir().listFiles(filenameFilter)) {
            scriptsRunner.add(file);
            scriptsRunner.enable(file);
        }
        scriptsMonitor.start(true);
    }

    private static File getBrowserScriptsDir() {
        return new File(StaticUtils.getConfigDir(), SCRIPTS_DIR_NAME);
    }

    private static void setupMenu() {
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
        toolsMenu.add(new ScriptsMenu("Browser scripts", scriptsRunner));
    }
}

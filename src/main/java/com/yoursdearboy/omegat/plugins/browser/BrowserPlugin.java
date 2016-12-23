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
import java.io.IOException;

/*
 * @author Kirill Voronin (yoursdearboy@gmail.com)
 */
public class BrowserPlugin {
    private static String BROWSER_SCRIPTS_DIR_NAME = "browser-scripts";
    private static ScriptsRunner scriptsRunner;

    public static void loadPlugins() {
        setupBrowserScriptsDir();
        setupScriptRunner();
        CoreEvents.registerApplicationEventListener(new IApplicationEventListener() {
            @Override
            public void onApplicationStartup() {
                Platform.setImplicitExit(false);
                setupMenu();
                scriptsRunner.start();
            }

            @Override
            public void onApplicationShutdown() {
            }
        });
    }

    public static void unloadPlugins() {
    }

    private static void setupBrowserScriptsDir() {
        File browserScriptsDir = getBrowserScriptsDir();
        if (!browserScriptsDir.exists()) browserScriptsDir.mkdir();
    }

    private static void setupScriptRunner() {
        scriptsRunner = new ScriptsRunner(getBrowserScriptsDir());
    }

    private static File getBrowserScriptsDir() {
        return new File(StaticUtils.getConfigDir(), BROWSER_SCRIPTS_DIR_NAME);
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
    }
}

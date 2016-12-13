package com.yoursdearboy.omegat.plugins.multitran;

import javafx.application.Platform;
import org.omegat.core.Core;
import org.omegat.core.CoreEvents;
import org.omegat.core.events.IApplicationEventListener;
import org.omegat.gui.main.DockableScrollPane;

import javax.swing.*;

/*
 * @author Kirill Voronin (yoursdearboy@gmail.com)
 */
public class MultitranPlugin {
    public static void loadPlugins() {
        Platform.setImplicitExit(false);
        CoreEvents.registerApplicationEventListener(new IApplicationEventListener() {
            public void onApplicationStartup() {
                MultitranPlugin.createPane();
            }

            public void onApplicationShutdown() {
            }
        });
    }

    public static void unloadPlugins() {
    }

    private static void createPane() {
        new MultitranPane(Core.getMainWindow());
    }
}

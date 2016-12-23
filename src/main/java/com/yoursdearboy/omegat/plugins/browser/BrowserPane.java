package com.yoursdearboy.omegat.plugins.browser;

import com.vlsolutions.swing.docking.DockingDesktop;
import com.vlsolutions.swing.docking.RelativeDockablePosition;
import org.omegat.gui.main.IMainWindow;
import org.omegat.gui.main.MainWindow;

import javax.swing.*;
import java.awt.*;

/**
 *
 */
class BrowserPane extends JPanel {
    private DockablePanel pane;
    private Browser browser;

    BrowserPane(final IMainWindow mainWindow, String key, String title) {
        new BrowserPane(mainWindow, key, title, null);
    }

    BrowserPane(final IMainWindow mainWindow, String key, String title, String domain) {
        super(new BorderLayout());

        pane = new DockablePanel(key, title, this, true);

        // FIXME: Add dockable using IMainWindow mainWindow
        final DockingDesktop desktop = getDockingDesktop((MainWindow) mainWindow);
        desktop.registerDockable(pane);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                desktop.addHiddenDockable(pane, RelativeDockablePosition.BOTTOM_LEFT);
            }
        });

        browser = new Browser(domain);
        add(browser, BorderLayout.CENTER);
        setVisible(true);
    }

    public Browser getBrowser() {
        return browser;
    }

    private DockingDesktop getDockingDesktop(MainWindow mainWindow) {
        DockingDesktop desktop = null;
        for (Component component : mainWindow.getContentPane().getComponents()) {
            if (component instanceof DockingDesktop) {
                desktop = (DockingDesktop) component;
                break;
            }
        }
        if (desktop == null) throw new RuntimeException("Can't find DockingDesktop to register Dockable panel");
        return desktop;
    }
}

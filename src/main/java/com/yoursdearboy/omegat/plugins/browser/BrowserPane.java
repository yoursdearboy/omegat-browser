package com.yoursdearboy.omegat.plugins.browser;

import com.vlsolutions.swing.docking.Dockable;
import com.vlsolutions.swing.docking.DockableState;
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
    private IMainWindow mainWindow;
    private DockablePanel pane;
    private Browser browser;

    BrowserPane(final IMainWindow mainWindow, String key, String title) {
        new BrowserPane(mainWindow, key, title, null);
    }

    BrowserPane(final IMainWindow mainWindow, String key, String title, String domain) {
        super(new BorderLayout());

        this.mainWindow = mainWindow;
        this.pane = new DockablePanel(key, title, this, true);

        // FIXME: Add dockable using IMainWindow mainWindow
        final DockingDesktop desktop = getDockingDesktop((MainWindow) mainWindow);
        desktop.registerDockable(pane);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                DockableState oldState = desktop.getDockableState(pane);
                if (oldState == null || oldState.isClosed()) {
                    desktop.addHiddenDockable(pane, RelativeDockablePosition.BOTTOM_LEFT);
                }
            }
        });

        this.browser = new Browser(domain);
        add(browser, BorderLayout.CENTER);
        setVisible(true);
    }

    public Browser getBrowser() {
        return browser;
    }

    // FIXME: actually close dockable, not just hide
    // The best I've achieved is just close (do nothing) and remove (hides from dock)
    // Algorithm from docs don't work or I don't understand it
    // https://code.google.com/archive/p/vldocking/wikis/tutorial3.wiki
    public void close() {
        DockingDesktop desktop = getDockingDesktop((MainWindow) mainWindow);
        desktop.close(pane);
        desktop.remove((Dockable) pane);
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

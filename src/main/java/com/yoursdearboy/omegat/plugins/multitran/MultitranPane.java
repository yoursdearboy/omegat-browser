package com.yoursdearboy.omegat.plugins.multitran;

import com.vlsolutions.swing.docking.DockingDesktop;
import com.vlsolutions.swing.docking.RelativeDockablePosition;
import org.omegat.gui.main.IMainWindow;
import org.omegat.gui.main.MainWindow;

import javax.swing.*;
import java.awt.*;

/**
 *
 */
class MultitranPane extends JPanel {
    private final DockablePanel pane;
    private final String key = "MULTITRAN";
    private final String title = "Multitran";
    private final Browser browser;

    MultitranPane(final IMainWindow mainWindow) {
        super(new BorderLayout());

        pane = new DockablePanel(key, title, this, true);

        // FIXME: Add dockable using IMainWindow mainWindow
        final DockingDesktop desktop = getDockingDesktop((MainWindow) mainWindow);
        desktop.registerDockable(pane);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (desktop.getDockableState(pane) == null) {
                    desktop.addHiddenDockable(pane, RelativeDockablePosition.BOTTOM_LEFT);
                }
            }
        });

        browser = new Browser("multitran.ru");
        add(browser, BorderLayout.CENTER);
        setVisible(true);
    }

    // NOTE: Requires MainWindow, not IMainWindow
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

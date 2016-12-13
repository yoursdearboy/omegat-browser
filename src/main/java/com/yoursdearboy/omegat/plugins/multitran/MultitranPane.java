package com.yoursdearboy.omegat.plugins.multitran;

import com.vlsolutions.swing.docking.DockingDesktop;
import com.vlsolutions.swing.docking.RelativeDockablePosition;
import org.omegat.gui.main.DockableScrollPane;
import org.omegat.gui.main.IMainWindow;
import org.omegat.gui.main.MainWindow;
import org.omegat.gui.main.MainWindowUI;
import org.omegat.util.StaticUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.TimerTask;

/**
 *
 */
class MultitranPane extends JPanel {
    private final DockableScrollPane pane;
    private final String key = "MULTITRAN";
    private final String title = "Multitran";
    private final Browser browser;

    MultitranPane(final IMainWindow mainWindow) {
        super(new BorderLayout());

        pane = new DockableScrollPane(key, title, this, true);

        // TODO: Check if this is neccessary on project change
        // FIXME: Why this hack is necessary?
        if (new File(StaticUtils.getConfigDir(), MainWindowUI.UI_LAYOUT_FILE).exists()) {
            // We're in XML. Start loading before OmegaT ivokes readXML
            // TODO: Good position by default
            // (can be done by obtaining DockingDesktop from mainWindow's (casted to MainWindow) children
            mainWindow.addDockable(pane);
        } else {
            // No, we aren't in XML. Wait for ??? something
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    mainWindow.addDockable(pane);
                }
            });
        }

        browser = new Browser("multitran.ru");
        add(browser);

        setVisible(true);
    }
}

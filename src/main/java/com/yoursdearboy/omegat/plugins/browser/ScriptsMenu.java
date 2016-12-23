package com.yoursdearboy.omegat.plugins.browser;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class ScriptsMenu extends JMenu implements ScriptsEventListener {
    private final Map<File,JCheckBoxMenuItem> menuItems;
    private final ScriptsRunner scriptsRunner;

    ScriptsMenu(String label, ScriptsRunner scriptsRunner) {
        super(label);
        this.scriptsRunner = scriptsRunner;
        this.menuItems = new HashMap<File, JCheckBoxMenuItem>();
        scriptsRunner.registerEventListener(this);
    }

    @Override
    public void onAdd(final File file) {
        final JCheckBoxMenuItem menuItem = new JCheckBoxMenuItem(file.getName());
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (menuItem.getState()) {
                    scriptsRunner.enable(file);
                } else {
                    scriptsRunner.disable(file);
                }
            }
        });
        menuItems.put(file, menuItem);
        add(menuItem);
    }

    @Override
    public void onEnable(File file) {
        JCheckBoxMenuItem menuItem = menuItems.get(file);
        if (menuItem != null) {
            menuItem.setState(true);
        }
    }

    @Override
    public void onDisable(File file) {
        JCheckBoxMenuItem menuItem = menuItems.get(file);
        if (menuItem != null) {
            menuItem.setState(false);
        }
    }

    @Override
    public void onRemove(File file) {
        JCheckBoxMenuItem menuItem = menuItems.get(file);
        if (menuItem != null) {
            remove(menuItem);
            menuItems.remove(file);
        }
    }
}

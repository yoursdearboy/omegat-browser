package com.yoursdearboy.omegat.plugins.browser;

import com.vlsolutions.swing.docking.DockKey;
import com.vlsolutions.swing.docking.Dockable;
import com.vlsolutions.swing.docking.DockingConstants;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.text.JTextComponent;
import java.awt.*;

/**
 *
 */
public class DockablePanel extends JPanel implements Dockable {
    DockKey dockKey;

    public void setToolTipText(String text) {
        this.dockKey.setTooltip(text);
    }

    public void setName(String name) {
        this.dockKey.setName(name);
    }

    public DockablePanel(String key, String name, Component view, boolean detouchable) {
        super(new BorderLayout());

        add(view);

        if(view instanceof JTextComponent && UIManager.getBoolean("OmegaTDockablePanel.isProportionalMargins")) {
            JTextComponent panelBorder = (JTextComponent)view;
            int viewportBorder = panelBorder.getFont().getSize() / 2;
            panelBorder.setBorder(new EmptyBorder(viewportBorder, viewportBorder, viewportBorder, viewportBorder));
        }

        Border panelBorder1 = UIManager.getBorder("OmegaTDockablePanel.border");
        if(panelBorder1 != null) {
            this.setBorder(panelBorder1);
        }

        this.dockKey = new DockKey(key, name, (String)null, (Icon)null, DockingConstants.HIDE_BOTTOM);
        this.dockKey.setFloatEnabled(detouchable);
        this.dockKey.setCloseEnabled(false);
    }

    public DockKey getDockKey() {
        return this.dockKey;
    }

    public Component getComponent() {
        return this;
    }
}

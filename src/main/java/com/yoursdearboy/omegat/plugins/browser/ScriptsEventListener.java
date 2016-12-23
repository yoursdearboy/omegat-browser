package com.yoursdearboy.omegat.plugins.browser;

import java.io.File;

/**
 *
 */
public interface ScriptsEventListener {
    void onAdd(File file);
    void onEnable(File file);
    void onDisable(File file);
    void onRemove(File file);
}

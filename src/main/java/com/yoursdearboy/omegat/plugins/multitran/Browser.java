package com.yoursdearboy.omegat.plugins.multitran;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * http://docs.oracle.com/javafx/2/swing/SimpleSwingBrowser.java.htm
 */
class Browser extends JFXPanel {
    private WebEngine engine;
    private final String domain;

    Browser(final String domain) {
        this.domain = domain;
        Platform.runLater(new Runnable() {
            public void run() {
                WebView webView = new WebView();
                engine = webView.getEngine();
                setScene(new Scene(webView));
                loadURL(domain);
            }
        });
    }

    void loadURL(final String url) {
        Platform.runLater(new Runnable() {
            public void run() {
                String tmp = toURL(url);
                if (tmp == null) tmp = toURL("http://" + url);
                engine.load(tmp);
            }
        });
    }

    private static String toURL(String str) {
        try {
            return new URL(str).toExternalForm();
        } catch (MalformedURLException ignored) {
            return null;
        }
    }
}

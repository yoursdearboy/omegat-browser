package com.yoursdearboy.omegat.plugins.browser;

import javafx.scene.web.WebHistory;
import org.omegat.util.Log;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.awt.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.regex.Pattern;

/**
 * http://docs.oracle.com/javafx/2/swing/SimpleSwingBrowser.java.htm
 */
class Browser extends JFXPanel {
    private WebEngine webEngine;
    private WebView webView;
    private final Pattern domain;

    Browser() {
        this((Pattern) null);
    }

    Browser(final String domain) {
        this(Pattern.compile(domain));
    }

    Browser(final Pattern domain) {
        this.domain = domain;
        setLayout(new BorderLayout());
        Platform.runLater(new Runnable() {
            public void run() {
                webView = new WebView();
                webEngine = webView.getEngine();
                if (domain != null) {
                    fixDomain();
                }
                setScene(new Scene(webView));
            }
        });
    }

    public void loadURL(final String url) {
        Platform.runLater(new Runnable() {
            public void run() {
                String tmp = toURL(url);
                if (tmp == null) tmp = toURL("http://" + url);
                webEngine.load(tmp);
            }
        });
    }

    public WebView getWebView() {
        return webView;
    }

    public WebEngine getWebEngine() {
        return webEngine;
    }

    private static String toURL(String str) {
        try {
            return new URL(str).toExternalForm();
        } catch (MalformedURLException ignored) {
            return null;
        }
    }

    private void fixDomain() {
        webEngine.locationProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> observable, final String oldValue, String newValue) {
                try {
                    URI newUri = new URI(newValue);
                    String newDomain = newUri.getHost();
                    if (newDomain != null && newDomain.startsWith("www.")) {
                        newDomain = newDomain.substring(4);
                    }
                    if (newDomain == null ||
                        (domain != null && !domain.matcher(newDomain).find())) {
                        Log.log(String.format(
                                "New domain %s doesn't match %s. Redirecting back.",
                                newDomain, domain));
                        Platform.runLater(new Runnable() {
                            public void run() {
                                WebHistory history = webEngine.getHistory();
                                String currentUrl = history.getEntries().get(history.getCurrentIndex()).getUrl();
                                webEngine.load(currentUrl);
                            }
                        });
                        try {
                            Desktop.getDesktop().browse(newUri);
                        } catch (IOException ignored) {
                        }
                    }
                } catch (URISyntaxException ignored) {
                }
            }
        });
    }
}

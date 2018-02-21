package com.yoursdearboy.omegat.plugins.browser;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.scene.web.WebEngine;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URLEncoder;
import java.util.Locale;
import java.util.regex.Pattern;

public class ScriptHelpers {
    /* Encode text to include in url
     */
    private static String  BLACKCHARS = "( |<|>)";
    private static Pattern STRIP_PATTERN = Pattern.compile(String.format(
            "^%s+|%s+\\$", BLACKCHARS, BLACKCHARS));

    public static String encodeText(String text) throws UnsupportedEncodingException {
        if (text == null) return null;
        return URLEncoder.encode(text, "UTF-8").replace("+", "%20");
    }

    public static String prepareText(String text, String text2) {
        String result = null;
        if (text != null && !text.isEmpty()) result = stripText(text);
        if (result == null) result = stripText(text2);
        return result;
    }

    public static String stripText(String text) {
        if (text == null) return null;
        text = text.trim();
        text = STRIP_PATTERN.matcher(text).replaceAll("");
        if (text.isEmpty()) return null;
        return text;
    }

    /* Reset fonts in BrowserPane
     */
    private static String RESET_FONTS_JS =
            "var css = '* { font-family: sans-serif !important; }',"
          + "    head = document.head || document.getElementsByTagName('head')[0],"
          + "    style = document.createElement('style');"
          + "    style.type = 'text/css';"
          + "if (style.styleSheet){"
          + "    style.styleSheet.cssText = css;"
          + "} else {"
          + "    style.appendChild(document.createTextNode(css));"
          + "}"
          + "head.appendChild(style);";

    public static void resetFonts(final WebEngine engine) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                engine.getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {
                    @Override
                    public void changed(ObservableValue<? extends Worker.State> observable, Worker.State oldValue, Worker.State newValue) {
                        if (newValue != Worker.State.SUCCEEDED) return;
                        engine.executeScript(RESET_FONTS_JS);
                    }
                });
            }
        });
    }

    /* Escape string
    *  Credits: Apache Commons
    */
    public static String escapeJavaStyleString(String str) throws IOException {
        if (str == null) {
            return null;
        }
        StringWriter writer = new StringWriter(str.length() * 2);
        escapeJavaStyleString(writer, str, true, true);
        return writer.toString();
    }

    private static void escapeJavaStyleString(Writer out, String str, boolean escapeSingleQuote,
                                       boolean escapeForwardSlash) throws IOException {
        if (out == null) {
            throw new IllegalArgumentException("The Writer must not be null");
        }
        if (str == null) {
            return;
        }
        int sz;
        sz = str.length();
        for (int i = 0; i < sz; i++) {
            char ch = str.charAt(i);

            // handle unicode
            if (ch > 0xfff) {
                out.write("\\u" + hex(ch));
            } else if (ch > 0xff) {
                out.write("\\u0" + hex(ch));
            } else if (ch > 0x7f) {
                out.write("\\u00" + hex(ch));
            } else if (ch < 32) {
                switch (ch) {
                    case '\b' :
                        out.write('\\');
                        out.write('b');
                        break;
                    case '\n' :
                        out.write('\\');
                        out.write('n');
                        break;
                    case '\t' :
                        out.write('\\');
                        out.write('t');
                        break;
                    case '\f' :
                        out.write('\\');
                        out.write('f');
                        break;
                    case '\r' :
                        out.write('\\');
                        out.write('r');
                        break;
                    default :
                        if (ch > 0xf) {
                            out.write("\\u00" + hex(ch));
                        } else {
                            out.write("\\u000" + hex(ch));
                        }
                        break;
                }
            } else {
                switch (ch) {
                    case '\'' :
                        if (escapeSingleQuote) {
                            out.write('\\');
                        }
                        out.write('\'');
                        break;
                    case '"' :
                        out.write('\\');
                        out.write('"');
                        break;
                    case '\\' :
                        out.write('\\');
                        out.write('\\');
                        break;
                    case '/' :
                        if (escapeForwardSlash) {
                            out.write('\\');
                        }
                        out.write('/');
                        break;
                    default :
                        out.write(ch);
                        break;
                }
            }
        }
    }

    private static String hex(char ch) {
        return Integer.toHexString((int) ch).toUpperCase(Locale.ENGLISH);
    }
}

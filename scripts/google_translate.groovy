import javafx.application.Platform
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.concurrent.Worker
import org.omegat.core.Core
import org.omegat.core.CoreEvents
import org.omegat.core.data.ProjectProperties
import org.omegat.core.data.SourceTextEntry
import org.omegat.core.events.IEntryEventListener
import org.omegat.core.events.IProjectEventListener

def FILENAME = "google_translate.groovy"
def KEY = "GOOGLE_TRANSLATE"
def TITLE = "Google Translate"
def DOMAIN = "translate.google.com"

def pane = BrowserPane.newInstance([Core.getMainWindow(), KEY, TITLE, DOMAIN] as Object[])

def updateSourceText = { text ->
    if (text == null) text = ""
    Platform.runLater(new Runnable() {
        @Override
        void run() {
            text = escapeJavaStyleString(text)
            String jsCode = "document.getElementById(\"source\").value = \"${text}\""
            pane.getBrowser().getWebEngine().executeScript(jsCode)
        }
    });
}

def entryEventListener = new IEntryEventListener() {
    @Override
    void onNewFile(String s) {
    }

    @Override
    void onEntryActivated(SourceTextEntry sourceTextEntry) {
        updateSourceText(sourceTextEntry.srcText)
    }
}

def projectEventListener = new IProjectEventListener() {
    @Override
    void onProjectChanged(IProjectEventListener.PROJECT_CHANGE_TYPE project_change_type) {
        if (project_change_type == IProjectEventListener.PROJECT_CHANGE_TYPE.LOAD ||
            project_change_type == IProjectEventListener.PROJECT_CHANGE_TYPE.CREATE ||
            project_change_type == IProjectEventListener.PROJECT_CHANGE_TYPE.MODIFIED
        ) {
            ProjectProperties pp = Core.getProject().getProjectProperties()
            String sourceCode = pp.getSourceLanguage().getLanguageCode().toLowerCase()
            String targetCode = pp.getTargetLanguage().getLanguageCode().toLowerCase()
            String url = "http://${DOMAIN}/#${sourceCode}/${targetCode}"
            browser = pane.getBrowser()
            browser.loadURL(url)
            Platform.runLater(new Runnable() {
                @Override
                void run() {
                    ReadOnlyObjectProperty<Worker.State> stateProperty = browser.getWebEngine().getLoadWorker().stateProperty()
                    stateProperty.addListener(new ChangeListener<Worker.State>() {
                        @Override
                        void changed(ObservableValue<? extends Worker.State> observable, Worker.State oldValue, Worker.State newValue) {
                            updateSourceText(Core.getEditor().getCurrentEntry().srcText)
                            stateProperty.removeListener(this)
                        }
                    })
                }
            })
        }

        if (project_change_type == IProjectEventListener.PROJECT_CHANGE_TYPE.CLOSE) {
            updateSourceText(null)
        }
    }
}

def scriptsEventListener = [onAdd: {}, onEnable: {}, onRemove: {}]
scriptsEventListener['onDisable'] = {File file ->
    if (file.getName() == FILENAME) {
        CoreEvents.unregisterEntryEventListener(entryEventListener)
        CoreEvents.unregisterProjectChangeListener(projectEventListener)
        scriptsRunner.unregisterEventListener(scriptsEventListener)
    }
};
scriptsEventListener = scriptsEventListener.asType(ScriptsEventListener)

CoreEvents.registerEntryEventListener(entryEventListener)
CoreEvents.registerProjectChangeListener(projectEventListener)
scriptsRunner.registerEventListener(scriptsEventListener)


/* Copied from Apache Commons */
String escapeJavaStyleString(String str) {
    if (str == null) {
        return null;
    }
    StringWriter writer = new StringWriter(str.length() * 2);
    escapeJavaStyleString(writer, str, true, true);
    return writer.toString();
}

void escapeJavaStyleString(Writer out, String str, boolean escapeSingleQuote,
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

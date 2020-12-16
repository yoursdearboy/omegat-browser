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

def pane = BrowserPane.get(KEY, TITLE, DOMAIN)
pane.getBrowser().loadURL(DOMAIN)

/* Main action that puts text into translation area */
def updateSourceText = { text ->
    if (text == null) text = ""
    Platform.runLater(new Runnable() {
        @Override
        void run() {
            text = ScriptHelpers.escapeJavaStyleString(text)
            String jsCode = "document.getElementById(\"source\").value = \"${text}\";document.getElementById('gt-ft-res').style.display = 'none';document.getElementById('gb').style.display = 'none';document.getElementById('gba').style.display = 'none';"
            pane.getBrowser().getWebEngine().executeScript(jsCode)
        }
    })
}

/* Listen for events and update text */
def entryEventListener = new IEntryEventListener() {
    @Override
    void onNewFile(String s) {
    }

    @Override
    void onEntryActivated(SourceTextEntry sourceTextEntry) {
        updateSourceText(sourceTextEntry.srcText)
    }
}

/* Also change language */
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

/* Disable everything when script is disabled */
def scriptsEventListener = [
        onAdd    : {},
        onEnable : {},
        onRemove : {},
        onDisable: { File file ->
            if (file.getName() == FILENAME) {
                pane.close()
                CoreEvents.unregisterEntryEventListener(entryEventListener)
                CoreEvents.unregisterProjectChangeListener(projectEventListener)
                scriptsRunner.unregisterEventListener(scriptsEventListener)
            }
        }].asType(ScriptsEventListener)

CoreEvents.registerEntryEventListener(entryEventListener)
CoreEvents.registerProjectChangeListener(projectEventListener)
scriptsRunner.registerEventListener(scriptsEventListener)

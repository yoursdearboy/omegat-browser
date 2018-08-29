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

def FILENAME = "deepl_translator.groovy"
def KEY = "DEEPL_TRANSLATOR"
def TITLE = "Deepl Translator"
def DOMAIN = "deepl.com"

def pane = BrowserPane.get(KEY, TITLE, DOMAIN)
pane.getBrowser().loadURL(DOMAIN)
ScriptHelpers.resetFonts(pane.getBrowser())

/* Method to extract source text area element for JS */
def getSourceTextAreaJS = { ->
    "document.getElementsByClassName('lmt__side_container--source')[0].getElementsByTagName('textarea')[0]"
}

/* Method to trigger translation update for JS */
def updateTranslationJS = { srcTextAreaRef ->
    """
var event = document.createEvent('HTMLEvents');
event.initEvent('change', false, true);
${srcTextAreaRef}.dispatchEvent(event);
"""
}

/* Main action that puts text into translation area */
def updateSourceText = { text ->
    if (text == null) text = ""
    Platform.runLater(new Runnable() {
        @Override
        void run() {
            text = ScriptHelpers.escapeJavaStyleString(text)
            String jsCode = """
var newText = "${text}";
var srcTextArea = ${getSourceTextAreaJS};
if (newText != srcTextArea.value) {
  srcTextArea.value = newText;
  ${updateTranslationJS("srcTextArea")}
}
"""
            pane.getBrowser().getWebEngine().executeScript(jsCode)
        }
    })
}

def updateLanguages = { srcLang, dstLang ->
    Platform.runLater(new Runnable() {
        @Override
        void run() {
            String jsCode = """
var sLang   = "${srcLang}";
var dLang   = "${dstLang}";
sLang       = sLang.toUpperCase();
dLang       = dLang.toUpperCase();

// set the target language
var dstLangItems = document.getElementsByClassName("lmt__language_select--target");
for (var i = 0, len = dstLangItems[0].childNodes[4].children.length; i < len; i++) {
    if (dstLangItems[0].childNodes[4].children[i].attributes[0].nodeValue == dLang){
      dstLangItems[0].childNodes[4].children[i].click();
    }
}

// set the source language
var srcLangItems = document.getElementsByClassName("lmt__language_select--source");
srcLangItems[0].childNodes[4].children[0].click();
for (var i = 0, len = srcLangItems[0].childNodes[4].children.length; i < len; i++) {
    if (srcLangItems[0].childNodes[4].children[i].attributes[0].nodeValue == sLang){
      srcLangItems[0].childNodes[4].children[i].click();
    }
}

var srcTextArea = ${getSourceTextAreaJS}
${updateTranslationJS("srcTextArea")}
"""
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
            String url = "https://${DOMAIN}/translator"
            browser = pane.getBrowser()
            browser.loadURL(url)
            Platform.runLater(new Runnable() {
                @Override
                void run() {
                    ReadOnlyObjectProperty<Worker.State> stateProperty = browser.getWebEngine().getLoadWorker().stateProperty()
                    stateProperty.addListener(new ChangeListener<Worker.State>() {
                        @Override
                        void changed(ObservableValue<? extends Worker.State> observable, Worker.State oldValue, Worker.State newValue) {
                            updateLanguages(sourceCode, targetCode)
                            updateSourceText(Core.getEditor().getCurrentEntry().srcText)
                            stateProperty.removeListener(this)
                        }
                    })
                }
            })
        }

        if (project_change_type == IProjectEventListener.PROJECT_CHANGE_TYPE.CLOSE) {
            updateLanguages(null, null)
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

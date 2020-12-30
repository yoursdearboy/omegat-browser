import javafx.application.Platform
import org.omegat.core.Core
import org.omegat.core.CoreEvents
import org.omegat.core.events.IEditorEventListener
import org.omegat.gui.editor.IPopupMenuConstructor
import org.omegat.gui.editor.SegmentBuilder
import org.omegat.gui.main.MainWindow

import javax.swing.*
import javax.swing.text.JTextComponent
import java.awt.event.ActionEvent
import java.awt.event.KeyEvent

def FILENAME = "sangyo_honyaku.groovy"
def KEY = "SANGYO_HONYAKU"
def TITLE = "Sangyo Honyaku"
def DOMAIN = "sangyo-honyaku.jp"
def URL = "sangyo-honyaku.jp/dictionaries"

def pane = BrowserPane.get(KEY, TITLE, DOMAIN)
pane.getBrowser().loadURL(URL)

/* Record word at caret */
String caretWord = null
def editorEventListener = new IEditorEventListener() {
    @Override
    void onNewWord(String s) {
        caretWord = s
    }
}
CoreEvents.registerEditorEventListener(editorEventListener)

/* Main action that submits the word */
Action action = new AbstractAction() {
    @Override
    void actionPerformed(ActionEvent e) {
        String q = ScriptHelpers.prepareText(Core.getEditor().getSelectedText(), caretWord)
        if (q == null) return
        Platform.runLater(new Runnable() {
            @Override
            void run() {
                q = ScriptHelpers.escapeJavaStyleString(q)
                String jsCode = """
                    document.getElementById(\"DictionarySearchText\").value = \"${q}\";
                    before_search_dictionary();
                """
                pane.getBrowser().getWebEngine().executeScript(jsCode)
            }
        })
    }
}

// FIXME: Remove menu items
/* Popup menu item */
Core.getEditor().registerPopupMenuConstructors(1000, new IPopupMenuConstructor() {
    @Override
    void addItems(JPopupMenu menu, JTextComponent comp, int mousepos, boolean isInActiveEntry,
                  boolean isInActiveTranslation, final SegmentBuilder sb) {
        if (isInActiveEntry) {
            menu.addSeparator()
            JMenuItem menuItem = menu.add("Lookup in Sangyo Honyaku")
            menuItem.addActionListener(action)
        }
    }
})
MainWindow mainWindow = (MainWindow) Core.getMainWindow()
int COMMAND_MASK = System.getProperty("os.name").contains("OS X") ? ActionEvent.META_MASK : ActionEvent.CTRL_MASK
KeyStroke keystroke = KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.ALT_MASK + COMMAND_MASK)
def actionMapKey = "lookupInSangyoHonyaku"
mainWindow.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keystroke, actionMapKey)
mainWindow.getRootPane().getActionMap().put(actionMapKey, action)

/* Remove all this when script is disabled */
def scriptsEventListener = [
        onAdd    : {},
        onEnable : {},
        onRemove : {},
        onDisable: { File file ->
            if (file.getName() == FILENAME) {
                pane.close()
                CoreEvents.unregisterEditorEventListener(editorEventListener)
                mainWindow.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).remove(keystroke)
                mainWindow.getRootPane().getActionMap().remove(actionMapKey)
                scriptsRunner.unregisterEventListener(scriptsEventListener)
            }
        }].asType(ScriptsEventListener)

scriptsRunner.registerEventListener(scriptsEventListener)

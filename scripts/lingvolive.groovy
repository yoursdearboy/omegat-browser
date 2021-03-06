import org.omegat.core.Core
import org.omegat.core.CoreEvents
import org.omegat.core.data.ProjectProperties
import org.omegat.core.events.IEditorEventListener
import org.omegat.gui.editor.IPopupMenuConstructor
import org.omegat.gui.editor.SegmentBuilder
import org.omegat.gui.main.MainWindow

import javax.swing.*
import javax.swing.text.JTextComponent
import java.awt.event.ActionEvent
import java.awt.event.KeyEvent

def FILENAME = "lingvolive.groovy"
def KEY = "ABBYYLINGVOLIVE"
def TITLE = "ABBYY Lingvo Live"
def DOMAIN = "lingvolive.com"

def pane = BrowserPane.get(KEY, TITLE, DOMAIN)
pane.getBrowser().loadURL(DOMAIN)
ScriptHelpers.resetFonts(pane.getBrowser())

/* Record word at caret */
String caretWord = null
def editorEventListener = new IEditorEventListener() {
    void onNewWord(String s) {
        caretWord = s
    }
}
CoreEvents.registerEditorEventListener(editorEventListener)

/* Main action that opens Lingvo Live */
Action action = new AbstractAction() {
    @Override
    void actionPerformed(ActionEvent e) {
        q = ScriptHelpers.prepareText(Core.getEditor().getSelectedText(), caretWord)
        if (q == null) return
        ProjectProperties pp = Core.getProject().getProjectProperties()
        String sourceLangCode = pp.getSourceLanguage().getLanguageCode().toLowerCase()
        String targetLangCode = pp.getTargetLanguage().getLanguageCode().toLowerCase()
        url = "http://${DOMAIN}/en-us/translate"
        url += "/${sourceLangCode}-${targetLangCode}"
        url += "/${ScriptHelpers.encodeText(q)}"
        pane.getBrowser().loadURL(url)
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
            JMenuItem menuItem = menu.add("Lookup in ABBYY Lingvo Live")
            menuItem.addActionListener(action)
        }
    }
})
/* Bind hotkey: Ctrl + ALT + L */
MainWindow mainWindow = (MainWindow) Core.getMainWindow()
int COMMAND_MASK = System.getProperty("os.name").contains("OS X") ? ActionEvent.META_MASK : ActionEvent.CTRL_MASK
KeyStroke keystroke = KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.ALT_MASK + COMMAND_MASK)
def actionMapKey = "lookupInAbbyyLingvoLive"
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

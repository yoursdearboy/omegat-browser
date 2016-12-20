import com.sun.glass.events.KeyEvent
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

def KEY = "ABBYYLINGVOLIVE"
def TITLE = "ABBYY Lingvo Live"
def DOMAIN = "lingvolive.com"
def PATH = "/en-us/translate"

def pane = BrowserPlugin.getPane(KEY, TITLE, DOMAIN)

String caretWord = null
CoreEvents.registerEditorEventListener(new IEditorEventListener() {
    @Override
    void onNewWord(String s) {
        caretWord = s
    }
})
Action action = new AbstractAction() {
    @Override
    void actionPerformed(ActionEvent e) {
        String s = Core.getEditor().getSelectedText()
        if (s == null || s.isEmpty()) {
            s = caretWord
        }
        String blackchars = "( |<|>)"
        String stripRegex = "^${blackchars}+|${blackchars}+\$"
        s = s.trim()
        s = s.replaceAll(stripRegex,"")
        if (s.isEmpty()) return
        ProjectProperties pp = Core.getProject().getProjectProperties()
        String sourceLangCode = pp.getSourceLanguage().getLanguageCode().toLowerCase()
        String targetLangCode = pp.getTargetLanguage().getLanguageCode().toLowerCase()
        url = "http://${DOMAIN}${PATH}"
        url += "/${sourceLangCode}-${targetLangCode}"
        url += "/${URLEncoder.encode(s, "UTF-8").replace("+", "%20")}"
        pane.getBrowser().loadURL(url)
    }
}

Core.getEditor().registerPopupMenuConstructors(1000, new IPopupMenuConstructor() {
    @Override
    public void addItems(JPopupMenu menu, JTextComponent comp, int mousepos, boolean isInActiveEntry,
                         boolean isInActiveTranslation, final SegmentBuilder sb) {
        if (isInActiveEntry) {
            menu.addSeparator()
            JMenuItem menuItem = menu.add("Lookup in ABBYY Lingvo Live")
            menuItem.addActionListener(action)
        }
    }
})
MainWindow mainWindow = (MainWindow) Core.getMainWindow()
int COMMAND_MASK = System.getProperty("os.name").contains("OS X") ? ActionEvent.META_MASK : ActionEvent.CTRL_MASK
KeyStroke keystroke = KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.ALT_MASK + COMMAND_MASK)
mainWindow.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keystroke, "lookupInAbbyyLingvoLive")
mainWindow.getRootPane().getActionMap().put("lookupInAbbyyLingvoLive", action)

// Better to install Roboto fonts
//String addCssJsCode = """
//var css = '* { font-family: "Arial"; }',
//    head = document.head || document.getElementsByTagName('head')[0],
//    style = document.createElement('style');
//style.type = 'text/css';
//if (style.styleSheet){
//  style.styleSheet.cssText = css;
//} else {
//  style.appendChild(document.createTextNode(css));
//}
//
//head.appendChild(style);
//"""
//Platform.runLater(new Runnable() {
//    @Override
//    void run() {
//        pane.getBrowser().getWebEngine().getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {
//            @Override
//            void changed(ObservableValue<? extends Worker.State> observable, Worker.State oldValue, Worker.State newValue) {
//                if (newValue != Worker.State.SUCCEEDED) return
//                pane.getBrowser().getWebEngine().executeScript(addCssJsCode)
//            }
//        });
//    }
//})

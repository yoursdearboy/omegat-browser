package com.yoursdearboy.omegat.plugins.browser;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
class ScriptsPrefs {
    private final File file;
    private final File scriptsDirectory;
    private List<File> enabledScripts;

    ScriptsPrefs(File file, File scriptsDirectory, ScriptsRunner scriptsRunner) {
        this.file = file;
        this.scriptsDirectory = scriptsDirectory;
        scriptsRunner.registerEventListener(new Updater());
    }

    void load() {
        java.util.List<File> enabledScripts = new ArrayList<File>();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                File script = new File(scriptsDirectory, line);
                enabledScripts.add(script);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.enabledScripts = enabledScripts;
    }

    void save() {
        StringBuilder out = new StringBuilder();
        for (File script : enabledScripts) {
            out.append(script.getName());
            if (enabledScripts.indexOf(script) < enabledScripts.size()-1) out.append("\n");
        }
        BufferedWriter br = null;
        try {
            br = new BufferedWriter(new FileWriter(file));
            br.write(out.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    List<File> getEnabledScripts() {
        return enabledScripts;
    }

    class Updater implements ScriptsEventListener {
        @Override
        public void onAdd(File file) {
        }

        @Override
        public void onEnable(File file) {
            if (!enabledScripts.contains(file)) {
                enabledScripts.add(file);
            }
        }

        @Override
        public void onDisable(File file) {
            enabledScripts.remove(file);
        }

        @Override
        public void onRemove(File file) {
        }
    }
}

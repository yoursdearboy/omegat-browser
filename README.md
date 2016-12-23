# OmegaT Browser plugin

A plugin for [OmegaT](http://omegat.org) for fast access to websites. Bindings can be scripted in Groovy with support for hotkeys, menus, other OmegaT functions.

![Demo](https://github.com/yoursdearboy/omegat-browser/raw/master/demo.gif)

## Scripts

Download using Right click -> Save as. Name must end with `.groovy`

* [Google Translate](https://raw.githubusercontent.com/yoursdearboy/omegat-browser/v1.0/scripts/google_translate.groovy) - translates current entry. Choose language manually.
* [Multitran](https://raw.githubusercontent.com/yoursdearboy/omegat-browser/v1.0/scripts/multitran.groovy) - lookup selected or focused word in [multitran.ru](multitran.ru) dictionary. Shortcut: `CTRL + ALT + M`.
* [Lingvo Live](https://raw.githubusercontent.com/yoursdearboy/omegat-browser/v1.0/scripts/lingvolive.groovy) - lookup selected or focused word in [lingvolive.ru](lingvolive.ru) dictionary. Shortcut: `CTRL + ALT + L`. If **all** symbols are broken - install [Roboto fonts](https://storage.googleapis.com/material-design/publish/material_v_10/assets/0B0J8hsRkk91LRjU4U1NSeXdjd1U/RobotoTTF.zip).

*On Mac use `CMD` instead of `Ctrl` for shortcuts.*

## Installation and usage

Download and place [`omegat-browser-1.0.jar`](https://github.com/yoursdearboy/omegat-browser/releases/download/v1.0/omegat-browser-1.0.jar) in plugins directory:

* Windows: `C:\Program Files\OmegaT\plugins`
* OS X: Go to `Applications` -> select `Omegat.app` -> right click -> select `Show Package Contents` -> go to `Conents/Java/plugins`
* Linux: `~/.omegat/plugins`

Start OmegaT as usually. In menu choose `Tools -> Open browser scripts`. Place there downloaded scripts, they will be enabled automagically.

Installed scripts can be enabled/disabled in `Tools -> Browser scripts`. In case of artifacts (multiple menu items) - restart OmegaT.

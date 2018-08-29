# OmegaT Browser plugin

A plugin for [OmegaT](http://omegat.org) for fast access to websites. Bindings can be scripted in Groovy with support for hotkeys, menus, other OmegaT functions.

![Demo](https://github.com/yoursdearboy/omegat-browser/raw/master/demo.gif)

## Scripts

Download using Right click -> Save as. Name must end with `.groovy`

* [Google Translate](https://raw.githubusercontent.com/yoursdearboy/omegat-browser/v1.3/scripts/google_translate.groovy) - translates current entry. Choose language manually.
* [Multitran](https://raw.githubusercontent.com/yoursdearboy/omegat-browser/v1.3/scripts/multitran.groovy) - lookup selected or focused word in [multitran.ru](multitran.ru) dictionary. Shortcut: `CTRL + ALT + M`.
* [Lingvo Live](https://raw.githubusercontent.com/yoursdearboy/omegat-browser/v1.3/scripts/lingvolive.groovy) - lookup selected or focused word in [lingvolive.ru](lingvolive.ru) dictionary. Shortcut: `CTRL + ALT + L`. If **all** symbols are broken - try to install [Roboto fonts](https://storage.googleapis.com/material-design/publish/material_v_10/assets/0B0J8hsRkk91LRjU4U1NSeXdjd1U/RobotoTTF.zip).
* [Deepl](https://raw.githubusercontent.com/yoursdearboy/omegat-browser/v1.3/scripts/deepl.groovy) - translates current entry using [Deepl](https://deepl.com). Choose language manually. Contributed by [@rosros2000](https://github.com/rosros2000).
* [Google Search](https://raw.githubusercontent.com/yoursdearboy/omegat-browser/v1.3/scripts/google.groovy) - search current entry using [Google](https://google.com).
* [Sangyo Honyaku](https://raw.githubusercontent.com/yoursdearboy/omegat-browser/v1.3/scripts/sangyo_honyaku.groovy) - Japanese dictionary [Sangyo Honyaku](https://sangyo-honyaku.jp/dictionaries).

*On Mac use `CMD` instead of `Ctrl` for shortcuts.*

## Installation and usage

Download and place [`omegat-browser-1.3.jar`](https://github.com/yoursdearboy/omegat-browser/releases/download/v1.3/omegat-browser-1.3.jar) in plugins directory:

* Windows: `C:\Program Files\OmegaT\plugins`
* OS X: Go to `Applications` -> select `Omegat.app` -> right click -> select `Show Package Contents` -> go to `Conents/Java/plugins`
* Linux: `~/.omegat/plugins`

Start OmegaT as usually. In menu choose `Tools -> Open browser scripts`. Place there downloaded scripts, they will be enabled automagically.

Installed scripts can be enabled/disabled in `Tools -> Browser scripts`. In case of artifacts (multiple menu items) - restart OmegaT.

Note: Java 8 or later required with JavaFX support. In most cases you already have it.
On Linux check that package `openjfx` installed or install Java from Oracle. More info can be found [in this thread](https://github.com/yoursdearboy/omegat-browser/issues/3).

# Changelog

Version 1.3:

* Fixed fonts issues on Deepl and LingvoLive, redownload those scripts
* Fixed Deepl connectivity problems

<br/> 

<a href="https://www.patreon.com/bePatron?u=9885919" target="_blank"><img src="https://c5.patreon.com/external/logo/become_a_patron_button@2x.png" height="30px"/></a>

If my software helps you, consider making a donation. Thank you.

# Cloud emoticon

A cloud solution to your favorite emoticons

## Now available on Play Store
<a href="https://play.google.com/store/apps/details?id=org.ktachibana.cloudemoji">
  <img alt="Get it on Google Play"
       src="https://developer.android.com/images/brand/en_generic_rgb_wo_60.png" />
</a>

## This is an unique Android emoticon app that
* reads in an emoticon repository on the cloud (e.g a Dropbox public shared XML file)
* shows on categorized lists
* is capable of easy one-click copy && paste

## So that you don't have to
* hack built-in dictionary files for IME's and copy paste files manually when you want to change
* use other cloud services where their apps are not one-click copy-paste capable
* use other emoticon apps which are not customizable by you

## Configurable Features
* Close after copying
* Show in notification panel/status bar
* Show notification after boot-up
* Navigation drawer / Split view (optimized or manual)
* Override system font to display emoticons correctly
 * Thanks to [+Jobin Wen](https://plus.google.com/u/0/+HaoyuWen) who extracts font from stock LG rom
 * LG please contact me if you have any copyright issues

## Utilizes
* [chrisbanes/ActionBar-PullToRefresh](https://github.com/chrisbanes/ActionBar-PullToRefresh)
* [Apache Commons IO] (http://commons.apache.org/proper/commons-io/)
* [Apache Commons Lang] (http://commons.apache.org/proper/commons-lang/)

## Known issues
* After boot-up an empty activity (basically the BootUpDummyActivity) is retained in background tasks list
* Only show in panel option not working for Android 2.x
* Action bar title not remembered

## How to run
* Import into IntelliJ IDEA 13, set up JDK and Android SDK, set up build configuration, and you are ready to go

## License
* [Apache Version 2.0](http://www.apache.org/licenses/LICENSE-2.0)

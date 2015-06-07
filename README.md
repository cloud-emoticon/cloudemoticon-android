# Cloud emoticon

A cloud solution to your favorite emoticons

<a href="https://play.google.com/store/apps/details?id=org.ktachibana.cloudemoji">
  <img alt="Get it on Google Play"
       src="https://developer.android.com/images/brand/en_generic_rgb_wo_60.png" />
</a>

## This app
* downloads emoticon repositories from remote servers
* shows them on categorized lists
* is one-click copy capable

## You only need to
* host your emoticons (Json or XML) on a remote server, e.g.
    * Dropbox public shared folder
    * your own server
* add others' repositories from our repository store

## So that you don't have to
* hack IME's and make changes manually when you need to
* use other cloud apps that is not one-click copy capable
* use other un-customizable emoticon apps

## Features
* Multi-repository management
* Bookmarks
* History
* Search
* Repository store
* Backup and restore bookmarks
* Swipe-up navigation bar icon to replace home
* Add your bookmarks to your user dictionary (some IME's can use it!)
* More...

## Screenshot
![screenshot](https://raw.githubusercontent.com/KTachibanaM/cloudemoji/master/screenshots/main.png)

## Development
We are using [Parse] (https://www.parse.com/) as our user sync backend service

so the PrivateConstants.java is git-ignored since it contains private app keys

You can either

1. Create PrivateConstants.java manually and fill in random stuff if you are not developing any user sync related features
2. Register a Parse account and create your own Parse backend (it has free tier service) if you are developing user sync related features

We might be rolling our own backend implementation (also open-sourced!) and migrate user data from Parse to there.

## Libraries via Gradle
* [appcompat-v4](https://developer.android.com/tools/support-library/features.html#v4)
* [appcompat-v7](https://developer.android.com/tools/support-library/features.html#v7)
* [android-support-v4-preferencefragment] (https://github.com/kolavar/android-support-v4-preferencefragment)
* [android-async-http](https://github.com/loopj/android-async-http)
* [EventBus](https://github.com/greenrobot/EventBus)
* [ButterKnife](https://github.com/JakeWharton/butterknife)
* [Gson](https://code.google.com/p/google-gson/)
* [Apache Commons IO](http://commons.apache.org/proper/commons-io/)
* [PagerSlidingTabStrip] (https://github.com/jpardogo/PagerSlidingTabStrip)
* [FloatingActionButton] (https://github.com/makovkastar/FloatingActionButton)
* [SnackBar] (https://github.com/MrEngineer13/SnackBar)
* [material-dialogs] (https://github.com/afollestad/material-dialogs)
* [MaterialDrawer] (https://github.com/mikepenz/MaterialDrawer)
* [picasso] (http://github.com/square/picasso)

## Libraries via Jar/Aar balls
* [ARCA](https://github.com/ACRA/acra)
* [sugar](https://github.com/satyan/sugar)
* [PinnedHeaderListView](https://github.com/JimiSmith/PinnedHeaderListView)

## Libraries via direct code usage
* [drag-sort-listview](https://github.com/bauerca/drag-sort-listview)
* [emojicon](https://github.com/rockerhieu/emojicon)

## License
* [Apache Version 2.0](http://www.apache.org/licenses/LICENSE-2.0)
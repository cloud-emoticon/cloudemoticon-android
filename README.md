# Cloud emoticon

A cloud solution for your favorite emoticons

<a href="https://play.google.com/store/apps/details?id=org.ktachibana.cloudemoji&utm_source=global_co&utm_medium=prtnr&utm_content=Mar2515&utm_campaign=PartBadge&pcampaignid=MKT-AC-global-none-all-co-pr-py-PartBadges-Oct1515-1"><img alt="Get it on Google Play" src="https://play.google.com/intl/en_us/badges/images/apps/en-play-badge.png" /></a>

## Features

### This app
* downloads emoticon repositories from remote servers
* shows them on categorized lists
* is one-click copy capable

### You only need to add your emoticons (Json or XML) from
* your own server
* others' repositories from our repository store

### So that you don't have to
* hack IME's and make changes manually when you need to
* use other cloud apps that is not one-click copy capable
* use other un-customizable emoticon apps

### Features
* Multi-repository management
* Bookmarks
* History
* Search
* Repository store
* Backup and restore bookmarks
* Swipe-up navigation bar icon to replace home
* Add your bookmarks to your user dictionary (some IME's can use it!)
* More...

### Screenshot
![screenshot](https://raw.githubusercontent.com/KTachibanaM/cloudemoji/master/screenshots/phone/repo.png)

## Development

On Windows, replace `./gradlew` with `gradlew`

### Clean
```bash
./gradlew app:clean
```

### Build debug variant
```bash
./gradlew app:assembleDebug
# Then find apk under app/build/outputs/apk/app-debug.apk
```

### Build and install debug variant
```bash
./gradlew app:installDebug
```

### Build release variant (requires `keystores.properties` under root project directory)
```bash
./gradlew app:assembleRelease
# Then find apk under app/build/outputs/apk/app-release.apk
```
# Cloud emoticon

A cloud solution to your favorite emoticons on Android

[<img src="https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png" alt="Get it on Google Play" height="60">](https://play.google.com/store/apps/details?id=org.ktachibana.cloudemoji)

[Unsigned GitHub release apk](https://github.com/cloud-emoticon/cloudemoji/releases)

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
<img src="https://raw.githubusercontent.com/KTachibanaM/cloudemoji/master/screenshots/phone/bookmarks.png"  width="500">
<img src="https://raw.githubusercontent.com/KTachibanaM/cloudemoji/master/screenshots/phone/history.png"  width="500">
<img src="https://raw.githubusercontent.com/KTachibanaM/cloudemoji/master/screenshots/phone/repo.png"  width="500">
<img src="https://raw.githubusercontent.com/KTachibanaM/cloudemoji/master/screenshots/phone/repo_manager.png"  width="500">
<img src="https://raw.githubusercontent.com/KTachibanaM/cloudemoji/master/screenshots/phone/repo_store.png"  width="500">
<img src="https://raw.githubusercontent.com/KTachibanaM/cloudemoji/master/screenshots/phone/settings.png"  width="500">

## Development

### Gradle commands
On Windows, replace `./gradlew` with `gradlew`

#### Clean
```bash
./gradlew app:clean
```

#### Build debug variant
```bash
./gradlew app:assembleDebug
# Then find apk under app/build/outputs/apk/debug/app-debug.apk
```

#### Build and install debug variant
```bash
./gradlew app:installDebug
```

#### Build release variant
```bash
cp keystores.example.properties keystores.properties
# fill out keystores.properties
# storeFile should be the file path to your *.jks keystore file
./gradlew app:assembleRelease
# Then find apk under app/build/outputs/apk/release/app-release.apk
```

### Make commands

```bash
make  # installs debug variant
make debug  # builds debug variant and put app-debug.apk under .
make release  # builds release variants and put app-release.apk under .
```

# Cloud emoticon

Your emoticon companion

[<img src="https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png" alt="Get it on Google Play" height="60">](https://play.google.com/store/apps/details?id=org.ktachibana.cloudemoji)

[Unsigned GitHub release apk](https://github.com/cloud-emoticon/cloudemoji/releases)

## Features

Easily discover and use cute Japanese emoticons!

Cloud emoticon is an app with a large collection of cute Japanese emoticons and let you conveniently use them in multiple ways.

On selected Android versions, the app lets you copy your favorite emoticons with
*  the app's own input method
*  any other input methods using personal dictionary (only works below Marshmallow)
*  any input fields using custom defined shortcuts (only works for Lollipop and above; required accessibility permission)

You can also manage emoticons with ease
* Bookmark your favorite emoticons
* Browse used emoticons
* Search emoticons
* Browse a large collection of user contributed emoticons

### Screenshot
<img src="https://raw.githubusercontent.com/KTachibanaM/cloudemoji/master/screenshots/phone/bookmarks.png"  width="500">
<img src="https://raw.githubusercontent.com/KTachibanaM/cloudemoji/master/screenshots/phone/history.png"  width="500">an
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

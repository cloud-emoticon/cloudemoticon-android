# Cloud emoticon

Your emoticon companion

[GitHub releases](https://github.com/cloud-emoticon/cloudemoji/releases)

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

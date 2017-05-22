# Cloud emoticon

A cloud solution for your favorite emoticons

重要！
请 **不要** 使用[小米商店的这个版本](http://app.mi.com/details?id=com.vrem.yunwenzisyj)。这个版本并不是由我本人发布，并且夹带了广告和无用的菜单选项。我不能保证这个版本不包含其他未经开源社区审核的代码（甚至是恶意代码），我也不能保证这个版本能够够维持更新，以跟进最新的特性和bug修复。唯一官方获取这个app的途径是[未签名的GitHub release apk](https://github.com/cloud-emoticon/cloudemoji/releases)和[Google Play](https://play.google.com/store/apps/details?id=org.ktachibana.cloudemoji)。

Important!
Please **DON'T** use [this version from Mi Store](http://app.mi.com/details?id=com.vrem.yunwenzisyj). I do not  publish this version, and it includes ads and useless menu options. I cannot guarantee this version contains only code from this very open-source repostitory which is under community scrutiny, neither can I ensure malicious code is not mixed in, and I cannot ensure this version can be kept up-to-date with new features and bug fixes. The only official ways to obtians this app is either [unsigned GitHub release apk](https://github.com/cloud-emoticon/cloudemoji/releases) or [Google Play](https://play.google.com/store/apps/details?id=org.ktachibana.cloudemoji).

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
# Then find apk under app/build/outputs/apk/$versionName-$versionCode-debug.apk
```

### Build and install debug variant
```bash
./gradlew app:installDebug
```

### Build release variant (requires `keystores.properties` under root project directory)
```bash
./gradlew app:assembleRelease
# Then find apk under app/build/outputs/apk/$versionName-$versionCode-release.apk
```
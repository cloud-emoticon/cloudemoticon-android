default: install-debug

clean:
		./gradlew app:clean

debug: clean
		rm app-debug.apk
		./gradlew app:assembleDebug
		mv ./app/build/outputs/apk/debug/app-debug.apk .

install-debug: clean
		./gradlew app:installDebug

release: clean
		rm app-release.apk
		./gradlew app:assembleRelease
		mv ./app/build/outputs/apk/release/app-release.apk .

.PHONY: clean debug install-debug release

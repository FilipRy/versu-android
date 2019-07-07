# Versu Android
This is an Android client application for [versu backend](https://github.com/FilipRy/versu/tree/master/backend). Via the application a user can create new votings, list votings of a user's followers and vote on those. The votings can be tagged in a specific geographic location. The location is enabled by [Google Geolocation API](https://developers.google.com/maps/documentation/geolocation/intro). Push notifications have been utilized in order to enable the users to be up to date with their followers all of the time. Therefore, the [Firebase Cloud Messaging](https://firebase.google.com/docs/cloud-messaging) is used. The [Firebase Crashlytics](https://firebase.google.com/docs/crashlytics) is employed to report crashes in the application.

## Configuration
In order to make use of the Firebase Crashlytics and Cloud Messaging in the application, `google-services.json` has to be generated according to a step by step tutorial, described [here](https://firebase.google.com/docs/android/setup). A [Google Geolocation API key](https://developers.google.com/maps/documentation/geolocation/get-api-key) has to be obtained and added to `AndroidManifest.xml`. Furthermore, you have to provide a set of properties in the `main/res/raw/config.properties` file. `backend.url` has to be point to the ip address of the versu backend application. `client.id` and `client.secret` is the OAuth2 client id and secret managed by versu backend. 

## Built With

* [gradle](https://gradle.org/) - Build & Dependecny Management
* [Android Java SDK](https://developer.android.com/studio) - SDK

## License
Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0).
# Google Play Publisher

This application interacts with the Google Developer Publishing API and uses the transactional "edits" functionality to upload APKs or App bundles to the Google Play Store.
If you are missing a feature, something is not working as expected, or you are in need of clarification, feel free to [create an issue](https://github.com/CollinAlpert/google-play-publisher/issues/new) or a PR. Just make sure to check out the [Roadmap](#roadmap) beforehand.

## Requirements
There are a few things you need for this:
#### Java 14 
This project uses Java 14. If you require it to run with a lower version of Java, simply clone the Repo, change the version in the `pom.xml` file and run it yourself.

#### A service account
A service account is an account which can talk to the Publishing API. You can create one in the [Google Console](https://console.cloud.google.com/apis/credentials). If you already have one (there is usually at least one already created), even better. Make sure you create a key and download the JSON file. You'll need this file when using the application. Note that this application **does not** support the use of a P12 key file. 

#### An APK or App bundle
Lastly, you'll need the file which you want to upload. When using the application, you will be prompted to select it. I recommend keeping the JSON key file as well as this file in an easy to find place.

#### Patients
Since this application executes multiple API calls and uploads a multi-megabyte file, it takes a while. Wait at least 60 seconds after clicking the Upload button until you go on your `htop` killing spree.
I will try to improve the interactivity going forward.

# Installation
You can either choose to clone this repo and run it yourself using Maven, or download the most recent [JAR](https://github.com/CollinAlpert/google-play-publisher/releases/latest) and run that. Remember, you need at least Java 14 for that.

# Usage
When launching the app and after looking past the fact that it's ugly as shit, you will be presented with a few configuration options.

#### App name
The name of the app. Simple as that.

#### Package name
The full package name of the app, as chosen by you. It is displayed under the app name in the Google Play Console app overview.

#### Track
The track to publish the app on. If you need to be able to publish to more tracks, let me know. As a disclaimer, I do not recommend using this app for releasing to Production. 

#### Release status
I found in some situations I needed to tinker with the status of the release, based on the status of my app. If you're not like me and know what you're doing, you probably know what this is for. In general, you should probably only need the "Completed" status. More information [here](https://developers.google.com/android-publisher/api-ref/edits/tracks).

#### The JSON key file
This is the key file for the service account which is needed to talk to the Google Publishing API. You need to choose it before uploading an APK or App bundle, or else it doesn't work.

# Roadmap
Things which I will try to implement down the road:
- Improved error handling/display. The current error display, is, let's just say, suboptimal.
- Support for multi-language release notes. Currently, only German is supported.
- More interactivity while waiting for the publishing process to complete.
- More stability with uploading App bundles. Sometimes the application just times out, and I haven't quite figured out why.
- A CLI version. Nobody really wants to look at this ugly Swing app.
- A .NET Core version. Having a .NET Core version should yield a great boost in performance. I only chose Java because Google offers the API in it. Unfortunately, this also means I'll need to do some reverse-engineering and a bit of time to get this one done. 
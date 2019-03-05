# Open Live iOS for Swift

*Read this in other languages: [中文](README.zh.md)*

This tutorial describes how to add live video chat to your iOS applications using Swift and the Agora Video SDK.

With this sample app, you can:

- [Join](#create-the-loadagorakit-method)/[leave](#create-the-leavechannel-method) a channel
- [Set user role as a broadcaster or audience member](#create-the-mainviewcontroller-extensions)
- [Mute/unmute audio](#define-private-variables)
- [Switch camera views](#create-the-doswitchcamerapressed-ibaction-method)
- [Set up the resolution, the frame rate, and the bit rate display](#create-the-loadagorakit-method)

## Prerequisites
- Xcode 10.0+
- Physical iOS device (iPhone or iPad)
	
	**Note:** Use a physical device to run the sample. Some simulators lack the functionality or the performance needed to run the sample.

## Quick Start

This section shows you how to prepare, build, and run the sample application.

- [Create an Account and Obtain an App ID](#create-an-account-and-obtain-an-app-id)
- [Update and Run the Sample Application](#update-and-run-the-sample-application) 


### Create an Account and Obtain an App ID
To build and run the sample application, you must obtain an app ID: 

1. Create a developer account at [agora.io](https://dashboard.agora.io/signin/). Once you finish the sign-up process, you are redirected to the dashboard.
2. Navigate in the dashboard tree on the left to **Projects** > **Project List**.
3. Copy the app ID that you obtained from the dashboard into a text file. You will use this when you launch the app.


### Update and Run the Sample Application 

1. Open `OpenLive.xcodeproj` and edit the `KeyCenter.swift` file. In the `agoraKit` declaration, update `<#Your App Id#>` with your app ID.

	``` Swift
    static let AppId: String = <#Your App Id#>
	```

2. Download the [Agora Video SDK](https://www.agora.io/en/download/). Unzip the downloaded SDK package and copy the `libs/AograRtcEngineKit.framework` file from the SDK folder into the sample application's `OpenLive` folder.
			
3. Connect your iPhone or iPad device and run the project. Ensure a valid provisioning profile is applied, or your project will not run.

## Resources
- A detailed code walkthrough for this sample is available in [Steps to Create this Sample](./guide.md).
- Find full API documentation in the [Document Center](https://docs.agora.io/en/)
- [File bugs about this sample](https://github.com/AgoraIO/Basic-Video-Broadcasting/issues)

## Learn More
- [1 to 1 Video Tutorial for iOS/Swift](https://github.com/AgoraIO/Basic-Video-Call/tree/master/One-to-One-Video/Agora-iOS-Tutorial-Swift-1to1)

## License
This software is licensed under the MIT License (MIT). [View the license](LICENSE.md).

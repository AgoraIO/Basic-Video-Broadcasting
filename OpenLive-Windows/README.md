# Open Live Windows

*其他语言版本： [简体中文](README.zh.md)*

The Open Live Windows Sample App is an open-source demo that will help you get live video chat integrated directly into your windows applications using the Agora Video SDK.

With this sample app, you can:

- Join / leave channel
- Set role as broadcaster or audience
- Mute / unmute audio
- Change camera
- Setup resolution, frame rate and bit rate

This demo is written in **C++**

A tutorial demo can be found here: [Agora-Windows-Tutorial-1to1](https://github.com/AgoraIO/Basic-Video-Call/tree/master/One-to-One-Video/Agora-Windows-Tutorial-1to1)

## Developer Environment Requirements
* VS 2013(or higher)
* QT5.6(or higher)
* Windows 7(or higher)

## Running the App
First, create a developer account at [Agora.io](https://dashboard.agora.io/signin/), and obtain an App ID. define the APP_ID with your App ID.

* #define APP_ID _T("Your App ID")

Next, download the **Agora Video SDK** from [Agora.io SDK](https://www.agora.io/en/download/). Unzip the downloaded SDK package and copy the **libs** to the project folder(the old one may be over written).

Finally, Open OpenLive.pro with your Qt5.6(or higher) and build current project and run.

* If you don't fill appid in the code file,you can place a file named AgoraConfigOpenLive.ini in the executable path.The file content:
  
    [BaseInfo]
    AppId=xxxx

**Note：** If you fill appid in the code file, appid in ini file will be omitted.

> To ensure communication security, Agora uses tokens (dynamic keys) to authenticate users joining a channel.
>
> Temporary tokens are for demonstration and testing purposes only and remain valid for 24 hours. In a production environment, you need to deploy your own server for generating tokens. See [Generate a Token](https://docs.agora.io/en/Interactive Broadcast/token_server)for details.

## Contact Us

- For potential issues, take a look at our [FAQ](https://docs.agora.io/en/faq) first
- Dive into [Agora SDK Samples](https://github.com/AgoraIO) to see more tutorials
- Take a look at [Agora Use Case](https://github.com/AgoraIO-usecase) for more complicated real use case
- Repositories managed by developer communities can be found at [Agora Community](https://github.com/AgoraIO-Community)
- You can find full API documentation at [Document Center](https://docs.agora.io/en/)
- If you encounter problems during integration, you can ask question in [Stack Overflow](https://stackoverflow.com/questions/tagged/agora.io)
- You can file bugs about this sample at [issue](https://github.com/AgoraIO/Basic-Video-Broadcasting/issues)

## License

The MIT License (MIT).

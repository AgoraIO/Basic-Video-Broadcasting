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
* VS 2013(or higher), default is vs2017
* Windows 7(or higher)

## Running the App
First, create a developer account at [Agora.io](https://dashboard.agora.io/signin/), and obtain an App ID. define the APP_ID with your App ID.

 * #define APP_ID _T("Your App ID")

If you don't want to modify the code part, you can create an AppId.ini file under Debug/Release. Modify the appId value to the App ID you just applied.

  #[AppID]

  #AppID=xxxxxxxxxxxxxxxxxxx

> To ensure communication security, Agora uses tokens (dynamic keys) to authenticate users joining a channel.
>
> Temporary tokens are for demonstration and testing purposes only and remain valid for 24 hours. In a production environment, you need to deploy your own server for generating tokens. See [Generate a Token](https://docs.agora.io/en/Interactive Broadcast/token_server)for details.

Next, download the **Agora Video SDK** from [Agora.io SDK](https://www.agora.io/en/download/). Unzip the downloaded SDK package and copy the files under **libs\include**, to the project folder **OpenLive-Windows-MFC\sdk\include** （create if not exist）. Then copy the **dll** file and **lib** file under **libs\x86**, to **sdk\dll** and **sdk\lib** respectively.

Finally, Open OpenLive.sln with your Vs 2013(or higher) and build all solution and run.

Note：
  1. After the program is compiled, if the program "xxx\xxx\xxx\Debug\Language\English.dll" cannot be started when running the program, 
      please select the OpenLive project in the Solution Explorer and right click. In the pop-up menu bar, select "Set as startup project" to solve. Then run the program again.
  2. You may encounter crash when running this demo under debug mode. Please run this demo under release mode.

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

# Open Live iOS for Swift

*[English](README.md) | 中文*

这个开源示例项目演示了如何快速集成Agora视频SDK，实现多人视频连麦直播。

在这个示例项目中包含了以下功能：

- 加入通话和离开通话；
- 主播和观众模式切换；
- 静音和解除静音；
- 切换前置摄像头和后置摄像头；
- 选择分辨率、码率和帧率；

本开源项目使用 **Swift** 语言，你可以在这里找到使用 **Objective-C** 的项目：[OpenLive-iOS-Objective-C](https://github.com/AgoraIO/Basic-Video-Broadcasting/tree/master/OpenLive-iOS-Objective-C)

你也可以在这里查看入门版的示例项目：[Agora-iOS-Tutorial-Swift-1to1](https://github.com/AgoraIO/Basic-Video-Call/tree/master/One-to-One-Video/Agora-iOS-Tutorial-Swift-1to1)

Agora视频SDK支持 iOS / Android / Windows / macOS 等多个平台，你可以查看对应各平台的示例项目：

- [OpenLive-Android](https://github.com/AgoraIO/Basic-Video-Broadcasting/tree/master/OpenLive-Android)
- [OpenLive-Windows](https://github.com/AgoraIO/Basic-Video-Broadcasting/tree/master/OpenLive-Windows)
- [OpenLive-macOS](https://github.com/AgoraIO/Basic-Video-Broadcasting/tree/master/OpenLive-macOS)

## 环境准备

- XCode 10.0 +
- iOS 真机设备
- 不支持模拟器

## 运行示例程序

这个段落主要讲解了如何编译和运行实例程序。

### 创建Agora账号并获取AppId

在编译和启动实例程序前，您需要首先获取一个可用的App ID:
1. 在[agora.io](https://dashboard.agora.io/signin/)创建一个开发者账号
2. 前往后台页面，点击左部导航栏的 **项目 > 项目列表** 菜单
3. 复制后台的 **App ID** 并备注，稍后启动应用时会用到它
4. 在项目页面生成临时 **Access Token** (24小时内有效)并备注，注意生成的Token只能适用于对应的频道名。

5. 将 AppID 和 Token 填写进 KeyCenter.swift

```
static let AppId: String = <#Your App Id#>
// 如果你没有打开Token功能，token可以直接给nil
static var Token: String? = <#Temp Access Token#>
```

### 集成 Agora 视频 SDK

在 [Agora.io SDK](https://www.agora.io/cn/blog/download/) 下载 **视频通话 + 直播 SDK**，解压后将其中**libs**文件夹下的 
  - AgoraRtcEngineKit.framework
  - AgoraRtcCryptoLoader.framework
  - libcrypto.a
三个文件复制到本项目的 OpenVideoCall 文件夹下。
最后使用 XCode 打开 OpenVideoCall.xcodeproj，连接 iPhone／iPad 测试设备，设置有效的开发者签名后即可运行。


## 联系我们

- 完整的 API 文档见 [文档中心](https://docs.agora.io/cn/)
- 如果在集成中遇到问题, 你可以到 [开发者社区](https://dev.agora.io/cn/) 提问
- 如果有售前咨询问题, 可以拨打 400 632 6626，或加入官方Q群 12742516 提问
- 如果需要售后技术支持, 你可以在 [Agora Dashboard](https://dashboard.agora.io) 提交工单
- 如果发现了示例代码的 bug, 欢迎提交 [issue](https://github.com/AgoraIO/Basic-Video-Call/issues)

## 代码许可

The MIT License (MIT)

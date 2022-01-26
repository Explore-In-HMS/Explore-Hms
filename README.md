#  HMS Sandbox  

## Overview

![ #Kits/Services in Sandbox](https://img.shields.io/badge/%20%23Kits%2FServices%20in%20Sandbox-83-yellow) ![% HMS Kit/Service Coverage ](https://img.shields.io/badge/%20HMS%20Kit%2FService%20Coverage%20-100%2F100-orange) ![% Functional Coverage per Kit/Service](https://img.shields.io/badge/%20Functional%20Coverage%20per%20Kit%2FService-99%2F100-yellow) ![% Kit/Service Up-to-Date](https://img.shields.io/badge/%20Kit%2FService%20Up--to--Date-98%2F100-lightgrey)


## Screenshots
<table>
<tr>
<td>
<img src="/art/1.png" width="300"/>

</td>
<td>
<img src="/art/2.png" width="300"/>

</td>
<td>
<img src="/art/3.png" width="300"/>

</td>
<td>
<img src="/art/4.png" width="300"/>

</td>
</tr>

<tr>
<td>
<img src="/art/5.png" width="300"/>

</td>
<td>
<img src="/art/6.png" width="300"/>

</td>
<td>
<img src="/art/7.png" width="300"/>

</td>
<td>
<img src="/art/8.png" width="300"/>

</td>
</tr>
</table>


## Introduction
HMS Sandbox application will host all HMS Core services and kits as example modules. It targets to include all features that HMS can provide. Because HMS Core is rapidly growing source, new modules and features fill be added. With each update, the application will be kept up to date, new features will be added, the application and modules will be constantly updated and optimized.

When users install the application from AppGallery, only 4 modules will be installed. However, because this project was created with HMS Dynamic Ability Service and Android App Bundle technology together, users will be able to download  the modules they need without having unnecessary modules. This ensures that the app download size and the size it takes up on the device remain optimal.

App Bundle On-Demand feature provides us the capability of downloading the requested modules. In this way, the application does not have non-required features.

## Focus
- Provide ease for software developers and content providers who want to examine and learn about the services and kits offered by HMS Core or anyone who is interested in HMS.
- Being a useful code sample source and reference for software developers or who wants to learn what services HMS Core includes and how to implement them into their projects.

## What You Will Need

**Hardware Requirements**
- A computer that can run Android Studio.
- An Android phone for debugging. 

**Software Requirements**
- Android SDK package
- Android Studio 3.X
- HMS Core (APK) 4.X or later

## Technical Information
* Project Software Language: JAVA
* Java Version: 1.8 or later.
* Android Studio Version: 4.0
* Android SDK version is 26 or later.
* Gradle Version: 4.1.1

## Before You Start
**You need to agconnect-services.json for run this project correctly.**

- If you don't have a Huawei Developer account check this document for create; https://developer.huawei.com/consumer/en/doc/start/10104
- Open your Android project and find Debug FingerPrint (SHA256) with follow this steps; View -> Tool Windows -> Gradle -> Tasks -> Android -> signingReport
- Login to Huawei Developer Console (https://developer.huawei.com/consumer/en/console)
- If you don't have any app check this document for create; https://developer.huawei.com/consumer/en/doc/distribution/app/agc-create_app
- Add SHA256 FingerPrint into your app with follow this steps on Huawei Console; My Apps -> Select App -> Project Settings
- Make enable necessary SDKs with follow this steps; My Apps -> Select App -> Project Settings -> Manage APIs
- For this project you have to set enable Map Kit, Site Kit, Auth Service, ML Kit
- Than go again Project Settings page and click "agconnect-services.json" button for download json file.
- Move to json file in base "app" folder that under your android project. (https://developer.huawei.com/consumer/en/doc/development/HMS-Guides/69407812#h1-1577692046342)
- Go to app level gradle file and change application id of your android project. It must be same with app id on AppGallery console you defined.


## Implemented Kits And Services  

 ### A/B Testing

A/B Testing provides a collection of refined operation tools to optimize app experience and improve key conversion and growth indicators. You can use the service to create one or more A/B tests engaging different user groups to compare your variants of app UI design, copywriting, product functions, or marketing activities for performance metrics and find the best one that meets user requirements. This helps you make correct decisions. A/B Testing is commonly used by large Internet companies, with hundreds of A/B tests conducted every week.

 ### Accelerate Kit

HUAWEI Accelerate Kit provides the multi-thread acceleration capability that efficiently improves concurrent execution of multiple threads. It is located above the kernel in the OS and opened to developers as a set of C language APIs.

Most of current Android devices run a multi-core system. To give full play to the system, programs that can execute multiple tasks concurrently are preferred. Generally, multi-thread programs at the Native layer control task execution by managing threads. Accelerate Kit provides a new multi-thread programming method by using the multi-thread library (multithread-lib). It frees you from the thread management details so that you can focus on developing apps that can fully utilize the multi-core hardware capability of the OS, promising more efficient running.

 ### Account Kit

*Version: 5.2.0.300*

Account Kit provides you with simple, secure, and quick sign-in and authorization functions. Instead of entering accounts and passwords and waiting for authentication, users can just tap the Sign In with HUAWEI ID button to quickly and securely sign in to your app with their HUAWEI IDs.

Features:

- Signing In with a HUAWEI ID.
- Silently Signing In with a HUAWEI ID.
- Signing Out from a HUAWEI ID.
- Revoking HUAWEI ID Authorization.
- Automatically Reading an SMS Verification Code.  



 ### Ads Kit

*Version: 13.4.47.302*

HUAWEI Ads Publisher Service is a monetization service that leverages Huawei's extensive data capabilities to display targeted, high quality ad content in your apps to the vast user base of Huawei devices.

Features:

- Banner Ads
- Native Ads
- Rewarded Ads
- Interstitial Ads
- Splash Ads
- In Stream Roll Ads  


 ### Analytics Kit

*Version: 6.3.0.300*

HUAWEI Analytics Kit offers a rich array of preset analytics models that help you gain a deeper insight into your users, products, and content. With this insight, you can then take a data-driven approach to market your apps and optimize your products.


Features:

- Custom Event
- Predefined Event  

 ### APM

App Performance Management (APM) of HUAWEI AppGallery Connect provides minute-level app performance monitoring capabilities. You can view and analyze app performance data collected by APM in AppGallery Connect to comprehensively understand online performance of apps in real time, helping you quickly and accurately rectify app performance problems and continuously improve user experience.

 ### App Bundle Release

Users are generally more willing to download smaller apps. Therefore, reducing the size of your app package to the greatest possible extent, is essential for boosting its download and installation success rate. App Bundle does this for you, and has become the preferred service for an increasing number of developers. HUAWEI AppGallery Connect allows you to release your app in App Bundle format, with the relevant Huawei SDKs integrated, ensuring that users only need to download the required content, bundled in a smaller, more streamlined app package.

 ### App Linking

*Version: 1.5.1.300*

App Linking allows you to create cross-platform links that can work as defined regardless of whether your app has been installed by a user. When a user taps the link on an Android or iOS device, the user will be redirected to the specified in-app content. If a user taps the link in a browser, the user will be redirected to the same content of the web version.

Features:

- Social sharing
- Creating short & long app link
- Feature Analyse  

 ### App Messaging

*Version: 1.5.1.300*

App Messaging even allows you to customize how your messages look and the way they will be sent, and define events for triggering message sending to your users at the right moment.

Features:

- Displaying an In-App Message 

 ### App Release

After an app is developed, you need to submit it to Huawei for review in HUAWEI AppGallery Connect.

Steps :

- Configuring App Information
- Setting Countries/Regions to Release Your App
- Setting Whether the Version Is for Open Testing
- Uploading the App Package
- Configuring Payment Information
- Configuring Content Rating
- Configuring the Privacy Description
- Configuring the Privacy Policy
- Configuring Copyright Information
- Completing Other Version Information
- Scheduling the App Release
- Submitting Your App for Review
- Adjusting the Release Date for Your App
- Appendix: App Languages  

 ### App Signing

Android apps use a signature key for signing. Each signature key has an associated key certificate. Devices and services can use the certificate to check whether the app is from a trusted source. For trusted app updates, ensure that the app signature of the update package is the same as that of the installed app so that your app update will be accepted. However, if the signature key is lost or stolen, you cannot update your app. In this case, you will have to use a new package name to release the updated app. This will lead to risks such as user loss.

 ### Audio Engine

Audio Engine provides the karaoke headset monitoring feature with open APIs to help you integrate the headset monitoring to your app, facilitating the utilization of Huawei's hardware-enabled headset monitoring capability, and achieving a low-latency and low-noise headset real-time listening experience.

 ### Audio Kit

*Version: 1.4.0.300*

HUAWEI Audio Kit is a set of audio capabilities developed by Huawei. It provides you with audio playback capabilities based on the HMS ecosystem, including audio encoding and decoding capabilities at the hardware level and system bottom layer.

You can quickly build your own audio playback capabilities and audio apps based on HUAWEI Audio Kit.

Features:

- Implement playlists (adding, removing items to lists, creating new playlists)
- Seek bar drag and release controls and thus changing the progress of audio
- Display relevant playlist name that is currently played (left-upper corner)
- Render cover image (if exists) of the album in relevant places (main screen and notification bar
- Implement notification bar with full basic audio control
- Play audio from local storage files
- Implement advanced playback buttons (shuffle, loop etc)
- Implement basic playback buttons (play/pause, skip etc)
- Synchronize seek bar with audio playback 


 ### Audio Editor Kit

*Version: 1.3.0.300*

Audio Editor Kit provides a wide range of audio editing capabilities, including AI dubbing, audio source separation, spatial audio, voice changer, and sound effects. 
With these capabilities, the kit serves as a one-stop solution for you to develop audio-related functions in your app with ease.

Features:

- Create and Edit Audio
- Audio Format Conversion 
- Video Audio Extraction
- Add Effects
- AI Debugging with Editing  


 ### Auth Service

*Version: 1.6.0.300*

Most apps need to identify and authenticate users to tailor the app experience for individual users. However, building such a system from scratch is a difficult process. Auth Service can quickly build a secure and reliable user authentication system for your app. You only need to access Auth Service capabilities in your app without caring about the facilities and implementation on the cloud.

Features:

- Login with Twitter, Facebook, HuaweiGameCenter, HuaweiId, AnonymousAccount, MailAddress, PhoneNumber
- Redirect To Guide Page Supporting other thirt party ( GoogleSign, GooglePlaySign, QQ, WeChat, Weibo )
- Deregistration / Delete User
- Sign-out  

 ### AV Pipeline Kit

*Version: 6.0.0.302*

AV Pipeline Kit presets pipelines for scenarios like video playback, video super-resolution, and sound event detection, and provides Java APIs for you to use these pipelines. 
You can also call a single preset plugin directly through C++ APIs. If you want to achieve more functions other than those provided by the preset plugins or pipelines, you can even customize some plugins or pipelines based on your own needs.

Features:

- Asset Information 
- Base Player 
- Screen Resolution
- Video Sound Detection  

 ### Awareness Kit

*Version: 1.0.8.301*

HUAWEI Awareness Kit provides your app with the ability to obtain contextual information including users' current time, location, behavior, audio device status, ambient light, weather, and nearby beacons. Your app can gain insight into a user's current situation more efficiently, making it possible to deliver a smarter, more considerate user experience.

Features:

- Time Awareness
- Location Awareness
- Behavior Awareness
- Ambient Light Awareness
- Weather Awareness
- Headset Awareness
- Bluetooth Car Stereo Awareness
- Beacon Awareness
- Phone Status Awareness
- Screen Status Awareness
- Wi-Fi Status Awareness
- Dark Mode Awareness  

 ### CaaS Engine

CaaS Engine provides open APIs that are based on the HUAWEI MeeTime service. It allows app and hardware developers to easily incorporate video calling into their apps, and directly tailor the video source to their preferences with the help of HUAWEI DeviceVirtualization service.

 ### Cast Engine

Designed to provide multi-screen collaboration revolving around mobile phones, HUAWEI Cast Engine enables fast, stable, and low-latency collaboration between mobile phones and external large screens, delivering a seamless, cross-device collaborative experience.

 ### Cloud DB (Beta)

*Version: 1.4.9.300*

Cloud DB is a device-cloud synergy database product that provides data synergy management capabilities between the device and cloud, unified data models, and various data management APIs. In addition to ensuring data availability, reliability, consistency, and security, CloudDB enables seamless data synchronization between the device and cloud, and supports offline application operations, helping developers quickly develop device-cloud and multi-device synergy applications. As a part of the AppGallery Connect solution, Cloud DB builds the Mobile Backend as a Service (MBaaS) capability for the AppGallery Connect platform. In this way, application developers can focus on application services, greatly improving the production efficiency.

Features:

- Inserting Data
- Querying Data
- Updating Data  

 ### Cloud Debugging

Cloud Debugging tests your app using mainstream Huawei devices provided by Huawei. You can run your app on the latest and most popular Huawei devices to test app functions.

The testing experience is smooth and pleasant, like running an app on your own phone. The service is easy to use. You can perform operations such as rotating the screen, taking screenshots, and exporting logs.

 ### Cloud Functions (Beta)

Cloud Functions enables serverless computing. It provides the Function as a Service (FaaS) capabilities to simplify app development and O&M so your functions can be implemented more easily and your service capabilities can be built more quickly.


 ### Cloud Hosting (Beta)

Cloud Hosting makes web page deployment easier. You can focus on the UI interaction, UI layout design, and service logic, and do not need to pay attention to security configurations (such as domain name application and certificate management) and page distribution. In this way, you can build a highly secure and fast-accessed web page.

 ### Cloud Storage (Beta)

Cloud Storage is scalable and maintenance-free. It allows you to store high volumes of data such as images, audios, and videos generated by your users securely and economically with direct device access.

The service is stable, secure, efficient, and easy-to-use, and can free you from development, deployment, O&M, and capacity expansion of storage servers. You do not need to pay attention to indicators such as availability, reliability, and durability and can focus on service capability building and operations, improving user experience.

 ### Cloud Testing

Cloud Testing provides a complete set of automatic test processes based on real mobile phone use. It automatically tests the compatibility, stability, performance, and power consumption of Android apps, without manual intervention. In Cloud Testing, you can test your app against the full range of Huawei phones, to determine if there are any potential issues, well in advance.

 ### Comments

To provide more convenient and high-quality services and further optimize user experience, we enabled the Comments menu in AppGallery Connect. You can sign in to AppGallery Connect, select your app, and manage comments on the Comments page.


 ### Computer Graphics Kit

CG Rendering Framework, a key capability of HUAWEI Computer Graphics (CG) Kit, is a Vulkan-based high-performance rendering framework that consists of the physically based rendering (PBR) material, model, texture, light, and component subsystems. This rendering framework is designed for Huawei device development kit (DDK) features and implementation details to provide the best 3D rendering capabilities on Huawei devices using Kirin SoCs. In addition, the framework supports secondary development, which reduces app development difficulty and complexity and therefore increases your development efficiency.

 ### Crash

The AppGallery Connect Crash service provides a powerful yet lightweight solution to app crash problems. With the service, you can quickly detect, locate, and resolve app crashes (unexpected exits of apps), and have access to highly readable crash reports in real time, without the need to write any code.

To ensure stable running of your app and prevent user experience deterioration caused by crashes, it is key to monitor the running status of your app on each device. The Crash service provides real-time reports, revealing any crash of your app on any device. In addition, the Crash service can intelligently aggregate crashes, providing context data when a crash occurs, such as environment information and stack, for you to prioritize the crash easily for rapid resolution.

 ### Distribution Analysis

Sign in to AppGallery Connect and click App analytics. Click an app, go to the Distribution analysis page, and choose Downloads & installs.

Native app:

- You can check metrics related to user installations and usage of your native app through the Downloads & installs, Download source analysis, Pre-orders, and User acquisition and retention menus.
- You can check the sales metrics for your native app through the In-App Purchases and Paid app details menus.

Quick app:

- You can learn about the user and running data of your quick app through the Quick app user report and Quick app running report menus.  

 ### Drive Kit

*Version: 5.0.0.305*

Drive Kit allows you to create apps that use HUAWEI Drive capabilities. Drive Kit provides cloud storage for your apps, enabling users to store files that are created while using your apps, including photos, videos, and documents, in Drive as well as download files, upload files, query historical file versions, search for files, comment on the files, and reply to comments.


Features:

- File Change Notification
- Commenting and replaying on files
- Batch Operations
- Search File
 

 ### Dynamic Ability

*Version: 1.0.16.301*

Dynamic Ability is a service in which HUAWEI AppGallery implements dynamic loading based on the Android App Bundle technology. Apps integrated with the Dynamic Ability SDK can dynamically download features or language packages from HUAWEI AppGallery as required, reducing the unnecessary consumption of network traffic and device storage space.

Features: 

- Requesting to Install Features
- Listening to the Dynamic Loading Status
- Delaying Feature Installation and Uninstalling a Feature
- Canceling Installation
- Obtaining Task Execution Status
- Obtaining the List of Installed Features

 

 ### Dynamic Tag Manager

*Version: 5.3.0.300*


HUAWEI Dynamic Tag Manager (DTM) is a dynamic tag management system. With DTM, you can dynamically update tracking tags on a web-based UI to track specific events and report data to third-party analytics platforms, tracking your marketing activity data as needed.


Features:

- Set Tags to track events
- Create Group
- Set Configuration management which includes import, export, edit and delete operations of configurations
- Set Version
- Set Condition
- Set Variable  


 ### Early Access

This service tests how well your game adapts to Huawei devices and provides gameplay data to optimize versions for upcoming release. Early access data will also be used as an important reference for rating games and allocating promotion resources. Therefore, it is strongly recommended that you perform early access on the game.

 ### FIDO

*Version :5.2.0.301*

HUAWEI FIDO provides your app with FIDO2 based on the WebAuthn standard. It provides Android Java APIs for apps and browsers, and allows users to complete authentication through roaming authenticators (USB, NFC, and Bluetooth authenticators) and platform authenticators (fingerprint and 3D face authenticator). In addition, FIDO provides your app with powerful local biometric authentication capabilities, including fingerprint authentication and 3D facial authentication. It allows your app to provide secure and easy-to-use password-free authentication for users while ensuring reliable authentication results.

Features:

- FIDO2 Client
- BioAuthn  


 ### Game Service

*Version: 6.1.0.301*

Game Service helps you build basic game functions such as achievements, leaderboards, and saved games at low costs. Improve efficiency in testing, managing, and releasing games and keep attracting users with continuous optimization based on game data analysis.


- HUAWEI ID sign-in
- Game addiction prevention
- Floating window
- Achievements
- Events
- Leaderboards
- Saved games
- Player statistics
- Access to basic game information  

 ### Gift Management

1. New Users Gift Packages and Regular Gift Packages

   1.1 New Users Gift Packages

   New users gift packages target users of specific Huawei device models and are provided as one of the selling points of new devices. Involved apps will be promoted via wide-ranging marketing resources including but not limited to product launch events, product overview pages, and official social accounts. That's not all, these apps will be pushed to users whose newly purchased devices are connected to the Internet for the first time.

   1.2 Regular Gift Packages

   Regular gift packages are classified into common gift packages and activity gift packages. These packages will be promoted using resources of HUAWEI AppGallery (including Kids Center, GameCenter, and EduCenter), including but not limited to the gift zone, app details page, and activity zone. Gift packages that meet specific conditions will have more exposure chances.



 ### Health Kit

*Version: 6.1.0.301*

HUAWEI Health Kit (Health Kit for short) allows ecosystem apps to access fitness and health data of users based on their HUAWEI ID and authorization.


Features:

- Activity Records Controller
- Auto Recorder Controller
- Data Controller
- Signing in and applying for scopes  

 ### Hem Kit

*Version: 1.0.0.303*

HUAWEI Enterprise Manager (HEM) is a mobile device management solution provided for you based on the powerful platform and hardware of Huawei. 
The device deployment service in HEM helps you automatically install a Device Policy Controller (DPC) app on enterprise devices in batches.

Features:

- Active Call
- De-active Call  

 ### hQUIC Kit

hQUIC Kit gives your apps low latency, high throughput, and secure and reliable communications capabilities. It supports the gQUIC protocol and provides intelligent congestion control algorithms to avoid congestions in different network environments, giving your apps faster connection establishment, reduced packet loss, and higher throughput.

 ### Identity Kit

*Version: 5.3.0.300*

Identity Kit provides unified address management services for users and allows your app to access users' addresses conveniently upon obtaining authorization from the users.

Features:

- Obtain User Address  

 ### Image Kit

*Version: 1.0.3.303*

HUAWEI Image Kit incorporates powerful scene-specific smart design and animation production functions into your app, giving it the power of efficient image content reproduction while providing a better image editing experience for your users.

Features:

- Image Vision Service
- Image Render Service  


 ### In App Purchases

IAP provides convenient in-app payment experience, which helps you boost monetization. Users can purchase a variety of virtual products, including one-time virtual products and subscriptions, directly within your app.


 ### Integration Check

HUAWEI Developers provides a tool that fully automates the checking of apps. You simply fill in the app information and upload the app package, and the tool will scan your package and provide you with a report. You can then optimize your app based on the report for improved performance.


 ### Location Kit

*Version: 6.0.0.302*


HUAWEI Location Kit combines the GNSS, Wi-Fi, and base station location functionalities into your app to build up global positioning capabilities, allowing you to provide flexible location-based services targeted at users around the globe

Features:

- Location Availability
- Current Location
- Fused location
- LocationHD
- Location Update With Intent
- Location Update With Callback
- GeoFence
- Activity identification
- Activity Recognition
- Navigation Context State
- Mock Mode
- Mock Location  

 ### Map Kit

*Version: 6.0.0.301*

HUAWEI Map Kit is an SDK for map development. It covers map data of more than 200 countries and regions, and supports dozens of languages. With this SDK, you can easily integrate map-based functions into your apps.

Features:

- Map Creation
- Map Interactions
- Drawing on a Map
- Map Style Customization 
- Show Direction Routing 
- Open Petal Map  

 ### ML Kit


HUAWEI ML Kit allows your apps to easily leverage Huawei's long-term proven expertise in machine learning to support diverse artificial intelligence (AI) applications throughout a wide range of industries. Thanks to Huawei's technology accumulation, ML Kit provides diversified leading machine learning capabilities that are easy to use, helping you develop various AI apps.


Features:

Text Related Service

- TER : Text Recognition - *2.0.1.300*
- DOR : Document Recognition - *2.0.1.300*
- BCR : Bank Card Recognition - *2.0.0.300*
- GCR : General Card Recognition- *2.0.1.300*

Language Related Service

- Language Detection - *2.0.3.300*
- Text Translation - *2.0.3.300*
- ASR : Speech Recognition - *2.0.3.300*
- TTS : Text to Speech - *2.0.2.300*
- AFT : audio file Transcription- *2.0.1.300*
- VCC : Video Course Creator - *2.0.2.301*
- Real-Time Transcription - *2.0.3.300*
- Sound Detection - *2.0.3.300*

Image Related Service

- Image Classification - *2.0.1.300*
- Object Detection - *2.0.3.300*
- Landmark Recognition - *2.0.3.300*
- Image Segmentation - *2.0.4.301*
- Product Visual Search - *2.0.4.300*
- Image Super-Resolution - *2.0.4.300*
- Document Skew Correction - *2.0.4.300*
- Text Image Super-Resolution - *2.0.4.300*
- Scene Detection - *2.0.3.300*
- Form Table Recognition - *2.0.4.300*

Face/Body Related Service

- Face Detection - *2.0.1.300*
- Skeleton Detection - *2.0.3.300*
- Liveness Detection - *2.0.2.300*
- Hand Key Point Detection - *2.0.3.300*

Natural Language Processing Services

- Text Embedding - *2.0.3.300*
- ML Kit Version : *2.0.3.300*   

 ### Nearby Service

*Version: 6.1.0.301*

HUAWEI Nearby Service allows apps to easily discover nearby devices and set up communication with them using technologies such as Bluetooth and Wi-Fi. The service provides Nearby Connection and Nearby Message APIs.

Features:

- Nearby Connection
- Nearby Message 


 ### Network Kit

*Version: 5.0.7.300*

Network Kit is a basic network service suite. It incorporates Huawei's experience in far-field network communications, and utilizes scenario-based RESTful APIs as well as file upload and download APIs.
 Therefore, Network Kit can provide you with easy-to-use device-cloud transmission channels featuring low latency, high throughput, and high security.

Features:

- URL Request
- File Upload 
- File Download
- HTTP Sync Request
- HTTP Async Request
- RestClient Annotation Sync Request 
- RestClient Annotation ASync Request  

 ### OneHop Engine

The HUAWEI OneHop Engine serves as a full scenario solution for seamless interaction between multiple devices. By leveraging NFC technology, HUAWEI OneHop Engine simplifies the collaboration and interaction between your smartphones and other devices with a simple tap. Huawei's new feature Multi-Screen Collaboration released with the EMUI 10.0 system allows users to use their phone on their tablet or laptop with a simple tap, allowing users to expand their productivity without buying more things. The one-tap connection is made possible with HUAWEI OneHop.

In addition, the HUAWEI OneHop Engine supports third-party apps and devices. Third-party developers can work jointly with Huawei to provide seamless and efficient interaction between the smartphones and other devices across all scenarios.

 ### Open Testing

You can use the open testing function to easily distribute your app to trusted test users for testing. In this way, you can receive user feedback and improve your app in a timely manner.

You can create a test user list in AppGallery Connect, configure trusted test users in the list, and upload the app package of the open testing version. After the version is approved, AppGallery Connect will send a test invitation to the configured test users. After a test user accepts the invitation and agrees to join the open testing plan, the test user can download the test version of your app on the HUAWEI AppGallery client for testing. The test user can directly report problems found in the app to you to complete the open testing.

 ### Operation Analysis

Details the financial status of your app, covering its global revenue, purchaser count, and conversion rate, and collects push message statistics to evaluate how effectively your app is being promoted.


 ### Paid Apps

AppGallery Connect enables consumers to download high-quality apps and pay through a variety of payment methods, providing a better way for you to monetize your apps. To make your app a paid one, you can set Payment type to Paid when releasing your app in AppGallery Connect. For details, please refer to Releasing Your App.

You can integrate the AppGallery DRM Service SDK to check whether a user has successfully purchased your paid app based on your needs.


 ### Panorama Kit

*Version: 5.0.2.304*

By integrating the SDK of HUAWEI Panorama Kit, your app can quickly display interactive viewing of 360-degree spherical or cylindrical panoramic images in simulated 3D space on Android phones, delivering an immersive experience to users.

Features:

- Displaying a panorama in an app
- Displaying panorama outside an app  


 ### Phased Release

By rolling out new versions to users on a gradual basis, you can solicit feedback and resolve issues in advance, minimizing possible adverse consequences.

To update a global version of your app, you can adopt the mode of release by phase. In this mode, the app updates are exposed only to a certain proportion of users first, and then the proportion will be increased slowly to finally cover all users. By exposing app updates only to a small scope of users, you can obtain feedback from these users and adjust your app accordingly before global release, effectively reducing risks that may occur after global release.

 ### Preorders

This service attracts potential users to your game prior to its release. During early access or initial release of a game, the system sends notifications to users who have preordered it to attract them to download the game. Therefore, it is recommended that you apply for Preorders.

 ### Product Management

Products can be classified into subscriptions and non-subscriptions:

1. Non-subscriptions are purchased once. They can be further divided into consumables and non-consumables. You can configure virtual products when you select consumables or non-consumables in editing state and the app distribution area contains the Chinese mainland.

  1.1 Consumables: They are products that are intended to be consumed and purchased repeatedly, such as in-game currencies.

  1.2 Non-consumables: Users only need to purchase them once. Such products do not expire or decrease as they are used.

2. Subscriptions can be automatically renewed, and do not support virtual products currently.
You can add products either by adding a single product, or by importing products in batches.


 ### Push Kit

*Version: 5.1.1.301*

HUAWEI Push Kit is a messaging service provided for you. It establishes a messaging channel from the cloud to devices. By integrating Push Kit, you can send messages to your apps on users' devices in real time. This helps you maintain closer ties with users and increases user awareness of and engagement with your apps. The following figure shows the process of sending messages from the cloud to devices.

Features:

- Service Configuration & Development
- Generate Notification on Device
- Basic Capabilities
- Other Capabilities
- Enhanced Capabilities  


 ### Quick App

Quick apps are a new form of installation-free apps developed based on industry standards. They are developed based on the frontend technology stack and support native rendering, therefore possessing the advantages of both HTML5 apps and native apps. Users do not need to install quick apps and only need to tap them open and enjoy the same experience and performance as native apps.

 ### Remote Configuration

*Version: 1.6.0.300*

AppGallery Connect Remote Configuration allows you to change the behavior and appearance of your app online without requiring users to update the app. With the service, you can provide tailored experience for your users in a timely manner.

Features:

- Fetching Values from Remote Configuration  

 ### Safety Detect

*Version: 6.1.0.302*

Safety Detect builds robust security capabilities, including system integrity check (SysIntegrity), app security check (AppsCheck), malicious URL check (URLCheck), fake user detection (UserDetect), and malicious Wi-Fi detection (WifiDetect), into your app, effectively protecting it against security threats.

Features:

- SysIntegrity API: Checks whether the device running your app is secure, for example, whether it is rooted.
- AppsCheck API: Obtains a list of malicious apps.
- URLCheck API: Determines the threat type of a specific URL.
- UserDetect API: Checks whether your app is interacting with a fake user.
- WifiDetect API: Checks whether the Wi-Fi to be connected is secure.  


 ### Scan Kit

*Version: 2.0.0.300*

HUAWEI Scan Kit scans and parses all major 1D and 2D barcodes and generates QR codes, helping you quickly build barcode scanning functions into your apps.

Scan Kit automatically detects, magnifies, and recognizes barcodes from a distance, and is also able to scan a very small barcode in the same way. It works even in suboptimal situations, such as under dim lighting or when the barcode is reflective, dirty, blurry, or printed on a cylindrical surface. This leads to a higher scanning success rate, and an improved user experience. Scan Kit can be integrated into both Android and iOS systems. The Android system supports barcode scanning in landscape mode after Scan Kit is integrated.

Features:

- Default View Mode
- Custom View Mode
- Bitmap Mode
- Multi-Processor Synchronous Mode
- Multi-Processor Asynchronous Mode
- Generate QR Code  


 ### Scene Kit

*Version: 6.0.0.301*

HUAWEI Scene Kit is a lightweight rendering engine that features high performance and low consumption. It provides advanced descriptive APIs for you to edit, operate, and render 3D materials. Scene Kit adopts physically based rendering (PBR) pipelines to achieve realistic rendering effects. With this Kit, you only need to call some APIs to easily load and display complicated 3D objects on Android phones.

Features:

- AR View
- Face View
- Scene View
- Render View
- 2d Fluid Simulation View  

 ### Search Kit

*Version: 5.0.4.303*

HUAWEI Search Kit fully opens Petal Search capabilities through the device-side SDK and cloud-side APIs, enabling ecosystem partners to quickly provide the optimal mobile app search experience.

- Keyword search
- Custom search
- Web search
- Auto Suggestion
- Spelling Check  

 ### Share Engine

Share Engine allows for high-speed file transfers between phones, PCs, and other devices by using Bluetooth to discover nearby devices and authenticate connections, and then setting up peer-to-peer Wi-Fi channels. File transfer speeds can be up to 80 MB/s when the device and environment permit it.

 ### Site Kit

*Version: 6.0.1.303*

Site Kit provides place search services including keyword search, nearby place search, place detail search, and place search suggestion, helping your app provide convenient place-related services to attract more users and improve user loyalty.

Features:

- Keyword Search: Returns a place list based on keywords entered by the user.
- Nearby Place Search: Searches for nearby places based on the current location of the user's device.
- Place Detail Search: Searches for details about a place.
- Place Search Suggestion: Returns a list of suggested places.
- Autocomplete: Returns an autocomplete place and a list of suggested places based on the entered keyword.  

 ### Video Kit

*Version: 1.0.10.300*

HUAWEI Video Kit provides video playback in this version, and will support video editing and video hosting in later versions, helping you quickly build desired video features to deliver a superb video experience to your app users.

Features:

- Play a video.
- Switch to the specified bitrate. (incomplete)
- Enable or disable the repeat mode.
- Set the playback mode (audio or audio+video).
- Mute or unmute.
- Adjust the playback speed.
- Adjust volume
- Paste URL to play the video
- Use Preloader to enhance the playback (incomplete)  


 ### Video Editor Kit

*Version: 1.1.0.301*

The one-stop toolkit provides a rich array of video processing capabilities, including video import/export, editing, rendering, and media asset management, for fast and easy development. 
Its open, reliable APIs allow you to create the perfect possible video to address any scenario.

Features:

- Edit Video 
- Add Stickers 
- Add Sounds
- Reverse Video 
- And More Editing Capabilities  


 ### Wallet Kit

HUAWEI Wallet Kit is an open capability that integrates Huawei's full-stack "chip-device-cloud" technologies to provide digital passes such as cards (including ID cards), coupons, tickets and keys on an integrated platform. It enables users to add their tickets, boarding passes, loyalty cards, coupons, gift cards, and other cards or passes on their mobile phones for convenient, anytime use. It also facilitates interactions between apps and users through location-based notifications, real-time status updates, and NFC capabilities.

 ### Wireless Kit

*Version: 5.3.0.311*

HUAWEI Wireless Kit encapsulates a range of wireless transmission capabilities and provides network quality of experience (QoE) information for your app to achieve efficient wireless transmission and a smooth performance.

Features:

- Network Qoe
- Report app quality   

 ### WisePlay DRM

WisePlay DRM provides you with digital content copyright protection capabilities, including hardware- and software-level DRM capabilities. This helps to support you in various use cases such as online application for client certificates, content encryption in multiple formats and algorithms, and online and offline playback. Video service providers (SPs) use keys to encrypt content. The content must be decrypted using the keys before being played.



 ### 3D Modeling Kit

*Version: 1.1.0.300*

Automatically generate 3D object models and PBR texture maps and capture motion in the real world, for remarkably efficient and cost-effective 3D content production.

Features:

- Create Models 
- Create Materials 
- Show Models
- Show Materials
- Download Models
- Download Materials  

 ### 5G Modem Kit

*Version: 6.0.0.302*

Bolster your app with cutting-edge 5G communicati efnan serkanons capabilities, by equipping it with exclusive 5G cell information services.

Features:

- Query Modem Parameters.
- Crowdtesting Function  


## Contributors

- Çağatay Kızıldağ
- Begüm Avcı
- Cem Genar
- Erdal Kaymak
- Efnan Akkuş
- Ismail Emre Bayırlı
- Mustafa Can
- Serkan Mutlu
- Ubeyde Akgül
- Yunus Emre Pekgüç

## Useful Links
* [Huawei Developers Medium Page EN](https://medium.com/huawei-developers)
* [Huawei Developers Medium Page TR](https://medium.com/huawei-developers-tr)
* [Huawei Developers Forum](https://forums.developer.huawei.com/forumPortal/en/home)

## Licence
Copyright 2020. Explore in HMS. All rights reserved.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

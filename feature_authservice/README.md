


# HMS Auth Service

HMS Auth Service App; It is created for phones running with the Android-based HMS service an application.
Provides whole features and capabilities of HUAWEI AppGalleryConnect AutService .

## :notebook_with_decorative_cover: Introduction 
Most apps need to identify and authenticate users to tailor the app experience for individual users. However, building such a system from scratch is a difficult process. Auth Service can quickly build a secure and reliable user authentication system for your app. You only need to access Auth Service capabilities in your app without caring about the facilities and implementation on the cloud.

-   Auth Service provides SDKs and backend services, supports multiple authentication modes, and provides a powerful management console, enabling you to easily develop and manage user authentication.
-   The Auth Service SDK supports multiple platforms and programming languages so that users can have consistent sign-in experience using the same user identity regardless across diverse device types.
-   Auth Service can automatically adapt to other serverless services. You can protect user data security in the serverless services by defining simple security rules.


 ## :question: Before You Start 
 **You need to agconnect-services.json for run this project correctly.**

- If you don't have a Huawei Developer account check this document for create; https://developer.huawei.com/consumer/en/doc/start/10104
- Open your Android project and find Debug FingerPrint (SHA256) with follow this steps; View -> Tool Windows -> Gradle -> Tasks -> Android -> signingReport
- Login to Huawei Developer Console (https://developer.huawei.com/consumer/en/console)
- If you don't have any app check this document for create; https://developer.huawei.com/consumer/en/doc/distribution/app/agc-create_app
- Add SHA256 FingerPrint into your app with follow this steps on Huawei Console; My Apps -> Select App -> Project Settings
- Make enable necessary SDKs with follow this steps; My Apps -> Select App -> Project Settings -> Manage APIs
- For this project you have to set enable AuthService, AnonymousAccount, HuaweiId, GameCenter, MobileNumberi MailAdress, Twitter, Facebook and Configure these. 
- Than go again Project Settings page and click "agconnect-services.json" button for download json file.
- Move to json file in base "app" folder that under your android project. (https://developer.huawei.com/consumer/en/doc/development/HMS-Guides/69407812#h1-1577692046342)
- Go to app level gradle file and change application id of your android project. It must be same with app id on AppGallery console you defined.


## :information_source: Things to Know
- Since the application is written entirely in HMS, you must have HMS Core installed on your device.
- For Android devices without HMS Core, you can download the latest version from this link; https://tik.to/9l6

## :rocket: Features  

Version : 1.4.2.301 ( 2020-11-30 )

- Login with PhoneNumber
- Login with EmailAdress
- Login with AnonymousAccount
- Login with Twitter
- Login with Facebook
- Login with HuaweiGameCenter
- Login with HuaweiId
- Login with QQ
- Login with WeChat
- Login with Weibo
- DeRegistration / deleteUser
- Change information for PhoneNumber Account
- Change information for EmailAdress Account

## :star2:  Useful Links 
* [About The Service Guide](https://developer.huawei.com/consumer/en/doc/development/AppGallery-connect-Guides/agc-auth-introduction-0000001053732605)
* [Integrating Build Dependencies](https://developer.huawei.com/consumer/en/doc/development/AppGallery-connect-Guides/agc-auth-android-getstarted-0000001053053922)
* [Version Change History](https://developer.huawei.com/consumer/en/doc/development/AppGallery-connect-Guides/agc-auth-android-releasenotes-0000001053213910)
* [Developer Page](https://developer.huawei.com/consumer/en/agconnect/auth-service/)
* [Forum Shares](https://forums.developer.huawei.com/forumPortal/en/home?search=auth%20Service)
* [Sample App](https://developer.huawei.com/consumer/en/doc/development/AppGallery-connect-Examples/agc-auth-android-samplecode-0000001058885130)
* [Codelab](https://developer.huawei.com/consumer/en/codelab/AuthenticationService/index.html#0)

## :information_source: Licence
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
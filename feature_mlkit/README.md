

# HiML

HiML App; It is created for phones running with the Android-based HMS service an application.
Provides whole features and capabilities of HUAWEI ML Kit.

## :notebook_with_decorative_cover: Introduction 
HUAWEI ML Kit allows your apps to easily leverage Huawei's long-term proven expertise in machine learning to support diverse artificial intelligence (AI) applications throughout a wide range of industries. Thanks to Huawei's technology accumulation, ML Kit provides diversified leading machine learning capabilities that are easy to use, helping you develop various AI apps.

## App View
...gif will come here

## Screenshots
...ss image will come here


 ## :question: Before You Start 
 **You need to agconnect-services.json for run this project correctly.**

- If you don't have a Huawei Developer account check this document for create; https://developer.huawei.com/consumer/en/doc/start/10104
- Open your Android project and find Debug FingerPrint (SHA256) with follow this steps; View -> Tool Windows -> Gradle -> Tasks -> Android -> signingReport
- Login to Huawei Developer Console (https://developer.huawei.com/consumer/en/console)
- If you don't have any app check this document for create; https://developer.huawei.com/consumer/en/doc/app/agc-help-releaseapkrpk-0000001106463276
- Add SHA256 FingerPrint into your app with follow this steps on Huawei Console; My Apps -> Select App -> Project Settings
- Make enable necessary SDKs with follow this steps; My Apps -> Select App -> Project Settings -> Manage APIs
- For this project you have to set enable Map Kit, Site Kit, Auth Service, ML Kit
- Than go again Project Settings page and click "agconnect-services.json" button for download json file.
- Move to json file in base "app" folder that under your android project. (https://developer.huawei.com/consumer/en/doc/development/HMS-Guides/69407812#h1-1577692046342)
- Go to app level gradle file and change application id of your android project. It must be same with app id on AppGallery console you defined.


## :information_source: Things to Know
- Since the application is written entirely in HMS, you must have HMS Core installed on your device.
- For Android devices without HMS Core, you can download the latest version from this link; https://tik.to/9l6

## :rocket::milky_way:  Features  

Version : 2.0.3.300 ( 2020-09-27 )

### Text Related Service
- TER : Text Recognition - 2.0.1.300
    - TER with using imageview on device
    - TER with using imageview on cloud
    - TER with using storage on device
    - TER with using storage on cloud 
    - TER with using camera streams

- DOR : Document Recognition - 2.0.1.300
    - DOR with using storage on cloud   
    - DOR with using imageview on cloud

- BCR : Bank Card Recognition - 2.0.2.300
    - BCR with using camera stream

- GCR : General Card Recognition - 2.0.1.300
    - GCR with using storage
    - GCR with using taked photo
    - GCR with using video camera stream

### Language Related Services
- Language Detection - 2.0.3.300
    - LD on device
    - LD on cloud

- Text Translation - 2.0.3.300
    - TT on device
    - TT on cloud

- ASR : Speech Recognition - 2.0.3.300
    - ASR with Plugin dialog
    - ASR with Custom dialog

- TTS : Text to Speech - 2.0.2.300

- AFT : audio file Transcription - 2.0.1.300 ( New )

- RTT : Real-Time Transcription - 2.0.3.300 ( New )

- SD  : Sound Detection - 2.0.3.300 ( New )

- VCC : Video Course Creator - 2.0.2.301 ( Beta )


###  Image Related Services
- Image Classification - 2.0.1.300
    - IC with using camera stream
    - IC with using imageview
    - IC with using storage
    - IC with using take a photo

- Object Detection - 2.0.1.300
    - OD with using camera stream

- Landmark Recognition - 2.0.1.300
    - LR with using imageview
    - LR with using storage
    - LR with using take a photo

- Image Segmentation - 2.0.2.301

- Product VisualSearch - 2.0.1.300
    - PVS with using imageview 
    - PVS with using storage 
    - PVS with using take a photo 
    - PVS with using camera Stream on Plug-in

- Image Super-Resolution - 2.0.3.300 ( New )
    - ISR with using storage

- Document Skew Correction - 2.0.2.300 ( New )
    - DSC with using imageview
    - DSC with using storage
    - DSC with using take a photo

- Text Image Super-Resolution - 2.0.3.300 ( New )
    - TISR with using storage

- Scene Detection - 2.0.3.300 ( New )
    - SCDTC with using camera stream
    - SCDTC with using imageview
    - SCDTC with using storage
    - SCDTC with using take a photo

- FormTable recognition - 2.0.4.300 ( New )
    - FTR with using imageview
    - FTR with using storage
    - FTR with using take a photo



###  Face/Body Related Services
- Face Detection - 2.0.1.300
    - FD with using imageview
    - FD with using storage
    - FD with using camera stream

- Skeleton Detection - 2.0.3.300 ( New )
    - SKLD with using camera stream
    - SKLD with using imageview
    - SKLD with using storage
    - SKLD with using take a photo

- Liveness Detection - 2.0.2.300 ( New )

- Hand Keypoint Detection - 2.0.3.300 ( New )
    - HKPD with using camera stream
    - HKPD with using imageview
    - HKPD with using storage
    - HKPD with using take a photo

###  Natural Language Processing Services
- Text Embedding            • 2.0.3.300 ( New )


## :star2:  Useful Links 
* [About The Service Guide](https://developer.huawei.com/consumer/en/doc/development/HMS-Guides/ml-introduction-4)
* [About The Service Guide V5](https://developer.huawei.com/consumer/en/doc/HMSCore-Guides-V5/service-introduction-0000001050040017-V5)
* [Developer Page](https://developer.huawei.com/consumer/en/hms/huawei-mlkit)
* [Forum Shares](https://forums.developer.huawei.com/forumPortal/en/topicview?tid=0201345167137050132&fid=0101187876626530001)
* [Samples on Developer Page](https://developer.huawei.com/consumer/en/doc/development/HMS-Examples/ml-samplecode-4)
* [Samples on Github](https://github.com/HMS-Core/hms-ml-demo)
* [Manifest Permissions](https://developer.huawei.com/consumer/en/doc/development/HMS-Guides/ml-assigning-permissions-4)
* [Integrating Build Dependencies](https://developer.huawei.com/consumer/en/doc/development/HMS-Guides/ml-sdk-overview)
* [Integrating Build Dependencies V5](https://developer.huawei.com/consumer/en/doc/HMSCore-Guides-V5/overview-sdk-0000001051070278-V5)
* [Version Change History V5](https://developer.huawei.com/consumer/en/doc/HMSCore-Guides-V5/version-changehistory-0000001050040023-V5)

## :information_source: Licence
Copyright (c) Huawei Technologies Co., Ltd. 2020-2022. All rights reserved.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
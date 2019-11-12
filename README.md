
# VideoRecordTimer
⭐️ Star us on GitHub — it helps!

## Motivation
This project was saved as a template for Android app creation to perform basic functions.

## Features
This takes the value for hours: minutes: seconds from the previous activity in String format,
This is a template for recording video screens.

## Build status
- Android Studio 3.4.1  
- Build #AI-183.6156.11.34.5522156, built on May 2, 2019  
- JRE: 1.8.0_152-release-1343-b01 amd64  
- JVM: OpenJDK 64-Bit Server VM by JetBrains s.r.o  
- Windows 10 10.0-  

## Screenshots
"![](/intro.jpg)"

## Installation
If you use the GitHub website
1. Open in Desktop or Download ZIP about this project

If you use git bash
1. git clone https://github.com/jogilsang/android-VideoRecordTimer.git

## How to use?
To open the project in Android Studio:  

1. Launch Android Studio   
2. select File-New-Import Project from the top menu bar  
3. Just run  

if it doesn't work, check build status at the top  

4. Check your manifes file, and add the contents below.   
   Implement getPermission () separately, or check the app's permissions manually after build  
```xml
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.RECORD_AUDIO" />
    <uses-permission android:name="android.permission.CAMERA" />

    <uses-feature android:name="android.hardware.camera" />
    <uses-feature android:name="android.hardware.camera.autofocus" />
    <uses-feature
        android:name="android.hardware.camera.front"
        android:required="false" />
```

5. The core code is shown below.  
```java
    private boolean prepareMediaRecorder() {

        mediaRecorder = new MediaRecorder();

        mCamera.unlock();
        mediaRecorder.setCamera(mCamera);

        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_720P));

        mediaRecorder.setOutputFile("/sdcard/myvideo.mp4");
        mediaRecorder.setMaxDuration(600000); // Set max duration 60 sec.
        mediaRecorder.setMaxFileSize(50000000); // Set max file size 50M

        try {
            mediaRecorder.prepare();
        } catch (IllegalStateException e) {
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            releaseMediaRecorder();
            return false;
        }
        return true;

    }
```

## Contribute
If you want to contribute to this project, it would be nice to add the following:
- UI / UX
- Screen Orientation function

## Credits
@jogilsang

## Reference
- Android-Video Intent and Video Paths, Video Playback Total Cleanup  
https://blog.naver.com/jogilsang/221694414001  

## Cotact
mail :
jogilsang@naver.com

kakao :
jogilsang

Instagram :
<https://www.instagram.com/jogilsang3>

Youtube :
<https://www.youtube.com/user/mrjogilsang>

more information : 
<https://blog.naver.com/jogilsang>

## Donate
Bitcoin : 351pQjDFFWW61HHKcSFQHcEMNYy4rP91ex

Etherium : 0xb2470124ac43a955c36d7a21e208fae5d0d5d2e0

## License
The MIT License © 2019 jogilsang
```
The MIT License

Copyright (c) 2019 jogilsang

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
```


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

## Code Style


## Screenshots
"![](/intro.jpg)"

## Tech/framework used
```
import android.media.MediaRecorder;
```

```
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"></uses-permission>
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.RECORD_AUDIO" />
    <uses-permission android:name="android.permission.CAMERA" />

    <uses-feature android:name="android.hardware.camera" />
    <uses-feature android:name="android.hardware.camera.autofocus" />
    <uses-feature
        android:name="android.hardware.camera.front"
        android:required="false" />
```

## Code Example
```
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

## Reference
- Android-Video Intent and Video Paths, Video Playback Total Cleanup
https://blog.naver.com/jogilsang/221694414001  

## Installation
If you use the GitHub website
1. Open in Desktop or Download ZIP about this project

If you use git bash
1. git clone https://github.com/jogilsang/android-VideoRecordTimer.git

## How to use?
Launch Android Studio and select File-New-Import Project from the top menu bar. Just run
if it doesn't work, check build status at the top

## Contribute
If you want to contribute to this project, it would be nice to add the following:
- UI / UX
- Screen Orientation function

## Credits
@jogilsang

## License
Apache © jogilsang
![AUR license](https://img.shields.io/aur/license/VideoRecordTimer)

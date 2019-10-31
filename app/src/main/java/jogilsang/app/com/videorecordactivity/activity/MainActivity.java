
package jogilsang.app.com.videorecordactivity.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.TimerTask;

import io.realm.Realm;
import jogilsang.app.com.videorecordactivity.R;
import jogilsang.app.com.videorecordactivity.view.CameraPreview;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private Camera mCamera;
    private CameraPreview mPreview;
    private MediaRecorder mediaRecorder;
    private ImageView capture, switchCamera, guide;

    private Context myContext;
    private LinearLayout cameraPreview;
    private boolean cameraFront = false;

    public static String KEY_1 = "key_1";
    public static String KEY_2 = "key_2";
    public static String KEY_3 = "key_3";
    public static String KEY_4 = "key_4";
    public static String KEY_5 = "key_5";
    public static String KEY_6 = "key_6";

    public String get_value_1 = "";
    public String get_value_2 = "";
    public String get_value_3 = "";
    public String get_value_4 = "";
    public String get_value_5 = "";
    public String get_value_6 = "";

    public String videoPath = "";

    public TextView count_view;

    TimerTask timerTask;

    // Realm database
    private Realm realm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        getPermission();

        // initSet();

        // TODO : onCreate 에 선언한다.
        // Realm 인스턴스를 얻습니다
        realm = Realm.getDefaultInstance();

        initView();

        // 촬영시간
        get_value_2 = "000030";
        bind("000030");

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        myContext = this;
        initialize();

        setListener();

    }

    public String getExternalPath(String forlderName){

        String sdPath ="";
        String ext = Environment.getExternalStorageState();
        if(ext.equals(Environment.MEDIA_MOUNTED)){
            sdPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + forlderName;
        }else{
            sdPath  = getFilesDir() +"/" + forlderName;

        }
        return sdPath;
    }

    public void getPermission(){

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.CAMERA,
                        Manifest.permission.WAKE_LOCK
                },
                1000);
    }

    public void countDown(String time) {

        long conversionTime = 0;

        // 1000 단위가 1초
        // 60000 단위가 1분
        // 60000 * 3600 = 1시간

        String getHour = time.substring(0, 2);
        String getMin = time.substring(2, 4);
        String getSecond = time.substring(4, 6);

        // "00"이 아니고, 첫번째 자리가 0 이면 제거
        if (getHour.substring(0, 1) == "0") {
            getHour = getHour.substring(1, 2);
        }

        if (getMin.substring(0, 1) == "0") {
            getMin = getMin.substring(1, 2);
        }

        if (getSecond.substring(0, 1) == "0") {
            getSecond = getSecond.substring(1, 2);
        }

        // 변환시간
        conversionTime = Long.valueOf(getHour) * 1000 * 3600 + Long.valueOf(getMin) * 60 * 1000 + Long.valueOf(getSecond) * 1000;

        new CountDownTimer(conversionTime, 1000) {

            // 특정 시간마다 뷰 변경
            public void onTick(long millisUntilFinished) {

                String hour = String.valueOf(millisUntilFinished / (60 * 60 * 1000));

                long getMin = millisUntilFinished - (millisUntilFinished / (60 * 60 * 1000)) ;

                String min = String.valueOf(getMin / (60 * 1000)); // 몫
                String second = String.valueOf((getMin % (60 * 1000)) / 1000); // 나머지
                String millis = String.valueOf((getMin % (60 * 1000)) % 1000); // 몫

                // 시간이 한자리면 0을 붙인다
                if (hour.length() == 1) {
                    hour = "0" + hour;
                }

                // 분이 한자리면 0을 붙인다
                if (min.length() == 1) {
                    min = "0" + min;
                }

                // 초가 한자리면 0을 붙인다
                // 분이 한자리면 0을 붙인다
                if (second.length() == 1) {
                    second = "0" + second;
                }

                count_view.setText(hour + ":" + min + ":" + second);
            }

            // 제한시간 종료시
            public void onFinish() {

                // 변경 후
                count_view.setText("촬영종료!");

                // 영상 저장
                // stop recording and release camera
                mediaRecorder.stop(); // stop the recording
                releaseMediaRecorder(); // release the MediaRecorder object
                Toast.makeText(MainActivity.this, "비디오가 저장되었습니다", Toast.LENGTH_LONG).show();
                recording = false;

                // 액티비티 종료
                finish();

            }
        }.start();

    }

    public void setListener() {

        // 스위치
        switchCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get the number of cameras
                if (!recording) {
                    int camerasNumber = Camera.getNumberOfCameras();
                    if (camerasNumber > 1) {
                        // release the old camera instance
                        // switch camera, from the front and the back and vice versa

                        releaseCamera();
                        chooseCamera();
                    } else {
                        Toast toast = Toast.makeText(myContext, "Sorry, your phone has only one camera!", Toast.LENGTH_LONG);
                        toast.show();
                    }
                }
            }
        });


        // 동영상 캡쳐 버튼
        // 한번 누르면 종료
        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recording) {

                    // 주어진 시간동안만 촬영할경우
                    // 현재 촬영중입니다. 메세지
                    Toast.makeText(MainActivity.this, "주어진 시간이 끝나면, 촬영이 종료됩니다.\n 저장을 원하시지않으면, 백버튼을 눌러주세요", Toast.LENGTH_LONG).show();

                    // TODO : 주어진 시간동안 촬영안할경우
                    // stop recording and release camera
                    // mediaRecorder.stop(); // stop the recording
                    // releaseMediaRecorder(); // release the MediaRecorder object
                    // Toast.makeText(MainActivity.this, "비디오가 저장되었습니다\n촬영을 종료합니다", Toast.LENGTH_LONG).show();
                    // recording = false;

                } else {
                    if (!prepareMediaRecorder()) {
                        // 촬영 준비가 되지않음
                        Toast.makeText(MainActivity.this, "Fail in prepareMediaRecorder()!\n - Ended -", Toast.LENGTH_LONG).show();
                        finish();
                    }
                    // work on UiThread for better performance
                    runOnUiThread(new Runnable() {
                        public void run() {
                            // If there are stories, add them to the table

                            try {

                                Toast.makeText(myContext, "촬영을 시작합니다", Toast.LENGTH_SHORT).show();

                                // 촬영 시작
                                mediaRecorder.start();

                                // 촬영 카운트 다운, 시간을 넣으면, 그 시간만큼 동작
                                countDown(get_value_2);

                            } catch (final Exception ex) {
                                // Log.i("---","Exception in thread");
                            }
                        }
                    });

                    recording = true;
                }
            }
        });

    }

    public void initView() {

        count_view = (TextView) findViewById(R.id.count_view);

    }

    private void bind(String get_value_2) {

        String getHour = get_value_2.substring(0, 2);
        String getMin = get_value_2.substring(2, 4);
        String getSecond = get_value_2.substring(4, 6);

        // 시간,분,초 설정
        count_view.setText(getHour + ":" + getMin + ":" + getSecond);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void initSet() {

        // 시간을 받아서 설정하구 화면표시
        // 재생버튼 클릭시 시간줄어들고
        // 다 줄어들면 자동으로 타이머 녹화끝, 저장
        // 중간에 다른것들 클릭 등, 중지 안됨

        // TODO : onCrate 등 액티비티 생성시 사용
        // 액티비티에서 값 가져오기

        get_value_1 = getIntent().getExtras().getString(KEY_1);
        get_value_2 = getIntent().getExtras().getString(KEY_2);
        get_value_3 = getIntent().getExtras().getString(KEY_3);
        get_value_4 = getIntent().getExtras().getString(KEY_4);
        get_value_5 = getIntent().getExtras().getString(KEY_5);
        get_value_6 = getIntent().getExtras().getString(KEY_6);


        if (get_value_1 == null) {
            throw new IllegalArgumentException("Must pass extra " + "get_value_1");
        }

        if (get_value_2 == null) {
            throw new IllegalArgumentException("Must pass extra " + "get_value_3");
        }

        if (get_value_3 == null) {
            throw new IllegalArgumentException("Must pass extra " + "get_value_4");
        }

        if (get_value_4 == null) {
            throw new IllegalArgumentException("Must pass extra " + "get_value_1");
        }

        if (get_value_5 == null) {
            throw new IllegalArgumentException("Must pass extra " + "get_value_6");
        }
        if (get_value_6 == null) {
            throw new IllegalArgumentException("Must pass extra " + "get_value_1");
        }

    }

    private int findFrontFacingCamera() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            CameraInfo info = new CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
                cameraFront = true;
                break;
            }
        }
        return cameraId;
    }

    private int findBackFacingCamera() {
        int cameraId = -1;
        // Search for the back facing camera
        // get the number of cameras
        int numberOfCameras = Camera.getNumberOfCameras();
        // for every camera check
        for (int i = 0; i < numberOfCameras; i++) {
            CameraInfo info = new CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i;
                cameraFront = false;
                break;
            }
        }
        return cameraId;
    }

    public void onResume() {
        super.onResume();
        if (!hasCamera(myContext)) {
            Toast toast = Toast.makeText(myContext, "Sorry, your phone does not have a camera!", Toast.LENGTH_LONG);
            toast.show();
            finish();
        }
        if (mCamera == null) {
            // if the front facing camera does not exist
            if (findFrontFacingCamera() < 0) {
                Toast.makeText(this, "No front facing camera found.", Toast.LENGTH_LONG).show();
                switchCamera.setVisibility(View.GONE);
            }
            mCamera = Camera.open(findBackFacingCamera());
            mCamera.setDisplayOrientation(90);
            mPreview.refreshCamera(mCamera);
        }
    }

    public void initialize() {

        cameraPreview = (LinearLayout) findViewById(R.id.camera_preview);
        mPreview = new CameraPreview(myContext, mCamera);
        cameraPreview.addView(mPreview);

        capture = (ImageView) findViewById(R.id.img_capture);

        switchCamera = (ImageView) findViewById(R.id.img_ChangeCamera);

        guide = (ImageView) findViewById(R.id.img_guide);

    }

    public void chooseCamera() {
        // if the camera preview is the front
        if (cameraFront) {
            int cameraId = findBackFacingCamera();
            if (cameraId >= 0) {
                // open the backFacingCamera
                // set a picture callback
                // refresh the preview

                mCamera = Camera.open(cameraId);
                mCamera.setDisplayOrientation(90);
                // mPicture = getPictureCallback();
                mPreview.refreshCamera(mCamera);
            }
        } else {
            int cameraId = findFrontFacingCamera();
            if (cameraId >= 0) {
                // open the backFacingCamera
                // set a picture callback
                // refresh the preview

                mCamera = Camera.open(cameraId);
                mCamera.setDisplayOrientation(90);
                // mPicture = getPictureCallback();
                mPreview.refreshCamera(mCamera);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // when on Pause, release camera in order to be used from other
        // applications
        releaseCamera();
    }

    private boolean hasCamera(Context context) {
        // check if the device has camera
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }

    boolean recording = false;


    private void releaseMediaRecorder() {
        if (mediaRecorder != null) {
            mediaRecorder.reset(); // clear recorder configuration
            mediaRecorder.release(); // release the recorder object
            mediaRecorder = null;
            mCamera.lock(); // lock camera for later use
        }
    }

    private boolean prepareMediaRecorder() {

        mediaRecorder = new MediaRecorder();

        mCamera.unlock();
        mediaRecorder.setCamera(mCamera);

        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_720P));

        // 파일경로 생성
        String path = getString(R.string.app_name);
        String fileName = "/" + System.currentTimeMillis() + ".mp4";
        String externalPath = getExternalPath(path);

        String address = externalPath + fileName;
        videoPath = address;

        BufferedOutputStream out = null;

        File dirFile = new File(externalPath);

        if (!dirFile.isDirectory()) {
            dirFile.mkdirs();
        }

        // before : "/sdcard/myvideo.mp4"
        mediaRecorder.setOutputFile(address);

        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.fromFile(dirFile)));

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

    private void releaseCamera() {
        // stop and release camera
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }
}
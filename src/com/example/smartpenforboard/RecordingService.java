package com.example.smartpenforboard;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.TimerTask;

/**
 * 录音的 Service
 *
 * Created by developerHaoz on 2017/8/12.
 */

public class RecordingService extends Service {

    private static final String LOG_TAG = "RecordingService";

    private String mFileName = null;
    private String mFilePath = null;

    private MediaRecorder mRecorder = null;
    private MyBinder mBinder = null;
    private long mStartingTimeMillis = 0;
    private long mElapsedMillis = 0;
    private TimerTask mIncrementTimerTask = null;
 public class MyBinder extends Binder{
     public RecordingService getService() {
         return RecordingService.this;
     }
	 
 }   
    
    
    @Override
    public IBinder onBind(Intent intent) { 
    	mBinder=new MyBinder();
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startRecording();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (mRecorder != null) {
            stopRecording();
        }
        super.onDestroy();
    }
/**
 * 
 * @param filename ：录音的文件名字，不包括文件后缀(.3gp)
 * @param isrecord :是否只是记录名字，不录音，主要用在笔迹还原
 */
    public void startRecording() {
        setFileNameAndPath();

        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFilePath);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mRecorder.setAudioChannels(1);
        mRecorder.setAudioSamplingRate(44100);
        mRecorder.setAudioEncodingBitRate(192000);

        try {
            mRecorder.prepare();
            mRecorder.start();
            mStartingTimeMillis = System.currentTimeMillis();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

//    public void setFileNameAndPath() {
//        int count = 0;
//        File f;
//
//        do {
//            count++;
//            mFileName = getString(R.string.default_file_name)
//                    + "_" + (System.currentTimeMillis()) + ".3gp";
//            mFilePath = Environment.getExternalStorageDirectory().getAbsolutePath();
//            mFilePath += "/SoundRecorder/" + mFileName;
//            f = new File(mFilePath);
//        } while (f.exists() && !f.isDirectory());
//    }
    public void setFileNameAndPath() {
        int count = 0;
        File f = null;
        
     
            count++;
//            mFileName = getString(R.string.default_file_name)
//                    + "_" + (System.currentTimeMillis()) + ".3gp";
            //固定录音文件的名字
            mFileName = "123"+".mp3";
            
            mFilePath = Environment.getExternalStorageDirectory().getAbsolutePath();
            mFilePath += "/xyz/" + mFileName;
            f = new File(mFilePath);
            if(f.exists())f.delete();
     }
    public void stopRecording() {
    	 mRecorder.reset();

         if ( mRecorder != null) {
             try {
            	 mRecorder.stop();
             } catch (IllegalStateException e) {
                 // TODO 如果当前java状态和jni里面的状态不一致，
                 //e.printStackTrace();
            	 mRecorder = null;
            	 mRecorder = new MediaRecorder();
             }
             mRecorder.release();
         }

        mElapsedMillis = (System.currentTimeMillis() - mStartingTimeMillis);
//        Log.e("zgm","1223: stopRecording-mElapsedMillis="+mElapsedMillis );
/*
        getSharedPreferences("sp_name_audio", MODE_PRIVATE)
                .edit()
                .putString("audio_path", mFilePath)
                .putLong("elpased", mElapsedMillis)
                .apply();*/
        if (mIncrementTimerTask != null) {
            mIncrementTimerTask.cancel();
            mIncrementTimerTask = null;
        }

        mRecorder = null;
    }
public String getMFilePath(){
	return mFilePath;
}
public String getMFileName(){
	return mFileName;
}
public long getMFileElpased(){
	return mElapsedMillis;
}
}


package com.bignerdranch.android.soundrecorder

import android.Manifest
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import java.io.File
import java.io.IOException
import java.nio.file.Files.delete
import java.nio.file.Files.exists


private const val LOG_TAG = "AudioRecordTest"
private const val REQUEST_RECORD_AUDIO_PERMISSION = 200


class MainActivity : AppCompatActivity() {


    private var mRecordButton: RecordButton? = null
    private var mPlayButton: PlayButton? = null
    private var mPlayer: MediaPlayer? = null
    private var mRecorder: MediaRecorder? = null
    private var mFileName: String = Environment.getExternalStorageDirectory().absolutePath + "/record.3gpp"

    private var permissionToRecordAccepted = false
    private var permissions: Array<String> = arrayOf(Manifest.permission.RECORD_AUDIO)

    override fun onRequestPermissionsResult( //Runtime permission!
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionToRecordAccepted = if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        } else {
            false
        }
        if (!permissionToRecordAccepted) finish()
    }


// Создаем свои кнопки и настраиваем OnClickListener макет будем создавать прямо в коде.
    internal inner class RecordButton(ctx: Context): Button(ctx){

    var mStartRecording = true

    var cliker: OnClickListener = OnClickListener {
        onRecord(mStartRecording)
        text = when(mStartRecording){
            true -> "Stop recording!"
            false -> "Start recording!"
        }
        mStartRecording = !mStartRecording
    }


    init {
        text = "Start recording!"
        setOnClickListener(cliker)
    }

}

    internal inner class PlayButton(ctx: Context): Button(ctx){

    var mStartPlaing = true

    var clicker: OnClickListener = OnClickListener {
        onPlay(mStartPlaing)

        text = when(mStartPlaing){
            true -> "Stop plaing"
            false -> "Start plaing"
        }

        mStartPlaing = !mStartPlaing
    }

    init{
        text = "Start plaing"
        setOnClickListener(clicker)
    }
}

    private fun onRecord(start: Boolean) = if(start){
            startRecording()
    }else{
            stopRecording()
    }

    private fun onPlay(start: Boolean) = if(start){
            startPlaying()
    }else{
        stopPlaying()
    }

// Тут мы просто управляем MediaPleer для воспроизведения записи из созданного при записи с микрофона файла.
    private fun startPlaying() {
        mPlayer = MediaPlayer().apply {
            try {
                setDataSource(mFileName)
                prepare()
                start()
            } catch (e: IOException) {
                Log.e(LOG_TAG, "prepare() failed")
            }
        }
    }

    private fun stopPlaying() {
        mPlayer?.release()
        mPlayer = null
    }

    private fun startRecording() {
        mRecorder = MediaRecorder()
        try{File(mFileName)}catch (e:Exception){e.printStackTrace()} // создаем файл для записи

            mRecorder?.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC) // запись будет с микрофона устройства
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP) // формат в коотором записываем 3gpp
            setOutputFile(mFileName)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB) //Sets the audio encoder to be used for recording. If this method is not called, the output file will not contain an audio track. Call this after setOutputFormat() but before prepare().

            try {
                prepare()
            } catch (e: IOException) {
                e.printStackTrace()
                Log.e(LOG_TAG, "prepare() failed")
            }

            mRecorder?.start()
        }
    }

    private fun stopRecording() {
        mRecorder?.stop()
        mRecorder?.release()
        mRecorder = null
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION)

        mRecordButton = RecordButton(this)
        mPlayButton = PlayButton(this)
// Так можно создать макет в коде!
        val mLinearLayout = LinearLayout(this).apply {
            addView(mRecordButton,
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    0f))
            addView(mPlayButton,
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    0f))
        }

        setContentView(mLinearLayout)

    }

    override fun onStop() {
        super.onStop()
        mRecorder?.release()
        mRecorder = null
        mPlayer?.release()
        mPlayer = null
    }

}

package com.bignerdranch.android.downloadimage

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import java.io.InputStream
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    lateinit var mButton: Button
    lateinit var mImageView: ImageView
    lateinit var mEditText: EditText
    var mURL:String ="https://pp.userapi.com/c840435/v840435135/75b2e/owE5dcoNaiY.jpg" // Адрес картинки по умолчанию


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mButton = findViewById(R.id.button)
        mImageView = findViewById(R.id.imageView)
        mEditText = findViewById(R.id.edit_text)

        // EditText для ввода собственного url
        mEditText.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    mURL = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {

            }


        })

        mButton.setOnClickListener {

        var myTask = DownloadImageTask()
        myTask.execute(mURL)// Загрузка вне UI реализована через AsyncTask
        }
    }

    inner class DownloadImageTask(): AsyncTask<String,Void,Bitmap>(){

    override fun doInBackground(vararg params: String?): Bitmap? {
        val urlAdress: String? = params[0]
        var mBitmap: Bitmap? = null

        try{
            val mInputStream: InputStream = java.net.URL(urlAdress).openStream() // Получаем поток байт с указанного url (не в UI)
            mBitmap = BitmapFactory.decodeStream(mInputStream) // Декодируем наш поток к Bitmap

        }catch (e:Exception){
            e.printStackTrace()
        }

        return mBitmap
    }

    override fun onPostExecute(result: Bitmap?) {
        super.onPostExecute(result)
        if(result != null){
            mImageView.setImageBitmap(result)// Устанавливаем скачанное изображение в mImageView в UI
        }
    }

}
}

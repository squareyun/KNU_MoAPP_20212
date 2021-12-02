package com.example.team_practice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Build
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import java.lang.Exception
import java.util.*


class MainCamera : AppCompatActivity(),SensorEventListener{
   private var imageView: ImageView? = null
    var sensorManager: SensorManager? = null
    var stepCountSensor: Sensor? = null
    var stepCountView: TextView? = null
    var currentSteps = 0
    var today : TextView? = null
    var now = Date()
    var dFormat :SimpleDateFormat? = null
    var temp = 0.000
    var KMS =0.000
    var distance: TextView? = null


    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.camera_main)

        stepCountView = findViewById(R.id.stepcount)
        distance = findViewById(R.id.distance)
        today = findViewById(R.id.date)

        dFormat = SimpleDateFormat("yyyy:MM:dd")
        today!!.text = dFormat!!.format(now).toString()

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) == PackageManager.PERMISSION_DENIED
        ) {
            requestPermissions(arrayOf(Manifest.permission.ACTIVITY_RECOGNITION), 0)
        }
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        stepCountSensor = sensorManager!!.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)

        if (stepCountSensor == null) {
            Toast.makeText(this, "No Step Sensor", Toast.LENGTH_SHORT).show()
        }
        imageView = findViewById(R.id.image)
        imageView?.setOnClickListener(View.OnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(intent, REQUEST_CODE)
        })
        val shareButton = findViewById<Button>(R.id.share)
        shareButton.setOnClickListener { ScreenShot() }
    }

    public override fun onStart() {
        super.onStart()
        if (stepCountSensor != null) {
        
            sensorManager!!.registerListener(
                this,
                stepCountSensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }
    override fun onSensorChanged(event: SensorEvent) {

        if (event.sensor.type == Sensor.TYPE_STEP_DETECTOR) {
            if (event.values[0] == 1.0f) {
                currentSteps++
                stepCountView!!.text = currentSteps.toString()
                temp = currentSteps.toDouble()
                KMS = temp * 60/1000000
                distance!!.text = KMS.toString()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                try {
                    val `in` = contentResolver.openInputStream(data!!.data!!)
                    val img = BitmapFactory.decodeStream(`in`)
                    `in`!!.close()
                    imageView!!.setImageBitmap(img)
                } catch (e: Exception) {
                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "사진 선택 취소", Toast.LENGTH_LONG).show()
            }
        }
    }

    companion object {
        private const val REQUEST_CODE = 0
    }

    fun ScreenShot() {
        val view = window.decorView.rootView
        view.isDrawingCacheEnabled = true


        val screenBitmap = Bitmap.createBitmap(view.drawingCache)
        try {
            val cachePath = File(applicationContext.cacheDir, "images")
            cachePath.mkdirs()
            val stream =
                FileOutputStream("$cachePath/image.png") // overwrites this image every time
            screenBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.close()
            val newFile = File(cachePath, "image.png")
            val contentUri = FileProvider.getUriForFile(
                applicationContext,
                "com.example.test.fileprovider", newFile
            )
            val Sharing_intent = Intent(Intent.ACTION_SEND)
            Sharing_intent.type = "image/png"
            Sharing_intent.putExtra(Intent.EXTRA_STREAM, contentUri)
            startActivity(Intent.createChooser(Sharing_intent, "Share image"))
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}

}

package com.example.team_practice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
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
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.lang.Exception
import java.util.*
import kotlin.math.round


class MainCamera : AppCompatActivity(){
   private var imageView: ImageView? = null
    var today : TextView? = null
    var now = Date()
    var dFormat :SimpleDateFormat? = null
    var temp = 0.000
    var KMS =0.000
    private lateinit var stepCount: TextView
    private lateinit var distance: TextView
    private lateinit var todayFrame: FrameLayout
    var Save : Button? = null

    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.camera_main)
        title = "Record"

        distance = findViewById(R.id.distance)
        today = findViewById(R.id.date)
        todayFrame = findViewById(R.id.todayFrame)

        SimpleDateFormat("yyyyMMdd").also { dFormat = it }
        today!!.text = dFormat!!.format(now).toString()


        imageView = findViewById(R.id.image)
        imageView?.setOnClickListener(View.OnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(intent, REQUEST_CODE)
        })

        stepCount = findViewById(R.id.stepCount)
        var firebaseDatabase = FirebaseDatabase.getInstance()
        var databaseReference = firebaseDatabase.getReference()

        var myWalkCntString : String? = ""
        databaseReference.child("user").child(MyData.ID).addValueEventListener( object : ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {
                myWalkCntString = snapshot.child("walkCnt").getValue(String::class.java)
                stepCount.text = myWalkCntString + "\uD83D\uDC63"
                val step = myWalkCntString!!.toDouble() / 100000
                distance.text = (round(step * 100) / 100).toString() + "km"
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
  
   Save = findViewById<Button>(R.id.SAVE)
        Save?.setOnClickListener {
            val rootView = todayFrame
            val screenShot = ScreenShot(rootView)
            if (screenShot != null) {
                sendBroadcast(
                    Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,Uri.fromFile(screenShot)))
                Toast.makeText(applicationContext, "오늘의 기록이 저장되었어요.", Toast.LENGTH_SHORT).show()
                Toast.makeText(applicationContext, "오늘 하루도 고생했어요😁", Toast.LENGTH_SHORT).show()
            }
        }
    }

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
                Toast.makeText(this, "사진 선택을 취소했어요.", Toast.LENGTH_LONG).show()
            }
        }
    }

    companion object {
        private const val REQUEST_CODE = 0
    }

    
      fun ScreenShot(view: View): File? {
        view.isDrawingCacheEnabled = true
        val screenBitmap = view.drawingCache
        val timestamp = java.text.SimpleDateFormat("yyyyMMddHHmmss").format(Date())
        val filename = "walkwalk_$timestamp.png"
        val file = File(
            Environment.getExternalStorageDirectory().toString() + "/Pictures",
            filename
        )
        var os: FileOutputStream? = null
        try {
            os = FileOutputStream(file)
            screenBitmap.compress(Bitmap.CompressFormat.PNG, 90, os)
            os.close()
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
        view.isDrawingCacheEnabled = false
        return file
    }
    //    로그아웃 구현
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        var mInflater = menuInflater
        mInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.itemLogout ->{
                android.app.AlertDialog.Builder(this /* 해당 액티비티를 가르킴 */)
                    .setTitle("로그아웃").setMessage("로그아웃 하시겠습니까?")
                    .setPositiveButton(
                        "로그아웃",
                        DialogInterface.OnClickListener { dialog, whichButton ->
                            val i = Intent(
                                this  /*현재 액티비티 위치*/,
                                LoginActivity::class.java /*이동 액티비티 위치*/
                            )
                            i.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(i)

                            val sharedPreferences = getSharedPreferences("setting", Context.MODE_PRIVATE)
                            val editor = sharedPreferences.edit()

                            editor.clear()
                            editor.apply()
                        })
                    .setNegativeButton("취소",
                        DialogInterface.OnClickListener { dialog, whichButton -> })
                    .show()

            }
        }
        return true
    }
//    로그아웃 구현
}

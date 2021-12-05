package com.example.team_practice

import android.Manifest
import android.app.AlertDialog
import android.content.*
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.UiThread
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.material.snackbar.Snackbar
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.util.MarkerIcons

import com.naver.maps.map.MapFragment
import kotlin.random.Random


class MainMap : AppCompatActivity(), OnMapReadyCallback, SensorEventListener {
    private lateinit var naverMap: NaverMap
    private lateinit var locationSource: FusedLocationSource
    private lateinit var moveToKnuBtn: Button

    private var sensorManager: SensorManager? = null
    private var running = false
    private var totalSteps = 0f
    private var previousTotalSteps = 0f

    private val knuLocations = ArrayList<KnuLocation>()
    private val missonLocations = ArrayList<KnuLocation>()

    private val markers = mutableListOf<Marker>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.map_main)
        title = "Walk Walk"

        // Adding a context of SENSOR_SERVICE aas Sensor Manager
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        // FusedLocationSource 등록
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)

        setLocationList()

        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map, it).commit()
            }

        mapFragment!!.getMapAsync(this)

        moveToKnu()

        loadData()
        resetSteps()


        // 날짜 바뀌면 마커 바뀌는 작업
        //  val intentFilter = IntentFilter(Intent.ACTION_TIME_TICK)
        val intentFilter = IntentFilter(Intent.ACTION_DATE_CHANGED)
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                Log.i("마커 변경", "receive : ${intent?.action}")

                // 기존 마커 해제
                markers.forEach { marker ->
                    marker.map = null
                }
                markers.clear()
                missonLocations.clear()

                //미션 장소 데이터 등록
                makeMissionLocationList()

                // 마커 등록
                setMarker();
            }
        }
        registerReceiver(receiver, intentFilter)
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
                AlertDialog.Builder(this /* 해당 액티비티를 가르킴 */)
                    .setTitle("로그아웃").setMessage("로그아웃 하시겠습니까?")
                    .setPositiveButton(
                        "로그아웃",
                        DialogInterface.OnClickListener { dialog, whichButton ->
                            val i = Intent(
                                this@MainMap  /*현재 액티비티 위치*/,
                                LoginActivity::class.java /*이동 액티비티 위치*/
                            )
                            i.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                            startActivity(i)
                        })
                    .setNegativeButton("취소",
                        DialogInterface.OnClickListener { dialog, whichButton -> })
                    .show()

            }
        }
        return true
    }
//    로그아웃 구현

    @UiThread
    override fun onMapReady(map: NaverMap) {
        naverMap = map

        // 줌 최대 최소 범위 지정
        naverMap.maxZoom = 18.0
        naverMap.minZoom = 10.0

        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)

        // 지도 기본 위치
        val cameraUpdate = CameraUpdate.scrollTo(LatLng(35.88880359446379, 128.61028951367845))
        naverMap.moveCamera(cameraUpdate)

        // default 버튼 관련 옵션
        val uiSetting = naverMap.uiSettings
        uiSetting.isLocationButtonEnabled = true
        uiSetting.isCompassEnabled = true
        uiSetting.isTiltGesturesEnabled = false

        naverMap.locationSource = locationSource

        //미션 장소 데이터 등록
        makeMissionLocationList()

        // 마커 등록
        setMarker();
    }

    private fun resetSteps() {
        var stepCount = findViewById<TextView>(R.id.stepCount)
        stepCount.setOnClickListener {
            Toast.makeText(this, "Long tap to reset steps", Toast.LENGTH_SHORT).show()
        }

        stepCount.setOnLongClickListener {
            previousTotalSteps = totalSteps
            stepCount.text = 0.toString()
            saveData()
            true
        }
    }

    private fun saveData() {
        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)

        val editor = sharedPreferences.edit()
        editor.putFloat("key1", previousTotalSteps)
        editor.apply()
    }

    private fun loadData() {
        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val savedNumber = sharedPreferences.getFloat("key1", 0f)

        previousTotalSteps = savedNumber
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // We do not have to write anything in this function for this app
    }

    private fun setMarker() {
        // 정보창 관련 작업
        val infoWindow = InfoWindow()
        infoWindow.adapter = object : InfoWindow.DefaultTextAdapter(applicationContext) {
            override fun getText(infoWindow: InfoWindow): CharSequence {
                return infoWindow.marker?.tag as CharSequence? ?: ""
            }
        }

        missonLocations!!.forEach {
            markers += Marker().apply {
                position = LatLng(it.latitude, it.longitude)
                width = Marker.SIZE_AUTO
                height = Marker.SIZE_AUTO
                icon = MarkerIcons.BLACK
                tag = it.name
                setOnClickListener {
                    infoWindow.open(this)
                    true
                }
            }
        }

        markers.forEach { marker ->
            marker.map = naverMap
        }

        // 지도를 클릭하면 정보 창을 닫음
        naverMap.setOnMapClickListener { _, _ ->
            infoWindow.close()
        }
    }

    private fun moveToKnu() {
        moveToKnuBtn = findViewById(R.id.moveToKnuBtn)
        moveToKnuBtn.setOnClickListener {
            val cameraUpdate = CameraUpdate.scrollAndZoomTo(
                LatLng(35.88880359446379, 128.61028951367845),
                14.0
            )
                .animate(CameraAnimation.Fly)
            naverMap.moveCamera(cameraUpdate)
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (locationSource.onRequestPermissionsResult(
                requestCode, permissions,
                grantResults
            )
        ) {
            if (!locationSource.isActivated) { // 권한 거부됨
                naverMap.locationTrackingMode = LocationTrackingMode.None
            }
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        var stepCount = findViewById<TextView>(R.id.stepCount)

        if (running) {
            totalSteps = event!!.values[0]

            val currentSteps = totalSteps.toInt() - previousTotalSteps.toInt()

            stepCount.text = ("$currentSteps")
        }
    }

    override fun onResume() {
        super.onResume()

        running = true

        val stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if (stepSensor == null) {
            Toast.makeText(this, "No sensor detected on this device", Toast.LENGTH_SHORT).show()
        } else {
            sensorManager?.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
        }

    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }

    data class KnuLocation(
        val latitude: Double,
        val longitude: Double,
        val name: String,
    )

    private fun setLocationList() {
        knuLocations.add(KnuLocation(35.887459016289945, 128.60852113361165, "공대 1호관"))
        knuLocations.add(KnuLocation(35.887924046584786, 128.6085425912978, "공대 2호관"))
        knuLocations.add(KnuLocation(35.88760243713861, 128.60965302578714, "공대 3호관"))
        knuLocations.add(KnuLocation(35.88756766847161, 128.61064544308437, "공대 4호관"))
        knuLocations.add(KnuLocation(35.88717651990859, 128.61162713163077, "IT대학 2호관"))
        knuLocations.add(KnuLocation(35.887189558226744, 128.60962620372104, "공대 6호관"))
        knuLocations.add(KnuLocation(35.88716782769834, 128.6106776296143, "공대 7호관"))
        knuLocations.add(KnuLocation(35.886594139587984, 128.6116432248488, "공대 8호관"))
        knuLocations.add(KnuLocation(35.88676363878973, 128.60848358268686, "공대 9호관"))
        knuLocations.add(KnuLocation(35.88733496590512, 128.61271498257793, "IT대학 1호관"))
        knuLocations.add(KnuLocation(35.888151882550034, 128.61073808258269, "IT대학 4호관"))
        knuLocations.add(KnuLocation(35.88826117098753, 128.61035405512777, "IT대학 3호관"))
        knuLocations.add(KnuLocation(35.88833505373963, 128.61008046981883, "공대 12호관"))
        knuLocations.add(KnuLocation(35.88805320883473, 128.61142374237969, "IT융복합공학관"))
    }

    fun makeMissionLocationList() {
        val random = java.util.Random()
        var numList = mutableListOf<Int>()

        while (numList.size < 5) {
            val randomNum = random.nextInt(knuLocations.size)
            if (numList.contains(randomNum))
                continue
            numList.add(randomNum)
        }

        val it = numList.iterator()
        while (it.hasNext()) {
            missonLocations.add(knuLocations.get(it.next()))
        }
    }
}
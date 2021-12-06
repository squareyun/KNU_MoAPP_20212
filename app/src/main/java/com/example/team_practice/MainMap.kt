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
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.annotation.UiThread
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import com.google.android.material.snackbar.Snackbar
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.util.MarkerIcons

import com.naver.maps.map.MapFragment
import kotlin.math.sqrt
import kotlin.random.Random


class MainMap : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var naverMap: NaverMap
    private lateinit var moveToKnuBtn: Button
    private lateinit var locationSource: FusedLocationSource
    private var numList = mutableListOf<Int>()

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }

    private var sensorManager: SensorManager? = null
    private lateinit var sensor: Sensor
    private var magnitudePrevious = 0.0
    private var stepCount = 0


    private val knuLocations = ArrayList<KnuLocation>()
    private val missonLocations = ArrayList<KnuLocation>()

    private val markers = mutableListOf<Marker>()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.map_main)
        title = "Walk Walk"

        locationSource =
            FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)

        // Adding a context of SENSOR_SERVICE aas Sensor Manager
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        sensor = sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        val stepDetector: SensorEventListener = object : SensorEventListener {
            override fun onSensorChanged(sensorEvent: SensorEvent) {
                if (sensorEvent != null) {
                    val x_acceleration = sensorEvent.values[0]
                    val y_acceleration = sensorEvent.values[1]
                    val z_acceleration = sensorEvent.values[2]
                    val magnitude =
                       sqrt((x_acceleration * x_acceleration + y_acceleration * y_acceleration + z_acceleration * z_acceleration).toDouble())
                    val magnitudeDelta = magnitude - magnitudePrevious
                    magnitudePrevious = magnitude
                    if (magnitudeDelta > 15) {
                        stepCount++
                    }
                    var stepCountView = findViewById<TextView>(R.id.stepCountView)
                    stepCountView.text = stepCount.toString()
                } else {
                    Toast.makeText(applicationContext, "만보기를 위한 센서를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onAccuracyChanged(sensor: Sensor, i: Int) {}
        }
        sensorManager!!.registerListener(stepDetector, sensor, SensorManager.SENSOR_DELAY_NORMAL)

        setLocationList()

        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map, it).commit()
            }

        mapFragment!!.getMapAsync(this)

        moveToKnu()

        // 날짜 바뀌면 마커 바뀌는 작업
          val intentFilter = IntentFilter(Intent.ACTION_TIME_TICK)
//        val intentFilter = IntentFilter(Intent.ACTION_DATE_CHANGED)
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
        when (item.itemId) {
            R.id.itemLogout -> {
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

    @UiThread
    override fun onMapReady(map: NaverMap) {
        naverMap = map
        naverMap.locationSource = locationSource
        naverMap.addOnLocationChangeListener { location ->
            Toast.makeText(this, "${location.latitude}, ${location.longitude}",
                Toast.LENGTH_SHORT).show()
        }

        // 줌 최대 최소 범위 지정
        naverMap.maxZoom = 18.0
        naverMap.minZoom = 10.0

        // 지도 기본 위치
        val cameraUpdate = CameraUpdate.scrollTo(LatLng(35.88880359446379, 128.61028951367845))
        naverMap.moveCamera(cameraUpdate)

        // default 버튼 관련 옵션
        val uiSetting = naverMap.uiSettings
        uiSetting.isLocationButtonEnabled = true
        uiSetting.isCompassEnabled = true
        uiSetting.isTiltGesturesEnabled = false

        //미션 장소 데이터 등록
        makeMissionLocationList()

        // 마커 등록
        setMarker();
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

    override fun onPause() {
        super.onPause()
        val sharedPreferences = getPreferences(MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.putInt("stepCount", stepCount)
        editor.apply()
    }

    override fun onStop() {
        super.onStop()
        val sharedPreferences = getPreferences(MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.putInt("stepCount", stepCount)
        editor.apply()
    }


    override fun onResume() {
        super.onResume()

        val sharedPreferences = getPreferences(MODE_PRIVATE)
        stepCount = sharedPreferences.getInt("stepCount", 0)

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

    data class KnuLocation(
        val latitude: Double,
        val longitude: Double,
        val name: String,
    )

    private fun setLocationList() {
        // total: 65
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
        knuLocations.add(KnuLocation(35.88646247465816, 128.61490608820674, "KNU센트럴파크"))
        knuLocations.add(KnuLocation(35.886671089344325, 128.61316801680104, "수의과대학"))
        knuLocations.add(KnuLocation(35.88651028224115, 128.61262084613773, "창업보육센터"))
        knuLocations.add(KnuLocation(35.88684928062104, 128.6107003844785, "화목관"))
        knuLocations.add(KnuLocation(35.88643205164111, 128.61068429122503, "협동관"))
        knuLocations.add(KnuLocation(35.88690143408896, 128.60989572180506, "봉사관"))
        knuLocations.add(KnuLocation(35.88685362674469, 128.609412924201, "진리관"))
        knuLocations.add(KnuLocation(35.886640666405874, 128.6096489585852, "긍지관"))
        knuLocations.add(KnuLocation(35.88621474400703, 128.60964895856355, "문화관"))
        knuLocations.add(KnuLocation(35.8866493586704, 128.6084473289487, "화학관"))
        knuLocations.add(KnuLocation(35.88616693624587, 128.60852243079822, "성실관"))
        knuLocations.add(KnuLocation(35.8872013158625, 128.60783578527122, "연구실안전관리센터"))
        knuLocations.add(KnuLocation(35.88695793364532, 128.60608698498893, "생물학관"))
        knuLocations.add(KnuLocation(35.88717322984431, 128.60492091267042, "학군단"))
        knuLocations.add(KnuLocation(35.88833636728088, 128.60433138658894, "백호관"))
        knuLocations.add(KnuLocation(35.88742615756613, 128.6055548303968, "야구장"))
        knuLocations.add(KnuLocation(35.88757604113011, 128.60638415290677, "축구장"))
        knuLocations.add(KnuLocation(35.888459831561995, 128.60758986028998, "농구장"))
        knuLocations.add(KnuLocation(35.88851668323955, 128.6079853833282, "축구장"))
        knuLocations.add(KnuLocation(35.88876476280895, 128.60903160558803, "테니스장"))
        knuLocations.add(KnuLocation(35.887642828929465, 128.61563678214594, "로스쿨"))
        knuLocations.add(KnuLocation(35.88861448020615, 128.61371658157117, "박물관"))
        knuLocations.add(KnuLocation(35.88860931188914, 128.61568143797518, "사회과학대학"))
        knuLocations.add(KnuLocation(35.889089964787566, 128.61570695559055, "경상대학"))
        knuLocations.add(KnuLocation(35.88943107152611, 128.6149350477256, "제4합동강의동"))
        knuLocations.add(KnuLocation(35.88996340183931, 128.6157771290373, "생활과학대학"))
        knuLocations.add(KnuLocation(35.890268327308185, 128.6137420991603, "사범대학"))
        knuLocations.add(KnuLocation(35.89056291519987, 128.6151583268355, "향토생활관"))
        knuLocations.add(KnuLocation(35.89085233383297, 128.61441193659385, "언어교육센터"))
        knuLocations.add(KnuLocation(35.89123477823054, 128.61476918320903, "첨성관"))
        knuLocations.add(KnuLocation(35.891281291612344, 128.6135251994597, "정보화본부"))
        knuLocations.add(KnuLocation(35.89241310881879, 128.61372934038, "융합학부"))
        knuLocations.add(KnuLocation(35.89188596309891, 128.61091602315574, "글로벌프라자"))
        knuLocations.add(KnuLocation(35.891363982011526, 128.61198138363565, "중앙도서관구관"))
        knuLocations.add(KnuLocation(35.89169474270429, 128.61260656521222, "중앙도서관신관"))
        knuLocations.add(KnuLocation(35.89242861306188, 128.61230673324175, "약학대학"))
        knuLocations.add(KnuLocation(35.8934208776624, 128.61117757873157, "예술대학"))
        knuLocations.add(KnuLocation(35.89251647033473, 128.61077567628948, "대강당"))
        knuLocations.add(KnuLocation(35.89331751736072, 128.61349330238826, "누리관"))
        knuLocations.add(KnuLocation(35.89138465457643, 128.60858754068016, "농대3호관"))
        knuLocations.add(KnuLocation(35.891115910566306, 128.60829408810338, "농대2호관"))
        knuLocations.add(KnuLocation(35.89093502465941, 128.60953807184265, "농대1호관"))
        knuLocations.add(KnuLocation(35.890671447308414, 128.60727976279873, "복현회관"))
        knuLocations.add(KnuLocation(35.890144289975254, 128.60659716658117, "자연과학대학"))
        knuLocations.add(KnuLocation(35.889684316504535, 128.60639940506204, "제2과학관"))
        knuLocations.add(KnuLocation(35.889725662543775, 128.6077837356959, "제1과학관"))
        knuLocations.add(KnuLocation(35.8896998212735, 128.60900858125984, "생명공학관"))
        knuLocations.add(KnuLocation(35.88958611957051, 128.60514904186064, "제2체육관"))
        knuLocations.add(KnuLocation(35.88903311353425, 128.60472162180318, "제1체육관"))
        knuLocations.add(KnuLocation(35.890307316262806, 128.61206198217036, "본관"))
        knuLocations.add(KnuLocation(35.89130687477114, 128.61071551330116, "인문대학"))
    }

    fun makeMissionLocationList() {
        val random = java.util.Random()
        var tempNumList = mutableListOf<Int>()

        while (tempNumList.size < 5) {
            val randomNum = random.nextInt(knuLocations.size)
            if (tempNumList.contains(randomNum) || numList.contains(randomNum))
                continue
            tempNumList.add(randomNum)
        }
        numList = tempNumList

        val it = tempNumList.iterator()
        while (it.hasNext()) {
            missonLocations.add(knuLocations.get(it.next()))
        }
    }
}
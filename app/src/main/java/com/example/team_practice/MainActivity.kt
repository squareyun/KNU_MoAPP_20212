package com.example.team_practice

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.app.ActionBar
import android.widget.PopupWindow
import java.net.IDN

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var loginBtn = findViewById<Button>(R.id.loginBtn)
        var ID       = findViewById<EditText>(R.id.loginID)
        var PW       = findViewById<EditText>(R.id.loginPW)

        loginBtn.setOnClickListener {
            var intent = Intent(applicationContext, FriendListActivity::class.java)
            startActivity(intent)
        }
    }
}

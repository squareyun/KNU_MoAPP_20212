package com.example.team_practice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase

class MainFriendList : AppCompatActivity() {

    var isFirst : Boolean = true
    var isChanged : Boolean = false;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.friend_main)

        var intent = intent
        var userID = intent.getStringExtra("userID")
        MyData.name = userID
        Toast.makeText(applicationContext, userID, Toast.LENGTH_SHORT).show()

        var firebaseDatabase = FirebaseDatabase.getInstance()
        var myRef = firebaseDatabase.getReference()

        myRef.child("test").setValue("team test")

        var myList = findViewById<ListView>(R.id.myListView)
        var friendList = findViewById<ListView>(R.id.friendListView)

        var myAdapter = FriendListItemAdapter()
        var friendAdapter = FriendListItemAdapter()

        myAdapter.addItem(FriendListItem("박휘성", "현재 걸음 수 : " + "717"))
        myList.adapter = myAdapter

        for(i in 1..50)
            friendAdapter.addItem(FriendListItem("친구" + i, "현재 걸음 수 : " + (i*10).toString()))
        friendList.adapter = friendAdapter

    }
}
























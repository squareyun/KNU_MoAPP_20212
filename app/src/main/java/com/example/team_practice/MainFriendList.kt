package com.example.team_practice

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainFriendList : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.friend_main)

        var myList = findViewById<ListView>(R.id.myListView)
        var friendList = findViewById<ListView>(R.id.friendListView)

        var myAdapter = FriendListItemAdapter()
        var friendAdapter = FriendListItemAdapter()

        myAdapter.addItem(FriendListItem(R.drawable.ic_launcher, "박휘성", "현재 걸음 수 : " + "717"))
        myList.adapter = myAdapter

        for(i in 1..50)
            friendAdapter.addItem(FriendListItem(R.drawable.ic_launcher, "친구" + i, "현재 걸음 수 : " + (i*10).toString()))
        friendList.adapter = friendAdapter

        friendList.onItemClickListener = AdapterView.OnItemClickListener{parent, v, position, id ->

            val item = parent.getItemAtPosition(position) as FriendListItem

            Toast.makeText(this, item.getName() + " 과 대화", Toast.LENGTH_SHORT).show()
        }
    }
}
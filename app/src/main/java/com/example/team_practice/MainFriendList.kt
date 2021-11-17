package com.example.team_practice

import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class MainFriendList : AppCompatActivity(){
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
    }
}
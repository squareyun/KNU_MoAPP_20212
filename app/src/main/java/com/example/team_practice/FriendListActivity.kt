package com.example.team_practice

import android.app.TabActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

@Suppress("deprecstion")
class FriendListActivity : TabActivity() {

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.friend_list)

        var tabHost = this.tabHost

        var tabSpecMap = tabHost.newTabSpec("MAP").setIndicator("MAP")
        tabSpecMap.setContent(R.id.map)
        tabHost.addTab(tabSpecMap)

        var tabSpecFriend = tabHost.newTabSpec("Friend").setIndicator("Friend")
        tabSpecFriend.setContent(R.id.friend)
        tabHost.addTab(tabSpecFriend)

        var tabSpecRecord = tabHost.newTabSpec("RECORD").setIndicator("RECORD")
        tabSpecRecord.setContent(R.id.record)
        tabHost.addTab(tabSpecRecord)

        tabHost.currentTab = 0

        var myList = findViewById<ListView>(R.id.myListView)
        var friendList = findViewById<ListView>(R.id.friendListView)

        var myAdapter : FriendListItemAdapter = FriendListItemAdapter()
        var friendAdapter : FriendListItemAdapter = FriendListItemAdapter()

        myAdapter.addItem(FriendListItem(R.drawable.ic_launcher, "박휘성", "현재 걸음 수 : " + "717"))
        myList.adapter = myAdapter

        for(i in 1..50)
            friendAdapter.addItem(FriendListItem(R.drawable.ic_launcher, "친구" + i, "현재 걸음 수 : " + (i*10).toString()))
        friendList.adapter = friendAdapter

    }
}
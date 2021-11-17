package com.example.team_practice

import android.app.TabActivity
import android.content.Intent
import android.os.Bundle

@Suppress("deprecation")
class FriendListActivity : TabActivity() {

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tab_adaptor)

        var tabHost = this.tabHost

        var map_intent = Intent(applicationContext, MainMap::class.java)
        var tabSpecMap = tabHost.newTabSpec("MAP").setIndicator("MAP")
        tabSpecMap.setContent(map_intent)
        tabHost.addTab(tabSpecMap)

        var friendList_intent = Intent(applicationContext, MainFriendList::class.java)
        var tabSpecFriend = tabHost.newTabSpec("Friend").setIndicator("Friend")
        tabSpecFriend.setContent(friendList_intent)
        tabHost.addTab(tabSpecFriend)

        var camera_intent = Intent(applicationContext, MainCamera::class.java)
        var tabSpecRecord = tabHost.newTabSpec("RECORD").setIndicator("RECORD")
        tabSpecRecord.setContent(camera_intent)
        tabHost.addTab(tabSpecRecord)

        tabHost.currentTab = 0

    }
}
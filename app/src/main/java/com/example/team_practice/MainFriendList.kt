package com.example.team_practice

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*


class MainFriendList : AppCompatActivity() {

    var isFirst : Boolean = true
    var isChanged : Boolean = false;

    var myAdapter = FriendListItemAdapter()
    var friendAdapter = FriendListItemAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.friend_main)

        var myList = findViewById<ListView>(R.id.myListView)
        var friendList = findViewById<ListView>(R.id.friendListView)

        var firebaseDatabase = FirebaseDatabase.getInstance()
        var databaseReference = firebaseDatabase.getReference()

        var myName : String? = ""
        var myWalkCntString : String? = ""

        databaseReference.child("user").child(MyData.ID).addValueEventListener( object : ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {
                myName = snapshot.child("userName").getValue(String::class.java)
                myWalkCntString = snapshot.child("walkCnt").getValue(String::class.java)

                myAdapter.addItem(FriendListItem(myName, "현재 걸음 수 : " + myWalkCntString))
                myList.adapter = myAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        databaseReference.child("user").child(MyData.ID).child("friendList").addValueEventListener( object : ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {
                for(childSnap in snapshot.children){
                    var friendName = childSnap.child("friendName").getValue(String::class.java)
                    var friendWalkCnt = childSnap.child("friendWalkCnt").getValue(String::class.java)
                    friendAdapter.addItem(FriendListItem(friendName, "현재 걸음 수 : " + friendWalkCnt))
                }
                friendList.adapter = friendAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }
    //    로그아웃 구현
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        var mInflater = menuInflater
        mInflater.inflate(R.menu.menu_friend, menu)
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
            R.id.itemFriendAdd ->{
                var dialogView = View.inflate(this, R.layout.friend_find, null)
                AlertDialog.Builder(this /* 해당 액티비티를 가르킴 */)
                    .setTitle("친구찾기").setMessage("아이디를 입력해주세요.")
                    .setView(dialogView)
                    .setPositiveButton(
                        "검색",
                        DialogInterface.OnClickListener { dialog, whichButton ->
                            var findingID = dialogView.findViewById<EditText>(R.id.findingID)

                            if(findingID.text.toString().equals("")) {
                                Toast.makeText(applicationContext, "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show()
                                return@OnClickListener
                            }
                            else if(findingID.text.toString().equals(MyData.ID)){
                                Toast.makeText(applicationContext, "본인의 아이디입니다.", Toast.LENGTH_SHORT).show()
                                findingID.setText("")
                                return@OnClickListener
                            }

                            var find : Boolean = false

                            var firebaseDatabase = FirebaseDatabase.getInstance()
                            var databaseReference = firebaseDatabase.getReference()

                            databaseReference.child("user").addValueEventListener( object : ValueEventListener{

                                override fun onDataChange(snapshot: DataSnapshot) {
                                    for(childSnap in snapshot.children){
                                        var newFriendID = childSnap.key

                                        if(findingID.text.toString().equals(newFriendID)){
//                                            Toast.makeText(applicationContext, newFriendID + "exist.", Toast.LENGTH_SHORT).show()
                                            var newFriendName = childSnap.child("userName").getValue(String::class.java)
                                            var newFriendWalkCnt = childSnap.child("walkCnt").getValue(String::class.java)
                                            friendAdapter.addItem(FriendListItem(newFriendName, "현재 걸음 수 : " + newFriendWalkCnt))
                                            friendAdapter.items.sortBy { it.name }
                                            friendAdapter.notifyDataSetChanged()
                                            break;
                                        }
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    TODO("Not yet implemented")
                                }
                            })


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
























package com.example.team_practice

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.time.format.DateTimeFormatter
import java.time.LocalDateTime

class ChatActivity : AppCompatActivity() {

    lateinit var chatInput : EditText
    lateinit var sendBtn : Button
    lateinit var chatListView : ListView
    lateinit var adapter : ChatAdapter
    var chatItems = ArrayList<ChatItem>()

    @RequiresApi(Build.VERSION_CODES.O)
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH시 mm")

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_layout)

        var actionBar = supportActionBar
        actionBar!!.hide()

        chatInput = findViewById(R.id.chatInput)
        chatListView = findViewById(R.id.chatListView)
        sendBtn = findViewById(R.id.sendBtn)
        adapter = ChatAdapter(chatItems, getLayoutInflater())
        chatListView.adapter = adapter

        var myID = MyData.ID
        var otherID = intent.getStringExtra("otherID")
        var otherName = intent.getStringExtra("otherName")

        var chatViewName : TextView = findViewById(R.id.chatViewName)
        chatViewName.setText(otherName)

        var arrayForSort : ArrayList<String> = ArrayList()

        arrayForSort.add(myID)
        arrayForSort.add(otherID!!)
        arrayForSort.sort()

        var chatName = arrayForSort[0] + "_" + arrayForSort[1]

        var firebaseDatabase = FirebaseDatabase.getInstance()
        var databaseReference = firebaseDatabase.getReference()

        sendBtn.setOnClickListener {

            if(chatInput.text.toString().equals(""))
                return@setOnClickListener

            var time = LocalDateTime.now().format(formatter).toString()
            var newItem : ChatItem = ChatItem(MyData.ID, MyData.NAME, chatInput.text.toString(), time)
            databaseReference.child("chat").child(chatName).push().setValue(newItem)
            chatInput.setText("")

        }

        databaseReference.child("chat").child(chatName).addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

                var chatItem = snapshot.getValue(ChatItem::class.java)

                chatItems.add(chatItem!!)

                adapter.notifyDataSetChanged()
                chatListView.setSelection(chatItems.size - 1)

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

}






























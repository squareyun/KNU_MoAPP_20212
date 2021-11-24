package com.example.team_practice

import android.os.Bundle
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ChatActivity : AppCompatActivity() {

    lateinit var chatInput : EditText
    lateinit var chatListView : ListView
    lateinit var adapter : ChatAdapter
    var messageItems = ArrayList<ChatItem>()

    var firebaseDatabase : FirebaseDatabase = FirebaseDatabase.getInstance()
    var chatRed : DatabaseReference = firebaseDatabase.getReference("chat");

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_layout)

        chatInput = findViewById(R.id.chatInput)
        chatListView = findViewById(R.id.chatListView)
        adapter = ChatAdapter(messageItems, getLayoutInflater())
        chatListView.adapter = adapter
    }
}
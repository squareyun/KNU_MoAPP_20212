package com.example.team_practice

import android.os.Bundle
import android.view.View
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
    var chatItems = ArrayList<ChatItem>()

    var firebaseDatabase : FirebaseDatabase = FirebaseDatabase.getInstance()
    var chatRef : DatabaseReference = firebaseDatabase.getReference("chat");

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_layout)

        chatInput = findViewById(R.id.chatInput)
        chatListView = findViewById(R.id.chatListView)
        adapter = ChatAdapter(chatItems, getLayoutInflater())
        chatListView.adapter = adapter

        chatRef.addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

                var chatItem = snapshot.getValue(ChatItem::class.java)!!

                chatItems.add(chatItem)

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

    fun clickSend(view : View){


    }
}






























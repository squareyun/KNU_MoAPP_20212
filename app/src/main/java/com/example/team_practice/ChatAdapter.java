package com.example.team_practice;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ChatAdapter extends BaseAdapter {

    ArrayList<ChatItem> chatItems;
    LayoutInflater layoutInflater;

    public ChatAdapter(ArrayList<ChatItem> chatItems, LayoutInflater layoutInflater) {
        this.chatItems = chatItems;
        this.layoutInflater = layoutInflater;
    }

    @Override
    public int getCount() {
        return chatItems.size();
    }

    @Override
    public Object getItem(int i) {
        return chatItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ChatItem item = chatItems.get(i);

        View itemView = null;

        if(item.getName().equals(MyData.ID)){
            itemView = layoutInflater.inflate(R.layout.chat_right_item, viewGroup, false);
        }
        else{
            itemView = layoutInflater.inflate(R.layout.chat_left_item, viewGroup, false);
        }

        TextView name = itemView.findViewById(R.id.chatItemName);
        TextView message = itemView.findViewById(R.id.chatItemMsg);
        TextView time = itemView.findViewById(R.id.chatItemTime);

        name.setText(item.getName());
        message.setText(item.getMessage());
        time.setText(item.getTime());

        return itemView;
    }
}



































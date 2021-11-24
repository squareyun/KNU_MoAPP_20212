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

        ImageView img;
        TextView name;
        TextView message;
        TextView time;

        if(item.getName().equals(MyData.name)){
            itemView = layoutInflater.inflate(R.layout.chat_right_item, viewGroup, false);
            message = itemView.findViewById(R.id.chatRightItemMsg);
            time = itemView.findViewById(R.id.chatRightItemTime);

            message.setText(item.getMessage());
            time.setText(item.getTime());
        }
        else{
            itemView = layoutInflater.inflate(R.layout.chat_left_item, viewGroup, false);
            img = itemView.findViewById(R.id.chatLeftItemImg);
            name = itemView.findViewById(R.id.chatLeftItemName);
            message = itemView.findViewById(R.id.chatLeftItemMsg);
            time = itemView.findViewById(R.id.chatLeftItemTime);

            img.setImageResource(R.drawable.ic_launcher);
            name.setText(item.getName());
            message.setText(item.getMessage());
            time.setText(item.getTime());
        }

            return itemView;
    }
}



































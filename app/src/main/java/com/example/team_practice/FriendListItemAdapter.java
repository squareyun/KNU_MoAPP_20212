package com.example.team_practice;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class FriendListItemAdapter extends BaseAdapter {

    ArrayList<FriendListItem> items = new ArrayList<FriendListItem>();
    Context context;

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        context = viewGroup.getContext();
        FriendListItem listItem = items.get(i);

        if(view == null){
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.friend_list_view_item, viewGroup, false);
        }

        TextView nameText = view.findViewById(R.id.friendName);
        TextView walkCntText = view.findViewById(R.id.friendWalkCnt);
        ImageView profileImage = view.findViewById(R.id.friendProfile);
        Button chatBtn = view.findViewById(R.id.friendChatBtn);

        chatBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Toast.makeText(context, listItem.getName() + " 과 대화", Toast.LENGTH_SHORT).show();
            }
        });

        nameText.setText(listItem.getName());
        walkCntText.setText(listItem.getWalkCnt());
        profileImage.setImageResource(listItem.getResId());

        return view;
    }

    public void addItem(FriendListItem item){
        items.add(item);
    }
}

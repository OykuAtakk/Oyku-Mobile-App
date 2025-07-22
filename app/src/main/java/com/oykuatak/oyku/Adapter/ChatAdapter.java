package com.oykuatak.oyku.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.oykuatak.oyku.Model.Chat;
import com.oykuatak.oyku.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatHolder> {
    private static final int MESSAGE_LEFT=1;
    private static final int MESSAGE_RIGHT=0;

    private ArrayList<Chat> chatList;
    private Context context;
    private String mUID;
    private String targetProfile;
    private View v;
    private Chat chat;

    public ChatAdapter(ArrayList<Chat> chatList, Context context,String mUID,String targetProfile) {
        this.chatList = chatList;
        this.context = context;
        this.mUID=mUID;
        this.targetProfile=targetProfile;
    }

    @NonNull
    @Override
    public ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MESSAGE_LEFT)
            v= LayoutInflater.from(context).inflate(R.layout.chat_item_left,parent,false);
        else if (viewType == MESSAGE_RIGHT) {
            v= LayoutInflater.from(context).inflate(R.layout.chat_item_right,parent,false);

        }
        return new ChatHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatHolder holder, int position) {
        chat = chatList.get(position);
        if (chat != null && chat.getMessageIcerigi() != null) {
            holder.textMessage.setText(chat.getMessageIcerigi());
            if (!chat.getGonderen().equals(mUID)) {
                if (targetProfile.equals("default"))
                    holder.imgProfile.setImageResource(R.mipmap.ic_launcher);
                else
                    Picasso.get().load(targetProfile).resize(56, 56).into(holder.imgProfile);
            }
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    class ChatHolder extends RecyclerView.ViewHolder{
        CircleImageView imgProfile;
        TextView textMessage;
        public ChatHolder(@NonNull View itemView) {
            super(itemView);

            imgProfile = itemView.findViewById(R.id.chat_item_imgProfile);
            textMessage=itemView.findViewById(R.id.chat_item_textMessage);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (chatList.get(position).getGonderen().equals(mUID))
            return MESSAGE_RIGHT;
        else
            return MESSAGE_LEFT;
    }
}

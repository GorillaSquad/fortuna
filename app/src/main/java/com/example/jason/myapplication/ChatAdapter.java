package com.example.jason.myapplication;

import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jason.myapplication.containers.Chat;

import org.w3c.dom.Text;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private Chat.ChatMessage[] mDataset;
    private String match;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        public ViewHolder(View v) {
            super(v);
            view = v;
        }
    }

    public ChatAdapter(Chat.ChatMessage[] myDataset, String match) {
        updateData(myDataset);
        this.match = match;
    }

    public Chat.ChatMessage[] getData(){
        return mDataset;
    }

    public void updateData(Chat.ChatMessage[] myDataset) {
        if(myDataset == null)
            myDataset = new Chat.ChatMessage[0];
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message, parent, false);

        // create a new view
        //TextView v = (TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.message, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        TextView textView = holder.view.findViewById(R.id.messageText);
        textView.setText(mDataset[position].message);
        LinearLayout container = (LinearLayout) holder.view.findViewById(R.id.messageContainer);
        if(!mDataset[position].from.equals(match)){
            container.setGravity(Gravity.RIGHT);
            textView.setBackgroundResource(R.drawable.message_bubble_send);
        }else{
            container.setGravity(Gravity.LEFT);
            textView.setBackgroundResource(R.drawable.message_bubble);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.length;
    }
}

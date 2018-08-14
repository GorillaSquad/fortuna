package com.example.jason.myapplication;

import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
    public static int dpToPx(int dp)
    {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }
    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        TextView textView = holder.view.findViewById(R.id.messageText);
        textView.setText(mDataset[position].message);
        LinearLayout container = (LinearLayout) holder.view.findViewById(R.id.messageContainer);

        if(mDataset[position].message.equals("/leave")) {
            container.setGravity(Gravity.CENTER);
            textView.setBackgroundResource(R.drawable.message_important);
            textView.setTypeface(null, Typeface.BOLD);
            textView.setText("A user has ended the chat.");
            return;
        }

        textView.setTypeface(null, Typeface.NORMAL);
        if(!mDataset[position].from.equals(match)){
            container.setGravity(Gravity.RIGHT);
            textView.setBackgroundResource(R.drawable.message_bubble_send);
        }else{
            container.setGravity(Gravity.LEFT);
            textView.setBackgroundResource(R.drawable.message_bubble);
        }

        GradientDrawable shape = (GradientDrawable) textView.getBackground().mutate();
        handleCorners(position,shape, container);
    }

    int CORNER_RADIUS = dpToPx(7);
    private void handleCorners(int position, GradientDrawable shape, LinearLayout c) {
        if(position >= 1) {
            if (!mDataset[position - 1].from.equals(match) && !mDataset[position].from.equals(match)) {
                shape.setCornerRadii(toBottom);
            }else if (mDataset[position - 1].from.equals(match) && mDataset[position].from.equals(match)) {
                shape.setCornerRadii(fromBottom);
            }
        }
        if(position <= mDataset.length-2) {
            if (!mDataset[position + 1].from.equals(match) && !mDataset[position].from.equals(match)) {
                shape.setCornerRadii(toTop);
            }else if (mDataset[position + 1].from.equals(match) && mDataset[position].from.equals(match)) {
                shape.setCornerRadii(fromTop);
            }
        }
        if(position >= 1 && position <= mDataset.length-2) {
            if (!mDataset[position - 1].from.equals(match) && !mDataset[position + 1].from.equals(match) && !mDataset[position].from.equals(match)) {
                shape.setCornerRadii(toMiddle);
            }else if (mDataset[position - 1].from.equals(match) && mDataset[position + 1].from.equals(match) && mDataset[position].from.equals(match)) {
                shape.setCornerRadii(fromMiddle);
            }
        }

    }

    private float[] fromTop = new float[]{CORNER_RADIUS,CORNER_RADIUS,CORNER_RADIUS,CORNER_RADIUS,CORNER_RADIUS,CORNER_RADIUS,0f,0f};
    private float[] fromMiddle = new float[]{0f,0f,CORNER_RADIUS,CORNER_RADIUS,CORNER_RADIUS,CORNER_RADIUS,0f,0f};
    private float[] fromBottom = new float[]{0f,0f,CORNER_RADIUS,CORNER_RADIUS,CORNER_RADIUS,CORNER_RADIUS,CORNER_RADIUS,CORNER_RADIUS};

    private float[] toTop = new float[]{CORNER_RADIUS,CORNER_RADIUS,CORNER_RADIUS,CORNER_RADIUS,0f,0f,CORNER_RADIUS,CORNER_RADIUS};
    private float[] toMiddle = new float[]{CORNER_RADIUS,CORNER_RADIUS,0f,0f,0f,0f,CORNER_RADIUS,CORNER_RADIUS};
    private float[] toBottom = new float[]{CORNER_RADIUS,CORNER_RADIUS,0f,0f,CORNER_RADIUS,CORNER_RADIUS,CORNER_RADIUS,CORNER_RADIUS};
    private float[] test = new float[]{0,0,0f,0f,0,0,0,0};

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.length;
    }
}

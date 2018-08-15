package com.example.jason.myapplication;

import android.content.Context;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.jason.myapplication.containers.Chat;
import com.inmobi.ads.InMobiBanner;
import com.inmobi.sdk.InMobiSdk;

import org.w3c.dom.Text;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private Chat.ChatMessage[] mDataset;
    private String match;
    private Context c;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        public ViewHolder(View v) {
            super(v);
            view = v;
        }
    }

    public ChatAdapter(Chat.ChatMessage[] myDataset, String match, Context c) {

        InMobiSdk.init(c, "36567d23a95f40d286f3abf52bb96720");
        InMobiSdk.setLogLevel(InMobiSdk.LogLevel.DEBUG);
        this.c = c;
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

    @Override
    public int getItemViewType(int position) {
        // Just as an example, return 0 or 2 depending on position
        // Note that unlike in ListView adapters, types don't have to be contiguous
        if(mDataset[position].message.equals("/advertisement")){
            return 3;
        }

        if(position == mDataset.length-1)
            return 1;

        if(position == 0)
            return 2;

        if(!mDataset[position-1].from.equals(mDataset[position].from)){
            return 2;
        }else{
            return 0;
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message, parent, false);

        // create a new view
        //TextView v = (TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.message, parent, false);

        if(viewType == 1) {
            LinearLayout c = (LinearLayout) v.findViewById(R.id.messageContainer);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) c.getLayoutParams();
            params.setMargins(0,0,0, dpToPx(17));
        }else if(viewType == 2) {
            LinearLayout c = (LinearLayout) v.findViewById(R.id.messageContainer);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) c.getLayoutParams();
            params.setMargins(0,dpToPx(17),0, 0);
        }else if(viewType == 3) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.advertisement, parent, false);
            //InMobiBanner bannerAd = (InMobiBanner) v.findViewById(R.id.banner);

            InMobiBanner bannerAd = new InMobiBanner(c, 1531708574428L);

            RelativeLayout adContainer = (RelativeLayout) v.findViewById(R.id.banner);
            RelativeLayout.LayoutParams bannerLp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            bannerLp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            bannerLp.addRule(RelativeLayout.CENTER_HORIZONTAL);
            adContainer.addView(bannerAd,bannerLp);

            bannerAd.load();

            Log.d("ADAPTER", bannerAd.getAdMetaInfo().toString());
        }

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

        if (mDataset[position].message.equals("/advertisement")) {
            return;
        }

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
        c.setPadding(c.getPaddingLeft(), c.getPaddingTop(), c.getPaddingRight(), dpToPx(1));
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

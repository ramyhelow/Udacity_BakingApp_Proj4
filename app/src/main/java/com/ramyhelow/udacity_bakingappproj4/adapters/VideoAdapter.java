package com.ramyhelow.udacity_bakingappproj4.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


import com.ramyhelow.udacity_bakingappproj4.R;
import com.ramyhelow.udacity_bakingappproj4.model.Step;
import com.ramyhelow.udacity_bakingappproj4.util.MyClickCallBack;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    private Context context;
    private List<Step> stepList;
    private MyClickCallBack clickCallBack;

    public VideoAdapter(Context context, List<Step> stepList) {
        this.context = context;
        this.stepList = stepList;
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.video_list, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VideoViewHolder holder, final int position) {
        String shortDescription = stepList.get(position).getShortDescription();
        holder.stepTextView.setText(shortDescription);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickCallBack.onClick(context,stepList.get(position).getId(),
                        stepList.get(position).getDescription(),
                        stepList.get(position).getVideoURL(),
                        stepList.get(position).getThumbnailURL());
            }
        });
    }

    public void setOnClick(MyClickCallBack clickCallBack) {
        this.clickCallBack = clickCallBack;
    }

    @Override
    public int getItemCount() {
        return stepList.size();
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.step_name_text_view)
        TextView stepTextView;
        @BindView(R.id.card_video_list)
        CardView cardView;

        public VideoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

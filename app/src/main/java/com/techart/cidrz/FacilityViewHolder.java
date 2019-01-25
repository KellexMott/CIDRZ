package com.techart.cidrz;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;


/**
 * Created by kelvin on 2/12/18.
 */

public final class FacilityViewHolder extends RecyclerView.ViewHolder {
    public TextView tvFacilityName;
    public TextView tvTime;
    public ImageView ivFacilityPicture;
    public View mView;

    public FacilityViewHolder(View itemView) {
        super(itemView);
        tvFacilityName = itemView.findViewById(R.id.tv_facility_name);
        ivFacilityPicture = itemView.findViewById(R.id.iv_facility_picture);
        tvTime = itemView.findViewById(R.id.tv_time);
        this.mView = itemView;
    }

    public void setTint(Context context){
        ivFacilityPicture.setColorFilter(ContextCompat.getColor(context, R.color.colorTint));
    }

    public void setIvImage(Context context, String imageUrl) {
        Glide.with(context)
        .load(imageUrl)
        .into(ivFacilityPicture);
    }
}

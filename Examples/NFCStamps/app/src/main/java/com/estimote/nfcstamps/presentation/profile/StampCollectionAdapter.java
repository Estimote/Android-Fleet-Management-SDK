package com.estimote.nfcstamps.presentation.profile;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.estimote.nfcstamps.R;
import com.estimote.nfcstamps.Utils;
import com.squareup.picasso.Picasso;

public class StampCollectionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private int collectedStamps;

    public StampCollectionAdapter(Context context, int collectedStamps) {
        super();
        this.context = context;
        this.collectedStamps = collectedStamps;
    }

    class StampViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;

        public StampViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.item_stamp_image);
        }
    }

    public void update(int collectedStamps) {
        this.collectedStamps = collectedStamps;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View stampView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stamp, parent, false);
        return new StampViewHolder(stampView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Picasso.with(context).load(position < collectedStamps ? R.drawable.coffee_small : R.drawable.coffee_small_not_connected).into(((StampViewHolder) holder).imageView);
    }

    @Override
    public int getItemCount() {
        return Utils.STAMPS_AMOUNT;
    }
}

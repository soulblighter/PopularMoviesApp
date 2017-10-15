package br.com.soulblighter.popularmoviesapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import br.com.soulblighter.popularmoviesapp.json.TmdbMovie;
import br.com.soulblighter.popularmoviesapp.network.NetworkUtils;

public class PicassoGridViewAdapter extends RecyclerView.Adapter<PicassoGridViewAdapter.ViewHolder> {

    private List<TmdbMovie> mData = null;
    private LayoutInflater mInflater;
    private PicassoClickListener mClickListener;
    Context mContext;

    public PicassoGridViewAdapter(Context context, List<TmdbMovie> data) {
        mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.grid_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String posterPath = mData.get(position).posterPath;
            Picasso.with(mContext)
                    .load(NetworkUtils.buildImageUrl(posterPath))
                    .placeholder(R.color.colorPrimary)
                    .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_image);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onPicassoItemClick(view, getAdapterPosition());
        }
    }

    public TmdbMovie getItem(int id) {
        return mData == null ? null : mData.get(id);
    }

    public void setClickListener(PicassoClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface PicassoClickListener {
        void onPicassoItemClick(View view, int position);
    }

    public void setData(List<TmdbMovie> data) {
        mData = data;
        notifyDataSetChanged();
    }
}

package br.com.soulblighter.popularmoviesapp;

import android.content.Context;
import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.Map;

public class PicassoGridViewAdapter extends RecyclerView.Adapter<PicassoGridViewAdapter.ViewHolder> {

    private Map<Integer,String> mData = null;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    Context mContext;
    Point displaySize;

    interface onClickHandler {
        void PicassoAdapterOnClick(int id);
    }

    // data is passed into the constructor
    public PicassoGridViewAdapter(Context context, Map<Integer,String> data) {
        mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        displaySize = new Point();
        display.getSize(displaySize);
    }

    // inflates the cell layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.grid_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    // binds the data to the textview in each cell
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String url = null;
        url = NetworkUtils.IMAGE_TMDB_URL + mData.get(position);

        // Used resize with screen site to make
        if(url != null) {
            Picasso.with(mContext)
                    .load(url)
                    .tag(position)
                    .resize(displaySize.x, displaySize.y)
                    .centerInside()
                    .into(holder.imageView);
//                .placeholder(android.support.v7.appcompat.R.drawable.)
//                .error(R.drawable.error)
        } else {
            Log.e(Utils.TAG, "onBindViewHolder: url is null");
        }
    }

    // total number of cells
    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.iv_image);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    public String getItem(int id) {
        return mData == null ? "" : mData.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setData(Map<Integer,String> data) {
        mData = data;
        notifyDataSetChanged();
    }
}

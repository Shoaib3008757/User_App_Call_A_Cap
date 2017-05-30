package come.texi.driver.adapter;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import come.texi.driver.R;

/**
 * Created by techintegrity on 15/07/16.
 */
public class PickupDropLocationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {

    Activity activity;
    ArrayList<HashMap<String,String>> picDrpArray;
    private int itemsCount = 0;
    private static final int VIEW_TYPE_DEFAULT = 1;
    private static final int VIEW_TYPE_LOADER = 2;
    private boolean showLoadingView = false;
    Typeface OpenSans_Regular;

    private OnDraoppickupClickListener onDraoppickupClickListener;

    public PickupDropLocationAdapter(Activity act,ArrayList<HashMap<String,String>> locArray){
        activity = act;
        picDrpArray = locArray;
        OpenSans_Regular = Typeface.createFromAsset(activity.getAssets(), "fonts/OpenSans-Regular_0.ttf");
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.drop_pickup_layout, parent, false);
        DropPickupViewHolder dropPickupViewHolder = new DropPickupViewHolder(view);
        dropPickupViewHolder.layout_main.setOnClickListener(this);
        return dropPickupViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        DropPickupViewHolder holder = (DropPickupViewHolder) viewHolder;
        if (getItemViewType(position) == VIEW_TYPE_DEFAULT) {
            bindPickupDropFeedItem(position, holder);
        } else if (getItemViewType(position) == VIEW_TYPE_LOADER) {
            bindLoadingFeedItem(holder);
        }
    }

    private void bindPickupDropFeedItem(int position, DropPickupViewHolder holder) {
        HashMap<String,String> DropPickupHashmap = picDrpArray.get(position);
        holder.txt_location_name.setText(DropPickupHashmap.get("location name"));
        holder.txt_location_name.setTypeface(OpenSans_Regular);
        holder.layout_main.setTag(holder);
    }

    private void bindLoadingFeedItem(final DropPickupViewHolder holder) {
        System.out.println("BindLoadingFeedItem >>>>>");
    }

    @Override
    public int getItemViewType(int position) {
        if (showLoadingView && position == 0) {
            return VIEW_TYPE_LOADER;
        } else {
            return VIEW_TYPE_DEFAULT;
        }
    }


    @Override
    public int getItemCount() {
        return picDrpArray.size();
    }

    public void updateItems() {
        itemsCount = picDrpArray.size();
        notifyDataSetChanged();
    }

    public void updateBlankItems(ArrayList<HashMap<String,String>> locationArray) {
        picDrpArray = locationArray;
        itemsCount = picDrpArray.size();
        notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        DropPickupViewHolder holder = (DropPickupViewHolder) v.getTag();
        if(viewId == R.id.layout_main){
            if(onDraoppickupClickListener != null)
                onDraoppickupClickListener.PickupDropClick(holder.getAdapterPosition());
        }

    }

    public class DropPickupViewHolder extends RecyclerView.ViewHolder{

        TextView txt_location_name;
        RelativeLayout layout_main;

        public DropPickupViewHolder(View view) {
            super(view);
            txt_location_name = (TextView)view.findViewById(R.id.txt_location_name);
            layout_main = (RelativeLayout)view.findViewById(R.id.layout_main);
        }
    }

    public void setOnDropPickupClickListener(OnDraoppickupClickListener onDraoppickupClickListener) {
        this.onDraoppickupClickListener = onDraoppickupClickListener;
    }

    public interface OnDraoppickupClickListener {

        public void PickupDropClick(int position);
    }
}

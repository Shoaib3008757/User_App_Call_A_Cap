package come.texi.driver.adapter;

import android.app.Activity;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import come.texi.driver.R;
import come.texi.driver.utils.CabDetails;
import come.texi.driver.utils.CircleTransform;
import come.texi.driver.utils.Url;

/**
 * Created by techintegrity on 11/07/16.
 */
public class CabDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {

    ArrayList<CabDetails> cabDetailsArrayList;
    Activity activity;
    private int itemsCount = 0;
    private static final int VIEW_TYPE_DEFAULT = 1;
    private static final int VIEW_TYPE_LOADER = 2;
    private boolean showLoadingView = false;

    private OnCabDetailClickListener onCabDetailClickListener;

    Typeface Roboto_Regular;

    public CabDetailAdapter(Activity act,ArrayList<CabDetails> cabArray){
        activity = act;
        cabDetailsArrayList = cabArray;
        Roboto_Regular = Typeface.createFromAsset(activity.getAssets(), "fonts/Roboto-Regular.ttf");
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(activity).inflate(R.layout.cabdetail_list_layout, parent, false);
        CabDetailViewHolder cabDetViewHol = new CabDetailViewHolder(view);
        cabDetViewHol.layout_tab.setOnClickListener(this);
        return cabDetViewHol;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        CabDetailViewHolder holder = (CabDetailViewHolder) viewHolder;
        if (getItemViewType(position) == VIEW_TYPE_DEFAULT) {
            bindCabDetailFeedItem(position, holder);
        } else if (getItemViewType(position) == VIEW_TYPE_LOADER) {
            bindLoadingFeedItem(holder);
        }
    }

    private void bindCabDetailFeedItem(int position, CabDetailViewHolder holder) {

        CabDetails cabDetails = cabDetailsArrayList.get(position);

        if(cabDetails.getiIsSelected())
            holder.img_selected_indicator.setVisibility(View.VISIBLE);
        else
            holder.img_selected_indicator.setVisibility(View.INVISIBLE);
        Log.d("Car Icon", "Car Icon = " + Url.carImageUrl+cabDetails.getIcon());
        if(!cabDetails.getIcon().equals("")) {
            Uri iconUri = Uri.parse(Url.carImageUrl+cabDetails.getIcon());
            Picasso.with(activity)
                    .load(iconUri)
                    .placeholder(R.drawable.truck_icon)
                    .transform(new CircleTransform())
                    .into(holder.img_car_icon);
        }else{
            Picasso.with(activity)
                    .load(R.drawable.truck_icon)
                    .placeholder(R.drawable.truck_icon)
                    .transform(new CircleTransform())
                    .into(holder.img_car_icon);
        }

        holder.txt_car_type.setText(cabDetails.getCartype());
        holder.txt_car_type.setTypeface(Roboto_Regular);

        holder.layout_tab.setTag(holder);
        if(!cabDetails.getFixPrice().equals("") && !cabDetails.getAreaId().equals("")){
            holder.img_fix_rate_icon.setVisibility(View.VISIBLE);
        }else{
            holder.img_fix_rate_icon.setVisibility(View.INVISIBLE);
        }

    }

    private void bindLoadingFeedItem(final CabDetailViewHolder holder) {

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
        return cabDetailsArrayList.size();
    }

    public void updateItems() {
        itemsCount = cabDetailsArrayList.size();
        notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {

        int viwId = v.getId();
        CabDetailViewHolder holder = (CabDetailViewHolder) v.getTag();
        if(viwId == R.id.layout_tab){
            if(onCabDetailClickListener != null)
                onCabDetailClickListener.CarDetailTab(holder.getAdapterPosition());
        }

    }

    public class CabDetailViewHolder extends RecyclerView.ViewHolder {

        ImageView img_selected_indicator;
        ImageView img_car_icon,img_fix_rate_icon;
        TextView txt_car_type;
        RelativeLayout layout_tab;

        public CabDetailViewHolder(View view) {
            super(view);

            img_selected_indicator = (ImageView)view.findViewById(R.id.img_selected_indicator);
            img_car_icon = (ImageView)view.findViewById(R.id.img_car_icon);
            img_fix_rate_icon = (ImageView)view.findViewById(R.id.img_fix_rate_icon);
            txt_car_type = (TextView)view.findViewById(R.id.txt_car_type);
            layout_tab = (RelativeLayout)view.findViewById(R.id.layout_tab);
        }
    }

    public void setOnCabDetailItemClickListener(OnCabDetailClickListener onCabDetailClickListener) {
        this.onCabDetailClickListener = onCabDetailClickListener;
    }

    public interface OnCabDetailClickListener {
        public void CarDetailTab(int position);
    }
}

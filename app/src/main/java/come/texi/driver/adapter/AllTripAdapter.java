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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import come.texi.driver.R;
import come.texi.driver.utils.AllTripFeed;
import come.texi.driver.utils.CircleTransform;
import come.texi.driver.utils.Url;

/**
 * Created by techintegrity on 13/07/16.
 */
public class AllTripAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {

    Activity activity;
    ArrayList<AllTripFeed> TripArray;
    private int itemsCount = 0;
    private static final int VIEW_TYPE_DEFAULT = 1;
    private static final int VIEW_TYPE_LOADER = 2;
    private boolean showLoadingView = false;

    Typeface OpenSans_Regular,OpenSans_Semi_Bold,OpenSans_Light;

    private OnAllTripClickListener onAllTripClickListener;

    public AllTripAdapter(Activity act,ArrayList<AllTripFeed> trpArray){
        activity = act;
        TripArray = trpArray;

        OpenSans_Regular = Typeface.createFromAsset(activity.getAssets(), "fonts/OpenSans-Regular_0.ttf");
        OpenSans_Semi_Bold = Typeface.createFromAsset(activity.getAssets(), "fonts/OpenSans-Semibold_0.ttf");
        OpenSans_Light = Typeface.createFromAsset(activity.getAssets(), "fonts/OpenSans-Light_0.ttf");
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(activity).inflate(R.layout.alltrip_list_layout, parent, false);
        AllTripViewHolder allTrpViewHol = new AllTripViewHolder(view);
        allTrpViewHol.layout_footer_detail.setOnClickListener(this);
        allTrpViewHol.layout_all_trip.setOnClickListener(this);
        allTrpViewHol.layout_footer_detail.setOnClickListener(this);
        allTrpViewHol.layout_detail.setOnClickListener(this);
        return allTrpViewHol;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        AllTripViewHolder holder = (AllTripViewHolder) viewHolder;
        if (getItemViewType(position) == VIEW_TYPE_DEFAULT) {
            bindCabDetailFeedItem(position, holder);
        } else if (getItemViewType(position) == VIEW_TYPE_LOADER) {
            bindLoadingFeedItem(holder);
        }
    }

    private void bindCabDetailFeedItem(int position, AllTripViewHolder holder) {

        holder.txt_current_booking.setTypeface(OpenSans_Semi_Bold);
        holder.txt_trip_date.setTypeface(OpenSans_Regular);
        holder.txt_pickup_address.setTypeface(OpenSans_Light);
        holder.txt_drop_address.setTypeface(OpenSans_Light);
        holder.txt_booking_id.setTypeface(OpenSans_Semi_Bold);
        holder.txt_truct_type.setTypeface(OpenSans_Semi_Bold);
        holder.txt_booking_id_val.setTypeface(OpenSans_Regular);
        holder.txt_truct_type_val.setTypeface(OpenSans_Regular);

        AllTripFeed allTripFeed = TripArray.get(position);

        Log.d("Status","Status = "+allTripFeed.getStatus());
        if(allTripFeed.getStatus().equals("1") || allTripFeed.getStatus().equals("5")) {
            holder.txt_current_booking.setText(activity.getResources().getString(R.string.pending));
            Picasso.with(activity)
                    .load(R.drawable.status_pending)
                    .into(holder.img_status);
            holder.layout_status_cancle.setVisibility(View.GONE);
            holder.img_driver_image.setVisibility(View.GONE);
            holder.img_cancle_status.setVisibility(View.GONE);
            holder.layout_detail.setVisibility(View.VISIBLE);

        }else if(allTripFeed.getStatus().equals("3")) {
            holder.txt_current_booking.setText(activity.getResources().getString(R.string.accepted));
            Picasso.with(activity)
                    .load(R.drawable.status_accepted)
                    .into(holder.img_status);
            holder.layout_status_cancle.setVisibility(View.VISIBLE);
            holder.img_driver_image.setVisibility(View.VISIBLE);
            holder.img_cancle_status.setVisibility(View.GONE);

            holder.layout_detail.setVisibility(View.VISIBLE);

            try {
                JSONObject DrvObj = new JSONObject(allTripFeed.getDriverDetail());
                Picasso.with(activity)
                        .load(Uri.parse(Url.DriverImageUrl+DrvObj.getString("image")))
                        .placeholder(R.drawable.mail_defoult)
                        .transform(new CircleTransform())
                        .into(holder.img_driver_image);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            RelativeLayout.LayoutParams drvParams = new RelativeLayout.LayoutParams((int) activity.getResources().getDimension(R.dimen.height_50),(int) activity.getResources().getDimension(R.dimen.height_50));
            drvParams.setMargins(0,0,0,0);
            holder.img_driver_image.setLayoutParams(drvParams);

        }else if(allTripFeed.getStatus().equals("4")) {
            holder.txt_current_booking.setText(activity.getResources().getString(R.string.user_cancelled));
            Picasso.with(activity)
                    .load(R.drawable.status_user_cancelled)
                    .into(holder.img_status);
            holder.layout_status_cancle.setVisibility(View.VISIBLE);
            //holder.7mm.setVisibility(View.VISIBLE);
            holder.img_cancle_status.setVisibility(View.VISIBLE);
            holder.img_driver_image.setVisibility(View.GONE);

            holder.layout_detail.setVisibility(View.VISIBLE);


//            if(!allTripFeed.getDriverDetail().equals("null")) {
//                try {
//                    JSONObject DrvObj = new JSONObject(allTripFeed.getDriverDetail());
//                    Picasso.with(activity)
//                            .load(Uri.parse(DrvObj.getString("image")))
//                            .placeholder(R.drawable.avatar_placeholder)
//                            .transform(new CircleTransform())
//                            .into(holder.img_driver_image);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            RelativeLayout.LayoutParams drvParams = new RelativeLayout.LayoutParams((int) activity.getResources().getDimension(R.dimen.height_50),(int) activity.getResources().getDimension(R.dimen.height_50));
//            drvParams.setMargins(0,(int) activity.getResources().getDimension(R.dimen.margin_40),0,0);
//            holder.img_driver_image.setLayoutParams(drvParams);

        }else if(allTripFeed.getStatus().equals("6")) {
            holder.txt_current_booking.setText(activity.getResources().getString(R.string.driver_unavailable));
            Picasso.with(activity)
                    .load(R.drawable.status_driver_unavailable)
                    .into(holder.img_status);
            holder.layout_status_cancle.setVisibility(View.GONE);

            holder.layout_detail.setVisibility(View.VISIBLE);

        }else if(allTripFeed.getStatus().equals("7") || allTripFeed.getStatus().equals("8")) {
            int StatusImg = R.drawable.status_on_trip;
            if(allTripFeed.getStatus().equals("8")) {
                holder.txt_current_booking.setText(activity.getResources().getString(R.string.on_trip));
                StatusImg = R.drawable.status_on_trip;
            }
            else if(allTripFeed.getStatus().equals("7")) {
                holder.txt_current_booking.setText(activity.getResources().getString(R.string.driver_arrived));
                StatusImg = R.drawable.status_driver_arrived;
            }
            Picasso.with(activity)
                    .load(StatusImg)
                    .into(holder.img_status);
            holder.layout_status_cancle.setVisibility(View.VISIBLE);
            holder.img_driver_image.setVisibility(View.VISIBLE);
            holder.img_cancle_status.setVisibility(View.GONE);
            holder.layout_detail.setVisibility(View.VISIBLE);

            try {
                JSONObject DrvObj = new JSONObject(allTripFeed.getDriverDetail());
                Picasso.with(activity)
                        .load(Uri.parse(Url.DriverImageUrl+DrvObj.getString("image")))
                        .placeholder(R.drawable.mail_defoult)
                        .transform(new CircleTransform())
                        .into(holder.img_driver_image);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            RelativeLayout.LayoutParams drvParams = new RelativeLayout.LayoutParams((int) activity.getResources().getDimension(R.dimen.height_50),(int) activity.getResources().getDimension(R.dimen.height_50));
            drvParams.setMargins(0,0,0,0);
            holder.img_driver_image.setLayoutParams(drvParams);
        }else if(allTripFeed.getStatus().equals("9")) {
            holder.txt_current_booking.setText(activity.getResources().getString(R.string.completed));
            Picasso.with(activity)
                    .load(R.drawable.status_completed)
                    .into(holder.img_status);
            holder.layout_status_cancle.setVisibility(View.VISIBLE);
            holder.img_driver_image.setVisibility(View.VISIBLE);
            holder.img_cancle_status.setVisibility(View.GONE);

            holder.layout_detail.setVisibility(View.VISIBLE);

            try {
                JSONObject DrvObj = new JSONObject(allTripFeed.getDriverDetail());
                Picasso.with(activity)
                        .load(Uri.parse(Url.DriverImageUrl+DrvObj.getString("image")))
                        .placeholder(R.drawable.mail_defoult)
                        .transform(new CircleTransform())
                        .into(holder.img_driver_image);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            RelativeLayout.LayoutParams drvParams = new RelativeLayout.LayoutParams((int) activity.getResources().getDimension(R.dimen.height_50),(int) activity.getResources().getDimension(R.dimen.height_50));
            drvParams.setMargins(0,0,0,0);
            holder.img_driver_image.setLayoutParams(drvParams);

            if(allTripFeed.getOldLocationList() != null && allTripFeed.getOldLocationList().size() > 0){
                allTripFeed.setOldLocationList(null);
            }
        }
//        else if(allTripFeed.getStatus().equals("5")) {
//            holder.txt_current_booking.setText(activity.getResources().getString(R.string.driver_cancelled));
//            Picasso.with(activity)
//                    .load(R.drawable.status_driver_cancelled)
//                    .into(holder.img_status);
//            holder.layout_status_cancle.setVisibility(View.VISIBLE);
//            //holder.7mm.setVisibility(View.VISIBLE);
//            holder.img_cancle_status.setVisibility(View.VISIBLE);
//
//            holder.img_cancel_trip.setVisibility(View.GONE);
//            holder.img_detail.setVisibility(View.VISIBLE);
//            Picasso.with(activity)
//                    .load(R.drawable.details)
//                    .into(holder.img_detail);
//        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String pickup_date_time = "";
        try {
            Date parceDate = simpleDateFormat.parse(allTripFeed.getPickupDateTime());
            SimpleDateFormat parceDateFormat = new SimpleDateFormat("dd MMM yyyy");
            pickup_date_time = parceDateFormat.format(parceDate.getTime());

        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.txt_trip_date.setText(pickup_date_time);
        holder.txt_pickup_address.setText(allTripFeed.getPickupArea());
        holder.txt_drop_address.setText(allTripFeed.getDropArea());
        holder.txt_booking_id_val.setText(allTripFeed.getBookingId());
        holder.txt_truct_type_val.setText(allTripFeed.getTaxiType().toUpperCase());

        holder.layout_footer_detail.setTag(holder);
        holder.layout_all_trip.setTag(holder);

        holder.layout_detail.setTag(holder);
        holder.layout_footer_detail.setTag(holder);
        Log.d("position", "position = " + position+"=="+getItemCount());
        if(getItemCount() > 9 && getItemCount() == position+1) {
            if (onAllTripClickListener != null)
                onAllTripClickListener.scrollToLoad(position);
        }
    }

    private void bindLoadingFeedItem(final AllTripViewHolder holder) {
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
        return TripArray.size();
    }

    public void updateItems() {
        itemsCount = TripArray.size();
        notifyDataSetChanged();
    }

    public void updateItemsFilter(ArrayList<AllTripFeed> allTripArray) {
        TripArray = allTripArray;
        itemsCount = TripArray.size();
        notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {

        int viewId = v.getId();
        AllTripViewHolder holder = (AllTripViewHolder) v.getTag();
        if(viewId == R.id.layout_footer_detail){
            onAllTripClickListener.clickDetailTrip(holder.getAdapterPosition());
        }else if(viewId == R.id.layout_all_trip || viewId == R.id.layout_footer_detail || viewId == R.id.layout_detail ){
            onAllTripClickListener.clickDetailTrip(holder.getAdapterPosition());
        }

    }

    public class AllTripViewHolder extends RecyclerView.ViewHolder{

        TextView txt_current_booking;
        TextView txt_trip_date;
        TextView txt_pickup_address;
        TextView txt_drop_address;
        TextView txt_booking_id;
        TextView txt_booking_id_val;
        TextView txt_truct_type;
        TextView txt_truct_type_val;
        RelativeLayout layout_footer_detail;
        LinearLayout layout_all_trip;
        RelativeLayout layout_status_cancle;
        ImageView img_status;
        ImageView img_driver_image;
        ImageView img_cancle_status;
        RelativeLayout layout_detail;


        public AllTripViewHolder(View view) {
            super(view);

            txt_current_booking = (TextView)view.findViewById(R.id.txt_current_booking);
            txt_trip_date = (TextView)view.findViewById(R.id.txt_trip_date);
            txt_pickup_address = (TextView)view.findViewById(R.id.txt_pickup_address);
            txt_drop_address = (TextView)view.findViewById(R.id.txt_drop_address);
            txt_booking_id = (TextView)view.findViewById(R.id.txt_booking_id);
            txt_booking_id_val = (TextView)view.findViewById(R.id.txt_booking_id_val);
            txt_truct_type = (TextView)view.findViewById(R.id.txt_truct_type);
            txt_truct_type_val = (TextView)view.findViewById(R.id.txt_truct_type_val);
            layout_footer_detail = (RelativeLayout)view.findViewById(R.id.layout_footer_detail);
            layout_all_trip = (LinearLayout)view.findViewById(R.id.layout_all_trip);
            layout_status_cancle = (RelativeLayout)view.findViewById(R.id.layout_status_cancle);
            img_status = (ImageView)view.findViewById(R.id.img_status);
            img_driver_image = (ImageView)view.findViewById(R.id.img_driver_image);
            img_cancle_status = (ImageView)view.findViewById(R.id.img_cancle_status);
            layout_detail = (RelativeLayout) view.findViewById(R.id.layout_detail);
        }
    }

    public void setOnAllTripItemClickListener(OnAllTripClickListener onAllTripClickListener) {
        this.onAllTripClickListener = onAllTripClickListener;
    }

    public interface OnAllTripClickListener {
        public void scrollToLoad(int position);
        public void clickDetailTrip(int position);
        public void tripCancel(int position);
    }
}

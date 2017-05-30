package come.texi.driver;

import android.app.Dialog;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.internal.Utility;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import come.texi.driver.adapter.CarTypeAdapter;
import come.texi.driver.utils.CircleTransform;
import come.texi.driver.utils.Common;
import come.texi.driver.utils.Url;

public class RateCardActivity extends AppCompatActivity implements CarTypeAdapter.OnCarTypeClickListener {

    RelativeLayout layout_slidemenu;
    RelativeLayout layout_category;
    ImageView img_truck_icon;
    String cabDetail;

    TextView txt_rate_card,txt_cateogry,txt_truck_typ_val,txt_loading_capacity,txt_loading_capacity_value,txt_standars_rate_day,txt_day_fir_km
            ,txt_fir_day_currency,txt_fir_day_price,txt_after_km,txt_aft_day_currency,txt_aft_day_price
            ,txt_aft_day_per_km,txt_standars_rate_night,txt_night_fir_km,txt_fir_night_currency
            ,txt_fir_nigth_price,txt_after_night_km,txt_aft_night_currency,txt_aft_night_price
            ,txt_aft_night_per_km,txt_extra_charges,txt_ride_time_chr_day,txt_ride_time_day_currency
            ,txt_ride_time_day_price,txt_ride_time_day_per_km,txt_wait_time_day,txt_ride_time_chr_night
            ,txt_ride_time_night_currency,txt_ride_time_night_price,txt_ride_time_night_per_km,txt_wait_time_night
            ,txt_per_time_charges,txt_per_time_charges_des,txt_service_tex,txt_service_tex_des,txt_toll_tex;

    SlidingMenu slidingMenu;

    Typeface OpenSans_Bold,OpenSans_Regular,Robot_Regular,Roboto_medium;

    Dialog CarTypeDialog;
    RecyclerView recycle_car_type;
    private RecyclerView.LayoutManager CarTypeLayoutManager;
    CarTypeAdapter carTypeAdapter;
    JSONArray cabDetailArray;

    Common common = new Common();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_card);

        cabDetailArray = Common.CabDetail;

        OpenSans_Bold = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Bold_0.ttf");
        OpenSans_Regular = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Regular_0.ttf");
        Robot_Regular = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
        Roboto_medium = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Medium.ttf");

        layout_slidemenu = (RelativeLayout)findViewById(R.id.layout_slidemenu);


        txt_rate_card = (TextView) findViewById(R.id.txt_rate_card);
        txt_cateogry = (TextView) findViewById(R.id.txt_cateogry);
        txt_truck_typ_val = (TextView) findViewById(R.id.txt_truck_typ_val);
        txt_loading_capacity = (TextView) findViewById(R.id.txt_loading_capacity);
        txt_loading_capacity_value = (TextView) findViewById(R.id.txt_loading_capacity_value);
        txt_standars_rate_day = (TextView) findViewById(R.id.txt_standars_rate_day);
        txt_day_fir_km = (TextView) findViewById(R.id.txt_day_fir_km);
        txt_fir_day_currency = (TextView) findViewById(R.id.txt_fir_day_currency);
        txt_fir_day_price = (TextView) findViewById(R.id.txt_fir_day_price);
        txt_after_km = (TextView) findViewById(R.id.txt_after_km);
        txt_aft_day_currency = (TextView) findViewById(R.id.txt_aft_day_currency);
        txt_aft_day_price = (TextView) findViewById(R.id.txt_aft_day_price);
        txt_aft_day_per_km = (TextView) findViewById(R.id.txt_aft_day_per_km);
        txt_standars_rate_night = (TextView) findViewById(R.id.txt_standars_rate_night);
        txt_night_fir_km = (TextView) findViewById(R.id.txt_night_fir_km);
        txt_fir_night_currency = (TextView) findViewById(R.id.txt_fir_night_currency);
        txt_fir_nigth_price = (TextView) findViewById(R.id.txt_fir_nigth_price);
        txt_after_night_km = (TextView) findViewById(R.id.txt_after_night_km);
        txt_aft_night_currency = (TextView) findViewById(R.id.txt_aft_night_currency);
        txt_aft_night_price = (TextView) findViewById(R.id.txt_aft_night_price);
        txt_aft_night_per_km = (TextView) findViewById(R.id.txt_aft_night_per_km);
        txt_extra_charges = (TextView) findViewById(R.id.txt_extra_charges);
        txt_ride_time_chr_day = (TextView) findViewById(R.id.txt_ride_time_chr_day);
        txt_ride_time_day_currency = (TextView) findViewById(R.id.txt_ride_time_day_currency);
        txt_ride_time_day_price = (TextView) findViewById(R.id.txt_ride_time_day_price);
        txt_ride_time_day_per_km = (TextView) findViewById(R.id.txt_ride_time_day_per_km);
        txt_wait_time_day = (TextView) findViewById(R.id.txt_wait_time_day);
        txt_ride_time_chr_night = (TextView) findViewById(R.id.txt_ride_time_chr_night);
        txt_ride_time_night_currency = (TextView) findViewById(R.id.txt_ride_time_night_currency);
        txt_ride_time_night_price = (TextView) findViewById(R.id.txt_ride_time_night_price);
        txt_ride_time_night_per_km = (TextView) findViewById(R.id.txt_ride_time_night_per_km);
        txt_wait_time_night = (TextView) findViewById(R.id.txt_wait_time_night);
        txt_per_time_charges = (TextView) findViewById(R.id.txt_per_time_charges);
        txt_per_time_charges_des = (TextView) findViewById(R.id.txt_per_time_charges_des);
        txt_service_tex = (TextView) findViewById(R.id.txt_service_tex);
        txt_service_tex_des = (TextView) findViewById(R.id.txt_service_tex_des);
        txt_toll_tex = (TextView) findViewById(R.id.txt_toll_tex);
        img_truck_icon = (ImageView)findViewById(R.id.img_truck_icon);

        txt_cateogry.setTypeface(Roboto_medium);
        txt_truck_typ_val.setTypeface(Roboto_medium);
        txt_loading_capacity.setTypeface(Roboto_medium);
        txt_loading_capacity_value.setTypeface(Roboto_medium);
        txt_day_fir_km.setTypeface(Roboto_medium);
        txt_fir_day_currency.setTypeface(Roboto_medium);
        txt_fir_day_price.setTypeface(Roboto_medium);
        txt_after_km.setTypeface(Roboto_medium);
        txt_aft_day_currency.setTypeface(Roboto_medium);
        txt_aft_day_price.setTypeface(Roboto_medium);
        txt_aft_day_per_km.setTypeface(Roboto_medium);
        txt_night_fir_km.setTypeface(Roboto_medium);
        txt_fir_night_currency.setTypeface(Roboto_medium);
        txt_fir_nigth_price.setTypeface(Roboto_medium);
        txt_after_night_km.setTypeface(Roboto_medium);
        txt_aft_night_currency.setTypeface(Roboto_medium);
        txt_aft_night_price.setTypeface(Roboto_medium);
        txt_aft_night_per_km.setTypeface(Roboto_medium);
        txt_ride_time_chr_day.setTypeface(Roboto_medium);
        txt_ride_time_day_currency.setTypeface(Roboto_medium);
        txt_ride_time_day_price.setTypeface(Roboto_medium);
        txt_ride_time_day_per_km.setTypeface(Roboto_medium);
        txt_wait_time_day.setTypeface(Roboto_medium);
        txt_ride_time_chr_night.setTypeface(Roboto_medium);
        txt_ride_time_night_currency.setTypeface(Roboto_medium);
        txt_ride_time_night_price.setTypeface(Roboto_medium);
        txt_ride_time_night_per_km.setTypeface(Roboto_medium);
        txt_per_time_charges.setTypeface(Roboto_medium);
        txt_service_tex.setTypeface(Roboto_medium);

        txt_rate_card.setTypeface(OpenSans_Bold);
        txt_standars_rate_day.setTypeface(Robot_Regular);
        txt_standars_rate_night.setTypeface(Robot_Regular);
        txt_extra_charges.setTypeface(Robot_Regular);
        txt_toll_tex.setTypeface(Robot_Regular);
        txt_service_tex_des.setTypeface(Robot_Regular);
        txt_per_time_charges_des.setTypeface(Robot_Regular);
        txt_wait_time_night.setTypeface(Robot_Regular);
        txt_wait_time_day.setTypeface(Robot_Regular);


        /*Cab Detail*/
        CabDetailView(0);

        layout_category = (RelativeLayout)findViewById(R.id.layout_category);

        layout_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*Car Type Dialog Start*/
                CarTypeDialog = new Dialog(RateCardActivity.this,android.R.style.Theme_Translucent_NoTitleBar);
                CarTypeDialog.setContentView(R.layout.cartype_dialog);
                recycle_car_type = (RecyclerView)CarTypeDialog.findViewById(R.id.recycle_car_type);

                CarTypeLayoutManager = new LinearLayoutManager(RateCardActivity.this);
                recycle_car_type.setLayoutManager(CarTypeLayoutManager);

                carTypeAdapter = new CarTypeAdapter(RateCardActivity.this, cabDetailArray);
                carTypeAdapter.updateItems();
                carTypeAdapter.setOnCarTypeItemClickListener(RateCardActivity.this);
                recycle_car_type.setAdapter(carTypeAdapter);

                CarTypeDialog.show();
                /*Car Type Dialog End*/

            }
        });

        /*Slide Menu Start*/
        slidingMenu = new SlidingMenu(this);
        slidingMenu.setMode(SlidingMenu.LEFT);
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        slidingMenu.setBehindOffsetRes(R.dimen.slide_menu_width);
        slidingMenu.setFadeDegree(0.20f);
        slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        slidingMenu.setMenu(R.layout.left_menu);

        common.SlideMenuDesign(slidingMenu, RateCardActivity.this,"rate card");

        layout_slidemenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slidingMenu.toggle();
            }
        });
    }

    public void CabDetailView(int position){

        try {

            JSONObject cabDetailObj = cabDetailArray.getJSONObject(position);

            Picasso.with(RateCardActivity.this)
                    .load(Uri.parse(Url.carImageUrl+cabDetailObj.getString("icon").toString()))
                    .placeholder(R.drawable.truck_slide_icon)
                    .transform(new CircleTransform())
                    .into(img_truck_icon);

            txt_truck_typ_val.setText(cabDetailObj.getString("cartype").toString());
            txt_loading_capacity_value.setText(cabDetailObj.getString("seat_capacity").toString());

            /*Day*/
            txt_fir_day_currency.setText(Common.Currency);
            txt_day_fir_km.setText(getResources().getString(R.string.first)+" "+ cabDetailObj.getString("intialkm").toString() +" "+getResources().getString(R.string.km));
            txt_fir_day_price.setText(cabDetailObj.getString("car_rate").toString());
            txt_after_km.setText(getResources().getString(R.string.after)+" "+ cabDetailObj.getString("intialkm").toString() +" "+getResources().getString(R.string.km));
            txt_aft_day_currency.setText(Common.Currency);
            txt_aft_day_price.setText(cabDetailObj.getString("fromintailrate").toString());

            /*Night*/
            txt_night_fir_km.setText(getResources().getString(R.string.first)+" "+ cabDetailObj.getString("intialkm").toString() +" "+getResources().getString(R.string.km));
            txt_fir_night_currency.setText(Common.Currency);
            txt_fir_nigth_price.setText(cabDetailObj.getString("night_intailrate").toString());
            txt_after_night_km.setText(getResources().getString(R.string.after)+" "+ cabDetailObj.getString("intialkm").toString() +" "+getResources().getString(R.string.km));
            txt_aft_night_currency.setText(Common.Currency);
            txt_aft_night_price.setText(cabDetailObj.getString("night_fromintailrate").toString());

            /*Extra Charge*/
            txt_ride_time_day_currency.setText(Common.Currency);
            txt_ride_time_day_price.setText(cabDetailObj.getString("ride_time_rate").toString());
            txt_ride_time_night_currency.setText(Common.Currency);
            txt_ride_time_night_price.setText(cabDetailObj.getString("night_ride_time_rate").toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void SelectCarType(int position) {
        CabDetailView(position);
        CarTypeDialog.cancel();
    }

    @Override
    public void onResume() {
        super.onResume();

        common.SlideMenuDesign(slidingMenu, RateCardActivity.this, "rate card");
    }
}

package come.texi.driver;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.victor.loading.rotate.RotateLoading;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import come.texi.driver.adapter.AllTripAdapter;
import come.texi.driver.utils.AllTripFeed;
import come.texi.driver.utils.Common;
import come.texi.driver.utils.Url;

public class AllTripActivity extends AppCompatActivity implements AllTripAdapter.OnAllTripClickListener {


    RelativeLayout layout_slidemenu;
    TextView txt_all_trip;
    RelativeLayout layout_filter;
    RecyclerView recycle_all_trip;
    SwipeRefreshLayout swipe_refresh_layout;
    RelativeLayout layout_background;
    RelativeLayout layout_no_recourd_found;
    LinearLayout layout_recycleview;

    CheckBox chk_all;
    CheckBox chk_pen_book;
    CheckBox chk_com_book;
    CheckBox chk_drv_reject;
    CheckBox chk_user_reject;
    CheckBox chk_drv_accept;

    boolean checkAllClick = false;

    AllTripAdapter allTripAdapter;

    SharedPreferences userPref;

    ArrayList<AllTripFeed> allTripArray;
    private RecyclerView.LayoutManager AllTripLayoutManager;

    Typeface OpenSans_Bold,OpenSans_Regular,Roboto_Bold;

    Dialog filterDialog;
    String FilterString = "";

    SlidingMenu slidingMenu;

    Dialog ProgressDialog;
    RotateLoading cusRotateLoading;

    CheckBox chk_drive_late;
    CheckBox chk_changed_mind;
    CheckBox chk_another_cab;
    CheckBox chk_denied_duty;
    AllTripFeed SelTripFeeds;

    Common common = new Common();
    BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_trip);

        layout_slidemenu = (RelativeLayout)findViewById(R.id.layout_slidemenu);
        txt_all_trip = (TextView)findViewById(R.id.txt_all_trip);
        layout_filter = (RelativeLayout)findViewById(R.id.layout_filter);
        recycle_all_trip = (RecyclerView)findViewById(R.id.recycle_all_trip);
        swipe_refresh_layout = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh_layout);
        layout_background = (RelativeLayout)findViewById(R.id.layout_background);
        layout_no_recourd_found = (RelativeLayout)findViewById(R.id.layout_no_recourd_found);
        layout_recycleview = (LinearLayout)findViewById(R.id.layout_recycleview);


        OpenSans_Bold = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Bold_0.ttf");
        OpenSans_Regular = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Regular_0.ttf");
        Roboto_Bold = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Bold.ttf");

        txt_all_trip.setTypeface(OpenSans_Bold);

        userPref = PreferenceManager.getDefaultSharedPreferences(AllTripActivity.this);

        AllTripLayoutManager = new LinearLayoutManager(this);
        recycle_all_trip.setLayoutManager(AllTripLayoutManager);

        ProgressDialog = new Dialog(AllTripActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
        ProgressDialog.setContentView(R.layout.custom_progress_dialog);
        ProgressDialog.setCancelable(false);
        cusRotateLoading = (RotateLoading)ProgressDialog.findViewById(R.id.rotateloading_register);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Common.isNetworkAvailable(AllTripActivity.this)) {
                    ProgressDialog.show();
                    cusRotateLoading.start();
                    Log.d("check Value", "check value = " + userPref.getInt("pending booking", 5) + "==" + userPref.getInt("user reject", 5) + "==" + userPref.getInt("driver unavailable", 5) + "==" + userPref.getInt("complete booking", 5));
                    if (userPref.getBoolean("setFilter", false) == true) {
                        if (userPref.getInt("pending booking", 0) == 1) {
                            FilterString += 1 + ",";
                        }
                        if (userPref.getInt("complete booking", 0) == 9) {
                            FilterString += 9 + ",";
                        }
                        if (userPref.getInt("driver unavailable", 0) == 6) {
                            FilterString += 6 + ",";
                        }
                        if (userPref.getInt("user reject", 0) == 4) {
                            FilterString += 4 + ",";
                        }
                        if (userPref.getInt("driver accept", 0) == 3) {
                            FilterString += 3 + ",";
                        }

                        FilterString = FilterString.substring(0, (FilterString.length() - 1));
                        Log.d("FilterString", "FilterString = " + FilterString);

                        if (userPref.getInt("pending booking", 0) == 1 && userPref.getInt("complete booking", 0) == 9 && userPref.getInt("driver unavailable", 0) == 6 && userPref.getInt("user reject", 0) == 4 && userPref.getInt("driver accept", 0) == 3) {
                            SharedPreferences.Editor checkAll = userPref.edit();
                            checkAll.putString("check all","check all");
                            checkAll.commit();
                            FilterString = "";
                        }else{
                            SharedPreferences.Editor checkAll = userPref.edit();
                            checkAll.putString("check all","");
                            checkAll.commit();
                        }
                        FilterAllTrips(0, "filter");
                        FilterString = "";
                    } else {
                        getAllTrip(0);
                    }

                } else {
                    Common.showInternetInfo(AllTripActivity.this, "Network is not available");
                    swipe_refresh_layout.setEnabled(false);
                }
            }
        }, 1000);

        swipe_refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                recycle_all_trip.setEnabled(false);
                if (Common.isNetworkAvailable(AllTripActivity.this)) {
                    if (userPref.getBoolean("setFilter", false) == true) {
                        if (userPref.getInt("pending booking", 6) == 1) {
                            FilterString += 1 + ",";
                        }
                        if (userPref.getInt("complete booking", 0) == 9) {
                            FilterString += 9 + ",";
                        }
                        if (userPref.getInt("driver unavailable", 0) == 6) {
                            FilterString += 6 + ",";
                        }
                        if (userPref.getInt("user reject", 0) == 4) {
                            FilterString += 4 + ",";
                        }
                        if (userPref.getInt("user reject", 0) == 4) {
                            FilterString += 4 + ",";
                        }
                        if (userPref.getInt("driver accept", 0) == 3) {
                            FilterString += 3 + ",";
                        }

                        FilterString = FilterString.substring(0, (FilterString.length() - 1));
                        Log.d("FilterString", "FilterString = " + FilterString);

                        if (userPref.getInt("pending booking", 0) == 1 && userPref.getInt("complete booking", 0) == 9 && userPref.getInt("driver unavailable", 0) == 6 && userPref.getInt("user reject", 0) == 4 && userPref.getInt("driver accept", 0) == 3){
                            FilterString = "";
                        }

                        FilterAllTrips(0,"");
                        FilterString = "";
                    } else {
                        getAllTrip(0);
                    }
                } else {
                    //Network is not available
                    recycle_all_trip.setEnabled(true);
                    Common.showInternetInfo(AllTripActivity.this, "Network is not available");
                }
            }
        });

        /*Filter Dialog Start*/

        filterDialog = new Dialog(AllTripActivity.this,R.style.DialogSlideAnim);
        filterDialog.setContentView(R.layout.all_trip_filter_dialog);

        layout_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                if (layout_background.getVisibility() == View.GONE) {
//                    layout_background.animate()
//                        .translationY(layout_background.getHeight()).alpha(2.0f)
//                        .setListener(new AnimatorListenerAdapter() {
//                            @Override
//                            public void onAnimationStart(Animator animation) {
//                                super.onAnimationStart(animation);
//                                layout_background.setVisibility(View.VISIBLE);
//                                layout_background.setAlpha(0.0f);
//                            }
//                        });
//                }
                layout_background.setVisibility(View.VISIBLE);
                filterDialog.show();
            }
        });

        TextView txt_all = (TextView)filterDialog.findViewById(R.id.txt_all);
        txt_all.setTypeface(OpenSans_Regular);
        TextView txt_pending_booking = (TextView)filterDialog.findViewById(R.id.txt_pending_booking);
        txt_pending_booking.setTypeface(OpenSans_Regular);
        TextView txt_com_booking = (TextView)filterDialog.findViewById(R.id.txt_com_booking);
        txt_com_booking.setTypeface(OpenSans_Regular);
        TextView txt_drv_una = (TextView)filterDialog.findViewById(R.id.txt_drv_una);
        txt_drv_una.setTypeface(OpenSans_Regular);
        TextView txt_usr_rej = (TextView)filterDialog.findViewById(R.id.txt_usr_rej);
        txt_usr_rej.setTypeface(OpenSans_Regular);
        TextView txt_drv_accept = (TextView)filterDialog.findViewById(R.id.txt_drv_accept);
        txt_drv_accept.setTypeface(OpenSans_Regular);

        chk_all = (CheckBox)filterDialog.findViewById(R.id.chk_all);

        RelativeLayout layout_all_check = (RelativeLayout)filterDialog.findViewById(R.id.layout_all_check);
        CheckBoxChecked(layout_all_check, chk_all, "all");

        chk_pen_book = (CheckBox)filterDialog.findViewById(R.id.chk_pen_book);
        RelativeLayout layour_pen_book_check = (RelativeLayout)filterDialog.findViewById(R.id.layour_pen_book_check);
        CheckBoxChecked(layour_pen_book_check, chk_pen_book, "pending book");

        chk_com_book = (CheckBox)filterDialog.findViewById(R.id.chk_com_book);
        RelativeLayout layout_com_book_check = (RelativeLayout)filterDialog.findViewById(R.id.layout_com_book_check);
        CheckBoxChecked(layout_com_book_check, chk_com_book, "completed book");

        chk_drv_reject = (CheckBox)filterDialog.findViewById(R.id.chk_drv_reject);
        RelativeLayout layout_drv_reject_check = (RelativeLayout)filterDialog.findViewById(R.id.layout_drv_reject_check);
        CheckBoxChecked(layout_drv_reject_check, chk_drv_reject, "driver reject");

        chk_user_reject = (CheckBox)filterDialog.findViewById(R.id.chk_user_reject);
        RelativeLayout layout_user_reject_check = (RelativeLayout)filterDialog.findViewById(R.id.layout_user_reject_check);
        CheckBoxChecked(layout_user_reject_check, chk_user_reject, "user reject");

        chk_drv_accept = (CheckBox)filterDialog.findViewById(R.id.chk_drv_accept);
        RelativeLayout layout_drv_accept_check = (RelativeLayout)filterDialog.findViewById(R.id.layout_drv_accept_check);
        CheckBoxChecked(layout_drv_accept_check, chk_drv_accept, "drive accept");

        Log.d("check Value","check value = "+userPref.getInt("pending booking",5)+"=="+userPref.getInt("user reject",5)+"=="+userPref.getInt("driver unavailable",5)+"=="+userPref.getInt("complete booking",5));
        if(userPref.getInt("user reject",0) == 4)
            chk_user_reject.setChecked(true);
        if(userPref.getInt("driver unavailable",0) == 6)
            chk_drv_reject.setChecked(true);
        if(userPref.getInt("complete booking",0) == 9)
            chk_com_book.setChecked(true);
        if(userPref.getInt("pending booking",0) == 1)
            chk_pen_book.setChecked(true);
        if (userPref.getInt("driver accept", 0) == 3) {
            chk_drv_accept.setChecked(true);
        }

        if(userPref.getInt("user reject",0) == 4 && userPref.getInt("driver unavailable",0) == 6 && userPref.getInt("complete booking",0) == 9 && userPref.getInt("pending booking",0) == 1 && userPref.getInt("driver accept",0) == 3){
            chk_all.setChecked(true);
        }

        ImageView img_close_icon = (ImageView)filterDialog.findViewById(R.id.img_close_icon);
        img_close_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            layout_background.setVisibility(View.GONE);
            filterDialog.cancel();
            }
        });

//        TextView txt_cancel_popup = (TextView)filterDialog.findViewById(R.id.txt_cancel_popup);
//        txt_cancel_popup.setTypeface(Roboto_Bold);

        RelativeLayout layout_calcel = (RelativeLayout) filterDialog.findViewById(R.id.layout_calcel);
        layout_calcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            layout_background.setVisibility(View.GONE);
            filterDialog.cancel();
            }
        });

        RelativeLayout layout_apply = (RelativeLayout) filterDialog.findViewById(R.id.layout_apply);
        layout_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ProgressDialog.show();
                cusRotateLoading.start();

                layout_background.setVisibility(View.GONE);
                filterDialog.cancel();
                boolean setFilter = false;
                SharedPreferences.Editor pending_booking = userPref.edit();
                if(chk_pen_book.isChecked()) {
                    FilterString = 1 + ",";
                    pending_booking.putInt("pending booking", 1);
                    setFilter = true;
                }else
                    pending_booking.putInt("pending booking", 0);
                pending_booking.commit();
                SharedPreferences.Editor complete_booking = userPref.edit();
                if(chk_com_book.isChecked()) {
                    FilterString += 9 + ",";
                    complete_booking.putInt("complete booking", 9);
                    setFilter = true;
                }else
                    complete_booking.putInt("complete booking", 0);
                complete_booking.commit();

                SharedPreferences.Editor user_reject = userPref.edit();
                if(chk_user_reject.isChecked()) {
                    FilterString += 4 + ",";
                    user_reject.putInt("user reject", 4);
                    setFilter = true;
                }else
                    user_reject.putInt("user reject", 0);
                user_reject.commit();

                SharedPreferences.Editor driver_accept = userPref.edit();
                if(chk_drv_accept.isChecked()) {
                    FilterString += 3 + ",";
                    driver_accept.putInt("driver accept", 3);
                    setFilter = true;
                }else
                    driver_accept.putInt("driver accept", 0);
                driver_accept.commit();

                SharedPreferences.Editor driver_unavailable = userPref.edit();
                if(chk_drv_reject.isChecked()) {
                    FilterString += 6 + ",";
                    driver_unavailable.putInt("driver unavailable", 6);
                    setFilter = true;
                }else
                    driver_unavailable.putInt("driver unavailable", 0);
                driver_unavailable.commit();

                if(FilterString.length() > 0)
                    FilterString = FilterString.substring(0, (FilterString.length() - 1));

                SharedPreferences.Editor clickfilter = userPref.edit();
                clickfilter.putBoolean("setFilter", setFilter);
                clickfilter.commit();

                if (userPref.getInt("pending booking", 0) == 1 && userPref.getInt("complete booking", 0) == 9 && userPref.getInt("driver unavailable", 0) == 6 && userPref.getInt("user reject", 0) == 4 && userPref.getInt("driver accept", 0) == 3){
                    FilterString = "";
                }

                FilterAllTrips(0,"filter");
                FilterString = "";

            }
        });

        /*Filter Dialog End*/


         /*Slide Menu Start*/

        slidingMenu = new SlidingMenu(this);
        slidingMenu.setMode(SlidingMenu.LEFT);
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        slidingMenu.setBehindOffsetRes(R.dimen.slide_menu_width);
        slidingMenu.setFadeDegree(0.20f);
        slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        slidingMenu.setMenu(R.layout.left_menu);

        common.SlideMenuDesign(slidingMenu, AllTripActivity.this,"all trip");

        layout_slidemenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slidingMenu.toggle();
            }
        });
    }

    public void CheckBoxChecked(RelativeLayout relativeLayout, final CheckBox checkBox, final String checkBoxValue){

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("checkAllClick", "checkAllClick = " + checkAllClick + "==" + checkAllClick);
                if (checkBoxValue.equals("all")) {
                    if (checkAllClick) {
                        chk_all.setChecked(false);
                        chk_pen_book.setChecked(false);
                        chk_com_book.setChecked(false);
                        chk_drv_reject.setChecked(false);
                        chk_user_reject.setChecked(false);
                        chk_drv_accept.setChecked(false);
                        checkAllClick = false;
                    } else {
                        chk_all.setChecked(true);
                        chk_pen_book.setChecked(true);
                        chk_com_book.setChecked(true);
                        chk_drv_reject.setChecked(true);
                        chk_user_reject.setChecked(true);
                        chk_drv_accept.setChecked(true);
                        checkAllClick = true;
                    }
                } else {
                    if (chk_pen_book.isChecked() && chk_com_book.isChecked() && chk_drv_reject.isChecked() && chk_user_reject.isChecked() && chk_drv_accept.isChecked()) {
                        chk_all.setChecked(true);
                        checkAllClick = true;
                    } else {
                        chk_all.setChecked(false);
                        checkAllClick = false;
                    }

                }
            }
        });

        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox.isChecked())
                    checkBox.setChecked(false);
                else
                    checkBox.setChecked(true);
                Log.d("checkAllClick", "checkAllClick = " + checkAllClick + "==" + checkAllClick);
                if (checkBoxValue.equals("all")) {
                    if (checkAllClick) {
                        chk_all.setChecked(false);
                        chk_pen_book.setChecked(false);
                        chk_com_book.setChecked(false);
                        chk_drv_reject.setChecked(false);
                        chk_user_reject.setChecked(false);
                        chk_drv_accept.setChecked(false);
                        checkAllClick = false;
                    } else {
                        chk_all.setChecked(true);
                        chk_pen_book.setChecked(true);
                        chk_com_book.setChecked(true);
                        chk_drv_reject.setChecked(true);
                        chk_user_reject.setChecked(true);
                        chk_drv_accept.setChecked(true);
                        checkAllClick = true;
                    }
                } else {
                    if (chk_pen_book.isChecked() && chk_com_book.isChecked() && chk_drv_reject.isChecked() && chk_user_reject.isChecked() && chk_drv_accept.isChecked()) {
                        chk_all.setChecked(true);
                        checkAllClick = true;
                    } else {
                        chk_all.setChecked(false);
                        checkAllClick = false;
                    }

                }

            }
        });
    }

    public void getAllTrip(final int offset) {

        if(offset == 0) {
            allTripArray = new ArrayList<>();
        }

        Log.d("loadTripsUrl","loadTripsUrl ="+Url.loadTripsUrl+"=="+userPref.getString("id", "")+"=="+String.valueOf(offset));
        Ion.with(AllTripActivity.this)
            .load(Url.loadTripsUrl)
            .setTimeout(6000)
            //.setJsonObjectBody(json)
            .setMultipartParameter("user_id", userPref.getString("id", ""))
            .setMultipartParameter("off", String.valueOf(offset))
            .asJsonObject()
            .setCallback(new FutureCallback<JsonObject>() {
                @Override
                public void onCompleted(Exception error, JsonObject result) {
                    // do stuff with the result or error
                    Log.d("load_trips result", "load_trips result = " + result + "==" + error);
                    if (error == null) {

                        ProgressDialog.cancel();
                        cusRotateLoading.stop();
                        try {
                            JSONObject resObj = new JSONObject(result.toString());
                            Log.d("loadTripsUrl", "loadTripsUrl two= " + resObj);
                            swipe_refresh_layout.setRefreshing(false);
                            if (resObj.getString("status").equals("success")) {
                                recycle_all_trip.setEnabled(true);
                                JSONArray tripArray = new JSONArray(resObj.getString("all_trip"));
                                for (int t = 0; t < tripArray.length(); t++) {
                                    JSONObject trpObj = tripArray.getJSONObject(t);
                                    AllTripFeed allTripFeed = new AllTripFeed();
                                    allTripFeed.setBookingId(trpObj.getString("id"));
                                    allTripFeed.setDropArea(trpObj.getString("drop_area"));
                                    allTripFeed.setPickupArea(trpObj.getString("pickup_area"));
                                    allTripFeed.setTaxiType(trpObj.getString("car_type"));
                                    allTripFeed.setPickupDateTime(trpObj.getString("pickup_date_time"));
                                    allTripFeed.setAmount(trpObj.getString("amount"));
                                    allTripFeed.setCarIcon(trpObj.getString("icon"));
                                    allTripFeed.setKm(trpObj.getString("km"));
                                    allTripFeed.setDriverDetail(trpObj.getString("driver_detail"));
                                    allTripFeed.setStatus(trpObj.getString("status"));
                                    allTripFeed.setApproxTime(trpObj.getString("approx_time"));
                                    allTripFeed.setOldLocationList(null);
                                    allTripFeed.setStartPickLatLng(trpObj.getString("pickup_lat"));
                                    allTripFeed.setEndPickLatLng(trpObj.getString("pickup_longs"));
                                    allTripFeed.setStartDropLatLng(trpObj.getString("drop_lat"));
                                    allTripFeed.setEndDropLatLng(trpObj.getString("drop_longs"));

                                    allTripArray.add(allTripFeed);
                                }
                                Log.d("loadTripsUrl", "loadTripsUrl three= " + allTripArray.size());
                                if (allTripArray != null && allTripArray.size() > 0) {
                                    if (offset == 0) {
                                        layout_recycleview.setVisibility(View.VISIBLE);
                                        layout_no_recourd_found.setVisibility(View.GONE);
                                        allTripAdapter = new AllTripAdapter(AllTripActivity.this, allTripArray);
                                        recycle_all_trip.setAdapter(allTripAdapter);
                                        allTripAdapter.setOnAllTripItemClickListener(AllTripActivity.this);


                                        ProgressDialog.cancel();
                                        cusRotateLoading.stop();
                                    }
                                    allTripAdapter.updateItems();
                                    swipe_refresh_layout.setEnabled(true);
                                }
                            }else if(resObj.getString("status").equals("false")){
                                Common.user_InActive = 1;
                                Common.InActive_msg = resObj.getString("message");

                                SharedPreferences.Editor editor = userPref.edit();
                                editor.clear();
                                editor.commit();

                                Intent logInt = new Intent(AllTripActivity.this, LoginOptionActivity.class);
                                logInt.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                logInt.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                logInt.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(logInt);

                            } else {
                                if (offset == 0) {
                                    ProgressDialog.cancel();
                                    cusRotateLoading.stop();
                                    layout_recycleview.setVisibility(View.GONE);
                                    layout_no_recourd_found.setVisibility(View.VISIBLE);
                                } else {
                                    Toast.makeText(AllTripActivity.this, resObj.getString("message").toString(), Toast.LENGTH_LONG).show();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else {
                        ProgressDialog.cancel();
                        cusRotateLoading.stop();

                       Common.ShowHttpErrorMessage(AllTripActivity.this, error.getMessage());
                    }
                }
            });

    }

public void FilterAllTrips(final int offset, final String filter){

    if(offset == 0)
        allTripArray = new ArrayList<>();

    Log.d("loadTripsUrl", "loadTripsUrl FilterString= " +Url.loadTripsFiltersUrl+"=="+FilterString+"=="+userPref.getString("id", "")+"=="+String.valueOf(offset));

    Ion.with(AllTripActivity.this)
    .load(Url.loadTripsFiltersUrl)
    .setTimeout(6000)
    //.setJsonObjectBody(json)
    .setMultipartParameter("user_id", userPref.getString("id", ""))
    .setMultipartParameter("off", String.valueOf(offset))
    .setMultipartParameter("filter", FilterString)
    .asJsonObject()
            .setCallback(new FutureCallback<JsonObject>() {
                @Override
                public void onCompleted(Exception error, JsonObject result) {
                    ProgressDialog.cancel();
                    cusRotateLoading.stop();
                    if (error == null) {
                        try {
                            JSONObject resObj = new JSONObject(result.toString());
                            Log.d("loadTripsUrl", "loadTripsUrl filter two= "+resObj.getString("status")+"=="+ resObj);
                            if (resObj.getString("status").equals("success")) {

                                recycle_all_trip.setEnabled(true);

                                JSONArray tripArray = new JSONArray(resObj.getString("all_trip"));
                                for (int t = 0; t < tripArray.length(); t++) {
                                    JSONObject trpObj = tripArray.getJSONObject(t);
                                    AllTripFeed allTripFeed = new AllTripFeed();
                                    allTripFeed.setBookingId(trpObj.getString("id"));
                                    allTripFeed.setDropArea(trpObj.getString("drop_area"));
                                    allTripFeed.setPickupArea(trpObj.getString("pickup_area"));
                                    allTripFeed.setTaxiType(trpObj.getString("car_type"));
                                    allTripFeed.setPickupDateTime(trpObj.getString("book_create_date_time"));
                                    allTripFeed.setAmount(trpObj.getString("amount"));
                                    allTripFeed.setCarIcon(trpObj.getString("icon"));
                                    allTripFeed.setKm(trpObj.getString("km"));
                                    allTripFeed.setDriverDetail(trpObj.getString("driver_detail"));
                                    allTripFeed.setStatus(trpObj.getString("status"));
                                    allTripFeed.setApproxTime(trpObj.getString("approx_time"));
                                    allTripFeed.setOldLocationList(null);
                                    allTripFeed.setStartPickLatLng(trpObj.getString("pickup_lat"));
                                    allTripFeed.setEndPickLatLng(trpObj.getString("pickup_longs"));
                                    allTripFeed.setStartDropLatLng(trpObj.getString("drop_lat"));
                                    allTripFeed.setEndDropLatLng(trpObj.getString("drop_longs"));
                                    allTripArray.add(allTripFeed);
                                }
                                Log.d("loadTripsUrl", "loadTripsUrl three= " + allTripArray.size());
                                if (allTripArray != null && allTripArray.size() > 0) {
                                    layout_recycleview.setVisibility(View.VISIBLE);
                                    layout_no_recourd_found.setVisibility(View.GONE);
                                    if (offset == 0) {
                                        allTripAdapter = new AllTripAdapter(AllTripActivity.this, allTripArray);
                                        recycle_all_trip.setAdapter(allTripAdapter);
                                        allTripAdapter.setOnAllTripItemClickListener(AllTripActivity.this);
                                        swipe_refresh_layout.setRefreshing(false);

                                    }
                                    allTripAdapter.updateItemsFilter(allTripArray);
                                    if (swipe_refresh_layout != null)
                                        swipe_refresh_layout.setEnabled(true);
                                }
                            }else if(resObj.getString("status").equals("false")){
                                Common.user_InActive = 1;
                                Common.InActive_msg = resObj.getString("message");

                                SharedPreferences.Editor editor = userPref.edit();
                                editor.clear();
                                editor.commit();

                                Intent logInt = new Intent(AllTripActivity.this, LoginOptionActivity.class);
                                logInt.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                logInt.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                logInt.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(logInt);
                            } else if (resObj.getString("status").equals("failed")) {
                                Log.d("allTripArray", "allTripArray = " + allTripArray.size());
                                if (swipe_refresh_layout != null)
                                    swipe_refresh_layout.setEnabled(false);
                                if (allTripAdapter != null)
                                    allTripAdapter.updateItemsFilter(allTripArray);

                                if (offset == 0) {
                                    ProgressDialog.cancel();
                                    cusRotateLoading.stop();
                                    layout_recycleview.setVisibility(View.GONE);
                                    layout_no_recourd_found.setVisibility(View.VISIBLE);
                                } else {
                                    Toast.makeText(AllTripActivity.this, resObj.getString("message").toString(), Toast.LENGTH_LONG).show();
                                }

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Common.ShowHttpErrorMessage(AllTripActivity.this, error.getMessage());
                    }
        }

    });

}

@Override
public void scrollToLoad(int position) {

    if (userPref.getBoolean("setFilter", true) == true && !userPref.getString("check all", "").equals("check all")) {
        if (userPref.getInt("pending booking", 0) == 1) {
            FilterString += 1 + ",";
        }
        if (userPref.getInt("complete booking", 0) == 9) {
            FilterString += 9 + ",";
        }
        if (userPref.getInt("driver unavailable", 0) == 6) {
            FilterString += 6 + ",";
        }
        if (userPref.getInt("user reject", 0) == 4) {
            FilterString += 4 + ",";
        }
        if (userPref.getInt("driver accept", 0) == 3) {
            FilterString += 3 + ",";
        }

        if(FilterString.length() > 0)
            FilterString = FilterString.substring(0, (FilterString.length() - 1));

        FilterAllTrips(position + 1, "");
        FilterString = "";
    } else {
        getAllTrip(position + 1);
    }

}

@Override
public void clickDetailTrip(int position) {

    if(allTripArray.size() > 0) {
        Common.allTripFeeds = allTripArray.get(position);
        Intent di = new Intent(AllTripActivity.this, BookingDetailActivity.class);
        startActivity(di);
    }
}

    @Override
    public void tripCancel(final int position) {

        final Dialog CancelBookingDialog = new Dialog(AllTripActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
        CancelBookingDialog.setContentView(R.layout.cancel_booking_dialog);
        CancelBookingDialog.show();

        chk_drive_late = (CheckBox) CancelBookingDialog.findViewById(R.id.chk_drive_late);
        chk_drive_late.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBoxCheck("driver_late");
            }
        });


        RelativeLayout layout_driver_late = (RelativeLayout) CancelBookingDialog.findViewById(R.id.layout_driver_late);
        layout_driver_late.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBoxCheck("driver_late");
            }
        });

        chk_changed_mind = (CheckBox) CancelBookingDialog.findViewById(R.id.chk_changed_mind);
        chk_changed_mind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBoxCheck("changed_mind");
            }
        });

        RelativeLayout layout_change_mind = (RelativeLayout) CancelBookingDialog.findViewById(R.id.layout_change_mind);
        layout_change_mind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBoxCheck("changed_mind");
            }
        });

        chk_another_cab = (CheckBox) CancelBookingDialog.findViewById(R.id.chk_another_cab);
        chk_another_cab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBoxCheck("another_cab");
            }
        });
        RelativeLayout layout_another_cab = (RelativeLayout) CancelBookingDialog.findViewById(R.id.layout_another_cab);
        layout_another_cab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBoxCheck("another_cab");
            }
        });
        chk_denied_duty = (CheckBox) CancelBookingDialog.findViewById(R.id.chk_denied_duty);
        chk_denied_duty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBoxCheck("denied_duty");
            }
        });
        RelativeLayout layout_denied_dute = (RelativeLayout) CancelBookingDialog.findViewById(R.id.layout_denied_dute);
        layout_denied_dute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBoxCheck("denied_duty");
            }
        });

        RelativeLayout layout_dont_cancel = (RelativeLayout) CancelBookingDialog.findViewById(R.id.layout_dont_cancel);
        layout_dont_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CancelBookingDialog.cancel();
            }
        });

        RelativeLayout layout_cancel_ride = (RelativeLayout) CancelBookingDialog.findViewById(R.id.layout_cancel_ride);
        layout_cancel_ride.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CancelBookingDialog.cancel();

                ProgressDialog.show();
                cusRotateLoading.start();

                SelTripFeeds = allTripArray.get(position);

                Log.d("deleteCabUrl","deleteCabUrl = "+Url.deleteCabUrl+"?"+SelTripFeeds.getBookingId()+"=="+userPref.getString("id", ""));
                Ion.with(AllTripActivity.this)
                    .load(Url.deleteCabUrl + "?booking_id=" + SelTripFeeds.getBookingId() + "&uid=" + userPref.getString("id", ""))
                    .setTimeout(6000)
                            //.setJsonObjectBody(json)
//                        .setMultipartParameter("booking_id", SelTripFeeds.getBookingId())
//                        .setMultipartParameter("uid", userPref.getString("id", ""))
                        .asJsonObject()
                        .setCallback(new FutureCallback<JsonObject>() {
                            @Override
                            public void onCompleted(Exception error, JsonObject result) {
                                ProgressDialog.cancel();
                                cusRotateLoading.stop();
                                if (error == null) {
                                    try {
                                        JSONObject resObj = new JSONObject(result.toString());
                                        if (resObj.getString("status").equals("success")) {
                                            SelTripFeeds.setStatus("4");
                                            allTripAdapter.notifyItemChanged(position);

                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Intent homeInt = new Intent(AllTripActivity.this, HomeActivity.class);
                                                    homeInt.putExtra("cancel_booking", "1");
                                                    startActivity(homeInt);
                                                    finish();
                                                }
                                            }, 1000);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    ProgressDialog.cancel();
                                    cusRotateLoading.stop();
                                    Common.ShowHttpErrorMessage(AllTripActivity.this, error.getMessage());
                                }
                            }

                        });

            }
        });

    }

    public void CheckBoxCheck(String CheckString) {

        if (CheckString.equals("driver_late"))
            chk_drive_late.setChecked(true);
        else
            chk_drive_late.setChecked(false);

        Log.d("CheckString", "CheckString = " + CheckString);
        if (CheckString.equals("changed_mind"))
            chk_changed_mind.setChecked(true);
        else
            chk_changed_mind.setChecked(false);

        if (CheckString.equals("another_cab"))
            chk_another_cab.setChecked(true);
        else
            chk_another_cab.setChecked(false);

        if (CheckString.equals("denied_duty"))
            chk_denied_duty.setChecked(true);
        else
            chk_denied_duty.setChecked(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        layout_slidemenu = null;
        txt_all_trip = null;
        layout_filter = null;
        recycle_all_trip = null;
        swipe_refresh_layout = null;
        layout_background = null;
        layout_no_recourd_found = null;
        layout_recycleview = null;
        chk_all = null;
        chk_pen_book = null;
        chk_com_book = null;
        chk_drv_reject = null;
        chk_user_reject = null;
        allTripAdapter = null;

        unregisterReceiver(receiver);
    }

    @Override
    public void onResume() {
        super.onResume();

        IntentFilter filter = new IntentFilter("come.naqil.naqil.AllTripActivity");
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (Common.isNetworkAvailable(AllTripActivity.this)) {

                    ProgressDialog.show();
                    cusRotateLoading.start();

                    if (userPref.getBoolean("setFilter", false) == true) {
                        if (userPref.getInt("pending booking", 6) == 1) {
                            FilterString += 1 + ",";
                        }
                        if (userPref.getInt("complete booking", 0) == 9) {
                            FilterString += 9 + ",";
                        }
                        if (userPref.getInt("driver unavailable", 0) == 6) {
                            FilterString += 6 + ",";
                        }
                        if (userPref.getInt("user reject", 0) == 4) {
                            FilterString += 4 + ",";
                        }
                        if (userPref.getInt("user reject", 0) == 4) {
                            FilterString += 4 + ",";
                        }
                        if (userPref.getInt("driver accept", 0) == 3) {
                            FilterString += 3 + ",";
                        }

                        FilterString = FilterString.substring(0, (FilterString.length() - 1));
                        Log.d("FilterString", "FilterString = " + FilterString);

                        if (userPref.getInt("pending booking", 0) == 1 && userPref.getInt("complete booking", 0) == 9 && userPref.getInt("driver unavailable", 0) == 6 && userPref.getInt("user reject", 0) == 4 && userPref.getInt("driver accept", 0) == 3){
                            FilterString = "";
                        }

                        FilterAllTrips(0,"");
                        FilterString = "";
                    } else {
                        getAllTrip(0);
                    }
                } else {
                    //Network is not available
                    recycle_all_trip.setEnabled(true);
                    Common.showInternetInfo(AllTripActivity.this, "Network is not available");
                }
            }
        };
        registerReceiver(receiver, filter);


        if(Common.is_pusnotification == 1){
            Common.is_pusnotification = 0;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (Common.isNetworkAvailable(AllTripActivity.this)) {
                        ProgressDialog.show();
                        cusRotateLoading.start();
                        boolean allFilter;
                        Log.d("check Value", "check value = " + userPref.getInt("pending booking", 5) + "==" + userPref.getInt("user reject", 5) + "==" + userPref.getInt("driver unavailable", 5) + "==" + userPref.getInt("complete booking", 5));
                        if (userPref.getBoolean("setFilter", false) == true) {
                            if (userPref.getInt("pending booking", 0) == 1) {
                                FilterString += 1 + ",";
                            }
                            if (userPref.getInt("complete booking", 0) == 9) {
                                FilterString += 9 + ",";
                            }
                            if (userPref.getInt("driver unavailable", 0) == 6) {
                                FilterString += 6 + ",";
                            }
                            if (userPref.getInt("user reject", 0) == 4) {
                                FilterString += 4 + ",";
                            }
                            if (userPref.getInt("driver accept", 0) == 3) {
                                FilterString += 3 + ",";
                            }

                            FilterString = FilterString.substring(0, (FilterString.length() - 1));
                            Log.d("FilterString", "FilterString = " + FilterString);

                            if (userPref.getInt("pending booking", 0) == 1 && userPref.getInt("complete booking", 0) == 9 && userPref.getInt("driver unavailable", 0) == 6 && userPref.getInt("user reject", 0) == 4 && userPref.getInt("driver accept", 0) == 3) {
                                SharedPreferences.Editor checkAll = userPref.edit();
                                checkAll.putString("check all","check all");
                                checkAll.commit();
                                FilterString = "";
                            }else{
                                SharedPreferences.Editor checkAll = userPref.edit();
                                checkAll.putString("check all","");
                                checkAll.commit();
                            }
                            FilterAllTrips(0, "filter");
                            FilterString = "";
                        } else {
                            getAllTrip(0);
                        }

                    } else {
                        Common.showInternetInfo(AllTripActivity.this, "Network is not available");
                        swipe_refresh_layout.setEnabled(false);
                    }
                }
            }, 500);
        }

        common.SlideMenuDesign(slidingMenu, AllTripActivity.this, "all trip");
    }

    @Override
    public void onBackPressed() {

        if(slidingMenu.isMenuShowing()){
            slidingMenu.toggle();
        }else{
            new AlertDialog.Builder(this)
                    .setTitle("Really Exit?")
                    .setMessage("Are you sure you want to exit?")
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface arg0, int arg1) {
                            AllTripActivity.super.onBackPressed();
                        }
                    }).create().show();
        }
    }
}

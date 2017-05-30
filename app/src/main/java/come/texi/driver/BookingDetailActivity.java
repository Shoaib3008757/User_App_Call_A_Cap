package come.texi.driver;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.facebooklibrary.FBBean;
import com.app.facebooklibrary.FB_Callback;
import com.app.facebooklibrary.FacebookLoginClass;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;
import com.victor.loading.rotate.RotateLoading;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import come.texi.driver.adapter.AllTripAdapter;
import come.texi.driver.utils.AllTripFeed;
import come.texi.driver.utils.CircleTransform;
import come.texi.driver.utils.Common;
import come.texi.driver.utils.Url;
import cz.msebera.android.httpclient.Header;

public class BookingDetailActivity extends AppCompatActivity implements FB_Callback {


    TextView txt_booking_id,txt_cancel_request;
    TextView txt_booking_id_val;
    TextView txt_pickup_point;
    TextView txt_pickup_point_val;
    TextView txt_booking_date;
    TextView txt_drop_point;
    TextView txt_drop_point_val;
    ImageView img_car_image;
    TextView txt_distance;
    TextView txt_distance_val;
    TextView txt_distance_km;
    TextView txt_total_price;
    TextView txt_total_price_dol;
    TextView txt_total_price_val;
    TextView txt_booking_detail;
    TextView txt_track_truck;
    RelativeLayout layout_back_arrow;
    RelativeLayout layout_track_truck;
    LinearLayout layout_car_detail;
    LinearLayout layout_driver_detail;
    TextView txt_truct_type_val;
    RelativeLayout layout_cancel_request_button;
    ImageView img_driver_image;
    TextView txt_driver_name;
    TextView txt_drv_trc_typ;
    TextView txt_num_plate;
    TextView txt_mobile_num;
    TextView txt_lic_num;
    ScrollView scroll_view;
    LinearLayout layout_accepted;
    RelativeLayout layout_accepted_call;
    LinearLayout layout_completed;
    LinearLayout layout_cancel_user;
    LinearLayout layout_on_trip;
    LinearLayout layout_driver_unavailabel;
    LinearLayout layout_cancel_driver;
    RelativeLayout layout_pending;
    TextView txt_travel_time;
    TextView txt_travel_time_val,txt_to,txt_vehicle_detail,txt_payment_detail;

    RelativeLayout layout_accepted_share_eta;
    RelativeLayout layout_completed_eta;
    RelativeLayout layout_completed_eta_chield;
    RelativeLayout layout_share_on_trip;
    RelativeLayout layout_share_driver_unavailabel;
    RelativeLayout layout_share_cancel_driver;

    Typeface OpenSans_Regular,Roboto_Regular,Roboto_Medium,Roboto_Bold,OpenSans_Semibold;

    AllTripFeed allTripFeed;
    SharedPreferences userPref;

    Dialog ProgressDialog;
    RotateLoading cusRotateLoading;

    CheckBox chk_drive_late;
    CheckBox chk_changed_mind;
    CheckBox chk_another_cab;
    CheckBox chk_denied_duty;

    String DriverPhNo = "";
    BroadcastReceiver receiver;

    CallbackManager callbackManager;

    Dialog ShareDialog;

    String ShareDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_booking_detail);

        callbackManager = CallbackManager.Factory.create();

        txt_cancel_request = (TextView)findViewById(R.id.txt_cancel_request);
        txt_booking_id = (TextView)findViewById(R.id.txt_booking_id);
        txt_booking_id_val = (TextView)findViewById(R.id.txt_booking_id_val);
        txt_pickup_point = (TextView)findViewById(R.id.txt_pickup_point);
        txt_pickup_point_val = (TextView)findViewById(R.id.txt_pickup_point_val);
        txt_booking_date = (TextView)findViewById(R.id.txt_booking_date);
        txt_drop_point = (TextView)findViewById(R.id.txt_drop_point);
        txt_drop_point_val = (TextView)findViewById(R.id.txt_drop_point_val);
        img_car_image = (ImageView)findViewById(R.id.img_car_image);
        txt_distance = (TextView)findViewById(R.id.txt_distance);
        txt_distance_val = (TextView)findViewById(R.id.txt_distance_val);
        txt_distance_km = (TextView)findViewById(R.id.txt_distance_km);
        txt_total_price = (TextView)findViewById(R.id.txt_total_price);
        txt_total_price_dol = (TextView)findViewById(R.id.txt_total_price_dol);
        txt_total_price_val = (TextView)findViewById(R.id.txt_total_price_val);
        txt_booking_detail = (TextView)findViewById(R.id.txt_booking_detail);
        txt_track_truck = (TextView)findViewById(R.id.txt_track_truck);
        layout_back_arrow = (RelativeLayout)findViewById(R.id.layout_back_arrow);
        layout_track_truck = (RelativeLayout)findViewById(R.id.layout_track_truck);
        layout_car_detail = (LinearLayout)findViewById(R.id.layout_car_detail);
        layout_driver_detail = (LinearLayout)findViewById(R.id.layout_driver_detail);
        txt_truct_type_val = (TextView)findViewById(R.id.txt_truct_type_val);
        layout_cancel_request_button = (RelativeLayout) findViewById(R.id.layout_cancel_request_button);
        img_driver_image = (ImageView)findViewById(R.id.img_driver_image);
        txt_driver_name = (TextView)findViewById(R.id.txt_driver_name);
        txt_drv_trc_typ = (TextView)findViewById(R.id.txt_drv_trc_typ);
        txt_num_plate = (TextView)findViewById(R.id.txt_num_plate);
        txt_mobile_num = (TextView)findViewById(R.id.txt_mobile_num);
        txt_lic_num = (TextView)findViewById(R.id.txt_lic_num);
        scroll_view = (ScrollView)findViewById(R.id.scroll_view);
        layout_accepted = (LinearLayout)findViewById(R.id.layout_accepted);
        layout_accepted_call = (RelativeLayout)findViewById(R.id.layout_accepted_call);
        layout_completed = (LinearLayout)findViewById(R.id.layout_completed);
        layout_cancel_user = (LinearLayout)findViewById(R.id.layout_cancel_user);
        layout_on_trip = (LinearLayout)findViewById(R.id.layout_on_trip);
        layout_driver_unavailabel = (LinearLayout)findViewById(R.id.layout_driver_unavailabel);
        layout_cancel_driver = (LinearLayout)findViewById(R.id.layout_cancel_driver);
        layout_pending = (RelativeLayout)findViewById(R.id.layout_pending);
        txt_travel_time = (TextView)findViewById(R.id.txt_travel_time);
        txt_travel_time_val = (TextView)findViewById(R.id.txt_travel_time_val);
        txt_to = (TextView)findViewById(R.id.txt_to);
        txt_vehicle_detail = (TextView)findViewById(R.id.txt_vehicle_detail);
        txt_payment_detail = (TextView)findViewById(R.id.txt_payment_detail);

        userPref = PreferenceManager.getDefaultSharedPreferences(BookingDetailActivity.this);

        OpenSans_Regular = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Regular_0.ttf");
        Roboto_Regular = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
        Roboto_Medium = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Medium.ttf");
        Roboto_Bold = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Bold_0.ttf");
        OpenSans_Semibold = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Semibold_0.ttf");

        txt_booking_detail.setTypeface(OpenSans_Regular);
        txt_track_truck.setTypeface(Roboto_Bold);
        txt_to.setTypeface(Roboto_Bold);
        txt_vehicle_detail.setTypeface(Roboto_Bold);
        txt_payment_detail.setTypeface(Roboto_Bold);
        txt_cancel_request.setTypeface(Roboto_Bold);

        allTripFeed = Common.allTripFeeds;

        ProgressDialog = new Dialog(BookingDetailActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
        ProgressDialog.setContentView(R.layout.custom_progress_dialog);
        ProgressDialog.setCancelable(false);
        cusRotateLoading = (RotateLoading)ProgressDialog.findViewById(R.id.rotateloading_register);


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String pickup_date_time = "";
        try {
            Date parceDate = simpleDateFormat.parse(allTripFeed.getPickupDateTime());
            SimpleDateFormat parceDateFormat = new SimpleDateFormat("h:mm a,dd,MMM yyyy");
            pickup_date_time = parceDateFormat.format(parceDate.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        txt_booking_id_val.setText(allTripFeed.getBookingId());
        txt_pickup_point_val.setText(allTripFeed.getPickupArea());
        txt_drop_point_val.setText(allTripFeed.getDropArea());
        txt_booking_date.setText(pickup_date_time);
        txt_distance_val.setText(allTripFeed.getKm());
        txt_total_price_val.setText(allTripFeed.getAmount());
        Log.d("TaxiType","TaxiType = "+allTripFeed.getTaxiType());
        txt_truct_type_val.setText(allTripFeed.getTaxiType());
        txt_travel_time_val.setText(allTripFeed.getApproxTime());

        txt_booking_id.setTypeface(Roboto_Regular);
        txt_pickup_point.setTypeface(Roboto_Regular);
        txt_booking_date.setTypeface(Roboto_Regular);
        txt_drop_point.setTypeface(Roboto_Regular);
        txt_distance_km.setTypeface(Roboto_Regular);
        txt_total_price_dol.setTypeface(Roboto_Regular);
        txt_total_price_dol.setText(Common.Currency);


        txt_pickup_point_val.setTypeface(OpenSans_Regular);
        txt_booking_date.setTypeface(OpenSans_Regular);
        txt_drop_point_val.setTypeface(OpenSans_Regular);
        txt_distance.setTypeface(OpenSans_Regular);
        txt_distance_val.setTypeface(OpenSans_Regular);
        txt_total_price.setTypeface(OpenSans_Regular);
        txt_total_price_val.setTypeface(OpenSans_Regular);
        txt_truct_type_val.setTypeface(OpenSans_Regular);
        txt_travel_time.setTypeface(OpenSans_Regular);
        txt_travel_time_val.setTypeface(OpenSans_Regular);

        txt_driver_name.setTypeface(Roboto_Regular);
        txt_drv_trc_typ.setTypeface(Roboto_Regular);
        txt_num_plate.setTypeface(Roboto_Regular);
        txt_mobile_num.setTypeface(Roboto_Regular);
        txt_lic_num.setTypeface(Roboto_Regular);


        if(allTripFeed.getDriverDetail() != null && allTripFeed.getDriverDetail().equals("null")){
            layout_car_detail.setVisibility(View.VISIBLE);
            layout_driver_detail.setVisibility(View.GONE);
            Log.d("allTripFeed", "allTripFeed = " + Url.carImageUrl + allTripFeed.getCarIcon());
            Picasso.with(BookingDetailActivity.this)
                    .load(Uri.parse(Url.carImageUrl+allTripFeed.getCarIcon()))
                    .placeholder(R.drawable.truck_icon)
                    .transform(new CircleTransform())
                    .into(img_car_image);
            layout_track_truck.setEnabled(false);
        }else{
            layout_car_detail.setVisibility(View.GONE);
            layout_driver_detail.setVisibility(View.VISIBLE);
            txt_drv_trc_typ.setText(allTripFeed.getTaxiType());
            if(allTripFeed.getStatus().equals("9"))
                layout_track_truck.setEnabled(false);
            else
                layout_track_truck.setEnabled(true);
            try {
                JSONObject drvObj = new JSONObject(allTripFeed.getDriverDetail());
                txt_driver_name.setText(drvObj.getString("name"));

                txt_num_plate.setText(drvObj.getString("car_no"));
                DriverPhNo = drvObj.getString("phone");
                txt_mobile_num.setText(DriverPhNo);
                txt_lic_num.setText(drvObj.getString("license_plate"));

                Picasso.with(BookingDetailActivity.this)
                        .load(Uri.parse(Url.DriverImageUrl+drvObj.getString("image")))
                        .placeholder(R.drawable.avatar_placeholder)
                        .transform(new CircleTransform())
                        .into(img_driver_image);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Log.d("Status", "Status = " + allTripFeed.getStatus());

        if(allTripFeed.getStatus().equals("1")){
            layout_pending.setVisibility(View.VISIBLE);
        }else if(allTripFeed.getStatus().equals("3")){
            layout_accepted.setVisibility(View.VISIBLE);
        }else if(allTripFeed.getStatus().equals("9")){
            layout_completed.setVisibility(View.VISIBLE);
        }else if(allTripFeed.getStatus().equals("4")){
            layout_cancel_user.setVisibility(View.VISIBLE);
        }else if(allTripFeed.getStatus().equals("8")) {
            layout_on_trip.setVisibility(View.VISIBLE);
        }else if(allTripFeed.getStatus().equals("6")){
            layout_driver_unavailabel.setVisibility(View.VISIBLE);
        }else if(allTripFeed.getStatus().equals("5")){
            layout_pending.setVisibility(View.VISIBLE);
        }

        layout_cancel_request_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog CancelBookingDialog = new Dialog(BookingDetailActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
                CancelBookingDialog.setContentView(R.layout.cancel_booking_dialog);
                CancelBookingDialog.show();

                chk_drive_late = (CheckBox)CancelBookingDialog.findViewById(R.id.chk_drive_late);
                chk_drive_late.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CheckBoxCheck("driver_late");
                    }
                });


                RelativeLayout layout_driver_late = (RelativeLayout)CancelBookingDialog.findViewById(R.id.layout_driver_late);
                layout_driver_late.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CheckBoxCheck("driver_late");
                    }
                });

                chk_changed_mind = (CheckBox)CancelBookingDialog.findViewById(R.id.chk_changed_mind);
                chk_changed_mind.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CheckBoxCheck("changed_mind");
                    }
                });

                RelativeLayout layout_change_mind = (RelativeLayout)CancelBookingDialog.findViewById(R.id.layout_change_mind);
                layout_change_mind.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CheckBoxCheck("changed_mind");
                    }
                });

                chk_another_cab = (CheckBox)CancelBookingDialog.findViewById(R.id.chk_another_cab);
                chk_another_cab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CheckBoxCheck("another_cab");
                    }
                });
                RelativeLayout layout_another_cab = (RelativeLayout)CancelBookingDialog.findViewById(R.id.layout_another_cab);
                layout_another_cab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CheckBoxCheck("another_cab");
                    }
                });
                chk_denied_duty = (CheckBox)CancelBookingDialog.findViewById(R.id.chk_denied_duty);
                chk_denied_duty.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CheckBoxCheck("denied_duty");
                    }
                });
                RelativeLayout layout_denied_dute = (RelativeLayout)CancelBookingDialog.findViewById(R.id.layout_denied_dute);
                layout_denied_dute.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CheckBoxCheck("denied_duty");
                    }
                });

                RelativeLayout layout_dont_cancel = (RelativeLayout)CancelBookingDialog.findViewById(R.id.layout_dont_cancel);
                layout_dont_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CancelBookingDialog.cancel();
                    }
                });

                RelativeLayout layout_cancel_ride = (RelativeLayout)CancelBookingDialog.findViewById(R.id.layout_cancel_ride);
                layout_cancel_ride.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CancelBookingDialog.cancel();
                        DeleteCab();
                    }
                });
            }
        });


        layout_back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        layout_track_truck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent di = new Intent(BookingDetailActivity.this,TrackTruckActivity.class);
                startActivity(di);
            }
        });

        /*Footer click event*/
        layout_accepted_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent callIntent = new Intent(Intent.ACTION_DIAL);
                        callIntent.setData(Uri.parse("tel:"+DriverPhNo));
                        startActivity(callIntent);
                    }
                }, 100);
            }
        });

        /*Share Layout Start*/
        layout_accepted_share_eta = (RelativeLayout)findViewById(R.id.layout_accepted_share_eta);

        layout_accepted_share_eta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareDialog();
            }
        });

        layout_completed_eta = (RelativeLayout)findViewById(R.id.layout_completed_eta);
        layout_completed_eta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareDialog();
            }
        });
        layout_completed_eta_chield = (RelativeLayout)findViewById(R.id.layout_completed_eta_chield);
        layout_completed_eta_chield.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareDialog();
            }
        });

        layout_share_on_trip = (RelativeLayout)findViewById(R.id.layout_share_on_trip);
        layout_share_on_trip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareDialog();
            }
        });

        layout_share_driver_unavailabel = (RelativeLayout)findViewById(R.id.layout_share_driver_unavailabel);
        layout_share_driver_unavailabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareDialog();
            }
        });

        layout_share_cancel_driver = (RelativeLayout)findViewById(R.id.layout_share_cancel_driver);
        layout_share_cancel_driver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareDialog();
            }
        });

        ShareDesc = "Name : "+userPref.getString("name","")+",";
        ShareDesc += "Pickup Address : "+allTripFeed.getPickupArea()+",";
        ShareDesc += "Drop Address : "+allTripFeed.getDropArea()+",";

    /*Share Layout End*/
    }

    public void CheckBoxCheck(String CheckString){

        if(CheckString.equals("driver_late"))
            chk_drive_late.setChecked(true);
        else
            chk_drive_late.setChecked(false);

        Log.d("CheckString","CheckString = "+CheckString);
        if(CheckString.equals("changed_mind"))
            chk_changed_mind.setChecked(true);
        else
            chk_changed_mind.setChecked(false);

        if(CheckString.equals("another_cab"))
            chk_another_cab.setChecked(true);
        else
            chk_another_cab.setChecked(false);

        if(CheckString.equals("denied_duty"))
            chk_denied_duty.setChecked(true);
        else
            chk_denied_duty.setChecked(false);
    }

    public void ShareDialog(){
        ShareDialog = new Dialog(BookingDetailActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
        ShareDialog.setContentView(R.layout.camera_dialog_layout);

        TextView facebook_text = (TextView)ShareDialog.findViewById(R.id.txt_open_camera);
        facebook_text.setText("Facebook Share");

        TextView twitter_text = (TextView)ShareDialog.findViewById(R.id.txt_open_gallery);
        twitter_text.setText("Twitter Share");

        RelativeLayout layout_open_camera = (RelativeLayout) ShareDialog.findViewById(R.id.layout_open_camera);
        layout_open_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareDialog.cancel();
                ShareFacebookLink(ShareDesc);
            }
        });

        RelativeLayout layout_open_gallery = (RelativeLayout) ShareDialog.findViewById(R.id.layout_open_gallery);
        layout_open_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareDialog.cancel();
                ShareTwitterLink();

            }
        });

        RelativeLayout layout_open_cancel = (RelativeLayout) ShareDialog.findViewById(R.id.layout_open_cancel);
        layout_open_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareDialog.cancel();
            }
        });

        ShareDialog.show();
    }

    public void ShareFacebookLink(final String Description){
        boolean loggedIn = AccessToken.getCurrentAccessToken() != null;
        if(loggedIn) {
            FacebookLoginClass shareLink = new FacebookLoginClass(BookingDetailActivity.this, callbackManager);
            shareLink.postStatusUpdate("NaqilCom", Description, Url.AppLogUrl, "");
        }else{

            callbackManager = CallbackManager.Factory.create();

            LoginManager.getInstance().logInWithPublishPermissions(BookingDetailActivity.this, Arrays.asList("publish_actions"));

            LoginManager.getInstance().registerCallback(callbackManager,
                    new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(final LoginResult loginResult) {

                            Log.d("loginResult", "loginResult = " + loginResult);
                            // App code
                            GraphRequest request = GraphRequest.newMeRequest(
                                    loginResult.getAccessToken(),
                                    new GraphRequest.GraphJSONObjectCallback() {
                                        @Override
                                        public void onCompleted(
                                                JSONObject object,
                                                GraphResponse response) {
                                            Log.d("object", "object = " + object + "==" + response);

                                            if (object != null) {
                                                FacebookLoginClass shareLink = new FacebookLoginClass(BookingDetailActivity.this, callbackManager);
                                                shareLink.postStatusUpdate("NaqilCom", Description, Url.AppLogUrl, "");

                                            } else {
                                                Toast.makeText(BookingDetailActivity.this, "Something went wrong", Toast.LENGTH_LONG);
                                            }


                                        }
                                    });
                            Bundle parameters = new Bundle();
                            parameters.putString("fields", "id,name,email");
                            request.setParameters(parameters);
                            request.executeAsync();
                        }

                        @Override
                        public void onCancel() {
                            Log.d("cancel", "cancel = ");
                        }

                        @Override
                        public void onError(FacebookException exception) {
                            Log.d("fb error", "fb error = " + exception.getMessage());
                            System.out.println("exception >>" + exception.getMessage());
                        }
                    });
        }
    }

    public void ShareTwitterLink(){
        String twitterUrl = Url.SocialShareUrl + "?uid=" + userPref.getString("id", "id");
        TweetComposer.Builder builder = null;


        builder = new TweetComposer.Builder(BookingDetailActivity.this)
                .text(ShareDesc)
                //.url(new URL(twitterUrl));

         .image(Uri.parse(Url.AppLogUrl));


        Intent intent = builder.createIntent();
        intent.setType("text/plain");
        startActivityForResult(intent, 111);
    }

    public void DeleteCab(){

        ProgressDialog.show();
        cusRotateLoading.start();

        Ion.with(BookingDetailActivity.this)
            .load(Url.deleteCabUrl + "?booking_id=" + allTripFeed.getBookingId() + "&uid=" + userPref.getString("id", ""))
            .setTimeout(10000)
            .asJsonObject()
            .setCallback(new FutureCallback<JsonObject>() {
                @Override
                public void onCompleted(Exception error, JsonObject result) {
                    // do stuff with the result or error
                    Log.d("Login result", "Login result = " + result + "==" + error);
                    if (error == null) {

                        ProgressDialog.cancel();
                        cusRotateLoading.stop();

                        try {
                            JSONObject resObj = new JSONObject(result.toString());
                            if (resObj.getString("status").equals("success")) {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent homeInt = new Intent(BookingDetailActivity.this, HomeActivity.class);
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

                        Common.ShowHttpErrorMessage(BookingDetailActivity.this, error.getMessage());
                    }
                }
            });

    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first

        IntentFilter filter = new IntentFilter("come.naqil.naqil.BookingDetailActivity");

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Common.is_pusnotification = 1;
            }
        };
        registerReceiver(receiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        txt_booking_id = null;
        txt_booking_id_val = null;
        txt_pickup_point = null;
        txt_pickup_point_val = null;
        txt_booking_date = null;
        txt_drop_point = null;
        txt_drop_point_val = null;
        img_car_image = null;
        txt_distance = null;
        txt_distance_val = null;
        txt_distance_km = null;
        txt_total_price = null;
        txt_total_price_dol = null;
        txt_total_price_val = null;
        txt_booking_detail = null;
        txt_track_truck = null;
        layout_back_arrow = null;
        layout_track_truck = null;

        unregisterReceiver(receiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 111) {
            if (resultCode == RESULT_OK) {
                if (data.getExtras().containsKey("extra_is_retweet")) {
                    boolean isReTweet = data.getExtras().getBoolean("extra_is_retweet");
                    if (isReTweet) {
                        Toast.makeText(BookingDetailActivity.this,"Duplicate Tweet. This tweet has been posted very recently",Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(BookingDetailActivity.this,"Your post was shared successfully.",Toast.LENGTH_LONG).show();
                    }
                    System.out.println("Image ID>>>>" + data.getExtras().getString("image_id"));
                }

            }
        }
    }

    @Override
    public void onLoginSuccess(FBBean beanObject) {

    }

    @Override
    public void onLoginFailure(String message) {
        Toast.makeText(BookingDetailActivity.this,message,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPostSuccess(String postID, String message) {
        Toast.makeText(BookingDetailActivity.this,message,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPostFailure(String message) {
        Toast.makeText(BookingDetailActivity.this,message,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLogout() {

    }
}

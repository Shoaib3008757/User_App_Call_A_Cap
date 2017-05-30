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
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDex;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import io.socket.client.IO;
import io.socket.client.Socket;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import come.texi.driver.utils.AllTripFeed;
import come.texi.driver.utils.CircleTransform;
import come.texi.driver.utils.Common;
import come.texi.driver.utils.DistanceUtil;
import come.texi.driver.utils.Url;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.ResponseHandler;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.params.HttpConnectionParams;
import cz.msebera.android.httpclient.params.HttpParams;
import cz.msebera.android.httpclient.util.EntityUtils;
import io.socket.emitter.Emitter;

public class TrackTruckActivity extends FragmentActivity implements OnMapReadyCallback {

    TextView txt_track_truck;
    TextView txt_month;
    TextView txt_date;
    TextView txt_locaton;
    TextView txt_point_val;
    TextView txt_remaining;
    TextView txt_remaining_val;
    TextView txt_covered;
    TextView txt_covered_val;
    TextView txt_total_km;
    TextView txt_total_km_val;
    RelativeLayout layout_main_drv_car;
    RelativeLayout layout_white_main;
    LinearLayout layout_footer;
    RelativeLayout layout_location_heading;
    RelativeLayout layout_dirvier_car;
    LinearLayout layout_booking_detail;
    TextView txt_driver_other;
    TextView txt_driver_name;
    TextView txt_mob_num;
    TextView txt_car_name;
    TextView txt_car_model;
    TextView txt_number_plate;
    TextView txt_pickup_address;
    TextView txt_drop_address;
    TextView txt_contact;
    TextView txt_share_eta;
    TextView txt_cancel,txt_location;
    ImageView img_drv_car_img;
    ImageView img_drv_img;
    RelativeLayout layout_back_arrow;
    LinearLayout layout_main_linear;
    RelativeLayout layout_contact;
    RelativeLayout layout_share;
    RelativeLayout layout_cancel;

    LatLng OldLatLog;


    Typeface OpenSans_Regular, OpenSans_Bold, Roboto_Regular, Roboto_Medium, Roboto_Bold, OpenSans_Semi_Bold;

    private GoogleMap googleMap;

    int devise_height;

    boolean animationStart = true;
    boolean MapLayoutClick = false;
    boolean FooterMapLayoutClick = false;

    private ArrayList<LatLng> arrayPoints = null;
    MarkerOptions markerOption;
    MarkerOptions DriverMarkerOption;
    Marker DriverMarker;

    int markerPositon;

    Socket mSocket;

    Animation slideUpAnimation, slideDownAnimation;

    Dialog ShareDialog;
    String DriverPhNo = "";
    SharedPreferences userPref;
    BroadcastReceiver receiver;

    AllTripFeed allTripFeed;

    int LatLngCom = 0;
    float googleZoom = 0;
    double CoverdValue = 0;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_truck);

        System.out.println(distance(22.3038866,70.802139,22.4709549,70.0577137, "M") + " Miles\n");
        System.out.println(distance(22.2812257,70.7762461,22.2810114,70.7757268, "K") + " Kilometers\n");
        System.out.println(distance(22.3038866,70.802139,22.4709549,70.0577137, "N") + " Nautical Miles\n");

        System.out.println(getDistance(new LatLng(22.2812257,70.7762461),new LatLng(22.2810114,70.7757268))+ " Nautical Miles Test\n");

        userPref = PreferenceManager.getDefaultSharedPreferences(TrackTruckActivity.this);

        txt_track_truck = (TextView)findViewById(R.id.txt_track_truck);
        txt_month = (TextView)findViewById(R.id.txt_month);
        txt_date = (TextView)findViewById(R.id.txt_date);
        txt_locaton = (TextView)findViewById(R.id.txt_locaton);
        txt_point_val = (TextView)findViewById(R.id.txt_point_val);
        txt_remaining = (TextView)findViewById(R.id.txt_remaining);
        txt_remaining_val = (TextView)findViewById(R.id.txt_remaining_val);
        txt_covered = (TextView)findViewById(R.id.txt_covered);
        txt_covered_val = (TextView)findViewById(R.id.txt_covered_val);
        txt_total_km = (TextView)findViewById(R.id.txt_total_km);
        txt_total_km_val = (TextView)findViewById(R.id.txt_total_km_val);
        layout_main_drv_car = (RelativeLayout)findViewById(R.id.layout_main_drv_car);
        layout_white_main = (RelativeLayout)findViewById(R.id.layout_white_main);
        layout_footer = (LinearLayout)findViewById(R.id.layout_footer);
        layout_location_heading = (RelativeLayout) findViewById(R.id.layout_location_heading);
        layout_dirvier_car = (RelativeLayout)findViewById(R.id.layout_dirvier_car);
        layout_booking_detail = (LinearLayout)findViewById(R.id.layout_booking_detail);
        txt_driver_other = (TextView)findViewById(R.id.txt_driver_other);
        txt_driver_name = (TextView)findViewById(R.id.txt_driver_name);
        txt_mob_num = (TextView)findViewById(R.id.txt_mob_num);
        txt_car_name = (TextView)findViewById(R.id.txt_car_name);
        txt_car_model = (TextView)findViewById(R.id.txt_car_model);
        txt_number_plate = (TextView)findViewById(R.id.txt_number_plate);
        txt_pickup_address = (TextView)findViewById(R.id.txt_pickup_address);
        txt_drop_address = (TextView)findViewById(R.id.txt_drop_address);
        txt_contact = (TextView)findViewById(R.id.txt_contact);
        txt_share_eta = (TextView)findViewById(R.id.txt_share_eta);
        txt_cancel = (TextView)findViewById(R.id.txt_cancel);
        img_drv_car_img = (ImageView)findViewById(R.id.img_drv_car_img);
        img_drv_img = (ImageView)findViewById(R.id.img_drv_img);
        layout_back_arrow = (RelativeLayout)findViewById(R.id.layout_back_arrow);
        layout_main_linear = (LinearLayout)findViewById(R.id.layout_main_linear);
        layout_contact = (RelativeLayout)findViewById(R.id.layout_contact);
        layout_share = (RelativeLayout)findViewById(R.id.layout_share);
        layout_cancel = (RelativeLayout)findViewById(R.id.layout_cancel);
        txt_location = (TextView)findViewById(R.id.txt_location);

        allTripFeed = Common.allTripFeeds;

        arrayPoints = new ArrayList<LatLng>();

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        devise_height = displaymetrics.heightPixels;

        OpenSans_Regular = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Regular_0.ttf");
        OpenSans_Bold = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Bold_0.ttf");
        OpenSans_Semi_Bold = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Semibold_0.ttf");
        Roboto_Regular = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
        Roboto_Medium = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Medium.ttf");
        Roboto_Bold = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Bold.ttf");

        txt_location.setTypeface(Roboto_Bold);
        txt_track_truck.setTypeface(OpenSans_Bold);
        txt_month.setTypeface(OpenSans_Semi_Bold);
        txt_date.setTypeface(OpenSans_Bold);
        txt_locaton.setTypeface(OpenSans_Regular);
        txt_point_val.setTypeface(OpenSans_Bold);
        txt_remaining.setTypeface(OpenSans_Regular);
        txt_covered.setTypeface(OpenSans_Regular);
        txt_total_km.setTypeface(OpenSans_Regular);
        txt_remaining_val.setTypeface(Roboto_Medium);
        txt_covered_val.setTypeface(Roboto_Medium);
        txt_total_km_val.setTypeface(Roboto_Medium);

        txt_driver_other.setTypeface(OpenSans_Regular);
        txt_driver_name.setTypeface(OpenSans_Regular);
        txt_mob_num.setTypeface(OpenSans_Regular);
        txt_car_name.setTypeface(OpenSans_Regular);
        txt_car_model.setTypeface(OpenSans_Regular);
        txt_number_plate.setTypeface(OpenSans_Regular);
        txt_pickup_address.setTypeface(OpenSans_Regular);
        txt_drop_address.setTypeface(OpenSans_Regular);

        txt_contact.setTypeface(OpenSans_Semi_Bold);
        txt_share_eta.setTypeface(OpenSans_Semi_Bold);
        txt_cancel.setTypeface(OpenSans_Semi_Bold);

        allTripFeed.getPickupDateTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String pickup_date_time = "";
        try {
            Date parceDate = simpleDateFormat.parse(allTripFeed.getPickupDateTime());
            SimpleDateFormat parceDateFormat = new SimpleDateFormat("MMM dd");
            pickup_date_time = parceDateFormat.format(parceDate.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String[] pick_date_spl = pickup_date_time.split(" ");
        Log.d("pickup_date_time", "pickup_date_time = " + pickup_date_time);
        if (pick_date_spl.length > 0) {
            txt_month.setText(pick_date_spl[0]);
            txt_date.setText(pick_date_spl[1]);
        }


        txt_locaton.setText(allTripFeed.getDropArea());
        txt_total_km_val.setText(Double.parseDouble(allTripFeed.getKm()) + " km");
        txt_pickup_address.setText(allTripFeed.getPickupArea());
        txt_drop_address.setText(allTripFeed.getDropArea());
        txt_remaining_val.setText(Double.parseDouble(allTripFeed.getKm()) + " km");
        txt_car_name.setText(allTripFeed.getTaxiType());
        /*Driver Layout*/

        if (allTripFeed.getDriverDetail() != null && !allTripFeed.getDriverDetail().equals("")) {

            try {
                final JSONObject drvObj = new JSONObject(allTripFeed.getDriverDetail());
                txt_driver_name.setText(drvObj.getString("name"));
                txt_driver_other.setText(drvObj.getString("address"));
                DriverPhNo = drvObj.getString("phone");
                txt_mob_num.setText(DriverPhNo);


                txt_car_model.setText(drvObj.getString("car_no"));
                txt_number_plate.setText(drvObj.getString("license_plate"));

                Picasso.with(TrackTruckActivity.this)
                        .load(Uri.parse(Url.DriverImageUrl+drvObj.getString("image")))
                        .placeholder(R.drawable.avatar_placeholder)
                        .transform(new CircleTransform())
                        .into(img_drv_car_img);

                Picasso.with(TrackTruckActivity.this)
                        .load(Uri.parse(Url.carImageUrl+allTripFeed.getCarIcon()))
                        .placeholder(R.drawable.mail_defoult)
                        .transform(new CircleTransform())
                        .into(img_drv_img);


                        try {
                            mSocket = IO.socket(Url.socketUrl);
                            mSocket.emit(Socket.EVENT_CONNECT_ERROR, onConnectError);
                            mSocket.connect();

                            JSONObject userobj = new JSONObject();
                            userobj.put("driver_id", drvObj.getInt("id"));
                            Log.d("connected ", "connected one = " + mSocket.connected() + "==" + userobj);
                            mSocket.emit("New User Register", userobj);

                            mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
                            mSocket.on("Driver Detail", onSocketConnectionListener);
                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }



            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        layout_contact.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                FooterMapLayoutClick = false;
                return false;
            }
        });
        layout_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FooterMapLayoutClick = true;
                animationStart = false;
                DropDownAnim anim = new DropDownAnim(layout_main_drv_car, (int) (devise_height * 0.55), animationStart);
                anim.setDuration(50);
                layout_main_drv_car.startAnimation(anim);
                animationStart = true;
                layout_dirvier_car.setBackgroundResource(R.drawable.track_truck_footer_bg);

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

        layout_share.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                FooterMapLayoutClick = false;
                return false;
            }
        });

        layout_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareDialog = new Dialog(TrackTruckActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
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
                        DropDownAnim anim = new DropDownAnim(layout_main_drv_car, (int) (devise_height * 0.55), animationStart);
                        anim.setDuration(50);
                        layout_main_drv_car.startAnimation(anim);
                        animationStart = true;
                        layout_dirvier_car.setBackgroundResource(R.drawable.track_truck_footer_bg);

                    }
                });

                RelativeLayout layout_open_gallery = (RelativeLayout) ShareDialog.findViewById(R.id.layout_open_gallery);
                layout_open_gallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ShareDialog.cancel();
                        DropDownAnim anim = new DropDownAnim(layout_main_drv_car, (int) (devise_height * 0.55), animationStart);
                        anim.setDuration(50);
                        layout_main_drv_car.startAnimation(anim);
                        animationStart = true;
                        layout_dirvier_car.setBackgroundResource(R.drawable.track_truck_footer_bg);

                    }
                });

                RelativeLayout layout_open_cancel = (RelativeLayout) ShareDialog.findViewById(R.id.layout_open_cancel);
                layout_open_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ShareDialog.cancel();
                        FooterMapLayoutClick = true;
                    }
                });

                ShareDialog.show();
            }
        });

        layout_cancel.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                FooterMapLayoutClick = false;
                return false;
            }
        });
        layout_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                animationStart = false;
                DropDownAnim anim = new DropDownAnim(layout_main_drv_car, (int) (devise_height * 0.55), animationStart);
                anim.setDuration(50);
                layout_main_drv_car.startAnimation(anim);
                animationStart = true;
                layout_dirvier_car.setBackgroundResource(R.drawable.track_truck_footer_bg);
            }
        });

        layout_back_arrow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                MapLayoutClick = false;
                return false;
            }
        });

        layout_back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        layout_main_drv_car.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(!FooterMapLayoutClick)
                    FooterMapLayoutClick = true;
                return false;
            }
        });

        layout_main_drv_car.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!FooterMapLayoutClick)
                    FooterMapLayoutClick = true;
            }
        });

        layout_dirvier_car.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                FooterMapLayoutClick = false;
                return false;
            }
        });

        layout_dirvier_car.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_white_main.setEnabled(false);
                layout_dirvier_car.setEnabled(false);
                DropDownAnim anim = new DropDownAnim(layout_main_drv_car, (int) (devise_height * 0.55), animationStart);
                anim.setDuration(50);
                layout_main_drv_car.startAnimation(anim);

                if (animationStart) {
                    animationStart = false;
                    //MapLayoutClick = false;
                    layout_dirvier_car.setBackgroundResource(0);
                } else {
                    animationStart = true;
                   // MapLayoutClick = true;
                    layout_dirvier_car.setBackgroundResource(R.drawable.track_truck_footer_bg);
                }

            }
        });

        OldLatLog = new LatLng(Double.parseDouble(allTripFeed.getStartPickLatLng()),Double.parseDouble(allTripFeed.getEndPickLatLng()));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(checkReady()) {
                    if(allTripFeed.getOldLocationList() != null && allTripFeed.getOldLocationList().size() > 0) {
                        //Toast.makeText(TrackTruckActivity.this,"without Call",Toast.LENGTH_LONG).show();
                        drawDashedPolyLine(googleMap, allTripFeed.getOldLocationList(), getResources().getColor(R.color.red));
                        MarkerAdd();
                    }else {
                        //Toast.makeText(TrackTruckActivity.this,"with Call",Toast.LENGTH_LONG).show();
                        new DrowLineGoogleMap().execute();
                    }
                }
            }
        }, 500);
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first

        IntentFilter filter = new IntentFilter("come.naqil.naqil.TrackTruckActivity");

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Common.is_pusnotification = 1;
            }
        };
        registerReceiver(receiver, filter);

//        if(!FooterMapLayoutClick && animationStart){
//            animationStart = false;
//            DropDownAnim anim = new DropDownAnim(layout_main_drv_car, (int) (devise_height * 0.55), animationStart);
//            anim.setDuration(50);
//            animationStart = true;
//            layout_main_drv_car.startAnimation(anim);
//            layout_dirvier_car.setBackgroundResource(R.drawable.footer_bg_white);
//        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;

        Geocoder geocoder = new Geocoder(TrackTruckActivity.this, Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocationName("india",1);

            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);
                Log.d("addressList", "addressList = " + address.getLatitude() + "==" + address.getLongitude());
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(address.getLatitude(), address.getLongitude()))      // Sets the center of the map to location user
                        .zoom(10)                   // Sets the zoom
                        //.bearing(90)                // Sets the orientation of the camera to east
                        //.tilt(40)                   // Sets the tilt of the camera to 30 degrees
                        .build();                   // Creates a CameraPosition from the builder
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
               // MapLayoutClick = true;
               // layout_main_drv_car.getLayoutParams().height = (int) getResources().getDimension(R.dimen.height_100);
            }
        });

        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

                MapLayoutClick = true;

                slideDownAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.slide_down_map);
                layout_main_drv_car.startAnimation(slideDownAnimation);
                layout_main_linear.startAnimation(slideDownAnimation);
                FooterMapLayoutClick = false;

                // layout_main_drv_car.startAnimation(slideDownAnimation);
//                layout_booking_detail.setAlpha(0);
//                layout_main_drv_car.setAlpha(0);

            }
        });

        googleMap.setOnCameraMoveCanceledListener(new GoogleMap.OnCameraMoveCanceledListener() {
            @Override
            public void onCameraMoveCanceled() {
                Toast.makeText(TrackTruckActivity.this,"Zoom Cancel",Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("MapLayoutClick","MapLayoutClick Down= "+MapLayoutClick+"=="+FooterMapLayoutClick);
//                        if (MapLayoutClick) {
//                            layout_booking_detail.setAlpha(0);
//                            layout_main_drv_car.setAlpha(0);
//                        }
                        if(FooterMapLayoutClick){

                            FooterMapLayoutClick = false;
                            DropDownAnim anim = new DropDownAnim(layout_main_drv_car, (int) (devise_height * 0.55), animationStart);
                            anim.setDuration(500);
                            layout_main_drv_car.startAnimation(anim);
                            animationStart = true;
                            layout_dirvier_car.setBackgroundResource(R.drawable.track_truck_footer_bg);

                        }
                    }
                }, 20);

                break;
            case MotionEvent.ACTION_UP:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        Log.d("MapLayoutClick","MapLayoutClick Up = "+MapLayoutClick);
                        if(MapLayoutClick) {
                            slideUpAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                                    R.anim.slide_up_map);
                            layout_main_drv_car.startAnimation(slideUpAnimation);
                            layout_main_linear.startAnimation(slideUpAnimation);
                            MapLayoutClick = false;
                        }
                        //if(MapLayoutClick) {
                           // layout_main_drv_car.setAlpha(1);
                           // layout_booking_detail.setAlpha(1);
                        //}
                    }
                }, 50);
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    private boolean checkReady() {
        if (googleMap == null) {
            Toast.makeText(this, "Google Map not ready", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public class DropDownAnim extends Animation {
        private final int targetHeight;
        private final RelativeLayout relativeLayout;
        private final boolean down;

        public DropDownAnim(RelativeLayout rel, int targetHeight, boolean down) {
            this.relativeLayout = rel;
            this.targetHeight = targetHeight;
            this.down = down;

        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            int newHeight;
            if (down) {

                Log.d("newHeight","newHeight one= "+interpolatedTime);
                if(interpolatedTime != 0.0 && interpolatedTime >= 0.3) {
                    newHeight = (int) (targetHeight * interpolatedTime);
                    Log.d("newHeight","newHeight two= "+interpolatedTime+"=="+newHeight);
                }else{
                    newHeight = (int) getResources().getDimension(R.dimen.height_100);
                }
            } else {
                if(interpolatedTime <= 0.3) {
                    newHeight = (int) (targetHeight * (1 - interpolatedTime));
                }else{
                    newHeight = (int) getResources().getDimension(R.dimen.height_100);
                }
            }
            Log.d("newHeight","newHeight three= "+newHeight+"=="+interpolatedTime);
            relativeLayout.getLayoutParams().height = newHeight;
            relativeLayout.requestLayout();
            if(newHeight > targetHeight * 0.60)
                layout_location_heading.setVisibility(View.VISIBLE);
            else
                layout_location_heading.setVisibility(View.GONE);
            if(newHeight > targetHeight * 0.80) {
                layout_footer.setVisibility(View.VISIBLE);
                FooterMapLayoutClick = true;
                layout_white_main.setEnabled(true);
                layout_dirvier_car.setEnabled(true);
            }
            else {
                layout_footer.setVisibility(View.GONE);
                FooterMapLayoutClick = false;
            }
        }

        @Override
        public void initialize(int width, int height, int parentWidth,
                               int parentHeight) {
            super.initialize(width, height, parentWidth, parentHeight);
        }

        @Override
        public boolean willChangeBounds() {
            return true;
        }
    }

    public class DrowLineGoogleMap extends AsyncTask<String, Integer, String> {

        private String content =  null;

        String DrowLocUrl = "";
        public DrowLineGoogleMap(){
                //DrowLocUrl = "http://maps.googleapis.com/maps/api/directions/json?sensor=true&mode=driving&origin="+ URLEncoder.encode(txt_pickup_address.getText().toString(), "UTF-8")+"&destination="+URLEncoder.encode(txt_drop_address.getText().toString(),"UTF-8");
                DrowLocUrl = "http://maps.googleapis.com/maps/api/directions/json?sensor=true&mode=driving&origin="+allTripFeed.getStartPickLatLng()+","+allTripFeed.getEndPickLatLng()+"&destination="+allTripFeed.getStartDropLatLng()+","+allTripFeed.getEndDropLatLng();

        }

        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... params) {
            try
            {
                HttpClient client = new DefaultHttpClient();
                HttpParams HttpParams = client.getParams();
                HttpConnectionParams.setConnectionTimeout(HttpParams, 60 * 60 * 1000);
                HttpConnectionParams.setSoTimeout(HttpParams, 60 * 60 * 1000);
                Log.d("DrowLocUrl","DrowLocUrl = "+DrowLocUrl);
                HttpGet getMethod = new HttpGet(DrowLocUrl);
                //getMethod.setEntity(entity);
                client.execute(getMethod, new ResponseHandler<String>() {
                    @Override
                    public String handleResponse(HttpResponse httpResponse) throws ClientProtocolException, IOException {

                        HttpEntity httpEntity = httpResponse.getEntity();
                        content = EntityUtils.toString(httpEntity);
                        Log.d("Result >>>","DrowLocUrl Result One"+ content);

                        return null;
                    }
                });

            } catch(Exception e)
            {
                e.printStackTrace();
                Log.d("Indiaries", "DrowLocUrl Result error" + e);
                return e.getMessage();
            }
            return content;
        }

        @Override
        protected void onPostExecute(String result) {
            boolean isStatus = Common.ShowHttpErrorMessage(TrackTruckActivity.this,result);
            if(isStatus) {
                try {
                    JSONObject resObj = new JSONObject(result);
                    if (resObj.getString("status").toLowerCase().equals("ok")) {


                        JSONArray routArray = new JSONArray(resObj.getString("routes"));
                        JSONObject routObj = routArray.getJSONObject(0);
                        Log.d("geoObj", "geoObj one= " + routObj);
                        JSONArray legsArray = new JSONArray(routObj.getString("legs"));
                        JSONObject legsObj = legsArray.getJSONObject(0);

                        JSONObject duration = new JSONObject(legsObj.getString("duration"));
                        JSONObject distanceObj = new JSONObject(legsObj.getString("distance"));

                        String durText = duration.getString("text");
                        String[] durTextSpi = durText.split(" ");
                        Log.d("durTextSpi", "min  = durTextSpi = " + durTextSpi.length);
                        int hours = 0;
                        int mintus = 0;
                        if (durTextSpi.length == 4) {
                            hours = Integer.parseInt(durTextSpi[0]) * 3600;
                            mintus = Integer.parseInt(durTextSpi[2]);
                        } else if (durTextSpi.length == 2) {
                            if (durTextSpi[1].contains("mins"))
                                mintus = Integer.parseInt(durTextSpi[0]);
                            else
                                mintus = Integer.parseInt(durTextSpi[0]) * 3600;
                        }

                        /*Start Location Latitude Longitude*/

                        PolylineOptions polylineOptions = new PolylineOptions();
                        polylineOptions.color(getResources().getColor(R.color.red));
                        polylineOptions.width(5);
                        polylineOptions.visible(true);



                        JSONObject endObj = new JSONObject(legsObj.getString("end_location"));
                        allTripFeed.setDropLarLng(new LatLng(Double.parseDouble(endObj.getString("lat")), Double.parseDouble(endObj.getString("lng"))));

                        //arrayPoints.add(new LatLng(Endlat, Endlng));

                        //options.add(PickupLarLng);
                        if (arrayPoints.size() > 0)
                            arrayPoints.clear();


                        JSONObject strLocObj = new JSONObject(legsObj.getString("start_location"));
                        allTripFeed.setPickupLarLng(new LatLng(Double.parseDouble(strLocObj.getString("lat")), Double.parseDouble(strLocObj.getString("lng"))));
                        allTripFeed.setDriverLarLng(new LatLng(Double.parseDouble(strLocObj.getString("lat")), Double.parseDouble(strLocObj.getString("lng"))));
                        arrayPoints.add(new LatLng(Double.parseDouble(strLocObj.getString("lat")), Double.parseDouble(strLocObj.getString("lng"))));
                        JSONArray stepsArray = new JSONArray(legsObj.getString("steps"));
                        Log.d("Step Array Length","Step Array Length = "+stepsArray.length());
                        for (int si = 0; si < stepsArray.length(); si++) {

                            JSONObject strObj = stepsArray.getJSONObject(si);

                            /*Start Location Latitude Longitude*/
                            JSONObject StrLocObj = new JSONObject(strObj.getString("start_location"));
                            double startlat = Double.parseDouble(StrLocObj.getString("lat"));
                            double startlng = Double.parseDouble(StrLocObj.getString("lng"));

                            arrayPoints.add(new LatLng(startlat, startlng));

                            /*Start Location Latitude Longitude*/
                            JSONObject EndLocObj = new JSONObject(strObj.getString("end_location"));
                            double endlat = Double.parseDouble(EndLocObj.getString("lat"));
                            double endlng = Double.parseDouble(EndLocObj.getString("lng"));

                            arrayPoints.add(new LatLng(endlat, endlng));
                        }

                        allTripFeed.setOldLocationList(arrayPoints);
                        markerPositon = arrayPoints.size() - 1;

                       // MarkerMoveTimer(arrayPoints.size() * 2000);
                        MarkerAdd();
                        //polylineOptions.addAll(arrayPoints);
                        // googleMap.addPolyline(polylineOptions);

                            /*End Location Latitude Longitude*/
                        //options.add(DropLarLng);

                        drawDashedPolyLine(googleMap, arrayPoints, getResources().getColor(R.color.red));


                    } else {
                        Toast.makeText(TrackTruckActivity.this, getResources().getString(R.string.location_long), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /*Add marker function*/
    public void MarkerAdd(){

        if(checkReady()) {

            LatLngBounds.Builder builder = new LatLngBounds.Builder();

            if(allTripFeed.getPickupLarLng() != null) {
                markerOption = new MarkerOptions()
                        .position(allTripFeed.getPickupLarLng())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_path_point));
                googleMap.addMarker(markerOption);
                builder.include(markerOption.getPosition());

            }

            if(allTripFeed.getDropLarLng() != null) {
                markerOption = new MarkerOptions()
                        .position(allTripFeed.getDropLarLng())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_path_point));
                googleMap.addMarker(markerOption);
                builder.include(markerOption.getPosition());

                DriverMarkerOption = new MarkerOptions()
                        .position(allTripFeed.getDriverLarLng())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker));
                DriverMarker = googleMap.addMarker(DriverMarkerOption);
                builder.include(DriverMarker.getPosition());

            }

            // .icon(BitmapDescriptorFactory.fromResource(R.drawable.location_icon))

            if(allTripFeed.getDriverLarLng() != null || allTripFeed.getPickupLarLng() != null) {
                LatLngBounds bounds = builder.build();

                Log.d("areBoundsTooSmall", "areBoundsTooSmall = " + areBoundsTooSmall(bounds, 300));
                if (areBoundsTooSmall(bounds, 300)) {
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(bounds.getCenter(), 10));
                } else {

                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 50);
                    googleMap.animateCamera(cu, new GoogleMap.CancelableCallback() {

                        @Override
                        public void onFinish() {
                            CameraUpdate zout = CameraUpdateFactory.zoomBy((float) -1.0);
                            googleMap.animateCamera(zout);
                        }

                        @Override
                        public void onCancel() {
//                            CameraUpdate zout = CameraUpdateFactory.zoomBy((float) -1.0);
//                            googleMap.animateCamera(zout);
                        }
                    });

                }
            }

            googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {

                private float currentZoom = -1;

                @Override
                public void onCameraChange(CameraPosition pos) {
                    if (pos.zoom != currentZoom){
                        currentZoom = pos.zoom;
                        googleZoom = currentZoom;
                        Log.d("currentZoom","currentZoom = "+currentZoom);
                        // do you action here
                    }
                }
            });

            Log.d("googleZoom","googleZoom = "+googleZoom);

          // googleZoom = googleMap.getCameraPosition().zoom;
        }
    }

    public void WayMarker(LatLng WayDriverLarLng,String booking_status){
        if(DriverMarker != null)
            DriverMarker.remove();
        if(checkReady()) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            DriverMarker = googleMap.addMarker(new MarkerOptions()
                    .position(WayDriverLarLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker)));
            builder.include(DriverMarker.getPosition());

            LatLngBounds bounds = builder.build();

            Log.d("areBoundsTooSmall", "areBoundsTooSmall = "+googleZoom+"=="+ areBoundsTooSmall(bounds, 300));
            //if (areBoundsTooSmall(bounds, 300)) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(bounds.getCenter(), googleZoom));
            /*} else {

                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 50);
                googleMap.animateCamera(cu, new GoogleMap.CancelableCallback() {

                    @Override
                    public void onFinish() {
                        CameraUpdate zout = CameraUpdateFactory.zoomBy((float) -1.0);
                        googleMap.animateCamera(zout);
                    }

                    @Override
                    public void onCancel() {
                        CameraUpdate zout = CameraUpdateFactory.zoomBy((float) -1.0);
                        googleMap.animateCamera(zout);
                    }
                });

            }*/
        }


        Log.d("DriverLarLng","DriverLarLng = "+allTripFeed.getDriverLarLng().latitude+"=="+allTripFeed.getDriverLarLng().longitude+"=="+WayDriverLarLng.latitude+"=="+WayDriverLarLng.longitude);
        Log.d("booking_status","booking_status = "+booking_status);
        if(booking_status != null && !booking_status.equals("") && booking_status.equals("begin trip")) {
            Log.d("LatLngCom","LatLngCom = "+LatLngCom);
//            if(LatLngCom == 0)
//                new DistanceTwoLatLng(allTripFeed.getPickupLarLng(),WayDriverLarLng).execute();

            System.out.println(getDistance(new LatLng(OldLatLog.latitude,OldLatLog.longitude),new LatLng(WayDriverLarLng.latitude,WayDriverLarLng.longitude))+ " Metre Nautical Miles Test\n");

            CoverdValue = CoverdValue + getDistance(new LatLng(OldLatLog.latitude,OldLatLog.longitude),new LatLng(WayDriverLarLng.latitude,WayDriverLarLng.longitude));

            float TotalKmMet = (float) (Double.parseDouble(allTripFeed.getKm())*1000);
            if(TotalKmMet > CoverdValue) {
                double FinalRemValue = TotalKmMet - CoverdValue;

                String RemValueStr = "";
                if (FinalRemValue > 1000) {
                    FinalRemValue = FinalRemValue / 1000;
                    //System.out.println();
                    RemValueStr = FinalRemValue + "";
                    RemValueStr = String.format("%.2f", Float.parseFloat(RemValueStr)) + getResources().getString(R.string.km);
                } else {
                    RemValueStr = FinalRemValue + "";
                    RemValueStr = String.format("%.2f", Float.parseFloat(RemValueStr)) + " M";
                }
                txt_remaining_val.setText(RemValueStr);
                Log.d("CoverdValue ","CoverdValue ="+CoverdValue+"=="+TotalKmMet+"=="+RemValueStr);
            }

            String CoverValueStr = "";
            if(CoverdValue>1000)
            {
                double CoverValueKm = CoverdValue/1000;
                //System.out.println();
                CoverValueStr = CoverValueKm+"";
                CoverValueStr = String.format("%.2f", Float.parseFloat(CoverValueStr))+getResources().getString(R.string.km);
            }else{
                CoverValueStr = CoverdValue+"";
                CoverValueStr = String.format("%.2f", Float.parseFloat(CoverValueStr))+" M";
            }
            Log.d("CoverdValue ","CoverdValue ="+CoverdValue+"=="+TotalKmMet+"=="+CoverValueStr);
            txt_covered_val.setText(CoverValueStr);

            OldLatLog = WayDriverLarLng;
        }

    }

    public void MarkerMoveTimer(int totalTimer){
        new CountDownTimer(totalTimer, 2000) { //40000 milli seconds is total time, 1000 milli seconds is time interval

            public void onTick(long millisUntilFinished) {

                Log.d("arrayPoints","arrayPoints = "+arrayPoints.size()+"=="+markerPositon);
                LatLng latLng = arrayPoints.get(markerPositon);
                WayMarker(latLng,"begin trip");

                Log.d("LatLngCom","LatLngCom = "+LatLngCom);
//                if(LatLngCom == 0)
//                    new DistanceTwoLatLng(allTripFeed.getPickupLarLng(),latLng).execute();

                //System.out.println(distance(OldLatLog.latitude,OldLatLog.longitude,latLng.latitude,latLng.longitude, "K") + " Kilometers\n");
                System.out.println(getDistance(new LatLng(OldLatLog.latitude,OldLatLog.longitude),new LatLng(latLng.latitude,latLng.longitude))+ " Metre Nautical Miles Test\n");

                CoverdValue = CoverdValue + getDistance(new LatLng(OldLatLog.latitude,OldLatLog.longitude),new LatLng(latLng.latitude,latLng.longitude));

                float TotalKmMet = (float) (Double.parseDouble(allTripFeed.getKm())*1000);
                double FinalRemValue = TotalKmMet - CoverdValue;

                String RemValueStr = "";
                if(FinalRemValue > 1000){
                    FinalRemValue = FinalRemValue/1000;
                    //System.out.println();
                    RemValueStr = FinalRemValue+"";
                    RemValueStr = String.format("%.2f", Float.parseFloat(RemValueStr))+getResources().getString(R.string.km);
                }else{
                    RemValueStr = FinalRemValue+"";
                    RemValueStr = String.format("%.2f", Float.parseFloat(RemValueStr))+" M";
                }

                String CoverValueStr = "";
                if(CoverdValue>1000)
                {
                    double CoverValueKm = CoverdValue/1000;
                    //System.out.println();
                    CoverValueStr = CoverValueKm+"";
                    CoverValueStr = String.format("%.2f", Float.parseFloat(CoverValueStr))+getResources().getString(R.string.km);
                }else{
                    CoverValueStr = CoverdValue+"";
                    CoverValueStr = String.format("%.2f", Float.parseFloat(CoverValueStr))+" M";
                }
                Log.d("CoverdValue ","CoverdValue ="+CoverdValue+"=="+TotalKmMet+"=="+RemValueStr+"=="+CoverValueStr);
                txt_covered_val.setText(CoverValueStr);
                txt_remaining_val.setText(RemValueStr);
                OldLatLog = latLng;

                markerPositon = markerPositon-1;
            }
            public void onFinish() {
                LatLng latLng = arrayPoints.get(0);
                WayMarker(latLng,"begin trip");
            }
        }.start();

    }

    public void drawDashedPolyLine(GoogleMap mMap, ArrayList<LatLng> listOfPoints, int color) {
    /* Boolean to control drawing alternate lines */
        boolean added = false;
        Log.d("listOfPoints","listOfPoints = "+listOfPoints.size());
        for (int i = 0; i < listOfPoints.size() - 1 ; i++) {

        /* Get distance between current and next point */
            double distance = getConvertedDistance(listOfPoints.get(i),listOfPoints.get(i + 1));

        /* If distance is less than 0.020 kms */
            if (distance < 0.020) {
                if (!added) {
                    mMap.addPolyline(new PolylineOptions()
                            .add(listOfPoints.get(i))
                            .add(listOfPoints.get(i + 1))
                            .color(color));
                    added = true;
                } else {/* Skip this piece */
                    added = false;
                }
            } else {
            /* Get how many divisions to make of this line */
                int countOfDivisions = (int) ((distance/0.020));

            /* Get difference to add per lat/lng */
                double latdiff = (listOfPoints.get(i+1).latitude - listOfPoints
                        .get(i).latitude) / countOfDivisions;
                double lngdiff = (listOfPoints.get(i + 1).longitude - listOfPoints
                        .get(i).longitude) / countOfDivisions;

            /* Last known indicates start point of polyline. Initialized to ith point */
                LatLng lastKnowLatLng = new LatLng(listOfPoints.get(i).latitude, listOfPoints.get(i).longitude);
                for (int j = 0; j < countOfDivisions; j++) {

                /* Next point is point + diff */
                    LatLng nextLatLng = new LatLng(lastKnowLatLng.latitude + latdiff, lastKnowLatLng.longitude + lngdiff);
                    if (!added) {
                        mMap.addPolyline(new PolylineOptions()
                                .add(lastKnowLatLng)
                                .add(nextLatLng)
                                .color(color));
                        added = true;
                    } else {
                        added = false;
                    }
                    lastKnowLatLng = nextLatLng;
                }
            }
        }
    }

    private double getConvertedDistance(LatLng latlng1, LatLng latlng2) {
        double distance = DistanceUtil.distance(latlng1.latitude,
                latlng1.longitude,
                latlng2.latitude,
                latlng2.longitude);
        BigDecimal bd = new BigDecimal(distance);
        BigDecimal res = bd.setScale(3, RoundingMode.DOWN);
        return res.doubleValue();
    }

    private boolean areBoundsTooSmall(LatLngBounds bounds, int minDistanceInMeter) {
        float[] result = new float[1];
        Location.distanceBetween(bounds.southwest.latitude, bounds.southwest.longitude, bounds.northeast.latitude, bounds.northeast.longitude, result);
        return result[0] < minDistanceInMeter;
    }

    private Emitter.Listener onSocketConnectionListener = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // handle the response args
                    Toast.makeText(TrackTruckActivity.this,"Driver on the way..",Toast.LENGTH_LONG).show();
                    JSONObject data = (JSONObject) args[0];

                    Log.d("data", "connected data = " + data);
                    try {
                        if(data.getString("success").equals("true")){
                            JSONArray dataArray = new JSONArray(data.getString("data"));
                            Log.d("dataArray","dataArray = "+dataArray);
                            for(int di=0;di<dataArray.length();di++){
                                JSONObject dataObj = dataArray.getJSONObject(di);
                                String Lotlon = dataObj.getString("loc");
                                JSONArray LotLanArray = new JSONArray(dataObj.getString("loc"));
                                String[] SplLotlon = Lotlon.split("\\,");

                                String DrvLat = SplLotlon[0].replace("[","");
                                String DrvLng = SplLotlon[1].replace("]","");

                                allTripFeed.setDriverLarLng(new LatLng(Double.parseDouble(DrvLat), Double.parseDouble(DrvLng)));
                                WayMarker(allTripFeed.getDriverLarLng(),dataObj.getString("booking_status").toString());
                                Log.d("Lotlon","connected Lotlon = "+DrvLng+"=="+DrvLat);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            });
        }
    };

    /**
     * Listener for socket connection error.. listener registered at the time of socket connection
     */
    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mSocket != null)
                        if (mSocket.connected() == false) {
                            Log.d("connected", "connected two= " + mSocket.connected());
                            //socketConnection();
                        }else
                        {
                            Log.d("connected", "connected three= " + mSocket.connected());
                        }
                }
            });
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();

        txt_track_truck = null;
        txt_month = null;
        txt_date = null;
        txt_locaton = null;
        txt_point_val = null;
        txt_remaining = null;
        txt_remaining_val = null;
        txt_covered = null;
        txt_covered_val = null;
        txt_total_km = null;
        txt_total_km_val = null;
        layout_main_drv_car = null;
        layout_location_heading = null;
        layout_footer = null;
        layout_dirvier_car = null;
        layout_booking_detail = null;
        txt_driver_name = null;
        txt_driver_other = null;
        txt_mob_num = null;
        txt_car_name = null;
        txt_car_model = null;
        txt_number_plate = null;
        txt_pickup_address = null;
        txt_drop_address = null;
        txt_contact = null;
        txt_share_eta = null;
        txt_cancel = null;
        layout_back_arrow = null;

        mSocket.disconnect();

        unregisterReceiver(receiver);
    }


    private static double distance(double lat1, double lon1, double lat2, double lon2, String unit) {

        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        if (unit == "K") {
            dist = dist * 1.609344;
        } else if (unit == "N") {
            dist = dist * 0.8684;
        }

        return (dist);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	/*::	This function converts decimal degrees to radians						 :*/
	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	/*::	This function converts radians to decimal degrees						 :*/
	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }


    private double getDistance(LatLng my_latlong,LatLng frnd_latlong){
        Location l1=new Location("One");
        l1.setLatitude(my_latlong.latitude);
        l1.setLongitude(my_latlong.longitude);

        Location l2=new Location("Two");
        l2.setLatitude(frnd_latlong.latitude);
        l2.setLongitude(frnd_latlong.longitude);

        float distance=l1.distanceTo(l2);
        float dist=distance;

        if(distance>1000.0f)
        {
            distance=distance/1000.0f;
            dist=distance;
        }
        return dist;
    }


    public class DistanceTwoLatLng extends AsyncTask<String, Integer, String> {

        private String content =  null;

        String LatLonUrl = "";
        public DistanceTwoLatLng(LatLng DriverLatLng,LatLng WayDriverLarLng){
            LatLngCom = 1;
            LatLonUrl = "https://maps.googleapis.com/maps/api/distancematrix/json?origins="+DriverLatLng.latitude+","+DriverLatLng.longitude+"&destinations="+WayDriverLarLng.latitude+","+WayDriverLarLng.longitude+"&mode=driving";
        }

        protected void onPreExecute() {
            //Toast.makeText(TrackTruckActivity.this,"Start",Toast.LENGTH_LONG).show();
        }

        @Override
        protected String doInBackground(String... params) {
            try
            {
                HttpClient client = new DefaultHttpClient();
                HttpParams HttpParams = client.getParams();
                HttpConnectionParams.setConnectionTimeout(HttpParams, 60 * 60 * 1000);
                HttpConnectionParams.setSoTimeout(HttpParams, 60 * 60 * 1000);
                Log.d("DrowLocUrl","DrowLocUrl = "+LatLonUrl);
                HttpGet getMethod = new HttpGet(LatLonUrl);
                //getMethod.setEntity(entity);
                client.execute(getMethod, new ResponseHandler<String>() {
                    @Override
                    public String handleResponse(HttpResponse httpResponse) throws ClientProtocolException, IOException {

                        HttpEntity httpEntity = httpResponse.getEntity();
                        content = EntityUtils.toString(httpEntity);
                        Log.d("Result >>>","DrowLocUrl Result One"+ content);

                        return null;
                    }
                });

            } catch(Exception e)
            {
                e.printStackTrace();
                Log.d("Indiaries", "DrowLocUrl Result error" + e);
                return e.getMessage();
            }
            return content;
        }

        @Override
        protected void onPostExecute(String result) {
            //Toast.makeText(TrackTruckActivity.this,"End = "+result,Toast.LENGTH_LONG).show();
            LatLngCom = 0;
            boolean isStatus = Common.ShowHttpErrorMessage(TrackTruckActivity.this,result);
            if(isStatus) {
                try {

                    JSONObject resObj = new JSONObject(result);
                    if (resObj.getString("status").toLowerCase().equals("ok")) {

                        JSONArray rowArray = new JSONArray(resObj.getString("rows"));
                        for(int li = 0;li<rowArray.length();li++){
                            JSONObject eleObj = rowArray.getJSONObject(li);

                            JSONArray eleArray = new JSONArray(eleObj.getString("elements"));
                            for(int ei = 0;ei<eleArray.length();ei++) {

                                JSONObject elementObj = eleArray.getJSONObject(li);
                                Log.d("Status","Status = "+elementObj.getString("status"));
                                if(elementObj.getString("status").equals("OK")) {

                                    JSONObject distanceObj = new JSONObject(elementObj.getString("distance"));

                                    int DistanceVal = Integer.parseInt(distanceObj.getString("value"))/1000;
                                    Log.d("DistanceVal","DistanceVal"+DistanceVal);
                                    if (txt_covered_val != null)
                                        txt_covered_val.setText(DistanceVal+" Km");


                                    double remDis = Double.parseDouble(allTripFeed.getKm()) - DistanceVal;
                                    if (txt_remaining_val != null)
                                        txt_remaining_val.setText(String.format("%.2f Km", remDis));
                                }
                            }
                        }


                    } else {
                        Toast.makeText(TrackTruckActivity.this, getResources().getString(R.string.location_long), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
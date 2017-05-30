package come.texi.driver;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsoluteLayout;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonObject;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.victor.loading.rotate.RotateLoading;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.security.PublicKey;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import come.texi.driver.adapter.AllTripAdapter;
import come.texi.driver.adapter.CabDetailAdapter;
import come.texi.driver.adapter.PickupDropLocationAdapter;
import come.texi.driver.gpsLocation.AutoCompleteAdapter;
import come.texi.driver.gpsLocation.GPSTracker;
import come.texi.driver.gpsLocation.LocationAddress;
import come.texi.driver.utils.AllTripFeed;
import come.texi.driver.utils.CabDetails;
import come.texi.driver.utils.Common;
import come.texi.driver.utils.Url;
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

public class HomeActivity extends FragmentActivity implements OnMapReadyCallback,CabDetailAdapter.OnCabDetailClickListener,PickupDropLocationAdapter.OnDraoppickupClickListener {

    TextView txt_home,txt_reservation,txt_now;
    RelativeLayout layout_slidemenu;
    EditText edt_pickup_location;
    EditText edt_drop_location;
    EditText edt_write_comment;
    RelativeLayout layout_now;
    RelativeLayout layout_reservation;
    ImageView img_pickup_close;
    ImageView img_drop_close;
    RecyclerView recycle_pickup_location;
    RelativeLayout layout_pickup_drag_location;
    LinearLayout layout_no_result;
    TextView txt_not_found;
    TextView no_location;
    TextView please_check;

    SharedPreferences userPref;

    Typeface OpenSans_Regular,OpenSans_Bold,Roboto_Regular,Roboto_Medium,Roboto_Bold;

    SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");

    GPSTracker gpsTracker;

    private GoogleMap googleMap;

    ArrayList<CabDetails> cabDetailArray;

    MarkerOptions marker;

    LatLng PickupLarLng;
    LatLng DropLarLng;
    double DropLongtude;
    double DropLatitude;
    double PickupLongtude;
    double PickupLatitude;

    ArrayList<HashMap<String,String>> locationArray;
    private ArrayList<LatLng> arrayPoints = null;

    Dialog NowDialog;
    Dialog CashDialog;
    Dialog ReservationDialog;
    CabDetailAdapter cabDetailAdapter;

    TextView txt_car_header;
    TextView txt_currency,txt_far_breakup,txt_book,txt_cancel;
    TextView txt_car_descriptin;
    TextView txt_first_price;
    TextView txt_first_km;
    TextView txt_sec_pric;
    TextView txt_sec_km;
    TextView txt_thd_price,txt_locatons;
    RelativeLayout layout_one;
    RelativeLayout layout_two;
    RelativeLayout layout_three;
    TextView txt_total_price;
    TextView txt_cash;
    RelativeLayout layout_cash;
    Spinner spinner_person;
    String person = "";
    TextView txt_first_currency;
    TextView txt_secound_currency;
    TextView txt_thd_currency;
    LinearLayout layout_timming;
    RelativeLayout layout_far_breakup;

    String car_rate;
    String fromintailrate;
    String ride_time_rate = "0";
    String DayNight;
    String transfertype;

    SlidingMenu slidingMenu;
    ArrayList<Calendar> arrActualDates = new ArrayList<>();
    SimpleDateFormat sdf = new SimpleDateFormat("E MMM, dd", Locale.getDefault());

    Float distance;
    int googleDuration = 0;
    String truckIcon;
    String truckType;
    String CabId;
    String AreaId;
    Float totlePrice;
    float FirstKm;
    int totalTime;
    String CarName = "";
    String AstTime = "";

    String bothLocationString = "";


    PickupDropLocationAdapter pickupDropLocationAdapter;

    LinearLayoutManager pickupDragLayoutManager;

    boolean ClickOkButton = false;
    String PaymentType = "Cash";
    Calendar myCalendar;
    TextView txt_date;
    TextView txt_time;
    DatePickerDialog.OnDateSetListener date;
    String BookingDateTime = "";
    SimpleDateFormat bookingFormate;
    int devise_width;

    String transaction_id = "";

    Common common = new Common();

    boolean LocationDistanse = false;
    Marker PickupMarker;
    Marker DropMarker;
    int CabPositon = 0;

/*Paypall integration variable*/
    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_NO_NETWORK;
    // note that these credentials will differ between live & sandbox environments.
    private static final String CONFIG_CLIENT_ID = "AYqm_vX5LIbsdhuZBgkVBHAJ9YR6yA2_3N81R9wZGkjBZPMHDu91uo47fwL7779Bxly6li5vQWfrO0fy";
    private static final int REQUEST_CODE_PAYMENT = 1;
    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(CONFIG_ENVIRONMENT)
            .clientId(CONFIG_CLIENT_ID);

    private static final int REQUEST_CODE_STRIPE = 2;

    String BookingType;

    ArrayList<HashMap<String,String>> FixRateArray;

    Dialog ProgressDialog;
    RotateLoading cusRotateLoading;
    RecyclerView recycle_cab_detail;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        userPref = PreferenceManager.getDefaultSharedPreferences(HomeActivity.this);

        txt_home = (TextView)findViewById(R.id.txt_home);
        layout_slidemenu = (RelativeLayout)findViewById(R.id.layout_slidemenu);
        edt_pickup_location = (EditText)findViewById(R.id.edt_pickup_location);
        edt_drop_location = (EditText)findViewById(R.id.edt_drop_location);
        edt_write_comment = (EditText)findViewById(R.id.edt_write_comment);
        layout_now = (RelativeLayout) findViewById(R.id.layout_now);
        layout_reservation = (RelativeLayout) findViewById(R.id.layout_reservation);
        img_pickup_close = (ImageView)findViewById(R.id.img_pickup_close);
        img_drop_close = (ImageView)findViewById(R.id.img_drop_close);
        recycle_pickup_location = (RecyclerView)findViewById(R.id.recycle_pickup_location);
        layout_pickup_drag_location = (RelativeLayout)findViewById(R.id.layout_pickup_drag_location);
        layout_no_result = (LinearLayout)findViewById(R.id.layout_no_result);
        txt_not_found = (TextView)findViewById(R.id.txt_not_found);
        no_location = (TextView)findViewById(R.id.no_location);
        please_check = (TextView)findViewById(R.id.please_check);
        txt_locatons = (TextView)findViewById(R.id.txt_locatons);
        txt_reservation = (TextView)findViewById(R.id.txt_reservation);
        txt_now = (TextView)findViewById(R.id.txt_now);

        String bookinCancel = getIntent().getStringExtra("cancel_booking");
        if(bookinCancel != null && bookinCancel.equals("1")){
            Common.showMkSucess(HomeActivity.this,getResources().getString(R.string.your_booking_cancel),"yes");
        }

        ProgressDialog = new Dialog(HomeActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
        ProgressDialog.setContentView(R.layout.custom_progress_dialog);
        ProgressDialog.setCancelable(false);
        cusRotateLoading = (RotateLoading)ProgressDialog.findViewById(R.id.rotateloading_register);
//        Log.d("device_token","device_token = "+Common.device_token);
//        Log.d("id_device_token","id_device_token = "+userPref.getString("id_device_token",""));

        if(!userPref.getString("id_device_token","").equals("1"))
            new Common.CallUnSubscribeTaken(HomeActivity.this,Common.device_token).execute();

        arrayPoints = new ArrayList<LatLng>();

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        devise_width = displaymetrics.widthPixels;

        layout_now.getLayoutParams().width = (int) (devise_width * 0.50);

        RelativeLayout.LayoutParams resParam = new RelativeLayout.LayoutParams((int) (devise_width * 0.51), ViewGroup.LayoutParams.WRAP_CONTENT);
        resParam.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        resParam.addRule(RelativeLayout.ALIGN_PARENT_END);

        layout_reservation.setLayoutParams(resParam);

        bookingFormate = new SimpleDateFormat("h:mm a, d, MMM yyyy,EEE");

        OpenSans_Bold = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Bold_0.ttf");
        Roboto_Regular = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
        Roboto_Medium = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Medium.ttf");
        Roboto_Bold = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Bold.ttf");

        txt_home.setTypeface(OpenSans_Bold);
        txt_not_found.setTypeface(OpenSans_Bold);
        txt_locatons.setTypeface(Roboto_Bold);
        txt_reservation.setTypeface(Roboto_Bold);
        txt_now.setTypeface(Roboto_Bold);

        edt_pickup_location.setTypeface(OpenSans_Regular);
        edt_drop_location.setTypeface(OpenSans_Regular);
        edt_write_comment.setTypeface(Roboto_Regular);
        no_location.setTypeface(Roboto_Regular);
        please_check.setTypeface(Roboto_Regular);

        pickupDragLayoutManager = new LinearLayoutManager(HomeActivity.this);
        recycle_pickup_location.setLayoutManager(pickupDragLayoutManager);

        /*get Current Location And Set Edittext*/
        PickupLatitude = getIntent().getDoubleExtra("PickupLatitude",0.0);
        PickupLongtude = getIntent().getDoubleExtra("PickupLongtude",0.0);

        gpsTracker = new GPSTracker(HomeActivity.this);

        if(PickupLongtude != 0.0 && PickupLatitude != 0.0){
            bothLocationString = "pickeup";
            if(Common.isNetworkAvailable(HomeActivity.this)) {
                LocationAddress locationAddress = new LocationAddress();
                locationAddress.getAddressFromLocation(PickupLatitude, PickupLongtude,
                        getApplicationContext(), new GeocoderHandler());

                PickupLarLng = new LatLng(PickupLatitude, PickupLongtude);
                ClickOkButton = true;

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MarkerAdd();
                    }
                }, 1000);
            }else{
                Toast.makeText(HomeActivity.this,"No Network",Toast.LENGTH_LONG).show();
            }
        }else{

            if(gpsTracker.checkLocationPermission()) {

                PickupLatitude = gpsTracker.getLatitude();
                PickupLongtude = gpsTracker.getLongitude();
                PickupLarLng = new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude());
                ClickOkButton = true;

                bothLocationString = "pickeup";
                if(Common.isNetworkAvailable(HomeActivity.this)) {
                    LocationAddress locationAddress = new LocationAddress();
                    locationAddress.getAddressFromLocation(PickupLatitude, PickupLongtude,
                            getApplicationContext(), new GeocoderHandler());

                    PickupLarLng = new LatLng(PickupLatitude, PickupLongtude);
                    ClickOkButton = true;

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            MarkerAdd();
                        }
                    }, 1000);
                }else{
                    Toast.makeText(HomeActivity.this,"No Network",Toast.LENGTH_LONG).show();
                }

            }else{
                gpsTracker.showSettingsAlert();
            }
        }

        Log.d("gpsTracker", "gpsTracker =" + gpsTracker.canGetLocation() + "==" + gpsTracker.checkLocationPermission());

        /*Pickup Location autocomplate start*/
        //LocationAutocompleate(edt_pickup_location, "pickeup");
        EditorActionListener(edt_pickup_location, "pickeup");
        AddTextChangeListener(edt_pickup_location, "pickeup");
        AddSetOnClickListener(edt_pickup_location, "pickeup");
        /*Pickup Location autocomplate end*/

        /*Drop Location autocomplate start*/
        //LocationAutocompleate(edt_drop_location, "drop");
        EditorActionListener(edt_drop_location, "drop");
        AddTextChangeListener(edt_drop_location, "drop");
        AddSetOnClickListener(edt_drop_location, "drop");
        /*Drop Location autocomplate end*/

        /*Slide Menu Start*/

        slidingMenu = new SlidingMenu(this);
        slidingMenu.setMode(SlidingMenu.LEFT);
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
        slidingMenu.setBehindOffsetRes(R.dimen.slide_menu_width);
        slidingMenu.setFadeDegree(0.20f);
        slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        slidingMenu.setMenu(R.layout.left_menu);

        common.SlideMenuDesign(slidingMenu, HomeActivity.this,"home");

        layout_slidemenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slidingMenu.toggle();
            }
        });

        /*Slide Menu End*/

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        /*Get Day Night Time*/

        try {

            String currentLocalTime = currentTime.format(new Date());
            Date StarDateFrm = null;
            if(!Common.StartDayTime.equals(""))
                StarDateFrm = currentTime.parse(Common.StartDayTime);
            Date EndDateFrm = null;
            if(!Common.StartDayTime.equals(""))
                EndDateFrm =  currentTime.parse(Common.EndDayTime);

            Date CurDateFrm =  currentTime.parse(currentLocalTime);

            if(StarDateFrm != null && EndDateFrm != null) {
                if (CurDateFrm.before(StarDateFrm) || CurDateFrm.after(EndDateFrm)) {
                    Log.d("get time", "get time = before");
                    DayNight = "night";
                } else {
                    DayNight = "day";
                }
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }


        /*Now Image Click popup start*/
        //NowDialog = new Dialog(HomeActivity.this,android.R.style.Theme_Translucent_NoTitleBar);
        NowDialog = new Dialog(HomeActivity.this,R.style.DialogUpDownAnim);
        NowDialog.setContentView(R.layout.now_dialog_layout);
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            NowDialog.getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        txt_car_header = (TextView)NowDialog.findViewById(R.id.txt_car_header);
        txt_car_header.setTypeface(Roboto_Medium);
        txt_currency = (TextView)NowDialog.findViewById(R.id.txt_currency);
        txt_currency.setTypeface(Roboto_Regular);
        txt_far_breakup = (TextView)NowDialog.findViewById(R.id.txt_far_breakup);
        txt_far_breakup.setTypeface(Roboto_Bold);
        txt_book = (TextView)NowDialog.findViewById(R.id.txt_book);
        txt_book.setTypeface(Roboto_Bold);
        txt_cancel = (TextView)NowDialog.findViewById(R.id.txt_cancel);
        txt_cancel.setTypeface(Roboto_Bold);

        txt_car_descriptin = (TextView)NowDialog.findViewById(R.id.txt_car_descriptin);
        txt_car_descriptin.setTypeface(Roboto_Regular);
        txt_first_price = (TextView)NowDialog.findViewById(R.id.txt_first_price);
        txt_first_price.setTypeface(Roboto_Regular);
        txt_first_km = (TextView)NowDialog.findViewById(R.id.txt_first_km);
        txt_first_km.setTypeface(Roboto_Regular);
        txt_sec_pric = (TextView)NowDialog.findViewById(R.id.txt_sec_pric);
        txt_sec_pric.setTypeface(Roboto_Regular);
        txt_sec_km = (TextView)NowDialog.findViewById(R.id.txt_sec_km);
        txt_sec_km.setTypeface(Roboto_Regular);
        txt_thd_price = (TextView)NowDialog.findViewById(R.id.txt_thd_price);
        txt_thd_price.setTypeface(Roboto_Regular);
        layout_one = (RelativeLayout)NowDialog.findViewById(R.id.layout_one);
        layout_two = (RelativeLayout)NowDialog.findViewById(R.id.layout_two);
        layout_three = (RelativeLayout)NowDialog.findViewById(R.id.layout_three);
        txt_total_price = (TextView)NowDialog.findViewById(R.id.txt_total_price);
        txt_cash = (TextView)NowDialog.findViewById(R.id.txt_cash);
        spinner_person = (Spinner)NowDialog.findViewById(R.id.spinner_person);
        txt_first_currency = (TextView)NowDialog.findViewById(R.id.txt_first_currency);
        txt_secound_currency = (TextView)NowDialog.findViewById(R.id.txt_secound_currency);
        txt_thd_currency = (TextView)NowDialog.findViewById(R.id.txt_thd_currency);
        layout_timming = (LinearLayout) NowDialog.findViewById(R.id.layout_timming);
        layout_far_breakup = (RelativeLayout) NowDialog.findViewById(R.id.layout_far_breakup);

        spinner_person.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                person = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        TextView txt_specailChr_note = (TextView)NowDialog.findViewById(R.id.txt_specailChr_note);
        txt_specailChr_note.setTypeface(Roboto_Regular);

        txt_total_price.setTypeface(Roboto_Regular);
        txt_cash.setTypeface(Roboto_Regular);
        txt_currency.setText(Common.Currency);
        txt_first_currency.setText(Common.Currency);
        txt_first_currency.setTypeface(Roboto_Bold);
        txt_secound_currency.setText(Common.Currency);
        txt_secound_currency.setTypeface(Roboto_Bold);
        txt_thd_currency.setText(Common.Currency);
        txt_thd_currency.setTypeface(Roboto_Bold);

        recycle_cab_detail = (RecyclerView)NowDialog.findViewById(R.id.recycle_cab_detail);
        RecyclerView.LayoutManager categoryLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recycle_cab_detail.setLayoutManager(categoryLayoutManager);

        cabDetailArray = new ArrayList<CabDetails>();

        final RelativeLayout layout_book = (RelativeLayout) NowDialog.findViewById(R.id.layout_book);
        layout_book.getLayoutParams().width = (int) (devise_width * 0.50);
        layout_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (edt_pickup_location.getText().toString().trim().length() == 0) {
                    Common.showMkError(HomeActivity.this, getResources().getString(R.string.enter_pickup));
                    return;
                } else if (edt_drop_location.getText().toString().trim().length() == 0) {
                    Common.showMkError(HomeActivity.this, getResources().getString(R.string.enter_drop));
                    return;
                }

                NowDialog.cancel();

                layout_reservation.setVisibility(View.VISIBLE);
//                if(car_rate != null && fromintailrate != null && ride_time_rate != null)
//                    totlePrice = Common.getTotalPrice(car_rate, FirstKm, distance, fromintailrate, ride_time_rate, totalTime);
//                else
//                    totlePrice = 0f;
                Log.d("total price", "total price = " + totlePrice);


                if(totlePrice != 0f) {
                    Intent bi = new Intent(HomeActivity.this, TripDetailActivity.class);
                    bi.putExtra("pickup_point", edt_pickup_location.getText().toString().trim());
                    bi.putExtra("drop_point", edt_drop_location.getText().toString().trim());
                    bi.putExtra("distance", distance);
                    bi.putExtra("truckIcon", truckIcon);
                    bi.putExtra("truckType", truckType);
                    bi.putExtra("CabId",CabId);
                    bi.putExtra("AreaId",AreaId);
                    bi.putExtra("booking_date", BookingDateTime);
                    bi.putExtra("totlePrice", totlePrice);
                    bi.putExtra("PickupLatitude", PickupLatitude);
                    bi.putExtra("PickupLongtude", PickupLongtude);
                    bi.putExtra("DropLatitude", DropLatitude);
                    bi.putExtra("DropLongtude", DropLongtude);
                    bi.putExtra("comment", edt_write_comment.getText().toString().trim());
                    bi.putExtra("DayNight", DayNight);
                    bi.putExtra("transfertype", transfertype);
                    bi.putExtra("PaymentType", PaymentType);
                    bi.putExtra("person", person);
                    bi.putExtra("transaction_id", transaction_id);
                    bi.putExtra("BookingType",BookingType);
                    bi.putExtra("AstTime",AstTime);
                    startActivity(bi);
                }else{
                    Common.showMkError(HomeActivity.this,getResources().getString(R.string.not_valid_total_price));
                }
            }
        });

        RelativeLayout layout_cancle = (RelativeLayout) NowDialog.findViewById(R.id.layout_cancle);
        RelativeLayout.LayoutParams CanParam = new RelativeLayout.LayoutParams((int) (devise_width * 0.51), ViewGroup.LayoutParams.WRAP_CONTENT);
        CanParam.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        CanParam.addRule(RelativeLayout.ALIGN_PARENT_END);
        layout_cancle.setLayoutParams(CanParam);

        layout_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NowDialog.cancel();
                layout_reservation.setVisibility(View.VISIBLE);
            }
        });

        JSONArray CabDetailAry = Common.CabDetail;

        for(int ci=0;ci<CabDetailAry.length();ci++){

            CabDetails cabDetails = new CabDetails();

            try {
                JSONObject cabObj = CabDetailAry.getJSONObject(ci);
                cabDetails.setId(cabObj.getString("cab_id"));
                cabDetails.setCartype(cabObj.getString("cartype"));
                cabDetails.setTransfertype(cabObj.getString("transfertype"));
                cabDetails.setIntialkm(cabObj.getString("intialkm"));
                cabDetails.setCarRate(cabObj.getString("car_rate"));
                cabDetails.setFromintialkm(cabObj.getString("intailrate"));
                cabDetails.setStandardrate(cabObj.getString("standardrate"));
                cabDetails.setFromintailrate(cabObj.getString("fromintailrate"));
                cabDetails.setFromstandardrate(cabObj.getString("fromstandardrate"));
                cabDetails.setNightFromintialkm(cabObj.getString("night_fromintialkm"));
                cabDetails.setNightFromintailrate(cabObj.getString("night_fromintailrate"));
                cabDetails.setIcon(cabObj.getString("icon"));
                cabDetails.setDescription(cabObj.getString("description"));
                cabDetails.setNightIntailrate(cabObj.getString("night_intailrate"));
                cabDetails.setNightStandardrate(cabObj.getString("night_standardrate"));
                cabDetails.setRideTimeRate(cabObj.getString("ride_time_rate"));
                cabDetails.setNightRideTimeRate(cabObj.getString("night_ride_time_rate"));
                cabDetails.setSeatCapacity(cabObj.getString("seat_capacity"));
                if(cabObj.has("fix_price")){
                    cabDetails.setFixPrice(cabObj.getString("fix_price"));
                }else{
                    cabDetails.setFixPrice("");
                }
                if(cabObj.has("area_id")){
                    cabDetails.setAreaId(cabObj.getString("area_id"));
                }else{
                    cabDetails.setFixPrice("");
                }

                if (ci == 0)
                    cabDetails.setIsSelected(true);
                else
                    cabDetails.setIsSelected(false);

                cabDetailArray.add(cabDetails);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        if(CabDetailAry != null && CabDetailAry.length() > 0){
            Log.d("cabDetailArray","cabDetailArray = "+cabDetailArray.size());
            if(cabDetailArray != null && cabDetailArray.size() > 0){

                CabDetails cabDetails = cabDetailArray.get(0);
                txt_car_header.setText(cabDetails.getCartype().toUpperCase());
                CarName = cabDetails.getCartype().toUpperCase();
                txt_car_descriptin.setText(cabDetails.getDescription());

                if(DayNight.equals("day")) {
                    car_rate = cabDetails.getCarRate();
                    fromintailrate = cabDetails.getFromintailrate();
                    if(cabDetails.getRideTimeRate() != null) {
                        ride_time_rate = cabDetails.getRideTimeRate();
                    }
                }
                else if(DayNight.equals("night")) {
                    car_rate = cabDetails.getNightIntailrate();
                    fromintailrate = cabDetails.getNightFromintailrate();
                    if(cabDetails.getNightRideTimeRate() != null && !cabDetails.getNightRideTimeRate().equals("0")) {
                        ride_time_rate = cabDetails.getNightRideTimeRate();
                    }
                }
                txt_first_price.setText(car_rate);
                FirstKm = Float.parseFloat(cabDetails.getIntialkm());
                txt_first_km.setText(getResources().getString(R.string.first)+" "+FirstKm+" "+getResources().getString(R.string.km));
                txt_sec_pric.setText(fromintailrate+"/"+getResources().getString(R.string.km));
                txt_sec_km.setText(getResources().getString(R.string.after)+" "+FirstKm+" "+getResources().getString(R.string.km));

                if(cabDetails.getRideTimeRate() != null || cabDetails.getNightRideTimeRate() != null && !cabDetails.getNightRideTimeRate().equals("0")){
                    layout_three.setVisibility(View.VISIBLE);
                    txt_thd_price.setText(ride_time_rate+"/"+getResources().getString(R.string.min));
                }else{
                    layout_three.setVisibility(View.GONE);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            AbsoluteLayout.LayoutParams.WRAP_CONTENT, AbsoluteLayout.LayoutParams.MATCH_PARENT);
                    params.weight = 1.5f;
                    layout_one.setLayoutParams(params);
                    layout_two.setLayoutParams(params);
                }

                truckIcon = cabDetails.getIcon();
                truckType = cabDetails.getCartype();
                CabId = cabDetails.getId();
                AreaId = cabDetails.getAreaId();
                transfertype = cabDetails.getTransfertype();

                cabDetailAdapter = new CabDetailAdapter(HomeActivity.this,cabDetailArray);
                recycle_cab_detail.setAdapter(cabDetailAdapter);
                cabDetailAdapter.setOnCabDetailItemClickListener(HomeActivity.this);
                cabDetailAdapter.updateItems();

                List<String> list = new ArrayList<String>();
                for(int si=0;si<Integer.parseInt(cabDetails.getSeatCapacity());si++){
                    int seat = si+1;
                    list.add(seat+"");
                }
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(HomeActivity.this,
                        android.R.layout.simple_spinner_item, list);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_person.setAdapter(dataAdapter);

                Log.d("Fix Price","Fix Price = "+cabDetails.getFixPrice());

            }

        }

        layout_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("length ", "length = " + edt_pickup_location.getText().toString().length());
                if (edt_pickup_location.getText().toString().length() == 0) {
                    Common.showMkError(HomeActivity.this, getResources().getString(R.string.enter_pickup));
                    return;
                } else if (edt_drop_location.getText().toString().length() == 0) {
                    Common.showMkError(HomeActivity.this, getResources().getString(R.string.enter_drop));
                    return;
                }else if (!LocationDistanse) {
                    Common.showMkError(HomeActivity.this, getResources().getString(R.string.location_long));
                    return;
                }else if(distance == 0.0){
                    Common.showMkError(HomeActivity.this, getResources().getString(R.string.location_short));
                    return;
                }

                BookingDateTime = bookingFormate.format(Calendar.getInstance().getTime());

                BookingType = "Now";
                layout_reservation.setVisibility(View.GONE);




                NowDialog.show();

//                if(intailrate != null && fromintailrate != null && ride_time_rate != null)
//                    totlePrice = Common.getTotalPrice(intailrate, FirstKm, distance, fromintailrate, ride_time_rate, totalTime);
//                else
//                    totlePrice = 0f;
//                Log.d("totlePrice","totlePrice = "+totlePrice);
//
//                txt_total_price.setText(String.valueOf(totlePrice));
            }
        });


    /*Cash Dialog Strat*/
        CashDialog = new Dialog(HomeActivity.this,R.style.DialogUpDownAnim);
        CashDialog.setContentView(R.layout.cash_dialog_layout);
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            CashDialog.getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        RelativeLayout layout__cash_cash = (RelativeLayout)CashDialog.findViewById(R.id.layout__cash_cash);
        layout__cash_cash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CashDialog.cancel();
                NowDialog.show();
                PaymentType = "Cash";
            }
        });
        RelativeLayout layout_cash_paypal = (RelativeLayout)CashDialog.findViewById(R.id.layout_cash_paypal);
        layout_cash_paypal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PaymentType = "Paypal";
                onBuyPressed();
            }
        });
        RelativeLayout layout_cash_credit_card = (RelativeLayout)CashDialog.findViewById(R.id.layout_cash_credit_card);
        layout_cash_credit_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PaymentType = "Stripe";
                Intent si = new Intent(HomeActivity.this,StripeFormActivity.class);
                startActivityForResult(si,REQUEST_CODE_STRIPE);
            }
        });
        RelativeLayout layout_cash_cancel = (RelativeLayout)CashDialog.findViewById(R.id.layout_cash_cancel);
        layout_cash_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CashDialog.cancel();
                NowDialog.show();
                PaymentType = "Cash";
            }
        });

        layout_cash = (RelativeLayout)NowDialog.findViewById(R.id.layout_cash);
        layout_cash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //NowDialog.cancel();
                //CashDialog.show();
            }
        });

    /*Cash Dialog End*/

        /*Now Image Click popup end*/

        /*Reservation Image Click popup start*/

        myCalendar = Calendar.getInstance();

        date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        ReservationDialog = new Dialog(HomeActivity.this,R.style.DialogUpDownAnim);
        ReservationDialog.setContentView(R.layout.reservation_dialog_layout);
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            ReservationDialog.getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        txt_date = (TextView)ReservationDialog.findViewById(R.id.txt_date);
        txt_time = (TextView)ReservationDialog.findViewById(R.id.txt_time);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        RelativeLayout layout_res = (RelativeLayout)ReservationDialog.findViewById(R.id.layout_res);
        layout_res.getLayoutParams().height = (int) (dm.heightPixels * 0.40);

        RelativeLayout layout_select_date = (RelativeLayout)ReservationDialog.findViewById(R.id.layout_select_date);
        layout_select_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar aftoneMont = Calendar.getInstance();
                aftoneMont.add(Calendar.MONTH, 1);

                DatePickerDialog dpd = new DatePickerDialog(HomeActivity.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Date d = null;
                try {
                    String formattedDate = sdf.format(Calendar.getInstance().getTime());
                    d = sdf.parse(formattedDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                dpd.getDatePicker().setMinDate(d.getTime());
                dpd.getDatePicker().setMaxDate(aftoneMont.getTimeInMillis());
                dpd.show();
            }
        });


        RelativeLayout layout_select_time = (RelativeLayout)ReservationDialog.findViewById(R.id.layout_select_time);
        layout_select_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                int hour = myCalendar.get(Calendar.HOUR_OF_DAY);
                int minute = myCalendar.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(HomeActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        txt_time.setText(selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, false);//Yes 24 hour time
                mTimePicker.setTitle(getResources().getString(R.string.select_time_res));
                mTimePicker.show();
            }
        });

        RelativeLayout layout_done = (RelativeLayout)ReservationDialog.findViewById(R.id.layout_done);
        layout_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("txt_date ","txt_date = "+txt_date.getText().toString());
                if(txt_date.getText().toString().length() == 0){
                    Common.showMkError(HomeActivity.this,getResources().getString(R.string.please_enter_date));
                    return;
                }else if(txt_time.getText().length() == 0){
                    Common.showMkError(HomeActivity.this,getResources().getString(R.string.please_enter_time));
                    return;
                }

                SimpleDateFormat currentDateFormate = new SimpleDateFormat("dd/MM/yyyy HH:mm aa");
                String DateTimeString = txt_date.getText()+" "+txt_time.getText();
                String SeletedtDate = "";
                Date SeletDate;
                try {
                    SeletDate = currentDateFormate.parse(DateTimeString);
                    SeletedtDate = currentDateFormate.format(SeletDate.getTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                String currentDate = currentDateFormate.format(Calendar.getInstance().getTime());
                boolean dateVal = CheckDates(DateTimeString, currentDate);

                /*After One Month Validation*/
                boolean afterOneMonth = false;
                Calendar onMonCal = Calendar.getInstance();
                onMonCal.add(Calendar.MONTH, 1);
                String curOneMonDate = currentDateFormate.format(onMonCal.getTime());
                SimpleDateFormat dfDate  = new SimpleDateFormat("dd/MM/yyyy HH:mm aa");
                Log.d("curOneMonDate","curOneMonDate = "+curOneMonDate+"=="+DateTimeString);
                try {
                    Date CrtDate = dfDate.parse(curOneMonDate);
                    Date SelDate = dfDate.parse(DateTimeString);
                    if(SelDate.after(CrtDate)){
                        Log.d("After","curOneMonDate After One");
                        afterOneMonth = true;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                try {
                    SimpleDateFormat selectedDateFormate  = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                    Date parshDate = selectedDateFormate.parse(DateTimeString);
                    BookingDateTime = bookingFormate.format(parshDate);

                } catch (ParseException e) {
                    e.printStackTrace();
                    Log.d("date Error","DateTimeString Error = "+e.getMessage());
                }
                //SimpleDateFormat bookingFormate = new SimpleDateFormat("h:mm a, d, MMM yyyy,EEE");
                Log.d("DateTimeString","DateTimeString one= "+DateTimeString);
                Log.d("DateTimeString","DateTimeString two= "+currentDate);
                Log.d("DateTimeString","DateTimeString three= "+dateVal);
                Log.d("DateTimeString","DateTimeString for= "+BookingDateTime);
                if(afterOneMonth){
                    Common.showMkError(HomeActivity.this,getResources().getString(R.string.time_is_large));
                    return;
                }else if(!dateVal){
                    Common.showMkError(HomeActivity.this,getResources().getString(R.string.date_time_not_valid));
                    return;
                }
                try {

                    Date ResCurDateFrm =  currentTime.parse(txt_time.getText().toString());
                    Date ResStarDateFrm = null;
                    if(!Common.StartDayTime.equals(""))
                            ResStarDateFrm = currentTime.parse(Common.StartDayTime);

                    Date ResEndDateFrm = null;
                    if(!Common.StartDayTime.equals(""))
                        ResEndDateFrm =  currentTime.parse(Common.EndDayTime);

                    if(ResStarDateFrm != null && ResEndDateFrm != null) {
                        if (ResCurDateFrm.before(ResStarDateFrm) || ResCurDateFrm.after(ResEndDateFrm)) {
                            Log.d("get time", "get time = before");
                            DayNight = "night";
                        } else {
                            DayNight = "day";
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Log.d("DayNight","DayNight = "+DayNight);
                CabDetails ResCabDetails = cabDetailArray.get(CabPositon);
                if(DayNight.equals("day")) {
                    car_rate = ResCabDetails.getCarRate();
                    fromintailrate = ResCabDetails.getFromintailrate();
                    if(ResCabDetails.getRideTimeRate() != null) {
                        ride_time_rate = ResCabDetails.getRideTimeRate();
                    }
                }
                else if(DayNight.equals("night")) {
                    car_rate = ResCabDetails.getNightIntailrate();
                    fromintailrate = ResCabDetails.getNightFromintailrate();
                    if(ResCabDetails.getNightRideTimeRate() != null && !ResCabDetails.getNightRideTimeRate().equals("0")) {
                        ride_time_rate = ResCabDetails.getNightRideTimeRate();
                    }
                }
                if(ResCabDetails.getRideTimeRate() != null || ResCabDetails.getNightRideTimeRate() != null && !ResCabDetails.getNightRideTimeRate().equals("0")){
                    layout_three.setVisibility(View.VISIBLE);
                    txt_thd_price.setText(ride_time_rate+"/"+getResources().getString(R.string.min));
                }
                txt_first_price.setText(car_rate);
                txt_sec_pric.setText(fromintailrate+"/"+getResources().getString(R.string.km));

                layout_reservation.setVisibility(View.GONE);
                NowDialog.show();
                ReservationDialog.cancel();

            }
        });

        layout_reservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("length ", "length = " + edt_pickup_location.getText().toString().length());
                if (edt_pickup_location.getText().toString().length() == 0) {
                    Common.showMkError(HomeActivity.this, getResources().getString(R.string.enter_pickup));
                    return;
                } else if (edt_drop_location.getText().toString().length() == 0) {
                    Common.showMkError(HomeActivity.this, getResources().getString(R.string.enter_drop));
                    return;
                }else if(!LocationDistanse){
                    Common.showMkError(HomeActivity.this, getResources().getString(R.string.location_long));
                    return;
                }else if(distance == 0.0){
                    Common.showMkError(HomeActivity.this, getResources().getString(R.string.location_short));
                    return;
                }

                BookingType = "Reservation";
                ReservationDialog.show();
            }
        });

        /*Reservation Image Click popup end*/

        img_pickup_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt_pickup_location.setText("");
                PickupLarLng = null;
                PickupLatitude = 0.0;
                PickupLongtude = 0.0;
                MarkerAdd();
            }
        });

        img_drop_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt_drop_location.setText("");
                DropLarLng = null;
                DropLongtude = 0.0;
                DropLatitude = 0.0;
                MarkerAdd();
            }
        });

    }

    public void onBuyPressed() {
        PayPalPayment thingToBuy = getThingToBuy(PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intent = new Intent(HomeActivity.this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);
        startActivityForResult(intent, REQUEST_CODE_PAYMENT);
    }

    private PayPalPayment getThingToBuy(String paymentIntent) {
        return new PayPalPayment(new BigDecimal(Math.round(totlePrice)), "USD", CarName,
                paymentIntent);
    }

    public static boolean CheckDates(String startDate, String currentDate)
    {
        SimpleDateFormat dfDate  = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        boolean b = false;
        try {
            if(dfDate.parse(startDate).after(dfDate.parse(currentDate)))
            {
                b = true;//If start date is before end date
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return b;
    }


    public void LocationAutocompleate(AutoCompleteTextView locationEditext, final String clickText){
        locationEditext.setThreshold(1);

        //Set adapter to AutoCompleteTextView
        locationEditext.setAdapter(new AutoCompleteAdapter(HomeActivity.this, R.layout.location_list_item));
        locationEditext.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                InputMethodManager imm = (InputMethodManager) getSystemService(
                        INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        });
        locationEditext.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Show Alert
                Toast.makeText(getBaseContext(), "Position:" + position + "==" + clickText + "==" + " Month:" + parent.getItemAtPosition(position),
                        Toast.LENGTH_LONG).show();

                Log.d("AutocompleteContacts", "Position:" + position + " Month:" + parent.getItemAtPosition(position));

                InputMethodManager imm = (InputMethodManager) getSystemService(
                        INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                if (clickText.equals("pickeup")) {
                    if (edt_pickup_location.getText().toString().length() > 0) {
                        //DrowLineGoogleMap();
                        if (Common.isNetworkAvailable(HomeActivity.this)) {
                            Log.d("Location name", "Location name = " + edt_pickup_location.getText().toString());
                            bothLocationString = "pickeup";
                            LocationAddress.getAddressFromLocation(edt_pickup_location.getText().toString(), getApplicationContext(), new GeocoderHandlerLatitude());
                        } else {
                            Toast.makeText(HomeActivity.this, "No Network", Toast.LENGTH_LONG).show();
                        }
                    }
                } else if (clickText.equals("drop")) {
                    Log.d("Location name", "Location name = " + edt_pickup_location.getText().toString());
                    if (Common.isNetworkAvailable(HomeActivity.this)) {
                        bothLocationString = "drop";
                        LocationAddress.getAddressFromLocation(edt_drop_location.getText().toString(), getApplicationContext(), new GeocoderHandlerLatitude());
                    } else {
                        Toast.makeText(HomeActivity.this, "No Network", Toast.LENGTH_LONG).show();
                    }
                }

            }
        });
    }

    private void updateLabel() {

        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());

        txt_date.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        Log.d("Map Ready", "Map Ready" + gpsTracker.getLatitude() + "==" + gpsTracker.getLongitude());
    }

    /**
     * When the map is not ready the CameraUpdateFactory cannot be used. This should be called on
     * all entry points that call methods on the Google Maps API.
     */
    private boolean checkReady() {
        if (googleMap == null) {
            Toast.makeText(this, "Google Map not ready", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void CaculationDirationIon(){
        String CaculationLocUrl = "";
//        try {
//            DrowLocUrl = "http://maps.googleapis.com/maps/api/directions/json?sensor=true&mode=driving&origin="+URLEncoder.encode(edt_pickup_location.getText().toString(), "UTF-8")+"&destination="+URLEncoder.encode(edt_drop_location.getText().toString(), "UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }

        CaculationLocUrl = "http://maps.googleapis.com/maps/api/directions/json?sensor=true&mode=driving&origin="+PickupLatitude+","+PickupLongtude+"&destination="+DropLatitude+","+DropLongtude;
        Log.d("CaculationLocUrl","CaculationLocUrl = "+CaculationLocUrl);
        Ion.with(HomeActivity.this)
            .load(CaculationLocUrl)
            .setTimeout(10000)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception error, JsonObject result) {
                        // do stuff with the result or error

                        ProgressDialog.cancel();
                        cusRotateLoading.stop();

                        Log.d("Enter", "Calculation result = " + result + "== Error = " + error);
                        if (error == null) {
                            try {
                                Log.d("Enter 1", "Yes");
                                JSONObject resObj = new JSONObject(result.toString());
                                Log.d("Enter 2", "Yes");
                                if (resObj.getString("status").toLowerCase().equals("ok")) {
                                    Log.d("Enter 3", "Yes");

                                    JSONArray routArray = new JSONArray(resObj.getString("routes"));
                                    JSONObject routObj = routArray.getJSONObject(0);
                                    Log.d("Enter", "DrowLocUrl geoObj one= " + routObj);
                                    JSONArray legsArray = new JSONArray(routObj.getString("legs"));
                                    JSONObject legsObj = legsArray.getJSONObject(0);

                                    JSONObject disObj = new JSONObject(legsObj.getString("distance"));
                                    //if (disObj.getInt("value") > 1000)
                                    distance = (float) disObj.getInt("value") / 1000;
//                                    else if (disObj.getInt("value") > 100)
//                                        distance = (float) disObj.getInt("value") / 100;
//                                    else if (disObj.getInt("value") > 10)
//                                        distance = (float) disObj.getInt("value") / 10;
//                                    else if(disObj.getInt("value") == 0)
//                                        distance = (float) disObj.getInt("value");
                                    Log.d("Enter", "distance = " + distance);
                                    Log.d("Enter", "dis = " + distance);

                                    JSONObject duration = new JSONObject(legsObj.getString("duration"));

                                    AstTime = duration.getString("text");
                                    String[] durTextSpi = AstTime.split(" ");
                                    Log.d("Enter", "min  = durTextSpi = " + durTextSpi.length);
                                    int hours = 0;
                                    int mintus = 0;
                                    if (durTextSpi.length == 4) {
                                        hours = Integer.parseInt(durTextSpi[0]) * 60;
                                        mintus = Integer.parseInt(durTextSpi[2]);
                                    } else if (durTextSpi.length == 2) {
                                        if (durTextSpi[1].contains("mins"))
                                            mintus = Integer.parseInt(durTextSpi[0]);
                                        else
                                            mintus = Integer.parseInt(durTextSpi[0]);
                                    }
                                Log.d("Enter","hours = "+hours+"=="+mintus);
                                    totalTime = mintus + hours;

                                    googleDuration = duration.getInt("value");


                                    if(false) {
                                        for (int ci = 0; ci < cabDetailArray.size(); ci++) {

                                            CabDetails FixCabDetails = cabDetailArray.get(ci);

                                            for (int fi = 0; fi < FixRateArray.size(); fi++){
                                                HashMap<String,String> FixHasMap = FixRateArray.get(fi);

                                                Log.d("Enter","car_type_id = "+FixHasMap.get("car_type_id")+"=="+FixCabDetails.getId());
                                                if(FixHasMap.get("car_type_id").equals(FixCabDetails.getId())){
                                                    CabDetails cabDetails = cabDetailArray.get(ci);
                                                    cabDetails.setFixPrice(FixHasMap.get("fix_price").toString());
                                                    cabDetails.setAreaId(FixHasMap.get("area_id").toString());
                                                    break;
                                                }

                                                Log.d("Enter","car_type_id fi = "+fi);
                                            }
                                            Log.d("Enter","car_type_id ci = "+ci);
                                        }
                                    }else{
                                        for (int ci = 0; ci < cabDetailArray.size(); ci++) {
                                            CabDetails AllCabDetails = cabDetailArray.get(ci);
                                            AllCabDetails.setFixPrice("");
                                            AllCabDetails.setAreaId("");
                                        }
                                    }
                                    CabDetails cabDetails = cabDetailArray.get(0);
                                    if(!cabDetails.getFixPrice().equals("")) {
                                        layout_timming.setVisibility(View.GONE);
                                        layout_far_breakup.setVisibility(View.INVISIBLE);
                                        totlePrice = Float.parseFloat(cabDetails.getFixPrice());
                                        txt_total_price.setText(Math.round(totlePrice) + "");
                                    }else {
                                        Log.d("Enter", "fromintailrate = " + car_rate + "==" + FirstKm + "==" + distance + "==" + fromintailrate + "==" + ride_time_rate + "==" + totalTime);
                                        if (car_rate != null && fromintailrate != null && ride_time_rate != null)
                                            totlePrice = Common.getTotalPrice(car_rate, FirstKm, distance, fromintailrate, ride_time_rate, totalTime);
                                        else
                                            totlePrice = 0f;

                                        Log.d("Enter", "totlePrice = " + totlePrice);

                                        layout_timming.setVisibility(View.VISIBLE);
                                        layout_far_breakup.setVisibility(View.VISIBLE);
                                        txt_total_price.setText(Math.round(totlePrice) + "");
                                    }

                                    LocationDistanse = true;


                                } else {
                                    LocationDistanse = false;
                                    Toast.makeText(HomeActivity.this, getResources().getString(R.string.location_long), Toast.LENGTH_LONG).show();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Common.ShowHttpErrorMessage(HomeActivity.this, error.getMessage());
                        }
                    }
                });

        MarkerAdd();

        SetNowDialogCabValue();
    }

    public class CaculationDiration extends AsyncTask<String, Integer, String>{

        private String content =  null;

        String DrowLocUrl = "";
        public CaculationDiration(){
            try {
                DrowLocUrl = "http://maps.googleapis.com/maps/api/directions/json?sensor=true&mode=driving&origin="+URLEncoder.encode(edt_pickup_location.getText().toString(), "UTF-8")+"&destination="+URLEncoder.encode(edt_drop_location.getText().toString(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
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
            boolean isStatus = Common.ShowHttpErrorMessage(HomeActivity.this,result);
            if(isStatus) {
                try {
                    JSONObject resObj = new JSONObject(result);
                    if (resObj.getString("status").toLowerCase().equals("ok")) {


                        JSONArray routArray = new JSONArray(resObj.getString("routes"));
                        JSONObject routObj = routArray.getJSONObject(0);
                        Log.d("geoObj", "DrowLocUrl geoObj one= " + routObj);
                        JSONArray legsArray = new JSONArray(routObj.getString("legs"));
                        JSONObject legsObj = legsArray.getJSONObject(0);

                        JSONObject disObj = new JSONObject(legsObj.getString("distance"));
                        if (disObj.getInt("value") > 1000)
                            distance = (float) disObj.getInt("value") / 1000;
                        else if (disObj.getInt("value") > 100)
                            distance = (float) disObj.getInt("value") / 100;
                        else if (disObj.getInt("value") > 10)
                            distance = (float) disObj.getInt("value") / 10;
                        else if(disObj.getInt("value") == 0)
                            distance = (float) disObj.getInt("value");
                        Log.d("distance", "distance = " + distance);
                        Log.d("dis", "dis = " + distance);

                        JSONObject duration = new JSONObject(legsObj.getString("duration"));

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

                        totalTime = mintus + hours;

                        googleDuration = duration.getInt("value");
                        Log.d("fromintailrate", "fromintailrate = " + car_rate + "==" + FirstKm + "==" + distance + "==" + fromintailrate + "==" + ride_time_rate + "==" + totalTime);
                        if (car_rate != null && fromintailrate != null && ride_time_rate != null)
                            totlePrice = Common.getTotalPrice(car_rate, FirstKm, distance, fromintailrate, ride_time_rate, totalTime);
                        else
                            totlePrice = 0f;

                        Log.d("totlePrice", "totlePrice = " + totlePrice);

                        txt_total_price.setText(Math.round(totlePrice) + "");
                        LocationDistanse = true;
                    } else {
                        LocationDistanse = false;
                        Toast.makeText(HomeActivity.this, getResources().getString(R.string.location_long), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void CarDetailTab(int position) {
        CabPositon = position;

        if(cabDetailArray != null && cabDetailArray.size() > 0){
            CabDetails cabDetails = cabDetailArray.get(position);


            if(DayNight.equals("day")) {
                car_rate = cabDetails.getCarRate();
                fromintailrate = cabDetails.getFromintailrate();
                Log.d("ride_time_rate","ride_time_rate one day= "+cabDetails.getRideTimeRate());
                if(cabDetails.getRideTimeRate() != null) {
                    ride_time_rate = cabDetails.getRideTimeRate();
                }
            }else if(DayNight.equals("night")) {
                car_rate = cabDetails.getNightIntailrate();
                fromintailrate = cabDetails.getNightFromintailrate();
                Log.d("ride_time_rate","ride_time_rate one night= "+cabDetails.getRideTimeRate());
                if(cabDetails.getNightRideTimeRate() != null) {
                    ride_time_rate = cabDetails.getNightRideTimeRate();
                }
            }

            Log.d("ride_time_rate","ride_time_rate two= "+ride_time_rate);

            txt_car_header.setText(cabDetails.getCartype().toUpperCase());
            CarName = cabDetails.getCartype().toUpperCase();
            txt_car_descriptin.setText(cabDetails.getDescription());
            txt_first_price.setText("$"+car_rate);
            FirstKm = Float.parseFloat(cabDetails.getIntialkm());
            txt_first_km.setText("First "+FirstKm+" km");
            txt_sec_pric.setText("$ "+fromintailrate+"/km");
            txt_sec_km.setText("After "+FirstKm+" km");

            truckIcon = cabDetails.getIcon();
            truckType = cabDetails.getCartype();
            CabId = cabDetails.getId();
            AreaId = cabDetails.getAreaId();
            transfertype = cabDetails.getTransfertype();

            if(cabDetails.getRideTimeRate() != null || cabDetails.getNightRideTimeRate() != null && !cabDetails.getNightRideTimeRate().equals("0")){

                layout_three.setVisibility(View.GONE);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        AbsoluteLayout.LayoutParams.WRAP_CONTENT, AbsoluteLayout.LayoutParams.MATCH_PARENT);
                params.weight = 1.0f;
                layout_one.setLayoutParams(params);
                layout_two.setLayoutParams(params);
                layout_three.setLayoutParams(params);

                layout_three.setVisibility(View.VISIBLE);
                txt_thd_price.setText(ride_time_rate+"/min");
            }else{
                layout_three.setVisibility(View.GONE);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        AbsoluteLayout.LayoutParams.WRAP_CONTENT, AbsoluteLayout.LayoutParams.MATCH_PARENT);
                params.weight = 1.5f;
                layout_one.setLayoutParams(params);
                layout_two.setLayoutParams(params);
            }

            for(int i=0;i<cabDetailArray.size();i++) {
                CabDetails allcabDetails = cabDetailArray.get(i);
                Log.d("position","position = "+position+"=="+i);
                if(i == position) {
                    allcabDetails.setIsSelected(true);
                }else{
                    allcabDetails.setIsSelected(false);
                }
            }
            cabDetailAdapter.notifyDataSetChanged();

            List<String> list = new ArrayList<String>();
            for(int si=0;si<Integer.parseInt(cabDetails.getSeatCapacity());si++){
                int seat = si+1;
                list.add(seat+"");
            }
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, list);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner_person.setAdapter(dataAdapter);

            if(!cabDetails.getFixPrice().equals("")) {
                layout_timming.setVisibility(View.GONE);
                layout_far_breakup.setVisibility(View.INVISIBLE);
                txt_total_price.setText(cabDetails.getFixPrice());
                totlePrice = Float.parseFloat(cabDetails.getFixPrice());
            }else {
                layout_timming.setVisibility(View.VISIBLE);
                layout_far_breakup.setVisibility(View.VISIBLE);
                Log.d("fromintailrate","fromintailrate = "+car_rate+"=="+fromintailrate+"=="+ride_time_rate);
                if(car_rate != null && fromintailrate != null && ride_time_rate != null)
                    totlePrice = Common.getTotalPrice(car_rate, FirstKm, distance, fromintailrate, ride_time_rate, totalTime);
                Log.d("totlePrice","totlePrice = "+totlePrice);
                txt_total_price.setText(Math.round(totlePrice) + "");
            }

        }
    }


    public void SetNowDialogCabValue(){

        JSONArray CabDetailAry = Common.CabDetail;
        if(CabDetailAry != null && CabDetailAry.length() > 0){
            Log.d("cabDetailArray","cabDetailArray = "+cabDetailArray.size());
            if(cabDetailArray != null && cabDetailArray.size() > 0){

                CabDetails cabDetails = cabDetailArray.get(0);
                txt_car_header.setText(cabDetails.getCartype().toUpperCase());
                CarName = cabDetails.getCartype().toUpperCase();
                txt_car_descriptin.setText(cabDetails.getDescription());

                if(DayNight.equals("day")) {
                    car_rate = cabDetails.getCarRate();
                    fromintailrate = cabDetails.getFromintailrate();
                    if(cabDetails.getRideTimeRate() != null) {
                        ride_time_rate = cabDetails.getRideTimeRate();
                    }
                }
                else if(DayNight.equals("night")) {
                    car_rate = cabDetails.getNightIntailrate();
                    fromintailrate = cabDetails.getNightFromintailrate();
                    if(cabDetails.getNightRideTimeRate() != null && !cabDetails.getNightRideTimeRate().equals("0")) {
                        ride_time_rate = cabDetails.getNightRideTimeRate();
                    }
                }
                txt_first_price.setText(car_rate);
                FirstKm = Float.parseFloat(cabDetails.getIntialkm());
                txt_first_km.setText(getResources().getString(R.string.first)+" "+FirstKm+" "+getResources().getString(R.string.km));
                txt_sec_pric.setText(fromintailrate+"/"+getResources().getString(R.string.km));
                txt_sec_km.setText(getResources().getString(R.string.after)+" "+FirstKm+" "+getResources().getString(R.string.km));

                if(cabDetails.getRideTimeRate() != null || cabDetails.getNightRideTimeRate() != null && !cabDetails.getNightRideTimeRate().equals("0")){
                    layout_three.setVisibility(View.VISIBLE);
                    txt_thd_price.setText(ride_time_rate+"/"+getResources().getString(R.string.min));
                }else{
                    layout_three.setVisibility(View.GONE);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            AbsoluteLayout.LayoutParams.WRAP_CONTENT, AbsoluteLayout.LayoutParams.MATCH_PARENT);
                    params.weight = 1.5f;
                    layout_one.setLayoutParams(params);
                    layout_two.setLayoutParams(params);
                }

                truckIcon = cabDetails.getIcon();
                truckType = cabDetails.getCartype();
                CabId = cabDetails.getId();
                AreaId = cabDetails.getAreaId();
                transfertype = cabDetails.getTransfertype();

                cabDetailAdapter = new CabDetailAdapter(HomeActivity.this,cabDetailArray);
                recycle_cab_detail.setAdapter(cabDetailAdapter);
                cabDetailAdapter.setOnCabDetailItemClickListener(HomeActivity.this);
                cabDetailAdapter.updateItems();

                List<String> list = new ArrayList<String>();
                for(int si=0;si<Integer.parseInt(cabDetails.getSeatCapacity());si++){
                    int seat = si+1;
                    list.add(seat+"");
                }
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(HomeActivity.this,
                        android.R.layout.simple_spinner_item, list);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_person.setAdapter(dataAdapter);

                Log.d("Fix Price","Fix Price = "+cabDetails.getFixPrice());

            }

        }

    }

    public void PickupFixRateCall(){

        ProgressDialog.show();
        cusRotateLoading.start();

        FixRateArray = new ArrayList<>();

        String FixAreaUrl = Url.FixAreaUrl+"?pick_lat="+PickupLatitude+"&pick_long="+PickupLongtude+"&drop_lat="+DropLatitude+"&drop_long="+DropLongtude;
        Log.d("Enter","FixAreaUrl ="+FixAreaUrl);

        Ion.with(HomeActivity.this)
                .load(FixAreaUrl)
                .setTimeout(6000)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception error, JsonObject result) {
                        // do stuff with the result or error
                        Log.d("Enter", "load_trips result = " + result + "==" + error);
                        if (error == null) {

                            try {
                                JSONObject resObj = new JSONObject(result.toString());
                                Log.d("Enter", "loadTripsUrl two= " + resObj);
                                JSONArray fixAreaArray = new JSONArray(resObj.getString("fixAreaPriceList"));
                                for(int fi=0;fi<fixAreaArray.length();fi++){
                                    JSONObject fixAreaObj = fixAreaArray.getJSONObject(fi);

                                    Log.d("FixRateArray","FixAreaUrl FixRateArray = "+fixAreaObj);
                                    if(!fixAreaObj.getString("fix_price").equals("0")) {
                                        HashMap<String,String> FixHasMap = new HashMap<String, String>();
                                        FixHasMap.put("fix_price", fixAreaObj.getString("fix_price").toString());
                                        FixHasMap.put("car_type_id", fixAreaObj.getString("car_type_id").toString());
                                        FixHasMap.put("car_type_name", fixAreaObj.getString("car_type_name").toString());
                                        FixHasMap.put("area_title", fixAreaObj.getString("area_title").toString());
                                        FixHasMap.put("area_id", fixAreaObj.getString("area_id").toString());
                                        FixRateArray.add(FixHasMap);
                                    }

                                }
                                Log.d("FixRateArray","FixAreaUrl FixRateArray = "+FixRateArray.size());
                                CaculationDirationIon();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        } else {
                            ProgressDialog.cancel();
                            cusRotateLoading.stop();

                            Common.ShowHttpErrorMessage(HomeActivity.this, error.getMessage());
                        }
                    }
                });

    }

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }
            if(locationAddress != null) {
                if (locationAddress.equals("Unable connect to Geocoder")) {
                    Toast.makeText(HomeActivity.this, "No Network conection", Toast.LENGTH_LONG).show();
                } else {
                    Log.d("locationAddress", "locationAddress = " + locationAddress + "==" + bothLocationString);
                    if (bothLocationString.equals("pickeup") && edt_pickup_location != null)
                        edt_pickup_location.setText(locationAddress);
                    else if (bothLocationString.equals("drop") && edt_drop_location != null)
                        edt_drop_location.setText(locationAddress);
                }
            }else{
                NowDialog.cancel();
                layout_reservation.setVisibility(View.VISIBLE);
                Toast.makeText(HomeActivity.this, getResources().getString(R.string.location_long), Toast.LENGTH_LONG).show();

            }
        }
    }

    private class GeocoderHandlerLatitude extends Handler{
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }
            Log.d("locationAddress","locationAddress = "+locationAddress);
            if(locationAddress != null) {
                if (locationAddress.equals("Unable connect to Geocoder")) {
                    Toast.makeText(HomeActivity.this, "No Network conection", Toast.LENGTH_LONG).show();
                } else {
                    String[] LocationSplit = locationAddress.split("\\,");
                    Log.d("locationAddress", "locationAddress = " + locationAddress + "==" + Double.parseDouble(LocationSplit[0]) + "==" + Double.parseDouble(LocationSplit[1]));
                    if (bothLocationString.equals("pickeup")) {
                        PickupLatitude = Double.parseDouble(LocationSplit[0]);
                        PickupLongtude = Double.parseDouble(LocationSplit[1]);
                        PickupLarLng = new LatLng(Double.parseDouble(LocationSplit[0]), Double.parseDouble(LocationSplit[1]));
                    }
                    else if (bothLocationString.equals("drop")) {
                        DropLongtude = Double.parseDouble(LocationSplit[1]);
                        DropLatitude = Double.parseDouble(LocationSplit[0]);

                        DropLarLng = new LatLng(Double.parseDouble(LocationSplit[0]), Double.parseDouble(LocationSplit[1]));
                    }


                    if (edt_drop_location.getText().length() > 0 && edt_pickup_location.getText().length() > 0) {
                        if (checkReady() && Common.isNetworkAvailable(HomeActivity.this)) {

                            //PickupFixRateCall();
                            CaculationDirationIon();
                        } else {
                            Common.showInternetInfo(HomeActivity.this, "");
                        }
                    }else{
                        MarkerAdd();
                    }
                }
            }
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        common.SlideMenuDesign(slidingMenu, HomeActivity.this, "home");
    }

    /*Add marker function*/
    public void MarkerAdd(){

        if(checkReady()) {

            if(marker != null)
                googleMap.clear();

            LatLngBounds.Builder builder = new LatLngBounds.Builder();

                if(PickupLarLng != null) {
                    marker = new MarkerOptions()
                            .position(PickupLarLng)
                            .title("Pick Up Location")

                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.pickup_location_icon));
                    PickupMarker =  googleMap.addMarker(marker);
                    PickupMarker.setDraggable(true);
                    builder.include(marker.getPosition());


                }

                if(DropLarLng != null) {
                    marker = new MarkerOptions()
                            .position(DropLarLng)
                            .title("Drop Location")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.drop_location_icon));

                    DropMarker = googleMap.addMarker(marker);
                    DropMarker.setDraggable(true);
                    builder.include(marker.getPosition());
                }

            // .icon(BitmapDescriptorFactory.fromResource(R.drawable.location_icon))

            if(DropLarLng != null || PickupLarLng != null) {
                LatLngBounds bounds = builder.build();

                //CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                Log.d("areBoundsTooSmall", "areBoundsTooSmall = " + areBoundsTooSmall(bounds, 300));
                if (areBoundsTooSmall(bounds, 300)) {
                    //googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(bounds.getCenter(), 10));
                    CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(bounds.getCenter(), 10);
                    googleMap.animateCamera(cu, new GoogleMap.CancelableCallback() {

                        @Override
                        public void onFinish() {
                            if(PickupMarker!=null)
                                BounceAnimationMarker(PickupMarker,PickupLarLng);
                            if(DropMarker != null)
                                BounceAnimationMarker(DropMarker,DropLarLng);
                        }

                        @Override
                        public void onCancel() {

                        }
                    });


//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            if(PickupMarker!=null)
//                                BounceAnimationMarker(PickupMarker,PickupLarLng);
//                            if(DropMarker != null)
//                                BounceAnimationMarker(DropMarker,DropLarLng);
//                        }
//                    }, 1000);

                } else {
                    //googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 3));
//                googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(
//                        bounds,
//                        (int) (this.getResources().getDisplayMetrics().widthPixels * 1),
//                        (int) (this.getResources().getDisplayMetrics().heightPixels * 1),
//                        200));

                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 50);
                    googleMap.animateCamera(cu, new GoogleMap.CancelableCallback() {

                        @Override
                        public void onFinish() {
                            CameraUpdate zout = CameraUpdateFactory.zoomBy((float) -1.0);
                            googleMap.animateCamera(zout);
                            BounceAnimationMarker(PickupMarker,PickupLarLng);
                            BounceAnimationMarker(DropMarker,DropLarLng);
                        }

                        @Override
                        public void onCancel() {
//                            CameraUpdate zout = CameraUpdateFactory.zoomBy((float) -1.0);
//                            googleMap.animateCamera(zout);
                        }
                    });

                }
            }


//            CameraUpdate zoom=CameraUpdateFactory.zoomTo(5);
//            googleMap.animateCamera(zoom);
            //googleMap.moveCamera(cu);


//

            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(final Marker pickMarker) {

                    Log.d("bothLocationString","bothLocationString pickup= "+bothLocationString+"=="+marker.getTitle()+"=="+edt_pickup_location.getText().toString());
                    if(marker.getTitle().equals("Pick Up Location"))
                        bothLocationString = "pickeup";
                    else if(marker.getTitle().equals("Drop Location"))
                        bothLocationString = "drop";
                    Log.d("bothLocationString","bothLocationString pickup= "+bothLocationString+"=="+marker.getTitle()+"=="+edt_pickup_location.getText().toString());
                    Log.d("bothLocationString", "bothLocationString drop= " + bothLocationString + "==" + marker.getTitle() + "==" + edt_drop_location.getText().toString());

                    return false;
                }
            });


            googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDragStart(Marker marker) {
                    if (marker.getTitle().equals("Pick Up Location"))
                        bothLocationString = "pickeup";
                    else if (marker.getTitle().equals("Drop Location"))
                        bothLocationString = "drop";
                    Log.d("bothLocationString", "bothLocationString pickup= " + bothLocationString + "==" + marker.getTitle() + "==" + edt_pickup_location.getText().toString());
                    Log.d("bothLocationString", "bothLocationString drop= " + bothLocationString + "==" + marker.getTitle() + "==" + edt_drop_location.getText().toString());
                    Log.d("latitude", "latitude one = " + marker.getPosition().latitude);
                }

                @Override
                public void onMarkerDrag(Marker marker) {
                    Log.d("latitude", "latitude two= " + marker.getPosition().latitude);
                }

                @Override
                public void onMarkerDragEnd(Marker mrk) {

                    Log.d("latitude", "latitude three = " + mrk.getPosition().latitude + "==" + mrk.getPosition().longitude);
                    if (Common.isNetworkAvailable(HomeActivity.this)) {
                        ClickOkButton = true;
                        LocationAddress locationAddress = new LocationAddress();
                        locationAddress.getAddressFromLocation(mrk.getPosition().latitude, mrk.getPosition().longitude,
                                getApplicationContext(), new GeocoderHandler());

                        Log.d("bothLocationString", "bothLocationString = " + bothLocationString);
//                        if (bothLocationString.equals("pickeup"))
//                            mrk.setTitle(edt_pickup_location.getText().toString());
//                        if (bothLocationString.equals("drop"))
//                            mrk.setTitle(edt_drop_location.getText().toString());
                    } else {
                        Toast.makeText(HomeActivity.this, "No network", Toast.LENGTH_LONG).show();
                    }
                }
            });


        }
    }

    public void BounceAnimationMarker(final Marker animationMarker, final LatLng animationLatLng){
        if(animationLatLng != null) {
            final Handler handler = new Handler();
            final long start = SystemClock.uptimeMillis();
            Projection proj = googleMap.getProjection();
            Point startPoint = proj.toScreenLocation(animationLatLng);
            startPoint.offset(0, -100);
            final LatLng startLatLng = proj.fromScreenLocation(startPoint);
            final long duration = 1500;
            final Interpolator interpolator = new BounceInterpolator();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    long elapsed = SystemClock.uptimeMillis() - start;
                    float t = interpolator.getInterpolation((float) elapsed / duration);
                    double lng = t * animationLatLng.longitude + (1 - t) * startLatLng.longitude;
                    double lat = t * animationLatLng.latitude + (1 - t) * startLatLng.latitude;
                    animationMarker.setPosition(new LatLng(lat, lng));
                    if (t < 1.0) {
                        // Post again 16ms later.
                        handler.postDelayed(this, 16);
                    }
                }
            });
        }
    }

    private boolean areBoundsTooSmall(LatLngBounds bounds, int minDistanceInMeter) {
        float[] result = new float[1];
        Location.distanceBetween(bounds.southwest.latitude, bounds.southwest.longitude, bounds.northeast.latitude, bounds.northeast.longitude, result);
        return result[0] < minDistanceInMeter;
    }

    public void EditorActionListener(final EditText locationEditext, final String clickText){

        locationEditext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                Log.d("Edit text", "Edit text = " + v.getText().toString());

                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {

                    Log.d("locationEditext", "locationEditext = " + locationEditext.getText().toString());
                    if (locationEditext.getText().toString().length() > 0) {

                        if (clickText.equals("pickeup")) {
                            if (Common.isNetworkAvailable(HomeActivity.this)) {
                                bothLocationString = "pickeup";
                                LocationAddress.getAddressFromLocation(edt_pickup_location.getText().toString(), getApplicationContext(), new GeocoderHandlerLatitude());
                            } else {
                                Toast.makeText(HomeActivity.this, "No Network", Toast.LENGTH_LONG).show();
                            }
                        } else if (clickText.equals("drop")) {
                            if (Common.isNetworkAvailable(HomeActivity.this)) {
                                bothLocationString = "drop";
                                LocationAddress.getAddressFromLocation(edt_drop_location.getText().toString(), getApplicationContext(), new GeocoderHandlerLatitude());
                            } else {
                                Toast.makeText(HomeActivity.this, "No Network", Toast.LENGTH_LONG).show();
                            }
                        }
                        layout_pickup_drag_location.setVisibility(View.GONE);
                        if (edt_drop_location.getText().length() > 0 && edt_pickup_location.getText().length() > 0) {
                            if (checkReady() && Common.isNetworkAvailable(HomeActivity.this)) {
                                //new CaculationDiration().execute();
                                //CaculationDirationIon();
                            } else {
                                Common.showInternetInfo(HomeActivity.this, "");
                            }
                        }

                    } else {
                        PickupLarLng = null;
                        PickupLatitude = 0.0;
                        PickupLongtude = 0.0;
                        Toast.makeText(HomeActivity.this, "Please Enter Location", Toast.LENGTH_LONG).show();
                    }
                }
                return false;
            }
        });
    }

    public void AddTextChangeListener(final EditText locationEditext, final String clickText){
        locationEditext.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                Log.d("clickText", "clickText = " + clickText);
                if (s.length() != 0) {

                    if (clickText.equals("drop")) {
                        img_drop_close.setVisibility(View.VISIBLE);
                    } else if (clickText.equals("pickeup")) {
                        img_pickup_close.setVisibility(View.VISIBLE);
                    }
                    Log.d("ClickOkButton", "ClickOkButton = " + ClickOkButton);
                    if (!ClickOkButton) {
                        layout_pickup_drag_location.setVisibility(View.VISIBLE);
                        Log.d("ClickOkButton", "ClickOkButton = " + s.toString());
                        //new getPickupDropAddress(s.toString()).execute();
                        getPickupDropAddressIon(s.toString());
                    }
                } else {
                    if (clickText.equals("drop")) {
                        img_drop_close.setVisibility(View.GONE);
                        DropLarLng = null;
                        DropLongtude = 0.0;
                        DropLatitude = 0.0;
                    } else if (clickText.equals("pickeup")) {
                        img_pickup_close.setVisibility(View.GONE);
                        PickupLarLng = null;
                        PickupLatitude = 0.0;
                        PickupLongtude = 0.0;
                    }
                    layout_pickup_drag_location.setVisibility(View.GONE);

                    MarkerAdd();
                }

            }
        });

    }

    public void AddSetOnClickListener(EditText locationEditext, final String ClickValue){

        locationEditext.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ClickOkButton = false;
                bothLocationString = ClickValue;
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                if (ClickValue.equals("drop")) {
                    params.setMargins(0, (int) getResources().getDimension(R.dimen.height_175), 0, 0);
                } else if (ClickValue.equals("pickeup")) {
                    params.setMargins(0, (int) getResources().getDimension(R.dimen.height_130), 0, 0);
                }
                layout_pickup_drag_location.setLayoutParams(params);
                return false;
            }
        });

        locationEditext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickOkButton = false;
                bothLocationString = ClickValue;
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                if (ClickValue.equals("drop")) {
                    params.setMargins(0, (int) getResources().getDimension(R.dimen.height_175), 0, 0);
                } else if (ClickValue.equals("pickeup")) {
                    params.setMargins(0, (int) getResources().getDimension(R.dimen.height_130), 0, 0);
                }
                layout_pickup_drag_location.setLayoutParams(params);
            }
        });
    }


    public void getPickupDropAddressIon(String inputSting){
        String locatinUrl = "";
        locationArray = new ArrayList<>();
        try {
            locatinUrl = "https://maps.googleapis.com/maps/api/place/autocomplete/json?key=AIzaSyD5QFt6IdBuIHpvV1Z9FdAs0yBnBBwyI_g&input=" + URLEncoder.encode(inputSting, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Log.d("locatinUrl","Login locatinUrl = "+locatinUrl);
        Ion.with(HomeActivity.this)
                .load(locatinUrl)
                .setTimeout(10000)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception error, JsonObject result) {
                        // do stuff with the result or error
                        Log.d("Location result", "Login result = " + result + "==" + error);

                        if (error == null) {
                            try {
                                Log.d("Enter 4", "Yes");
                                JSONObject resObj = new JSONObject(result.toString());
                                if (resObj.getString("status").toLowerCase().equals("ok")) {
                                    Log.d("Enter 5", "Yes");
                                    JSONArray predsJsonArray = resObj.getJSONArray("predictions");
                                    for (int i = 0; i < predsJsonArray.length(); i++) {
                                        HashMap<String, String> locHashMap = new HashMap<String, String>();
                                        locHashMap.put("location name", predsJsonArray.getJSONObject(i).getString("description"));
                                        locationArray.add(locHashMap);
                                    }

                                    if (locationArray != null && locationArray.size() > 0) {
                                        recycle_pickup_location.setVisibility(View.VISIBLE);
                                        layout_no_result.setVisibility(View.GONE);
                                        pickupDropLocationAdapter = new PickupDropLocationAdapter(HomeActivity.this, locationArray);
                                        recycle_pickup_location.setAdapter(pickupDropLocationAdapter);
                                        pickupDropLocationAdapter.setOnDropPickupClickListener(HomeActivity.this);
                                        pickupDropLocationAdapter.updateItems();
                                    }

                                    Log.d("locationArray", "locationArray = " + locationArray.size());
                                } else if (resObj.getString("status").equals("ZERO_RESULTS")) {
                                    if (locationArray != null && locationArray.size() > 0)
                                        locationArray.clear();

                                    layout_no_result.setVisibility(View.VISIBLE);
                                    recycle_pickup_location.setVisibility(View.GONE);

                                    Log.d("locationArray", "locationArray = " + locationArray.size());
                                    if (pickupDropLocationAdapter != null)
                                        pickupDropLocationAdapter.updateBlankItems(locationArray);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } else {
                            Common.ShowHttpErrorMessage(HomeActivity.this, error.getMessage());
                        }
                    }
            });
    }

    @Override
    public void PickupDropClick(int position) {

        if(locationArray != null && locationArray.size() > 0){
        HashMap<String,String> picDrpHash = locationArray.get(position);
        Log.d("bothLocationString","bothLocationString = "+bothLocationString);
            if(!bothLocationString.equals("")) {
                if (bothLocationString.equals("pickeup")) {
                    edt_pickup_location.setText(picDrpHash.get("location name"));
                    if (Common.isNetworkAvailable(HomeActivity.this)) {
                        Log.d("Location name", "Location name = " + edt_pickup_location.getText().toString());
                        bothLocationString = "pickeup";
                        LocationAddress.getAddressFromLocation(picDrpHash.get("location name"), getApplicationContext(), new GeocoderHandlerLatitude());
                    } else {
                        Toast.makeText(HomeActivity.this, "No Network", Toast.LENGTH_LONG).show();
                    }
                } else if (bothLocationString.equals("drop")) {
                    edt_drop_location.setText(picDrpHash.get("location name"));
                    if (Common.isNetworkAvailable(HomeActivity.this)) {
                        Log.d("Location name", "Location name = " + edt_pickup_location.getText().toString());
                        bothLocationString = "drop";
                        LocationAddress.getAddressFromLocation(picDrpHash.get("location name"), getApplicationContext(), new GeocoderHandlerLatitude());
                    } else {
                        Toast.makeText(HomeActivity.this, "No Network", Toast.LENGTH_LONG).show();
                    }
                }


//                if (Common.isNetworkAvailable(HomeActivity.this)) {
//                    if (checkReady() && edt_drop_location.getText().length() > 0 && edt_pickup_location.getText().length() > 0) {
//                        //new CaculationDiration().execute();
//                        CaculationDirationIon();
//                    }
//                } else {
//                    Common.showInternetInfo(HomeActivity.this, "");
//                    return;
//                }
            }
        }
        InputMethodManager imm = (InputMethodManager) getSystemService(
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        layout_pickup_drag_location.setVisibility(View.GONE);
        //recycle_pickup_location.setVisibility(View.GONE);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        txt_home = null;
        layout_slidemenu = null;
        edt_pickup_location = null;
        edt_drop_location = null;
        edt_write_comment = null;
        layout_now = null;
        layout_reservation = null;
        gpsTracker = null;
        googleMap = null;
        cabDetailArray = null;
        marker = null;
        PickupLarLng = null;
        DropLarLng = null;
        arrayPoints = null;
        NowDialog = null;
        ReservationDialog = null;
        cabDetailAdapter = null;
        txt_car_header = null;
        txt_car_descriptin = null;
        txt_first_price = null;
        txt_first_km = null;
        txt_sec_pric = null;
        txt_sec_km = null;
        txt_thd_price = null;
        layout_one = null;
        layout_two = null;
        layout_three = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("requestCode","requestCode = "+requestCode);
        if (requestCode == 3) {
            if(data != null){
                String userUpd = data.getStringExtra("update_user_profile").toString();
                Log.d("requestCode","requestCode = "+userUpd);
                if(userUpd.equals("1")){
                    common.SlideMenuDesign(slidingMenu,HomeActivity.this,"home");
                }
            }
        }else if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm =
                        data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {
                        Log.d("paypal data", "paypal data = " + confirm);
                        Log.e("Show", "Show" + confirm.toJSONObject().toString(4));
                        Log.e("Show", "Show" + confirm.getPayment().toJSONObject().toString(4));
                        CashDialog.cancel();
                        NowDialog.show();
                        JSONObject conObj = new JSONObject(confirm.toJSONObject().toString(4));
                        JSONObject ResObj = new JSONObject(conObj.getString("response"));
                        transaction_id = ResObj.getString("id");
                        Log.d("stripe_id","stripe_id = "+transaction_id);
                        /**
                         *  TODO: send 'confirm' (and possibly confirm.getPayment() to your server for verification
                         */
                        Toast.makeText(getApplicationContext(), "PaymentConfirmation info received" +
                                " from PayPal", Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), "an extremely unlikely failure" +
                                " occurred:", Toast.LENGTH_LONG).show();
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "The user canceled.", Toast.LENGTH_LONG).show();
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Toast.makeText(getApplicationContext(), "An invalid Payment or PayPalConfiguration" +
                        " was submitted. Please see the docs.", Toast.LENGTH_LONG).show();
            }
        }else if(requestCode == REQUEST_CODE_STRIPE){
            if(data != null){
                transaction_id = data.getStringExtra("stripe_id").toString();
                Log.d("stripe_id","stripe_id = "+transaction_id);
                CashDialog.cancel();
                NowDialog.show();
            }
        }
    }


    @Override
    public void onBackPressed() {

        if(slidingMenu.isMenuShowing()){
            slidingMenu.toggle();
        }else {
            new AlertDialog.Builder(this)
                    .setTitle("Really Exit?")
                    .setMessage("Are you sure you want to exit?")
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface arg0, int arg1) {
                            HomeActivity.super.onBackPressed();
                        }
                    }).create().show();
        }
    }

}

package com.indosoft.binder;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.indosoft.binder.Accounts.Enable_location_A;
import com.indosoft.binder.Accounts.Login_A;
import com.indosoft.binder.CodeClasses.Variables;
import com.indosoft.binder.Main_Menu.MainMenuActivity;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.ads.AdRequest;
import com.facebook.ads.InterstitialAd;



import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Splash_A extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{
     final String TAG = Splash_A.class.getSimpleName();
    public static String statususer,bannerfb,interfb,statusapp,apkupdate,interadmob,banneradmob,admobappid;
    com.google.android.gms.ads.InterstitialAd mInterstitialAd;
    InterstitialAd interstitialAd;
    SharedPreferences sharedPreferences;
    SweetAlertDialog pDialog;
    Button startbutton;

    Handler max_handler;
    Runnable max_runable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_);

        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);
        pDialog.show();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            //Verifica permisos para Android 6.0+
            int permissionCheck = ContextCompat.checkSelfPermission(
                    this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                Log.i("Mensaje", "No se tiene permiso para leer.");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 225);
            } else {
                Log.i("Mensaje", "Se tiene permiso para leer!");
            }
        }


        getStatusapp("https://fando.xyz/minder/getstatus.php");
//
//        startbutton= findViewById(R.id.startbutt);
//        startbutton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//               showinters();
//
//            }
//        });

        sharedPreferences=getSharedPreferences(Variables.pref_name,MODE_PRIVATE);

            // here we check the user is already login or not
//        new Handler().postDelayed(new Runnable() {
//                public void run() {
//
//                    if (sharedPreferences.getBoolean(Variables.islogin, false)) {
//                        // if user is already login then we get the current location of user
//                        if(getIntent().hasExtra("action_type")){
//                            Intent intent= new Intent(Splash_A.this, MainMenuActivity.class);
//                            String action_type=getIntent().getExtras().getString("action_type");
//                            String receiverid=getIntent().getExtras().getString("senderid");
//                            String title=getIntent().getExtras().getString("title");
//                            String icon=getIntent().getExtras().getString("icon");
//
//                            intent.putExtra("icon",icon);
//                            intent.putExtra("action_type",action_type);
//                            intent.putExtra("receiverid",receiverid);
//                            intent.putExtra("title",title);
//
//
//                            startActivity(intent);
//                            finish();
//                        }
//                        else
//                        GPSStatus();
//
//                    } else {
//
//                        // else we will move the user to login screen
//                        startActivity(new Intent(Splash_A.this, Login_A.class));
//                        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
//                        finish();
//
//                    }
//                }
//            }, 2000);

        Get_screen_size();

        set_language_local();

//        max_handler=new Handler();
//        max_runable=new Runnable() {
//            @Override
//            public void run() {
//
//                if (sharedPreferences.getString(Variables.current_Lat, "").equals("") || sharedPreferences.getString(Variables.current_Lon, "").equals("")) {
//                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                    editor.putString(Variables.current_Lat, Variables.default_lat);
//                    editor.putString(Variables.current_Lon, Variables.default_lon);
//                    editor.commit();
//                }
//
//                startActivity(new Intent(Splash_A.this, MainMenuActivity.class));
//                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
//                finish();
//            }
//        };
//        max_handler.postDelayed(max_runable,15000);
    }


    public void set_language_local(){


        String language=sharedPreferences.getString(Variables.selected_language,null);
        Locale myLocale = new Locale(Locale.getDefault().getLanguage());
        if(language!=null && language.equalsIgnoreCase(getString(R.string.english))){
            myLocale = new Locale("en");

        }
        else if(language!=null && language.equalsIgnoreCase(getString(R.string.arabic))){
            myLocale = new Locale("ar");
        }

        if(myLocale.getLanguage().equalsIgnoreCase("en") || myLocale.getLanguage().equalsIgnoreCase("ar")) {
            Resources res = getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = new Configuration();
            conf.locale = myLocale;
            res.updateConfiguration(conf, dm);
            onConfigurationChanged(conf);

            Resources applicationRes = getApplicationContext().getResources();
            Configuration applicationConf = applicationRes.getConfiguration();
            applicationConf.setLocale(myLocale);
            applicationRes.updateConfiguration(applicationConf, applicationRes.getDisplayMetrics());

        }

    }


    // get the Gps status to check that either a mobile gps is on or off
    public void GPSStatus(){
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(!GpsStatus)
        {
            enable_location();

        }else {

            // if on then get the location of the user and save the location into the local database

            GetCurrentlocation();
        }
    }


    // if the Gps is successfully on then we will on the again check the Gps status
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==2){
            GPSStatus();
        }
    }



    public void Get_screen_size(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        Variables.screen_height = displayMetrics.heightPixels;
        Variables.screen_width = displayMetrics.widthPixels;
    }



    // if user does not permitt the app to get the location then we will go to the enable location screen to enable the location permission
    private void enable_location() {
        startActivity(new Intent(this, Enable_location_A.class));
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
        finishAffinity();
    }






    private FusedLocationProviderClient mFusedLocationClient;

    private void GetCurrentlocation() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // here if user did not give the permission of location then we move user to enable location screen
            enable_location();
            return;
        }

        buildGoogleApiClient();
        createLocationRequest();

    }


    public void Go_Next(Location location){

        if (location != null) {

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Variables.current_Lat, "" + location.getLatitude());
            editor.putString(Variables.current_Lon, "" + location.getLongitude());
            editor.commit();
            startActivity(new Intent(Splash_A.this, MainMenuActivity.class));
            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
            finish();

        } else {
            // else we will use the basic location

            if (sharedPreferences.getString(Variables.current_Lat, "").equals("") || sharedPreferences.getString(Variables.current_Lon, "").equals("")) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(Variables.current_Lat, Variables.default_lat);
                editor.putString(Variables.current_Lon, Variables.default_lon);
                editor.commit();
            }
            startActivity(new Intent(Splash_A.this, MainMenuActivity.class));
            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
            finish();
        }
    }



    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private static int UPDATE_INTERVAL = 3000;
    private static int FATEST_INTERVAL = 3000;
    private static int DISPLACEMENT = 0;
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();

        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    protected void createLocationRequest() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }


    LocationCallback locationCallback;
    protected void startLocationUpdates() {
        mGoogleApiClient.connect();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationCallback= new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {

                        Go_Next(location);
                        stopLocationUpdates();

                    }
                }
            }
        };

        mFusedLocationClient.requestLocationUpdates(mLocationRequest,locationCallback
                , Looper.myLooper());

    }


    protected void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(locationCallback);
    }


    @Override
    public void onDestroy() {
        if (mGoogleApiClient!=null && mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
            mGoogleApiClient.disconnect();
        }

        if(max_handler!=null && max_runable!=null){
            max_handler.removeCallbacks(max_runable);
        }

        super.onDestroy();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {

    }

    @Override
    public void onConnected(Bundle arg0) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }




    public  void showinters(){
        startbutton= findViewById(R.id.startbutt);
        startbutton.setVisibility(View.GONE);
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);
        pDialog.show();


        interstitialAd = new InterstitialAd(this, interfb);
        // Set listeners for the Interstitial Ad
        interstitialAd.setAdListener(new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
                // Interstitial ad displayed callback
                Log.e(TAG, "Interstitial ad displayed.");
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                pDialog.hide();
                if (sharedPreferences.getBoolean(Variables.islogin, false)) {
                    // if user is already login then we get the current location of user
                    if(getIntent().hasExtra("action_type")){
                        Intent intent= new Intent(Splash_A.this, MainMenuActivity.class);
                        String action_type=getIntent().getExtras().getString("action_type");
                        String receiverid=getIntent().getExtras().getString("senderid");
                        String title=getIntent().getExtras().getString("title");
                        String icon=getIntent().getExtras().getString("icon");

                        intent.putExtra("icon",icon);
                        intent.putExtra("action_type",action_type);
                        intent.putExtra("receiverid",receiverid);
                        intent.putExtra("title",title);


                        startActivity(intent);
                        finish();
                    }
                    else

                        GPSStatus();

                } else {
                    // else we will move the user to login screen
                    Intent intent= new Intent(Splash_A.this, Login_A.class);
                    overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                    startActivity(intent);
                    finish();

                }
                // Interstitial dismissed callback
                Log.e(TAG, "Interstitial ad dismissed.");
            }

            @Override
            public void onError(Ad ad, AdError adError) {
               interadmobload();

                // Ad error callback
                Log.e(TAG, "Interstitial ad failed to load: " + adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Interstitial ad is loaded and ready to be displayed
                Log.d(TAG, "Interstitial ad is loaded and ready to be displayed!");
                // Show the ad
                interstitialAd.show();
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback
                Log.d(TAG, "Interstitial ad clicked!");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Ad impression logged callback
                Log.d(TAG, "Interstitial ad impression logged!");
            }
        });

        // For auto play video ads, it's recommended to load the ad
        // at least 30 seconds before it is shown
        interstitialAd.loadAd();
    }



    private void getStatusapp(String url){
        pDialog.show();

        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {


                try {
//                    JSONObject jsonObject=response.getJSONObject("status");
                    statususer = response.getString("status");
                    bannerfb = response.getString("bannerfb");
                    interfb=response.getString("interfb");
                    banneradmob = response.getString("banneradmob");
                    interadmob=response.getString("interadmob");
                    apkupdate=response.getString("apkupdate");
                    statusapp=response.getString("statusapp");
                    admobappid=response.getString("admobappid");




                    Button button= findViewById(R.id.startbutt);
                    pDialog.hide();
                    button.setVisibility(View.VISIBLE);

                    if (statusapp.equals("0")){
                        update();
                        button.setText("UPDATE");
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                update();
                            }
                        });

                    }
                    else{
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                showinters();
                            }
                        });

                    }
















                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        Volley.newRequestQueue(getApplicationContext()).add(jsonObjectRequest);


    }

    private void update() {
        new SweetAlertDialog(Splash_A.this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Update App")
                .setContentText("App Need To Update")
                .setConfirmText("Update")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog
                                .setTitleText("Update From Playstore")
                                .setContentText("Please Wait, Open Playstore")
                                .setConfirmText("Go")
                                .changeAlertType(SweetAlertDialog.PROGRESS_TYPE);

                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(Uri.parse(
                                        "https://play.google.com/store/apps/details?id=" + apkupdate));
                                intent.setPackage("com.android.vending");
                                startActivity(intent);
//                                Do something after 100ms
                            }
                        }, 3000);



                    }
                })

                .show();
    }

    public  void interadmobload(){
        MobileAds.initialize(this,
                admobappid);
        mInterstitialAd = new com.google.android.gms.ads.InterstitialAd(this);
        mInterstitialAd.setAdUnitId(interadmob);
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdLoaded() {
                mInterstitialAd.show();
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                pDialog.hide();

                if (sharedPreferences.getBoolean(Variables.islogin, false)) {
                    // if user is already login then we get the current location of user
                    if(getIntent().hasExtra("action_type")){
                        Intent intent= new Intent(Splash_A.this, MainMenuActivity.class);
                        String action_type=getIntent().getExtras().getString("action_type");
                        String receiverid=getIntent().getExtras().getString("senderid");
                        String title=getIntent().getExtras().getString("title");
                        String icon=getIntent().getExtras().getString("icon");

                        intent.putExtra("icon",icon);
                        intent.putExtra("action_type",action_type);
                        intent.putExtra("receiverid",receiverid);
                        intent.putExtra("title",title);


                        startActivity(intent);
                        finish();
                    }
                    else

                        GPSStatus();

                } else {
                    // else we will move the user to login screen
                    Intent intent= new Intent(Splash_A.this, Login_A.class);
                    overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                    startActivity(intent);
                    finish();

                }
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                pDialog.hide();
                if (sharedPreferences.getBoolean(Variables.islogin, false)) {
                    // if user is already login then we get the current location of user
                    if(getIntent().hasExtra("action_type")){
                        Intent intent= new Intent(Splash_A.this, MainMenuActivity.class);
                        String action_type=getIntent().getExtras().getString("action_type");
                        String receiverid=getIntent().getExtras().getString("senderid");
                        String title=getIntent().getExtras().getString("title");
                        String icon=getIntent().getExtras().getString("icon");

                        intent.putExtra("icon",icon);
                        intent.putExtra("action_type",action_type);
                        intent.putExtra("receiverid",receiverid);
                        intent.putExtra("title",title);


                        startActivity(intent);
                        finish();
                    }
                    else
                        GPSStatus();
                } else {
                    // else we will move the user to login screen
                    Intent intent= new Intent(Splash_A.this, Login_A.class);
                    overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                    startActivity(intent);
                    finish();

                }
                // Code to be executed when the interstitial ad is closed.
            }
        });


    }







}

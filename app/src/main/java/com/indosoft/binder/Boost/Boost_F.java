package com.indosoft.binder.Boost;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.indosoft.binder.CodeClasses.ApiRequest;
import com.indosoft.binder.CodeClasses.Callback;
import com.indosoft.binder.CodeClasses.Functions;
import com.indosoft.binder.CodeClasses.Variables;
import com.indosoft.binder.Main_Menu.MainMenuActivity;
import com.indosoft.binder.Main_Menu.RelateToFragment_OnBack.RootFragment;
import com.indosoft.binder.R;
import com.indosoft.binder.Splash_A;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import org.json.JSONException;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class Boost_F extends RootFragment implements View.OnClickListener {

    View view;
    Context context;
    CircularProgressBar circularProgressBar;

    final String TAG = Splash_A.class.getSimpleName();
    InterstitialAd interstitialAd;
    SweetAlertDialog pDialog;




    public Boost_F() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        context=getContext();

       if(!Check_IS_Boost_On()) {
            view = inflater.inflate(R.layout.fragment_boost, container, false);

            view.findViewById(R.id.boost_btn).setOnClickListener(this);

       }

       else {

           view = inflater.inflate(R.layout.fragment_boost_on, container, false);
           circularProgressBar = view.findViewById(R.id.circularProgressBar);
           view.findViewById(R.id.okay_btn).setOnClickListener(this);
           Set_Progress();
       }


        view.findViewById(R.id.transparent_layout).setOnClickListener(this);

        return view;
    }



    long time_gone;
    public boolean Check_IS_Boost_On(){

        long requesttime= Long.parseLong(MainMenuActivity.sharedPreferences.getString(Variables.Boost_On_Time,"0"));
        long currenttime=System.currentTimeMillis();

        time_gone=(currenttime-requesttime);

        if(requesttime==0){

            return false;
        }
        else if(time_gone<Variables.Boost_Time){

          return true;

        }
        else {

            return false;

        }


    }



    public void Set_Progress(){
        long requesttime= Long.parseLong(MainMenuActivity.sharedPreferences.getString(Variables.Boost_On_Time,"0"));
        long currenttime=System.currentTimeMillis();

        time_gone=(currenttime-requesttime);


        Start_Timer();
    }


    CountDownTimer timer;
    public void Start_Timer(){
        long time_left=Variables.Boost_Time-time_gone;
        timer=new CountDownTimer(time_left,1000) {
            @Override
            public void onTick(long l) {
                long millis = l;

                String time_string= Functions.convertSeconds((int) (millis/1000));
                TextView textView=view.findViewById(R.id.remaining_txt);
                textView.setText(time_string+" Remaining");

                float progress=  ((l*100)/Variables.Boost_Time);
                circularProgressBar.setProgress(progress);
            }

            @Override
            public void onFinish() {

                Stop_timer();
            }
        };

        timer.start();
    }

    public void Stop_timer(){

        if(timer!=null)
            timer.cancel();

    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.boost_btn:


                showinters();


                break;

            case R.id.transparent_layout:
                getActivity().onBackPressed();
                break;

            case R.id.okay_btn:
                getActivity().onBackPressed();
                break;

        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Stop_timer();
    }

    private void Call_Api_For_BoostProfile() {
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("fb_id", MainMenuActivity.user_id);
            parameters.put("mins", "30");
            parameters.put("promoted", "1");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Functions.Show_loader(context,false,false);
        ApiRequest.Call_Api(context, Variables.boostProfile, parameters, new Callback() {
            @Override
            public void Responce(String resp) {

                Functions.cancel_loader();

                try {
                    JSONObject jsonObject=new JSONObject(resp);

                    long min = System.currentTimeMillis();

                    MainMenuActivity.sharedPreferences.edit().putString(Variables.Boost_On_Time,""+min).commit();

                    getActivity().onBackPressed();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });


    }

    public  void showinters(){

        pDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);
        pDialog.show();


        interstitialAd = new InterstitialAd(context, Splash_A.inter);
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
                Call_Api_For_BoostProfile();
                // Interstitial dismissed callback
                Log.e(TAG, "Interstitial ad dismissed.");
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                pDialog.hide();

                Call_Api_For_BoostProfile();

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



}

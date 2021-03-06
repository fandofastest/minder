package com.indosoft.binder.Matchs;


import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.indosoft.binder.Chat.Chat_Activity;
import com.indosoft.binder.CodeClasses.ApiRequest;
import com.indosoft.binder.CodeClasses.Variables;
import com.indosoft.binder.Inbox.Match_Get_Set;
import com.indosoft.binder.Main_Menu.MainMenuActivity;
import com.indosoft.binder.Main_Menu.RelateToFragment_OnBack.RootFragment;
import com.indosoft.binder.R;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.indosoft.binder.Splash_A;
import com.labo.kaji.fragmentanimations.MoveAnimation;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.indosoft.binder.Splash_A.admobappid;
import static com.indosoft.binder.Splash_A.interadmob;

/**
 * A simple {@link Fragment} subclass.
 */


// this is the view which is show when both users like each other

public class Match_F extends RootFragment {
    InterstitialAd interstitialAd;
    final String TAG ="TAG";
    SweetAlertDialog pDialog;

    View view;
    Context context;

    TextView match_txt;
    ImageView user1_pic,user2_pic;

    LinearLayout send_message_layout;
    Match_Get_Set item;
    com.google.android.gms.ads.InterstitialAd mInterstitialAd;

    DatabaseReference rootref;

    public Match_F() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view= inflater.inflate(R.layout.fragment_match, container, false);
        context=getContext();

        ImageButton cross_btn=view.findViewById(R.id.cross_btn);
        cross_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });


        rootref=FirebaseDatabase.getInstance().getReference();

        match_txt=view.findViewById(R.id.match_txt);
        user1_pic=view.findViewById(R.id.user1_pic);
        user2_pic=view.findViewById(R.id.user2_pic);


        send_message_layout=view.findViewById(R.id.send_message_layout);


        Bundle bundle=getArguments();
        if(bundle!=null){

            // get  user data from privous view and show i that view
            item= (Match_Get_Set) bundle.getSerializable("data");

            SendPushNotification(item.getU_id());

            match_txt.setText(" You and "+item.getUsername()+" Like each other");

            if(MainMenuActivity.user_pic!=null && !MainMenuActivity.user_pic.equals("")){
                Uri uri;
                if(MainMenuActivity.user_pic.contains("http"))
                    uri = Uri.parse(MainMenuActivity.user_pic);
                else
                    uri = Uri.parse(Variables.image_base_url+MainMenuActivity.user_pic);

                user1_pic.setImageURI(uri);
            }

            if(item.getPicture()!=null && !item.getPicture().equals("")){

                Uri uri;
                if(item.getPicture().contains("http"))
                    uri = Uri.parse(item.getPicture());
                else
                    uri = Uri.parse(Variables.image_base_url+item.getPicture());


                user2_pic.setImageURI(uri);
            }



        }

        // click listener of message btn
        send_message_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showinters();


            }
        });


        return view;
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (enter) {
            Animation anim= MoveAnimation.create(MoveAnimation.UP, enter, 300);
            return anim;

        } else {
            return MoveAnimation.create(MoveAnimation.DOWN, enter, 300);
        }
    }


    //open the chat fragment and on item click and pass your id and the other person id in which
    //you want to chat with them
    public void chatFragment(String senderid,String receiverid,String name,String picture){
        getActivity().onBackPressed();
        Chat_Activity chat_activity = new Chat_Activity();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.in_from_bottom, R.anim.out_to_top, R.anim.in_from_top, R.anim.out_from_bottom);
        Bundle args = new Bundle();
        args.putString("Sender_Id",senderid);
        args.putString("Receiver_Id",receiverid);
        args.putString("picture",picture);
        args.putString("name",name);
        args.putBoolean("is_match_exits",true);
        chat_activity.setArguments(args);
        transaction.addToBackStack(null);
        transaction.replace(R.id.MainMenuFragment, chat_activity).commit();
    }


    // when this screen open it will send the notification to other user that
    // both are like the each other and match will build between the users
    public void SendPushNotification(final String receverid){
        rootref.child("Users").child(receverid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                String token=dataSnapshot.child("token").getValue().toString();
                JSONObject notimap=new JSONObject();
                    try {
                        notimap.put("title",MainMenuActivity.user_name);
                        notimap.put("message","Congrats! you got a match");

                        String image=MainMenuActivity.user_pic;
                        if(!image.contains("http")){
                            image=Variables.image_base_url+MainMenuActivity.user_pic;
                        }
                        notimap.put("icon", image);
                        notimap.put("tokon",token);
                        notimap.put("senderid",MainMenuActivity.user_id);
                        notimap.put("receiverid", receverid);
                        notimap.put("action_type", "match");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ApiRequest.Call_Api(context, Variables.sendPushNotification, notimap,null);


            }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }



    public  void showinters(){

        pDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);
        pDialog.show();

        interstitialAd = new InterstitialAd(Objects.requireNonNull(getContext()), Splash_A.interfb);
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
                chatFragment(MainMenuActivity.user_id,item.getU_id(),item.getUsername(),item.getPicture());

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
    public  void interadmobload(){
        MobileAds.initialize(context,
                admobappid);
        mInterstitialAd = new com.google.android.gms.ads.InterstitialAd(context);
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
                chatFragment(MainMenuActivity.user_id,item.getU_id(),item.getUsername(),item.getPicture());
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
                chatFragment(MainMenuActivity.user_id,item.getU_id(),item.getUsername(),item.getPicture());
                // Code to be executed when the interstitial ad is closed.
            }
        });


    }


}

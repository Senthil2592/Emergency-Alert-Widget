package rsklabs.com.dangeralerts;

import android.*;
import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.media.Image;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.nhaarman.supertooltips.ToolTip;
import com.nhaarman.supertooltips.ToolTipRelativeLayout;
import com.nhaarman.supertooltips.ToolTipView;

import static rsklabs.com.dangeralerts.R.id.adView1;


public class MainActivity extends AppCompatActivity implements ToolTipView.OnToolTipViewClickedListener{

    //SmsManager.getDefault().sendTextMessage(phoneNumber, null, "Hello SMS!", null, null);
    private Button updateButton;
    private InterstitialAd mInterstitialAd;
    public static final String MyPREFERENCES = "Emergency_Alert_Pref";
    private final static int TAG_CODE_PERMISSION_LOCATION = 101;
    int count =0;
    SharedPreferences sharedpreferences ;
    public static final String Address = "addressKey";
    public static final String Phone = "phoneKey";
    public static final String Phone2 = "phoneKey2";
    public static final String Phone3 = "phoneKey3";
    private ToolTipView myToolTipView;
    private ToolTipView myToolTipViewPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        ToolTipRelativeLayout toolTipRelativeLayout = (ToolTipRelativeLayout) findViewById(R.id.activity_main_tooltipRelativeLayout);

        ToolTip toolTip = new ToolTip()
                .withText("How to use .?")
                .withColor(Color.YELLOW)
                .withShadow();
        myToolTipView = toolTipRelativeLayout.showToolTipForView(toolTip, findViewById(R.id.imageView1));
        myToolTipView.setOnToolTipViewClickedListener(MainActivity.this);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        if(sharedpreferences.getString(Phone, null) != ""){
            String phoneNo = sharedpreferences.getString(Phone, null);
            String phoneNo2 = sharedpreferences.getString(Phone2, null);
            String phoneNo3 = sharedpreferences.getString(Phone3, null);
            String address = sharedpreferences.getString(Address, null);
            EditText numberField = (EditText) findViewById(R.id.emergContNoValue);
            numberField.setText(phoneNo);
            EditText numberField2 = (EditText) findViewById(R.id.emergContNoValue2);
            numberField2.setText(phoneNo2);
            EditText numberField3 = (EditText) findViewById(R.id.emergContNoValue3);
            numberField3.setText(phoneNo3);
            EditText addressField = (EditText) findViewById(R.id.additionalMsgx);
            addressField.setText(address);
        }

        updateButton = ((Button) findViewById(R.id.next_level_button));
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                EditText number = (EditText) findViewById(R.id.emergContNoValue);
                EditText number2 = (EditText) findViewById(R.id.emergContNoValue2);
                EditText number3 = (EditText) findViewById(R.id.emergContNoValue3);
                String numberString = number.getText().toString();

                if(checkNumberLength(number.getText().toString()) &&
                checkNumberLength(number2.getText().toString())&&
                checkNumberLength(number3.getText().toString())) {

                    if (!numberString.isEmpty() && numberString != null && number.length() == 10) {
                        EditText address = (EditText) findViewById(R.id.additionalMsgx);
                        editor.putString(Address, address.getText().toString());
                        editor.putString(Phone, number.getText().toString());
                        editor.putString(Phone2, number2.getText().toString());
                        editor.putString(Phone3, number3.getText().toString());
                        editor.commit();
                        loadInterstitial();
                        showInterstitial();
                        Toast.makeText(MainActivity.this, "Updated!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Please enter contact number 1", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        AdView adView = (AdView)findViewById(adView1);
        AdRequest adRequest =new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        // Create the InterstitialAd and set the adUnitId (defined in values/strings.xml).
        mInterstitialAd = newInterstitialAd();
        loadInterstitial();

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.SEND_SMS},
                    TAG_CODE_PERMISSION_LOCATION);

        }

        ImageView image1 = (ImageView) findViewById(R.id.imageView1);
        ImageView image2 = (ImageView) findViewById(R.id.imageView2);
        image1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.disclaimer_layout);
                dialog.setTitle("How to use");
                Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        myToolTipView.remove();

                        ToolTipRelativeLayout toolTipRelativeLayout = (ToolTipRelativeLayout) findViewById(R.id.activity_main_tooltipPreview);

                        ToolTip toolTip = new ToolTip()
                                .withText("Preview")
                                .withColor(Color.YELLOW)
                                .withShadow();
                        myToolTipViewPreview = toolTipRelativeLayout.showToolTipForView(toolTip, findViewById(R.id.imageView2));
                        myToolTipViewPreview.setOnToolTipViewClickedListener(MainActivity.this);
                    }
                });

                dialog.show();
            }
        });

        image2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.preview_layout);
                dialog.setTitle("Preview");
                EditText value = (EditText) findViewById(R.id.additionalMsgx);
                TextView textView = (TextView) dialog.findViewById(R.id.text8);
                String msg ="http://maps.google.com/maps?q=loc:" + 0.0 + "," + 0.0;
                textView.setText(value.getText().toString()+System.getProperty("line.separator")+ msg);
                Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK2);
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        if(null != myToolTipViewPreview) {
                            myToolTipViewPreview.remove();
                        }
                    }
                });

                dialog.show();
            }
        });

        }

        public boolean checkNumberLength(String number){
            if(number.length()<10 && !number.isEmpty()){
                Toast.makeText(MainActivity.this, "Contact number must be of 10 digits", Toast.LENGTH_SHORT).show();
                return false;
            }
            return true;
        }
    @Override
    public void onToolTipViewClicked(final ToolTipView toolTipView) {
        if (myToolTipView == toolTipView) {
            myToolTipView = null;
        }
    }

    private InterstitialAd newInterstitialAd() {
        InterstitialAd interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getString(R.string.interstitialAdId));
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {

            }

            @Override
            public void onAdFailedToLoad(int errorCode) {

            }

            @Override
            public void onAdClosed() {
                // Proceed to the next level.
                goToNextLevel();
            }
        });
        return interstitialAd;
    }

    private void showInterstitial() {
        // Show the ad if it's ready. Otherwise toast and reload the ad.
        if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            goToNextLevel();
        }
    }

    private void loadInterstitial() {
        // Disable the next level button and load the ad.

        AdRequest adRequest = new AdRequest.Builder().build();
        mInterstitialAd.loadAd(adRequest);
    }


    private void goToNextLevel() {
        // Show the next level and reload the ad to prepare for the level after.
        mInterstitialAd = newInterstitialAd();
        loadInterstitial();
    }

}

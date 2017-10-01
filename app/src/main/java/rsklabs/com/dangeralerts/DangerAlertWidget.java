package rsklabs.com.dangeralerts;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.telephony.SmsManager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import static android.R.attr.button;
import static rsklabs.com.dangeralerts.MainActivity.Address;
import static rsklabs.com.dangeralerts.MainActivity.MyPREFERENCES;
import static rsklabs.com.dangeralerts.MainActivity.Phone;
import static rsklabs.com.dangeralerts.MainActivity.Phone2;
import static rsklabs.com.dangeralerts.MainActivity.Phone3;

/**
 * Implementation of App Widget functionality.
 */
public class DangerAlertWidget extends AppWidgetProvider {

    private static final String MyOnClick = "myOnClickTag";
    double latitude = 0.0;
    double longitude = 0.0;
    private RemoteViews views;
    SharedPreferences sharedpreferences;


    void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                         int appWidgetId) {

        views = new RemoteViews(context.getPackageName(), R.layout.danger_alert_widget);
        views.setOnClickPendingIntent(R.id.push_button, getPendingSelfIntent(context, MyOnClick));
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);//add this line

        if (MyOnClick.equals(intent.getAction())) {
            //  if (sharedpreferences.getString(Phone, null) != "") {
            GPSTracker gps = new GPSTracker(context);
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
            sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            if (latitude != 0) {
                String messageToSend = "http://maps.google.com/maps?q=loc:" + latitude + "," + longitude;
                String phoneNo = "";
                String phoneNo2 = "";
                String phoneNo3 = "";
                String address = "";


                if (sharedpreferences.getString(Phone, null) != "") {
                    phoneNo = sharedpreferences.getString(Phone, null);
                    phoneNo2 = sharedpreferences.getString(Phone2, null);
                    phoneNo3 = sharedpreferences.getString(Phone3, null);
                    address = sharedpreferences.getString(Address, null);
                    String msg = address + System.getProperty("line.separator") + messageToSend;
                    SmsManager.getDefault().sendTextMessage(phoneNo, null, msg, null, null);

                    if(phoneNo2.length()== 10){
                        SmsManager.getDefault().sendTextMessage(phoneNo2, null, msg, null, null);
                    }
                    if(phoneNo3.length()== 10){
                        SmsManager.getDefault().sendTextMessage(phoneNo3, null, msg, null, null);
                    }

                    views = new RemoteViews(context.getPackageName(), R.layout.danger_alert_widget);
                    views.setInt(R.id.push_button, "setBackgroundResource", R.drawable.button_bg_round_success);
                    views.setTextViewText(R.id.push_button, "Message Sent!");
                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                    views.setTextColor(R.id.push_button, Color.BLACK);
                    appWidgetManager.updateAppWidget(new ComponentName(context, DangerAlertWidget.class), views);
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                views = new RemoteViews(context.getPackageName(), R.layout.danger_alert_widget);
                views.setInt(R.id.push_button, "setBackgroundResource", R.drawable.button_bg_round_retry);
                views.setTextViewText(R.id.push_button, "Please Check GPS connectivity and retry");
                views.setTextColor(R.id.push_button, Color.WHITE);
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                appWidgetManager.updateAppWidget(new ComponentName(context, DangerAlertWidget.class), views);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
           /* } else {
                Intent configIntent = new Intent(context, MainActivity.class);
                PendingIntent configPendingIntent = PendingIntent.getActivity(context, 0, configIntent, 0);
                try {
                    configPendingIntent.send();
                } catch (PendingIntent.CanceledException e) {
                    e.printStackTrace();
                }
            }*/

        }
        views = new RemoteViews(context.getPackageName(), R.layout.danger_alert_widget);
        views.setInt(R.id.push_button, "setBackgroundResource", R.drawable.button_bg_round);
        views.setTextViewText(R.id.push_button, "Send Location by SMS");
        views.setTextColor(R.id.push_button, Color.WHITE);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        appWidgetManager.updateAppWidget(new ComponentName(context, DangerAlertWidget.class), views);

    };

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}


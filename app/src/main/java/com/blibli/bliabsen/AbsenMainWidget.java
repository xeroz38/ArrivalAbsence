package com.blibli.bliabsen;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


/**
 * Implementation of App Widget functionality.
 */
public class AbsenMainWidget extends AppWidgetProvider {

    public static String UPDATE_ACTION = "UpdateAction";
    public static final String MY_PREF = "MyPreference";
    public static final String TIME_PREF_VALUE = "TimePreference";
    public static final String OUT_TIME_PREF_VALUE = "OutTimePreference";
    public static final String DATE_PREF_VALUE = "DatePreference";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        ComponentName watchWidget = new ComponentName(context, AbsenMainWidget.class);
        updateAppWidget(context, appWidgetManager, watchWidget);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (intent.getAction().equals(UPDATE_ACTION)) {
            // Format time
            Calendar c = Calendar.getInstance();
            String currentTime = String.valueOf(c.get(Calendar.HOUR_OF_DAY) + "\n" + c.get(Calendar.MINUTE));
            String currentDate = String.valueOf(c.get(Calendar.DAY_OF_MONTH) + " " + c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US) + " " + c.get(Calendar.YEAR));
            c.setTimeInMillis(System.currentTimeMillis()+32400000);
            String outTime = String.valueOf("Exit at " + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE));
            // Save to sharedpref
            context.getSharedPreferences(MY_PREF, Context.MODE_PRIVATE).edit()
                    .putString(TIME_PREF_VALUE, currentTime)
                    .putString(DATE_PREF_VALUE, currentDate)
                    .putString(OUT_TIME_PREF_VALUE, outTime)
                    .apply();
            // Update when receive click listener
            ComponentName watchWidget = new ComponentName(context, AbsenMainWidget.class);
            updateAppWidget(context, AppWidgetManager.getInstance(context), watchWidget);
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

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, ComponentName appWidgetId) {

        Intent intent = new Intent(context, AbsenMainWidget.class);
        intent.setAction(UPDATE_ACTION);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        // Get sharedprefence
        String currentTime = context.getSharedPreferences(MY_PREF, Context.MODE_PRIVATE).getString(TIME_PREF_VALUE, null);
        String currentDate = context.getSharedPreferences(MY_PREF, Context.MODE_PRIVATE).getString(DATE_PREF_VALUE, null);
        String outTime = context.getSharedPreferences(MY_PREF, Context.MODE_PRIVATE).getString(OUT_TIME_PREF_VALUE, null);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.absen_main_widget);
        if (currentTime != null) {
            views.setTextViewText(R.id.incoming_time, String.valueOf(currentTime));
            views.setTextViewText(R.id.date_information, String.valueOf(currentDate));
            views.setTextViewText(R.id.outgoing_time, String.valueOf(outTime));
        } else {
            views.setTextViewText(R.id.incoming_time, "NEW");
        }
        views.setOnClickPendingIntent(R.id.incoming_time, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}


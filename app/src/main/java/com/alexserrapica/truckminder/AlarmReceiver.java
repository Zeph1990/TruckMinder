package com.alexserrapica.truckminder;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by ALESSANDROSERRAPICA on 11/01/2018.
 */

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub

        ArrayList<String> checklist = check(context);
        if (!checklist.isEmpty()) {
            long when = System.currentTimeMillis();
            NotificationManager notificationManager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);

            Intent notificationIntent = new Intent(context, MainActivity.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                  notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);


            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(
                    context)
                    .setSmallIcon(R.drawable.ic_stat_maps_directions_bus)
                    .setContentTitle(checklist.size() + " veicoli necessitano manutenzione")
                    .setContentText("Espandi per maggiori informazioni.").setSound(alarmSound)
                    .setAutoCancel(true).setWhen(when)
                    .setContentIntent(pendingIntent)
                    .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
            NotificationCompat.InboxStyle inboxStyle =new NotificationCompat.InboxStyle();
            if(checklist.size() ==1){
                inboxStyle.setBigContentTitle("Il veicolo "+checklist.get(0)+" necessita manutenzione!");
            }
            else{
            inboxStyle.setBigContentTitle("Veicoli che necessitano manutenzione:");
            for (int i=0; i < checklist.size(); i++) {
                inboxStyle.addLine((i+1) + ") "+checklist.get(i));
            }
            }

            mNotifyBuilder.setStyle(inboxStyle);
            notificationManager.notify(0, mNotifyBuilder.build());


        }
    }
    public static ArrayList<String> check(Context c){
        ArrayList<String> checklist= new ArrayList<>();
        DbHandler db  = new DbHandler(c);
        ArrayList<Registro> registro = (ArrayList<Registro>) db.getAllEvents();
        for(Registro r : registro) {

            String strThatDay = r.getData();
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy");
            Date d = null;
            try {
                d = formatter.parse(strThatDay);//catch exception
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


            Calendar thatDay = Calendar.getInstance();
            thatDay.setTime(d);
            Calendar today = Calendar.getInstance();

            long diff = today.getTimeInMillis() - thatDay.getTimeInMillis();
            long days = diff / (24 * 60 * 60 * 1000);
            SharedPreferences prefs = c.getSharedPreferences("truckpr", Context.MODE_PRIVATE);
            int numdays = prefs.getInt("days", 15);
            if(days >=numdays) {
               checklist.add(r.getTarga());
            }
        }

        return checklist;
    }
}

package com.alexserrapica.truckminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    int HOUR;
    int MINUTES;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTarga(view);
            }
        });
        SharedPreferences prefs = getSharedPreferences("truckpr", Context.MODE_PRIVATE);
        String textData = prefs.getString("switch", "false");
        if(textData.equals("true")){
            Switch sw = (Switch) findViewById(R.id.switch1);
            sw.setChecked(true);
            TextView text = (TextView) findViewById(R.id.text);
            text.setText(prefs.getString("str",""));
            text.setVisibility(View.VISIBLE);
        }


        updatelist();
    }
    @Override
    public void onBackPressed()
    {
           finish();
    }

    public void activateAlarm(@Nullable  View view){
        if(((Switch)view).isChecked()) {
            SharedPreferences prefs = getSharedPreferences("truckpr", Context.MODE_PRIVATE);
           final  SharedPreferences.Editor editor = prefs.edit();
            editor.putString("switch","true");
            editor.commit();
            Calendar mcurrentTime = Calendar.getInstance();
          final  LayoutInflater inflater = this.getLayoutInflater();
            HOUR = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            MINUTES = mcurrentTime.get(Calendar.MINUTE);
            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {

                @Override
                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                    HOUR = selectedHour;
                    MINUTES = selectedMinute;
                    final AlertDialog.Builder d = new AlertDialog.Builder(MainActivity.this);

                    View dialogView = inflater.inflate(R.layout.number_picker_dialog, null);
                    d.setTitle("Giorni");
                    d.setMessage("Seleziona il numero di giorni minimo per la notifica");
                    d.setView(dialogView);
                    final NumberPicker numberPicker = (NumberPicker) dialogView.findViewById(R.id.dialog_number_picker);
                    numberPicker.setMaxValue(50);
                    numberPicker.setMinValue(1);
                    numberPicker.setWrapSelectorWheel(false);
                    numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                        @Override
                        public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                        }
                    });
                    d.setPositiveButton("Fatto", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            editor.putInt("days",numberPicker.getValue());
                            editor.commit();
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(Calendar.HOUR_OF_DAY, HOUR);
                            calendar.set(Calendar.MINUTE, MINUTES);
                            calendar.set(Calendar.SECOND, 0);
                            Intent intent1 = new Intent(getApplicationContext(), AlarmReceiver.class);
                            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 1101, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
                            AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                            am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
                            TextView text = (TextView) findViewById(R.id.text);
                            String s;
                            if(MINUTES<10)
                                s = "Notifica tutti i giorni alle "+HOUR+":0"+MINUTES+" per manutenzioni di almeno "+numberPicker.getValue()+ " giorni fa.";
                            else
                                s = "Notifica tutti i giorni alle "+HOUR+":"+MINUTES+" per manutenzioni di almeno "+numberPicker.getValue()+ " giorni fa.";

                            text.setText(s);
                            text.setVisibility(View.VISIBLE);
                            editor.putString("str",s);
                            editor.commit();
                            updatelist();
                        }
                    });
                    d.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
                    AlertDialog alertDialog = d.create();
                    alertDialog.show();
                }
            }, HOUR, MINUTES, true);
            mTimePicker.setTitle("Scegli l'orario per le notifiche");
            mTimePicker.show();

        }
        else {
            SharedPreferences prefs = getSharedPreferences("truckpr", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("switch","false");
            editor.commit();
            TextView text = (TextView) findViewById(R.id.text);
            text.setVisibility(View.GONE);
            Intent intent1 = new Intent(getApplicationContext(), AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 1101, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
            am.cancel(pendingIntent);
            updatelist();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void updatelist() {
        ListView lv = (ListView) findViewById(R.id.listView1);
        lv.removeAllViewsInLayout();
        final DbHandler db = new DbHandler(this);
        final ArrayList<Registro> r = (ArrayList<Registro>) db.getAllEvents();
        ArrayList<HashMap<String, String>> data = new ArrayList<>();
        final ArrayList<String> checklist = AlarmReceiver.check(MainActivity.this);
        for (int i = 0; i < r.size(); i++) {
            Registro p = r.get(i);
            HashMap<String, String> regMap = new HashMap<>();
            regMap.put("data", p.getData());
            regMap.put("targa", p.getTarga());

            data.add(regMap);
        }
        String[] from = {"data", "targa"};
        int[] to = {R.id.datatext, R.id.targatext};
        SimpleAdapter adapter = new SimpleAdapter(
                this,
                data,
                R.layout.listelem,
                from,
                to){

            @Override
            public View getView (final int position, View convertView, ViewGroup parent)
            {
                View v = super.getView(position, convertView, parent);
                Button b2=(Button)v.findViewById(R.id.button3);
                Button b=(Button)v.findViewById(R.id.button2);
                int dpswidth = 165;
                final float scale = getResources().getDisplayMetrics().density;
                int width = (int) (dpswidth * scale + 0.5f);
                if(checklist.contains(r.get(position).getTarga().toString()) && ((Switch)findViewById(R.id.switch1)).isChecked() ){
                    v.setBackgroundColor(getResources().getColor(R.color.elapsed));
                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) b.getLayoutParams();
                    lp.width = width;
                    b.setLayoutParams(lp);
                    b2.setVisibility(View.VISIBLE);
                }

                b2.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View arg0) {
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("Richiesta Conferma")
                                .setMessage("Confermi di aver eseguito la manutenzione sulla targa "+ r.get(position).getTarga().toString()+"?")
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        String myFormat = "dd/MM/yy"; //In which you need put here
                                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ITALIAN);
                                        db.updateOrAdd(sdf.format(Calendar.getInstance().getTime()).toString(),r.get(position).getTarga().toString());
                                        updatelist();
                                    }})
                                .setNegativeButton(android.R.string.no, null).show();
                    }
                });
                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        // TODO Auto-generated method stub
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("Richiesta Conferma")
                                .setMessage("Vuoi davvero eliminare dalla lista la targa "+ r.get(position).getTarga().toString()+"?")
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        db.delete(r.get(position).getTarga().toString());
                                        updatelist();
                                    }})
                                .setNegativeButton(android.R.string.no, null).show();

                    }
                });
                return v;
            }
        };


        ((ListView) findViewById(R.id.listView1)).setAdapter(adapter);
        db.close();
    }

    public void addTarga(@Nullable View view){
        Intent intent = new Intent(this, NuovaOperazione.class);
        startActivity(intent);


    }



}

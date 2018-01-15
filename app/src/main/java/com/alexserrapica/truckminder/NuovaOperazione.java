package com.alexserrapica.truckminder;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class NuovaOperazione extends AppCompatActivity {

    EditText edittext;
    Calendar myCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuova_operazione);


        myCalendar = Calendar.getInstance();
        edittext = (EditText) findViewById(R.id.dateinput);
      final  DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

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

        edittext.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    // TODO Auto-generated method stub
                                                    new DatePickerDialog(NuovaOperazione.this, date, myCalendar
                                                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                                                }
                                            });
    }

    private void updateLabel() {
        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ITALIAN);

        edittext.setText(sdf.format(myCalendar.getTime()));
    }

    public void saveOp(@Nullable View view){


        EditText targatext = (EditText) findViewById(R.id.targainput);
        DbHandler db = new DbHandler(this);
        if(targatext.getText().toString().isEmpty() || edittext.getText().toString().isEmpty()){
            Snackbar snackbar = Snackbar
                    .make(view, "Riempi prima tutti i campi", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
        else{
        db.updateOrAdd(edittext.getText().toString(),targatext.getText().toString());
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }

    }


}

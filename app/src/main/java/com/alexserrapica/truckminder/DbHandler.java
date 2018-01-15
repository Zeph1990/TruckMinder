package com.alexserrapica.truckminder;

        import android.content.ContentValues;
        import android.content.Context;
        import android.database.Cursor;
        import android.database.sqlite.SQLiteDatabase;
        import android.database.sqlite.SQLiteOpenHelper;

        import java.util.ArrayList;
        import java.util.List;

/**
 * Created by alexs on 30/06/2016.
 */
public class DbHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "tmRegistro";
    private static final String TABLE_REG = "registro";
    private static final String KEY_DATE = "data";
    private static final String KEY_TIME = "targa";

    public DbHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Crea la tabella nel Database
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE =
                "CREATE TABLE " + TABLE_REG + "(" + KEY_DATE + " TEXT," + KEY_TIME + " TEXT)";
        System.out.println(CREATE_TABLE);
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REG);
        onCreate(db);
    }

    //Aggiunge una nuova riga alla tabella.
    public void addEvent(Registro r) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_DATE, String.valueOf(r.getData()));
        values.put(KEY_TIME, String.valueOf(r.getTarga()));

        db.insert(TABLE_REG, null, values);
    }

    //Restituisce tutti gli eventi del registro
    public List<Registro> getAllEvents() {
        List<Registro> registro = new ArrayList<Registro>();
        String selectQuery = "SELECT * FROM " + TABLE_REG + " ORDER BY "+ KEY_DATE +" DESC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToLast()) {
            do {
                Registro r = new Registro();
                r.setData(cursor.getString(0));
                r.setTarga(cursor.getString(1));
                registro.add(r);
            } while (cursor.moveToPrevious());
        }
        return registro;
    }

    public void updateOrAdd(String data, String targa) {
        String selectQuery = "SELECT * FROM " + TABLE_REG + " WHERE " + KEY_TIME + " = '"+targa+"'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        Registro r = new Registro();
        if (cursor.moveToLast()) {
                r.setData(data);
                r.setTarga(cursor.getString(1));
                String updateQuery = "UPDATE " + TABLE_REG + " SET " + KEY_DATE + " = '"+data+"' WHERE " + KEY_TIME + " = '"+targa+"'";
                db.execSQL(updateQuery);
        }
        else {
            r.setData(data);
            r.setTarga(targa);
            addEvent(r);
        }

    }

    public void delete(String targa) {
        String selectQuery = "DELETE FROM " + TABLE_REG + " WHERE " + KEY_TIME + " = '"+targa+"'";
        SQLiteDatabase db = this.getWritableDatabase();
       db.execSQL(selectQuery);
    }

}



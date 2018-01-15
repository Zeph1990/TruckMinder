package com.alexserrapica.truckminder;


/**
 * Created by alexs on 30/06/2016.
 */
//Questa classe è una classe di supporto per gli oggetti del registro degli eventi
public class Registro {
    private String data;
    private String targa;

    public Registro() {

    }

    public Registro(String d, String t) {
        this.setData(d);
        this.setTarga(t);
    }


    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getTarga() {
        return targa;
    }

    public void setTarga(String targa) {
        this.targa = targa;
    }
}

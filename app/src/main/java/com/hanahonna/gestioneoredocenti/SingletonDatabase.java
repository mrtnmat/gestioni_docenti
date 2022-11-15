package com.hanahonna.gestioneoredocenti;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

public class SingletonDatabase {

    private static MyDbSetting myDb = null;
    private  static String variabile;

    public static MyDbSetting getDb(Context c){
        if(myDb == null){
            myDb = new MyDbSetting(c);
        }
        return myDb;
    }

    public static String getVariabile(){
        return variabile;
    }
    public static void setVariabile(String v){
       variabile = v;
    }

}

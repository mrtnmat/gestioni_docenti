package com.hanahonna.gestioneoredocenti;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.File;
import java.nio.channels.FileLockInterruptionException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MyDbSetting extends SQLiteOpenHelper {

    Context context;

    // variabili database impostazioni
    private static final String DATABASE_NAME = "impostazioni.db";
    private static final int DATABASE_VERSION = 6;
    private static final String TABELLA_COLORI = "Tb_Colori";

    // variabili tabella

    private static final String COLUMN_ID = "Id";
    private static final String COLUMN_NOME = "Nome";
    private static final String COLUMN_VALORE = "Valore";
    private static final String FILE_DIR = "Archivio";

    public MyDbSetting(@Nullable Context context) {
        super(context, Objects.requireNonNull(context).getExternalFilesDir(FILE_DIR)
                + File.separator + "/Database/" + File.separator
                + DATABASE_NAME, null, DATABASE_VERSION);
        Log.i("Prova","Cartella" + FILE_DIR);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String query = "CREATE TABLE " + TABELLA_COLORI +
                " (" + COLUMN_ID + " INTEGER NOT NULL UNIQUE PRIMARY KEY," +
                COLUMN_NOME + " TEXT NOT NULL UNIQUE, " +
                COLUMN_VALORE + " INTEGER NOT NULL" +
                ");";

        db.execSQL(query);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABELLA_COLORI);
        onCreate(db);

    }

    void aggiungiColore(String nome, int valore){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cvSez = new ContentValues();


        cvSez.put(COLUMN_NOME, nome);
        cvSez.put(COLUMN_VALORE, valore);

        long result = db.insertWithOnConflict(TABELLA_COLORI, null,cvSez,SQLiteDatabase.CONFLICT_REPLACE);
        if(result == -1){
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, "Aggiunto con Successo", Toast.LENGTH_SHORT).show();
        }
    }

    public List<ContainerColori> getColori(){

        SQLiteDatabase db = getReadableDatabase();
        List<ContainerColori> listColori =new ArrayList<>();
        Cursor cursor = db.query(TABELLA_COLORI,new String[]{COLUMN_NOME,COLUMN_VALORE},null,null,null,null,null);

        if(cursor.moveToFirst()){
            do {

                ContainerColori c = new ContainerColori(cursor.getString(0),cursor.getInt(1));
                listColori.add(c);
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        db.close();


        return listColori;
    }
}

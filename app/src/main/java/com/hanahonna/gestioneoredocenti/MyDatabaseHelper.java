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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MyDatabaseHelper extends SQLiteOpenHelper {

    private Context context;

    private static final String DATABASE_NAME = "gestionedocenti.db";
    private static final int DATABASE_VERSION = 6;
    private static final String TABELLA_DOCENTI = "Tb_Docenti";
    private static final String TABELLA_CLASSI ="Tb_Classi";
    private static final String TABELLA_PRESENZE ="Tb_Presenze";
    private static final String TABELLA_SEDI ="Tb_Sedi";


    // Id
    private static final String COLUMN_ID = "_ID";

    // Tabella Docenti
    private static final String COLUMN_NOME = "Nome";
    private static final String COLUMN_COGNOME = "Cognome";
    private static final String COLUMN_MATERIA = "Materia";

    // Tabella classi
    private static final String COLUMN_SEDE_ID = "SedeId";

    // Tabella presenze
    private static final String COLUMN_GIORNO = "Giorno";
    private static final String COLUMN_DOCENTE_ID = "DocenteId";
    private static final String COLUMN_CLASSE_ID = "ClasseId";
    private static final String COLUMN_CLASSE = "Classe";
    private static final String COLUMN_SEDE = "SedeId";
    private static final String COLUMN_ORA = "Ora";

    // View presenze
    private static final String VIEW_PRESENZE = "View_Presenze";

    private static final String FILE_DIR = "Archivio";

    public MyDatabaseHelper(@Nullable Context context) {

        super(context, Objects.requireNonNull(context).getExternalFilesDir(FILE_DIR)
                + File.separator + "/Database/" + File.separator
                + DATABASE_NAME, null, DATABASE_VERSION);
        Log.i("File","Destination" + Objects.requireNonNull(context).getFilesDir());
        this.context = context;
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        db.execSQL("PRAGMA foreign_keys = ON;");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String query = "CREATE TABLE " + TABELLA_DOCENTI +
                " (" + COLUMN_ID + " INTEGER NOT NULL UNIQUE PRIMARY KEY," +
                COLUMN_NOME + " TEXT NOT NULL, " +
                COLUMN_COGNOME + " TEXT NOT NULL, " +
                COLUMN_MATERIA + " TEXT NOT NULL, " +
                "UNIQUE (" + COLUMN_NOME + "," + COLUMN_COGNOME + ")" +
                ");";
        db.execSQL(query);

        query = "CREATE TABLE " + TABELLA_CLASSI +
                " (" + COLUMN_ID + " INTEGER NOT NULL UNIQUE PRIMARY KEY," +
                COLUMN_NOME + " TEXT NOT NULL, " +
                COLUMN_SEDE_ID + " INTEGER NOT NULL, " +
                "UNIQUE (" + COLUMN_NOME + "," + COLUMN_SEDE_ID + ")," +
                "FOREIGN KEY(" + COLUMN_SEDE_ID + ") REFERENCES " + TABELLA_SEDI + "(" + COLUMN_ID + ") ON DELETE CASCADE" +
                ");";
        db.execSQL(query);

        query = "CREATE TABLE " + TABELLA_SEDI +
                " (" + COLUMN_ID + " INTEGER NOT NULL UNIQUE PRIMARY KEY," +
                COLUMN_NOME + " TEXT NOT NULL" +
                ");";
        db.execSQL(query);
/*
        // Inserisco la sede Villa
        query = "INSERT INTO " + TABELLA_SEDI + " (Nome) " +
                "VALUES (\"villa\")" +
                 "";
        db.execSQL(query);

        // Inserisco la sede Gramsci
        query = "INSERT INTO " + TABELLA_SEDI + " (Nome) " +
                "VALUES (\"gramsci\")" +
                "";
        db.execSQL(query);
*/
        query = "CREATE TABLE " + TABELLA_PRESENZE +
                " (" + COLUMN_ID + " INTEGER NOT NULL UNIQUE PRIMARY KEY," +
                COLUMN_GIORNO + " TEXT NOT NULL," +
                COLUMN_ORA + " INTEGER NOT NULL," +
                COLUMN_DOCENTE_ID + " INTEGER NOT NULL," +
                COLUMN_CLASSE_ID + " INTEGER," +
                COLUMN_SEDE_ID + " INTEGER NOT NULL," +
                "UNIQUE (" + COLUMN_GIORNO + "," + COLUMN_ORA + "," + COLUMN_DOCENTE_ID + ")," +
                "UNIQUE (" + COLUMN_GIORNO + "," + COLUMN_ORA + "," + COLUMN_CLASSE_ID + ")," +
                "FOREIGN KEY(" + COLUMN_DOCENTE_ID + ") REFERENCES " + TABELLA_DOCENTI + "(" + COLUMN_ID + ") ON DELETE CASCADE," +
                "FOREIGN KEY(" + COLUMN_CLASSE_ID + ") REFERENCES " + TABELLA_CLASSI + "(" + COLUMN_ID + ") ON DELETE CASCADE," +
                "FOREIGN KEY(" + COLUMN_SEDE_ID + ") REFERENCES " + TABELLA_SEDI + "(" + COLUMN_ID + ") ON DELETE CASCADE " +
                ");";
        db.execSQL(query);

        query = "CREATE VIEW View_Presenze AS SELECT P.Giorno, P.Ora, S.Nome AS Sede, C.Nome AS Classe, D.Nome, D.Cognome, D.Materia " +
                "FROM Tb_Presenze AS P " +
                "JOIN Tb_Sedi AS S " +
                "ON P.SedeId = S._ID " +
                "LEFT JOIN Tb_Classi AS C " +
                "ON P.ClasseId = C._ID " +
                "JOIN Tb_Docenti AS D " +
                "ON P.DocenteId = D._ID " +
                "ORDER BY C.Nome";

        db.execSQL(query);

        query = DatiIniziali.sedi;

        db.execSQL(query);

        query = DatiIniziali.docenti;

        db.execSQL(query);

        query = DatiIniziali.classi;

        db.execSQL(query);

        query = DatiIniziali.presenze;

        db.execSQL(query);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABELLA_DOCENTI);
        db.execSQL("DROP TABLE IF EXISTS " + TABELLA_CLASSI);
        db.execSQL("DROP TABLE IF EXISTS " + TABELLA_PRESENZE);
        db.execSQL("DROP VIEW IF EXISTS " + VIEW_PRESENZE);
        onCreate(db);
    }

    void aggiungiDocente(String Nome,String Cognome, String Materia){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_NOME, Nome);
        cv.put(COLUMN_COGNOME, Cognome);
        cv.put(COLUMN_MATERIA, Materia);
        long result = db.insert(TABELLA_DOCENTI, null,cv);
        if(result == -1){
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, "Aggiunto con Successo", Toast.LENGTH_SHORT).show();
        }

    }
    void aggiungiSezione(String nome, String sede){
        SQLiteDatabase db = getWritableDatabase();

        String[] projection = {COLUMN_ID};
        String selection = COLUMN_NOME + " = ?";
        String[] selectionArgs = {sede};

        Cursor cursor = db.query(TABELLA_SEDI,projection,selection,selectionArgs,null,null,null);
        Integer sede_id = null;
        if(cursor.moveToFirst()){
            sede_id = cursor.getInt(0);
        }
        Log.i("Aggiungi Sezione","id: " + sede_id);
        ContentValues cvSez = new ContentValues();

        cvSez.put(COLUMN_SEDE_ID, sede_id);
        cvSez.put(COLUMN_NOME, nome);

        long result = db.insert(TABELLA_CLASSI, null,cvSez);
        if(result == -1){
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, "Aggiunto con Successo", Toast.LENGTH_SHORT).show();
        }
    }

    void aggiungiPresenza(String giorno, int ora, ContainerDocente d, ContainerSezione c, String sede){

        SQLiteDatabase db = getWritableDatabase();

        // Select Docenti
        String[] projection = {COLUMN_ID};
        String selection = COLUMN_NOME + " = ? AND " + COLUMN_COGNOME + " = ?";
        String[] selectionArgs = {d.getNome(),d.getCognome()};

        Cursor cursor = db.query(TABELLA_DOCENTI,projection,selection,selectionArgs,null,null,null);
        Integer docente_id = null;
        if(cursor.moveToFirst()){
            docente_id = cursor.getInt(0);
        }

        // Select Classe
        projection = new String[]{COLUMN_ID};
        selection = COLUMN_NOME + " = ?";
        String selectionEmpty = COLUMN_NOME + " IS NULL";
        selectionArgs = new String[]{c.getNome()};

        cursor = c.getNome() == null
                ? db.query(TABELLA_CLASSI,projection,selection,null,null,null,null)
                : db.query(TABELLA_CLASSI,projection,selection,selectionArgs,null,null,null);
        Integer classe_id = null;
        if(cursor.moveToFirst()){
            classe_id = cursor.getInt(0);
        }

        // Select Sede
        projection = new String[]{COLUMN_ID};
        selection = COLUMN_NOME + " = ?";
        selectionArgs = new String[]{sede};

        cursor = db.query(TABELLA_SEDI,projection,selection,selectionArgs,null,null,null);
        Integer sede_id = null;
        if(cursor.moveToFirst()){
            sede_id = cursor.getInt(0);
        }

        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_ORA, ora);
        contentValues.put(COLUMN_GIORNO, giorno);
        contentValues.put(COLUMN_DOCENTE_ID, docente_id);
        contentValues.put(COLUMN_CLASSE_ID, classe_id);
        contentValues.put(COLUMN_SEDE_ID, sede_id);
        Log.i("aggiungiPresenza", "ora: " + ora );
        Log.i("aggiungiPresenza", "giorno: " + giorno );
        Log.i("aggiungiPresenza", "docente_id: " + docente_id);
        Log.i("aggiungiPresenza", "classe_id: " + classe_id );
        long errorCode = db.insertWithOnConflict(TABELLA_PRESENZE,null,contentValues,SQLiteDatabase.CONFLICT_REPLACE);

        if(errorCode == -1){
            Toast.makeText(context, "Falllito", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, "Aggiunto con successo", Toast.LENGTH_SHORT).show();
        }


    }

    void aggiungiPresenza(ContainerPresenze p){
        aggiungiPresenza(
                p.getGiorno(),
                p.getOra(),
                new ContainerDocente(p.getNome(),p.getCognome()),
                new ContainerSezione(p.getClasse(),p.getSede()),
                p.getSede()
        );
    }



    Cursor readAllData(){
        String query = "SELECT * FROM " + TABELLA_DOCENTI;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if(db != null){
            cursor =  db.rawQuery(query, null);
        }
        return cursor;
    }

    public List<ContainerSezione> getSezioni(){
        List<ContainerSezione> list = new ArrayList<ContainerSezione>();

        // Select All Query
        String selectQuery = "SELECT " + TABELLA_CLASSI + "." + COLUMN_NOME + ", S." + COLUMN_NOME + " AS Sede FROM " +
                TABELLA_CLASSI + " JOIN " + TABELLA_SEDI + " AS S ON " +
                TABELLA_CLASSI + "." + COLUMN_SEDE_ID + " = S." + COLUMN_ID + ";";


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);//selectQuery,selectedArguments

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                list.add(new ContainerSezione(cursor.getString(0),cursor.getString(1)));//adding 2nd column data
            } while (cursor.moveToNext());
        }
        // closing connection
        cursor.close();
        db.close();
        // returning lables
        return list;
    }

    public List<ContainerDocente> getTeachers(){
        List<ContainerDocente> list = new ArrayList<ContainerDocente>();

        // Select All Query
        String selectQuery = "SELECT  _ID, Nome, Cognome, Materia FROM " + TABELLA_DOCENTI;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);//selectQuery,selectedArguments

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                list.add(new ContainerDocente(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getString(3)));//adding 2nd column data
            } while (cursor.moveToNext());
        }
        // closing connection
        cursor.close();
        db.close();
        // returning lables
        return list;
    }

    public List<ContainerPresenze> getPresenze(){
        List<ContainerPresenze> list = new ArrayList<ContainerPresenze>();

        // Select All Query
        String selectQuery = "SELECT  Giorno, Ora,Sede,Classe, Nome, Cognome,Materia FROM " + VIEW_PRESENZE;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                list.add(new ContainerPresenze(

                        cursor.getString(0),
                        cursor.getInt(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6))
                );

            } while (cursor.moveToNext());
        }
        // closing connection
        cursor.close();
        db.close();
        // returning lables
        return list;
    }


    public void cancellaPresenza(ContainerDocente d, int ora, String giorno) {

        SQLiteDatabase db = this.getWritableDatabase();

        String[] projection = {COLUMN_ID};
        String selection = COLUMN_NOME + " = ? AND " + COLUMN_COGNOME + " = ?";
        String[] selectionArgs = {d.getNome(),d.getCognome()};
        Cursor cursor = db.query(TABELLA_DOCENTI,projection,selection,selectionArgs,null,null,null);
        cursor.moveToFirst();
        int docente_id = cursor.getInt(0);
        while(cursor.moveToNext()){
            Log.i("Primo Select", cursor.getInt(0) + "");
        }

        selection = COLUMN_DOCENTE_ID + " = ? AND " + COLUMN_GIORNO + " = ? AND " + COLUMN_ORA + " = ?";
        selectionArgs = new String[]{String.valueOf(docente_id),giorno,String.valueOf(ora)};

        db.delete(TABELLA_PRESENZE,selection,selectionArgs);

    }

    public void cancellaClasse(ContainerSezione s) {

        SQLiteDatabase db = this.getWritableDatabase();

        String selection = COLUMN_NOME + " = ?";
        String[] selectionArgs = new String[]{s.getNome()};

        db.delete(TABELLA_CLASSI,selection,selectionArgs);

    }

    public void cancellaDocente(ContainerDocente d) {
        SQLiteDatabase db = this.getWritableDatabase();

        String selection = COLUMN_NOME + " = ? AND " + COLUMN_COGNOME + " = ? AND " + COLUMN_MATERIA + " = ?";
        String[] selectionArgs = new String[]{d.getNome(),d.getCognome(),d.getMateria()};
        db.delete(TABELLA_DOCENTI,selection,selectionArgs);
    }


}

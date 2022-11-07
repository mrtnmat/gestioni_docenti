package com.hanahonna.gestioneoredocenti;

import static com.hanahonna.gestioneoredocenti.R.id.add_button_classi;
import static com.hanahonna.gestioneoredocenti.R.id.spSede;
import static com.hanahonna.gestioneoredocenti.R.id.spinnerSede;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    EditText nome_input,cognome_input,materia_input;
    ListView viewDisposizioni;
    Spinner mySpinner,mySpinnerSez,mySpGiorno,mySpOra,mySpSede;
    Button add_docente,delete_docente,add_clas,btn_Exit,btn_view,cercaDisposizioni;
    MyDatabaseHelper myDocenti;
    MyDbSetting mySetting;
    ArrayAdapter adapterDisposizioni;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myDocenti.close();
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cercaDisposizioni = findViewById(R.id.cercaDisp);
        viewDisposizioni = findViewById(R.id.listViewDocenti);
        myDocenti = new MyDatabaseHelper(MainActivity.this);
        mySetting = new MyDbSetting(MainActivity.this);
        Log.i("Prova","Cartella" + mySetting);
        nome_input = findViewById(R.id.nome_input);
        cognome_input = findViewById(R.id.cognome_input);
        materia_input = findViewById(R.id.materia_input);
        add_docente = findViewById(R.id.aggiungi_docente);
        delete_docente = findViewById(R.id.cancella_docente);
        btn_view = findViewById(R.id.add_button_view);
        btn_Exit = findViewById(R.id.exit);
        mySpinner = findViewById(R.id.spDocenti);
        mySpGiorno = findViewById(R.id.spGiorno);
        mySpOra = findViewById(R.id.spOra);
        mySpSede = findViewById(R.id.spSede);
        add_clas = findViewById(add_button_classi);



        String[] sd = {"gramsci","villa"};
        String[] gn = {"Lunedì","Martedì","Mercoledì","Giovedì","Venerdì"};
        Integer[] or = {1,2,3,4,5,6};

        ArrayAdapter<String> adapterSede = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,sd);
        adapterSede.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpSede.setAdapter(adapterSede);

        ArrayAdapter<String> adapterGiorno = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,gn);
        adapterGiorno.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpGiorno.setAdapter(adapterGiorno);

        ArrayAdapter<Integer> adapterOra = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item,or);
        adapterOra.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpOra.setAdapter(adapterOra);

        loadSpinnerData();


        cercaDisposizioni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDisposizioniDocenti();
            }
        });

        add_docente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String n = nome_input.getText().toString().trim();
                String c = cognome_input.getText().toString().trim();
                String d = materia_input.getText().toString().trim();
                if(n.isEmpty() || c.isEmpty() || d.isEmpty()){
                    Toast.makeText(MainActivity.this, "Non lasciare campi vuoti", Toast.LENGTH_SHORT).show();
                }else{
                    myDocenti.aggiungiDocente( n,c,d);
                    nome_input.setText("");
                    cognome_input.setText("");
                    materia_input.setText("");
                    nome_input.requestFocus();
                    loadSpinnerData();
                }
            }
        });

        delete_docente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(((ContainerDocente) mySpinner.getSelectedItem())!=null){
                ContainerDocente c = ((ContainerDocente) mySpinner.getSelectedItem());
                myDocenti.cancellaDocente(c);
                loadSpinnerData();} else {
                    Toast.makeText(MainActivity.this, "Seleziona un docente", Toast.LENGTH_SHORT).show();
                }

            }
        });

        add_clas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,addClassi.class);
                startActivity(intent);
            }
        });

        btn_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, ActivityTabellaPresenze.class);
                startActivity(intent);
            }
        });

        btn_Exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finishAndRemoveTask();
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }
        });

    }




    private void loadSpinnerData() {
        MyDatabaseHelper db = new MyDatabaseHelper(getApplicationContext());
        List<ContainerDocente> labels = db.getTeachers();

        // Creating adapter for spinner
        ArrayAdapter<ContainerDocente> dataAdapter = new ArrayAdapter<ContainerDocente>(this,android.R.layout.simple_spinner_item, labels);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        mySpinner.setAdapter(dataAdapter);

    }

    private void loadSpinnerDataSez() {
        MyDatabaseHelper db = new MyDatabaseHelper(getApplicationContext());
        List<ContainerSezione> labels_sez = db.getSezioni();

        // Creating adapter for spinner
        ArrayAdapter<ContainerSezione> dataAdapterSez = new ArrayAdapter<ContainerSezione>(this,android.R.layout.simple_spinner_item, labels_sez);

        // Drop down layout style - list view with radio button
        dataAdapterSez.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        mySpinnerSez.setAdapter(dataAdapterSez);
    }

    private void getDisposizioniDocenti(){

        String g = ((String) mySpGiorno.getSelectedItem());
        String s = ((String) mySpSede.getSelectedItem());
        Integer o = ((Integer) mySpOra.getSelectedItem());

        if(g!=null&&s!=null&&o!=null){

            List<ContainerPresenze> listaPresenze = myDocenti.getPresenze();
            listaPresenze.removeIf(p -> p.getOra()!=o || !p.getSede().equals(s) || !p.getGiorno().equals(g));
            adapterDisposizioni = new ArrayAdapter<ContainerPresenze>(this, android.R.layout.simple_list_item_1,listaPresenze);
            viewDisposizioni.setAdapter(adapterDisposizioni);}else{
            Toast.makeText(this, "Imposta i filtri", Toast.LENGTH_SHORT).show();
        }


    }





}
package com.hanahonna.gestioneoredocenti;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.chivorn.smartmaterialspinner.SmartMaterialSpinner;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class addClassi extends AppCompatActivity {

    EditText nome;
    SmartMaterialSpinner spinnerClassi,spinnerSedi;
    FloatingActionButton add_sezione;
    FloatingActionButton del_sezione;
    MyDatabaseHelper myDocenti;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myDocenti.close();
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_classi);

        myDocenti = new MyDatabaseHelper(addClassi.this);

        nome = findViewById(R.id.nome);
        del_sezione = findViewById(R.id.del_Classe);
        add_sezione = findViewById(R.id.add_classe);
        spinnerClassi= findViewById(R.id.classi);
        spinnerSedi = findViewById(R.id.spinnerSede);

        // Prova Classe Statica
/*
        MyDbSetting s = SingletonDatabase.getDb(this);
        Log.i("Test","Campo" + s );
        List<ContainerColori> coloris = s.getColori();
        coloris.forEach(c -> Log.i("Colori","Lista" + c));
        Log.i("Recupero la Variabile","Var" + SingletonDatabase.getVariabile());
 */

        String[] sd = {"gramsci","villa"};


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,sd);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSedi.setAdapter(adapter);

        loadSpinnerDataSez();

        del_sezione.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContainerSezione c = ((ContainerSezione) spinnerClassi.getSelectedItem());
                if(c != null){
                myDocenti.cancellaClasse(c);
                loadSpinnerDataSez();
                }else{
                    Toast.makeText(addClassi.this, "Seleziona una classe", Toast.LENGTH_SHORT).show();
                }
            }
        });



        add_sezione.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String s = ((String) spinnerSedi.getSelectedItem());
                String n =  nome.getText().toString().trim();
                if(s != null){
                    if(n.isEmpty() || s.isEmpty()){
                        Toast.makeText(addClassi.this, "Non lasciare campi vuoti", Toast.LENGTH_SHORT).show();
                    }else{
                        myDocenti.aggiungiSezione(n,s);
                        nome.setText("");
                        nome.requestFocus();
                        loadSpinnerDataSez();
                    }
                }
            }
        });

    }

    private void loadSpinnerDataSez() {
        MyDatabaseHelper db = new MyDatabaseHelper(getApplicationContext());
        List<ContainerSezione> labels_sez = db.getSezioni();

        // Creating adapter for spinner
        ArrayAdapter<ContainerSezione> dataAdapterSez = new ArrayAdapter<ContainerSezione>(this,android.R.layout.simple_spinner_item, labels_sez);

        // Drop down layout style - list view with radio button
        dataAdapterSez.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinnerClassi.setAdapter(dataAdapterSez);
    }
}
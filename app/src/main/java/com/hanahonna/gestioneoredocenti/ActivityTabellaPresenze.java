package com.hanahonna.gestioneoredocenti;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.skydoves.colorpickerview.ColorPickerDialog;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ActivityTabellaPresenze extends AppCompatActivity {

    ExtendedFloatingActionButton btnInserisciPresenze;
    TextView title;
    Spinner spinnerDocente;
    TableLayout tabellaOraria;
    MyDatabaseHelper dbHelper;
    MyDbSetting dbSetting;
    EditText password;
    View fragVilla;
    View fragGramsci;
    TextView coloreVilla, coloreGramsci;
    ContainerPresenze[][] matricePresenze;

    HashMap<String, Integer> myColors = new HashMap<String, Integer>();
    String[] giorni = {"Lunedì", "Martedì", "Mercoledì", "Giovedì", "Venerdì"};


    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabella_presenze);
        // inizio

        fragVilla = findViewById(R.id.contentVilla);
        password = findViewById(R.id.pwd);
        fragGramsci = findViewById(R.id.contentGramsci);
        btnInserisciPresenze = findViewById(R.id.aggiungiPresenza);
        coloreVilla = findViewById(R.id.villaColor);
        coloreGramsci = findViewById(R.id.gramsciColor);
        spinnerDocente = findViewById(R.id.prova);
        title = findViewById(R.id.docTitle);
        tabellaOraria = findViewById(R.id.tableLayout);
        dbHelper = new MyDatabaseHelper(ActivityTabellaPresenze.this);
        List<ContainerSezione> listaSezioni = dbHelper.getSezioni();

        // Prova classe statica
/*
        MyDbSetting s = SingletonDatabase.getDb(this);
        Log.i("Test","Campo" + s );
        List<ContainerColori> coloris = s.getColori();
        coloris.forEach(c -> Log.i("Colori","Lista" + c.getValore()));
        SingletonDatabase.setVariabile(coloris.get(0).getValore()+"");
*/

        // Riempio lo Spinner della lista dei docenti

        ArrayAdapter<ContainerDocente> spin = new ArrayAdapter<ContainerDocente>(this,   android.R.layout.simple_spinner_item, dbHelper.getTeachers());
        spin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        spinnerDocente.setAdapter(spin);

        dbSetting = new MyDbSetting(this);
        List<ContainerColori> coloriList = dbSetting.getColori();

        if(coloriList.size()<2){
            myColors.put("Gramsci",ContextCompat.getColor(this,R.color.myColorGrigio));
            myColors.put("Villa",ContextCompat.getColor(this,R.color.myColorGrigio));
        }else{
            dbSetting.getColori().forEach(containerColore->{
                myColors.put(containerColore.getNome(),containerColore.getValore());
            });
        }





        // Aggiorno i colori della legenda

        fragVilla.setBackgroundColor(myColors.get("Villa"));
        fragGramsci.setBackgroundColor(myColors.get("Gramsci"));

        // Riempio gli spinner della tabella


        btnInserisciPresenze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(password.getText().toString().equals("CurieLevi2023")){
                    ContainerDocente d = ((ContainerDocente) spinnerDocente.getSelectedItem());
                    if(d != null){
                        for (int i = 0; i < matricePresenze.length; i++) {
                            for (int j = 0; j < matricePresenze[i].length; j++) {
                                ContainerPresenze p = matricePresenze[i][j];
                                if (p != null) {
                                    Log.i("aggiungi presenze", "ora " + p.getOra());
                                    Log.i("aggiungi presenze","giorno " + p.getGiorno());
                                }
                                if (p == null) {
                                    dbHelper.cancellaPresenza(d,i+1,giorni[j]);
                                }else {
                                    dbHelper.aggiungiPresenza(p);
                                }
                            }
                        }
                    }else{
                        Toast.makeText(ActivityTabellaPresenze.this, "Seleziona un docente", Toast.LENGTH_SHORT).show();
                    }

                }else{
                    Toast.makeText(ActivityTabellaPresenze.this, "Inserisci la Password giusta", Toast.LENGTH_SHORT).show();
                }


            }
        });

        spinnerDocente.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                matricePresenze = popolaMatrice();
                title.setText(spinnerDocente.getSelectedItem().toString().toUpperCase());

                for(int index = 1; index < tabellaOraria.getChildCount(); index++) {

                    TableRow rigaTabella = (TableRow) tabellaOraria.getChildAt(index);
                    for(int j = 1; j < rigaTabella.getChildCount();j++){
                        TextView cellaTabella = (TextView) rigaTabella.getChildAt(j);
                        ContainerPresenze pres = matricePresenze[index-1][j-1];
                        cellaTabella.setBackground(ContextCompat.getDrawable(ActivityTabellaPresenze.this,R.color.myColorGrigio));
                        if (pres != null) {
                            cellaTabella.setText(pres.stampaFormatoTabella());
                            if(pres.getSede().equals("gramsci"))
                            {
                                cellaTabella.setBackgroundColor(myColors.get("Gramsci"));
                            } else if (pres.getSede().equals("villa")){
                                cellaTabella.setBackgroundColor(myColors.get("Villa"));
                            }
                        } else {
                            cellaTabella.setText("-");
                        }
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                title.setTextSize(14);
                title.setText("Docenti");
            }
        });

        for(int riga = 1; riga < tabellaOraria.getChildCount(); riga++) {
            final int rigaFinal = riga - 1 ;
            TableRow rigaTabella = (TableRow) tabellaOraria.getChildAt(riga);
            for(int colonna = 1; colonna < rigaTabella.getChildCount();colonna++){
                final int colonnaFinal = colonna - 1;
                TextView cellaTabella = (TextView) rigaTabella.getChildAt(colonna);
                cellaTabella.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        ContainerDocente d = ((ContainerDocente) spinnerDocente.getSelectedItem());
                        if (d != null) {
                            AlertDialog.Builder scegliClasse = new AlertDialog.Builder(ActivityTabellaPresenze.this,R.style.MyDialogTheme);
                            scegliClasse.setTitle("Classi");
                            int scelteFisse = 3;
                            String[] lista = new String[listaSezioni.size()+scelteFisse];
                            lista[0] = "Elimina Presenza";
                            lista[1] = "Disposizione Gramsci";
                            lista[2] = "Disposizione Villa";
                            for(int i = scelteFisse; i < lista.length;i++){
                                lista[i] = listaSezioni.get(i-scelteFisse).toString();
                            }
                            scegliClasse.setItems(lista, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ContainerSezione s;
                                    ContainerPresenze p = null;


                                    switch (which) {
                                        case 0 :

                                            break;
                                        case 1 :
                                            p = new ContainerPresenze(
                                                    giorni[colonnaFinal],
                                                    rigaFinal + 1,
                                                    "gramsci",
                                                    null,
                                                    d.getNome(),
                                                    d.getCognome(),
                                                    d.getMateria()
                                            );


                                            break;
                                        case 2 :
                                            p = new ContainerPresenze(
                                                    giorni[colonnaFinal],
                                                    rigaFinal + 1,
                                                    "villa",
                                                    null,
                                                    d.getNome(),
                                                    d.getCognome(),
                                                    d.getMateria()
                                            );


                                            break;
                                        default:
                                            s = listaSezioni.get(which-scelteFisse);
                                            p = new ContainerPresenze(
                                                    giorni[colonnaFinal],
                                                    rigaFinal + 1,
                                                    s.getSede(),
                                                    s.getNome(),
                                                    d.getNome(),
                                                    d.getCognome(),
                                                    d.getMateria()
                                            );

                                            break;
                                    }
                                    if(p == null){
                                        cellaTabella.setText("-");
                                    }else{
                                        cellaTabella.setText(p.stampaFormatoTabella());
                                    }


                                    matricePresenze[rigaFinal][colonnaFinal] = p;

                                }
                            });
                            AlertDialog dialog = scegliClasse.create();
                            dialog.show();
                        }
                    }
                });
            }
        }


        coloreVilla.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                scegliColore("Villa");
                return false;
            }
        });

        coloreGramsci.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                scegliColore("Gramsci");
                return false;
            }
        });
    }

    private ContainerPresenze[][] popolaMatrice(){

        List<ContainerPresenze> presenzeList = dbHelper.getPresenze();
        ContainerDocente d = ((ContainerDocente) spinnerDocente.getSelectedItem());
        ContainerPresenze[][] m = new ContainerPresenze[6][5];
        for(int riga=0; riga < m.length; riga++){

            for(int colonna = 0; colonna < m[riga].length;colonna++){
                ContainerPresenze p  = cercaPresenze(presenzeList,giorni[colonna],riga+1,d.getNome(),d.getCognome());
                m[riga][colonna] = p;
            }
        }

        return m;
    }

    private ContainerPresenze cercaPresenze(List<ContainerPresenze> p, String giorno, int ora, String dNome, String dCognome){
        for(int i = 0; i < p.size(); i++) {
            ContainerPresenze cp = p.get(i);
            if (Objects.equals(cp.getGiorno(), giorno) && cp.getOra() == ora && Objects.equals(cp.getNome(), dNome) && Objects.equals(cp.getCognome(), dCognome)){
                return cp;
            }
        }
        return null;
    }

    private void scegliColore(String sede){

        new ColorPickerDialog.Builder(ActivityTabellaPresenze.this)
                .setTitle("Scegli un colore")
                .setPreferenceName("MyColorPickerDialog")
                .setPositiveButton("Salva",
                        (ColorEnvelopeListener) (envelope, fromUser) -> {

                            int myColor = envelope.getColor();
                            dbSetting = new MyDbSetting(this);
                            dbSetting.aggiungiColore(sede,myColor);
                            if(sede == "Villa"){
                                fragVilla.setBackgroundColor(myColor);

                            }else if(sede == "Gramsci"){
                                fragGramsci.setBackgroundColor(myColor);
                            }
                            myColors.put(sede,myColor);

                          //  Log.i("Colore","#" + Integer.toHexString(myColor));

                        })
                .setNegativeButton(getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();

                            }
                        })
                .attachAlphaSlideBar(true) // the default value is true.
                .attachBrightnessSlideBar(true)  // the default value is true.
                .setBottomSpace(12) // set a bottom space between the last slidebar and buttons.
                .show();

    }




}
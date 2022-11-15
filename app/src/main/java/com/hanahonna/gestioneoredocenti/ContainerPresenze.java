package com.hanahonna.gestioneoredocenti;

import java.util.ArrayList;

public class ContainerPresenze {

    private String giorno;
    private int ora;
    private String sede;
    private String classe;
    private String nome;
    private String cognome;
    private String materia;

    public ContainerPresenze(String giorno, int ora, String sede, String classe, String nome, String cognome, String materia) {
        this.giorno = giorno;
        this.ora = ora;
        this.sede = sede;
        this.classe = classe;
        this.nome = nome;
        this.cognome = cognome;
        this.materia = materia;

    }

    public String getGiorno(){
        return giorno;
    }
    public int getOra(){
        return ora;
    }
    public String getSede(){
        return sede;
    }
    public String getClasse(){
        return classe;
    }
    public String getNome(){
        return nome;
    }
    public String getCognome(){ return cognome;}

    @Override
    public String toString(){

        String c = classe == null
                ? "D"
                : classe;
        return  nome + " " + cognome + "  " + c;
    }


    public String stampaFormatoTabella(){

        if(classe == null){
            if(sede.equals("gramsci")){
                return "Dg";
            } else if(sede.equals("villa")){
                return "Dv";
            }
        }
        return classe + " " + sede.charAt(0);
    }

}

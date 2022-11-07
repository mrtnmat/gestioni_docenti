package com.hanahonna.gestioneoredocenti;



public class ContainerDocente {

    private int id;
    private String nome;
    private String cognome;
    private String materia;




    public ContainerDocente(int id, String nome, String cognome, String materia){
        this.id = id;
        this.nome = nome;
        this.cognome = cognome;
        this.materia = materia;
    }

    public ContainerDocente(String nome, String cognome){

        this.nome = nome;
        this.cognome = cognome;

    }

    public String getNome(){
        return nome;
    }
    public String getCognome(){
        return cognome;
    }
    public String getMateria(){
        return materia;
    }

    @Override
    public String toString() {
        return cognome + " " + nome;
     }

}

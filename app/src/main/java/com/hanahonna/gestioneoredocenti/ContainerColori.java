package com.hanahonna.gestioneoredocenti;

public class ContainerColori {

    private String nome;
    private int valore;

    public ContainerColori(String nome,int valore){
        this.nome = nome;
        this.valore = valore;

    }

    public String getNome(){
        return nome;
    }
    public int getValore(){
        return valore;
    }
}

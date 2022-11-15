package com.hanahonna.gestioneoredocenti;

import android.util.Log;

public class ContainerSezione {



    private final String nome;
    private final String sede;


    public ContainerSezione(String nome, String sede ){

        this.nome = nome;
        this.sede = sede;
    }

    public String getNome(){
        return nome;
    }
    public String getSede(){
        return sede;
    }

    @Override
    public String toString() {

        String result;
        Log.i("nome","" + nome);
        if(nome.equals("Disposizione")){
            result = String.valueOf(nome.charAt(0));
        }else{
            result = nome + " " ;
        }

        return result + sede.charAt(0);
    }
}

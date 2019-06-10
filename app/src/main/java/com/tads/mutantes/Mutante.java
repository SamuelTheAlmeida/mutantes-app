package com.tads.mutantes;

import java.io.Serializable;

public class Mutante implements Serializable {
    private int id;
    private String nome;
    private String urlFoto;
    private Usuario usuario;

    public Mutante() {

    }

    public Mutante(String nome, String urlFoto, Usuario usuario) {
        this.nome = nome;
        this.urlFoto = urlFoto;
        this.usuario = usuario;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getUrlFoto() {
        return urlFoto;
    }

    public void setUrlFoto(String urlFoto) {
        this.urlFoto = urlFoto;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

}

package com.tads.mutantes;

import java.io.Serializable;

public class Mutante implements Serializable {
    private int id;
    private String nome;
    private Usuario usuario;
    private String foto;

    public Mutante() {

    }

    public Mutante(String nome, Usuario usuario) {
        this.nome = nome;
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

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

}

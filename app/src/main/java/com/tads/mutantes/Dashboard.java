package com.tads.mutantes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Dashboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
    }

    public void listarMutantes(View view) {
        Intent it = new Intent(this, ListaActivity.class);
        startActivity(it);
    }

    public void novoMutante(View view) {
        Intent it = new Intent(this, NovoMutante.class);
        startActivity(it);
    }
}

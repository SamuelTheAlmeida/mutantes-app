package com.tads.mutantes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PesquisarMutante extends AppCompatActivity {
    private EditText inputHabiidade;
    private Button btnPesquisar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pesquisar_mutante);
        inputHabiidade = findViewById(R.id.inputHabilidade);
        btnPesquisar = findViewById(R.id.btnPesquisar);
    }

    public void pesquisar(View view) {
        if (inputHabiidade.getText().toString() == null) {
            Toast.makeText(getApplicationContext(), "Digite uma habilidade", Toast.LENGTH_LONG).show();
            return;
        }

        Intent it = new Intent(PesquisarMutante.this, ListaActivity.class);
        Bundle params = new Bundle();
        params.putString("habilidade", inputHabiidade.getText().toString());
        it.putExtras(params);
        startActivity(it);
    }
}

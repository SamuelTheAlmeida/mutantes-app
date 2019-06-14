package com.tads.mutantes;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ListaActivity extends AppCompatActivity implements Response.Listener, Response.ErrorListener{

    private RequestQueue mQueue;
    public static final String REQUEST_TAG = "listarMutantes";
    public Button mButton;
    public TextView mTextView;
    List<Mutante> listMutantes;
    ListView listaDeMutantes;
    private String habilidade;
    private String acao;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista);
        listMutantes = new ArrayList<Mutante>();
        listaDeMutantes = findViewById(R.id.lista);

        try {
            habilidade = getIntent().getExtras().getString("habilidade");
            acao = "pesquisar";
        } catch (NullPointerException e ) {
            acao = "listarTodos";
        }
    }

    @Override protected void onStart() {
        super.onStart();
        // limpa variaveis para cada vez que for feita uma requisicao
        listaDeMutantes.setAdapter(null);
        listMutantes.clear();
        // requisicao volley
        mQueue = CustomVolleyRequestQueue.getInstance(this.getApplicationContext()).getRequestQueue();
        if (acao.equals("pesquisar")) {
            url = Endpoints.ip + "/mutantes-api/resources/mutantes/listar/" + habilidade;
        } else {
            url = Endpoints.ip + "/mutantes-api/resources/mutantes/";
        }
        final CustomJSONObjectRequest jsonRequest = new CustomJSONObjectRequest(Request.Method.GET,
                url,
                new JSONObject(),
                this,
                this);
        jsonRequest.setTag(REQUEST_TAG);
        mQueue.add(jsonRequest);
    }


    @Override
    public void onResponse(Object response) {
        try {
            // obtém o json recebido
            JSONObject jsonObject = new JSONObject(response.toString());
            // obtém o array (lista) de mutantes do json
            JSONArray mutantes = jsonObject.getJSONArray("mutantes");
            if (mutantes.length() == 0) {
                final AlertDialog alertDialog = new AlertDialog.Builder(ListaActivity.this).create();
                alertDialog.setTitle("Pesquisa");
                alertDialog.setMessage("Nenhum mutante com essa habilidade!");

                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                onBackPressed();
                            }
                        });
                alertDialog.show();

            } else {
                // itera sobre o array para preencher a lista de Mutantes
                for (int i = 0; i < mutantes.length(); i++) {
                    JSONObject mutanteObj = mutantes.getJSONObject(i);
                    Mutante m = new Mutante();
                    m.setId(mutanteObj.getInt("id"));
                    m.setNome(mutanteObj.getString("nome"));
                    m.setFoto(mutanteObj.getString("foto"));
                    listMutantes.add(m);
                }
                // preenche a listView com os mutantes
                AdapterMutantesPersonalizado adapter =
                        new AdapterMutantesPersonalizado(listMutantes, this);
                listaDeMutantes.setAdapter(adapter);
                // seta o evento de click para abrir os detalhes de cada mutante
                listaDeMutantes.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                        //arg2 é o index do item clicado
                        Mutante m = listMutantes.get(arg2);
                        Intent it = new Intent(ListaActivity.this, MutanteActivity.class);
                        Bundle params = new Bundle();
                        params.putInt("id", m.getId());
                        it.putExtras(params);
                        startActivity(it);
                    }
                });
            }

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
    }
}

package com.tads.mutantes;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EditarActivity extends AppCompatActivity {
    private int id;
    private String nome;
    private ArrayList<String> habilidades;
    private String foto;
    private RequestQueue mQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mutante);

        try {
            id = getIntent().getExtras().getInt("id");
            nome = getIntent().getExtras().getString("nome");
            habilidades = getIntent().getExtras().getStringArrayList("habilidades");
            foto = getIntent().getExtras().getString("foto");
            if (id > 0 && nome != "" && habilidades != null && foto != "") {
                salvarEdicao();
            }
        } catch (NullPointerException e ) {
            Toast.makeText(getApplicationContext(), "Mutante inválido", Toast.LENGTH_LONG).show();
        }
    }

    public void salvarEdicao() {
        try {
            String URL = Endpoints.ip + "/mutantes-api/resources/mutantes/editar/" + String.valueOf(id);
            JSONObject jsonBody = new JSONObject();

            jsonBody.put("id", id);
            jsonBody.put("nome", nome);
            jsonBody.put("habilidade1", habilidades.get(0));
            jsonBody.put("habilidade2", habilidades.get(1));
            jsonBody.put("habilidade3", habilidades.get(2));
            jsonBody.put("imagem", foto);

            JsonObjectRequest jsonObject = new JsonObjectRequest(Request.Method.POST, URL, jsonBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    int id = 0;
                    try {
                        id = Integer.valueOf(response.getString("id"));
                    } catch (JSONException e) {
                        Log.d("json", e.getMessage());
                    }
                    if (id > 0) {
                        Toast.makeText(getApplicationContext(), "Mutante " + id + " salvo com sucesso", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Erro ao salvar o mutante", Toast.LENGTH_LONG).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    onBackPressed();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    final Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "Basic " + "c2FnYXJAa2FydHBheS5jb206cnMwM2UxQUp5RnQzNkQ5NDBxbjNmUDgzNVE3STAyNzI=");//put your token here
                    return headers;
                }
            };
            mQueue = CustomVolleyRequestQueue.getInstance(this.getApplicationContext()).getRequestQueue();
            mQueue.add(jsonObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

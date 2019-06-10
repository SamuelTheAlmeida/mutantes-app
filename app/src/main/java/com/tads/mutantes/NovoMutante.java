package com.tads.mutantes;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NovoMutante extends AppCompatActivity {

    private RequestQueue mQueue;
    private TextView nome;
    private TextView h1;
    private TextView h2;
    private TextView h3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_mutante);
        nome = findViewById(R.id.inputNome);
        h1 = findViewById(R.id.inputHabilidade1);
        h2 = findViewById(R.id.inputHabilidade2);
        h3 = findViewById(R.id.inputHabilidade3);
    }

    public void salvar(View view) {
        try {
            String URL = "http://192.168.25.4:8080/mutantes-api/resources/mutantes/novo";
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("nome", nome.getText());

            jsonBody.put("habilidade1", h1.getText());

            jsonBody.put("habilidade2", h2.getText());

            jsonBody.put("habilidade3", h3.getText());

            JsonObjectRequest jsonObject = new JsonObjectRequest(Request.Method.POST, URL, jsonBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    Toast.makeText(getApplicationContext(), "Response:  " + response.toString(), Toast.LENGTH_SHORT).show();
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
        // Toast.makeText(getApplicationContext(), "done", Toast.LENGTH_LONG).show();

    }

}

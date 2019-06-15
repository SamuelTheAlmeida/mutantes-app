package com.tads.mutantes;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import java.util.HashMap;
import java.util.Map;

public class RemoverActivity extends AppCompatActivity {
    private int id;
    private RequestQueue mQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mutante);

        try {
            id = getIntent().getExtras().getInt("id");
            if (id > 0) {
                removerMutante();
            }
        } catch (NullPointerException e ) {
            Toast.makeText(getApplicationContext(), "Mutante inválido", Toast.LENGTH_LONG).show();
        }
    }

    public void removerMutante() {
        try {
            String URL = Endpoints.ip + "/mutantes-api/resources/mutantes/remover/" + String.valueOf(id);
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("id", id);

            JsonObjectRequest jsonObject = new JsonObjectRequest(Request.Method.POST, URL, jsonBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Boolean result = false;
                    try {
                        result = Boolean.parseBoolean(response.getString("result"));
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    if (result) {
                        Toast.makeText(getApplicationContext(), "Mutante removido com sucesso", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Erro ao remover o mutante", Toast.LENGTH_LONG).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
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

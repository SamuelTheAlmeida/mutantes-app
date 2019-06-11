package com.tads.mutantes;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;

public class MutanteActivity extends AppCompatActivity implements Response.Listener, Response.ErrorListener{
    public int idMutante;
    private TextView nome;
    private ImageView imagem;
    private TextView[] habilidades;
    private RequestQueue mQueue;

    public static final String REQUEST_TAG = "obterMutante";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mutante);
        nome = findViewById(R.id.txtNomeMutante);
        imagem = findViewById(R.id.imageView);
        habilidades = new TextView[3];
        habilidades[0] = findViewById(R.id.txtHabilidade1);
        habilidades[1] = findViewById(R.id.txtHabilidade2);
        habilidades[2] = findViewById(R.id.txtHabilidade3);
        Intent it = getIntent();

        if (it != null) {
            Bundle params = it.getExtras();
            if (params != null) {
                idMutante = params.getInt("id");
            } else {
                Toast.makeText(getApplicationContext(), "Mutante inválido", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override protected void onStart() {
        super.onStart();
        mQueue = CustomVolleyRequestQueue.getInstance(this.getApplicationContext()).getRequestQueue();
        final String url = "http://192.168.25.4:8080/mutantes-api/resources/mutantes/" + String.valueOf(idMutante);
        final CustomJSONObjectRequest jsonRequest = new CustomJSONObjectRequest(
                Request.Method.GET,
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
            JSONObject jsonObject = new JSONObject(response.toString());
            Context context = imagem.getContext();
            int idResource = context.getResources().getIdentifier("res_" + String.valueOf(idMutante), "drawable", context.getPackageName());
            if (idResource > 0) {
                imagem.setImageResource(idResource);
            } else {
                String photoPath = Environment.getExternalStorageDirectory() + "/Android/data/"
                        + getApplicationContext().getPackageName() + "/Files" + "/MI_" + String.valueOf(idMutante) + ".jpg";
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                Bitmap bitmap = BitmapFactory.decodeFile(photoPath, options);
                imagem.setImageBitmap(bitmap);
            }
            nome.setText(jsonObject.getString("nome"));

            JSONArray habilidadesArray = jsonObject.getJSONArray("habilidades");
            for (int i = 0; i < habilidadesArray.length(); i++) {
                String habilidade = habilidadesArray.getJSONObject(i).getString("descricao");
                habilidades[i].setText(habilidade);
            }

            Toast.makeText(getApplicationContext(), jsonObject.getString("nome"), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
    }
}

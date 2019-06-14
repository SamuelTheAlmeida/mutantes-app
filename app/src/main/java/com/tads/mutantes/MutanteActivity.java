package com.tads.mutantes;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

public class MutanteActivity extends AppCompatActivity implements Response.Listener, Response.ErrorListener{
    public int idMutante;
    private TextView nome;
    private ImageView imagem;
    private TextView[] habilidades;
    private RequestQueue mQueue;
    private Button btnEditar;
    private Button btnRemover;
    private Button btnSalvar;


    public static final String REQUEST_TAG = "obterMutante";
    private int MY_PERMISSIONS_REQUEST_WRITE;
    private int RESULT_LOAD_IMG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mutante);
        nome = findViewById(R.id.txtNomeMutante);
        nome.setFocusable(false);
        imagem = findViewById(R.id.imageView);
        btnEditar = findViewById(R.id.btnEditar);
        btnRemover = findViewById(R.id.btnRemover);
        btnSalvar = findViewById(R.id.btnSalvar);
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
        final String url = Endpoints.ip + "/mutantes-api/resources/mutantes/" + String.valueOf(idMutante);
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
            String encodedImage = jsonObject.getString("foto");
            if (idResource > 0) {
                imagem.setImageResource(idResource);
            } else {
                if (encodedImage != "") {
                    byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    imagem.setImageBitmap(decodedByte);
                }

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

    public void editarDados(View view) {
        nome.setFocusable(true);
        nome.setFocusableInTouchMode(true);
        btnSalvar.setVisibility(view.VISIBLE);
    }

    public void salvar(View view) {
        BitmapDrawable drawable = (BitmapDrawable) imagem.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        ByteArrayOutputStream stream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
        byte[] image=stream.toByteArray();

        String img_str = Base64.encodeToString(image, 0);

        Intent it = new Intent(MutanteActivity.this, EditarActivity.class);
        Bundle params = new Bundle();
        params.putString("nome", nome.getText().toString());
        params.putString("foto", img_str);
        String[] habilidadesArray = {habilidades[0].getText().toString(), habilidades[1].getText().toString(), habilidades[2].getText().toString()};
        params.putStringArray("habilidades", habilidadesArray);
        it.putExtras(params);
        startActivity(it);
    }

    public void remover(View view) {
        Intent it = new Intent(MutanteActivity.this, RemoverActivity.class);
        Bundle params = new Bundle();
        params.putInt("id", idMutante);
        it.putExtras(params);
        startActivity(it);
    }

    public void escolherFoto(View view) {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE);


                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
    }
}

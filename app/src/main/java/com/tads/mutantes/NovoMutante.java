package com.tads.mutantes;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class NovoMutante extends AppCompatActivity {

    private int MY_PERMISSIONS_REQUEST_READ_CONTACTS;
    private RequestQueue mQueue;
    private TextView nome;
    private EditText h1;
    private EditText h2;
    private EditText h3;
    private ImageView fotoMutante;
    private int RESULT_LOAD_IMG;
    private Bitmap selectedImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_mutante);
        nome = findViewById(R.id.inputNome);
        h1 = findViewById(R.id.inputHabilidade1);
        h2 = findViewById(R.id.inputHabilidade2);
        h3 = findViewById(R.id.inputHabilidade3);
        fotoMutante = findViewById(R.id.fotoMutante);
    }

    public void salvar(View view) {
        if (nome.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "Preencha o nome do mutante!", Toast.LENGTH_LONG).show();
            return;
        }
        if (h1.getText().toString().equals("") && h2.getText().toString().equals("") && h3.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "Preencha ao menos uma habilidade!", Toast.LENGTH_LONG).show();
            return;
        }
        if (fotoMutante.getDrawable() == null) {
            Toast.makeText(getApplicationContext(), "Insira a foto do mutante!", Toast.LENGTH_LONG).show();
            return;
        }
        try {
            String URL = Endpoints.ip + "/mutantes-api/resources/mutantes/novo";
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("nome", nome.getText());

            jsonBody.put("habilidade1", h1.getText());

            jsonBody.put("habilidade2", h2.getText());

            jsonBody.put("habilidade3", h3.getText());

            BitmapDrawable drawable = (BitmapDrawable) fotoMutante.getDrawable();
            Bitmap bitmap = drawable.getBitmap();
            ByteArrayOutputStream stream=new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
            byte[] image=stream.toByteArray();
            System.out.println("byte array:"+image);

            String img_str = Base64.encodeToString(image, 0);
            System.out.println("string:"+img_str);
            jsonBody.put("imagem", img_str);

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

    public void escolherFoto(View view) {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            } else {


                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

            }
        }

        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                selectedImage = BitmapFactory.decodeStream(imageStream);
                fotoMutante.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(NovoMutante.this, "Erro ao abrir a foto", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(NovoMutante.this, "Escolha uma foto",Toast.LENGTH_LONG).show();
        }
    }
}

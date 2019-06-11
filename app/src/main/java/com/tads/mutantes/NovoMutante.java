package com.tads.mutantes;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
            String URL = "http://192.168.25.4:8080/mutantes-api/resources/mutantes/novo";
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("nome", nome.getText());

            jsonBody.put("habilidade1", h1.getText());

            jsonBody.put("habilidade2", h2.getText());

            jsonBody.put("habilidade3", h3.getText());

            JsonObjectRequest jsonObject = new JsonObjectRequest(Request.Method.POST, URL, jsonBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    int id = 0;
                    try {
                        id = Integer.valueOf(response.getString("id"));
                        if (id > 0) {
                            storeImage(selectedImage, id);
                        }
                    } catch (JSONException e) {
                        Log.d("json", e.getMessage());
                    }
                    if (id > 0) {
                        Toast.makeText(getApplicationContext(), "Mutante " + id + " salvo com sucesso", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Erro ao salvar o mutante", Toast.LENGTH_LONG).show();
                    }
                    //Toast.makeText(getApplicationContext(), "Response:  " + response.toString(), Toast.LENGTH_SHORT).show();
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
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);


                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
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
                //Bitmap fotoSalva = loadBitmap(NovoMutante.this, "fotomutante2");
                //Toast.makeText(NovoMutante.this, fotoSalva.getGenerationId(), Toast.LENGTH_LONG).show();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(NovoMutante.this, "Erro ao salvar a foto", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(NovoMutante.this, "Escolha uma foto",Toast.LENGTH_LONG).show();
        }
    }

    private void storeImage(Bitmap image, int id) {
        File pictureFile = getOutputMediaFile(id);
        if (pictureFile == null) {
            Log.d("img",
                    "Error creating media file, check storage permissions: ");// e.getMessage());
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d("img", "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d("img", "Error accessing file: " + e.getMessage());
        }
    }

    private  File getOutputMediaFile(int id){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + getApplicationContext().getPackageName()
                + "/Files");

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }
        // Create a media file name
        //String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        File mediaFile;
        String mImageName="MI_"+ String.valueOf(id) +".jpg";
        Log.d("img", mediaStorageDir.getPath());
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }

}

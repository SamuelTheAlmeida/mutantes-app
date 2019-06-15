package com.tads.mutantes;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class AdapterMutantesPersonalizado extends BaseAdapter {

    private final List<Mutante> mutantes;
    private final Activity act;

    public AdapterMutantesPersonalizado(List<Mutante> mutantes, Activity act) {
        this.mutantes = mutantes;
        this.act = act;
    }

    @Override
    public int getCount() {
        return mutantes.size();
    }

    @Override
    public Object getItem(int position) {
        return mutantes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mutantes.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = act.getLayoutInflater()
                .inflate(R.layout.mylist, parent, false);
        Mutante mutante = mutantes.get(position);

        //pegando as referências das Views
        TextView id = (TextView)
                view.findViewById(R.id.lista_id);
        TextView nome = (TextView)
                view.findViewById(R.id.lista_nome);
        ImageView imagem = (ImageView)
                view.findViewById(R.id.lista_img);

        //populando as Views
        id.setText(String.valueOf(mutante.getId()));
        nome.setText(mutante.getNome());
        //imagem.setImageResource(com.example.mutantes.R.drawable.juggernaut);

            byte[] decodedString = Base64.decode(mutante.getFoto(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            imagem.setImageBitmap(decodedByte);





        return view;
    }
}
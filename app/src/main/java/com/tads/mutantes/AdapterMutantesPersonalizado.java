package com.tads.mutantes;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
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
        Context context = imagem.getContext();
        int idResource = context.getResources().getIdentifier("res_" + String.valueOf(mutante.getId()), "drawable", context.getPackageName());
        if (idResource > 0) {
            imagem.setImageResource(idResource);
        } else {
            String photoPath = Environment.getExternalStorageDirectory() + "/Android/data/"
                    + parent.getContext().getPackageName() + "/Files" + "/MI_" + String.valueOf(mutante.getId()) + ".jpg";
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(photoPath, options);
            imagem.setImageBitmap(bitmap);
        }




        return view;
    }
}
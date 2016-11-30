package jivamukti.techdev.com.jivamukti;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by reyes on 12/7/15.
 */
public class premiosAdapter extends RecyclerView.Adapter<premiosAdapter.RevistaViewHolder>

{

    private List<premio> items;
    View v;


    public static class RevistaViewHolder extends RecyclerView.ViewHolder {
        // Campos de la lista
        public ImageView imagen;
        public TextView titulo;
        public TextView contenido;
        public TextView vence;
        private Context context;

        public RevistaViewHolder(View v) {
            super(v);
            imagen = (ImageView) v.findViewById(R.id.imagen);
            titulo = (TextView) v.findViewById(R.id.titulo);
            contenido = (TextView) v.findViewById(R.id.contenido);
            vence   = (TextView) v.findViewById(R.id.vence);

        }


    }

    public premiosAdapter(List<premio> items) {
        this.items = items;
    }

    /*
    Añade una lista completa de items
     */
    public void addAll(List<premio> lista) {
        items.addAll(lista);
        notifyDataSetChanged();
    }

    /*
    Permite limpiar todos los elementos del recycler
     */
    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public RevistaViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
         v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_item, viewGroup, false);
        return new RevistaViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final RevistaViewHolder viewHolder, final int i) {
        viewHolder.imagen.setImageBitmap(items.get(i).getIdImagen());
        viewHolder.imagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("url",items.get(i).getUrl());
                Log.e("titulo",items.get(i).getTitulo());
                Intent intent = new Intent(v.getContext(), TerminosActivity.class);
                intent.putExtra("url", items.get(i).getUrl());
                ((Activity)v.getContext()).startActivity(intent);
                ((Activity)v.getContext()).overridePendingTransition(R.anim.zoom_forward_in, R.anim.zoom_forward_out);

            }
        });
        viewHolder.titulo.setText(items.get(i).getTitulo());
        viewHolder.contenido.setText(String.valueOf(items.get(i).getContenido()));
        viewHolder.vence.setText(String.valueOf(items.get(i).getVencimiento()));
    }


}
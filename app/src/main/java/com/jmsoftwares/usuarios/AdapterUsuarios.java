package com.jmsoftwares.usuarios;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import static android.support.constraint.Constraints.TAG;


public class AdapterUsuarios extends ArrayAdapter<Usuarios> {

    private Activity activity;
    private List<Usuarios> Usuario;
    private static LayoutInflater inflater = null;


    public AdapterUsuarios (Activity activity, int textViewResourceId,List<Usuarios> Usuario) {
        super(activity, textViewResourceId, Usuario);
        try {
            this.activity = activity;
            this.Usuario = (ArrayList<Usuarios>) Usuario;

            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        } catch (Exception e) {

        }
    }

    public int getCount() {
        return Usuario.size();
    }

    public Usuarios getId(Usuarios position) {
        return position;
    }


    public int getId(int position) {
        return position;
    }
    public int getNome(int position) {
        return position;
    }
    public int getAvatar(int position) {
        return position;
    }

    public static class ViewHolder {
       public ImageView avatar;
        public TextView id;
        public TextView nome;

    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        final ViewHolder holder;
        try {
            if (convertView == null) {
                vi = inflater.inflate(R.layout.list_dados, null);
                holder = new ViewHolder();


                holder.id = (TextView) vi.findViewById(R.id.id);
                holder.nome = (TextView) vi.findViewById(R.id.nome);
                holder.avatar = (ImageView) vi.findViewById(R.id.AvatarList);


                vi.setTag(holder);
            } else {
                holder = (ViewHolder) vi.getTag();
            }

            System.out.println(Usuario.get(position).getId()+"  "+Usuario.get(position).nome);

            holder.id.setText("ID: "+String.valueOf(Usuario.get(position).getId()));
            holder.nome.setText("Nome: "+Usuario.get(position).nome);
            holder.avatar.setImageBitmap(Usuario.get(position).getAvatar());




        } catch (Exception e) {


        }
        return vi;
    }



    private Bitmap getImageBitmap(String url) {
        Bitmap bm = null;
        try {
            URL aURL = new URL(url);
            URLConnection conn = aURL.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();
        } catch (IOException e) {
            Log.e(TAG, "Error getting bitmap", e);
        }
        return bm;
    }

}
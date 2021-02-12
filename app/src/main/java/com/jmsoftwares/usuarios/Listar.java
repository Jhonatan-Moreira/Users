package com.jmsoftwares.usuarios;

import android.app.AlertDialog;
import android.app.ListActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class Listar extends AppCompatActivity {

    ListView lista;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar);


        lista = (ListView) findViewById(R.id.listview1);


        new ConsumirDados()
                .execute("http://5bf57c322a6f080013a34eaf.mockapi.io/api/v1/entity/");


        if (verificaConexao() == true) {

            lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override

                public void onItemClick(AdapterView<?> a, View v, int position, long id) {

                    Usuarios user = (Usuarios) a.getItemAtPosition(position);

                    String mensagem = "" + String.valueOf(user.getId());

                    Intent intent = new Intent(Listar.this, Detalhes.class);

                    Context context = getApplicationContext();
                    int duration = Toast.LENGTH_SHORT;

                    intent.putExtra("id", mensagem);

                    startActivity(intent);

                }
            });

        } else {

            Context context = getApplicationContext();
            int duration = Toast.LENGTH_SHORT;

            Toast toas = Toast.makeText(context, "Por favor, se conecte com a internet!", duration);
            toas.show();


        }

    }


    public boolean verificaConexao() {
        boolean conectado;
        ConnectivityManager conectivtyManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conectivtyManager.getActiveNetworkInfo() != null
                && conectivtyManager.getActiveNetworkInfo().isAvailable()
                && conectivtyManager.getActiveNetworkInfo().isConnected()) {
            conectado = true;
        } else {
            conectado = false;
        }
        return conectado;
    }


    class ConsumirDados extends AsyncTask<String, Void, List<Usuarios>> {

        List<Usuarios> usuarios = new ArrayList<Usuarios>();

        ProgressDialog dialog;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(Listar.this, "Aguarde...",
                    "Carregando usuários, Por favor aguarde...");
        }


        public String json;


        @Override
        protected List<Usuarios> doInBackground(String... params) {
            String urlString = params[0];

            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpget = new HttpGet(urlString);

            try {
                HttpResponse response = httpclient.execute(httpget);
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    InputStream instream = entity.getContent();
                    String json = getStringFromInputStream(instream);
                    instream.close();
                    List<Usuarios> usuarios = getUsuarios(json);

                    return usuarios;
                } else {
                    return null;

                }
            } catch (Exception e) {
                Log.e("Erro", "Falha ao acessar Web service", e);
                return null;
            }


        }


        public List<Usuarios> getUsuarios(String jsonString) {

            try {
                JSONArray usuariosJson = new JSONArray(jsonString);
                JSONObject usuario;

                for (int i = 0; i < usuariosJson.length(); i++) {
                    usuario = new JSONObject(usuariosJson.getString(i));

                    Usuarios objUser = new Usuarios();
                    objUser.setId(usuario.getInt("id"));
                    objUser.setData(usuario.getString("createdAt"));
                    objUser.setNome(usuario.getString("name"));

                    Bitmap avatar = null;
                    try {
                        InputStream is = new URL(usuario.getString("avatar")).openStream();

                        avatar = BitmapFactory.decodeStream(is);
                    } catch (Exception e) { // Catch the download exception
                        e.printStackTrace();
                    }

                    objUser.setAvatar(avatar);


                    usuarios.add(objUser);
                }

                for (int i = 0; i < usuarios.size(); i++) {

                    Log.i("Users: ", usuarios.get(i).getId() + "  " + usuarios.get(i).getNome() + "  " + usuarios.get(i).getAvatar() + "   " + usuarios.get(i).getData());

                }

            } catch (JSONException e) {
                Log.e("Erro", "Erro no parsing do JSON", e);
            }
            return usuarios;

        }


        @Override
        protected void onPostExecute(List<Usuarios> result) {
            super.onPostExecute(result);
            dialog.dismiss();
            if (result.size() > 0) {

                AdapterUsuarios adapterUsuarios = new AdapterUsuarios(Listar.this, 0, result);
                lista.setAdapter(adapterUsuarios);


            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        Listar.this).setTitle("Atenção")
                        .setMessage("Não foi possivel acessar essas informções...")
                        .setPositiveButton("OK", null);
                builder.create().show();
            }
        }


        private String getStringFromInputStream(InputStream instream) throws IOException {

            String json = "";
            BufferedReader reader = null;
            InputStream in = new BufferedInputStream(instream);

            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            StringBuffer buffer = new StringBuffer();
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            json = buffer.toString();
            return json;
        }


    }
}



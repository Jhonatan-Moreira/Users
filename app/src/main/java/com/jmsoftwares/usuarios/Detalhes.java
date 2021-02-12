package com.jmsoftwares.usuarios;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jmsoftwares.usuarios.database.BancoOpenHelper;

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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.valueOf;

public class Detalhes extends AppCompatActivity {

    TextView id;
    TextView nome;
    TextView data;
    ImageView avatar;
    Button remover;


    String ListarId;

    List<UsuarioDetalhes> usuarios = new ArrayList<UsuarioDetalhes>();

    private SQLiteDatabase conexao;
    private BancoOpenHelper dadosopenhelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes);


        Intent in = getIntent();
        Bundle b = in.getExtras();

        ListarId = b.getString("id");

        id = (TextView) findViewById(R.id.Detalhesid);
        nome = (TextView) findViewById(R.id.Detalhenome);
        data = (TextView) findViewById(R.id.Detalhedata);
        avatar = (ImageView) findViewById(R.id.Detalhesavatar);
        remover = (Button) findViewById(R.id.remover);


        id.setText("ID: " + ListarId);
        


        boolean jaExiste = isCadastrado(ListarId);
        if (!jaExiste) {


            new SegundaConsulta()
                    .execute("http://5bf57c322a6f080013a34eaf.mockapi.io/api/v1/entity/");


        } else {

            ConsultarSql("select * from usuarios where id = '"+ListarId+"';");


        }



        remover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ExecSql("DELETE from USUARIOS where id = '"+ListarId+"';");
                finish();


            }

        });


    }

    public String ImageToString(Bitmap bitmap) {

        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp=Base64.encodeToString(b, Base64.DEFAULT);

        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, temp, duration);
        toast.show();


        return temp;
        }



        public static Bitmap StringToBitMap(String encodedString){
        try{
            byte [] encodeByte=Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        }catch(Exception e){
            e.getMessage();
            return null;
        }
    }



    public boolean isCadastrado(String idd) {

        criarConexao();

        boolean existe = false;
        try {
            dadosopenhelper = new BancoOpenHelper(this);
            conexao = dadosopenhelper.getWritableDatabase();

            Cursor cursor = conexao.rawQuery("SELECT *FROM USUARIOS WHERE id =  '" + idd + "';", null);
            System.out.println(cursor.getCount() + "  " + idd);
            if (cursor.getCount() >= 1) {
                existe = true;

            }
        } catch (SQLException ex) {

            Context context = getApplicationContext();
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, "Erro ao Consultar! : " + ex, duration);
            toast.show();

        }
        return existe;
    }


    public void criarConexao() {

        try {

            dadosopenhelper = new BancoOpenHelper(this);

            conexao = dadosopenhelper.getWritableDatabase();

        } catch (SQLException ex) {

            Context context = getApplicationContext();
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, "Erro na conexão! : " + ex, duration);
            toast.show();

        }
    }

    public void ConsultarSql(String sql) {

        criarConexao();

        try {

            Cursor cursor = conexao.rawQuery(sql, null);


            int Coluna_nome = cursor.getColumnIndex("nome");
            int Coluna_avatar = cursor.getColumnIndex("avatar");
            int Coluna_data = cursor.getColumnIndex("data");

            if (cursor.getCount() > 0) {

                cursor.moveToFirst();

                do {

                    nome.setText("Nome: "+cursor.getString(Coluna_nome));
                    data.setText("Criado em: "+cursor.getString(Coluna_data));

                    avatar.setImageBitmap(StringToBitMap(cursor.getString(Coluna_avatar)));


                    cursor.moveToNext();
                } while (!cursor.isAfterLast());

            }

        } catch (SQLException ex) {

            Context context = getApplicationContext();



        }
    }

    public void InserirBanco(String nome, String id, String avatar, String data) {

        criarConexao();

        String sql = "Insert into usuarios (id, nome, avatar, data) values ('" + id + "','" + nome + "','" + avatar + "','" + data + "')";
        ExecSql(sql);

        Context cont = getApplicationContext();
        int durat = Toast.LENGTH_SHORT;

        Toast toa = Toast.makeText(cont, "Usuario Salvo!", durat);
         toa.show();


    }

    public void ExecSql(String sql) {

        try {

            criarConexao();

            conexao.execSQL(sql);

            System.out.println("Inseriu!");
        } catch (SQLException ex) {

            Context context = getApplicationContext();
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, "Erro ao inserir! : " + ex, duration);
            toast.show();

        }


    }


    class SegundaConsulta extends AsyncTask<String, Void, List<UsuarioDetalhes>> {


        ProgressDialog dialog;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(Detalhes.this, "Aguarde...",
                    "Carregando usuário. Por favor aguarde...");
        }


        public String json;


        @Override
        protected List<UsuarioDetalhes> doInBackground(String... params) {
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
                    List<UsuarioDetalhes> UsuarioDetalhes = getUsuarios(json);

                    return usuarios;
                } else {
                    return null;

                }
            } catch (Exception e) {
                Log.e("Erro", "Falha ao acessar Web service", e);
                return null;
            }


        }


        public List<UsuarioDetalhes> getUsuarios(String jsonString) {

            try {
                JSONArray usuariosJson = new JSONArray(jsonString);
                JSONObject usuario;

                for (int i = 0; i < usuariosJson.length(); i++) {
                    usuario = new JSONObject(usuariosJson.getString(i));

                    UsuarioDetalhes objUser = new UsuarioDetalhes();
                    objUser.setId(usuario.getInt("id"));
                    objUser.setData(usuario.getString("createdAt"));
                    objUser.setNome(usuario.getString("name"));

                    if (valueOf(usuario.getInt("id")).equals(ListarId)) {
                        Bitmap logo = null;
                        try {
                            InputStream is = new URL(usuario.getString("avatar")).openStream();

                            logo = BitmapFactory.decodeStream(is);
                        } catch (Exception e) { // Catch the download exception
                            e.printStackTrace();
                        }

                        objUser.setAvatar(logo);

                        usuarios.add(objUser);
                    }
                }

                for (int i = 0; i < usuarios.size(); i++) {

                    Log.i("Users Second : ", usuarios.get(i).getId() + "  " + usuarios.get(i).getNome() + "  " + usuarios.get(i).getAvatar() + "   " + usuarios.get(i).getData());

                }


            } catch (JSONException e) {
                Log.e("Erro", "Erro ao carregar JSON", e);
            }

            return usuarios;


        }


        @Override
        protected void onPostExecute(List<UsuarioDetalhes> result) {
            super.onPostExecute(result);
            dialog.dismiss();
            if (result.size() > 0) {


                for (int i = 0; i < usuarios.size(); i++)
                    if (valueOf(usuarios.get(i).getId()).equals(ListarId)) {

                        nome.setText("Nome: " + result.get(i).getNome());
                        data.setText("Criado em: " + result.get(i).getData());
                        avatar.setImageBitmap(result.get(i).getAvatar());

                        InserirBanco(result.get(i).getNome(), ListarId, ImageToString(result.get(i).getAvatar()), result.get(i).getData());


                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                Detalhes.this).setTitle("Atenção")
                                .setMessage("Não foi possivel acessar essas informções...")
                                .setPositiveButton("OK", null);
                        builder.create().show();
                    }
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





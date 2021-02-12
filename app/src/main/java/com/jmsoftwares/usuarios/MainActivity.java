package com.jmsoftwares.usuarios;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;


public class MainActivity extends AppCompatActivity {

    Button consultar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

consultar = (Button) findViewById(R.id.consultar);


consultar.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {


        TextView texto = (TextView) findViewById(R.id.texto);

if (verificaConexao() == true ) {


    //chamada para a nova Activity
    Intent intent = new Intent(MainActivity.this, Listar.class);


    startActivity(intent);
}else{

    Context context = getApplicationContext();
    int duration = Toast.LENGTH_SHORT;

    Toast toas = Toast.makeText(context, "Por favor, se conecte com a internet!", duration);
    toas.show();


}


    }

});


    }

    public  boolean verificaConexao() {
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
}

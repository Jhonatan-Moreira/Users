package com.jmsoftwares.usuarios.database;

public class ScriptDLL {


    public static String getCreateTableUsuarios(){

        StringBuilder sql = new StringBuilder();

        sql.append( "CREATE TABLE IF NOT EXISTS USUARIOS");
        sql.append( "(id INTEGER,");
        sql.append( "nome VARCHAR(100),");
        sql.append( "avatar VARCHAR,");
        sql.append( "data VARCHAR(45) );");

       return sql.toString();

    }
}

package com.example.pm1ea0471.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/** DAO: consultas SQL sobre la tabla de paises. */
public class PaisDao {

    private final BaseDatos baseDatos;

    public PaisDao(BaseDatos baseDatos) {
        this.baseDatos = baseDatos;
    }

    /** Devuelve false si el pais ya existe. */
    public boolean insertar(String nombrePais) {
        SQLiteDatabase db = baseDatos.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put(BaseDatos.COL_PAIS_NOMBRE, nombrePais);
        long id = db.insertWithOnConflict(BaseDatos.TABLA_PAISES, null, valores, SQLiteDatabase.CONFLICT_IGNORE);
        db.close();
        return id != -1;
    }

    public List<String> listar() {
        List<String> paises = new ArrayList<>();
        SQLiteDatabase db = baseDatos.getReadableDatabase();
        Cursor cursor = db.query(BaseDatos.TABLA_PAISES, null, null, null, null, null, BaseDatos.COL_PAIS_ID);
        while (cursor.moveToNext()) {
            paises.add(cursor.getString(cursor.getColumnIndexOrThrow(BaseDatos.COL_PAIS_NOMBRE)));
        }
        cursor.close();
        db.close();
        return paises;
    }
}

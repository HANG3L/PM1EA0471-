package com.example.pm1ea0471.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.pm1ea0471.modelo.Contacto;

import java.util.ArrayList;
import java.util.List;

/** DAO: unico lugar que ejecuta consultas SQL sobre la tabla de contactos. */
public class ContactoDao {

    private final BaseDatos baseDatos;

    public ContactoDao(BaseDatos baseDatos) {
        this.baseDatos = baseDatos;
    }

    public long insertar(Contacto contacto) {
        SQLiteDatabase db = baseDatos.getWritableDatabase();
        long id = db.insert(BaseDatos.TABLA_CONTACTOS, null, crearValores(contacto));
        db.close();
        return id;
    }

    public List<Contacto> listar() {
        return consultar(null, null);
    }

    /** Busca por nombre o telefono. */
    public List<Contacto> buscar(String texto) {
        String filtro = "%" + texto + "%";
        return consultar(BaseDatos.COL_NOMBRE + " LIKE ? OR " + BaseDatos.COL_TELEFONO + " LIKE ?",
                new String[]{filtro, filtro});
    }

    public Contacto obtener(int id) {
        List<Contacto> resultado = consultar(BaseDatos.COL_ID + " = ?", new String[]{String.valueOf(id)});
        return resultado.isEmpty() ? null : resultado.get(0);
    }

    public int actualizar(Contacto contacto) {
        SQLiteDatabase db = baseDatos.getWritableDatabase();
        int filas = db.update(BaseDatos.TABLA_CONTACTOS, crearValores(contacto), BaseDatos.COL_ID + " = ?",
                new String[]{String.valueOf(contacto.getId())});
        db.close();
        return filas;
    }

    public int eliminar(int id) {
        SQLiteDatabase db = baseDatos.getWritableDatabase();
        int filas = db.delete(BaseDatos.TABLA_CONTACTOS, BaseDatos.COL_ID + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
        return filas;
    }

    private List<Contacto> consultar(String condicion, String[] argumentos) {
        List<Contacto> contactos = new ArrayList<>();
        SQLiteDatabase db = baseDatos.getReadableDatabase();
        Cursor cursor = db.query(BaseDatos.TABLA_CONTACTOS, null, condicion, argumentos, null, null,
                BaseDatos.COL_NOMBRE + " COLLATE NOCASE");

        while (cursor.moveToNext()) {
            contactos.add(new Contacto(
                    cursor.getInt(cursor.getColumnIndexOrThrow(BaseDatos.COL_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(BaseDatos.COL_PAIS)),
                    cursor.getString(cursor.getColumnIndexOrThrow(BaseDatos.COL_NOMBRE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(BaseDatos.COL_TELEFONO)),
                    cursor.getString(cursor.getColumnIndexOrThrow(BaseDatos.COL_NOTA)),
                    cursor.getString(cursor.getColumnIndexOrThrow(BaseDatos.COL_IMAGEN))));
        }
        cursor.close();
        db.close();
        return contactos;
    }

    private ContentValues crearValores(Contacto contacto) {
        ContentValues valores = new ContentValues();
        valores.put(BaseDatos.COL_PAIS, contacto.getPais());
        valores.put(BaseDatos.COL_NOMBRE, contacto.getNombre());
        valores.put(BaseDatos.COL_TELEFONO, contacto.getTelefono());
        valores.put(BaseDatos.COL_NOTA, contacto.getNota());
        valores.put(BaseDatos.COL_IMAGEN, contacto.getImagen());
        return valores;
    }
}

package com.example.pm1ea0471.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/** Crea y actualiza la base de datos SQLite. Las consultas viven en los DAO. */
public class BaseDatos extends SQLiteOpenHelper {

    private static final String NOMBRE_BD = "agenda.db";
    private static final int VERSION_BD = 3;

    public static final String TABLA_CONTACTOS = "contactos";
    public static final String COL_ID = "id";
    public static final String COL_PAIS = "pais";
    public static final String COL_NOMBRE = "nombre";
    public static final String COL_TELEFONO = "telefono";
    public static final String COL_NOTA = "nota";
    public static final String COL_IMAGEN = "imagen";

    public static final String TABLA_PAISES = "paises";
    public static final String COL_PAIS_ID = "id";
    public static final String COL_PAIS_NOMBRE = "nombre";

    private static final String CREAR_TABLA_CONTACTOS =
            "CREATE TABLE " + TABLA_CONTACTOS + " (" +
                    COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_PAIS + " TEXT, " +
                    COL_NOMBRE + " TEXT, " +
                    COL_TELEFONO + " TEXT, " +
                    COL_NOTA + " TEXT, " +
                    COL_IMAGEN + " TEXT" +
                    ");";

    private static final String CREAR_TABLA_PAISES =
            "CREATE TABLE " + TABLA_PAISES + " (" +
                    COL_PAIS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_PAIS_NOMBRE + " TEXT UNIQUE" +
                    ");";

    public BaseDatos(Context contexto) {
        super(contexto, NOMBRE_BD, null, VERSION_BD);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREAR_TABLA_CONTACTOS);
        db.execSQL(CREAR_TABLA_PAISES);
        insertarPaisesIniciales(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int versionAnterior, int versionNueva) {
        if (versionAnterior == 2) {
            // La version 2 tenia las tablas en ingles: solo se renombran para no perder datos
            db.execSQL("ALTER TABLE contacts RENAME TO " + TABLA_CONTACTOS);
            db.execSQL("ALTER TABLE countries RENAME TO " + TABLA_PAISES);
            return;
        }
        db.execSQL("DROP TABLE IF EXISTS contacts");
        db.execSQL("DROP TABLE IF EXISTS countries");
        db.execSQL("DROP TABLE IF EXISTS " + TABLA_CONTACTOS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLA_PAISES);
        onCreate(db);
    }

    private void insertarPaisesIniciales(SQLiteDatabase db) {
        String[] paisesIniciales = {
                "Honduras (+504)",
                "El Salvador (+503)",
                "Guatemala (+502)",
                "Costa Rica (+506)",
                "Nicaragua (+505)",
                "Panamá (+507)",
                "Estados Unidos (+1)",
                "México (+52)"
        };
        for (String pais : paisesIniciales) {
            ContentValues valores = new ContentValues();
            valores.put(COL_PAIS_NOMBRE, pais);
            db.insertWithOnConflict(TABLA_PAISES, null, valores, SQLiteDatabase.CONFLICT_IGNORE);
        }
    }
}

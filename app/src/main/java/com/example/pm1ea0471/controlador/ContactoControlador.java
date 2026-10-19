package com.example.pm1ea0471.controlador;

import android.content.Context;

import com.example.pm1ea0471.dao.BaseDatos;
import com.example.pm1ea0471.dao.ContactoDao;
import com.example.pm1ea0471.modelo.Contacto;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * CONTROLADOR: reglas de negocio de los contactos (validar, guardar, buscar, formato del telefono).
 * Las vistas hablan con esta clase y nunca con el DAO directamente.
 */
public class ContactoControlador {

    /** Primer error encontrado al validar el formulario. */
    public static class Error {
        public enum Campo {NOMBRE, TELEFONO, NOTA}

        public final Campo campo;
        public final String titulo;
        public final String mensaje;
        public final String errorCampo;

        Error(Campo campo, String titulo, String mensaje, String errorCampo) {
            this.campo = campo;
            this.titulo = titulo;
            this.mensaje = mensaje;
            this.errorCampo = errorCampo;
        }
    }

    private static final Pattern PATRON_NOMBRE = Pattern.compile("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$");
    private static final Pattern PATRON_TELEFONO = Pattern.compile("^\\d{8}$");
    private static final Pattern PATRON_CODIGO_PAIS = Pattern.compile("\\(\\+?(\\d+)\\)");

    private final ContactoDao dao;

    public ContactoControlador(Context contexto) {
        this.dao = new ContactoDao(new BaseDatos(contexto));
    }

    /** Devuelve el primer error del formulario, o null si todos los datos son validos. */
    public Error validar(String nombre, String telefono, String nota) {
        if (nombre.isEmpty()) {
            return new Error(Error.Campo.NOMBRE, "Falta el nombre",
                    "Debe escribir un nombre.", "Campo obligatorio");
        }
        if (!PATRON_NOMBRE.matcher(nombre).matches()) {
            return new Error(Error.Campo.NOMBRE, "Nombre no válido",
                    "El nombre solo debe contener letras y espacios.", "Solo letras y espacios");
        }
        if (telefono.isEmpty()) {
            return new Error(Error.Campo.TELEFONO, "Falta el teléfono",
                    "Debe escribir un teléfono de 8 dígitos.", "Campo obligatorio");
        }
        if (!PATRON_TELEFONO.matcher(telefono).matches()) {
            return new Error(Error.Campo.TELEFONO, "Teléfono no válido",
                    "El teléfono debe tener exactamente 8 dígitos numéricos.", "Deben ser 8 dígitos");
        }
        if (nota.isEmpty()) {
            return new Error(Error.Campo.NOTA, "Falta la nota",
                    "Debe escribir una nota.", "Campo obligatorio");
        }
        return null;
    }

    public boolean guardar(Contacto contacto) {
        return dao.insertar(contacto) != -1;
    }

    public boolean actualizar(Contacto contacto) {
        return dao.actualizar(contacto) > 0;
    }

    public void eliminar(int id) {
        dao.eliminar(id);
    }

    public Contacto obtener(int id) {
        return dao.obtener(id);
    }

    public List<Contacto> listar() {
        return dao.listar();
    }

    public List<Contacto> buscar(String texto) {
        String limpio = texto == null ? "" : texto.trim();
        return limpio.isEmpty() ? dao.listar() : dao.buscar(limpio);
    }

    /** Codigo de llamada del pais: "Honduras (+504)" -> "504". Vacio si no tiene. */
    public String codigoPais(String pais) {
        if (pais == null) {
            return "";
        }
        Matcher coincidencia = PATRON_CODIGO_PAIS.matcher(pais);
        return coincidencia.find() ? coincidencia.group(1) : "";
    }

    /** Numero para marcar: +50499765645 */
    public String telefonoParaMarcar(Contacto contacto) {
        String codigo = codigoPais(contacto.getPais());
        return codigo.isEmpty() ? contacto.getTelefono() : "+" + codigo + contacto.getTelefono();
    }

    /** Numero para mostrar: +504 9976-5645 */
    public String telefonoParaMostrar(Contacto contacto) {
        String codigo = codigoPais(contacto.getPais());
        String local = formatearLocal(contacto.getTelefono());
        return codigo.isEmpty() ? local : "+" + codigo + " " + local;
    }

    /** 99765645 -> 9976-5645 */
    private String formatearLocal(String telefono) {
        if (telefono != null && telefono.length() == 8) {
            return telefono.substring(0, 4) + "-" + telefono.substring(4);
        }
        return telefono;
    }
}

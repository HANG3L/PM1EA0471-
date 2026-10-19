package com.example.pm1ea0471.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;

import com.example.pm1ea0471.R;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.util.function.Consumer;

/**
 * Todas las alertas de la app en un solo lugar, con el mismo estilo:
 * - mostrar: dialogo informativo con un boton.
 * - confirmar: dialogo con Cancelar / Aceptar (usa AlertDialog.Builder de Material).
 * - aviso: mensaje corto en la parte inferior (Snackbar), sin interrumpir.
 */
public final class Alertas {

    public enum Tipo {
        EXITO(R.color.alerta_exito, R.color.alerta_exito_fondo, R.drawable.ic_exito),
        ERROR(R.color.alerta_error, R.color.alerta_error_fondo, R.drawable.ic_error),
        ADVERTENCIA(R.color.alerta_aviso, R.color.alerta_aviso_fondo, R.drawable.ic_advertencia),
        INFO(R.color.alerta_info, R.color.alerta_info_fondo, R.drawable.ic_info);

        final int color;
        final int fondo;
        final int icono;

        Tipo(@ColorRes int color, @ColorRes int fondo, @DrawableRes int icono) {
            this.color = color;
            this.fondo = fondo;
            this.icono = icono;
        }
    }

    private Alertas() {
    }

    public static void mostrar(Context contexto, Tipo tipo, String titulo, String mensaje) {
        mostrar(contexto, tipo, titulo, mensaje, null);
    }

    /** Dialogo con un solo boton. alCerrar se ejecuta al cerrarlo (puede ser null). */
    public static void mostrar(Context contexto, Tipo tipo, String titulo, String mensaje, Runnable alCerrar) {
        AlertDialog dialogo = crearDialogo(contexto, tipo, tipo.icono, titulo, mensaje,
                contexto.getString(R.string.aceptar), null, null);
        if (alCerrar != null) {
            dialogo.setOnDismissListener(d -> alCerrar.run());
        }
        dialogo.show();
    }

    /** Dialogo de confirmacion. alAceptar solo se ejecuta si el usuario confirma. */
    public static void confirmar(Context contexto, Tipo tipo, @DrawableRes int icono, String titulo,
                                 String mensaje, String textoAceptar, String textoCancelar, Runnable alAceptar) {
        crearDialogo(contexto, tipo, icono, titulo, mensaje, textoAceptar, textoCancelar, alAceptar).show();
    }

    /** Pide el nombre de un pais nuevo. El dialogo no se cierra si el campo esta vacio. */
    public static void pedirPais(Context contexto, Consumer<String> alGuardar) {
        View vista = LayoutInflater.from(contexto).inflate(R.layout.dialogo_pais, null);
        pintarIcono(vista, Tipo.INFO, R.drawable.ic_mundo);

        TextInputLayout campoPais = vista.findViewById(R.id.campoNuevoPais);
        EditText txtPais = vista.findViewById(R.id.txtNuevoPais);
        Utilidades.limpiarErrorAlEscribir(campoPais);

        AlertDialog dialogo = new MaterialAlertDialogBuilder(contexto).setView(vista).create();

        vista.findViewById(R.id.btnCancelar).setOnClickListener(v -> dialogo.dismiss());
        vista.findViewById(R.id.btnAceptar).setOnClickListener(v -> {
            String nombrePais = txtPais.getText().toString().trim();
            if (nombrePais.isEmpty()) {
                campoPais.setError("Escribe el nombre del país");
                return;
            }
            dialogo.dismiss();
            alGuardar.accept(nombrePais);
        });

        if (dialogo.getWindow() != null) {
            dialogo.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }
        txtPais.requestFocus();
        dialogo.show();
    }

    /** Muestra la foto de un contacto en grande. */
    public static void verFoto(Context contexto, String titulo, String rutaFoto) {
        View vista = LayoutInflater.from(contexto).inflate(R.layout.dialogo_foto, null);
        ((TextView) vista.findViewById(R.id.txtTitulo)).setText(titulo);
        ((ImageView) vista.findViewById(R.id.imgFotoGrande)).setImageURI(Uri.parse(rutaFoto));

        AlertDialog dialogo = new MaterialAlertDialogBuilder(contexto).setView(vista).create();
        vista.findViewById(R.id.btnAceptar).setOnClickListener(v -> dialogo.dismiss());
        dialogo.show();
    }

    public static void aviso(Activity actividad, Tipo tipo, String mensaje) {
        aviso(actividad, tipo, mensaje, null);
    }

    /** Mensaje corto con color e icono. ancla: vista sobre la que aparece (ej. un boton flotante). */
    public static void aviso(Activity actividad, Tipo tipo, String mensaje, View ancla) {
        View raiz = actividad.findViewById(android.R.id.content);
        Snackbar barra = Snackbar.make(raiz, mensaje, Snackbar.LENGTH_LONG);
        barra.setBackgroundTint(color(actividad, tipo.fondo));
        barra.setTextColor(MaterialColors.getColor(raiz, com.google.android.material.R.attr.colorOnSurface));

        TextView texto = barra.getView().findViewById(com.google.android.material.R.id.snackbar_text);
        Drawable icono = AppCompatResources.getDrawable(actividad, tipo.icono);
        if (icono != null) {
            icono = icono.mutate();
            icono.setTint(color(actividad, tipo.color));
            texto.setCompoundDrawablesRelativeWithIntrinsicBounds(icono, null, null, null);
            texto.setCompoundDrawablePadding(Utilidades.dp(actividad, 12));
        }

        if (ancla != null) {
            barra.setAnchorView(ancla);
        }
        barra.show();
    }

    private static AlertDialog crearDialogo(Context contexto, Tipo tipo, @DrawableRes int icono,
                                            String titulo, String mensaje, String textoAceptar,
                                            String textoCancelar, Runnable alAceptar) {
        View vista = LayoutInflater.from(contexto).inflate(R.layout.dialogo_alerta, null);
        pintarIcono(vista, tipo, icono);
        ((TextView) vista.findViewById(R.id.txtTitulo)).setText(titulo);
        ((TextView) vista.findViewById(R.id.txtMensaje)).setText(mensaje);

        AlertDialog dialogo = new MaterialAlertDialogBuilder(contexto).setView(vista).create();

        Button btnAceptar = vista.findViewById(R.id.btnAceptar);
        btnAceptar.setText(textoAceptar);
        btnAceptar.setBackgroundTintList(ColorStateList.valueOf(color(contexto, tipo.color)));
        btnAceptar.setTextColor(color(contexto, R.color.sobre_alerta));
        btnAceptar.setOnClickListener(v -> {
            dialogo.dismiss();
            if (alAceptar != null) {
                alAceptar.run();
            }
        });

        Button btnCancelar = vista.findViewById(R.id.btnCancelar);
        if (textoCancelar == null) {
            btnCancelar.setVisibility(View.GONE);
        } else {
            btnCancelar.setText(textoCancelar);
            btnCancelar.setOnClickListener(v -> dialogo.dismiss());
        }
        return dialogo;
    }

    /** Pinta el circulo superior del dialogo con los colores del tipo de alerta. */
    private static void pintarIcono(View vista, Tipo tipo, @DrawableRes int icono) {
        Context contexto = vista.getContext();
        View contenedor = vista.findViewById(R.id.contenedorIcono);
        contenedor.setBackgroundTintList(ColorStateList.valueOf(color(contexto, tipo.fondo)));

        ImageView imgIcono = vista.findViewById(R.id.imgIcono);
        imgIcono.setImageResource(icono);
        ImageViewCompat.setImageTintList(imgIcono, ColorStateList.valueOf(color(contexto, tipo.color)));
    }

    private static int color(Context contexto, @ColorRes int recurso) {
        return ContextCompat.getColor(contexto, recurso);
    }
}

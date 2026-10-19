package com.example.pm1ea0471.util;

import android.content.Context;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;

import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.ImageViewCompat;

import com.example.pm1ea0471.R;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public final class Utilidades {

    private Utilidades() {
    }

    /** Evita que el contenido quede debajo de la barra de estado, la de navegacion o el teclado. */
    public static void ajustarBordes(View raiz) {
        ViewCompat.setOnApplyWindowInsetsListener(raiz, (vista, margenes) -> {
            Insets barras = margenes.getInsets(
                    WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime());
            vista.setPadding(barras.left, barras.top, barras.right, barras.bottom);
            return WindowInsetsCompat.CONSUMED;
        });
    }

    /** Muestra la foto del contacto, o una silueta si no tiene o ya no se puede leer. */
    public static void cargarFoto(ImageView imagen, String rutaFoto) {
        if (fotoAccesible(imagen.getContext(), rutaFoto)) {
            ImageViewCompat.setImageTintList(imagen, null);
            imagen.setPadding(0, 0, 0, 0);
            imagen.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imagen.setImageURI(Uri.parse(rutaFoto));
            return;
        }
        mostrarSilueta(imagen);
    }

    /** Comprueba que la app todavia puede leer la foto (las URI de galeria viejas pueden fallar). */
    public static boolean fotoAccesible(Context contexto, String rutaFoto) {
        if (rutaFoto == null || rutaFoto.isEmpty()) {
            return false;
        }
        try (InputStream entrada = contexto.getContentResolver().openInputStream(Uri.parse(rutaFoto))) {
            return entrada != null;
        } catch (Exception e) {
            return false;
        }
    }

    /** Copia la foto elegida de la galeria a la carpeta de la app, donde siempre se podra leer. */
    public static String copiarFoto(Context contexto, Uri origen) {
        try {
            File carpeta = contexto.getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES);
            File destino = File.createTempFile("GAL_", ".jpg", carpeta);
            try (InputStream entrada = contexto.getContentResolver().openInputStream(origen);
                 OutputStream salida = new FileOutputStream(destino)) {
                if (entrada == null) {
                    return null;
                }
                byte[] buffer = new byte[8192];
                int leidos;
                while ((leidos = entrada.read(buffer)) != -1) {
                    salida.write(buffer, 0, leidos);
                }
            }
            return FileProvider.getUriForFile(contexto, contexto.getPackageName() + ".fileprovider", destino).toString();
        } catch (Exception e) {
            return null;
        }
    }

    private static void mostrarSilueta(ImageView imagen) {
        int colorIcono = MaterialColors.getColor(imagen,
                com.google.android.material.R.attr.colorOnPrimaryContainer);
        int relleno = imagen.getLayoutParams().width / 4;
        imagen.setImageResource(R.drawable.ic_persona);
        ImageViewCompat.setImageTintList(imagen, ColorStateList.valueOf(colorIcono));
        imagen.setPadding(relleno, relleno, relleno, relleno);
        imagen.setScaleType(ImageView.ScaleType.FIT_CENTER);
    }

    /** Quita el mensaje de error del campo en cuanto el usuario vuelve a escribir. */
    public static void limpiarErrorAlEscribir(TextInputLayout campo) {
        if (campo.getEditText() == null) {
            return;
        }
        campo.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence texto, int inicio, int cantidad, int despues) {
            }

            @Override
            public void onTextChanged(CharSequence texto, int inicio, int antes, int cantidad) {
                campo.setError(null);
            }

            @Override
            public void afterTextChanged(Editable texto) {
            }
        });
    }

    public static int dp(Context contexto, int valor) {
        return Math.round(valor * contexto.getResources().getDisplayMetrics().density);
    }
}

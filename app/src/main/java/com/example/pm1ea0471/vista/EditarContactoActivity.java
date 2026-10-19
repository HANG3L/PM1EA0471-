package com.example.pm1ea0471.vista;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.pm1ea0471.R;
import com.example.pm1ea0471.controlador.ContactoControlador;
import com.example.pm1ea0471.controlador.PaisControlador;
import com.example.pm1ea0471.modelo.Contacto;
import com.example.pm1ea0471.util.Alertas;
import com.example.pm1ea0471.util.Utilidades;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/** VISTA: pantalla para actualizar un contacto existente. */
public class EditarContactoActivity extends AppCompatActivity {

    /** Id del contacto que se va a editar. */
    public static final String EXTRA_ID = "id_contacto";

    private ImageView imgFoto;
    private Button btnTomarFoto, btnGaleria, btnAgregarPais, btnGuardar, btnCancelar;
    private AutoCompleteTextView txtPais;
    private TextInputLayout campoNombre, campoTelefono, campoNota;
    private EditText txtNombre, txtTelefono, txtNota;

    private int idContacto;
    private String rutaFoto = "";
    private Uri uriCamara;
    private ContactoControlador contactoControlador;
    private PaisControlador paisControlador;

    private static final int CODIGO_CAMARA = 2;
    private static final int CODIGO_GALERIA = 4;
    private static final int PERMISO_CAMARA = 103;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_editar_contacto);
        Utilidades.ajustarBordes(findViewById(R.id.raiz));

        MaterialToolbar barra = findViewById(R.id.barra);
        barra.setNavigationOnClickListener(v -> finish());

        imgFoto = findViewById(R.id.imgFoto);
        btnTomarFoto = findViewById(R.id.btnTomarFoto);
        btnGaleria = findViewById(R.id.btnGaleria);
        btnAgregarPais = findViewById(R.id.btnAgregarPais);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnCancelar = findViewById(R.id.btnCancelar);
        txtPais = findViewById(R.id.txtPais);
        campoNombre = findViewById(R.id.campoNombre);
        campoTelefono = findViewById(R.id.campoTelefono);
        campoNota = findViewById(R.id.campoNota);
        txtNombre = findViewById(R.id.txtNombre);
        txtTelefono = findViewById(R.id.txtTelefono);
        txtNota = findViewById(R.id.txtNota);

        contactoControlador = new ContactoControlador(this);
        paisControlador = new PaisControlador(this);

        cargarPaises();
        Utilidades.limpiarErrorAlEscribir(campoNombre);
        Utilidades.limpiarErrorAlEscribir(campoTelefono);
        Utilidades.limpiarErrorAlEscribir(campoNota);

        btnTomarFoto.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISO_CAMARA);
            } else {
                abrirCamara();
            }
        });

        btnGaleria.setOnClickListener(v -> abrirGaleria());
        btnAgregarPais.setOnClickListener(v -> mostrarDialogoPais());
        btnGuardar.setOnClickListener(v -> actualizar());
        btnCancelar.setOnClickListener(v -> finish());

        // Datos actuales del contacto
        idContacto = getIntent().getIntExtra(EXTRA_ID, -1);
        Contacto contacto = contactoControlador.obtener(idContacto);
        if (contacto == null) {
            finish();
            return;
        }
        rutaFoto = contacto.getImagen() == null ? "" : contacto.getImagen();
        txtNombre.setText(contacto.getNombre());
        txtTelefono.setText(contacto.getTelefono());
        txtNota.setText(contacto.getNota());
        if (contacto.getPais() != null && !contacto.getPais().isEmpty()) {
            txtPais.setText(contacto.getPais(), false);
        }
        Utilidades.cargarFoto(imgFoto, rutaFoto);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISO_CAMARA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                abrirCamara();
            } else {
                Alertas.mostrar(this, Alertas.Tipo.ADVERTENCIA, "Permiso denegado",
                        "Sin acceso a la cámara no es posible tomar fotos. Puedes elegir una imagen de la galería.");
            }
        }
    }

    private void cargarPaises() {
        List<String> paises = paisControlador.listar();
        txtPais.setAdapter(new ArrayAdapter<>(this, R.layout.item_pais, paises));
    }

    private void mostrarDialogoPais() {
        Alertas.pedirPais(this, nombrePais -> {
            if (paisControlador.agregar(nombrePais)) {
                cargarPaises();
                txtPais.setText(nombrePais, false);
                Alertas.aviso(this, Alertas.Tipo.EXITO, "País agregado: " + nombrePais);
            } else {
                Alertas.mostrar(this, Alertas.Tipo.ADVERTENCIA, "País repetido",
                        "«" + nombrePais + "» ya está registrado en la lista.");
            }
        });
    }

    private void abrirCamara() {
        Intent intentCamara = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intentCamara.resolveActivity(getPackageManager()) == null) {
            Alertas.mostrar(this, Alertas.Tipo.INFO, "Cámara no disponible",
                    "Este dispositivo no tiene una cámara disponible. Usa el botón «Galería».");
            return;
        }

        File archivoFoto;
        try {
            archivoFoto = crearArchivoImagen();
        } catch (IOException ex) {
            Alertas.mostrar(this, Alertas.Tipo.ERROR, "Error",
                    "No se pudo crear el archivo para la foto. Intenta de nuevo.");
            return;
        }

        uriCamara = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", archivoFoto);
        intentCamara.putExtra(MediaStore.EXTRA_OUTPUT, uriCamara);
        intentCamara.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intentCamara, CODIGO_CAMARA);
    }

    private void abrirGaleria() {
        Intent intentGaleria = new Intent(Intent.ACTION_PICK);
        intentGaleria.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intentGaleria, CODIGO_GALERIA);
    }

    private File crearArchivoImagen() throws IOException {
        String fecha = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File carpeta = getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES);
        return File.createTempFile("JPEG_" + fecha + "_", ".jpg", carpeta);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == CODIGO_CAMARA && uriCamara != null) {
            rutaFoto = uriCamara.toString();
            Utilidades.cargarFoto(imgFoto, rutaFoto);
        } else if (requestCode == CODIGO_GALERIA && data != null && data.getData() != null) {
            String copia = Utilidades.copiarFoto(this, data.getData());
            if (copia == null) {
                Alertas.mostrar(this, Alertas.Tipo.ERROR, "Error",
                        "No se pudo cargar la imagen seleccionada. Prueba con otra.");
                return;
            }
            rutaFoto = copia;
            Utilidades.cargarFoto(imgFoto, rutaFoto);
        }
    }

    private void actualizar() {
        String pais = txtPais.getText().toString().trim();
        String nombre = txtNombre.getText().toString().trim();
        String telefono = txtTelefono.getText().toString().trim();
        String nota = txtNota.getText().toString().trim();

        ContactoControlador.Error error = contactoControlador.validar(nombre, telefono, nota);
        if (error != null) {
            mostrarError(error);
            return;
        }

        if (contactoControlador.actualizar(new Contacto(idContacto, pais, nombre, telefono, nota, rutaFoto))) {
            Alertas.mostrar(this, Alertas.Tipo.EXITO, "Cambios guardados",
                    "Los datos de " + nombre + " se actualizaron correctamente.", this::finish);
        } else {
            Alertas.mostrar(this, Alertas.Tipo.ERROR, "No se pudo actualizar",
                    "Ocurrió un error al actualizar el contacto. Intenta de nuevo.");
        }
    }

    private void mostrarError(ContactoControlador.Error error) {
        switch (error.campo) {
            case NOMBRE:
                campoNombre.setError(error.errorCampo);
                break;
            case TELEFONO:
                campoTelefono.setError(error.errorCampo);
                break;
            case NOTA:
                campoNota.setError(error.errorCampo);
                break;
        }
        Alertas.mostrar(this, error.titulo.startsWith("Falta") ? Alertas.Tipo.ADVERTENCIA : Alertas.Tipo.ERROR,
                error.titulo, error.mensaje);
    }
}

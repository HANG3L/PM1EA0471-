package com.example.pm1ea0471.vista;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.pm1ea0471.R;
import com.example.pm1ea0471.controlador.ContactoControlador;
import com.example.pm1ea0471.modelo.Contacto;
import com.example.pm1ea0471.util.Alertas;
import com.example.pm1ea0471.util.Utilidades;
import com.google.android.material.appbar.MaterialToolbar;

/** VISTA: pantalla de llamada. Pide el permiso CALL_PHONE y marca con ACTION_CALL. */
public class LlamarActivity extends AppCompatActivity {

    /** Id del contacto al que se va a llamar. */
    public static final String EXTRA_ID = "id_contacto";

    private ContactoControlador controlador;
    private Contacto contacto;

    // Se registra antes de que la pantalla este visible, como exige Android
    private final ActivityResultLauncher<String> pedirPermiso =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), concedido -> {
                if (concedido) {
                    marcar();
                } else {
                    Alertas.mostrar(this, Alertas.Tipo.ADVERTENCIA, "Permiso denegado",
                            "Sin el permiso de llamadas no es posible llamar desde la app.");
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_llamar);
        Utilidades.ajustarBordes(findViewById(R.id.raiz));

        controlador = new ContactoControlador(this);
        contacto = controlador.obtener(getIntent().getIntExtra(EXTRA_ID, -1));
        if (contacto == null) {
            finish();
            return;
        }

        MaterialToolbar barra = findViewById(R.id.barra);
        barra.setNavigationOnClickListener(v -> finish());

        ((TextView) findViewById(R.id.txtNombre)).setText(contacto.getNombre());
        ((TextView) findViewById(R.id.txtTelefono)).setText(controlador.telefonoParaMostrar(contacto));
        ((TextView) findViewById(R.id.txtPais)).setText(contacto.getPais());
        Utilidades.cargarFoto((ImageView) findViewById(R.id.imgFoto), contacto.getImagen());

        findViewById(R.id.btnLlamar).setOnClickListener(v -> llamar());
    }

    private void llamar() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            marcar();
        } else {
            pedirPermiso.launch(Manifest.permission.CALL_PHONE);
        }
    }

    private void marcar() {
        Intent llamar = new Intent(Intent.ACTION_CALL);
        llamar.setData(Uri.parse("tel:" + controlador.telefonoParaMarcar(contacto)));
        startActivity(llamar);
    }
}

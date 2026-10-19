package com.example.pm1ea0471.vista;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pm1ea0471.R;
import com.example.pm1ea0471.controlador.ContactoControlador;
import com.example.pm1ea0471.modelo.Contacto;
import com.example.pm1ea0471.util.Alertas;
import com.example.pm1ea0471.util.Utilidades;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/** VISTA: lista de contactos con buscador. */
public class ListaContactosActivity extends AppCompatActivity implements AdaptadorContactos.AccionesContacto {

    private MaterialToolbar barra;
    private RecyclerView listaContactos;
    private EditText txtBuscar;
    private View vistaVacia;
    private TextView txtVacioTitulo, txtVacioAyuda;
    private ExtendedFloatingActionButton btnNuevo;
    private ContactoControlador controlador;
    private AdaptadorContactos adaptador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lista_contactos);
        Utilidades.ajustarBordes(findViewById(R.id.raiz));

        barra = findViewById(R.id.barra);
        listaContactos = findViewById(R.id.listaContactos);
        txtBuscar = findViewById(R.id.txtBuscar);
        vistaVacia = findViewById(R.id.vistaVacia);
        txtVacioTitulo = findViewById(R.id.txtVacioTitulo);
        txtVacioAyuda = findViewById(R.id.txtVacioAyuda);
        btnNuevo = findViewById(R.id.btnNuevo);
        controlador = new ContactoControlador(this);

        adaptador = new AdaptadorContactos(this, new ArrayList<>(), this);
        listaContactos.setLayoutManager(new LinearLayoutManager(this));
        listaContactos.setAdapter(adaptador);

        barra.setNavigationOnClickListener(v -> finish());
        btnNuevo.setOnClickListener(v -> finish());

        txtBuscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence texto, int inicio, int cantidad, int despues) {
            }

            @Override
            public void onTextChanged(CharSequence texto, int inicio, int antes, int cantidad) {
                cargarContactos();
            }

            @Override
            public void afterTextChanged(Editable texto) {
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarContactos();
    }

    private void cargarContactos() {
        String busqueda = txtBuscar.getText().toString();
        List<Contacto> contactos = controlador.buscar(busqueda);
        adaptador.actualizar(contactos);

        int total = contactos.size();
        boolean buscando = !busqueda.trim().isEmpty();
        vistaVacia.setVisibility(total == 0 ? View.VISIBLE : View.GONE);
        txtVacioTitulo.setText(buscando ? R.string.sin_resultados : R.string.lista_vacia);
        txtVacioAyuda.setText(buscando ? R.string.sin_resultados_ayuda : R.string.lista_vacia_ayuda);
        barra.setSubtitle(total == 1 ? "1 contacto" : total + " contactos");
    }

    /** Pregunta antes de abrir la pantalla de llamada. */
    @Override
    public void alLlamar(Contacto contacto) {
        Alertas.confirmar(this, Alertas.Tipo.INFO, R.drawable.ic_llamar, "Acción",
                "¿Desea llamar a " + contacto.getNombre() + "?", "Sí", "No", () -> {
                    Intent intent = new Intent(this, LlamarActivity.class);
                    intent.putExtra(LlamarActivity.EXTRA_ID, contacto.getId());
                    startActivity(intent);
                });
    }

    @Override
    public void alEliminar(Contacto contacto) {
        Alertas.confirmar(this, Alertas.Tipo.ERROR, R.drawable.ic_eliminar, "Eliminar contacto",
                "¿Seguro que deseas eliminar a " + contacto.getNombre() + "? Esta acción no se puede deshacer.",
                "Eliminar", getString(R.string.cancelar), () -> {
                    controlador.eliminar(contacto.getId());
                    cargarContactos();
                    Alertas.aviso(this, Alertas.Tipo.EXITO, contacto.getNombre() + " fue eliminado", btnNuevo);
                });
    }
}

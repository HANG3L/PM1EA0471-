package com.example.pm1ea0471.vista;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pm1ea0471.R;
import com.example.pm1ea0471.controlador.ContactoControlador;
import com.example.pm1ea0471.modelo.Contacto;
import com.example.pm1ea0471.util.Alertas;
import com.example.pm1ea0471.util.Utilidades;

import java.util.List;

/** VISTA: dibuja cada contacto de la lista. */
public class AdaptadorContactos extends RecyclerView.Adapter<AdaptadorContactos.VistaContacto> {

    /** Acciones que resuelve la pantalla de la lista (necesitan confirmacion o recargar datos). */
    public interface AccionesContacto {
        void alLlamar(Contacto contacto);

        void alEliminar(Contacto contacto);
    }

    private final Context contexto;
    private final List<Contacto> contactos;
    private final AccionesContacto acciones;
    private final ContactoControlador controlador;

    public AdaptadorContactos(Context contexto, List<Contacto> contactos, AccionesContacto acciones) {
        this.contexto = contexto;
        this.contactos = contactos;
        this.acciones = acciones;
        this.controlador = new ContactoControlador(contexto);
    }

    public void actualizar(List<Contacto> nuevosContactos) {
        contactos.clear();
        contactos.addAll(nuevosContactos);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VistaContacto onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(contexto).inflate(R.layout.item_contacto, parent, false);
        return new VistaContacto(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull VistaContacto vista, int posicion) {
        Contacto contacto = contactos.get(posicion);

        vista.txtNombre.setText(contacto.getNombre());
        vista.txtTelefono.setText(controlador.telefonoParaMostrar(contacto));
        mostrarSiTieneTexto(vista.txtPais, contacto.getPais());
        mostrarSiTieneTexto(vista.txtNota, contacto.getNota());
        Utilidades.cargarFoto(vista.imgFoto, contacto.getImagen());

        vista.itemView.setOnClickListener(v -> acciones.alLlamar(contacto));
        vista.btnLlamar.setOnClickListener(v -> acciones.alLlamar(contacto));
        vista.btnCompartir.setOnClickListener(v -> compartir(contacto));
        vista.btnVerFoto.setOnClickListener(v -> verFoto(contacto));
        vista.btnEditar.setOnClickListener(v -> editar(contacto));
        vista.btnEliminar.setOnClickListener(v -> acciones.alEliminar(contacto));
    }

    @Override
    public int getItemCount() {
        return contactos.size();
    }

    private void compartir(Contacto contacto) {
        String texto = "Contacto:\nNombre: " + contacto.getNombre() +
                "\nTeléfono: " + controlador.telefonoParaMostrar(contacto) +
                "\nPaís: " + contacto.getPais() +
                "\nNota: " + (contacto.getNota() != null ? contacto.getNota() : "");

        Intent intentCompartir = new Intent(Intent.ACTION_SEND);
        intentCompartir.setType("text/plain");
        intentCompartir.putExtra(Intent.EXTRA_TEXT, texto);
        contexto.startActivity(Intent.createChooser(intentCompartir, "Compartir contacto vía"));
    }

    private void verFoto(Contacto contacto) {
        if (Utilidades.fotoAccesible(contexto, contacto.getImagen())) {
            Alertas.verFoto(contexto, contacto.getNombre(), contacto.getImagen());
        } else {
            Alertas.mostrar(contexto, Alertas.Tipo.INFO, "Sin fotografía",
                    contacto.getNombre() + " no tiene una foto registrada. Puedes agregarla desde «Editar».");
        }
    }

    private void editar(Contacto contacto) {
        Intent intent = new Intent(contexto, EditarContactoActivity.class);
        intent.putExtra(EditarContactoActivity.EXTRA_ID, contacto.getId());
        contexto.startActivity(intent);
    }

    private static void mostrarSiTieneTexto(TextView vista, String texto) {
        boolean tieneTexto = texto != null && !texto.isEmpty();
        vista.setVisibility(tieneTexto ? View.VISIBLE : View.GONE);
        vista.setText(tieneTexto ? texto : "");
    }

    public static class VistaContacto extends RecyclerView.ViewHolder {
        ImageView imgFoto;
        TextView txtNombre, txtTelefono, txtPais, txtNota;
        Button btnLlamar, btnCompartir, btnVerFoto, btnEditar, btnEliminar;

        public VistaContacto(@NonNull View itemView) {
            super(itemView);
            imgFoto = itemView.findViewById(R.id.imgFoto);
            txtNombre = itemView.findViewById(R.id.txtNombre);
            txtTelefono = itemView.findViewById(R.id.txtTelefono);
            txtPais = itemView.findViewById(R.id.txtPais);
            txtNota = itemView.findViewById(R.id.txtNota);
            btnLlamar = itemView.findViewById(R.id.btnLlamar);
            btnCompartir = itemView.findViewById(R.id.btnCompartir);
            btnVerFoto = itemView.findViewById(R.id.btnVerFoto);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }
    }
}

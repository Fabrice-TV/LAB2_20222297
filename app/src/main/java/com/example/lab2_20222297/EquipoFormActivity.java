package com.example.lab2_20222297;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lab2_20222297.databinding.ActivityEquipoFormBinding;
import com.example.lab2_20222297.model.Equipo;

public class EquipoFormActivity extends AppCompatActivity {

    private ActivityEquipoFormBinding binding;
    private Equipo equipoEditar;
    private int posicionEditar = -1;
    private boolean modoEdicion = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEquipoFormBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        configurarSpinnerTipo();

        Intent intent = getIntent();
        if (intent.hasExtra(MainActivity.EXTRA_EQUIPO)) {
            modoEdicion = true;
            equipoEditar = (Equipo) intent.getSerializableExtra(MainActivity.EXTRA_EQUIPO);
            posicionEditar = intent.getIntExtra(MainActivity.EXTRA_POSITION, -1);
            setTitle(R.string.title_editar);
            cargarDatosEquipo(equipoEditar);

            // Código y Tipo no modificables en edición
            binding.etCodigo.setEnabled(false);
            binding.etCodigo.setFocusable(false);
            binding.spinnerTipo.setEnabled(false);
            binding.spinnerTipo.setFocusable(false);
        } else {
            setTitle(R.string.title_agregar);
        }

        binding.btnGuardar.setOnClickListener(v -> validarYConfirmar());
    }

    private void configurarSpinnerTipo() {
        String[] tipos = getResources().getStringArray(R.array.tipos_equipo);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, tipos);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerTipo.setAdapter(adapter);
    }

    private void cargarDatosEquipo(Equipo equipo) {
        binding.etCodigo.setText(equipo.getCodigo());
        binding.etNombre.setText(equipo.getNombre());

        String[] tipos = getResources().getStringArray(R.array.tipos_equipo);
        for (int i = 0; i < tipos.length; i++) {
            if (tipos[i].equals(equipo.getTipo())) {
                binding.spinnerTipo.setSelection(i);
                break;
            }
        }

        switch (equipo.getEstado()) {
            case "Operativo":
                binding.radioOperativo.setChecked(true);
                break;
            case "En mantenimiento":
                binding.radioMantenimiento.setChecked(true);
                break;
            case "Fuera de servicio":
                binding.radioFueraServicio.setChecked(true);
                break;
        }

        binding.etObservaciones.setText(equipo.getObservaciones());
    }

    private void validarYConfirmar() {
        String codigo = binding.etCodigo.getText().toString().trim();
        String nombre = binding.etNombre.getText().toString().trim();

        if (codigo.isEmpty()) {
            binding.etCodigo.setError(getString(R.string.error_campo_requerido));
            return;
        }
        if (nombre.isEmpty()) {
            binding.etNombre.setError(getString(R.string.error_campo_requerido));
            return;
        }
        if (binding.radioGroupEstado.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, R.string.error_seleccione_estado, Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setMessage("¿Está seguro que desea registrar?")
                .setPositiveButton("ACEPTAR", (dialog, which) -> guardarEquipo())
                .setNegativeButton("CANCELAR", null)
                .show();
    }

    private void guardarEquipo() {
        String codigo = binding.etCodigo.getText().toString().trim();
        String nombre = binding.etNombre.getText().toString().trim();
        String tipo = binding.spinnerTipo.getSelectedItem().toString();
        String observaciones = binding.etObservaciones.getText().toString().trim();

        int radioSeleccionado = binding.radioGroupEstado.getCheckedRadioButtonId();
        String estado;
        if (radioSeleccionado == R.id.radioOperativo) {
            estado = getString(R.string.estado_operativo);
        } else if (radioSeleccionado == R.id.radioMantenimiento) {
            estado = getString(R.string.estado_mantenimiento);
        } else {
            estado = getString(R.string.estado_fuera_servicio);
        }

        Equipo equipo = new Equipo(codigo, nombre, tipo, estado, observaciones);

        Intent result = new Intent();
        result.putExtra(MainActivity.EXTRA_EQUIPO, equipo);
        if (posicionEditar >= 0) {
            result.putExtra(MainActivity.EXTRA_POSITION, posicionEditar);
        }
        setResult(RESULT_OK, result);
        finish();
    }
}

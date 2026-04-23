package com.example.lab2_20222297;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lab2_20222297.adapter.EquipoAdapter;
import com.example.lab2_20222297.databinding.ActivityMainBinding;
import com.example.lab2_20222297.model.Equipo;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_EQUIPO = "equipo";
    public static final String EXTRA_POSITION = "position";
    public static final int REQUEST_ADD = 1;
    public static final int REQUEST_EDIT = 2;

    private ActivityMainBinding binding;
    private EquipoAdapter adapter;
    private ActionMode activeActionMode;

    private final List<Equipo> listaCompleta = new ArrayList<>();
    private List<Equipo> listaFiltrada = new ArrayList<>();

    private String filtroTipo = "";
    private String filtroEstado = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        configurarSpinners();

        adapter = new EquipoAdapter(this, listaFiltrada);
        binding.listView.setAdapter(adapter);

        // Long press → Context Action Bar (Editar / Eliminar)
        binding.listView.setOnItemLongClickListener((parent, view, position, id) -> {
            if (activeActionMode != null) return false;

            Equipo equipo = listaFiltrada.get(position);

            activeActionMode = startActionMode(new ActionMode.Callback() {
                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    mode.getMenuInflater().inflate(R.menu.context_menu, menu);
                    mode.setTitle(equipo.getNombre());
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    int itemId = item.getItemId();
                    if (itemId == R.id.action_edit) {
                        editarEquipo(equipo);
                        mode.finish();
                        return true;
                    } else if (itemId == R.id.action_delete) {
                        confirmarEliminar(equipo);
                        mode.finish();
                        return true;
                    }
                    return false;
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {
                    activeActionMode = null;
                }
            });
            return true;
        });

        binding.fab.setOnClickListener(v -> {
            Intent intent = new Intent(this, EquipoFormActivity.class);
            startActivityForResult(intent, REQUEST_ADD);
        });

        cargarDatosDePrueba();
        aplicarFiltros();
    }

    private void cargarDatosDePrueba() {
        listaCompleta.add(new Equipo("EQ-001", "Multímetro Fluke 87V", "Multímetro", "Operativo", "Calibrado"));
        listaCompleta.add(new Equipo("EQ-002", "OTDR Yokogawa AQ7280", "OTDR", "En mantenimiento", "En revisión"));
        listaCompleta.add(new Equipo("EQ-003", "Medidor óptico", "Medidor de potencia", "Fuera de servicio", "Dañado"));
    }

    private void configurarSpinners() {
        // Spinner Tipo
        String[] tiposArray = getResources().getStringArray(R.array.tipos_equipo);
        String[] tiposConTodos = new String[tiposArray.length + 1];
        tiposConTodos[0] = getString(R.string.spinner_todos_tipos);
        System.arraycopy(tiposArray, 0, tiposConTodos, 1, tiposArray.length);

        ArrayAdapter<String> adapterTipo = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, tiposConTodos);
        adapterTipo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerTipo.setAdapter(adapterTipo);

        // Spinner Estado
        String[] estados = {
                getString(R.string.spinner_todos_estados),
                getString(R.string.estado_operativo),
                getString(R.string.estado_mantenimiento),
                getString(R.string.estado_fuera_servicio)
        };
        ArrayAdapter<String> adapterEstado = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, estados);
        adapterEstado.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerEstado.setAdapter(adapterEstado);

        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int pos, long id) {
                String sel = parent.getSelectedItem().toString();
                if (parent.getId() == R.id.spinnerTipo) {
                    filtroTipo = pos == 0 ? "" : sel;
                } else {
                    filtroEstado = pos == 0 ? "" : sel;
                }
                aplicarFiltros();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };

        binding.spinnerTipo.setOnItemSelectedListener(listener);
        binding.spinnerEstado.setOnItemSelectedListener(listener);
    }

    private void aplicarFiltros() {
        listaFiltrada.clear();
        for (Equipo e : listaCompleta) {
            boolean matchTipo = filtroTipo.isEmpty() || e.getTipo().equals(filtroTipo);
            boolean matchEstado = filtroEstado.isEmpty() || e.getEstado().equals(filtroEstado);
            if (matchTipo && matchEstado) {
                listaFiltrada.add(e);
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
            binding.spinnerTipo.setSelection(0);
            binding.spinnerEstado.setSelection(0);
            filtroTipo = "";
            filtroEstado = "";
            aplicarFiltros();
            Toast.makeText(this, "Lista actualizada", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void editarEquipo(Equipo equipo) {
        int realPos = listaCompleta.indexOf(equipo);
        Intent intent = new Intent(this, EquipoFormActivity.class);
        intent.putExtra(EXTRA_EQUIPO, equipo);
        intent.putExtra(EXTRA_POSITION, realPos);
        startActivityForResult(intent, REQUEST_EDIT);
    }

    private void confirmarEliminar(Equipo equipo) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_eliminar_titulo)
                .setMessage(R.string.dialog_eliminar_mensaje)
                .setPositiveButton(R.string.dialog_btn_si, (dialog, which) -> {
                    listaCompleta.remove(equipo);
                    aplicarFiltros();
                    Toast.makeText(this, R.string.msg_equipo_eliminado, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(R.string.dialog_btn_no, null)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) return;

        Equipo equipo = (Equipo) data.getSerializableExtra(EXTRA_EQUIPO);
        if (equipo == null) return;

        if (requestCode == REQUEST_ADD) {
            listaCompleta.add(equipo);
            Toast.makeText(this, R.string.msg_equipo_guardado, Toast.LENGTH_SHORT).show();
        } else if (requestCode == REQUEST_EDIT) {
            int pos = data.getIntExtra(EXTRA_POSITION, -1);
            if (pos >= 0 && pos < listaCompleta.size()) {
                listaCompleta.set(pos, equipo);
                Toast.makeText(this, R.string.msg_equipo_guardado, Toast.LENGTH_SHORT).show();
            }
        }
        aplicarFiltros();
    }
}

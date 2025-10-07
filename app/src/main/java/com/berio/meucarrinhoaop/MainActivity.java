package com.berio.meucarrinhoaop;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Item> listaCompras;
    private ListView listView;
    private EditText searchField;
    private TextView totalText;
    private Button btnLimpar;
    private ItemAdapter adapter;
    private final String PREFS_NAME = "app_compras";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listaCompras = new ArrayList<>();

        listView = findViewById(R.id.listView);
        searchField = findViewById(R.id.searchField);
        totalText = findViewById(R.id.totalText);
        btnLimpar = findViewById(R.id.btnLimpar);

        carregarLista(); // Carrega lista ao iniciar
        atualizarLista();

        findViewById(R.id.btnAdicionar).setOnClickListener(v -> mostrarDialogAdicionar());
        btnLimpar.setOnClickListener(v -> confirmarLimparLista());

        searchField.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                buscarItens(s.toString());
            }
        });

        listView.setOnItemClickListener((parent, view, position, id) -> mostrarDialogEditar(position));
        listView.setOnItemLongClickListener((adapterView, view, position, id) -> {
            confirmarRemoverItem(position);
            return true;
        });
    }

    // --- Adicionar item com validação ---
    private void mostrarDialogAdicionar() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_adicionar_item, null);
        builder.setView(view);

        EditText etNome = view.findViewById(R.id.etNome);
        EditText etValor = view.findViewById(R.id.etValor);
        EditText etQuantidade = view.findViewById(R.id.etQuantidade);

        builder.setTitle("Adicionar Item");
        builder.setNegativeButton("Cancelar", null);
        builder.setPositiveButton("Adicionar", null);

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String nome = etNome.getText().toString().trim();
            String valorStr = etValor.getText().toString().trim();
            String qtdStr = etQuantidade.getText().toString().trim();

            if(nome.isEmpty() || valorStr.isEmpty() || qtdStr.isEmpty()){
                Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
                return;
            }

            double valor;
            int qtd;
            try {
                valor = Double.parseDouble(valorStr);
                qtd = Integer.parseInt(qtdStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Valor ou quantidade inválida!", Toast.LENGTH_SHORT).show();
                return;
            }

            listaCompras.add(new Item(nome, valor, qtd));
            ordenarPorNome();
            atualizarLista();
            salvarLista(); // salva sempre que adicionar
            dialog.dismiss();
        });
    }

    // --- Editar item com validação ---
    private void mostrarDialogEditar(int index) {
        Item item = listaCompras.get(index);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_adicionar_item, null);
        builder.setView(view);

        EditText etNome = view.findViewById(R.id.etNome);
        EditText etValor = view.findViewById(R.id.etValor);
        EditText etQuantidade = view.findViewById(R.id.etQuantidade);

        etNome.setText(item.getNome());
        etValor.setText(String.valueOf(item.getValorUnitario()));
        etQuantidade.setText(String.valueOf(item.getQuantidade()));

        builder.setTitle("Editar Item");
        builder.setNegativeButton("Cancelar", null);
        builder.setPositiveButton("Salvar", null);

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String nome = etNome.getText().toString().trim();
            String valorStr = etValor.getText().toString().trim();
            String qtdStr = etQuantidade.getText().toString().trim();

            if(nome.isEmpty() || valorStr.isEmpty() || qtdStr.isEmpty()){
                Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
                return;
            }

            double valor;
            int qtd;
            try {
                valor = Double.parseDouble(valorStr);
                qtd = Integer.parseInt(qtdStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Valor ou quantidade inválida!", Toast.LENGTH_SHORT).show();
                return;
            }

            item.setNome(nome);
            item.setValorUnitario(valor);
            item.setQuantidade(qtd);
            ordenarPorNome();
            atualizarLista();
            salvarLista(); // salva sempre que editar
            dialog.dismiss();
        });
    }

    // --- Remover item ---
    private void confirmarRemoverItem(int index) {
        new AlertDialog.Builder(this)
            .setTitle("Remover")
            .setMessage("Deseja realmente remover este item?")
            .setPositiveButton("Sim", (dialog, which) -> {
                listaCompras.remove(index);
                atualizarLista();
                salvarLista(); // salva sempre que remover
            })
            .setNegativeButton("Cancelar", null)
            .show();
    }

    // --- Limpar lista ---
    private void confirmarLimparLista() {
        new AlertDialog.Builder(this)
            .setTitle("Limpar Lista")
            .setMessage("Tem certeza que deseja apagar todos os itens?")
            .setPositiveButton("Sim", (dialog, which) -> {
                listaCompras.clear();
                atualizarLista();
                salvarLista(); // salva lista vazia
            })
            .setNegativeButton("Cancelar", null)
            .show();
    }

    // --- Buscar itens ---
    private void buscarItens(String termo) {
        ArrayList<Item> resultados = new ArrayList<>();
        for (Item i : listaCompras) {
            if (i.getNome().toLowerCase().contains(termo.toLowerCase())) {
                resultados.add(i);
            }
        }
        adapter = new ItemAdapter(this, resultados);
        listView.setAdapter(adapter);
    }

    // --- Atualizar lista e total ---
    private void atualizarLista() {
        adapter = new ItemAdapter(this, listaCompras);
        listView.setAdapter(adapter);
        totalText.setText("Total: R$ " + String.format("%.2f", calcularTotalGeral()));
    }

    // --- Calcular total geral ---
    private double calcularTotalGeral() {
        double total = 0;
        for (Item i : listaCompras) {
            total += i.getTotal();
        }
        return total;
    }

    // --- Ordenar por nome ---
    private void ordenarPorNome() {
        Collections.sort(listaCompras, Comparator.comparing(Item::getNome, String.CASE_INSENSITIVE_ORDER));
    }

    // --- Persistência: salvar lista ---
    private void salvarLista() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        JSONArray array = new JSONArray();
        for (Item item : listaCompras) {
            JSONObject obj = new JSONObject();
            try {
                obj.put("nome", item.getNome());
                obj.put("valor", item.getValorUnitario());
                obj.put("qtd", item.getQuantidade());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            array.put(obj);
        }

        editor.putString("lista", array.toString());
        editor.apply();
    }

    // --- Persistência: carregar lista ---
    private void carregarLista() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String json = prefs.getString("lista", "[]");

        listaCompras.clear();
        try {
            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                String nome = obj.getString("nome");
                double valor = obj.getDouble("valor");
                int qtd = obj.getInt("qtd");
                listaCompras.add(new Item(nome, valor, qtd));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
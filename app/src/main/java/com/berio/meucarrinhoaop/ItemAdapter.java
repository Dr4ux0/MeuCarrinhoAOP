package com.berio.meucarrinhoaop;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;

public class ItemAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Item> lista;

    // Cores base (rosa, azul, amarelo, verde, vermelho)
    private int[] coresBase = {
            Color.parseColor("#F48FB1"), // rosa
            Color.parseColor("#90CAF9"), // azul
            Color.parseColor("#FFF59D"), // amarelo
            Color.parseColor("#A5D6A7"), // verde
            Color.parseColor("#EF9A9A")  // vermelho
    };

    public ItemAdapter(Context context, ArrayList<Item> lista) {
        this.context = context;
        this.lista = lista;
    }

    @Override
    public int getCount() {
        return lista.size();
    }

    @Override
    public Object getItem(int position) {
        return lista.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private int getCorFundo(int position) {
        // Determina a cor base pelo ciclo de 5 cores
        int grupo = (position / 4) % coresBase.length;
        int corBase = coresBase[grupo];

        // Determina opacidade pelo índice dentro do bloco de 4
        int indice = position % 4;
        int alpha;

        switch (indice) {
            case 0: alpha = 255; break;  // 100%
            case 1: alpha = 179; break;  // 70%
            case 2: alpha = 128; break;  // 50%
            case 3: alpha = 77; break;   // 30%
            default: alpha = 255; break;
        }

        // Combina alpha + cor
        return (alpha << 24) | (corBase & 0x00FFFFFF);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = convertView;
        if (itemView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            itemView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        Item item = lista.get(position);
        TextView text = (TextView) itemView.findViewById(android.R.id.text1);
        text.setText(item.toString());
        text.setBackgroundColor(getCorFundo(position));

        return itemView;
    }
}
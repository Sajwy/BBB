package cz.sajwy.silencer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cz.sajwy.silencer.R;

public class KategoriePravidelAdapter extends ArrayAdapter<String> {

    public KategoriePravidelAdapter(Context context, List<String> kategorie) {
        super(context, R.layout.lv_kategorie_pravidel_radek, kategorie);
    }

    private static class ViewHolder {
        TextView tvKategorie;
        ImageView ivKategorie;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inf = (LayoutInflater) getContext().getSystemService(getContext().LAYOUT_INFLATER_SERVICE);
            convertView = inf.inflate(R.layout.lv_kategorie_pravidel_radek, null);
            viewHolder.tvKategorie = (TextView) convertView.findViewById(R.id.tvRadek);
            viewHolder.ivKategorie = (ImageView) convertView.findViewById(R.id.ivKategorie);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String kategorie = getItem(position);
        viewHolder.tvKategorie.setText(kategorie);
        switch (kategorie) {
            case "Časové pravidlo":
                viewHolder.ivKategorie.setImageResource(R.drawable.ic_clock);
                break;
            case "Kalendářové pravidlo":
                viewHolder.ivKategorie.setImageResource(R.drawable.ic_calendar);
                break;
            case "Wifi pravidlo":
                viewHolder.ivKategorie.setImageResource(R.drawable.ic_wifi);
                break;
        }

        return convertView;
    }
}
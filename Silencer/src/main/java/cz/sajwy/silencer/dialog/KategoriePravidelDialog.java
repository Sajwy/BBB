package cz.sajwy.silencer.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import java.util.List;

import cz.sajwy.silencer.R;
import cz.sajwy.silencer.activity.NovePravidloActivity;
import cz.sajwy.silencer.adapter.KategoriePravidelAdapter;
import cz.sajwy.silencer.dao.KategorieDao;
import cz.sajwy.silencer.daoImpl.KategorieDaoImpl;

public class KategoriePravidelDialog extends DialogFragment implements OnItemClickListener {
    private ListView lvKategorie;

    public KategoriePravidelDialog() {}

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.kategorie_pravidel_dialog_box, null);

        lvKategorie = (ListView) view.findViewById(R.id.lvKategoriePravidel);
        KategorieDao kategorieDao = new KategorieDaoImpl();
        List<String> kategorie = kategorieDao.getAllKategorieNazvy();
        KategoriePravidelAdapter adapter = new KategoriePravidelAdapter(getActivity(), kategorie);
        lvKategorie.setAdapter(adapter);
        lvKategorie.setOnItemClickListener(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        builder.setTitle(R.string.vyber_kategorie);

        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        return alertDialog;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String nazevKategorie = lvKategorie.getItemAtPosition(position).toString().trim();
        Intent intent = new Intent(getActivity(), NovePravidloActivity.class);
        intent.putExtra("kategorie", nazevKategorie);
        intent.putExtra("id", 0);
        startActivity(intent);
    }
}
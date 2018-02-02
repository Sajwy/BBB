package cz.sajwy.silencer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.Toast;

import java.util.List;

import cz.sajwy.silencer.R;
import cz.sajwy.silencer.adapter.SeznamPravidelAdapter;
import cz.sajwy.silencer.callback.ExpLVCallback;
import cz.sajwy.silencer.dao.KategorieDao;
import cz.sajwy.silencer.dao.PravidloDao;
import cz.sajwy.silencer.daoImpl.KategorieDaoImpl;
import cz.sajwy.silencer.daoImpl.PravidloDaoImpl;
import cz.sajwy.silencer.model.Kategorie;
import cz.sajwy.silencer.model.Pravidlo;
import cz.sajwy.silencer.utils.Utils;

public class SeznamPravidelActivity extends AppCompatActivity implements ExpLVCallback, OnChildClickListener, OnGroupExpandListener {

    private SeznamPravidelAdapter seznamPravidelAdapter;
    private ExpandableListView expandableListView;
    private int groupPos;
    private int childPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.SP_name);
        setContentView(R.layout.activity_seznam_pravidel);

        expandableListView = (ExpandableListView) findViewById(R.id.expandableListView);
        KategorieDao kategorieDao = new KategorieDaoImpl();
        List<Kategorie> expListItems = kategorieDao.getKategorieSPravidly();
        seznamPravidelAdapter = new SeznamPravidelAdapter(this, expListItems, this);
        expandableListView.setAdapter(seznamPravidelAdapter);
        expandableListView.setOnChildClickListener(this);
        expandableListView.setOnGroupExpandListener(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Pravidlo p = seznamPravidelAdapter.getChild(groupPos, childPos);
        Pravidlo pn;
        PravidloDao pravidloDao = new PravidloDaoImpl();
        try {
            pn = pravidloDao.getPravidlo(p.getId_pravidlo());
        } catch (Exception e) {
            pn = null;
        }

        if(pn != null) {
            seznamPravidelAdapter.getGroup(groupPos).setPravidla(pravidloDao.getPravidlaByKategorie(seznamPravidelAdapter.getGroup(groupPos).getId_kategorie()));
            seznamPravidelAdapter.notifyDataSetChanged();
        } else {
            seznamPravidelAdapter.getGroup(groupPos).getPravidla().remove(childPos);

            if(seznamPravidelAdapter.getGroup(groupPos).getPravidla().isEmpty())
                expandableListView.collapseGroup(groupPos);
            else
                seznamPravidelAdapter.notifyDataSetChanged();

            Toast.makeText(this, R.string.pravidloOdstraneno, Toast.LENGTH_SHORT).show();

            Utils.automatickeVypnutiObsluhy(getApplicationContext());
        }
    }

    @Override
    public void collapseGroup(int pozice) {
        expandableListView.collapseGroup(pozice);
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        String kategorie = seznamPravidelAdapter.getGroup(groupPosition).getNazev();
        Pravidlo pravidlo = seznamPravidelAdapter.getChild(groupPosition, childPosition);

        groupPos = groupPosition;
        childPos = childPosition;

        Intent intent = new Intent(getApplicationContext(), NovePravidloActivity.class);
        intent.putExtra("kategorie", kategorie);
        intent.putExtra("id", pravidlo.getId_pravidlo());
        startActivity(intent);

        return false;
    }

    @Override
    public void onGroupExpand(int groupPosition) {
        if (seznamPravidelAdapter.getGroup(groupPosition).getPravidla().isEmpty()) {
            expandableListView.collapseGroup(groupPosition);
            Toast.makeText(getApplicationContext(),R.string.kategorie_bez_pravidel,Toast.LENGTH_SHORT).show();
        }
    }
}
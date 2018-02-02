package cz.sajwy.silencer.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.DialogInterface.OnShowListener;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.view.View;

import java.util.ArrayList;

import cz.sajwy.silencer.R;
import cz.sajwy.silencer.callback.DnyCallback;
import cz.sajwy.silencer.dao.DenDao;
import cz.sajwy.silencer.daoImpl.DenDaoImpl;

public class DnyDialog extends DialogFragment implements OnShowListener, OnMultiChoiceClickListener, View.OnClickListener, OnClickListener {
    private AlertDialog alertDialog;
    private ArrayList<Integer> vybraneDny;
    private ArrayList<Integer> vybraneDnyZaloha;
    private boolean[] isSelectedArray = {false,false,false,false,false,false,false};
    private DnyCallback activity;

    public DnyDialog() {}

    public static DnyDialog newInstance(String nadpis, ArrayList<Integer> vybraneDny) {
        DnyDialog frag = new DnyDialog();
        Bundle args = new Bundle();
        args.putString("nadpis", nadpis);
        args.putIntegerArrayList("vybraneDny", vybraneDny);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        activity = (DnyCallback) getActivity();
        String nadpis = getArguments().getString("nadpis");
        vybraneDny = getArguments().getIntegerArrayList("vybraneDny");
        vybraneDnyZaloha = (ArrayList<Integer>) vybraneDny.clone();

        if(!vybraneDny.isEmpty()) {
            for(int i = 0;i < vybraneDny.size();i++) {
                isSelectedArray[vybraneDny.get(i)] = true;
            }
        }

        DenDao denDao = new DenDaoImpl();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(nadpis);
        builder.setMultiChoiceItems(denDao.getNazvyDnuArray(), isSelectedArray, this);
        builder.setNegativeButton(R.string.btnZrusit, this);
        builder.setPositiveButton(R.string.btnOk, this);
        builder.setNeutralButton(R.string.btnOznacitVse, null);

        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setOnShowListener(this);
        return alertDialog;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        activity.setDny(vybraneDnyZaloha);
    }

    @Override
    public void onShow(DialogInterface dialog) {
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorBlack, null));
        alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL).setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorBlack, null));
        alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL).setOnClickListener(this);
    }

    @Override
    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
        if (isChecked) {
            vybraneDny.add(which);
        } else {
            vybraneDny.remove(Integer.valueOf(which));
        }
    }

    @Override
    public void onClick(View v) {
        vybraneDny.clear();
        for (int i = 0; i < isSelectedArray.length; i++) {
            alertDialog.getListView().setItemChecked(i, true);
            vybraneDny.add(i);
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_NEGATIVE:
                activity.setDny(vybraneDnyZaloha);
                break;
            case DialogInterface.BUTTON_POSITIVE:
                activity.setDny(vybraneDny);
                break;
        }
    }
}
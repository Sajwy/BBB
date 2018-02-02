package cz.sajwy.silencer.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnShowListener;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;

import cz.sajwy.silencer.R;
import cz.sajwy.silencer.callback.SmazatPravidloCallback;

public class SmazPravidloDialog extends DialogFragment implements OnClickListener, OnShowListener {
    private AlertDialog alertDialog;
    private int kategorie;
    private int pravidlo;
    private SmazatPravidloCallback callback;

    public SmazPravidloDialog() {}

    public static SmazPravidloDialog newInstance(int kategoriePozice, int pravidloPozice) {
        SmazPravidloDialog frag = new SmazPravidloDialog();
        Bundle args = new Bundle();
        args.putInt("kategorie", kategoriePozice);
        args.putInt("pravidlo", pravidloPozice);
        frag.setArguments(args);
        return frag;
    }

    public void setListener(SmazatPravidloCallback callback) {
        this.callback = callback;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        kategorie = getArguments().getInt("kategorie", -1);
        pravidlo = getArguments().getInt("pravidlo", -1);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.odstraneniPravidla);
        builder.setMessage(R.string.odstraneniPravidlaMessage);
        builder.setNegativeButton(R.string.btnZrusit, null);
        builder.setPositiveButton(R.string.btnOk, this);

        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setOnShowListener(this);
        return alertDialog;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if(kategorie == -1) {
            SmazatPravidloCallback activity = (SmazatPravidloCallback) getActivity();
            activity.smazPravidlo(kategorie, pravidlo);
        } else
            callback.smazPravidlo(kategorie, pravidlo);
    }

    @Override
    public void onShow(DialogInterface dialog) {
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorBlack, null));
    }
}
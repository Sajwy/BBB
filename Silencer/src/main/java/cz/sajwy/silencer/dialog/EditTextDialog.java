package cz.sajwy.silencer.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnShowListener;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import cz.sajwy.silencer.R;
import cz.sajwy.silencer.adapter.ParametryPravidelAdapter;
import cz.sajwy.silencer.callback.EditTextCallback;

public class EditTextDialog extends DialogFragment implements OnClickListener, OnShowListener {
    private EditText input;
    private AlertDialog alertDialog;
    private String parametr;

    public EditTextDialog() {}

    public static EditTextDialog newInstance(String nadpis, String obsah, String parametr) {
        EditTextDialog frag = new EditTextDialog();
        Bundle args = new Bundle();
        args.putString("nadpis", nadpis);
        args.putString("obsah", obsah);
        args.putString("parametr", parametr);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String nadpis = getArguments().getString("nadpis");
        String obsah = getArguments().getString("obsah");
        parametr = getArguments().getString("parametr");
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View mView = inflater.inflate(R.layout.edittext_dialog_box, null);

        input = (EditText) mView.findViewById(R.id.etInput);

        String porovnavaciText = "";
        switch (parametr) {
            case ParametryPravidelAdapter.NAZEV:
                porovnavaciText = getString(R.string.nazevObsah);
                break;
            case ParametryPravidelAdapter.UDALOST:
                porovnavaciText = getString(R.string.udalostObsah);
                break;
        }

        if(porovnavaciText.equals(obsah)){
            input.setHint(obsah);
        } else {
            input.setText(obsah);
            input.setSelection(input.getText().length());
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(mView);
        builder.setTitle(nadpis);
        builder.setNegativeButton(R.string.btnZrusit, null);
        builder.setPositiveButton(R.string.btnOk, this);

        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setOnShowListener(this);
        return alertDialog;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        EditTextCallback activity = (EditTextCallback) getActivity();
        switch (parametr) {
            case ParametryPravidelAdapter.NAZEV:
                activity.setNazevPravidla(input.getText().toString());
                break;
            case ParametryPravidelAdapter.UDALOST:
                activity.setNazevUdalosti(input.getText().toString());
                break;
        }
    }

    @Override
    public void onShow(DialogInterface dialog) {
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorBlack, null));
    }
}
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
import android.widget.NumberPicker;

import java.util.Calendar;

import cz.sajwy.silencer.R;
import cz.sajwy.silencer.adapter.ParametryPravidelAdapter;
import cz.sajwy.silencer.callback.CasCallback;

public class CasDialog extends DialogFragment implements OnClickListener, OnShowListener {
    private NumberPicker npHod, npMin;
    private AlertDialog alertDialog;
    private String parametr;

    public CasDialog() {}

    public static CasDialog newInstance(String nadpis, String obsah, String parametr) {
        CasDialog frag = new CasDialog();
        Bundle args = new Bundle();
        args.putString("nadpis", nadpis);
        args.putString("obsah", obsah);
        args.putString("parametr", parametr);
        frag.setArguments(args);
        return frag;
    }

    public static CasDialog newInstance(String nadpis, String parametr) {
        CasDialog frag = new CasDialog();
        Bundle args = new Bundle();
        args.putString("nadpis", nadpis);
        args.putString("parametr", parametr);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String nadpis = getArguments().getString("nadpis");
        String obsah = getArguments().getString("obsah", "");
        parametr = getArguments().getString("parametr");
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View mView = inflater.inflate(R.layout.mytimepicker_dialog_box, null);

        npHod = (NumberPicker) mView.findViewById(R.id.npHod);
        npHod.setMaxValue(23);
        npHod.setMinValue(0);
        npHod.setWrapSelectorWheel(true);

        String[] pickerValuesHod = new String[24];
        for (int i = 0; i < pickerValuesHod.length; i++) {
            pickerValuesHod[i] = pad(i);
        }
        npHod.setDisplayedValues(pickerValuesHod);

        npMin = (NumberPicker) mView.findViewById(R.id.npMin);
        npMin.setMaxValue(59);
        npMin.setMinValue(0);
        npMin.setWrapSelectorWheel(true);

        String[] pickerValuesMin = new String[60];
        for (int i = 0; i < pickerValuesMin.length; i++) {
            pickerValuesMin[i] = pad(i);
        }
        npMin.setDisplayedValues(pickerValuesMin);

        String regexStr = "^(2[0-3]|[01]?[0-9]):([0-5]?[0-9])$";
        if(obsah.matches(regexStr))
        {
            npHod.setValue(Integer.parseInt(unpad(obsah.substring(0, obsah.indexOf(":")))));
            npMin.setValue(Integer.parseInt(unpad(obsah.substring(obsah.indexOf(":") + 1, obsah.length()))));
        } else {
            Calendar cal = Calendar.getInstance();
            int hod = cal.get(Calendar.HOUR_OF_DAY);
            int min = cal.get(Calendar.MINUTE);
            npHod.setValue(hod);
            npMin.setValue(min);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DialogStyle);
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
        CasCallback activity = (CasCallback) getActivity();
        switch (parametr) {
            case ParametryPravidelAdapter.CAS_OD:
                activity.setCas(pad(npHod.getValue()) + ":" + pad(npMin.getValue()), parametr);
                break;
            case ParametryPravidelAdapter.CAS_DO:
                activity.setCas(pad(npHod.getValue()) + ":" + pad(npMin.getValue()), parametr);
                break;
            case "CAS_OBNOVY":
                activity.setCas(pad(npHod.getValue()) + ":" + pad(npMin.getValue()), parametr);
                break;
        }
    }

    @Override
    public void onShow(DialogInterface dialog) {
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorBlack, null));
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if(parametr.equals("CAS_OBNOVY")) {
            getActivity().finish();
        }
    }

    private String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }

    private String unpad(String c) {
        if (Integer.parseInt(c) >= 10)
            return String.valueOf(c);
        else
            return c.substring(1);
    }
}
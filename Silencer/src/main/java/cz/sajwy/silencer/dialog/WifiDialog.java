package cz.sajwy.silencer.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

import java.util.List;

import cz.sajwy.silencer.R;
import cz.sajwy.silencer.callback.WifiCallback;
import cz.sajwy.silencer.utils.Utils;

public class WifiDialog extends DialogFragment implements OnShowListener, DialogInterface.OnClickListener, View.OnClickListener {
    private EditText input;
    private Spinner spinner;
    private RadioButton rbWifiDaU;
    private RadioButton rbWifiVlastni;
    private boolean boolVlastnorucne;
    private AlertDialog alertDialog;

    public WifiDialog() {}

    public static WifiDialog newInstance(String nadpis, String obsah) {
        WifiDialog frag = new WifiDialog();
        Bundle args = new Bundle();
        args.putString("nadpis", nadpis);
        args.putString("obsah", obsah);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String nadpis = getArguments().getString("nadpis");
        String obsah = getArguments().getString("obsah");

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View mView = inflater.inflate(R.layout.wifi_dialog_box, null);

        input = (EditText) mView.findViewById(R.id.etInput);
        spinner = (Spinner) mView.findViewById(R.id.spinWifi);
        rbWifiDaU = (RadioButton) mView.findViewById(R.id.rbWifiDostAUloz);
        rbWifiVlastni = (RadioButton) mView.findViewById(R.id.rbWifiVlastni);
        WifiManager wifiManager = (WifiManager) getActivity().getSystemService(getActivity().WIFI_SERVICE);
        if(wifiManager.isWifiEnabled()) {
            List<String> nazvyWifinList = Utils.vratNazvyWifinList(wifiManager);
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, R.id.text1, nazvyWifinList);
            dataAdapter.setDropDownViewResource(R.layout.spinner_item);
            spinner.setAdapter(dataAdapter);

            if (nazvyWifinList.isEmpty()) {
                boolVlastnorucne = true;
                rbWifiDaU.setEnabled(false);
            } else {
                if (obsah.equals(getString(R.string.wifiObsah)) || nazvyWifinList.contains(obsah)) {
                    boolVlastnorucne = false;
                } else {
                    boolVlastnorucne = true;
                }
            }

            rbWifiDaU.setChecked(!boolVlastnorucne);
            rbWifiVlastni.setChecked(boolVlastnorucne);

            if(boolVlastnorucne) {
                spinner.setEnabled(false);
                input.setEnabled(true);

                if(obsah.equals(getString(R.string.wifiObsah))){
                    input.setHint(R.string.wifiObsah);
                } else {
                    input.setText(obsah);
                    input.setSelection(input.getText().length());
                }
            } else {
                spinner.setEnabled(true);
                input.setEnabled(false);
                input.setHint(R.string.wifiObsah);
                if (!obsah.equals(getString(R.string.wifiObsah))) {
                    spinner.setSelection(nazvyWifinList.indexOf(obsah));
                }
            }

            rbWifiDaU.setOnClickListener(this);
            rbWifiVlastni.setOnClickListener(this);
        } else {
            boolVlastnorucne = true;
            rbWifiDaU.setEnabled(false);
            rbWifiVlastni.setChecked(boolVlastnorucne);
            spinner.setEnabled(false);
            input.setEnabled(true);

            if(obsah.equals(getString(R.string.wifiObsah))){
                input.setHint(R.string.wifiObsah);
            } else {
                input.setText(obsah);
                input.setSelection(input.getText().length());
            }
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
    public void onClick(View v) {
        if(v.getId() == R.id.rbWifiDostAUloz) {
            rbWifiDaU.setChecked(true);
            rbWifiVlastni.setChecked(false);
            spinner.setEnabled(true);
            input.setEnabled(false);
            boolVlastnorucne = false;
        } else if(v.getId() == R.id.rbWifiVlastni) {
            rbWifiDaU.setChecked(false);
            rbWifiVlastni.setChecked(true);
            spinner.setEnabled(false);
            input.setEnabled(true);
            boolVlastnorucne = true;
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        WifiCallback activity = (WifiCallback) getActivity();
        if (boolVlastnorucne)
            activity.setNazevWifi(input.getText().toString());
        else
            activity.setNazevWifi(spinner.getSelectedItem().toString());
    }

    @Override
    public void onShow(DialogInterface dialog) {
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorBlack, null));
    }
}
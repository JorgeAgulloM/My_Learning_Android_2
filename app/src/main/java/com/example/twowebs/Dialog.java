package com.example.twowebs;

import android.app.AlertDialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import java.util.Objects;

public class Dialog extends DialogFragment {

    //Crea una nueva instancia del dialog
    public static Dialog newInstance() {
       return new Dialog();
    }

    @NonNull
    @Override
    public android.app.Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        //Constructor. Crea el dialog en la actividad actual.
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        //Titulo del dialogo
        builder.setTitle(R.string.app_name);

        //Mensage del dialogo.
        builder.setMessage(R.string.webs_Ok);

        //Botón positivo, usado para que esta opción aparezca primero para mantener un orden.
        builder.setNegativeButton(R.string.goWeb1, (dialog, which) -> {
            //Llama a la actividad para acceder al su método.
            ((MainActivity) Objects.requireNonNull(getActivity())).fragments(1);
        });

        //Botón negativo, usado para que esta opción salga como segunda opción
        builder.setPositiveButton(R.string.goWeb2, (dialog, which) -> {
            //Llama a la actividad para acceder al su método.
            ((MainActivity) Objects.requireNonNull(getActivity())).fragments(2);
        });

        //Bóton cancelar, no hace nada, pero al pulsarlo se sale del dialog.
        builder.setNeutralButton(R.string.cancel, (dialog, which) -> {
            //Se cierra el dialog.
        });

        //Se devuelve el Dialog creado.
        return builder.create();
    }

}

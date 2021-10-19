package com.example.twowebs;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

public class StartFragment extends Fragment {

    //Variables para enlazar a objetos xml
    @SuppressLint("StaticFieldLeak")
    static EditText editTextWeb1;
    @SuppressLint("StaticFieldLeak")
    static EditText editTextWeb2;
    Button btnGoToWeb;
    Button btnDefaultWebs;
    Button btnEraseWebs;

    //Variables globales.
    String url1 = "";
    String url2 = "";
    View view;

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Se declara el inflater para colocar los fragment
        view = inflater.inflate(R.layout.fragment_main, container, false);

        //Se enlazan las variables a los objetos xml
        editTextWeb1 = view.findViewById(R.id.editTextWeb1);
        editTextWeb2 = view.findViewById(R.id.editTextWeb2);
        btnGoToWeb = view.findViewById(R.id.btnGoToWeb);
        btnEraseWebs = view.findViewById(R.id.btnEraseWebs);
        btnDefaultWebs = view.findViewById(R.id.btnDefaultWebs);

        //Listener del editTextWeb1 para introducir el texto https:// por defecto y asegturar la
        //aparición del teclado virtual
        editTextWeb1.setOnClickListener(v -> {
            if (editTextWeb1.getText().toString().equals("")) {
                editTextWeb1.setText("https://");
                editTextWeb1.setSelection(editTextWeb1.getText().length());
                ((MainActivity) Objects.requireNonNull(getActivity())).hideKeyboard(btnGoToWeb, 1);
            }
        });

        //Listener del editTextWeb2 para introducir el texto https:// por defecto y asegturar la
        //aparición del teclado virtual
        editTextWeb2.setOnFocusChangeListener((v, hasFocus) -> {
            if (editTextWeb2.getText().toString().equals("")) {
                editTextWeb2.setText("https://");
                editTextWeb2.setSelection(editTextWeb2.getText().length());
                ((MainActivity) Objects.requireNonNull(getActivity())).hideKeyboard(btnGoToWeb, 1);
            }
        });

        //Listener del boton GoToWeb para enviar las url a los fragment y mostrar el dialogo
        btnGoToWeb.setOnClickListener(v -> {
            if (checkText()) {
                showDialog();
            }
        });

        //Listener del btnDefaultWebs para mostrar webs predefinidas.
        btnDefaultWebs.setOnClickListener(v -> {
            editTextWeb1.setText("https://github.com");
            editTextWeb2.setText("https://es.stackoverflow.com/");
        });

        //Listener del btnEraseWebs para borrar el contenido de los editText
        btnEraseWebs.setOnClickListener(v -> {
            editTextWeb1.setText("");
            editTextWeb2.setText("");
        });

        return view;
    }

    //Método sobreescrito para mostrar el titulo del fragment al aparecer.
    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) Objects.requireNonNull(getActivity())).title(0);
    }

    //Método sobreescrito para averiguar si se has escrito un texto y adquirirlo en las variables.
    public Boolean checkText() {
        if (!editTextWeb1.getText().toString().equals("") && !editTextWeb1.getText().toString().equals("https://")) { //Si hay texto
            if (!editTextWeb2.getText().toString().equals("") && !editTextWeb2.getText().toString().equals("https://")) { //y aqui tb hay texto
                //Se capturan en las variables.
                url1 = editTextWeb1.getText().toString().trim();
                url2 = editTextWeb2.getText().toString().trim();
                //Se envian los datos a MainACtivity para que se almacenen
                ((MainActivity) Objects.requireNonNull(getActivity())).receiveUrls(url1, url2);
                //Se solicita la ocultación del teclado virtual.
                ((MainActivity) Objects.requireNonNull(getActivity())).hideKeyboard(btnGoToWeb, 0);

                return true;
            } else { //Si no hay texto para web 2
                //Lama a una función donde se ha definido un SnackBar
                editTextWeb2.setError(getString(R.string.fill_text));
                snackBarDefinite();
                return false;
            }
        } else { //Si no hay texto para web 1
            //Lama a una función donde se ha definido un SnackBar
            editTextWeb1.setError(getString(R.string.fill_text));
            snackBarDefinite();
            return false;
        }
    }

    //Método sobreescrito para mostrar el dialog.
    public void showDialog() {
        Dialog dialog = Dialog.newInstance();
        assert getFragmentManager() != null;
        dialog.show(getFragmentManager(), "dialog");
    }

    //Método sobreescrito  para mostrar el SnackBar.
    public void snackBarDefinite() {
        Snackbar.make(view, R.string.snackText, Snackbar.LENGTH_SHORT).show();
    }


}

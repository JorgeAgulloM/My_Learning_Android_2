package com.example.twowebs;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

public class Web1Fragment extends Fragment {

    //Varibles enlazadas a objetos xml
    @SuppressLint("StaticFieldLeak")
    static WebView webView; //Se declaran static para que no pierdan el valor cuando se llama al fragment.
    private ProgressDialog progressDialog;

    //Variable global
    private String url; //Se declara static para que no pierda el valor cuando se llama al fragment.


    //Método sobreescrito que es llamado en el evento onAttach, al entrar la app al fragment
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        //Si la url no es nula y no está vacía
        if (!(url == null) && !url.equals("")) {
            //Se declara un nuevo progresDialog con el context
            progressDialog = new ProgressDialog(context);
            progressDialog.setIcon(R.mipmap.ic_launcher);//Se le agrega un icono
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); //Un estilo
            progressDialog.setMessage(getString(R.string.load)); //Un mensage
            progressDialog.show(); //Y se muestra
        }
    }


    //Método sobreescrito onCreated
    @SuppressLint("SetJavaScriptEnabled")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Se declara el inflater para colocar los fragment
        View view = inflater.inflate(R.layout.fragment_web1, container, false);

        //Se enlazan las variables a los objetos xml
        webView = view.findViewById(R.id.webView1);

        //Propiedades para webView
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(true);

        //Llama al método de MainActivity para cambiar el título de la barra superior
        ((MainActivity) Objects.requireNonNull(getActivity())).title(1);

        if (!(url == null) && !url.equals("")) {
            //Si tiene valor
            webView.loadUrl(url); //Se carga el url
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    progressDialog.dismiss();
                }
            });
        } else {
            //Lama a una función donde se ha definido un SnackBar
            Snackbar.make(view, R.string.snackText, Snackbar.LENGTH_LONG).setAction(R.string.snackTextQuest,
                    v -> ((MainActivity) Objects.requireNonNull(getActivity())).fragments(0)).show();
        }

        return view;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

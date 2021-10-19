package com.example.twowebs;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.annotation.SuppressLint;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    //Varialbes de enlace a objetos xml
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;
    ListView lateralMenu;
    static WebList webList;

    //Variables globales
    Boolean startApp = false;
    static String url1;
    static String url2;
    int indexFragment;
    SharedPreferences sharedPreferences;
    String json;
    Menu menu;
    InputMethodManager inputMethodManager;
    FragmentManager fragmentManager;
    FragmentTransaction transaction;
    StartFragment startFragment;
    Web1Fragment web1Fragment;
    Web2Fragment web2Fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Se enlazan las variables a los objetos xml.
        drawerLayout = findViewById(R.id.mainLayout);
        lateralMenu = findViewById(R.id.lateralMenu);
        drawerLayout.addDrawerListener(drawerToggle);

        //Inicialización de variables.
        url1 = "";
        url2 = "";
        indexFragment = 0;
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        //Se inician los fragment.
        startFragment = new StartFragment();
        web1Fragment = new Web1Fragment();
        web2Fragment = new Web2Fragment();

        //Se declaran las sharePreferences para guardar las web favoritas
        sharedPreferences = getSharedPreferences("Webs", MODE_PRIVATE);
        json = sharedPreferences.getString("webs", "");

        //Se crea una nueva instancia de webList con los objetos json
        webList = new WebList();
        webList = webList.fromJson(json);

        //Se define el botón de la barra UI para que el usuario conozca la existencia de ese menú.
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.app_name, R.string.app_name);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        //y se define un listener para captar los clicks sobre sus botones.
        lateralMenu.setOnItemClickListener(this);

        //Se invoca al método para iniciar el fragmentMain.
        fragments(0);
    }


    //Método sobreescrito para el uso del menú de barra superior.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        this.menu = menu;
        //Se declara un inflater para colocar los botones con el menú definido en xml.
        getMenuInflater().inflate(R.menu.superior_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //Método sobreescrito para actualizar el menu de Webs favoritas.
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        try {
            if (webList == null) {
                webList = new WebList();
            }

            //Bucle para recorer las webs guardadas por el usuario en las sharepreferences
            for (int i = 0; i < webList.web.size(); i++) {

                boolean exist = false;
                String itemInMenu = webList.web.get(i).nameWeb; //Se obtiene el texto del enlace.
                int x; //Se declara fuera del siguiente bucle su variable índice para usarla más adelante

                for (x = 0; x < menu.size(); x++) { //Bucle que recorre las webs que ya pudieran estar en el menú
                    String itemInShare = menu.getItem(x).toString(); //Se obtiene la web correspondiente a analizar
                    if (itemInShare.equals(itemInMenu)) { //Se iguala para saber si no existe
                        exist = true; //Si existe se sale del bucle
                        break;
                    }
                }

                if (!exist) { //Si no existe se añade al menú
                    menu.add(1, 1, x, webList.web.get(i).nameWeb)
                            .setIcon(R.drawable.ic_baseline_add_24);
                }

            }

        } catch (Exception e) {
            //En caso de error, se envia un toast con un error generico
            theToast(R.string.error);
        }

        //Se devuelve el resultado del menú
        return super.onPrepareOptionsMenu(menu);
    }

    //Método para escuchar los clicks sobre los botones del menú superior.
    //***************************** sin terminar.
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        //Índice para el menú superior.
        int index = item.getItemId();

        //Switch para seleccionar una sentencia en condición del item pulsado.
        switch (index) {

            case R.id.favorites: //En caso de seleccionar favoritos, si no se está en el fragmet_main

                if (indexFragment != 0) {
                    addFavorites(); //Se invoca al método.

                } else {
                    //Se muestra un Toast en caso de que el usuario intente usar el boton favoritos en el fragment_main
                    theToast(R.string.onlyUseinWebs);

                }
                break;

            case R.id.reloadWeb: //En caso de usar el botón reloadWeb si no se está en fragment_main

                if (indexFragment == 1) {
                    Web1Fragment.webView.reload();
                    //se hace un reload del webView1 y se muestra un toast
                    theToast(R.string.reloadWeb1);

                } else if (indexFragment == 2) {
                    Web2Fragment.webView.reload();
                    //se hace un reload del webView2 y se muestra un toast
                    theToast(R.string.reloadWeb2);

                } else {
                    //Se muestra un Toast si el usuario usar el reload en fragment_main
                    theToast(R.string.onlyUseinWebs);

                }
                break;

            case 1: //Al hacer click sobre una de las webs guardadas en favoritos.

                if (indexFragment != 0) { //Si la app no está en fragment_main
                    //Informa al usuario
                    theToast(R.string.useMeInMain);

                } else { //Si se está en fragment_main

                    //Se determinan y almacenan el resultado
                    boolean text1 = StartFragment.editTextWeb1.getText().toString().equals("");
                    boolean text2 = StartFragment.editTextWeb2.getText().toString().equals("");

                    if (text1 && text2) { // Si ambos están vacíos
                        //Se añade la web seleccionada al primer fragment
                        StartFragment.editTextWeb1.setText(item.getTitle());

                    } else if (!text1 && text2) { //Si solo el segundo está vacío
                        //Se coloca la web elegida en el segundo edittext
                        StartFragment.editTextWeb2.setText(item.getTitle());

                    } else if (!text1 && !text2) {
                        //Se borran ambos editText y se establece la elección en el primero
                        StartFragment.editTextWeb1.setText("");
                        StartFragment.editTextWeb2.setText("");
                        StartFragment.editTextWeb1.setText(item.getTitle());

                    }
                    break;
                }

            default:

                //Nothig
        }

        // devuelve el item seleccionado
        drawerToggle.onOptionsItemSelected(item);
        return super.onOptionsItemSelected(item);
    }


    //Método sobresescrito para sincronizar el icono de las tres barras/Flecha con el menu lateral
    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }


    //Método sobresescrito que observa la pulsación de las opciones del menú.
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //Se traslada el item pulsado a la función
        fragments(position);
    }


    private static final int INTERVALO = 3000; //Otorga 3 segundos para salir
    private long timeFirstClick; //Se guarda y se compara el valor de tiempo

    //Método sobreescrito para evitar que el botón back del telefono cierre la app
    @Override
    public void onBackPressed() {
        if (indexFragment != 0) { //Si la app no está en el fragment Main.
            fragments(0);  //Retrocede a este.
        } else { //Si lo está.
            if (timeFirstClick + INTERVALO > System.currentTimeMillis()) { //Y es la segunda vez que se pulsa
                super.onBackPressed(); //se sale de la app
                return;
            } else { //Si no es la segunda vez o no se está dentro del tiempo otorgado, se informa al usuario.
                Snackbar.make(this.findViewById(R.id.fragment_main), R.string.exitconfirm, BaseTransientBottomBar.LENGTH_SHORT).show();
            }
            timeFirstClick = System.currentTimeMillis(); //Se guarda el valor de tiempo.
        }
    }


    //Método que evalua el evento que ha sido pulsado.
    public void fragments(int index) {

        //Se declaran las variables para los fragments
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();

        //Guarda el valor de index.
        indexFragment = index;

        //Swich para determina la sentencia adecuada.
        switch (index) {

            case 0:
                if (startApp) {
                    //Animación para el fragment principal una vez iniciada la app.
                    transaction.setCustomAnimations(R.anim.to_left, R.anim.out_left);

                } else {
                    //Animación de inicio para el fragment principal.
                    startApp = true;
                    transaction.setCustomAnimations(R.anim.show_anim, R.anim.out_left);

                }

                //En esta sentencia se declara el fragmente que se quiere visualizar en el contenedor.
                transaction.replace(R.id.content, startFragment);
                break;

            case 1:
                //Animación para el fragment 2
                transaction.setCustomAnimations(R.anim.to_left, R.anim.out_left);
                transaction.replace(R.id.content, web1Fragment);
                web1Fragment.setUrl(url1);
                break;

            case 2:
                //Animación para el fragment 3
                transaction.setCustomAnimations(R.anim.to_left, R.anim.out_left);
                transaction.replace(R.id.content, web2Fragment);
                web2Fragment.setUrl(url2);
                break;

        }

        //Se lleva a cabo la transición.
        transaction.commit();

        //Sincronización para esconconder el menú al pulsar un boton de cabecera de este.
        drawerLayout.closeDrawer(GravityCompat.START);

    }

    //Método para colocar el nombre del fragmente al desplazarnos desde cualquier menú.
    //Se accede desde los propios fragment desde el evento onResume.
    public void title(int index) {

        switch (index) { //Coloca los títulos sobre cada fragment en la barra UI

            case 0:
                setTitle(getString(R.string.main));
                break;

            case 1:
                setTitle(getString(R.string.web1));
                break;

            case 2:
                setTitle(getString(R.string.web2));
                break;

            default:

        }
    }

    //método para obtener las url y almacenarlas
    public void receiveUrls(String url1, String url2) {
        MainActivity.url1 = url1;
        MainActivity.url2 = url2;
    }


    //Método para esconder/visualizar el teclado virtual
    void hideKeyboard(Button button, int onOff) {
        inputMethodManager.hideSoftInputFromWindow(button.getWindowToken(), onOff);
    }

    //Método para guardar lo selecionado por el usuario en las sharepreferences
    private void addFavorites() {
        //Si la app no esta en fragment_main y los editText no está vacios o no solo tienen el texto predefinido
        if (indexFragment != 0 && !url1.equals("") && !url2.equals("")) {

            //Se declara una variable para almacenar la url a guardar
            String webName = "";
            if (indexFragment == 1) {// si está en Web1Fragment
                webName = url1;

            } else if (indexFragment == 2) { //Si está en Web2Fragment
                webName = url2;

            }

            //se evalua si ya existe
            boolean webExist = false;
            try {
                //Bucle for que recorre lo guardado en sharepreferences y almacenado como objeto web en weblist
                for (int i = 0; i < webList.web.size(); i++) {

                    if (webList.web.get(i).nameWeb.equals(webName)) { //Si existe, se sale del bucle
                        webExist = true;
                        break;

                    }
                }
            } catch (Exception e) {
                //En caso de error se muestra un error genérico
                theToast(R.string.error);
            }

            //Si no existe la web
            if (!webExist) {

                Web web = new Web(webName); //Se construye un nuevo objeto web

                //if que carga un nuevo weblist si este es null
                if (webList == null) {
                    webList = new WebList();

                }
                webList.web.add(web); //Se añade el bojeto web

                //Se crea el editor para las sharepreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("webs", webList.toJson()); //Se carga el editor con la web filtrada por gson
                editor.apply(); //Se aplica la edición.

                //Se invoca al método para actualziar la lsita de favoritos.
                onPrepareOptionsMenu(menu);

                //Se muestra un toast informando al usuario.
                theToast(R.string.fav_text);

            } else {
                //Se muestra un toast en caso de que ya exista esta web.
                theToast(R.string.webExist);

            }
        }
    }

    //Toast para exclamar un error o un mensaje a l usuario
    private void theToast(int message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}




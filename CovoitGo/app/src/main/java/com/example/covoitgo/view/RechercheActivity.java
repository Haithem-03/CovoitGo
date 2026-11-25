package com.example.covoitgo.view;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.covoitgo.R;
import com.example.covoitgo.controller.AuthController;
import com.example.covoitgo.model.Utilisateur;

import java.util.Calendar;
import java.util.Locale;

/** Vue (maquette 1) : accueil et recherche de trajets */
public class RechercheActivity extends AppCompatActivity {

    public static final String EXTRA_DEPART = "depart";
    public static final String EXTRA_ARRIVEE = "arrivee";
    public static final String EXTRA_DATE = "date";
    public static final String EXTRA_PLACES = "places";

    private static final String ESPRIT = "Esprit · El Ghazala";
    private static final String[] ALLER = {"Ezzahra", "Manouba", "Mégrine", "Ariana", "Ben Arous"};
    private static final String[] RETOUR = {"Nouvelle Médina 3", "Tunis centre", "La Marsa", "Bardo"};

    private EditText etDepart, etArrivee;
    private TextView tvDate, tvPlaces;
    private String date;
    private int places = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utilisateur u = new AuthController(this).getUtilisateurConnecte();
        if (u == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        setContentView(R.layout.activity_recherche);
        Navigation.installer(this, R.id.nav_rechercher);

        etDepart = findViewById(R.id.etDepart);
        etArrivee = findViewById(R.id.etArrivee);
        tvDate = findViewById(R.id.tvDate);
        tvPlaces = findViewById(R.id.tvPlaces);
        date = Ui.aujourdhui();

        findViewById(R.id.btnEchanger).setOnClickListener(v -> {
            String d = Ui.texte(etDepart);
            etDepart.setText(Ui.texte(etArrivee));
            etArrivee.setText(d);
        });
        findViewById(R.id.zoneDate).setOnClickListener(v -> choisirDate());
        findViewById(R.id.zonePlaces).setOnClickListener(v -> choisirPlaces());
        findViewById(R.id.btnRechercher).setOnClickListener(v -> rechercher(Ui.texte(etDepart), Ui.texte(etArrivee)));
        findViewById(R.id.btnProfil).setOnClickListener(v -> startActivity(new Intent(this, ProfilActivity.class)));
        findViewById(R.id.btnPublierBandeau).setOnClickListener(v ->
                startActivity(new Intent(this, PublierTrajetActivity.class)));

        LinearLayout aller = findViewById(R.id.listeAller);
        for (String lieu : ALLER) ajouterPuce(aller, lieu, ESPRIT);
        LinearLayout retour = findViewById(R.id.listeRetour);
        for (String lieu : RETOUR) ajouterPuce(retour, ESPRIT, lieu);
    }

    private void ajouterPuce(LinearLayout parent, String depart, String arrivee) {
        TextView puce = new TextView(this);
        puce.setText(Ui.lieuCourt(depart) + " → " + Ui.lieuCourt(arrivee));
        puce.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        puce.setTextColor(ContextCompat.getColor(this, R.color.texte));
        puce.setTypeface(puce.getTypeface(), android.graphics.Typeface.BOLD);
        puce.setBackgroundResource(R.drawable.bg_pill);
        int h = dp(20), v = dp(12);
        puce.setPadding(h, v, h, v);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMarginEnd(dp(8));
        puce.setLayoutParams(lp);
        puce.setOnClickListener(x -> {
            etDepart.setText(depart);
            etArrivee.setText(arrivee);
            rechercher(depart, arrivee);
        });
        parent.addView(puce);
    }

    private void choisirDate() {
        Calendar c = Calendar.getInstance();
        DatePickerDialog d = new DatePickerDialog(this, (view, a, m, j) -> {
            date = String.format(Locale.FRANCE, "%04d-%02d-%02d", a, m + 1, j);
            tvDate.setText(Ui.jourChoisi(date));
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        d.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        d.show();
    }

    private void choisirPlaces() {
        String[] choix = {Ui.passagers(1), Ui.passagers(2), Ui.passagers(3), Ui.passagers(4)};
        new AlertDialog.Builder(this)
                .setTitle("Nombre de passagers")
                .setItems(choix, (dlg, which) -> {
                    places = which + 1;
                    tvPlaces.setText(Ui.passagers(places));
                })
                .show();
    }

    private void rechercher(String depart, String arrivee) {
        Intent i = new Intent(this, ResultatsActivity.class);
        i.putExtra(EXTRA_DEPART, depart);
        i.putExtra(EXTRA_ARRIVEE, arrivee);
        i.putExtra(EXTRA_DATE, date);
        i.putExtra(EXTRA_PLACES, places);
        startActivity(i);
    }

    private int dp(int valeur) {
        return (int) (valeur * getResources().getDisplayMetrics().density);
    }
}

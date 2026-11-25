package com.example.covoitgo.view;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.covoitgo.R;
import com.example.covoitgo.controller.AuthController;
import com.example.covoitgo.controller.ResultCallback;
import com.example.covoitgo.controller.TrajetController;
import com.example.covoitgo.controller.VehiculeController;
import com.example.covoitgo.model.Requetes;
import com.example.covoitgo.model.Trajet;
import com.example.covoitgo.model.Utilisateur;
import com.example.covoitgo.model.Vehicule;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** Vue (maquette 5) : Gerer les trajets (publier) + Estimer le prix de la place */
public class PublierTrajetActivity extends AppCompatActivity {

    private EditText etDepart, etArrivee, etPrix;
    private TextView tvDate, tvHeure, tvNbPlaces, tvConseil, tvEtatConducteur;
    private CheckBox cbRecurrent;
    private View formulaire, barreBas, carteConducteur, zoneJours, tvAideRecurrence;
    private ProgressBar progress;
    private Button btnPublier;

    private String date;
    private String heure = "07:30";
    private int nbPlaces = 3;
    private int maxPlaces = 4;
    private Double prixMax;
    private String prixAuto = "";
    private final Map<String, Boolean> jours = new LinkedHashMap<>();
    private final Map<String, TextView> vuesJours = new LinkedHashMap<>();

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable estimation = this::estimer;

    private TrajetController trajetController;
    private AuthController authController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publier_trajet);
        trajetController = new TrajetController(this);
        authController = new AuthController(this);

        etDepart = findViewById(R.id.etDepart);
        etArrivee = findViewById(R.id.etArrivee);
        etPrix = findViewById(R.id.etPrix);
        tvDate = findViewById(R.id.tvDate);
        tvHeure = findViewById(R.id.tvHeure);
        tvNbPlaces = findViewById(R.id.tvNbPlaces);
        tvConseil = findViewById(R.id.tvConseil);
        tvEtatConducteur = findViewById(R.id.tvEtatConducteur);
        cbRecurrent = findViewById(R.id.cbRecurrent);
        formulaire = findViewById(R.id.formulaire);
        barreBas = findViewById(R.id.barreBas);
        carteConducteur = findViewById(R.id.carteConducteur);
        zoneJours = findViewById(R.id.zoneJours);
        tvAideRecurrence = findViewById(R.id.tvAideRecurrence);
        progress = findViewById(R.id.progress);
        btnPublier = findViewById(R.id.btnPublier);

        date = Ui.aujourdhui();
        tvDate.setText(Ui.jourChoisi(date));
        tvHeure.setText(heure);
        tvNbPlaces.setText(String.valueOf(nbPlaces));

        // Bouton retour
        findViewById(R.id.btnRetour).setOnClickListener(v -> finish());

        findViewById(R.id.btnEchanger).setOnClickListener(v -> {
            String d = Ui.texte(etDepart);
            etDepart.setText(Ui.texte(etArrivee));
            etArrivee.setText(d);
        });
        findViewById(R.id.zoneDate).setOnClickListener(v -> choisirDate());
        findViewById(R.id.zoneHeure).setOnClickListener(v -> choisirHeure());
        findViewById(R.id.btnMoins).setOnClickListener(v -> changerPlaces(-1));
        findViewById(R.id.btnPlus).setOnClickListener(v -> changerPlaces(1));
        findViewById(R.id.btnVehicule).setOnClickListener(v -> startActivity(new Intent(this, VehiculeActivity.class)));
        btnPublier.setOnClickListener(v -> publier());

        TextWatcher surLieu = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
            @Override public void onTextChanged(CharSequence s, int a, int b, int c) {}
            @Override public void afterTextChanged(Editable s) {
                handler.removeCallbacks(estimation);
                handler.postDelayed(estimation, 700);
            }
        };
        etDepart.addTextChangedListener(surLieu);
        etArrivee.addTextChangedListener(surLieu);

        // Jours de la recurrence (codes envoyes au backend)
        jour("L", R.id.jourL);
        jour("MA", R.id.jourMA);
        jour("ME", R.id.jourME);
        jour("J", R.id.jourJ);
        jour("V", R.id.jourV);
        jour("S", R.id.jourS);
        cbRecurrent.setOnCheckedChangeListener((b, coche) -> {
            Ui.visible(zoneJours, coche);
            Ui.visible(tvAideRecurrence, coche);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        verifierConducteur();
    }

    /** Le formulaire n'est accessible qu'a un conducteur valide par l'administration. */
    private void verifierConducteur() {
        authController.rafraichir(new ResultCallback<Utilisateur>() {
            @Override
            public void onSuccess(Utilisateur u) { afficherSelonStatut(u); }

            @Override
            public void onError(String message) { afficherSelonStatut(authController.getUtilisateurConnecte()); }
        });
    }

    private void afficherSelonStatut(Utilisateur u) {
        boolean valide = u != null && u.estVerifie();
        Ui.visible(formulaire, valide);
        Ui.visible(barreBas, valide);
        Ui.visible(carteConducteur, !valide);
        if (valide) {
            chargerVehicule();
            return;
        }
        String statut = u == null ? "" : u.getStatutConducteur();
        Button btnVehicule = findViewById(R.id.btnVehicule);
        if ("EN_ATTENTE".equals(statut)) {
            tvEtatConducteur.setText("Votre véhicule est enregistré. Votre compte conducteur est en attente de validation par l'administration d'Esprit.");
            btnVehicule.setText("Voir mon véhicule");
        } else if ("REFUSE".equals(statut)) {
            tvEtatConducteur.setText("Votre compte conducteur a été refusé. Contactez l'administration.");
            btnVehicule.setText("Voir mon véhicule");
        } else {
            tvEtatConducteur.setText("Pour publier un trajet, enregistrez d'abord votre véhicule. L'administration validera ensuite votre compte conducteur.");
            btnVehicule.setText("Enregistrer mon véhicule");
        }
    }

    private void chargerVehicule() {
        new VehiculeController(this).chargerMesVehicules(new ResultCallback<List<Vehicule>>() {
            @Override
            public void onSuccess(List<Vehicule> liste) {
                if (liste != null && !liste.isEmpty()) {
                    maxPlaces = liste.get(liste.size() - 1).getNbPlaces();
                    if (nbPlaces > maxPlaces) nbPlaces = maxPlaces;
                    tvNbPlaces.setText(String.valueOf(nbPlaces));
                }
            }

            @Override
            public void onError(String message) { /* on garde la limite par defaut */ }
        });
    }

    private void jour(String code, int idVue) {
        TextView tv = findViewById(idVue);
        jours.put(code, false);
        vuesJours.put(code, tv);
        afficherJour(code);
        tv.setOnClickListener(v -> {
            jours.put(code, !jours.get(code));
            afficherJour(code);
        });
    }

    private void afficherJour(String code) {
        TextView tv = vuesJours.get(code);
        boolean actif = jours.get(code);
        tv.setBackgroundResource(actif ? R.drawable.bg_rond_rouge : R.drawable.bg_rond_bordure);
        tv.setTextColor(ContextCompat.getColor(this, actif ? R.color.white : R.color.texte));
    }

    private void changerPlaces(int delta) {
        int n = nbPlaces + delta;
        if (n < 1 || n > maxPlaces) {
            if (n > maxPlaces) Ui.toast(this, "Votre véhicule a " + maxPlaces + " places passagers");
            return;
        }
        nbPlaces = n;
        tvNbPlaces.setText(String.valueOf(n));
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

    private void choisirHeure() {
        int h = Integer.parseInt(heure.substring(0, 2));
        int m = Integer.parseInt(heure.substring(3, 5));
        new TimePickerDialog(this, (view, hh, mm) -> {
            heure = String.format(Locale.FRANCE, "%02d:%02d", hh, mm);
            tvHeure.setText(heure);
        }, h, m, true).show();
    }

    private void estimer() {
        String depart = Ui.texte(etDepart), arrivee = Ui.texte(etArrivee);
        if (depart.isEmpty() || arrivee.isEmpty()) return;
        trajetController.estimerPrix(depart, arrivee, new ResultCallback<Requetes.PrixEstime>() {
            @Override
            public void onSuccess(Requetes.PrixEstime p) {
                prixMax = p.getPrixMax();
                tvConseil.setText("Prix conseillé pour ce trajet : " + Ui.prix(p.getPrixConseille())
                        + " · maximum " + Ui.prix(p.getPrixMax()));
                // On remplit le prix tant que l'utilisateur ne l'a pas modifie lui-meme
                String actuel = Ui.texte(etPrix);
                if (actuel.isEmpty() || actuel.equals(prixAuto)) {
                    prixAuto = Ui.montant(p.getPrixConseille());
                    etPrix.setText(prixAuto);
                }
            }

            @Override
            public void onError(String message) { tvConseil.setText(message); }
        });
    }

    private void publier() {
        List<String> joursCoches = null;
        if (cbRecurrent.isChecked()) {
            joursCoches = new ArrayList<>();
            for (Map.Entry<String, Boolean> e : jours.entrySet()) {
                if (e.getValue()) joursCoches.add(e.getKey());
            }
        }
        Ui.chargement(progress, true);
        btnPublier.setEnabled(false);
        trajetController.publier(Ui.texte(etDepart), Ui.texte(etArrivee), date, heure, nbPlaces,
                Ui.texte(etPrix), prixMax, joursCoches, new ResultCallback<Trajet>() {
                    @Override
                    public void onSuccess(Trajet t) {
                        Ui.toast(PublierTrajetActivity.this, "Trajet publié : " + Ui.lieuCourt(t.getVilleDepart())
                                + " → " + Ui.lieuCourt(t.getVilleArrivee()));
                        startActivity(new Intent(PublierTrajetActivity.this, MesTrajetsActivity.class));
                        finish();
                    }

                    @Override
                    public void onError(String message) {
                        Ui.chargement(progress, false);
                        btnPublier.setEnabled(true);
                        Ui.toast(PublierTrajetActivity.this, message);
                    }
                });
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(estimation);
        super.onDestroy();
    }
}
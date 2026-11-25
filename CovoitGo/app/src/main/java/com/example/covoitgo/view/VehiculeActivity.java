package com.example.covoitgo.view;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.covoitgo.R;
import com.example.covoitgo.controller.AuthController;
import com.example.covoitgo.controller.ResultCallback;
import com.example.covoitgo.controller.VehiculeController;
import com.example.covoitgo.model.Utilisateur;
import com.example.covoitgo.model.Vehicule;

import java.util.List;

/** Vue : Enregistrer son vehicule */
public class VehiculeActivity extends AppCompatActivity {

    private EditText etMarque, etModele, etImmatriculation, etCouleur, etNbPlaces;
    private TextView tvMesVehicules, tvStatut;
    private ProgressBar progress;
    private Button btnEnregistrer;
    private VehiculeController controller;
    private AuthController authController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicule);
        controller = new VehiculeController(this);
        authController = new AuthController(this);

        etMarque = findViewById(R.id.etMarque);
        etModele = findViewById(R.id.etModele);
        etImmatriculation = findViewById(R.id.etImmatriculation);
        etCouleur = findViewById(R.id.etCouleur);
        etNbPlaces = findViewById(R.id.etNbPlaces);
        tvMesVehicules = findViewById(R.id.tvMesVehicules);
        tvStatut = findViewById(R.id.tvStatut);
        progress = findViewById(R.id.progress);
        btnEnregistrer = findViewById(R.id.btnEnregistrer);

        findViewById(R.id.btnRetour).setOnClickListener(v -> finish());
        btnEnregistrer.setOnClickListener(v -> enregistrer());
        afficherStatut(authController.getUtilisateurConnecte());
        charger();
    }

    private void afficherStatut(Utilisateur u) {
        String statut = u.getStatutConducteur();
        if ("VALIDE".equals(statut)) tvStatut.setText("Conducteur validé : vous pouvez publier des trajets.");
        else if ("EN_ATTENTE".equals(statut)) tvStatut.setText("En attente de validation par l'administration d'Esprit.");
        else if ("REFUSE".equals(statut)) tvStatut.setText("Compte conducteur refusé. Contactez l'administration.");
        else tvStatut.setText("Enregistrez votre véhicule : l'administration validera ensuite votre compte conducteur.");
    }

    private void charger() {
        controller.chargerMesVehicules(new ResultCallback<List<Vehicule>>() {
            @Override
            public void onSuccess(List<Vehicule> liste) {
                if (liste == null || liste.isEmpty()) {
                    tvMesVehicules.setText("Aucun véhicule enregistré.");
                    return;
                }
                StringBuilder sb = new StringBuilder();
                for (Vehicule v : liste) {
                    if (sb.length() > 0) sb.append("\n");
                    sb.append("• ").append(v.getMarque()).append(" ").append(v.getModele());
                    if (v.getCouleur() != null && !v.getCouleur().isEmpty()) sb.append(" · ").append(v.getCouleur());
                    sb.append("\n   ").append(v.getImmatriculation()).append(" · ").append(Ui.places(v.getNbPlaces()));
                }
                tvMesVehicules.setText(sb.toString());
            }

            @Override
            public void onError(String message) {
                tvMesVehicules.setText(message);
            }
        });
    }

    private void enregistrer() {
        Ui.chargement(progress, true);
        btnEnregistrer.setEnabled(false);
        controller.enregistrer(Ui.texte(etMarque), Ui.texte(etModele), Ui.texte(etImmatriculation),
                Ui.texte(etCouleur), Ui.texte(etNbPlaces), new ResultCallback<Vehicule>() {
                    @Override
                    public void onSuccess(Vehicule v) {
                        Ui.chargement(progress, false);
                        btnEnregistrer.setEnabled(true);
                        Ui.toast(VehiculeActivity.this, "Véhicule enregistré");
                        etMarque.setText("");
                        etModele.setText("");
                        etImmatriculation.setText("");
                        etCouleur.setText("");
                        etNbPlaces.setText("");
                        charger();
                        // Le statut conducteur a change cote serveur : on met la session a jour
                        authController.rafraichir(new ResultCallback<Utilisateur>() {
                            @Override
                            public void onSuccess(Utilisateur u) { afficherStatut(u); }

                            @Override
                            public void onError(String message) { }
                        });
                    }

                    @Override
                    public void onError(String message) {
                        Ui.chargement(progress, false);
                        btnEnregistrer.setEnabled(true);
                        Ui.toast(VehiculeActivity.this, message);
                    }
                });
    }
}

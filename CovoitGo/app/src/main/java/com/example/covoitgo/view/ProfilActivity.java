package com.example.covoitgo.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.covoitgo.R;
import com.example.covoitgo.controller.AuthController;
import com.example.covoitgo.controller.ResultCallback;
import com.example.covoitgo.model.Utilisateur;

/** Vue (onglet Profil) : Gerer profil + acces conducteur */
public class ProfilActivity extends AppCompatActivity {

    private EditText etNom, etPrenom, etTelephone, etMotDePasse;
    private ProgressBar progress;
    private Button btnEnregistrer;
    private AuthController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);
        Navigation.installer(this, R.id.nav_profil);
        controller = new AuthController(this);

        etNom = findViewById(R.id.etNom);
        etPrenom = findViewById(R.id.etPrenom);
        etTelephone = findViewById(R.id.etTelephone);
        etMotDePasse = findViewById(R.id.etMotDePasse);
        progress = findViewById(R.id.progress);
        btnEnregistrer = findViewById(R.id.btnEnregistrer);

        findViewById(R.id.ligneVehicule).setOnClickListener(v -> startActivity(new Intent(this, VehiculeActivity.class)));
        findViewById(R.id.ligneMesTrajets).setOnClickListener(v -> startActivity(new Intent(this, MesTrajetsActivity.class)));
        findViewById(R.id.ligneDemandes).setOnClickListener(v -> startActivity(new Intent(this, DemandesActivity.class)));
        btnEnregistrer.setOnClickListener(v -> enregistrer());
        findViewById(R.id.btnDeconnexion).setOnClickListener(v -> {
            controller.seDeconnecter();
            Intent i = new Intent(this, LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        });

        Utilisateur u = controller.getUtilisateurConnecte();
        etNom.setText(u.getNom());
        etPrenom.setText(u.getPrenom());
        etTelephone.setText(u.getTelephone());
        afficher(u);
    }

    @Override
    protected void onResume() {
        super.onResume();
        controller.rafraichir(new ResultCallback<Utilisateur>() {
            @Override
            public void onSuccess(Utilisateur u) { afficher(u); }

            @Override
            public void onError(String message) { /* affichage local conserve */ }
        });
    }

    private void afficher(Utilisateur u) {
        ((TextView) findViewById(R.id.tvInitiales)).setText(u.getInitiales());
        ((TextView) findViewById(R.id.tvNom)).setText(u.getNomComplet());
        ((TextView) findViewById(R.id.tvEmail)).setText(u.getEmail());
        ((TextView) findViewById(R.id.tvNote)).setText(Ui.note(u, true));

        TextView tvStatut = findViewById(R.id.tvStatutConducteur);
        String statut = u.getStatutConducteur();
        if ("VALIDE".equals(statut)) tvStatut.setText("Conducteur validé · e-mail Esprit vérifié");
        else if ("EN_ATTENTE".equals(statut)) tvStatut.setText("Compte conducteur en attente de validation par l'administration.");
        else if ("REFUSE".equals(statut)) tvStatut.setText("Compte conducteur refusé. Contactez l'administration.");
        else tvStatut.setText("Vous avez une voiture ? Enregistrez-la pour publier des trajets.");

        boolean conducteur = u.estConducteur();
        Ui.visible(findViewById(R.id.ligneMesTrajets), conducteur);
        Ui.visible(findViewById(R.id.ligneDemandes), conducteur);
    }

    private void enregistrer() {
        Ui.chargement(progress, true);
        btnEnregistrer.setEnabled(false);
        controller.modifierProfil(Ui.texte(etNom), Ui.texte(etPrenom), Ui.texte(etTelephone),
                etMotDePasse.getText().toString(), new ResultCallback<Utilisateur>() {
                    @Override
                    public void onSuccess(Utilisateur u) {
                        Ui.chargement(progress, false);
                        btnEnregistrer.setEnabled(true);
                        etMotDePasse.setText("");
                        afficher(u);
                        Ui.toast(ProfilActivity.this, "Profil mis à jour");
                    }

                    @Override
                    public void onError(String message) {
                        Ui.chargement(progress, false);
                        btnEnregistrer.setEnabled(true);
                        Ui.toast(ProfilActivity.this, message);
                    }
                });
    }
}

package com.example.covoitgo.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.covoitgo.R;
import com.example.covoitgo.controller.AdminController;
import com.example.covoitgo.controller.AuthController;
import com.example.covoitgo.controller.ResultCallback;
import com.example.covoitgo.model.Signalement;
import com.example.covoitgo.model.Utilisateur;

import java.util.ArrayList;
import java.util.List;

/** Vue : Valider un conducteur + Gerer les reclamations (administrateur) */
public class AdminActivity extends AppCompatActivity {

    private TextView ongletConducteurs, ongletReclamations, tvVide;
    private ProgressBar progress;
    private ListView listView;
    private boolean modeConducteurs = true;

    private final AdminController controller = new AdminController();
    private final List<Utilisateur> conducteurs = new ArrayList<>();
    private final List<Signalement> signalements = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        ongletConducteurs = findViewById(R.id.ongletConducteurs);
        ongletReclamations = findViewById(R.id.ongletReclamations);
        tvVide = findViewById(R.id.tvVide);
        progress = findViewById(R.id.progress);
        listView = findViewById(R.id.listView);

        ongletConducteurs.setOnClickListener(v -> { modeConducteurs = true; charger(); });
        ongletReclamations.setOnClickListener(v -> { modeConducteurs = false; charger(); });
        findViewById(R.id.tvDeconnexion).setOnClickListener(v -> {
            new AuthController(this).seDeconnecter();
            Intent i = new Intent(this, LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        charger();
    }

    private void charger() {
        Ui.onglet(ongletConducteurs, modeConducteurs);
        Ui.onglet(ongletReclamations, !modeConducteurs);
        Ui.chargement(progress, true);
        if (modeConducteurs) {
            controller.chargerConducteursEnAttente(new ResultCallback<List<Utilisateur>>() {
                @Override
                public void onSuccess(List<Utilisateur> resultat) {
                    Ui.chargement(progress, false);
                    conducteurs.clear();
                    if (resultat != null) conducteurs.addAll(resultat);
                    Ui.listeVide(tvVide, conducteurs.isEmpty(), "Aucun conducteur en attente de validation.");
                    listView.setAdapter(new CarteAdapter<>(AdminActivity.this, R.layout.item_carte, conducteurs,
                            AdminActivity.this::remplirConducteur));
                }

                @Override
                public void onError(String message) { erreur(message); }
            });
        } else {
            controller.chargerSignalements(new ResultCallback<List<Signalement>>() {
                @Override
                public void onSuccess(List<Signalement> resultat) {
                    Ui.chargement(progress, false);
                    signalements.clear();
                    if (resultat != null) signalements.addAll(resultat);
                    Ui.listeVide(tvVide, signalements.isEmpty(), "Aucune réclamation.");
                    listView.setAdapter(new CarteAdapter<>(AdminActivity.this, R.layout.item_carte, signalements,
                            AdminActivity.this::remplirSignalement));
                }

                @Override
                public void onError(String message) { erreur(message); }
            });
        }
    }

    private void erreur(String message) {
        Ui.chargement(progress, false);
        Ui.listeVide(tvVide, true, message);
    }

    private void remplirConducteur(View v, Utilisateur u) {
        VueCarte c = VueCarte.de(v);
        c.tvHaut.setText(u.getEmail());
        Ui.badge(c.tvBadge, "EN_ATTENTE");
        c.tvTitre.setText(u.getNomComplet());
        c.tvLigne1.setText("+216 " + u.getTelephone());
        c.ligne2("Véhicule enregistré · demande de compte conducteur");
        ResultCallback<Utilisateur> retour = new ResultCallback<Utilisateur>() {
            @Override
            public void onSuccess(Utilisateur maj) {
                Ui.toast(AdminActivity.this, maj.getNomComplet() + " : " + Ui.libelle(maj.getStatutConducteur()));
                charger();
            }

            @Override
            public void onError(String message) { Ui.toast(AdminActivity.this, message); }
        };
        c.bouton1("Refuser", x -> controller.refuserConducteur(u, retour));
        c.bouton2("Valider", x -> controller.validerConducteur(u, retour));
    }

    private void remplirSignalement(View v, Signalement s) {
        VueCarte c = VueCarte.de(v);
        c.tvHaut.setText(s.getDateSignalement());
        Ui.badge(c.tvBadge, s.getStatut());
        c.tvTitre.setText(s.getAuteur().getNomCourt() + " signale " + s.getCible().getNomComplet());
        c.tvLigne1.setText(s.getMotif());
        c.ligne2(s.getDecision() == null ? "" : "Décision : " + s.getDecision());
        if (!"TRAITE".equals(s.getStatut())) {
            c.bouton1("Traiter la réclamation", x -> Ui.dialogueTexte(this, "Décision",
                    "Ex. avertissement envoyé au conducteur", decision ->
                            controller.traiterSignalement(s, decision, new ResultCallback<Signalement>() {
                                @Override
                                public void onSuccess(Signalement maj) {
                                    Ui.toast(AdminActivity.this, "Réclamation traitée");
                                    charger();
                                }

                                @Override
                                public void onError(String message) { Ui.toast(AdminActivity.this, message); }
                            })));
        }
    }
}

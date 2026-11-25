package com.example.covoitgo.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.example.covoitgo.R;
import com.example.covoitgo.model.Utilisateur;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/** Outils d'affichage partages par les ecrans (partie Vue) : formats des maquettes, badges, dialogues. */
public final class Ui {

    private static final Locale FR = Locale.FRANCE;

    private Ui() {}

    // ---------- Messages et etats

    public static void toast(Context c, String message) {
        Toast.makeText(c, message, Toast.LENGTH_LONG).show();
    }

    public static void chargement(View progress, boolean visible) {
        progress.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public static void listeVide(TextView tvVide, boolean vide, String message) {
        tvVide.setText(message);
        tvVide.setVisibility(vide ? View.VISIBLE : View.GONE);
    }

    public static String texte(EditText et) {
        return et.getText().toString().trim();
    }

    // ---------- Formats des maquettes

    /** 4.5 -> "4,500 DT" */
    public static String prix(Double p) {
        return p == null ? "-" : String.format(FR, "%.3f DT", p);
    }

    /** 4.5 -> "4,500" (sans unite) */
    public static String montant(Double p) {
        return p == null ? "" : String.format(FR, "%.3f", p);
    }

    public static boolean estEsprit(String lieu) {
        String l = lieu == null ? "" : lieu.toLowerCase(FR);
        return l.contains("esprit") || l.contains("ghazala");
    }

    /** "Esprit · El Ghazala" -> "Esprit" */
    public static String lieuCourt(String lieu) {
        return estEsprit(lieu) ? "Esprit" : lieu;
    }

    public static String precisionLieu(String lieu, boolean depart) {
        if (estEsprit(lieu)) return "Pôle technologique El Ghazala";
        return depart ? "Départ précisé par le conducteur" : "Arrivée précisée par le conducteur";
    }

    public static String note(Utilisateur u, boolean avecAvis) {
        String n = String.format(FR, "★ %.1f", u.getNoteMoyenne());
        if (avecAvis) return n + " · " + u.getNbEvaluations() + " avis";
        return n + (u.estVerifie() ? " · Vérifié Esprit" : "");
    }

    public static String passagers(int n) {
        return n + (n > 1 ? " passagers" : " passager");
    }

    public static String places(int n) {
        return n + (n > 1 ? " places" : " place");
    }

    /** Date du jour au format "aaaa-mm-jj" */
    public static String aujourdhui() {
        return new SimpleDateFormat("yyyy-MM-dd", FR).format(new Date());
    }

    /** "aaaa-mm-jj hh:mm" deja passe ? */
    public static boolean estPasse(String dateHeure) {
        String maintenant = new SimpleDateFormat("yyyy-MM-dd HH:mm", FR).format(new Date());
        return dateHeure != null && dateHeure.compareTo(maintenant) < 0;
    }

    /** "aaaa-mm-jj" -> "Aujourd'hui" / "Demain" / "Lundi 16 nov." */
    public static String jourCourt(String jour) {
        String relatif = jourRelatif(jour);
        if (relatif != null) return relatif;
        return majuscule(formater(jour, "EEEE d MMM"));
    }

    /** "aaaa-mm-jj" -> "Aujourd'hui, jeudi 12 novembre" / "Lundi 16 novembre" */
    public static String jourTitre(String jour) {
        String complet = formater(jour, "EEEE d MMMM");
        String relatif = jourRelatif(jour);
        return relatif != null ? relatif + ", " + complet : majuscule(complet);
    }

    /** "aaaa-mm-jj" -> "Aujourd'hui" / "Lun. 16 nov." (ecran Publier) */
    public static String jourChoisi(String jour) {
        String relatif = jourRelatif(jour);
        return relatif != null ? relatif : majuscule(formater(jour, "EEE d MMM"));
    }

    private static String jourRelatif(String jour) {
        Calendar c = Calendar.getInstance();
        String auj = new SimpleDateFormat("yyyy-MM-dd", FR).format(c.getTime());
        c.add(Calendar.DAY_OF_MONTH, 1);
        String demain = new SimpleDateFormat("yyyy-MM-dd", FR).format(c.getTime());
        if (auj.equals(jour)) return "Aujourd'hui";
        if (demain.equals(jour)) return "Demain";
        return null;
    }

    private static String formater(String jour, String motif) {
        try {
            Date d = new SimpleDateFormat("yyyy-MM-dd", FR).parse(jour);
            return new SimpleDateFormat(motif, FR).format(d);
        } catch (ParseException | NullPointerException e) {
            return jour == null ? "" : jour;
        }
    }

    private static String majuscule(String s) {
        return s == null || s.isEmpty() ? "" : s.substring(0, 1).toUpperCase(FR) + s.substring(1);
    }

    // ---------- Badges de statut (Acceptee, En attente, Refusee...)

    public static String libelle(String statut) {
        if (statut == null) return "";
        switch (statut) {
            case "EN_ATTENTE": return "En attente";
            case "ACCEPTEE": return "Acceptée";
            case "REFUSEE": return "Refusée";
            case "ANNULEE": return "Annulée";
            case "OUVERT": return "Ouvert";
            case "COMPLET": return "Complet";
            case "ANNULE": return "Annulé";
            case "VALIDE": return "Validé";
            case "REFUSE": return "Refusé";
            case "TRAITE": return "Traité";
            default: return statut;
        }
    }

    public static void badge(TextView tv, String statut) {
        int fond, texte;
        switch (statut == null ? "" : statut) {
            case "ACCEPTEE":
            case "OUVERT":
            case "VALIDE":
                fond = R.color.rouge_clair; texte = R.color.rouge; break;
            case "EN_ATTENTE":
            case "COMPLET":
                fond = R.color.beige; texte = R.color.beige_texte; break;
            default:
                fond = R.color.gris_clair; texte = R.color.texte_doux;
        }
        Context c = tv.getContext();
        tv.setText(libelle(statut));
        tv.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(c, fond)));
        tv.setTextColor(ContextCompat.getColor(c, texte));
    }

    /** Puce / onglet selectionne (fond sombre ou blanc) ou non. */
    public static void puce(TextView tv, boolean active) {
        Context c = tv.getContext();
        tv.setBackgroundResource(active ? R.drawable.bg_pill_noir : R.drawable.bg_pill);
        tv.setTextColor(ContextCompat.getColor(c, active ? R.color.white : R.color.texte));
    }

    public static void onglet(TextView tv, boolean actif) {
        Context c = tv.getContext();
        if (actif) tv.setBackgroundResource(R.drawable.bg_segment_actif);
        else tv.setBackground(null);
        tv.setTextColor(ContextCompat.getColor(c, actif ? R.color.texte : R.color.texte_doux));
    }

    public static void visible(View v, boolean visible) {
        v.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    // ---------- Boites de dialogue

    public interface SurEvaluation { void valider(int note, String commentaire); }
    public interface SurTexte { void valider(String texte); }

    public static void dialogueEvaluation(Activity a, String nomCible, SurEvaluation action) {
        LinearLayout box = conteneur(a);
        RatingBar rating = new RatingBar(a);
        rating.setNumStars(5);
        rating.setStepSize(1f);
        rating.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        EditText commentaire = new EditText(a);
        commentaire.setHint("Commentaire (optionnel)");
        box.addView(rating);
        box.addView(commentaire);
        new AlertDialog.Builder(a)
                .setTitle("Évaluer " + nomCible)
                .setView(box)
                .setPositiveButton("Envoyer", (d, w) ->
                        action.valider((int) rating.getRating(), commentaire.getText().toString().trim()))
                .setNegativeButton("Annuler", null)
                .show();
    }

    public static void dialogueTexte(Activity a, String titre, String indice, SurTexte action) {
        LinearLayout box = conteneur(a);
        EditText et = new EditText(a);
        et.setHint(indice);
        et.setMinLines(2);
        et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        box.addView(et);
        new AlertDialog.Builder(a)
                .setTitle(titre)
                .setView(box)
                .setPositiveButton("Envoyer", (d, w) -> action.valider(et.getText().toString()))
                .setNegativeButton("Annuler", null)
                .show();
    }

    public static void confirmer(Activity a, String titre, String message, Runnable action) {
        new AlertDialog.Builder(a)
                .setTitle(titre)
                .setMessage(message)
                .setPositiveButton("Oui", (d, w) -> action.run())
                .setNegativeButton("Non", null)
                .show();
    }

    private static LinearLayout conteneur(Activity a) {
        LinearLayout box = new LinearLayout(a);
        box.setOrientation(LinearLayout.VERTICAL);
        int p = (int) (20 * a.getResources().getDisplayMetrics().density);
        box.setPadding(p, p / 2, p, 0);
        return box;
    }
}

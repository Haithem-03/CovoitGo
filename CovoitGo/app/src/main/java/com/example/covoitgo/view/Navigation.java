package com.example.covoitgo.view;

import android.app.Activity;
import android.content.Intent;

import com.example.covoitgo.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/** Barre de navigation du bas : Rechercher, Publier, Reservations, Profil (maquettes). */
public final class Navigation {

    private Navigation() {}

    public static void installer(Activity activite, int itemSelectionne) {
        BottomNavigationView nav = activite.findViewById(R.id.bottomNav);
        nav.setItemActiveIndicatorEnabled(false);
        nav.setSelectedItemId(itemSelectionne);
        nav.setOnItemSelectedListener(item -> {
            Class<?> cible = ecran(item.getItemId());
            if (cible != null && cible != activite.getClass()) {
                Intent i = new Intent(activite, cible);
                i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                activite.startActivity(i);
                activite.overridePendingTransition(0, 0);
            }
            return false; // la selection reste celle de l'ecran courant
        });
    }

    private static Class<?> ecran(int itemId) {
        if (itemId == R.id.nav_rechercher) return RechercheActivity.class;
        if (itemId == R.id.nav_publier) return PublierTrajetActivity.class;
        if (itemId == R.id.nav_reservations) return MesReservationsActivity.class;
        if (itemId == R.id.nav_profil) return ProfilActivity.class;
        return null;
    }
}

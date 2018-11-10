package fr.esiee.bde.macao.Settings;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.design.widget.Snackbar;

import com.google.firebase.iid.FirebaseInstanceId;

import fr.esiee.bde.macao.MainActivity;
import fr.esiee.bde.macao.R;

import static android.content.Context.CLIPBOARD_SERVICE;

public class SettingsFragment extends PreferenceFragment
{
    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        Preference button = findPreference("about_button");
        button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Snackbar.make(getView(), R.string.about_text, Snackbar.LENGTH_LONG).show();
                return true;
            }
        });

        final String firebaseID = FirebaseInstanceId.getInstance().getInstanceId().toString();
        Preference firebase = findPreference("firebase_button");
        firebase.setSummary(firebaseID);
        firebase.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("firebaseID", firebaseID);
                clipboard.setPrimaryClip(clip);
                Snackbar.make(getView(), "Copié dans le presse-papier", Snackbar.LENGTH_SHORT).show();
                return false;
            }
        });

    }
}
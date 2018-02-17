package be.jssgns.mtype1;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void loadBolus(View view){
        Intent intent = new Intent(this, Bolus.class);
        startActivity(intent);
    }

    public void loadKh(View view){
        Intent intent = new Intent(this, Koolhydraten.class);
        startActivity(intent);
    }


    public void loadStatistieken(View view){
        Intent intent = new Intent(this, Statistieken.class);
        startActivity(intent);
    }

    public void loadSettings(View view){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void sendReport(View view){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        exportDatabse("bolus");
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{prefs.getString("DokterEmail", "(geen ontvanger ingesteld)")});
        i.putExtra(Intent.EXTRA_SUBJECT, "Verslag " + prefs.getString("PatientNaam", "(geen naam ingesteld)"));
        i.putExtra(Intent.EXTRA_STREAM, Uri.parse( "file://" + "mtype1.db"));
        //i.setData(Uri.parse("mailto:" + prefs.getString("DokterEmail", "(geen ontvanger ingesteld)")));
        i.putExtra(Intent.EXTRA_TEXT   , "Automatisch gegenereerd verslag" +
                                       "\n-------------------------------" +
                                       "\nGemiddelde");
        try {
            startActivity(Intent.createChooser(i, "Verslag versturen"));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(MainActivity.this, "Er werd geen e-mail applicatie gevonden :/", Toast.LENGTH_SHORT).show();
        }
    }

    public void exportDatabse(String databaseName) {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String currentDBPath = "//data//"+getPackageName()+"//databases//"+databaseName+"";
                String backupDBPath = "mtype1.db";
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }
        } catch (Exception e) {

        }
    }
}

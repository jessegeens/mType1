package be.jssgns.mtype1;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.database.sqlite.*;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AddBolus extends AppCompatActivity {

    private static final String TAG = Bolus.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bolus);
        setTitle("Waarde toevoegen");
        EditText txtDate = (EditText) findViewById(R.id.txtDate);
        EditText txtTime = (EditText) findViewById(R.id.txtTime);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String curTime = sdf.format(new Date());
        txtDate.setText(curTime.substring(6,8) + "/" + curTime.substring(4, 6) + "/" + curTime.substring(0, 4));
        txtTime.setText(curTime.substring(9, 11) + ":" + curTime.substring(11, 13));
    }

    public void addValues(View view){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        EditText textblsw = (EditText) findViewById(R.id.txtBlsw);
        EditText textBolus = (EditText) findViewById(R.id.txtBolus);
        EditText textMeal = (EditText) findViewById(R.id.txtMeal);
        EditText txtDate = (EditText) findViewById(R.id.txtDate);
        EditText txtTime = (EditText) findViewById(R.id.txtTime);
        String blsw = textblsw.getText().toString();
        String bolus = textBolus.getText().toString();
        String meal = textMeal.getText().toString();
        if(meal == "" && bolus == "" && blsw == ""){
            Toast.makeText(getApplicationContext(), "Gelieve correcte waarden in te vullen", Toast.LENGTH_LONG).show();
            return;
        }
        SQLiteDatabase bolusdb = openOrCreateDatabase("bolus",MODE_PRIVATE,null);
        bolusdb.execSQL("CREATE TABLE IF NOT EXISTS bolustbl(blsw VARCHAR, bolusw VARCHAR, meal VARCHAR, time VARCHAR, id VARCHAR);");
        final Cursor resultSet = bolusdb.rawQuery("Select * from bolustbl",null);
        int id = resultSet.getCount();
        String date = txtDate.getText().toString().substring(6, 10) + txtDate.getText().toString().substring(3, 5) + txtDate.getText().toString().substring(0, 2);
        String time = txtTime.getText().toString().substring(0,2) + txtTime.getText().toString().substring(3, 5);
        String curTime = date + "_" + time;
        bolusdb.execSQL("INSERT INTO bolustbl VALUES('" + blsw + "', '" + bolus + "', '" + meal + "', '" + curTime + "', '" + Integer.toString(id) + "');");
        if(!blsw.equals("")) {
            if (prefs.getInt("lowTarget", 60) > Integer.parseInt(blsw) && prefs.getBoolean("giveWarnings", true)) {
                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
                dlgAlert.setMessage("Neem snelle suikers en controleer uw bloedsuikerwaarde regelmatig tot deze gestegen is!");
                dlgAlert.setTitle("Lage bloedsuiker");
                dlgAlert.setPositiveButton("Oké",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getApplicationContext(), "Waarde toegevoegd", Toast.LENGTH_LONG).show();
                                resultSet.close();
                                finish();
                            }
                        });
                dlgAlert.setCancelable(true);
                dlgAlert.create().show();
            } else if (prefs.getInt("highTarget", 180) < Integer.parseInt(blsw) && prefs.getBoolean("giveWarnings", true)) {
                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
                dlgAlert.setMessage("Uw bloedsuiker is te hoog, controleer glucose regelmatig");
                dlgAlert.setTitle("Hoge bloedsuiker");
                dlgAlert.setPositiveButton("Oké",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getApplicationContext(), "Waarde toegevoegd", Toast.LENGTH_LONG).show();
                                resultSet.close();
                                finish();
                            }
                        });
                dlgAlert.setCancelable(true);
                dlgAlert.create().show();
            } else {
                Toast.makeText(getApplicationContext(), "Waarde toegevoegd", Toast.LENGTH_LONG).show();
                resultSet.close();
                finish();
            }
        }
        else {
            Toast.makeText(getApplicationContext(), "Waarde toegevoegd", Toast.LENGTH_LONG).show();
            resultSet.close();
            finish();
        }
    }
}

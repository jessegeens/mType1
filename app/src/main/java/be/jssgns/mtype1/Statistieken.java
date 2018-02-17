package be.jssgns.mtype1;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

public class Statistieken extends AppCompatActivity {

    private static final String TAG = Bolus.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistieken);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        setTitle("Statistieken");

        TextView txtTotal = (TextView) findViewById(R.id.txtMeasure);
        TextView txt50 = (TextView) findViewById(R.id.txtGem50);
        TextView txt100 = (TextView) findViewById(R.id.txtGem100);
        TextView txtInTarget =  (TextView) findViewById(R.id.txtInTarget);
        SQLiteDatabase khdb = openOrCreateDatabase("khdb",MODE_PRIVATE,null);
        SQLiteDatabase bolusdb = openOrCreateDatabase("bolus",MODE_PRIVATE,null);
        Cursor resultSet = bolusdb.rawQuery("Select * from bolustbl",null);

        resultSet.moveToLast();
        txtTotal.setText(Integer.toString(resultSet.getCount()));
        calcAverage(50, txt50, resultSet);
        calcAverage(100, txt100, resultSet);
        calcInTarget(Integer.parseInt(prefs.getString("targetLow", "60")), Integer.parseInt(prefs.getString("targetHigh", "180")), txtInTarget, resultSet);
        resultSet.close();
    }

    public void calcAverage(int range, TextView textView, Cursor table){
        int total = table.getCount();
        table.moveToLast();
        int temp1 = 0;
        if(total == 0){
            textView.setText("N/A");
        }
        else {
            int counter = 0;
            for(int i = 0; i < range && i < total; i++){
                if(!table.getString(0).equals("")) {
                    temp1 += Integer.parseInt(table.getString(0));
                    counter += 1;
                }
                else{
                    range += 1;
                }
                table.moveToPrevious();
            }
            String average;
            if(!Integer.toString(counter).equals("0")) {
                average = Integer.toString(temp1 / counter);
            }
            else{
                average = "N/A";
            }
            textView.setText(average);
        }
    }

    public void calcInTarget(int lower, int upper, TextView textView, Cursor table){
        table.moveToLast();
        int total = 0;
        int inRange = 0;
        for(int i = 0; i < table.getCount() && i < 200; i++){
            String valueString = table.getString(0);
            if(!valueString.equals("")){
                int value = Integer.parseInt(valueString);
                if(value > lower && value < upper){
                    inRange += 1;
                }
                total += 1;
            }
            table.moveToPrevious();
        }
        if(!Integer.toString(table.getCount()).equals("0")) {
            int percentage = inRange * 100 / total;
            Log.d(TAG, "In Range: " + Integer.toString(inRange) + "; Total: " + Integer.toString(total));
            textView.setText(Integer.toString(percentage) + "%");
        }
        else{
            textView.setText("N/A");
        }
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}

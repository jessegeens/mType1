package be.jssgns.mtype1;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.content.Intent;
import android.database.sqlite.*;
import android.database.Cursor;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.graphics.Point;
import android.preference.PreferenceManager;


public class Bolus extends AppCompatActivity {
    private static final String TAG = Bolus.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bolus);
        setTitle("Bloedsuikerwaarden");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), AddBolus.class));
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public String formatDate(String date){
        return date.substring(6, 8) + "/" + date.substring(4, 6) + "/" + date.substring(0, 4);
    }
    public String formatTime(String date){
        return date.substring(9, 11) + ":" + date.substring(11, 13) /*+ ":" + date.substring(13, 15)*/;
    }

    public void drop2(TableLayout tblMain){
        TableRow rowk = new TableRow(this);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        rowk.setLayoutParams(lp);
        TextView te = new TextView(this);
        te.setText("\n\n");
        rowk.addView(te);
        tblMain.addView(rowk);
    }
    public TextView colorValue(TextView textView){
        if(!textView.getText().equals("")) {
            int value = Integer.parseInt(textView.getText().toString());
            if (value <= 60) {
                textView.setTextColor(Color.rgb(219, 49, 30));
            } else if (value <= 80) {
                textView.setTextColor(Color.rgb(219, 178, 30));
            } else if (value > 80 && value < 150) {
                textView.setTextColor(Color.rgb(35, 186, 105));
            } else if (value >= 180) {
                textView.setTextColor(Color.rgb(219, 49, 30));
            } else if (value >= 150) {
                textView.setTextColor(Color.rgb(219, 178, 30));
            }
        }
        return textView;
    }

    public void onResume() {
        super.onResume();  // Always call the superclass method first
        TableLayout tblMain = (TableLayout) findViewById(R.id.tblBolusMain);
        Log.d(TAG, "Activity resumed");
        tblMain.removeAllViews();
        runTable();
    }

    public void runTable(){
        SQLiteDatabase bolusdb = openOrCreateDatabase("bolus",MODE_PRIVATE,null);
        bolusdb.execSQL("CREATE TABLE IF NOT EXISTS bolustbl(blsw VARCHAR, bolusw VARCHAR, meal VARCHAR, time VARCHAR, id VARCHAR);");
        Cursor resultSet = bolusdb.rawQuery("Select * from bolustbl",null);
        resultSet.moveToLast();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        TableLayout tblMain = (TableLayout) findViewById(R.id.tblBolusMain);
        //Make sure table doesnt display over title
        drop2(tblMain);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        //Toast.makeText(getApplicationContext(), "kanker tabel geladen, lengte is " + resultSet.getCount(), Toast.LENGTH_LONG).show();

        if(resultSet.getCount() == 0) {
            //Nog geen waarden
        }
        else {
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            Log.d(TAG, "Screen width: " + Integer.toString(size.x));
            String date = resultSet.getString(3).substring(0, 8);
            TableRow row2S = new TableRow(this);
            TextView textViewDateS = new TextView(this);
            TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            params.span = 4;
            textViewDateS.setText(formatDate(date));
            textViewDateS.setTextSize(TypedValue.COMPLEX_UNIT_SP,19);
            textViewDateS.setLayoutParams(params);
            textViewDateS.setTextColor(getResources().getColor(R.color.colorPrimary));
            row2S.addView(textViewDateS);
            tblMain.addView(row2S);

            for (int i = resultSet.getCount(); i > resultSet.getCount() - 50 && i > 0; i--) {
                TableRow row = new TableRow(this);
                String curDate = resultSet.getString(3).substring(0, 8);
                if(!curDate.equals(date)){
                    Log.d(TAG, "Curdate: " + curDate + "; date: " + date);
                    TableRow row2 = new TableRow(this);
                    TextView textViewDate = new TextView(this);
                    textViewDate.setText("\n" + formatDate(curDate));
                    textViewDate.setTextSize(TypedValue.COMPLEX_UNIT_SP,19);
                    TableRow.LayoutParams params2 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);//(TableRow.LayoutParams)textViewDate.getLayoutParams();
                    params2.span = 4;
                    textViewDate.setLayoutParams(params2);
                    textViewDate.setTextColor(getResources().getColor(R.color.colorPrimary));
                    date = curDate;
                    row2.addView(textViewDate);
                    tblMain.addView(row2);
                }
                row.setLayoutParams(lp);
                TextView textViewblsw = new TextView(this);
                TextView textViewbolusw = new TextView(this);
                TextView textViewCurT = new TextView(this);
                TextView textViewMeal = new TextView(this);
                TableRow.LayoutParams txtCurTPar = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                TableRow.LayoutParams txtBlswPar = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                TableRow.LayoutParams txtBoluswPar = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                textViewMeal.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
                textViewblsw.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
                textViewCurT.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
                textViewbolusw.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
                textViewblsw.setText(resultSet.getString(0));
                textViewbolusw.setText(resultSet.getString(1));
                textViewMeal.setText(resultSet.getString(2));
                textViewCurT.setText(formatTime(resultSet.getString(3)));
                txtCurTPar.width = size.x/5;
                textViewCurT.setLayoutParams(txtCurTPar);
                txtBlswPar.width = size.x/7;
                textViewblsw.setLayoutParams(txtBlswPar);
                txtBoluswPar.width = size.x/10;
                textViewbolusw.setLayoutParams(txtBoluswPar);
                Resources resource = this.getResources();
                //textViewblsw.setBackgroundColor(resource.getColor(R.color.colorPrimaryDark));
                if(prefs.getBoolean("example_switch", true)){
                    row.addView(colorValue(textViewblsw));
                }
                else{
                    row.addView(textViewblsw);
                }
                row.addView(textViewCurT);
                row.addView(textViewbolusw);
                row.addView(textViewMeal);
                tblMain.addView(row);
                resultSet.moveToPrevious();
            }
        }

        resultSet.close();
    }

}

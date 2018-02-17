package be.jssgns.mtype1;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.graphics.Typeface;
import android.media.Image;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class Koolhydraten extends AppCompatActivity {
    private static final String TAG = Bolus.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kh);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Koolhydraten");
        final SQLiteDatabase khdb = openOrCreateDatabase("khdb",MODE_PRIVATE,null);
        if(!tableExists("khdb", khdb)){
            createTable(khdb);
        }
        Cursor resultSet = khdb.rawQuery("Select * from khdb",null);
        resultSet.moveToFirst();
        final TableLayout tblMain = (TableLayout) findViewById(R.id.tblKHMain);
        addMessage(tblMain, "Gelieve een zoekterm in te geven...");
        EditText txtSearch = (EditText) findViewById(R.id.txtSearch);
        txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String k = s.toString();
                tblMain.removeAllViews();
                if(k.length() == 0){
                    addMessage(tblMain, "Gelieve een zoekterm in te geven...");
                }
                else {
                    if(k.length() > 1) {
                        k = Character.toUpperCase(k.charAt(0)) + k.substring(1);
                    }
                    else{
                        k = String.valueOf(Character.toUpperCase(k.charAt(0)));
                    }
                    Cursor resultSet = khdb.rawQuery("Select * from khdb WHERE instr(name, '" + k + "') > 0;", null);
                    Search(resultSet, tblMain);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    public boolean tableExists(String tableName, SQLiteDatabase mDatabase) {
        Cursor cursor = mDatabase.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '"+tableName+"'", null);
        if(cursor!=null) {
            if(cursor.getCount()>0) {
                cursor.close();
                return true;
            }
            cursor.close();
        }
        return false;
    }

    public void createTable(SQLiteDatabase database){
        database.execSQL("CREATE TABLE IF NOT EXISTS khdb(id INT, name VARCHAR, kh INT, sugar INT);");
        tblAdd(database, "khdb", 0, "Frieten", 41, 0);
        tblAdd(database, "khdb", 1, "Broodje döner kebab", 15, 0);
        tblAdd(database, "khdb", 2, "Aardappelen (gekookt)", 17, 0);
        tblAdd(database, "khdb", 3, "Aardappelen (gebakken)", 16, 1);
        tblAdd(database, "khdb", 4, "Spaghetti (gekookt)", 12, 3);
        tblAdd(database, "khdb", 5, "Pasta (gekookt)", 17, 1);
        tblAdd(database, "khdb", 6, "Lasagne", 14, 5);
        tblAdd(database, "khdb", 7, "Macaroni (tomatensaus)", 12, 3);
        tblAdd(database, "khdb", 8, "Pizza", 26, 4);
        tblAdd(database, "khdb", 9, "Rijst", 20, 0);
        tblAdd(database, "khdb", 10, "Aardappelen (puree)", 12, 0);
        tblAdd(database, "khdb", 11, "Aardappelkroketten", 27, 1);
        //tblAdd(database, "khdb", 12, "", , );
        //tblAdd(database, "khdb", 13, "", , );
        //tblAdd(database, "khdb", 14, "", , );
        //tblAdd(database, "khdb", 15, "", , );


    }

    public void tblAdd(SQLiteDatabase database, String table, int id, String name, int kh, int sugar){
        database.execSQL("INSERT INTO " + table + " VALUES('" + id + "', '" + name + "', '" + kh + "', '" + sugar + "');");
    }

    public void Search(Cursor resultSet, TableLayout tblMain){
        resultSet.moveToFirst();
        TextView Multiplier = (TextView) findViewById(R.id.txtMultiplier);
        int mult = Integer.parseInt(Multiplier.getText().toString());
        if(resultSet.getCount() > 0) {
            tblMain.addView(addRow("Voedingswaren", "KH", "S", true));
            for (int i = 0; i < 20 && i < resultSet.getCount(); i++) {
                tblMain.addView(addRow(resultSet.getString(1), modMult(resultSet.getString(2), mult) + "g", modMult(resultSet.getString(3), mult) + "g", false));
                resultSet.moveToNext();
            }
        }
        else{
            addMessage(tblMain, "Geen resultaten gevonden");
        }
    }

    public String modMult(String value, int mult){
        float val = Float.parseFloat(value);
        Log.d(TAG, value + "; int: " + Float.toString(val));
        val = Math.round(val / 100 * mult);
        value = Float.toString(val);
        String[] parts = value.split("\\.");
        //Log.d(TAG, Integer.toString(parts.length));
        value = parts[0];
        return value;
    }

    public TableRow addRow(String name, String kh, String sugar, boolean bold){
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        TableRow row = new TableRow(this);
        TextView textViewKH = new TextView(this);
        TextView textViewName = new TextView(this);
        TextView textViewSugar = new TextView(this);
        TableRow.LayoutParams txtKHPar = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        TableRow.LayoutParams txtSugarPar = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        TableRow.LayoutParams txtNamePar = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        txtKHPar.width = size.x/8;
        txtSugarPar.width = size.x/8;
        txtNamePar.width = size.x*5/8;
        textViewKH.setTextSize(TypedValue.COMPLEX_UNIT_SP, 19);
        textViewName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 19);
        textViewSugar.setTextSize(TypedValue.COMPLEX_UNIT_SP, 19);
        if(bold){
            textViewKH.setTypeface(null, Typeface.BOLD);
            textViewName.setTypeface(null, Typeface.BOLD);
            textViewSugar.setTypeface(null, Typeface.BOLD);
        }
        textViewKH.setLayoutParams(txtKHPar);
        textViewName.setLayoutParams(txtNamePar);
        textViewSugar.setLayoutParams(txtSugarPar);
        textViewName.setText(name);
        textViewKH.setText(kh);
        textViewSugar.setText(sugar);
        row.addView(textViewName);
        row.addView(textViewKH);
        row.addView(textViewSugar);
        return row;
    }

    public void addMessage(TableLayout table, String message){
        TableRow row = new TableRow(this);
        TextView textView = new TextView(this);
        TableRow.LayoutParams par = new TableRow.LayoutParams((TableRow.LayoutParams.WRAP_CONTENT));
        textView.setLayoutParams(par);
        textView.setText(message);
        row.addView(textView);
        table.addView(row);
    }

}

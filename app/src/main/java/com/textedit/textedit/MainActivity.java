package com.textedit.textedit;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Environment;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class MainActivity extends AppCompatActivity {

    public EditText editText;
    public String filename = null;
    private String path = Environment.getExternalStorageDirectory().toString() + "/files/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        float fSize = Float.parseFloat(sharedPreferences.getString(getString(R.string.pref_size), "20"));
        editText.setTextSize(fSize);

        String regular = sharedPreferences.getString(getString(R.string.pref_style), "");
        int typeface = Typeface.NORMAL;
        if (regular.contains("Bold"))
            typeface += Typeface.BOLD;
        if (regular.contains("Italic"))
            typeface += Typeface.ITALIC;

        editText.setTypeface(null, typeface);

        int color = Color.BLACK;
        if (sharedPreferences.getBoolean(getString(R.string.pref_color_red),false))
            color += Color.RED;
        if (sharedPreferences.getBoolean(getString(R.string.pref_color_green),false))
            color += Color.GREEN;
        if (sharedPreferences.getBoolean(getString(R.string.pref_color_grey),false))
            color += Color.GRAY;

        editText.setTextColor(color);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_clear:
                editText.setText("");
                Toast.makeText(getApplicationContext(), "Cleared", Toast.LENGTH_SHORT).show();
                return  true;
            case R.id.action_open:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("File name");
                builder.setMessage("Input open file name");
                final EditText input = new EditText(this);
                builder.setView(input);
                builder.setPositiveButton("Open", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        editText.setText("");
                        String s = input.getText().toString();
                        filename = s;
                        File file = new File(path + filename);
                        if(file.exists() && file.isFile()){
                            editText.setText(openfile(filename));
                        }else{
                            Toast.makeText(MainActivity.this, "File does not exist!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.show();
                return true;
            case R.id.action_save:
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("File name");
                alert.setMessage("Input save file name");
                final EditText input2 = new EditText(this);
                alert.setView(editText);
                alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String value = input2.getText().toString();
                        filename = value;
                        savefile(filename, editText.getText().toString());
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(MainActivity.this, "Action canceled", Toast.LENGTH_SHORT).show();
                    }
                });
                alert.show();
                return true;
            case R.id.action_settings:
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void savefile(String filename, String body){
        try{
            File root = new File(this.path);
            if (!root.exists()){
                root.mkdirs();
            }
            File file = new File(root, filename);
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.append(body);
            fileWriter.flush();
            fileWriter.close();
            Toast.makeText(MainActivity.this, "Saved", Toast.LENGTH_SHORT).show();
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    private String openfile(String filename){
        StringBuilder text = new StringBuilder();
        try{
            File file = new File(this.path, filename);
            BufferedReader bufferedReader = new BufferedReader(new FileReader((file)));
            String line;
            while ((line = bufferedReader.readLine())!=null){
                text.append(line + "/n");
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return text.toString();
    }
}

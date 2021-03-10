package com.example.bringyourumbrellaAlpha;

import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.preference.PreferenceManager;

public class LanguageSelect extends AppCompatActivity {

    private int countBack;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Button btEn;
    private Button btRo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_select);

        this.getSupportActionBar().hide();

        countBack = 0;

          sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
           editor = sharedPreferences.edit();

    }


    public void enButton(View view) {
       editor.putString(getString(R.string.language), getString(R.string.english));
        editor.commit();

        Log.d("Language", getString(R.string.english));
        Intent intent = new Intent(this, WeekHourActivity.class);
        startActivity(intent);
    }

    public void roButton(View view){
        editor.putString(getString(R.string.language), getString(R.string.romana));
        editor.commit();

        Log.d("Language", getString(R.string.romana));
        Intent intent = new Intent(this, WeekHourActivity.class);
        startActivity(intent);

    }


    // Disable back option
    @Override
    public void onBackPressed() {

        if (countBack == 5)
            Toast.makeText(this, "Stop it >:C", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "You can't go back anymore", Toast.LENGTH_SHORT).show();

        countBack++;
    }




}

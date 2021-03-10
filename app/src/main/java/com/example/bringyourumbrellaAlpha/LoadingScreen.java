package com.example.bringyourumbrellaAlpha;

import android.content.Intent;
import android.os.SystemClock;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class LoadingScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_screen);

        Intent intent = new Intent(this, Home.class);
        SystemClock.sleep(3000);
        startActivity(intent);

    }
}

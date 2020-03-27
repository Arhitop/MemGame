package com.example.memorycanvas;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    TilesView view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        view = findViewById(R.id.view);
        view.context = getApplicationContext();
    }
    public void onNewGameClick(View v) {
        Toast toast = Toast.makeText(getApplicationContext(), "Новая игра", Toast.LENGTH_SHORT);
        toast.show();
        view.newGame();

        // запустить игру заново

    }
}

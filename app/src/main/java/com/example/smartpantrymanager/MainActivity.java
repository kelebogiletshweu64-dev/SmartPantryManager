
package com.example.smartpantrymanager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Button;
import android.content.Intent;
public class MainActivity extends Activity {

    private Button addItemButton;
    private Button viewItemsButton;
    private TextView welcomeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        welcomeText = findViewById(R.id.welcomeText);
        addItemButton = findViewById(R.id.addItemButton);
        viewItemsButton = findViewById(R.id.viewItemsButton);

        Button recipesButton = findViewById(R.id.recipesButton);

        recipesButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RecipesActivity.class);
            startActivity(intent);
        });

        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        MainActivity.this,
                        AddItemActivity.class
                );
                startActivity(intent);
            }
        });


        viewItemsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        MainActivity.this,
                        ViewItemsActivity.class
                );
                startActivity(intent);
            }
        });

    }
}

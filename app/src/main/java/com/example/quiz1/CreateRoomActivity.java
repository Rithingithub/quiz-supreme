package com.example.quiz1;

import static com.example.quiz1.AboutActivity.MyPREFERENCES;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;

public class CreateRoomActivity extends AppCompatActivity {
    MaterialCardView CustomCard, CreateCard;

    public static int checked;
    private Object materialAlertDialogBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        SharedPreferences sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        int default1;
        switch (Configuration.UI_MODE_NIGHT_MASK) {
            case AppCompatDelegate.MODE_NIGHT_NO:
                default1 = 0;
                break;
            case AppCompatDelegate.MODE_NIGHT_YES:
                default1 = 0;
                break;
            default:
                default1 = 2;
        }

        checked = sharedPreferences.getInt("checked", default1);
        switch (checked) {
            case 0:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case 1:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case 2:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
            case 3:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY);
                break;
        }

        CustomCard = findViewById(R.id.CustomCard);
        CreateCard = findViewById(R.id.CreateCard);

        CustomCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CreateRoomActivity.this, AttemptTest.class));
                finish();
            }
        });
    }
}



package com.example.fichiers;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    ActivityResultLauncher<String> launcher;
    ActivityResultLauncher<Intent> launcherIntent;
    Button btClic;
    TextView tvTexte;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btClic = findViewById(R.id.btOK);
        tvTexte = findViewById(R.id.tvTexte);
        btClic.setVisibility(View.GONE);

        launcher = registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                new ActivityResultCallback<Boolean>() {
                    @Override
                    public void onActivityResult(Boolean result) {
                        if(result == false)
                            Toast.makeText(MainActivity.this, "Permission refusée", Toast.LENGTH_SHORT).show();
                        else
                            btClic.setVisibility(View.VISIBLE);
                    }
                });

        launcherIntent = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        Uri uri = result.getData().getData();
                        try {
                            StringBuilder stringBuilder = new StringBuilder();

                            InputStream inputStream = getContentResolver().openInputStream(uri);
                            BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream)));

                            String line;
                            while ((line = reader.readLine()) != null) {
                                stringBuilder.append(line);
                            }
                            tvTexte.setText(stringBuilder.toString());

                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    }
                });



        permission();
    }

    public void Clic(View v)
    {
        String etat = Environment.getExternalStorageState();
        if (etat.equals(Environment.MEDIA_MOUNTED) || etat.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {

            File file = new File(getExternalFilesDir( Environment.DIRECTORY_DOCUMENTS), "Mes Fichiers");
            if (!file.mkdirs())
            {
                Toast.makeText(this,"dossier non créé", Toast.LENGTH_SHORT).show();
            }
            else
            {
                String nomFichier = file.getPath()+ "/monFichier.txt";
                String contenu = "Ceci est du texte";
                FileOutputStream outputStream;
                try {
                    outputStream = new FileOutputStream(nomFichier);
                    outputStream.write(contenu.getBytes());
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }




    }

    public void permission()
    {
        if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
        {
            launcher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        else
            btClic.setVisibility(View.VISIBLE);
    }
}
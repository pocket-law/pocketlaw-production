package ca.ggolda.reference_criminal_code.activities;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import ca.ggolda.reference_criminal_code.R;
import ca.ggolda.reference_criminal_code.data_utils.DbHelper;


/**
 * Created by gcgol on 01/25/2017.
 */

public class ActivitySplash extends AppCompatActivity {

    private String DATABASE_NAME;
    private DbHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_populate);

        DATABASE_NAME = getString(R.string.database_name);
        dbHelper = DbHelper.getInstance(getApplicationContext());


        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 1000ms
                tryImport();
            }
        }, 1000);

    }

    private void tryImport() {


        try {
            importDB();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // If the no sections in database, import via ActivityImport
            if (dbHelper.getAllSection().size() > 1) {
                Log.d("LoadSections:", "Success, size: " + dbHelper.getAllSection().size());

                Intent intent = new Intent(ActivitySplash.this, ActivityMain.class);
                startActivity(intent);


            } else {
                tryImport();
            }
        }
    }





    private void importDB() throws IOException {

        //Open your assets db as the input stream
        InputStream in = getApplicationContext().getAssets().open(DATABASE_NAME);

        String destPath = getApplicationContext().getDatabasePath(DATABASE_NAME).getPath();

        // Create empty file at destination path
        File f = new File(destPath);

        //Open the empty db as the output stream
        try {
            OutputStream out = new FileOutputStream(new File(destPath));

            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
            in.close();
            out.close();

            Log.e("DB Import", "imported");

            checkImportStatus();


        } catch (FileNotFoundException e) {
            Log.e("DB Import", "File not foound" + e);
        }

    }

    private void checkImportStatus() {

        String destPath = getApplicationContext().getDatabasePath(DATABASE_NAME).getPath();

        // Create empty file at destination path
        boolean test = new File(destPath).exists();

        if (!test) {

            Log.e("Database ", "doesn't exist");

        } else {
            Log.e("Database ", "exists");
        }

    }


}
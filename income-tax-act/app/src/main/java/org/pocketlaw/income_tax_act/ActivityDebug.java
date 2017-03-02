package org.pocketlaw.income_tax_act;


import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

/**
 * Created by gcgol on 01/18/2017.
 */

public class ActivityDebug extends AppCompatActivity {

    Button btn_next, btn_db, btn_exp, btn_imp_two, btn_init;
    DbHelper dbHelper;
    private String DATABASE_NAME = ActivityMain.DATABASE_NAME;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);


        dbHelper = DbHelper.getInstance(getApplicationContext());

        btn_next = (Button) findViewById(R.id.btn_add);
        btn_db = (Button) findViewById(R.id.btn_view);
        btn_imp_two = (Button) findViewById(R.id.btn_imp_two);
        btn_exp = (Button) findViewById(R.id.btn_exp);
        btn_init = (Button) findViewById(R.id.btn_init);


        // add to db
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ActivityDebug.this, ActivityPopulate.class);
                startActivity(intent);
            }
        });


        // skip to db
        btn_db.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityDebug.this, ActivityMain.class);
                startActivity(intent);
            }
        });


        // export db
        btn_exp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(ActivityDebug.this, "CLICKED!", Toast.LENGTH_SHORT).show();
                exportDB();

            }
        });


        // import db
        btn_imp_two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("EEEP", "trying... 0");

                Toast.makeText(ActivityDebug.this, "CLICKED!", Toast.LENGTH_SHORT).show();

                try {
                    importDB();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Log.e("EEEP", "trying... 999");
            }
        });

        // skip to db
        btn_init.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityDebug.this, ActivityMain.class);
                startActivity(intent);
            }
        });


    }

    private void exportDB() {

        Log.e("Export DB", "PackageName: "+ getApplicationContext().getPackageName());
        Log.e("Export DB", "DatabasePath: "+ getApplicationContext().getDatabasePath(DATABASE_NAME).getPath());

        // SD card
        File sd = Environment.getExternalStorageDirectory();
        File data = Environment.getDataDirectory();
        FileChannel source = null;
        FileChannel destination = null;

        String currentDBPath = "/data/" + getApplicationContext().getPackageName() + "/databases/" + DATABASE_NAME;

        String backupDBPath = "/tmp/" + DATABASE_NAME;
        File currentDB = new File(data, currentDBPath);
        File backupDB = new File(sd, backupDBPath);
        try {


            //TODO: REMEMBER TO CHANGE PERMISSIONS
            //TODO: THIS HAS STUMPED YOU TWICE NOW

            source = new FileInputStream(currentDB).getChannel();

            destination = new FileOutputStream(backupDB).getChannel();

            destination.transferFrom(source, 0, source.size());


            source.close();
            destination.close();

            Toast.makeText(ActivityDebug.this, "exported successfully!", Toast.LENGTH_LONG).show();

        } catch (IOException e) {
            e.printStackTrace();
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

            Log.e("EEEP", "inside import try");

            OutputStream out = new FileOutputStream(new File(destPath));

            Log.e("EEEP", "trying... dfafa 2");

            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
            in.close();
            out.close();

            Toast.makeText(ActivityDebug.this, "DB imported!", Toast.LENGTH_SHORT).show();

            Log.e("EEEP", "horray... 7");


        } catch (FileNotFoundException e) {
            Log.e("EEEP", "Filenotfoound");
        }

    }

}

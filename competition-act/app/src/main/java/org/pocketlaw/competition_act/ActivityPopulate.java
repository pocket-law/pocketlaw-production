package org.pocketlaw.competition_act;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gcgol on 01/10/2017.
 */

public class ActivityPopulate extends AppCompatActivity {
    public static final String ANY = "Any";
    private static final String URL = "http://laws-lois.justice.gc.ca/eng/XML/D-3.4.xml";

    // Whether the display should be refreshed.
    public static boolean refreshDisplay = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_populate);


        loadPage();

    }


    // Implementation of AsyncTask used to download Sections from XML feed.
    private class DownloadSectionXmlTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                return loadSectionXmlFromNetwork(urls[0]);
            } catch (IOException e) {
                Log.e("ActivityPopulate", "CONNECTION ERROR");
                return "connection error";
            } catch (XmlPullParserException e) {
                Log.e("ActivityPopulate", "XML ERROR");
                return "xml error";
            }
        }

        @Override
        protected void onPostExecute(String result) {

            Intent intent = new Intent(ActivityPopulate.this, ActivityMain.class);
            startActivity(intent);

        }
    }


    // Creates instance of async task the XML feed from local xml or laws-lois.justice.gc.ca.
    public void loadPage() {

        new DownloadSectionXmlTask().execute(URL);

    }


    // Loads section from XML
    private String loadSectionXmlFromNetwork(String urlString) throws XmlPullParserException, IOException {

        InputStream stream = null;

        List<Section> sections = new ArrayList<>();

        // Instantiate the parser
        XmlParser xmlParser = new XmlParser();

        try {
            // TODO: use downloadUrl as source when updating
            //stream = downloadUrl(urlString);

            //   stream = getResources().openRawResource(R.raw.p21stripped);

            stream = getResources().openRawResource(
                    getResources().getIdentifier(getString(R.string.database_name),
                            "raw", getPackageName()));

            sections = xmlParser.parse(stream);

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (stream != null) {
                stream.close();
            }
        }


        if (sections.size() > 0) {
            Log.e("XML sections.get(0)", "" + sections.get(0));
        }

        return "" + sections.size();

    }

    // Given a string representation of a URL, sets up a connection and gets
    // an input stream. TODO: use for updating
    private InputStream downloadUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        return conn.getInputStream();
    }
}
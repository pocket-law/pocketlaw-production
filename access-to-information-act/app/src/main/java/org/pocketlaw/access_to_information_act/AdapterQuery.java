package org.pocketlaw.access_to_information_act;

/**
 * Created by gcgol on 01/06/2017.
 */

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class AdapterQuery extends ArrayAdapter<Section> {


    private Context mContext;

    // Layouts
    private TextView resultLocation;
    private TextView resultText;

    private ListView listViewQuery;
    private ListView listViewSection;
    private WebView webView;


    private EditText editTextQuery;
    private TextView resultsTotal;


    public AdapterQuery(Context context, int resource, List<Section> objects) {
        super(context, resource, objects);

        mContext = context;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.card_query, parent, false);
        }

        // Heading text/number
        resultLocation = (TextView) convertView.findViewById(R.id.address);
        resultText = (TextView) convertView.findViewById(R.id.text);

        final Section current = getItem(position);

        // TODO: change to full address
        resultLocation.setText(current.getSection());
        resultText.setText(current.getFulltext());

        // Get section and query listViews and webview from main activity
        listViewSection = (ListView) ((ActivityMain) mContext).findViewById(R.id.listview_section);
        listViewQuery = (ListView) ((ActivityMain) mContext).findViewById(R.id.listview_query);

        editTextQuery = (EditText) ((ActivityMain) mContext).findViewById(R.id.edt_search);
        resultsTotal = (TextView) ((ActivityMain) mContext).findViewById(R.id.total_results);

        webView = (WebView) ((ActivityMain) mContext).findViewById(R.id.webview);


        // TODO: move to corresponding provision on click of query result
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clear resultsTotal and editText
                editTextQuery.setText("");
                resultsTotal.setText("");

                // Set section listview on basis of TOC selection
                listViewSection.setVisibility(View.VISIBLE);
                listViewSection.setSelection(current.getID() - 1);

                // Take focus off EditText
                listViewSection.requestFocus();

                listViewQuery.setVisibility(View.GONE);
                webView.setVisibility(View.GONE);

                ActivityMain.resultsVisible = 0;

                ActivityMain.hideSoftKeyboard((ActivityMain) mContext);


            }
        });

        return convertView;

    }


}
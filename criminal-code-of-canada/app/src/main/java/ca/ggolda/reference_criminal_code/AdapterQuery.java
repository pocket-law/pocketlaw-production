package ca.ggolda.reference_criminal_code;

/**
 * Created by gcgol on 01/06/2017.
 */

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.text.Html;
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

        String responseClean = current.getFulltext();

        String term1 = DbHelper.TERM1;
        String term2 = DbHelper.TERM2;
        String term3 = DbHelper.TERM3;
        String term4 = DbHelper.TERM4;
        String term5 = DbHelper.TERM5;
        String term6 = DbHelper.TERM6;


        if (term1 != "") {
            responseClean = responseClean.replaceAll("(?i)" + term1, "<font color='#ff5656'><i>" + term1 + "</i></font>");
        }
        if (term2 != "") {
            responseClean = responseClean.replaceAll("(?i)" + term2, "<font color='#ff5656'><i>" + term2 + "</i></font>");
        }
        if (term3 != "") {
            responseClean = responseClean.replaceAll("(?i)" + term3, "<font color='#ff5656'><i>" + term3 + "</i></font>");
        }
        if (term4 != "") {
            responseClean = responseClean.replaceAll("(?i)" + term4, "<font color='#ff5656'><i>" + term4 + "</i></font>");
        }
        if (term5 != "") {
            responseClean = responseClean.replaceAll("(?i)" + term5, "<font color='#ff5656'><i>" + term5 + "</i></font>");
        }
        if (term6 != "") {
            responseClean = responseClean.replaceAll("(?i)" + term6, "<font color='#ff5656'><i>" + term6 + "</i></font>");
        }

        // Check build version, as fromHtml is deprecated...
        if (Build.VERSION.SDK_INT >= 24) {
            resultText.setText(Html.fromHtml(responseClean, Html.FROM_HTML_MODE_LEGACY));
        } else {
            resultText.setText(Html.fromHtml(responseClean));
        }

        // resultText.setText(current.getFulltext());

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
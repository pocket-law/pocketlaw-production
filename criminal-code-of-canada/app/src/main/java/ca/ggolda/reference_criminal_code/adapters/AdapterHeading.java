package ca.ggolda.reference_criminal_code.adapters;

/**
 * Created by gcgol on 01/06/2017.
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import ca.ggolda.reference_criminal_code.R;
import ca.ggolda.reference_criminal_code.objects.Section;
import ca.ggolda.reference_criminal_code.activities.ActivityMain;

public class AdapterHeading extends ArrayAdapter<Section> {


    private Context mContext;
    private ListView listviewSection;
    private ListView listviewQuery;
    private WebView webView;
    private TextView results;


    public AdapterHeading(Context context, int resource, List<Section> objects) {
        super(context, resource, objects);

        mContext = context;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.card_heading, parent, false);
        }

        final Section current = getItem(position);

        LinearLayout headingLayout = (LinearLayout) convertView.findViewById(R.id.headingLayout);
        TextView heading = (TextView) convertView.findViewById(R.id.heading);
        TextView section = (TextView) convertView.findViewById(R.id.section);

        // Get listViews and webView from main activity
        listviewSection = (ListView) ((ActivityMain) mContext).findViewById(R.id.listview_section);
        listviewQuery = (ListView) ((ActivityMain) mContext).findViewById(R.id.listview_query);
        webView = (WebView) ((ActivityMain) mContext).findViewById(R.id.webview);

        results = (TextView) ((ActivityMain) mContext).findViewById(R.id.total_results);

        // Set section and heading text
        heading.setText("" + current.getFulltext());
        section.setText("" + current.getSection());


        //Change background color based on heading type
        //TODO: make switch
        if (current.getPinpoint().equals("level1")) {

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    results.setText("");

                    listviewQuery.setVisibility(View.GONE);
                    webView.setVisibility(View.GONE);
                    listviewSection.setVisibility(View.VISIBLE);
                    // Set section listview on basis of TOC selection
                    listviewSection.setSelection(current.getID() - 1);
                    ActivityMain.partsHideShow();
                }
            });
            heading.setBackgroundColor(Color.parseColor("#8C292e34"));
            section.setBackgroundColor(Color.parseColor("#8C292e34"));
        }

        if (current.getPinpoint().equals("level2")) {

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    results.setText("");

                    listviewQuery.setVisibility(View.GONE);
                    webView.setVisibility(View.GONE);
                    listviewSection.setVisibility(View.VISIBLE);
                    // Set section listview on basis of TOC selection
                    listviewSection.setSelection(current.getID() - 1);
                    ActivityMain.partsHideShow();
                }
            });
            heading.setBackgroundColor(Color.parseColor("#00FFFFFF"));
            section.setBackgroundColor(Color.parseColor("#00FFFFFF"));
        }

        if (current.getPinpoint().equals("level3")) {

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    results.setText("");

                    listviewQuery.setVisibility(View.GONE);
                    webView.setVisibility(View.GONE);
                    listviewSection.setVisibility(View.VISIBLE);
                    // Set section listview on basis of TOC selection
                    listviewSection.setSelection(current.getID() - 1);
                    ActivityMain.partsHideShow();
                }
            });
            heading.setBackgroundColor(Color.parseColor("#12FFFFFF"));
            section.setBackgroundColor(Color.parseColor("#12FFFFFF"));
        }

        if (current.getPinpoint().equals("level4")) {

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    results.setText("");

                    listviewQuery.setVisibility(View.GONE);
                    webView.setVisibility(View.GONE);
                    listviewSection.setVisibility(View.VISIBLE);
                    // Set section listview on basis of TOC selection
                    listviewSection.setSelection(current.getID() - 1);
                    ActivityMain.partsHideShow();
                }
            });
            heading.setBackgroundColor(Color.parseColor("#12FFFFFF"));
            section.setBackgroundColor(Color.parseColor("#12FFFFFF"));
        }


        if (current.getPinpoint().equals("forms")) {
            heading.setBackgroundColor(Color.parseColor("#66e13f0d"));
            section.setBackgroundColor(Color.parseColor("#66e13f0d"));

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    results.setText("");

                    listviewQuery.setVisibility(View.GONE);

                    // Set section listview on basis of TOC selection
                    listviewSection.setVisibility(View.GONE);
                    webView.loadUrl("file:///android_asset/forms.html");
                    webView.setVisibility(View.VISIBLE);
                    ActivityMain.partsHideShow();
                }
            });
        }


        if (current.getPinpoint().equals("schedules")) {
            heading.setBackgroundColor(Color.parseColor("#66e13f0d"));
            section.setBackgroundColor(Color.parseColor("#66e13f0d"));

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    results.setText("");

                    listviewQuery.setVisibility(View.GONE);

                    // Set section listview on basis of TOC selection
                    listviewSection.setVisibility(View.GONE);

                    webView.loadUrl("file:///android_asset/schedules.html");

                    webView.setVisibility(View.VISIBLE);
                    ActivityMain.partsHideShow();
                }
            });
        }



        if (current.getPinpoint().equals("related_provs")) {
            heading.setBackgroundColor(Color.parseColor("#66e13f0d"));
            section.setBackgroundColor(Color.parseColor("#66e13f0d"));

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    results.setText("");

                    listviewQuery.setVisibility(View.GONE);

                    // Set section listview on basis of TOC selection
                    listviewSection.setVisibility(View.GONE);
                    webView.loadUrl("file:///android_asset/related_provs.html");
                    webView.setVisibility(View.VISIBLE);
                    ActivityMain.partsHideShow();
                }
            });
        }

        if (current.getPinpoint().equals("amendments_nif")) {
            heading.setBackgroundColor(Color.parseColor("#66e13f0d"));
            section.setBackgroundColor(Color.parseColor("#66e13f0d"));

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    results.setText("");

                    listviewQuery.setVisibility(View.GONE);

                    // Set section listview on basis of TOC selection
                    listviewSection.setVisibility(View.GONE);
                    webView.loadUrl("file:///android_asset/anif.html");
                    webView.setVisibility(View.VISIBLE);
                    ActivityMain.partsHideShow();
                }
            });
        }



        return convertView;

    }

}
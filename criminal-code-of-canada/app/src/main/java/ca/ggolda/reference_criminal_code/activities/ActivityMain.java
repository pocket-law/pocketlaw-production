package ca.ggolda.reference_criminal_code.activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import ca.ggolda.reference_criminal_code.adapters.AdapterHeading;
import ca.ggolda.reference_criminal_code.adapters.AdapterQuery;
import ca.ggolda.reference_criminal_code.adapters.AdapterSection;
import ca.ggolda.reference_criminal_code.data_utils.DbHelper;
import ca.ggolda.reference_criminal_code.R;
import ca.ggolda.reference_criminal_code.dialogs.DialogInfo;

import static java.lang.Thread.sleep;


/**
 * Created by gcgol on 01/18/2017.
 */

public class ActivityMain extends AppCompatActivity {

    private ListView mListViewSections;
    private AdapterSection mAdapterSection;

    private ListView mListViewHeadings;
    private AdapterHeading mAdapterHeading;

    private ListView mListViewQuery;
    private AdapterQuery mAdapterQuery;

    private ImageView mBtnSearch;
    private EditText mEdtSearch;
    private TextView mTotalResults;

    private ImageView mBtnInfo;

    private static LinearLayout mViewQuery;

    private LinearLayout mLayoutLoad;

    private ImageView mBtnParts;
    public static LinearLayout mParts;

    private WebView webView;

    private String LAST_SEARCH = "";

    private String DATABASE_NAME;


    //Hacky override to comparing to last search
    private boolean triedSearch = false;

    DbHelper dbHelper;

    public static int partsVisible = 0;
    public static int resultsVisible = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DATABASE_NAME = getString(R.string.database_name);

        dbHelper = DbHelper.getInstance(getApplicationContext());

        mLayoutLoad = (LinearLayout) findViewById(R.id.load_layout);


        setListWebViews();


        mListViewQuery = (ListView) findViewById(R.id.listview_query);

        mBtnParts = (ImageView) findViewById(R.id.btn_parts);
        mParts = (LinearLayout) findViewById(R.id.parts);

        mViewQuery = (LinearLayout) findViewById(R.id.view_query);

        mBtnSearch = (ImageView) findViewById(R.id.btn_search);
        mEdtSearch = (EditText) findViewById(R.id.edt_search);
        mTotalResults = (TextView) findViewById(R.id.total_results);

        mBtnInfo = (ImageView) findViewById(R.id.btn_info);


        mBtnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DialogInfo cdd = new DialogInfo(ActivityMain.this);
                cdd.show();

            }
        });




        // bring parts up or down
        mBtnParts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mEdtSearch.setText("");
                hideSoftKeyboard(ActivityMain.this);

                partsHideShow();

            }
        });


        // Search on enter press
        mEdtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    hideSoftKeyboard(ActivityMain.this);

                    actionSearch();

                    return true;
                }
                return false;
            }
        });


        // Search on search button click
        mBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mEdtSearch.requestFocus();

                //TODO: this is not a perfect solution to returning focus
                if ((mEdtSearch.length() != 0) && !(mEdtSearch.getText().toString().equals(LAST_SEARCH)) || (triedSearch == true)) {
                    triedSearch = false;
                    hideSoftKeyboard(ActivityMain.this);
                    actionSearch();
                } else {
                    if (mEdtSearch.getText().toString().equals(LAST_SEARCH)) {
                        triedSearch = true;
                    }
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.showSoftInput(mEdtSearch, 0);
                }
            }
        });


    }

    private void setListWebViews() {
        mAdapterSection = new AdapterSection(ActivityMain.this, R.layout.card_section, dbHelper.getAllSection());
        mListViewSections = (ListView) findViewById(R.id.listview_section);
        mListViewSections.setAdapter(mAdapterSection);

        mAdapterHeading = new AdapterHeading(ActivityMain.this, R.layout.card_heading, dbHelper.getAllHeading());
        mListViewHeadings = (ListView) findViewById(R.id.listview_heading);
        mListViewHeadings.setAdapter(mAdapterHeading);

        webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);


    }

    private void actionSearch() {

        String query = mEdtSearch.getText().toString();
        mEdtSearch.clearFocus();

        LAST_SEARCH = query;



        if (!query.equals("")) {


            mAdapterQuery = new AdapterQuery(ActivityMain.this, R.layout.card_query, dbHelper.getSearchResults(query));
            mListViewQuery.setAdapter(mAdapterQuery);
        }

        if ((mAdapterQuery != null) && mAdapterQuery.getCount() == 1) {
            mTotalResults.setText("" + mAdapterQuery.getCount() + " result for... " + LAST_SEARCH);
        } else if ((mAdapterQuery != null) && mAdapterQuery.getCount() > 1) {
            mTotalResults.setText("" + mAdapterQuery.getCount() + " results for... " + LAST_SEARCH);
        } else if ((mAdapterQuery != null) && mAdapterQuery.getCount() == 0) {
            mTotalResults.setText("No results for... " + LAST_SEARCH);
        }

        mViewQuery.setVisibility(View.VISIBLE);
        mListViewQuery.setVisibility(View.VISIBLE);

        // Hide headings listview (inside mParts linearlayout) if it's up
        mParts.setVisibility(View.GONE);

        resultsVisible = 1;
        partsVisible = 0;
    }

    public static void partsHideShow() {

        if (partsVisible == 0) {
            mParts.setVisibility(View.VISIBLE);

            mParts.requestFocus();
            partsVisible = 1;
        } else if (partsVisible == 1) {
            mParts.setVisibility(View.GONE);
            mViewQuery.setVisibility(View.GONE);
            partsVisible = 0;
        }
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

    @Override
    public void onBackPressed() {

        if (resultsVisible == 1 || partsVisible == 1) {
            //  mListViewSections.setVisibility(View.VISIBLE);

            mParts.setVisibility(View.GONE);
            mViewQuery.setVisibility(View.GONE);

            mEdtSearch.clearFocus();
            mTotalResults.setText("");

            LAST_SEARCH = "";
            mEdtSearch.setText(LAST_SEARCH);

            resultsVisible = 0;
            partsVisible = 0;
        } else {

            if (!LAST_SEARCH.equals("")) {

                mEdtSearch.setText(LAST_SEARCH);
                actionSearch();

            } else {
                super.onBackPressed();
            }
        }

    }


}
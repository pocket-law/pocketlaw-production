package org.pocketlaw.immigration_refugee_protection_act;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gcgol on 01/18/2017.
 */

public class DbHelper extends SQLiteOpenHelper {

    private static final String TAG = "DbHelper";

    // Database Info
    private static final String DATABASE_NAME = "i2_5";
    private static final int DATABASE_VERSION = 1;

    //Table Names
    private static final String TABLE_EVIDENCE_ACT = "i2_5";

    // Evidence Act Table Columns
    private static final String _ID = "_id";
    private static final String FULLTEXT = "fulltext";
    private static final String TYPE = "type";
    private static final String SECTION = "section";
    private static final String PINPOINT = "pinpoint";

    private static DbHelper mDbHelper;


    public static synchronized DbHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.

        if (mDbHelper == null) {
            mDbHelper = new DbHelper(context.getApplicationContext());
        }
        return mDbHelper;
    }


    /**
     * Constructor should be private to prevent direct instantiation.
     * Make a call to the static method "getInstance()" instead.
     */
    private DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

   /*
    Called when the database is created for the FIRST time.
    If a database already exists on disk with the same DATABASE_NAME, this method will NOT be called.
    */

    @Override
    public void onCreate(SQLiteDatabase db) {


        String CREATE_USERDETAIL_TABLE = "CREATE TABLE " + TABLE_EVIDENCE_ACT +
                "(" +
                _ID + " INTEGER PRIMARY KEY ," +
                FULLTEXT + " TEXT, " +
                TYPE + " TEXT, " +
                SECTION + " TEXT, " +
                PINPOINT + " TEXT" +
                ")";
        db.execSQL(CREATE_USERDETAIL_TABLE);



    }


    /*
       Called when the database needs to be upgraded.
       This method will only be called if a database already exists on disk with the same DATABASE_NAME,
       but the DATABASE_VERSION is different than the version of the database that exists on disk.
       */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVIDENCE_ACT);

            onCreate(db);
        }
    }

    /*
   Insert a  user detail into database
   */

    public void insertSectionDetail(Section userData) {

        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();

        try {
            ContentValues values = new ContentValues();
            values.put(FULLTEXT, userData.getFulltext());
            values.put(TYPE, userData.getType());
            values.put(SECTION, userData.getSection());
            values.put(PINPOINT, userData.getPinpoint());

            db.insertOrThrow(TABLE_EVIDENCE_ACT, null, values);
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            e.printStackTrace();
            Log.d(TAG, "Error while trying to add post to database");
        } finally {

            db.endTransaction();
        }


    }

   /*
   fetch all Sections from database
    */

    public List<Section> getAllSection() {

        List<Section> sectionDetail = new ArrayList<>();

        String USER_DETAIL_SELECT_QUERY = "SELECT * FROM " + TABLE_EVIDENCE_ACT;

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(USER_DETAIL_SELECT_QUERY, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    Section sectionData = new Section(1, -777, "dbhelper", "dbhelper", "dbhelper");
                    sectionData.setID(Integer.valueOf(cursor.getString(cursor.getColumnIndex(_ID))));
                    sectionData.setFulltext(cursor.getString(cursor.getColumnIndex(FULLTEXT)));
                    sectionData.setType(Integer.valueOf(cursor.getString(cursor.getColumnIndex(TYPE))));
                    sectionData.setSection(cursor.getString(cursor.getColumnIndex(SECTION)));
                    sectionData.setPinpoint(cursor.getString(cursor.getColumnIndex(PINPOINT)));

                    sectionDetail.add(sectionData);


                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get posts from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return sectionDetail;

    }


     /*
   fetch all Headings from database
    */

    public List<Section> getAllHeading() {

        List<Section> sectionDetail = new ArrayList<>();

        // TODO: Display section 849
        String USER_DETAIL_SELECT_QUERY = "SELECT * FROM " + TABLE_EVIDENCE_ACT + " WHERE type = '0'";

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(USER_DETAIL_SELECT_QUERY, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    Section sectionData = new Section(1, -777, "dbhelper", "dbhelper", "dbhelper");
                    sectionData.setID(Integer.valueOf(cursor.getString(cursor.getColumnIndex(_ID))));
                    sectionData.setType(Integer.valueOf(cursor.getString(cursor.getColumnIndex(TYPE))));
                    sectionData.setPinpoint(cursor.getString(cursor.getColumnIndex(PINPOINT)));
                    sectionData.setSection(cursor.getString(cursor.getColumnIndex(SECTION)));
                    sectionData.setFulltext(cursor.getString(cursor.getColumnIndex(FULLTEXT)));

                    sectionDetail.add(sectionData);


                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get posts from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        // Add schedule and related provisions
        Section schedulesAdd = new Section(-1, 737, "schedule", "S", "Schedule");
        sectionDetail.add(schedulesAdd);
        Section relatedProvsAdd = new Section(-3, 737, "related_provs", "RP", "Related Provisions");
        sectionDetail.add(relatedProvsAdd);
        Section amendmentsNifAdd = new Section(-3, 737, "amendments_nif", "RP", "Amendments Not In Force");
        sectionDetail.add(amendmentsNifAdd);


        return sectionDetail;

    }


   /*
   fetch search results from database
    */

    public List<Section> getSearchResults(String query) {

        List<Section> sectionDetail = new ArrayList<>();

        // TODO: there's gotta be a cleaner way than using both the list and arraylist...

        String[] queryList = query.split(" ");

        // Add words from Query List to Query Array, and pad blanks at end if less than 6 words
        ArrayList queryArray = new ArrayList<>();
        for (int i = 0; i < 6; i++){
            if(i < queryList.length) {
                queryArray.add(queryList[i]);
            } else {
                queryArray.add("");
            }
        }

        String USER_DETAIL_SELECT_QUERY = "SELECT * FROM " + TABLE_EVIDENCE_ACT + " WHERE "
                + FULLTEXT + " LIKE '%" + queryArray.get(0) +"%' AND " + FULLTEXT + " LIKE '%" + queryArray.get(1) +"%' AND "
                + FULLTEXT + " LIKE '%" + queryArray.get(2) +"%' AND " + FULLTEXT + " LIKE '%" + queryArray.get(3) +"%' AND "
                + FULLTEXT + " LIKE '%" + queryArray.get(4) +"%' AND " + FULLTEXT + " LIKE '%" + queryArray.get(5) +"%'";


        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(USER_DETAIL_SELECT_QUERY, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    Section sectionData = new Section(1, -777, "dbhelper", "dbhelper", "dbhelper");
                    sectionData.setID(Integer.valueOf(cursor.getString(cursor.getColumnIndex(_ID))));
                    sectionData.setFulltext(cursor.getString(cursor.getColumnIndex(FULLTEXT)));
                    sectionData.setType(Integer.valueOf(cursor.getString(cursor.getColumnIndex(TYPE))));
                    sectionData.setSection(cursor.getString(cursor.getColumnIndex(SECTION)));
                    sectionData.setPinpoint(cursor.getString(cursor.getColumnIndex(PINPOINT)));

                    sectionDetail.add(sectionData);


                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get posts from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return sectionDetail;

    }


}
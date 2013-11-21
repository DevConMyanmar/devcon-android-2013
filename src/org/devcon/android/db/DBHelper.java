package org.devcon.android.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.devcon.android.BuildConfig;
import org.devcon.android.R;
import org.devcon.android.objects.Speaker;
import org.devcon.android.objects.Talk;
import org.devcon.android.util.LogUtil;

import java.util.ArrayList;

import static org.devcon.android.util.LogUtil.LOGD;
import static org.devcon.android.util.LogUtil.makeLogTag;

public class DBHelper {

    // make a tag for debugging
    private static final String TAG = makeLogTag(DBHelper.class);

    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    Cursor tCursor, fCursor, sCursor;

    private boolean status;
    public static String DB_NAME;
    public static final int DB_VERSION = 1;

    Talk talk;
    final Context mCtx;

    public static String TABLE_TALK, TABLE_SPEAKER, TABLE_FAV;
    public static String tID, tTIME, tTITLE, tSPEAKER, tDESC, tFAV;
    public static String sID, sNAME, sTITLE, sBIO, sPHOTO, sSID;

    final String[] titles = new String[]{"Ultimate Productivity with Vim",
            "Introduction to Elixir", "Go Concurrency Patterns ",
            "Android Designs for UI Developers", "REST is over.",
            "Real-Time Rails", "Sinatra in SIX lines",
            "Running Sinatra on Rails", "Monitoring the Health of Your App",
            "Closing Keynote"};
    final String[] speakers = new String[]{"Christ Hunt", "Jose Valim",
            "Robe Pike", "Roman Numrik", "Steve Klabnik", "Brian Cardarella",
            "Konstantin Haase", "Jos� Valim", "Yuheda Katz", "Aaron Patterson",
            "DHH", "Thar Htet", "Matz", "Guido", "Larry Page", "Sergey",
            "Andrew Gerrand"};

    final String[] dc12Talks = new String[]{"Opening & Opening Speech",
            "Keynote Speech", "EVALUATION FROM GPS TO AI DRIVEN CARS",
            "MOTIVATION, PRODUCTIVITY AND THE CODER'S JOURNEY",
            "Q&A WITH GOOGLE (OPEN DISCUSSION)",
            "BETTER DEVELOPER LIFE WITH AGILE METHODOLOGY",
            "POWER OF PRODUCTIVITY", "THE SOCIAL MEDIA DEVELOPER",
            "MYANMAR PRIMER & MYANMAR UNICODE TECHNIQUES",
            "OPENING THE MOBILE WEB - MOZILLLA AND FIREFOX OS",
            "DESIGNING SCALABLE WEB APPLICATION", "BEYOND \"HELLO WORLD\"",
            "BUSINESS INTELLIGENT: WHY YOU NEED TO CHANGE TO DATA WAREHOUSE",
            "HTML 5 CANVAS"};

    final String[] dc12Speakers = new String[]{"U Thaung Su Nyein",
            "U Thaung Tin", "Htoo Myint Naung (CEO, Technomation)",
            "Moe Hein Kyaw (Tbit Solution)",
            "Divon Lan (Next Wave Emerging Markets, Google)",
            "Phone Pye Oo (Product Manager, Tbit Solution)",
            "Thuta Hlaing (Senior Consultant, Microsoft Consulting)",
            "Thet Aung (IT Director, Tbit Solution)",
            "Aung Zeya (Fonts Creator, Myanmars.net)",
            "Chit Thiri Maung (Community Manager, Mozilla Myanmar)",
            "Thar Htet (CTO, Rival Edge Pte. Ltd.)",
            "Myint Kyaw Thu (Total Gameplay Studio)", "Win Htoo Myint",
            "Nyan Lin Htut"};

    public DBHelper(Context context) {
        this.mCtx = context;
        DB_NAME = mCtx.getResources().getString(R.string.db_name);

        TABLE_TALK = mCtx.getResources().getString(R.string.table_talk);

        // NOTE
        tID = mCtx.getString(R.string.tId);
        tTIME = mCtx.getString(R.string.tTime);
        tTITLE = mCtx.getString(R.string.tTitle);
        tSPEAKER = mCtx.getString(R.string.tSpeaker);
        tDESC = mCtx.getString(R.string.tDesc);
        tFAV = mCtx.getString(R.string.tFav);

        TABLE_SPEAKER = mCtx.getResources().getString(R.string.table_speaker);

        sID = mCtx.getString(R.string.sID);
        sNAME = mCtx.getString(R.string.sName);
        sTITLE = mCtx.getString(R.string.sTitle);
        sBIO = mCtx.getString(R.string.sBio);
        sPHOTO = mCtx.getString(R.string.sPhoto);
        sSID = mCtx.getString(R.string.sSID);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            if (BuildConfig.DEBUG) {
                LOGD(TAG, "Creating all the tables..");
            }

            String createTalk = "CREATE TABLE IF NOT EXISTS " + TABLE_TALK
                    + "(" + tID + " INTEGER PRIMARY KEY," + tTIME + " TEXT,"
                    + tTITLE + " TEXT," + tSPEAKER + " TEXT," + tDESC
                    + " TEXT," + tFAV + " INTEGER )";

            try {
                db.execSQL(createTalk);
            } catch (SQLException ex) {
                LogUtil.LOGD(TAG, ex.getMessage());
            }

            String createSpeaker = "CREATE TABLE IF NOT EXISTS "
                    + TABLE_SPEAKER + "(" + sID + " INTEGER PRIMARY KEY,"
                    + sNAME + " TEXT," + sTITLE + " TEXT," + sBIO + " TEXT,"
                    + sPHOTO + " TEXT," + sSID + " INTEGER )";
            try {
                db.execSQL(createSpeaker);
            } catch (SQLException ex) {
                LogUtil.LOGD(TAG, ex.getMessage());
            }

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_TALK);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_SPEAKER);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAV);
            onCreate(db);
        }

    }

    public DBHelper open() throws SQLException {

        mDbHelper = new DatabaseHelper(mCtx);
        try {
            mDb = mDbHelper.getWritableDatabase();
        } catch (SQLiteException ex) {
            LogUtil.LOGD(TAG, ex.getMessage());
            mDb = mDbHelper.getReadableDatabase();
        }
        return this;

    }

    public void close() {
        mDbHelper.close();
    }

    public void addTalk(Talk talk) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.tID, talk.getID());
        values.put(DBHelper.tTIME, talk.getTime());
        values.put(DBHelper.tTITLE, talk.getTitle());
        values.put(DBHelper.tSPEAKER, talk.getSpeaker());
        values.put(DBHelper.tDESC, talk.getDesc());
        values.put(DBHelper.tFAV, talk.getFav());

        mDb.insert(DBHelper.TABLE_TALK, null, values);
    }

    public void addSpeaker(Speaker speaker) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.sID, speaker.getID());
        values.put(DBHelper.sNAME, speaker.getName());
        values.put(DBHelper.sTITLE, speaker.getTitle());
        values.put(DBHelper.sBIO, speaker.getBio());
        values.put(DBHelper.sPHOTO, speaker.getPhoto());
        values.put(DBHelper.sSID, speaker.getScheduleID());

        mDb.insert(DBHelper.TABLE_SPEAKER, null, values);
//        LogUtil.LOGD(TAG, "Key and values are: " + values.valueSet());
    }

	/*
     * public boolean deleteAllValues() { int doneDelete = 0; doneDelete =
	 * mDb.delete(TABLE_TALK, null, null); if (AppConfig.DEBUG_MOOD)
	 * Log.w(getClass().getSimpleName(), Integer.toString(doneDelete)); return
	 * doneDelete > 0; }
	 */

    public void RemoveAllRecord() {
        LOGD(TAG, "Deleting the records..");
        mDb.execSQL("DELETE FROM " + TABLE_TALK);
        mDb.execSQL("DELETE FROM " + TABLE_SPEAKER);
    }

    // dafaq I wrote ?
    public void selectAllSpeaker() {
        Cursor count = mDb.rawQuery("SELECT COUNT(*) FROM " + TABLE_SPEAKER,
                null);
        LOGD(TAG, String.valueOf(count.getCount()));
    }

    // Load all talks to cursor
    public Cursor fetchAllTalk() {
        tCursor = mDb.query(DBHelper.TABLE_TALK, new String[]{tID, tTIME,
                tTITLE, tSPEAKER, tDESC, tFAV}, null, null, null, null, null);

        if (tCursor != null) {
            tCursor.moveToFirst();
        }
        LogUtil.LOGD(TAG, "count " + (tCursor != null ? tCursor.getCount() : 0));
        return tCursor;
    }

    // Load fav=1 from Talk to Fav Cursor
    public Cursor fetchAllFav() {
        fCursor = mDb.rawQuery("SELECT * FROM " + DBHelper.TABLE_TALK
                + " WHERE " + tFAV + " = 1", null);
        return fCursor;
    }

    // Load all speakers to cursor
    public Cursor fetchAllSpeaker() {
        sCursor = mDb.query(DBHelper.TABLE_SPEAKER, new String[]{sID, sNAME,
                sTITLE, sBIO, sPHOTO, sSID}, null, null, null, null, null);
        if (sCursor != null) {
            sCursor.moveToFirst();
        }
        return sCursor;
    }

    public Cursor commitQuery(String query) {
        return mDb.rawQuery(query, null);
    }

    public String getPhotosURL(String sID) {

        String url = "";

        Cursor sC = mDb.rawQuery("select * from " + DBHelper.TABLE_TALK, null);
        // new String[] { sID });

        if (sC.moveToFirst()) {
            Log.i("cursor", sC.getString(1));
        }

        return url;
    }

    // Convert talk cursor to arraylist
    public ArrayList<Talk> getAllTalks() {
        Cursor c = fetchAllTalk();
        ArrayList<Talk> list = new ArrayList<Talk>();
        if (c != null && c.getCount() > 0) {
            do {
                Talk g = new Talk();
                g._id = c.getInt(0);
                g.time = c.getString(1);
                g.title = c.getString(2);
                g.speaker = c.getString(3);
                g.desc = c.getString(4);
                if (c.getInt(5) == 0) {
                    // YLA:I worte if else here. :D
                    g.fav = false;
                } else if (c.getInt(5) == 1) {
                    g.fav = true;
                }
                list.add(g);
            } while (c.moveToNext());
        }
        return list;
    }

    // Convert only fav talks to array list
    public ArrayList<Talk> getAllFav() {
        Cursor c = fetchAllFav();
        c.moveToFirst();
        ArrayList<Talk> list = new ArrayList<Talk>();
        if (c.getCount() > 0) {
            do {
                Talk g = new Talk();
                g._id = c.getInt(0);
                g.time = c.getString(1);
                g.title = c.getString(2);
                g.speaker = c.getString(3);
                g.desc = c.getString(4);
                if (c.getInt(5) == 0) {
                    g.fav = false;
                } else if (c.getInt(5) == 1) {
                    g.fav = true;
                }
                list.add(g);
            } while (c.moveToNext());
        }
        return list;
    }

    // Convert speaker cursor to array list
    public ArrayList<Speaker> getAllSpeakers() {
        Cursor c = fetchAllSpeaker();
        ArrayList<Speaker> list = new ArrayList<Speaker>();
        if (c != null && c.getCount() > 0) {
            do {
                Speaker s = new Speaker();
                s._id = c.getInt(0);
                s.name = c.getString(1);
                s.title = c.getString(2);
                s.bio = c.getString(3);
                s.photo = c.getString(4);
                s.schedule_id = c.getInt(5);
                // sigh. this line took me hours to figure out what's wrong
                list.add(s);
            } while (c.moveToNext());
        }

        return list;

    }

    public void addToFav(String tID) {
        // because I don't know how to write update query
        // update table_name
        // set column1 = value1,
        // set column2 = value2
        // where [condition]
        String update = "UPDATE talks SET fav=1 WHERE _id=" + tID;
        mDb.execSQL(update);

    }

    public void removeFromFav(String tID) {
        String update = "UPDATE talks SET fav=0 WHERE _id=" + tID;
        mDb.execSQL(update);
    }

    public boolean getTalkStatus(String tID) {
        Cursor fav = mDb.rawQuery(
                ("SELECT " + DBHelper.tFAV + " FROM " + DBHelper.TABLE_TALK
                        + " WHERE " + DBHelper.tID + " = " + tID), null);
        // it makes me half my of day gone
        fav.moveToFirst();

        if (fav.getCount() > 0) {
            do {
                if (fav.getInt(0) == 0) {
                    status = false;
                } else if (fav.getInt(0) == 1) {
                    status = true;
                }
                LogUtil.LOGD(TAG, "and value: " + fav.getInt(0));
            } while (fav.moveToNext());
        }

        return status;
    }
}

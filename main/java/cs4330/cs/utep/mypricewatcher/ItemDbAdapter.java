package cs4330.cs.utep.mypricewatcher;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class ItemDbAdapter {

    static final String DB_NAME = "ItemDB";
    static final String DB_TABLE = "items";
    static final int DB_VERSION = 1;
    static final String TAG = "ItemDbAdapter";
    static final String DB_CREATE =
            "create table items (_id integer primary key autoincrement, " +
                    "name text not null, initial_price real not null, " +
                    "current_price real not null, url text not null," +
                    "percent_change real not null);";

    static final String KEY_ROWID = "_id";
    static final String KEY_NAME = "name";
    static final String KEY_IPRICE = "initial_price";
    static final String KEY_CPRICE = "current_price";
    static final String KEY_URL = "url";
    static final String KEY_CHANGE = "percent_change";

    final Context context;
    DatabaseHelper DBHelper;
    SQLiteDatabase db;

    public ItemDbAdapter(Context context){
        this.context = context;
        DBHelper = new DatabaseHelper(context);
    }
    private static class DatabaseHelper extends SQLiteOpenHelper{
        DatabaseHelper(Context context){
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try{
                db.execSQL(DB_CREATE);
            }
            catch(SQLException e){
                e.printStackTrace();
            }
        }
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to " +
                    newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS items");
            onCreate(db);
        }
    }
    //---opens the database---
    public ItemDbAdapter open() throws SQLException{
        db = DBHelper.getWritableDatabase();
        return this;
    }
    //---closes the database---
    public void close(){
        DBHelper.close();
    }
    //---insert an item into the database---
    public long insertItem(String name, double init_price, double current_price, String url, double percent){
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NAME, name);
        initialValues.put(KEY_IPRICE, init_price);
        initialValues.put(KEY_CPRICE, current_price);
        initialValues.put(KEY_URL, url);
        initialValues.put(KEY_CHANGE, percent);
        return db.insert(DB_TABLE, null, initialValues);
    }
    //---deletes a particular item---
    public boolean deleteItem(long rowId){
        return db.delete(DB_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    //---retrieves all items---
    public Cursor getAllItems(){
        return db.query(DB_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_IPRICE, KEY_CPRICE, KEY_URL, KEY_CHANGE},
                null, null, null, null, null);
    }
    //---retrieves a particular item---
    public Cursor getItem(long rowID) throws SQLException{
        Cursor mCursor = db.query(true, DB_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_IPRICE, KEY_CPRICE, KEY_URL, KEY_CHANGE},
                KEY_ROWID + "=" + rowID, null, null, null, null, null);
        if(mCursor != null){
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    //---updates an item---
    public boolean updateItem(long rowId, String name, double init_price, double current_price, String url, double percent){
        ContentValues args = new ContentValues();
        args.put(KEY_NAME, name);
        args.put(KEY_IPRICE, init_price);
        args.put(KEY_CPRICE, current_price);
        args.put(KEY_URL, url);
        args.put(KEY_CHANGE, percent);
        return db.update(DB_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
}

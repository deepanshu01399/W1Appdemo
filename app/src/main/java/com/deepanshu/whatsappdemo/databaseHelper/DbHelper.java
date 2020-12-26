package com.deepanshu.whatsappdemo.databaseHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.List;

import static com.deepanshu.whatsappdemo.databaseHelper.DbTables.COLUMN_KEY;
import static com.deepanshu.whatsappdemo.databaseHelper.DbTables.COLUMN_VALUE;
import static com.deepanshu.whatsappdemo.databaseHelper.DbTables.SALUTATION;
import static com.deepanshu.whatsappdemo.databaseHelper.DbTables.SORT_ORDER;
import static com.deepanshu.whatsappdemo.databaseHelper.DbTables.createTable;

public class DbHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "my_whatsapp";
    private static final int DB_VERSION = 1;
    private static DbHelper dbHelper;

    public  DbHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }
    public static SQLiteDatabase getSQLiteInstance(Context pContext) {
        if (dbHelper == null)
            dbHelper = new DbHelper(pContext);
        return dbHelper.getWritableDatabase();
    }
    public static SQLiteDatabase getSQLiteInstanceRead(Context pContext) {
        if (dbHelper == null)
            dbHelper = new DbHelper(pContext);
        return dbHelper.getReadableDatabase();
    }

    public static void clearDb(Context context){
        SQLiteDatabase sqLiteDatabase = getSQLiteInstance(context);
        try{
            sqLiteDatabase.delete(SALUTATION,null,null);
        }catch (SQLiteException sqliteexception){
            Log.e("clearDbError",sqliteexception.getLocalizedMessage());

        }
    }
    public static void insertAll(Context pContext, String pTableName, List<CommanDataHolder> pContentValues){
        SQLiteDatabase db = getSQLiteInstance(pContext);
        db.beginTransaction();
        try {
            for (int i = 0; i < pContentValues.size(); i++) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(SORT_ORDER, i);
                contentValues.put(COLUMN_KEY, pContentValues.get(i).getKey().trim());
                contentValues.put(COLUMN_VALUE, pContentValues.get(i).getValue().trim());
                db.insert(pTableName, null, contentValues);
//                contentValuesList.add(contentValues);
            }
//            for (Cities city : list) {
//                values.put(CityId, city.getCityid());
//                values.put(CityName, city.getCityName());
//                db.insert(TABLE_CITY, null, values);
//            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

    }
    public static Cursor fetchRow(Context context, String table_Name) {
        return getSQLiteInstance(context).rawQuery("Select * from " + table_Name, null);
    }

    public static Cursor fetchRow(Context pContext, String pTableName, String[] columns, String
            where, String[] whereArgs, String orderBy) {
        return getSQLiteInstance(pContext).query(true, pTableName, columns, where, whereArgs, null,
                null, orderBy, null);
    }

    public static Cursor fetchRow(Context pContext, String pTableName, String[] columns, String
            where, String[] whereArgs, String orderBy, String limit) {
        return getSQLiteInstance(pContext).query(true, pTableName, columns, where, whereArgs, null,
                null, orderBy, limit);
    }

    public static int deleteRow(Context pContext, String pTableName, String where, String[] whereArgs) {
        return getSQLiteInstance(pContext).delete(pTableName, where, whereArgs);
    }

    public static void clearTable(Context pContext, String pTableName) {
        SQLiteDatabase db = getSQLiteInstance(pContext);
        db.delete(pTableName, null, null);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createTable(SALUTATION));

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + SALUTATION);

    }
}

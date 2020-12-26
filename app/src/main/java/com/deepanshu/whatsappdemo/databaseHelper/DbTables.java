package com.deepanshu.whatsappdemo.databaseHelper;

public class DbTables {
    public static final String COLUMN_A_ID = "aId";
    public static final String COLUMN_KEY = "key";
    public static final String COLUMN_VALUE = "value";
    public static final String SORT_ORDER = "sort_order";

    public static final String SALUTATION = "db_salutation";

    public static String createTable(String tableName) {
        String query = "CREATE TABLE IF NOT EXISTS " + tableName + " ( " +
                "" + COLUMN_A_ID + "" +
                " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                + COLUMN_KEY + " TEXT  , "
                + SORT_ORDER + " INTEGER  , "
                + COLUMN_VALUE + " TEXT "
                + ")";
        return query;
    }
}

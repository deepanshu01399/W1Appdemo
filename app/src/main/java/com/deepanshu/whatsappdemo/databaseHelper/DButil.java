package com.deepanshu.whatsappdemo.databaseHelper;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import static com.deepanshu.whatsappdemo.databaseHelper.DbTables.COLUMN_A_ID;
import static com.deepanshu.whatsappdemo.databaseHelper.DbTables.COLUMN_KEY;
import static com.deepanshu.whatsappdemo.databaseHelper.DbTables.COLUMN_VALUE;
import static com.deepanshu.whatsappdemo.databaseHelper.DbTables.SORT_ORDER;

public class DButil {

    public static List<CommanDataHolder> getSpinnerList(Cursor cursor) {
        List<CommanDataHolder> dataHolderList = new ArrayList<>();
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    CommanDataHolder model = new CommanDataHolder();
                    model.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_A_ID)));
                    model.setSortOrder(cursor.getInt(cursor.getColumnIndex(SORT_ORDER)));
                    model.setKey(cursor.getString(cursor.getColumnIndex(COLUMN_KEY)));
                    model.setValue(cursor.getString(cursor.getColumnIndex(COLUMN_VALUE)));
                    dataHolderList.add(model);
                }
            }
            cursor.close();
        }
        return dataHolderList;
    }

    public static CommanDataHolder getSelectedSpinnerItemObj(Cursor cursor) {
        CommanDataHolder spinnerDataHolder = new CommanDataHolder();
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    spinnerDataHolder.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_A_ID)));
                    spinnerDataHolder.setSortOrder(cursor.getInt(cursor.getColumnIndex(SORT_ORDER)));
                    spinnerDataHolder.setKey(cursor.getString(cursor.getColumnIndex(COLUMN_KEY)));
                    spinnerDataHolder.setValue(cursor.getString(cursor.getColumnIndex(COLUMN_VALUE)));
                    return spinnerDataHolder;
                }
            }
            cursor.close();
        }
        // to avoid null pointer
        spinnerDataHolder.setValue("");
        spinnerDataHolder.setKey("");
        spinnerDataHolder.setSortOrder(0);
        return spinnerDataHolder;
    }
    public static List<CommanDataHolder> addNoneAtZeroIndex(List<CommanDataHolder> list){
        List<CommanDataHolder> tempList=new ArrayList<>();
        CommanDataHolder spinnerDataHolder=new CommanDataHolder();
        spinnerDataHolder.setKey("");
        spinnerDataHolder.setValue("--None--");
        tempList.add(spinnerDataHolder);
        tempList.addAll(list);
        list.clear();
        list.addAll(tempList);
        return list;
    }

}

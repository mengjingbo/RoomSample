package com.sample.room.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import com.sample.room.bean.PhoneBean;
import com.sample.room.dao.PhoneDao;

/**
 * 作者：蒙景博
 * 时间：2017/11/16
 * 描述：
 */
@Database(entities = {PhoneBean.class}, version = 1, exportSchema = false)
@TypeConverters({ConversionFactory.class})
public abstract class PhoneDatabase extends RoomDatabase {

    public static PhoneDatabase getDefault(Context context) {
        return buildDatabase(context);
    }

    private static PhoneDatabase buildDatabase(Context context) {
        return Room.databaseBuilder(context.getApplicationContext(), PhoneDatabase.class, "PHONE.db")
                .allowMainThreadQueries()
                .build();
    }

    public abstract PhoneDao getPhoneDao();
}

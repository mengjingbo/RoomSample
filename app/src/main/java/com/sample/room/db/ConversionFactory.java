package com.sample.room.db;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

/**
 * 作者：蒙景博
 * 时间：2018/3/7
 * 描述：
 */
public class ConversionFactory {

    @TypeConverter
    public static Long fromDateToLong(Date date) {
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public static Date fromLongToDate(Long value) {
        return value == null ? null : new Date(value);
    }
}

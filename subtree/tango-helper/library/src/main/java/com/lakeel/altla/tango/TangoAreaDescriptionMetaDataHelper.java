package com.lakeel.altla.tango;

import com.google.atap.tangoservice.TangoAreaDescriptionMetaData;

import android.support.annotation.NonNull;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Date;

public final class TangoAreaDescriptionMetaDataHelper {

    private static int LONG_BYTE_COUNT = 8;

    private static int DOUBLE_BYTE_COUNT = 8;

    private TangoAreaDescriptionMetaDataHelper() {
    }

    public static String getUuid(@NonNull TangoAreaDescriptionMetaData data) {
        return getString(data, TangoAreaDescriptionMetaData.KEY_UUID);
    }

    public static String getName(@NonNull TangoAreaDescriptionMetaData data) {
        return getString(data, TangoAreaDescriptionMetaData.KEY_NAME);
    }

    public static void setName(@NonNull TangoAreaDescriptionMetaData data, String value) {
        setString(data, TangoAreaDescriptionMetaData.KEY_NAME, value);
    }

    public static Date getMsSinceEpoch(@NonNull TangoAreaDescriptionMetaData data) {
        long ms = getLong(data, TangoAreaDescriptionMetaData.KEY_DATE_MS_SINCE_EPOCH);
        return new Date(ms);
    }

    public static void setMsSinceEpoch(@NonNull TangoAreaDescriptionMetaData data, Date value) {
        long ms = value.getTime();
        setLong(data, TangoAreaDescriptionMetaData.KEY_DATE_MS_SINCE_EPOCH, ms);
    }

    public static double[] getTransformation(@NonNull TangoAreaDescriptionMetaData data) {
        return getDoubleArray(data, TangoAreaDescriptionMetaData.KEY_TRANSFORMATION);
    }

    public static void setTransformation(@NonNull TangoAreaDescriptionMetaData data, double[] values) {
        setDoubleArray(data, TangoAreaDescriptionMetaData.KEY_TRANSFORMATION, values);
    }

    public static String getString(@NonNull TangoAreaDescriptionMetaData data, @NonNull String key) {
        byte[] bytes = data.get(key);
        return new String(bytes);
    }

    public static void setString(@NonNull TangoAreaDescriptionMetaData data, @NonNull String key,
                                 @NonNull String value) {
        byte[] bytes = value.getBytes();
        data.set(key, bytes);
    }

    //
    // MEMO
    //
    // Tango のライブラリは、数値のバイト列化にリトル エンディアンを用いている。
    // Java はビッグ エンディアンを基本とするため、これらの相違に注意しなければならない。
    //

    public static long getLong(@NonNull TangoAreaDescriptionMetaData data, @NonNull String key) {
        byte[] bytes = data.get(key);
        return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getLong();
    }

    public static void setLong(@NonNull TangoAreaDescriptionMetaData data, @NonNull String key, long value) {
        byte[] bytes = ByteBuffer.allocate(LONG_BYTE_COUNT)
                                 .putLong(value)
                                 .order(ByteOrder.LITTLE_ENDIAN)
                                 .array();
        data.set(key, bytes);
    }

    public static double[] getDoubleArray(@NonNull TangoAreaDescriptionMetaData data, @NonNull String key) {
        byte[] bytes = data.get(key);
        ByteBuffer buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);

        int count = bytes.length / DOUBLE_BYTE_COUNT;
        double[] array = new double[count];
        for (int i = 0; i < count; i++) {
            array[i] = buffer.getDouble();
        }

        return array;
    }

    public static void setDoubleArray(@NonNull TangoAreaDescriptionMetaData data, @NonNull String key,
                                      @NonNull double[] values) {
        ByteBuffer buffer = ByteBuffer.allocate(DOUBLE_BYTE_COUNT * values.length);

        for (int i = 0; i < values.length; i++) {
            buffer.putDouble(values[i]);
        }

        byte[] bytes = buffer.order(ByteOrder.LITTLE_ENDIAN).array();
        data.set(key, bytes);
    }
}

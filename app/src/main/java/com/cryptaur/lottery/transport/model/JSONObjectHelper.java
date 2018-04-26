package com.cryptaur.lottery.transport.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.cryptaur.lottery.util.BigIntegers;
import com.cryptaur.lottery.util.encoders.Hex;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.threeten.bp.Instant;
import org.threeten.bp.format.DateTimeFormatter;

import java.math.BigInteger;

public class JSONObjectHelper {

    private final JSONObject source;

    public JSONObjectHelper(JSONObject source) {
        this.source = source;
    }

    /**
     * parses string like 0x203040 as unsigned integer to BigInteger
     *
     * @param name -- field name in json
     * @return
     */
    @Nullable
    public BigInteger getUnsignedBigInteger(String name) throws JSONException {
        if (source.isNull(name))
            return null;

        String src = source.getString(name);
        if (src == null)
            return null;

        src = src.substring(2);
        if (src.length() % 2 == 1) {
            src = "0" + src;
        }
        byte[] bytes = Hex.decode(src);
        return BigIntegers.fromUnsignedByteArray(bytes);
    }

    /**
     * parses int array; return int[0] if it does not exist
     *
     * @param name
     * @return
     */
    @NonNull
    public int[] getIntArray(String name) throws JSONException {
        if (source.isNull(name))
            return new int[0];
        JSONArray array = source.getJSONArray(name);
        int[] result = new int[array.length()];
        for (int i = 0; i < result.length; i++) {
            result[i] = array.getInt(i);
        }
        return result;
    }

    /**
     * same as JSONObject.getString(), but return null if mapping does not exist
     *
     * @param name
     * @return
     * @throws JSONException
     */
    @Nullable
    public String getStringNullable(String name) throws JSONException {
        if (source.isNull(name))
            return null;
        return source.getString(name);
    }

    public Instant getInstant(String name) throws JSONException {
        String instantStr = source.getString(name);
        return Instant.parse(instantStr);
    }

    public JSONObjectHelper put(String name, BigInteger value) throws JSONException {
        if (value != null) {
            byte[] bytes = BigIntegers.asUnsignedByteArray(value);
            String s = Hex.toHexString(bytes);
            source.put(name, "0x" + s);
        }
        return this;
    }

    public JSONObjectHelper put(String name, Instant time) throws JSONException {
        source.put(name, DateTimeFormatter.ISO_INSTANT.format(time));
        return this;
    }

    public JSONObject getJson() {
        return source;
    }

    public JSONObjectHelper put(String name, int[] numbers) throws JSONException {
        if (numbers != null) {
            JSONArray arr = new JSONArray();
            for (int i = 0; i < numbers.length; i++) {
                arr.put(numbers[i]);
            }
            source.put(name, arr);
        }
        return this;
    }
}

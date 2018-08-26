package net.rodolfoboffo.indicadorrb.model.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonUtil {

    public static Gson getGsonInstance() {
        GsonBuilder builder = new GsonBuilder();
        builder = builder.serializeSpecialFloatingPointValues();
        return builder.create();
    }

}

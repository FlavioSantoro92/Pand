package it.unibas.pand.persistance;

import android.content.Context;

import com.google.dexmaker.stock.ProxyBuilder;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;

import it.unibas.pand.Utility;
import it.unibas.pand.model.BeanInvocationHandler;

public class DAOJson {
    private static String TAG = DAOJson.class.getName();
    private Context context;
    private String datePatternFormat = "dd-MM-yyyy HH:mm:ss";

    public DAOJson(Context context) {
        this.context = context;
    }

    private Class getClassProxyType(Class c){
        if(ProxyBuilder.isProxyClass(c) || !c.getName().startsWith(Utility.getBasePackage(context))){
            return c;
        }
        try {
            return ProxyBuilder.forClass(c).buildProxyClass();
        } catch (IOException e) {
            return c;
        }
    }

    public Object load(String name, InputStream inputStream, Class t) throws DAOException {
        Object object = null;
        Reader reader = null;
        try {
            reader = new InputStreamReader(inputStream);
            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(Date.class, new AdapterDate());
            builder.registerTypeAdapter(Collection.class, new CollectionDeserializer());
            builder.registerTypeHierarchyAdapter(Collection.class, new CollectionDeserializer());
            builder.setExclusionStrategies(new TestExclStrat());
            Gson gson = builder.create();
            object = gson.fromJson(reader, getClassProxyType(t));
            if(ProxyBuilder.isProxyClass(object.getClass())){
                ProxyBuilder.setInvocationHandler(object, new BeanInvocationHandler(name));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new DAOException(e);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (java.io.IOException ioe) {
            }
        }
        return object;
    }

    public void save(Object oggetto, OutputStream out) throws DAOException {
        PrintWriter printWriter = null;
        try {
            printWriter = new java.io.PrintWriter(out);
            String stringJson = toJson(oggetto);
            printWriter.print(stringJson);
        } catch (Exception ioe) {
            throw new DAOException(ioe);
        } finally {
            if (printWriter != null) {
                printWriter.close();
            }
        }
    }

    public String toJson(Object object){
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Date.class, new AdapterDate());
        builder.setExclusionStrategies(new TestExclStrat());
        builder.setPrettyPrinting();
        Gson gson = builder.create();
        return gson.toJson(object);
    }

    class CollectionDeserializer implements JsonDeserializer<Collection<?>> {
        @Override
        public Collection<?> deserialize(JsonElement json, Type typeOfT,
                                         JsonDeserializationContext context) throws JsonParseException {
            Type realType = ((ParameterizedType)typeOfT).getActualTypeArguments()[0];
            Class c = getClassProxyType((Class<?>) realType);
            return parseAsArrayList(json, c);
        }

        @SuppressWarnings("unchecked")
        public <T> ArrayList<T> parseAsArrayList(JsonElement json, T type) {
            ArrayList<T> newArray = new ArrayList<T>();
            Gson gson = new Gson();

            JsonArray array= json.getAsJsonArray();
            Iterator<JsonElement> iterator = array.iterator();

            while(iterator.hasNext()){
                JsonElement json2 = iterator.next();
                T object = (T) gson.fromJson(json2, (Class<?>)type);
                newArray.add(object);
            }

            return newArray;
        }

    }

    private class AdapterDate implements JsonSerializer<Date>, JsonDeserializer<Date> {
        public JsonElement serialize(Date date, Type tipo, JsonSerializationContext context) {
            DateFormat dateFormat = new SimpleDateFormat(datePatternFormat);
            return new JsonPrimitive(dateFormat.format(date.getTime()));
        }

        public Date deserialize(JsonElement json, Type tipo, JsonDeserializationContext context) throws JsonParseException {
            try {
                String dateString = json.getAsString();
                DateFormat dateFormat = new SimpleDateFormat(datePatternFormat);
                Date date = dateFormat.parse(dateString);
                GregorianCalendar calendar = new GregorianCalendar();
                calendar.setTime(date);
                return calendar.getTime();
            } catch (ParseException ex) {
                throw new JsonParseException(ex);
            }
        }
    }

    class TestExclStrat implements ExclusionStrategy {
        public boolean shouldSkipClass(Class<?> arg0) {
            return false;
        }

        public boolean shouldSkipField(FieldAttributes f) {
            return (f.getName().equals("$__handler"));
        }
    }

    public String getDatePatternFormat() {
        return datePatternFormat;
    }

    public void setDatePatternFormat(String datePatternFormat) {
        this.datePatternFormat = datePatternFormat;
    }
}

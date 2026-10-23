package com.example.restaurantapp.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.restaurantapp.models.User;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context) {
        super(context, "test.db", null, 3);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "age INTEGER, creditPoints INTEGER, isActive INTEGER, image TEXT, "
                + "fullName TEXT, username TEXT, password TEXT, phone TEXT, gender TEXT, "
                + "role TEXT, specialty TEXT, email TEXT, location TEXT, joinedDate TEXT)");
        if (DatabaseUtils.queryNumEntries(db, "users") == 0) {
            addDemoUser(db, "Morgan Lee", "morgan.lee@example.com", "Admin");
            addDemoUser(db, "Alex Rivera", "alex.rivera@example.com", "Member");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 3) onCreate(db);
    }

    private void addDemoUser(SQLiteDatabase db, String name, String email, String role) {
        ContentValues values = new ContentValues();
        values.put("fullName", name);
        values.put("email", email);
        values.put("password", "1234");
        values.put("role", role);
        values.put("isActive", 1);
        db.insert("users", null, values);
    }

    public User login(String email, String password) {
        Cursor cursor = getReadableDatabase().query("users", null, "email=? AND password=?",
                new String[]{email, password}, null, null, null);
        User user = null;
        if (cursor.moveToFirst()) {
            user = new User();
            user.id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            user.fullName = cursor.getString(cursor.getColumnIndexOrThrow("fullName"));
            user.email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
            user.password = cursor.getString(cursor.getColumnIndexOrThrow("password"));
            user.role = cursor.getString(cursor.getColumnIndexOrThrow("role"));
            user.image = cursor.getString(cursor.getColumnIndexOrThrow("image"));
        }
        cursor.close();
        return user;
    }

    public boolean emailExists(String email) {
        return DatabaseUtils.queryNumEntries(getReadableDatabase(), "users", "email=?",
                new String[]{email}) > 0;
    }

    public boolean register(User user) {
        ContentValues values = new ContentValues();
        values.put("fullName", user.fullName);
        values.put("email", user.email);
        values.put("password", user.password);
        values.put("role", user.role);
        values.put("image", user.image);
        values.put("isActive", user.isActive);
        long id = getWritableDatabase().insert("users", null, values);
        user.id = (int) id;
        return id != -1;
    }

    private String getTableName(Class<?> modelClass) throws ReflectiveOperationException {
        return (String) modelClass.getField("TABLE_NAME").get(null);
    }

    private boolean isColumnType(Class<?> type) {
        return type == String.class || type == int.class || type == Integer.class
                || type == double.class || type == Double.class;
    }

    private ContentValues toContentValues(Object model) throws IllegalAccessException {
        ContentValues values = new ContentValues();
        for (Field field : model.getClass().getFields()) {
            if (Modifier.isStatic(field.getModifiers()) || field.getName().equals("id")) continue;
            if (!isColumnType(field.getType())) throw new IllegalArgumentException(field.getName());
            Object value = field.get(model);
            if (value == null) values.putNull(field.getName());
            else if (value instanceof String) values.put(field.getName(), (String) value);
            else if (value instanceof Integer) values.put(field.getName(), (Integer) value);
            else values.put(field.getName(), (Double) value);
        }
        return values;
    }

    private <T> T fromCursor(Cursor cursor, Class<T> modelClass) throws ReflectiveOperationException {
        T model = modelClass.getDeclaredConstructor().newInstance();
        for (Field field : modelClass.getFields()) {
            if (Modifier.isStatic(field.getModifiers())) continue;
            Class<?> type = field.getType();
            if (!isColumnType(type)) throw new IllegalArgumentException(field.getName());
            int column = cursor.getColumnIndexOrThrow(field.getName());
            if (cursor.isNull(column) && !type.isPrimitive()) field.set(model, null);
            else if (type == String.class) field.set(model, cursor.getString(column));
            else if (type == int.class || type == Integer.class) field.set(model, cursor.getInt(column));
            else field.set(model, cursor.getDouble(column));
        }
        return model;
    }

    public boolean create(Object model) throws ReflectiveOperationException {
        return getWritableDatabase().insert(getTableName(model.getClass()), null,
                toContentValues(model)) != -1;
    }

    public boolean update(Object model, int id) throws ReflectiveOperationException {
        return getWritableDatabase().update(getTableName(model.getClass()), toContentValues(model),
                "id=?", new String[]{String.valueOf(id)}) > 0;
    }

    public boolean delete(Class<?> modelClass, int id) throws ReflectiveOperationException {
        return getWritableDatabase().delete(getTableName(modelClass), "id=?",
                new String[]{String.valueOf(id)}) > 0;
    }

    public <T> T show(Class<T> modelClass, int id) throws ReflectiveOperationException {
        Cursor cursor = getReadableDatabase().query(getTableName(modelClass), null, "id=?",
                new String[]{String.valueOf(id)}, null, null, null);
        T model = cursor.moveToFirst() ? fromCursor(cursor, modelClass) : null;
        cursor.close();
        return model;
    }

    public <T> ArrayList<T> get(Class<T> modelClass) throws ReflectiveOperationException {
        ArrayList<T> models = new ArrayList<>();
        Cursor cursor = getReadableDatabase().query(getTableName(modelClass), null,
                null, null, null, null, "id DESC");
        while (cursor.moveToNext()) models.add(fromCursor(cursor, modelClass));
        cursor.close();
        return models;
    }
}

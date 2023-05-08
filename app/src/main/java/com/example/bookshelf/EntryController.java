package com.example.bookshelf;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;

import com.example.bookshelf.repository.Repository;
import com.example.bookshelf.repository.converters.RepositoryConverter;
import com.example.bookshelf.repository.converters.UserConverter;
import com.example.bookshelf.repository.objects.User;

import java.security.MessageDigest;
import java.util.Objects;

public class EntryController {
    private static User loggedUser;
    private static final String USERS_STORAGE_NAME = "users";
    private static final String ID_KEY = "id";
    private static final String HASH_KEY = "hash";
    private static SharedPreferences settings;

    private EntryController() {}

    public static void init(Context context) {
        settings = context.getSharedPreferences(USERS_STORAGE_NAME, Context.MODE_PRIVATE);
    }
    public static boolean isUserLoggedIn() {
        int id = settings.getInt(ID_KEY, 0);
        String hash = settings.getString(HASH_KEY, "");

        if (id == 0 || hash.isEmpty())
            return false;

        User user = (User) Repository.selectObject(id, new UserConverter());
        if (user == null)
            return false;
        boolean result = Objects.equals(user.getHash(), hash);

        if (result)
            rememberUser(user);
        return result;
    }

    public static boolean logIn(String login, String password) {
        User criteria = new User(0, login, "");
        User user = (User) Repository.selectObject(criteria, new UserConverter());

        if (user == null)
            return false;

        String enteredHash = calculateMD5Hash(password);
        boolean result = Objects.equals(enteredHash, user.getHash());

        if (result)
            rememberUser(user);
        return result;
    }

    public static void logOut() {
        loggedUser = null;

        SharedPreferences.Editor editor = settings.edit();
        editor.remove(ID_KEY);
        editor.remove(HASH_KEY);
        editor.apply();
    }

    public static void register(String login, String password) {
        String enteredHash = calculateMD5Hash(password);
        if (enteredHash == null)
            return;
        User user = new User(0, login, enteredHash);
        RepositoryConverter converter = new UserConverter();
        Repository.insertNewObject(user, converter);
        User result = (User) Repository.selectObject(user, converter);

        assert result != null;
        rememberUser(result);
    }

    public static User getLoggedUser() {
        return loggedUser;
    }

    private static void rememberUser(User loggedUser) {
        EntryController.loggedUser = loggedUser;

        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(ID_KEY, loggedUser.getId());
        editor.putString(HASH_KEY, loggedUser.getHash());
        editor.apply();
    }

    @Nullable
    private static String calculateMD5Hash(String data) {
        try {
            final MessageDigest digest = MessageDigest.getInstance("md5");
            digest.update(data.getBytes());
            final byte[] bytes = digest.digest();
            final StringBuilder sb = new StringBuilder();
            for (byte dataByte : bytes) {
                sb.append(String.format("%02X", dataByte));
            }
            return sb.toString().toLowerCase();
        } catch (Exception exc) {
            return null;
        }
    }
}

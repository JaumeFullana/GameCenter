package com.cifpfbmoll.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.cifpfbmoll.gamecenter.Score;

import java.util.ArrayList;


public class DataBaseAssistant extends SQLiteOpenHelper {

    private final static String DB_NAME="myDataBase";
    private final static int DB_VERSION=1;

    private SQLiteDatabase dbWritable;
    private SQLiteDatabase dbReadable;

    public DataBaseAssistant(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    /**
     * Overrided method. Creates the tables of the DDBB.
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDb) {
        sqLiteDb.execSQL("CREATE TABLE IF NOT EXISTS user (user_name TEXT PRIMARY KEY, user_password TEXT NOT NULL," +
                " user_picture BLOB);");
        sqLiteDb.execSQL("CREATE TABLE IF NOT EXISTS score (score INTEGER, time TEXT, game TEXT, mode TEXT, user_name TEXT);");
    }

    /**
     * Overrided method. Recreates the tables of the DDBB.
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDb, int i, int i1) {
        sqLiteDb.execSQL("DROP TABLE IF EXISTS user");
        sqLiteDb.execSQL("DROP TABLE IF EXISTS score");
        onCreate(sqLiteDb);
    }

    /**
     * Inserts a user into the DDBB.
     * @param name name of the user
     * @param password password of the user
     * @return boolean to know if the user was inserted or not
     */
    public boolean addUser(String name, String password){
        boolean inserted=false;
        if (this.dbWritable == null) {
            this.dbWritable = this.getWritableDatabase();
        }

        try{
            ContentValues c=new ContentValues();
            c.put("user_name", name);
            c.put("user_password", password);
            c.put("user_picture", new byte[0]);
            dbWritable.insertOrThrow("user",null, c);
            inserted=true;
            dbWritable.close();
        } catch (Exception ex){
            Log.d("Error",ex.getMessage());
            inserted=false;
        }

        return inserted;
    }

    /**
     * Insets a score into the DDBB
     * @param score score object where all the values of the score are saved.
     * @return boolean to know if the score was inserted or not
     */
    public boolean addScore(Score score){
        boolean inserted=false;
        if (this.dbWritable == null) {
            this.dbWritable = this.getWritableDatabase();
        }

        try{
            ContentValues c=new ContentValues();
            c.put("score", score.getScore());
            c.put("time", score.getTime());
            c.put("game", score.getGame());
            c.put("mode", score.getMode());
            c.put("user_name", score.getUser_name());
            dbWritable.insertOrThrow("score",null, c);
            inserted=true;
            dbWritable.close();
        } catch (Exception ex){
            Log.d("Error",ex.getMessage());
            inserted=false;
        }

        return inserted;
    }

    /**
     * Method to update a user setting a new user_picture.
     * @param userName the name of the user
     * @param pictureUri the new picture to insert in the DDBB
     * @return boolean to know if a row was updated or not
     */
    public boolean changeUserPicture(String userName, byte[] pictureUri){
        boolean updated=false;
        if (this.dbWritable == null) {
            this.dbWritable = this.getWritableDatabase();
        }

        try{
            ContentValues c=new ContentValues();
            c.put("user_picture", pictureUri);
            dbWritable.update("user", c, "user_name = ?",new String[]{userName});
            updated=true;
        } catch (Exception ex){
            Log.d("Error",ex.getMessage());
            updated=false;
        }
        return updated;
    }

    /**
     * Method to update a user setting a new password
     * @param userName the name of the user
     * @param password the new password to insert in the DDBB
     * @return boolean to know if a row was updated or not
     */
    public boolean changeUserPassword(String userName, String password){
        boolean updated=false;
        if (this.dbWritable == null) {
            this.dbWritable = this.getWritableDatabase();
        }

        try{
            ContentValues c=new ContentValues();
            c.put("user_password", password);
            dbWritable.update("user", c, "user_name = ?",new String[]{userName});
            updated=true;
        } catch (Exception ex){
            Log.d("Error",ex.getMessage());
            updated=false;
        }
        return updated;
    }

    /**
     * Method to know if exists a user with a specific password in the DDBB.
     * @param name name of the user
     * @param password password of the user
     * @return boolean to know if exists a user with that password
     */
    public boolean searchUser(String name, String password){
        boolean found = false;
        if (this.dbReadable == null) {
            this.dbReadable = this.getReadableDatabase();
        }
        Cursor cursor=dbReadable.rawQuery("SELECT user_name, user_password FROM user WHERE user_name = ? " +
                "and user_password = ?", new String[] {name, password});

        if (cursor != null && cursor.getCount() != 0){
            if (cursor.moveToFirst()){
                if (name.equals(cursor.getString(0))){
                    found = true;
                }
            }
        }
        cursor.close();

        return found;
    }

    /**
     * method to get the profile picture of a user as a byte[]
     * @param name name of the user
     * @return picture as a byte[]
     */
    public byte[] getUserPicture(String name){
        byte[] picture = null;
        if (this.dbReadable == null) {
            this.dbReadable = this.getReadableDatabase();
        }
        Cursor cursor=dbReadable.rawQuery("SELECT user_name, user_picture FROM user WHERE user_name = ?",
                new String[] {name});

        if (cursor != null && cursor.getCount() != 0){
            if (cursor.moveToFirst()){
                if (name.equals(cursor.getString(0))){
                    picture = cursor.getBlob(1);
                }
            }
        }
        cursor.close();

        return picture;
    }

    /**
     * Method that gets records from the DDBB. That records can be filtered througth the parameters
     * passed to the method
     * @param userName user name to filter the records, can be null
     * @param game game name to filter the records, can be null
     * @param orderBy order by condition, in some cases is not necessary
     * @param scoreFiltering the way to filter the score, could be >, < or =
     * @param score score to filter the records, can be null
     * @return Cursor with the records
     */
    public Cursor getRecords(String userName, String game, String orderBy, String scoreFiltering, String score){
        if (this.dbReadable == null) {
            this.dbReadable = this.getReadableDatabase();
        }

        if (game != null && game.equals("2048")){
            orderBy+=" DESC";
        }

        Cursor cursor=null;
        if (game == null){
            cursor = dbReadable.rawQuery("SELECT * FROM score WHERE user_name = ?",new String[]{userName});
        }
        else if (userName == null || userName.isEmpty()) {
            if (score == null || score.isEmpty()) {
                cursor = dbReadable.rawQuery("SELECT * FROM score WHERE game=? ORDER BY " + orderBy, new String[]{game});
            } else {
                cursor = dbReadable.rawQuery("SELECT * FROM score WHERE game=? AND score "+scoreFiltering+
                        " ? ORDER BY " + orderBy, new String[]{game, score});
            }
        } else {
            userName="%"+userName+"%";
            if (score == null || score.isEmpty()){
                cursor = dbReadable.rawQuery("SELECT * FROM score WHERE user_name LIKE ? AND game=? ORDER BY " + orderBy,
                        new String[]{userName, game});
            } else {
                cursor = dbReadable.rawQuery("SELECT * FROM score WHERE user_name LIKE ? AND game=? AND score "
                                + scoreFiltering + " ? ORDER BY " + orderBy,
                        new String[]{userName, game, score});
            }
        }

        return cursor;
    }

    /**
     * Method that deletes a user from the DDBB.
     * @param name name of the user to delete.
     * @return boolean to know if the user was deleted
     */
    public boolean deleteUser(String name){
        boolean deleted = false;
        if (this.dbWritable == null) {
            this.dbWritable = this.getReadableDatabase();
        }

        int deletedRows=this.dbWritable.delete("user","user_name=?",new String[]{name});
        if (deletedRows==1){
            deleted=true;
        }

        return deleted;
    }

    /**
     * Method that deletes a score from the DDBB.
     * @param name user name that owns the record
     * @param score score puntuation
     * @param time time of the score
     * @return boolean to know if the score was deleted.
     */
    public boolean deleteScore(String name, String score, String time){
        boolean deleted = false;
        if (this.dbWritable == null) {
            this.dbWritable = this.getReadableDatabase();
        }

        int deletedRows=this.dbWritable.delete("score","user_name=? AND score=? AND time=?",
                new String[]{name, score, time});
        if (deletedRows==1){
            deleted=true;
        }

        return deleted;
    }
}


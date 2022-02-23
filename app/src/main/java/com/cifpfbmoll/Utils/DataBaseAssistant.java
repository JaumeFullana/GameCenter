package com.cifpfbmoll.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DataBaseAssistant extends SQLiteOpenHelper {

    private final static String DB_NAME="myDataBase";
    private final static int DB_VERSION=1;

    private SQLiteDatabase dbWritable;
    private SQLiteDatabase dbReadable;

    public DataBaseAssistant(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDb) {
        sqLiteDb.execSQL("CREATE TABLE IF NOT EXISTS user (user_name TEXT PRIMARY KEY, user_password TEXT NOT NULL);");
        //comprobar score, per ara aixi. Pensa si fe dues o una, ja que alomillo com a socre no guardarem lo mateix
        //ja que es peg sera per temps encanvi es 2048 sera punutacio (+ temps alomillo?)
        sqLiteDb.execSQL("CREATE TABLE IF NOT EXISTS score (score INTEGER, time TEXT, game TEXT, user_name TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDb, int i, int i1) {
        sqLiteDb.execSQL("DROP TABLE IF EXISTS user");
        sqLiteDb.execSQL("DROP TABLE IF EXISTS score");
        onCreate(sqLiteDb);
    }

    public boolean addUser(String name, String password){
        boolean inserted=false;
        if (this.dbWritable == null) {
            this.dbWritable = this.getWritableDatabase();
        }

        try{
            ContentValues c=new ContentValues();
            c.put("user_name", name);
            c.put("user_password", password);
            dbWritable.insertOrThrow("user",null, c);
            inserted=true;
            dbWritable.close();
        } catch (Exception ex){
            inserted=false;
        }

        return inserted;
    }


    public boolean selectUser(String name, String password){
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

    /*
    public void addComment(String name, String text){
        SQLiteDatabase db = this.getWritableDatabase();
        String [] args={name,text};
        db.execSQL("INSERT INTO comment (comment_name, comment_text) VALUES (?, ?)",args);
        db.close();
    }

    public ArrayList<String[]> getComments(){
        ArrayList <String[]> commentsList=new ArrayList<>();
        Cursor cursor=this.getReadableDatabase().rawQuery("SELECT * FROM comment",null);
        if (cursor!=null && cursor.getCount() != 0){
            if (cursor.moveToFirst()){
                do {
                    Log.d("hola",cursor.getString(0));
                    String name=cursor.getString(1);
                    String text=cursor.getString(2);
                    String [] comment={name,text};
                    commentsList.add(comment);
                } while (cursor.moveToNext());
            }
        }
        cursor.close();
        return commentsList;
    }

    public void removeComment(String name){
        String[] args = {name};
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("DELETE FROM comment WHERE comment_name=?",args);
        db.close();
    }
     */
}


package com.example.android.pets.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.android.pets.data.PetContract.PetEntry;

public class PetProvider extends ContentProvider {

    public static final String LOG_TAG = PetProvider.class.getSimpleName();

    private static final int PETS = 100;
    private static final int PETS_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private PetDbHelper mDbHelper;
    static {
        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY,PetContract.PATH_PETS,PETS);
        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY,PetContract.PATH_PETS+"/#",PETS_ID);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new PetDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projections, String selections, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        Cursor cursor;
        int match = sUriMatcher.match(uri);
        switch (match){
            case PETS:
                cursor = db.query(PetEntry.TABLE_NAME,projections,selections,selectionArgs,null,null,sortOrder);

                break;
            case PETS_ID:
                selections = PetEntry._ID+"=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(PetEntry.TABLE_NAME,projections,selections,selectionArgs,null,null,sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot Query Unknown URI "+uri);
        }
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch(match){
            case PETS:
                return insertPet(uri,contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for URI: "+uri);
        }
    }

    private Uri insertPet(Uri uri, ContentValues contentValues) {
        //series of sanity checks
        //name
        String name = contentValues.getAsString(PetEntry.COLUMN_PET_NAME);
        if(name==null){
            throw new IllegalArgumentException("Pet Requires a name");
        }
        //gender
        Integer gender = contentValues.getAsInteger(PetEntry.COLUMN_PET_GENDER);
        if(gender == null||!PetEntry.isValidGender(gender)){
            throw new IllegalArgumentException("Pet requires valid Gender");
        }
        //If the weight is provided, check that its greater than or equal to 0kg
        Integer weight = contentValues.getAsInteger(PetEntry.COLUMN_PET_WEIGHT);
        if(weight!=null&&weight<0){
            throw new IllegalArgumentException("Pet requires Valid weight");
        }


        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long newRowId = db.insert(PetEntry.TABLE_NAME,null,contentValues);
        if(newRowId==-1){
            Log.e(LOG_TAG,"Failed to insert row for "+uri);
            return null;
        }
        return ContentUris.withAppendedId(uri,newRowId);
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}

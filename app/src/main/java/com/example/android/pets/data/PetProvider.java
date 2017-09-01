package com.example.android.pets.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.sql.SQLData;

/**
 * Created by Dan14z on 30/08/2017.
 */

public class PetProvider extends ContentProvider {

    public static final String LOG_TAG = PetProvider.class.getSimpleName();
    private static final int PETS = 100;
    private static final int PET_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        // The content URI of the form "content://com.example.android.pets/pets" will map to the
        // integer code {@link #PETS}. This URI is used to provide access to MULTIPLE rows
        // of the pets table.
        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PATH_PETS, PETS);

        // The content URI of the form "content://com.example.android.pets/pets/#" will map to the
        // integer code {@link #PET_ID}. This URI is used to provide access to ONE single row
        // of the pets table.
        //
        // In this case, the "#" wildcard is used where "#" can be substituted for an integer.
        // For example, "content://com.example.android.pets/pets/3" matches, but
        // "content://com.example.android.pets/pets" (without a number at the end) doesn't match.
        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PATH_PETS + "/#", PET_ID);
    }

    /**
     * Initialize the provider and the database helper object.
     */
    private PetDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new PetDbHelper(getContext());
        return true;
    }
    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        Cursor cursor = null;

        int match = sUriMatcher.match(uri);

        switch(match){
            case PETS:
                cursor = db.query(PetContract.PetEntry.TABLE_NAME,projection,selection,selectionArgs,
                        null,null,sortOrder);
                break;
            case PET_ID:
                selection = PetContract.PetEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(PetContract.PetEntry.TABLE_NAME,projection,selection,selectionArgs,
                        null,null,sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(),uri);

        return cursor;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch(match){
            case PETS:
                return insertPet(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertPet(Uri uri, ContentValues values){

        String name = values.getAsString(PetContract.PetEntry.COLUMN_PET_NAME).trim();
        if(name == null || name == ""){
            throw new IllegalArgumentException("Pet requires a name");
        }

        Integer gender = values.getAsInteger(PetContract.PetEntry.COLUMN_PET_GENDER);
        if(gender == null ||  !PetContract.PetEntry.isValidGender(gender)){
            throw new IllegalArgumentException("Pet requires a valid gender");
        }

        Integer weight = values.getAsInteger(PetContract.PetEntry.COLUMN_PET_WEIGHT);
        if(weight == null || weight < 0){
            throw new IllegalArgumentException("Pet requires a valid weight");
        }

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        long newRowId = db.insert(PetContract.PetEntry.TABLE_NAME,null,values);

        if(newRowId == -1){
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri,newRowId);
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch(match){
            case PETS:
                return updatePet(uri,contentValues,selection,selectionArgs);
            case PET_ID:
                selection = PetContract.PetEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                return updatePet(uri,contentValues,selection,selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    public int updatePet(Uri uri, ContentValues values, String selection, String[] selectionArgs){

        if(values.containsKey(PetContract.PetEntry.COLUMN_PET_NAME)){
            String name = values.getAsString(PetContract.PetEntry.COLUMN_PET_NAME).trim();
            if(name == null || name == ""){
                throw new IllegalArgumentException("Pet requires a name");
            }
        }

        if(values.containsKey(PetContract.PetEntry.COLUMN_PET_GENDER)){
            Integer gender = values.getAsInteger(PetContract.PetEntry.COLUMN_PET_GENDER);
            if(gender == null || !PetContract.PetEntry.isValidGender(gender)){
                throw new IllegalArgumentException("Pet requires a valid gender");
            }
        }

        if(values.containsKey(PetContract.PetEntry.COLUMN_PET_WEIGHT)){
            Integer weight = values.getAsInteger(PetContract.PetEntry.COLUMN_PET_WEIGHT);
            if(weight == null || weight < 0){
                throw new IllegalArgumentException("Pet requires a valid weight");
            }
        }

        if(values.size() == 0){
            return 0;
        }

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int rowsUpdated = db.update(PetContract.PetEntry.TABLE_NAME,values,selection,selectionArgs);

        if(rowsUpdated != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;

    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);

        int rowsDeleted;
        int rowDeleted;

        switch(match){
            case PETS:
                rowsDeleted = db.delete(PetContract.PetEntry.TABLE_NAME,selection,selectionArgs);

                if(rowsDeleted != 0){
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return rowsDeleted;
            case PET_ID:
                selection = PetContract.PetEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri))};

                rowDeleted = db.delete(PetContract.PetEntry.TABLE_NAME,selection,selectionArgs);

                if(rowDeleted != 0){
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return rowDeleted;
            default:
                throw new IllegalArgumentException("Deletion is not supported for  " + uri);
        }
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch(match){
            case PETS:
                return PetContract.PetEntry.CONTENT_LIST_TYPE;
            case PET_ID:
                return PetContract.PetEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

}

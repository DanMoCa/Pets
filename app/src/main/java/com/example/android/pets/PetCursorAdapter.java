package com.example.android.pets;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.pets.R;
import com.example.android.pets.data.PetContract;

/**
 * Created by Dan14z on 31/08/2017.
 */

public class PetCursorAdapter extends CursorAdapter{

    public PetCursorAdapter(Context context, Cursor c) {
        super(context, c,0 /* falgs */);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Return a list_item view
        return LayoutInflater.from(context).inflate(R.layout.list_item,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // TODO: fill out this method
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView summaryTextView = (TextView) view.findViewById(R.id.summary);

        int nameColumnIndex = cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_NAME);
        int breedColumnIndex = cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_BREED);

        String petName = cursor.getString(nameColumnIndex);
        String petBreed = cursor.getString(breedColumnIndex);

        if(TextUtils.isEmpty(petBreed)){
            petBreed = context.getString(R.string.unknown_breed);
        }

        nameTextView.setText(petName);
        summaryTextView.setText(petBreed);
    }
}


// 2 blandos 1 tortilla Karen 20
// 4 dorados un chesco  Lalo  52
// 4 medios un chesco   JL  52
// 4 blandos un chesco  Dan  52
// 2 dorados un chesco  Pati 32


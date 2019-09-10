/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.pets;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.android.pets.data.PetContract.PetEntry;
import com.example.android.pets.data.PetDbHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity {

    /** Database helper that will provide us access to the database */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    private void displayDatabaseInfo() {

        //Create and or open a database

        //Perform raw SQL select from the database opened
        String[] projection = {
                PetEntry._ID,
                PetEntry.COLUMN_PET_NAME,
                PetEntry.COLUMN_PET_BREED,
                PetEntry.COLUMN_PET_GENDER,
                PetEntry.COLUMN_PET_WEIGHT
        };

        Cursor cursor = getContentResolver().query(PetEntry.CONTENT_URI, projection, null, null, null);
        TextView displayView = findViewById(R.id.text_view_pet);

        try {

            displayView.setText("Number of rows in Pets Database Table: " + cursor.getCount()+"\n");
            displayView.append("\n" + PetEntry._ID + " - " + PetEntry.COLUMN_PET_NAME+" - "+PetEntry.COLUMN_PET_BREED+
                    " - "+PetEntry.COLUMN_PET_GENDER+" - "+PetEntry.COLUMN_PET_WEIGHT);

            int mIdColumnIndex = cursor.getColumnIndex(PetEntry._ID);
            int mNameColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_NAME);
            int mBreedColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_BREED);
            int mGenderColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_GENDER);
            int mWeightColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_WEIGHT);

            while (cursor.moveToNext()) {
                int cursorId = cursor.getInt(mIdColumnIndex);
                String cursorName = cursor.getString(mNameColumnIndex);
                String cursorBreed = cursor.getString(mBreedColumnIndex);
                int cursorGender = cursor.getInt(mGenderColumnIndex);
                int cursorWeight = cursor.getInt(mWeightColumnIndex);

                displayView.append("\n" + cursorId + " - " + cursorName+" - "+cursorBreed+" - "+cursorGender+" - "+cursorWeight);
            }

        } finally {
            //close the cursor always
            cursor.close();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertDummyData();
                displayDatabaseInfo();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                // Do nothing for now
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void insertDummyData() {
        ContentValues values = new ContentValues();
        values.put(PetEntry.COLUMN_PET_NAME,"Toto");
        values.put(PetEntry.COLUMN_PET_BREED,"Terrier");
        values.put(PetEntry.COLUMN_PET_GENDER,PetEntry.GENDER_MALE);
        values.put(PetEntry.COLUMN_PET_WEIGHT,7);

        Uri newUri = getContentResolver().insert(PetEntry.CONTENT_URI,values);
    }
}

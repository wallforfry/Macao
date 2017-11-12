package fr.esiee.bde.macao;

/**
 * Created by Wallerand on 06/06/2017.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import fr.esiee.bde.macao.Calendar.CalendarEvent;
import fr.esiee.bde.macao.Events.Event;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class DataBaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Macao_V2.db";
    private static final int DATABASE_VERSION = 1;

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    static {
        // register our models
        cupboard().register(CalendarEvent.class);
        cupboard().register(Event.class);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // this will ensure that all tables are created
        cupboard().withDatabase(db).createTables();
        // add indexes and other database tweaks in this method if you want
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // this will upgrade tables, adding columns and new tables.
        // Note that existing columns will not be converted
        //cupboard().withDatabase(db).dropAllTables();
        //cupboard().withDatabase(db).dropAllIndices();
        //cupboard().withDatabase(db).createTables();
        cupboard().withDatabase(db).upgradeTables();
        // do migration work if you have an alteration to make to your schema here

    }

}

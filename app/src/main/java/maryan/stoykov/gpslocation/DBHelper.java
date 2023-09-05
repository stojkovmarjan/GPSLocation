package maryan.stoykov.gpslocation;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "tracking.db";
    private static final int DATABASE_VERSION = 1;
    private static final String CREATE_TABLE_LOCATIONS = "CREATE TABLE Locations (" +
            "Id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "Date TEXT NOT NULL," +
            "Time TEXT NOT NULL," +
            "Latitude REAL NOT NULL," +
            "Longitude REAL NOT NULL," +
            "Accuracy REAL NOT NULL," +
            "Message TEXT)";
    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_LOCATIONS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }
}

package maryan.stoykov.gpslocation;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "tracking.db";
    private static final int DATABASE_VERSION = 1;
    private static final String LOCATIONS_TABLE = "Locations";
    private static final String CREATE_TABLE_LOCATIONS = "CREATE TABLE Locations (" +
            "Id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "DateTime TEXT NOT NULL," +
            "DeviceId TEXT NOT NULL,"+
            "Latitude REAL NOT NULL," +
            "Longitude REAL NOT NULL," +
            "Accuracy REAL NOT NULL," +
            "Provider TEXT NOT NULL,"+
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
    public Long addLocation(LocationDbRecord locationDbRecord){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("DateTime", locationDbRecord.getDateTime());
        values.put("DeviceId",locationDbRecord.getDeviceId());
        values.put("Longitude",locationDbRecord.getLongitude());
        values.put("Latitude",locationDbRecord.getLatitude());
        values.put("Accuracy",locationDbRecord.getAccuracy());
        values.put("Provider",locationDbRecord.getProvider());

        long newRowId = db.insert(LOCATIONS_TABLE, null, values);
        db.close();

        return newRowId;
    }
}

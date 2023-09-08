package maryan.stoykov.gpslocation;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

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
    /** @noinspection ReassignedVariable*/
    public Long addLocation(LocationDbRecord locationDbRecord){

        SQLiteDatabase db = this.getWritableDatabase();

        String dbMessage="db record";

        ContentValues values = new ContentValues();

        if (!locationDbRecord.getMessage().equals("")){
            dbMessage = ", db record";
        }

        values.put("DateTime", locationDbRecord.getDateTime());
        values.put("DeviceId",locationDbRecord.getDeviceId());
        values.put("Longitude",locationDbRecord.getLongitude());
        values.put("Latitude",locationDbRecord.getLatitude());
        values.put("Accuracy",locationDbRecord.getAccuracy());
        values.put("Provider",locationDbRecord.getProvider());
        values.put("Message",locationDbRecord.getMessage()+dbMessage);

        long newRowId = db.insert(LOCATIONS_TABLE, null, values);

        db.close();

        return newRowId;
    }
    public List<LocationDbRecord> getLocationsList(){

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;

        try {
            String query = "SELECT * FROM "+ DBHelper.LOCATIONS_TABLE;
            cursor = db.rawQuery(query, null);
        } catch (Exception e) {
            Log.e("DBHelper", "Error creating cursor");
            e.printStackTrace();
        }

        List<LocationDbRecord> locationDbRecords = new ArrayList<>();

        if (cursor != null){
            try {

                while (cursor.moveToNext()){

                    Long id = cursor.getLong(cursor.getColumnIndexOrThrow("Id"));
                    String dateTime = cursor.getString(cursor.getColumnIndexOrThrow("DateTime"));
                    String deviceId = cursor.getString(cursor.getColumnIndexOrThrow("DeviceId"));
                    Double latitude = cursor.getDouble(cursor.getColumnIndexOrThrow("Latitude"));
                    Double longitude = cursor.getDouble(cursor.getColumnIndexOrThrow("Longitude"));
                    Float accuracy = cursor.getFloat(cursor.getColumnIndexOrThrow("Accuracy"));
                    String provider = cursor.getString(cursor.getColumnIndexOrThrow("Provider"));
                    String message = cursor.getString(cursor.getColumnIndexOrThrow("Message"));

                    locationDbRecords.add(new LocationDbRecord(
                            id, dateTime, deviceId, latitude, longitude,accuracy, provider, message
                    ));

                }

            } catch (Exception e){
                Log.e("DBHelper", "Error creating locations list from DB");
                e.printStackTrace();
            } finally {
                cursor.close();
                db.close();
            }
        }

        return locationDbRecords;
    }

    public int deleteLocationRecord(long id) {

        try (SQLiteDatabase db = this.getWritableDatabase()) {

            String whereClause = "Id = ?";
            String[] whereArgs = {String.valueOf(id)};
            int rowsDeleted = db.delete(LOCATIONS_TABLE, whereClause, whereArgs);
            Log.i("DBHelper", id+" deleted");
            return rowsDeleted;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }

    }
    public int getRecordsCount() {

        SQLiteDatabase db = this.getReadableDatabase();

        int count = 0;

        try {
            String query = "SELECT COUNT(*) FROM "+LOCATIONS_TABLE;
            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
            db.close();
        }

        db.close();

        return count;
    }
}

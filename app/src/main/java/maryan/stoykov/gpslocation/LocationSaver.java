package maryan.stoykov.gpslocation;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class LocationSaver {

    boolean success = true;
    //String filename;
    public LocationSaver(){
        //this.filename = filename;
    }

    public void SaveFile(String filename) {

        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), "my_file.txt"
        );

        Log.e("FILE SAVE HIT:", "1");
        Thread fileSaveThread= new Thread(new Runnable() {

            @Override
            public void run() {

                if (!file.exists()){
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        Log.e("FILE SAVE ERROR:", "Create file failed!");
                        success = false;
                    }
                }

                // Create a FileOutputStream object to write to the file.
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(file, true);
                } catch (FileNotFoundException e) {
                    Log.e("FILE SAVE ERROR:", "File not found!");
                    success = false;
                }

                // Write the data to the file.
                String data = "This is some data that I want to write to the file.";
                try {
                    fos.write(data.getBytes());
                    fos.flush();
                } catch (IOException e) {
                    Log.e("FILE SAVE ERROR:", "Writing to file error!");
                    success = false;
                }

                // Close the FileOutputStream object.
                try {
                    fos.close();
                } catch (IOException e) {
                    Log.e("FILE SAVE ERROR:", "Closing output stream failed!");
                    success = false;
                }
            }

        });

        fileSaveThread.start();

//        if (success){
//            Log.e("FILE SAVE:","SUCCESS");
//        } else {
//            Log.e("FILE SAVE:","FAILED");
//        }
//        return success;
    }
}

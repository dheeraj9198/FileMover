package mebeerhu.dheeraj.sachan.filemover;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.StatFs;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;

public class MainActivity extends ActionBarActivity {
    private static final String ext = "Android/data/mebeerhu.dheeraj.sachan.filemover/files";
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.list_view);
        final ArrayAdapter<StorageOption> storageOptionArrayAdapter = new ArrayAdapter<StorageOption>(getApplicationContext(), R.layout.simple_view, new ArrayList<StorageOption>(getStorageOptions(getApplicationContext()))) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                StorageOption storageOption = getItem(position);
                View view = getLayoutInflater().inflate(R.layout.simple_view, null);
                TextView textViewName, textViewLocation, textViewFreeSpace;
                textViewName = (TextView) view.findViewById(R.id.name);
                textViewLocation = (TextView) view.findViewById(R.id.location);
                textViewFreeSpace = (TextView) view.findViewById(R.id.free);

                textViewName.setText(storageOption.getName());
                textViewLocation.setText(storageOption.getLocation());
                textViewFreeSpace.setText(storageOption.getFreeSpace() + " MB");

                return view;
            }
        };
        listView.setAdapter(storageOptionArrayAdapter);
        new AsyncTask<Void, Void, Boolean>() {

            String fileString = "" ;
            @Override
            protected Boolean doInBackground(Void... params) {
                File file1 = getApplicationContext().getExternalFilesDir(null);
/*
                File file1 = new File("/storage/extSdCard/Android/data/mebeerhu.dheeraj.sachan.filemover/files/dheeraja.txt");
*/
                fileString = file1.getAbsolutePath();
                OutputStream outStream = null;
                try {
                    outStream = new FileOutputStream(file1);
                    BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file1));
                    for(int k =0;k<10;k++){
                        bufferedWriter.write("dheeraj");
                        bufferedWriter.newLine();
                    }

                    bufferedWriter.close();
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                Toast.makeText(getApplicationContext(), fileString + "->" + (aBoolean ? "success" : "failed"), Toast.LENGTH_SHORT).show();
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private static long kilo = 1024L;
    private static String INTERAL_STORAGE = "internal storage";
    private static String EXTERNAL_STORAGE = "external storage";

    public static HashSet<StorageOption> getStorageOptions(Context context) {
        ArrayList<StorageOption> storageOptions = new ArrayList<>();
        HashSet<StorageOption> storageOptionHashSetFinal = new HashSet<StorageOption>();

        File internalCard = Environment.getExternalStorageDirectory();
        String state = Environment.getExternalStorageState();

        StatFs statFs = new StatFs(internalCard.getAbsolutePath());
        long freeSpace = (long) statFs.getBlockSize() * (long) statFs.getAvailableBlocks() / kilo / kilo;
        long totalSpace = (long) statFs.getBlockSize() * (long) statFs.getBlockCount() / kilo / kilo;
        StorageOption storageOption = new StorageOption(INTERAL_STORAGE, internalCard.getAbsolutePath(), totalSpace, freeSpace);
        storageOptionHashSetFinal.add(storageOption);

        File[] files = internalCard.getParentFile().listFiles();

        if (files.length > 0) {
            for (File testFile : files) {
                if (testFile.exists() && testFile.isDirectory() && testFile.getFreeSpace() > 0L && testFile.compareTo(Environment.getExternalStorageDirectory()) != 0 && testFile.getFreeSpace() != internalCard.getFreeSpace()) {
                    statFs = new StatFs(testFile.getAbsolutePath());
                    freeSpace = (long) statFs.getBlockSize() * (long) statFs.getAvailableBlocks() / kilo / kilo;
                    totalSpace = (long) statFs.getBlockSize() * (long) statFs.getBlockCount() / kilo / kilo;
                    storageOption = new StorageOption(EXTERNAL_STORAGE, testFile.getAbsolutePath(), totalSpace, freeSpace);
                    storageOptions.add(storageOption);
                }
            }
        }

        File file = new File("/mnt");
        if (file.exists() && file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null && children.length > 0) {
                for (File testFile : children) {
                    if (testFile.exists() && testFile.isDirectory() && testFile.getFreeSpace() > 0L && testFile.compareTo(Environment.getExternalStorageDirectory()) != 0 && testFile.getFreeSpace() != internalCard.getFreeSpace()) {
                        statFs = new StatFs(testFile.getAbsolutePath());
                        freeSpace = (long) statFs.getBlockSize() * (long) statFs.getAvailableBlocks() / kilo / kilo;
                        totalSpace = (long) statFs.getBlockSize() * (long) statFs.getBlockCount() / kilo / kilo;

                        storageOption = new StorageOption(EXTERNAL_STORAGE, testFile.getAbsolutePath(), totalSpace, freeSpace);
                        storageOptions.add(storageOption);
                    }
                }
            }
        }

        file = new File("/storage");
        if (file.exists() && file.isDirectory()) {
            File[] childs = file.listFiles();
            if (childs != null && childs.length > 0) {
                for (File testFile : childs) {
                    if (testFile.exists() && testFile.isDirectory() && testFile.getFreeSpace() > 0L && testFile.compareTo(Environment.getExternalStorageDirectory()) != 0 && testFile.getFreeSpace() != internalCard.getFreeSpace()) {
                        statFs = new StatFs(testFile.getAbsolutePath());
                        freeSpace = (long) statFs.getBlockSize() * (long) statFs.getAvailableBlocks() / kilo / kilo;
                        totalSpace = (long) statFs.getBlockSize() * (long) statFs.getBlockCount() / kilo / kilo;

                        storageOption = new StorageOption(EXTERNAL_STORAGE, testFile.getAbsolutePath(), totalSpace, freeSpace);
                        storageOptions.add(storageOption);
                    }
                }
            }
        }

        for (StorageOption storageOption1 : storageOptions) {
            boolean contains = false;
            for (String s : STRING_HASH_SET) {
                if (storageOption1.getLocation().contains(s)) {
                    contains = true;
                    break;
                }
            }
            if (contains) {
                continue;
            } else {
                boolean add = true;
                for (StorageOption storageOption2 : storageOptionHashSetFinal) {
                    if (storageOption2.getFreeSpace().equals(storageOption1.getFreeSpace())) {
                        add = false;
                        break;
                    }
                }
                if (add) {
                    storageOptionHashSetFinal.add(storageOption1);
                }
            }
        }

        return storageOptionHashSetFinal;

    }

    public static class StorageOption {
        private String name;
        private String location;
        private long freeSpace;
        private long totalSpace;

        public StorageOption(String name, String location, long totalSpace, long freeSpace) {
            this.name = name;
            this.location = location;
            this.freeSpace = freeSpace;
            this.totalSpace = totalSpace;
        }

        public String getLocation() {
            return location;
        }

        public String getFreeSpace() {
            return freeSpace + "(free) /" + totalSpace+" (total)" ;
        }

        public long getTotalSpace() {
            return totalSpace;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return name + " (" + NumberFormat.getNumberInstance(Locale.US).format(Math.round(freeSpace)) + "/" + NumberFormat.getNumberInstance(Locale.US).format(Math.round(totalSpace)) + " MB free)";
        }

        @Override
        public boolean equals(Object o) {
            StorageOption storageOption = (StorageOption) o;
            return location.equals(storageOption.getLocation()) && name.equals(storageOption.getName());
        }

        @Override
        public int hashCode() {
            return location.hashCode() + name.hashCode();
        }
    }

    private static final HashSet<String> STRING_HASH_SET = new HashSet<String>(Arrays.asList(new String[]{"/mnt/secure", "/mnt/asec", "/mnt/obb", "/dev/mapper", "tmpfs", "/storage/emulated"}));

}
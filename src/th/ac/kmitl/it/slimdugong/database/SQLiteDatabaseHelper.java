package th.ac.kmitl.it.slimdugong.database;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import th.ac.kmitl.it.slimdugong.R;
import th.ac.kmitl.it.slimdugong.database.entity.Athletic;
import th.ac.kmitl.it.slimdugong.database.entity.Barcode;
import th.ac.kmitl.it.slimdugong.database.entity.Food;
import th.ac.kmitl.it.slimdugong.database.entity.FoodType;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLiteDatabaseHelper{
	
	private Context context;
	private SQLiteDatabase db;
	
	// If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "slimdugong";
    
    private final String DROP = "DROP TABLE IF EXISTS ";
    private final String END = ";";
    private final String DROP_ALL_TABLE =
    		DROP + Athletic.TABLE_NAME + END +
    		DROP + FoodType.TABLE_NAME + END +
    		DROP + Food.TABLE_NAME + END +
    		DROP + Barcode.TABLE_NAME + END;


	public SQLiteDatabaseHelper(Context context) {
		this.context = context;
		db = context.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null);
		onCreate();
	}
	
	public void onCreate() {
		
		executeQueries(DROP_ALL_TABLE.split(END));
		
		InputStream inputStream;
		try {
			inputStream = context.openFileInput("lastest_database");
		} catch (FileNotFoundException e) {
			inputStream = context.getResources().openRawResource(R.raw.defualt_database);
		}
		
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
		
		StringBuilder sb = new StringBuilder();
		String line;			
		try {
			while ((line = bufferedReader.readLine()) != null){
			    sb.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		executeQueries(sb.toString().split(END));
	}
	
	private void executeQueries(String[] queries){
		for (String query : queries) {
			db.execSQL(query);
		}		
	}
	
	public SQLiteDatabase getSQLiteDatabase() {
		return db;		
	}
}
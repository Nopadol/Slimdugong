package th.ac.kmitl.it.slimdugong.database;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Vector;

import javax.net.ssl.HttpsURLConnection;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpResponseException;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import th.ac.kmitl.it.slimdugong.MainActivity;
import th.ac.kmitl.it.slimdugong.R;
import th.ac.kmitl.it.slimdugong.SlimDugong;
import th.ac.kmitl.it.slimdugong.database.entity.Athletic;
import th.ac.kmitl.it.slimdugong.database.entity.Barcode;
import th.ac.kmitl.it.slimdugong.database.entity.Food;
import th.ac.kmitl.it.slimdugong.database.entity.FoodType;
import th.ac.kmitl.it.slimdugong.database.entity.local.Consume;
import th.ac.kmitl.it.slimdugong.database.entity.local.Exercise;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

public class DatabaseManager {
	
    private static final String TOTAL = "TOTAL";    
    private static final String URL_UPDATE = "https://dl.dropboxusercontent.com/s/geo8qml81gbd0z9/version.txt";
    private static final String KEY_VERSION = "version";
    
    private static SlimDugong app;
    private Activity activity;
    
    private static final String DATABASE_FOODLIST = "DATABASE_FOODLIST";
	private static final String DATABASE_FOODTYPE = "DATABASE_FOODTYPE";
	private static final String DATABASE_CONSUME = "DATABASE_CONSUME";
	private static final String DATABASE_BARCODE = "DATABASE_BARCODE";
	private static final String DATABASE_ATHLETIC = "DATABASE_ATHLETIC";
	private static final String DATABASE_EXERCISE = "DATABASE_EXERCISE";
	private static final String DATABASE_VERSION = "DATABASE_VERSION";
    
    private TinyDB food_list_preference;
    private TinyDB food_type_preference;
    private TinyDB barcode_preference;
    private TinyDB athletic_preference;
    private TinyDB consume_preference;  
    private TinyDB exercise_preference;
    private TinyDB version_preference;
    
    public DatabaseManager(SlimDugong app) {
    	DatabaseManager.app = app;
    	this.food_list_preference = new TinyDB(app, DATABASE_FOODLIST, Context.MODE_PRIVATE);
    	this.food_type_preference = new TinyDB(app, DATABASE_FOODTYPE, Context.MODE_PRIVATE);
    	this.barcode_preference = new TinyDB(app, DATABASE_BARCODE, Context.MODE_PRIVATE);
    	this.athletic_preference = new TinyDB(app, DATABASE_ATHLETIC, Context.MODE_PRIVATE);
    	this.consume_preference = new TinyDB(app, DATABASE_CONSUME, Context.MODE_PRIVATE);
    	this.exercise_preference = new TinyDB(app, DATABASE_EXERCISE, Context.MODE_PRIVATE);
    	this.version_preference = new TinyDB(app, DATABASE_VERSION, Context.MODE_PRIVATE);
	}
	
	public void doUpdate(Activity activity){
		this.activity = activity;
		UpdateTaskRunner runner = new UpdateTaskRunner();
		runner.execute();
	}
	
	public void loadDatabase() {
		loadDataFromSQLite();
	}
	
	private void loadDataFromSQLite(){
		SQLiteDatabaseHelper mDbHelper = new SQLiteDatabaseHelper(app);
		SQLiteDatabase db = mDbHelper.getSQLiteDatabase();
		
		Cursor c = db.query(Food.TABLE_NAME, null, null, null, null, null, null);		
		ArrayList<Food> foodList = new ArrayList<Food>();
		c.moveToPosition(-1);
		while (c.moveToNext()) {
			Food entity = new Food();
			entity.setFoodId(c.getInt(c.getColumnIndexOrThrow(Food.COLUMN_NAME_ENTRY_ID)));
			entity.setFoodName(c.getString(c.getColumnIndexOrThrow(Food.COLUMN_NAME_ENTRY_NAME)));
			entity.setFoodCal(c.getInt(c.getColumnIndexOrThrow(Food.COLUMN_NAME_ENTRY_CAL)));
			entity.setFoodTypeId(c.getInt(c.getColumnIndexOrThrow(Food.COLUMN_NAME_ENTRY_FOODTYPE_ID)));
			foodList.add(entity);
		}
		c.close();
		app.setFoodList(foodList);
		
		
		c = db.query(FoodType.TABLE_NAME, null, null, null, null, null, null);		
		ArrayList<FoodType> foodTypeList = new ArrayList<FoodType>();
		c.moveToPosition(-1);
		while (c.moveToNext()) {
			FoodType entity = new FoodType();
			entity.setFoodTypeId(c.getInt(c.getColumnIndexOrThrow(FoodType.COLUMN_NAME_ENTRY_ID)));
			entity.setFoodTypeName(c.getString(c.getColumnIndexOrThrow(FoodType.COLUMN_NAME_ENTRY_NAME)));
			foodTypeList.add(entity);
		}
		c.close();
		app.setFoodTypeList(foodTypeList);
		
		
		c = db.query(Barcode.TABLE_NAME, null, null, null, null, null, null);		
		ArrayList<Barcode> barcodeList = new ArrayList<Barcode>();
		c.moveToPosition(-1);
		while (c.moveToNext()) {
			Barcode entity = new Barcode();
			entity.setBarId(c.getInt(c.getColumnIndexOrThrow(Barcode.COLUMN_NAME_ENTRY_ID)));
			entity.setBarCode(c.getString(c.getColumnIndexOrThrow(Barcode.COLUMN_NAME_ENTRY_CODE)));
			entity.setFoodId(c.getInt(c.getColumnIndexOrThrow(Barcode.COLUMN_NAME_ENTRY_FOOD_ID)));
			barcodeList.add(entity);
		}
		c.close();
		app.setBarcodeList(barcodeList);
		
		
		c = db.query(Athletic.TABLE_NAME, null, null, null, null, null, null);		
		ArrayList<Athletic> athleticList = new ArrayList<Athletic>();
		c.moveToPosition(-1);
		while (c.moveToNext()) {
			Athletic entity = new Athletic();
			entity.setAthId(c.getInt(c.getColumnIndexOrThrow(Athletic.COLUMN_NAME_ENTRY_ID)));
			entity.setAthName(c.getString(c.getColumnIndexOrThrow(Athletic.COLUMN_NAME_ENTRY_NAME)));
			entity.setAthBph(c.getInt(c.getColumnIndexOrThrow(Athletic.COLUMN_NAME_ENTRY_BPH)));
			athleticList.add(entity);
		}
		c.close();
		app.setAthleticList(athleticList);
		
	}
	
	public Food getFoodById(Integer Id) {
		for(Food f : app.getFoodList()){
			if(f.getFoodId().equals(Id)){
				return f;
			}
		}
		return null;
	}
	
	public Athletic getAthleticById(Integer Id) {
		for(Athletic f : app.getAthleticList()){
			if(f.getAthId().equals(Id)){
				return f;
			}
		}
		return null;
	}
	
	public Barcode getBarcodebyBarCode(String barcode) {
		for(Barcode b : app.getBarcodeList()){
			if(b.getBarCode().equals(barcode)){
				return b;
			}
		}
		return null;
	}
	
	public void consumeCommit(Consume consume) {		
		int total = consume_preference.getInt(TOTAL);
		ArrayList<String> marray = new ArrayList<String>();		
		marray.add(total+"");
		marray.add(consume.getFoodId().toString());
		marray.add(SlimDugong.dateFormat.format(consume.getConsumeTime()));
		consume_preference.putList(total+"", marray);
		consume_preference.putInt(TOTAL, total+1);
		
	}
	
	public ArrayList<Consume> getAllConsume(){		
		ArrayList<Consume> res = new ArrayList<Consume>();
		int total = consume_preference.getInt(TOTAL);
		try {
			for(int i=0;i<total;i++){
				Consume consume = new Consume();
				ArrayList<String> marray = consume_preference.getList(i+"");
				consume.setConsumeId(Integer.valueOf(marray.get(0)));
				consume.setFoodId(Integer.valueOf(marray.get(1)));
				consume.setConsumeTime(SlimDugong.dateFormat.parse(marray.get(2)));				
				res.add(consume);
			}
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
		return res;
	}
	
	public void exerciseCommit(Exercise exer) {		
		int total = exercise_preference.getInt(TOTAL);
		ArrayList<String> marray = new ArrayList<String>();		
		marray.add(total+"");
		marray.add(exer.getAthId().toString());
		marray.add(exer.getEnegyBurn().toString());
		marray.add(exer.getExerDuration().toString());
		marray.add(SlimDugong.dateFormat.format(exer.getExerTime()));
		exercise_preference.putList(total+"", marray);
		exercise_preference.putInt(TOTAL, total+1);				
	}
	
	public ArrayList<Exercise> getAllExercise(){		
		ArrayList<Exercise> res = new ArrayList<Exercise>();
		int total = exercise_preference.getInt(TOTAL);
		try {
			for(int i=0;i<total;i++){
				Exercise exer = new Exercise();
				ArrayList<String> marray = exercise_preference.getList(i+"");
				exer.setExerId(Integer.valueOf(marray.get(0)));
				exer.setAthId(Integer.valueOf(marray.get(1)));
				exer.setEnegyBurn(Integer.valueOf(marray.get(2)));
				exer.setExerDuration(Integer.valueOf(marray.get(3)));
				exer.setExerTime(SlimDugong.dateFormat.parse(marray.get(4)));				
				res.add(exer);				
			}
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
		return res;
	}
	
	private class UpdateTaskRunner extends AsyncTask<String, String, String>{
		 
		 private String title;
		 private String message;
		 private int totalSize;
		 private int downloadedSize;
		 private ProgressDialog dialog;
		 
		 private InputStream getInputStream(String str_url) throws IOException{
			 URL url = new URL(str_url);
			 HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
			 
			 //set up some things on the connection
			 urlConnection.setRequestMethod("GET");
			 urlConnection.setDoOutput(true);
			 urlConnection.connect();
			 totalSize = urlConnection.getContentLength();
			 downloadedSize = 0;
			 return urlConnection.getInputStream();
		 }
		 
		 @Override
		 protected String doInBackground(String... params) {
			 String res = null;
			 try {
				 dialog.setMessage(app.getText(R.string.update_on_checking));;
				 InputStream inputStream = getInputStream(URL_UPDATE);
				 BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
				 long version = Long.valueOf(bufferedReader.readLine());
				 if(version > version_preference.getLong(KEY_VERSION)){
					 dialog.dismiss();
					 
					 activity.runOnUiThread(new Runnable() {
						 public void run() {
							  dialog = new ProgressDialog(activity);
							  dialog.setTitle(R.string.update_action);
							  dialog.setIcon(R.drawable.ic_action_update);
							  dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
							  dialog.setMessage(app.getText(R.string.update_on_downloading));
							  dialog.setProgress(0);
							  dialog.setMax(totalSize);
							  dialog.show();
						 }
					 });
					 
					 String url_download = bufferedReader.readLine();
					 bufferedReader.close();
					 inputStream.close();					 
					 inputStream = getInputStream(url_download);
					 bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

					 
					 FileOutputStream fileOutput = app.openFileOutput(SQLiteDatabaseHelper.DATABASE_LASTEST, Context.MODE_PRIVATE);
					 byte[] buffer = new byte[1024];
						
					 int bufferLength = 0; //used to store a temporary size of the buffer
						
					 //now, read through the input buffer and write the contents to the file
					 while ( (bufferLength = inputStream.read(buffer)) > 0 ){
						 fileOutput.write(buffer, 0, bufferLength);
						 downloadedSize += bufferLength;
						 dialog.setProgress(downloadedSize);
					 }
					 bufferedReader.close();
					 inputStream.close();
					 fileOutput.close();
					 version_preference.putLong(KEY_VERSION, version);
					 title = (String) app.getText(R.string.update_success) + " (" + version + ")";
				 }else{
					 title = (String) app.getText(R.string.update_already_latest) + " (" + version_preference.getLong(KEY_VERSION) + ")";
				 }
			 } catch (MalformedURLException e) {
				 title = (String) app.getText(R.string.defualt_title_error);
				 message = e.getMessage();
				 res = "";
			 } catch (IOException e) {
				 title = (String) app.getText(R.string.defualt_title_error);
				 message = e.getMessage();
				 res = "";
			 } catch (NumberFormatException e) {
				 title = (String) app.getText(R.string.defualt_title_error);
				 message = e.getMessage();
				 res = "";
			 }
			 
			 return res;
		 }
		 
		  @Override
		  protected void onPostExecute(String result) {
			  dialog.dismiss();
			  int icon = result==null?R.drawable.ic_action_success:R.drawable.ic_action_alert;
			  new AlertDialog.Builder(activity)
			  .setTitle(title)
			  .setIcon(icon)
			  .setMessage(message)
			  .setPositiveButton(R.string.defualt_ok, null)
			  .show();
		 }
		  
		  @Override
		  protected void onPreExecute() {
			  dialog = new ProgressDialog(activity);
			  dialog.setTitle(R.string.update_action);
			  dialog.setIcon(R.drawable.ic_action_update);
			  dialog.show();
		 }
		  
		  @Override
		  protected void onProgressUpdate(String... text) {
			  dialog.setMessage(text[0]);
		  }
	}
	
}

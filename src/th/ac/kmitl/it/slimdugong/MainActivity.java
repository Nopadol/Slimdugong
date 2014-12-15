package th.ac.kmitl.it.slimdugong;


import th.ac.kmitl.it.slimdugong.custom.view.CharacterView;
import th.ac.kmitl.it.slimdugong.database.DatabaseManager;
import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;


public class MainActivity extends ActionBarActivity {
	
	private DatabaseManager mDatabaseManager;	
	

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadDatabase();
    }
    
    private void loadDatabase(){
        mDatabaseManager = new DatabaseManager((SlimDugong)getApplication());
        ((SlimDugong)getApplication()).setDatabase(mDatabaseManager);
        if(mDatabaseManager.isNoUser()){
        	Intent intent = new Intent(MainActivity.this, CreateCharacterActivity.class);
        	startActivity(intent);
        	finish();
        }else{
        	showCharacter();
        }
	}
    
    private void showStatus(){
    	ImageButton status_workout = (ImageButton) findViewById(R.id.status_workout);
    	ImageButton status_eat = (ImageButton) findViewById(R.id.status_eat);
    }
    
    private void showCharacter(){
    	CharacterView character_view = (CharacterView) findViewById(R.id.character_view);
    	character_view.setCharacter(mDatabaseManager.getUserCharacter());
    	character_view.invalidate();
    	character_view.clearMemoryAll();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Intent intent;
        if (id == R.id.action_select_dish) {
        	mDatabaseManager.loadDatabase();
        	intent = new Intent(this, SelectDishActivity.class);
        	startActivity(intent);
        }else if(id == R.id.action_exercise){
        	mDatabaseManager.loadDatabase();
        	intent = new Intent(this, ExerciseActivity.class);
        	startActivity(intent);
        }else if(id == R.id.action_update){
            mDatabaseManager.doUpdate(MainActivity.this);
        }
        return super.onOptionsItemSelected(item);
    }
}

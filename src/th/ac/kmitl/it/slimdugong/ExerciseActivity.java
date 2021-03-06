package th.ac.kmitl.it.slimdugong;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import th.ac.kmitl.it.slimdugong.custom.widget.AthleticAdapter;
import th.ac.kmitl.it.slimdugong.database.DatabaseManager;
import th.ac.kmitl.it.slimdugong.database.entity.Athletic;
import th.ac.kmitl.it.slimdugong.database.entity.local.Exercise;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class ExerciseActivity extends ActionBarActivity {
	
	private DatabaseManager mDatabaseManager;
	private SlimDugong app;
	
	private static final Integer DEFUALT_TIME_EXERCISE = 60;
	private static final Double PROCESS_MODIFICATION = 1.8;
	private static final Integer TO_HOUR = 60;	
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);
        app = SlimDugong.getInstance();
        
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        
        mDatabaseManager = app.getDatabase();
        
        Tab recent_tab = actionBar.newTab()
    			.setTag(0)
                .setText(R.string.exercise_recent_tab)
                .setTabListener(new mTab());
    	actionBar.addTab(recent_tab);
    	
    	Tab tab = actionBar.newTab()
    			.setTag(1)
                .setText(R.string.exercise_tab)
                .setTabListener(new mTab());
    	actionBar.addTab(tab);

    }
	
	public ArrayList<Athletic> getRecentAthleticList() {
		ArrayList<Athletic> res = new ArrayList<Athletic>();
    	final Map<Athletic, Integer> list = new HashMap<Athletic, Integer>();
    	for(Exercise c : mDatabaseManager.getAllExercise()){
			Athletic id = mDatabaseManager.getAthleticById(c.getAthId());
			if(list.containsKey(id)){
				list.put(id, list.get(id)+1);
			}else{
				list.put(id, 1);
			}				
		}					
		res.addAll(list.keySet());			
		Collections.sort(res, new Comparator<Athletic>() {
	        @Override
	        public int compare(Athletic  food1, Athletic  food2){		        	
	            return list.get(food2)-list.get(food1);
	        }
	    });
    	return res;
	}
	
	class mTab implements TabListener{

		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			// TODO Auto-generated method stub
			
			ListView listExercise = (ListView) findViewById(R.id.listExercise);
			
			ArrayList<Athletic> toShowList = new ArrayList<Athletic>();
			
			if(tab.getTag().equals(0)){
				toShowList = getRecentAthleticList();
			}else{
				toShowList = app.getAthleticList();
			}			
			
			ArrayAdapter<Athletic> modeAdapter = new AthleticAdapter(ExerciseActivity.this, toShowList);
			listExercise.setAdapter(modeAdapter);
			listExercise.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View v,
						int position, long id) {
					// TODO Auto-generated method stub
					
					Athletic ath = (Athletic) parent.getItemAtPosition(position);
					new ExerciseCustomDialogFragment(ath).show(getSupportFragmentManager(), null);
					
				}
			});
			
		}

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			// TODO Auto-generated method stub
			
		}   	
    }
	
	public class ExerciseCustomDialogFragment extends DialogFragment {
    	
		Athletic ath;
    	
		ExerciseCustomDialogFragment(Athletic ath){
    		super();
    		this.ath = ath;
    	}
    	
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
        	LayoutInflater inflator = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        	View view = inflator.inflate(R.layout.dialog_exercise, null);
        	SeekBar sb = (SeekBar)view.findViewById(R.id.dialog_exercise_seekbar);        	
        	final TextView tv = (TextView)view.findViewById(R.id.dialog_exercise_text);
        	tv.setTag(DEFUALT_TIME_EXERCISE);        	
        	tv.setText(DEFUALT_TIME_EXERCISE/TO_HOUR + " " + getText(R.string.default_hours));
        	sb.setProgress((int) (DEFUALT_TIME_EXERCISE/PROCESS_MODIFICATION));
        	sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
				
				@Override
				public void onStopTrackingTouch(SeekBar arg0) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onStartTrackingTouch(SeekBar arg0) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
					int time = (int) (arg1*PROCESS_MODIFICATION);
					int hour = time / TO_HOUR;
					int minute = time - hour * TO_HOUR;
					if(hour==0){
						tv.setText(minute + " " + getText(R.string.default_miniutes));
					}else if(minute!=0){
						tv.setText(hour + " " + getText(R.string.default_hours) + " " + minute + " " + getText(R.string.default_miniutes));
					}else{
						tv.setText(hour + " " + getText(R.string.default_hours));
					}
					tv.setTag(time);
				}
			});
            AlertDialog.Builder builder = new AlertDialog.Builder(ExerciseActivity.this);            
            builder.setTitle(ath.getAthName())
            	   .setView(view)
            	   .setIcon(R.drawable.ic_action_dish)
                   .setPositiveButton(R.string.defualt_ok, new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int id) {
                    	   Double time = ((Integer) tv.getTag()).doubleValue();
                    	   Double energy = ath.getAthBph()*(time/TO_HOUR);
                    	   
                    	   Exercise c = new Exercise();
                    	   c.setAthId(ath.getAthId());
                    	   c.setEnegyBurn(energy.intValue());
                    	   c.setExerTime(new Date());
                    	   c.setExerDuration(time.intValue());
                    	   c.setAthName(ath.getAthName());
                    	   mDatabaseManager.exerciseCommit(c);
                    	   
                    	   Toast.makeText(ExerciseActivity.this, ath.getAthName() + " "
                    			   + c.getExerDuration() + " " + getText(R.string.default_miniutes) + "\n"
                    			   + c.getEnegyBurn() + " " + getText(R.string.select_dish_calories), 
                    			   Toast.LENGTH_SHORT).show();
                    	   
                       }
                   })
                   .setNegativeButton(R.string.defualt_cancel, new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int id) {
                           // User cancelled the dialog
                       }
                   });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }

	

}

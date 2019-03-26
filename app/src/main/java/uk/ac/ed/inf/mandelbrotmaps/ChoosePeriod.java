package uk.ac.ed.inf.mandelbrotmaps;


import android.app.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

public class ChoosePeriod extends Activity implements View.OnClickListener
{
int currentValue = FractalActivity.period;
int selectedValue = currentValue;
boolean changed = false;
Button applyButton;


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{

		super.onCreate(savedInstanceState);
		setContentView(R.layout.chooseperiod);
		NumberPicker np = (NumberPicker)findViewById(R.id.number_picker);
		np.setMinValue(1);// restricted number to minimum value i.e 1
		np.setMaxValue(5);// restricked number to maximum value i.e. 31
		np.setValue(currentValue);
		np.setWrapSelectorWheel(true);
		applyButton = (Button)findViewById(R.id.choosePeriod_apply_button);
		applyButton.setOnClickListener(this);

		np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener()
		{

			@Override
			public void onValueChange(NumberPicker picker, int oldVal, int newVal)
			{
				selectedValue = newVal;
			}
		});

	}


	public void onClick(View view) {
		int button = view.getId();

		if(button == R.id.choosePeriod_apply_button) {
			FractalActivity.period = selectedValue;
			changed = !(selectedValue == currentValue);
			finish();
		}
	}
	public void finish() {
		Intent result = new Intent();
		result.putExtra(FractalActivity.DETAIL_CHANGED_KEY, changed);

		setResult(Activity.RESULT_OK, result);

		super.finish();
	}


}
/* NumberPickerActivity */

/*
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import 	android.widget.NumberPicker;
import android.widget.TextView;

public class PeriodControl extends Activity implements OnClickListener {
	
	private final String TAG = "MMaps";
	
	Button applyButton;
	Button cancelButton;
	
	SeekBar mandelbrotBar;

	TextView mandelbrotText;

	boolean changed = false;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.periodcontrol);
		
		// Get references to UI elements
		applyButton = (Button)findViewById(R.id.period_apply_button);
		applyButton.setOnClickListener(this);
		cancelButton = (Button) findViewById(R.id.period_cancel_button);
		cancelButton.setOnClickListener(this);
		
		// Assign TextViews before their values are set when the SeekBars change
		mandelbrotText = (TextView)findViewById(R.id.mandelbrotText);		

		// Get references to SeekBars, set their value from the prefs
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		
		mandelbrotBar = (SeekBar) findViewById(R.id.mandelbrot_seekbar);
		mandelbrotBar.setProgress((int)prefs.getFloat(FractalActivity.mandelbrotDetailKey, (float) AbstractFractalView.DEFAULT_DETAIL_LEVEL));
}
	

	public void onClick(View view) {
		int button = view.getId();		
		
		if(button == R.id.detail_cancel_button) {
			finish();
		}
		else if(button == R.id.detail_apply_button) {
			//Set shared prefs and return value (to indicate if shared prefs have changed)
			SharedPreferences.Editor prefsEditor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
			prefsEditor.putInt(FractalActivity.periodKey, ;

			prefsEditor.commit();
			
			changed = true;
			
			finish();
		}
	}
	
	@Override
	public void finish() {
		Intent result = new Intent();
		result.putExtra(FractalActivity.period_CHANGED_KEY, changed);
	   
		setResult(Activity.RESULT_OK, result);
		
		super.finish();
	}


	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}

	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}

}
 */
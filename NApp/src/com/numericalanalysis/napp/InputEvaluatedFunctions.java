package com.numericalanalysis.napp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import android.app.Activity;
import android.app.ActionBar.LayoutParams;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;


public class InputEvaluatedFunctions extends Activity {

	private RelativeLayout input_eval_functions_choice_view;
	private EditText pointsNumber;
	private RelativeLayout input_eval_functions_view;
	private TableLayout xnTable;
	private TableLayout fXnTable;
	private boolean isAppOnTableChoiceView = true;
	private int numberOfPoints;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_input_evaluated_functions);
		initViewElements();
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	public void initViewElements(){
		input_eval_functions_choice_view = (RelativeLayout) 
										   findViewById(R.id.input_eval_functions_choice_view);
		pointsNumber = (EditText) findViewById(R.id.input_eval_functions_pointsNumber);
		input_eval_functions_view = (RelativeLayout) findViewById(R.id.input_eval_functions_view);
		xnTable = (TableLayout) findViewById(R.id.input_eval_functions_Xn_table);
		fXnTable = (TableLayout) findViewById(R.id.input_eval_functions_fXn_table);
		input_eval_functions_view.setVisibility(View.GONE);
		isAppOnTableChoiceView = true;
		invalidateOptionsMenu();
		input_eval_functions_choice_view.setVisibility(View.VISIBLE);
	}
	
	public void createTable(View v){
		if(!pointsNumber.getText().toString().isEmpty()){
			if(Double.parseDouble(pointsNumber.getText().toString()) > 1){
				numberOfPoints = Integer.parseInt(pointsNumber.getText().toString());
				input_eval_functions_choice_view.setVisibility(View.GONE);
				isAppOnTableChoiceView = false;
				invalidateOptionsMenu();
				input_eval_functions_view.setVisibility(View.VISIBLE);
				setInputEvalFunctionsView();
			}
			else{
				Toast.makeText(getApplicationContext(), getResources()
					           .getString(R.string.enter_valid_points_number), Toast.LENGTH_SHORT).show();
			}
		}else{
			Toast.makeText(getApplicationContext(), getResources()
					       .getString(R.string.enter_valid_points_number), Toast.LENGTH_SHORT).show();
		}
	}
	
	public void setInputEvalFunctionsView(){
		xnTable.removeAllViews();
		fXnTable.removeAllViews();
		
		//Para las tablas de Xn y fXn.
	    for (int i = 0; i < numberOfPoints; i++) {
	    	TableRow rowXn = new TableRow(this);
	    	TableRow rowFXn = new TableRow(this);
            EditText cellXn = new EditText(this);
            EditText cellFXn = new EditText(this);
            cellXn.setHint("X" + i);
            cellFXn.setHint("f(X" + i + ")");
            cellXn.setTextSize(13.5f);
            cellFXn.setTextSize(13.5f);
            cellXn.setWidth(120);
            cellFXn.setWidth(120);
            cellXn.setGravity(Gravity.CENTER);
            cellFXn.setGravity(Gravity.CENTER);
            cellXn.setSingleLine(true);
            cellFXn.setSingleLine(true);
            rowXn.addView(cellXn);
            rowFXn.addView(cellFXn);
            xnTable.addView(rowXn ,new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
					  										    LayoutParams.WRAP_CONTENT));
            fXnTable.addView(rowFXn ,new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
					  											  LayoutParams.WRAP_CONTENT));
	    }
	}

	public void captureInputEvalFunctions(View v){
		ArrayList<Double> xNValues = new ArrayList<Double>();
		ArrayList<Double> fXnValues = new ArrayList<Double>();
		boolean isInfoValid = true;
		
		//Para las tablas de Xn y fXn.
		for(int j = 0; j < numberOfPoints && isInfoValid; j++){
			TableRow rowXn = (TableRow) xnTable.getChildAt(j);
			TableRow rowFXn = (TableRow) fXnTable.getChildAt(j);
		    EditText positionXn = (EditText) rowXn.getChildAt(0);
		    EditText positionFXn = (EditText) rowFXn.getChildAt(0);
		    String positionContentXn = positionXn.getText().toString();
		    String positionContentFXn = positionFXn.getText().toString();
		    if(!positionContentXn.isEmpty() && !positionContentFXn.isEmpty()){
		    	if(isValuesFormatOK(positionContentXn) && isValuesFormatOK(positionContentFXn)){ 
		    										   //La info. diligenciada está correcta y puede
		    										  //ser almacenada.
		    		xNValues.add(Double.parseDouble(positionContentXn));
		    		fXnValues.add(Double.parseDouble(positionContentFXn));
		    	}else{
		    		Toast.makeText(getApplicationContext(), getResources()
					           	   .getString(R.string.entered_values_format_not_valid), 
					               Toast.LENGTH_SHORT).show();
		    		isInfoValid = false;
		    	}
		    }else{
		    	Toast.makeText(getApplicationContext(), getResources()
		    			       .getString(R.string.fill_in_all_fields), 
				               Toast.LENGTH_SHORT).show();
		    	isInfoValid = false;
		    }
		}
		
		Set tempSet = new HashSet();
        for (Double xN : xNValues) {
            if (!tempSet.add(xN)) {
            	Toast.makeText(getApplicationContext(), getResources()
	    			       .getString(R.string.each_f_x_to_single_x), 
			               Toast.LENGTH_SHORT).show();
                isInfoValid = false;
            }
        }
		
		if(isInfoValid){  //Todos los datos fueron guardados exitósamente.
			new InterpolationTables(xNValues, fXnValues);  //Creación de las tablas respectivas.
			Intent openInterpolation = new Intent(InputEvaluatedFunctions.this, Interpolation.class);
			startActivity(openInterpolation);
		}
	}
	
	public boolean isValuesFormatOK(String positionContent){
		int valuesPointCount = 0;
		for(int i = 0; i < positionContent.length(); i++){
			if(positionContent.charAt(i) != '-' && positionContent.charAt(i) != '.' && 
			   !Character.isDigit(positionContent.charAt(i))){
				return false;
			}else{
				if(positionContent.charAt(i) == '-'){
					if(i != 0){
						return false;
					}
				}else if(positionContent.charAt(i) == '.'){
					 		valuesPointCount++;
					 		if(valuesPointCount > 1 || i == 0 ){
					 			return false;
					 		}
				 }
			}
		}
		return true;
	}
	
	public void returnToTableChoice(View v){
		input_eval_functions_view.setVisibility(View.GONE);
		isAppOnTableChoiceView = true;
		invalidateOptionsMenu();
		input_eval_functions_choice_view.setVisibility(View.VISIBLE);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
		Intent openSelectedItem = new Intent(InputEvaluatedFunctions.this, GuideMenu.class);
		Bundle methodToHelp = new Bundle();
		methodToHelp.putInt("methodNameId", R.string.how_to_enter_eval_function);
	    switch (item.getItemId()) {
	    	case R.id.seeHowToEnterEvaluatedFunctions:
        		methodToHelp.putInt("actionId", R.string.how_to_enter_eval_function);
        		break;
	    	case R.id.seeExample:
        		methodToHelp.putInt("actionId", R.string.see_example);
        		break;
	        case android.R.id.home:
	        	if(isAppOnTableChoiceView){
	        		finish();
	        	}else{
	        		returnToTableChoice(null); //No hace falta enviar ning�n par�metro.
	        	}
        		return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	    openSelectedItem.putExtras(methodToHelp);
	    startActivity(openSelectedItem);
        return true;
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		super.onCreateOptionsMenu(menu);
		
		if(ViewConfiguration.get(getApplicationContext()).hasPermanentMenuKey()){
			//Para el men� f�sico.
			if (!isAppOnTableChoiceView){
				menu.add(0, R.id.seeHowToEnterEvaluatedFunctions, Menu.FIRST+1, getResources()
						 .getString(R.string.how_to_enter_eval_function));
		    	menu.add(0, R.id.seeExample, Menu.FIRST+2, getResources()
		    		     .getString(R.string.see_example));
			}
		}

    	//Para la ActionBar.
    	getMenuInflater().inflate(R.menu.input_evaluated_functions, menu);
		if (!isAppOnTableChoiceView){
			menu.findItem(R.id.seeHowToEnterEvaluatedFunctions).setVisible(true);
			menu.findItem(R.id.seeExample).setVisible(true);
		}else{
			menu.findItem(R.id.seeHowToEnterEvaluatedFunctions).setVisible(false);
			menu.findItem(R.id.seeExample).setVisible(false);
		}
		
		return true;
	}

}

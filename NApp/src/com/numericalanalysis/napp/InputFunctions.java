package com.numericalanalysis.napp;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.EditText;
import android.widget.Toast;

public class InputFunctions extends Activity {
	
	private EditText fX;
	private String fXString;
	private EditText dfX;
	private String dfXString;
	private EditText d2fX;
	private String d2fXString;
	private EditText gX;
	private String gXString;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_input_functions);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		initViewElements();
	}
	
	public void initViewElements(){
		fX = (EditText) findViewById(R.id.inputFunction_fX);
		dfX = (EditText) findViewById(R.id.inputFunction_dfX);
		d2fX = (EditText) findViewById(R.id.inputFunction_d2fX);
		gX = (EditText) findViewById(R.id.inputFunction_gX);
	}
	
	public void captureInputFunctions(View v){
		fXString = fX.getText().toString();
		dfXString = dfX.getText().toString();
		d2fXString = d2fX.getText().toString();
		gXString = gX.getText().toString();
		if(fXString.equals("")){
			Toast.makeText(getApplicationContext(), getResources().getString(R.string.f_x_empty), 
			          	   Toast.LENGTH_SHORT).show();
		}else{
			FunctionsEvaluator fEvaluator = new FunctionsEvaluator(fXString, dfXString, d2fXString, gXString);
			//Si se ha ingresado por lo menos f(x) se muestra la lista de m�todos.
			boolean untilThisFunctionOK = true;
			for(int i = 0; i < fEvaluator.getBuiltFunctionsStatus().length && untilThisFunctionOK; i++){
				if (!fEvaluator.getInputFunctions()[i].isEmpty() && !fEvaluator.getBuiltFunctionsStatus()[i]){
					untilThisFunctionOK = false;
					if(i == 0){
						Toast.makeText(getApplicationContext(),getResources().getString(R.string.error_in_fx),Toast.LENGTH_SHORT).show();
					}else if(i == 1){
						Toast.makeText(getApplicationContext(),getResources().getString(R.string.error_in_dfx),Toast.LENGTH_SHORT).show();
					}else if(i == 2){
						Toast.makeText(getApplicationContext(),getResources().getString(R.string.error_in_d2fx),Toast.LENGTH_SHORT).show();
					}else{ 
						Toast.makeText(getApplicationContext(),getResources().getString(R.string.error_in_gx),Toast.LENGTH_SHORT).show();
					}
				}
			}
			if(untilThisFunctionOK){
				Intent openOneVarEqs = new Intent(InputFunctions.this, OneVariableEquations.class);
				openOneVarEqs.putExtra("fEvaluator", fEvaluator);
				startActivity(openOneVarEqs);
			}
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
		Intent openSelectedItem = new Intent(InputFunctions.this, GuideMenu.class);
		Bundle methodToHelp = new Bundle();
		methodToHelp.putInt("methodNameId", R.string.how_to_enter_function);
	    switch (item.getItemId()) {
	    	case R.id.seeHowToEnterFunctions:
        		methodToHelp.putInt("actionId", R.string.how_to_enter_function);
        		break;
	        case android.R.id.home:
	        	finish();
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
			menu.add(0, R.id.seeHowToEnterFunctions, Menu.FIRST, getResources()
					 .getString(R.string.how_to_enter_function));
		}

    	//Para la ActionBar.
		getMenuInflater().inflate(R.menu.input_functions, menu);
		return true;
	}

}

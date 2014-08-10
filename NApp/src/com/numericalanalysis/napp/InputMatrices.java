package com.numericalanalysis.napp;

import java.util.ArrayList;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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


public class InputMatrices extends Activity {

	private RelativeLayout inputMatricesChoiceView;
	private EditText unknownsNumber;
	private RelativeLayout inputMatricesView;
	private TableLayout matrixA;
	private TableLayout vectorB;
	private boolean isAppOnMatrixChoiceView = true;
	private int numberOfUnknowns;
	
	//Para validar valores de la matriz.
	private int valuesPointCount;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_input_matrices);
		initViewElements();
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	public void initViewElements(){
		inputMatricesChoiceView = (RelativeLayout) findViewById(R.id.input_matrices_choice_view);
		unknownsNumber = (EditText) findViewById(R.id.inputMatrices_unknownsNumber);
		inputMatricesView = (RelativeLayout) findViewById(R.id.input_matrices_view);
		matrixA = (TableLayout) findViewById(R.id.inputMatrices_inputMatA);
		vectorB = (TableLayout) findViewById(R.id.inputMatrices_inputVecB);
		inputMatricesView.setVisibility(View.GONE);
		isAppOnMatrixChoiceView = true;
		invalidateOptionsMenu();
		inputMatricesChoiceView.setVisibility(View.VISIBLE);
	}
	
	public void createMatrix(View v){
		if(!unknownsNumber.getText().toString().isEmpty()){
			if(Double.parseDouble(unknownsNumber.getText().toString()) >= 2){
				numberOfUnknowns = Integer.parseInt(unknownsNumber.getText().toString());
				inputMatricesChoiceView.setVisibility(View.GONE);
				isAppOnMatrixChoiceView = false;
				invalidateOptionsMenu();
				inputMatricesView.setVisibility(View.VISIBLE);
				setInputMatrixView();
			}
			else{
				Toast.makeText(getApplicationContext(), getResources()
					           .getString(R.string.enter_valid_unk_number), Toast.LENGTH_SHORT).show();
			}
		}else{
			Toast.makeText(getApplicationContext(), getResources()
					       .getString(R.string.enter_valid_unk_number), Toast.LENGTH_SHORT).show();
		}
	}
	
	public void setInputMatrixView(){
		matrixA.removeAllViews();
		vectorB.removeAllViews();
		
		//Para la matriz A.
	    for (int i = 0; i < numberOfUnknowns; i++) {
	        TableRow row = new TableRow(this);
	        for (int j = 0; j < numberOfUnknowns; j++) {
	            EditText cell = new EditText(this);
	            cell.setHint("A[" + i + "][" + j + "]");
	            cell.setTextSize(13.5f);
	            cell.setGravity(Gravity.CENTER);
	            cell.setSingleLine(true);
	            if(j > 0){
	            	row.getChildAt(j-1).setNextFocusDownId(cell.getId());
	            	row.getChildAt(j-1).setNextFocusForwardId(cell.getId());
	            	row.addView(cell);
	            }else{
	            	row.addView(cell);
	            }
	        }
	        matrixA.addView(row, new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
	                                                          LayoutParams.WRAP_CONTENT));
	    }
	    
	    //Para el vector b.
	    TableRow row = new TableRow(this);
	    for (int i = 0; i < numberOfUnknowns; i++) {
            EditText cell = new EditText(this);
            cell.setHint("b[" + i + "][0]");
            cell.setTextSize(13.5f);
            cell.setGravity(Gravity.CENTER);
            cell.setSingleLine(true);
            if(i > 0){
            	row.getChildAt(i-1).setNextFocusDownId(cell.getId());
            	row.getChildAt(i-1).setNextFocusForwardId(cell.getId());
            	row.addView(cell);
            }else{
            	row.addView(cell);
            }
	    }
	    vectorB.addView(row ,new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				  										  LayoutParams.WRAP_CONTENT));
	}

	public void captureInputMatrices(View v){
		ArrayList<ArrayList<Double>> matrixAValues = new ArrayList<ArrayList<Double>>();
		ArrayList<Double> vectorBValues = new ArrayList<Double>();
		boolean isInfoValid = true;
		
		//Para la matriz A.
		for(int i = 0; i < numberOfUnknowns && isInfoValid; i++){
			ArrayList<Double> matrixARowValues = new ArrayList<Double>();  //Para almacenar cada fila.
			TableRow row = (TableRow) matrixA.getChildAt(i);
			for(int j = 0; j < numberOfUnknowns && isInfoValid; j++){
			    EditText position = (EditText) row.getChildAt(j);
			    String positionContent = position.getText().toString();
			    if(!positionContent.isEmpty()){
			    	if(isValuesFormatOK(positionContent)){ //La info. diligenciada está correcta y puede
			    										  //ser almacenada.
			    		matrixARowValues.add(Double.parseDouble(positionContent));
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
			if(isInfoValid){  //En las posiciones de esa ArrayList se almacena cada fila de la matriz A.
				matrixAValues.add(matrixARowValues);
			}
		}
		
		//Para el vector b.
		TableRow row = (TableRow) vectorB.getChildAt(0);
		for(int j = 0; j < numberOfUnknowns && isInfoValid; j++){
		    EditText position = (EditText) row.getChildAt(j);
		    String positionContent = position.getText().toString();
		    if(!positionContent.isEmpty()){
		    	if(isValuesFormatOK(positionContent)){ //La info. diligenciada está correcta y puede
		    										  //ser almacenada.
		    		vectorBValues.add(Double.parseDouble(positionContent));
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
	
		if(isInfoValid){  //Todos los datos fueron guardados exitósamente.
			for(ArrayList<Double> matrixARow : matrixAValues){
				for(Double value: matrixARow){
					Log.i("matrix", String.valueOf(value));
				}
				Log.i("matrix", "Pasó de fila");
			}
			for(Double value: vectorBValues){
				Log.i("matrix", String.valueOf(value));
				Log.i("matrix", "Pasó de fila");
			}
		    
			new Matrix(matrixAValues, vectorBValues);  //Creación de las matrices respectivas.
			Intent openSystemsOfEqs = new Intent(InputMatrices.this, SystemsOfEquations.class);
			//openSystemsOfEqs.putExtra("matrix", matrix);
			startActivity(openSystemsOfEqs);
		}
	}
	
	public boolean isValuesFormatOK(String positionContent){
		valuesPointCount = 0;
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
	
	public void returnToMatrixChoice(View v){
		inputMatricesView.setVisibility(View.GONE);
		isAppOnMatrixChoiceView = true;
		invalidateOptionsMenu();
		inputMatricesChoiceView.setVisibility(View.VISIBLE);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
		Intent openSelectedItem = new Intent(InputMatrices.this, GuideMenu.class);
		Bundle methodToHelp = new Bundle();
		methodToHelp.putInt("methodNameId", R.string.how_to_enter_matrix);
	    switch (item.getItemId()) {
	    	case R.id.seeHowToEnterMatrices:
        		methodToHelp.putInt("actionId", R.string.how_to_enter_matrix);
        		break;
	    	case R.id.seeExample:
        		methodToHelp.putInt("actionId", R.string.see_example);
        		break;
	        case android.R.id.home:
	        	if(isAppOnMatrixChoiceView){
	        		finish();
	        	}else{
	        		returnToMatrixChoice(null); //No hace falta enviar ning�n par�metro.
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
			if (!isAppOnMatrixChoiceView){
				menu.add(0, R.id.seeHowToEnterMatrices, Menu.FIRST+1, getResources()
						 .getString(R.string.how_to_enter_matrix));
		    	menu.add(0, R.id.seeExample, Menu.FIRST+2, getResources()
		    		     .getString(R.string.see_example));
			}
		}

    	//Para la ActionBar
    	getMenuInflater().inflate(R.menu.input_matrices, menu);
		if (!isAppOnMatrixChoiceView){
			menu.findItem(R.id.seeHowToEnterMatrices).setVisible(true);
			menu.findItem(R.id.seeExample).setVisible(true);
		}else{
			menu.findItem(R.id.seeHowToEnterMatrices).setVisible(false);
			menu.findItem(R.id.seeExample).setVisible(false);
		}
		
		return true;
	}
}

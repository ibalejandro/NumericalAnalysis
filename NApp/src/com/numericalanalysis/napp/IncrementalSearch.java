package com.numericalanalysis.napp;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class IncrementalSearch extends Activity {

	private FunctionsEvaluator fEvaluator;
	private RelativeLayout incrementalSearchView;
	private EditText etX0;
	private EditText etDelta;
	private EditText etNIterations;
	private RelativeLayout resultView;
	private TextView methodResult;
	private double x0;
	private double delta;
	private int nIterations;
	private ArrayList<String> resultsMap = new ArrayList<String>();
	private ArrayList<String> incrementalSearchArrayList = new ArrayList<String>();
	//Para validar valor X0.
	private int valuesPointCount;
	//
	private boolean isAppOnMethodView = true;  //Se hace para saber qu� opciones mostrar
											  //en el men� y cu�les esconder.
	//Para la tabla
	private ArrayList<Integer> nColumn = new ArrayList<Integer>();
	private ArrayList<String> nColumnStrings = new ArrayList<String>();
	private ArrayList<Double> xNColumn = new ArrayList<Double>();
	private ArrayList<String> xNColumnStrings = new ArrayList<String>();
	private ArrayList<Double> fXnColumn = new ArrayList<Double>();
	private ArrayList<String> fXnColumnStrings = new ArrayList<String>();
	//
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_incremental_search);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		fEvaluator = (FunctionsEvaluator) getIntent().getParcelableExtra("fEvaluator");
		initViewElements();
	}
	
	public void initViewElements(){
		incrementalSearchView = (RelativeLayout) findViewById(R.id.incremental_search_view);
		etX0 = (EditText) findViewById(R.id.incrementalSearch_etX0);
		etDelta = (EditText) findViewById(R.id.incrementalSearch_etDelta);
		etNIterations = (EditText) findViewById(R.id.incrementalSearch_etNIterations);
		resultView = (RelativeLayout) findViewById(R.id.incrementalSearch_result_view);
		methodResult = (TextView) findViewById(R.id.incrementalSearch_method_result);
		resultView.setVisibility(View.GONE);
		isAppOnMethodView = true;
		invalidateOptionsMenu();
		incrementalSearchView.setVisibility(View.VISIBLE);
	}
	
	public void incrementalSearch(View v){
		valuesPointCount = 0;
		if(!isAnyFieldEmpty()){
			if(isValuesFormatOK()){
				captureIncrementalSearchData();
				resultsMap = calculateWithIncrementalSearch();
				incrementalSearchView.setVisibility(View.GONE);
				isAppOnMethodView = false;
				invalidateOptionsMenu();
				resultView.setVisibility(View.VISIBLE);
				methodResult.setText(resultsMap.get(1));
			}else{
				Toast.makeText(getApplicationContext(), getResources()
				               .getString(R.string.entered_values_format_not_valid), 
				               Toast.LENGTH_SHORT).show();
			}
		}else{
			Toast.makeText(getApplicationContext(), getResources()
				           .getString(R.string.fill_in_all_fields), Toast.LENGTH_SHORT).show();
		}	
	}
	
	public boolean isValuesFormatOK(){
		for(int i = 0; i < etX0.getText().toString().length(); i++){
			if(etX0.getText().toString().charAt(i) != '-' && 
			   etX0.getText().toString().charAt(i) != '.' && 
			   !Character.isDigit(etX0.getText().toString().charAt(i))){
				return false;
			}else{
				if(etX0.getText().toString().charAt(i) == '-'){
					if(i != 0){
						return false;
					}
				 }else if(etX0.getText().toString().charAt(i) == '.'){
					 		valuesPointCount++;
					 		if(valuesPointCount > 1 || i == 0 ){
					 			return false;
					 		}
				 }
			}
		}
		return true;
	}
	
	public void captureIncrementalSearchData(){
		x0 = Double.parseDouble(etX0.getText().toString());
		delta = Double.parseDouble(etDelta.getText().toString());
		nIterations = Integer.parseInt(etNIterations.getText().toString());
	}
	
	public ArrayList<String> calculateWithIncrementalSearch(){
		incrementalSearchArrayList.clear();
		nColumn.clear();
		xNColumn.clear();
		fXnColumn.clear();
		nColumn.add(0);
		xNColumn.add(x0);
		double y0 = fEvaluator.f(x0);
		fXnColumn.add(y0);
		if(y0 == 0){ 
			 incrementalSearchArrayList.add("0");  //0 : el resultado fue una ra�z.
			 String x0IsRoot = x0 + " is a root.";
			 incrementalSearchArrayList.add(String.valueOf(x0IsRoot));
		     return incrementalSearchArrayList; 
		}else{
			double x1 = x0 + delta;
			xNColumn.add(x1);
			double y1 = fEvaluator.f(x1);
			fXnColumn.add(y1);
			int count = 1;
			nColumn.add(count);
			while(((y0*y1) > 0) && (count < nIterations)){
				x0 = x1;
				y0 = y1;
				x1 = x0 + delta;
				xNColumn.add(x1);
				y1 = fEvaluator.f(x1);
				fXnColumn.add(y1);
				count++;
				nColumn.add(count);
			}
		    if(y1 == 0){
		    	incrementalSearchArrayList.add("0");  //0 : el resultado fue una ra�z.
		    	String x1isRoot = x1 + " is a root.";
		    	incrementalSearchArrayList.add(String.valueOf(x1isRoot));
		    	return incrementalSearchArrayList;    
			}else if((y0 * y1) < 0){
			    	incrementalSearchArrayList.add("1");  //1 : el resultado fue un intervalo que 
			    										 //contiene la ra�z.
			    	String interval = "There is a root in the interval [" + x0 + ", " + x1 + "]."; 
				 	incrementalSearchArrayList.add(String.valueOf(interval));
				 	return incrementalSearchArrayList;  
			 	}else{
			 		incrementalSearchArrayList.add("2");  //2 : el resultado fue fracaso 
					 							 //en la aplicaci�n del m�todo.
		        	String failure = "Failure applying the Incremental Search method because the " +
		        					 "number of iterations were exceeded."; 
		            incrementalSearchArrayList.add(String.valueOf(failure));
		            return incrementalSearchArrayList;  
		    	  }
			}
	}
	
	public boolean isAnyFieldEmpty(){
		if(etX0.getText().toString().isEmpty() || etX0.getText().toString().equals("-")
		   || etDelta.getText().toString().isEmpty() || etNIterations.getText().toString()
		   .isEmpty()){
			return true;
		}else{
			return false;
		}
	}
	
	public void returnToMethod(View v){
		resultView.setVisibility(View.GONE);
		isAppOnMethodView = true;
		invalidateOptionsMenu();
		incrementalSearchView.setVisibility(View.VISIBLE);
	}
	
	public void convertColumnsToStrings(){
		nColumnStrings.clear();
		xNColumnStrings.clear();
		fXnColumnStrings.clear();
		for (Integer n : nColumn) {
			nColumnStrings.add(String.valueOf(n));
		}
		for (Double xN : xNColumn) {
			xNColumnStrings.add(String.valueOf(xN));
		}
		for (Double fXn : fXnColumn) {
			fXnColumnStrings.add(String.valueOf(fXn));
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
		Intent openSelectedItem = new Intent(IncrementalSearch.this, GuideMenu.class);
		Bundle methodToHelp = new Bundle();
		methodToHelp.putInt("methodNameId", R.string.incremental_search);
	    switch (item.getItemId()) {
	    	case R.id.resultsTable:
	    		convertColumnsToStrings();
	    		methodToHelp.putStringArrayList("nColumn", nColumnStrings);
	    		methodToHelp.putStringArrayList("xNColumn", xNColumnStrings);
	    		methodToHelp.putStringArrayList("fXnColumn", fXnColumnStrings);
        		methodToHelp.putInt("actionId", R.string.results_table);
        	break;
	        case R.id.help:
	        	methodToHelp.putInt("actionId", R.string.help);
	        	break;
	        case R.id.seeExample:
	        	methodToHelp.putInt("actionId", R.string.see_example);
	        	break;
	        case android.R.id.home:
	        	if(isAppOnMethodView){
	        		finish();
	        	}else{
	        		returnToMethod(null); //No hace falta enviar ning�n par�metro.
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
			if (isAppOnMethodView){
				menu.add(0, R.id.help, Menu.FIRST+1, getResources().getString(R.string.help));
		    	menu.add(0, R.id.seeExample, Menu.FIRST+2, getResources().
		    		     getString(R.string.see_example));
			}else{
				menu.add(0, R.id.resultsTable, Menu.FIRST, getResources().getString(R.string.results_table));
			}
		}

    	//Para la ActionBar
    	getMenuInflater().inflate(R.menu.incremental_search, menu);
		if (isAppOnMethodView){
			menu.findItem(R.id.resultsTable).setVisible(false);
			menu.findItem(R.id.help).setVisible(true);
			menu.findItem(R.id.seeExample).setVisible(true);
		}else{
			menu.findItem(R.id.resultsTable).setVisible(true);
			menu.findItem(R.id.help).setVisible(false);
			menu.findItem(R.id.seeExample).setVisible(false);
		}
		
		return true;
	}

}

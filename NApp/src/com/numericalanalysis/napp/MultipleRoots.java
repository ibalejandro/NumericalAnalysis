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
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MultipleRoots extends Activity {

	private FunctionsEvaluator fEvaluator;
	private RelativeLayout multipleRootsView;
	private EditText etX0;
	private EditText etTolerance;
	private RadioButton rbAbsError;
	private RadioButton rbRelError;
	private EditText etNIterations;
	private RelativeLayout resultView;
	private TextView methodResult;
	private double x0;
	private String toleranceString;
	private double tolerance;
	private int nIterations;
	//Para validar tolerancia.
	private int minusCount;
	private int pointCount;
	private int tenExponentCount;
	private int exponentCount;
	//
	//Para validar valor X0.
	private int valuesPointCount;
	//
	private ArrayList<String> resultsMap = new ArrayList<String>();
	private ArrayList<String> multipleRootsArrayList = new ArrayList<String>();
	private String errorType = "Absolute"; //Por defecto se va a calcular con error absoluto.
	private boolean isAppOnMethodView = true;  //Se hace para saber qu� opciones mostrar
	  										  //en el men� y cu�les esconder.
	
	//Para la tabla
	private ArrayList<Integer> nColumn = new ArrayList<Integer>();
	private ArrayList<String> nColumnStrings = new ArrayList<String>();
	private ArrayList<Double> xNColumn = new ArrayList<Double>();
	private ArrayList<String> xNColumnStrings = new ArrayList<String>();
	private ArrayList<Double> fXnColumn = new ArrayList<Double>();
	private ArrayList<String> fXnColumnStrings = new ArrayList<String>();
	private ArrayList<Double> dFXnColumn = new ArrayList<Double>();
	private ArrayList<String> dFXnColumnStrings = new ArrayList<String>();
	private ArrayList<Double> d2FXnColumn = new ArrayList<Double>();
	private ArrayList<String> d2FXnColumnStrings = new ArrayList<String>();
	private ArrayList<Double> errorColumn = new ArrayList<Double>();
	private ArrayList<String> errorColumnStrings = new ArrayList<String>();
	//
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_multiple_roots);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		fEvaluator = (FunctionsEvaluator) getIntent().getParcelableExtra("fEvaluator");
		initViewElements();
	}

	public void initViewElements(){
		multipleRootsView = (RelativeLayout) findViewById(R.id.multiple_roots_view);
		etX0 = (EditText) findViewById(R.id.multipleRoots_etX0);
		etTolerance = (EditText) findViewById(R.id.multipleRoots_etTolerance);
		rbAbsError = (RadioButton) findViewById(R.id.multipleRoots_absError);
		rbAbsError.setChecked(true); //Por defecto se har� el c�lculo con error absoluto.
		rbRelError = (RadioButton) findViewById(R.id.multipleRoots_relError);
		etNIterations = (EditText) findViewById(R.id.multipleRoots_etNIterations);
		resultView = (RelativeLayout) findViewById(R.id.multipleRoots_result_view);
		methodResult = (TextView) findViewById(R.id.multipleRoots_method_result);
		resultView.setVisibility(View.GONE);
		isAppOnMethodView = true;
		invalidateOptionsMenu();
		multipleRootsView.setVisibility(View.VISIBLE);
	}
	
	public void multipleRoots(View v){
		valuesPointCount = 0;
		minusCount = 0;
		pointCount = 0;
		tenExponentCount = 0;
		exponentCount = 0;
		if(!isAnyFieldEmpty()){
			if(isValuesFormatOK()){
				captureMultipleRootsData();
					if(isToleranceOK()){
						resultsMap = calculateWithMultipleRoots();
						if(resultsMap.get(0).equals("-2")){
							Toast.makeText(getApplicationContext(), getResources()
								       .getString(R.string.error_calc_error), 
								       Toast.LENGTH_SHORT).show();
						}else{
							multipleRootsView.setVisibility(View.GONE);
							isAppOnMethodView = false;
							invalidateOptionsMenu();
							resultView.setVisibility(View.VISIBLE);
							methodResult.setText(resultsMap.get(1));
						}
					}else{
						Toast.makeText(getApplicationContext(), getResources()
							          .getString(R.string.invalid_tolerance), 
							          Toast.LENGTH_SHORT).show();
					}
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
	
	public boolean isAnyFieldEmpty(){
		if(etX0.getText().toString().isEmpty() || etTolerance.getText().toString().isEmpty() 
		   || etNIterations.getText().toString().isEmpty()){
			return true;
			
		}else{
			return false;
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
	
	public void captureMultipleRootsData(){
		x0 = Double.parseDouble(etX0.getText().toString());
		toleranceString = etTolerance.getText().toString();
		nIterations = Integer.parseInt(etNIterations.getText().toString());
	}
	
	public boolean isToleranceOK(){
		boolean untilHereOk = true;
		String trimToleranceString = toleranceString.trim();
		for(int i = 0; i < trimToleranceString.length() && untilHereOk; i++){
			untilHereOk = isCharValid(trimToleranceString.charAt(i));
		}
		if(untilHereOk){ //Se sali� debido a que se termin� la cadena, pero la tolerancia parece 
						 //v�lida.
			if(isToleranceFormatValid(trimToleranceString)){
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
	}
	
	public boolean isCharValid(char i){
		if(i != '-' && i != '.' && i != 'E' && i != '^' && !Character.isDigit(i)){  
			//Se trata de un caracter inv�lido.
			return false;
		}else{
			if(i == '-'){
				minusCount++;
				if(minusCount > 1){
					return false;
				}
			}else if(i == '.'){
				pointCount++;
				if(pointCount > 1){
					return false;
				}
			}else if(i == 'E'){
				tenExponentCount++;
				if(tenExponentCount > 1 || exponentCount != 0){
					return false;
				}
			}else if(i == '^'){
				exponentCount++;
				if(exponentCount > 1 || tenExponentCount != 0){
					return false;
				}
			}
				
			return true;  //Se trata de un d�gito o de un n�mero 
						  //permitido de los dem�s caracteres.
		}
	}
	
	public boolean isToleranceFormatValid(String tol){
		String[] toleranceNumbers = new String[2];
		if(tenExponentCount != 0){ //La tolerancia se trabaja con E.
			toleranceNumbers = tol.split("E");	
		}else if(exponentCount != 0){  //La tolerancia se trabaja con ^.
			toleranceNumbers = tol.split("\\^");
		}else{
			return false;
		}
		
		if(toleranceNumbers.length != 2){
			return false;
		}else{
			if(toleranceNumbers[0].isEmpty() || toleranceNumbers[1].isEmpty()){
				return false;
			}else{ //Se sabe que en ambas posiciones hay contenido. Es decir, el caracter
				   //se encuentra en medio de dos cantidades.
				//Se valida el primer String, el de la posici�n 0.
				if(toleranceNumbers[0].contains("-")){ 
						return false;
				}
				if(toleranceNumbers[0].contains(".")){
					if(toleranceNumbers[0].charAt(1) != '.'){
						return false;
					}
				} //Si no se retorn� falso, se estar�a afirmando que el String en la posici�n 0
				 //es v�lido.
				
				if(toleranceNumbers[1].contains("-")){ 
					if(toleranceNumbers[1].charAt(0) != '-'){
						return false;
					}
				}
				if(toleranceNumbers[1].contains(".")){
					return false;
				} //Si no se retorn� falso, se estar�a afirmando que el String en la posici�n 1
				 //tambi�n es v�lido.
				convertToleranceToDouble(toleranceNumbers);
				return true;
			}
		}
	}
	
	public void selectErrorType(View v){
		switch(v.getId()) {
        case R.id.multipleRoots_absError:
        	rbRelError.setChecked(false);
        	errorType = "Absolute";
            break;
        case R.id.multipleRoots_relError:
        	rbAbsError.setChecked(false);
        	errorType = "Relative";
            break;
		} 
	}
	
	public void convertToleranceToDouble(String[] toleranceToConvert){
		double base, exponent;
		base = Double.parseDouble(toleranceToConvert[0]);
		exponent = Double.parseDouble(toleranceToConvert[1]);
		if(tenExponentCount != 0){
			tolerance = base * (Math.pow(10, exponent));
		}else{
			tolerance = Math.pow(base, exponent);
		}
	}
	
	public ArrayList<String> calculateWithMultipleRoots(){
		multipleRootsArrayList.clear();
		nColumn.clear();
		xNColumn.clear();
		fXnColumn.clear();
		dFXnColumn.clear();
		d2FXnColumn.clear();
		errorColumn.clear();
		xNColumn.add(x0);
		double fx0 = fEvaluator.f(x0);
		fXnColumn.add(fx0);
		double dFx0 = fEvaluator.dF(x0);
		dFXnColumn.add(dFx0);
		double d2Fx0 = fEvaluator.d2F(x0);
		d2FXnColumn.add(d2Fx0);
		int counter = 0;
		nColumn.add(counter);
		double error = tolerance + 1;
		errorColumn.add(-1.0);
		while(fx0 != 0 && error > tolerance && (dFx0 != 0 || d2Fx0 != 0) && counter < nIterations){
			 double x1 = x0-((fx0*dFx0)/((dFx0*dFx0)-(fx0*d2Fx0)));
			 xNColumn.add(x1);
			 fx0 = fEvaluator.f(x1);
			 fXnColumn.add(fx0);
			 dFx0 = fEvaluator.dF(x1);
			 dFXnColumn.add(dFx0);
			 d2Fx0 = fEvaluator.d2F(x1);
			 d2FXnColumn.add(d2Fx0);
			 error = calculateError(x1, x0);
             if(error == -1){
          	     multipleRootsArrayList.add("-2");  //-2 : hubo error calculando 
          	   								    //el error. 
                 return multipleRootsArrayList; 
             }
             errorColumn.add(error);
			 x0 = x1;
			 counter++;
			 nColumn.add(counter);
		 }
		 if(fx0 == 0){
			 multipleRootsArrayList.add("0");  //0 : el resultado fue una ra�z.
			 String x0IsRoot = x0 + " is a root.";
			 multipleRootsArrayList.add(String.valueOf(x0IsRoot));
		     return multipleRootsArrayList; 
		 }else if(error <= tolerance){
				 	multipleRootsArrayList.add("1");  //1 : el resultado fue una 
				 							         //aproximaci�n a la ra�z.
					String approximation = x0 + " is an approximation to a root, " +
										        "with tolerance = " + toleranceString + "."; 
					multipleRootsArrayList.add(String.valueOf(approximation));
					return multipleRootsArrayList;  
		 	   }else if(dFx0 == 0 && d2Fx0 == 0){
		 		   		multipleRootsArrayList.add("2");    //2 : el resultado fue 
											               //ecuaci�n insoluble.
						String insolubleEq = "Equation is insoluble.";
						multipleRootsArrayList.add(String.valueOf(insolubleEq));
						return multipleRootsArrayList; 
		 	        }else{
		 	        	multipleRootsArrayList.add("3");     //3 : el resultado fue 
						                                    //fracaso en la aplicaci�n
						                                   //del m�todo.
						String failure = "Failure applying the Multiple Roots method " +
								         "because the number of iterations were " +
						                 "exceeded."; 
						multipleRootsArrayList.add(String.valueOf(failure));
						return multipleRootsArrayList;
		 	        }
	}
	
	public double calculateError(double xM, double xMPrev){
		if(errorType.equals("Absolute")){
			return Math.abs(xM - xMPrev); 
		}else{
			if(xM != 0){
				return Math.abs((xM - xMPrev) / xM); 
			}else{
				return -1;
			}
		}
	}
	
	public void returnToMethod(View v){
		resultView.setVisibility(View.GONE);
		isAppOnMethodView = true;
		invalidateOptionsMenu();
		multipleRootsView.setVisibility(View.VISIBLE);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
		Intent openSelectedItem = new Intent(MultipleRoots.this, GuideMenu.class);
		Bundle methodToHelp = new Bundle();
		methodToHelp.putInt("methodNameId", R.string.multiple_roots);
	    switch (item.getItemId()) {
		    case R.id.resultsTable:
		    	convertColumnsToStrings();
	    		methodToHelp.putStringArrayList("nColumn", nColumnStrings);
	    		methodToHelp.putStringArrayList("xNColumn", xNColumnStrings);
	    		methodToHelp.putStringArrayList("fXnColumn", fXnColumnStrings);
	    		methodToHelp.putStringArrayList("dFXnColumn", dFXnColumnStrings);
	    		methodToHelp.putStringArrayList("d2FXnColumn", d2FXnColumnStrings);
	    		methodToHelp.putStringArrayList("errorColumn", errorColumnStrings);
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
	
	public void convertColumnsToStrings(){
		nColumnStrings.clear();
		xNColumnStrings.clear();
		fXnColumnStrings.clear();
		dFXnColumnStrings.clear();
		d2FXnColumnStrings.clear();
		errorColumnStrings.clear();
		for (Integer n : nColumn) {
			nColumnStrings.add(String.valueOf(n));
		}
		for (Double xN : xNColumn) {
			xNColumnStrings.add(String.valueOf(xN));
		}
		for (Double fXn : fXnColumn) {
			fXnColumnStrings.add(String.valueOf(fXn));
		}
		for (Double dFXn : dFXnColumn) {
			dFXnColumnStrings.add(String.valueOf(dFXn));
		}
		for (Double d2FXn : d2FXnColumn) {
			d2FXnColumnStrings.add(String.valueOf(d2FXn));
		}
		for (Double error : errorColumn) {
			if(error == -1.0){
				errorColumnStrings.add("-");
			}else{
				errorColumnStrings.add(String.valueOf(error));
			}
		}
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
    	getMenuInflater().inflate(R.menu.multiple_roots, menu);
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

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

public class FalsePosition extends Activity {

	private FunctionsEvaluator fEvaluator;
	private RelativeLayout falsePositionView;
	private EditText etXLower;
	private EditText etXUpper;
	private EditText etTolerance;
	private RadioButton rbAbsError;
	private RadioButton rbRelError;
	private EditText etNIterations;
	private RelativeLayout resultView;
	private TextView methodResult;
	private double xLower;
	private double xUpper;
	private String toleranceString;
	private double tolerance;
	private int nIterations;
	//Para validar tolerancia.
	private int minusCount;
	private int pointCount;
	private int tenExponentCount;
	private int exponentCount;
	//
	//Para validar valor XLower y XUpper.
	private int valuesPointCount;
	//
	private ArrayList<String> resultsMap = new ArrayList<String>();
	private ArrayList<String> falsePositionArrayList = new ArrayList<String>();
	private String errorType = "Absolute"; //Por defecto se va a calcular con error absoluto.
	private boolean isAppOnMethodView = true;  //Se hace para saber qu� opciones mostrar
	  										  //en el men� y cu�les esconder.
	
	//Para la tabla
	private ArrayList<Integer> nColumn = new ArrayList<Integer>();
	private ArrayList<String> nColumnStrings = new ArrayList<String>();
	private ArrayList<Double> xLowerColumn = new ArrayList<Double>();
	private ArrayList<String> xLowerColumnStrings = new ArrayList<String>();
	private ArrayList<Double> xUpperColumn = new ArrayList<Double>();
	private ArrayList<String> xUpperColumnStrings = new ArrayList<String>();
	private ArrayList<Double> xMColumn = new ArrayList<Double>();
	private ArrayList<String> xMColumnStrings = new ArrayList<String>();
	private ArrayList<Double> fXmColumn = new ArrayList<Double>();
	private ArrayList<String> fXmColumnStrings = new ArrayList<String>();
	private ArrayList<Double> errorColumn = new ArrayList<Double>();
	private ArrayList<String> errorColumnStrings = new ArrayList<String>();
	//
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_false_position);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		fEvaluator = (FunctionsEvaluator) getIntent().getParcelableExtra("fEvaluator");
		initViewElements();
	}
	
	public void initViewElements(){
		falsePositionView = (RelativeLayout) findViewById(R.id.false_position_view);
		etXLower = (EditText) findViewById(R.id.falsePosition_etXLower);
		etXUpper = (EditText) findViewById(R.id.falsePosition_etXUpper);
		etTolerance = (EditText) findViewById(R.id.falsePosition_etTolerance);
		rbAbsError = (RadioButton) findViewById(R.id.falsePosition_absError);
		rbAbsError.setChecked(true); //Por defecto se har� el c�lculo con error absoluto.
		rbRelError = (RadioButton) findViewById(R.id.falsePosition_relError);
		etNIterations = (EditText) findViewById(R.id.falsePosition_etNIterations);
		resultView = (RelativeLayout) findViewById(R.id.falsePosition_result_view);
		methodResult = (TextView) findViewById(R.id.falsePosition_method_result);
		resultView.setVisibility(View.GONE);
		isAppOnMethodView = true;
		invalidateOptionsMenu();
		falsePositionView.setVisibility(View.VISIBLE);
	}
	
	public void falsePosition(View v){
			valuesPointCount = 0;
			minusCount = 0;
			pointCount = 0;
			tenExponentCount = 0;
			exponentCount = 0;
			if(!isAnyFieldEmpty()){
				if(isValuesFormatOK()){
					captureFalsePositionData();
					if(isXLowerLessThanXUpper()){
						if(isToleranceOK()){
							resultsMap = calculateWithFalsePosition();
							if(resultsMap.get(0).equals("-2")){
								Toast.makeText(getApplicationContext(), getResources()
									       .getString(R.string.error_calc_error), 
									       Toast.LENGTH_SHORT).show();
							}else{
								falsePositionView.setVisibility(View.GONE);
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
								       .getString(R.string.x_lower_not_less), Toast.LENGTH_SHORT)
								       .show();
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
	
	public void captureFalsePositionData(){
		xLower = Double.parseDouble(etXLower.getText().toString());
		xUpper = Double.parseDouble(etXUpper.getText().toString());
		toleranceString = etTolerance.getText().toString();
		nIterations = Integer.parseInt(etNIterations.getText().toString());
	}
	
	public ArrayList<String> calculateWithFalsePosition(){
		falsePositionArrayList.clear();
		nColumn.clear();
		xLowerColumn.clear();
		xUpperColumn.clear();
		xMColumn.clear();
		fXmColumn.clear();
		errorColumn.clear();
		nColumn.add(1);
		xLowerColumn.add(xLower);
		xUpperColumn.add(xUpper);
		double yLower = fEvaluator.f(xLower);
		double yUpper = fEvaluator.f(xUpper);
		if(yLower == 0){ 
			 falsePositionArrayList.add("0");  //0 : el resultado fue una ra�z.
			 String xLowerIsRoot = xLower + " is a root.";
			 falsePositionArrayList.add(String.valueOf(xLowerIsRoot));
		     return falsePositionArrayList; 
		}else if(yUpper == 0){
				 falsePositionArrayList.add("0");  //0 : el resultado fue una ra�z.
				 String xUpperIsRoot = xUpper + " is a root.";
				 falsePositionArrayList.add(String.valueOf(xUpperIsRoot));
			     return falsePositionArrayList;  
		         }else if((yLower * yUpper) < 0){
		        	 	 double xM = xLower-((yLower*(xUpper-xLower))/(yUpper-yLower));
		        	 	 xMColumn.add(xM);
		                 double yM = fEvaluator.f(xM);
		                 fXmColumn.add(yM);
		                 double error = tolerance + 1;
		                 errorColumn.add(-1.0);
		                 int count = 1;
		                    while((yM != 0) && (error > tolerance) && (count < nIterations)){
		                           if((yLower * yM) < 0){
		                        	   xUpper = xM;
		                               yUpper = yM;
		                           }else{
		                        	   xLower = xM;
		                               yLower = yM;
		                           }
		                           xLowerColumn.add(xLower);
		                           xUpperColumn.add(xUpper);
		                           double xMPrev = xM;
		  		        	 	   xM = xLower-((yLower*(xUpper-xLower))/(yUpper-yLower));
		  		        	 	   xMColumn.add(xM);
		                           yM = fEvaluator.f(xM);
		                           fXmColumn.add(yM);
		                           error = calculateError(xM, xMPrev);
		                           if(error == -1){
		                        	   falsePositionArrayList.add("-2");  //-2 : hubo error 
		                        	   								     //calculando 
		                        	   								 	//el error. 
				                       return falsePositionArrayList; 
		                           }
		                           errorColumn.add(error);
		                           count++;
		                           nColumn.add(count);
		                    }
		                    if(yM == 0){
		                    	falsePositionArrayList.add("0");  //0 : el resultado fue una ra�z.
		                    	String xMisRoot = xM + " is a root.";
		       				 	falsePositionArrayList.add(String.valueOf(xMisRoot));
		       				 	return falsePositionArrayList;    
		                    }else if(error <= tolerance){
			                    	falsePositionArrayList.add("1");  //1 : el resultado fue una 
			                    								     //aproximaci�n a la ra�z.
			                    	String approximation = xM + " is an approximation to a root, " +
			                    						   "with tolerance = " + toleranceString + 
			                    						   "."; 
			       				 	falsePositionArrayList.add(String.valueOf(approximation));
			       				 	return falsePositionArrayList;  
		                    	  }else{
				                    	falsePositionArrayList.add("2");  //2 : el resultado fue 
				                    									 //fracaso en la aplicaci�n 
				                    									//del m�todo.
				                    	String failure = "Failure applying the False Position " +
				                    					 "method because the number of iterations" +
				                    					 " were exceeded."; 
				                    	falsePositionArrayList.add(String.valueOf(failure));
				                    	return falsePositionArrayList;  
		                    	  }
		           		}else{
		           			falsePositionArrayList.add("-1");  //-1 : el resultado fue un intervalo 
		           										  //inv�lido. 
		           			String intervalFailure = "The False Position method doesn�t allow the " 
		           									  + "entered interval.";  
		           			falsePositionArrayList.add(String.valueOf(intervalFailure));
		           			return falsePositionArrayList; 
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
	
	public boolean isAnyFieldEmpty(){
		if(etXLower.getText().toString().isEmpty() || etXLower.getText().toString().equals("-")
		   || etXUpper.getText().toString().isEmpty() || etXUpper.getText().toString().equals("-")
		   || etTolerance.getText().toString().isEmpty() || etNIterations.getText().toString()
		   .isEmpty()){
			return true;
			
		}else{
			return false;
		}
	}
	
	public void selectErrorType(View v){
		switch(v.getId()) {
        case R.id.falsePosition_absError:
        	rbRelError.setChecked(false);
        	errorType = "Absolute";
            break;
        case R.id.falsePosition_relError:
        	rbAbsError.setChecked(false);
        	errorType = "Relative";
            break;
		} 
	}
	
	public boolean isValuesFormatOK(){
		for(int i = 0; i < etXLower.getText().toString().length(); i++){
			if(etXLower.getText().toString().charAt(i) != '-' && 
			   etXLower.getText().toString().charAt(i) != '.' && 
			   !Character.isDigit(etXLower.getText().toString().charAt(i))){
				return false;
			}else{
				if(etXLower.getText().toString().charAt(i) == '-'){
					if(i != 0){
						return false;
					}
				 }else if(etXLower.getText().toString().charAt(i) == '.'){
					 		valuesPointCount++;
					 		if(valuesPointCount > 1 || i == 0 ){
					 			return false;
					 		}
				 }
			}
		}
		
		valuesPointCount = 0;
		
		for(int i = 0; i < etXUpper.getText().toString().length(); i++){
			if(etXUpper.getText().toString().charAt(i) != '-' && 
			   etXUpper.getText().toString().charAt(i) != '.' && 
			   !Character.isDigit(etXUpper.getText().toString().charAt(i))){
				return false;
			}else{
				if(etXUpper.getText().toString().charAt(i) == '-'){
					if(i != 0){
						return false;
					}
				 }else if(etXUpper.getText().toString().charAt(i) == '.'){
					 		valuesPointCount++;
					 		if(valuesPointCount > 1 || i == 0 ){
					 			return false;
					 		}
				 }
			}
		}
		
		return true;
	}
	
	public boolean isXLowerLessThanXUpper(){
		return (xLower < xUpper) ? true : false;
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
	
	public void returnToMethod(View v){
		resultView.setVisibility(View.GONE);
		isAppOnMethodView = true;
		invalidateOptionsMenu();
		falsePositionView.setVisibility(View.VISIBLE);
	}

	public void convertColumnsToStrings(){
		nColumnStrings.clear();
		xLowerColumnStrings.clear();
		xUpperColumnStrings.clear();
		xMColumnStrings.clear();
		fXmColumnStrings.clear();
		errorColumnStrings.clear();
		for (Integer n : nColumn) {
			nColumnStrings.add(String.valueOf(n));
		}
		for (Double xLower : xLowerColumn) {
			xLowerColumnStrings.add(String.valueOf(xLower));
		}
		for (Double xUpper : xUpperColumn) {
			xUpperColumnStrings.add(String.valueOf(xUpper));
		}
		for (Double xM : xMColumn) {
			xMColumnStrings.add(String.valueOf(xM));
		}
		for (Double fXm : fXmColumn) {
			fXmColumnStrings.add(String.valueOf(fXm));
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
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
		Intent openSelectedItem = new Intent(FalsePosition.this, GuideMenu.class);
		Bundle methodToHelp = new Bundle();
		methodToHelp.putInt("methodNameId", R.string.false_position);
	    switch (item.getItemId()) {
		    case R.id.resultsTable:
		    	convertColumnsToStrings();
	    		methodToHelp.putStringArrayList("nColumn", nColumnStrings);
	    		methodToHelp.putStringArrayList("xLowerColumn", xLowerColumnStrings);
	    		methodToHelp.putStringArrayList("xUpperColumn", xUpperColumnStrings);
	    		methodToHelp.putStringArrayList("xMColumn", xMColumnStrings);
	    		methodToHelp.putStringArrayList("fXmColumn", fXmColumnStrings);
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
    	getMenuInflater().inflate(R.menu.false_position, menu);
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

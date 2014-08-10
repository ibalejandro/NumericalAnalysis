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

public class Secant extends Activity {
	
	private FunctionsEvaluator fEvaluator;
	private RelativeLayout secantView;
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
	//Para validar valor X0.
	private int valuesPointCount;
	//
	private ArrayList<String> resultsMap = new ArrayList<String>();
	private ArrayList<String> secantArrayList = new ArrayList<String>();
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
	private ArrayList<Double> errorColumn = new ArrayList<Double>();
	private ArrayList<String> errorColumnStrings = new ArrayList<String>();
	//
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_secant);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		fEvaluator = (FunctionsEvaluator) getIntent().getParcelableExtra("fEvaluator");
		initViewElements();
	}

	public void initViewElements(){
		secantView = (RelativeLayout) findViewById(R.id.secant_view);
		etXLower = (EditText) findViewById(R.id.secant_etXLower);
		etXUpper = (EditText) findViewById(R.id.secant_etXUpper);
		etTolerance = (EditText) findViewById(R.id.secant_etTolerance);
		rbAbsError = (RadioButton) findViewById(R.id.secant_absError);
		rbAbsError.setChecked(true); //Por defecto se har� el c�lculo con error absoluto.
		rbRelError = (RadioButton) findViewById(R.id.secant_relError);
		etNIterations = (EditText) findViewById(R.id.secant_etNIterations);
		resultView = (RelativeLayout) findViewById(R.id.secant_result_view);
		methodResult = (TextView) findViewById(R.id.secant_method_result);
		resultView.setVisibility(View.GONE);
		isAppOnMethodView = true;
		invalidateOptionsMenu();
		secantView.setVisibility(View.VISIBLE);
	}
	
	public void secant(View v){
		valuesPointCount = 0;
		minusCount = 0;
		pointCount = 0;
		tenExponentCount = 0;
		exponentCount = 0;
		if(!isAnyFieldEmpty()){
			if(isValuesFormatOK()){
				captureSecantData();
				if(isXLowerLessThanXUpper()){
					if(isToleranceOK()){
						resultsMap = calculateWithSecant();
						if(resultsMap.get(0).equals("-2")){
							Toast.makeText(getApplicationContext(), getResources()
								       .getString(R.string.error_calc_error), 
								       Toast.LENGTH_SHORT).show();
						}else{
							secantView.setVisibility(View.GONE);
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
	
	public boolean isAnyFieldEmpty(){
		if(etXLower.getText().toString().isEmpty() || etXUpper.getText().toString().isEmpty() || 
		   etTolerance.getText().toString().isEmpty() || etNIterations.getText().toString()
		   .isEmpty()){
			return true;
			
		}else{
			return false;
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
	
	public void captureSecantData(){
		xLower = Double.parseDouble(etXLower.getText().toString());
		xUpper = Double.parseDouble(etXUpper.getText().toString());
		toleranceString = etTolerance.getText().toString();
		nIterations = Integer.parseInt(etNIterations.getText().toString());
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
	
	public void selectErrorType(View v){
		switch(v.getId()) {
        case R.id.secant_absError:
        	rbRelError.setChecked(false);
        	errorType = "Absolute";
            break;
        case R.id.secant_relError:
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
	
	public ArrayList<String> calculateWithSecant(){
		secantArrayList.clear();
		nColumn.clear();
		xNColumn.clear();
		fXnColumn.clear();
		errorColumn.clear();
		nColumn.add(0);
		xNColumn.add(xLower);
		double fxLower = fEvaluator.f(xLower);
		fXnColumn.add(fxLower);
		errorColumn.add(-1.0);
		if(fxLower == 0){
			secantArrayList.add("0");  //0 : el resultado fue una ra�z.
			String x0IsRoot = xLower + " is a root.";
			secantArrayList.add(String.valueOf(x0IsRoot));
		    return secantArrayList; 
		}else{
			nColumn.add(1);
			xNColumn.add(xUpper);
			double fxUpper = fEvaluator.f(xUpper);
			fXnColumn.add(fxUpper);
			int count = 0;
			double error = tolerance + 1;
			errorColumn.add(-1.0);
			double den = fxUpper - fxLower;
			while(fxUpper != 0 && error > tolerance && den != 0 && count < nIterations){
				 double xN = xUpper - ((fxUpper*(xUpper - xLower))/den);
				 xNColumn.add(xN);
				 error = calculateError(xN, xUpper);
	             if(error == -1){
	          	     secantArrayList.add("-2");      //-2 : hubo error calculando 
	          	   								    //el error. 
	                 return secantArrayList; 
	             }
	             errorColumn.add(error);
				 xLower = xUpper;
				 fxLower = fxUpper;
				 xUpper = xN;
				 fxUpper = fEvaluator.f(xUpper);
				 fXnColumn.add(fxUpper);
				 den = fxUpper - fxLower;
				 count++;
				 nColumn.add(count+1);
			 }
			 if(fxUpper == 0){
				 secantArrayList.add("0");  //0 : el resultado fue una ra�z.
				 String xUpperIsRoot = xUpper + " is a root.";
				 secantArrayList.add(String.valueOf(xUpperIsRoot));
			     return secantArrayList; 
			 }else if(error <= tolerance){
					 	secantArrayList.add("1");  //1 : el resultado fue una 
					 							  //aproximaci�n a la ra�z.
						String approximation = xUpper + " is an approximation to a root, " +
											        "with tolerance = " + toleranceString + "."; 
						secantArrayList.add(String.valueOf(approximation));
						return secantArrayList;  
			 	   }else if(den == 0){
			 		   		secantArrayList.add("2");    //2 : el resultado fue 
												        //posible ra�z multiple.
							String posMultRoot = xUpper + " is a possible multiple root.";
							secantArrayList.add(String.valueOf(posMultRoot));
							return secantArrayList; 
			 	        }else{
			 	        	secantArrayList.add("3");    //3 : el resultado fue 
							                            //fracaso en la aplicaci�n
							                           //del m�todo.
							String failure = "Failure applying the Secant method " +
									         "because the number of iterations were " +
							                 "exceeded."; 
							secantArrayList.add(String.valueOf(failure));
							return secantArrayList;
			 	        }
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
		secantView.setVisibility(View.VISIBLE);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
		Intent openSelectedItem = new Intent(Secant.this, GuideMenu.class);
		Bundle methodToHelp = new Bundle();
		methodToHelp.putInt("methodNameId", R.string.secant);
	    switch (item.getItemId()) {
		    case R.id.resultsTable:
		    	convertColumnsToStrings();
	    		methodToHelp.putStringArrayList("nColumn", nColumnStrings);
	    		methodToHelp.putStringArrayList("xNColumn", xNColumnStrings);
	    		methodToHelp.putStringArrayList("fXnColumn", fXnColumnStrings);
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
    	getMenuInflater().inflate(R.menu.secant, menu);
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

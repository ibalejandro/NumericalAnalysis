package com.numericalanalysis.napp;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.app.ActionBar.LayoutParams;
import android.content.Intent;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class StationaryIterativeMethods extends Activity {
	
	private RelativeLayout stationaryIterativeMethodsView;
	private TableLayout tlX0s;
	// Provisional mientras se define el table view.
	private EditText[] etsX0 = new EditText[Matrix.getMatrixA().length]; 
	private EditText etTolerance;
	private RadioButton rbJacobiMethod;
	private RadioButton rbSeidelMethod;
	private RadioButton rbAbsDispersion;
	private RadioButton rbRelDispersion;
	private EditText etNIterations;
	private EditText etAlfa;
	private RelativeLayout resultView;
	private TextView methodUsed;
	private TextView methodResult;
	private double[] x0 = new double[Matrix.getMatrixA().length];
	private String toleranceString;
	private double tolerance;
	private double alfa = 1.0; // Por defecto se clacula el metodo sin relajación. 
	private int nIterations;
	
	//Para validar tolerancia.
	private int minusCount;
	private int pointCount;
	private int tenExponentCount;
	private int exponentCount;
	
	//Para validar valor X0.
	private int valuesPointCount;
	//
	private ArrayList<String> resultsMap = new ArrayList<String>();
	private ArrayList<String> stationaryIterativeMethodsArrayList = new ArrayList<String>();
	private String dispersionType = "Absolute"; //Por defecto se va a calcular con error absoluto.
	private String methodType = "Jacobi"; // Por defecto se va a calcular con el metodo de Jacobi.
	private int methodUsedID = R.string.jacobi;
	private boolean isAppOnMethodView = true;  //Se hace para saber qu� opciones mostrar
	  										  //en el men� y cu�les esconder.
	
	//Para la tabla
	private ArrayList<Integer> nColumn = new ArrayList<Integer>();
	private static ArrayList<String> nColumnStrings = new ArrayList<String>();
	private ArrayList<ArrayList<Double>> xNColumns = new ArrayList<ArrayList<Double>>();
	private ArrayList<ArrayList<String>> xNColumnsString = new ArrayList<ArrayList<String>>();
	private ArrayList<Double> dispersionColumn = new ArrayList<Double>();
	private ArrayList<String> dispersionColumnStrings = new ArrayList<String>();
	//
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stationary_iterative_methods);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		initViewElements();
	}

	public void initViewElements(){
		stationaryIterativeMethodsView = (RelativeLayout) findViewById(R.id.stationary_iterative_methods_view);
		tlX0s = (TableLayout) findViewById(R.id.stationary_iterative_methods_initial_values);
		initEtsX0();
		etTolerance = (EditText) findViewById(R.id.stationary_iterative_methods_etTolerance);
		rbJacobiMethod = (RadioButton) findViewById(R.id.stationary_iterative_methods_jacobi_method);
		rbJacobiMethod.setChecked(true); //Por defecto se har� el c�lculo con el metodo de jacobi.
		rbSeidelMethod = (RadioButton) findViewById(R.id.stationary_iterative_methods_seidel_method);
		rbAbsDispersion = (RadioButton) findViewById(R.id.stationary_iterative_methods_absDispersion);
		rbAbsDispersion.setChecked(true); //Por defecto se har� el c�lculo con error absoluto.
		rbRelDispersion = (RadioButton) findViewById(R.id.stationary_iterative_methods_relDispersion);
		etAlfa = (EditText) findViewById(R.id.stationary_iterative_etAlfa);
		etNIterations = (EditText) findViewById(R.id.stationary_iterative_methods_etNIterations);
		resultView = (RelativeLayout) findViewById(R.id.stationary_iterative_methods_result_view);
		methodUsed = (TextView) findViewById(R.id.method_used_iterative);
		methodResult = (TextView) findViewById(R.id.stationary_iterative_methods_method_result);
		resultView.setVisibility(View.GONE);
		isAppOnMethodView = true;
		invalidateOptionsMenu();
		stationaryIterativeMethodsView.setVisibility(View.VISIBLE);
	}
	
	public void initEtsX0(){
		TableRow row = new TableRow(this);
	    for (int i = 0; i < Matrix.getVectorB().length; i++) {
            EditText cell = new EditText(this);
            cell.setHint("Ini. X" + (i+1));
            cell.setTextSize(13.5f);
            cell.setGravity(Gravity.CENTER);
            cell.setSingleLine(true);
            etsX0[i] = cell;
            if(i > 0){
            	row.getChildAt(i-1).setNextFocusDownId(cell.getId());
            	row.getChildAt(i-1).setNextFocusForwardId(cell.getId());
            	row.addView(cell);
            }else{
            	row.addView(cell);
            }
	    }
	    tlX0s.addView(row ,new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				  										LayoutParams.WRAP_CONTENT));
		
	}
	
	public void stationaryIterativeMethods(View v){
		valuesPointCount = 0;
		minusCount = 0;
		pointCount = 0;
		tenExponentCount = 0;
		exponentCount = 0;
		if(!isAnyFieldEmpty()){
			if(isValuesFormatOK()){
				if(isAlfaValueOK()){
					captureStationaryIterativeMethodsData();
						if(isToleranceOK()){
							resultsMap = calculateWithStationaryIterativeMethods();
							if(resultsMap.get(0).equals("-2")){
								Toast.makeText(getApplicationContext(), getResources()
									       .getString(R.string.error_calc_disp), 
									       Toast.LENGTH_SHORT).show();
							}else{
								stationaryIterativeMethodsView.setVisibility(View.GONE);
								isAppOnMethodView = false;
								invalidateOptionsMenu();
								resultView.setVisibility(View.VISIBLE);
								methodUsed.setText(methodType);
								methodResult.setText(resultsMap.get(1));
							}
						}else{
							Toast.makeText(getApplicationContext(), getResources()
								          .getString(R.string.invalid_tolerance), 
								          Toast.LENGTH_SHORT).show();
						}
				}else{
					Toast.makeText(getApplicationContext(), getResources()
					          .getString(R.string.invalid_alfa_value), 
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
		for(int i = 0; i < etsX0.length; i++){
			if(etsX0[i].getText().toString().isEmpty() || etTolerance.getText().toString().isEmpty() 
			   || etNIterations.getText().toString().isEmpty()){
				return true;
			}		
		}
		return false;
	}
	
	public boolean isValuesFormatOK(){
		for(int a = 0; a < etsX0.length; a++){
			valuesPointCount = 0;
			for(int i = 0; i < etsX0[a].getText().toString().length(); i++){
				if(etsX0[a].getText().toString().charAt(i) != '-' && 
				   etsX0[a].getText().toString().charAt(i) != '.' && 
				   !Character.isDigit(etsX0[a].getText().toString().charAt(i))){
					return false;
				}else{
					if(etsX0[a].getText().toString().charAt(i) == '-'){
						if(i != 0){
							return false;
						}
					 }else if(etsX0[a].getText().toString().charAt(i) == '.'){
						 		valuesPointCount++;
						 		if(valuesPointCount > 1 || i == 0 ){
						 			return false;
						 		}
					 }
				}
			}
		}
		
		return true;
	}
	
	public boolean isAlfaValueOK(){
		if(!etAlfa.getText().toString().isEmpty()){
			double inputAlfa = Double.parseDouble(etAlfa.getText().toString());
			if(inputAlfa >= 0 && inputAlfa <= 2){
				return true;
			}else{
				return false;
			}
		}else{
			return true;
		}
	}
	
	public void captureStationaryIterativeMethodsData(){
		for(int i = 0; i < x0.length; i++){
			x0[i] = Double.parseDouble(etsX0[i].getText().toString());	
		}
		if(!etAlfa.getText().toString().isEmpty()){
			alfa = Double.parseDouble(etAlfa.getText().toString());
		}else{
			alfa = 1;
		}
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
	
	public void selectDispersionType(View v){
		switch(v.getId()) {
        case R.id.stationary_iterative_methods_absDispersion:
        	rbRelDispersion.setChecked(false);
        	dispersionType = "Absolute";
            break;
        case R.id.stationary_iterative_methods_relDispersion:
        	rbAbsDispersion.setChecked(false);
        	dispersionType = "Relative";
            break;
		} 
	}
	
	public void selectMethodType(View v){
		switch(v.getId()) {
        case R.id.stationary_iterative_methods_jacobi_method:
        	rbSeidelMethod.setChecked(false);
        	methodType = "Jacobi";
        	methodUsedID = R.string.jacobi;
            break;
        case R.id.stationary_iterative_methods_seidel_method:
        	rbJacobiMethod.setChecked(false);
        	methodType = "Gauss-Seidel";
        	methodUsedID = R.string.gauss_seidel;
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
	
	public ArrayList<String> calculateWithStationaryIterativeMethods(){
		stationaryIterativeMethodsArrayList.clear();
		nColumn.clear();
		xNColumns.clear();
		dispersionColumn.clear();
		initXnColumnsArrayLists(Matrix.getVectorB().length);
		double[] x1 = new double[x0.length];
		int counter = 0;
		nColumn.add(counter);
		double dispersion = tolerance + 1;
		dispersionColumn.add(-1.0);
		fillXNRow(x0);
		while(dispersion > tolerance && counter < nIterations){
			if(methodType.equals("Jacobi")) {
				x1 = calculateWithJacobi(x0);
			}else {
				x1 = calculateWithSeidel(x0);
			}
			if(x1 == null){
				//0 : el sistema tiene infinitas soluciones. 
				stationaryIterativeMethodsArrayList.add("0"); 
				stationaryIterativeMethodsArrayList.add(getResources()
														.getString(R.string.inf_solutions));
                return stationaryIterativeMethodsArrayList; 
			}
			dispersion = rule(x0,x1);
			if(dispersion == -1){
				//-2 : hubo error calculando el error. 
				stationaryIterativeMethodsArrayList.add("-2");  
                return stationaryIterativeMethodsArrayList; 
            }
            dispersionColumn.add(dispersion);
			x0 = x1;
			fillXNRow(x1);
			counter++;
			nColumn.add(counter);
		}
		if(dispersion <= tolerance){
			String approximation = arrayToString(x1);
			// 1: Los x1 son aproximaciones a las raices.
			stationaryIterativeMethodsArrayList.add("1");
			stationaryIterativeMethodsArrayList.add(approximation);
		}else{
			//2 : el resultado fue fracaso en la aplicaci�n del m�todo.
			stationaryIterativeMethodsArrayList.add("2");    
			String failure = "Failure applying the " + methodType + " method " +
							 "because the number of iterations were exceeded."; 
			stationaryIterativeMethodsArrayList.add(String.valueOf(failure));
			return stationaryIterativeMethodsArrayList;
		}
		return stationaryIterativeMethodsArrayList;
	}

	public double[] calculateWithJacobi(double[] x0){
		double [] x1 = new double[x0.length];
		for(int i = 0; i < x0.length; i++) {
			double sum = 0;
			for(int j = 0; j < x0.length; j++) {
				if(j != i) {
					sum = sum + (Matrix.getMatrixA()[i][j] * x0[j]);
				}
			}
			if(Matrix.getMatrixA()[i][i] != 0){
				x1[i] = ( alfa *((Matrix.getVectorB()[i] - sum)/ Matrix.getMatrixA()[i][i])) + ((1-alfa) * x0[i]);
			}else{
				return null;
			}
		}
		return x1;
	}
	
	public double[] calculateWithSeidel(double[] x0){
		double [] x1 = new double[x0.length];
		for (int i = 0; i < x0.length; i++) {
			x1[i] = x0[i];
		}
		for(int i = 0; i < x0.length; i++) {
			double sum = 0;
			for(int j = 0; j < x0.length; j++) {
				if(j != i) {
					sum = sum + (Matrix.getMatrixA()[i][j] * x1[j]);
				}
			}
			if(Matrix.getMatrixA()[i][i] != 0){
				x1[i] = ( alfa *((Matrix.getVectorB()[i] - sum)/ Matrix.getMatrixA()[i][i])) + ((1-alfa) * x0[i]);
			}else{
				return null;
			}
			
		}
		return x1;
	}
	
	public double rule(double[] x0, double[] x1){
		double max = 0;
		if(dispersionType.equals("Absolute")) {
			for(int i = 0; i < x0.length; i++){
				if(Math.abs(x1[i]-x0[i]) > max) {
					max = Math.abs(x1[i]-x0[i]);
				}
			}
		}else {
			for(int i = 0; i < x0.length; i++){
				// Revisar condicion de denominador 0.
				if(x1[i] != 0){
					if(Math.abs((x1[i]-x0[i])/x1[i]) > max) {
						max = Math.abs((x1[i]-x0[i])/x1[i]);
					}
				}
			}
			if(max == 0){
				return -1.0;
			}
		}
		return max;
	}
	
	public void initXnColumnsArrayLists(int n){
		for(int i = 0; i < n; i++){
			ArrayList<Double> unknown = new ArrayList<Double>();
			xNColumns.add(unknown);
		}
	}

	public void fillXNRow(double[] x1){
		for(int i = 0; i < x1.length; i++){
			xNColumns.get(i).add(x1[i]);
		}
	}
	
	public String arrayToString(double[] x1){
		String approximation = "";	
		for(int i = 0; i < x1.length; i++) {
			approximation += "X" + (i+1) + " = " + String.valueOf(x1[i]) + "\n";
		}
		approximation += "\nare approximations with tolerance = " + toleranceString + ".";
		return approximation;
	}
	
	public void returnToMethod(View v){
		resultView.setVisibility(View.GONE);
		isAppOnMethodView = true;
		invalidateOptionsMenu();
		stationaryIterativeMethodsView.setVisibility(View.VISIBLE);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
		Intent openSelectedItem = new Intent(StationaryIterativeMethods.this, GuideMenu.class);
		Bundle methodToHelp = new Bundle();
		methodToHelp.putInt("methodNameId", methodUsedID);
	    switch (item.getItemId()) {
		    case R.id.resultsTable:
		    	convertColumnsToStrings();
	    		methodToHelp.putStringArrayList("nColumn", nColumnStrings);
	    		//methodToHelp.putStringArrayList("xNColumn", xNColumnsString);
	    		new Matrix(xNColumnsString);   //Se hace para poder acceder a las columnas que va a 
	    									  //tener la tabla de resultados.
	    		methodToHelp.putStringArrayList("dispersionColumn", dispersionColumnStrings);
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
		xNColumnsString.clear();
		dispersionColumnStrings.clear();
		for (Integer n : nColumn) {
			nColumnStrings.add(String.valueOf(n));
		}
		// Cada iteracion i es una columna xN.
		for(int i = 0; i < xNColumns.size(); i++){
			ArrayList<Double> xNColumn = xNColumns.get(i);
			ArrayList<String> xNColumnString = new ArrayList<String>();
			for (int j = 0; j < xNColumn.size(); j++) {
				xNColumnString.add(String.valueOf(xNColumn.get(j)));
			}
			xNColumnsString.add(xNColumnString);
		}
		for (Double dispersion : dispersionColumn) {
			if(dispersion == -1.0){
				dispersionColumnStrings.add("-");
			}else{
				dispersionColumnStrings.add(String.valueOf(dispersion));
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
    	getMenuInflater().inflate(R.menu.stationary_iterative_methods, menu);
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

package com.numericalanalysis.napp;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class CubicSpline extends Activity {

	private FunctionsEvaluator fEvaluator;
	private TextView methodLabel;
	private TextView methodResult;
	private TextView xValueLabel;
	private EditText etXValue;
	private Button evaluate;
	// Valor para evaluar el polinomio respuesta.
	private double x;
	private ArrayList<ArrayList<Double>> resultMatrix = new ArrayList<ArrayList<Double>>();
	private ArrayList<Double> copiedXnValues = new ArrayList<Double>();
	private double [][] interpolationMatrix;
	private int [] marks;
	int eqsDone = 0;
	private ArrayList<Double> coefficients = new ArrayList<Double>();
	private ArrayList<String> individualPolynomials = new ArrayList<String>();
	private String polynomial; 
	private ArrayList<String> equations = new ArrayList<String>();
	private boolean isAppOnEvalView = false;
	private boolean isAppOnEnterXView = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cubic_spline);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		initViewElements();
	}
	
	public void initViewElements(){
		methodLabel = (TextView) findViewById(R.id.cubic_spline_method_result_label);
		methodResult = (TextView) findViewById(R.id.cubic_spline_method_result);
		xValueLabel = (TextView) findViewById(R.id.cubic_spline_evaluate_value_label);
		etXValue = (EditText) findViewById(R.id.cubic_spline_evaluate_value);
		evaluate = (Button) findViewById(R.id.cubic_spline_evaluate);
		xValueLabel.setVisibility(View.GONE);
		etXValue.setVisibility(View.GONE);
		evaluate.setVisibility(View.GONE);
		isAppOnEvalView = false;
		isAppOnEnterXView = false;
		invalidateOptionsMenu();
		calculateWithCubicSpline();
	}
	
	public void calculateWithCubicSpline(){
		resultMatrix.clear();
		copiedXnValues.clear();
		coefficients.clear();
		individualPolynomials.clear();
		equations.clear();
		copiedXnValues = cloneTable(InterpolationTables.getxNValues());
		int sXEqs = (copiedXnValues.size() - 1) * 2;
		int dSXEqs = copiedXnValues.size() - 2;
		int d2SXEqs = copiedXnValues.size() - 2;
		int supEqs = 2;
		int totalEqs = 4 * (copiedXnValues.size() - 1);
		for(int i = 0; i < totalEqs; i++){
			resultMatrix.add(new ArrayList<Double>());  //Se pone un nuevo ArrayList en cada posición.
		}
		
		//Ecuaciones con s(x).
		buildSXEqs(sXEqs, totalEqs);
		//Ecuaciones con s'(x).
		buildDSXEqs(dSXEqs, totalEqs);
		//Ecuaciones con s''(x).
		buildD2SXEqs(d2SXEqs, totalEqs);
		//Ecuaciones con s''(x) + suposición.
		buildSupEqs(supEqs, totalEqs);
		
		convertResultToMatrix();
		marks = Matrix.fillMarks(interpolationMatrix.length);
		solveWithTotalPivoting();
		if(coefficients.size() > 0){
			buildPiecewisePolynomial(totalEqs);
		}else{  //Hubo algún error resolviendo el sistema de ecuaciones.
			polynomial = getResources().getString(R.string.polynom_not_determined);
		}
		
		methodResult.setText(Html.fromHtml(polynomial), TextView.BufferType.SPANNABLE);
	}

	public void buildSXEqs(int sXEqs, int totalEqs){
		int xPos = 0;
		for(int i = 0; i < sXEqs; i++){
			int zeroCont = (i / 2) * 4; //Se hace para saber cuántos ceros agregar al inicio.
			
			String equation = "";
			int subIndex = i / 2;
			
			for(int j = 0; j < zeroCont; j++){  //j es sólo para iterar.
				resultMatrix.get(i).add(0.0);
			}
			
			resultMatrix.get(i).add(Math.pow(copiedXnValues.get(xPos), 3));
			equation += resultMatrix.get(i).get(resultMatrix.get(i).size() - 1) + "a" + subIndex + " + ";
			resultMatrix.get(i).add(Math.pow(copiedXnValues.get(xPos), 2));
			equation += resultMatrix.get(i).get(resultMatrix.get(i).size() - 1) + "b" + subIndex + " + ";
			resultMatrix.get(i).add(copiedXnValues.get(xPos));
			equation += resultMatrix.get(i).get(resultMatrix.get(i).size() - 1) + "c" + subIndex + " + ";
			resultMatrix.get(i).add(1.0);
			equation += "d" + subIndex + " = ";
			
			for(int k = (zeroCont + 4); k < totalEqs; k++){  //k es sólo para iterar.
				resultMatrix.get(i).add(0.0);
			}
			
			resultMatrix.get(i).add(InterpolationTables.getFXnValues().get(xPos));
			equation += resultMatrix.get(i).get(resultMatrix.get(i).size() - 1);
			
			equations.add(equation);
			
			if((i % 2) == 0){
				xPos++;
			}
		}
		eqsDone += sXEqs;
	}
	
	public void buildDSXEqs(int dSXEqs, int totalEqs){ 
		int xPos = 1;
		for(int i = eqsDone; i < (eqsDone + dSXEqs); i++){
			int zeroCount = ((xPos - 1) * 4);  //Se hace para saber cuántos ceros agregar al inicio.
			
			String equation = "";
			int subIndex = xPos - 1;
			
			for(int j = 0; j < zeroCount; j++){  //j es sólo para iterar.
				resultMatrix.get(i).add(0.0);
			}
			
			resultMatrix.get(i).add(3.0 * Math.pow(copiedXnValues.get(xPos), 2));
			equation += resultMatrix.get(i).get(resultMatrix.get(i).size() - 1) + "a" + subIndex + " + ";
			resultMatrix.get(i).add(2.0 * copiedXnValues.get(xPos));
			equation += resultMatrix.get(i).get(resultMatrix.get(i).size() - 1) + "b" + subIndex + " + ";
			resultMatrix.get(i).add(1.0);
			equation += "c" + subIndex + " = ";
			resultMatrix.get(i).add(0.0);
			resultMatrix.get(i).add(-3.0 * Math.pow(copiedXnValues.get(xPos), 2));
			equation += ((-1) * resultMatrix.get(i).get(resultMatrix.get(i).size() - 1)) + "a" 
						+ (subIndex + 1) + " + ";
			resultMatrix.get(i).add(-2.0 * copiedXnValues.get(xPos));
			equation += ((-1) * resultMatrix.get(i).get(resultMatrix.get(i).size() - 1)) + "b" 
						+ (subIndex + 1) + " + ";
			resultMatrix.get(i).add(-1.0);
			equation += "c" + (subIndex + 1);
			resultMatrix.get(i).add(0.0);
			
			equations.add(equation);
			
			for(int k = (zeroCount + 8); k < (totalEqs + 1); k++){  //Para añadir también el valor 
													               //del término independiente.
				resultMatrix.get(i).add(0.0);
			}
			
			xPos++;
		}
		eqsDone += dSXEqs;
	}
	
	public void buildD2SXEqs(int d2SXEqs, int totalEqs){ 
		int xPos = 1;
		for(int i = eqsDone; i < (eqsDone + d2SXEqs); i++){
			int zeroCount = ((xPos - 1) * 4);  //Se hace para saber cuántos ceros agregar al inicio.
			
			String equation = "";
			int subIndex = xPos - 1;
			
			for(int j = 0; j < zeroCount; j++){  //j es sólo para iterar.
				resultMatrix.get(i).add(0.0);
			}
			
			resultMatrix.get(i).add(6.0 * copiedXnValues.get(xPos));
			equation += resultMatrix.get(i).get(resultMatrix.get(i).size() - 1) + "a" + subIndex + " + ";
			resultMatrix.get(i).add(2.0);
			equation += resultMatrix.get(i).get(resultMatrix.get(i).size() - 1) + "b" + subIndex + " = ";
			resultMatrix.get(i).add(0.0);
			resultMatrix.get(i).add(0.0);
			resultMatrix.get(i).add(-6.0 * copiedXnValues.get(xPos));
			equation += ((-1)* resultMatrix.get(i).get(resultMatrix.get(i).size() - 1)) + "a" 
						+ (subIndex + 1) + " + ";
			resultMatrix.get(i).add(-2.0);
			equation += ((-1)* resultMatrix.get(i).get(resultMatrix.get(i).size() - 1)) + "b" 
						+ (subIndex + 1);
			resultMatrix.get(i).add(0.0);
			resultMatrix.get(i).add(0.0);
			
			equations.add(equation);
			
			for(int k = (zeroCount + 8); k < (totalEqs + 1); k++){  //Para añadir también el valor 
	               												   //del término independiente.
				resultMatrix.get(i).add(0.0);
			}
			
			xPos++;
		}
		eqsDone += d2SXEqs;
	}
	
	public void buildSupEqs(int supEqs, int totalEqs){ 
		int xPos = 0;
		for(int i = eqsDone; i < (eqsDone + supEqs); i++){
			
			String equation = "";
			int subIndex = xPos;
			
			if(xPos == 0){
				resultMatrix.get(i).add(6.0 * copiedXnValues.get(xPos));
				equation += resultMatrix.get(i).get(resultMatrix.get(i).size() - 1) + "a" + subIndex 
							+ " + ";
				resultMatrix.get(i).add(2.0);
				equation += resultMatrix.get(i).get(resultMatrix.get(i).size() - 1) + "b" + subIndex 
							+ " = ";
				resultMatrix.get(i).add(0.0);
				resultMatrix.get(i).add(0.0);
				for(int j = 4; j < (totalEqs + 1); j++){  //Para añadir también el valor del término
														 //independiente.
					resultMatrix.get(i).add(0.0);
				}
				
				equation += resultMatrix.get(i).get(resultMatrix.get(i).size() - 1);
				
			}else{
				subIndex -= 1;  //Se hace para que el subíndice de los coeficientes sea coherente.
				
				for(int k = 0; k < (totalEqs - 4); k++){  //Para añadir ceros antes de los últimos
														 //cuatro términos.
					resultMatrix.get(i).add(0.0);
				}
				resultMatrix.get(i).add(6.0 * copiedXnValues.get(xPos));
				equation += resultMatrix.get(i).get(resultMatrix.get(i).size() - 1) + "a" + subIndex 
							+ " + ";
				resultMatrix.get(i).add(2.0);
				equation += resultMatrix.get(i).get(resultMatrix.get(i).size() - 1) + "b" + subIndex 
							+ " = ";
				resultMatrix.get(i).add(0.0);
				resultMatrix.get(i).add(0.0);
				resultMatrix.get(i).add(0.0);  //Esto se hace para añadir el término independiente.
				
				equation += resultMatrix.get(i).get(resultMatrix.get(i).size() - 1);
			}
			
			xPos = copiedXnValues.size() - 1;  //Hace referencia al último punto.
			
			equations.add(equation);
		}
		eqsDone += supEqs;
	}
	
	public void convertResultToMatrix(){
		interpolationMatrix = new double [resultMatrix.size()][(resultMatrix.size() + 1)];
		for(int i = 0; i < resultMatrix.size(); i++){
			for(int j = 0; j < (resultMatrix.size() + 1); j++){
				interpolationMatrix[i][j] = resultMatrix.get(i).get(j);
			}
		}
	}
	
	public void solveWithTotalPivoting(){
		double [][] result = cloneMatrix(interpolationMatrix);
		for (int k = 0; k <= (result[0].length - 2); k++){
			result = calculateTotalPivoting(result, result.length, k);
			if(result == null) return;

			for (int i = k+1; i < result.length; i++){
				if(result[k][k] != 0){
					double mult = result[i][k] / result[k][k];
					
					for (int j = k; j < (result.length + 1); j++) {
						result[i][j] = result[i][j] - (mult*result[k][j]);
					}
				}else{
					// El sistema tiene soluciones infinitas.
					return;
				}	
			}
		}
		if(result[result.length-1][result.length-1] == 0){
			return;
		}else{  //Se le puede aplicar sustitución regresiva a la matriz para encontrar x.
			double[] coeffArray = new double[result.length];
			coeffArray = Matrix.regressiveSubstitutionElimination(result, result.length);
			orderCoefficients(coeffArray);
		}
	}
	
	public double[][] calculateTotalPivoting(double[][]Ab, int n, int k){
		double max = 0;
		int maxRow = k;
		int maxColumn = k;
		for (int r = k; r < n; r++){
			for (int s = k; s < n; s++){
				if (Math.abs(Ab[r][s]) > max){
					max = Math.abs(Ab[r][s]);
					maxRow = r;
					maxColumn = s;
				}
			}
		}
		if(max == 0) {
			// El sistema tiene infinitas soluciones. 
			return null;
		}else{ 
			if(maxRow != k){
				Ab = Matrix.changeRows(Ab, maxRow, k);
			}
			if(maxColumn != k) {
				Ab = Matrix.changeColumns(Ab, maxColumn, k);
				marks = Matrix.changeMarks(marks, maxColumn, k);
			}
			return Ab;
		}		
	}
	
	public void orderCoefficients(double[] x){
		boolean allSolutionsFound = false;
		for(int i = 0 , j = 0; i < marks.length && !allSolutionsFound; i++){
		      if(marks[i] == j+1){
		           coefficients.add(j, x[i]);
		           if(j < marks.length-1){
		               i = -1;
		               j++;
		            }
		            else{
		              allSolutionsFound = true;
		            }
		      } 
		}
	}
	
	public void buildPiecewisePolynomial(int totalEqs){
		polynomial = "";
		for(int i = 0; i < (copiedXnValues.size() - 1); i++){
			int pos = (i * 4);
			String individualPolynomial = "";
			individualPolynomial += String.valueOf(coefficients.get(pos)) + "*(x^3) + " 
									+  String.valueOf(coefficients.get(pos+1)) + "*(x^2) + "
									+  String.valueOf(coefficients.get(pos+2)) + "*(x) + "
									+  String.valueOf(coefficients.get(pos+3));
			individualPolynomials.add(individualPolynomial);  //Se agrega sólo la función.
			
			//Luego se le adicionan los límites.
			String interval = "";
			interval = "&nbsp;&nbsp;&nbsp;&nbsp;<strong>" + String.valueOf(copiedXnValues.get(i)) 
						+  " &lt;= x &lt;= " + String.valueOf(copiedXnValues.get(i+1)) + "</strong>";
			individualPolynomial += interval;

			polynomial += individualPolynomial + "<br><br>";	
		}
	}
	
	public void evaluateWithX(View v){
		if(!etXValue.getText().toString().isEmpty()){
			if(isValuesFormatOK(etXValue.getText().toString())){
				x = Double.parseDouble(etXValue.getText().toString());
				if(x >= copiedXnValues.get(0) && x <= copiedXnValues.get(copiedXnValues.size()-1)){
					String methodResultString = "The result of Cubic Spline' s piecewise polynomial" 
												+ " evaluated in x = " + String.valueOf(x) + " is " 
												+ "p(" + String.valueOf(x) +") = ";
					methodLabel.setText(methodResultString);
					for(int i = 0; i < (copiedXnValues.size() - 1); i++){
						if(x >= copiedXnValues.get(i) && x <= copiedXnValues.get(i+1)){
							fEvaluator = new FunctionsEvaluator(individualPolynomials.get(i), "", "", "");
							fEvaluator.buildFunctions(individualPolynomials.get(i), 0);  
																	  //La función construida quedaría 
																	 //en la primera posición del arreglo 
																	//en FunctionsEvaluator.
						}
					}
					double result = fEvaluator.f(x);
					methodResult.setText(String.valueOf(result));
					methodResult.setTextAppearance(getApplicationContext(), 
											   	   android.R.style.TextAppearance_Large);
					methodResult.setTextColor(Color.BLACK);
					isAppOnEvalView = true;
					invalidateOptionsMenu();
				}else{
					Toast.makeText(getApplicationContext(), getResources()
													   .getString(R.string.value_not_between_interval)
													   + " [" + copiedXnValues.get(0) + ", " +
													   copiedXnValues.get(copiedXnValues.size()-1) + "]", 
													   Toast.LENGTH_LONG).show();
				}
			}else{
				Toast.makeText(getApplicationContext(), getResources()
			           	   	   .getString(R.string.entered_values_format_not_valid), 
			           	   	   Toast.LENGTH_SHORT).show();
			}
		}else{
			Toast.makeText(getApplicationContext(), getResources()
 			       		   .getString(R.string.fill_in_all_fields), 
 			       		   Toast.LENGTH_SHORT).show();
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

	
	public ArrayList<Double> cloneTable(ArrayList<Double> source) {
		ArrayList<Double> copiedXnValues = new ArrayList<Double>();
		for(int i = 0; i < source.size(); i++){
			copiedXnValues.add(source.get(i));
		}
		return copiedXnValues;
	}
	
	public double [][]cloneMatrix(double [][] source) {
		double [][] copiedInterpolationMatrix = new double [source.length][source.length+1];
		for(int i = 0; i < source.length; i++){
			for(int j = 0; j < (source.length + 1); j++){
				copiedInterpolationMatrix[i][j] = interpolationMatrix[i][j];
			}
		}
		return copiedInterpolationMatrix;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
		Intent openSelectedItem = new Intent(CubicSpline.this, GuideMenu.class);
		Bundle methodToHelp = new Bundle();
		methodToHelp.putInt("methodNameId", R.string.cubic_spline);
	    switch (item.getItemId()) {
	    	case R.id.evaluatePolynomial:
	    		xValueLabel.setVisibility(View.VISIBLE);
	    		etXValue.setVisibility(View.VISIBLE);
	    		evaluate.setVisibility(View.VISIBLE);
	    		methodLabel.setText("");
	    		methodResult.setText("");
	    		isAppOnEvalView = true;
	    		isAppOnEnterXView = true;
	    		invalidateOptionsMenu();
	    		return true;
	    	case R.id.stagesTable:
	    		new InterpolationTables(equations, true); //El booleano se usa sólo para indicar
	    												 //que se trata de acceder a un constructor
	    												//distinto.
	    		methodToHelp.putInt("actionId", R.string.stages_table);
	    		break;
	    	case R.id.help:
	        	methodToHelp.putInt("actionId", R.string.help);
	        	break;
	        case android.R.id.home:
	        	if(!isAppOnEvalView){
	        		finish();
	        	}else{
	        		methodLabel.setText(getResources()
	        					.getString(R.string.cubic_spline_polynomial));
	        		methodResult.setText(Html.fromHtml(polynomial), TextView.BufferType.SPANNABLE);
    	    		methodResult.setTextAppearance(getApplicationContext(), 
							   					   android.R.style.TextAppearance_Small);
    	    		methodResult.setTextColor(Color.BLACK);
    	    		xValueLabel.setVisibility(View.GONE);
    	    		etXValue.setVisibility(View.GONE);
    	    		evaluate.setVisibility(View.GONE);
    	    		isAppOnEvalView = false;
    	    		isAppOnEnterXView = false;
    	    		invalidateOptionsMenu();
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
			if (!isAppOnEvalView){
				if(!isAppOnEnterXView){
					menu.add(0, R.id.evaluatePolynomial, Menu.FIRST, getResources()
					        									.getString(R.string.evaluate_polynomial));
				}
				menu.add(0, R.id.stagesTable, Menu.FIRST+1, getResources()
														.getString(R.string.result_eqs_interpolation));
				menu.add(0, R.id.help, Menu.FIRST+2, getResources().getString(R.string.help));
			}
		}

		//Para la ActionBar.
		getMenuInflater().inflate(R.menu.cubic_spline, menu);
		if(!isAppOnEvalView){
			if(!isAppOnEnterXView){
				menu.findItem(R.id.evaluatePolynomial).setVisible(true);
			}else{
				menu.findItem(R.id.evaluatePolynomial).setVisible(false);
			}
			menu.findItem(R.id.stagesTable).setVisible(true);
			menu.findItem(R.id.help).setVisible(true);
		}else{
			menu.findItem(R.id.evaluatePolynomial).setVisible(false);
			menu.findItem(R.id.stagesTable).setVisible(false);
			menu.findItem(R.id.help).setVisible(false);
		}
		
		return true;
	}
	
}

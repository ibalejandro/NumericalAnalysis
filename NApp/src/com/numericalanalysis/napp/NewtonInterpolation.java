package com.numericalanalysis.napp;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class NewtonInterpolation extends Activity {

	private FunctionsEvaluator fEvaluator;
	private TextView methodLabel;
	private TextView methodResult;
	private TextView xValueLabel;
	private EditText etXValue;
	private Button evaluate;
	// Valor para evaluar el polinomio respuesta.
	private double x;
	private ArrayList<Double> xN = new ArrayList<Double>();
	private ArrayList<Double> fxN = new ArrayList<Double>();
	private ArrayList<Double> b = new ArrayList<Double>();
	private ArrayList<String> terms = new ArrayList<String>();
	private String polynomial; 
	private boolean isAppOnEvalView = false;
	private boolean isAppOnEnterXView = false;
	//Para la tabla por etapas de las diferencias divididas.
	private ArrayList<ArrayList<Double>> divDifs = new ArrayList<ArrayList<Double>>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_newton_interpolation);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		initViewElements();
	}
	
	public void initViewElements(){
		methodLabel = (TextView) findViewById(R.id.newton_interpolation_method_result_label);
		methodResult = (TextView) findViewById(R.id.newton_interpolation_method_result);
		xValueLabel = (TextView) findViewById(R.id.newton_interpolation_evaluate_value_label);
		etXValue = (EditText) findViewById(R.id.newton_interpolation_evaluate_value);
		evaluate = (Button) findViewById(R.id.newton_interpolation_evaluate);
		xValueLabel.setVisibility(View.GONE);
		etXValue.setVisibility(View.GONE);
		evaluate.setVisibility(View.GONE);
		isAppOnEvalView = false;
		isAppOnEnterXView = false;
		invalidateOptionsMenu();
		calculateWithNewtonInterpolation();
	}
	
	public void calculateWithNewtonInterpolation(){
		xN = InterpolationTables.getxNValues();
		fxN = cloneTable(InterpolationTables.getFXnValues());
		b.clear();
		polynomial = "";
		terms.clear();
		b.add(fxN.get(0));
		terms.add(String.valueOf(fxN.get(0)));
		ArrayList<Double> dividedDifferencesColumn = new ArrayList<Double>();
		// Calcula el polinomio con el numero de diferencias divididas, que equivale
		// a la longitud de Xn ingresado por el usuario.
		for(int i = 1; i < xN.size(); i++){
			dividedDifferencesColumn.clear();
			for(int j = 0; j < fxN.size()-1; j++){
				double divideDifferencesElement = (fxN.get(j+1) - fxN.get(j))/ (xN.get(j+i) - xN.get(j));
				dividedDifferencesColumn.add(divideDifferencesElement);
			}
			divDifs.add(cloneTable(dividedDifferencesColumn));
			double currentB = dividedDifferencesColumn.get(0);
			b.add(currentB);
			terms.add(buildTerm(currentB, i));
			// Se actualiza la columna Fxn para la siguiente diferencia dividida.
			fxN.clear();
			fxN = cloneTable(dividedDifferencesColumn);
		}
		new InterpolationTables(divDifs);
		buildPolynomial();
		methodResult.setText(polynomial);
	}
	
	public String buildTerm(double b, int divDif){
		// Formar el término del polínomio. 
		String termWithOutB = "";
		for(int i = 0; i < divDif; i++){
			termWithOutB += "(x - " + xN.get(i) +")*";
			
		}
		String cutTerm = termWithOutB.substring(0, termWithOutB.length()-1);
		String term = String.valueOf(b) + "*" + cutTerm;
		return term;
	}
	
	public void buildPolynomial(){
		for(int i = 0; i < terms.size()-1; i++){
			polynomial += terms.get(i) + " + ";
		}
		polynomial += terms.get(terms.size()-1);		
	}
	
	public ArrayList<Double> cloneTable(ArrayList<Double> source) {
		ArrayList<Double> copiedFXnValues = new ArrayList<Double>();
		for(int i = 0; i < source.size(); i++){
			copiedFXnValues.add(source.get(i));
		}
		return copiedFXnValues;
	}
	
	public void evaluateWithX(View v){
		if(!etXValue.getText().toString().isEmpty()){
			if(isValuesFormatOK(etXValue.getText().toString())){
				x = Double.parseDouble(etXValue.getText().toString());
				String methodResultString = "The result of Newton's divided differences interpolating polynomial" 
											+ " evaluated in x = " + String.valueOf(x) + " is " 
											+ "p(" + String.valueOf(x) +") = ";
				methodLabel.setText(methodResultString);
				fEvaluator = new FunctionsEvaluator(polynomial, "", "", "");
				fEvaluator.buildFunctions(polynomial, 0);  //La función construida quedaría en la primera 
													  //posición del arreglo en FunctionsEvaluator.
				double result = fEvaluator.f(x);
				methodResult.setText(String.valueOf(result));
				methodResult.setTextAppearance(getApplicationContext(), 
										   android.R.style.TextAppearance_Large);
				methodResult.setTextColor(Color.BLACK);
				isAppOnEvalView = true;
				invalidateOptionsMenu();
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

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
		Intent openSelectedItem = new Intent(NewtonInterpolation.this, GuideMenu.class);
		Bundle methodToHelp = new Bundle();
		methodToHelp.putInt("methodNameId", R.string.newton_interpolation);
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
	        					.getString(R.string.newton_divided_differences_interpolation_polynomial));
    	    		methodResult.setText(polynomial);
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
															.getString(R.string.stages_table));
				menu.add(0, R.id.help, Menu.FIRST+2, getResources().getString(R.string.help));
			}
		}

		//Para la ActionBar.
		getMenuInflater().inflate(R.menu.newton_interpolation, menu);
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

package com.numericalanalysis.napp;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LagrangeInterpolation extends Activity {

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
	private ArrayList<String> l = new ArrayList<String>();
	private ArrayList<String> terms = new ArrayList<String>();
	private String polynomial; 
	private boolean isAppOnEvalView = false;
	private boolean isAppOnEnterXView = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lagrange_interpolation);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		initViewElements();
	}
	
	public void initViewElements(){
		methodLabel = (TextView) findViewById(R.id.lagrange_interpolation_method_result_label);
		methodResult = (TextView) findViewById(R.id.lagrange_interpolation_method_result);
		xValueLabel = (TextView) findViewById(R.id.lagrange_interpolation_evaluate_value_label);
		etXValue = (EditText) findViewById(R.id.lagrange_interpolation_evaluate_value);
		evaluate = (Button) findViewById(R.id.lagrange_interpolation_evaluate);
		xValueLabel.setVisibility(View.GONE);
		etXValue.setVisibility(View.GONE);
		evaluate.setVisibility(View.GONE);
		isAppOnEvalView = false;
		isAppOnEnterXView = false;
		invalidateOptionsMenu();
		calculateWithLagrangeInterpolation();
	}
	
	public void calculateWithLagrangeInterpolation(){
		xN = InterpolationTables.getxNValues();
		fxN = cloneTable(InterpolationTables.getFXnValues());
		l.clear();
		polynomial = "";
		terms.clear();
		// Calcula el polinomio con el numero de xn ingresados por el usuario.
		for(int i = 0; i < xN.size(); i++){
			String termNumerator = "";
			double termDenominator = 1;
			for(int j = 0; j < xN.size(); j++){
				if(j != i){
					termNumerator += "(x - " + xN.get(j) + ")*";
					termDenominator *= (xN.get(i) - xN.get(j)); 
				}	
			}
			String currentL = buildL(termNumerator, termDenominator);
			l.add(currentL);
			terms.add(buildTerm(currentL, i));
			// Se actualiza la columna Fxn para la siguiente diferencia dividida.
		}
		buildPolynomial();
		methodResult.setText(polynomial);
	}
	
	public String buildL(String termNumerator, double termDenominator){
		// Al final de termNumerator hay un * sobrante.
		String cutTermNumerator = termNumerator.substring(0, termNumerator.length()-1);
		String l = "((" + cutTermNumerator + ")/" + String.valueOf(termDenominator)+ ")";
		return l;
	}
	
	public String buildTerm(String l, int i){
		String term = String.valueOf(fxN.get(i)) + "*" + l;
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
				String methodResultString = "The result of Lagrange' s polynomial" 
											+ " evaluated in x = " + String.valueOf(x) + " is " 
											+ "\n p(" + String.valueOf(x) +") = ";
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
		Intent openSelectedItem = new Intent(LagrangeInterpolation.this, GuideMenu.class);
		Bundle methodToHelp = new Bundle();
		methodToHelp.putInt("methodNameId", R.string.lagrange_interpolation);
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
	    	case R.id.help:
	        	methodToHelp.putInt("actionId", R.string.help);
	        	break;
	        case android.R.id.home:
	        	if(!isAppOnEvalView){
	        		finish();
	        	}else{
	        		methodLabel.setText(getResources()
	        					.getString(R.string.lagrange_polynomial));
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
				menu.add(0, R.id.help, Menu.FIRST+1, getResources().getString(R.string.help));
			}
		}

		//Para la ActionBar.
		getMenuInflater().inflate(R.menu.lagrange_interpolation, menu);
		if(!isAppOnEvalView){
			if(!isAppOnEnterXView){
				menu.findItem(R.id.evaluatePolynomial).setVisible(true);
			}else{
				menu.findItem(R.id.evaluatePolynomial).setVisible(false);
			}
			menu.findItem(R.id.help).setVisible(true);
		}else{
			menu.findItem(R.id.evaluatePolynomial).setVisible(false);
			menu.findItem(R.id.help).setVisible(false);
		}
		
		return true;
	}
}
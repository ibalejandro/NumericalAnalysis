package com.numericalanalysis.napp;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class GaussianElimination extends Activity {
	
	private RadioButton withoutPivoting;
	private RadioButton partialPivoting;
	private RadioButton totalPivoting;
	private RadioButton scaledPartialPivoting;
	private RelativeLayout gaussianEliminationView;
	private RelativeLayout resultView;
	private TextView pivotingUsed;
	private TextView methodResult;
	private int[] marks;
	private double[] maxValuesRows;
	private String pivotingType = "without"; //Por defecto se va a calcular sin pivote.
	private String pivotingUsedString = "None";
	private int pivotingTypeID = R.string.without_pivoting;
	//private Matrix matrix;
	private ArrayList<String> resultMap = new ArrayList<String>();
	//Se hace para saber qu� opciones mostra en el men� y cu�les esconder.
	private boolean isAppOnMethodView = true;
	//Para almacenar la matriz tras cada etapa.
	private double [] [][] stageByStageMatrices;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gaussian_elimination);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		//matrix = (Matrix) getIntent().getParcelableExtra("matrix");
		initViewElements();
	}
	
	public void initViewElements(){
		gaussianEliminationView = (RelativeLayout) findViewById(R.id.gaussian_elimination_view);
		withoutPivoting = (RadioButton) findViewById(R.id.gaussian_elimination_without_pivoting);
		withoutPivoting.setChecked(true);  //Por defecto se hara el calculo sin pivoteo.
		partialPivoting = (RadioButton) findViewById(R.id.gaussian_elimination_partial_pivoting);
		totalPivoting = (RadioButton) findViewById(R.id.gaussian_elimination_total_pivoting);
		scaledPartialPivoting = (RadioButton) findViewById(R.id.gaussian_elimination_scaled_partial_pivoting);
		resultView = (RelativeLayout) findViewById(R.id.gaussian_elimination_result_view);
		pivotingUsed = (TextView) findViewById(R.id.gaussian_elimiantion_pivoting_used);
		methodResult = (TextView) findViewById(R.id.gaussian_elimination_method_result);
		resultView.setVisibility(View.GONE);
		isAppOnMethodView = true;
		invalidateOptionsMenu();
		gaussianEliminationView.setVisibility(View.VISIBLE);
	}
	
	public void gaussianElimination(View v){
		resultMap.clear();
		resultMap = calculateWithGaussianElimination(Matrix.getMatrixAb(), Matrix.getMatrixAb().length);
		String unknowns = "";
		if(resultMap.get(0).equals("1")){  //Existe una respuesta.
			for(int i = 1; i < resultMap.size(); i++){
				unknowns += "X" + i + " = " + resultMap.get(i) + "\n";
			}
		}else{  //Infinitas soluciones.
			unknowns = getResources().getString(R.string.inf_solutions);
		}
		gaussianEliminationView.setVisibility(View.GONE);
		isAppOnMethodView = false;
		invalidateOptionsMenu();
		resultView.setVisibility(View.VISIBLE);
		pivotingUsed.setText(pivotingUsedString);
		methodResult.setText(unknowns);
	}
	
	public ArrayList<String> calculateWithGaussianElimination(double[][] matrixAb, int n){
		ArrayList<String> result = new ArrayList<String>();
		marks = Matrix.fillMarks(n);     //Se pone para ejecutar el algoritmo de convertToArrayList, pero 
		                                //solo influye con pivoteo total.
		double[] x = new double[n];
		double[][] matrixUb = new double[n][n+1];
		// Aplicar eliminacion a la matrix Ab
		matrixUb = elimination(matrixAb, n);
		
		if(matrixUb != null){
			// Aplicar sustitucion regresiva a la matriz resultante de la eliminacion
			x = Matrix.regressiveSubstitutionElimination(matrixUb, n);
			// Convertir x en ArrayList<String>
			result = convertToArrayList(x);
			result.add(0,"1");
			// Retornar ArrayList
			return result;
		}else{
			result.add("0");
			return result;
		}
	}
	
	public double[][] cloneMatrix(double [][] source) {
		double [][] result = new double [source.length][source[0].length];
		for (int i = 0; i < source.length; i++) {
			for (int j = 0; j < source[0].length; j++) {
				result[i][j] = source[i][j];
			}
		}
		return result;
	}
	
	public double[][] elimination(double[][] Ab, int n){
		stageByStageMatrices = new double [n] [n][n+1];
		double[][] result = Ab;
		//Se clona la matriz.
		stageByStageMatrices[0] = cloneMatrix(result); //Aquí se almacena la primera matriz sin modificar.
		if(pivotingType.equals("scaledPartial")){
			maxValuesRows = Matrix.calculateMaxValuesRows(Ab, n);
		}
		for (int k = 0; k <= n - 2; k++){
			Log.i("parcial", "-------------------");
			Log.i("parcial", "Etapa " + (k+1) + ":");
			if(!pivotingType.equals("without")){
				result = makePivoting(result, n, k);
				if(result == null) return null;
			}
			Log.i("parcial", "Multiplicadores columna " + (k+1) + ":");
			for (int i = k+1; i < n; i++){
				if(result[k][k] != 0){
					double mult = result[i][k] / result[k][k];
					
					Log.i("parcial", "Multiplicador " + (i+1) + (k+1) + " = " + String.valueOf(mult));
					for (int j = k; j < n+1; j++) {
						result[i][j] = result[i][j] - (mult*result[k][j]);
					}
				}else{
					// El sistema tiene soluciones infinitas.
					return null;
				}	
			}
		
			stageByStageMatrices[k+1] = cloneMatrix(result);  //Se va almacenando cada matriz tras la eliminación.
		}
		if(result[n-1][n-1] == 0){
			return null;
		}else{
			return result;
		}
	}
	
	public double[][] makePivoting(double[][] Ab, int n, int k){
		double[][] result = Ab;
		if(pivotingType.equals("partial")){
			result = calculatePartialPivoting(Ab,n,k);
			return result;
		}else if(pivotingType.equals("total")){
			result = calculateTotalPivoting(Ab,n,k);
			Log.i("parcial", "Arreglo de marcas para empezar esta etapa:");
			String markis = "";
			for(int i = 0; i < marks.length; i++){
				markis += String.valueOf(marks[i]) + ", ";
			}
			Log.i("parcial", markis);
			return result;
		}else{
			result = calculateScaledPartialPivoting(Ab,n,k);
			Log.i("parcial", "Arreglo de valores máximos para empezar esta etapa:");
			String maxValues = "";
			for(int i = 0; i < maxValuesRows.length; i++){
				maxValues += String.valueOf(maxValuesRows[i]) + ", ";
			}
			Log.i("parcial", maxValues);
			return result; 	
		}
		
	}
	
	public double[][] calculatePartialPivoting(double[][]Ab, int n, int k){
	    double higher = Ab[k][k];
	    int maxRow = k;
	    for (int s = k+1; s < n; s++){
	    	if (Math.abs(Ab[s][k]) > higher){
	    		higher = Math.abs(Ab[s][k]);
	    		maxRow = s;
	    	}
	    }
	    if (higher == 0){
	    	return null;
	    }else{
	    	if (maxRow != k){
	    		Ab = Matrix.changeRows(Ab, maxRow, k);
	    	}
	            return Ab;
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
	
	public double[][] calculateScaledPartialPivoting(double[][]Ab, int n, int k){
		int maxRow = k;
		double max = 0;
		for (int s = k; s < n; s++){ // Column
			for (int r = k; r < n; r++){  // Row
				if ( Math.abs(Ab[r][s]) / maxValuesRows[r] > max){
					max = Math.abs(Ab[r][s]) / maxValuesRows[r];
					maxRow = r;
				}
			}
		}
		if(max == 0){ 
			// El sistema tiene infinitas soluciones. 
			return null;
		}else{ 
			if(maxRow != k){
				Ab = Matrix.changeRows(Ab, maxRow, k);
				maxValuesRows = Matrix.changeMaxValuesRows(maxValuesRows, maxRow, k);
			}
			return Ab;
		}
	}
	
	public ArrayList<String> convertToArrayList(double[] x){
		ArrayList<String> result = new ArrayList<String>();
		boolean allSolutionsFound = false;
		for(int i = 0 , j = 0; i < marks.length && !allSolutionsFound; i++){
		      if(marks[i] == j+1){
		           result.add(j, String.valueOf(x[i]));
		           if(j < marks.length-1){
		               i = -1;
		               j++;
		            }
		            else{
		              allSolutionsFound = true;
		            }
		      }
		       
		}
		return result;
	}

	public void selectPivotingType(View v){
		switch(v.getId()) {
        case R.id.gaussian_elimination_without_pivoting:
        	partialPivoting.setChecked(false);
        	totalPivoting.setChecked(false);
        	scaledPartialPivoting.setChecked(false);
        	pivotingType = "without";
        	pivotingUsedString = "None";
        	pivotingTypeID = R.string.without_pivoting;
            break;
        case R.id.gaussian_elimination_partial_pivoting:
        	withoutPivoting.setChecked(false);
        	totalPivoting.setChecked(false);
        	scaledPartialPivoting.setChecked(false);
        	pivotingType = "partial";
        	pivotingUsedString = "Partial";
        	pivotingTypeID = R.string.partial_pivoting;
            break;
        case R.id.gaussian_elimination_total_pivoting:
        	withoutPivoting.setChecked(false);
        	partialPivoting.setChecked(false);
        	scaledPartialPivoting.setChecked(false);
        	pivotingType = "total";
        	pivotingUsedString = "Total";
        	pivotingTypeID = R.string.total_pivoting;
            break;
        case R.id.gaussian_elimination_scaled_partial_pivoting:
        	withoutPivoting.setChecked(false);
        	totalPivoting.setChecked(false);
        	partialPivoting.setChecked(false);
        	pivotingType = "scaledPartial";
        	pivotingUsedString = "Scaled Partial";
        	pivotingTypeID = R.string.scaled_partial_pivoting;
            break;
		}
	}
	
	public void returnToMethod(View v){
		resultView.setVisibility(View.GONE);
		isAppOnMethodView = true;
		invalidateOptionsMenu();
		gaussianEliminationView.setVisibility(View.VISIBLE);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
		Intent openSelectedItem = new Intent(GaussianElimination.this, GuideMenu.class);
		Bundle methodToHelp = new Bundle();
		methodToHelp.putInt("methodNameId", pivotingTypeID);
	    switch (item.getItemId()) {
	    	case R.id.stagesTable:
	    		new Matrix(stageByStageMatrices, "Gaussian");   //Creación de la matriz etapa por etapa. 
	    		methodToHelp.putInt("actionId", R.string.stages_table);
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
				menu.add(0, R.id.stagesTable, Menu.FIRST, getResources().getString(R.string.stages_table));
			}
		}

		//Para la ActionBar.
		getMenuInflater().inflate(R.menu.gaussian_elimination, menu);
		if (isAppOnMethodView){
			menu.findItem(R.id.stagesTable).setVisible(false);
			menu.findItem(R.id.help).setVisible(true);
			menu.findItem(R.id.seeExample).setVisible(true);
		}else{
			menu.findItem(R.id.stagesTable).setVisible(true);
			menu.findItem(R.id.help).setVisible(false);
			menu.findItem(R.id.seeExample).setVisible(false);
		}
		
		return true;
	}

}

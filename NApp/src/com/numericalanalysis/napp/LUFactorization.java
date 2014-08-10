package com.numericalanalysis.napp;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ActionBar.LayoutParams;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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


public class LUFactorization extends Activity {
	
	private RelativeLayout luFactorizationView;
	private RadioButton rCrout;
	private RadioButton rDoolittle;
	private RadioButton rCholesky;
	private TableLayout newVectorB;
	private RelativeLayout resultView;
	private TextView methodLabel;
	private TextView methodUsed;
	private TextView methodResult;
	private String methodType = "crout"; 
	private String methodUsedString = "Crout";
	private int methodUsedID = R.string.crout;
	//private Matrix matrix;
	private ArrayList<String> resultMap = new ArrayList<String>();
	private boolean negNumbSqrt = false;   //Para indicar si se trató de hallar la raíz de un número
										  //negativo.
	//Se hace para saber qu� opciones mostra en el men� y cu�les esconder.
	private boolean isAppOnMethodView = true;
	private boolean isAppShowingDet = false;
	private boolean isAppShowingNewVectorB = false;
	private String unknowns;
	private double determinant;
	private double [] newVectorBValues;
	private double [] [][] stageByStageMatricesL;
	private double [] [][] stageByStageMatricesU;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lufactorization);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		//matrix = (Matrix) getIntent().getParcelableExtra("matrix");
		initViewElements();
	}
	
	public void initViewElements(){
		luFactorizationView = (RelativeLayout) findViewById(R.id.luFactorization_view);
		rCrout = (RadioButton) findViewById(R.id.lu_factorization_crout_method);
		rDoolittle = (RadioButton) findViewById(R.id.lu_factorization_doolittle_method);
		rCholesky = (RadioButton) findViewById(R.id.lu_factorization_cholesky_method);
		newVectorB = (TableLayout) findViewById(R.id.newVectorB);
		resultView = (RelativeLayout) findViewById(R.id.luFactorization_result_view);
		methodLabel = (TextView) findViewById(R.id.lu_factorization_method_label);
		methodUsed = (TextView) findViewById(R.id.lu_factorization_method_used);
		methodResult = (TextView) findViewById(R.id.luFactorization_method_result);
		resultView.setVisibility(View.GONE);
		isAppOnMethodView = true;
		invalidateOptionsMenu();
		luFactorizationView.setVisibility(View.VISIBLE);
		newVectorB.setVisibility(View.GONE);
	}
	
	public void luFactorization(View v){
		resultMap.clear();
		if(isAppShowingNewVectorB){  //Se quiere cambiar el vector b.
			if(isNewVectorBOK()){
				Matrix.setNewVectorB(newVectorBValues);
				resultMap = calculateWithLuFactorization(Matrix.getMatrixA(), Matrix.getNewVectorB(), 
        		    		Matrix.getMatrixA().length);
			}else{
				return;
			}
		}else{  //Se ejecuta el proceso común y corriente.
			resultMap = calculateWithLuFactorization(Matrix.getMatrixA(), Matrix.getVectorB(), 
                    	Matrix.getMatrixA().length); 
		}
		unknowns = "";
		if(resultMap.get(0).equals("1")){  //Existe una respuesta.
			for(int i = 1; i < resultMap.size(); i++){
				unknowns += "X" + i + " = " + resultMap.get(i) + "\n";
			}
		}else{  //Infinitas soluciones o problemas de raíces de números negativos.
			if(!negNumbSqrt){    //Calculando con Cholesky se trató de hallar la raíz de un número 
							    //negativo
				unknowns = getResources().getString(R.string.inf_solutions);
			}else{
				unknowns = getResources().getString(R.string.impossible_to_solve);
			}
		}
		luFactorizationView.setVisibility(View.GONE);
		isAppOnMethodView = false;
		invalidateOptionsMenu();
		resultView.setVisibility(View.VISIBLE);
		methodUsed.setText(methodUsedString);
		methodResult.setText(unknowns);
	}
	
	public ArrayList<String> calculateWithLuFactorization(double [][] A, double[] b, int n){
		ArrayList<String> result = new ArrayList<String>();
	    double [][] L = new double[n][n];
	    double [][] U = new double[n][n];
	    double [] [][] LU = new double [2] [n][n];  //Arreglo que va a tener en cada posición una matriz.
	                                               //Una posición para L, una posición para U.
	    LU = composeLU(A, n);
	    if(LU == null){
	    	result.add("0");
	    	determinant = 0;
	    }else{
	    	L = LU[0];
		    U = LU[1];
		    double determinantL = 1;
		    double determinantU = 1;
		    for(int i = 0; i < L.length; i++){
		    	determinantL *= L[i][i];
		    	determinantU *= U[i][i];
		    }
		    determinant = determinantL*determinantU;
		    calculateInverse(L, U, n);
		    double [] z = new double[n];
		    z = Matrix.progressiveSubstitution(L, b, n);
		    Log.i("parcial", "-----------");
		    Log.i("parcial", "Este es el vector Z tras la sustitución progresiva");
		    String row = "";
		    for(int i = 0; i < z.length; i++){
		    	row += String.valueOf(z[i]) + ", ";
		    }
		    Log.i("parcial", row);
		    double [] x = new double[n];
		    x = Matrix.regressiveSubstitutionLU(U, z, n);
		    result = convertToArrayList(x);
		    result.add(0,"1");
	    }
	    
	    return result;
	}

	public double[][] cloneMatrix(double [][] source) {
		double [][] result = new double [source.length][source.length];
		for (int i = 0; i < source.length; i++) {
			for (int j = 0; j < source.length; j++) {
				result[i][j] = source[i][j];
			}
		}
		return result;
	}
	
	public double [] [][] composeLU(double [][]A, int n){ 
		negNumbSqrt = false;
		stageByStageMatricesL = new double [n+1] [n][n];
		stageByStageMatricesU = new double [n+1] [n][n];
		//Antes de comenzar el algoritmo, se deben inicializar las matrices L y U de acuerdo con el
		//método elegido.
	    double [][] L = new double[n][n];
	    double [][] U = new double[n][n];
	    stageByStageMatricesL[0] = cloneMatrix(L);
	    stageByStageMatricesU[0] = cloneMatrix(U);
	    if(methodType.equals("crout")){
	    	U = cloneMatrix(Matrix.getIdentityMatrix());	
	    	stageByStageMatricesU[0] = cloneMatrix(U);
	    }else if(methodType.equals("doolittle")){
	    	L = cloneMatrix(Matrix.getIdentityMatrix());
	    	stageByStageMatricesL[0] = cloneMatrix(L);
	    }
	    for (int k = 0; k < n; k++){
	       double acumP = 0;
	       for (int p = 0; p <= k-1; p++){
	              acumP = acumP + (L[k][p]*U[p][k]);
	       }
	       if(methodType.equals("crout")){
	    	   L[k][k] = (A[k][k] - acumP)/U[k][k];
	    	   if(L[k][k] == 0){
	    		   return null; 
	    	   }
	       }else if(methodType.equals("doolittle")){
	    	   U[k][k] = (A[k][k] - acumP)/L[k][k];
	    	   if(U[k][k] == 0){
	    		   return null; 
	    	   }
	       }else{
	    	   if((A[k][k] - acumP) >= 0){   
	    		   L[k][k] = Math.sqrt(A[k][k] - acumP);
			       U[k][k] = L[k][k];
	    	   }else{      //Raíz de un número negativo.
	    		   negNumbSqrt = true;
	    		   return null;
	    	   }
		       if(L[k][k] == 0){
		    	   return null; 
		       }
	       }
	      
	       for (int i = k+1; i < n; i++){
	    	   double acumR = 0;
	    	   for (int r = 0; r <= k-1; r++){
	                    acumR = acumR + (L[i][r]*U[r][k]);
	           }
	    	   if(methodType.equals("crout")){
	    		   L[i][k] = (A[i][k] - acumR)/U[k][k];   //En estye caso U[k][k] == 1.
	  	       }else if(methodType.equals("doolittle")){
	  	    	   L[i][k] = (A[i][k] - acumR)/U[k][k]; 
	  	       }else{
	  	    	   L[i][k] = (A[i][k] - acumR)/L[k][k]; 
	  	       }
	       }
	       
	       for (int j = k+1; j < n; j++){
	    	   double acumS = 0;
	             for (int s = 0; s <= k-1; s++){
	                    acumS = acumS + (L[k][s]*U[s][j]);
	             }
	             if(methodType.equals("crout")){
	            	 U[k][j] = (A[k][j] - acumS)/L[k][k];  
		  	     }else if(methodType.equals("doolittle")){
		  	    	 U[k][j] = (A[k][j] - acumS)/L[k][k];   //En estye caso L[k][k] == 1.
		  	     }else{
		  	    	 U[k][j] = (A[k][j] - acumS)/L[k][k];
		  	     } 
	       }
	       stageByStageMatricesL[k+1] = cloneMatrix(L);
	       stageByStageMatricesU[k+1] = cloneMatrix(U);
	       Log.i("parcial", "--------------");
	       Log.i("parcial", "Matriz L en la etapa " + (k+1) + ":");
	       for(int e = 0; e < L.length; e++){
	    	   String row = "";
	    	   for(int f = 0; f < L.length; f++){
	    		   row += String.valueOf(L[e][f]) + ", ";
	    	   }
	    	   Log.i("parcial", row);
	       }
	       Log.i("parcial", "Matriz U en la etapa " + (k+1) + ":");
	       for(int e = 0; e < U.length; e++){
	    	   String row = "";
	    	   for(int f = 0; f < U.length; f++){
	    		   row += String.valueOf(U[e][f]) + ", ";
	    	   }
	    	   Log.i("parcial", row);
	       }
	   }
	   double [] [][] LU = new double [2] [n][n];
	   LU[0] = L;
	   LU[1] = U;
	   return LU;
	}
	
	public ArrayList<String> convertToArrayList(double[] x){
		ArrayList<String> result = new ArrayList<String>();
		for(int i = 0; i < x.length; i++){
		      	result.add(String.valueOf(x[i]));
		}
		return result;
	}
	
	public void createNewVectorB(){
	    TableRow row = new TableRow(this);
	    for (int i = 0; i < Matrix.getVectorB().length; i++) {
            EditText cell = new EditText(this);
            cell.setHint("b[" + i + "][0]");
            cell.setText(String.valueOf(Matrix.getVectorB()[i]));
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
	    newVectorB.addView(row ,new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				  										  LayoutParams.WRAP_CONTENT));
	    newVectorB.setVisibility(View.VISIBLE);
	}
	
	public boolean isNewVectorBOK(){
		newVectorBValues = new double [Matrix.getVectorB().length];
		boolean isInfoValid = true;
		TableRow row = (TableRow) newVectorB.getChildAt(0);
		for(int j = 0; j < Matrix.getVectorB().length && isInfoValid; j++){
		    EditText position = (EditText) row.getChildAt(j);
		    String positionContent = position.getText().toString();
		    if(!positionContent.isEmpty()){
		    	if(isValuesFormatOK(positionContent)){ //La info. diligenciada está correcta y puede
		    										  //ser almacenada.
		    		newVectorBValues[j] = Double.parseDouble(positionContent);
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
		if(isInfoValid){
			return true;
		}else{
			return false;
		}
	}
	
	public void calculateInverse(double [][] L, double [][] U, int n){
		ArrayList<ArrayList<Double>> matInverse = new ArrayList<ArrayList<Double>>();
		for(int i = 0; i < n; i++){
			ArrayList<Double> inverseColumn = new ArrayList<Double>();
			
			double [] b = new double[n];
			for(int j = 0; j < n; j++){
				b[j] = Matrix.getIdentityMatrix()[i][j];
			}
			
			double [] z = new double[n];
		    z = Matrix.progressiveSubstitution(L, b, n);
		   
		    double [] x = new double[n];
		    x = Matrix.regressiveSubstitutionLU(U, z, n);
		    
		    for(int k = 0; k < x.length; k++){
		    	inverseColumn.add(x[k]);
		    }
		    
		    matInverse.add(inverseColumn);
		}
		
		new Matrix(matInverse, true); //Se hace para acceder al constructor indicado.
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

	public void selectMethod(View v){
		switch(v.getId()) {
        case R.id.lu_factorization_crout_method:
        	rDoolittle.setChecked(false);
        	rCholesky.setChecked(false);
        	methodType = "crout";
        	methodUsedString = "Crout";
        	methodUsedID = R.string.crout;
            break;
        case R.id.lu_factorization_doolittle_method:
        	rCrout.setChecked(false);
        	rCholesky.setChecked(false);
        	methodType = "doolittle";
        	methodUsedString = "Doolittle";
        	methodUsedID = R.string.doolittle;
            break;
        case R.id.lu_factorization_cholesky_method:
        	rCrout.setChecked(false);
        	rDoolittle.setChecked(false);
        	methodType = "cholesky";
        	methodUsedString = "Cholesky";
        	methodUsedID = R.string.cholesky;
            break;
		} 
	}
	
	public void returnToMethod(View v){
		resultView.setVisibility(View.GONE);
		isAppOnMethodView = true;
		invalidateOptionsMenu();
		luFactorizationView.setVisibility(View.VISIBLE);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
		Intent openSelectedItem = new Intent(LUFactorization.this, GuideMenu.class);
		Bundle methodToHelp = new Bundle();
		methodToHelp.putInt("methodNameId", methodUsedID);
	    switch (item.getItemId()) {
	    	case R.id.changeVectorB:
	    		createNewVectorB();
	    		newVectorB.setVisibility(View.VISIBLE);
	    		isAppShowingNewVectorB = true;
	    		invalidateOptionsMenu();
	    		return true;
	    	case R.id.determinant:  //No es necesario ir a GuideMenu. Sólo se quiere mostrar el 
	    						   //determinante de la matriz A.
	    		methodLabel.setText(getResources().getString(R.string.determinant) + ":");
	    		methodUsed.setVisibility(View.GONE);
	    		methodResult.setText(String.valueOf(determinant));
	    		methodResult.setTextAppearance(getApplicationContext(), 
	    									   android.R.style.TextAppearance_Large);
	    		methodResult.setTextColor(Color.BLACK);
	    		isAppShowingDet = true;
	    		invalidateOptionsMenu();
	    		return true;
	    	case R.id.matrixInverse:
	    		methodToHelp.putInt("methodNameId", R.string.mat_inverse);
	    		methodToHelp.putInt("actionId", R.string.stages_table);
	    		break;
	    	case R.id.stagesTable:
	    		new Matrix(stageByStageMatricesL, "L");   //Creación de la matriz etapa por etapa. 
	    		new Matrix(stageByStageMatricesU, "U");   //Creación de la matriz etapa por etapa. 
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
	        		if(isAppShowingDet){
	        			methodLabel.setText(getResources().getString(R.string.method));
	    	    		methodUsed.setVisibility(View.VISIBLE);
	    	    		methodResult.setText(unknowns);
	    	    		methodResult.setTextAppearance(getApplicationContext(), 
								   android.R.style.TextAppearance_Medium);
	    	    		methodResult.setTextColor(Color.BLACK);
	    	    		isAppShowingDet = false;
	    	    		invalidateOptionsMenu();
	        		}else{
	        			returnToMethod(null); //No hace falta enviar ning�n par�metro.
	        		}
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
				if(!isAppShowingNewVectorB){
					menu.add(0, R.id.changeVectorB, Menu.FIRST, getResources()
																.getString(R.string.change_vector_b));
				}
				menu.add(0, R.id.help, Menu.FIRST+4, getResources().getString(R.string.help));
		    	menu.add(0, R.id.seeExample, Menu.FIRST+5, getResources()
		    		     								   .getString(R.string.see_example));
			}else{
				if(!isAppShowingDet){
					menu.add(0, R.id.determinant, Menu.FIRST+1, getResources()
							 								  .getString(R.string.determinant));
				}
				menu.add(0, R.id.matrixInverse, Menu.FIRST+2, getResources()
															  .getString(R.string.calc_mat_inverse));
				menu.add(0, R.id.stagesTable, Menu.FIRST+3, getResources()
															.getString(R.string.stages_table));
			}
		}

		//Para la ActionBar.
		getMenuInflater().inflate(R.menu.lufactorization, menu);
		if (isAppOnMethodView){
			if(isAppShowingNewVectorB){
				menu.findItem(R.id.changeVectorB).setVisible(false);
			}else{
				menu.findItem(R.id.changeVectorB).setVisible(true);
			}
			menu.findItem(R.id.determinant).setVisible(false);
			menu.findItem(R.id.matrixInverse).setVisible(false);
			menu.findItem(R.id.stagesTable).setVisible(false);
			menu.findItem(R.id.help).setVisible(true);
			menu.findItem(R.id.seeExample).setVisible(true);
		}else{
			if(!isAppShowingDet){
				menu.findItem(R.id.determinant).setVisible(true);
			}else{
				menu.findItem(R.id.determinant).setVisible(false);
			}
			menu.findItem(R.id.changeVectorB).setVisible(false);
			menu.findItem(R.id.matrixInverse).setVisible(true);
			menu.findItem(R.id.stagesTable).setVisible(true);
			menu.findItem(R.id.help).setVisible(false);
			menu.findItem(R.id.seeExample).setVisible(false);
		}
		
		return true;
	}
	
}
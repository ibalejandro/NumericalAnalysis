package com.numericalanalysis.napp;

import java.util.ArrayList;
import android.os.Bundle;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ZoomControls;

public class GuideMenu extends Activity {

	private RelativeLayout gm_root_view;
	private ZoomControls zoomControls;
	private float zoomStatus = 1f;
	private RelativeLayout resultsTableView;
	private TextView tvResultsTableAction;
	private TextView tvResultsTableMethodName;
	private TableLayout tlResultsTable;
	private TextView tvStagesTableAction;
	private TextView tvStagesTableMethodName;
	private TableLayout stageMatrix;
	private TableLayout stageMatrixLU;
	private RadioButton stageL;
	private RadioButton stageU;
	private TextView tvEquation;
	private TextView previousStage;
	private TextView currentStage;
	private TextView nextStage;
	private RelativeLayout helpView;
	private TextView tvAction;
	private TextView tvMethodName;
	private TextView tvActionDevelopment;
	private RelativeLayout seeExampleView;
	private Bundle methodToHelp;
	private int methodNameId;
	private String methodName;
	private int actionId;
	private String action;
	private String actionDevelopment;
	private ArrayList<ArrayList<String>> tableColumns = new ArrayList<ArrayList<String>>();
	private ArrayList<String> columnsIdsStrings = new ArrayList<String>();
	private ArrayList<TextView> columnsIds = new ArrayList<TextView>();
	private ArrayList<ArrayList<TableRow>> stageByStageMatTables = new ArrayList<ArrayList<TableRow>>();
	private int currentStageNumber = 0;
	private ArrayList<ArrayList<TableRow>> stageByStageMatTablesL = new ArrayList<ArrayList<TableRow>>();
	private ArrayList<ArrayList<TableRow>> stageByStageMatTablesU = new ArrayList<ArrayList<TableRow>>();
	private int currentStageNumberLU = 0;
	private String lOrUStage = "L";
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if(getIntent().getExtras().getInt("actionId") == R.string.stages_table){
			if(getIntent().getExtras().getInt("methodNameId") != R.string.crout &&
			   getIntent().getExtras().getInt("methodNameId") != R.string.doolittle &&
			   getIntent().getExtras().getInt("methodNameId") != R.string.cholesky){
				if(getIntent().getExtras().getInt("methodNameId") != R.string.newton_interpolation){
					if(getIntent().getExtras().getInt("methodNameId") != R.string.cubic_spline){
						setContentView(R.layout.activity_guide_menu_slider);
					}else{
						setContentView(R.layout.activity_guide_menu_slider_cs);
					}
				}else{
					setContentView(R.layout.activity_guide_menu_slider_newton_int);
				}
			}else{
				setContentView(R.layout.activity_guide_menu_slider_lu);
			}
		}else{
			setContentView(R.layout.activity_guide_menu);
		}

		getActionBar().setDisplayHomeAsUpEnabled(true);	
		receiveInfoFromIntent();
		setActionAndMethodName();
		
		zoomControls.setOnZoomInClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				zoomView("In");
			}
		});
	 
		zoomControls.setOnZoomOutClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				zoomView("Out");
			}
		});
		
		
	}
	
	public void zoomView(String inOrOut) {
		ScaleAnimation anim;
		if(inOrOut.equals("In")){
			
			anim = new ScaleAnimation(zoomStatus, zoomStatus + 0.05f, zoomStatus, zoomStatus + 0.05f,
								      Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
			anim.setDuration(500);
		    anim.setFillAfter(true);
		    gm_root_view.startAnimation(anim);
			zoomStatus += 0.05f;
		}else{
			if(zoomStatus - 0.05f >= 1f){
				anim = new ScaleAnimation(zoomStatus, zoomStatus - 0.05f, zoomStatus, zoomStatus - 0.05f,
										  Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 
										  0.5f);
				anim.setDuration(500);
			    anim.setFillAfter(true);
			    gm_root_view.startAnimation(anim);
			    zoomStatus -= 0.05f;
			}
		}
    }
	
	public void receiveInfoFromIntent(){
		methodToHelp = getIntent().getExtras();  //Aqu� estar�an los par�metros recibidos.
		methodNameId = methodToHelp.getInt("methodNameId");
		methodName = getResources().getString(methodNameId);
		actionId = methodToHelp.getInt("actionId");
		action = getResources().getString(actionId);
		initViewElements(action);
	}
	
	public void initViewElements(String action){
		gm_root_view = (RelativeLayout) findViewById(R.id.guide_menu_root_view);
		zoomControls = (ZoomControls) findViewById(R.id.zoomControls);
		resultsTableView = (RelativeLayout) findViewById(R.id.ove_results_table_view);
		tvResultsTableAction = (TextView) findViewById(R.id.ove_results_table_action);
		tvResultsTableMethodName = (TextView) findViewById(R.id.ove_results_table_method_name);
		tlResultsTable = (TableLayout) findViewById(R.id.ove_results_table);
		tvStagesTableAction = (TextView) findViewById(R.id.soe_results_table_action);
		tvStagesTableMethodName = (TextView) findViewById(R.id.soe_results_table_method_name);
		stageMatrix = (TableLayout) findViewById(R.id.soe_stage_matrix);
		stageMatrixLU = (TableLayout) findViewById(R.id.soe_stage_matrix_l);
		stageL = (RadioButton) findViewById(R.id.showStageL);
		stageU = (RadioButton) findViewById(R.id.showStageU);
		tvEquation = (TextView) findViewById(R.id.int_equation);
		previousStage = (TextView) findViewById(R.id.soe_previous_label);
		currentStage = (TextView) findViewById(R.id.soe_current_stage);
		nextStage = (TextView) findViewById(R.id.soe_next_label);
		helpView = (RelativeLayout) findViewById(R.id.ove_help_view);
		tvAction = (TextView) findViewById(R.id.ove_action);
		tvMethodName = (TextView) findViewById(R.id.ove_method_name);
		tvActionDevelopment = (TextView) findViewById(R.id.ove_action_development);
		seeExampleView = (RelativeLayout) findViewById(R.id.ove_see_example_view);
		if(action.equals("Results Table")){
			helpView.setVisibility(View.GONE);
			seeExampleView.setVisibility(View.GONE);
			resultsTableView.setVisibility(View.VISIBLE);
		}else if(action.equals("Help")){
			resultsTableView.setVisibility(View.GONE);
			seeExampleView.setVisibility(View.GONE);
			helpView.setVisibility(View.VISIBLE);
		}else if(action.equals("See an example") || action.equals("How to enter a function?") || 
				 action.equals("How to enter a matrix?") || 
				 action.equals("How to enter an evaluated function?")){
			resultsTableView.setVisibility(View.GONE);
			helpView.setVisibility(View.GONE);
			seeExampleView.setVisibility(View.VISIBLE);
		}
	}
	
	public void setActionAndMethodName(){
		if(action.equals("Results Table")){  //Mostrar Tabla de Resultados de alg�n m�todo.
			tvResultsTableAction.setText(action);
			tvResultsTableMethodName.setText(methodName);
			tlResultsTable.setStretchAllColumns(true);
	        tlResultsTable.setShrinkAllColumns(true);
			tableColumns.clear();
			columnsIdsStrings.clear();
			columnsIds.clear();
			switch (methodNameId) {
		        case R.string.incremental_search:
		        	columnsIdsStrings.add("n");
		        	columnsIdsStrings.add("Xn");
		        	columnsIdsStrings.add("f(Xn)");
		        	tableColumns.add(methodToHelp.getStringArrayList("nColumn"));
		        	tableColumns.add(methodToHelp.getStringArrayList("xNColumn"));
		        	tableColumns.add(methodToHelp.getStringArrayList("fXnColumn"));
		        	break;
		        case R.string.bisection:
		        	columnsIdsStrings.add("n");
		        	columnsIdsStrings.add("Xl");
		        	columnsIdsStrings.add("Xu");
		        	columnsIdsStrings.add("Xm");
		        	columnsIdsStrings.add("f(Xm)");
		        	columnsIdsStrings.add("Error");
		        	tableColumns.add(methodToHelp.getStringArrayList("nColumn"));
		        	tableColumns.add(methodToHelp.getStringArrayList("xLowerColumn"));
		        	tableColumns.add(methodToHelp.getStringArrayList("xUpperColumn"));
		        	tableColumns.add(methodToHelp.getStringArrayList("xMColumn"));
		        	tableColumns.add(methodToHelp.getStringArrayList("fXmColumn"));
		        	tableColumns.add(methodToHelp.getStringArrayList("errorColumn"));
		        	break;
		        case R.string.false_position:
		        	columnsIdsStrings.add("n");
		        	columnsIdsStrings.add("Xl");
		        	columnsIdsStrings.add("Xu");
		        	columnsIdsStrings.add("Xm");
		        	columnsIdsStrings.add("f(Xm)");
		        	columnsIdsStrings.add("Error");
		        	tableColumns.add(methodToHelp.getStringArrayList("nColumn"));
		        	tableColumns.add(methodToHelp.getStringArrayList("xLowerColumn"));
		        	tableColumns.add(methodToHelp.getStringArrayList("xUpperColumn"));
		        	tableColumns.add(methodToHelp.getStringArrayList("xMColumn"));
		        	tableColumns.add(methodToHelp.getStringArrayList("fXmColumn"));
		        	tableColumns.add(methodToHelp.getStringArrayList("errorColumn"));
		        	break;
		        case R.string.fixed_point:
		        	columnsIdsStrings.add("n");
		        	columnsIdsStrings.add("Xn");
		        	columnsIdsStrings.add("f(Xn)");
		        	columnsIdsStrings.add("Error");
		        	tableColumns.add(methodToHelp.getStringArrayList("nColumn"));
		        	tableColumns.add(methodToHelp.getStringArrayList("xNColumn"));
		        	tableColumns.add(methodToHelp.getStringArrayList("fXnColumn"));
		        	tableColumns.add(methodToHelp.getStringArrayList("errorColumn"));
		        	break;
		        case R.string.newton:
		        	columnsIdsStrings.add("n");
		        	columnsIdsStrings.add("Xn");
		        	columnsIdsStrings.add("f(Xn)");
		        	columnsIdsStrings.add("f '(Xn)");
		        	columnsIdsStrings.add("Error");
		        	tableColumns.add(methodToHelp.getStringArrayList("nColumn"));
		        	tableColumns.add(methodToHelp.getStringArrayList("xNColumn"));
		        	tableColumns.add(methodToHelp.getStringArrayList("fXnColumn"));
		        	tableColumns.add(methodToHelp.getStringArrayList("dFXnColumn"));
		        	tableColumns.add(methodToHelp.getStringArrayList("errorColumn"));
		        	break;
		        case R.string.secant:
		        	columnsIdsStrings.add("n");
		        	columnsIdsStrings.add("Xn");
		        	columnsIdsStrings.add("f(Xn)");
		        	columnsIdsStrings.add("Error");
		        	tableColumns.add(methodToHelp.getStringArrayList("nColumn"));
		        	tableColumns.add(methodToHelp.getStringArrayList("xNColumn"));
		        	tableColumns.add(methodToHelp.getStringArrayList("fXnColumn"));
		        	tableColumns.add(methodToHelp.getStringArrayList("errorColumn"));
		        	break;
		        case R.string.multiple_roots:
		          	columnsIdsStrings.add("n");
		        	columnsIdsStrings.add("Xn");
		        	columnsIdsStrings.add("f(Xn)");
		        	columnsIdsStrings.add("f '(Xn)");
		        	columnsIdsStrings.add("f ''(Xn)");
		        	columnsIdsStrings.add("Error");
		        	tableColumns.add(methodToHelp.getStringArrayList("nColumn"));
		        	tableColumns.add(methodToHelp.getStringArrayList("xNColumn"));
		        	tableColumns.add(methodToHelp.getStringArrayList("fXnColumn"));
		        	tableColumns.add(methodToHelp.getStringArrayList("dFXnColumn"));
		        	tableColumns.add(methodToHelp.getStringArrayList("d2FXnColumn"));
		        	tableColumns.add(methodToHelp.getStringArrayList("errorColumn"));
		        	break;
		        case R.string.jacobi:
		        	columnsIdsStrings.add("n");
		        	for(int i = 0; i < Matrix.getXNColumnsStrings().size(); i++){
		        		columnsIdsStrings.add("X" + (i+1));
		        	}
		        	columnsIdsStrings.add("Dispersion");
		        	tableColumns.add(methodToHelp.getStringArrayList("nColumn"));
		        	for(int j = 0; j < Matrix.getXNColumnsStrings().size(); j++){
		        		tableColumns.add(Matrix.getXNColumnsStrings().get(j));
		        	}
		        	tableColumns.add(methodToHelp.getStringArrayList("dispersionColumn"));
		        	break;
		        case R.string.gauss_seidel:
		        	columnsIdsStrings.add("n");
		        	for(int i = 0; i < Matrix.getXNColumnsStrings().size(); i++){
		        		columnsIdsStrings.add("X" + (i+1));
		        	}
		        	columnsIdsStrings.add("Dispersion");
		        	tableColumns.add(methodToHelp.getStringArrayList("nColumn"));
		        	for(int j = 0; j < Matrix.getXNColumnsStrings().size(); j++){
		        		tableColumns.add(Matrix.getXNColumnsStrings().get(j));
		        	}
		        	tableColumns.add(methodToHelp.getStringArrayList("dispersionColumn"));
		        	break;
		        default:
		            return;
			}
			saveColumnsIds(columnsIdsStrings);
			printTable();
		}else if(action.equals("Stages Table")){
			tvStagesTableAction.setText(action);
			tvStagesTableMethodName.setText(methodName);
			if(!methodName.equals("Crout") && !methodName.equals("Doolittle") && 
			   !methodName.equals("Cholesky")){
				stageByStageMatTables.clear();
				if(!methodName.equals("Newton Interpolation")){
					if(!methodName.equals("Cubic Spline")){
						if(!methodName.equals("Matrix Inverse")){
							printTableForSoE();
						}else{
							tvStagesTableAction.setText(getResources()
									   		   			.getString(R.string.mat_inverse));
							tvStagesTableMethodName.setText(getResources()
				   		   									.getString(R.string.lu_factorization));
							previousStage.setVisibility(View.GONE);
							currentStage.setVisibility(View.GONE);
							nextStage.setVisibility(View.GONE);
							printTableForMatInverse();
						}
					}else{
						tvStagesTableAction.setText(getResources()
										   .getString(R.string.result_eqs_interpolation));
						printTableForInterpolationCS();
					}
				}else{
					printTableForInterpolation();
				}
			}else{
				stageByStageMatTablesL.clear();
				stageByStageMatTablesU.clear();
				//Por defecto, primero se muestra la matriz L de la etapa.
				stageL.setChecked(true);
				printTableForSoELU();
			}
		}else if(action.equals("Help")){  //Mostrar Help de alg�n m�todo.
			tvAction.setText(action);
			tvMethodName.setText(methodName);
			switch (methodNameId) {
		        case R.string.incremental_search:
		        	actionDevelopment = getResources().getString(R.string.incremental_search_help);
		        	break;
		        case R.string.bisection:
		        	actionDevelopment = getResources().getString(R.string.bisection_help);
		        	break;
		        case R.string.false_position:
		        	actionDevelopment = getResources().getString(R.string.false_position_help);
		        	break;
		        case R.string.fixed_point:
		        	actionDevelopment = getResources().getString(R.string.fixed_point_help);
		        	break;
		        case R.string.newton:
		        	actionDevelopment = getResources().getString(R.string.newton_help);
		        	break;
		        case R.string.secant:
		        	actionDevelopment = getResources().getString(R.string.secant_help);
		        	break;
		        case R.string.multiple_roots:
		        	actionDevelopment = getResources().getString(R.string.multiple_roots_help);
		        	break;
		        case R.string.without_pivoting:
		        	actionDevelopment = getResources().getString(R.string.without_pivoting_help);
		        	break;
		        case R.string.partial_pivoting:
		        	actionDevelopment = getResources().getString(R.string.partial_pivoting_help);
		        	break;
		        case R.string.total_pivoting:
		        	actionDevelopment = getResources().getString(R.string.total_pivoting_help);
		        	break;
		        case R.string.scaled_partial_pivoting:
		        	actionDevelopment = getResources().getString(R.string.scaled_partial_pivoting_help);
		        	break;
		        case R.string.crout:
		        	actionDevelopment = getResources().getString(R.string.crout_help);
		        	break;
		        case R.string.doolittle:
		        	actionDevelopment = getResources().getString(R.string.doolittle_help);
		        	break;
		        case R.string.cholesky:
		        	actionDevelopment = getResources().getString(R.string.cholesky_help);
		        	break;
		        case R.string.jacobi:
		        	actionDevelopment = getResources().getString(R.string.jacobi_help);
		        	break;
		        case R.string.gauss_seidel:
		        	actionDevelopment = getResources().getString(R.string.gauss_seidel_help);
		        	break;
		        case R.string.newton_interpolation:
		        	actionDevelopment = getResources().getString(R.string.newton_interpolation_help);
		        	break;
		        case R.string.lagrange_interpolation:
		        	actionDevelopment = getResources().getString(R.string.lagrange_interpolation_help);
		        	break;
		        case R.string.cubic_spline:
		        	actionDevelopment = getResources().getString(R.string.cubic_spline_help);
		        	Spanned result = Html.fromHtml(actionDevelopment);
		        	tvActionDevelopment.setText(result);
		        	break;
		        	 
		        default:
		            return;
			}	
			if(methodNameId != R.string.cubic_spline){
				tvActionDevelopment.setText(actionDevelopment);
			}
			
		}else if(action.equals("See an example")){  //Mostrar See an example de alg�n m�todo.
			switch (methodNameId) {
			
				case R.string.incremental_search:
		        	seeExampleView.setBackgroundResource(R.drawable.see_example_incremental_search);
		        	break;
		        case R.string.bisection:
		        	seeExampleView.setBackgroundResource(R.drawable.see_example_bisection);
		        	break;
		        case R.string.false_position:
		        	seeExampleView.setBackgroundResource(R.drawable.see_example_false_position);
		        	break;
		        case R.string.fixed_point:
		        	seeExampleView.setBackgroundResource(R.drawable.see_example_fixed_point);
		        	break;
		        case R.string.newton:
		        	seeExampleView.setBackgroundResource(R.drawable.see_example_newton);
		        	break;
		        case R.string.secant:
		        	seeExampleView.setBackgroundResource(R.drawable.see_example_secant);
		        	break;
		        case R.string.multiple_roots:
		        	seeExampleView.setBackgroundResource(R.drawable.see_example_multiple_roots);
		        	break;
		        case R.string.how_to_enter_matrix:
		        	seeExampleView.setBackgroundResource(R.drawable.see_example_matrix);
		        	break;
		        case R.string.without_pivoting:
		        	seeExampleView.setBackgroundResource(R.drawable.see_example_w_piv);
		        	break;
		        case R.string.partial_pivoting:
		        	seeExampleView.setBackgroundResource(R.drawable.see_example_p_piv);
		        	break;
		        case R.string.total_pivoting:
		        	seeExampleView.setBackgroundResource(R.drawable.see_example_t_piv);
		        	break;
		        case R.string.scaled_partial_pivoting:
		        	seeExampleView.setBackgroundResource(R.drawable.see_example_sp_piv);
		        	break;
		        case R.string.crout:
		        	seeExampleView.setBackgroundResource(R.drawable.see_example_crout);
		        	break;
		        case R.string.doolittle:
		        	seeExampleView.setBackgroundResource(R.drawable.see_example_doolittle);
		        	break;
		        case R.string.cholesky:
		        	seeExampleView.setBackgroundResource(R.drawable.see_example_cholesky);
		        	break;
		        case R.string.jacobi:
		        	seeExampleView.setBackgroundResource(R.drawable.see_example_jacobi);
		        	break;
		        case R.string.gauss_seidel:
		        	seeExampleView.setBackgroundResource(R.drawable.see_example_gauss_seidel);
		        	break;
		        case R.string.how_to_enter_eval_function:
		        	seeExampleView.setBackgroundResource(R.drawable.see_example_eval_function);
		        	break;
		        case R.string.interpolation:
		        	seeExampleView.setBackgroundResource(R.drawable.see_example_interpolation);
		        	break;
		        	
		        default:
		            return;
			}
		}else if(action.equals("How to enter a function?")){  //Mostrar cómo se entra una función.
			seeExampleView.setBackgroundResource(R.drawable.how_enter_functions);
		}else if(action.equals("How to enter a matrix?")){    //Mostrar cómo se entra una matriz.
			seeExampleView.setBackgroundResource(R.drawable.how_to_enter_matrix);
		}else if(action.equals("How to enter an evaluated function?")){
			seeExampleView.setBackgroundResource(R.drawable.how_to_enter_eval_function);
		}
	}
	
	//Se almacenan los Ids de las columnas como TextViews para pegarlos en la tabla de resultados.
	public void saveColumnsIds(ArrayList<String> columnsIdsStrings){
		for(int i = 0; i < columnsIdsStrings.size(); i++){
			TextView columnId = new TextView(this);
			columnId.setText(columnsIdsStrings.get(i));
			columnId.setTextSize(12.0f);
			columnId.setTextColor(Color.BLACK);
			columnId.setGravity(Gravity.CENTER_HORIZONTAL);
			columnId.setTypeface(Typeface.DEFAULT_BOLD);
			columnsIds.add(columnId);
		}
	}
	
	public void printTable(){
		//Para ver resultados en consola.
		for (ArrayList<String> column : tableColumns){
			for (String data : column){
				Log.i("table", data);
			}
			Log.i("table", "Pasa a la sgte columna");
		}
		//
		
		ArrayList<TableRow> tRowsArrayList = new ArrayList<TableRow>();
		TableRow tRowForColumnsIds = new TableRow(this);
		tRowsArrayList.add(tRowForColumnsIds);  //Se adiciona otra fila para almacenar los ids de las
												//columnas.
		for(int i = 0; i < tableColumns.get(0).size(); i++){
			TableRow tRow = new TableRow(this);
			tRowsArrayList.add(tRow);  //Se crean tantas filas tenga la tabla de resultados.
		}
		
		for(int j = -1; j < tRowsArrayList.size()-1; j++){
			if(j == -1){   //Agrega los nombres de las columnas.
				for(int k = 0; k < columnsIds.size(); k++){
					columnsIds.get(k).setBackground(getResources().
							                        getDrawable(R.drawable.results_table_columns_shape));
					tRowsArrayList.get(j+1).addView(columnsIds.get(k));
				}
			}else{
				for(int m = 0; m < columnsIds.size(); m++){  //Agrega la informaci�n dentro de cada columna.
					TextView columnData = new TextView(this);
					columnData.setText(tableColumns.get(m).get(j));
					columnData.setTextSize(9.5f);
					columnData.setTextColor(Color.BLACK);
					columnData.setGravity(Gravity.CENTER_HORIZONTAL);
					columnData.setBackground(getResources().getDrawable(R.drawable.results_table_shape));
					tRowsArrayList.get(j+1).addView(columnData);
				}
			}
		}
		
		tlResultsTable.removeAllViews();
		for(int n = 0; n < tRowsArrayList.size(); n++){
			tlResultsTable.addView(tRowsArrayList.get(n));  //Se crea la tabla con cada fila generada.
		}

	}

	public void printTableForSoE(){
		double [] [][] stageByStageMatrices = Matrix.getStageByStageMatrices();
		Log.i("parcial", "---------------------");
		Log.i("parcial", "Matrices por etapas:");
		for(int a = 0; a < stageByStageMatrices.length; a++){
			Log.i("parcial", "---------------------");
			Log.i("parcial", "Etapa " + a +":");
			for(int i = 0; i < stageByStageMatrices.length; i++){
				String row = "";
				for(int j = 0; j < stageByStageMatrices.length + 1; j++){
					row += String.valueOf(stageByStageMatrices[a] [i][j]) + ", ";
				}
				Log.i("parcial", row);
			}
			
		}
		for(int i = 0; i < stageByStageMatrices.length; i++){
			ArrayList<TableRow> tRowsArrayList = new ArrayList<TableRow>();
			
			for(int j = 0; j < stageByStageMatrices.length; j++){
				TableRow tRow = new TableRow(this);
				tRowsArrayList.add(tRow);  //Se crean tantas filas tenga la tabla de resultados.
			}
			
			for(int k = 0; k < stageByStageMatrices.length; k++){  //Agrega la informaci�n dentro de 
																  //cada columna.
				for(int m = 0; m < stageByStageMatrices.length + 1; m++){
					TextView columnData = new TextView(this);
					columnData.setText(String.valueOf(stageByStageMatrices[i] [k][m]));
					columnData.setTextSize(12.5f);
					columnData.setTextColor(Color.BLACK);
					columnData.setGravity(Gravity.CENTER_HORIZONTAL);
					columnData.setBackground(getResources().getDrawable(R.drawable.results_table_shape));
					tRowsArrayList.get(k).addView(columnData);
				}
			}
			
			stageByStageMatTables.add(tRowsArrayList);
		}
		stageMatrix.removeAllViews();    //Se muestra la matriz inicial.
		for(int n = 0; n < stageByStageMatTables.get(0).size(); n++){
			stageMatrix.addView(stageByStageMatTables.get(0).get(n));  
		}
		previousStage.setTextColor(Color.LTGRAY);
		previousStage.setClickable(false);
		currentStage.setText("Stage 0");		
	}
	
	
	public void printTableForSoELU(){
		double [] [][] stageByStageMatricesL = Matrix.getStageByStageMatricesL();
		double [] [][] stageByStageMatricesU = Matrix.getStageByStageMatricesU();
		
		for(int i = 0; i < stageByStageMatricesL.length; i++){
			ArrayList<TableRow> tRowsArrayListL = new ArrayList<TableRow>();
			ArrayList<TableRow> tRowsArrayListU = new ArrayList<TableRow>();
			
			for(int j = 0; j < stageByStageMatricesL.length - 1; j++){
				TableRow tRowL = new TableRow(this);
				tRowsArrayListL.add(tRowL);  //Se crean tantas filas tenga la tabla de resultados.
				TableRow tRowU = new TableRow(this);
				tRowsArrayListU.add(tRowU);  //Se crean tantas filas tenga la tabla de resultados.
			}
			
			for(int k = 0; k < stageByStageMatricesL.length - 1; k++){  //Agrega la informaci�n dentro 
																	   //de cada columna.
				for(int m = 0; m < stageByStageMatricesL.length - 1; m++){
					TextView columnDataL = new TextView(this);
					columnDataL.setText(String.valueOf(stageByStageMatricesL[i] [k][m]));
					columnDataL.setTextSize(12.5f);
					columnDataL.setTextColor(Color.BLACK);
					columnDataL.setGravity(Gravity.CENTER_HORIZONTAL);
					columnDataL.setBackground(getResources().getDrawable(R.drawable.results_table_shape));
					tRowsArrayListL.get(k).addView(columnDataL);
					TextView columnDataU = new TextView(this);
					columnDataU.setText(String.valueOf(stageByStageMatricesU[i] [k][m]));
					columnDataU.setTextSize(12.5f);
					columnDataU.setTextColor(Color.BLACK);
					columnDataU.setGravity(Gravity.CENTER_HORIZONTAL);
					columnDataU.setBackground(getResources().getDrawable(R.drawable.results_table_shape));
					tRowsArrayListU.get(k).addView(columnDataU);
				}
			}
			
			stageByStageMatTablesL.add(tRowsArrayListL);
			stageByStageMatTablesU.add(tRowsArrayListU);
		}
		stageMatrixLU.removeAllViews();    //Se muestran las matrices iniciales.
		for(int n = 0; n < stageByStageMatTablesL.get(0).size(); n++){
			stageMatrixLU.addView(stageByStageMatTablesL.get(0).get(n));  
		}
		previousStage.setTextColor(Color.LTGRAY);
		previousStage.setClickable(false);
		currentStage.setText("Stage 0");	
	}
	
	public void printTableForInterpolation(){
		ArrayList<ArrayList<String>> divDifsStrings = InterpolationTables.getDivDifsStrings();
		
		ArrayList<TableRow> tRowsArrayListH = new ArrayList<TableRow>();
		
		for(int i = 0; i < InterpolationTables.getxNValues().size() + 1; i++){
			TableRow tRow = new TableRow(this);
			tRowsArrayListH.add(tRow);  //Se crean tantas filas tenga la tabla de resultados.
		}
		
		//Para el encabezado.
		TextView columnDataHXn = new TextView(this);
		TextView columnDataHFXn = new TextView(this);
		columnDataHXn.setText("Xn");
		columnDataHFXn.setText("f(Xn)");
		columnDataHXn.setTextSize(14.5f);
		columnDataHFXn.setTextSize(14.5f);
		columnDataHXn.setTextColor(Color.BLACK);
		columnDataHFXn.setTextColor(Color.BLACK);
		columnDataHXn.setGravity(Gravity.CENTER_HORIZONTAL);
		columnDataHFXn.setGravity(Gravity.CENTER_HORIZONTAL);
		columnDataHXn.setTypeface(Typeface.DEFAULT_BOLD);
		columnDataHFXn.setTypeface(Typeface.DEFAULT_BOLD);
		columnDataHXn.setBackground(getResources()
								   .getDrawable(R.drawable.results_table_columns_shape));
		columnDataHFXn.setBackground(getResources()
								    .getDrawable(R.drawable.results_table_columns_shape));
		tRowsArrayListH.get(0).addView(columnDataHXn);
		tRowsArrayListH.get(0).addView(columnDataHFXn);
		//
		
		for(int j = 0; j < tRowsArrayListH.size() - 1; j++){  
			TextView columnDataXn = new TextView(this);
			TextView columnDataFXn = new TextView(this);
			columnDataXn.setText(InterpolationTables.getxNValuesStrings().get(j));
			columnDataFXn.setText(InterpolationTables.getFXnValuesStrings().get(j));
			columnDataXn.setTextSize(12.5f);
			columnDataFXn.setTextSize(12.5f);
			columnDataXn.setTextColor(Color.BLACK);
			columnDataFXn.setTextColor(Color.BLACK);
			columnDataXn.setGravity(Gravity.CENTER_HORIZONTAL);
			columnDataFXn.setGravity(Gravity.CENTER_HORIZONTAL);
			columnDataXn.setBackground(getResources()
									   .getDrawable(R.drawable.results_table_shape));
			columnDataFXn.setBackground(getResources()
									    .getDrawable(R.drawable.results_table_shape));
			tRowsArrayListH.get(j+1).addView(columnDataXn);
			tRowsArrayListH.get(j+1).addView(columnDataFXn);
		}	
		
		stageByStageMatTables.add(tRowsArrayListH);
	
		for(int k = 0; k < divDifsStrings.size(); k++){  
			ArrayList<TableRow> tRowsArrayList = new ArrayList<TableRow>();
			
			for(int j = 0; j < InterpolationTables.getxNValues().size(); j++){
				TableRow tRow = new TableRow(this);
				tRowsArrayList.add(tRow);  //Se crean tantas filas tenga la tabla de resultados.
			}
			
			for(int m = 0; m < divDifsStrings.get(k).size(); m++){
				TextView columnData = new TextView(this);
				columnData.setText(divDifsStrings.get(k).get(m));
				columnData.setTextSize(12.5f);
				columnData.setTextColor(Color.BLACK);
				columnData.setGravity(Gravity.CENTER_HORIZONTAL);
				columnData.setBackground(getResources().getDrawable(R.drawable.results_table_shape));
				tRowsArrayList.get(m).addView(columnData);
			}
			
			stageByStageMatTables.add(tRowsArrayList);
		}
		
		stageMatrix.removeAllViews();    //Se muestra la matriz inicial.
		for(int n = 0; n < stageByStageMatTables.get(0).size(); n++){
			stageMatrix.addView(stageByStageMatTables.get(0).get(n));  
		}
		previousStage.setTextColor(Color.LTGRAY);
		previousStage.setClickable(false);
		currentStage.setText("Points");		
	}
	
	public void printTableForInterpolationCS(){
		tvEquation.setText(InterpolationTables.getCubicSplineEquations().get(0));
		previousStage.setTextColor(Color.LTGRAY);
		previousStage.setClickable(false);
		currentStage.setText("1. Equation");
	}
	
	public void printTableForMatInverse(){
		ArrayList<ArrayList<String>> matInverse = Matrix.getInverseColumnsStrings();
		
		ArrayList<TableRow> tRowsArrayList = new ArrayList<TableRow>();
			
		for(int i = 0; i < matInverse.size(); i++){
			TableRow tRow = new TableRow(this);
			tRowsArrayList.add(tRow);  //Se crean tantas filas tenga la tabla de resultados.
		}
		
		for(int j = 0; j < matInverse.size(); j++){  //Agrega la informaci�n dentro de 
													//cada columna.
			for(int k = 0; k < matInverse.size(); k++){
				TextView columnData = new TextView(this);
				columnData.setText(matInverse.get(k).get(j));
				columnData.setTextSize(12.5f);
				columnData.setTextColor(Color.BLACK);
				columnData.setGravity(Gravity.CENTER_HORIZONTAL);
				columnData.setBackground(getResources().getDrawable(R.drawable.results_table_shape));
				tRowsArrayList.get(j).addView(columnData);
			}
		}

		stageMatrix.removeAllViews();    //Se muestra la matriz inicial.
		for(int m = 0; m < tRowsArrayList.size(); m++){
			stageMatrix.addView(tRowsArrayList.get(m));  
		}		
	}
	
	public void setStageAndMatrix(View v){
		previousStage.setTextColor(Color.DKGRAY);
		nextStage.setTextColor(Color.DKGRAY);
		previousStage.setClickable(true);
		nextStage.setClickable(true);
		switch(v.getId()){
			case R.id.soe_previous_label:
				if((currentStageNumber - 1) == 0){
					previousStage.setTextColor(Color.LTGRAY);
					previousStage.setClickable(false);
				}
				currentStageNumber--;
				break;
			case R.id.soe_next_label:
				if((currentStageNumber + 1) == (stageByStageMatTables.size() - 1)){
					nextStage.setTextColor(Color.LTGRAY);
					nextStage.setClickable(false);
				}
				currentStageNumber++;
				break;
			default:
	            return;
		}

		stageMatrix.removeAllViews();
		for(int n = 0; n < stageByStageMatTables.get(currentStageNumber).size(); n++){
			stageMatrix.addView(stageByStageMatTables.get(currentStageNumber).get(n));  
		}
		if(!methodName.equals("Newton Interpolation")){
			currentStage.setText("Stage " + currentStageNumber);
		}else{
			if(currentStageNumber == 0){
				currentStage.setText("Points");
			}else{
				currentStage.setText(currentStageNumber + ". Divided Difference");
			}
		}
	}
	
	public void setStageAndMatrixLU(View v){
		previousStage.setTextColor(Color.DKGRAY);
		nextStage.setTextColor(Color.DKGRAY);
		previousStage.setClickable(true);
		nextStage.setClickable(true);
		switch(v.getId()){
			case R.id.soe_previous_label:
				if((currentStageNumberLU - 1) == 0){
					previousStage.setTextColor(Color.LTGRAY);
					previousStage.setClickable(false);
				}
				currentStageNumberLU--;
				break;
			case R.id.soe_next_label:
				if((currentStageNumberLU + 1) == (stageByStageMatTablesL.size() - 1)){
					nextStage.setTextColor(Color.LTGRAY);
					nextStage.setClickable(false);
				}
				currentStageNumberLU++;
				break;
			default:
	            return;
		}

		stageMatrixLU.removeAllViews();
		for(int n = 0; n < stageByStageMatTablesL.get(currentStageNumberLU).size(); n++){
			if(lOrUStage.equals("L")){
				stageMatrixLU.addView(stageByStageMatTablesL.get(currentStageNumberLU).get(n)); 
			}else{
				stageMatrixLU.addView(stageByStageMatTablesU.get(currentStageNumberLU).get(n)); 
			}
		}
		currentStage.setText("Stage " + currentStageNumberLU);
	}
	
	public void setCubicSplineEquation(View v){
		previousStage.setTextColor(Color.DKGRAY);
		nextStage.setTextColor(Color.DKGRAY);
		previousStage.setClickable(true);
		nextStage.setClickable(true);
		switch(v.getId()){
			case R.id.soe_previous_label:
				if((currentStageNumber - 1) == 0){
					previousStage.setTextColor(Color.LTGRAY);
					previousStage.setClickable(false);
				}
				currentStageNumber--;
				break;
			case R.id.soe_next_label:
				if((currentStageNumber + 1) == (InterpolationTables.getCubicSplineEquations()
																   .size() - 1)){
					nextStage.setTextColor(Color.LTGRAY);
					nextStage.setClickable(false);
				}
				currentStageNumber++;
				break;
			default:
	            return;
		}
		
		tvEquation.setText(InterpolationTables.getCubicSplineEquations().get(currentStageNumber));
		//La primera ecuación se desea mostrar con el número 1 y no con el 0. 
		currentStage.setText((currentStageNumber + 1) + ". Equation");
	}
	
	public void selectMatForStage(View v){
		switch(v.getId()){
		case R.id.showStageL:
			stageU.setChecked(false);
			lOrUStage = "L";
			break;
		case R.id.showStageU:
			stageL.setChecked(false);
			lOrUStage = "U";
			break;
		default:
			return;
		}
		
		stageMatrixLU.removeAllViews();
		for(int n = 0; n < stageByStageMatTablesL.get(currentStageNumberLU).size(); n++){
			if(lOrUStage.equals("L")){
				stageMatrixLU.addView(stageByStageMatTablesL.get(currentStageNumberLU).get(n)); 
			}else{
				stageMatrixLU.addView(stageByStageMatTablesU.get(currentStageNumberLU).get(n)); 
			}
		}
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        	case android.R.id.home:
        		finish();
        		break;
        	default:
        		return super.onOptionsItemSelected(item);
        }
        return true;
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.guide_menu, menu);
		return true;
	}

}

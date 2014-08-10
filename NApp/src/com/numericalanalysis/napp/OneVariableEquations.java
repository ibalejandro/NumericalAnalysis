package com.numericalanalysis.napp;

import android.os.Bundle;
import android.app.ListActivity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class OneVariableEquations extends ListActivity {
	
	private FunctionsEvaluator fEvaluator;
	private String[] oneVarEqsMethods;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_one_var_eqs_list_element);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		fEvaluator = (FunctionsEvaluator) getIntent().getParcelableExtra("fEvaluator");
		oneVarEqsMethods = getResources().getStringArray(R.array.one_var_eqs_methods);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, 
										                        oneVarEqsMethods);
		setListAdapter(adapter); 
	}
	
	@Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
          super.onListItemClick(l, v, position, id);
          Intent openClickedMethod = new Intent();
          switch (position) {
      			case 0:
      				openClickedMethod = new Intent(OneVariableEquations.this, IncrementalSearch.class);
      				openClickedMethod.putExtra("fEvaluator", fEvaluator);
      				startActivity(openClickedMethod);
      				break;
      			case 1:
      				openClickedMethod = new Intent(OneVariableEquations.this, Bisection.class);
      				openClickedMethod.putExtra("fEvaluator", fEvaluator);
      				startActivity(openClickedMethod);
      				break;
      			case 2:
      				openClickedMethod = new Intent(OneVariableEquations.this, FalsePosition.class);
      				openClickedMethod.putExtra("fEvaluator", fEvaluator);
      				startActivity(openClickedMethod);
      				break;
      			case 3:
      				if(fEvaluator.getBuiltFunctionsStatus()[3]){
      					openClickedMethod = new Intent(OneVariableEquations.this, FixedPoint.class);
      					openClickedMethod.putExtra("fEvaluator", fEvaluator);
      					startActivity(openClickedMethod);
      				}else{
      					Toast.makeText(getApplicationContext(), getResources()
      							       .getString(R.string.g_x_empty), Toast.LENGTH_SHORT).show();
      				}
      				break;
      			case 4:
      				if(fEvaluator.getBuiltFunctionsStatus()[1]){
      					openClickedMethod = new Intent(OneVariableEquations.this, Newton.class);
      					openClickedMethod.putExtra("fEvaluator", fEvaluator);
          				startActivity(openClickedMethod);
      				}else{
      					Toast.makeText(getApplicationContext(), getResources()
					               .getString(R.string.d_f_x_empty), Toast.LENGTH_SHORT).show();
      				}
      				break;
      			case 5:
      				openClickedMethod = new Intent(OneVariableEquations.this, Secant.class);
      				openClickedMethod.putExtra("fEvaluator", fEvaluator);
      				startActivity(openClickedMethod);
      				break;
      			case 6:
      				if(fEvaluator.getBuiltFunctionsStatus()[1] && fEvaluator.getBuiltFunctionsStatus()[2]){
      					openClickedMethod = new Intent(OneVariableEquations.this, MultipleRoots.class);
      					openClickedMethod.putExtra("fEvaluator", fEvaluator);
      					startActivity(openClickedMethod);
      				}else{
      					Toast.makeText(getApplicationContext(), getResources()
						               .getString(R.string.d_f_x_and_d_2_f_x_empty), Toast.LENGTH_SHORT)
						               .show();
      				}
      				break;
      			default:
      				return;
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
		getMenuInflater().inflate(R.menu.one_variable_equations, menu);
		return true;
	}

}

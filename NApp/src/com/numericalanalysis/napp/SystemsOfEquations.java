package com.numericalanalysis.napp;



import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;



public class SystemsOfEquations extends ListActivity {
	
	//private Matrix matrix;
	private String[] systemsOfEqsMethods;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_systems_of_equations);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		//matrix = (Matrix) getIntent().getParcelableExtra("matrix");
		systemsOfEqsMethods = getResources().getStringArray(R.array.systems_of_eqs_methods);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, 
										                        systemsOfEqsMethods);
		setListAdapter(adapter); 
	}
	
	@Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
          super.onListItemClick(l, v, position, id);
          Intent openClickedMethod = new Intent();
          switch (position) {
      			case 0:
      				openClickedMethod = new Intent(SystemsOfEquations.this, GaussianElimination.class);
      				//openClickedMethod.putExtra("matrix", matrix);
      				startActivity(openClickedMethod);
      				break;
      			case 1:
      				openClickedMethod = new Intent(SystemsOfEquations.this, LUFactorization.class);
      				//openClickedMethod.putExtra("matrix", matrix);
      				startActivity(openClickedMethod);
      				break;
      			case 2:
      				openClickedMethod = new Intent(SystemsOfEquations.this, StationaryIterativeMethods.class);
      				//openClickedMethod.putExtra("matrix", matrix);
      				startActivity(openClickedMethod);
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
		getMenuInflater().inflate(R.menu.systems_of_equations, menu);
		return true;
	}
}

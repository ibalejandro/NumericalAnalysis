package com.numericalanalysis.napp;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class Interpolation extends ListActivity {
	
	private String[] interpolationMethods;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_interpolation);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		interpolationMethods = getResources().getStringArray(R.array.interpolation_methods);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, 
										                        interpolationMethods);
		setListAdapter(adapter); 
	}

	@Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
          super.onListItemClick(l, v, position, id);
          Intent openClickedMethod = new Intent();
          switch (position) {
      			case 0:
      				openClickedMethod = new Intent(Interpolation.this, NewtonInterpolation.class);
      				startActivity(openClickedMethod);
      				break;
      			case 1:
      				openClickedMethod = new Intent(Interpolation.this, LagrangeInterpolation.class);
      				startActivity(openClickedMethod);
      				break;
      			case 2:
      				openClickedMethod = new Intent(Interpolation.this, CubicSpline.class);
      				startActivity(openClickedMethod);
      				break;
      			default:
      				return;
          }  
    }

	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		Intent openSelectedItem = new Intent(Interpolation.this, GuideMenu.class);
		Bundle methodToHelp = new Bundle();
		methodToHelp.putInt("methodNameId", R.string.interpolation);
        switch (item.getItemId()) {
        	case R.id.seeExample:
        		methodToHelp.putInt("actionId", R.string.see_example);
        		break;
        	case android.R.id.home:
        		finish();
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
			menu.add(0, R.id.seeExample, Menu.FIRST, getResources().getString(R.string.see_example));
		}

		//Para la ActionBar.
    	getMenuInflater().inflate(R.menu.interpolation, menu);
	
		return true;
	}

}

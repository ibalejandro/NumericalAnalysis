package com.numericalanalysis.napp;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class Topics extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_topics);
	}
	
	public void goToOneVariableEq(View v){
		Intent openOneVariableEq = new Intent(Topics.this, InputFunctions.class);
		startActivity(openOneVariableEq);
	}
	
	public void goToSystemsOfEq(View v){
		Intent openSystemsOfEq = new Intent(Topics.this, InputMatrices.class);
		startActivity(openSystemsOfEq);
	}
	
	public void goToInterpolation(View v){
		Intent openInterpolation = new Intent(Topics.this, InputEvaluatedFunctions.class);
		startActivity(openInterpolation);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.topics, menu);
		return true;
	}

}

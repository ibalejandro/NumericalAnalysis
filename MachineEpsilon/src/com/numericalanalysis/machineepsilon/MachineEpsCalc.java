package com.numericalanalysis.machineepsilon;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.TextView;

public class MachineEpsCalc extends Activity {
	
	private int typeAndCond;
	private TextView tVMachineEpsilon;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_machine_eps_calc);
		initViewElements();
		getIntentExtras();
		calculateWithTypeAndCond();
	}

	public void initViewElements(){
		tVMachineEpsilon = (TextView) findViewById(R.id.machineEps);
	}
	
	public void getIntentExtras(){
		Bundle paramsBag = getIntent().getExtras();
        typeAndCond = paramsBag.getInt("rBIdSelected");
	}
	
	public void calculateWithTypeAndCond(){
		switch(typeAndCond){
			
			case 2131230724: 
				calculateWithFlCond1();
			  break;
			case 2131230723:  
				calculateWithDbCond1();
			  break;
			case  2131230725:
				calculateWithFlCond2();
			  break;
			case 2131230726 :
				calculateWithDbCond2();
			  break;
			  
		}
		
	}
	
	public void calculateWithFlCond1(){
		float machineEpsilon = 0.0f;
		float d = 0.5f;
		while(1 != 1 + d){
			machineEpsilon = d;
			d = d/2;
		}
		
		tVMachineEpsilon.setText(String.valueOf(machineEpsilon));
	}
	
	public void calculateWithDbCond1(){
		double machineEpsilon = 0.0;
		double d = 0.5;
		while(1 != 1 + d){
			machineEpsilon = d;
			d = d/2;
		}
		
		tVMachineEpsilon.setText(String.valueOf(machineEpsilon));
	}
	
	public void calculateWithFlCond2(){
		float machineEpsilon = 0.0f;
		float d = 0.5f;
		while(0 != d){
			machineEpsilon = d;
			d = d/2;
		}
		
		tVMachineEpsilon.setText(String.valueOf(machineEpsilon));
	}
	
	public void calculateWithDbCond2(){
		double machineEpsilon = 0.0;
		double d = 0.5;
		while(0 != d){
			machineEpsilon = d;
			d = d/2;
		}
		
		tVMachineEpsilon.setText(String.valueOf(machineEpsilon));
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.machine_eps_calc, menu);
		return true;
	}

}

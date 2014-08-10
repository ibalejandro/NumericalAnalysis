package com.numericalanalysis.machineepsilon;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.RadioButton;

public class MainActivity extends Activity {
	
	private RadioButton flCond1;
	private RadioButton dbCond1;
	private RadioButton flCond2;
	private RadioButton dbCond2;
	private ArrayList <RadioButton> rBArrayList = new ArrayList<RadioButton>();
	private int typeAndCond;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViewElements();
    }

    public void initViewElements(){
    	flCond1 = (RadioButton) findViewById(R.id.flCond1);
    	dbCond1 = (RadioButton) findViewById(R.id.dbCond1);
    	flCond2 = (RadioButton) findViewById(R.id.flCond2);
    	dbCond2 = (RadioButton) findViewById(R.id.dbCond2);
    	rBArrayList.add(flCond1);
    	rBArrayList.add(dbCond1);
    	rBArrayList.add(flCond2);
    	rBArrayList.add(dbCond2);
    }

    public void selectTypeAndCond(View v){
        for(int i = 0; i < rBArrayList.size(); i++){
        	if(v.getId() != rBArrayList.get(i).getId()){
        		rBArrayList.get(i).setChecked(false);
        	}
        	else{
        		typeAndCond = v.getId();
        	}
        }
        
    }
    
    public void calculateMachineEpsilon(View v){
    	//Para pasar de una actividad a otra. "Desde donde estoy".this, "Para donde voy".class
    	Intent goToMachineEpsCalc = new Intent(MainActivity.this, MachineEpsCalc.class);
    	//Adiciono parámetros
    	Bundle bagParams = new Bundle();
    	bagParams.putInt("rBIdSelected", typeAndCond);
    	//Se los anexo a la nueva actividad
    	goToMachineEpsCalc.putExtras(bagParams);
    	//Inicio la nueva actividad
    	startActivity(goToMachineEpsCalc);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}

package com.numericalanalysis.napp;


import java.util.ArrayList;


public class InterpolationTables {

	public static ArrayList<Double> xNValues;
	public static ArrayList<Double> fXnValues;
	public static ArrayList<String> xNValuesStrings;
	public static ArrayList<String> fXnValuesStrings;
	public static ArrayList<ArrayList<String>> divDifsStrings;
	public static ArrayList<String> cubicSplineEquations;
	
	public InterpolationTables(ArrayList<Double> xNValuesCreated, ArrayList<Double> fXnValuesCreated){
		xNValues = new ArrayList<Double>();
		fXnValues = new ArrayList<Double>();
		xNValues = xNValuesCreated;
		fXnValues = fXnValuesCreated;
		xNValuesStrings = new ArrayList<String>();
		fXnValuesStrings = new ArrayList<String>();
		createXnAndFXnStringTables();
	}
	
	public InterpolationTables(ArrayList<ArrayList<Double>> divDifsCreated){
		divDifsStrings = new ArrayList<ArrayList<String>>();
		createDivDifsStringTables(divDifsCreated);
	}
	
	public InterpolationTables(ArrayList<String> equationsCreated, boolean isCubicSpline){
		cubicSplineEquations = new ArrayList<String>();
		createCubicSplineEquations(equationsCreated);
	}
	
	public void createXnAndFXnStringTables(){
		for(int i = 0; i < xNValues.size(); i++){
			xNValuesStrings.add(String.valueOf(xNValues.get(i)));
			fXnValuesStrings.add(String.valueOf(fXnValues.get(i)));
		}
	}
	
	public void createDivDifsStringTables(ArrayList<ArrayList<Double>> divDifsCreated){
		for(int i = 0; i < divDifsCreated.size(); i++){
			ArrayList<String> xDivDif = new ArrayList<String>();
			for(int j = 0; j < xNValues.size() - divDifsCreated.get(i).size(); j++){
				xDivDif.add("-"); //Agrega signos "-" para indicar que en esas posiciones no irán datos.
			}
			for(int k = 0; k < divDifsCreated.get(i).size(); k++){
				xDivDif.add(String.valueOf(divDifsCreated.get(i).get(k)));
			}
			divDifsStrings.add(xDivDif);	
		}
	}
	
	public void createCubicSplineEquations(ArrayList<String> cubicSplineEquationsCreated){
		for(int i = 0; i < cubicSplineEquationsCreated.size(); i++){
			cubicSplineEquations.add(cubicSplineEquationsCreated.get(i));
		}
	}
	
	public static ArrayList<Double> getxNValues() {
		ArrayList<Double> copiedXnValues = new ArrayList<Double>();
		for(int i = 0; i < xNValues.size(); i++){
			copiedXnValues.add(xNValues.get(i));
		}
		return copiedXnValues;
	}
	
	public static ArrayList<String> getxNValuesStrings() {
		ArrayList<String> copiedXnValuesStrings = new ArrayList<String>();
		for(int i = 0; i < xNValuesStrings.size(); i++){
			copiedXnValuesStrings.add(xNValuesStrings.get(i));
		}
		return copiedXnValuesStrings;
	}

	public static ArrayList<Double> getFXnValues() {
		ArrayList<Double> copiedFXnValues = new ArrayList<Double>();
		for(int i = 0; i < fXnValues.size(); i++){
			copiedFXnValues.add(fXnValues.get(i));
		}
		return copiedFXnValues;
	}
	
	public static ArrayList<String> getFXnValuesStrings() {
		ArrayList<String> copiedFXnValuesStrings = new ArrayList<String>();
		for(int i = 0; i < fXnValuesStrings.size(); i++){
			copiedFXnValuesStrings.add(fXnValuesStrings.get(i));
		}
		return copiedFXnValuesStrings;
	}
	
	public static ArrayList<ArrayList<String>> getDivDifsStrings() {
		ArrayList<ArrayList<String>> copiedDivDifsStrings = new ArrayList<ArrayList<String>>();
		for(int i = 0; i < divDifsStrings.size(); i++){
			copiedDivDifsStrings.add(divDifsStrings.get(i));
		}
		return copiedDivDifsStrings;
	}
	
	public static ArrayList<String> getCubicSplineEquations() {
		ArrayList<String> copiedCubicSplineEquations = new ArrayList<String>();
		for(int i = 0; i < cubicSplineEquations.size(); i++){
			copiedCubicSplineEquations.add(cubicSplineEquations.get(i));
		}
		return copiedCubicSplineEquations;
	}
	
	
}

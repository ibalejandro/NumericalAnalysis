package com.numericalanalysis.napp;


import java.util.ArrayList;
//import android.os.Parcel;
//import android.os.Parcelable;




public class Matrix /*implements Parcelable*/{

	public static double[][] matrixAb;
	public static double[][] matrixA;
	public static double[] vectorB;
	public static double[] newVectorB;
	public static double[][] identityMatrix;
	public static double [] [][] stageByStageMatrices;
	public static double [] [][] stageByStageMatricesL;
	public static double [] [][] stageByStageMatricesU;
	public static ArrayList<ArrayList<String>> xNColumnsStrings;
	public static ArrayList<ArrayList<String>> inverseColumnsStrings;
	
	public Matrix(ArrayList<ArrayList<Double>> matrixAa, ArrayList<Double> vectorBa){
		matrixA = new double[matrixAa.size()][matrixAa.size()];
		vectorB = new double[vectorBa.size()];
		newVectorB = new double[vectorBa.size()];
		matrixAb = new double[matrixAa.size()][matrixAa.size()+1];
		identityMatrix = new double[matrixAa.size()][matrixAa.size()];
		fillMatrices(matrixAa, vectorBa);
		fillMatrixAb();
		fillIdentityMatrix();
	}
	
	public Matrix(double [] [][] stageByStageMatricesCreated, String matType){
		if(matType.equals("Gaussian")){
			stageByStageMatrices = new double [stageByStageMatricesCreated.length] 
					  						  [stageByStageMatricesCreated.length] 
					  						  [stageByStageMatricesCreated.length + 1];
		}else if(matType.equals("L")){
			stageByStageMatricesL = new double [stageByStageMatricesCreated.length] 
					  						   [stageByStageMatricesCreated.length - 1] 
					  						   [stageByStageMatricesCreated.length - 1];
			
		}else{
			stageByStageMatricesU = new double [stageByStageMatricesCreated.length] 
					   						   [stageByStageMatricesCreated.length - 1] 
					   						   [stageByStageMatricesCreated.length - 1];
		}
		
		fillStageByStageMatrices(stageByStageMatricesCreated, matType);
	}
	
	public Matrix(ArrayList<ArrayList<String>> xNColumnsStringsCreated){
		xNColumnsStrings = new ArrayList<ArrayList<String>>();
		fillXNColumnsStrings(xNColumnsStringsCreated);
	}
	
	public Matrix(ArrayList<ArrayList<Double>> inverseColumnsCreated, boolean matInverse){
		inverseColumnsStrings = new ArrayList<ArrayList<String>>();
		fillInverseColumnsStrings(inverseColumnsCreated);
	}
	
	/*
	public Matrix(){
		
	}
	
	private Matrix(Parcel in){
		in.readDoubleArray(this.vectorB);
	}
	 */
	public void fillMatrices(ArrayList<ArrayList<Double>> matrixAa, ArrayList<Double> vectorBa){
		for (int i = 0; i < matrixAa.size(); i++) {
			for (int j = 0; j < matrixAa.size(); j++) {
				matrixA[i][j] = matrixAa.get(i).get(j);
			}
		}
		for (int r = 0; r < vectorBa.size(); r++) {
			vectorB[r] = vectorBa.get(r);
			newVectorB[r] = vectorBa.get(r);
		}
	}
	
	public void fillMatrixAb(){
		for(int i = 0; i < matrixAb.length; i++){
			for(int j = 0; j < matrixAb.length +1; j++){
				if(j == matrixAb.length){
					matrixAb[i][j] = vectorB[i];		
				}else{
					matrixAb[i][j] = matrixA[i][j];
				} 
			}
		}
	}
	
	public void fillIdentityMatrix(){
		for(int i = 0; i < identityMatrix.length; i++){
			for(int j = 0; j < identityMatrix.length; j++){
				if(i == j){
					identityMatrix[i][j] = 1.0;	
				}else{
					identityMatrix[i][j] = 0.0;
				}
			}
		}
	}
	
	public static int[] fillMarks(int n){
		int[] marks = new int[n];
		for(int i = 0; i < n; i++){
			marks[i] = i+1;
		}
		return marks;
	}
	
	public void fillStageByStageMatrices(double [] [][]stageByStageMatricesCreated, String matType){
		for (int i = 0; i < stageByStageMatricesCreated.length; i++) {
			if(matType.equals("Gaussian")){
				stageByStageMatrices[i] = stageByStageMatricesCreated[i];
			}else if(matType.equals("L")){
				stageByStageMatricesL[i] = stageByStageMatricesCreated[i];
			}else{
				stageByStageMatricesU[i] = stageByStageMatricesCreated[i];
			}
		}
	}
	
	public void fillXNColumnsStrings(ArrayList<ArrayList<String>> xNColumnsStringCreated){
		for (int i = 0; i < xNColumnsStringCreated.size(); i++) {
			xNColumnsStrings.add(xNColumnsStringCreated.get(i));
		}
	}
	
	public void fillInverseColumnsStrings(ArrayList<ArrayList<Double>> inverseColumnsCreated){
		for (int i = 0; i < inverseColumnsCreated.size(); i++) {
			ArrayList<String> inverseColumnStrings = new ArrayList<String>();
			for(int j = 0; j < inverseColumnsCreated.size(); j++){
				inverseColumnStrings.add(String.valueOf(inverseColumnsCreated.get(i).get(j)));
			}
			inverseColumnsStrings.add(inverseColumnStrings);
		}
	}
	
	public static double[][] changeRows(double[][] Ab, int maxRow, int k){
		if (k != maxRow) {
			for(int j = 0; j < Ab[0].length; j++){
				double aux = Ab[k][j];
				Ab[k][j] = Ab[maxRow][j];
				Ab[maxRow][j] = aux;
			}
		}
		return Ab;
	}

	public static double[][] changeColumns(double[][] Ab, int maxColumn, int k){
		if (k != maxColumn) {
			for(int i = 0; i < Ab.length; i++){
				double aux = Ab[i][k];
				Ab[i][k] = Ab[i][maxColumn];
				Ab[i][maxColumn]= aux;
			}
		}
		return Ab;
	}
	
	public static double[] calculateMaxValuesRows(double[][] Ab, int n){
		double[] maxValuesRows = new double[n];
		double max = 0;
		for (int r = 0; r < n; r++){ // Row
			for (int s = 0; s < n; s++){ // Column
				if ( Math.abs(Ab[r][s]) > max){
					max = Math.abs(Ab[r][s]);
				}
			}
			maxValuesRows[r] = max;
		}
		return maxValuesRows;	
	} 	

	public static int[] changeMarks(int[] marks, int maxColum, int k){
		int aux = marks[maxColum];
		marks[maxColum] = marks[k];
		marks[k] = aux;	
		return marks;
	}
	
	public static double[] changeMaxValuesRows(double[] maxValuesRows, int maxRow, int k){
		double aux = maxValuesRows[maxRow];
		maxValuesRows[maxRow] = maxValuesRows[k];
		maxValuesRows[k] = aux;	
		return maxValuesRows;
	}
	
	public static double[] regressiveSubstitutionElimination(double[][] Ab, int n){
		double[] x = new double[n];
		x[n-1] = Ab[n-1][n] / Ab[n-1][n-1];
		for (int i = n-2; i >= 0; i--){
			double sum = 0;
			for (int p = i+1; p < n; p++){
				sum = sum + Ab[i][p] * x[p];
			}
			x[i] = (Ab[i][n] - sum)/Ab[i][i];
		}
		return x;	
	}
	
	public static double [] regressiveSubstitutionLU(double [][] U, double [] z,  int n){
		double[] x = new double[n];
		x[n-1] = z[n-1] / U[n-1][n-1];
	    for (int i = n-2; i >= 0; i--){
	    	double acum = 0;
	    	for (int j = i+1; j < n; j++){
	    		acum = acum + (U[i][j]*x[j]);
	    	}
	    	x[i] = (z[i] - acum) / U[i][i];
	    }
	    return x;
	}

	
	public static double [] progressiveSubstitution(double [][] L, double [] b, int n){
		double [] z = new double [n];
		z[0] = b[0] / L[0][0];
		for (int i = 1; i < n; i++){
			double acum = 0;
			for (int j = i-1; j >= 0; j--){
				acum = acum + (L[i][j]*z[j]);
			}
			z[i] = (b[i] - acum) / L[i][i];
		}
		return z;
	}

	public static double[][] getMatrixAb() {
		double [][] copiedMatrixAb = new double [matrixAb.length][matrixAb.length+1];
		for(int i = 0; i < matrixAb.length; i++){
			for(int j = 0; j < matrixAb.length + 1; j++){
				copiedMatrixAb[i][j] = matrixAb[i][j];
			}
		}
		return copiedMatrixAb;
	}

	public static double[][] getMatrixA() {
		return matrixA;
	}

	public static double[] getVectorB() {
		return vectorB;
	}
	
	public static double [][] getIdentityMatrix() {
		double [][] copiedIdentityMatrix = new double [identityMatrix.length][identityMatrix.length];
		for(int i = 0; i < identityMatrix.length; i++){
			for(int j = 0; j < identityMatrix.length; j++){
				copiedIdentityMatrix[i][j] = identityMatrix[i][j];
			}
		}
		return copiedIdentityMatrix;
    }
	
	public static double [] [][] getStageByStageMatrices() {
		return stageByStageMatrices;
    }
	
	public static double [] [][] getStageByStageMatricesL() {
		return stageByStageMatricesL;
    }
	
	public static double [] [][] getStageByStageMatricesU() {
		return stageByStageMatricesU;
    }
	
	public static ArrayList<ArrayList<String>> getXNColumnsStrings() {
		return xNColumnsStrings;
    }

	public static double[] getNewVectorB() {
		return newVectorB;
	}
	
	public static void setNewVectorB(double[] newVectorB) {
		Matrix.newVectorB = newVectorB;
	}
	
	public static ArrayList<ArrayList<String>> getInverseColumnsStrings() {
		return inverseColumnsStrings;
    }
	
	
	/*
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeDoubleArray(this.vectorB);
		
	}
	public static final Parcelable.Creator<Matrix> CREATOR = new Parcelable.Creator<Matrix>() {
		public Matrix createFromParcel(Parcel in) {
			return new Matrix(in);
		}

		public Matrix[] newArray(int size) {
			return new Matrix[size];
		}
	};
	*/
}


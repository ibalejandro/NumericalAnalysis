package com.numericalanalysis.napp;


import android.os.Parcel;
import android.os.Parcelable;
import de.congrace.exp4j.Calculable;
import de.congrace.exp4j.CustomFunction;
import de.congrace.exp4j.ExpressionBuilder;


public class FunctionsEvaluator implements Parcelable {
	// Guarda el estado de las funciones construidas por el evaluador.
	// La posicion 0 corresponde a fX, la 1 a dfX, la 2 a d2fX, la 3 a gX
	private boolean[] builtFunctionsStatus = new boolean[4];
	private String[] inputFunctions = new String[4];
	public static Calculable[] builtExpressions = new Calculable[4];
	private String variable = "x";
	
	
	//Constructor para guardar las funciones ingresadas por el usuario.
	public FunctionsEvaluator(String fX, String dfX, String d2fX, String gX){
		this.inputFunctions[0] = fX;
		this.inputFunctions[1] = dfX;
		this.inputFunctions[2] = d2fX;
		this.inputFunctions[3] = gX;
		checkFunctions();
    }
	
	public FunctionsEvaluator(){
		
	}
	
	private FunctionsEvaluator(Parcel in){
		//this.comments = in.readParcelable(DuingComment.class.getClassLoader());
		//this();
		in.readBooleanArray(this.builtFunctionsStatus);
		in.readStringArray(this.inputFunctions);
		//this.inputFunctions = (String[]) in.readArray(String.class.getClassLoader());
		this.variable = in.readString();
	}
	public void checkFunctions(){
		
		for(int i = 0; i < 4; i++){
			if(!inputFunctions[i].isEmpty()){
				builtFunctionsStatus[i] = buildFunctions(inputFunctions[i],i);
			}else{
				builtFunctionsStatus[i] = false;
			}
		}
	}
	
	public boolean buildFunctions(String function, int position){
		try {
			if(function.contains("log10")){
				// Funcion personalizada para Log(x) en base 10.
				CustomFunction log10Func = new CustomFunction("log10") {
					@Override
					public double applyFunction(double... arg0) {
						return Math.log10(arg0[0]);
					}
				};
				builtExpressions[position] = new ExpressionBuilder(function)
			 							.withCustomFunction(log10Func)
			 							.withVariableNames(variable).build();
			}else{
				builtExpressions[position] = new ExpressionBuilder(function)
										   .withVariableNames(variable).build();
			}
			return true;
		}catch(Exception e){
			// No fue posible construir la funcion.
			return false;
		}
	}

	public double f(double x){
		//double y = (7.14292992) - (20.0471*x) + (19.0956*x*x) - (7.45*x*x*x) + (x*x*x*x);
		builtExpressions[0].setVariable(variable, x);
		double y = builtExpressions[0].calculate();
		return y;
	}
	
	public double dF(double x){
		//double y = (-20.0471) + (2*19.0956*x) - (3*7.45*x*x) + (4*x*x*x);
		builtExpressions[1].setVariable(variable, x);
		double y = builtExpressions[1].calculate();
		return y;
	}
	
	public double d2F(double x){
		//double y = (2*19.0956) - (3*2*7.45*x) + (4*3*x*x);
		builtExpressions[2].setVariable(variable, x);
		double y = builtExpressions[2].calculate();
		return y;
	}
	
	public double g(double x){   
		//double y = Math.log((((x*x) + (5*x) + 3) / x));
		builtExpressions[3].setVariable(variable, x);
		double y = builtExpressions[3].calculate();
		return y;
	}
	
	public boolean[] getBuiltFunctionsStatus() {
		return builtFunctionsStatus;
	}
	
	public Calculable[] getBuiltExpressions() {
		return builtExpressions;
	}

	public String[] getInputFunctions() {
		return inputFunctions;
	}
	

	public String getVariable() {
		return variable;
	}

	public void setVariable(String variable) {
		this.variable = variable;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeBooleanArray(this.builtFunctionsStatus);
		dest.writeStringArray(this.inputFunctions);
		dest.writeString(this.variable);	
	}
	public static final Parcelable.Creator<FunctionsEvaluator> CREATOR = new Parcelable.Creator<FunctionsEvaluator>() {
		public FunctionsEvaluator createFromParcel(Parcel in) {
			return new FunctionsEvaluator(in);
		}

		public FunctionsEvaluator[] newArray(int size) {
			return new FunctionsEvaluator[size];
		}
	};
	}

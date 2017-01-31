


public abstract class Solver {

	int param1;
	int param2;
	double[] otherParams;
	int type;

	public Solver(int t, int p1, int p2, double[] op){

		type = t;
		param1 = p1;
		param2 = p2;
		otherParams = new double[op.length];
		for (int i = 0; i < op.length; i++)
			otherParams[i] = op[i];

	}

	protected abstract double[] function(double[] density, double[] params);


	public double[][] timeSeries(double[] density, double p1, double p2, double T, double step, int maxPoints){
		int steps = (int)(T / step);
		int skip = steps/maxPoints + 1;
		double[][] ret = new double[density.length][steps / skip + 1];
		for (int i = 0; i < density.length; i++)
			ret[i][0] = density[i];
		for (int i = 1; i < ret[0].length; i++){
			double[] temp = new double[density.length];
			for (int j = 0; j < density.length; j++)
				temp[j] = ret[j][i-1];
			for (int j = 0; j < skip; j++)
				temp = RK4Step(temp, p1, p2, step);
			for (int j = 0; j < density.length; j++)
				ret[j][i] = temp[j];
		}
		return ret;
	}


	private double[] RK4Step(double[] density, double p1, double p2, double step){

		double[] parameters = new double[otherParams.length + 2];
		int j = 0;
		for (int i = 0; i < parameters.length; i++){
			if (i == param1)
				parameters[i] = p1;
			else if (i == param2)
				parameters[i] = p2;
			else
				parameters[i] = otherParams[j++];
		}

		int numpop = density.length;
		double[] temp = new double[numpop];

		double[] k1 = function(density, parameters);

		for (int i = 0; i < numpop; i++)
		  temp[i] = density[i] + 0.5 * step * k1[i];
		double[] k2 = function(temp, parameters);

		for (int i = 0; i < numpop; i++)
		  temp[i] = density[i] + 0.5 * step * k2[i];
		double[] k3 = function(temp, parameters);

		for (int i = 0; i < numpop; i++)
		  temp[i] = density[i] + step * k3[i];
		double[] k4 = function(temp, parameters);

		double[] ret = new double[numpop];
		for (int i = 0; i < numpop; i++)
		  ret[i] = density[i] + step * (k1[i] + 2*k2[i] + 2*k3[i] + k4[i]) / 6; 

		return ret;

	}








}
public class WrightSolver extends Solver {




	public WrightSolver(int t, int p1, int p2, double[] op){
		super(t, p1, p2, op);
	}

	protected double[] function(double[] density, double[] params){
		switch (type) {
			case 0:	return huismanmod(density, params);
			case 1: return abrams(density, params);
			case 2: return holt(density, params);
		}
		return density;
	}







// Huisman & Weissing
// The density vector is in the form 
// (N1, N2, ..., N_{numpop/2}, R1, R2, ..., R_{numpop/2}). Parameters are in the
// form (r1, r2, ..., K11, K12, ..., m1, m2, ..., D, S1, S2, ..., c11, c12, ...)

// Monod equation
double monod(int j, int i, double R, double[] parameters, int numpop){

	return parameters[i+1] * R / (parameters[numpop/2 + (numpop/2) * j + i] + R);

}

// min[p1i(R1), p2i(R2), p3i(R3), ...]
double minGrowth(int i, double[] density, double[] parameters){

	int numpop = density.length;
	double minG = 1.0;
	for (int j = 0; j < numpop/2; j++)
		minG *= monod(j, i, density[numpop/2 + j], parameters, numpop);
	return minG;

}

double[] huisman(double[] density, double[] parameters){

	int numpop = density.length;
	double[] f = new double[numpop];

	double[] min = new double[numpop/2];
	for (int i = 0; i < numpop/2; i++){
		min[i] = minGrowth(i, density, parameters);
		f[i] = density[i]*(min[i] - parameters[numpop/2 + numpop*numpop/4 + i]);
	}


	for (int j = 0; j < numpop/2; j++){
		f[numpop/2 + j] = parameters[numpop + numpop*numpop/4] * (parameters[numpop + numpop*numpop/4 + 1 + j] - density[numpop/2 + j]);
			for (int i = 0; i < numpop/2; i++)
				f[numpop/2 + j] -= parameters[3*numpop/2 + numpop*numpop/4 + 1 + (numpop/2)*j + i] * density[i] * min[i];
	}

	return f;

}



double[] huismanmod(double[] density, double[] parameters){

	int numpop = density.length;
	double[] f = new double[numpop];

	double[] min = new double[(numpop-1)/2];
	for (int i = 0; i < (numpop-1)/2; i++){
		min[i] = minGrowth(i, density, parameters);
		f[i] = density[i]*(min[i] - parameters[(numpop-1)/2 + (numpop-1)*(numpop-1)/4 + i] - parameters[(numpop-1)*(numpop-1)/2 + 3*(numpop-1)/2 + 1 + i] * density[numpop-1]);
	}


	for (int j = 0; j < (numpop-1)/2; j++){
		f[(numpop-1)/2 + j] = parameters[(numpop-1) + (numpop-1)*(numpop-1)/4] * (parameters[(numpop-1) + (numpop-1)*(numpop-1)/4 + 1 + j] - density[(numpop-1)/2 + j]);
		for (int i = 0; i < (numpop-1)/2; i++)
			f[(numpop-1)/2 + j] -= parameters[3*(numpop-1)/2 + (numpop-1)*(numpop-1)/4 + 1 + ((numpop-1)/2)*j + i] * density[i] * min[i];
	}

	f[numpop-1] = -parameters[(numpop-1)*(numpop-1)/2 + 5*(numpop-1)/2 + 1];
	for (int i = 0; i < (numpop-1)/2; i++)
		f[numpop-1] += parameters[(numpop-1)*(numpop-1)/2 + 3*(numpop-1)/2 + 1 + i] * parameters[(numpop-1)*(numpop-1)/2 + 4*(numpop-1)/2 + 1 + i] * density[i];
	f[numpop-1] *= density[numpop-1];

  return f;

}








// Noonburg & Abrams 2005, page 323
// The density vector is in the form (R, N1, N2, P). Parameters are in the form
// (r, K, b1, b2, c1, c2, d1, d2, D, e1, e2, s1, s2).
// (0  1  2   3   4   5   6   7   8  9   10  11  12)
double[] abrams(double[] density, double[] parameters){

  double[] f = new double[4];
  f[0] = parameters[0] * density[0] * (1 - density[0] / parameters[1])
         - parameters[4] * density[1] * density[0]
         - parameters[5] * density[2] * density[0];

  f[1] = density[1] * (parameters[2] * parameters[4] * density[0]
         - parameters[6] - parameters[11] * density[3]);

  f[2] = 1.0 * density[2] * (parameters[3] * parameters[5] * density[0]
         - parameters[7] - parameters[12] * density[3]);

  f[3] = density[3] * (parameters[9] * parameters[11] * density[1]
         + parameters[10] * parameters[12] * density[2] - parameters[8]);

  return f;

}



// Holt 1977, page 204
// The density vector is in the form (R1, R2, P). Parameters are in the form
// (r1, r2, K1, K2, a1, a2, b1, b2, B, C).
// (0   1   2   3   4   5   6   7   8  9)

double[] holt(double[] density, double[] parameters){

  double[] f = new double[3];
  f[0] = parameters[10] * density[0] * (parameters[0]*(1 - density[0]/parameters[2]) - parameters[4]*density[2]);
  f[1] = density[1] * (parameters[1]*(1 - density[1]/parameters[3]) - parameters[5]*density[2]);
  f[2] = parameters[11] * density[2] * parameters[8] * (parameters[4]*parameters[6]*density[0] + parameters[5]*parameters[7]*density[1] - parameters[9]);

  return f;

}








}
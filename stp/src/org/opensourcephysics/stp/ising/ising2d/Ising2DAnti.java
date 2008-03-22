package org.opensourcephysics.stp.ising.ising2d;

public class Ising2DAnti extends Ising2D{
	double Sm_acc = 0, Sm2_acc = 0;
	
	public void accumulate_EM(){
		super.accumulate_EM();
		double sm = getStaggeredM();
		Sm_acc += sm;
		Sm2_acc += sm*sm;
	}
	public double getStaggeredM(){
		double sm = 0;
		  for (int i = 0; i < L; ++i){
			  for (int j = 0; j < L; ++j){
				  if( (i + j)%2 == 0){
					  sm += spin[i][j];
				  }
			  }
		  }
		  return sm;
	}

	public double Staggeredsusceptibility() {
		double M2_avg = Sm2_acc/mcs;
	    double M_avg = Sm_acc / mcs;
	    return (M2_avg - M_avg*M_avg) / (T*N);
	}
	public void resetData(){
		super.resetData();
		Sm_acc = 0;
		Sm2_acc = 0;
	}
}

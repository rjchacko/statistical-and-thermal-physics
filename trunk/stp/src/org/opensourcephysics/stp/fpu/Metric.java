package org.opensourcephysics.stp.fpu;
/**
 * The Fermi-Pasta-Ulam model.
 *
 * @author Hui Wang
 * @version 1.0
 * @created 11/01/2006
 */
public class Metric {
	int numOfPoints;
	double []data,data_av;
	int counter;
	double metric;
	double m0 = 1.0;
	
	public Metric(int n){
		numOfPoints = n;
		data = new double[n];
		data_av = new double[n];
		for(int i = 1; i < n; i++)data[i] = data_av[i] = 0.0;
		counter = 0;
	}
	
	public double append(double[]e){
		counter++;
		
		for(int i = 1; i < e.length; i++){
			data[i] += e[i];
			data_av[i] = data[i]/counter;
		}
		double s = 0.0, s2 = 0.0;
		
		for(int i = 1; i < e.length; i++){
			s += data_av[i];
			s2 += data_av[i]*data_av[i];
		}
		s /= (e.length - 1);
		s2 /= (e.length - 1);
		metric = s2 - s*s;
		metric /= e.length;
		metric /= m0;
		return metric;
		
	}
	public void setM0(double m){
		m0 = m;
		//System.out.println("#Zero metric is " + 1/m);
	}
	public void zeroData(){
		for(int i = 0; i < numOfPoints; i++)data[i] = data_av[i] = 0.0;
	}
	public void reset(){
		zeroData();
		counter = 0;
		m0 = 1;
	}
}

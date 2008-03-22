package org.opensourcephysics.stp.util;

public class Rdf {
	public double bin = 0.1;
	public double[] x,y;
	public double Lx,Ly;
	public double rmax;
	public int N,nbins;
	public boolean pbc = true;
	public int ncorr = 0;
	
	double[] gr;
	public double[] rx,ngr;	//normalized gr
	
	public Rdf(){
		
	}
	public Rdf(double lx, double ly){
		initialize(lx,ly,0.1);
	}
	
	public void initialize(double lx, double ly, double _bin){
		Lx = lx;
		Ly = ly;
		rmax = Lx > Ly?0.5*Lx:0.5*Ly;
		bin = _bin;
		nbins = (int)(rmax/bin);
		gr = new double[nbins];
		ngr = new double[nbins];
		rx = new double[nbins];
		for(int i = 0; i < nbins; i++){
			rx[i] = i*bin;
			gr[i] = 0.0;	
		}
	}
	public void reset(){
		for(int i = 0; i < nbins; i++){
			rx[i] = i*bin;
			gr[i] = 0.0;	
		}
		ncorr = 0;
	}
	public void append(double[] x, double[] y){
		N = x.length;
		ncorr++;
		
		for(int i = 0; i < N; i++){
			for(int j = i + 1; j < N; j++){
				double dx = x[i] - x[j];
				double dy = y[i] - y[j];
				if(pbc){
					dx = separation(dx,Lx);
					dy = separation(dy,Ly);
				}
				double dr = dx*dx + dy*dy;
				dr = Math.sqrt(dr);
				if(dr < rmax){
					int nbin = (int)(dr/bin);
					gr[nbin]++;
				}
			}
		}	
	}
	
	public void normalize(){
		double area;
		double pi = Math.PI;
		double r = 0;
		double rho = N/Lx/Ly;
		double norm = 0.5*rho*ncorr*N;
		
		while(r < rmax){
			area = pi*((r + bin)*(r + bin) - r*r);
			int i = (int)(r/bin);
			area *= norm;
			ngr[i] = gr[i]/area;
			r += bin;
		}
	}
	public void setBinSize(double _bin){
		bin = _bin;
		nbins = (int)(rmax/bin) + 1;
	}
	public void setPbc(boolean p){
		pbc = p;
	}
	public double separation(double dx, double lx){
		if(dx > 0.5*lx)return dx - lx;
		if(dx < -0.5*lx)return dx + lx;
		return dx;
	}
}

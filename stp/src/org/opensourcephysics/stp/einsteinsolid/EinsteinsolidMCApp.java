package org.opensourcephysics.stp.einsteinsolid;


import org.opensourcephysics.frames.PlotFrame;
import org.opensourcephysics.controls.*;
import java.text.NumberFormat;
import javax.swing.JButton;
import java.util.*;
	/*
	 *
	 * @author Hui Wang
	 * @created Oct 20, 2006
	 */
	public class EinsteinsolidMCApp extends AbstractSimulation
	{
		int N, acceptedMoves = 0, mcs = 0;
		int[] e;
		double systemEnergy, systemEnergyAccumulator = 0;
		double systemEnergyAccumulator2 = 0;
		double[] Ea, Ca, Ta;		//array for curves
		
		int npoints = 0;
		double tolerance = 0.01;
		static final int nmaxTrials = 1000;
		static final int nequil = 10000;
		
		double T;	//system temperature
		JButton AcceptButton;
		NumberFormat nf;
	    Random rnd = new Random();
	    PlotFrame plotFrame = new PlotFrame("mcs", "E", "E");
	    PlotFrame ETFrame = new PlotFrame("T", "E", "E .vs. T");
	    PlotFrame CTFrame = new PlotFrame("T", "Cv", "Cv .vs. T");

	public void doStep()
	{
		T = control.getDouble("T");
		for (int i = 0; i < 10000 / N; i++)
		{
			for (int j = 0; j < N; ++j)
			{
				int particleIndex = (int) (Math.random() * N);
				int de = 2*rnd.nextInt(2) - 1;//get -1 or 1
				if( (de == 1 && rnd.nextDouble() < Math.exp(-de/T)) || (de == -1 && e[particleIndex] > 0)){
					acceptedMoves++;
					e[particleIndex] += de;
					systemEnergy += de;
				}
				if(mcs > nequil){
					systemEnergyAccumulator += systemEnergy;
					systemEnergyAccumulator2 += systemEnergy*systemEnergy;
				}
			}
			mcs++;
		}
		if(mcs < nequil)return;
      	double norm = 1.0 / (mcs * N);
		control.println("mcs = " + nf.format(mcs));
		control.println("<E> = " + nf.format(meanE()));
		control.println("C_v = " + nf.format(heatCapacity()));
		
		plotFrame.append(0,mcs,meanE());
		control.println("acceptance ratio = "
						+ nf.format(acceptedMoves * norm));
	}
	public void initialize()
	{
		N = control.getInt("N");
		T = control.getDouble("T");
      	e = new int[N];
      	for(int i = 0; i < N; i++)e[i] = 1;
      	clearEnergies();
		mcs = 0;
		acceptedMoves = 0;
		control.clearMessages();
		plotFrame.setPreferredMinMax(0, 10, 0, 10);
		plotFrame.setAutoscaleX(true);
		plotFrame.setAutoscaleY(true);
		ETFrame.setPreferredMinMaxX(0, 10);
		CTFrame.setPreferredMinMaxX(0, 10);
		ETFrame.setAutoscaleX(true);
		CTFrame.setAutoscaleX(true);
		ETFrame.setPreferredMinMaxY(0, 10);
		CTFrame.setPreferredMinMaxY(0, 10);
		ETFrame.setAutoscaleY(true);
		CTFrame.setAutoscaleY(true);
		this.delayTime = 0;
		
	}
	public void clearEnergies(){
		systemEnergy = 0;
		for(int i = 0; i < N; i++)systemEnergy += e[i];
		systemEnergyAccumulator = 0;
		systemEnergyAccumulator2 = 0;
	}
	public void reset()
	{
		control.setValue("N", 20);
		control.setAdjustableValue("T", 2.0);
		control.clearMessages();
	}
		
	public void AcceptResults(){		
	   Ea[npoints] = meanE();
	   Ca[npoints] = heatCapacity();
	   Ta[npoints] = T;
	   npoints++;
	   if(npoints > nmaxTrials){
		   control.print("Too many points");
		   System.exit(-1);
	   }
	   plot();
	   clearEnergies();
	   mcs = 0;
	   acceptedMoves = 0;
	   plotFrame.clearData();
	}

	public double meanE(){
		double norm = 1.0 / ((mcs - nequil) * N);
		return systemEnergyAccumulator * norm;
	}
	public double heatCapacity(){
		double norm = 1.0 / ((mcs - nequil) * N);
		double e2 = systemEnergyAccumulator*systemEnergyAccumulator;
		return (systemEnergyAccumulator2 * norm - e2*norm*norm)/T/T;	
	}
	
	public void plot(){
	   for(int i = 0; i < npoints; i++){
		   ETFrame.append(2,Ta[i],Ea[i]);
		   CTFrame.append(2,Ta[i],Ca[i]);
	   }
	   ETFrame.render();
	   CTFrame.render();
	}
	public EinsteinsolidMCApp()
	{
		Ea = new double[nmaxTrials];
		Ca = new double[nmaxTrials];
		Ta = new double[nmaxTrials];

		nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(3);
	}

	public static void main(String[] args)
	{
		EinsteinsolidMCApp application = new EinsteinsolidMCApp();
	    SimulationControl control=SimulationControl.createApp(application,args);
		application.AcceptButton = control.addButton("AcceptResults","Accept E and Cv");
	}
}

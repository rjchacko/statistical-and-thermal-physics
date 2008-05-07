package org.opensourcephysics.stp.hdmc;

import org.opensourcephysics.controls.*;
import org.opensourcephysics.display.GUIUtils;
import org.opensourcephysics.frames.DisplayFrame;
import org.opensourcephysics.frames.PlotFrame;
import org.opensourcephysics.stp.util.Rdf;

public class HDMCApp extends AbstractSimulation {
	HDMC mc = new HDMC();
	DisplayFrame display = new DisplayFrame("Hard Disks");
	PlotFrame grFrame = new PlotFrame("r", "g(r)", "Radial distribution function");
	Rdf gr = new Rdf();
	int timestep=0;
	
	public static void main(String[] args) {
		SimulationControl control = SimulationControl.createApp(new HDMCApp(),args);
		control.addButton("resetData", "Reset Data");
	}	
	
	public void reset(){
	    control.setValue("nx", 8);
	    control.setValue("ny", 8);
	    control.setAdjustableValue("Lx", 18);
	    control.setAdjustableValue("Ly", 18);
	    control.setAdjustableValue("Step Size", 0.1);
	    OSPCombo combo = new OSPCombo(new String[] {"triangular","rectangular","random"},0);  // second argument is default
	    control.setValue("initial configuration", combo);
	    control.setAdjustableValue("compression",0.9);
	    enableStepsPerDisplay(true);
	    super.setStepsPerDisplay(10);  // draw configurations every 10 steps
	    display.setSquareAspect(true); // so particles will appear as circular disks
	    gr.reset();
	    mc.steps=0;
	}
	
	public void initialize(){
	   mc.nx = control.getInt("nx"); // number of particles per row
	   mc.ny = control.getInt("ny"); // number of particles per column   
	   mc.Lx = control.getDouble("Lx");
	   mc.Ly = control.getDouble("Ly");
	   mc.initialConfiguration = control.getString("initial configuration");
	   mc.stepSize=control.getDouble("Step Size");
	   
	   mc.initialize();
	   gr.initialize(mc.Lx,mc.Ly,0.1);
	   grFrame.setPreferredMinMaxX(0, 0.5*mc.Lx);
	   display.addDrawable(mc);
	   display.setPreferredMinMax(0, mc.Lx, 0, mc.Ly); 
	}
	
	public void startRunning() {
	   double Lx = control.getDouble("Lx");
	   double Ly = control.getDouble("Ly");
	   mc.s=control.getDouble("compression");
	   if((Lx!=mc.Lx)||(Ly!=mc.Ly)) {
	     mc.Lx = Lx;
	     mc.Ly = Ly;
	     display.setPreferredMinMax(0, Lx, 0, Ly);
	     resetData();
	   }
	 }
	  
	protected void doStep() {
		 mc.step(); 
		 if(mc.steps%mc.N==0)control.println(mc.steps/mc.N + " mcs");
		 gr.append(mc.x, mc.y);
		 gr.normalize();
		 grFrame.clearData();
		 grFrame.append(2, gr.rx, gr.ngr);	   
		 grFrame.render();
	}
	
	public void stop() {
		 control.println("Density = "+decimalFormat.format(mc.rho));
		 control.println("Number of time steps = "+mc.steps);
	}
	
	public void resetData() {
		 GUIUtils.clearDrawingFrameData(false); // clears old data from the plot frames
	 }
}

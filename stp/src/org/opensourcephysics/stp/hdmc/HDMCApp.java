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
		OSPCombo combo = new OSPCombo(new String[] {"64","128","256"},0);  // second argument is default
	    control.setValue("number of particles", combo);
	    control.setAdjustableValue("L", 40);
	    control.setAdjustableValue("step size", 0.02);
	    OSPCombo combo2 = new OSPCombo(new String[] {"triangular","rectangular","random"},1);  // second argument is default
	    control.setValue("initial configuration", combo2);
	    control.setAdjustableValue("scale lengths",0.999);
	    enableStepsPerDisplay(true);
	    super.setStepsPerDisplay(100);  // draw configurations every 10 steps
	    display.setSquareAspect(true); // so particles will appear as circular disks
	    gr.reset();
	    mc.steps=0;
	}
	
	public void initialize(){
	   String number=control.getString("number of particles");
	   if(number=="64") mc.N=64;
	   else if(number=="128")mc.N=128;
	   else mc.N=256;
	    
	   mc.L = control.getDouble("L");
	   mc.initialConfiguration = control.getString("initial configuration");
	   mc.stepSize=control.getDouble("step size");
	   
	   mc.initialize();
	   gr.initialize(mc.L,mc.L,0.1);
	   grFrame.setPreferredMinMaxX(0, 0.5*mc.L);
	   display.addDrawable(mc);
	   display.setPreferredMinMax(0, mc.L, 0, mc.L); 
	}
	
//	public void startRunning() {
//	   double L = control.getDouble("L");
//	   mc.s=control.getDouble("scale lengths");
//	   if((L!=mc.L)) {
//	     mc.L = L;
//	     display.setPreferredMinMax(0, L, 0, L);
//	     resetData();
//	   }
//	 }
	  
	protected void doStep() {
		 mc.s=control.getDouble("scale lengths");
		 mc.oneMCstep(); 
		 if(mc.steps%mc.N==0)control.println(mc.steps/mc.N + " mcs");
		 gr.append(mc.x, mc.y);
		 gr.normalize();
		 grFrame.clearData();
		 grFrame.append(2, gr.rx, gr.ngr);	   
		 grFrame.render();
	}
	
	public void stop() {
		 control.println("Density = "+ decimalFormat.format(mc.N/(mc.equivL*mc.equivL)));
	}
	
	public void resetData() {
		 GUIUtils.clearDrawingFrameData(false); // clears old data from the plot frames
	 }
}

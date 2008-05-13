package org.opensourcephysics.stp.partfunccalc;

import org.opensourcephysics.controls.*;
import org.opensourcephysics.frames.*;


public class PartFuncCalcApp extends AbstractSimulation{
PartFuncCalc ising = new PartFuncCalc();
private PlotFrame plotFrame = new PlotFrame("T", "[ln Z(T)-ln Z(0.1)]/N", "Partition Function");
private PlotFrame plotFrame2 = new PlotFrame("T", "H[T]", "Temperature Histogram");
private LatticeFrame lattice = new LatticeFrame("Ising Model");
public int displayDelay = 100;
public int n = 0;

public PartFuncCalcApp() {
	plotFrame.setPreferredMinMaxX(0, 10);
	plotFrame.setAutoscaleX(true);
	plotFrame.setAutoscaleY(true);
	plotFrame2.setPreferredMinMax(0,10,0,10);
	plotFrame2.setAutoscaleX(true);
	plotFrame2.setAutoscaleY(true);
}

public void initialize() {
	ising.initial(control.getInt("L"),control.getDouble("prob"),lattice);
	displayDelay = control.getInt("display delay");
}


public void doStep() {
    int mcs = (int)(100*ising.N/Math.sqrt(ising.lnf));
	displayDelay = control.getInt("display delay");
	for(int i = 0; i < mcs;i++) {
		if(i % displayDelay == 0) lattice.render();
		ising.doMcStep();
	}
	control.println("ln f = " + ising.lnf);
    //System.out.print("logZ latest = "+ising.lnZ[ising.currenttempindex]);
    control.println(" E accept = "+ising.acceptEnergy/(ising.tryEnergy));
    control.println(" T accept = "+ising.acceptTemperature/(ising.tryTemperature));
    plotFrame.clearData();
	plotFrame2.clearData();
	for (int j = 0; j < ising.Tsize; j++){
		if (ising.lnZ[j] > 0)
			plotFrame.append(0, ising.T[j], (ising.lnZ[j]/ising.N) - (ising.lnZ[0]/ising.N));
		if(ising.H[j] >0)
			plotFrame2.append(0, ising.T[j], ising.H[j]*ising.increment[j]);
		}
    ising.lnf /= Math.sqrt(10.0);
	ising.resetData();
    if(ising.lnf < 1.0E-9){
    	System.out.println("done");
    	stopSimulation();
    }
}


public void reset() {
    control.setValue("L", 32);
    control.setValue("prob",0.01);
    control.setAdjustableValue("display delay", 10000);
 //   enableStepsPerDisplay(true); // allow user to speed up simulation
  }

public void resetData() {
	ising.resetData();
    plotFrame.clearData();
    plotFrame2.clearData();
    plotFrame.repaint();
    plotFrame2.repaint();
    control.clearMessages();
  }


public static void main(String[] args) {
    SimulationControl control = SimulationControl.createApp(new PartFuncCalcApp());
 //   control.addButton("resetData", "Reset Data");
  }
}

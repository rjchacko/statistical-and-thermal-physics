package org.opensourcephysics.stp.oscillatornh;
/**
 * The Nose-Hoover constant temperature simulation of an harmonic oscillator.
 *
 * @author Hui Wang
 * @version 1.0
 * @created 12/21/2006
 */
import org.opensourcephysics.controls.*;
import org.opensourcephysics.frames.PlotFrame;

public class OscillatorApp extends AbstractSimulation {
	Oscillator osci = new Oscillator();
	PlotFrame phaseFrame = new PlotFrame("q","p","Nose-Hoover harmonic oscillator");
	PlotFrame energyFrame = new PlotFrame("time", "Energy",
	"Energy versus time");
	
	double mcsPerDisplay;
	
  public void doStep() {
	  osci.step(0.05);
	  phaseFrame.append(1,osci.x,osci.vx);
	  energyFrame.append(2,osci.t,osci.Et);
	}


  public void initialize() {
	  osci.x = control.getDouble("x");
	  osci.vx = control.getDouble("vx");
	  osci.Q = control.getDouble("Q");
	  energyFrame.setAutoscaleX(true);
	  this.delayTime = 0;
  }

  public void reset() {
	  control.setAdjustableValue("Q",1.0);
	  control.setAdjustableValue("x",1.0);
	  control.setAdjustableValue("vx",1.0);
	  
	  energyFrame.clearData();
  }
  public static void main(String[] args) {
	  SimulationControl.createApp(new OscillatorApp());
  }
}

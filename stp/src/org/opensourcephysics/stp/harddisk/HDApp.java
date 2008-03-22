package org.opensourcephysics.stp.harddisk;

import java.text.NumberFormat;
import org.opensourcephysics.controls.*;
import org.opensourcephysics.frames.*;
import org.opensourcephysics.stp.util.Rdf;;
/**
 * Simulates hard disks.
 *
 * @author Jan Tobochnik
 * @author Joshua Gould
 * @author Peter Sibley
 * @created December 26, 2002
 */

public class HDApp
    extends AbstractSimulation {
  PlotFrame temperatureFrame = new PlotFrame("time", "Temperature",
                                             "Temperature versus time");
  PlotFrame pressureFrame = new PlotFrame("time", "pressure",
                                          "Pressure versus time");
  DisplayFrame displayFrame = new DisplayFrame("HD Display");
  HistogramFrame histogramFrame = new HistogramFrame("v_x", "P(v_x)",
	"P(v_x) versus v_x");
  
  Rdf gr = new Rdf();
  PlotFrame grFrame = new PlotFrame("r", "g(r)", "Radial distribution function");

  HD hd;
  NumberFormat numberFormatTwoDigits = NumberFormat.getInstance();
  NumberFormat numberFormatFourDigits = NumberFormat.getInstance();

  public HDApp() {
    numberFormatTwoDigits.setMaximumFractionDigits(2);
    numberFormatTwoDigits.setMinimumFractionDigits(2);
    numberFormatFourDigits.setMaximumFractionDigits(4);
    numberFormatFourDigits.setMinimumFractionDigits(4);
    hd = new HD();
    displayFrame.setPreferredMinMax( -0.1 * hd.Lx, 1.1 * hd.Lx,
                                    -0.1 * hd.Lx, 1.1 * hd.Lx);
    displayFrame.addDrawable(hd);
    temperatureFrame.setAutoscaleX(true);
   // temperatureFrame.setPreferredMinMaxY(0, 2);
   // pressureFrame.setPreferredMinMaxY(0, 2);
    
    histogramFrame.setAutoscaleX(true);
	histogramFrame.setAutoscaleY(true);
	histogramFrame.addDrawable(hd.getVelocityHistogram());
  }

  public void initialize() {
      hd.setNumberOfDisks(control.getInt("N"));
      hd.setLx(control.getDouble("Lx"));
      hd.setLy(control.getDouble("Ly"));
      hd.setVelocityMax(control.getDouble("Temperature"));
      hd.initialize();
      temperatureFrame.clearData();
      pressureFrame.clearData();
      
      gr.initialize(hd.Lx,hd.Ly,0.1);
      
     //double tmax = control.getDouble("Maximum for temperature axis");
    //double pmax = control.getDouble("Maximum for pressure axis");
    //temperatureFrame.setPreferredMinMaxY(0, tmax);
    //pressureFrame.setPreferredMinMaxY(0, pmax);
    renderPanels();
  }

  public void renderPanels() {
    //double tmax = control.getDouble("Maximum for temperature axis");
    //double pmax = control.getDouble("Maximum for pressure axis");
    temperatureFrame.append(0, hd.getTime(), hd.getInstantaneousTemperature());
    pressureFrame.append(0, hd.getTime(), hd.getInstantanousPressure());
    //temperatureFrame.setPreferredMinMaxY(0, tmax);
    //pressureFrame.setPreferredMinMaxY(0, pmax);
    displayFrame.setPreferredMinMax( -0.1 * hd.Lx, 1.1 * hd.Lx,
                                    -0.1 * hd.Lx, 1.1 * hd.Lx);
    histogramFrame.render();    
  }

  public void doStep() {
    hd.step();
    gr.append(hd.positionX, hd.positionY);

    gr.normalize();
    grFrame.clearData();
    grFrame.append(2, gr.rx, gr.ngr);	   
    grFrame.render();
    renderPanels();
  }

 
  public void reset() {
    control.clearMessages();
    control.setValue("N", 64);
    control.setValue("Lx", 18.0);
    control.setValue("Ly", 18.0);
    control.setValue("Temperature", 1.0);
    gr.reset();
	
    //control.setValue("Maximum for temperature axis", 2.0);
    //control.setValue("Maximum for pressure axis", 2.0);
    this.delayTime = 0;
  }

  public void zeroAverages(){
	  hd.zeroAverages();
	  control.clearMessages();
  }
  public void stop(){
	    output();   
  }
  public void output() {
    //control.println("Density = " + numberFormatFourDigits.format(hd.rho));
    control.println("Temperature = "
                    + numberFormatFourDigits.format(hd.getTemperature()));
    control.println("<PA/NkT> = "
                    + numberFormatFourDigits.format(hd.getMeanPressure()));
    control.println("Mean free path = " + numberFormatFourDigits.format(hd.getMFP()));
    control.println("Mean collision time = " + numberFormatFourDigits.format(hd.getMFT()));
    
  }

  public static void main(String[] args) {
	SimulationControl control = SimulationControl.createApp(new HDApp(),args);
    control.addButton("zeroAverages", "Zero averages");
  }
}

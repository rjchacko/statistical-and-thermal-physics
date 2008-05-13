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
  HistogramFrame histogramFrame = new HistogramFrame("v_x", "H(v_x)",
	"H(v_x) versus v_x");
  
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
    
    temperatureFrame.setPreferredMinMaxX(0,10);
    temperatureFrame.setAutoscaleX(true);
    temperatureFrame.setPreferredMinMaxY(0, 10);
    temperatureFrame.setAutoscaleY(true);
    
    pressureFrame.setPreferredMinMaxX(0,10);
    pressureFrame.setAutoscaleX(true);
    pressureFrame.setPreferredMinMaxY(0, 2);
    
    histogramFrame.setAutoscaleX(true);
    histogramFrame.setPreferredMinMaxY(0, 10);
	histogramFrame.setAutoscaleY(true);
	histogramFrame.addDrawable(hd.getVelocityHistogram());
	
	grFrame.setPreferredMinMaxX(0, 10);
	grFrame.setAutoscaleX(true);
	grFrame.setPreferredMinMaxY(0,10);
	grFrame.setAutoscaleY(true);
  }

  public void initialize() {
      hd.setNumberOfDisks(control.getInt("N"));
      hd.setLx(control.getDouble("Lx"));
      hd.setLy(control.getDouble("Ly"));
      hd.setVelocityMax(control.getDouble("temperature"));
      hd.initialConfiguration = control.getString("initial configuration");
      hd.initialize();
      temperatureFrame.clearData();
      pressureFrame.clearData();
      
      gr.initialize(hd.Lx,hd.Ly,0.1);
      displayFrame.setPreferredMinMax( -0.1 * hd.Lx, 1.1 * hd.Lx,
              -0.1 * hd.Lx, 1.1 * hd.Lx);
//      renderPanels();
  }

  public void renderPanels() {
    temperatureFrame.append(0, hd.getTime(), hd.getInstantaneousTemperature());
    pressureFrame.append(0, hd.getTime(), hd.getInstantanousPressure());
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
    control.setValue("temperature", 1.0);
	OSPCombo combo = new OSPCombo(new String[] {"crystal",  "random"},0);  // second argument is default
    control.setValue("initial configuration", combo);
    gr.reset();
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

package org.opensourcephysics.stp.ising.ising2d;
import java.text.NumberFormat;

import org.opensourcephysics.controls.*;
import org.opensourcephysics.display.DatasetManager;
import org.opensourcephysics.display.DrawingFrame;
import org.opensourcephysics.display.DrawingPanel;
import org.opensourcephysics.display.PlottingPanel;
/**
 * IsingTriApp class.
 *
 *  @author Hui Wang
 *  @version 1.0   revised 11/26/06
 */
public class IsingTriangularAntiApp extends AbstractSimulation {
	IsingTriangularAnti ising;
   DrawingPanel drawingPanel;
   DrawingFrame drawingFrame;
   PlottingPanel plottingPanel;
   DrawingFrame plottingFrame;
   DatasetManager datasetManager = new DatasetManager();
   NumberFormat nf;

   public IsingTriangularAntiApp() {
      ising = new IsingTriangularAnti();
      plottingPanel = new PlottingPanel("time", "E and M", null);
      plottingPanel.setPreferredMinMaxX(0, 10);
      plottingPanel.setAutoscaleX(true);
      plottingPanel.setAutoscaleY(true);
      plottingPanel.addDrawable(datasetManager);
      plottingFrame = new DrawingFrame("Thermodynamic Quantities", plottingPanel);
      drawingPanel = new DrawingPanel();
      drawingPanel.addDrawable(ising);
      drawingFrame = new DrawingFrame("Spins Configuration", drawingPanel);
      nf = NumberFormat.getInstance();
	  nf.setMaximumFractionDigits(3);
   }

   public void initialize() {
      ising.temperature = control.getDouble("temperature");
      ising.initialize(control.getInt("L"));
      ising.resetData();
      datasetManager.clear();
      plottingPanel.repaint();
      drawingPanel.setPreferredMinMax(-5, ising.L+5,-5,ising.L+5);
      drawingPanel.repaint();
      control.clearMessages();
   }

   public void doStep() {
      ising.doOneMCStep();
      datasetManager.append(0, ising.mcs, ising.magnetization*1.0/ising.N);
      datasetManager.append(1, ising.mcs, ising.energy*1.0/ising.N);
      plottingPanel.repaint();
      drawingPanel.repaint();		
   }

   public void stop() {
      double norm = 1.0/(ising.mcs*ising.N);
      control.println("mcs = " + ising.mcs);
      control.println("acceptance ratio = " + ising.acceptedMoves*norm);
      control.println("<E> = " + ising.energyAccumulator*norm);
      control.println("Specific heat = " + ising.specificHeat());
      control.println("<M> = " + ising.magnetizationAccumulator*norm);
      control.println("Susceptibility = " + ising.susceptibility());
   }

   public void reset() {
      control.setValue("L", 32);
      control.setAdjustableValue("temperature", 4.0);//IsingTriangularAnti.criticalTemperature);
      initialize();
   }
   
   public static void main (String[] args) {
	   IsingTriangularAntiApp app = new IsingTriangularAntiApp();
	   SimulationControl control = new SimulationControl(app);
      app.setControl(control);
   }
}
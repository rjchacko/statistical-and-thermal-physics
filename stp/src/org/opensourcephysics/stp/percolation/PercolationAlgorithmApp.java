/// what is the mouse listener doing? I don't see anything happening

package org.opensourcephysics.stp.percolation;

import java.util.Random;
import org.opensourcephysics.controls.*;
import org.opensourcephysics.frames.DisplayFrame;

/**
 * <b>Input Parameters</b> <br>
 * <code>L</code> Lattice size. <br>
 * <code>p</code> Probability of a site being occupied.
 * <p>
 *
 * <b>Frames</b> <br>
 * <code>OSPFrame0</code> Lattice. illustrate the Hoshen Kopelman cluster
 * labeling algorithm
 *
 * @author jgould
 * @created January 23, 2002
 */
public class PercolationAlgorithmApp extends AbstractSimulation {
  DisplayFrame displayFrame = new DisplayFrame("");
  int L;
  double p;
  // chance of being occupied
  // iterate from 1 to L (ignore np[0]
  String mode = ""; // mode of algorithm
  HoshenKopelman algorithm;
  Random random;
  PercolationLattice lattice = new PercolationLattice();
  PercolationMouseListener mouseListener;

  public PercolationAlgorithmApp() {
    random = new Random();
    // drawingPanel.setShowCoordinates(false);
    displayFrame.setSquareAspect(true);
    displayFrame.setSize(600, 600);
    displayFrame.addDrawable(lattice);
    mouseListener = new PercolationMouseListener(displayFrame, lattice);
  }

  public void initialize() {
    displayFrame.removeMouseListener(mouseListener);
    mouseListener.setControl(control);
    random.setSeed(System.currentTimeMillis());
    L = control.getInt("L");
    p = control.getDouble("p");
    lattice.initialize(L, p, random);
    algorithm = new HoshenKopelman(lattice, L);
    displayFrame.repaint();
  }

  public void reset() {
    control.setValue("L", 20);
    control.setValue("p", 0.5);
  }

  public static void main(String[] args) {
    SimulationControl control = SimulationControl.createApp(new
        PercolationAlgorithmApp());
    control.addButton("step_all", "calculate all");
  }

  public void step_all() {
    while (!algorithm.isComplete()) {
      lattice.currentSite = algorithm.singleStep();
    }
    doStep();
  }

  protected void doStep() {
    if (algorithm.isComplete()) {
      lattice.currentSite = null;
      control.clearMessages();
      ///animationThread = null;
      stopSimulation();
      displayFrame.getDrawingPanel().addMouseListener(mouseListener);
      control
          .calculationDone(
          "Labelling finished.\nClick on a cluster with the mouse.");
    }
    else {
      lattice.currentSite = algorithm.singleStep();
    }

    // change calculationDone to animationDone
    // animationDone should only stop, not reset
    String temp = algorithm.getMode();
    if (!mode.equals(temp)) {
      mode = temp;
      control.clearMessages();
      control.println(mode);
    }
  }
}

/*
 * Open Source Physics software is free software as described near the bottom of this code file.
 *
 * For additional information and documentation on Open Source Physics please see: 
 * <http://www.opensourcephysics.org/>
 */

package org.opensourcephysics.stp.isinglg;
import java.awt.*;
import org.opensourcephysics.controls.*;
import org.opensourcephysics.frames.*;

/**
 * IsingApp simulates a two-dimensional Ising model.
 *
 * @author Jan Tobochnik, Wolfgang Christan, Harvey Gould
 * @version 1.0  revised 07/05/05
 */
public class IsingLG2App extends AbstractSimulation {
  IsingLG2 ising = new IsingLG2();
  LatticeFrame displayFrame = new LatticeFrame("Ising Model");
  PlotFrame plotFrame = new PlotFrame("time", "nLeft and nRight", "Number of Particles");;

  public IsingLG2App() {
    plotFrame.setXYColumnNames(0, "mcs", "nLeft");
    plotFrame.setXYColumnNames(1, "mcs", "nRight");
    plotFrame.setMarkerColor(0, Color.green);
    plotFrame.setPreferredMinMax(0,10,0,10);
    plotFrame.setAutoscaleX(true);
    plotFrame.setAutoscaleY(true);
  }

  public void initialize() {
    ising.temperature = control.getDouble("temperature");
    ising.chemicalPotentialLeft = control.getDouble("left chemical potential");
    ising.chemicalPotentialRight= control.getDouble("right chemical potential");
    ising.nLeft = control.getInt("number of particles on left");
    ising.nRight = control.getInt("number of particles on right");
    ising.initialize(control.getInt("L"), displayFrame);
 //   resetData();
  }

  public void doStep() {
    ising.doOneMCStep();
    plotFrame.append(0, ising.mcs, ising.nLeft);
    plotFrame.append(1, ising.mcs, ising.nRight);
  }

  public void stop() {
    control.println("mcs = "+ising.mcs);
    control.println("Number of particles in the left = " + ising.nLeft);
    control.println("Number of particles in the right = " + ising.nRight);
  }

  public void startRunning() {
    ising.temperature = control.getDouble("temperature");
    ising.chemicalPotentialLeft = control.getDouble("left chemical potential");
    ising.chemicalPotentialRight= control.getDouble("right chemical potential");
    ising.setBoltzmannArrays();
  }

  public void reset() {
    control.setValue("L", 32);
    control.setAdjustableValue("temperature", 1.0);
    control.setAdjustableValue("left chemical potential", -1);
    control.setAdjustableValue("right chemical potential", -2);
    control.setValue("number of particles on right", 40);
    control.setValue("number of particles on left", 200);
    
    enableStepsPerDisplay(true); // allow user to speed up simulation
  }

  public void zeroAverages() {
    ising.resetData();
    plotFrame.clearData();
    plotFrame.repaint();
    control.clearMessages();
  }


  public static void main(String[] args) {
    SimulationControl control = SimulationControl.createApp(new IsingLG2App());
    control.addButton("zeroAverages", "Zero averages");
  }
}

/* 
 * Open Source Physics software is free software; you can redistribute
 * it and/or modify it under the terms of the GNU General Public License (GPL) as
 * published by the Free Software Foundation; either version 2 of the License,
 * or(at your option) any later version.

 * Code that uses any portion of the code in the org.opensourcephysics package
 * or any subpackage (subdirectory) of this package must must also be be released
 * under the GNU GPL license.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston MA 02111-1307 USA
 * or view the license online at http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2007  The Open Source Physics project
 *                     http://www.opensourcephysics.org
 */

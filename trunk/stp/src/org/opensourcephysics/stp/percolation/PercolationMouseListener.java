package org.opensourcephysics.stp.percolation;

import java.awt.event.*;
import org.opensourcephysics.controls.Control;
import org.opensourcephysics.display.DrawingPanel;
import org.opensourcephysics.display.GUIUtils;
import org.opensourcephysics.frames.DisplayFrame;

public class PercolationMouseListener extends MouseAdapter
{
	DrawingPanel drawingPanel;
	PercolationLattice lattice;
	Control control;

	public PercolationMouseListener(DisplayFrame frame,
			PercolationLattice lattice)
	{
		drawingPanel = frame.getDrawingPanel();
		this.lattice = lattice;
	}

	public PercolationMouseListener(DrawingPanel panel,
			PercolationLattice lattice)
	{
		drawingPanel = panel;
		this.lattice = lattice;
	}

	public void setControl(Control c)
	{
		control = c;
	}

	public void mouseClicked(MouseEvent e)
	{
		System.out.println("mouse clicked");
		int xClicked = (int) drawingPanel.pixToX(e.getX());
		int yClicked = (int) drawingPanel.pixToY(e.getY());
		int selectedClusterSize = 0;
		if (xClicked <= lattice.L && yClicked <= lattice.L && yClicked >= 1
				&& xClicked >= 1)
		{
			lattice.selectedCluster = lattice.rsite[xClicked][yClicked];
			if (lattice.selectedCluster == HoshenKopelman.EMPTY)
				return;
//			lattice.selectedColor = GUIUtils.randomColor();
			for (int y = 1; y <= lattice.L; y++)
				// find all with this clusterNumber
				for (int x = 1; x <= lattice.L; x++)
					if (lattice.rsite[x][y] == lattice.selectedCluster)
						selectedClusterSize++;
		}
		control.clearMessages();
		control.println("Selected cluster size = " + selectedClusterSize);
		drawingPanel.repaint();
	}
}

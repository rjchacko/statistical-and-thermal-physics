package org.opensourcephysics.stp.entropy;

import org.opensourcephysics.controls.*;
import org.opensourcephysics.frames.*;
import org.opensourcephysics.stp.util.MyMath;

/**
 * put your documentation comment here
 *
 * @author Joshua Gould
 * @author Peter Sibley
 * @created January 28, 2002
 */
/**
 * @author nicholas
 * appears no different from first application.
 */
public class EntropyApp2 extends AbstractCalculation
{
	boolean showTable = false;
	int E;// = Ea + Eb
	int Na;
	int Nb;
	final static int MAXIMUM_NUMERATOR = 20;
	TableFrame tableFrame = new TableFrame("table");
	PlotFrame plotFrame = new PlotFrame("E", "S(E)", "Entropy");

	public EntropyApp2()
	{
		tableFrame.setColumnNames(0, "S(E_a)");
		tableFrame.setColumnNames(0, "Ln of omega_a");
		tableFrame.setColumnNames(0, "Ln of omega_b");
	}

	public void addPoints()
	{
		for (int i = 0; i <= E; i++)
		{
			int Ea = i;
			int Eb = E - Ea;
			double log_omegaA = 0;
			double log_omegaB = 0;
			if ((Ea + Na - 1) > MAXIMUM_NUMERATOR)
			{
				log_omegaA = MyMath.stirling(Ea + Na - 1) - MyMath.stirling(Ea)
						- MyMath.stirling(Na - 1);
			}
			else
			{
				log_omegaA = Math.log(EntropyApp.factorial(Ea + Na - 1, Ea,
						Na - 1));
			}
			if ((Eb + Nb - 1) > MAXIMUM_NUMERATOR)
			{
				log_omegaB = MyMath.stirling(Eb + Nb - 1) - MyMath.stirling(Eb)
						- MyMath.stirling(Nb - 1);
			}
			else
			{
				log_omegaB = Math.log(EntropyApp.factorial(Eb + Nb - 1, Eb,
						Nb - 1));
			}
			double S_of_Ea = log_omegaA + log_omegaB;
			tableFrame.appendRow(new double[]{S_of_Ea, log_omegaA, log_omegaB});
			plotFrame.append(0, Ea, S_of_Ea);
			plotFrame.append(0, Ea, log_omegaA);
			plotFrame.append(0, Ea, log_omegaB);
		}
		plotFrame.setPreferredMinMaxX(0, E);
		plotFrame.repaint();
	}

	public static int factorial(int numerator, int denominator1,
			int denominator2)
	{
		if (numerator < Math.max(denominator1, denominator2))
		{
			throw new IllegalArgumentException(
					"A must be greater than Math.max(B,C)");
		}
		int maxDenominator = 0;
		int minDenominator = 0;
		if (denominator1 > denominator2)
		{
			maxDenominator = denominator1;
			minDenominator = denominator2;
		}
		else
		{
			maxDenominator = denominator2;
			minDenominator = denominator1;
		}
		int total = numerator;
		for (int i = numerator - 1; i > maxDenominator; i--)
		{
			total *= i;
		}
		return total / (MyMath.factorial(minDenominator));
	}

	public void resetCalculation()
	{
		plotFrame.clearData();
		plotFrame.repaint();
		tableFrame.clearData();
		tableFrame.refreshTable();
		E = 12;
		Na = 4;
		Nb = 8;
		control.setValue("E", E);
		control.setValue("Na", Na);
		control.setValue("Nb", Nb);
	}

	public void calculate()
	{
		E = control.getInt("E");
		Na = control.getInt("Na");
		Nb = control.getInt("Nb");
		addPoints();
		tableFrame.refreshTable();
		// we do this since the frame reappears for some reason unknown to me.
		/*
		 * if(!showTable && dataTableFrame != null &&
		 * dataTableFrame.isVisible()) { dataTableFrame.setVisible(false); }
		 * if(showTable) { dataTable.refreshTable(); }
		 */
	}

	/*
	 * public void showTable() { if(dataTableFrame == null) { dataTableFrame =
	 * new DataTableFrame(dataTable); dataTableFrame.setTitle("table"); }
	 * dataTable.add(dataset1); dataTable.add(dataset2);
	 * dataTable.add(dataset3); dataTableFrame.setVisible(true);
	 * dataTable.setColumnVisible(dataset2, 1, false);
	 * dataTable.setColumnVisible(dataset3, 1, false); dataTable.refreshTable();
	 * showTable = true; }
	 */

	public static void main(String[] args)
	{
		CalculationControl.createApp(new EntropyApp2(),args);
	}
}

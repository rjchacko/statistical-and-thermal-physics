package org.opensourcephysics.stp.util;

import java.awt.Color;
import java.util.Random;
import org.opensourcephysics.display.Dataset;
import org.opensourcephysics.numerics.Function;

public class Util
{

	public static Color randomColor()
	{
		return new Color((int) (Math.random() * 255),
				(int) (Math.random() * 255), (int) (Math.random() * 255));
	}

	public static int pbc(int i, int L)
	{
		if (i >= L)
			return i - L;
		else if (i < 0)
			return L + i;
		else
			return i;
	}

	public static void fill(Random random, int[][] site, double p, int up,
			int down)
	{
		for (int i = 0; i < site.length; i++)
		{
			for (int j = 0; j < site[0].length; j++)
			{
				double d = random.nextDouble();
				if (d < p)
				{
					site[i][j] = up;
				}
				else
				{
					site[i][j] = down;
				}
			}
		}
	}

	public static void fill(Random random, int[][] site, double p)
	{
		for (int i = 0; i < site.length; i++)
		{
			for (int j = 0; j < site[0].length; j++)
			{
				double d = random.nextDouble();
				if (d < p)
				{
					site[i][j] = 1;
				}
				else
				{
					site[i][j] = 0;
				}
			}
		}
	}

	public static Function computeLinearRegression(Dataset dataset)
	{
		double xBar_yBar = 0;
		double xBar = 0;
		double yBar = 0;
		double x2Bar = 0;
		double x = 0;
		double y = 0;
		double[] xpoints = dataset.getXPoints();
		double[] ypoints = dataset.getYPoints();
		for (int i = 0; i < xpoints.length; i++)
		{
			x = xpoints[i];
			y = ypoints[i];
			xBar_yBar += x * y;
			xBar += x;
			yBar += y;
			x2Bar += x * x;
		}
		int n = xpoints.length;
		xBar_yBar = xBar_yBar / n;
		xBar = xBar / n;
		yBar = yBar / n;
		x2Bar = x2Bar / n;
		double deltaX2 = x2Bar - xBar * xBar;
		final double m = (xBar_yBar - xBar * yBar) / deltaX2;
		final double b = yBar - m * xBar;
		return new Function()
		{

			public double evaluate(double x)
			{
				return m * x + b;
			}
		};
	}

}

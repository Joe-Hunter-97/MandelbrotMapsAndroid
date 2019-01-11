package uk.ac.ed.inf.mandelbrotmaps;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

import static java.lang.Boolean.TRUE;

public class JuliaFractalView extends AbstractFractalView{
	
	// Point paramaterising this Julia set
	private double juliaX = 0;
	private double juliaY = 0;
	private float alpha = 0;

	
	
	public JuliaFractalView(Context context, FractalViewSize size) {
		super(context, size);
		thisFractal = FractalActivity.FractalType.JULIA;
		setColouringScheme(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("JULIA_COLOURS", "JuliaDefault")
				, false);

		for(int i = 0; i < noOfThreads; i++) {
			renderThreadList.get(i).setName("Julia thread " + i);
		}
		
		// Set the "maximum iteration" calculation constants
		// Empirically determined values for Julia sets.
		ITERATION_BASE = 1.58;
		ITERATION_CONSTANT_FACTOR = 6.46;
		
		// Set home area
		homeGraphArea = new MandelbrotJuliaLocation().getJuliaGraphArea();
		
		// How deep a zoom do we allow?
		MAXZOOM_LN_PIXEL = -20; // Beyond -21, "double"s break down(!).
        fractalType = FractalActivity.FractalType.JULIA;
	}
		
		
	public void setJuliaParameter(double newJuliaX, double newJuliaY) {
		//stopAllRendering();
		Log.d("JuliaParams","Setting Julia params");
		Log.d("JuliaParams","newJuliaX = " + newJuliaX + " newJuliaY = " + newJuliaY);
		if (parentActivity.showPoint){
			juliaX = parentActivity.mPoint.getX();
			juliaY = parentActivity.mPoint.getY();

			/*double xRotated = juliaX;
			double yRotated = juliaY;
			if (rotation != 0){
				xRotated = (xScreenCenter + ((juliaX-xScreenCenter)*Math.cos(this.rotation) - (juliaY-yScreenCenter)*Math.sin(this.rotation)));
				yRotated = (yScreenCenter + ((juliaX-xScreenCenter)*Math.sin(this.rotation) + (juliaY-yScreenCenter)*Math.cos(this.rotation)));
			}
			*/
			Log.d("JuliaParams","setting to Jparams To mPoint X = " + juliaX + " Y = " + juliaY);

		}else {
			juliaX = newJuliaX;
			juliaY = newJuliaY;
		}
		setGraphArea(graphArea, true);
	}
	
	
	public double[] getJuliaParam() {
		double[] juliaParam = new double[2];
		juliaParam[0] = juliaX;
		juliaParam[1] = juliaY;
		return juliaParam;
	}
	
	
	// Load a location
	void loadLocation(MandelbrotJuliaLocation mjLocation) {
		//setScaledIterationCount(mjLocation.getJuliaContrast());
		double[] juliaParam = mjLocation.getJuliaParam();
		setGraphArea(mjLocation.getJuliaGraphArea(), true);
		setJuliaParameter(juliaParam[0], juliaParam[1]);
	}

	int pixelInSet(double xPixel, double yPixel, int maxIterations) {
		boolean inside = true;
		int iterationNr;
		double newx, newy;
		double x, y;
		
		x = xMin + (xPixel * pixelSize);
		y = yMax - (yPixel * pixelSize);
		
		for (iterationNr=0; iterationNr<maxIterations; iterationNr++) {
			// z^2 + c
			newx = (x*x) - (y*y) + juliaX;
			newy = (2 * x * y) + juliaY;
		
			x = newx;
			y = newy;
		
			// Well known result: if distance is >2, escapes to infinity...
			if ( (x*x + y*y) > 4) { 
				inside = false;
				break;
			}
		}
		
		if(inside)
			return colourer.colourInsidePoint();
		else
			return colourer.colourOutsidePoint(iterationNr, maxIterations);
		
	}
	
}

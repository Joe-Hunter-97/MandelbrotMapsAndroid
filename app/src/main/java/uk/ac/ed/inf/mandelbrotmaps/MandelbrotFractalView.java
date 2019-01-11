package uk.ac.ed.inf.mandelbrotmaps;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;

public class MandelbrotFractalView extends AbstractFractalView{
	
	public float lastTouchX = 0;
	public float lastTouchY = 0;
	
	public double[] currentJuliaParams = null;
	private float[] pinCoords = new float[2];
	
	Paint outerPinPaint;
	Paint innerPinPaint;
	Paint selectedPinPaint;
	Paint littlePinPaint;
	
	int outerPinAlpha = 150;
	int innerPinAlpha = 150;
	int selectedPinAlpha = 150;
	int littlePinAlpha = 180;
	
	public float smallPinRadius = 5.0f;
	public float largePinRadius = 20.0f;
	private boolean displayingDomains = false;
	private int period = 1;
	
	
	public MandelbrotFractalView(Context context, FractalViewSize size) {
		super(context, size);
		thisFractal = FractalActivity.FractalType.MANDELBROT;
		setColouringScheme(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("MANDELBROT_COLOURS", "MandelbrotDefault")
							, false);
		
		for(int i = 0; i < noOfThreads; i++) {
			renderThreadList.get(i).setName("Mandelbrot thread " + i);
		}
		
		// Set the "maximum iteration" calculation constants
		// Empirically determined values for Mandelbrot set.
		ITERATION_BASE = 1.24;
		ITERATION_CONSTANT_FACTOR = 54;
		
		// Set home area
		homeGraphArea = new MandelbrotJuliaLocation().getMandelbrotGraphArea();
		
		// How deep a zoom do we allow?
		MAXZOOM_LN_PIXEL = -31; // Beyond -31, "double"s break down(!).
		
		
		int pinColour = Color.parseColor(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("PIN_COLOUR", "blue"));
		
		outerPinPaint = new Paint();
		outerPinPaint.setColor(pinColour);
		outerPinPaint.setAlpha(outerPinAlpha);
		//outerPinPaint.setStyle(Style.STROKE);
		
		innerPinPaint = new Paint();
		innerPinPaint.setColor(pinColour);
		innerPinPaint.setAlpha(innerPinAlpha);
		
		littlePinPaint = new Paint();
		littlePinPaint.setColor(pinColour);
		littlePinPaint.setAlpha(littlePinAlpha);
		littlePinPaint.setStyle(Style.STROKE);
		
		selectedPinPaint = new Paint();
		selectedPinPaint.setColor(pinColour);
		selectedPinPaint.setAlpha(selectedPinAlpha);
	}
		
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		if(parentActivity.showingLittle && drawPin) {
			if(controlmode != ControlMode.ZOOMING) pinCoords = getPinCoords();
			float[] mappedCoords = new float[2];
			matrix.mapPoints(mappedCoords, pinCoords);
			
			if(fractalViewSize == FractalViewSize.LARGE ) {

				canvas.drawCircle(mappedCoords[0], mappedCoords[1], smallPinRadius, innerPinPaint);
				
				//Draw larger outer circle if pin is held down.
				if(!holdingPin)
					canvas.drawCircle(mappedCoords[0], mappedCoords[1], largePinRadius, outerPinPaint);
				else
					canvas.drawCircle(mappedCoords[0], mappedCoords[1], largePinRadius*2, selectedPinPaint);
			}
			else if (fractalViewSize == FractalViewSize.LITTLE) {
				canvas.drawCircle(mappedCoords[0], mappedCoords[1], smallPinRadius, littlePinPaint);
			}else if (fractalViewSize == FractalViewSize.HALF){
                canvas.drawCircle(mappedCoords[0], mappedCoords[1], smallPinRadius*2, innerPinPaint);

				//Draw larger outer circle if pin is held down.
				if(!holdingPin)
					canvas.drawCircle(mappedCoords[0], mappedCoords[1], largePinRadius*2, outerPinPaint);
				else
					canvas.drawCircle(mappedCoords[0], mappedCoords[1], largePinRadius*3, selectedPinPaint);
            }
		}
	}
	
	
	/* Runs when the view changes size. 
	 * Sets the size of the pin based on screen size. */
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
	   
		// Show the little view at the start, if allowed.
		if(fractalViewSize == FractalViewSize.LARGE) {
			DisplayMetrics currentDisplayMetrics = new DisplayMetrics();
			parentActivity.getWindowManager().getDefaultDisplay().getMetrics(currentDisplayMetrics);
			
			int dpi = currentDisplayMetrics.densityDpi;
			largePinRadius = dpi/6; 
			smallPinRadius = dpi/30;
		}
	}
	
	
	// Load a location
	void loadLocation(MandelbrotJuliaLocation _mjLocation) {
		//setScaledIterationCount(mjLocation.getMandelbrotContrast());
		if(pixelSizes != null)
			clearPixelSizes();
		//Log.d(TAG, ""+ _mjLocation.getMandelbrotGraphArea()[0]);
		setGraphArea(_mjLocation.getMandelbrotGraphArea(), true);
		currentJuliaParams = _mjLocation.getJuliaParam();
	}

	public void reset(){
		stopAllRendering();

		parentActivity.showPoint = false;
		bitmapCreations = 0;
		rotation = 0;
		this.parentActivity.mMagnification = 1;
		this.parentActivity.jMagnification = 1;
		matrix.reset();
		fractalPixels = new int[getWidth() * getHeight()];
		clearPixelSizes();
		canvasHome();
		displayingDomains = false;

		//postInvalidate();
	}
		
	
	int pixelInSet (double xPixel, double yPixel, int maxIterations) {
		boolean inside = true;
		int iterationNr;
		double newx, newy;
		double x, y;
		int preperiod;

		// Set x0 (real part of c)
		double x0 = xMin + ((double) xPixel * pixelSize);

		double y0 = yMax - ( (double)yPixel * pixelSize ); //TODO This shouldn't be calculated every time
		ComplexPoint currentPoint = new ComplexPoint(x0, y0);
		// Start at x0, y0
		x = x0;
		y = y0;
		
		//Run iterations over this point
		for (iterationNr=0; iterationNr<maxIterations; iterationNr++) {
			// z^2 + c
			newx = (x*x) - (y*y) + x0;
			newy = (2 * x * y) + y0;
		
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
			if (displayingDomains) {
				preperiod = MisiurewiczDomainUtill.whatIsPreperiod(currentPoint, period, 30);
				if (preperiod == 0) {
					return colourer.colourOutsidePoint(iterationNr, maxIterations);
				} else {
					//Log.d("Misiurewicz domains", "preperiod = " + preperiod);
					return colourer.colourDomain(preperiod, period, iterationNr, maxIterations);
				}
			}else{
				return colourer.colourOutsidePoint(iterationNr, maxIterations);
			}
	}
	
	
	public double[] getJuliaParams(float touchX, float touchY)
	{
		lastTouchX = touchX;
		lastTouchY = touchY;
		
		double[] mandelbrotGraphArea = graphArea;
		double pixelSize = getPixelSize();
		double[] juliaParams = new double[2];
		// Touch position, on the complex plane (translated from pixels)
		juliaParams[0] = mandelbrotGraphArea[0] + ( (double)touchX * pixelSize );
		juliaParams[1] = mandelbrotGraphArea[1] - ( (double)touchY * pixelSize );
		currentJuliaParams = juliaParams;
		
		return juliaParams;
	}


	public double[] getTranslatedTouch(float touchX, float touchY)
	{
		double[] mandelbrotGraphArea = graphArea;
		double pixelSize = getPixelSize();
		double[] translated = new double[2];
		// Touch position, on the complex plane (translated from pixels)
		translated[0] = mandelbrotGraphArea[0] + ( (double)touchX * pixelSize );
		translated[1] = mandelbrotGraphArea[1] - ( (double)touchY * pixelSize );
		return translated;
	}
	
	public float[] getPinCoords() {		
		float[] pinCoords = new float[2];
		double pixelSize = getPixelSize();
		
		if (fractalViewSize == FractalViewSize.LITTLE) {
			currentJuliaParams = ((JuliaFractalView)parentActivity.fractalView).getJuliaParam();
		}
		pinCoords[0] = (float) ((currentJuliaParams[0] - graphArea[0]) / pixelSize);
		pinCoords[1] = (float) (-(currentJuliaParams[1] - graphArea[1]) / pixelSize);
		
		return pinCoords;
	}
	
	
	public void setPinColour(int newColour) {
		outerPinPaint.setColor(newColour);
		selectedPinPaint.setColor(newColour);
		innerPinPaint.setColor(newColour);
		littlePinPaint.setColor(newColour);
		
		// This somehow resets the alphas as well, so reset those.
		outerPinPaint.setAlpha(outerPinAlpha);
		innerPinPaint.setAlpha(innerPinAlpha);
		littlePinPaint.setAlpha(littlePinAlpha);		
		selectedPinPaint.setAlpha(selectedPinAlpha);
		
		invalidate();
	}

	public void displayDomains(){
		displayingDomains = true;
	}

	public void stopDisplayingDomains(){
		displayingDomains = false;
	}

	public int getCurrentPeriod(){
		return period;
	}

	public ComplexPoint findMPoint(int preperiod, ComplexPoint touchCompPoint ){
		return MisiurewiczPointUtill.findMisiurewiczPoint(preperiod, period, touchCompPoint, pixelSize);
	}
}
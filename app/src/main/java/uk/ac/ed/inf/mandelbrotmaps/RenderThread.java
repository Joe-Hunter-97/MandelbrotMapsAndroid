package uk.ac.ed.inf.mandelbrotmaps;


import android.graphics.Color;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

class RenderThread extends Thread {
	private AbstractFractalView mjCanvas;
	private volatile boolean abortThisRendering = false;
	public boolean isRunning = false;
	private int threadID = -1;
	
	public RenderThread(AbstractFractalView mjCanvasHandle, int _threadID, int _noOfThreads) {
		mjCanvas = mjCanvasHandle;
		threadID = _threadID;
		//setPriority(Thread.MAX_PRIORITY);
	}
	
	public void abortRendering() {
		abortThisRendering = true;
	}
	
	public void allowRendering() {
		abortThisRendering = false;
	}
	
	public boolean abortSignalled() {
		return abortThisRendering;
	}
	
	public void run() {
        while(true) {
            try {
                Rendering newRendering = mjCanvas.getNextRendering(threadID);
                mjCanvas.computeAllPixels(newRendering.getPixelBlockSize(), threadID);
                abortThisRendering = true;
            } catch (InterruptedException e) {
                return;
            }

        }
	}
}
package uk.ac.ed.inf.mandelbrotmaps;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;

class MisiurewiczPoints {
    private ArrayList<MisiurewiczPoint> points = new  ArrayList<MisiurewiczPoint>();

    MisiurewiczPoints(){

    }
    MisiurewiczPoints(MisiurewiczPoint point){
        this.addPoint(point);
    }

    MisiurewiczPoints(ArrayList<MisiurewiczPoint> points){
        this.points = points;
    }

    public void addPoint(MisiurewiczPoint point){
        points.add(point);
    }

    public MisiurewiczPoint getPoint(int position){
        return points.get(position);
    }

    public void shuffle(){
        Collections.shuffle(points);
    }

    public int size(){
        return points.size();
    }
}

class ComplexPoint{
    private double x;
    private double y;
    protected double mMagnificationFactor = 0;
    protected double mRotation = 0;
    protected double jMagnificationFactor = 0;
    protected double jRotation = 0;

    ComplexPoint(double x, double y){
        this.x = x;
        this.y = y;
    }

    ComplexPoint(double x, double y, double mMagnification, double jMagnification, double mRotation, double jRotation){
        this.x = x;
        this.y = y;
        this.mMagnificationFactor = mMagnification;
        this.jMagnificationFactor = jMagnification;
        this.mRotation = mRotation;
        this.jRotation = jRotation;
    }

    public double getX(){
        return x;
    }

    public double getY(){
        return y;
    }

    public static ComplexPoint add(ComplexPoint a, ComplexPoint b){
        ComplexPoint result = new ComplexPoint((a.getX()+ b.getX()), (a.getY() + b.getY()));
        return result;
    }

    public static ComplexPoint subtract(ComplexPoint a, ComplexPoint b){
        ComplexPoint result = new ComplexPoint((a.getX()- b.getX()), (a.getY() - b.getY()));
        return result;
    }

    public static ComplexPoint multiply(ComplexPoint a, ComplexPoint b){
        ComplexPoint result = new ComplexPoint(((a.getX()* b.getX()) - (a.getY() * b.getY())), ((a.getX() * b.getY()) + (a.getY() * b.getX())));
        return result;
    }

    public static double abv(ComplexPoint a){
        return Math.sqrt((a.getX() * a.getX()) + (a.getY() * a.getY()));
    }

    public static ComplexPoint divide(ComplexPoint a, ComplexPoint b){
        double numerator = Math.pow(b.getX(), 2) + Math.pow(b.getY(), 2);
        double denominatorX = (a.getX() * b.getX()) - (a.getY() * b.getY());
        double denominatorY = (a.getX() * b.getY()) + (a.getY() * b.getX());
        ComplexPoint result = new ComplexPoint(denominatorX/numerator, denominatorY/numerator) ;
        return result;
    }

    public static double distanceBetween(ComplexPoint a, ComplexPoint b){
        ComplexPoint diff = ComplexPoint.subtract(b, a);
        return Math.sqrt(Math.pow(diff.getX(), 2) + Math.pow(diff.getY(), 2));
    }

    public static double arg(ComplexPoint a){
        return Math.atan2(a.getX(), a.getY());
    }

    public static ComplexPoint sqrRoot(ComplexPoint c){
        double a = c.getX();
        double b = c.getY();
        double resultX = Math.sqrt((Math.sqrt(Math.pow(a,2) + Math.pow(b,2)) + a)/2);
        double resultY = Math.sqrt((Math.sqrt(Math.pow(a,2) + Math.pow(b,2)) - a)/2);
        return  new ComplexPoint(resultX, resultY);
    }

    public static ComplexPoint inversion(ComplexPoint c){
        double x = c.getX();
        double y = c.getY();
        double resultX = x/(Math.pow(x,2) + Math.pow(y,2));
        double resultY = -y/(Math.pow(x,2) + Math.pow(y,2));
        return new ComplexPoint(resultX, resultY);

    }
}


class MisiurewiczPoint extends ComplexPoint{
    int preperiod;
    int period;

    public MisiurewiczPoint(double x, double y, int preperiod, int period){
        super(x,y);
        this.preperiod = preperiod;
        this.period = period;
        ComplexPoint compP = new ComplexPoint(x,y);
        ComplexPoint a = MisiurewiczPointUtill.applyFDash(3,compP, compP);
        ComplexPoint uDash = MisiurewiczPointUtill.applyuDash(period, preperiod, compP);
        double mMag = ComplexPoint.abv(uDash);
        double jMag = ComplexPoint.abv(a);
        double numerator = Math.min(mMag, jMag);
        mMagnificationFactor =mMag / numerator;
        mRotation = ComplexPoint.arg(uDash);
        jMagnificationFactor = jMag / numerator;
        jRotation = ComplexPoint.arg(a);

    }

    public MisiurewiczPoint(double x, double y, double mMagnification, double jMagnification, double mRotation, double jRotation){ super(x,y); }

    public double getmMagnification() {
        return mMagnificationFactor;
    }

    public double getmRotation() {
        return mRotation;
    }

    public double getjMagnification() {
        return jMagnificationFactor;
    }

    public double getjRotation() {
        return jRotation;
    }

    public int getPreperiod() {return preperiod;}

    public int getPeriod() { return period; }
}

class MisiurewiczPointUtill{
    public static MisiurewiczPoint findMisiurewiczPoint(int preperiod, int period, ComplexPoint guess, double pixleSize){
        Log.d("Finding MPoint", "Guess was X = " + guess.getX() + " Y = " + guess.getY());
        ComplexPoint c = new ComplexPoint(guess.getX(), guess.getY());
        ComplexPoint prevC ;
        double currentDistance = pixleSize + 1;
        int count = 0;
        while (currentDistance > pixleSize/1000000 && count < 10000|| count < 50) {
            ComplexPoint g2 = ComplexPoint.subtract(applyF(preperiod + period, c, c), applyF(preperiod, c, c));
            ComplexPoint h2 = ComplexPoint.subtract(applyF(period, c, c), applyF(0, c, c));
            for (int i = 1; i <= preperiod - 1; i++) {
                h2 = ComplexPoint.multiply(h2, ComplexPoint.subtract(applyF(i + period, c, c), applyF(i, c, c)));
            }
            ComplexPoint f2 = ComplexPoint.divide(g2, h2);
            ComplexPoint g2Dash = ComplexPoint.subtract(applyFDash(preperiod + period, c, c), applyFDash(preperiod, c, c));
            ComplexPoint h2DashSummer = new ComplexPoint(0, 0);
            for (int i = 0; i <= preperiod - 1; i++) {
                h2DashSummer = ComplexPoint.add(h2DashSummer, ComplexPoint.divide(ComplexPoint.subtract(applyFDash(i + period, c, c)
                        , applyF(i, c, c)), ComplexPoint.subtract(applyF(i + period, c, c), applyF(i, c, c))));
            }
            ComplexPoint h2Dash = ComplexPoint.multiply(h2, h2DashSummer);
            ComplexPoint f2Dash = ComplexPoint.divide(ComplexPoint.subtract(ComplexPoint.multiply(g2Dash, h2)
                    , ComplexPoint.multiply(g2, h2Dash)), ComplexPoint.multiply(h2, h2));
            prevC = c;
            c = ComplexPoint.subtract(c, ComplexPoint.divide(f2, f2Dash));
            currentDistance = ComplexPoint.distanceBetween(c, prevC);
            if (count%1000 == 0 || count < 10){
                Log.d("Finding MPoint","Current Count = " + count);
                Log.d("Finding MPoint", "Current C is  X = " + c.getX() + " Y = " + c.getY());
                Log.d("Calculating MPoint","g2X = " + g2.getX() + " g2Y = " + g2.getY());
                Log.d("Calculating MPoint","h2X = " + h2.getX() + " h2Y = " + h2.getY());
                Log.d("Calculating MPoint","f2X = " + f2.getX() + " f2Y = " + f2.getY());
                Log.d("Calculating MPoint","g2DashX = " + g2Dash.getX() + " g2DashY = " + g2Dash.getY());
                Log.d("Calculating MPoint","h2Dash = " + h2Dash.getX() + " h2DashY = " + h2Dash.getY());
                Log.d("Calculating MPoint","f2DashX = " + f2Dash.getX() + " f2DashY = " + f2Dash.getY());

            }
            count++;
        }

        Log.d("Finding MPoint", "Count was = " + count);
        Log.d("Finding MPoint", "Current MPoint X = " + c.getX() + " Y = " + c.getY());
        MisiurewiczPoint mPoint = new MisiurewiczPoint(c.getX(), c.getY(), preperiod, period);
        return mPoint;
    }

    public static ComplexPoint applyFDash1(int itters, ComplexPoint c, ComplexPoint z ){
        ComplexPoint point = z;

        for (int i = 0; i <= itters; i++){
            point = ComplexPoint.add(ComplexPoint.add(point, point), c);
        }
        return point;
    }

    public static ComplexPoint applyFDash(int itters, ComplexPoint c, ComplexPoint z ){
        ComplexPoint point = new ComplexPoint(z.getX(), z.getY());

        for (int i = 0; i <= itters; i++){
            point = ComplexPoint.add(point, point);
            point = ComplexPoint.add(point, c);
        }

        return point;
    }

    /*
    public static ComplexPoint applyFDash(int itters, ComplexPoint c, ComplexPoint z ){
        ComplexPoint zn = z;
        ComplexPoint zDashn = new ComplexPoint(1,0);
        //ComplexPoint zDashn = ComplexPoint.add(ComplexPoint.multiply(new ComplexPoint(2,2), ComplexPoint.multiply(zn,new ComplexPoint(1,1))),new ComplexPoint(1,1));

        for (int i = 1; i <= itters; i++){
            zDashn = ComplexPoint.add(ComplexPoint.multiply(new ComplexPoint(2,0), ComplexPoint.multiply(zn,zDashn)),new ComplexPoint(1,0));
            zn = ComplexPoint.add(ComplexPoint.multiply(zn, zn), c);
        }
        return zDashn;
    }
    */

    public static ComplexPoint applyF(int itters, ComplexPoint c, ComplexPoint z ){
        ComplexPoint point = z;

        for (int i = 0; i <= itters; i++){
            point = ComplexPoint.multiply(point, point);
            point = ComplexPoint.add(point, c);
        }

        return point;
    }
    public static ComplexPoint applyWDash(int period, int preperiod, ComplexPoint c){
        return ComplexPoint.subtract(applyFDash((period + 1 + preperiod), c, c ), applyFDash(preperiod+1, c, c));
    }
    /*
    public static ComplexPoint applyQ(ComplexPoint c){
        return ComplexPoint.subtract(new ComplexPoint(1,0), (ComplexPoint.sqrRoot(ComplexPoint.subtract(new ComplexPoint(1,0), ComplexPoint.multiply(new ComplexPoint(4,0), c)))));
    }
    */

    public static ComplexPoint applyQ(int period, int preperiod, ComplexPoint c){
        return applyFDash(period, c, applyF(preperiod, c, c));
    }

    public static ComplexPoint applyuDash(int period, int preperiod, ComplexPoint c){
        return ComplexPoint.multiply(ComplexPoint.inversion( ComplexPoint.subtract(applyQ(period, preperiod, c), new ComplexPoint(1,0))), applyWDash(period, preperiod, c));
    }
}

class MisiurewiczDomainUtill{
    // Given a point, a period and a maximum preperiod it determins what the preperiod of a Misiurewicz point
    // that the given point is in the domain of has if it is not in the domain of a Misiurewicz point then it returns zero.
    public static int whatIsPreperiod(ComplexPoint c, int p, int maxIters){
        ComplexPoint z = c;
        ComplexPoint zp = c;
        int q = 0;
        double mq2 = 100;
        for (int n = 0; n < p; ++n){
            z = ComplexPoint.add(ComplexPoint.multiply(z,z), c);
        }
        for (int n = 0; n < maxIters - p; ++n){
            double q2 = ComplexPoint.distanceBetween(z, zp);
            if(q2 < mq2){
                mq2 = q2;
                q = n;
            }
            z = ComplexPoint.add(ComplexPoint.multiply(z, z), c);
            zp = ComplexPoint.add(ComplexPoint.multiply(zp, zp), c);
        }
        return q;
    }

    public static int whatIsMinN(ComplexPoint c, int maxIters){
        ComplexPoint z = c;
        ComplexPoint origin = new ComplexPoint(0,0);

        int N= 0;
        double currentMin = 100;
        for (int n = 0; n < maxIters; ++n){
            double q2 = ComplexPoint.distanceBetween(z, origin);
            if(q2 < currentMin){
                currentMin = q2;
                N = n;
            }
            z = ComplexPoint.add(ComplexPoint.multiply(z, z), c);
        }
        return N;
    }

}

class FindMPointThread extends Thread {
        private volatile boolean abortThisRendering = false;
        public boolean isRunning = false;
        private ComplexPoint c;
        private  int preperiod;


        public FindMPointThread(int preperiod, ComplexPoint touchCompPoint ){
            this.preperiod = preperiod;
            //setPriority(Thread.MAX_PRIORITY);
        }

        public boolean abortSignalled() {
            return abortThisRendering;
        }

        public boolean isRunning() {
        return isRunning;
        }

        public void run() {
            isRunning = true;
            isRunning = false;
        }
    }
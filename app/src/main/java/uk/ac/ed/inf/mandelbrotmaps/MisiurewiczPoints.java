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

    public boolean equals(ComplexPoint a){
        if (x == a.getX() && y == a.getY()){
            return true;
        }else {
            return false;
        }

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
        return new ComplexPoint(((a.getX()* b.getX()) - (a.getY() * b.getY())), ((a.getX() * b.getY()) + (a.getY() * b.getX())));

    }

    public static double abv(ComplexPoint a){
        return Math.sqrt((a.getX() * a.getX()) + (a.getY() * a.getY()));
    }

    public static ComplexPoint divide(ComplexPoint a, ComplexPoint b){
        double numerator = Math.pow(b.getX(), 2) + Math.pow(b.getY(), 2);
        double denominatorX = (a.getX() * b.getX()) + (a.getY() * b.getY());
        double denominatorY = (a.getY() * b.getX()) - (a.getX() * b.getY());
        ComplexPoint result = new ComplexPoint(denominatorX/numerator, denominatorY/numerator) ;
        return result;
    }

    public static double distanceBetween(ComplexPoint a, ComplexPoint b){
        ComplexPoint diff = ComplexPoint.subtract(b, a);
        return Math.sqrt(Math.pow(diff.getX(), 2) + Math.pow(diff.getY(), 2));
    }

    public static double arg(ComplexPoint a){
        return  Math.atan2(a.getY(), a.getX());
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

    public static boolean isValid(ComplexPoint c){
        return ((Math.abs(c.getX()) < Double.POSITIVE_INFINITY) && (Math.abs(c.getY()) < Double.POSITIVE_INFINITY));
    }

    public static boolean isZero(ComplexPoint c){
        return (c.getX() == 0) && (c.getY() == 0);
    }

    public static ComplexPoint realMult(ComplexPoint c, double a) {
        return new ComplexPoint(c.getX()*a, c.getY()*a);
    }

    public static ComplexPoint realSub( ComplexPoint c, double a) {
        return new ComplexPoint(c.getX() - a, c.getY());
    }
    public static ComplexPoint realSub(double a, ComplexPoint c) {
        return new ComplexPoint(a - c.getX(), -c.getY());
    }

}


class MisiurewiczPoint extends ComplexPoint{
    int preperiod;
    int period;
    ComplexPoint alpha0;
    ComplexPoint uDash;
    ComplexPoint alpha;
    ComplexPoint Q;
    ComplexPoint a;

    public MisiurewiczPoint(double x, double y, int preperiod, int period){
        super(x,y);
        this.preperiod = preperiod;
        this.period = period;
        ComplexPoint compP = new ComplexPoint(x, y);
        this.alpha0 = MisiurewiczPointUtill.applyF(preperiod, compP, compP);
        a = MisiurewiczPointUtill.applyFDash1(preperiod, compP, compP);
        uDash = MisiurewiczPointUtill.applyUDash(period, preperiod, compP);
        alpha = ComplexPoint.realSub(1, ComplexPoint.sqrRoot(ComplexPoint.realSub(ComplexPoint.realMult(compP, 4) , 1)));
        Q = ComplexPoint.realMult(alpha, 2) ;
        double mMag = ComplexPoint.abv(uDash);
        double jMag = ComplexPoint.abv(a);
        double numerator = Math.min(mMag, jMag);
        mMagnificationFactor = mMag/ jMag;
        mRotation = ComplexPoint.arg(uDash);
        jMagnificationFactor = 1;
        jRotation = ComplexPoint.arg(a);
    }

    public MisiurewiczPoint(double x, double y, double mMagnification, double jMagnification, double mRotation, double jRotation){ super(x,y); }

    public ComplexPoint getA(){ return a; }

    public ComplexPoint getUDash(){ return uDash; }

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

    /*public ComplexPoint getUDash(ComplexPoint c0, int preperiod, int period){

    }
    */
}

class MisiurewiczPointUtill{

    private final static double eps =  0.0001;
    private final static ComplexPoint zero = new ComplexPoint(0,0);


    public static MisiurewiczPoint findMisiurewiczPoint(int preperiod, int period, ComplexPoint guess, double pixleSize){
        Log.d("Finding MPoint", "Guess was X = " + guess.getX() + " Y = " + guess.getY());
        ComplexPoint c = new ComplexPoint(guess.getX(), guess.getY());
        ComplexPoint prevC;
        ComplexPoint g2 = new ComplexPoint(0,0);
        ComplexPoint h2 = new ComplexPoint(0,0);
        ComplexPoint f2 = new ComplexPoint(0,0);
        ComplexPoint g2Dash = new ComplexPoint(0,0);
        ComplexPoint h2Dash = new ComplexPoint(0,0);
        ComplexPoint f2Dash = new ComplexPoint(0,0);
        double currentDistance = pixleSize + 1;
        int count = 0;
        while (currentDistance > pixleSize/100 && count < 200) {
            /*
            if (count%100 == 0 || count < 10) {
                Log.d("Finding MPoint", "Current Count = " + count);
                Log.d("Finding MPoint", "Current C is  X = " + c.getX() + " Y = " + c.getY());
            }
            */
            //Log.d("ApplyF", "Calling ApplyF From findingMisicuwiczPointComplex find g2" );
            g2 = ComplexPoint.subtract(applyF(preperiod + period, c, c), applyF(preperiod, c, c));
            //Log.d("ApplyF", "Calling ApplyF From findingMisicuwiczPointComplex find h2" );
            h2 = ComplexPoint.subtract(applyF(period, c, c), applyF(0, c, c));
            for (int i = 0; i <= preperiod-1; i++) {
                //Log.d("ApplyF", "Calling ApplyF From findingMisicuwiczPointComplex find h2" );
                h2 = ComplexPoint.multiply(h2, ComplexPoint.subtract(applyF(i + period, c, c), applyF(i, c, c)));
            }
            f2 = ComplexPoint.divide(g2, h2);
            //Log.d("ApplyF", "Calling ApplyF From findingMisicuwiczPointComplex find g2Dash" );
            g2Dash = ComplexPoint.subtract(applyFDash1(preperiod + period, c, c), applyFDash1(preperiod, c, c));
            ComplexPoint h2DashSummer = new ComplexPoint(0, 0);
            for (int i = 0; i <= preperiod - 1; i++) {
               // Log.d("ApplyF", "Calling ApplyF From findingMisicuwiczPointComplex find h2Dash" );
                h2DashSummer = ComplexPoint.add(h2DashSummer, ComplexPoint.divide(ComplexPoint.subtract(applyFDash1(i + period, c, c)
                        , applyF(i, c, c)), ComplexPoint.subtract(applyF(i + period, c, c), applyF(i, c, c))));
            }
            h2Dash = ComplexPoint.multiply(h2, h2DashSummer);
            f2Dash = ComplexPoint.divide(ComplexPoint.subtract(ComplexPoint.multiply(g2Dash, h2)
                    , ComplexPoint.multiply(g2, h2Dash)), ComplexPoint.multiply(h2, h2));
            prevC = c;
            c = ComplexPoint.subtract(c, ComplexPoint.divide(f2, f2Dash));
            currentDistance = ComplexPoint.distanceBetween(c, prevC);

            if (count%100 == 0 || count < 10){
                Log.d("Calculating nStep","count = " + count );
                Log.d("Calculating nStep","g2X = " + g2.getX() + " g2Y = " + g2.getY());
                Log.d("Calculating nStep","h2X = " + h2.getX() + " h2Y = " + h2.getY());
                Log.d("Calculating nStep","f2X = " + f2.getX() + " f2Y = " + f2.getY());
                Log.d("Calculating nStep","g2DashX = " + g2Dash.getX() + " g2DashY = " + g2Dash.getY());
                Log.d("Calculating nStep","h2Dash = " + h2Dash.getX() + " h2DashY = " + h2Dash.getY());
                Log.d("Calculating nStep","f2DashX = " + f2Dash.getX() + " f2DashY = " + f2Dash.getY());
            }
            count++;
        }

        Log.d("Calculating nStep","count = " + (count -1) );
        Log.d("Calculating nStep","g2X = " + g2.getX() + " g2Y = " + g2.getY());
        Log.d("Calculating nStep","h2X = " + h2.getX() + " h2Y = " + h2.getY());
        Log.d("Calculating nStep","f2X = " + f2.getX() + " f2Y = " + f2.getY());
        Log.d("Calculating nStep","g2DashX = " + g2Dash.getX() + " g2DashY = " + g2Dash.getY());
        Log.d("Calculating nStep","h2Dash = " + h2Dash.getX() + " h2DashY = " + h2Dash.getY());
        Log.d("Calculating nStep","f2DashX = " + f2Dash.getX() + " f2DashY = " + f2Dash.getY());

        Log.d("Finding MPoint", "Count was = " + count);
        Log.d("Finding MPoint", "Current MPoint X = " + c.getX() + " Y = " + c.getY());
        MisiurewiczPoint mPoint = new MisiurewiczPoint(c.getX(), c.getY(), preperiod, period);
        return mPoint;
    }

    public static MisiurewiczPoint findMisiurewiczPoint1(int preperiod, int period, ComplexPoint guess, double pixleSize){
        Log.d("Finding MPoint", "Guess was X = " + guess.getX() + " Y = " + guess.getY());
        ComplexPoint c = new ComplexPoint(guess.getX(), guess.getY());
        ComplexPoint prevC = new ComplexPoint(1, 1);
        double currentDistance = pixleSize + 1;
        int count = 0;
        while (currentDistance > pixleSize/100 && count < 1000) {
            Log.d("ApplyF", "Calling ApplyF From Find Misicuwics Point simple" );
            ComplexPoint f = ComplexPoint.subtract(applyF(preperiod, c, c), applyF(preperiod + period, c, c));
            ComplexPoint fDash = ComplexPoint.subtract(applyFDash2(preperiod, c, c), applyFDash2(preperiod + period, c, c));
            prevC = c;
            c = ComplexPoint.subtract(c, ComplexPoint.divide(f, fDash));
            currentDistance = ComplexPoint.distanceBetween(c, prevC);

            if (count%100 == 0 || count < 10){
                Log.d("Finding MPoint","Current Count = " + count);
                Log.d("Finding MPoint","Current C is  X = " + c.getX() + " Y = " + c.getY());
                Log.d("Finding MPoint","f2DashX = " + fDash.getX() + " f2DashY = " + fDash.getY());
                Log.d("Finding MPoint","currentDistance  = " + currentDistance);
            }
            count++;
        }

        Log.d("Finding MPoint", "Count was = " + count);
        Log.d("Finding MPoint", "Current MPoint X = " + c.getX() + " Y = " + c.getY());
        MisiurewiczPoint mPoint = new MisiurewiczPoint(c.getX(), c.getY(), preperiod, period);
        return mPoint;
    }


    public static ComplexPoint applyFDash1(int itters, ComplexPoint c, ComplexPoint z ){
        //Log.d("WDash", "itters = " + itters);
        if (itters <= 1){
            if(z.equals(zero)){ return new ComplexPoint(1,0);
            }else { return ComplexPoint.multiply(new ComplexPoint(2,0), z); }
        }else{
            //Log.d("WDash", "a = " + itters);
            //Log.d("WDash", "f params itters = " + (itters-1) + ", c = (" + c.getX() + "," + c.getY() + ")" + ", z = (" + z.getX() + "," + z.getY() + ")");
            ComplexPoint fN = applyF1(itters-1, c, z);
            //Log.d("FDash1", "fN x = " + fN.getX() + " y = " + fN.getY());
            ComplexPoint part1 = ComplexPoint.multiply(fN, new ComplexPoint(2,0));
            ComplexPoint part2 = applyFDash1(itters-1, c, z);
            ComplexPoint result = ComplexPoint.multiply(part1, part2);
            Log.d("FDash1", "x = " + result.getX() + " y = " + result.getY());
            return result;
        }
    }

    public static ComplexPoint applyFDash2(int itters, ComplexPoint c, ComplexPoint z){
       // Log.d("FDash2", "FDash2 c X = " + c.getX() + " Y = " + c.getY());
        ComplexPoint h0 = new ComplexPoint(1,0);
        ComplexPoint diff0 = new ComplexPoint(-1000, 0);
        ComplexPoint diff1 = new ComplexPoint(1000, 0);
        ComplexPoint fDelta = new ComplexPoint(1,0);
        //Log.d("ApplyF", "Calling ApplyF From ApplyFDash2 for fX" );
        ComplexPoint fX = applyF(itters, c, c);
        int count = 0;
        while (ComplexPoint.abv(ComplexPoint.subtract(diff0, diff1)) > eps ){//&& ComplexPoint.abv(fDelta)< 10000000 ){
            count++;
            diff0 = diff1;
            //Log.d("ApplyF", "Calling ApplyF From ApplyFDash2 for fDelta" );
            fDelta = applyF(itters, ComplexPoint.add(h0, c), ComplexPoint.add(h0, c));
            diff1 = ComplexPoint.divide(ComplexPoint.subtract(fDelta, fX) , h0);
            h0 = ComplexPoint.multiply(h0, new ComplexPoint(0.5,0));
            Log.d("FDash2", "FDash2 h0 = " + h0.getX() );
            Log.d("FDash2", "FDash2 fX X = " + fX.getX() + " Y = " + fX.getY());
            Log.d("FDash2", "FDash2 fDelta X = " + fDelta.getX() + " Y = " + fDelta.getY());
            Log.d("FDash2", "FDash2 prev diff X = " + diff0.getX() + " Y = " + diff0.getY());
            Log.d("FDash2", "FDash2 diff X = " + diff1.getX() + " Y = " + diff1.getY());
            Log.d("FDash2", "FDash2 ABV diff = " + ComplexPoint.abv(ComplexPoint.subtract(diff1, diff0)));
        }
       Log.d("FDash2", "FDash2 counter = " + count);

        return diff1;
    }

    public static ComplexPoint applyFDash(int itters, ComplexPoint c, ComplexPoint z ){
        ComplexPoint point = new ComplexPoint(z.getX(), z.getY());

        for (int i = 0; i <= itters; i++){
            point = ComplexPoint.add(point, point);
            //point = ComplexPoint.add(point, c);
        }

        return point;
    }


    public static ComplexPoint applyF1(int itters, ComplexPoint c, ComplexPoint z ){
        ComplexPoint point = z;

        for (int i = 0; i < itters; i++){
            point = ComplexPoint.multiply(point, point);
            point = ComplexPoint.add(point, c);
       }

        return point;
    }


    public static ComplexPoint applyF(int itters, ComplexPoint c, ComplexPoint z ){
        ComplexPoint point = z;
        //Log.d("ApplyF"," initial z X = " + z.getX()+ " Y = " + z.getY() );
        Log.d("ApplyF"," initial c X = " + c.getX()+ " Y = " + c.getY() );
        Log.d("ApplyF"," itters= " + itters );
        for (int i = 0; i < itters; i++) {
            if (itters >= 16 && itters <= 20){
               // Log.d("ApplyF"," point X = " + point.getX()+ " Y = " + point.getY() );
            }
            point = ComplexPoint.add(ComplexPoint.multiply(point, point), c);
        }
        Log.d("ApplyF"," c X = " + point.getX()+ " Y = " + point.getY() );

        return point;
    }
    public static ComplexPoint applyWDash(int period, int preperiod, ComplexPoint c){
        ComplexPoint h0 = new ComplexPoint(0.0000005,0);
        ComplexPoint tempW;
        ComplexPoint diff0 = new ComplexPoint(-1000, 0);
        ComplexPoint diff1 = new ComplexPoint(1000, 0);
        int count = 0;
        ComplexPoint wAtPoint = applyW(period, preperiod, c);
        tempW = applyW(period, preperiod, ComplexPoint.add(h0, c));
        diff1 = ComplexPoint.divide(ComplexPoint.subtract(tempW, wAtPoint), h0);
        Log.d("WDash", "applyW X = " + wAtPoint.getX() + " Y = " + wAtPoint.getY());
        Log.d("WDash", "applyW + h0 X = " + applyW(period, preperiod, ComplexPoint.add(h0, c)).getX() + " Y = " + applyW(period, preperiod, ComplexPoint.add(h0, c)).getY());
        /*
        while (ComplexPoint.abv(h0) > 0.000005){//ComplexPoint.abv(ComplexPoint.subtract(diff0, diff1)) > eps ){
            count++;
            diff0 = diff1;
            tempW = applyW(period, preperiod, ComplexPoint.add(h0, c));
            if (!ComplexPoint.isValid(tempW)){
                tempW = new ComplexPoint(1000, 1000);
            }
            diff1 = ComplexPoint.divide(ComplexPoint.subtract(tempW, wAtPoint), h0);
            /*
            Log.d("WDash","wAtPoint x = " + wAtPoint.getX() + " y = " + wAtPoint.getY());
            Log.d("WDash","tempW    x = " + tempW.getX() + " y = " + tempW.getY());
            Log.d("WDash","Diff    x = " + diff1.getX() + " y = " + diff1.getY());
            //

            h0 = ComplexPoint.multiply(h0,new ComplexPoint(0.5,0));
        }
        */
        //ComplexPoint result = ComplexPoint.subtract(applyFDash1(preperiod + period + 1, c, zero), applyFDash1(preperiod+1, c, zero));

        Log.d("WDash","WDash count = " + count);
        Log.d("WDash","WDash x = " + diff1.getX() + " y = " + diff1.getY());
        return diff1;
    }

    public static ComplexPoint applyW(int period, int preperiod, ComplexPoint c){
        Log.d("ApplyF", "Calling ApplyF From applyW" );
        return ComplexPoint.subtract(applyF(preperiod + period + 1, c, zero), applyF(preperiod+1, c, zero));
    }


   public static ComplexPoint applyQ(int period, int preperiod, ComplexPoint c){
       Log.d("ApplyF", "Calling ApplyF From applyQ" );
       ComplexPoint c1 = applyF(preperiod, c , c);
       return applyFDash1(period, c, c1);
   }


    public static ComplexPoint applyUDash(int period, int preperiod, ComplexPoint c){
        ComplexPoint Q = applyQ(period, preperiod, c);
        Log.d("WDash","Q X = " + Q.getX() + " y = " + Q.getY());
        ComplexPoint wDash = applyWDash(period, preperiod, c);
        Log.d("WDash","WDash X = " + wDash.getX() + " y = " + wDash.getY());
        ComplexPoint uDash = ComplexPoint.divide(wDash, new ComplexPoint(Q.getX()-1, Q.getY()));
        Log.d("WDash","u' X = " + uDash.getX() + " y = " + uDash.getY());
        return uDash;
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
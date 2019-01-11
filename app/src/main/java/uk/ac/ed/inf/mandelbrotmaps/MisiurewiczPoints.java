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
    protected double magnification;

    ComplexPoint(double x, double y){
        this.x = x;
        this.y = y;
    }

    ComplexPoint(double x, double y, double magnification){
        this.x = x;
        this.y = y;
        this.magnification = magnification;
    }

    public double getX(){
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY(){
        return y;
    }

    public void setY(double y) {
        this.y = y;
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
}


class MisiurewiczPoint extends ComplexPoint{
    public MisiurewiczPoint(double x, double y){
        super(x,y);
    }
    public MisiurewiczPoint(double x, double y, double magnification){
        super(x,y,magnification);
    }

    public double getMagnification() {
        return magnification;
    }

    public void setMagnification(double magnification) {
        this.magnification = magnification;
    }
}

class MisiurewiczPointUtill{
    public static ComplexPoint findMisiurewiczPoint(int preperiod, int period, ComplexPoint guess, double pixleSize){
        ComplexPoint c = new ComplexPoint(guess.getX(), guess.getY());
        ComplexPoint prevC = new ComplexPoint(10, 10);
        double prevDistance;
        double currentDistance = pixleSize + 1;
        int count = 0;
        while (currentDistance > pixleSize/100000000) {
            count++;
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
        }

        Log.d("Finding MPoint", "Count was = " + count);
        return c;
    }

    private static ComplexPoint applyFDash(int itters, ComplexPoint c, ComplexPoint z ){
        ComplexPoint point = new ComplexPoint(z.getX(), z.getY());

        for (int i = 0; i <= itters; i++){
            point = ComplexPoint.add(point, point);
            point = ComplexPoint.add(point, c);
        }

        return point;
    }

    private static ComplexPoint applyF(int itters, ComplexPoint c, ComplexPoint z ){
        ComplexPoint point = new ComplexPoint(z.getX(), z.getY());

        for (int i = 0; i <= itters; i++){
            point = ComplexPoint.multiply(point, point);
            point = ComplexPoint.add(point, c);
        }

        return point;
    }
}

class MisiurewiczDomainUtill{
    // Given a point, a period and a maximum preperiod it determins what the preperiod of a Misiurewicz point
    // that the given point is in the domain of has if it is not in the domain of a Misiurewicz point then it returns zero.
    public static int whatIsPreperiod(ComplexPoint c, int p, int maxIters){
        ComplexPoint z = c;
        ComplexPoint zp = c;
        int q = 0;
        double mq2 = 1;
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
}
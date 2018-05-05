package com.jamil;

public class Main
{
    public static void main(String[] args)
    {
        new Main();
    }

    Main()
    {
        double[] c = new double[]{9, 5, 6, 4};
        double[][] vals = new double[][]{{6, 3, 5, 2}, {0, 0, 1, 1}, {-1, 0, 1, 0}, {0, -1, 0, 1}, {0, -1, 0, 0}, {-1, 0, 0, 0}};
        double[] b = new double[]{10, 1, 0, 0, -1, -1};
        boolean[] ineq = new boolean[]{true, true, true, true, true, true};
        boolean[] integral = new boolean[]{false, false, false, false};
        Tableau tableau = new Tableau(c.length, vals, b, c, ineq, integral);
        tableau.solve();
    }
}

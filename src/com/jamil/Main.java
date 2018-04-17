package com.jamil;

public class Main
{
    public static void main(String[] args)
    {
        new Main();
    }

    Main()
    {
        double[][] vals = new double[][]{{3, 1, -1},
                {8, 4, -1},
                {2, 2, 1}};
        double[] b = new double[]{15, 50, 20};
        double[] c = new double[]{1, 2, 1};
        boolean[] ineq = new boolean[]{false, false, false};
        boolean[] integral = new boolean[]{false, false, false};
        Tableau tableau = new Tableau(b.length, vals, b, c, ineq, integral);
        tableau.solve();
    }

    /*void dualSimplex(double[][] arr)
    {
        int col = arr[0].length;
        int row = arr.length;

        //select pivot row
        double min = 9999999;
        int pivotRow = -1;
        for (int i = 0; i < row - 1; i++)
            if (arr[i][col - 1] < min)
            {
                min = arr[row - 1][i];
                pivotRow = i;
            }

        //select pivot column
        double max = -999999;
        int pivotCol = -1;
        for (int i = 0; i < col - 1; i++)
            if (arr[pivotRow][i] < 0)
                if (arr[row - 1][i] / arr[pivotRow][i] > max)
                {
                    max = arr[row - 1][i] / arr[pivotRow][i];
                    pivotCol = i;
                }

        //normalize pivot row
        double pivot = arr[pivotRow][pivotCol];
        for (int i = 0; i < col; i++)
            arr[pivotRow][i] /= pivot;

        //set all other entries in pivot col to 0
        for (int i = 0; i < row; i++)
        {
            //obviously we don't want to do this to the pivot row itself
            if (i == pivotRow)
                continue;

            //select the row multiple
            double mult = arr[i][pivotCol];

            //subtract rows
            for (int j = 0; j < col; j++)
                arr[i][j] -= mult * arr[pivotRow][j];
        }
    }*/

    /*void simplex(double[][] arr)
    {
        int col = arr[0].length;
        int row = arr.length;

        //select pivot column
        double min = 9999999;
        int pivotCol = -1;
        for (int i = 0; i < col - 1; i++)
            if (arr[row - 1][i] < min)
            {
                min = arr[row - 1][i];
                pivotCol = i;
            }

        //select pivot row
        min = 9999999;
        int pivotRow = -1;
        for (int i = 0; i < row - 1; i++)
            if (arr[i][pivotCol] > 0 && arr[i][col - 1] > 0)
                if (arr[i][col - 1] / arr[i][pivotCol] < min)
                {
                    min = arr[i][col - 1] / arr[i][pivotCol];
                    pivotRow = i;
                }

        //normalize pivot row
        double pivot = arr[pivotRow][pivotCol];
        for (int i = 0; i < col; i++)
            arr[pivotRow][i] /= pivot;

        //set all other entries in pivot col to 0
        for (int i = 0; i < row; i++)
        {
            //obviously we don't want to do this to the pivot row itself
            if (i == pivotRow)
                continue;

            //select the row multiple
            double mult = arr[i][pivotCol];

            //subtract rows
            for (int j = 0; j < col; j++)
                arr[i][j] -= mult * arr[pivotRow][j];
        }
    }*/
}

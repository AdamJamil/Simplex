package com.jamil;

public class Main
{
    public static void main(String[] args)
    {
        new Main();
    }

    Main()
    {
        double arr[][] = new double[][]{{5, 1, 3, 1, 0, 0, 0, 0, 4},
                {-5, -1, -3, 0, 1, 0, 0, 0, -2},
                {2, 4, 7, 0, 0, 1, 0, 0, 5},
                {-2, -4, -7, 0, 0, 0, 1, 0, -3},
                {1, 1, 1, 0, 0, 0, 0, 1, 6},
                {-1, -1, -1, 0, 0, 0, 0, 0, -10}};

        simplex(arr);
        simplex(arr);
        simplex(arr);
    }

    void simplex(double[][] arr)
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

        //print
        System.out.println("\\begin{tabular}{|cc|cccccccc|c|}");
        System.out.println("\t\\hline");
        System.out.println("\t& & 0 & 0 & 0 & -1 & -1 & -1 & -1 & -1 & \\\\");
        System.out.println("\t$c_b$ & & $x_1$ & $x_2$ & $x_3$ & $u_1$ & $u_2$ & $u_3$ & $u_4$ & $y_1$ & $x_b$ \\\\");
        System.out.println("\t\\hline");
        for (double[] arrr : arr)
        {
            System.out.print("\t-1 & $y_1$");
            for (double arrrr : arrr)
            {
                String s = toFraction(arrrr);
                while (s.length() < 4)
                    s += " ";
                System.out.print(" & " + s + " ");
            }
            System.out.println("\\\\ ");
        }
        System.out.println("\t\\hline\n" +
                "\\end{tabular}");
        System.out.println();
        System.out.println();
    }

    String toFraction(double input)
    {
        double original = input;
        boolean isNegative = input < 0;
        input = Math.abs(input);
        int integerPart = (int) Math.floor(input);
        input -= integerPart;

        if (input < 0.0001)
            if (isNegative)
                return "-" + integerPart;
            else
                return "" + integerPart;

        if (1 - input < 0.0001)
            if (isNegative)
                return "-" + (1 + integerPart);
            else
                return "" + (1 + integerPart);

        for (int i = 2; i < 101; i++)
        {
            double temp = input * i;
            for (int j = 1; j < i; j++)
            {
                if (Math.abs(j - temp) < 0.0001)
                {
                    int numerator = (integerPart * i) + j;
                    int denominator = i;
                    if (isNegative)
                        return "-" + numerator + "/" + denominator;
                    return + numerator + "/" + denominator;
                }
            }
        }
        return "wtf";
    }
}

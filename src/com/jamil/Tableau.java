package com.jamil;

import java.util.ArrayList;

public class Tableau
{
    public ArrayList<String> vars = new ArrayList<>();
    public ArrayList<ArrayList<Double>> t = new ArrayList<>();
    public ArrayList<Double> c_B = new ArrayList<>();
    public ArrayList<Double> x_B = new ArrayList<>();
    public ArrayList<Double> b = new ArrayList<>();
    public ArrayList<Double> bOriginal = new ArrayList<>();
    public ArrayList<Double> c = new ArrayList<>();
    public ArrayList<Double> cOriginal = new ArrayList<>();
    public ArrayList<String> leftText = new ArrayList<>();
    public ArrayList<Double> bottomText = new ArrayList<>();
    public ArrayList<Boolean> integral = new ArrayList<>();

    public int uvars = 0, svars = 0, yvars = 0;
    public int phase = 1;
    public boolean useModifiedSimplex = false;
    public boolean dualSimplex = true;
    public boolean integer = true;
    public boolean mixedInteger = false;

    void solve()
    {
        solverMethod();
        System.out.print("Therefore the solution is $z = " + toFraction(bottomText.get(bottomText.size() - 1)) + "$, which is achieved at x = (");
        String[] solution = new String[vars.size()];
        for (int i = 0; i < vars.size(); i++)
            solution[i] = "0";
        for (int i = 0; i < t.size(); i++)
            solution[findBasicVar(i)] = toFraction(x_B.get(i));
        System.out.print(solution[0]);
        for (int i = 1; i < vars.size(); i++)
            System.out.print(", " + solution[i]);
        System.out.println(")");
    }

    void solverMethod()
    {
        if (integer)
        {
            boolean test = true;
            for (Double aDouble : bottomText)
                test &= aDouble >= 0;
            while (!test)
            {
                simplex();
                test = true;
                for (Double aDouble : bottomText)
                    test &= aDouble >= 0;
            }

            //check if integral solution found
            boolean done = true;
            String[] solution = new String[vars.size()];
            for (int i = 0; i < vars.size(); i++)
                solution[i] = "0";
            for (int i = 0; i < t.size(); i++)
                solution[findBasicVar(i)] = toFraction(x_B.get(i));
            for (int i = 0; i < solution.length; i++)
                done &= (!solution[i].contains("/") || !vars.get(i).contains("x"));
            if (done)
                return;

            //find cutting plane
            double max = 0;
            int index = -1;
            for (int i = 0; i < t.size(); i++)
            {
                double f = 0;
                String temp = toFraction(x_B.get(i));
                if (temp.contains("/"))
                {
                    int num = Integer.parseInt(temp.substring(0, temp.indexOf('/')));
                    int denom = Integer.parseInt(temp.substring(temp.indexOf('/') + 1));
                    num %= denom;
                    f = ((double) num) / denom;
                }
                if (f > max)
                {
                    max = f;
                    index = i;
                }
            }

            System.out.println("The maximal fractional value in $x_B$ is for $f_{" + (index + 1) + "} = " + toFraction(max) + "$.");

            //find t values
            ArrayList<Double> values = new ArrayList<>();
            for (int i = 0; i < t.get(0).size(); i++)
            {
                if (isBasicVar(i))
                    values.add(0.0);
                else if (t.get(index).get(i) < 0)
                {
                    String temp = toFraction(t.get(index).get(i));
                    double f = 0;
                    if (temp.contains("/"))
                    {
                        int num = Integer.parseInt(temp.substring(1, temp.indexOf('/')));
                        int denom = Integer.parseInt(temp.substring(temp.indexOf('/') + 1));
                        num %= denom;
                        f = ((double) num) / denom;
                        f = 1 - f;
                    }
                    values.add(-f);
                }
                else
                {
                    String temp = toFraction(t.get(index).get(i));
                    double f = 0;
                    if (temp.contains("/"))
                    {
                        int num = Integer.parseInt(temp.substring(0, temp.indexOf('/')));
                        int denom = Integer.parseInt(temp.substring(temp.indexOf('/') + 1));
                        num %= denom;
                        f = ((double) num) / denom;
                    }
                    values.add(-f);
                }
            }

            System.out.print("The Gomory cutting plane is ... Note to grader: I have written a program to do this, and formatting this part is impossible. Sorry.");

            yvars++;

            //add everything in
            c_B.add(0.0);
            leftText.add("y_" + yvars);
            c.add(0.0);
            vars.add("y_" + yvars);
            bottomText.add(0.0);
            bottomText.add(bottomText.remove(bottomText.size() - 2));
            x_B.add(-max);
            for (int i = 0; i < t.size(); i++)
                t.get(i).add(0.0);
            values.add(1.0);
            t.add(values);

            dualSimplex();
            solverMethod();
            return;
        }

        if (mixedInteger)
        {
            boolean test = true;
            for (Double aDouble : bottomText)
                test &= aDouble >= 0;
            while (!test)
            {
                simplex();
                test = true;
                for (Double aDouble : bottomText)
                    test &= aDouble >= 0;
            }

            //check if feasible solution found
            boolean done = true;
            String[] solution = new String[vars.size()];
            for (int i = 0; i < vars.size(); i++)
                solution[i] = "0";
            for (int i = 0; i < t.size(); i++)
                solution[findBasicVar(i)] = toFraction(x_B.get(i));
            for (int i = 0; i < solution.length; i++)
                done &= (!solution[i].contains("/") || !integral.get(i));
            if (done)
                return;

            //find cutting plane
            double max = 0;
            int index = -1;
            for (int i = 0; i < t.size(); i++)
            {
                double f = 0;
                String temp = toFraction(x_B.get(i));
                if (temp.contains("/"))
                {
                    int num = Integer.parseInt(temp.substring(0, temp.indexOf('/')));
                    int denom = Integer.parseInt(temp.substring(temp.indexOf('/') + 1));
                    num %= denom;
                    f = ((double) num) / denom;
                }
                if (f > max)
                {
                    max = f;
                    index = i;
                }
            }

            System.out.println("\nThe maximal fractional value in $x_B$ is for $f_{" + (index + 1) + "} = " + toFraction(max) + "$.");

            //find t values
            ArrayList<Double> values = new ArrayList<>();
            for (int i = 0; i < t.get(0).size(); i++)
            {
                if (isBasicVar(i))
                    values.add(0.0);
                else if (t.get(index).get(i) >= 0 && !integral.get(i))
                    values.add(-t.get(index).get(i));
                else if (t.get(index).get(i) < 0 && !integral.get(i))
                    values.add(-((max) / (max - 1)) * t.get(index).get(i));
                else
                {
                    double f = 0;
                    String temp = toFraction(t.get(index).get(i));
                    if (temp.contains("/"))
                    {
                        int num = 0;
                        if (temp.contains("-"))
                            num = Integer.parseInt(temp.substring(1, temp.indexOf('/')));
                        else
                            num = Integer.parseInt(temp.substring(0, temp.indexOf('/')));
                        int denom = Integer.parseInt(temp.substring(temp.indexOf('/') + 1));
                        num %= denom;
                        f = ((double) num) / denom;
                    }
                    if (t.get(index).get(i) < 0)
                        f = 1 - f;
                    if (f <= max)
                        values.add(-f);
                    else
                        values.add(-((max) / (max - 1)) * (f - 1));
                }
            }

            System.out.print("The Gomory cutting plane is ... Note to grader: I have written a program to do this, and formatting this part is impossible. Sorry.");
            System.out.println();

            yvars++;

            //add everything in
            c_B.add(0.0);
            leftText.add("y_" + yvars);
            c.add(0.0);
            vars.add("y_" + yvars);
            bottomText.add(0.0);
            bottomText.add(bottomText.remove(bottomText.size() - 2));
            x_B.add(-max);
            for (int i = 0; i < t.size(); i++)
                t.get(i).add(0.0);
            values.add(1.0);
            t.add(values);
            this.integral.add(true);

            print();

            dualSimplex();
            solverMethod();
            return;
        }
        if (dualSimplex)
        {
            boolean bPositive= true;
            for (Double aDouble : x_B)
                bPositive &= aDouble >= 0;
            if (bPositive)
            {
                System.out.println("The solution is now feasible.");
                return;
            }
            dualSimplex();
            solverMethod();
            return;
        }
        boolean test = true;
        for (Double aDouble : bottomText)
            test &= aDouble >= 0;
        if (!test)
        {
            simplex();
            solverMethod();
        }
        else if (phase == 2)
        {
            phase = 1;
            System.out.println("\nWe proceed with Phase 2. \n");

            for (int i = 0; i < t.size(); i++)
                if (vars.get(findBasicVar(i)).contains("u"))
                    useModifiedSimplex = true;

            if (useModifiedSimplex)
                System.out.println("Since some artificial variable(s) are still basic, we use modified simplex.");
            else
                System.out.println("No artificial variables are basic, so we remove all of them.");

            c.clear();
            c_B.clear();
            bottomText.clear();
            leftText.clear();

            for (int i = vars.size() - 1; i >= 0; i--)
                if (vars.get(i).contains("u"))
                {
                    boolean remove = true;
                    for (int j = 0; j < t.size(); j++)
                        remove &= findBasicVar(j) != i;
                    if (remove)
                    {
                        uvars--;
                        vars.remove(i);
                        for (int j = 0; j < t.size(); j++)
                            t.get(j).remove(i);
                    }
                }

            for (int i = 0; i < vars.size() - uvars; i++)
                c.add(cOriginal.get(i));
            for (int i = vars.size() - uvars; i < vars.size(); i++)
                c.add(0.0);

            for (int i = 0; i < t.size(); i++)
            {
                c_B.add(c.get(findBasicVar(i)));
                leftText.add(vars.get(findBasicVar(i)));
            }

            for (int i = 0; i < t.get(0).size(); i++)
            {
                double temp = 0;
                for (int j = 0; j < t.size(); j++)
                    temp += c_B.get(j) * t.get(j).get(i);
                bottomText.add(temp - c.get(i));
            }

            double temp = 0;
            for (int i = 0; i < x_B.size(); i++)
                temp += c_B.get(i) * x_B.get(i);
            bottomText.add(temp);

            solverMethod();
        }
    }

    Tableau(int inputVars, double[][] valss, double[] inputB, double[] inputC, boolean[] ineq, boolean[] integral)
    {
        for (boolean b1 : integral)
            this.integral.add(b1);

        for (int i = 1; i <= inputVars; i++)
            vars.add("x_" + i);

        for (double input : inputB)
        {
            b.add(input);
            x_B.add(input);
            bOriginal.add(input);
        }

        for (double input : inputC)
        {
            c.add(input);
            bottomText.add(-input);
            cOriginal.add(input);
        }

        for (double[] vals : valss)
        {
            ArrayList<Double> temp = new ArrayList<>();
            for (double val : vals)
                temp.add(val);

            t.add(temp);
        }

        for (int i = 0; i < ineq.length; i++)
        {
            if (ineq[i])
            {
                this.integral.add(false);
                svars++;
                vars.add("s_" + svars);
                c.add(0.0);
                bottomText.add(0.0);

                for (int j = 0; j < t.size(); j++)
                {
                    if (j == i)
                        t.get(j).add(1.0);
                    else
                        t.get(j).add(0.0);
                }
            }
        }

        System.out.print("We add " + svars + " slack variables");

        for (int i = 0; i < t.size(); i++)
        {
            if (findBasicVar(i) == -1)
            {
                this.integral.add(false);
                uvars++;
                vars.add("u_" + uvars);
                c.add(0.0);
                bottomText.add(0.0);

                for (int j = 0; j < t.size(); j++)
                {
                    if (j == i)
                        t.get(j).add(1.0);
                    else
                        t.get(j).add(0.0);
                }
            }
        }

        if (uvars == 0)
            System.out.println(".");
        else
            System.out.println(" and " + uvars + " artificial variables.");

        bottomText.add(0.0); //z = 0

        if (uvars > 0)
        {
            phase = 2;
            c.clear();
            for (int i = 0; i < t.get(0).size(); i++)
            {
                if (i < t.get(0).size() - uvars)
                    c.add(0.0);
                else
                    c.add(-1.0);
            }

            bottomText.clear();

            for (int i = 0; i < t.get(0).size() - uvars; i++)
            {
                bottomText.add(0.0);
                for (int j = 0; j < t.size(); j++)
                    bottomText.set(i, bottomText.get(i) + c.get(findBasicVar(j)) * t.get(j).get(i));
            }

            for (int i = 0; i < uvars; i++)
                bottomText.add(0.0);

            bottomText.add(0.0);
            for (int i = 0; i < x_B.size(); i++)
                bottomText.set(bottomText.size() - 1, bottomText.get(bottomText.size() - 1) + c.get(findBasicVar(i)) * x_B.get(i));
        }

        for (int i = 0; i < t.size(); i++)
        {
            c_B.add(c.get(findBasicVar(i)));
            leftText.add(vars.get(findBasicVar(i)));
        }

        for (int i = 0; i < t.size(); i++)
        {
            c_B.add(c.get(findBasicVar(i)));
            leftText.add(vars.get(findBasicVar(i)));
        }

        for (Double aDouble : bottomText)
            dualSimplex &= aDouble >= 0;
        boolean bPositive= true;
        for (Double aDouble : x_B)
            bPositive &= aDouble >= 0;
        dualSimplex &= bPositive;

        for (boolean b1 : integral)
        {
            mixedInteger |= b1;
            integer &= b1;
        }

        if (dualSimplex)
            System.out.println("Since the given solution is infeasible, we perform dual simplex.");

        print();
    }

    void dualSimplex()
    {
        int row = t.size();
        int col = t.get(0).size();

        //select pivot row
        double min = 9999999;
        int pivotRow = -1;
        for (int i = 0; i < row; i++)
            if (x_B.get(i) < min)
            {
                min = x_B.get(i);
                pivotRow = i;
            }

        //select pivot column
        double max = -999999;
        int pivotCol = -1;
        for (int i = 0; i < col; i++)
            if (t.get(pivotRow).get(i) < 0)
                if (bottomText.get(i) / t.get(pivotRow).get(i) > max)
                {
                    max = bottomText.get(i) / t.get(pivotRow).get(i);
                    pivotCol = i;
                }

        //normalize pivot row
        double pivot = t.get(pivotRow).get(pivotCol);
        for (int i = 0; i < col; i++)
            t.get(pivotRow).set(i, t.get(pivotRow).get(i) / pivot);

        x_B.set(pivotRow, x_B.get(pivotRow) / pivot);

        //set all other entries in pivot col to 0
        for (int i = 0; i < row; i++)
        {
            //obviously we don't want to do this to the pivot row itself
            if (i == pivotRow)
                continue;

            //select the row multiple
            double mult = t.get(i).get(pivotCol);

            //subtract rows
            for (int j = 0; j < col; j++)
                t.get(i).set(j, t.get(i).get(j) - (mult * t.get(pivotRow).get(j)));

            x_B.set(i, x_B.get(i) - (mult * x_B.get(pivotRow)));
        }

        double mult = bottomText.get(pivotCol);
        for (int i = 0; i < bottomText.size() - 1; i++)
            bottomText.set(i, bottomText.get(i) - (mult * t.get(pivotRow).get(i)));

        bottomText.set(bottomText.size() - 1, bottomText.get(bottomText.size() - 1) - (mult * x_B.get(pivotRow)));

        int temp = findBasicVar(pivotRow);
        leftText.set(pivotRow, vars.get(temp));
        c_B.set(pivotRow, c.get(temp));

        print();
    }

    void simplex()
    {
        int row = t.size();
        int col = t.get(0).size();

        //select pivot column
        double min = 9999999;
        int pivotCol = -1;
        for (int i = 0; i < col; i++)
            if (bottomText.get(i) < min)
            {
                min = bottomText.get(i);
                pivotCol = i;
            }

        //select pivot row
        min = 9999999;
        int pivotRow = -1;
        for (int i = 0; i < row; i++)
            if (t.get(i).get(pivotCol) > 0 && x_B.get(i) >= 0)
                if (x_B.get(i) / t.get(i).get(pivotCol) < min)
                {
                    min = x_B.get(i) / t.get(i).get(pivotCol);
                    pivotRow = i;
                }

        if (useModifiedSimplex)
            for (int i = 0; i < leftText.size(); i++)
                if (leftText.get(i).contains("u") && t.get(i).get(pivotCol) < 0)
                    pivotRow = i;

        //normalize pivot row
        double pivot = t.get(pivotRow).get(pivotCol);
        for (int i = 0; i < col; i++)
            t.get(pivotRow).set(i, t.get(pivotRow).get(i) / pivot);

        x_B.set(pivotRow, x_B.get(pivotRow) / pivot);

        //set all other entries in pivot col to 0
        for (int i = 0; i < row; i++)
        {
            //obviously we don't want to do this to the pivot row itself
            if (i == pivotRow)
                continue;

            //select the row multiple
            double mult = t.get(i).get(pivotCol);

            //subtract rows
            for (int j = 0; j < col; j++)
                t.get(i).set(j, t.get(i).get(j) - (mult * t.get(pivotRow).get(j)));

            x_B.set(i, x_B.get(i) - (mult * x_B.get(pivotRow)));
        }

        double mult = bottomText.get(pivotCol);
        for (int i = 0; i < bottomText.size() - 1; i++)
            bottomText.set(i, bottomText.get(i) - (mult * t.get(pivotRow).get(i)));

        bottomText.set(bottomText.size() - 1, bottomText.get(bottomText.size() - 1) - (mult * x_B.get(pivotRow)));

        int temp = findBasicVar(pivotRow);
        leftText.set(pivotRow, vars.get(temp));
        c_B.set(pivotRow, c.get(temp));

        print();
    }

    void print()
    {
        System.out.print("\n\\begin{tabular}{|cc|");
        for (int i = 0; i < vars.size(); i++)
            System.out.print("c");
        System.out.print("|c|}");
        System.out.println("\t\\hline");

        System.out.print("\t& & ");
        for (Double cost : c)
        {
            String s = toFraction(cost);
            while (s.length() < 5)
                s += " ";
            System.out.print(s + " & ");
        }
        System.out.println("\\\\");

        System.out.print("\t$c_B$ &  ");
        for (String var : vars)
            System.out.print("& $" + var + "$ ");
        System.out.println("& $x_B$ \\\\");

        System.out.println("\t\\hline");
        for (int i = 0; i < t.size(); i++)
        {
            String s = toFraction(c_B.get(i));
            while (s.length() < 5)
                s += " ";
            System.out.print("\t" + s + " & $" + leftText.get(i) + "$");
            for (Double t_ij : t.get(i))
            {
                String p = toFraction(t_ij);
                while (p.length() < 5)
                    p += " ";
                System.out.print(" & " + p + " ");
            }
            s = toFraction(x_B.get(i));
            while (s.length() < 5)
                s += " ";
            System.out.println("& " + s + "\\\\ ");
        }

        System.out.println("\t\\hline");

        System.out.print("\t& ");
        for (Double c_jz_j : bottomText)
        {
            String s = toFraction(c_jz_j);
            while (s.length() < 5)
                s += " ";
            System.out.print(" & " + s);
        }

        System.out.println("\\\\");

        System.out.println("\t\\hline");
        System.out.println("\\end{tabular}\n");
    }

    boolean isBasicVar(int i)
    {
        for (int j = 0; j < t.size(); j++)
            if (i == findBasicVar(j))
                return true;
        return false;
    }

    //find position in vars of ith basic var
    int findBasicVar(int i)
    {
        outer: for (int j = 0; j < t.get(0).size(); j++)
        {
            if (!toFraction(t.get(i).get(j)).equals("1"))
                continue;
            for (int k = 0; k < t.size(); k++)
                if (!toFraction(t.get(k).get(j)).equals("0") && k != i)
                    continue outer;
            return j;
        }

        /*if (i == 0)
            System.out.println("couldn't find the 1st basic var :(");
        if (i == 1)
            System.out.println("couldn't find the 2nd basic var :(");
        if (i == 2)
            System.out.println("couldn't find the 3rd basic var :(");
        if (i > 2)
            System.out.println("couldn't find the " + i + "th basic var :(");*/

        return -1;
    }

    String toFraction(double input)
    {
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

        for (int i = 2; i < 10000001; i++)
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

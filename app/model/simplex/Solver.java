package model.simplex;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Mart0
 * Date: 7/12/12
 */
public class Solver {
    private String[][] tableau;
    private List<String[][]> results;
    private int currentIteration;
    private Objective objective;
    private int mValue;
    private final int maxIterations = 50;
    private List<String> pivots;
    private boolean isAlternativeSolution;
    private List<Boolean> openPolyhedronList;
    private List<String> thetaTies;
    private boolean incompatibleSolution;


    public void init(String[][] tableau, String mValue, String objective) {
        this.tableau = tableau;
        this.objective = objective.equals("MAX") ? Objective.MAX : Objective.MIN;
        this.mValue = Integer.parseInt(mValue);
        currentIteration = 0;
        results = new ArrayList<String[][]>();
        pivots = new ArrayList<String>();
        thetaTies = new ArrayList<String>();
        openPolyhedronList = new ArrayList<Boolean>();
        isAlternativeSolution = false;
        incompatibleSolution = false;
        solveTableau();
    }

    private void solveTableau() {
        results.add(tableau);
        for (int z = 0; z < maxIterations; z++) {
            if (doIteration()) break;
        }

        incompatibleSolution = checkIncompatibleSolution(tableau);
        if (!incompatibleSolution) {
            checkAlternativeSolutions(tableau);
            if (isAlternativeSolution) {
                doIteration();
                checkAlternativeSolutions(tableau);
            }
        } else {
            System.out.println("End Iterating: Incompatible solution (" + results.size() + " iterations)");
        }
    }

    private boolean checkIncompatibleSolution(String[][] tableau) {
        List<String> base = getBase(tableau);
        for (String s : base) {
            if (s.contains("U") || s.contains("L")) {
                return true;
            }
        }
        return false;
    }

    private boolean doIteration() {
        String[][] newTableau = getTableauCopy();
        int zijIndex = getZijIndex(tableau);
        System.out.println("zij index " + String.valueOf(zijIndex));

        int thetaIndex = getThetaIndex(tableau, zijIndex);

        if (thetaTies.size() < results.size()) {
            // No Theta ties in this iterarion
            thetaTies.add("NoTie");
        }
        pivots.add(zijIndex + "," + thetaIndex);

        if (thetaIndex == 0) {
            openPolyhedronList.add(true);
            System.out.println("End Iterating: Open polyhedron (" + results.size() + " iterations)");
            return true;
        } else {
            openPolyhedronList.add(false);
        }
        double zijValue = parseMExpression(newTableau[zijIndex][newTableau[0].length - 1] + "", mValue);
        if (objective == Objective.MAX) {
            if (zijValue >= 0 && !isAlternativeSolution) {
                System.out.println("End Iterating: Found Optimal ( " + results.size() + " iterations)");
                return true;
            }
        } else {
            if (zijValue <= 0 && !isAlternativeSolution) {
                System.out.println("End Iterating: Found Optimal (" + results.size() + " iterations)");
                return true;
            }
        }

        double pivot = Double.parseDouble(tableau[zijIndex][thetaIndex]);

        int rows = tableau[0].length;
        int columns = tableau.length;

        //        cambio variables en la base
        String newBaseVariable = tableau[zijIndex][1];
        String newBaseVariableCoef = tableau[zijIndex][0];
        newTableau[1][thetaIndex] = newBaseVariable;
        newTableau[0][thetaIndex] = newBaseVariableCoef;

        //        cambio fila del pivot
        for (int i = 2; i < columns - 1; i++) {
            double previousValue = Double.parseDouble(tableau[i][thetaIndex]);
            double newValue = previousValue / pivot;
            newTableau[i][thetaIndex] = roundNumber(newValue) + "";
        }

        //        Gauss
        for (int j = 2; j < rows - 1; j++) {
            if (j != thetaIndex) {
                for (int i = 2; i < columns - 1; i++) {
                    double c = Double.parseDouble(tableau[i][j]);
                    double a = Double.parseDouble(tableau[zijIndex][j]);
                    double b = Double.parseDouble(tableau[i][thetaIndex]);

                    double result = (c - ((a * b) / pivot));
                    newTableau[i][j] = roundNumber(result) + "";
                }
            }
        }

        calculateZij(newTableau);

        int zijIndex2 = getZijIndex(newTableau);
        calculateThetas(zijIndex2, newTableau);

        results.add(newTableau);
        if (isAlternativeSolution) {
            if (thetaTies.size() < results.size()) {
                // No Theta ties in this iterarion
                thetaTies.add("NoTie");
            }
            pivots.add(zijIndex + "," + thetaIndex);
        }
        tableau = newTableau;
        return false;
    }

    private void checkAlternativeSolutions(String[][] newTableau) {
        int rows = tableau[0].length;
        int columns = tableau.length;
        List<String> base = getBase(newTableau);

        for (int i = 2; i < columns - 1; i++) {
            double zero = parseMExpression(newTableau[i][rows - 1], mValue);
            if (Math.abs(zero) < 0.0001 ) {
                String name = newTableau[i][1];
                if (!base.contains(name)) {
                    newTableau[i][rows - 1] = "0*";
                    isAlternativeSolution = true;
                }
            }

        }
    }

    private List<String> getBase(String[][] tableau) {
        int rows = tableau[0].length;
        List<String> base = new LinkedList<String>();
        for (int j = 2; j < rows - 1; j++) {
            base.add(tableau[1][j]);
        }
        return base;
    }

    private void calculateThetas(int zijIndex, String[][] newTableau) {
        int rows = tableau[0].length;
        int columns = tableau.length;
        for (int i = 2; i < rows - 1; i++) {
            double bi = Double.parseDouble(newTableau[2][i]);
            double aij = Double.parseDouble(newTableau[zijIndex][i]);
            double theta = roundNumber(bi / aij);
            newTableau[columns - 1][i] = theta + "";
        }
    }

    private void calculateZij(String[][] newTableau) {
        int rows = tableau[0].length;
        int columns = tableau.length;
        for (int i = 2; i < columns - 1; i++) {
            double zij = 0;
            double zijM = 0;
            for (int j = 2; j < rows - 1; j++) {
                String ck = newTableau[0][j];
                String coef = newTableau[i][j];
                if (ck.contains("M")) {
                    ck = ck.replaceAll("M", "1");
                    zijM += Double.parseDouble(ck) * Double.parseDouble(coef);
                } else {
                    zij += Double.parseDouble(ck) * Double.parseDouble(coef);
                }
            }
            if (i != 2) {
                String cj = newTableau[i][0];
                if (cj.contains("M")) {
                    cj = cj.replaceAll("M", "1");
                    zijM -= Double.parseDouble(cj);
                } else {
                    zij -= Double.parseDouble(cj);
                }
            }
            zij = roundNumber(zij);
            zijM = roundNumber(zijM);

            String result = zijM == 0 ? "" : zijM + "M";
            if (zij > 0) {
                result += "+" + zij;
            } else if (zij < 0) {
                result += zij;
            }
            if (result.equals("")) result = "0";
            newTableau[i][rows - 1] = result;
        }
    }

    private double roundNumber(double number) {
        return Math.round(number * 1000000000) / 1000000000.00;
    }

    private int getThetaIndex(String[][] tableau, int zijIndex) {
        int rows = tableau[0].length;
        int columns = tableau.length;

        List<Double> thetas = new ArrayList<Double>();
        for (int i = 2; i < rows - 1; i++) {
            double possiblePivot = parseMExpression(tableau[zijIndex][i], mValue);
            System.out.println("possiblePivot = " + possiblePivot);
            if (possiblePivot >= 0) {
                double theta = parseMExpression(this.tableau[columns - 1][i] + "", mValue);
                thetas.add(theta);
            }
        }

        Collections.sort(thetas);
        System.out.println("thetas = " + thetas);

        double extreme = 0;
        int extremeRow = 0;

        if (!thetas.isEmpty()) {
            extreme = thetas.get(0);
            System.out.println("El minimo theta es : " + extreme);
            for (int i = 2; i < rows - 1; i++) {
                double aux = parseMExpression(this.tableau[columns - 1][i] + "", mValue);
                if (aux == extreme) {
                    extremeRow = i;
                }
            }

//          empate de thetas
            for (int i = 2; i < rows - 1; i++) {
                double candidate = parseMExpression(this.tableau[columns - 1][i] + "", mValue);
                int candidateRow = i;

                if (candidate == extreme && candidateRow != extremeRow) {
                    thetaTies.add(candidateRow + "," + extremeRow);
                    double pivotExtreme = Double.parseDouble(tableau[zijIndex][extremeRow]);
                    double pivotCandidate = Double.parseDouble(tableau[zijIndex][candidateRow]);

                    for (int j = 2; j < columns - 1; j++) {
                       /* double extremeValue = Math.abs(Double.parseDouble(tableau[j][extremeRow]) / pivotExtreme);
                        double candidateValue = Math.abs(Double.parseDouble(tableau[j][candidateRow]) / pivotCandidate); */
                        double extremeValue = (Double.parseDouble(tableau[j][extremeRow]) / pivotExtreme);
                        double candidateValue = (Double.parseDouble(tableau[j][candidateRow]) / pivotCandidate);
                        if (candidateValue < extremeValue) {
                            extremeRow = candidateRow;
                            break;
                        } else if (extremeValue < candidateValue) {
                            break;
                        }
                    }
                }
            }
        }


        if (extreme < 0 || thetas.isEmpty()) {
            System.out.println("Error: No positive or valid Theta (open polyhedron) (" + results.size() + " iterations)");
            return 0;
        }
        thetas.clear();
        return extremeRow;
    }

    private int getZijIndex(String[][] tableau) {
        int rows = tableau[0].length;
        int columns = tableau.length;
        List<String> base = getBase(tableau);
        List<Double> zjs = new ArrayList<Double>();

        for (int i = 3; i < columns - 1; i++) {
            String zjName = tableau[i][1];
            if (!base.contains(zjName)) {
                System.out.println(parseMExpression(tableau[i][rows - 1], mValue));
                zjs.add(parseMExpression(tableau[i][rows - 1], mValue));
            }
        }

        Collections.sort(zjs);
        for (Double zj : zjs) {
            System.out.println("orndenados " + zj);
        }
        double zj = 0;
        if (!zjs.isEmpty()) {
            if (objective == Objective.MAX) {
                zj = zjs.get(0);
            } else {
                zj = zjs.get(zjs.size() - 1);
                System.out.println("entro a minimizar...el ZJ que tiene es: " + zj);

            }


            for (int i = 3; i < columns - 1; i++) {
                String zjName = tableau[i][1];
                System.out.println(String.valueOf(i) + " - " + zjName);
                if (!base.contains(zjName)) {
                    System.out.println(String.valueOf(i) + " - " + zjName + " entro al if");
                    double value = parseMExpression(tableau[i][rows - 1], mValue);
                    if (value == zj) {
                        return i;
                    }
                }
            }
        }
        System.out.println("ERROR: Cant find Zj-Cj (MAX or MIN) index (" + results.size() + " iterations)");
        return 0;
    }

    private double parseMExpression(String formula, int mValue) {
        String[] ms = formula.split("M");
        if( ms.length == 0 || ms[0] == ""){
            formula = formula.replace("M", "1M");
        }
        if (formula.contains("0*")) {
            formula = formula.replace("0*", "0");
        }

        String result = formula.replace("M", "*" + mValue);
        ScriptEngineManager mgr = new ScriptEngineManager();
        ScriptEngine engine = mgr.getEngineByName("JavaScript");
        try {
            Object aux = engine.eval(result);
            System.out.println(Double.parseDouble(aux.toString()));
            System.out.println( "El de arriba es" + formula);

            return Double.parseDouble(aux.toString());
        } catch (Exception e) {
            System.out.println("ERROR: Cant parse expression: " + formula + " (" + results.size() + " iterations)");
            return -1;
        }
    }

    private static void printTableau(String[][] tableau) {
        for (int i = 0; i < tableau[0].length; i++) {
            String line = " ";
            for (int j = 0; j < tableau.length; j++) {
                line += tableau[j][i] + "     ";
            }
            System.out.println(line);
        }
    }

    public String[][] getTableauCopy() {
        String[][] tableauCopy = new String[tableau.length][tableau[0].length];
        for (int i = 0; i < tableau.length; i++) {
            for (int j = 0; j < tableau[i].length; j++) {
                tableauCopy[i][j] = tableau[i][j];
            }
        }
        return tableauCopy;
    }

    public int getCurrentIteration() {
        return currentIteration;
    }

    public int getOptimalIteration() {
        return results.size() - 1;
    }

    public void iterate() {
        currentIteration = currentIteration + 1 >= results.size() - 1 ? results.size() - 1 : currentIteration + 1;
    }

    public void unIterate() {
        currentIteration = currentIteration - 1 <= 0 ? 0 : currentIteration - 1;
    }

    public void goToOptimal() {
        currentIteration = results.size() - 1;
    }

    public void goToInitial() {
        currentIteration = 0;
    }

    public String[][] getCurrentTableau() {
        return results.get(currentIteration);
    }

    public String getPivot() {
        return pivots.get(currentIteration);
    }

    public String getThetaTie() {
        return thetaTies.get(currentIteration);
    }

    public boolean isAlternativeSolution() {
        return isAlternativeSolution;
    }

    public boolean getOpenPolyhedronList() {
        return openPolyhedronList.get(currentIteration);
    }

    public boolean isIncompatibleSolution() {
        return incompatibleSolution;
    }
}

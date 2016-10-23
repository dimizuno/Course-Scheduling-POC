import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;

import java.util.Arrays;
import java.util.List;

/**
 * Created by the two world leading experts in CP: asekulsk & dkotlovs.
 */
public class Choco {

    public static void main(String [] args) {

        // TEMP: Test für die mögliche Platzierung in der Matrix in Verbindung mit den Modul-Abhängigkeiten
        //
        // x-Achse: Anazahl der Module pro Semester
        // y-Achse: Anzahl der benötigten Fachsemester
        //
        //         x1   x2   x3   x4   ...
        //    y1  0.GMI  1.EPR  2.LDS   -
        //    y2  3.MIN  4.TENI 5.TGI   -
        //    y3  6.OPR  7.ADS  8.THI   -
        //    y4  9...   10...  11...   -
        //    y5
        //    ...

        // Calculation
        int TGI = 0; // index in 1D-indexed matrix
        int BSY = 9; // index in 1D-indexed matrix
        boolean result = false; // is bys-index valid?

        // Easy example to understand index validation (TGI has to be passed in a previous semester,
        // before BSY can be done).
        if ((TGI % 3) == 0) {
            result = BSY > TGI + 2;     // BSY > (TGI + (3 - 1))
        } else if ((TGI % 3) == 1) {
            result = BSY > TGI + 1;     // BSY > (TGI + (3 - 2))
        } else if ((TGI % 3) == 2) {
            result = BSY > TGI;         // BSY > (TGI + (3 - 3))
        }
        System.out.println(TGI + ". : " + result);

        // Put in a single condition
        int modsPerSem = 3;
        for (; TGI < 25; TGI++) {
            result = BSY > (TGI + (modsPerSem - ((TGI % modsPerSem) + 1)));
            System.out.println(TGI + ". : " + result);
        }

        // MODULE PROBLEM
        solveModule();
    }

    private static void solveModule() {

        // Maximum modules per semester.
        int modsPerSem = 3;

        // Modules
        List<String> modules = Arrays.asList(
                "GMI", "EPR", "LDS", "TENI", "TGI", // 0, 1, 2, 3, 4
                "MIN", "OPR", "ADS", "THI", "REN", // 5, 6, 7, 8, 9
                "DBA", "INS", "SWT", "MCI", "BSY", // 10, 11, 12, 13, 14
                "IDB", "SPIN1", "INP", "WM1", "WM2", // 15, 16, 17, 18, 19
                "PPR", "SPIN2", "WM3", "WM4", "WM5", // 20, 21, 22, 23, 24
                "BAIN", "KBIN", "PXP"); //25, 26, 27

        // Amount of modules to be considered
        int n = modules.size() - 2; // modules 0 - 24

        // CS-Model
        Model model = new Model("module problem");

        // Variables
        IntVar[] modulVars = new IntVar[n];
        for (int i = 0; i < modulVars.length; i++) {
            modulVars[i] = model.intVar(modules.get(i), 0, (modsPerSem * n) - 1);
        }

        // Constraints: Alldiff
        model.allDifferent(modulVars).post();

//        // SetVar representing a subset of {mod1, mod2, mod3, ...}
//        SetVar y;
//        for (int m = 1; m < ((25 + modsPerSem - 1) / modsPerSem); m++) { // ceil(a / b) = (a + b - 1) / b;
//            y = model.setVar(m + ". sem", new int[]{}, new int[]{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24});
//            // possible values: {}, {2}, {1,3,5} ...
//        }

        IntVar modsPerSemVar = model.intVar("modsPerSem", modsPerSem);

        // Constraints: Dependencies
        // MIN: GMI
        setup_dependency2(model, modulVars[0], modulVars[5], modsPerSemVar);
        // OPR: EPR
        setup_dependency2(model, modulVars[1], modulVars[6], modsPerSemVar);
        // ADS: EPR, LDS
        setup_dependency2(model, modulVars[1], modulVars[7], modsPerSemVar);
        setup_dependency2(model, modulVars[2], modulVars[7], modsPerSemVar);
        // THI: EPR, LDS
        setup_dependency2(model, modulVars[1], modulVars[8], modsPerSemVar);
        setup_dependency2(model, modulVars[2], modulVars[8], modsPerSemVar);
        // BSY: TGI
        setup_dependency2(model, modulVars[4], modulVars[14], modsPerSemVar);
        // INS: EPR, OPR
        setup_dependency2(model, modulVars[1], modulVars[11], modsPerSemVar);
        setup_dependency2(model, modulVars[6], modulVars[11], modsPerSemVar);
        // SWT: GMI, EPR, LDS, OPR, ADS
        setup_dependency2(model, modulVars[0], modulVars[12], modsPerSemVar);
        setup_dependency2(model, modulVars[1], modulVars[12], modsPerSemVar);
        setup_dependency2(model, modulVars[2], modulVars[12], modsPerSemVar);
        setup_dependency2(model, modulVars[6], modulVars[12], modsPerSemVar);
        setup_dependency2(model, modulVars[7], modulVars[12], modsPerSemVar);
        // DBA: EPR, OPR
        setup_dependency2(model, modulVars[1], modulVars[10], modsPerSemVar);
        setup_dependency2(model, modulVars[6], modulVars[10], modsPerSemVar);
        // MCI: EPR, OPR
        setup_dependency2(model, modulVars[1], modulVars[13], modsPerSemVar);
        setup_dependency2(model, modulVars[6], modulVars[13], modsPerSemVar);
        // SPIN1: GMI, EPR, LDS, OPR, ADS, SWT, MCI
        setup_dependency2(model, modulVars[0], modulVars[16], modsPerSemVar);
        setup_dependency2(model, modulVars[1], modulVars[16], modsPerSemVar);
        setup_dependency2(model, modulVars[2], modulVars[16], modsPerSemVar);
        setup_dependency2(model, modulVars[6], modulVars[16], modsPerSemVar);
        setup_dependency2(model, modulVars[7], modulVars[16], modsPerSemVar);
        setup_dependency2(model, modulVars[12], modulVars[16], modsPerSemVar);
        setup_dependency2(model, modulVars[13], modulVars[16], modsPerSemVar);
        // IDB: EPR, OPR, DBA, INS
        setup_dependency2(model, modulVars[1], modulVars[15], modsPerSemVar);
        setup_dependency2(model, modulVars[6], modulVars[15], modsPerSemVar);
        setup_dependency2(model, modulVars[10], modulVars[15], modsPerSemVar);
        setup_dependency2(model, modulVars[11], modulVars[15], modsPerSemVar);
        // SPIN2: GMI, EPR, LDS, OPR, ADS, SWT, MCI, SPIN1
        setup_dependency2(model, modulVars[0], modulVars[21], modsPerSemVar);
        setup_dependency2(model, modulVars[1], modulVars[21], modsPerSemVar);
        setup_dependency2(model, modulVars[2], modulVars[21], modsPerSemVar);
        setup_dependency2(model, modulVars[6], modulVars[21], modsPerSemVar);
        setup_dependency2(model, modulVars[7], modulVars[21], modsPerSemVar);
        setup_dependency2(model, modulVars[12], modulVars[21], modsPerSemVar);
        setup_dependency2(model, modulVars[13], modulVars[21], modsPerSemVar);
        setup_dependency2(model, modulVars[16], modulVars[21], modsPerSemVar);
        model.arithm(modulVars[21], "=", modulVars[16].add(3).intVar()).post(); // SPIN2 has to be right after SPIN1
        // PPR: EPR, LDS, OPR, ADS
        setup_dependency2(model, modulVars[1], modulVars[20], modsPerSemVar);
        setup_dependency2(model, modulVars[2], modulVars[20], modsPerSemVar);
        setup_dependency2(model, modulVars[6], modulVars[20], modsPerSemVar);
        setup_dependency2(model, modulVars[7], modulVars[20], modsPerSemVar);
        // ALL MODULES -> BAIN (just for testing)
        setup_dependency2(model, modulVars[0], modulVars[25], modsPerSemVar);
        setup_dependency2(model, modulVars[1], modulVars[25], modsPerSemVar);
        setup_dependency2(model, modulVars[2], modulVars[25], modsPerSemVar);
        setup_dependency2(model, modulVars[3], modulVars[25], modsPerSemVar);
        setup_dependency2(model, modulVars[4], modulVars[25], modsPerSemVar);
        setup_dependency2(model, modulVars[5], modulVars[25], modsPerSemVar);
        setup_dependency2(model, modulVars[6], modulVars[25], modsPerSemVar);
        setup_dependency2(model, modulVars[7], modulVars[25], modsPerSemVar);
        setup_dependency2(model, modulVars[8], modulVars[25], modsPerSemVar);
        setup_dependency2(model, modulVars[9], modulVars[25], modsPerSemVar);
        setup_dependency2(model, modulVars[10], modulVars[25], modsPerSemVar);
        setup_dependency2(model, modulVars[11], modulVars[25], modsPerSemVar);
        setup_dependency2(model, modulVars[12], modulVars[25], modsPerSemVar);
        setup_dependency2(model, modulVars[13], modulVars[25], modsPerSemVar);
        setup_dependency2(model, modulVars[14], modulVars[25], modsPerSemVar);
        setup_dependency2(model, modulVars[15], modulVars[25], modsPerSemVar);
        setup_dependency2(model, modulVars[16], modulVars[25], modsPerSemVar);
        setup_dependency2(model, modulVars[17], modulVars[25], modsPerSemVar);
        setup_dependency2(model, modulVars[18], modulVars[25], modsPerSemVar);
        setup_dependency2(model, modulVars[19], modulVars[25], modsPerSemVar);
        setup_dependency2(model, modulVars[20], modulVars[25], modsPerSemVar);
        setup_dependency2(model, modulVars[21], modulVars[25], modsPerSemVar);
        setup_dependency2(model, modulVars[22], modulVars[25], modsPerSemVar);
        setup_dependency2(model, modulVars[23], modulVars[25], modsPerSemVar);
        setup_dependency2(model, modulVars[24], modulVars[25], modsPerSemVar);
        model.arithm(modulVars[25], "=", 27).post(); // pseudo optimal solution (manually set boundary)

        String result = "";
        Solution solution = model.getSolver().findSolution();
//        model.setObjective(Model.MINIMIZE, modulVars[25]);
//        Solution solution = model.getSolver().findOptimalSolution(modulVars[25], Model.MINIMIZE);
        if(solution != null) {
            // Remove all TMP's from solution
            String[] resultArray = solution.toString().split(",");
            for (String s : resultArray) {
                if (!s.contains("TMP") && !s.contains("mod_exp") && !s.contains("sum_exp") && !s.contains("sub_exp")) {
                    result += s;
                }
            }
            System.out.println(result.trim());
        }

//        List<Solution> solutions = model.getSolver().findAllSolutions();
//        if(solutions != null) {
//            int no = 0;
//            for(Solution sol : solutions) {
//                no++;
//                // Remove all TMP's from solution
//                result = "";
//                String[] resultArray = sol.toString().split(",");
//                for (String s : resultArray) {
//                    if (!s.contains("TMP") && !s.contains("mod_exp") && !s.contains("sum_exp") && !s.contains("sub_exp")) {
//                        result += s;
//                    }
//                }
//                System.out.println(no + ". " + result.trim());
//            }
//            System.out.println("Found " + solutions.size() + " solutions.");
//        }

    }

//    public static void solveModule2() {
//
//        List<String> modules = Arrays.asList(
//                "GMI", "EPR", "LDS", "TENI", "TGI", // 0,1,2,3,4
//                "MIN", "OPR", "ADS", "THI", "REN", // 5,6,7,8,9
//                "DBA", "INS", "SWT", "MCI", "BSY", // 10, 11, 12, 13, 14
//                "IDB", "SPIN", "INP", "WM1", "WM2", // 15, 16, 17, 18, 19
//                "PPR", "SPIN", "WM3", "WM4", "WM5", // 20, 21, 22, 23, 24
//                "BAIN", "KBIN", "PXP"); //25, 26, 27
//
//        List<String> modules1 = Arrays.asList(
//                "GMI", "EPR", "LDS", "TENI", "TGI"); // 0,1,2,3,4
//        List<String> modules2 = Arrays.asList(
//                "MIN", "OPR", "ADS", "THI", "REN"); // 5,6,7,8,9
//        List<String> modules3 = Arrays.asList(
//                "DBA", "INS", "SWT", "MCI", "BSY"); // 10, 11, 12, 13, 14
//        List<String> modules4 = Arrays.asList(
//                "IDB", "SPIN", "INP", "WM1", "WM2"); // 15, 16, 17, 18, 19
//        List<String> modules5 = Arrays.asList(
//                "PPR", "SPIN", "WM3", "WM4", "WM5"); // 20, 21, 22, 23, 24
//        List<String> modules6 = Arrays.asList(
//                "BAIN", "KBIN", "PXP"); //25, 26, 27
//
//        int n = modules.size() - 3;
//
//        Model model = new Model(n + "-module problem");
//
//        BoolVar[] vars1 = new BoolVar[modules1.size()];
//        BoolVar[] vars2 = new BoolVar[modules2.size()];
//        BoolVar[] vars3 = new BoolVar[modules3.size()];
//        BoolVar[] vars4 = new BoolVar[modules4.size()];
//        BoolVar[] vars5 = new BoolVar[modules5.size()];
//        BoolVar[] vars6 = new BoolVar[modules6.size()];
//
//        init_this_shit(model, vars1, modules1);
//        init_this_shit(model, vars2, modules2);
//        init_this_shit(model, vars3, modules3);
//        init_this_shit(model, vars4, modules4);
//        init_this_shit(model, vars5, modules5);
//        //init_this_shit(model, vars6, modules6);
//
//        // GMI -> MIN
//        setup_dependency(model, vars1[0 % 5], vars2[5 % 5]);
//        // EPR -> OPR
//        setup_dependency(model, vars1[1 % 5], vars2[6 % 5]);
//        // EPR & LDS -> ADS
//        setup_dependency(model, vars1[1 % 5], vars2[7 % 5]);
//        setup_dependency(model, vars1[2 % 5], vars2[7 % 5]);
//        // EPR & LDS -> THI
//        setup_dependency(model, vars1[1 % 5], vars2[8 % 5]);
//        setup_dependency(model, vars1[2 % 5], vars2[8 % 5]);
//        // TGI -> BSY
//        setup_dependency(model, vars1[4 % 5], vars3[14 % 5]);
//        // OPR -> INS
//        setup_dependency(model, vars2[6 % 5], vars3[11 % 5]);
//        // GMI & OPR & ADS -> SWT
//        setup_dependency(model, vars1[1 % 5], vars3[12 % 5]);
//        setup_dependency(model, vars2[6 % 5], vars3[12 % 5]);
//        setup_dependency(model, vars2[7 % 5], vars3[12 % 5]);
//        // OPR -> DBA
//        setup_dependency(model, vars2[6 % 5], vars3[10 % 5]);
//        // OPR -> MCI
//        setup_dependency(model, vars2[6 % 5], vars3[13 % 5]);
//        // SWT & MCI -> SPIN
//        setup_dependency(model, vars3[12 % 5], vars4[16 % 5]);
//        setup_dependency(model, vars3[13 % 5], vars4[16 % 5]);
//        setup_dependency(model, vars3[12 % 5], vars5[21 % 5]);
//        setup_dependency(model, vars3[13 % 5], vars5[21 % 5]);
//        // DBA & INS -> IDB
//        setup_dependency(model, vars3[10 % 5], vars4[15 % 5]);
//        setup_dependency(model, vars3[11 % 5], vars4[15 % 5]);
//        // OPR & ADS -> PPR
//        setup_dependency(model, vars2[6 % 5], vars5[20 % 5]);
//        setup_dependency(model, vars2[7 % 5], vars5[20 % 5]);
//
//        String result = "";
//        Solution solution = model.getSolver().findSolution();
//        if(solution != null) {
//            // Remove all TMP's from solution
//            String[] resultArray = solution.toString().split(",");
//            for (int i = 0; i < resultArray.length; i++) {
//                if (!resultArray[i].contains("TMP")) {
//                    result += resultArray[i];
//                }
//            }
//            System.out.println(result.trim());
//        }
//
//        List<Solution> solutions = model.getSolver().findAllSolutions();
//        if(solutions != null) {
//            int no = 0;
//            for(Solution sol : solutions) {
//                no++;
//                // Remove all TMP's from solution
//                result = "";
//                String[] resultArray = sol.toString().split(",");
//                for (int i = 0; i < resultArray.length; i++) {
//                    if (!resultArray[i].contains("TMP")) {
//                        result += resultArray[i];
//                    }
//                }
//                System.out.println(no + ". " + result.trim());
//            }
//            System.out.println("Found " + solutions.size() + " solutions.");
//        }
//
//    }

    private static void setup_dependency2(Model model, IntVar a, IntVar b, IntVar modsPerSemVar) {
        // explanation in main method above!!
        //model.arithm(b, ">", (a + (modsPerSem - ((a % modsPerSem) + 1)))).post();
        model.arithm(b, ">", a.add(modsPerSemVar.sub(a.mod(modsPerSemVar).add(1))).intVar()).post();
    }

    private static void setup_dependency(Model model, IntVar a, IntVar b) {
        BoolVar c1 = model.arithm(a, "=", b).reify();
        BoolVar c2 = model.arithm(a, ">", b).reify();
        model.arithm(c1, "+", c2, "=", 1).post();
    }

    private static void init_this_shit(Model model, BoolVar[] vars, List<String> modules) {
        for(int i = 0; i < vars.length; i++) {
            vars[i] = model.boolVar(modules.get(i));
        }
    }

}

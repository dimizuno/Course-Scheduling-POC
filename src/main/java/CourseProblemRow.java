import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.constraints.nary.alldifferent.conditions.Condition;
import org.chocosolver.solver.variables.IntVar;
import util.StringPadding;

import java.util.Arrays;
import java.util.List;

/**
 * Created by the two world leading experts in CP: asekulsk & dkotlovs.
 */
public class CourseProblemRow {

    public static void main(String[] args) {
        solveModule();
    }

    private static void solveModule() {

        // Maximum modules per semester.
        int modsPerSem = 3;

        // Maximum semesters over all
        int maxSems = 10;


        ////////////
        // MODEL: //
        ////////////

        // x-axis: number of modules per semester (curses per term)
        // y-axis: number of necessary terms over all
        //
        //         x0   x1   x2   x3   ...
        //    y0  0.GMI  1.EPR  2.LDS   -
        //    y1  3.MIN  4.TENI 5.TGI   -
        //    y2  6.OPR  7.ADS  8.THI   -
        //    y3  9...   10...  11...   -
        //    y4
        //    ...

        // Modules
        List<String> modules = Arrays.asList(
                "TGI", "TENI", "GMI", "EPR", "LDS", // 0, 1, 2, 3, 4
                "MIN", "REN", "OPR", "ADS", "THI", // 5, 6, 7, 8, 9
                "BSY", "INS", "SWT", "DBA", "MCI", // 10, 11, 12, 13, 14
                "SPIN1", "IDB", "INP", "WM1", "WM2", // 15, 16, 17, 18, 19
                "SPIN2", "PPR", "WM3", "WM4", "WM5", // 20, 21, 22, 23, 24
                "BAIN", "KBIN", "PXP"); //25, 26, 27

        // Selectable modules
//        List<String> selModules = Arrays.asList(
//                "BWIN", "BV", "ITS", "ITR", "KBE", "MRO", "OPC", // 0, 1, 2, 3, 4, 5, 6
//                "BKV", "KI", "MOC", "PAP", "ROB", "SWD"); // 7, 8, 9, 10, 11, 12

        // Amount of modules to be considered
        int n = modules.size();

        // CS-Model
        Model model = new Model("module problem");

        // Variables: modules
        IntVar[] modulVars = new IntVar[n];
        for (int i = 0; i < modulVars.length; i++) {
            modulVars[i] = model.intVar(modules.get(i), 0, maxSems - 1);
//            modulVars[i] = model.intVar(modules.get(i), new int[]{0,0,0,1,1,1,2,2,2,3,3,3,4,4,4,5,5,5,6,6,6,7,7,7,8,8,8,9,9,9});
        }

        // Variables: credit points
        IntVar cp = model.intVar("cp", 0, 180); // 150 + 12 + 3 + 15
        IntVar[] cpVars = new IntVar[n];
        cpVars[0] = model.intVar(modules.get(0), 5);    // TGI
        cpVars[1] = model.intVar(modules.get(1), 5);    // TENI
        cpVars[2] = model.intVar(modules.get(2), 7);    // GMI
        cpVars[3] = model.intVar(modules.get(3), 7);    // EPR
        cpVars[4] = model.intVar(modules.get(4), 6);    // LDS
        cpVars[5] = model.intVar(modules.get(5), 6);    // MIN
        cpVars[6] = model.intVar(modules.get(6), 5);    // REN
        cpVars[7] = model.intVar(modules.get(7), 7);    // OPR
        cpVars[8] = model.intVar(modules.get(8), 6);    // ADS
        cpVars[9] = model.intVar(modules.get(9), 6);    // THI
        cpVars[10] = model.intVar(modules.get(10), 6);  // BSY              | premise: 30cp
        cpVars[11] = model.intVar(modules.get(11), 6);  // INS              | premise: 30cp
        cpVars[12] = model.intVar(modules.get(12), 6);  // SWT              | premise: 30cp
        cpVars[13] = model.intVar(modules.get(13), 6);  // DBA              | premise: 30cp
        cpVars[14] = model.intVar(modules.get(14), 6);  // MCI              | premise: 30cp
        cpVars[15] = model.intVar(modules.get(15), 6);  // SPIN1 (6 of 12)  | premise: 50cp
        cpVars[16] = model.intVar(modules.get(16), 6);  // IDB              | premise: 50cp
        cpVars[17] = model.intVar(modules.get(17), 6);  // INP              | premise: 50cp
        cpVars[18] = model.intVar(modules.get(18), 6);  // WM1              | premise: 50cp
        cpVars[19] = model.intVar(modules.get(19), 6);  // WM2              | premise: 50cp
        cpVars[20] = model.intVar(modules.get(20), 6);  // SPIN2 (6 of 12)  | premise: 50cp
        cpVars[21] = model.intVar(modules.get(21), 6);  // PPR              | premise: 70cp
        cpVars[22] = model.intVar(modules.get(22), 6);  // WM3              | premise: 70cp
        cpVars[23] = model.intVar(modules.get(23), 6);  // WM4              | premise: 70cp
        cpVars[24] = model.intVar(modules.get(24), 6);  // WM5              | premise: 70cp
        cpVars[25] = model.intVar(modules.get(25), 12); // BAIN             | premise: 150cp
        cpVars[26] = model.intVar(modules.get(26), 3);  // KBIN             | premise: 150cp
        cpVars[27] = model.intVar(modules.get(27), 15); // PPX              | premise: 90cp

        // BWIN, WS, 6CP
        // BV, WS, 6CP (GMI, EPR, MIN, OPR, ADS, PPR)
        // ITS, WS, 6CP
        // ITR, WS, 6CP
        // KBE, WS, 6CP (SWT, DBA, MCI, IDB)
        // MRO, WS, 6CP (GMI, EPR, MIN, OPR, ADS, PPR)
        // OPC, WS, 6CP (EPR, OPR, PPR)

        // BKV, SS, 6CP (REN, BSY)
        // KI, SS, 6CP (EPR, LDS, OPR, ADS)
        // MOC, SS, 6CP (REN)
        // PAP, SS, 6CP (GMI, EPR, OPR, ADS, PPR)
        // ROB, SS, 6CP (GMI, EPR, MIN, OPR, ADS, PPR)
        // SWD, SS, 6CP (SWT, DBA, MCI, IDB)


        //////////////////
        // CONSTRAINTS: //
        //////////////////

        // Constraints: maximum modules per semester
        for (int semester = 0; semester < maxSems; semester++) {
            Constraint[] constraints = new Constraint[modsPerSem + 1];
            for (int j = 0; j <= modsPerSem; j++) {
                constraints[j] = model.among(model.intVar(j), modulVars, new int[]{semester});
            }
            model.or(constraints).post();
        }

//        for (int semester = 0; semester < maxSems; semester++) {
//for (int j = 1; j < )
//            Constraint const1 = model.among(model.intVar(1), modulVars, new int[]{semester});
//            Constraint const2 = model.among(model.intVar(2), modulVars, new int[]{semester});
//            Constraint const3 = model.among(model.intVar(3), modulVars, new int[]{i});
//            model.or(const1, const2, const3).post();
//        }



        // Constraints: Module Dependencies
        // MIN: GMI
        model.arithm(modulVars[5], ">", modulVars[2]).post();
        // OPR: EPR
        model.arithm(modulVars[7], ">", modulVars[3]).post();
        // ADS: EPR, LDS
        model.arithm(modulVars[8], ">", modulVars[3]).post();
        model.arithm(modulVars[8], ">", modulVars[4]).post();
        // THI: EPR, LDS
        model.arithm(modulVars[9], ">", modulVars[3]).post();
        model.arithm(modulVars[9], ">", modulVars[4]).post();
        // BSY: TGI
        model.arithm(modulVars[10], ">", modulVars[0]).post();
        // INS: EPR, OPR
        model.arithm(modulVars[11], ">", modulVars[3]).post();
        model.arithm(modulVars[11], ">", modulVars[7]).post();
        // SWT: GMI, EPR, LDS, OPR, ADS
        model.arithm(modulVars[12], ">", modulVars[2]).post();
        model.arithm(modulVars[12], ">", modulVars[3]).post();
        model.arithm(modulVars[12], ">", modulVars[4]).post();
        model.arithm(modulVars[12], ">", modulVars[7]).post();
        model.arithm(modulVars[12], ">", modulVars[8]).post();
        // DBA: EPR, OPR
        model.arithm(modulVars[13], ">", modulVars[3]).post();
        model.arithm(modulVars[13], ">", modulVars[7]).post();
        // MCI: EPR, OPR
        model.arithm(modulVars[14], ">", modulVars[3]).post();
        model.arithm(modulVars[14], ">", modulVars[7]).post();
        // SPIN1: GMI, EPR, LDS, OPR, ADS, SWT, MCI
        model.arithm(modulVars[15], ">", modulVars[2]).post();
        model.arithm(modulVars[15], ">", modulVars[3]).post();
        model.arithm(modulVars[15], ">", modulVars[4]).post();
        model.arithm(modulVars[15], ">", modulVars[7]).post();
        model.arithm(modulVars[15], ">", modulVars[8]).post();
        model.arithm(modulVars[15], ">", modulVars[12]).post();
        model.arithm(modulVars[15], ">", modulVars[14]).post();
        // IDB: EPR, OPR, INS, DBA
        model.arithm(modulVars[16], ">", modulVars[3]).post();
        model.arithm(modulVars[16], ">", modulVars[7]).post();
        model.arithm(modulVars[16], ">", modulVars[11]).post();
        model.arithm(modulVars[16], ">", modulVars[13]).post();
        // WM1
        model.arithm(modulVars[18], "<=", modulVars[19]).post(); // WM1.row <= WM2.row
        model.arithm(modulVars[18], "<=", modulVars[22]).post(); // WM1.row <= WM3.row
        model.arithm(modulVars[18], "<=", modulVars[23]).post(); // WM1.row <= WM4.row
        model.arithm(modulVars[18], "<=", modulVars[24]).post(); // WM1.row <= WM5.row
        // WM2
        model.arithm(modulVars[19], "<=", modulVars[22]).post(); // WM2.row <= WM3.row
        model.arithm(modulVars[19], "<=", modulVars[23]).post(); // WM2.row <= WM4.row
        model.arithm(modulVars[19], "<=", modulVars[24]).post(); // WM2.row <= WM5.row
        // SPIN2: GMI, EPR, LDS, OPR, ADS, SWT, MCI, SPIN1
        model.arithm(modulVars[20], ">", modulVars[2]).post();
        model.arithm(modulVars[20], ">", modulVars[3]).post();
        model.arithm(modulVars[20], ">", modulVars[4]).post();
        model.arithm(modulVars[20], ">", modulVars[7]).post();
        model.arithm(modulVars[20], ">", modulVars[8]).post();
        model.arithm(modulVars[20], ">", modulVars[12]).post();
        model.arithm(modulVars[20], ">", modulVars[14]).post();
        model.arithm(modulVars[20], ">", modulVars[15]).post();
        model.arithm(modulVars[20], "=", modulVars[15].add(1).intVar()).post(); // SPIN2 has to be right after SPIN1
        // PPR: EPR, LDS, OPR, ADS
        model.arithm(modulVars[21], ">", modulVars[3]).post();
        model.arithm(modulVars[21], ">", modulVars[4]).post();
        model.arithm(modulVars[21], ">", modulVars[7]).post();
        model.arithm(modulVars[21], ">", modulVars[8]).post();
        // WM3
        model.arithm(modulVars[22], "<=", modulVars[23]).post(); // WM3.row <= WM4.row
        model.arithm(modulVars[22], "<=", modulVars[24]).post(); // WM3.row <= WM5.row
        // WM4
        model.arithm(modulVars[23], "<=", modulVars[24]).post(); // WM4.row <= WM5.row
        // BAIN: All modules, PPX
        for (int i = 0; i <= 24; i++) {
            model.arithm(modulVars[25], ">", modulVars[i]).post();
        }
        model.arithm(modulVars[25], ">=", modulVars[27]).post(); // BAIN.row >= PXX.row
        model.arithm(modulVars[25], "=", modulVars[26]).post(); // BAIN.row = KBIN.row
        // PPX: all modules from the first 3 semesters
        for (int i = 0; i <= 14; i++) {
            model.arithm(modulVars[27], ">", modulVars[i]).post();
        }



        // Constraints: WS cycle dependency (modules are only accessible during winter)
        model.arithm(modulVars[0].mod(2).intVar(), "=", 0).post(); // TGI
        model.arithm(modulVars[1].mod(2).intVar(), "=", 0).post(); // TENI
        model.arithm(modulVars[2].mod(2).intVar(), "=", 0).post(); // GMI
        model.arithm(modulVars[3].mod(2).intVar(), "=", 0).post(); // EPR
        model.arithm(modulVars[4].mod(2).intVar(), "=", 0).post(); // LDS
        model.arithm(modulVars[10].mod(2).intVar(), "=", 0).post(); // BSY
        model.arithm(modulVars[11].mod(2).intVar(), "=", 0).post(); // INS
        model.arithm(modulVars[12].mod(2).intVar(), "=", 0).post(); // SWT
        model.arithm(modulVars[13].mod(2).intVar(), "=", 0).post(); // DBA
        model.arithm(modulVars[14].mod(2).intVar(), "=", 0).post(); // MCI
        model.arithm(modulVars[20].mod(2).intVar(), "=", 0).post(); // SPIN2
        model.arithm(modulVars[21].mod(2).intVar(), "=", 0).post(); // PPR

        // Constraints: SS cycle dependency (modules are only accessible during summer)
        model.arithm(modulVars[5].mod(2).intVar(), "=", 1).post(); // MIN
        model.arithm(modulVars[6].mod(2).intVar(), "=", 1).post(); // REN
        model.arithm(modulVars[7].mod(2).intVar(), "=", 1).post(); // OPR
        model.arithm(modulVars[8].mod(2).intVar(), "=", 1).post(); // ADS
        model.arithm(modulVars[9].mod(2).intVar(), "=", 1).post(); // THI
        model.arithm(modulVars[15].mod(2).intVar(), "=", 1).post(); // SPIN1
        model.arithm(modulVars[16].mod(2).intVar(), "=", 1).post(); // IDB
        model.arithm(modulVars[17].mod(2).intVar(), "=", 1).post(); // INP
        model.arithm(modulVars[27].mod(2).intVar(), "=", 1).post(); // PXP



        // Constraints: credit points premise and cycle dependency
        model.arithm(modulVars[5], ">", 0).post(); // TGI (redundant for SS)
        model.arithm(modulVars[6], ">", 0).post(); // TENI (redundant for SS)
        model.arithm(modulVars[7], ">", 0).post(); // GMI (redundant for SS)
        model.arithm(modulVars[8], ">", 0).post(); // EPR (redundant for SS)
        model.arithm(modulVars[9], ">", 0).post(); // LDS (redundant for SS)
        model.arithm(modulVars[10], ">", 1).post(); // BSY (30cp + WS)
        model.arithm(modulVars[11], ">", 1).post(); // INS (30cp + WS)
        model.arithm(modulVars[12], ">", 1).post(); // SWT (30cp + WS)
        model.arithm(modulVars[13], ">", 1).post(); // DBA (30cp + WS)
        model.arithm(modulVars[14], ">", 1).post(); // MCI (30cp + WS)
        model.arithm(modulVars[15], ">", 2).post(); // SPIN1 (50cp + SS)
        model.arithm(modulVars[16], ">", 2).post(); // IDB (50cp + SS)
        model.arithm(modulVars[17], ">", 2).post(); // INP (50cp + SS)
        model.arithm(modulVars[18], ">", 1).post(); // WM1 (50cp/70cp)
        model.arithm(modulVars[19], ">", 1).post(); // WM2 (50cp/70cp)
        model.arithm(modulVars[20], ">", 3).post(); // SPIN2 (50cp + SPIN1)
        model.arithm(modulVars[21], ">", 1).post(); // PPR (70cp + WS)
        model.arithm(modulVars[22], ">", 1).post(); // WM3 (50cp/70cp)
        model.arithm(modulVars[23], ">", 1).post(); // WM4 (50cp/70cp)
        model.arithm(modulVars[24], ">", 1).post(); // WM5 (50cp/70cp)
        model.arithm(modulVars[25], ">", 4).post(); // BAIN (150cp)
        model.arithm(modulVars[26], ">", 4).post(); // KBIN (150cp)
        model.arithm(modulVars[27], ">", 2).post(); // PXP (90cp + SS)

//        IntVar[] cpCounts = new IntVar[5];
//        for (int i = 10; i <= 14; i++) {  // "BSY", "INS", "SWT", "DBA", "MCI"
//            cpCounts[i - 10] = model.intVar("cpCounts_" + i, 0);
//            BoolVar[] passedModules = new BoolVar[modulVars.length];
//            for (int j = 0; j < modulVars.length; j++) {
//                passedModules[j] = model.arithm(modulVars[j], "<", modulVars[i]).reify();
//                model.arithm(cpVars[j]).post();
//            }
//        }



        //////////////
        // SOLVING: //
        //////////////

        Solver solver = model.getSolver();
        solver.showShortStatistics();
        Solution solution = solver.findSolution();
//        model.setObjective(Model.MINIMIZE, modulVars[25]);
//        Solution solution = model.getSolver().findOptimalSolution(modulVars[25], Model.MINIMIZE);

        String[] output = new String[maxSems];
        if (solution != null) {
//            // Remove unnecessary strings from solution
//            String result = "";
//            String[] resultArray = solution.toString().split(",");
//            for (String s : resultArray) {
//                if (!s.contains("TMP") && !s.contains("div_exp") && !s.contains("sum_exp") && !s.contains("mod_exp")) {
//                    result += s;
//                }
//            }
//            System.out.println(result.trim());

            // New output
            for (IntVar modulVar : modulVars) {
                String rowString = output[solution.getIntVal(modulVar)];
                int row = solution.getIntVal(modulVar);
                String modulName = StringPadding.rightPad(modulVar.getName(), 5);
                if (output[row] != null) {
                    output[row] += modulName + "   ";
                } else {
                    output[row] = modulName + "   ";
                }
            }
            for (int i = 0; i < maxSems; i++) {
                if (output[i] != null) {
                    System.out.println(StringPadding.leftPad("" + (i + 1), 2) + ". Sem:   " + output[i]);
                } else {
                    System.out.println(StringPadding.leftPad("" + (i + 1), 2) + ". Sem:   ");
                }
            }
        } else {
            System.out.println("NO SOLUTION FOUND!");
        }

//        List<Solution> solutions = solver.findAllSolutions();
//        if(solutions != null) {
//            int no = 0;
//            for(Solution sol : solutions) {
//                no++;
//                // Remove unnecessary strings from solution
//                String result = "";
//                String[] resultArray = sol.toString().split(",");
//                for (String s : resultArray) {
//                    if (!s.contains("TMP") && !s.contains("div_exp") && !s.contains("sum_exp") && !s.contains("mod_exp")) {
//                        result += s;
//                    }
//                }
//                System.out.println(no + ". " + result.trim());
//            }
//            System.out.println("Found " + solutions.size() + " solutions.");
//        }

    }

}

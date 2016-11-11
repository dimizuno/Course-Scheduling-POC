import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import util.StringPadding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by the two world leading experts in CP: asekulsk & dkotlovs.
 */
public class CourseScheduling_IndexBased {

    public static void main(String[] args) {
        solveModule();
    }

    private static void solveModule() {

        // Maximum modules per semester.
        int coursesPerTerm = 3;

        // Maximum semesters over all
        int maxTerms = 10;


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
        int numberOfCourses = modules.size();

        // CS-Model
        Model model = new Model("module problem");

        // Variables: modules
        IntVar[] modulVars = new IntVar[numberOfCourses];
        for (int i = 0; i < numberOfCourses; i++) {
            modulVars[i] = model.intVar(modules.get(i), 0, (maxTerms * coursesPerTerm) - 1);
        }

        // Variables: credit points
        int[] achievableCreditPoints = new int[]{
                5, 5, 7, 7, 6,  // TGI, TENI, GMI, EPR, LDS
                6, 5, 7, 6, 6,  // MIN, REN, OPR, ADS, THI
                6, 6, 6, 6, 6,  // BSY, INS, SWT, DBA, MCI
                6, 6, 6, 6, 6,  // SPIN1, IDB, INP, WM1, WM2
                6, 6, 6, 6, 6,  // SPIN2, PPR, WM3, WM4, WM5,
                12, 3, 15};     // BAIN, KBIN, PXP
        IntVar cp = model.intVar("cp", 0, 180); // 150 + 12 + 3 + 15
        IntVar[] cpVars = new IntVar[numberOfCourses];
        cpVars[0] = model.intVar(5);    // TGI
        cpVars[1] = model.intVar(5);    // TENI
        cpVars[2] = model.intVar(7);    // GMI
        cpVars[3] = model.intVar(7);    // EPR
        cpVars[4] = model.intVar(6);    // LDS
        cpVars[5] = model.intVar(6);    // MIN
        cpVars[6] = model.intVar(5);    // REN
        cpVars[7] = model.intVar(7);    // OPR
        cpVars[8] = model.intVar(6);    // ADS
        cpVars[9] = model.intVar(6);    // THI
        cpVars[10] = model.intVar(6);   // BSY              | premise: 30cp
        cpVars[11] = model.intVar(6);   // INS              | premise: 30cp
        cpVars[12] = model.intVar(6);   // SWT              | premise: 30cp
        cpVars[13] = model.intVar(6);   // DBA              | premise: 30cp
        cpVars[14] = model.intVar(6);   // MCI              | premise: 30cp
        cpVars[15] = model.intVar(6);   // SPIN1 (6 of 12)  | premise: 50cp
        cpVars[16] = model.intVar(6);   // IDB              | premise: 50cp
        cpVars[17] = model.intVar(6);   // INP              | premise: 50cp
        cpVars[18] = model.intVar(6);   // WM1              | premise: 50cp
        cpVars[19] = model.intVar(6);   // WM2              | premise: 50cp
        cpVars[20] = model.intVar(6);   // SPIN2 (6 of 12)  | premise: 50cp
        cpVars[21] = model.intVar(6);   // PPR              | premise: 70cp
        cpVars[22] = model.intVar(6);   // WM3              | premise: 70cp
        cpVars[23] = model.intVar(6);   // WM4              | premise: 70cp
        cpVars[24] = model.intVar(6);   // WM5              | premise: 70cp
        cpVars[25] = model.intVar(12);  // BAIN             | premise: 150cp
        cpVars[26] = model.intVar(3);   // KBIN             | premise: 150cp
        cpVars[27] = model.intVar(15);  // PPX              | premise: 90cp

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

        // Constraints: Alldiff
        model.allDifferent(modulVars).post();



        // Constraints: Module Dependencies
        // MIN: GMI
        setup_dependency2(model, modulVars[2], modulVars[5], coursesPerTerm);
        // OPR: EPR
        setup_dependency2(model, modulVars[3], modulVars[7], coursesPerTerm);
        // ADS: EPR, LDS
        setup_dependency2(model, modulVars[3], modulVars[8], coursesPerTerm);
        setup_dependency2(model, modulVars[4], modulVars[8], coursesPerTerm);
        // THI: EPR, LDS
        setup_dependency2(model, modulVars[3], modulVars[9], coursesPerTerm);
        setup_dependency2(model, modulVars[4], modulVars[9], coursesPerTerm);
        // BSY: TGI
        setup_dependency2(model, modulVars[0], modulVars[10], coursesPerTerm);
        // INS: EPR, OPR
        setup_dependency2(model, modulVars[3], modulVars[11], coursesPerTerm);
        setup_dependency2(model, modulVars[7], modulVars[11], coursesPerTerm);
        // SWT: GMI, EPR, LDS, OPR, ADS
        setup_dependency2(model, modulVars[2], modulVars[12], coursesPerTerm);
        setup_dependency2(model, modulVars[3], modulVars[12], coursesPerTerm);
        setup_dependency2(model, modulVars[4], modulVars[12], coursesPerTerm);
        setup_dependency2(model, modulVars[7], modulVars[12], coursesPerTerm);
        setup_dependency2(model, modulVars[8], modulVars[12], coursesPerTerm);
        // DBA: EPR, OPR
        setup_dependency2(model, modulVars[3], modulVars[13], coursesPerTerm);
        setup_dependency2(model, modulVars[7], modulVars[13], coursesPerTerm);
        // MCI: EPR, OPR
        setup_dependency2(model, modulVars[3], modulVars[14], coursesPerTerm);
        setup_dependency2(model, modulVars[7], modulVars[14], coursesPerTerm);
        // SPIN1: GMI, EPR, LDS, OPR, ADS, SWT, MCI
        setup_dependency2(model, modulVars[2], modulVars[15], coursesPerTerm);
        setup_dependency2(model, modulVars[3], modulVars[15], coursesPerTerm);
        setup_dependency2(model, modulVars[4], modulVars[15], coursesPerTerm);
        setup_dependency2(model, modulVars[7], modulVars[15], coursesPerTerm);
        setup_dependency2(model, modulVars[8], modulVars[15], coursesPerTerm);
        setup_dependency2(model, modulVars[12], modulVars[15], coursesPerTerm);
        setup_dependency2(model, modulVars[14], modulVars[15], coursesPerTerm);
        // IDB: EPR, OPR, INS, DBA
        setup_dependency2(model, modulVars[3], modulVars[16], coursesPerTerm);
        setup_dependency2(model, modulVars[7], modulVars[16], coursesPerTerm);
        setup_dependency2(model, modulVars[11], modulVars[16], coursesPerTerm);
        setup_dependency2(model, modulVars[13], modulVars[16], coursesPerTerm);
        // WM1
        model.arithm(modulVars[18], "<", modulVars[19]).post(); // WM1 < WM2
        model.arithm(modulVars[18], "<", modulVars[22]).post(); // WM1 < WM3
        model.arithm(modulVars[18], "<", modulVars[23]).post(); // WM1 < WM4
        model.arithm(modulVars[18], "<", modulVars[24]).post(); // WM1 < WM5
        // WM2
        model.arithm(modulVars[19], "<", modulVars[22]).post(); // WM2 < WM3
        model.arithm(modulVars[19], "<", modulVars[23]).post(); // WM2 < WM4
        model.arithm(modulVars[19], "<", modulVars[24]).post(); // WM2 < WM5
        // SPIN2: GMI, EPR, LDS, OPR, ADS, SWT, MCI, SPIN1
        setup_dependency2(model, modulVars[2], modulVars[20], coursesPerTerm);
        setup_dependency2(model, modulVars[3], modulVars[20], coursesPerTerm);
        setup_dependency2(model, modulVars[4], modulVars[20], coursesPerTerm);
        setup_dependency2(model, modulVars[7], modulVars[20], coursesPerTerm);
        setup_dependency2(model, modulVars[8], modulVars[20], coursesPerTerm);
        setup_dependency2(model, modulVars[12], modulVars[20], coursesPerTerm);
        setup_dependency2(model, modulVars[14], modulVars[20], coursesPerTerm);
        setup_dependency2(model, modulVars[15], modulVars[20], coursesPerTerm);
        model.arithm(modulVars[20], "=", modulVars[15].add(3).intVar()).post(); // SPIN2 has to be right after SPIN1
        // PPR: EPR, LDS, OPR, ADS
        setup_dependency2(model, modulVars[3], modulVars[21], coursesPerTerm);
        setup_dependency2(model, modulVars[4], modulVars[21], coursesPerTerm);
        setup_dependency2(model, modulVars[7], modulVars[21], coursesPerTerm);
        setup_dependency2(model, modulVars[8], modulVars[21], coursesPerTerm);
        // WM3
        model.arithm(modulVars[22], "<", modulVars[23]).post(); // WM3 < WM4
        model.arithm(modulVars[22], "<", modulVars[24]).post(); // WM3 < WM5
        // WM4
        model.arithm(modulVars[23], "<", modulVars[24]).post(); // WM4 < WM5
        // BAIN: All modules, PPX
        for (int i = 0; i <= 24; i++) {
            setup_dependency2(model, modulVars[i], modulVars[25], coursesPerTerm);
        }
        IntVar rowB = modulVars[25].div(coursesPerTerm).intVar();
        IntVar rowK = modulVars[26].div(coursesPerTerm).intVar();
        IntVar rowP = modulVars[27].div(coursesPerTerm).intVar();
        model.arithm(rowB, ">=", rowP).post(); // BAIN.row >= PXX.row
        model.arithm(rowB, "=", rowK).post(); // BAIN.row = KBIN.row
        model.arithm(modulVars[25], "<", modulVars[26]).post(); // BAIN < KBIN
        // PPX: all modules from the first 3 semesters
        for (int i = 0; i <= 14; i++) {
            setup_dependency2(model, modulVars[i], modulVars[27], coursesPerTerm);
        }



        // Constraints: WS cycle dependency (modules are only accessible during winter)
        setupCycleDependencyWS(model, modulVars[0], coursesPerTerm); // TGI
        setupCycleDependencyWS(model, modulVars[1], coursesPerTerm); // TENI
        setupCycleDependencyWS(model, modulVars[2], coursesPerTerm); // GMI
        setupCycleDependencyWS(model, modulVars[3], coursesPerTerm); // EPR
        setupCycleDependencyWS(model, modulVars[4], coursesPerTerm); // LDS
        setupCycleDependencyWS(model, modulVars[10], coursesPerTerm); // BSY
        setupCycleDependencyWS(model, modulVars[11], coursesPerTerm); // INS
        setupCycleDependencyWS(model, modulVars[12], coursesPerTerm); // SWT
        setupCycleDependencyWS(model, modulVars[13], coursesPerTerm); // DBA
        setupCycleDependencyWS(model, modulVars[14], coursesPerTerm); // MCI
        setupCycleDependencyWS(model, modulVars[20], coursesPerTerm); // SPIN2
        setupCycleDependencyWS(model, modulVars[21], coursesPerTerm); // PPR

        // Constraints: SS cycle dependency (modules are only accessible during summer)
        setupCycleDependencySS(model, modulVars[5], coursesPerTerm); // MIN
        setupCycleDependencySS(model, modulVars[6], coursesPerTerm); // REN
        setupCycleDependencySS(model, modulVars[7], coursesPerTerm); // OPR
        setupCycleDependencySS(model, modulVars[8], coursesPerTerm); // ADS
        setupCycleDependencySS(model, modulVars[9], coursesPerTerm); // THI
        setupCycleDependencySS(model, modulVars[15], coursesPerTerm); // SPIN1
        setupCycleDependencySS(model, modulVars[16], coursesPerTerm); // IDB
        setupCycleDependencySS(model, modulVars[17], coursesPerTerm); // INP
        setupCycleDependencySS(model, modulVars[27], coursesPerTerm); // PXP



        // Constraints: credit points premise and cycle dependency
        model.arithm(modulVars[5].div(coursesPerTerm).intVar(), ">", 0).post(); // MIN (redundant for SS)
        model.arithm(modulVars[6].div(coursesPerTerm).intVar(), ">", 0).post(); // REN (redundant for SS)
        model.arithm(modulVars[7].div(coursesPerTerm).intVar(), ">", 0).post(); // OPR (redundant for SS)
        model.arithm(modulVars[8].div(coursesPerTerm).intVar(), ">", 0).post(); // ADS (redundant for SS)
        model.arithm(modulVars[9].div(coursesPerTerm).intVar(), ">", 0).post(); // THI (redundant for SS)
        model.arithm(modulVars[10].div(coursesPerTerm).intVar(), ">", 1).post(); // BSY (30cp + WS)
        model.arithm(modulVars[11].div(coursesPerTerm).intVar(), ">", 1).post(); // INS (30cp + WS)
        model.arithm(modulVars[12].div(coursesPerTerm).intVar(), ">", 1).post(); // SWT (30cp + WS)
        model.arithm(modulVars[13].div(coursesPerTerm).intVar(), ">", 1).post(); // DBA (30cp + WS)
        model.arithm(modulVars[14].div(coursesPerTerm).intVar(), ">", 1).post(); // MCI (30cp + WS)
        model.arithm(modulVars[15].div(coursesPerTerm).intVar(), ">", 2).post(); // SPIN1 (50cp + SS)
        model.arithm(modulVars[16].div(coursesPerTerm).intVar(), ">", 2).post(); // IDB (50cp + SS)
        model.arithm(modulVars[17].div(coursesPerTerm).intVar(), ">", 2).post(); // INP (50cp + SS)
        model.arithm(modulVars[18].div(coursesPerTerm).intVar(), ">", 1).post(); // WM1 (50cp/70cp)
        model.arithm(modulVars[19].div(coursesPerTerm).intVar(), ">", 1).post(); // WM2 (50cp/70cp)
        model.arithm(modulVars[20].div(coursesPerTerm).intVar(), ">", 3).post(); // SPIN2 (50cp + SPIN1)
        model.arithm(modulVars[21].div(coursesPerTerm).intVar(), ">", 1).post(); // PPR (70cp + WS)
        model.arithm(modulVars[22].div(coursesPerTerm).intVar(), ">", 1).post(); // WM3 (50cp/70cp)
        model.arithm(modulVars[23].div(coursesPerTerm).intVar(), ">", 1).post(); // WM4 (50cp/70cp)
        model.arithm(modulVars[24].div(coursesPerTerm).intVar(), ">", 1).post(); // WM5 (50cp/70cp)
        model.arithm(modulVars[25].div(coursesPerTerm).intVar(), ">", 4).post(); // BAIN (150cp)
        model.arithm(modulVars[26].div(coursesPerTerm).intVar(), ">", 4).post(); // KBIN (150cp)
        model.arithm(modulVars[27].div(coursesPerTerm).intVar(), ">", 2).post(); // PXP (90cp + SS)

//        IntVar[] cpCounts = new IntVar[5];
//        for (int i = 10; i <= 14; i++) {  // "BSY", "INS", "SWT", "DBA", "MCI"
//            cpCounts[i - 10] = model.intVar(0);
//            BoolVar[] passedModules = new BoolVar[numberOfCourses];
//            ArrayList list = new ArrayList();
//            for (int j = 0; j < modulVars.length; j++) {
//                passedModules[j] = model.arithm(modulVars[j], "<", modulVars[i]).reify();
//                cpCounts[i - 10] = model.arithm(cpVars[j]).post();
//                model.ifThen(
//                    model.arithm(modulVars[j], "<", modulVars[i]),
//                    list.add(cpVars[j]),
//                );
//            }
//            mode.sum()
//        }

        // BSY
//        IntVar cps = model.intVar(0);
//        for (int i = 0; i < numberOfCourses; i++) {
//            IntVar rowI = modulVars[i].div(coursesPerTerm).intVar();
//            IntVar rowM = modulVars[10].div(coursesPerTerm).intVar();
//            model.ifThen(
//                    model.arithm(rowI, "<", rowM),
//                    cps = model.intOffsetView(cps, 2)
//            );
//            BoolVar passed = model.arithm(rowI, "<", rowM).reify(); // modul_i.row < BSY.row
//        }

        IntVar[] points = model.intVarArray("points", maxTerms * coursesPerTerm, 0, 15, true);
//        for (int i = 0; i < numberOfCourses; i++) {
//            model.element(cpVars[i], points, modulVars[i]).post();
//        }
//        model.sum(points[j], ">=", 15).post();

        for (int i = 0; i < numberOfCourses; i++) {
            for (int j = 0; j < maxTerms * coursesPerTerm; j++) {
//                model.ifThen(
//                        model.arithm(modulVars[i], "=", j),
//                        model.arithm(points[j], "=", achievableCreditPoints[i])
//                );

//                model.ifThen(
//                        model.arithm(modulVars[i], "=", j),
//                        model.element(points[j], achievableCreditPoints, model.intVar(i))
//                );

//                model.ifThenElse(
//                        model.arithm(modulVars[i], "=", j),
//                        model.arithm(points[j], "=", achievableCreditPoints[i]),
//                        model.arithm(points[j], "=", 0)
//                );
            }
        }



        //////////////
        // SOLVING: //
        //////////////

        Solver solver = model.getSolver();
        solver.showShortStatistics();

        Solution solution = solver.findSolution();
//        model.setObjective(Model.MINIMIZE, modulVars[25]);
//        Solution solution = model.getSolver().findOptimalSolution(modulVars[25], Model.MINIMIZE);

        String[] output = new String[maxTerms * coursesPerTerm];
        if (solution != null) {
            String result = "";
            String[] resultArray = solution.toString().split(",");
            for (String s : resultArray) {
                if (s.contains("points")) {
                    result += s + "\n";
                }
            }
            System.out.println(result.trim());
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
                output[solution.getIntVal(modulVar)] = modulVar.getName();
            }
            for (int i = 0; i < maxTerms; i++) {
                String mods = "";
                int start = i * coursesPerTerm;
                for (int j = start; j < (start + coursesPerTerm); j++) {
                    String modulName = (output[j] == null) ? "" : output[j];
                    mods += StringPadding.rightPad(modulName, 5) + "   ";
                }
                System.out.println(StringPadding.leftPad("" + (i + 1), 2) + ". Sem:   " + mods);
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
//                result = "";
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

    private static void setupCycleDependencyWS(Model model, IntVar a, int modsPerSem) {
        IntVar rowMod = a.div(modsPerSem).mod(2).intVar();
        model.arithm(rowMod, "=", 0).post(); // even
    }

    private static void setupCycleDependencySS(Model model, IntVar a, int modsPerSem) {
        IntVar rowMod = a.div(modsPerSem).mod(2).intVar();
        model.arithm(rowMod, "=", 1).post(); // odd
    }

    private static void setup_dependency2(Model model, IntVar a, IntVar b, int modsPerSem) {
        IntVar rowB = b.div(modsPerSem).intVar();
        model.arithm(rowB, ">", a.div(modsPerSem).intVar()).post();
    }

}

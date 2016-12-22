import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.exception.ContradictionException;
import org.chocosolver.solver.search.loop.monitors.IMonitorOpenNode;
import org.chocosolver.solver.search.loop.monitors.IMonitorSolution;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.SetVar;
import org.chocosolver.solver.variables.Variable;
import org.chocosolver.solver.variables.impl.BitsetIntVarImpl;
import org.chocosolver.solver.variables.impl.IntervalIntVarImpl;
import util.StringPadding;

import java.util.Arrays;
import java.util.List;

/**
 * Course Scheduling problem. Solved with an row based solution (see documentation). This is best solution so far.
 *
 * @authors Dimitri Kotlovsky, Andreas Sekulski
 */
public class CourseScheduling_RowBased {

    public static void main(String[] args) {
        solveProblem();
    }

    private static void solveProblem() {

        long startTime = System.nanoTime();

        // Maximum courses per term
        int coursesPerTerm = 3;

        // Maximum terms over all
        int maxTerms = 10;



        ////////////
        // MODEL: //
        ////////////

        // x-axis: number of curses per term
        // y-axis: number of necessary terms over all
        //
        //         x0   x1   x2   x3   ...
        //    y0   GMI  EPR  LDS  -
        //    y1   MIN  OPR  ADS  -
        //    y2   INS  SWT  DBA  -
        //    y3   ...  ...  ...  -
        //    y4
        //    ...

        // Modules
        List<String> courseNames = Arrays.asList(
                "TGI", "TENI", "GMI", "EPR", "LDS", // 0, 1, 2, 3, 4
                "MIN", "REN", "OPR", "ADS", "THI", // 5, 6, 7, 8, 9
                "BSY", "INS", "SWT", "DBA", "MCI", // 10, 11, 12, 13, 14
                "SPIN1", "IDB", "INP", "WM1", "WM2", // 15, 16, 17, 18, 19
                "SPIN2", "PPR", "WM3", "WM4", "WM5", // 20, 21, 22, 23, 24
                "BAIN", "KBIN", "PXP"); //25, 26, 27

        // Selectable courses
//        List<String> selModules = Arrays.asList(
//                "BWIN", "BV", "ITS", "ITR", "KBE", "MRO", "OPC", // 0, 1, 2, 3, 4, 5, 6
//                "BKV", "KI", "MOC", "PAP", "ROB", "SWD"); // 7, 8, 9, 10, 11, 12

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

        // Amount of courses to be considered
        int numberOfCourses = courseNames.size();

        // CS-Model
        Model model = new Model("CourseScheduling_RowBased");

        // Variables: courses
        IntVar[] courseVars = new IntVar[numberOfCourses];
        for (int i = 0; i < numberOfCourses; i++) {
            courseVars[i] = model.intVar(courseNames.get(i), 0, maxTerms - 1, false);
        }

        // Variables: credit points
        int[] achievableCreditPoints = new int[]{
                5, 5, 7, 7, 6,  // TGI, TENI, GMI, EPR, LDS
                6, 5, 7, 6, 6,  // MIN, REN, OPR, ADS, THI
                6, 6, 6, 6, 6,  // BSY, INS, SWT, DBA, MCI
                6, 6, 6, 6, 6,  // SPIN1, IDB, INP, WM1, WM2
                6, 6, 6, 6, 6,  // SPIN2, PPR, WM3, WM4, WM5,
                12, 3, 15};     // BAIN, KBIN, PXP

        int[] courseNumbers = new int[]{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27};
        SetVar[] scheduledCourses = model.setVarArray("scheduledCourses", maxTerms, new int[]{}, courseNumbers);
        IntVar[] points = model.intVarArray(maxTerms, 15, 21); // consider to make a 0-100 bound and set two constraints >= 15 and <= 21 (more efficient)



        //////////////////
        // CONSTRAINTS: //
        //////////////////

        // Constraints: maximum courses per term
//        for (int term = 0; term < maxTerms; term++) {
//            IntVar coursesInTerm = model.intVar("coursesInTerm_" + term, 0, coursesPerTerm, true);
//            model.count(term, courseVars, coursesInTerm).post();
//        }


        // Constraints: Module Dependencies
        // MIN: GMI
        model.arithm(courseVars[5], ">", courseVars[2]).post();
        // OPR: EPR
        model.arithm(courseVars[7], ">", courseVars[3]).post();
        // ADS: EPR, LDS
        model.arithm(courseVars[8], ">", courseVars[3]).post();
        model.arithm(courseVars[8], ">", courseVars[4]).post();
        // THI: EPR, LDS
        model.arithm(courseVars[9], ">", courseVars[3]).post();
        model.arithm(courseVars[9], ">", courseVars[4]).post();
        // BSY: TGI
        model.arithm(courseVars[10], ">", courseVars[0]).post();
        // INS: EPR, OPR
        model.arithm(courseVars[11], ">", courseVars[3]).post();
        model.arithm(courseVars[11], ">", courseVars[7]).post();
        // SWT: GMI, EPR, LDS, OPR, ADS
        model.arithm(courseVars[12], ">", courseVars[2]).post();
        model.arithm(courseVars[12], ">", courseVars[3]).post();
        model.arithm(courseVars[12], ">", courseVars[4]).post();
        model.arithm(courseVars[12], ">", courseVars[7]).post();
        model.arithm(courseVars[12], ">", courseVars[8]).post();
        // DBA: EPR, OPR
        model.arithm(courseVars[13], ">", courseVars[3]).post();
        model.arithm(courseVars[13], ">", courseVars[7]).post();
        // MCI: EPR, OPR
        model.arithm(courseVars[14], ">", courseVars[3]).post();
        model.arithm(courseVars[14], ">", courseVars[7]).post();
        // SPIN1: GMI, EPR, LDS, OPR, ADS, SWT, MCI
        model.arithm(courseVars[15], ">", courseVars[2]).post();
        model.arithm(courseVars[15], ">", courseVars[3]).post();
        model.arithm(courseVars[15], ">", courseVars[4]).post();
        model.arithm(courseVars[15], ">", courseVars[7]).post();
        model.arithm(courseVars[15], ">", courseVars[8]).post();
        model.arithm(courseVars[15], ">", courseVars[12]).post();
        model.arithm(courseVars[15], ">", courseVars[14]).post();
        // IDB: EPR, OPR, INS, DBA
        model.arithm(courseVars[16], ">", courseVars[3]).post();
        model.arithm(courseVars[16], ">", courseVars[7]).post();
        model.arithm(courseVars[16], ">", courseVars[11]).post();
        model.arithm(courseVars[16], ">", courseVars[13]).post();
        // WM1
        model.arithm(courseVars[18], "<=", courseVars[19]).post(); // WM1.row <= WM2.row
        model.arithm(courseVars[18], "<=", courseVars[22]).post(); // WM1.row <= WM3.row
        model.arithm(courseVars[18], "<=", courseVars[23]).post(); // WM1.row <= WM4.row
        model.arithm(courseVars[18], "<=", courseVars[24]).post(); // WM1.row <= WM5.row
        // WM2
        model.arithm(courseVars[19], "<=", courseVars[22]).post(); // WM2.row <= WM3.row
        model.arithm(courseVars[19], "<=", courseVars[23]).post(); // WM2.row <= WM4.row
        model.arithm(courseVars[19], "<=", courseVars[24]).post(); // WM2.row <= WM5.row
        // SPIN2: GMI, EPR, LDS, OPR, ADS, SWT, MCI, SPIN1
        model.arithm(courseVars[20], ">", courseVars[2]).post();
        model.arithm(courseVars[20], ">", courseVars[3]).post();
        model.arithm(courseVars[20], ">", courseVars[4]).post();
        model.arithm(courseVars[20], ">", courseVars[7]).post();
        model.arithm(courseVars[20], ">", courseVars[8]).post();
        model.arithm(courseVars[20], ">", courseVars[12]).post();
        model.arithm(courseVars[20], ">", courseVars[14]).post();
        model.arithm(courseVars[20], ">", courseVars[15]).post();
        model.arithm(courseVars[20], "=", courseVars[15].add(1).intVar()).post(); // SPIN2 has to be right after SPIN1
        // PPR: EPR, LDS, OPR, ADS
        model.arithm(courseVars[21], ">", courseVars[3]).post();
        model.arithm(courseVars[21], ">", courseVars[4]).post();
        model.arithm(courseVars[21], ">", courseVars[7]).post();
        model.arithm(courseVars[21], ">", courseVars[8]).post();
        // WM3
        model.arithm(courseVars[22], "<=", courseVars[23]).post(); // WM3.row <= WM4.row
        model.arithm(courseVars[22], "<=", courseVars[24]).post(); // WM3.row <= WM5.row
        // WM4
        model.arithm(courseVars[23], "<=", courseVars[24]).post(); // WM4.row <= WM5.row
        // BAIN/KBIN: All courses, PPX
        for (int i = 0; i <= 24; i++) {
            model.arithm(courseVars[25], ">", courseVars[i]).post();
            model.arithm(courseVars[26], ">", courseVars[i]).post();
        }
        model.arithm(courseVars[25], ">=", courseVars[27]).post(); // BAIN.row >= PXX.row
        model.arithm(courseVars[26], ">=", courseVars[27]).post(); // KBIN.row >= PXX.row
        model.arithm(courseVars[25], "=", courseVars[26]).post();  // BAIN.row = KBIN.row
        // PPX: all courses from the first 3 terms
        for (int i = 0; i <= 14; i++) {
            model.arithm(courseVars[27], ">", courseVars[i]).post();
        }


        // Constraints: WS cycle dependency (courses are only accessible during winter)
        model.arithm(courseVars[0].mod(2).intVar(), "=", 0).post(); // TGI
        model.arithm(courseVars[1].mod(2).intVar(), "=", 0).post(); // TENI
        model.arithm(courseVars[2].mod(2).intVar(), "=", 0).post(); // GMI
        model.arithm(courseVars[3].mod(2).intVar(), "=", 0).post(); // EPR
        model.arithm(courseVars[4].mod(2).intVar(), "=", 0).post(); // LDS
        model.arithm(courseVars[10].mod(2).intVar(), "=", 0).post(); // BSY
        model.arithm(courseVars[11].mod(2).intVar(), "=", 0).post(); // INS
        model.arithm(courseVars[12].mod(2).intVar(), "=", 0).post(); // SWT
        model.arithm(courseVars[13].mod(2).intVar(), "=", 0).post(); // DBA
        model.arithm(courseVars[14].mod(2).intVar(), "=", 0).post(); // MCI
        model.arithm(courseVars[20].mod(2).intVar(), "=", 0).post(); // SPIN2
        model.arithm(courseVars[21].mod(2).intVar(), "=", 0).post(); // PPR

        // Constraints: SS cycle dependency (courses are only accessible during summer)
        model.arithm(courseVars[5].mod(2).intVar(), "=", 1).post(); // MIN
        model.arithm(courseVars[6].mod(2).intVar(), "=", 1).post(); // REN
        model.arithm(courseVars[7].mod(2).intVar(), "=", 1).post(); // OPR
        model.arithm(courseVars[8].mod(2).intVar(), "=", 1).post(); // ADS
        model.arithm(courseVars[9].mod(2).intVar(), "=", 1).post(); // THI
        model.arithm(courseVars[15].mod(2).intVar(), "=", 1).post(); // SPIN1
        model.arithm(courseVars[16].mod(2).intVar(), "=", 1).post(); // IDB
        model.arithm(courseVars[17].mod(2).intVar(), "=", 1).post(); // INP
        model.arithm(courseVars[27].mod(2).intVar(), "=", 1).post(); // PXP


        // Constraints: credit points premise and cycle dependency
        model.arithm(courseVars[5], ">", 0).post(); // MIN (redundant for SS)
        model.arithm(courseVars[6], ">", 0).post(); // REN (redundant for SS)
        model.arithm(courseVars[7], ">", 0).post(); // OPR (redundant for SS)
        model.arithm(courseVars[8], ">", 0).post(); // ADS (redundant for SS)
        model.arithm(courseVars[9], ">", 0).post(); // THI (redundant for SS)
        model.arithm(courseVars[10], ">", 1).post(); // BSY (30cp + WS)
        model.arithm(courseVars[11], ">", 1).post(); // INS (30cp + WS)
        model.arithm(courseVars[12], ">", 1).post(); // SWT (30cp + WS)
        model.arithm(courseVars[13], ">", 1).post(); // DBA (30cp + WS)
        model.arithm(courseVars[14], ">", 1).post(); // MCI (30cp + WS)
        model.arithm(courseVars[15], ">", 2).post(); // SPIN1 (50cp + SS)
        model.arithm(courseVars[16], ">", 2).post(); // IDB (50cp + SS)
        model.arithm(courseVars[17], ">", 2).post(); // INP (50cp + SS)
        model.arithm(courseVars[18], ">", 1).post(); // WM1 (50cp/70cp)
        model.arithm(courseVars[19], ">", 1).post(); // WM2 (50cp/70cp)
        model.arithm(courseVars[20], ">", 3).post(); // SPIN2 (50cp + SPIN1)
        model.arithm(courseVars[21], ">", 1).post(); // PPR (70cp + WS)
        model.arithm(courseVars[22], ">", 1).post(); // WM3 (50cp/70cp)
        model.arithm(courseVars[23], ">", 1).post(); // WM4 (50cp/70cp)
        model.arithm(courseVars[24], ">", 1).post(); // WM5 (50cp/70cp)
        model.arithm(courseVars[25], ">", 4).post(); // BAIN (150cp)
        model.arithm(courseVars[26], ">", 4).post(); // KBIN (150cp)
        model.arithm(courseVars[27], ">", 2).post(); // PXP (90cp + SS)

        // Constraints: calculate credit points
        for (int term = 0; term < maxTerms; term++) {

            // Check which courses are scheduled to which term
            for (int course = 0; course < numberOfCourses; course++) {
                model.ifThenElse(
                        model.arithm(courseVars[course], "=", term),
                        model.member(course, scheduledCourses[term]),
                        model.notMember(course, scheduledCourses[term])
                );
            }

            // Calculate achieved credit points for each term
            model.sumElements(scheduledCourses[term], achievableCreditPoints, points[term]).post();
        }

        // Constraints: credit points premise
        for (int term = 1; term < maxTerms; term++) {
            IntVar[] p = new IntVar[term];
            for (int k = term - 1; k >= 0; k--) {
                p[k] = points[k];
            }
            Constraint min30Points = model.sum(p, ">=", 30);
            Constraint min50Points = model.sum(p, ">=", 50);
            Constraint min70Points = model.sum(p, ">=", 70);
            Constraint min90Points = model.sum(p, ">=", 90);
            Constraint min150Points = model.sum(p, ">=", 150);

            BoolVar BSY = model.arithm(courseVars[10], "=", term).reify();
            BoolVar INS = model.arithm(courseVars[11], "=", term).reify();
            BoolVar SWT = model.arithm(courseVars[12], "=", term).reify();
            BoolVar DBA = model.arithm(courseVars[13], "=", term).reify();
            BoolVar MCI = model.arithm(courseVars[14], "=", term).reify();
            BoolVar SPIN1 = model.arithm(courseVars[15], "=", term).reify();
            BoolVar IDB = model.arithm(courseVars[16], "=", term).reify();
            BoolVar INP = model.arithm(courseVars[17], "=", term).reify();
            BoolVar WM1 = model.arithm(courseVars[18], "=", term).reify();
            BoolVar WM2 = model.arithm(courseVars[19], "=", term).reify();
            BoolVar SPIN2 = model.arithm(courseVars[20], "=", term).reify();
            BoolVar PPR = model.arithm(courseVars[21], "=", term).reify();
            BoolVar WM3 = model.arithm(courseVars[22], "=", term).reify();
            BoolVar WM4 = model.arithm(courseVars[23], "=", term).reify();
            BoolVar WM5 = model.arithm(courseVars[24], "=", term).reify();
            BoolVar BAIN = model.arithm(courseVars[25], "=", term).reify();
            BoolVar KBIN = model.arithm(courseVars[26], "=", term).reify();
            BoolVar PPX = model.arithm(courseVars[27], "=", term).reify();

            model.ifThen(BSY, min30Points);
            model.ifThen(INS, min30Points);
            model.ifThen(SWT, min30Points);
            model.ifThen(DBA, min30Points);
            model.ifThen(MCI, min30Points);
            model.ifThen(SPIN1, min50Points);
            model.ifThen(IDB, min50Points);
            model.ifThen(INP, min50Points);
            model.ifThen(WM1, min50Points);
            model.ifThen(WM2, min50Points);
            model.ifThen(SPIN2, min50Points);
            model.ifThen(PPR, min70Points);
            model.ifThen(WM3, min70Points);
            model.ifThen(WM4, min70Points);
            model.ifThen(WM5, min70Points);
            model.ifThen(BAIN, min150Points);
            model.ifThen(KBIN, min150Points);
            model.ifThen(PPX, min90Points);
        }



        //////////////
        // SOLVING: //
        //////////////

        final Solver solver = model.getSolver();

        solver.showShortStatistics();
        solver.showStatistics();
        solver.showDecisions();
        solver.showContradiction();

//        solver.plugMonitor(new IMonitorOpenNode() {
//            public void beforeOpenNode() {
//                System.out.println(Arrays.toString(solver.getSearch().getVariables()));
//                Variable[] variables = solver.getSearch().getVariables();
//                BitsetIntVarImpl var = (BitsetIntVarImpl) variables[1];
//                IntervalIntVarImpl var = (IntervalIntVarImpl) variables[1];
//                System.out.println(variables[1].getName() + variables[1].getClass().getName());
//            }
//        });

        Solution solution = solver.findSolution();
        printSolution(solution, maxTerms, courseVars);

//        solver.limitSolution(100);
//        List<Solution> solutions = solver.findAllSolutions();
//        if(solutions != null) {
//            System.out.println("Number of all solutions: " + solutions.size());
//        }

        long estimatedTime = System.nanoTime() - startTime;
        long seconds = estimatedTime / 1000000000;
        System.out.println("Estimated time: " + seconds / 60 + " min");
    }

    private static void printSolution(Solution solution, int maxTerms, IntVar[] courseVars) {
        String[] output = new String[maxTerms];
        if (solution != null) {
            for (IntVar courseVar : courseVars) {
                int row = solution.getIntVal(courseVar);
                String courseName = StringPadding.rightPad(courseVar.getName(), 5);
                if (output[row] != null) {
                    output[row] += courseName + "   ";
                } else {
                    output[row] = courseName + "   ";
                }
            }
            for (int i = 0; i < maxTerms; i++) {
                if (output[i] != null) {
                    System.out.println(StringPadding.leftPad("" + (i + 1), 2) + ". Sem:   " + output[i]);
                } else {
                    System.out.println(StringPadding.leftPad("" + (i + 1), 2) + ". Sem:   ");
                }
            }
        } else {
            System.out.println("NO SOLUTION FOUND!");
        }
    }

}

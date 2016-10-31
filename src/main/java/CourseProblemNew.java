import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.tools.ArrayUtils;
import util.StringPadding;

import java.util.Arrays;

/**
 * Created by Dimi on 27.10.2016.
 */
public class CourseProblemNew {

    public static void main(String [] args) {
        solveProblem();
    }

    private static void solveProblem() {

        // load parameters
        int maxCoursesPerTerm = 3;
        int numberOfCourses = 28;   // stores
        int maxTerms = 10;          // warehouses
//        int maintenanceCost = 30;
//        int[] achievableCreditPoints = new int[]{
//                5, 5, 7, 7, 6,  // TGI, TENI, GMI, EPR, LDS
//                6, 5, 7, 6, 6,  // MIN, REN, OPR, ADS, THI
//                6, 6, 6, 6, 6,  // BSY, INS, SWT, DBA, MCI
//                6, 6, 6, 6, 6,  // SPIN1, IDB, INP, WM1, WM2
//                6, 6, 6, 6, 6,  // SPIN2, PPR, WM3, WM4, WM5,
//                12, 3, 15};     // BAIN, KBIN, PXP
//        int[][] matrixOfCreditPoints = new int[][]{ // x=terms | y=courses
//                {5, 5, 7, 7, 6, 6, 5, 7, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 12, 3, 15}, // term 1
//                {5, 5, 7, 7, 6, 6, 5, 7, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 12, 3, 15}, // term 2
//                {5, 5, 7, 7, 6, 6, 5, 7, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 12, 3, 15}, // term 3
//                {5, 5, 7, 7, 6, 6, 5, 7, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 12, 3, 15}, // term 4
//                {5, 5, 7, 7, 6, 6, 5, 7, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 12, 3, 15}, // term 5
//                {5, 5, 7, 7, 6, 6, 5, 7, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 12, 3, 15}, // term 6
//                {5, 5, 7, 7, 6, 6, 5, 7, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 12, 3, 15}, // term 7
//                {5, 5, 7, 7, 6, 6, 5, 7, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 12, 3, 15}, // term 8
//                {5, 5, 7, 7, 6, 6, 5, 7, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 12, 3, 15}, // term 9
//                {5, 5, 7, 7, 6, 6, 5, 7, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 12, 3, 15}  // term 10
//        };

        // x=courses | y=terms
        int[][] achievableCreditPoints = new int[][]{
                {5, 5, 5, 5, 5, 5, 5, 5, 5, 5},  // TGI
                {5, 5, 5, 5, 5, 5, 5, 5, 5, 5},  // TENI
                {7, 7, 7, 7, 7, 7, 7, 7, 7, 7},  // GMI
                {7, 7, 7, 7, 7, 7, 7, 7, 7, 7},  // EPR
                {6, 6, 6, 6, 6, 6, 6, 6, 6, 6},  // LDS
                {6, 6, 6, 6, 6, 6, 6, 6, 6, 6},  // MIN
                {5, 5, 5, 5, 5, 5, 5, 5, 5, 5},  // REN
                {7, 7, 7, 7, 7, 7, 7, 7, 7, 7},  // OPR
                {6, 6, 6, 6, 6, 6, 6, 6, 6, 6},  // ADS
                {6, 6, 6, 6, 6, 6, 6, 6, 6, 6},  // THI
                {6, 6, 6, 6, 6, 6, 6, 6, 6, 6},  // BSY
                {6, 6, 6, 6, 6, 6, 6, 6, 6, 6},  // INS
                {6, 6, 6, 6, 6, 6, 6, 6, 6, 6},  // SWT
                {6, 6, 6, 6, 6, 6, 6, 6, 6, 6},  // DBA
                {6, 6, 6, 6, 6, 6, 6, 6, 6, 6},  // MCI
                {6, 6, 6, 6, 6, 6, 6, 6, 6, 6},  // SPIN1
                {6, 6, 6, 6, 6, 6, 6, 6, 6, 6},  // IDB
                {6, 6, 6, 6, 6, 6, 6, 6, 6, 6},  // INP
                {6, 6, 6, 6, 6, 6, 6, 6, 6, 6},  // WM1
                {6, 6, 6, 6, 6, 6, 6, 6, 6, 6},  // WM2
                {6, 6, 6, 6, 6, 6, 6, 6, 6, 6},  // SPIN2
                {6, 6, 6, 6, 6, 6, 6, 6, 6, 6},  // PPR
                {6, 6, 6, 6, 6, 6, 6, 6, 6, 6},  // WM3
                {6, 6, 6, 6, 6, 6, 6, 6, 6, 6},  // WM4
                {6, 6, 6, 6, 6, 6, 6, 6, 6, 6},  // WM5
                {12, 12, 12, 12, 12, 12, 12, 12, 12, 12},  // BAIN
                {3, 3, 3, 3, 3, 3, 3, 3, 3, 3},  // KBIN
                {15, 15, 15, 15, 15, 15, 15, 15, 15, 15}}; // PXP

        // A new model instance
        Model model = new Model("CourseScheduling");

        // VARIABLES

        //     _____________________
        //     |         |         |
        //     |  termNr |  used   |
        //     |_________|_________|
        //     |    0    |  true   |
        //     |    1    |  true   |
        //     |    2    |  true   |
        //     |    3    |  false  |
        //     |    4    |  true   |
        //     |    5    |  true   |
        //     |    6    |  false  |
        //     |    7    |  false  |
        //     |    8    |  false  |
        //     |   ...   |   ...   |
        //
//        IntVar[] terms = model.intVarArray("terms", maxTerms, 0, maxCoursesPerTerm, false); //true ?
        BoolVar[] terms = model.boolVarArray("terms", maxTerms);

        //     _____________________
        //     |         |         |
        //     |  course | termNr  |
        //     |_________|_________|
        //     |    0    |    0    |
        //     |    1    |    0    |
        //     |    2    |    0    |
        //     |    3    |    1    |
        //     |    4    |    1    |
        //     |    5    |    1    |
        //     |    6    |    2    |
        //     |    7    |    2    |
        //     |    8    |    2    |
        //     |   ...   |   ...   |
        //
        IntVar[] courses =  model.intVarArray("courses", numberOfCourses, 0, maxTerms - 1);

        // supplying cost per store
//        IntVar[] costs = model.intVarArray("costs", numberOfCourses, 3, 15, true);

        // Total credit points
//        IntVar tot_costs = model.intVar("tot_costs", 0, 150, true);

        // CONSTRAINTS
        for (int j = 0; j < numberOfCourses; j++) {
            // a term is 'used', if a course is set
//            model.element(model.intVar(1), terms, courses[j], 0).post();
            // Compute credit points for each course
//            model.element(costs[j], achievableCreditPoints[j], courses[j], 0).post();
        }
        for (int i = 0; i < maxTerms; i++) {
            // additional variable 'occ' is created on the fly
            // its domain includes the constraint on capacity
            IntVar occ = model.intVar("occur_" + i, 0, maxCoursesPerTerm, true);
            // we count occurrences of term indices in 'courses' with the maximum of maxCoursesPerTerm
            model.count(i, courses, occ).post();
            // redundant link between 'occ' and 'terms' for better propagation
//            occ.ge(terms[i]).post();
        }

        // Prepare the constraint that maintains 'tot_cost'
//        int[] coeffs = new int[maxTerms + numberOfCourses];
//        Arrays.fill(coeffs, 0, maxTerms, 1);
//        Arrays.fill(coeffs, maxTerms, maxTerms + numberOfCourses, 1);
//        // then post it
//        model.scalar(ArrayUtils.append(terms, costs), coeffs, "=", tot_costs).post();
//
//        model.setObjective(false, tot_costs);
        Solver solver = model.getSolver();
//        solver.setSearch(new IntStrategy(
//                ...,
//                new VariableSelectorWithTies(
//                        new FirstFail(model),
//                        new Smallest()),
//                new IntDomainMiddle(false),
//                ...
//        ));
//
//        solver.set(SearchStrategyFactory.intVarSearch(
//                new VariableSelectorWithTies<>(
//                        new FirstFail(model),
//                        new Smallest()),
//                ValSelectorFactory.midIntVal(false),
//                ArrayUtils.append(supplier, cost, open))
//        );

        solver.showShortStatistics();
        Solution solution = solver.findSolution();
        if (solution != null) {
            System.out.println(solution.toString());
        }

        String[] output = new String[maxTerms];
        if (solution != null) {
            for (IntVar course : courses) {
                int row = solution.getIntVal(course);
                String modulName = StringPadding.rightPad(course.getName(), 11);
                if (output[row] != null) {
                    output[row] += modulName + "   ";
                } else {
                    output[row] = modulName + "   ";
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

//        while (solver.solve()) {
//            prettyPrint(model, openWarehouses, numberOfWarehouses, supplier, numberOfStores, tot_cost);
//        }

        // Find a solution that minimizes 'tot_cost'
//        Solution best = solver.findOptimalSolution(tot_cost, false);






//        // A new model instance
//        Model model = new Model("CourseScheduling");
//
//        // VARIABLES
//        // a course is already scheduled or not
//        BoolVar[] openCourses = model.boolVarArray("o", numberOfCourses);
//
//        // which course fits to witch term
//        //     ___________________________________
//        //     |         |         |         |
//        //     |  term1  |  term2  |  term3  | ...
//        //     |_________|_________|_________|____
//        //     | course1 | course4 | course7 | ...
//        //     | course2 | course5 | course8 | ...
//        //     | course3 | course6 | course9 | ...
//        //     |   ...   |   ...   |   ...   | ...
//        //
//        IntVar[][] plan = new IntVar[maxTerms][];
//        for (int i = 0; i < maxTerms; i++) {
//            plan[i] = model.intVarArray("semester_"+(i+1), maxCoursesPerTerm, 0, numberOfCourses, true);
//        }
//
//        // supplying cost per store
//        IntVar[] cost = model.intVarArray("cost", maxTerms, 1, 150, true);
//        // Total of all costs
//        IntVar tot_cost = model.intVar("tot_cost", 0, 99999, true);






//        // load parameters
//        int numberOfTerms = 10; // warehouses
//        int numberOfCourses = 28; // stores
////        int maintenanceCost = 30;
//        int[] capacityOfEachTerm = new int[]{3, 3, 3, 3, 3, 3, 3, 3, 3, 3}; // TO BE CHANGED TO ONE INT !!!
////        int[] achievableCreditPoints = new int[]{
////                5, 5, 7, 7, 6,  // TGI, TENI, GMI, EPR, LDS
////                6, 5, 7, 6, 6,  // MIN, REN, OPR, ADS, THI
////                6, 6, 6, 6, 6,  // BSY, INS, SWT, DBA, MCI
////                6, 6, 6, 6, 6,  // SPIN1, IDB, INP, WM1, WM2
////                6, 6, 6, 6, 6,  // SPIN2, PPR, WM3, WM4, WM5,
////                12, 3, 15};     // BAIN, KBIN, PXP
//        int[][] achievableCreditPoints = new int[][]{ // x=courses | y=terms
//                {5, 5, 5, 5, 5, 5, 5, 5, 5, 5},  // TGI
//                {5, 5, 5, 5, 5, 5, 5, 5, 5, 5},  // TENI
//                {7, 7, 7, 7, 7, 7, 7, 7, 7, 7},  // GMI
//                {7, 7, 7, 7, 7, 7, 7, 7, 7, 7},  // EPR
//                {6, 6, 6, 6, 6, 6, 6, 6, 6, 6},  // LDS
//                {6, 6, 6, 6, 6, 6, 6, 6, 6, 6},  // MIN
//                {5, 5, 5, 5, 5, 5, 5, 5, 5, 5},  // REN
//                {7, 7, 7, 7, 7, 7, 7, 7, 7, 7},  // OPR
//                {6, 6, 6, 6, 6, 6, 6, 6, 6, 6},  // ADS
//                {6, 6, 6, 6, 6, 6, 6, 6, 6, 6},  // THI
//                {6, 6, 6, 6, 6, 6, 6, 6, 6, 6},  // BSY
//                {6, 6, 6, 6, 6, 6, 6, 6, 6, 6},  // INS
//                {6, 6, 6, 6, 6, 6, 6, 6, 6, 6},  // SWT
//                {6, 6, 6, 6, 6, 6, 6, 6, 6, 6},  // DBA
//                {6, 6, 6, 6, 6, 6, 6, 6, 6, 6},  // MCI
//                {6, 6, 6, 6, 6, 6, 6, 6, 6, 6},  // SPIN1
//                {6, 6, 6, 6, 6, 6, 6, 6, 6, 6},  // IDB
//                {6, 6, 6, 6, 6, 6, 6, 6, 6, 6},  // INP
//                {6, 6, 6, 6, 6, 6, 6, 6, 6, 6},  // WM1
//                {6, 6, 6, 6, 6, 6, 6, 6, 6, 6},  // WM2
//                {6, 6, 6, 6, 6, 6, 6, 6, 6, 6},  // SPIN2
//                {6, 6, 6, 6, 6, 6, 6, 6, 6, 6},  // PPR
//                {6, 6, 6, 6, 6, 6, 6, 6, 6, 6},  // WM3
//                {6, 6, 6, 6, 6, 6, 6, 6, 6, 6},  // WM4
//                {6, 6, 6, 6, 6, 6, 6, 6, 6, 6},  // WM5
//                {12, 12, 12, 12, 12, 12, 12, 12, 12, 12},  // BAIN
//                {3, 3, 3, 3, 3, 3, 3, 3, 3, 3},  // KBIN
//                {15, 15, 15, 15, 15, 15, 15, 15, 15, 15}}; // PXP
//
//        // A new model instance
//        Model model = new Model("CourseScheduling");
//
//        // VARIABLES
//        // a term is either open for another course or it is full
//        BoolVar[] openTerms = model.boolVarArray("o", numberOfTerms);
//        // which term provides a course
//        IntVar[] provider = model.intVarArray("provider", numberOfCourses, 1, numberOfTerms, false);
//        // achieved credit points per term
//        IntVar[] creditPoints = model.intVarArray("creditPoints", numberOfCourses, 1, 96, true);
    }

//    private static void prettyPrint(Model model, IntVar[] open, int W, IntVar[] supplier, int S, IntVar tot_cost) {
//        StringBuilder st = new StringBuilder();
//        st.append("Solution #").append(model.getSolver().getSolutionCount()).append("\n");
//        for (int i = 0; i < W; i++) {
//            if (open[i].getValue() > 0) {
//                st.append(String.format("\tWarehouse %d supplies customers : ", (i + 1)));
//                for (int j = 0; j < S; j++) {
//                    if (supplier[j].getValue() == (i + 1)) {
//                        st.append(String.format("%d ", (j + 1)));
//                    }
//                }
//                st.append("\n");
//            }
//        }
//        st.append("\tTotal C: ").append(tot_cost.getValue());
//        System.out.println(st.toString());
//    }
}

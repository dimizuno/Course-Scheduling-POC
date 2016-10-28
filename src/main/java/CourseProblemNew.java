import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;

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
        int numberOfCourses = 28; // warehouses
        int numberOfTerms = 10;   // stores
//        int maintenanceCost = 30;
        int[] capacityOfEachCourse = new int[numberOfCourses]; Arrays.fill(capacityOfEachCourse, 1);
//        int[] achievableCreditPoints = new int[]{
//                5, 5, 7, 7, 6,  // TGI, TENI, GMI, EPR, LDS
//                6, 5, 7, 6, 6,  // MIN, REN, OPR, ADS, THI
//                6, 6, 6, 6, 6,  // BSY, INS, SWT, DBA, MCI
//                6, 6, 6, 6, 6,  // SPIN1, IDB, INP, WM1, WM2
//                6, 6, 6, 6, 6,  // SPIN2, PPR, WM3, WM4, WM5,
//                12, 3, 15};     // BAIN, KBIN, PXP
        int[][] matrixOfCreditPoints = new int[][]{ // x=courses | y=terms
                {5, 5, 7, 7, 6, 6, 5, 7, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 12, 3, 15}, // term 1
                {5, 5, 7, 7, 6, 6, 5, 7, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 12, 3, 15}, // term 2
                {5, 5, 7, 7, 6, 6, 5, 7, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 12, 3, 15}, // term 3
                {5, 5, 7, 7, 6, 6, 5, 7, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 12, 3, 15}, // term 4
                {5, 5, 7, 7, 6, 6, 5, 7, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 12, 3, 15}, // term 5
                {5, 5, 7, 7, 6, 6, 5, 7, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 12, 3, 15}, // term 6
                {5, 5, 7, 7, 6, 6, 5, 7, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 12, 3, 15}, // term 7
                {5, 5, 7, 7, 6, 6, 5, 7, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 12, 3, 15}, // term 8
                {5, 5, 7, 7, 6, 6, 5, 7, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 12, 3, 15}, // term 9
                {5, 5, 7, 7, 6, 6, 5, 7, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 12, 3, 15}  // term 10
        };

        // A new model instance
        Model model = new Model("CourseScheduling");

        // VARIABLES
        // a course is already scheduled or not
        BoolVar[] openCourses = model.boolVarArray("o", numberOfCourses);
        // which course supplies a term
        IntVar[] supplier = model.intVarArray("supplier", numberOfTerms, 1, numberOfCourses, false);
        // supplying cost per store
        IntVar[] cost = model.intVarArray("cost", numberOfTerms, 1, 150, true);
        // Total of all costs
        IntVar tot_cost = model.intVar("tot_cost", 0, 99999, true);






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
}

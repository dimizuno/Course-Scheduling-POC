/*
 * Copyright 2016 Dimitri Kotlovsky, Andreas Sekulski
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import util.StringPadding;

import java.util.Arrays;
import java.util.List;

/**
 * Course Scheduling problem. This is not a complete solution but an example for successful credit points calculations
 * (see documentation). It is loosely based on the Warehouse example from the choco tutorials.
 *
 * @authors Dimitri Kotlovsky, Andreas Sekulski
 */
public class CourseScheduling_CreditPoints {

    public static void main(String [] args) {
        solveProblem();
    }

    private static void solveProblem() {



        ////////////
        // MODEL: //
        ////////////

        List<String> courseNames = Arrays.asList( "____", // 0
                "TGI", "TENI", "GMI", "EPR", "LDS", // 1, 2, 3, 4, 5
                "MIN", "REN", "OPR", "ADS", "THI", // 6, 7, 8, 9, 10
                "BSY", "INS", "SWT", "DBA", "MCI", // 11, 12, 13, 14, 15
                "SPIN1", "IDB", "INP", "WM1", "WM2", // 16, 17, 18, 19, 20
                "SPIN2", "PPR", "WM3", "WM4", "WM5", // 21, 22, 23, 24, 25
                "BAIN", "KBIN", "PXP"); //26, 27, 28

        // load parameters
        int maxCoursesPerTerm = 3;
        int numberOfCourses = 29;   // warehouses (28 + 1 blank)
        int maxTerms = 10;          // stores
        int[] achievableCreditPoints = new int[]{ 0, // BLANK
                5, 5, 7, 7, 6,  // TGI, TENI, GMI, EPR, LDS
                6, 5, 7, 6, 6,  // MIN, REN, OPR, ADS, THI
                6, 6, 6, 6, 6,  // BSY, INS, SWT, DBA, MCI
                6, 6, 6, 6, 6,  // SPIN1, IDB, INP, WM1, WM2
                6, 6, 6, 6, 6,  // SPIN2, PPR, WM3, WM4, WM5,
                12, 3, 15};     // BAIN, KBIN, PXP

        // A new model instance
        Model model = new Model("CourseScheduling_CreditPoints");

        // VARIABLES
        // a course is either scheduled or not
        BoolVar[] courses = model.boolVarArray("courses", numberOfCourses);
        // which course is bound to a specific term
//        IntVar[][] terms = new IntVar[maxTerms][];
//        for (int i = 0; i < maxTerms; i++) {
//            terms[i] = model.intVarArray("terms_"+i, maxCoursesPerTerm, 0, numberOfCourses - 1, true);
//        }

        IntVar[] terms = model.intVarArray("terms", maxTerms * maxCoursesPerTerm, 0, numberOfCourses - 1, false);

        // credit points per term
        IntVar[][] points = new IntVar[maxTerms][];
        for (int i = 0; i < maxTerms; i++) {
            points[i] = model.intVarArray("points_"+i, maxCoursesPerTerm, 0, 15, true);
        }

        // accumulated points
        IntVar[] accPoints = new IntVar[maxTerms];
        for (int i = 0; i < maxTerms; i++) {
//            accPoints[i] = model.intVar("accPoints"+i, 15, 21, true);
            accPoints[i] = model.intVar("accPoints"+i, 0, 100, false);
        }



        //////////////////
        // CONSTRAINTS: //
        //////////////////

        model.allDifferentExcept0(terms).post();
        for (int j = 0; j < maxTerms; j++) {
            // a course is scheduled, if it is 'bound' to a store
            for (int i = 0; i < maxCoursesPerTerm; i++) {
//                model.element(model.intVar(1), courses, terms[(j * maxCoursesPerTerm) + i], 0).post();
                model.element(points[j][i], achievableCreditPoints, terms[(j * maxCoursesPerTerm) + i], 0).post(); // Compute credit points for each term
            }
            model.sum(points[j], "=", accPoints[j]).post();
            model.sum(points[j], ">=", 15).post();
            model.sum(points[j], "<=", 21).post();
        }




        //////////////
        // SOLVING: //
        //////////////

        Solver solver = model.getSolver();
        solver.showShortStatistics();
        Solution solution = solver.findSolution();

        String[] output = new String[maxTerms];
        if (solution != null) {
            for (int i = 0; i < maxTerms; i++) {
                int start = i * maxCoursesPerTerm;
                for (int j = start; j < (start + maxCoursesPerTerm); j++) {
                    String courseName = StringPadding.rightPad(courseNames.get(terms[j].getValue()), 5);
                    if (output[i] != null) {
                        output[i] += courseName + "   ";
                    } else {
                        output[i] = courseName + "   ";
                    }
                }
                System.out.println(StringPadding.leftPad("" + (i + 1), 2) + ". Sem:   " + output[i]);
            }
        } else {
            System.out.println("NO SOLUTION FOUND!");
        }
    }
}

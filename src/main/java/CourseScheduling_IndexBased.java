/*
 * Copyright 2016 Dimitri Kotlovsky
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
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.SetVar;
import util.StringPadding;

import java.util.Arrays;
import java.util.List;

/**
 * Course Scheduling problem. Solved with an index based solution (see documentation). This is an already updated
 * version that has constraints for credit points requirements included.
 *
 * @authors Dimitri Kotlovsky, Andreas Sekulski
 */
public class CourseScheduling_IndexBased {

    public static void main(String[] args) {
        solveProblem();
    }

    private static void solveProblem() {

        long startTime = System.nanoTime();

        // Maximum courses per term.
        int maxCoursesPerTerm = 3;

        // Maximum terms over all
        int maxTerms = 10;



        ////////////
        // MODEL: //
        ////////////

        // x-axis: number of curses per term
        // y-axis: number of necessary terms over all
        //
        //         x0   x1   x2   x3   ...
        //    y0  0.GMI  1.EPR  2.LDS   -
        //    y1  3.MIN  4.TENI 5.TGI   -
        //    y2  6.OPR  7.ADS  8.THI   -
        //    y3  9...   10...  11...   -
        //    y4
        //    ...

        // Courses
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
        Model model = new Model("CourseScheduling_IndexBased");

        // Variables: courses
        IntVar[] courseVars = new IntVar[numberOfCourses];
        for (int i = 0; i < numberOfCourses; i++) {
            courseVars[i] = model.intVar(courseNames.get(i), 0, (maxTerms * maxCoursesPerTerm) - 1);
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

        // Constraints: Alldiff
        model.allDifferent(courseVars).post();



        // Constraints: Module Dependencies
        // MIN: GMI
        setupCourseDependency(model, courseVars[2], courseVars[5], maxCoursesPerTerm);
        // OPR: EPR
        setupCourseDependency(model, courseVars[3], courseVars[7], maxCoursesPerTerm);
        // ADS: EPR, LDS
        setupCourseDependency(model, courseVars[3], courseVars[8], maxCoursesPerTerm);
        setupCourseDependency(model, courseVars[4], courseVars[8], maxCoursesPerTerm);
        // THI: EPR, LDS
        setupCourseDependency(model, courseVars[3], courseVars[9], maxCoursesPerTerm);
        setupCourseDependency(model, courseVars[4], courseVars[9], maxCoursesPerTerm);
        // BSY: TGI
        setupCourseDependency(model, courseVars[0], courseVars[10], maxCoursesPerTerm);
        // INS: EPR, OPR
        setupCourseDependency(model, courseVars[3], courseVars[11], maxCoursesPerTerm);
        setupCourseDependency(model, courseVars[7], courseVars[11], maxCoursesPerTerm);
        // SWT: GMI, EPR, LDS, OPR, ADS
        setupCourseDependency(model, courseVars[2], courseVars[12], maxCoursesPerTerm);
        setupCourseDependency(model, courseVars[3], courseVars[12], maxCoursesPerTerm);
        setupCourseDependency(model, courseVars[4], courseVars[12], maxCoursesPerTerm);
        setupCourseDependency(model, courseVars[7], courseVars[12], maxCoursesPerTerm);
        setupCourseDependency(model, courseVars[8], courseVars[12], maxCoursesPerTerm);
        // DBA: EPR, OPR
        setupCourseDependency(model, courseVars[3], courseVars[13], maxCoursesPerTerm);
        setupCourseDependency(model, courseVars[7], courseVars[13], maxCoursesPerTerm);
        // MCI: EPR, OPR
        setupCourseDependency(model, courseVars[3], courseVars[14], maxCoursesPerTerm);
        setupCourseDependency(model, courseVars[7], courseVars[14], maxCoursesPerTerm);
        // SPIN1: GMI, EPR, LDS, OPR, ADS, SWT, MCI
        setupCourseDependency(model, courseVars[2], courseVars[15], maxCoursesPerTerm);
        setupCourseDependency(model, courseVars[3], courseVars[15], maxCoursesPerTerm);
        setupCourseDependency(model, courseVars[4], courseVars[15], maxCoursesPerTerm);
        setupCourseDependency(model, courseVars[7], courseVars[15], maxCoursesPerTerm);
        setupCourseDependency(model, courseVars[8], courseVars[15], maxCoursesPerTerm);
        setupCourseDependency(model, courseVars[12], courseVars[15], maxCoursesPerTerm);
        setupCourseDependency(model, courseVars[14], courseVars[15], maxCoursesPerTerm);
        // IDB: EPR, OPR, INS, DBA
        setupCourseDependency(model, courseVars[3], courseVars[16], maxCoursesPerTerm);
        setupCourseDependency(model, courseVars[7], courseVars[16], maxCoursesPerTerm);
        setupCourseDependency(model, courseVars[11], courseVars[16], maxCoursesPerTerm);
        setupCourseDependency(model, courseVars[13], courseVars[16], maxCoursesPerTerm);
        // WM1
        model.arithm(courseVars[18], "<", courseVars[19]).post(); // WM1 < WM2
        model.arithm(courseVars[18], "<", courseVars[22]).post(); // WM1 < WM3
        model.arithm(courseVars[18], "<", courseVars[23]).post(); // WM1 < WM4
        model.arithm(courseVars[18], "<", courseVars[24]).post(); // WM1 < WM5
        // WM2
        model.arithm(courseVars[19], "<", courseVars[22]).post(); // WM2 < WM3
        model.arithm(courseVars[19], "<", courseVars[23]).post(); // WM2 < WM4
        model.arithm(courseVars[19], "<", courseVars[24]).post(); // WM2 < WM5
        // SPIN2: GMI, EPR, LDS, OPR, ADS, SWT, MCI, SPIN1
        setupCourseDependency(model, courseVars[2], courseVars[20], maxCoursesPerTerm);
        setupCourseDependency(model, courseVars[3], courseVars[20], maxCoursesPerTerm);
        setupCourseDependency(model, courseVars[4], courseVars[20], maxCoursesPerTerm);
        setupCourseDependency(model, courseVars[7], courseVars[20], maxCoursesPerTerm);
        setupCourseDependency(model, courseVars[8], courseVars[20], maxCoursesPerTerm);
        setupCourseDependency(model, courseVars[12], courseVars[20], maxCoursesPerTerm);
        setupCourseDependency(model, courseVars[14], courseVars[20], maxCoursesPerTerm);
        setupCourseDependency(model, courseVars[15], courseVars[20], maxCoursesPerTerm);
        model.arithm(courseVars[20], "=", courseVars[15].add(3).intVar()).post(); // SPIN2 has to be right after SPIN1
        // PPR: EPR, LDS, OPR, ADS
        setupCourseDependency(model, courseVars[3], courseVars[21], maxCoursesPerTerm);
        setupCourseDependency(model, courseVars[4], courseVars[21], maxCoursesPerTerm);
        setupCourseDependency(model, courseVars[7], courseVars[21], maxCoursesPerTerm);
        setupCourseDependency(model, courseVars[8], courseVars[21], maxCoursesPerTerm);
        // WM3
        model.arithm(courseVars[22], "<", courseVars[23]).post(); // WM3 < WM4
        model.arithm(courseVars[22], "<", courseVars[24]).post(); // WM3 < WM5
        // WM4
        model.arithm(courseVars[23], "<", courseVars[24]).post(); // WM4 < WM5
        // BAIN: All courses, PPX
        for (int i = 0; i <= 24; i++) {
            setupCourseDependency(model, courseVars[i], courseVars[25], maxCoursesPerTerm);
        }
        IntVar rowB = courseVars[25].div(maxCoursesPerTerm).intVar();
        IntVar rowK = courseVars[26].div(maxCoursesPerTerm).intVar();
        IntVar rowP = courseVars[27].div(maxCoursesPerTerm).intVar();
        model.arithm(rowB, ">=", rowP).post(); // BAIN.row >= PXX.row
        model.arithm(rowK, ">=", rowP).post(); // KBIN.row >= PXX.row
        model.arithm(rowB, "=", rowK).post(); // BAIN.row = KBIN.row
        model.arithm(courseVars[25], "<", courseVars[26]).post(); // BAIN < KBIN
        // PPX: all courses from the first 3 semesters
        for (int i = 0; i <= 14; i++) {
            setupCourseDependency(model, courseVars[i], courseVars[27], maxCoursesPerTerm);
        }



        // Constraints: WS cycle dependency (courses are only accessible during winter)
        setupCycleDependencyWS(model, courseVars[0], maxCoursesPerTerm); // TGI
        setupCycleDependencyWS(model, courseVars[1], maxCoursesPerTerm); // TENI
        setupCycleDependencyWS(model, courseVars[2], maxCoursesPerTerm); // GMI
        setupCycleDependencyWS(model, courseVars[3], maxCoursesPerTerm); // EPR
        setupCycleDependencyWS(model, courseVars[4], maxCoursesPerTerm); // LDS
        setupCycleDependencyWS(model, courseVars[10], maxCoursesPerTerm); // BSY
        setupCycleDependencyWS(model, courseVars[11], maxCoursesPerTerm); // INS
        setupCycleDependencyWS(model, courseVars[12], maxCoursesPerTerm); // SWT
        setupCycleDependencyWS(model, courseVars[13], maxCoursesPerTerm); // DBA
        setupCycleDependencyWS(model, courseVars[14], maxCoursesPerTerm); // MCI
        setupCycleDependencyWS(model, courseVars[20], maxCoursesPerTerm); // SPIN2
        setupCycleDependencyWS(model, courseVars[21], maxCoursesPerTerm); // PPR

        // Constraints: SS cycle dependency (courses are only accessible during summer)
        setupCycleDependencySS(model, courseVars[5], maxCoursesPerTerm); // MIN
        setupCycleDependencySS(model, courseVars[6], maxCoursesPerTerm); // REN
        setupCycleDependencySS(model, courseVars[7], maxCoursesPerTerm); // OPR
        setupCycleDependencySS(model, courseVars[8], maxCoursesPerTerm); // ADS
        setupCycleDependencySS(model, courseVars[9], maxCoursesPerTerm); // THI
        setupCycleDependencySS(model, courseVars[15], maxCoursesPerTerm); // SPIN1
        setupCycleDependencySS(model, courseVars[16], maxCoursesPerTerm); // IDB
        setupCycleDependencySS(model, courseVars[17], maxCoursesPerTerm); // INP
        setupCycleDependencySS(model, courseVars[27], maxCoursesPerTerm); // PXP



        // Constraints: credit points requirements and cycle dependency
        model.arithm(courseVars[5].div(maxCoursesPerTerm).intVar(), ">", 0).post(); // MIN (redundant for SS)
        model.arithm(courseVars[6].div(maxCoursesPerTerm).intVar(), ">", 0).post(); // REN (redundant for SS)
        model.arithm(courseVars[7].div(maxCoursesPerTerm).intVar(), ">", 0).post(); // OPR (redundant for SS)
        model.arithm(courseVars[8].div(maxCoursesPerTerm).intVar(), ">", 0).post(); // ADS (redundant for SS)
        model.arithm(courseVars[9].div(maxCoursesPerTerm).intVar(), ">", 0).post(); // THI (redundant for SS)
        model.arithm(courseVars[10].div(maxCoursesPerTerm).intVar(), ">", 1).post(); // BSY (30cp + WS)
        model.arithm(courseVars[11].div(maxCoursesPerTerm).intVar(), ">", 1).post(); // INS (30cp + WS)
        model.arithm(courseVars[12].div(maxCoursesPerTerm).intVar(), ">", 1).post(); // SWT (30cp + WS)
        model.arithm(courseVars[13].div(maxCoursesPerTerm).intVar(), ">", 1).post(); // DBA (30cp + WS)
        model.arithm(courseVars[14].div(maxCoursesPerTerm).intVar(), ">", 1).post(); // MCI (30cp + WS)
        model.arithm(courseVars[15].div(maxCoursesPerTerm).intVar(), ">", 2).post(); // SPIN1 (50cp + SS)
        model.arithm(courseVars[16].div(maxCoursesPerTerm).intVar(), ">", 2).post(); // IDB (50cp + SS)
        model.arithm(courseVars[17].div(maxCoursesPerTerm).intVar(), ">", 2).post(); // INP (50cp + SS)
        model.arithm(courseVars[18].div(maxCoursesPerTerm).intVar(), ">", 1).post(); // WM1 (50cp/70cp)
        model.arithm(courseVars[19].div(maxCoursesPerTerm).intVar(), ">", 1).post(); // WM2 (50cp/70cp)
        model.arithm(courseVars[20].div(maxCoursesPerTerm).intVar(), ">", 3).post(); // SPIN2 (50cp + SPIN1)
        model.arithm(courseVars[21].div(maxCoursesPerTerm).intVar(), ">", 1).post(); // PPR (70cp + WS)
        model.arithm(courseVars[22].div(maxCoursesPerTerm).intVar(), ">", 1).post(); // WM3 (50cp/70cp)
        model.arithm(courseVars[23].div(maxCoursesPerTerm).intVar(), ">", 1).post(); // WM4 (50cp/70cp)
        model.arithm(courseVars[24].div(maxCoursesPerTerm).intVar(), ">", 1).post(); // WM5 (50cp/70cp)
        model.arithm(courseVars[25].div(maxCoursesPerTerm).intVar(), ">", 4).post(); // BAIN (150cp)
        model.arithm(courseVars[26].div(maxCoursesPerTerm).intVar(), ">", 4).post(); // KBIN (150cp)
        model.arithm(courseVars[27].div(maxCoursesPerTerm).intVar(), ">", 2).post(); // PXP (90cp + SS)



        // Constraints: calculate credit points
        for (int term = 0; term < maxTerms; term++) {

            // Check which courses are scheduled to which term
            for (int course = 0; course < numberOfCourses; course++) {
                model.ifThenElse(
                        model.arithm(courseVars[course].div(maxCoursesPerTerm).intVar(), "=", term),
                        model.member(course, scheduledCourses[term]),
                        model.notMember(course, scheduledCourses[term])
                );
            }

            // Calculate achieved credit points for each term
            model.sumElements(scheduledCourses[term], achievableCreditPoints, points[term]).post();
        }

        // Constraints: credit points requirements
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

            BoolVar BSY = model.arithm(courseVars[10].div(maxCoursesPerTerm).intVar(), "=", term).reify();
            BoolVar INS = model.arithm(courseVars[11].div(maxCoursesPerTerm).intVar(), "=", term).reify();
            BoolVar SWT = model.arithm(courseVars[12].div(maxCoursesPerTerm).intVar(), "=", term).reify();
            BoolVar DBA = model.arithm(courseVars[13].div(maxCoursesPerTerm).intVar(), "=", term).reify();
            BoolVar MCI = model.arithm(courseVars[14].div(maxCoursesPerTerm).intVar(), "=", term).reify();
            BoolVar SPIN1 = model.arithm(courseVars[15].div(maxCoursesPerTerm).intVar(), "=", term).reify();
            BoolVar IDB = model.arithm(courseVars[16].div(maxCoursesPerTerm).intVar(), "=", term).reify();
            BoolVar INP = model.arithm(courseVars[17].div(maxCoursesPerTerm).intVar(), "=", term).reify();
            BoolVar WM1 = model.arithm(courseVars[18].div(maxCoursesPerTerm).intVar(), "=", term).reify();
            BoolVar WM2 = model.arithm(courseVars[19].div(maxCoursesPerTerm).intVar(), "=", term).reify();
            BoolVar SPIN2 = model.arithm(courseVars[20].div(maxCoursesPerTerm).intVar(), "=", term).reify();
            BoolVar PPR = model.arithm(courseVars[21].div(maxCoursesPerTerm).intVar(), "=", term).reify();
            BoolVar WM3 = model.arithm(courseVars[22].div(maxCoursesPerTerm).intVar(), "=", term).reify();
            BoolVar WM4 = model.arithm(courseVars[23].div(maxCoursesPerTerm).intVar(), "=", term).reify();
            BoolVar WM5 = model.arithm(courseVars[24].div(maxCoursesPerTerm).intVar(), "=", term).reify();
            BoolVar BAIN = model.arithm(courseVars[25].div(maxCoursesPerTerm).intVar(), "=", term).reify();
            BoolVar KBIN = model.arithm(courseVars[26].div(maxCoursesPerTerm).intVar(), "=", term).reify();
            BoolVar PPX = model.arithm(courseVars[27].div(maxCoursesPerTerm).intVar(), "=", term).reify();

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

        Solver solver = model.getSolver();
        solver.showStatistics();
        solver.showShortStatistics();

        Solution solution = solver.findSolution();
//        model.setObjective(Model.MINIMIZE, modulVars[25]);
//        Solution solution = model.getSolver().findOptimalSolution(modulVars[25], Model.MINIMIZE);

        String[] output = new String[maxTerms * maxCoursesPerTerm];
        if (solution != null) {
            for (IntVar modulVar : courseVars) {
                output[solution.getIntVal(modulVar)] = modulVar.getName();
            }
            for (int i = 0; i < maxTerms; i++) {
                String mods = "";
                int start = i * maxCoursesPerTerm;
                for (int j = start; j < (start + maxCoursesPerTerm); j++) {
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
//            System.out.println(solutions.size());
//        }

        long estimatedTime = System.nanoTime() - startTime;
        long seconds = estimatedTime / 1000000000;
        System.out.println("Time: " + seconds / 60 + " min");

    }

    private static void setupCycleDependencyWS(Model model, IntVar a, int modsPerSem) {
        IntVar rowMod = a.div(modsPerSem).mod(2).intVar();
        model.arithm(rowMod, "=", 0).post(); // even
    }

    private static void setupCycleDependencySS(Model model, IntVar a, int modsPerSem) {
        IntVar rowMod = a.div(modsPerSem).mod(2).intVar();
        model.arithm(rowMod, "=", 1).post(); // odd
    }

    private static void setupCourseDependency(Model model, IntVar a, IntVar b, int modsPerSem) {
        IntVar rowB = b.div(modsPerSem).intVar();
        model.arithm(rowB, ">", a.div(modsPerSem).intVar()).post();
    }

}

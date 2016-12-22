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
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.SetVar;
import util.StringPadding;

import java.util.Arrays;
import java.util.List;

/**
 * Course Scheduling problem. Solved with an set based solution (see documentation).
 *
 * @authors Dimitri Kotlovsky, Andreas Sekulski
 */
public class CourseScheduling_SetBased {

    public static void main(String[] args) {
        solveProblem();
    }

    public static void solveProblem() {

        int maxTerms = 10;
        int numberOfCourses = 28;



        ////////////
        // MODEL: //
        ////////////

        List<String> courseNames = Arrays.asList(
                "TGI", "TENI", "GMI", "EPR", "LDS", // 0, 1, 2, 3, 4
                "MIN", "REN", "OPR", "ADS", "THI", // 5, 6, 7, 8, 9
                "BSY", "INS", "SWT", "DBA", "MCI", // 10, 11, 12, 13, 14
                "SPIN1", "IDB", "INP", "WM1", "WM2", // 15, 16, 17, 18, 19
                "SPIN2", "PPR", "WM3", "WM4", "WM5", // 20, 21, 22, 23, 24
                "BAIN", "KBIN", "PXP"); //25, 26, 27

        int[] courseNumbers = new int[]{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27};

//        int[][] courseToTermRestrictions = new int[maxTerms][];
//        courseToTermRestrictions[0] = new int[]{0,1,2,3,4};
//        courseToTermRestrictions[1] = new int[]{5,6,7,8,9};
//        courseToTermRestrictions[2] = new int[]{0,1,2,3,4,10,11,12,13,14,18,19,21,22,23,24};
//        courseToTermRestrictions[3] = new int[]{5,6,7,8,9,15,16,17,18,19,22,23,24,27};
//        courseToTermRestrictions[4] = new int[]{0,1,2,3,4,10,11,12,13,14,18,19,20,21,22,23,24};
//        courseToTermRestrictions[5] = new int[]{5,6,7,8,9,15,16,17,18,19,22,23,24,25,26,27};
//        courseToTermRestrictions[6] = new int[]{0,1,2,3,4,10,11,12,13,14,18,19,20,21,22,23,24,25,26};
//        courseToTermRestrictions[7] = new int[]{5,6,7,8,9,15,16,17,18,19,22,23,24,25,26,27};
//        courseToTermRestrictions[8] = new int[]{0,1,2,3,4,10,11,12,13,14,18,19,20,21,22,23,24,25,26};
//        courseToTermRestrictions[9] = new int[]{5,6,7,8,9,15,16,17,18,19,22,23,24,25,26,27};


        Model model = new Model("CourseScheduling_SetBased");

        SetVar union = model.setVar("union", courseNumbers);

        SetVar[] terms = model.setVarArray("terms", maxTerms, new int[]{}, courseNumbers);
//        SetVar[] terms = new SetVar[maxTerms];
//        for (int i = 0; i < maxTerms; i++) {
//            terms[i] = model.setVar("term_"+i, new int[]{}, courseToTermRestrictions[i]);
//        }

        IntVar[] creditPoints = new IntVar[maxTerms];
        for (int i = 0; i < maxTerms; i++) {
            creditPoints[i] = model.intVar("creditPoints_"+i, 15, 21, true); // consider false !!!
        }

//        int[] minTerm = new int[]{
//                0,0,0,0,0, // TGI, TENI, GMI, EPR, LDS
//                1,1,1,1,1, // MIN, REN, OPR, ADS, THI
//                2,2,2,2,2, // BSY, INS, SWT, DBA, MCI
//                3,3,3,2,2, // SPIN1, IDB, INP, WM1, WM2
//                4,2,2,2,2, // SPIN2, PPR, WM3, WM4, WM5,
//                5,5,3  // BAIN, KBIN, PXP
//        };

        int[] achievableCreditPoints = new int[]{
                5, 5, 7, 7, 6,  // TGI, TENI, GMI, EPR, LDS
                6, 5, 7, 6, 6,  // MIN, REN, OPR, ADS, THI
                6, 6, 6, 6, 6,  // BSY, INS, SWT, DBA, MCI
                6, 6, 6, 6, 6,  // SPIN1, IDB, INP, WM1, WM2
                6, 6, 6, 6, 6,  // SPIN2, PPR, WM3, WM4, WM5,
                12, 3, 15};     // BAIN, KBIN, PXP



        //////////////////
        // CONSTRAINTS: //
        //////////////////

        // Ensures that every course is scheduled
        model.union(terms, union).post();

        // Ensures that each course is scheduled only once
        model.allDisjoint(terms).post();

        /*
        // Ensure that the courses MIN, REN, OPR, ADS and THI are not scheduled before the second term
        for (int i = 1 - 1; i >= 0; i--) {
            model.notMember(5, terms[i]).post();
            model.notMember(6, terms[i]).post();
            model.notMember(7, terms[i]).post();
            model.notMember(8, terms[i]).post();
            model.notMember(9, terms[i]).post();
        }

        // Ensure that the courses BSY, INS, SWT, DBA, MCI, WM1, WM2, PPR, WM3, WM4 and WM5 are not scheduled before the third term
        for (int i = 2 - 1; i >= 0; i--) {
            model.notMember(10, terms[i]).post();
            model.notMember(11, terms[i]).post();
            model.notMember(12, terms[i]).post();
            model.notMember(13, terms[i]).post();
            model.notMember(14, terms[i]).post();
            model.notMember(18, terms[i]).post();
            model.notMember(19, terms[i]).post();
            model.notMember(21, terms[i]).post();
            model.notMember(22, terms[i]).post();
            model.notMember(23, terms[i]).post();
            model.notMember(24, terms[i]).post();
        }

        // Ensure that the courses SPIN1, IDB, INP and PXP are not scheduled before the fourth term
        for (int i = 3 - 1; i >= 0; i--) {
            model.notMember(15, terms[i]).post();
            model.notMember(16, terms[i]).post();
            model.notMember(17, terms[i]).post();
            model.notMember(27, terms[i]).post();
        }

        // Ensure that the course SPIN2 is not scheduled before the fifth term
        for (int i = 4 - 1; i >= 0; i--) {
            model.notMember(20, terms[i]).post();
        }

        // Ensure that the courses BAIN and KBIN are not scheduled before the sixth term
        for (int i = 5 - 1; i >= 0; i--) {
            model.notMember(25, terms[i]).post();
            model.notMember(26, terms[i]).post();
        }
        */

        // Ensure that all courses, that are only provided in the summer terms, are not scheduled in winter (WS)
        for (int i = 0; i < maxTerms; i+=2) {
            model.notMember(5, terms[i]).post();
            model.notMember(6, terms[i]).post();
            model.notMember(7, terms[i]).post();
            model.notMember(8, terms[i]).post();
            model.notMember(9, terms[i]).post();
            model.notMember(15, terms[i]).post();
            model.notMember(16, terms[i]).post();
            model.notMember(17, terms[i]).post();
            model.notMember(27, terms[i]).post();
        }

        // Ensure that all courses, that are only provided in the winter terms, are not scheduled in summer (ss)
        for (int i = 1; i < maxTerms; i+=2) {
            model.notMember(0, terms[i]).post();
            model.notMember(1, terms[i]).post();
            model.notMember(2, terms[i]).post();
            model.notMember(3, terms[i]).post();
            model.notMember(4, terms[i]).post();
            model.notMember(10, terms[i]).post();
            model.notMember(11, terms[i]).post();
            model.notMember(12, terms[i]).post();
            model.notMember(13, terms[i]).post();
            model.notMember(14, terms[i]).post();
            model.notMember(20, terms[i]).post();
            model.notMember(21, terms[i]).post();
        }

        // Ensure that all courses, that demand a minimum number of credit points are not scheduled in the first term
        model.notMember(10, terms[0]).post();
        model.notMember(11, terms[0]).post();
        model.notMember(12, terms[0]).post();
        model.notMember(13, terms[0]).post();
        model.notMember(14, terms[0]).post();
        model.notMember(15, terms[0]).post();
        model.notMember(16, terms[0]).post();
        model.notMember(17, terms[0]).post();
        model.notMember(18, terms[0]).post();
        model.notMember(19, terms[0]).post();
        model.notMember(20, terms[0]).post();
        model.notMember(21, terms[0]).post();
        model.notMember(22, terms[0]).post();
        model.notMember(23, terms[0]).post();
        model.notMember(24, terms[0]).post();
        model.notMember(25, terms[0]).post();
        model.notMember(26, terms[0]).post();
        model.notMember(27, terms[0]).post();


        oldCPVersion(model, maxTerms, creditPoints, terms); // for some reason the old code is faster (to be examined)

//        // Ensure that for courses, that demand a minimum number of credit points, those are achieved
//        for (int i = 1; i < maxTerms; i++) {
//            IntVar[] points = new IntVar[i];
//            for (int k = i - 1; k >= 0; k--) {
//                points[k] = creditPoints[k];
//            }
//            Constraint min30Points = model.sum(points, ">=", 30);
//            Constraint min50Points = model.sum(points, ">=", 50);
//            Constraint min70Points = model.sum(points, ">=", 70);
//            Constraint min90Points = model.sum(points, ">=", 90);
//            Constraint min150Points = model.sum(points, ">=", 150);
//
//            BoolVar BSY = model.member(10, terms[i]).reify();
//            BoolVar INS = model.member(11, terms[i]).reify();
//            BoolVar SWT = model.member(12, terms[i]).reify();
//            BoolVar DBA = model.member(13, terms[i]).reify();
//            BoolVar MCI = model.member(14, terms[i]).reify();
//            BoolVar SPIN1 = model.member(15, terms[i]).reify();
//            BoolVar IDB = model.member(16, terms[i]).reify();
//            BoolVar INP = model.member(17, terms[i]).reify();
//            BoolVar WM1 = model.member(18, terms[i]).reify();
//            BoolVar WM2 = model.member(19, terms[i]).reify();
//            BoolVar SPIN2 = model.member(20, terms[i]).reify();
//            BoolVar PPR = model.member(21, terms[i]).reify();
//            BoolVar WM3 = model.member(22, terms[i]).reify();
//            BoolVar WM4 = model.member(23, terms[i]).reify();
//            BoolVar WM5 = model.member(24, terms[i]).reify();
//            BoolVar BAIN = model.member(25, terms[i]).reify();
//            BoolVar KBIN = model.member(26, terms[i]).reify();
//            BoolVar PPX = model.member(27, terms[i]).reify();
//
//            model.ifThen(BSY, min30Points);
//            model.ifThen(INS, min30Points);
//            model.ifThen(SWT, min30Points);
//            model.ifThen(DBA, min30Points);
//            model.ifThen(MCI, min30Points);
//            model.ifThen(SPIN1, min50Points);
//            model.ifThen(IDB, min50Points);
//            model.ifThen(INP, min50Points);
//            model.ifThen(WM1, min50Points);
//            model.ifThen(WM2, min50Points);
//            model.ifThen(SPIN2, min50Points);
//            model.ifThen(PPR, min70Points);
//            model.ifThen(WM3, min70Points);
//            model.ifThen(WM4, min70Points);
//            model.ifThen(WM5, min70Points);
//            model.ifThen(BAIN, min150Points);
//            model.ifThen(KBIN, min150Points);
//            model.ifThen(PPX, min90Points);
//        }

        // Ensure that the dependencies for MIN, OPR, ADS and THI are valid
        for (int i = 0; i < maxTerms; i++) {
            BoolVar MINDependency = model.member(5, terms[i]).reify();
            BoolVar OPRDependency = model.member(7, terms[i]).reify();
            BoolVar ADSDependency = model.member(8, terms[i]).reify();
            BoolVar THIDependency = model.member(9, terms[i]).reify();
            BoolVar OPR_ADS_THI = model.or(OPRDependency, ADSDependency, THIDependency).reify();
            BoolVar ADS_THI = model.or(ADSDependency, THIDependency).reify();
            for (int j = i; j < maxTerms; j++) {
                model.ifThen(MINDependency, model.notMember(2, terms[j])); // GMI has to be scheduled before
                model.ifThen(OPR_ADS_THI,   model.notMember(3, terms[j])); // EPR has to be scheduled before
                model.ifThen(ADS_THI,       model.notMember(4, terms[j])); // LDS has to be scheduled before
            }
        }

        // Ensure that the dependencies for BSY, INS, SWT, DBA, MCI and PPR are valid
        for (int i = 0; i < maxTerms; i++) {
            BoolVar BSYDependency = model.member(10, terms[i]).reify();
            BoolVar INSDependency = model.member(11, terms[i]).reify();
            BoolVar SWTDependency = model.member(12, terms[i]).reify();
            BoolVar DBADependency = model.member(13, terms[i]).reify();
            BoolVar MCIDependency = model.member(14, terms[i]).reify();
            BoolVar WM2Dependency = model.member(19, terms[i]).reify();
            BoolVar PPRDependency = model.member(21, terms[i]).reify();
            BoolVar WM3Dependency = model.member(22, terms[i]).reify();
            BoolVar WM4Dependency = model.member(23, terms[i]).reify();
            BoolVar WM5Dependency = model.member(24, terms[i]).reify();
            BoolVar INS_SWT_DBA_MCI_PPR = model.or(INSDependency, SWTDependency, DBADependency, MCIDependency, PPRDependency).reify();
            BoolVar SWT_PPR = model.or(SWTDependency, PPRDependency).reify();
            BoolVar WM2_WM3_WM4_WM5 = model.or(WM2Dependency, WM3Dependency, WM4Dependency, WM5Dependency).reify();
            BoolVar WM3_WM4_WM5 = model.or(WM3Dependency, WM4Dependency, WM5Dependency).reify();
            BoolVar WM4_WM5 = model.or(WM4Dependency, WM5Dependency).reify();
            for (int j = i; j < maxTerms; j++) {
                model.ifThen(BSYDependency,       model.notMember(0, terms[j]));  // TGI has to be scheduled before
                model.ifThen(SWTDependency,       model.notMember(2, terms[j]));  // GMI has to be scheduled before
                model.ifThen(INS_SWT_DBA_MCI_PPR, model.notMember(3, terms[j]));  // EPR has to be scheduled before
                model.ifThen(SWT_PPR,             model.notMember(4, terms[j]));  // LDS has to be scheduled before
                model.ifThen(INS_SWT_DBA_MCI_PPR, model.notMember(7, terms[j]));  // OPR has to be scheduled before
                model.ifThen(SWT_PPR,             model.notMember(8, terms[j]));  // ADS has to be scheduled before
//                model.ifThen(WM2_WM3_WM4_WM5,     model.notMember(18, terms[j])); // WM1 has to be scheduled before
//                model.ifThen(WM3_WM4_WM5,         model.notMember(19, terms[j])); // WM2 has to be scheduled before
//                model.ifThen(WM4_WM5,             model.notMember(22, terms[j])); // WM3 has to be scheduled before
//                model.ifThen(WM5Dependency,       model.notMember(23, terms[j])); // WM4 has to be scheduled before
            }
        }

        // Ensure that the dependencies for SPIN1, IDB and PPX are valid
        for (int i = 0; i < maxTerms; i++) {
            BoolVar SPIN1Dependency = model.member(15, terms[i]).reify();
            BoolVar IDBDependency = model.member(16, terms[i]).reify();
            BoolVar PPXDependency = model.member(27, terms[i]).reify();
            BoolVar SPIN1_IDB = model.or(SPIN1Dependency, IDBDependency).reify();
            for (int j = i; j < maxTerms; j++) {
                model.ifThen(SPIN1Dependency, model.notMember(2, terms[j]));  // GMI has to be scheduled before
                model.ifThen(SPIN1_IDB,       model.notMember(3, terms[j]));  // EPR has to be scheduled before
                model.ifThen(SPIN1Dependency, model.notMember(4, terms[j]));  // LDS has to be scheduled before
                model.ifThen(SPIN1_IDB,       model.notMember(7, terms[j]));  // OPR has to be scheduled before
                model.ifThen(SPIN1Dependency, model.notMember(8, terms[j]));  // ADS has to be scheduled before
                model.ifThen(IDBDependency,   model.notMember(11, terms[j])); // INS has to be scheduled before
                model.ifThen(SPIN1Dependency, model.notMember(12, terms[j])); // SWT has to be scheduled before
                model.ifThen(IDBDependency,   model.notMember(13, terms[j])); // DBA has to be scheduled before
                model.ifThen(SPIN1Dependency, model.notMember(14, terms[j])); // MCI has to be scheduled before
                for (int k = 0; k <= 14; k++) {
                    // All courses of first three regular terms have to be scheduled before
                    model.ifThen(PPXDependency, model.notMember(k, terms[j]));
                }
            }
        }

        // Ensure that the dependencies for SPIN2 and PPR are valid
        for (int i = 1; i < maxTerms; i++) {
            BoolVar SPIN2Dependency = model.member(20, terms[i]).reify();
            model.ifThen(SPIN2Dependency, model.member(15, terms[i-1])); // SPIN1 has to be scheduled before
            for (int j = i; j < maxTerms; j++) {
                model.ifThen(SPIN2Dependency, model.notMember(2, terms[j]));  // GMI has to be scheduled before
                model.ifThen(SPIN2Dependency, model.notMember(3, terms[j]));  // EPR has to be scheduled before
                model.ifThen(SPIN2Dependency, model.notMember(4, terms[j]));  // LDS has to be scheduled before
                model.ifThen(SPIN2Dependency, model.notMember(7, terms[j]));  // OPR has to be scheduled before
                model.ifThen(SPIN2Dependency, model.notMember(8, terms[j]));  // ADS has to be scheduled before
                model.ifThen(SPIN2Dependency, model.notMember(12, terms[j])); // SWT has to be scheduled before
                model.ifThen(SPIN2Dependency, model.notMember(14, terms[j])); // MCI has to be scheduled before
                model.ifThen(SPIN2Dependency, model.notMember(15, terms[j])); // SPIN1 has to be scheduled before
            }
        }

        // Ensure that the dependencies for BAIN and KBIN are valid
        for (int i = 0; i < maxTerms; i++) {
            BoolVar BAINDependency = model.member(25, terms[i]).reify();
            BoolVar KBINDependency = model.member(26, terms[i]).reify();
            BoolVar BAIN_KBIN = model.or(BAINDependency, KBINDependency).reify();
            model.ifThen(BAINDependency, model.member(26, terms[i])); // KBIN has to be scheduled in the same term as BAIN
            for (int j = i; j < maxTerms; j++) {
                if (j > i) {
                    model.ifThen(KBINDependency, model.notMember(25, terms[j])); // BAIN is not allowed to be scheduled after KBIN
                    model.ifThen(BAINDependency, model.notMember(26, terms[j])); // KBIN is not allowed to be scheduled after BAIN
                    model.ifThen(BAIN_KBIN,      model.notMember(27, terms[j])); // PPX is not allowed to be scheduled after BAIN or KBIN
                }
                for (int k = 0; k <= 24; k++) {
                    // All other courses  have to be scheduled before BAIN or KBIN
                    model.ifThen(BAIN_KBIN, model.notMember(k, terms[j]));
                }
            }
        }

        // Set minimum credit points per term
        for (int i = 0; i < maxTerms; i++) {
            model.sumElements(terms[i], achievableCreditPoints, creditPoints[i]).post();
        }

        // Set maximum cardinality for each set, i.e. maximum courses per term
//        for (int i = 0; i < maxTerms; i++) {
//            IntVar occ = model.intVar("occ_"+i, 0, 3, true);
//            terms[i].setCard(occ);
//        }



        //////////////
        // SOLVING: //
        //////////////

        Solver solver = model.getSolver();
        solver.showStatistics();
        solver.showShortStatistics();
        Solution solution = solver.findSolution();

        String[] output = new String[maxTerms];
        if (solution != null) {
            for (int term = 0; term < maxTerms; term++) {
                int[] courses = solution.getSetVal(terms[term]);
                for (int i = 0; i < courses.length; i++) {
                    String courseName = StringPadding.rightPad(courseNames.get(courses[i]), 5);
                    if (output[term] != null) {
                        output[term] += courseName + "   ";
                    } else {
                        output[term] = courseName + "   ";
                    }
                }
                if (output[term] != null) {
                    System.out.println(StringPadding.leftPad("" + (term + 1), 2) + ". Sem:   " + output[term]);
                } else {
                    System.out.println(StringPadding.leftPad("" + (term + 1), 2) + ". Sem:   ");
                }
            }
        } else {
            System.out.println("NO SOLUTION FOUND!");
        }

//        List<Solution> solutions = solver.findAllSolutions();
//        if(solutions != null) {
//            System.out.println(solutions.size());
//        }
    }

    private static void oldCPVersion(Model model, int maxTerms, IntVar[] creditPoints, SetVar[] terms) {
        // Ensure that the minimum number of credit points for the courses BSY, INS, SWT, DBA, MCI, WM1, WM2, PPR, WM3,
        // WM4, and WM5 is achieved
        for (int i = 1; i < maxTerms; i++) {
            IntVar[] points = new IntVar[i];
            for (int k = i - 1; k >= 0; k--) {
                points[k] = creditPoints[k];
            }
            Constraint min30Points = model.sum(points, ">=", 30);
            Constraint min50Points = model.sum(points, ">=", 50);
            Constraint min70Points = model.sum(points, ">=", 70);

            BoolVar BSY = model.member(10, terms[i]).reify();
            BoolVar INS = model.member(11, terms[i]).reify();
            BoolVar SWT = model.member(12, terms[i]).reify();
            BoolVar DBA = model.member(13, terms[i]).reify();
            BoolVar MCI = model.member(14, terms[i]).reify();
            BoolVar WM1 = model.member(18, terms[i]).reify();
            BoolVar WM2 = model.member(19, terms[i]).reify();
            BoolVar PPR = model.member(21, terms[i]).reify();
            BoolVar WM3 = model.member(22, terms[i]).reify();
            BoolVar WM4 = model.member(23, terms[i]).reify();
            BoolVar WM5 = model.member(24, terms[i]).reify();

            model.ifThen(BSY, min30Points);
            model.ifThen(INS, min30Points);
            model.ifThen(SWT, min30Points);
            model.ifThen(DBA, min30Points);
            model.ifThen(MCI, min30Points);
            model.ifThen(WM1, min50Points);
            model.ifThen(WM2, min50Points);
            model.ifThen(PPR, min70Points);
            model.ifThen(WM3, min70Points);
            model.ifThen(WM4, min70Points);
            model.ifThen(WM5, min70Points);
        }

        // Ensure that the minimum number of credit points for the courses SPIN1, IDB, INP and PPX is achieved
        for (int i = 1; i < maxTerms; i++) {
            IntVar[] points = new IntVar[i];
            for (int k = i - 1; k >= 0; k--) {
                points[k] = creditPoints[k];
            }
            Constraint min50Points = model.sum(points, ">=", 50);
            Constraint min90Points = model.sum(points, ">=", 90);

            BoolVar SPIN1 = model.member(15, terms[i]).reify();
            BoolVar IDB = model.member(16, terms[i]).reify();
            BoolVar INP = model.member(17, terms[i]).reify();
            BoolVar PPX = model.member(27, terms[i]).reify();

            model.ifThen(SPIN1, min50Points);
            model.ifThen(IDB, min50Points);
            model.ifThen(INP, min50Points);
            model.ifThen(PPX, min90Points);
        }

        // Ensure that the minimum number of credit points for the course SPIN2 is achieved
        for (int i = 1; i < maxTerms; i++) {
            IntVar[] points = new IntVar[i];
            for (int k = i - 1; k >= 0; k--) {
                points[k] = creditPoints[k];
            }
            Constraint min50Points = model.sum(points, ">=", 50);

            BoolVar SPIN2 = model.member(20, terms[i]).reify();

            model.ifThen(SPIN2, min50Points);
        }

        // Ensure that the minimum number of credit points for the courses BAIN and KBIN is achieved
        for (int i = 1; i < maxTerms; i++) {
            IntVar[] points = new IntVar[i];
            for (int k = i - 1; k >= 0; k--) {
                points[k] = creditPoints[k];
            }
            Constraint min150Points = model.sum(points, ">=", 150);

            BoolVar BAIN = model.member(25, terms[i]).reify();
            BoolVar KBIN = model.member(26, terms[i]).reify();

            model.ifThen(BAIN, min150Points);
            model.ifThen(KBIN, min150Points);
        }
    }

}

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;

import java.util.Arrays;
import java.util.List;

/**
 * Created by the two world leading experts in CP: asekulsk & dkotlovs.
 */
public class CourseProblemOld {

    public static void main(String[] args) {
        solveModule();
    }

    public static void solveModule() {

        List<String> modules = Arrays.asList(
                "GMI", "EPR", "LDS", "TENI", "TGI", // 0,1,2,3,4
                "MIN", "OPR", "ADS", "THI", "REN", // 5,6,7,8,9
                "DBA", "INS", "SWT", "MCI", "BSY", // 10, 11, 12, 13, 14
                "IDB", "SPIN", "INP", "WM1", "WM2", // 15, 16, 17, 18, 19
                "PPR", "SPIN", "WM3", "WM4", "WM5", // 20, 21, 22, 23, 24
                "BAIN", "KBIN", "PXP"); //25, 26, 27

        List<String> modules1 = Arrays.asList(
                "GMI", "EPR", "LDS", "TENI", "TGI"); // 0,1,2,3,4
        List<String> modules2 = Arrays.asList(
                "MIN", "OPR", "ADS", "THI", "REN"); // 5,6,7,8,9
        List<String> modules3 = Arrays.asList(
                "DBA", "INS", "SWT", "MCI", "BSY"); // 10, 11, 12, 13, 14
        List<String> modules4 = Arrays.asList(
                "IDB", "SPIN", "INP", "WM1", "WM2"); // 15, 16, 17, 18, 19
        List<String> modules5 = Arrays.asList(
                "PPR", "SPIN", "WM3", "WM4", "WM5"); // 20, 21, 22, 23, 24
        List<String> modules6 = Arrays.asList(
                "BAIN", "KBIN", "PXP"); //25, 26, 27

        int n = modules.size() - 3;

        Model model = new Model(n + "-module problem");

        BoolVar[] vars1 = new BoolVar[modules1.size()];
        BoolVar[] vars2 = new BoolVar[modules2.size()];
        BoolVar[] vars3 = new BoolVar[modules3.size()];
        BoolVar[] vars4 = new BoolVar[modules4.size()];
        BoolVar[] vars5 = new BoolVar[modules5.size()];
        BoolVar[] vars6 = new BoolVar[modules6.size()];

        init_this_shit(model, vars1, modules1);
        init_this_shit(model, vars2, modules2);
        init_this_shit(model, vars3, modules3);
        init_this_shit(model, vars4, modules4);
        init_this_shit(model, vars5, modules5);
        //init_this_shit(model, vars6, modules6);

        // GMI -> MIN
        setup_dependency(model, vars1[0 % 5], vars2[5 % 5]);
        // EPR -> OPR
        setup_dependency(model, vars1[1 % 5], vars2[6 % 5]);
        // EPR & LDS -> ADS
        setup_dependency(model, vars1[1 % 5], vars2[7 % 5]);
        setup_dependency(model, vars1[2 % 5], vars2[7 % 5]);
        // EPR & LDS -> THI
        setup_dependency(model, vars1[1 % 5], vars2[8 % 5]);
        setup_dependency(model, vars1[2 % 5], vars2[8 % 5]);
        // TGI -> BSY
        setup_dependency(model, vars1[4 % 5], vars3[14 % 5]);
        // OPR -> INS
        setup_dependency(model, vars2[6 % 5], vars3[11 % 5]);
        // GMI & OPR & ADS -> SWT
        setup_dependency(model, vars1[1 % 5], vars3[12 % 5]);
        setup_dependency(model, vars2[6 % 5], vars3[12 % 5]);
        setup_dependency(model, vars2[7 % 5], vars3[12 % 5]);
        // OPR -> DBA
        setup_dependency(model, vars2[6 % 5], vars3[10 % 5]);
        // OPR -> MCI
        setup_dependency(model, vars2[6 % 5], vars3[13 % 5]);
        // SWT & MCI -> SPIN
        setup_dependency(model, vars3[12 % 5], vars4[16 % 5]);
        setup_dependency(model, vars3[13 % 5], vars4[16 % 5]);
        setup_dependency(model, vars3[12 % 5], vars5[21 % 5]);
        setup_dependency(model, vars3[13 % 5], vars5[21 % 5]);
        // DBA & INS -> IDB
        setup_dependency(model, vars3[10 % 5], vars4[15 % 5]);
        setup_dependency(model, vars3[11 % 5], vars4[15 % 5]);
        // OPR & ADS -> PPR
        setup_dependency(model, vars2[6 % 5], vars5[20 % 5]);
        setup_dependency(model, vars2[7 % 5], vars5[20 % 5]);

        String result = "";
        Solution solution = model.getSolver().findSolution();
        if(solution != null) {
            // Remove all TMP's from solution
            String[] resultArray = solution.toString().split(",");
            for (int i = 0; i < resultArray.length; i++) {
                if (!resultArray[i].contains("TMP")) {
                    result += resultArray[i];
                }
            }
            System.out.println(result.trim());
        }

        List<Solution> solutions = model.getSolver().findAllSolutions();
        if(solutions != null) {
            int no = 0;
            for(Solution sol : solutions) {
                no++;
                // Remove all TMP's from solution
                result = "";
                String[] resultArray = sol.toString().split(",");
                for (int i = 0; i < resultArray.length; i++) {
                    if (!resultArray[i].contains("TMP")) {
                        result += resultArray[i];
                    }
                }
                System.out.println(no + ". " + result.trim());
            }
            System.out.println("Found " + solutions.size() + " solutions.");
        }

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

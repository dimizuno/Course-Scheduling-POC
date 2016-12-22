package exp;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.tools.ArrayUtils;

import java.util.Arrays;

/**
 * This is an example from the choco tutorials: http://choco-tuto.readthedocs.io/en/latest/src/401.description.html
 */
public class Warehouse {

    public static void main(String [] args) {
        solveWarehouse();
    }

    private static void solveWarehouse() {

        // load parameters
        int numberOfWarehouses = 5;
        int numberOfStores = 10;
        int maintenanceCost = 30;
        int[] capacityOfEachWarehouse = new int[]{1, 4, 2, 1, 3};
        int[][] matrixOfSupplyCosts = new int[][]{ // x=stores | y=warehouses
                {20, 24, 11, 25, 30},   // store 1
                {28, 27, 82, 83, 74},   // store 2
                {74, 97, 71, 96, 70},   // store 3
                {2, 55, 73, 69, 61},    // store 4
                {46, 96, 59, 83, 4},    // store 5
                {42, 22, 29, 67, 59},   // store 6
                {1, 5, 73, 59, 56},     // store 7
                {10, 73, 13, 43, 96},   // store 8
                {93, 35, 63, 85, 46},   // store 9
                {47, 65, 55, 71, 95}};  // store 10

        // A new model instance
        Model model = new Model("WarehouseLocation");

        // VARIABLES
        // a warehouse is either open or closed
        BoolVar[] openWarehouses = model.boolVarArray("o", numberOfWarehouses);
        // which warehouse supplies a store
        IntVar[] supplier = model.intVarArray("supplier", numberOfStores, 1, numberOfWarehouses, false);
        // supplying cost per store
        IntVar[] cost = model.intVarArray("cost", numberOfStores, 1, 96, true);
        // Total of all costs
        IntVar tot_cost = model.intVar("tot_cost", 0, 99999, true);

        // CONSTRAINTS
        for (int j = 0; j < numberOfStores; j++) {
            // a warehouse is 'open', if it supplies to a store
            model.element(model.intVar(1), openWarehouses, supplier[j], 1).post();
            // Compute 'cost' for each store
            model.element(cost[j], matrixOfSupplyCosts[j], supplier[j], 1).post();
        }
        for (int i = 0; i < numberOfWarehouses; i++) {
            // additional variable 'occ' is created on the fly
            // its domain includes the constraint on capacity
            IntVar occ = model.intVar("occur_" + i, 0, capacityOfEachWarehouse[i], true);
            // for-loop starts at 0, warehouse index starts at 1
            // => we count occurrences of (i+1) in 'supplier'
            model.count(i+1, supplier, occ).post();
            // redundant link between 'occ' and 'open' for better propagation
            occ.ge(openWarehouses[i]).post();
        }
        // Prepare the constraint that maintains 'tot_cost'
        int[] coeffs = new int[numberOfWarehouses + numberOfStores];
        Arrays.fill(coeffs, 0, numberOfWarehouses, maintenanceCost);
        Arrays.fill(coeffs, numberOfWarehouses, numberOfWarehouses + numberOfStores, 1);
        // then post it
        model.scalar(ArrayUtils.append(openWarehouses, cost), coeffs, "=", tot_cost).post();

        model.setObjective(false, tot_cost);
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
        while (solver.solve()) {
            prettyPrint(model, openWarehouses, numberOfWarehouses, supplier, numberOfStores, tot_cost);
        }

        // Find a solution that minimizes 'tot_cost'
//        Solution best = solver.findOptimalSolution(tot_cost, false);
    }

    private static void prettyPrint(Model model, IntVar[] open, int W, IntVar[] supplier, int S, IntVar tot_cost) {
        StringBuilder st = new StringBuilder();
        st.append("Solution #").append(model.getSolver().getSolutionCount()).append("\n");
        for (int i = 0; i < W; i++) {
            if (open[i].getValue() > 0) {
                st.append(String.format("\texp.Warehouse %d supplies customers : ", (i + 1)));
                for (int j = 0; j < S; j++) {
                    if (supplier[j].getValue() == (i + 1)) {
                        st.append(String.format("%d ", (j + 1)));
                    }
                }
                st.append("\n");
            }
        }
        st.append("\tTotal C: ").append(tot_cost.getValue());
        System.out.println(st.toString());
    }
}

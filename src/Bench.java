import algo.Sorting;
import java.util.*;

public class Bench {
    public static void main(String[] args) {
        int[] sizes = {500, 1000, 2000};
        System.out.println("n,algo,ms");
        for (int n : sizes) {
            List<Integer> data = new Random().ints(n, 0, 10_000_000).boxed().toList();

            time("Insertion", () -> {
                List<Integer> a = new ArrayList<>(data);
                Sorting.insertionSort(a, Comparator.naturalOrder());
            }, n);

            time("Selection", () -> {
                List<Integer> a = new ArrayList<>(data);
                Sorting.selectionSort(a, Comparator.naturalOrder());
            }, n);

            time("Merge", () -> {
                List<Integer> a = new ArrayList<>(data);
                Sorting.mergeSort(a, Comparator.naturalOrder());
            }, n);

            time("Quick", () -> {
                List<Integer> a = new ArrayList<>(data);
                Sorting.quickSort(a, Comparator.naturalOrder());
            }, n);
        }
    }

    private static void time(String name, Runnable r, int n) {
        long t0 = System.nanoTime();
        r.run();
        long t1 = System.nanoTime();
        System.out.println(n + "," + name + "," + ((t1 - t0) / 1_000_000.0));
    }
}

package algo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Sorting {
    public static <T> void insertionSort(List<T> a, Comparator<? super T> cmp) {
        for (int i = 1; i < a.size(); i++) {
            T cur = a.get(i);
            int j = i - 1;
            while (j >= 0 && cmp.compare(a.get(j), cur) > 0) {
                a.set(j + 1, a.get(j));
                j--;
            }
            a.set(j + 1, cur);
        }
    }

    public static <T> void selectionSort(List<T> a, Comparator<? super T> cmp) {
        for (int i = 0; i < a.size(); i++) {
            int minIdx = i;
            for (int j = i + 1; j < a.size(); j++) {
                if (cmp.compare(a.get(j), a.get(minIdx)) < 0) minIdx = j;
            }
            T tmp = a.get(i);
            a.set(i, a.get(minIdx));
            a.set(minIdx, tmp);
        }
    }

    public static <T> List<T> mergeSort(List<T> a, Comparator<? super T> cmp) {
        if (a.size() <= 1) return new ArrayList<>(a);
        int mid = a.size() / 2;
        List<T> left = mergeSort(a.subList(0, mid), cmp);
        List<T> right = mergeSort(a.subList(mid, a.size()), cmp);
        return merge(left, right, cmp);
    }

    private static <T> List<T> merge(List<T> left, List<T> right, Comparator<? super T> cmp) {
        List<T> res = new ArrayList<>(left.size() + right.size());
        int i = 0, j = 0;
        while (i < left.size() && j < right.size()) {
            if (cmp.compare(left.get(i), right.get(j)) <= 0) res.add(left.get(i++));
            else res.add(right.get(j++));
        }
        while (i < left.size()) res.add(left.get(i++));
        while (j < right.size()) res.add(right.get(j++));
        return res;
    }

    public static <T> void quickSort(List<T> a, Comparator<? super T> cmp) {
        quick(a, 0, a.size() - 1, cmp);
    }

    private static <T> void quick(List<T> a, int lo, int hi, Comparator<? super T> cmp) {
        if (lo < hi) {
            int p = partition(a, lo, hi, cmp);
            quick(a, lo, p - 1, cmp);
            quick(a, p + 1, hi, cmp);
        }
    }

    private static <T> int partition(List<T> a, int lo, int hi, Comparator<? super T> cmp) {
        T pivot = a.get(hi);
        int i = lo;
        for (int j = lo; j < hi; j++) {
            if (cmp.compare(a.get(j), pivot) <= 0) {
                T tmp = a.get(i); a.set(i, a.get(j)); a.set(j, tmp);
                i++;
            }
        }
        T tmp = a.get(i); a.set(i, a.get(hi)); a.set(hi, tmp);
        return i;
    }
}

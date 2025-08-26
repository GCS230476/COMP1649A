package algo;

import java.util.Comparator;
import java.util.List;

public class Searching {
    public static <T> int linearSearch(List<T> a, T target, Comparator<? super T> cmp) {
        for (int i = 0; i < a.size(); i++) {
            if (cmp.compare(a.get(i), target) == 0) return i;
        }
        return -1;
    }

    public static <T> int binarySearch(List<T> a, T target, Comparator<? super T> cmp) {
        int lo = 0, hi = a.size() - 1;
        while (lo <= hi) {
            int mid = (lo + hi) >>> 1;
            int c = cmp.compare(a.get(mid), target);
            if (c == 0) return mid;
            else if (c < 0) lo = mid + 1;
            else hi = mid - 1;
        }
        return -1;
    }
}

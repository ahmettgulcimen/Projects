import org.knowm.xchart.*;
import org.knowm.xchart.style.Styler;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

class Main {

    public static void main(String args[]) throws IOException {
        int[] inputSizes = {500, 1000, 2000, 4000, 8000, 16000, 32000, 64000, 128000, 250000};
        String[] algorithms = {"Insertion", "Quick", "Merge", "Shell", "Radix"};
        
        double[][] randomResults = new double[5][10];
        double[][] sortedResults = new double[5][10];
        double[][] reverseResults = new double[5][10];
        
        System.out.println("Testler basliyor, bu islem birkac dakika surebilir...");

        for (int i = 0; i < inputSizes.length; i++) {
            int size = inputSizes[i];
            
            // 1. RASTGELE VERI TESTLERI
            int[] randomData = readVolumeData("all_stocks_5yr.csv", size);
            System.out.println("\n--- Girdi Boyutu: " + size + " (Random) ---");
            for (int j = 0; j < algorithms.length; j++) {
                randomResults[j][i] = testAlgorithm(algorithms[j], randomData);
                System.out.println(algorithms[j] + ": " + randomResults[j][i] + " ms");
            }

            // 2. SIRALI VERI TESTLERI
            int[] sortedData = Arrays.copyOf(randomData, size);
            Arrays.sort(sortedData);
            System.out.println("\n--- Girdi Boyutu: " + size + " (Sorted) ---");
            for (int j = 0; j < algorithms.length; j++) {
                sortedResults[j][i] = testAlgorithm(algorithms[j], sortedData);
                System.out.println(algorithms[j] + ": " + sortedResults[j][i] + " ms");
            }

            // 3. TERS SIRALI VERI TESTLERI
            int[] reverseData = new int[size];
            for (int k = 0; k < size; k++) {
                reverseData[k] = sortedData[size - 1 - k];
            }
            System.out.println("\n--- Girdi Boyutu: " + size + " (Reverse) ---");
            for (int j = 0; j < algorithms.length; j++) {
                reverseResults[j][i] = testAlgorithm(algorithms[j], reverseData);
                System.out.println(algorithms[j] + ": " + reverseResults[j][i] + " ms");
            }
        }

        // --- GRAFIKLERI KAYDET (4 TANE) ---
        saveChartFinal("Tests on Random Data", inputSizes, randomResults, true, "Random_Plot");
        saveChartFinal("Tests on Sorted Data", inputSizes, sortedResults, true, "Sorted_Plot");
        saveChartFinal("Tests on Reversely Sorted Data", inputSizes, reverseResults, true, "Reverse_Plot");

        // 4. KRITIK GRAFIK: Hizli Algoritmalar Kiyaslamasi (Insertion Atlanir)
        double[][] fastAlgosData = {randomResults[1], randomResults[2], randomResults[3], randomResults[4]};
        saveChartFinal("Comparison of Fast Algorithms (Random Data)", inputSizes, fastAlgosData, false, "Comparison_Plot");

        System.out.println("\n--- ISLEM BASARIYLA TAMAMLANDI ---");
        System.out.println("Klasorunde su 4 grafik dosyasi olusturuldu:");
        System.out.println("1. Random_Plot.png");
        System.out.println("2. Sorted_Plot.png");
        System.out.println("3. Reverse_Plot.png");
        System.out.println("4. Comparison_Plot.png (Hizli Algoritmalar)");
    }

    public static void saveChartFinal(String title, int[] x, double[][] y, boolean includeInsertion, String fileName) throws IOException {
        XYChart chart = new XYChartBuilder().width(800).height(600).title(title)
                .xAxisTitle("Input Size").yAxisTitle("Time (ms)").build();
        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNW);
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line);
        
        double[] dx = Arrays.stream(x).asDoubleStream().toArray();
        String[] allAlgos = {"Insertion", "Quick", "Merge", "Shell", "Radix"};
        String[] fastAlgos = {"Quick", "Merge", "Shell", "Radix"};
        String[] currentNames = includeInsertion ? allAlgos : fastAlgos;
        
        for (int i = 0; i < y.length; i++) {
            chart.addSeries(currentNames[i], dx, y[i]);
        }
        
        BitmapEncoder.saveBitmap(chart, "./" + fileName, BitmapEncoder.BitmapFormat.PNG);
    }

    public static int[] readVolumeData(String filePath, int size) {
        int[] data = new int[size];
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            br.readLine(); 
            for (int i = 0; i < size; i++) {
                String line = br.readLine();
                if (line == null) break;
                String[] cols = line.split(",");
                data[i] = Integer.parseInt(cols[5]); 
            }
        } catch (Exception e) { e.printStackTrace(); }
        return data;
    }

    public static double testAlgorithm(String algo, int[] original) {
        long total = 0;
        for (int i = 0; i < 10; i++) {
            int[] copy = Arrays.copyOf(original, original.length);
            long start = System.currentTimeMillis();
            if (algo.equals("Insertion")) insertionSort(copy);
            else if (algo.equals("Quick")) quickSort(copy, 0, copy.length - 1);
            else if (algo.equals("Merge")) mergeSort(copy);
            else if (algo.equals("Shell")) shellSort(copy);
            else if (algo.equals("Radix")) radixSort(copy);
            total += (System.currentTimeMillis() - start);
        }
        return total / 10.0;
    }

    public static void insertionSort(int[] A) {
        for (int j = 1; j < A.length; j++) {
            int key = A[j], i = j - 1;
            while (i >= 0 && A[i] > key) {
                A[i + 1] = A[i]; i--;
            }
            A[i + 1] = key;
        }
    }

    public static void quickSort(int[] A, int low, int high) {
        int[] stack = new int[high - low + 1];
        int top = -1;
        stack[++top] = low; stack[++top] = high;
        while (top >= 0) {
            high = stack[top--]; low = stack[top--];
            int p = partition(A, low, high);
            if (p - 1 > low) { stack[++top] = low; stack[++top] = p - 1; }
            if (p + 1 < high) { stack[++top] = p + 1; stack[++top] = high; }
        }
    }

    private static int partition(int[] A, int low, int high) {
        int pivot = A[high], i = low - 1;
        for (int j = low; j < high; j++) {
            if (A[j] <= pivot) {
                i++;
                int t = A[i]; A[i] = A[j]; A[j] = t;
            }
        }
        int t = i + 1;
        int tempVal = A[t]; A[t] = A[high]; A[high] = tempVal;
        return t;
    }

    public static void mergeSort(int[] A) {
        int n = A.length;
        int[] temp = new int[n];
        for (int curr = 1; curr < n; curr *= 2) {
            for (int left = 0; left < n - 1; left += 2 * curr) {
                int mid = Math.min(left + curr - 1, n - 1);
                int right = Math.min(left + 2 * curr - 1, n - 1);
                merge(A, temp, left, mid, right);
            }
        }
    }

    private static void merge(int[] A, int[] temp, int l, int m, int r) {
        int i = l, j = m + 1, k = l;
        while (i <= m && j <= r) temp[k++] = (A[i] <= A[j]) ? A[i++] : A[j++];
        while (i <= m) temp[k++] = A[i++];
        while (j <= r) temp[k++] = A[j++];
        for (i = l; i <= r; i++) A[i] = temp[i];
    }

    public static void shellSort(int[] A) {
        int n = A.length, h = 1;
        while (h < n / 3) h = 3 * h + 1;
        while (h >= 1) {
            for (int i = h; i < n; i++) {
                for (int j = i; j >= h && A[j] < A[j - h]; j -= h) {
                    int t = A[j]; A[j] = A[j - h]; A[j - h] = t;
                }
            }
            h /= 3;
        }
    }

    public static void radixSort(int[] A) {
        if (A.length == 0) return;
        int max = A[0];
        for (int x : A) if (x > max) max = x;
        for (int exp = 1; max / exp > 0; exp *= 10) countingSort(A, exp);
    }

    private static void countingSort(int[] A, int exp) {
        int n = A.length;
        int[] output = new int[n], count = new int[10];
        for (int i = 0; i < n; i++) count[(A[i] / exp) % 10]++;
        for (int i = 1; i < 10; i++) count[i] += count[i - 1];
        for (int i = n - 1; i >= 0; i--) {
            output[count[(A[i] / exp) % 10] - 1] = A[i];
            count[(A[i] / exp) % 10]--;
        }
        System.arraycopy(output, 0, A, 0, n);
    }
}
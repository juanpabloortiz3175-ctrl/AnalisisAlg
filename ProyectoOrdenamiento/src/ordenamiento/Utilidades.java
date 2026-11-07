package ordenamiento;

import java.util.Random;

/** Métodos de apoyo: generar datos y comprobar resultados. */
public final class Utilidades {

    private Utilidades() { }

    /** Arreglo aleatorio con valores entre 0 y 999.999 (semilla fija = reproducible). */
    public static int[] aleatorio(int n, long semilla) {
        Random r = new Random(semilla);
        int[] a = new int[n];
        for (int i = 0; i < n; i++) a[i] = r.nextInt(1_000_000);
        return a;
    }

    /** Arreglo ya ordenado: 0, 1, 2, ... (mejor caso típico). */
    public static int[] ordenado(int n) {
        int[] a = new int[n];
        for (int i = 0; i < n; i++) a[i] = i;
        return a;
    }

    /** Arreglo en orden inverso: n, n-1, ..., 1 (peor caso típico). */
    public static int[] inverso(int n) {
        int[] a = new int[n];
        for (int i = 0; i < n; i++) a[i] = n - i;
        return a;
    }

    public static boolean estaOrdenado(int[] a) {
        for (int i = 1; i < a.length; i++) {
            if (a[i - 1] > a[i]) return false;
        }
        return true;
    }
}

package ordenamiento;

import java.awt.Color;

/** Catálogo de algoritmos del proyecto (crea instancias nuevas para evitar mezclar contadores). */
public final class Algoritmos {

    private Algoritmos() { }

    public static final String[] NOMBRES = {"Shell Sort", "Insertion Sort", "Selection Sort"};
    public static final Color[] COLORES = {
        new Color(30, 90, 220), new Color(30, 150, 30), new Color(220, 30, 30)
    };

    public static int cantidad() { return NOMBRES.length; }

    public static AlgoritmoOrdenamiento crear(int i) {
        switch (i) {
            case 0:  return new ShellSort();
            case 1:  return new InsertionSort();
            default: return new SelectionSort();
        }
    }

    public static AlgoritmoOrdenamiento[] todos() {
        AlgoritmoOrdenamiento[] a = new AlgoritmoOrdenamiento[cantidad()];
        for (int i = 0; i < a.length; i++) a[i] = crear(i);
        return a;
    }
}

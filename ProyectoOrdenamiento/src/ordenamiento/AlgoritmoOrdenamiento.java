package ordenamiento;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Clase base de todos los métodos de ordenamiento del proyecto.
 *
 * Cada algoritmo hereda de aquí y ofrece:
 *   - su HISTORIA, sus CARACTERÍSTICAS y su SEUDOCÓDIGO,
 *   - su RENDIMIENTO teórico O(),
 *   - el método ordenar() con contadores de comparaciones y escrituras
 *     (para medir el rendimiento REAL y compararlo con el teórico),
 *   - un modo "grabación" que guarda cada paso para la animación.
 */
public abstract class AlgoritmoOrdenamiento {

    protected static final int[] NADA = new int[0];

    protected long comparaciones;
    protected long escrituras;

    private boolean grabando = false;
    private final List<PasoVisual> pasos = new ArrayList<PasoVisual>();

    // ---------------------------------------------------------------
    //  Información didáctica (la implementa cada algoritmo)
    // ---------------------------------------------------------------
    public abstract String getNombre();
    public abstract String getHistoria();
    public abstract String[] getCaracteristicas();
    public abstract String getSeudocodigo();
    public abstract String getMejorCaso();
    public abstract String getCasoPromedio();
    public abstract String getPeorCaso();
    public abstract String getEspacio();
    public abstract boolean esEstable();

    /** Lógica del algoritmo. Debe incrementar comparaciones y escrituras. */
    protected abstract void ordenar(int[] a);

    // ---------------------------------------------------------------
    //  Ejecución
    // ---------------------------------------------------------------
    public final void sort(int[] a) {
        comparaciones = 0;
        escrituras = 0;
        pasos.clear();
        ordenar(a);
    }

    /** Ordena una COPIA del arreglo y devuelve tiempo y contadores. */
    public final Resultado medir(int[] original) {
        int[] copia = Arrays.copyOf(original, original.length);
        long inicio = System.nanoTime();
        sort(copia);
        long fin = System.nanoTime();
        return new Resultado(copia.length, fin - inicio, comparaciones, escrituras);
    }

    public long getComparaciones() { return comparaciones; }
    public long getEscrituras()    { return escrituras; }
    /** Operaciones elementales = comparaciones + escrituras. */
    public long getOperaciones()   { return comparaciones + escrituras; }

    // ---------------------------------------------------------------
    //  Grabación de pasos (para la animación)
    // ---------------------------------------------------------------
    public void setGrabando(boolean g) { this.grabando = g; }
    public List<PasoVisual> getPasos() { return pasos; }
    protected final boolean grabando() { return grabando; }

    /** Guarda un paso. Se debe llamar solo dentro de "if (grabando())". */
    protected final void paso(int linea, String texto, int[] a, int[] claves, int[] comparados,
                              int ordenadoHasta, int gap, Integer mano) {
        pasos.add(new PasoVisual(linea, texto, a.clone(), claves, comparados,
                ordenadoHasta, gap, mano, comparaciones, escrituras));
    }

    protected static int[] idx(int... v) { return v; }

    @Override
    public String toString() { return getNombre(); }
}

package ordenamiento;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

/**
 * Compara los algoritmos entre sí: mide y construye las gráficas.
 * Todos los algoritmos ordenan EXACTAMENTE los mismos datos (misma semilla)
 * para que la comparación sea justa.
 */
public final class Comparador {

    private Comparador() { }

    /** Datos medidos de un algoritmo para varios tamaños n. */
    public static class Medicion {
        public final AlgoritmoOrdenamiento algoritmo;
        public final double[] n, comparaciones, escrituras, operaciones, ms;
        public final Color color;

        Medicion(AlgoritmoOrdenamiento alg, int k, Color color) {
            this.algoritmo = alg;
            this.color = color;
            n = new double[k];
            comparaciones = new double[k];
            escrituras = new double[k];
            operaciones = new double[k];
            ms = new double[k];
        }
    }

    /** Curvas teóricas de referencia que se comparan contra los datos reales. */
    private static final String[] NOMBRES_REF = {"O(n)", "O(n log n)", "O(n^1.5)", "O(n²)"};
    private static final Color[] COLORES_REF = {
        new Color(140, 140, 140), new Color(0, 150, 150), new Color(255, 140, 0), new Color(170, 0, 170)
    };
    private static final DoubleUnaryOperator[] FUNCIONES_REF = {
        n -> n,
        n -> (n <= 1) ? 0 : n * (Math.log(n) / Math.log(2)),
        n -> Math.pow(n, 1.5),
        n -> n * n
    };

    /** Tamaños de prueba: fracciones del máximo. */
    public static int[] tamanos(int max) {
        double[] fr = {0.02, 0.05, 0.10, 0.20, 0.40, 0.60, 0.80, 1.00};
        int[] t = new int[fr.length];
        for (int i = 0; i < fr.length; i++) t[i] = Math.max(10, (int) Math.round(max * fr[i]));
        return t;
    }

    /** Mide un algoritmo. 'alTerminarTamano' se llama después de cada tamaño (para la barra de progreso). */
    public static Medicion medir(AlgoritmoOrdenamiento alg, int[] tamanos, int repeticiones,
                                 Color color, Runnable alTerminarTamano) {
        Medicion m = new Medicion(alg, tamanos.length, color);

        // Calentamiento de la JVM para que el primer tiempo no salga inflado
        alg.sort(Utilidades.aleatorio(2000, 1));

        for (int i = 0; i < tamanos.length; i++) {
            int[] datos = Utilidades.aleatorio(tamanos[i], 12345);
            double sumaMs = 0;
            Resultado r = null;
            for (int rep = 0; rep < repeticiones; rep++) {
                r = alg.medir(datos);
                sumaMs += r.getTiempoMs();
            }
            m.n[i] = tamanos[i];
            m.comparaciones[i] = r.comparaciones;
            m.escrituras[i] = r.escrituras;
            m.operaciones[i] = r.getOperaciones();
            m.ms[i] = sumaMs / repeticiones;
            if (alTerminarTamano != null) alTerminarTamano.run();
        }
        return m;
    }

    /** Nombre de la complejidad Big O que mejor ajusta a los datos reales. */
    public static String complejidadDetectada(Medicion m) {
        return NOMBRES_REF[mejorAjuste(m)];
    }

    // ------------------------------------------------------------------
    //  Gráficas
    // ------------------------------------------------------------------
    public static List<GraficaLineas> construirGraficas(List<Medicion> ms) {
        List<GraficaLineas> lista = new ArrayList<GraficaLineas>();

        GraficaLineas g1 = new GraficaLineas("Comparación: operaciones (escala lineal)",
                "Tamaño de entrada (n)", "Operaciones f(n)", false);
        GraficaLineas g2 = new GraficaLineas("Comparación: operaciones (escala logarítmica)",
                "Tamaño de entrada (n)", "Operaciones f(n) - escala log", true);
        GraficaLineas g3 = new GraficaLineas("Comparación: tiempo de ejecución real",
                "Tamaño de entrada (n)", "Tiempo (ms)", false);
        for (int i = 0; i < ms.size(); i++) {
            Medicion m = ms.get(i);
            // Insertion y Selection hacen casi las mismas operaciones totales (~n²/2):
            // la línea de Insertion se dibuja más gruesa para que no quede tapada.
            float grosor = (i == 1) ? 7f : 2.4f;
            g1.agregar(new GraficaLineas.Serie(m.algoritmo.getNombre(), m.color, m.n, m.operaciones).estilo(false, true, grosor));
            g2.agregar(new GraficaLineas.Serie(m.algoritmo.getNombre(), m.color, m.n, m.operaciones).estilo(false, true, grosor));
            g3.agregar(new GraficaLineas.Serie(m.algoritmo.getNombre(), m.color, m.n, m.ms));
        }
        g1.setAviso("Insertion y Selection se superponen (≈ n²/2 operaciones)");
        g2.setAviso("Insertion y Selection se superponen (≈ n²/2 operaciones)");
        lista.add(g1);
        lista.add(g2);
        lista.add(g3);

        for (Medicion m : ms) lista.add(graficaAjuste(m));
        return lista;
    }

    private static GraficaLineas graficaAjuste(Medicion m) {
        int best = mejorAjuste(m);
        GraficaLineas g = new GraficaLineas("Calidad de " + m.algoritmo.getNombre() + ": datos reales vs Big O",
                "Tamaño de entrada (n)", "Operaciones f(n)", false);

        double nMax = m.n[m.n.length - 1];
        double opsMax = m.operaciones[m.operaciones.length - 1];
        int pasos = 60;
        for (int k = 0; k < FUNCIONES_REF.length; k++) {
            double escala = opsMax / FUNCIONES_REF[k].applyAsDouble(nMax);
            double[] x = new double[pasos + 1], y = new double[pasos + 1];
            for (int i = 0; i <= pasos; i++) {
                x[i] = nMax * i / pasos;
                y[i] = escala * FUNCIONES_REF[k].applyAsDouble(x[i]);
            }
            String nombre = NOMBRES_REF[k] + (k == best ? "  <- mejor ajuste" : "");
            g.agregar(new GraficaLineas.Serie(nombre, COLORES_REF[k], x, y)
                    .estilo(k != best, false, k == best ? 2.6f : 1.4f));
        }
        g.agregar(new GraficaLineas.Serie("Operaciones reales (medidas)", m.color, m.n, m.operaciones));
        g.setAviso("Complejidad detectada: " + NOMBRES_REF[best]);
        return g;
    }

    /** Índice de la curva teórica con menor error relativo promedio frente a los datos reales. */
    static int mejorAjuste(Medicion m) {
        double nMax = m.n[m.n.length - 1];
        double opsMax = m.operaciones[m.operaciones.length - 1];
        int mejor = 0;
        double mejorError = Double.MAX_VALUE;
        for (int k = 0; k < FUNCIONES_REF.length; k++) {
            double escala = opsMax / FUNCIONES_REF[k].applyAsDouble(nMax);
            double error = 0;
            for (int i = 0; i < m.n.length; i++) {
                double estimado = escala * FUNCIONES_REF[k].applyAsDouble(m.n[i]);
                error += Math.abs(m.operaciones[i] - estimado) / m.operaciones[i];
            }
            error /= m.n.length;
            if (error < mejorError) {
                mejorError = error;
                mejor = k;
            }
        }
        return mejor;
    }

    /** Guarda todas las gráficas como PNG dentro de la carpeta indicada. Devuelve cuántas guardó. */
    public static int guardarPNG(List<GraficaLineas> gs, File carpeta) throws IOException {
        if (!carpeta.exists() && !carpeta.mkdirs()) throw new IOException("No se pudo crear la carpeta " + carpeta);
        int cuenta = 0;
        for (GraficaLineas g : gs) {
            String nombre = g.getTitulo().toLowerCase()
                    .replace("á", "a").replace("é", "e").replace("í", "i").replace("ó", "o").replace("ú", "u")
                    .replaceAll("[^a-z0-9]+", "_").replaceAll("^_|_$", "") + ".png";
            g.guardarPNG(new File(carpeta, nombre), 900, 560);
            cuenta++;
        }
        return cuenta;
    }
}

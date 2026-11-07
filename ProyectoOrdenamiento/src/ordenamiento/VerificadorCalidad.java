package ordenamiento;

import java.util.Arrays;
import java.util.Random;

/**
 * Verifica la CALIDAD de UN algoritmo por separado:
 *   1. Corrección: el resultado coincide con Arrays.sort en casos especiales.
 *   2. Robustez: cientos de arreglos aleatorios (con repetidos y negativos).
 *   3. Rendimiento: al duplicar n, ¿cuánto crece el trabajo? (comparado con la teoría).
 */
public final class VerificadorCalidad {

    private VerificadorCalidad() { }

    public static InformeCalidad verificar(AlgoritmoOrdenamiento alg) {
        InformeCalidad inf = new InformeCalidad();
        boolean todoOk = true;

        // 1. Casos especiales
        Random r = new Random(7);
        int[] conNegativos = new int[200];
        for (int i = 0; i < conNegativos.length; i++) conNegativos[i] = r.nextInt(2001) - 1000;
        int[] conRepetidos = new int[200];
        for (int i = 0; i < conRepetidos.length; i++) conRepetidos[i] = r.nextInt(5);
        int[] iguales = new int[100];
        Arrays.fill(iguales, 42);

        String[] nombres = {
            "Arreglo vacío", "Un solo elemento", "Dos elementos desordenados",
            "Ya ordenado (n=1000)", "Orden inverso (n=1000)", "Todos iguales (n=100)",
            "Muchos repetidos (n=200)", "Con números negativos (n=200)", "Aleatorio (n=5000)"
        };
        int[][] casos = {
            new int[0], new int[] {5}, new int[] {9, 3},
            Utilidades.ordenado(1000), Utilidades.inverso(1000), iguales,
            conRepetidos, conNegativos, Utilidades.aleatorio(5000, 11)
        };
        for (int k = 0; k < casos.length; k++) {
            int[] copia = Arrays.copyOf(casos[k], casos[k].length);
            int[] esperado = Arrays.copyOf(casos[k], casos[k].length);
            Arrays.sort(esperado);
            alg.sort(copia);
            boolean ok = Arrays.equals(copia, esperado);
            todoOk &= ok;
            inf.casos.add(new InformeCalidad.Caso(nombres[k], ok, alg.getComparaciones(), alg.getEscrituras()));
        }

        // 2. Pruebas aleatorias masivas
        inf.pruebas = 500;
        Random rnd = new Random(2026);
        for (int t = 0; t < inf.pruebas; t++) {
            int n = rnd.nextInt(201);
            int[] a = new int[n];
            for (int i = 0; i < n; i++) a[i] = rnd.nextInt(401) - 200;
            int[] esperado = Arrays.copyOf(a, n);
            Arrays.sort(esperado);
            alg.sort(a);
            if (Arrays.equals(a, esperado)) inf.aprobadas++;
        }
        todoOk &= (inf.aprobadas == inf.pruebas);

        // 3. Crecimiento del trabajo al duplicar n
        inf.razonAleatorio = razon(alg, false);
        inf.razonOrdenado = razon(alg, true);
        inf.interpAleatorio = interpretar(inf.razonAleatorio);
        inf.interpOrdenado = interpretar(inf.razonOrdenado);
        inf.teoriaMejor = alg.getMejorCaso();
        inf.teoriaPromedio = alg.getCasoPromedio();

        inf.aprobado = todoOk;
        return inf;
    }

    private static double razon(AlgoritmoOrdenamiento alg, boolean ordenado) {
        int[] p = ordenado ? Utilidades.ordenado(4000) : Utilidades.aleatorio(4000, 5);
        int[] g = ordenado ? Utilidades.ordenado(8000) : Utilidades.aleatorio(8000, 5);
        alg.sort(p);
        long opsP = alg.getOperaciones();
        alg.sort(g);
        long opsG = alg.getOperaciones();
        return (double) opsG / opsP;
    }

    private static String interpretar(double razon) {
        if (razon < 2.5) return "crecimiento casi lineal / n log n";
        if (razon < 3.3) return "crecimiento intermedio (≈ n^1.5)";
        return "crecimiento cuadrático (≈ n²)";
    }
}

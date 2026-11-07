package ordenamiento;

/** Insertion Sort (ordenamiento por inserción). */
public class InsertionSort extends AlgoritmoOrdenamiento {

    @Override public String getNombre() { return "Insertion Sort"; }

    @Override
    public String getHistoria() {
        return "  Es uno de los métodos más antiguos y naturales: es lo que hace una\n"
             + "  persona al ordenar cartas en la mano. No tiene un único inventor; según\n"
             + "  Donald Knuth (The Art of Computer Programming, vol. 3), ya se mencionaba\n"
             + "  en los primeros análisis de ordenamiento por computador (años 40).\n"
             + "  Hoy se sigue usando en arreglos pequeños o casi ordenados, y es la base\n"
             + "  sobre la que Shell construyó su algoritmo en 1959.";
    }

    @Override
    public String[] getCaracteristicas() {
        return new String[] {
            "Divide el arreglo en una parte ORDENADA (izquierda) y otra SIN ORDENAR (derecha).",
            "Toma el siguiente elemento (la 'clave') y lo inserta en su lugar dentro de la parte ordenada.",
            "Es 'en el sitio' (in-place): no necesita memoria extra.",
            "ES estable: elementos iguales mantienen su orden relativo.",
            "Muy rápido si el arreglo ya está casi ordenado (casi O(n)).",
            "Muy lento en arreglos grandes y desordenados (O(n²))."
        };
    }

    @Override
    public String getSeudocodigo() {
        return String.join("\n",
            "  ALGORITMO InsertionSort(A, n)",
            "      PARA i <- 1 HASTA n - 1 HACER",
            "          clave <- A[i]",
            "          j <- i - 1",
            "          MIENTRAS j >= 0 Y A[j] > clave HACER",
            "              A[j + 1] <- A[j]",
            "              j <- j - 1",
            "          FIN MIENTRAS",
            "          A[j + 1] <- clave",
            "      FIN PARA",
            "  FIN ALGORITMO");
    }

    @Override public String getMejorCaso()     { return "O(n)  (arreglo ya ordenado)"; }
    @Override public String getCasoPromedio()  { return "O(n²)"; }
    @Override public String getPeorCaso()      { return "O(n²)  (arreglo en orden inverso)"; }
    @Override public String getEspacio()       { return "O(1)"; }
    @Override public boolean esEstable()       { return true; }

    @Override
    protected void ordenar(int[] a) {
        int n = a.length;
        boolean rec = grabando();
        if (rec) paso(0, "Inicio. La parte ORDENADA (verde) es solo a[0]; el resto está SIN ORDENAR. "
                + "Se irá insertando cada elemento en su lugar.", a, NADA, NADA, 1, 0, null);

        for (int i = 1; i < n; i++) {
            int clave = a[i];
            int j = i - 1;
            if (rec) paso(2, "i = " + i + ": tomo la clave " + clave + ". La saco del arreglo y la tengo 'en la mano'; "
                    + "queda un hueco en la posición " + i + ".", a, idx(i), NADA, i + 1, 0, clave);

            while (j >= 0) {
                comparaciones++;
                if (a[j] <= clave) {
                    if (rec) paso(4, "Comparo a[" + j + "] = " + a[j] + " con la clave " + clave + ": " + a[j]
                            + " <= " + clave + ", NO hay que desplazar. Aquí termina la búsqueda.",
                            a, idx(j + 1), idx(j), i + 1, 0, clave);
                    break;
                }
                if (rec) paso(4, "Comparo a[" + j + "] = " + a[j] + " con la clave " + clave + ": " + a[j]
                        + " > " + clave + ", hay que desplazarlo a la derecha.",
                        a, idx(j + 1), idx(j), i + 1, 0, clave);
                a[j + 1] = a[j];
                escrituras++;
                if (rec) paso(5, "Desplazo " + a[j + 1] + " una posición a la derecha. El hueco ahora está en la posición "
                        + j + ".", a, idx(j), NADA, i + 1, 0, clave);
                j--;
            }
            a[j + 1] = clave;
            escrituras++;
            if (rec) paso(8, "Inserto la clave " + clave + " en la posición " + (j + 1) + ". Ahora las posiciones 0 a "
                    + i + " están ordenadas.", a, idx(j + 1), NADA, i + 1, 0, null);
        }
        if (rec) paso(-1, "¡Listo! El arreglo quedó ordenado. Comparaciones: " + comparaciones
                + " | Escrituras: " + escrituras + ".", a, NADA, NADA, n, 0, null);
    }
}

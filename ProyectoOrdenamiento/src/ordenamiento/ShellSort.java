package ordenamiento;


/** Shell Sort con la secuencia de saltos original de Shell (n/2, n/4, ..., 1). */
public class ShellSort extends AlgoritmoOrdenamiento {

    @Override public String getNombre() { return "Shell Sort"; }

    @Override
    public String getHistoria() {
        return "  Lo publicó Donald L. Shell en 1959 en la revista Communications of the ACM\n"
             + "  (artículo \"A High-Speed Sorting Procedure\"). Fue uno de los primeros\n"
             + "  algoritmos que logró ser más rápido que los métodos cuadráticos simples.\n"
             + "  La idea: mejorar Insertion Sort permitiendo que los elementos se muevan\n"
             + "  grandes distancias desde el principio, en lugar de un puesto a la vez.\n"
             + "  Su velocidad depende de la secuencia de saltos (gaps). Después de Shell\n"
             + "  otros investigadores propusieron mejores secuencias: Hibbard (1963),\n"
             + "  Pratt (1971), Knuth (1973) y Sedgewick (1986).";
    }

    @Override
    public String[] getCaracteristicas() {
        return new String[] {
            "Es una generalización de Insertion Sort.",
            "Ordena primero elementos lejanos (salto 'gap') y va reduciendo el salto hasta 1.",
            "Cuando el gap llega a 1 el arreglo ya está casi ordenado y Insertion Sort termina rápido.",
            "Es 'en el sitio' (in-place): no necesita memoria extra.",
            "NO es estable: elementos iguales pueden cambiar su orden relativo.",
            "Su rendimiento depende de la secuencia de gaps (aquí: n/2, n/4, ..., 1).",
            "Es sencillo de programar y muy rápido para arreglos medianos."
        };
    }

    @Override
    public String getSeudocodigo() {
        return String.join("\n",
            "  ALGORITMO ShellSort(A, n)",
            "      gap <- n / 2",
            "      MIENTRAS gap > 0 HACER",
            "          PARA i <- gap HASTA n - 1 HACER",
            "              temp <- A[i]",
            "              j <- i",
            "              MIENTRAS j >= gap Y A[j - gap] > temp HACER",
            "                  A[j] <- A[j - gap]",
            "                  j <- j - gap",
            "              FIN MIENTRAS",
            "              A[j] <- temp",
            "          FIN PARA",
            "          gap <- gap / 2",
            "      FIN MIENTRAS",
            "  FIN ALGORITMO");
    }

    @Override public String getMejorCaso()     { return "O(n log n)  (arreglo ya ordenado)"; }
    @Override public String getCasoPromedio()  { return "depende de los gaps; en la práctica entre O(n log n) y O(n^1.5)"; }
    @Override public String getPeorCaso()      { return "O(n²)  con la secuencia original de Shell"; }
    @Override public String getEspacio()       { return "O(1)"; }
    @Override public boolean esEstable()       { return false; }

    @Override
    protected void ordenar(int[] a) {
        int n = a.length;
        boolean rec = grabando();
        if (rec) paso(1, "Inicio. El primer salto (gap) es n / 2 = " + (n / 2) + ". Elementos separados por 'gap' "
                + "posiciones forman una sublista.", a, NADA, NADA, 0, 0, null);

        for (int gap = n / 2; gap > 0; gap /= 2) {
            if (rec) paso(2, "gap = " + gap + ". Cada color es una sublista (posiciones separadas " + gap
                    + "). Se ordena cada sublista por inserción.", a, NADA, NADA, 0, gap, null);

            for (int i = gap; i < n; i++) {
                int temp = a[i];
                int j = i;
                if (rec) paso(4, "i = " + i + ": tomo temp = " + temp + " y lo tengo 'en la mano'. "
                        + "Se compara con los de su misma sublista (de " + gap + " en " + gap + ").",
                        a, idx(i), NADA, 0, gap, temp);

                while (j >= gap) {
                    comparaciones++;
                    if (a[j - gap] <= temp) {
                        if (rec) paso(6, "Comparo a[" + (j - gap) + "] = " + a[j - gap] + " con temp = " + temp + ": "
                                + a[j - gap] + " <= " + temp + ", NO hay que desplazar.",
                                a, idx(j), idx(j - gap), 0, gap, temp);
                        break;
                    }
                    if (rec) paso(6, "Comparo a[" + (j - gap) + "] = " + a[j - gap] + " con temp = " + temp + ": "
                            + a[j - gap] + " > " + temp + ", hay que desplazarlo " + gap + " posiciones.",
                            a, idx(j), idx(j - gap), 0, gap, temp);
                    a[j] = a[j - gap];
                    escrituras++;
                    if (rec) paso(7, "Desplazo " + a[j] + " de la posición " + (j - gap) + " a la " + j
                            + ". El hueco queda en la posición " + (j - gap) + ".",
                            a, idx(j - gap), NADA, 0, gap, temp);
                    j -= gap;
                }
                a[j] = temp;
                escrituras++;
                if (rec) paso(10, "Inserto temp = " + temp + " en la posición " + j + ".", a, idx(j), NADA, 0, gap, null);
            }
            if (rec) paso(12, "Terminó la pasada con gap = " + gap + ". " + (gap > 1
                    ? "El arreglo está más cerca de estar ordenado; ahora el gap se reduce a " + (gap / 2) + "."
                    : "Era el último gap (1): el arreglo quedó ordenado."), a, NADA, NADA, 0, gap, null);
        }
        if (rec) paso(-1, "¡Listo! El arreglo quedó ordenado. Comparaciones: " + comparaciones
                + " | Escrituras: " + escrituras + ".", a, NADA, NADA, n, 0, null);
    }
}

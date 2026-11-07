package ordenamiento;

/** Selection Sort (ordenamiento por selección). */
public class SelectionSort extends AlgoritmoOrdenamiento {

    @Override public String getNombre() { return "Selection Sort"; }

    @Override
    public String getHistoria() {
        return "  Es uno de los algoritmos de ordenamiento más simples e intuitivos y no\n"
             + "  se le atribuye un inventor único. Knuth lo clasifica dentro de la familia\n"
             + "  de los métodos de 'selección' estudiados desde los primeros años de la\n"
             + "  computación. Se enseña mucho porque es fácil de entender; su ventaja\n"
             + "  práctica es que hace muy pocas escrituras (máximo n-1 intercambios).";
    }

    @Override
    public String[] getCaracteristicas() {
        return new String[] {
            "En cada vuelta BUSCA el elemento más pequeño de la parte sin ordenar.",
            "Lo INTERCAMBIA con el primer elemento de la parte sin ordenar.",
            "Siempre hace el mismo número de comparaciones, esté o no ordenado el arreglo.",
            "Hace como máximo n-1 intercambios (pocas escrituras).",
            "Es 'en el sitio' (in-place): no necesita memoria extra.",
            "NO es estable: el intercambio puede alterar el orden de elementos iguales."
        };
    }

    @Override
    public String getSeudocodigo() {
        return String.join("\n",
            "  ALGORITMO SelectionSort(A, n)",
            "      PARA i <- 0 HASTA n - 2 HACER",
            "          min <- i",
            "          PARA j <- i + 1 HASTA n - 1 HACER",
            "              SI A[j] < A[min] ENTONCES",
            "                  min <- j",
            "              FIN SI",
            "          FIN PARA",
            "          SI min <> i ENTONCES",
            "              intercambiar A[i] con A[min]",
            "          FIN SI",
            "      FIN PARA",
            "  FIN ALGORITMO");
    }

    @Override public String getMejorCaso()     { return "O(n²)"; }
    @Override public String getCasoPromedio()  { return "O(n²)"; }
    @Override public String getPeorCaso()      { return "O(n²)"; }
    @Override public String getEspacio()       { return "O(1)"; }
    @Override public boolean esEstable()       { return false; }

    @Override
    protected void ordenar(int[] a) {
        int n = a.length;
        boolean rec = grabando();
        if (rec) paso(0, "Inicio. En cada vuelta se busca el MENOR de la parte sin ordenar y se coloca al principio de ella.",
                a, NADA, NADA, 0, 0, null);

        for (int i = 0; i < n - 1; i++) {
            int min = i;
            if (rec) paso(2, "i = " + i + ": busco el menor entre las posiciones " + i + " y " + (n - 1)
                    + ". Por ahora el mínimo es a[" + i + "] = " + a[i] + ".", a, idx(min), NADA, i, 0, null);

            for (int j = i + 1; j < n; j++) {
                comparaciones++;
                boolean menor = a[j] < a[min];
                if (rec) paso(4, "¿a[" + j + "] = " + a[j] + " es menor que el mínimo actual a[" + min + "] = " + a[min]
                        + "? " + (menor ? "SÍ, es el nuevo mínimo." : "No."), a, idx(min), idx(j), i, 0, null);
                if (menor) min = j;
            }

            if (min != i) {
                int tmp = a[i];
                a[i] = a[min];
                a[min] = tmp;
                escrituras += 2;
                if (rec) paso(9, "El mínimo es " + a[i] + " (estaba en la posición " + min + "). Se INTERCAMBIA con a["
                        + i + "]. La posición " + i + " ya quedó definitiva.", a, idx(i), idx(min), i + 1, 0, null);
            } else {
                if (rec) paso(8, "El mínimo " + a[i] + " ya estaba en la posición " + i + ": no hace falta intercambiar.",
                        a, idx(i), NADA, i + 1, 0, null);
            }
        }
        if (rec) paso(-1, "¡Listo! El arreglo quedó ordenado. Comparaciones: " + comparaciones
                + " | Escrituras: " + escrituras + ".", a, NADA, NADA, n, 0, null);
    }
}

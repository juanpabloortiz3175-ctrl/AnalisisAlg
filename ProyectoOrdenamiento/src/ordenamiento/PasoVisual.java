package ordenamiento;

/**
 * Una "fotografía" del algoritmo en un instante: sirve para la animación
 * paso a paso (barras de colores + explicación + línea del seudocódigo).
 */
public class PasoVisual {
    public final int linea;            // línea del seudocódigo que se está ejecutando (-1 = ninguna)
    public final String texto;         // explicación en español
    public final int[] datos;          // estado del arreglo
    public final int[] claves;         // posiciones resaltadas en naranja (clave / hueco / mínimo)
    public final int[] comparados;     // posiciones resaltadas en rojo (se están comparando)
    public final int ordenadoHasta;    // posiciones menores a este valor se pintan en verde
    public final int gap;              // salto de Shell Sort (0 = no aplica)
    public final Integer mano;         // valor que se tiene "en la mano" (null = ninguno)
    public final long comparaciones;   // contadores acumulados hasta este paso
    public final long escrituras;

    public PasoVisual(int linea, String texto, int[] datos, int[] claves, int[] comparados,
                      int ordenadoHasta, int gap, Integer mano, long comparaciones, long escrituras) {
        this.linea = linea;
        this.texto = texto;
        this.datos = datos;
        this.claves = claves;
        this.comparados = comparados;
        this.ordenadoHasta = ordenadoHasta;
        this.gap = gap;
        this.mano = mano;
        this.comparaciones = comparaciones;
        this.escrituras = escrituras;
    }
}

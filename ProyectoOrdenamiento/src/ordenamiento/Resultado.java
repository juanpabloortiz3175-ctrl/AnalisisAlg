package ordenamiento;

/** Resultado de ejecutar un algoritmo sobre un arreglo de tamaño n. */
public class Resultado {
    public final int n;
    public final long tiempoNs;
    public final long comparaciones;
    public final long escrituras;

    public Resultado(int n, long tiempoNs, long comparaciones, long escrituras) {
        this.n = n;
        this.tiempoNs = tiempoNs;
        this.comparaciones = comparaciones;
        this.escrituras = escrituras;
    }

    public long getOperaciones() { return comparaciones + escrituras; }
    public double getTiempoMs()  { return tiempoNs / 1_000_000.0; }
}

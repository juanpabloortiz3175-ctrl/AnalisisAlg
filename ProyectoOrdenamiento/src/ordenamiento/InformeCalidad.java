package ordenamiento;

import java.util.ArrayList;
import java.util.List;

/** Resultado de la verificación de calidad de UN algoritmo. */
public class InformeCalidad {

    public static class Caso {
        public final String nombre;
        public final boolean ok;
        public final long comparaciones, escrituras;

        public Caso(String nombre, boolean ok, long comparaciones, long escrituras) {
            this.nombre = nombre;
            this.ok = ok;
            this.comparaciones = comparaciones;
            this.escrituras = escrituras;
        }
    }

    public final List<Caso> casos = new ArrayList<Caso>();
    public int pruebas, aprobadas;
    public double razonAleatorio, razonOrdenado;
    public String interpAleatorio, interpOrdenado;
    public String teoriaMejor, teoriaPromedio;
    public boolean aprobado;
}

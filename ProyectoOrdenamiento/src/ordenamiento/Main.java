package ordenamiento;

import javax.swing.SwingUtilities;

/**
 * PROYECTO DIDÁCTICO Y COMPARATIVO DE MÉTODOS DE ORDENAMIENTO
 *   - Shell Sort (algoritmo propio), Insertion Sort y Selection Sort
 *
 * Ejecuta esta clase: se abre la ventana. Todo (información, animaciones,
 * verificación de calidad, mediciones y gráficas) se genera al ejecutar.
 */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override public void run() { VentanaPrincipal.mostrar(); }
        });
    }
}

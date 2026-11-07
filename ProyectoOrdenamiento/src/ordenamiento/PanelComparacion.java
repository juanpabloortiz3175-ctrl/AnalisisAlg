package ordenamiento;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/** Pestaña "Comparación": mide los tres algoritmos y muestra todas las gráficas y tablas. */
public class PanelComparacion extends JPanel {

    private final JComboBox<Integer> comboMax = new JComboBox<Integer>(new Integer[]{5000, 10000, 20000, 50000, 100000});
    private final JButton btnMedir = new JButton("Medir y graficar");
    private final JButton btnGuardar = new JButton("Guardar gráficas (PNG)");
    private final JProgressBar progreso = new JProgressBar(0, 100);
    private final JLabel estado = new JLabel("Elige el tamaño máximo y pulsa «Medir y graficar».");
    private final JPanel contenedor = new JPanel(new BorderLayout());

    private List<GraficaLineas> graficas = new ArrayList<GraficaLineas>();

    public PanelComparacion() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        comboMax.setSelectedItem(20000);
        progreso.setStringPainted(true);
        progreso.setPreferredSize(new Dimension(220, 26));
        btnGuardar.setEnabled(false);

        JPanel arriba = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        arriba.add(new JLabel("Tamaño máximo de n:"));
        arriba.add(comboMax);
        arriba.add(btnMedir);
        arriba.add(progreso);
        arriba.add(btnGuardar);
        add(arriba, BorderLayout.NORTH);

        JLabel vacio = new JLabel("Las gráficas y tablas aparecerán aquí", SwingConstants.CENTER);
        vacio.setForeground(Color.GRAY);
        vacio.setFont(vacio.getFont().deriveFont(18f));
        contenedor.add(vacio, BorderLayout.CENTER);
        add(contenedor, BorderLayout.CENTER);

        estado.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
        add(estado, BorderLayout.SOUTH);

        btnMedir.addActionListener(e -> medir());
        btnGuardar.addActionListener(e -> guardar());
    }

    private void medir() {
        final int max = (Integer) comboMax.getSelectedItem();
        final int[] tamanos = Comparador.tamanos(max);
        final int total = tamanos.length * Algoritmos.cantidad();

        btnMedir.setEnabled(false);
        btnGuardar.setEnabled(false);
        comboMax.setEnabled(false);
        progreso.setValue(0);
        estado.setText("Midiendo con n hasta " + max + "... (los métodos O(n²) tardan un poco)");

        new SwingWorker<List<Comparador.Medicion>, Integer>() {
            private int hechos = 0;

            @Override protected List<Comparador.Medicion> doInBackground() {
                List<Comparador.Medicion> res = new ArrayList<Comparador.Medicion>();
                for (int i = 0; i < Algoritmos.cantidad(); i++) {
                    res.add(Comparador.medir(Algoritmos.crear(i), tamanos, 3, Algoritmos.COLORES[i], new Runnable() {
                        @Override public void run() { publish(++hechos); }
                    }));
                }
                return res;
            }

            @Override protected void process(List<Integer> trozos) {
                int ultimo = trozos.get(trozos.size() - 1);
                progreso.setValue(ultimo * 100 / total);
            }

            @Override protected void done() {
                try {
                    mostrarResultados(get());
                    progreso.setValue(100);
                    estado.setText("Listo. Mismos datos aleatorios para los tres algoritmos, n máximo = " + max + ".");
                    btnGuardar.setEnabled(true);
                } catch (Exception ex) {
                    estado.setText("Error al medir: " + ex.getMessage());
                }
                btnMedir.setEnabled(true);
                comboMax.setEnabled(true);
            }
        }.execute();
    }

    private void mostrarResultados(List<Comparador.Medicion> mediciones) {
        graficas = Comparador.construirGraficas(mediciones);

        JTabbedPane tabs = new JTabbedPane();
        String[] cortos = {"Operaciones", "Operaciones (log)", "Tiempo"};
        for (int i = 0; i < graficas.size(); i++) {
            String t = i < cortos.length ? cortos[i] : "Calidad: " + Algoritmos.NOMBRES[i - cortos.length];
            tabs.addTab(t, graficas.get(i));
        }
        tabs.addTab("Tablas y conclusión", panelTablas(mediciones));

        contenedor.removeAll();
        contenedor.add(tabs, BorderLayout.CENTER);
        contenedor.revalidate();
        contenedor.repaint();
    }

    private JComponent panelTablas(List<Comparador.Medicion> mediciones) {
        JPanel caja = new JPanel();
        caja.setLayout(new BoxLayout(caja, BoxLayout.Y_AXIS));

        StringBuilder sb = new StringBuilder("COMPLEJIDAD DETECTADA A PARTIR DE LOS DATOS REALES:\n");
        for (Comparador.Medicion m : mediciones) {
            sb.append("   •  ").append(m.algoritmo.getNombre()).append("  ->  ")
              .append(Comparador.complejidadDetectada(m))
              .append("      (teoría, caso promedio: ").append(m.algoritmo.getCasoPromedio()).append(")\n");
        }
        JTextArea concl = new JTextArea(sb.toString().trim());
        concl.setEditable(false);
        concl.setFont(concl.getFont().deriveFont(Font.BOLD, 15f));
        concl.setBackground(new Color(255, 252, 232));
        concl.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        concl.setAlignmentX(Component.LEFT_ALIGNMENT);
        caja.add(concl);

        for (Comparador.Medicion m : mediciones) {
            DefaultTableModel modelo = new DefaultTableModel(
                    new Object[]{"n", "Comparaciones", "Escrituras", "Operaciones", "Tiempo (ms)"}, 0) {
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };
            for (int i = 0; i < m.n.length; i++) {
                modelo.addRow(new Object[]{(long) m.n[i], (long) m.comparaciones[i], (long) m.escrituras[i],
                        (long) m.operaciones[i], String.format("%.2f", m.ms[i])});
            }
            JTable tabla = new JTable(modelo);
            tabla.setRowHeight(24);
            JScrollPane sp = new JScrollPane(tabla);
            sp.setBorder(BorderFactory.createTitledBorder(m.algoritmo.getNombre()));
            sp.setPreferredSize(new Dimension(700, 24 * (m.n.length + 1) + 50));
            sp.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24 * (m.n.length + 1) + 50));
            sp.setAlignmentX(Component.LEFT_ALIGNMENT);
            caja.add(sp);
        }
        return new JScrollPane(caja);
    }

    private void guardar() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Elige la carpeta donde guardar las gráficas");
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        try {
            int n = Comparador.guardarPNG(graficas, fc.getSelectedFile());
            JOptionPane.showMessageDialog(this, "Se guardaron " + n + " gráficas en:\n" + fc.getSelectedFile(),
                    "Gráficas guardadas", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "No se pudo guardar: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

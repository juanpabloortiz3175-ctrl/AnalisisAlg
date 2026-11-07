package ordenamiento;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/** Verificación de calidad de UN algoritmo (una pestaña por algoritmo). */
public class PanelCalidad extends JPanel {

    private final int indice;
    private final JButton boton = new JButton("Ejecutar verificación");
    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"Caso de prueba", "Estado", "Comparaciones", "Escrituras"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable tabla = new JTable(modelo);
    private final JTextArea resumen = new JTextArea();
    private final JLabel veredicto = new JLabel("Aún no se ha verificado", SwingConstants.CENTER);

    public PanelCalidad(int indice) {
        this.indice = indice;
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel arriba = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        JLabel t = new JLabel("Calidad de " + Algoritmos.NOMBRES[indice]);
        t.setFont(t.getFont().deriveFont(Font.BOLD, 20f));
        arriba.add(t);
        arriba.add(boton);
        add(arriba, BorderLayout.NORTH);

        tabla.setRowHeight(24);
        tabla.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tb, Object v, boolean sel, boolean foc, int r, int c) {
                Component comp = super.getTableCellRendererComponent(tb, v, sel, foc, r, c);
                setHorizontalAlignment(CENTER);
                boolean ok = "OK".equals(v);
                comp.setForeground(ok ? new Color(20, 130, 20) : new Color(200, 20, 20));
                comp.setFont(comp.getFont().deriveFont(Font.BOLD));
                return comp;
            }
        });
        JScrollPane sp = new JScrollPane(tabla);
        sp.setBorder(BorderFactory.createTitledBorder("1) Casos especiales (se compara contra Arrays.sort de Java)"));

        resumen.setEditable(false);
        resumen.setLineWrap(true);
        resumen.setWrapStyleWord(true);
        resumen.setMargin(new Insets(6, 8, 6, 8));
        resumen.setFont(resumen.getFont().deriveFont(15f));
        JScrollPane sr = new JScrollPane(resumen);
        sr.setBorder(BorderFactory.createTitledBorder("2) Pruebas masivas   y   3) Rendimiento al duplicar n"));

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, sp, sr);
        split.setResizeWeight(0.65);
        add(split, BorderLayout.CENTER);

        veredicto.setOpaque(true);
        veredicto.setFont(veredicto.getFont().deriveFont(Font.BOLD, 22f));
        veredicto.setBackground(new Color(235, 235, 235));
        veredicto.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(veredicto, BorderLayout.SOUTH);

        boton.addActionListener(e -> ejecutar());
    }

    public JButton getBoton() { return boton; }

    /** Corre la verificación en un hilo aparte para no congelar la ventana. */
    public void ejecutar() {
        boton.setEnabled(false);
        veredicto.setText("Verificando...");
        veredicto.setBackground(new Color(235, 235, 235));
        veredicto.setForeground(Color.BLACK);

        new SwingWorker<InformeCalidad, Void>() {
            @Override protected InformeCalidad doInBackground() {
                return VerificadorCalidad.verificar(Algoritmos.crear(indice));
            }
            @Override protected void done() {
                try {
                    mostrar(get());
                } catch (Exception ex) {
                    veredicto.setText("Error al verificar: " + ex.getMessage());
                }
                boton.setEnabled(true);
            }
        }.execute();
    }

    private void mostrar(InformeCalidad inf) {
        modelo.setRowCount(0);
        for (InformeCalidad.Caso c : inf.casos) {
            modelo.addRow(new Object[]{c.nombre, c.ok ? "OK" : "FALLO", c.comparaciones, c.escrituras});
        }
        resumen.setText(
                "Pruebas aleatorias masivas:  " + inf.aprobadas + " de " + inf.pruebas
                + " arreglos aleatorios (con repetidos y negativos) quedaron bien ordenados.\n\n"
                + "Al DUPLICAR n (de 4000 a 8000) el trabajo se multiplica por:\n"
                + "   (referencia:  n log n ≈ x2.1   |   n^1.5 ≈ x2.8   |   n² ≈ x4.0)\n"
                + String.format("   •  Datos aleatorios:     x%.2f  ->  %s%n", inf.razonAleatorio, inf.interpAleatorio)
                + String.format("   •  Datos ya ordenados:   x%.2f  ->  %s%n%n", inf.razonOrdenado, inf.interpOrdenado)
                + "Teoría:   mejor caso " + inf.teoriaMejor + "   |   promedio " + inf.teoriaPromedio);
        resumen.setCaretPosition(0);

        if (inf.aprobado) {
            veredicto.setText("APROBADO: " + Algoritmos.NOMBRES[indice] + " ordena correctamente");
            veredicto.setBackground(new Color(200, 235, 200));
            veredicto.setForeground(new Color(10, 100, 10));
        } else {
            veredicto.setText("REPROBADO: " + Algoritmos.NOMBRES[indice] + " tiene errores");
            veredicto.setBackground(new Color(245, 200, 200));
            veredicto.setForeground(new Color(160, 0, 0));
        }
    }
}

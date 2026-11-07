package ordenamiento;

import java.awt.*;
import java.util.List;
import java.util.Random;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;

/**
 * Pestaña "Paso a paso": animación del algoritmo con barras, explicación,
 * contadores y la línea del seudocódigo que se está ejecutando.
 */
public class PanelPasoAPaso extends JPanel {

    private final JComboBox<String> comboAlgoritmo = new JComboBox<String>(Algoritmos.NOMBRES);
    private final JSpinner spinnerN = new JSpinner(new SpinnerNumberModel(10, 5, 16, 1));
    private final JTextField campoDatos = new JTextField(32);
    private final PanelBarras barras = new PanelBarras();
    private final JTextArea explicacion = new JTextArea(3, 40);
    private final JLabel contadores = new JLabel(" ");
    private final JLabel posicion = new JLabel("Paso 0 / 0");
    private final JTextArea seudocodigo = new JTextArea();
    private final JButton btnInicio = new JButton("|◀ Inicio");
    private final JButton btnAnterior = new JButton("◀ Anterior");
    private final JButton btnPlay = new JButton("▶ Reproducir");
    private final JButton btnSiguiente = new JButton("Siguiente ▶");
    private final JSlider velocidad = new JSlider(1, 10, 6);
    private final Timer timer;
    private final Random azar = new Random();

    private List<PasoVisual> pasos;
    private int actual = 0;

    public PanelPasoAPaso() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        // ---- Barra superior ----
        JPanel arriba = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        arriba.add(new JLabel("Algoritmo:"));
        arriba.add(comboAlgoritmo);
        arriba.add(new JLabel("Elementos:"));
        arriba.add(spinnerN);
        JButton btnAleatorio = new JButton("Aleatorio");
        arriba.add(btnAleatorio);
        arriba.add(new JLabel("Datos (editables):"));
        arriba.add(campoDatos);
        JButton btnIniciar = new JButton("Iniciar");
        arriba.add(btnIniciar);
        add(arriba, BorderLayout.NORTH);

        // ---- Centro: barras + explicación ----
        explicacion.setEditable(false);
        explicacion.setLineWrap(true);
        explicacion.setWrapStyleWord(true);
        explicacion.setFont(explicacion.getFont().deriveFont(Font.BOLD, 18f));
        explicacion.setBackground(new Color(255, 252, 232));
        JPanel panelExp = new JPanel(new BorderLayout(4, 4));
        panelExp.setBorder(BorderFactory.createTitledBorder("¿Qué está pasando?"));
        panelExp.add(new JScrollPane(explicacion), BorderLayout.CENTER);
        contadores.setFont(contadores.getFont().deriveFont(Font.BOLD, 15f));
        panelExp.add(contadores, BorderLayout.SOUTH);

        JSplitPane centro = new JSplitPane(JSplitPane.VERTICAL_SPLIT, barras, panelExp);
        centro.setResizeWeight(0.72);
        centro.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        add(centro, BorderLayout.CENTER);

        // ---- Derecha: seudocódigo ----
        seudocodigo.setEditable(false);
        seudocodigo.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        JScrollPane spSeudo = new JScrollPane(seudocodigo);
        spSeudo.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                "Seudocódigo (línea en ejecución resaltada)", TitledBorder.LEFT, TitledBorder.TOP));
        spSeudo.setPreferredSize(new Dimension(520, 300));
        add(spSeudo, BorderLayout.EAST);

        // ---- Controles inferiores ----
        JPanel abajo = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 4));
        abajo.add(btnInicio);
        abajo.add(btnAnterior);
        abajo.add(btnPlay);
        abajo.add(btnSiguiente);
        abajo.add(new JLabel("   Velocidad:"));
        velocidad.setPreferredSize(new Dimension(160, 40));
        abajo.add(velocidad);
        posicion.setFont(posicion.getFont().deriveFont(Font.BOLD));
        abajo.add(posicion);
        add(abajo, BorderLayout.SOUTH);

        // ---- Lógica ----
        timer = new Timer(900, e -> avanzarAuto());
        timer.setRepeats(true);
        velocidad.addChangeListener(e -> timer.setDelay(2300 - velocidad.getValue() * 200));
        timer.setDelay(2300 - velocidad.getValue() * 200);

        btnAleatorio.addActionListener(e -> {
            generarAleatorio();
            iniciar();
        });
        btnIniciar.addActionListener(e -> iniciar());
        comboAlgoritmo.addActionListener(e -> iniciar());
        btnInicio.addActionListener(e -> { detener(); mostrar(0); });
        btnAnterior.addActionListener(e -> { detener(); mostrar(actual - 1); });
        btnSiguiente.addActionListener(e -> { detener(); mostrar(actual + 1); });
        btnPlay.addActionListener(e -> {
            if (timer.isRunning()) detener();
            else {
                if (pasos != null && actual >= pasos.size() - 1) mostrar(0);
                timer.start();
                btnPlay.setText("Pausar");
            }
        });

        generarAleatorio();
        iniciar();
    }

    private void generarAleatorio() {
        int n = (Integer) spinnerN.getValue();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            if (i > 0) sb.append(", ");
            sb.append(azar.nextInt(90) + 10);
        }
        campoDatos.setText(sb.toString());
    }

    private int[] leerDatos() {
        String[] partes = campoDatos.getText().trim().split("[,\\s;]+");
        if (partes.length < 2 || partes.length > 16) return null;
        int[] d = new int[partes.length];
        try {
            for (int i = 0; i < d.length; i++) {
                d[i] = Integer.parseInt(partes[i]);
                if (d[i] < 1 || d[i] > 999) return null;
            }
        } catch (NumberFormatException ex) {
            return null;
        }
        return d;
    }

    /** Ejecuta el algoritmo elegido sobre los datos y graba todos sus pasos. */
    private void iniciar() {
        detener();
        int[] datos = leerDatos();
        if (datos == null) {
            JOptionPane.showMessageDialog(this,
                    "Escribe entre 2 y 16 números enteros entre 1 y 999, separados por comas.",
                    "Datos no válidos", JOptionPane.WARNING_MESSAGE);
            return;
        }
        AlgoritmoOrdenamiento alg = Algoritmos.crear(comboAlgoritmo.getSelectedIndex());
        alg.setGrabando(true);
        alg.sort(datos.clone());
        pasos = alg.getPasos();

        seudocodigo.setText(alg.getSeudocodigo());
        seudocodigo.setCaretPosition(0);
        mostrar(0);
    }

    private void detener() {
        timer.stop();
        btnPlay.setText("▶ Reproducir");
    }

    private void avanzarAuto() {
        if (pasos == null || actual >= pasos.size() - 1) {
            detener();
            return;
        }
        mostrar(actual + 1);
        if (actual >= pasos.size() - 1) detener();
    }

    private void mostrar(int k) {
        if (pasos == null || pasos.isEmpty()) return;
        actual = Math.max(0, Math.min(k, pasos.size() - 1));
        PasoVisual p = pasos.get(actual);

        barras.setPaso(p);
        explicacion.setText(p.texto);
        contadores.setText("   Comparaciones: " + p.comparaciones + "     Escrituras: " + p.escrituras);
        posicion.setText("Paso " + (actual + 1) + " / " + pasos.size());
        btnInicio.setEnabled(actual > 0);
        btnAnterior.setEnabled(actual > 0);
        btnSiguiente.setEnabled(actual < pasos.size() - 1);

        Highlighter h = seudocodigo.getHighlighter();
        h.removeAllHighlights();
        if (p.linea >= 0) {
            try {
                int ini = seudocodigo.getLineStartOffset(p.linea);
                int fin = seudocodigo.getLineEndOffset(p.linea);
                h.addHighlight(ini, fin, new DefaultHighlighter.DefaultHighlightPainter(new Color(255, 225, 100)));
            } catch (BadLocationException ex) {
                // línea fuera de rango: no se resalta nada
            }
        }
    }
}

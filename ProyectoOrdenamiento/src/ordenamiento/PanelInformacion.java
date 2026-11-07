package ordenamiento;

import java.awt.*;
import javax.swing.*;

/** Pestaña "Información": historia, características, seudocódigo y rendimiento O(). */
public class PanelInformacion extends JPanel {

    private final JTextArea historia = area(false);
    private final JTextArea caracteristicas = area(false);
    private final JTextArea seudocodigo = area(true);
    private final JTextArea rendimiento = area(false);
    private final JLabel titulo = new JLabel();

    public PanelInformacion() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel arriba = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        arriba.add(new JLabel("Algoritmo:"));
        ButtonGroup grupo = new ButtonGroup();
        for (int i = 0; i < Algoritmos.cantidad(); i++) {
            final int idx = i;
            JToggleButton b = new JToggleButton(Algoritmos.NOMBRES[i], i == 0);
            b.setPreferredSize(new Dimension(160, 34));
            b.addActionListener(e -> cargar(idx));
            grupo.add(b);
            arriba.add(b);
        }
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 22f));
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 24, 0, 0));
        arriba.add(titulo);
        add(arriba, BorderLayout.NORTH);

        JPanel centro = new JPanel(new GridLayout(2, 2, 8, 8));
        centro.add(conTitulo(historia, "Historia"));
        centro.add(conTitulo(caracteristicas, "Características"));
        centro.add(conTitulo(seudocodigo, "Seudocódigo"));
        centro.add(conTitulo(rendimiento, "Rendimiento: notación Big O"));
        add(centro, BorderLayout.CENTER);

        cargar(0);
    }

    private static JTextArea area(boolean mono) {
        JTextArea a = new JTextArea();
        a.setEditable(false);
        a.setLineWrap(true);
        a.setWrapStyleWord(true);
        a.setMargin(new Insets(8, 10, 8, 10));
        a.setFont(mono ? new Font(Font.MONOSPACED, Font.PLAIN, 15) : a.getFont().deriveFont(16f));
        return a;
    }

    private static JComponent conTitulo(JTextArea a, String titulo) {
        JScrollPane sp = new JScrollPane(a);
        sp.setBorder(BorderFactory.createTitledBorder(titulo));
        return sp;
    }

    private void cargar(int i) {
        AlgoritmoOrdenamiento alg = Algoritmos.crear(i);
        titulo.setText(alg.getNombre());

        historia.setText(alg.getHistoria().replaceAll("\\s*\\n\\s*", " ").trim());

        StringBuilder sb = new StringBuilder();
        for (String c : alg.getCaracteristicas()) sb.append("•  ").append(c).append("\n\n");
        caracteristicas.setText(sb.toString().trim());

        seudocodigo.setText(alg.getSeudocodigo());

        rendimiento.setText(
                "Mejor caso:\n   " + alg.getMejorCaso()
                + "\n\nCaso promedio:\n   " + alg.getCasoPromedio()
                + "\n\nPeor caso:\n   " + alg.getPeorCaso()
                + "\n\nMemoria extra:   " + alg.getEspacio()
                + "\n¿Es estable?:   " + (alg.esEstable() ? "Sí" : "No"));

        for (JTextArea a : new JTextArea[]{historia, caracteristicas, seudocodigo, rendimiento}) a.setCaretPosition(0);
    }
}

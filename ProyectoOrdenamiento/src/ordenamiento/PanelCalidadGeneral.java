package ordenamiento;

import java.awt.*;
import javax.swing.*;

/** Pestaña "Calidad": una sub-pestaña por algoritmo + botón para verificar los tres. */
public class PanelCalidadGeneral extends JPanel {

    public PanelCalidadGeneral() {
        setLayout(new BorderLayout(6, 6));

        final PanelCalidad[] paneles = new PanelCalidad[Algoritmos.cantidad()];
        JTabbedPane tabs = new JTabbedPane();
        for (int i = 0; i < paneles.length; i++) {
            paneles[i] = new PanelCalidad(i);
            tabs.addTab(Algoritmos.NOMBRES[i], paneles[i]);
        }

        JPanel arriba = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        JLabel info = new JLabel("Cada algoritmo se verifica POR SEPARADO.");
        JButton todos = new JButton("Verificar los 3 algoritmos");
        todos.addActionListener(e -> {
            for (PanelCalidad p : paneles) p.ejecutar();
        });
        arriba.add(info);
        arriba.add(todos);

        add(arriba, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
    }
}

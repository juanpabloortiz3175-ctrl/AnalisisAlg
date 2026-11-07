package ordenamiento;

import java.awt.*;
import java.util.Collections;
import javax.swing.*;
import javax.swing.plaf.FontUIResource;

/** Ventana principal: TODO el proyecto se ve aquí (nada por consola). */
public class VentanaPrincipal extends JFrame {

    public VentanaPrincipal() {
        super("Métodos de ordenamiento: Shell Sort, Insertion Sort y Selection Sort");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JLabel banner = new JLabel("  Métodos de ordenamiento · Producto didáctico y comparativo", SwingConstants.LEFT);
        banner.setOpaque(true);
        banner.setBackground(new Color(30, 60, 130));
        banner.setForeground(Color.WHITE);
        banner.setFont(banner.getFont().deriveFont(Font.BOLD, 22f));
        banner.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(tabs.getFont().deriveFont(Font.BOLD, 16f));
        tabs.addTab("1. Información", new PanelInformacion());
        tabs.addTab("2. Paso a paso", new PanelPasoAPaso());
        tabs.addTab("3. Calidad", new PanelCalidadGeneral());
        tabs.addTab("4. Comparación y gráficas", new PanelComparacion());

        setLayout(new BorderLayout());
        add(banner, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);

        setMinimumSize(new Dimension(1000, 650));
        setSize(1280, 800);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    /** Aplica un aspecto moderno (Nimbus) y letra más grande para proyectar. */
    public static void configurarAspecto() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // si falla, se queda con el aspecto por defecto
        }
        for (Object key : Collections.list(UIManager.getDefaults().keys())) {
            Object valor = UIManager.get(key);
            if (valor instanceof FontUIResource) {
                Font f = (Font) valor;
                UIManager.put(key, new FontUIResource(f.deriveFont(Math.max(f.getSize2D(), 15f))));
            }
        }
    }

    public static void mostrar() {
        configurarAspecto();
        new VentanaPrincipal().setVisible(true);
    }
}

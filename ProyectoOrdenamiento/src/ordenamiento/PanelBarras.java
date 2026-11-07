package ordenamiento;

import java.awt.*;

/** Dibuja el arreglo como barras de colores para la animación paso a paso. */
public class PanelBarras extends javax.swing.JPanel {

    private static final Color GRIS_AZUL = new Color(150, 170, 205);
    private static final Color VERDE     = new Color(110, 195, 120);
    private static final Color NARANJA   = new Color(255, 160, 40);
    private static final Color ROJO      = new Color(230, 80, 80);
    private static final Color[] GRUPOS = {
        new Color(120, 160, 230), new Color(175, 135, 220), new Color(100, 200, 200), new Color(230, 195, 90),
        new Color(225, 140, 195), new Color(160, 160, 160), new Color(120, 120, 215), new Color(190, 150, 110)
    };

    private PasoVisual paso;
    private String mensajeVacio = "Pulsa «Iniciar» para ver el algoritmo paso a paso";

    public PanelBarras() {
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(600, 330));
    }

    public void setPaso(PasoVisual p) {
        this.paso = p;
        repaint();
    }

    private static boolean contiene(int[] v, int x) {
        for (int e : v) if (e == x) return true;
        return false;
    }

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        int w = getWidth(), h = getHeight();

        if (paso == null) {
            g.setColor(Color.GRAY);
            g.setFont(new Font("SansSerif", Font.PLAIN, 18));
            int tw = g.getFontMetrics().stringWidth(mensajeVacio);
            g.drawString(mensajeVacio, (w - tw) / 2, h / 2);
            return;
        }

        int[] d = paso.datos;
        int n = d.length;
        int mIzq = 30, mDer = 30, mSup = 70, mInf = 80;
        int aw = w - mIzq - mDer, ah = h - mSup - mInf;

        int max = 1;
        for (int v : d) max = Math.max(max, v);
        if (paso.mano != null) max = Math.max(max, paso.mano);

        double slot = (double) aw / n;
        int bw = (int) Math.min(80, slot * 0.74);
        float tam = Math.max(11f, Math.min(22f, bw / 2.1f));
        Font fuenteValor = new Font("SansSerif", Font.BOLD, Math.round(tam));
        Font fuenteIdx = new Font("SansSerif", Font.PLAIN, Math.round(Math.max(11f, tam - 3)));

        for (int i = 0; i < n; i++) {
            int x = (int) (mIzq + slot * i + (slot - bw) / 2);
            boolean esClave = contiene(paso.claves, i);
            boolean esComp = contiene(paso.comparados, i);
            boolean ordenado = i < paso.ordenadoHasta;

            if (esClave && paso.mano != null) {
                // HUECO: contorno punteado con la altura de la clave que se tiene en la mano
                int bh = (int) (ah * (double) paso.mano / max);
                g.setColor(NARANJA);
                g.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{6, 5}, 0));
                g.drawRect(x, mSup + ah - bh, bw, bh);
                g.setFont(fuenteValor);
                String s = String.valueOf(paso.mano);
                g.drawString(s, x + (bw - g.getFontMetrics().stringWidth(s)) / 2, mSup + ah - bh - 6);
            } else {
                int bh = Math.max(6, (int) (ah * (double) d[i] / max));
                Color c;
                if (esComp) c = ROJO;
                else if (esClave) c = NARANJA;
                else if (ordenado) c = VERDE;
                else if (paso.gap > 0) c = GRUPOS[(i % paso.gap) % GRUPOS.length];
                else c = GRIS_AZUL;
                g.setColor(c);
                g.fillRect(x, mSup + ah - bh, bw, bh);
                g.setColor(c.darker().darker());
                g.setStroke(new BasicStroke(1.3f));
                g.drawRect(x, mSup + ah - bh, bw, bh);
                g.setColor(Color.BLACK);
                g.setFont(fuenteValor);
                String s = String.valueOf(d[i]);
                g.drawString(s, x + (bw - g.getFontMetrics().stringWidth(s)) / 2, mSup + ah - bh - 6);
            }

            // Índice debajo de la barra (y banda de color de la sublista en Shell Sort)
            g.setColor(new Color(90, 90, 90));
            g.setFont(fuenteIdx);
            String si = String.valueOf(i);
            g.drawString(si, x + (bw - g.getFontMetrics().stringWidth(si)) / 2, mSup + ah + 18);
            if (paso.gap > 0) {
                g.setColor(GRUPOS[(i % paso.gap) % GRUPOS.length]);
                g.fillRect(x, mSup + ah + 25, bw, 8);
            }
        }

        // Texto superior
        g.setFont(new Font("SansSerif", Font.BOLD, 18));
        if (paso.mano != null) {
            g.setColor(new Color(200, 110, 0));
            g.drawString("En la mano: " + paso.mano, mIzq, 32);
        }
        if (paso.gap > 0) {
            g.setColor(new Color(60, 60, 60));
            String s = "gap = " + paso.gap;
            g.drawString(s, w - mDer - g.getFontMetrics().stringWidth(s), 32);
        }

        // Leyenda
        String[] et = {"Ordenado", "Clave / mínimo / hueco", "Comparando", paso.gap > 0 ? "Sublistas (un color cada una)" : "Sin ordenar"};
        Color[] co = {VERDE, NARANJA, ROJO, paso.gap > 0 ? GRUPOS[0] : GRIS_AZUL};
        g.setFont(new Font("SansSerif", Font.PLAIN, 13));
        int x = mIzq, y = h - 16;
        for (int i = 0; i < et.length; i++) {
            g.setColor(co[i]);
            g.fillRect(x, y - 11, 14, 14);
            g.setColor(Color.BLACK);
            g.drawString(et[i], x + 20, y + 1);
            x += 20 + g.getFontMetrics().stringWidth(et[i]) + 28;
        }
    }
}

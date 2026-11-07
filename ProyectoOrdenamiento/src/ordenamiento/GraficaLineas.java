package ordenamiento;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * Panel genérico que dibuja una gráfica de líneas con varias series.
 * Soporta escala lineal o logarítmica en Y y se puede guardar como PNG
 * (útil para pegarla en la presentación).
 */
public class GraficaLineas extends JPanel {

    /** Una línea de la gráfica. */
    public static class Serie {
        final String nombre;
        final Color color;
        final double[] x, y;
        boolean punteada = false;
        boolean marcadores = true;
        float grosor = 2.4f;

        public Serie(String nombre, Color color, double[] x, double[] y) {
            this.nombre = nombre;
            this.color = color;
            this.x = x;
            this.y = y;
        }

        public Serie estilo(boolean punteada, boolean marcadores, float grosor) {
            this.punteada = punteada;
            this.marcadores = marcadores;
            this.grosor = grosor;
            return this;
        }
    }

    private final String titulo, ejeX, ejeY;
    private final List<Serie> series = new ArrayList<Serie>();
    private final boolean logY;
    private String aviso;

    public GraficaLineas(String titulo, String ejeX, String ejeY, boolean logY) {
        this.titulo = titulo;
        this.ejeX = ejeX;
        this.ejeY = ejeY;
        this.logY = logY;
        setBackground(Color.WHITE);
    }

    public GraficaLineas agregar(Serie s) { series.add(s); return this; }
    public GraficaLineas setAviso(String aviso) { this.aviso = aviso; return this; }
    public String getTitulo() { return titulo; }

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        dibujar((Graphics2D) g0, getWidth(), getHeight());
    }

    /** Guarda la gráfica como imagen PNG. */
    public void guardarPNG(File archivo, int ancho, int alto) throws IOException {
        BufferedImage img = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, ancho, alto);
        dibujar(g, ancho, alto);
        g.dispose();
        ImageIO.write(img, "png", archivo);
    }

    // ------------------------------------------------------------------
    private void dibujar(Graphics2D g, int ancho, int alto) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int mIzq = 85, mDer = 30, mSup = 50, mInf = 60;
        int aw = ancho - mIzq - mDer;
        int ah = alto - mSup - mInf;

        // Rangos de datos
        double xMax = 1, yMaxDatos = 1, yMinPos = Double.MAX_VALUE;
        for (Serie s : series) {
            for (int i = 0; i < s.x.length; i++) {
                xMax = Math.max(xMax, s.x[i]);
                yMaxDatos = Math.max(yMaxDatos, s.y[i]);
                if (s.y[i] > 0) yMinPos = Math.min(yMinPos, s.y[i]);
            }
        }
        if (yMinPos == Double.MAX_VALUE) yMinPos = 1;

        double yMin, yTop;
        double paso = 0;
        if (logY) {
            yMin = Math.pow(10, Math.floor(Math.log10(yMinPos)));
            yTop = Math.pow(10, Math.ceil(Math.log10(yMaxDatos)));
            if (yTop <= yMin) yTop = yMin * 10;
        } else {
            yMin = 0;
            paso = pasoBonito(yMaxDatos * 1.05 / 5);
            yTop = Math.ceil(yMaxDatos * 1.02 / paso) * paso;
            if (yTop <= 0) yTop = 1;
        }

        // Título
        g.setColor(Color.BLACK);
        g.setFont(new Font("SansSerif", Font.BOLD, 16));
        FontMetrics fmT = g.getFontMetrics();
        g.drawString(titulo, (ancho - fmT.stringWidth(titulo)) / 2, 28);

        // Cuadrícula y etiquetas del eje Y
        g.setFont(new Font("SansSerif", Font.PLAIN, 11));
        Stroke punteado = new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{4, 4}, 0);
        if (logY) {
            int d0 = (int) Math.round(Math.log10(yMin));
            int d1 = (int) Math.round(Math.log10(yTop));
            for (int d = d0; d <= d1; d++) {
                double v = Math.pow(10, d);
                lineaY(g, punteado, mIzq, aw, mSup, ah, mapY(v, yMin, yTop, ah), formatear(v));
            }
        } else {
            for (double v = 0; v <= yTop + paso * 0.001; v += paso) {
                lineaY(g, punteado, mIzq, aw, mSup, ah, mapY(v, yMin, yTop, ah), formatear(v));
            }
        }

        // Cuadrícula y etiquetas del eje X
        for (int i = 0; i <= 5; i++) {
            int x = mIzq + aw * i / 5;
            g.setColor(new Color(225, 225, 225));
            g.setStroke(punteado);
            g.drawLine(x, mSup, x, mSup + ah);
            g.setColor(new Color(60, 60, 60));
            String et = formatear(xMax * i / 5);
            g.drawString(et, x - g.getFontMetrics().stringWidth(et) / 2, mSup + ah + 18);
        }

        // Marco
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(1.2f));
        g.drawRect(mIzq, mSup, aw, ah);

        // Nombres de los ejes
        g.setFont(new Font("SansSerif", Font.PLAIN, 12));
        int twx = g.getFontMetrics().stringWidth(ejeX);
        g.drawString(ejeX, mIzq + (aw - twx) / 2, alto - 14);
        Graphics2D gy = (Graphics2D) g.create();
        gy.rotate(-Math.PI / 2);
        int twy = gy.getFontMetrics().stringWidth(ejeY);
        gy.drawString(ejeY, -(mSup + (ah + twy) / 2), 18);
        gy.dispose();

        // Series
        for (Serie s : series) {
            GeneralPath path = new GeneralPath();
            for (int i = 0; i < s.x.length; i++) {
                double px = mIzq + aw * (s.x[i] / xMax);
                double py = mSup + mapY(s.y[i], yMin, yTop, ah);
                if (i == 0) path.moveTo(px, py); else path.lineTo(px, py);
            }
            g.setColor(s.color);
            g.setStroke(s.punteada
                    ? new BasicStroke(s.grosor, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{6, 4}, 0)
                    : new BasicStroke(s.grosor));
            g.draw(path);
            if (s.marcadores) {
                for (int i = 0; i < s.x.length; i++) {
                    double px = mIzq + aw * (s.x[i] / xMax);
                    double py = mSup + mapY(s.y[i], yMin, yTop, ah);
                    g.setColor(s.color);
                    g.fill(new Ellipse2D.Double(px - 4, py - 4, 8, 8));
                    g.setColor(Color.WHITE);
                    g.fill(new Ellipse2D.Double(px - 2, py - 2, 4, 4));
                }
            }
        }

        // Leyenda
        g.setFont(new Font("SansSerif", Font.PLAIN, 12));
        int lx = mIzq + 14, ly = mSup + 22;
        int anchoCaja = 0;
        for (Serie s : series) anchoCaja = Math.max(anchoCaja, g.getFontMetrics().stringWidth(s.nombre));
        anchoCaja += 56;
        int altoCaja = series.size() * 20 + 8;
        g.setColor(new Color(255, 255, 255, 235));
        g.fillRect(lx - 8, ly - 15, anchoCaja, altoCaja);
        g.setColor(new Color(180, 180, 180));
        g.setStroke(new BasicStroke(1f));
        g.drawRect(lx - 8, ly - 15, anchoCaja, altoCaja);
        for (int i = 0; i < series.size(); i++) {
            Serie s = series.get(i);
            int y = ly + i * 20;
            g.setColor(s.color);
            g.setStroke(s.punteada
                    ? new BasicStroke(s.grosor, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{5, 3}, 0)
                    : new BasicStroke(s.grosor));
            g.drawLine(lx, y, lx + 28, y);
            g.setColor(Color.BLACK);
            g.drawString(s.nombre, lx + 36, y + 4);
        }

        // Aviso (por ejemplo: "Complejidad detectada: O(n²)")
        if (aviso != null) {
            g.setFont(new Font("SansSerif", Font.BOLD, 14));
            FontMetrics fm = g.getFontMetrics();
            int w = fm.stringWidth(aviso) + 20;
            int x = mIzq + aw - w - 8, y = mSup + ah - 40;
            g.setColor(new Color(255, 255, 230));
            g.fillRoundRect(x, y, w, 28, 8, 8);
            g.setColor(new Color(90, 90, 90));
            g.drawRoundRect(x, y, w, 28, 8, 8);
            g.setColor(Color.BLACK);
            g.drawString(aviso, x + 10, y + 19);
        }
    }

    private void lineaY(Graphics2D g, Stroke punteado, int mIzq, int aw, int mSup, int ah, double yRel, String etiqueta) {
        int y = (int) Math.round(mSup + yRel);
        g.setColor(new Color(225, 225, 225));
        g.setStroke(punteado);
        g.drawLine(mIzq, y, mIzq + aw, y);
        g.setColor(new Color(60, 60, 60));
        g.drawString(etiqueta, mIzq - g.getFontMetrics().stringWidth(etiqueta) - 8, y + 4);
    }

    /** Convierte un valor a posición vertical (0 = arriba del área de dibujo). */
    private double mapY(double v, double yMin, double yTop, int ah) {
        double frac;
        if (logY) {
            double lv = Math.log10(Math.max(v, yMin));
            frac = (lv - Math.log10(yMin)) / (Math.log10(yTop) - Math.log10(yMin));
        } else {
            frac = (v - yMin) / (yTop - yMin);
        }
        return ah - ah * frac;
    }

    /** Redondea un paso a 1, 2, 5 x 10^k para que las marcas del eje sean "bonitas". */
    private static double pasoBonito(double crudo) {
        if (crudo <= 0) return 1;
        double exp = Math.pow(10, Math.floor(Math.log10(crudo)));
        double f = crudo / exp;
        double n = f <= 1 ? 1 : f <= 2 ? 2 : f <= 5 ? 5 : 10;
        return n * exp;
    }

    /** Formato compacto: 1500 -> 1.5k, 2000000 -> 2M, 3e9 -> 3G. */
    static String formatear(double v) {
        double a = Math.abs(v);
        if (a >= 1e9) return recorte(v / 1e9) + "G";
        if (a >= 1e6) return recorte(v / 1e6) + "M";
        if (a >= 1e4) return recorte(v / 1e3) + "k";
        if (a == Math.floor(a)) return String.valueOf((long) v);
        return recorte(v);
    }

    private static String recorte(double v) {
        String s = String.format(java.util.Locale.US, "%.1f", v);
        return s.endsWith(".0") ? s.substring(0, s.length() - 2) : s;
    }
}

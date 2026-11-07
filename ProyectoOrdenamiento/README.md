# Métodos de ordenamiento: proyecto didáctico y comparativo (Java)

Todo el proyecto se ve en **una sola ventana** (Swing), sin usar la consola.
Compara **tres algoritmos de ordenamiento**:

| Algoritmo | Tipo | Rendimiento promedio |
|---|---|---|
| **Shell Sort** (algoritmo propio) | Inserción con saltos (gaps) | entre O(n log n) y O(n^1.5) según los gaps |
| **Insertion Sort** | Inserción | O(n²) (mejor caso O(n)) |
| **Selection Sort** | Selección | O(n²) |

## Cómo ejecutarlo

Requisitos: **JDK 8 o superior**.

- **NetBeans:** *File > New Project > Java Project with Existing Sources*, agregar la carpeta `src`, y ejecutar `Main.java` (clic derecho > Run File).
- Windows: doble clic en `ejecutar.bat`
- Linux / Mac: `./ejecutar.sh`
- A mano:
  ```
  javac -encoding UTF-8 -d out src/ordenamiento/*.java
  java -cp out ordenamiento.Main
  ```

## Pestañas de la ventana

1. **Información**: historia, características, seudocódigo y rendimiento O() de cada método.
2. **Paso a paso**: animación con barras de colores, explicación de cada paso, contadores y la línea del seudocódigo que se está ejecutando. Se puede reproducir, pausar, ir adelante/atrás y escribir tus propios datos.
3. **Calidad**: cada algoritmo se verifica POR SEPARADO (casos especiales, 500 pruebas aleatorias contra `Arrays.sort` y cómo crece el trabajo al duplicar n).
4. **Comparación y gráficas**: mide los tres con los mismos datos y muestra las gráficas (operaciones lineal y logarítmica, tiempo, y datos reales vs Big O por algoritmo) más las tablas. Todo se genera al instante, al ejecutar; el botón «Guardar gráficas (PNG)» es opcional.

## Estructura

```
src/ordenamiento/
  Main.java                    abre la ventana
  VentanaPrincipal.java        JFrame con las 4 pestañas
  PanelInformacion / PanelPasoAPaso / PanelBarras / PanelCalidad(General) / PanelComparacion
  AlgoritmoOrdenamiento.java   clase base (historia, características, seudocódigo, O(), contadores, pasos)
  ShellSort.java               algoritmo propio
  InsertionSort.java
  SelectionSort.java
  VerificadorCalidad.java      pruebas de calidad por algoritmo
  Comparador.java              mediciones y gráficas comparativas
  GraficaLineas.java           dibujo de gráficas y exportación a PNG
```

## Cómo se mide el rendimiento

- **Comparaciones** entre elementos y **escrituras** en el arreglo; su suma son las *operaciones*.
- **Tiempo real** en milisegundos (promedio de 3 ejecuciones).
- Los tres algoritmos ordenan **exactamente los mismos datos** (misma semilla).
- Para cada algoritmo se compara la curva real contra O(n), O(n log n), O(n^1.5) y O(n²) y se muestra cuál ajusta mejor.

## Entrega

- Código en GitHub: **10 de noviembre**
- Presentación: **12 de noviembre**

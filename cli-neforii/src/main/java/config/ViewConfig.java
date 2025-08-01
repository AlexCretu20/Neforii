package config;

public class ViewConfig {
    public static final int DEFAULT_WIDTH = 32; // width default pentru afisare
    public static final int PADDING = 2;
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm";
    public static final String COLOR_NORMAL = "\u001B[0m"; // pentru a reveni la culoarea alba default
    public static final String COLOR_RED = "\u001B[31m"; // pentru culoarea rosie, in general intalnita la afisarea erorilor
    public static final String COLOR_GREEN = "\u001B[32m"; // pentru culoarea verde, in general intalnita la success
    public static final String COLOR_YELLOW = "\u001b[43m"; // galben ar trebui sa fie culoarea optiunilor de meniu
    public static final String COLOR_BLUE = "\u001b[44m"; // pentru detalii precum @username sau chestii de genu, depinde cum arata mai frumos
}

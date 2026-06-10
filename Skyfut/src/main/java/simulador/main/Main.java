package simulador.torneo.main;

import simulador.torneo.persistence.DatabaseConnection;

public class Main {
    public static void main(String[] args) {
        try {
            System.out.println("=== Simulador de Torneo de Fútbol ===");
            System.out.println("Inicializando base de datos...");

            // Inicializa la conexión (Singleton) y carga el schema si es necesario
            DatabaseConnection dbConnection = DatabaseConnection.getInstance();
            System.out.println("✓ Conexión a base de datos establecida.");

            // TODO: Aquí va la lógica de la aplicación
            // Por ahora solo confirma que la BD está lista
            System.out.println("Base de datos lista para usar.");

        } catch (Exception e) {
            System.err.println("✗ Error al inicializar la aplicación:");
            e.printStackTrace();
        }
    }
}

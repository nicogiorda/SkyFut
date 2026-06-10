package simulador.main;

import simulador.facade.TorneoFacade;

public class Main {
    public static void main(String[] args) {
        try {
            System.out.println("=== Simulador de Torneo de Futbol ===");
            System.out.println("Inicializando base de datos...");

            new TorneoFacade();

            System.out.println("Base de datos lista para usar.");
            System.out.println("El torneo se debe crear desde la opcion Nuevo torneo de la CLI.");
        } catch (Exception e) {
            System.err.println("Error al inicializar la aplicacion:");
            e.printStackTrace();
        }
    }
}

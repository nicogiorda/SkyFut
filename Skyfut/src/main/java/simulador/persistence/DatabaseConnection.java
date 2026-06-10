package simulador.torneo.persistence;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static DatabaseConnection instancia;
    private final Connection connection;
    private static final String URL = "jdbc:sqlite:db/torneo.db";
    private static final String SCHEMA_PATH = "db/schema.sql";

    private DatabaseConnection() throws SQLException {
        this.connection = DriverManager.getConnection(URL);
        this.connection.setAutoCommit(false);
        inicializarBaseDatos();
    }

    public static DatabaseConnection getInstance() throws SQLException {
        if (instancia == null) {
            instancia = new DatabaseConnection();
        }
        return instancia;
    }

    public Connection getConnection() {
        return connection;
    }

    /**
     * Inicializa la base de datos cargando el schema si aún no existe.
     * Verifica si la tabla "tactica" existe para determinar si ya fue inicializada.
     */
    private void inicializarBaseDatos() throws SQLException {
        try {
            // Verificar si ya existe la tabla tactica
            if (!tablaExiste("tactica")) {
                cargarSchema();
                System.out.println("✓ Base de datos inicializada correctamente.");
            }
        } catch (IOException e) {
            throw new SQLException("Error al inicializar la base de datos: " + e.getMessage(), e);
        }
    }

    /**
     * Verifica si una tabla existe en la base de datos.
     */
    private boolean tablaExiste(String nombreTabla) throws SQLException {
        String query = "SELECT name FROM sqlite_master WHERE type='table' AND name=?";
        try (var stmt = connection.prepareStatement(query)) {
            stmt.setString(1, nombreTabla);
            try (var rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Carga y ejecuta el esquema SQL desde el archivo schema.sql.
     */
    private void cargarSchema() throws SQLException, IOException {
        Path schemaPath = Paths.get(SCHEMA_PATH);

        // Intentar desde archivo local primero
        if (Files.exists(schemaPath)) {
            ejecutarScriptSQL(Files.readString(schemaPath));
        } else {
            // Alternativa: cargar desde classpath (para producción)
            InputStream resourceStream = getClass().getClassLoader()
                    .getResourceAsStream("db/schema.sql");
            if (resourceStream != null) {
                String schema = new String(resourceStream.readAllBytes(), StandardCharsets.UTF_8);
                ejecutarScriptSQL(schema);
                resourceStream.close();
            } else {
                throw new IOException("No se encontró schema.sql en " + SCHEMA_PATH +
                        " ni en classpath");
            }
        }
    }

    /**
     * Ejecuta un script SQL dividiéndolo por punto y coma.
     */
    private void ejecutarScriptSQL(String script) throws SQLException {
        String[] sentencias = script.split(";");
        try (Statement stmt = connection.createStatement()) {
            for (String sentencia : sentencias) {
                String sql = sentencia.trim();
                if (!sql.isEmpty() && !sql.startsWith("--")) {
                    stmt.execute(sql);
                }
            }
            connection.commit();
        }
    }

    /**
     * Cierra la conexión (a llamar al finalizar la aplicación).
     */
    public void cerrar() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}

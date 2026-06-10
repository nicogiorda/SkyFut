package simulador.persistence;

import java.io.IOException;
import java.io.InputStream;
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
    private static final String URL = "jdbc:sqlite:db/torneo.db";
    private static final String SCHEMA_PATH = "db/schema.sql";
    private static final String SEED_PATH = "db/seed.sql";

    private final Connection connection;

    private DatabaseConnection() throws SQLException {
        cargarDriverSqlite();
        this.connection = DriverManager.getConnection(URL);
        activarForeignKeys();
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

    private void cargarDriverSqlite() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new SQLException("No se encontro el driver SQLite. Verifica la dependencia sqlite-jdbc del pom.xml.", e);
        }
    }

    private void activarForeignKeys() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON");
        }
    }

    private void inicializarBaseDatos() throws SQLException {
        try {
            cargarScript(SCHEMA_PATH);
            cargarScript(SEED_PATH);
            System.out.println("Base de datos verificada correctamente.");
        } catch (IOException e) {
            throw new SQLException("Error al inicializar la base de datos: " + e.getMessage(), e);
        }
    }

    private void cargarScript(String scriptPath) throws SQLException, IOException {
        Path path = Paths.get(scriptPath);

        if (Files.exists(path)) {
            ejecutarScriptSQL(Files.readString(path));
            return;
        }

        try (InputStream resourceStream = getClass().getClassLoader().getResourceAsStream(scriptPath)) {
            if (resourceStream == null) {
                throw new IOException("No se encontro " + scriptPath + " ni en classpath");
            }
            String script = new String(resourceStream.readAllBytes(), StandardCharsets.UTF_8);
            ejecutarScriptSQL(script);
        }
    }

    private void ejecutarScriptSQL(String script) throws SQLException {
        String scriptSinComentarios = removerComentariosSQL(script);
        String[] sentencias = scriptSinComentarios.split(";");

        try (Statement stmt = connection.createStatement()) {
            for (String sentencia : sentencias) {
                String sql = sentencia.trim();
                if (!sql.isEmpty()) {
                    stmt.execute(sql);
                }
            }
            connection.commit();
        }
    }

    private String removerComentariosSQL(String script) {
        StringBuilder resultado = new StringBuilder();
        for (String linea : script.split("\\R")) {
            String lineaLimpia = linea.stripLeading();
            if (!lineaLimpia.startsWith("--")) {
                resultado.append(linea).append(System.lineSeparator());
            }
        }
        return resultado.toString();
    }

    public void cerrar() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}

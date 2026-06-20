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
    private static final String DB_DIRECTORY = "db";
    private static final String DATABASE_FILE = "torneo.db";
    private static final String SCHEMA_FILE = "schema.sql";
    private static final String SEED_FILE = "seed.sql";

    private final Connection connection;
    private final Path databasePath;

    private DatabaseConnection() throws SQLException {
        cargarDriverSqlite();
        this.databasePath = resolverRutaBaseDatos();
        asegurarDirectorioBaseDatos();
        this.connection = DriverManager.getConnection("jdbc:sqlite:" + databasePath.toAbsolutePath());
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
            cargarScript(SCHEMA_FILE);
            cargarScript(SEED_FILE);
            System.out.println("Base de datos verificada correctamente.");
        } catch (IOException e) {
            throw new SQLException("Error al inicializar la base de datos: " + e.getMessage(), e);
        }
    }

    private void cargarScript(String scriptFile) throws SQLException, IOException {
        Path path = resolverRutaScript(scriptFile);

        if (path != null && Files.exists(path)) {
            ejecutarScriptSQL(Files.readString(path));
            return;
        }

        try (InputStream resourceStream = getClass().getClassLoader().getResourceAsStream(DB_DIRECTORY + "/" + scriptFile)) {
            if (resourceStream == null) {
                throw new IOException("No se encontro " + scriptFile + " ni en filesystem ni en classpath");
            }
            String script = new String(resourceStream.readAllBytes(), StandardCharsets.UTF_8);
            ejecutarScriptSQL(script);
        }
    }

    private Path resolverRutaBaseDatos() {
        Path directorioScripts = resolverDirectorioScripts();
        if (directorioScripts != null) {
            return directorioScripts.resolve(DATABASE_FILE);
        }
        return Paths.get(DB_DIRECTORY, DATABASE_FILE).toAbsolutePath();
    }

    private void asegurarDirectorioBaseDatos() throws SQLException {
        try {
            Path parent = databasePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
        } catch (IOException e) {
            throw new SQLException("No se pudo crear el directorio de la base de datos: " + e.getMessage(), e);
        }
    }

    private Path resolverRutaScript(String scriptFile) {
        Path directorioScripts = resolverDirectorioScripts();
        if (directorioScripts != null) {
            Path ruta = directorioScripts.resolve(scriptFile);
            if (Files.exists(ruta)) {
                return ruta;
            }
        }
        return null;
    }

    private Path resolverDirectorioScripts() {
        Path actual = Paths.get("").toAbsolutePath();

        while (actual != null) {
            Path candidatoDirecto = actual.resolve(DB_DIRECTORY);
            if (contieneScriptsSQL(candidatoDirecto)) {
                return candidatoDirecto;
            }

            Path candidatoModulo = actual.resolve("Skyfut").resolve(DB_DIRECTORY);
            if (contieneScriptsSQL(candidatoModulo)) {
                return candidatoModulo;
            }

            actual = actual.getParent();
        }

        return null;
    }

    private boolean contieneScriptsSQL(Path directorio) {
        return Files.exists(directorio.resolve(SCHEMA_FILE)) && Files.exists(directorio.resolve(SEED_FILE));
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

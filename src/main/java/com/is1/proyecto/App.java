package com.is1.proyecto; // Asegúrate de que el paquete coincide con tu groupId y estructura de carpetas

import com.fasterxml.jackson.databind.ObjectMapper; // Necesario para convertir objetos Java a JSON

import static spark.Spark.*; // Importa los métodos estáticos de Spark (get, post, before, after, etc.)

// Importaciones de ActiveJDBC
import org.javalite.activejdbc.Base; // Clase central de ActiveJDBC para gestión de DB
import com.is1.proyecto.models.User; // Tu modelo User para interactuar con la tabla 'users'

import java.util.Map; // Para usar Map.of() en las respuestas JSON

public class App {

    // ObjectMapper es una clase de Jackson para serializar/deserializar JSON.
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) {
        port(8080); // La aplicación Spark escuchará en el puerto 8080

        // --- Configuración de filtros para abrir y cerrar conexiones de ActiveJDBC ---
        // Este filtro se ejecuta ANTES de que cualquier ruta sea procesada.
        // Aquí abrimos la conexión a la base de datos para la solicitud actual.
        before((req, res) -> {
            try {
                // Base.open() gestiona la conexión. Se conecta a la BD 'students_db' con el usuario 'muva'.
                // ¡ADVERTENCIA! Credenciales en código fuente NO SEGURAS para producción.
                Base.open("com.mysql.cj.jdbc.Driver", "jdbc:mysql://localhost:3306/students_db", "muva", "muva");
            } catch (Exception e) {
                System.err.println("Error al abrir conexión con ActiveJDBC: " + e.getMessage());
                // Si la conexión falla, detiene la solicitud y envía un error 500 al cliente.
                halt(500, "{\"error\": \"Error interno del servidor: Fallo al conectar a la base de datos.\"}" + e.getMessage());
            }
        });

        // Este filtro se ejecuta DESPUÉS de que cualquier ruta ha sido procesada (o si hubo un error).
        // Aquí cerramos la conexión de la base de datos, devolviéndola al pool de ActiveJDBC.
        after((req, res) -> {
            try {
                Base.close(); // Cierra la conexión de ActiveJDBC para esta solicitud.
            } catch (Exception e) {
                System.err.println("Error al cerrar conexión con ActiveJDBC: " + e.getMessage());
                // Este error es de limpieza, no debería detener la respuesta principal.
            }
        });
        // -------------------------------------------------------------------------

        // --- Endpoint de prueba simple (ruta raíz) ---
        get("/", (req, res) -> {
            res.type("application/json"); // La respuesta será en formato JSON.
            return objectMapper.writeValueAsString(Map.of("message", "¡Bienvenido a la API simplificada! Usa /users para crear usuarios."));
        });

        // --- Endpoint para dar de alta un nuevo User (método POST) ---
        // Espera parámetros 'name' y 'password' en el cuerpo de la solicitud (form-urlencoded).
        // Ejemplo de uso con curl:
        // curl -X POST -d "name=Alice&password=pass123" http://localhost:8080/users
        post("/users", (req, res) -> {
            res.type("application/json"); // La respuesta será en formato JSON.

            // Obtiene los parámetros 'name' y 'password' de la solicitud.
            String name = req.queryParams("name");
            String password = req.queryParams("password");

            // --- Validaciones básicas ---
            if (name == null || name.isEmpty() || password == null || password.isEmpty()) {
                res.status(400); // Código de estado HTTP 400 (Bad Request).
                return objectMapper.writeValueAsString(Map.of("error", "Nombre y contraseña son requeridos."));
            }

            try {
                // --- Creación y guardado del usuario usando el modelo ActiveJDBC ---
                User newUser = new User(); // Crea una nueva instancia de tu modelo User.
                newUser.set("name", name); // Asigna el nombre al campo 'name'.
                newUser.set("password", password); // Asigna la contraseña al campo 'password'.
                // ¡ADVERTENCIA DE SEGURIDAD CRÍTICA!
                // En una aplicación real, las contraseñas DEBEN ser hasheadas (ej. con BCrypt)
                // ANTES de guardarse en la base de datos, NUNCA en texto plano.

                newUser.saveIt(); // Guarda el nuevo usuario en la tabla 'users'.

                res.status(201); // Código de estado HTTP 201 (Created) para una creación exitosa.
                // Devuelve una respuesta JSON con el mensaje y el ID del nuevo usuario.
                return objectMapper.writeValueAsString(Map.of("message", "Usuario '" + name + "' registrado con éxito.", "id", newUser.getId()));

            } catch (Exception e) {
                // Si ocurre cualquier error durante la operación de DB, se captura aquí.
                System.err.println("Error al registrar usuario: " + e.getMessage());
                e.printStackTrace(); // Imprime el stack trace para depuración.
                res.status(500); // Código de estado HTTP 500 (Internal Server Error).
                return objectMapper.writeValueAsString(Map.of("error", "Error interno al registrar usuario: " + e.getMessage()));
            }
        });

        // Este mensaje se mostrará en la consola cuando la aplicación se inicie.
        System.out.println("Servidor Spark simplificado iniciado en http://localhost:8080");
        System.out.println("Prueba POST a /users con 'name' y 'password' en el cuerpo.");
    }
}

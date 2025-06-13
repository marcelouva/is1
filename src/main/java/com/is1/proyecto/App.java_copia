package com.is1.proyecto;
import static spark.Spark.*;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class App {

     // Declara e inicializa el logger para esta clase
    private static final Logger LOG = LoggerFactory.getLogger(App.class);
    public static void main(String[] args) {
        // Configurar el puerto del servidor
        port(4567);
        LOG.info("servidor    en: 4567");

        // Definir rutas
        get("/", (req, res) -> "¡Bienvenido al servidor Spark!");

        get("/hello", (req, res) -> "¡Hola, mundo!");

        // Ruta con parámetros
        get("/saludar/:nombre", (req, res) -> {
            String nombre = req.params("nombre");
            return "¡Hola, " + nombre + "!";
        });

        // Manejo de POST
        post("/mensaje", (req, res) -> {
            String mensaje = req.body();
            return "Mensaje recibido: " + mensaje;
        });

        // Manejo de respuestas JSON
        get("/json", (req, res) -> {
            res.type("application/json");
            return "{\"mensaje\": \"¡Hola desde Spark!\"}";
        });
    }
}

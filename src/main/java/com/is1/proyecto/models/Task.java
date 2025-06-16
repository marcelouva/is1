package com.is1.proyecto.models;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;
import org.javalite.activejdbc.annotations.Many2Many;
import org.javalite.activejdbc.annotations.BelongsTo; // Importar BelongsTo

// Anotación para mapear la clase al nombre de la tabla en la base de datos
@Table("tasks")
// Anotación para definir la relación de muchos a muchos con Team
// other: La otra clase del modelo en la relación (Team.class)
// join: El nombre de la tabla intermedia que une las dos tablas ("teams_tasks")
// sourceFKName: El nombre de la columna en la tabla 'teams_tasks' que es la clave foránea para 'tasks' (este modelo)
// targetFKName: El nombre de la columna en la tabla 'teams_tasks' que es la clave foránea para 'teams' (el otro modelo)

@Many2Many(other = Team.class, join = "teams_tasks", sourceFKName = "task_id", targetFKName = "team_id")
public class Task extends Model {

    // Puedes definir getters y setters si lo deseas para un acceso más tipado
    // Aunque ActiveJDBC permite el acceso dinámico con getString(), set(), etc.

    public String getName() {
        return getString("name");
    }

    public void setName(String name) {
        set("name", name);
    }

    public String getGoal() {
        return getString("goal");
    }

    public void setGoal(String goal) {
        set("goal", goal);
    }
}
package com.is1.proyecto.models;
import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;
import org.javalite.activejdbc.annotations.HasMany;
import org.javalite.activejdbc.annotations.Many2Many;

@Table("teams")
@HasMany(child = User.class, foreignKeyName = "team_id") // Un Team tiene muchos Users
// Añadimos la anotación Many2Many para la relación con Task
// other: La otra clase del modelo en la relación (Task.class)
// join: El nombre de la tabla intermedia que une las dos tablas ("teams_tasks")
// sourceFKName: El nombre de la columna en la tabla 'teams_tasks' que es la clave foránea para 'teams' (este modelo)
// targetFKName: El nombre de la columna en la tabla 'teams_tasks' que es la clave foránea para 'tasks' (el otro modelo)
@Many2Many(other = Task.class, join = "teams_tasks", sourceFKName = "team_id", targetFKName = "task_id")




public class Team extends Model {
    // Puedes definir getters/setters si querés, pero no es obligatorio.
    public String getName() {
        return getString("name");
    }

    public void setName(String name) {
        set("name", name);
    }

    public String getDescription() {
        return getString("description");
    }

    public void setDescription(String description) {
        set("description", description);
    }
}

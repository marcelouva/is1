package com.is1.proyecto.models;

import org.eclipse.jetty.util.thread.Scheduler.Task;
import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;
import org.javalite.activejdbc.annotations.HasMany;
import org.javalite.activejdbc.annotations.Many2Many;

@Table("teams")
@HasMany(child = User.class, foreignKeyName = "team_id") // Un Team tiene muchos Users

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

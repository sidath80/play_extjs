package models;

import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 */
@Entity
public class Service extends Model {
    public String name;
    public String action;
    public String type;
    public String description;
    public String genClass;
    public boolean disabled;

    public Service(String name, String action, String genClass, String type) {
        this.name = name;
        this.action = action;
        this.genClass = genClass;
        this.type = type;
    }

    @ManyToOne
    public ServiceGroup serviceGroup;

    @Override
    public String toString() {
        return "Service{" +
                "name='" + name + '\'' +
                "action='" + action + '\'' +
                "genClass='" + genClass + '\'' +
                "type='" + type + '\'' +
                '}';
    }
}

package models;

import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 */
@Entity
public class ServiceProperty extends Model {
    public String name;
    public String value;

    @ManyToOne
    public Service service;

    public ServiceProperty(String name, String value, Service service) {
        this.name = name;
        this.value = value;
        this.service = service;
    }

    @Override
    public String toString() {
        return "ServiceProperty{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}

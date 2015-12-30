package models;

import play.db.jpa.Model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 */
@Entity
public class Node extends Model {
    public String host;
    public String port;

    public Node(String host, String port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public String toString() {
        return "Node{" +
                "host='" + host + '\'' +
                ", port='" + port + '\'' +
                '}';
    }

    @ManyToMany(cascade = CascadeType.ALL)
    public Set<ServiceGroup> serviceGroups  = new HashSet<ServiceGroup>();;
}

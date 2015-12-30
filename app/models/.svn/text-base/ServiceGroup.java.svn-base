package models;

import play.db.jpa.Model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;

/**
 */
@Entity
public class ServiceGroup extends Model {
    public String groupId;
    public String artifactId;
    public String version;

    @OneToMany(mappedBy = "serviceGroup", cascade = CascadeType.ALL)
    public Set<Service> services = new HashSet<Service>();

    @ManyToMany(mappedBy = "serviceGroups")
    public Set<Node> nodes = new HashSet<Node>();

    public ServiceGroup(String groupId, String artifactId, String version) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
    }

    @Override
    public String toString() {
        return "ServiceGroup{" +
                "groupId='" + groupId + '\'' +
                ", artifactId='" + artifactId + '\'' +
                ", version='" + version + '\'' +
                '}';
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        ServiceGroup clone = new ServiceGroup(this.groupId, this.artifactId, this.version);
        return clone;
    }

    public void addService(Service service) {
        services.add(service);
        service.serviceGroup = this;
    }

    public void addNode(Node node) {
        nodes.add(node);
        node.serviceGroups.add(this);
    }

}

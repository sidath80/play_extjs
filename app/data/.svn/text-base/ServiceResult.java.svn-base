package data;

/**
 */
public class ServiceResult {
    private Long serviceId;
    private String groupId;
    private String artifactId;
    private String version;
    private String name;
    private String type;

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ServiceResult(Long serviceId, String groupId, String artifactId, String version, String name, String type) {
        this.serviceId = serviceId;
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.name = name;
        this.type = type;
    }

    @Override
    public String toString() {
        return "ServiceResult{" +
                "serviceId='" + serviceId + '\'' +
                ", groupId='" + groupId + '\'' +
                ", artifactId='" + artifactId + '\'' +
                ", version='" + version + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

    public String getFullName() {
        return groupId + "-" + artifactId + "-" + version + "-" + name;
    }
}

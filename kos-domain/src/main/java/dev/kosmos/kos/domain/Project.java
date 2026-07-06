package dev.kosmos.kos.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(
    name = "project",
    uniqueConstraints = @UniqueConstraint(columnNames = {"namespace", "name"})
)
public class Project {

    @Id
    private String id;

    @Column(nullable = false, length = 64)
    private String name;

    @Column(nullable = false, length = 64)
    private String namespace;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private ProjectStatus status;

    @Column(name = "service_endpoint", nullable = false, length = 512)
    private String serviceEndpoint;

    @Column(name = "bundle_ref", length = 512)
    private String bundleRef;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false)
    private Map<String, String> settings = Map.of();

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    void prePersist() {
        if (id == null) id = UUID.randomUUID().toString();
        if (status == null) status = ProjectStatus.DRAFT;
        createdAt = updatedAt = OffsetDateTime.now();
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = OffsetDateTime.now();
    }

    public String getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getNamespace() { return namespace; }
    public void setNamespace(String namespace) { this.namespace = namespace; }

    public ProjectStatus getStatus() { return status; }
    public void setStatus(ProjectStatus status) { this.status = status; }

    public String getServiceEndpoint() { return serviceEndpoint; }
    public void setServiceEndpoint(String serviceEndpoint) { this.serviceEndpoint = serviceEndpoint; }

    public String getBundleRef() { return bundleRef; }
    public void setBundleRef(String bundleRef) { this.bundleRef = bundleRef; }

    public Map<String, String> getSettings() { return settings; }
    public void setSettings(Map<String, String> settings) { this.settings = settings != null ? settings : Map.of(); }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }

    public ProjectSummary toSummary() {
        return new ProjectSummary(id, name, namespace, status, updatedAt);
    }
}

package com.example.accountservice.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseEntity extends AbstractAuditingEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    protected Long id;

    @Column(name = "deleted")
    private Boolean deleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Override
    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        }
        if(obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        BaseEntity other = (BaseEntity) obj;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
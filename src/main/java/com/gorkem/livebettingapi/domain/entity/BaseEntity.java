package com.gorkem.livebettingapi.domain.entity;

import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@SuperBuilder
@NoArgsConstructor
public abstract class BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 4969381288071476820L;

    public abstract Serializable getId();

    public int hashCode() {
        return Objects.hashCode(this.getId());
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (other != null && this.getClass() == other.getClass()) {
            return (
                    (this.getId() != null && ((BaseEntity) other).getId() != null) &&
                            Objects.equals(this.getId(), ((BaseEntity) other).getId())
            );
        } else {
            return false;
        }
    }
}

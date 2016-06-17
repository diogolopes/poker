package br.lopes.poker.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.util.ClassUtils;

@MappedSuperclass
public class AbstractEntity<PK extends Serializable> implements Persistable<PK> {

    private static final long serialVersionUID = -5554308939380869754L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private PK id;

    @Version
    @Column(name = "version")
    private Date version;

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.data.domain.Persistable#getId()
     */
    public PK getId() {
        return id;
    }

    /**
     * Sets the id of the entity.
     * 
     * @param id
     *            the id to set
     */
    protected void setId(final PK id) {
        this.id = id;
    }

    public Date getVersion() {
        return version;
    }

    public void setVersion(final Date version) {
        this.version = version;
    }

    /**
     * Must be {@link Transient} in order to ensure that no JPA provider
     * complains because of a missing setter.
     * 
     * @see DATAJPA-622
     * @see org.springframework.data.domain.Persistable#isNew()
     */
    @Transient
    public boolean isNew() {
        return null == getId();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("Entity of type %s with id: %s", this.getClass().getName(), getId());
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {

        if (null == obj) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        if (!getClass().equals(ClassUtils.getUserClass(obj))) {
            return false;
        }

        AbstractEntity<?> that = (AbstractEntity<?>) obj;

        return null == this.getId() ? false : this.getId().equals(that.getId());
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {

        int hashCode = 17;

        hashCode += null == getId() ? 0 : getId().hashCode() * 31;

        return hashCode;
    }

}

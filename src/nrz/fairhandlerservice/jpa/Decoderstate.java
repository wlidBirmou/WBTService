/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nrz.fairhandlerservice.jpa;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author rahimAdmin
 */
@Entity
@Table(name = "decoderstate")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Decoderstate.findAll", query = "SELECT d FROM Decoderstate d")
    , @NamedQuery(name = "Decoderstate.findByIdDecoderState", query = "SELECT d FROM Decoderstate d WHERE d.idDecoderState = :idDecoderState")
    , @NamedQuery(name = "Decoderstate.findByIsdecoderOn", query = "SELECT d FROM Decoderstate d WHERE d.isdecoderOn = :isdecoderOn")
    , @NamedQuery(name = "Decoderstate.findByFirstDate", query = "SELECT d FROM Decoderstate d WHERE d.firstDate = :firstDate")
    , @NamedQuery(name = "Decoderstate.findBySecondDate", query = "SELECT d FROM Decoderstate d WHERE d.secondDate = :secondDate")})
public class Decoderstate implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idDecoderState")
    private Integer idDecoderState;
    @Basic(optional = false)
    @Column(name = "isdecoderOn")
    private int isdecoderOn;
    @Basic(optional = false)
    @Column(name = "firstDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date firstDate;
    @Basic(optional = false)
    @Column(name = "secondDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date secondDate;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "decoderState")
    private Collection<Balancestate> balancestateCollection;

    public Decoderstate() {
    }

    public Decoderstate(Integer idDecoderState) {
        this.idDecoderState = idDecoderState;
    }

    public Decoderstate(Integer idDecoderState, int isdecoderOn, Date firstDate, Date secondDate) {
        this.idDecoderState = idDecoderState;
        this.isdecoderOn = isdecoderOn;
        this.firstDate = firstDate;
        this.secondDate = secondDate;
    }

    public Integer getIdDecoderState() {
        return idDecoderState;
    }

    public void setIdDecoderState(Integer idDecoderState) {
        this.idDecoderState = idDecoderState;
    }

    public int getIsdecoderOn() {
        return isdecoderOn;
    }

    public void setIsdecoderOn(int isdecoderOn) {
        this.isdecoderOn = isdecoderOn;
    }

    public Date getFirstDate() {
        return firstDate;
    }

    public void setFirstDate(Date firstDate) {
        this.firstDate = firstDate;
    }

    public Date getSecondDate() {
        return secondDate;
    }

    public void setSecondDate(Date secondDate) {
        this.secondDate = secondDate;
    }

    @XmlTransient
    public Collection<Balancestate> getBalancestateCollection() {
        return balancestateCollection;
    }

    public void setBalancestateCollection(Collection<Balancestate> balancestateCollection) {
        this.balancestateCollection = balancestateCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idDecoderState != null ? idDecoderState.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Decoderstate)) {
            return false;
        }
        Decoderstate other = (Decoderstate) object;
        if ((this.idDecoderState == null && other.idDecoderState != null) || (this.idDecoderState != null && !this.idDecoderState.equals(other.idDecoderState))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "nrz.fairhandlerservice.jpa.Decoderstate[ idDecoderState=" + idDecoderState + " ]";
    }
    
}

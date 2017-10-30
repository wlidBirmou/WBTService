/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nrz.fairhandlerservice.jpa;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author rahimAdmin
 */
@Entity
@Table(name = "balancestate")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Balancestate.findAll", query = "SELECT b FROM Balancestate b")
    , @NamedQuery(name = "Balancestate.findByIdbalanceState", query = "SELECT b FROM Balancestate b WHERE b.idbalanceState = :idbalanceState")
    , @NamedQuery(name = "Balancestate.findByBalanceState", query = "SELECT b FROM Balancestate b WHERE b.balanceState = :balanceState")
    , @NamedQuery(name = "Balancestate.findByFirstDate", query = "SELECT b FROM Balancestate b WHERE b.firstDate = :firstDate")
    , @NamedQuery(name = "Balancestate.findBySecondDate", query = "SELECT b FROM Balancestate b WHERE b.secondDate = :secondDate")})
public class Balancestate implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idbalanceState")
    private Integer idbalanceState;
    @Basic(optional = false)
    @Column(name = "balanceState")
    private int balanceState;
    @Basic(optional = false)
    @Column(name = "firstDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date firstDate;
    @Basic(optional = false)
    @Column(name = "secondDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date secondDate;
    @JoinColumn(name = "decoderState", referencedColumnName = "idDecoderState")
    @ManyToOne(optional = false)
    private Decoderstate decoderState;

    public Balancestate() {
    }

    public Balancestate(Integer idbalanceState) {
        this.idbalanceState = idbalanceState;
    }

    public Balancestate(Integer idbalanceState, int balanceState, Date firstDate, Date secondDate) {
        this.idbalanceState = idbalanceState;
        this.balanceState = balanceState;
        this.firstDate = firstDate;
        this.secondDate = secondDate;
    }

    public Integer getIdbalanceState() {
        return idbalanceState;
    }

    public void setIdbalanceState(Integer idbalanceState) {
        this.idbalanceState = idbalanceState;
    }

    public int getBalanceState() {
        return balanceState;
    }

    public void setBalanceState(int balanceState) {
        this.balanceState = balanceState;
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

    public Decoderstate getDecoderState() {
        return decoderState;
    }

    public void setDecoderState(Decoderstate decoderState) {
        this.decoderState = decoderState;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idbalanceState != null ? idbalanceState.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Balancestate)) {
            return false;
        }
        Balancestate other = (Balancestate) object;
        if ((this.idbalanceState == null && other.idbalanceState != null) || (this.idbalanceState != null && !this.idbalanceState.equals(other.idbalanceState))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "nrz.fairhandlerservice.jpa.Balancestate[ idbalanceState=" + idbalanceState + " ]";
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nrz.fairhandlerservice.jpa;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author rahimAdmin
 */
@Entity
@Table(name = "unit")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Unit.findAll", query = "SELECT u FROM Unit u")
    , @NamedQuery(name = "Unit.findByIdunite", query = "SELECT u FROM Unit u WHERE u.idunite = :idunite")
    , @NamedQuery(name = "Unit.findByAbrevation", query = "SELECT u FROM Unit u WHERE u.abrevation = :abrevation")
    , @NamedQuery(name = "Unit.findByDesignation", query = "SELECT u FROM Unit u WHERE u.designation = :designation")
    , @NamedQuery(name = "Unit.findByIsUniteReference", query = "SELECT u FROM Unit u WHERE u.isUniteReference = :isUniteReference")
    , @NamedQuery(name = "Unit.findByMultiplicateur", query = "SELECT u FROM Unit u WHERE u.multiplicateur = :multiplicateur")
    , @NamedQuery(name = "Unit.findByUnitePredecesseur", query = "SELECT u FROM Unit u WHERE u.unitePredecesseur = :unitePredecesseur")
    , @NamedQuery(name = "Unit.findByUniteSuccesseur", query = "SELECT u FROM Unit u WHERE u.uniteSuccesseur = :uniteSuccesseur")})
public class Unit implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idunite")
    private Integer idunite;
    @Column(name = "abrevation")
    private String abrevation;
    @Column(name = "designation")
    private String designation;
    @Column(name = "isUniteReference")
    private Short isUniteReference;
    @Column(name = "multiplicateur")
    private Integer multiplicateur;
    @Column(name = "unitePredecesseur")
    private Integer unitePredecesseur;
    @Column(name = "uniteSuccesseur")
    private Integer uniteSuccesseur;

    public Unit() {
    }

    public Unit(Integer idunite) {
        this.idunite = idunite;
    }

    public Integer getIdunite() {
        return idunite;
    }

    public void setIdunite(Integer idunite) {
        this.idunite = idunite;
    }

    public String getAbrevation() {
        return abrevation;
    }

    public void setAbrevation(String abrevation) {
        this.abrevation = abrevation;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public Short getIsUniteReference() {
        return isUniteReference;
    }

    public void setIsUniteReference(Short isUniteReference) {
        this.isUniteReference = isUniteReference;
    }

    public Integer getMultiplicateur() {
        return multiplicateur;
    }

    public void setMultiplicateur(Integer multiplicateur) {
        this.multiplicateur = multiplicateur;
    }

    public Integer getUnitePredecesseur() {
        return unitePredecesseur;
    }

    public void setUnitePredecesseur(Integer unitePredecesseur) {
        this.unitePredecesseur = unitePredecesseur;
    }

    public Integer getUniteSuccesseur() {
        return uniteSuccesseur;
    }

    public void setUniteSuccesseur(Integer uniteSuccesseur) {
        this.uniteSuccesseur = uniteSuccesseur;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idunite != null ? idunite.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Unit)) {
            return false;
        }
        Unit other = (Unit) object;
        if ((this.idunite == null && other.idunite != null) || (this.idunite != null && !this.idunite.equals(other.idunite))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "nrz.fairhandlerservice.jpa.Unit[ idunite=" + idunite + " ]";
    }

}

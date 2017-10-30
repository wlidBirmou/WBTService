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
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "pruduct")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Pruduct.findAll", query = "SELECT p FROM Pruduct p")
    , @NamedQuery(name = "Pruduct.findByIdArticle", query = "SELECT p FROM Pruduct p WHERE p.idArticle = :idArticle")
    , @NamedQuery(name = "Pruduct.findByAddDate", query = "SELECT p FROM Pruduct p WHERE p.addDate = :addDate")
    , @NamedQuery(name = "Pruduct.findByArticleCode", query = "SELECT p FROM Pruduct p WHERE p.articleCode = :articleCode")
    , @NamedQuery(name = "Pruduct.findByCategorie", query = "SELECT p FROM Pruduct p WHERE p.categorie = :categorie")
    , @NamedQuery(name = "Pruduct.findByDesignation", query = "SELECT p FROM Pruduct p WHERE p.designation = :designation")})
public class Pruduct implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idArticle")
    private Integer idArticle;
    @Column(name = "addDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date addDate;
    @Column(name = "articleCode")
    private String articleCode;
    @Column(name = "categorie")
    private String categorie;
    @Column(name = "designation")
    private String designation;
    @JoinColumn(name = "unite", referencedColumnName = "idunite")
    @ManyToOne
    private Unit unite;

    public Pruduct() {
    }

    public Pruduct(Integer idArticle) {
        this.idArticle = idArticle;
    }

    public Integer getIdArticle() {
        return idArticle;
    }

    public void setIdArticle(Integer idArticle) {
        this.idArticle = idArticle;
    }

    public Date getAddDate() {
        return addDate;
    }

    public void setAddDate(Date addDate) {
        this.addDate = addDate;
    }

    public String getArticleCode() {
        return articleCode;
    }

    public void setArticleCode(String articleCode) {
        this.articleCode = articleCode;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public Unit getUnite() {
        return unite;
    }

    public void setUnite(Unit unite) {
        this.unite = unite;
    }

    

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idArticle != null ? idArticle.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Pruduct)) {
            return false;
        }
        Pruduct other = (Pruduct) object;
        if ((this.idArticle == null && other.idArticle != null) || (this.idArticle != null && !this.idArticle.equals(other.idArticle))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "nrz.fairhandlerservice.jpa.Pruduct[ idArticle=" + idArticle + " ]";
    }

}

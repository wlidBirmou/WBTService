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
@Table(name = "weight")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Weight.findAll", query = "SELECT w FROM Weight w")
    , @NamedQuery(name = "Weight.findByIdWeight", query = "SELECT w FROM Weight w WHERE w.idWeight = :idWeight")
    , @NamedQuery(name = "Weight.findByFirstBalanceZero", query = "SELECT w FROM Weight w WHERE w.firstBalanceZero = :firstBalanceZero")
    , @NamedQuery(name = "Weight.findByFirstTicketDate", query = "SELECT w FROM Weight w WHERE w.firstTicketDate = :firstTicketDate")
    , @NamedQuery(name = "Weight.findByFirstTicketTime", query = "SELECT w FROM Weight w WHERE w.firstTicketTime = :firstTicketTime")
    , @NamedQuery(name = "Weight.findByGenericCode", query = "SELECT w FROM Weight w WHERE w.genericCode = :genericCode")
    , @NamedQuery(name = "Weight.findByGrossWeight", query = "SELECT w FROM Weight w WHERE w.grossWeight = :grossWeight")
    , @NamedQuery(name = "Weight.findByNetWeight", query = "SELECT w FROM Weight w WHERE w.netWeight = :netWeight")
    , @NamedQuery(name = "Weight.findByProgressiveCode", query = "SELECT w FROM Weight w WHERE w.progressiveCode = :progressiveCode")
    , @NamedQuery(name = "Weight.findByReference", query = "SELECT w FROM Weight w WHERE w.reference = :reference")
    , @NamedQuery(name = "Weight.findBySecondBalanceZero", query = "SELECT w FROM Weight w WHERE w.secondBalanceZero = :secondBalanceZero")
    , @NamedQuery(name = "Weight.findBySecondTicketDate", query = "SELECT w FROM Weight w WHERE w.secondTicketDate = :secondTicketDate")
    , @NamedQuery(name = "Weight.findBySecondTicketTime", query = "SELECT w FROM Weight w WHERE w.secondTicketTime = :secondTicketTime")
    , @NamedQuery(name = "Weight.findByTare", query = "SELECT w FROM Weight w WHERE w.tare = :tare")
    , @NamedQuery(name = "Weight.findByTareCode", query = "SELECT w FROM Weight w WHERE w.tareCode = :tareCode")
    , @NamedQuery(name = "Weight.findByType", query = "SELECT w FROM Weight w WHERE w.type = :type")})
public class Weight implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idWeight")
    private Integer idWeight;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "firstBalanceZero")
    private Double firstBalanceZero;
    @Column(name = "firstTicketDate")
    @Temporal(TemporalType.DATE)
    private Date firstTicketDate;
    @Column(name = "firstTicketTime")
    @Temporal(TemporalType.TIME)
    private Date firstTicketTime;
    @Column(name = "genericCode")
    private String genericCode;
    @Column(name = "grossWeight")
    private Double grossWeight;
    @Column(name = "netWeight")
    private Double netWeight;
    @Column(name = "progressiveCode")
    private String progressiveCode;
    @Column(name = "reference")
    private String reference;
    @Column(name = "secondBalanceZero")
    private Double secondBalanceZero;
    @Column(name = "secondTicketDate")
    @Temporal(TemporalType.DATE)
    private Date secondTicketDate;
    @Column(name = "secondTicketTime")
    @Temporal(TemporalType.TIME)
    private Date secondTicketTime;
    @Column(name = "tare")
    private Double tare;
    @Column(name = "tareCode")
    private String tareCode;
    @Column(name = "type")
    private String type;
    @JoinColumn(name = "article", referencedColumnName = "idArticle")
    @ManyToOne
    private Pruduct article;

    public Weight() {
    }

    public Weight(Integer idWeight) {
        this.idWeight = idWeight;
    }

    public Integer getIdWeight() {
        return idWeight;
    }

    public void setIdWeight(Integer idWeight) {
        this.idWeight = idWeight;
    }

    public Double getFirstBalanceZero() {
        return firstBalanceZero;
    }

    public void setFirstBalanceZero(Double firstBalanceZero) {
        this.firstBalanceZero = firstBalanceZero;
    }

    public Date getFirstTicketDate() {
        return firstTicketDate;
    }

    public void setFirstTicketDate(Date firstTicketDate) {
        this.firstTicketDate = firstTicketDate;
    }

    public Date getFirstTicketTime() {
        return firstTicketTime;
    }

    public void setFirstTicketTime(Date firstTicketTime) {
        this.firstTicketTime = firstTicketTime;
    }

    public String getGenericCode() {
        return genericCode;
    }

    public void setGenericCode(String genericCode) {
        this.genericCode = genericCode;
    }

    public Double getGrossWeight() {
        return grossWeight;
    }

    public void setGrossWeight(Double grossWeight) {
        this.grossWeight = grossWeight;
    }

    public Double getNetWeight() {
        return netWeight;
    }

    public void setNetWeight(Double netWeight) {
        this.netWeight = netWeight;
    }

    public String getProgressiveCode() {
        return progressiveCode;
    }

    public void setProgressiveCode(String progressiveCode) {
        this.progressiveCode = progressiveCode;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public Double getSecondBalanceZero() {
        return secondBalanceZero;
    }

    public void setSecondBalanceZero(Double secondBalanceZero) {
        this.secondBalanceZero = secondBalanceZero;
    }

    public Date getSecondTicketDate() {
        return secondTicketDate;
    }

    public void setSecondTicketDate(Date secondTicketDate) {
        this.secondTicketDate = secondTicketDate;
    }

    public Date getSecondTicketTime() {
        return secondTicketTime;
    }

    public void setSecondTicketTime(Date secondTicketTime) {
        this.secondTicketTime = secondTicketTime;
    }

    public Double getTare() {
        return tare;
    }

    public void setTare(Double tare) {
        this.tare = tare;
    }

    public String getTareCode() {
        return tareCode;
    }

    public void setTareCode(String tareCode) {
        this.tareCode = tareCode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Pruduct getArticle() {
        return article;
    }

    public void setArticle(Pruduct article) {
        this.article = article;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idWeight != null ? idWeight.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Weight)) {
            return false;
        }
        Weight other = (Weight) object;
        if ((this.idWeight == null && other.idWeight != null) || (this.idWeight != null && !this.idWeight.equals(other.idWeight))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "nrz.fairhandlerservice.jpa.Weight[ idWeight=" + idWeight + " ]";
    }
    
}

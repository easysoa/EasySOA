package com.axxx.dps.apv.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class PrecomptePartenaire {

    @XmlElement(nillable=false, required=true)
	private String identifiantClientPivotal;
    @XmlElement(nillable=false, required=true)
	private TypeStructure typeStructure; // enum ex. Association nat.
    @XmlElement(nillable=false, required=true)
	private String nomStructure;
    @XmlElement(nillable=false, required=true)
	private int anciennete;
    @XmlElement(nillable=false, required=true)
	private String telephone;
	private String email;
	@XmlElement(nillable=false, required=true)
	private String adresse;
	@XmlElement(nillable=false, required=true)
	private String ville;
    @XmlElement(nillable=false, required=true)
	private String cp;
    @XmlElement(nillable=false, required=true)
	private String apeNaf; //. ex. 512E
    @XmlElement(nillable=false, required=true)
	private String sirenSiret; // rule
	

    public String getAdresse() {
		return adresse;
	}
	public void setAdresse(String adresse) {
		this.adresse = adresse;
	}
	public String getVille() {
		return ville;
	}
	public void setVille(String ville) {
		this.ville = ville;
	}
	public String getNomStructure() {
		return nomStructure;
	}
	public void setNomStructure(String nomStructure) {
		this.nomStructure = nomStructure;
	}
	public String getApeNaf() {
		return apeNaf;
	}
	public void setApeNaf(String apeNaf) {
		this.apeNaf = apeNaf;
	}
	public String getSirenSiret() {
		return sirenSiret;
	}
	public void setSirenSiret(String sirenSiret) {
		this.sirenSiret = sirenSiret;
	}
	public String getCp() {
		return cp;
	}
	public void setCp(String cp) {
		this.cp = cp;
	}
	public TypeStructure getTypeStructure() {
		return typeStructure;
	}
	public void setTypeStructure(TypeStructure typeStructure) {
		this.typeStructure = typeStructure;
	}
	public String getIdentifiantClientPivotal() {
		return identifiantClientPivotal;
	}
	public void setIdentifiantClientPivotal(String identifiantClientPivotal) {
		this.identifiantClientPivotal = identifiantClientPivotal;
	}
    public int getAnciennete() {
		return anciennete;
	}
	public void setAnciennete(int anciennete) {
		this.anciennete = anciennete;
	}
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
}

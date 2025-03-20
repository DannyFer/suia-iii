package ec.gob.ambiente.suia.domain.desechosfisicos;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the provincia database table.
 * 
 */
@Entity
@Table(name="provincia", schema="public")
@NamedQuery(name="Provincia.findAll", query="SELECT p FROM Provincia p")
public class Provincia implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)	
	@Column(name="id")
	private Integer id;

	@Column(name="nombre")
	private String nombre;

	@Column(name="pais")
	private Integer pais;
	
	@Column(name="region")
	private Integer region;

	@Column(name="prefijo")
	private String prefijo;
	
	@Column(name="zona_bv")
	private Integer zonaBv;
	
	@Column(name="bloqueo")
	private Boolean bloqueo;
	
	@Column(name="extent")
	private String extent;
	
	@Column(name="idprovincia")
	private String idProvincia;
		  
	@Column(name="provincia_inec")
	private String provinciaInec;

	public Integer getId() {
		return id;
	}

	public String getNombre() {
		return nombre;
	}

	public Integer getPais() {
		return pais;
	}

	public Integer getRegion() {
		return region;
	}

	public String getPrefijo() {
		return prefijo;
	}

	public Integer getZonaBv() {
		return zonaBv;
	}

	public Boolean getBloqueo() {
		return bloqueo;
	}

	public String getExtent() {
		return extent;
	}

	public String getIdProvincia() {
		return idProvincia;
	}

	public String getProvinciaInec() {
		return provinciaInec;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public void setPais(Integer pais) {
		this.pais = pais;
	}

	public void setRegion(Integer region) {
		this.region = region;
	}

	public void setPrefijo(String prefijo) {
		this.prefijo = prefijo;
	}

	public void setZonaBv(Integer zonaBv) {
		this.zonaBv = zonaBv;
	}

	public void setBloqueo(Boolean bloqueo) {
		this.bloqueo = bloqueo;
	}

	public void setExtent(String extent) {
		this.extent = extent;
	}

	public void setIdProvincia(String idProvincia) {
		this.idProvincia = idProvincia;
	}

	public void setProvinciaInec(String provinciaInec) {
		this.provinciaInec = provinciaInec;
	}
	
	

}
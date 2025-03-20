package ec.gob.ambiente.suia.domain.desechosfisicos;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/**
 * The persistent class for the licencia_provincia database table.
 * 
 */
@Entity
@Table(name="licencia_provincia", schema="public")
@NamedQuery(name="LicenciaProvincia.findAll", query="SELECT lp FROM LicenciaProvincia lp")
public class LicenciaProvincia implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)	
	@Column(name="lprov_codigo")
	private Integer lprovCodigo;

	@ManyToOne
	@JoinColumn(name="lprov_licencia")
	private LicenciaAmbientalFisica licenciaAmbientalFisica;

	@ManyToOne
	@JoinColumn(name="lprov_provincia")
	private Provincia provincia;

	public Integer getLprovCodigo() {
		return lprovCodigo;
	}

	public LicenciaAmbientalFisica getLicenciaAmbientalFisica() {
		return licenciaAmbientalFisica;
	}

	public Provincia getProvincia() {
		return provincia;
	}

	public void setLprovCodigo(Integer lprovCodigo) {
		this.lprovCodigo = lprovCodigo;
	}

	public void setLicenciaAmbientalFisica(
			LicenciaAmbientalFisica licenciaAmbientalFisica) {
		this.licenciaAmbientalFisica = licenciaAmbientalFisica;
	}

	public void setProvincia(Provincia provincia) {
		this.provincia = provincia;
	}

	

}
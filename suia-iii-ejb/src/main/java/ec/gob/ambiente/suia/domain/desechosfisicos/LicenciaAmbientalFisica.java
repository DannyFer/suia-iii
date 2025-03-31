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
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/**
 * The persistent class for the licencia_ambiental_fisica database table.
 * 
 */
@Entity
@Table(name="licencia_ambiental_fisica", schema="public")
@NamedQuery(name="LicenciaAmbientalFisica.findAll", query="SELECT l FROM LicenciaAmbientalFisica l ")
public class LicenciaAmbientalFisica implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)	
	@Column(name="lic_secuencial")
	private Integer licSecuencial;

	@Column(name="lic_borrado")
	private Boolean licBorrado;

	@Column(name="lic_borrado_detalle")
	private String licBorradoDetalle;

	@Column(name="lic_codigo")
	private String licCodigo;

	@Temporal(TemporalType.DATE)
	@Column(name="lic_create_date")
	private Date licCreateDate;

	@Column(name="lic_create_user")
	private String licCreateUser;

	@Column(name="lic_detalle_estado")
	private String licDetalleEstado;

	@Column(name="lic_estado")
	private String licEstado;

	@Temporal(TemporalType.DATE)
	@Column(name="lic_fecha_emision")
	private Date licFechaEmision;

	@Temporal(TemporalType.DATE)
	@Column(name="lic_fecha_ingreso")
	private Date licFechaIngreso;

	@Column(name="lic_intersecta")
	private Boolean licIntersecta;

	@Column(name="lic_intersecta_descripcion")
	private String licIntersectaDescripcion;

	@Column(name="lic_nom_proponente")
	private String licNomProponente;

	@Column(name="lic_nom_proyecto")
	private String licNomProyecto;

	@Column(name="lic_num_licencia")
	private String licNumLicencia;

	@Column(name="lic_sector")
	private Long licSector;

	@Column(name="lic_shape")
	private String licShape;

	@Column(name="lic_subsector")
	private Long licSubsector;

	@Column(name="lic_tramite_centralizado")
	private Boolean licTramiteCentralizado;

	@Temporal(TemporalType.DATE)
	@Column(name="lic_update_date")
	private Date licUpdateDate;

	@Column(name="lic_update_user")
	private String licUpdateUser;
	
	//bi-directional many-to-one association to Licensing
	@OneToMany(mappedBy="licenciaAmbientalFisica", fetch = FetchType.EAGER)
	private List<Licensing> licensings;
	
	//bi-directional many-to-one association to LicenciaAmbientalFisicaDetalle
	@OneToMany(mappedBy="licenciaAmbientalFisica")
	private List<LicenciaAmbientalFisicaDetalle> licenciaAmbientalFisicaDetalles;

	public LicenciaAmbientalFisica() {
	}

	public Integer getLicSecuencial() {
		return this.licSecuencial;
	}

	public void setLicSecuencial(Integer licSecuencial) {
		this.licSecuencial = licSecuencial;
	}

	public Boolean getLicBorrado() {
		return this.licBorrado;
	}

	public void setLicBorrado(Boolean licBorrado) {
		this.licBorrado = licBorrado;
	}

	public String getLicBorradoDetalle() {
		return this.licBorradoDetalle;
	}

	public void setLicBorradoDetalle(String licBorradoDetalle) {
		this.licBorradoDetalle = licBorradoDetalle;
	}

	public String getLicCodigo() {
		return this.licCodigo;
	}

	public void setLicCodigo(String licCodigo) {
		this.licCodigo = licCodigo;
	}

	public Date getLicCreateDate() {
		return this.licCreateDate;
	}

	public void setLicCreateDate(Date licCreateDate) {
		this.licCreateDate = licCreateDate;
	}

	public String getLicCreateUser() {
		return this.licCreateUser;
	}

	public void setLicCreateUser(String licCreateUser) {
		this.licCreateUser = licCreateUser;
	}

	public String getLicDetalleEstado() {
		return this.licDetalleEstado;
	}

	public void setLicDetalleEstado(String licDetalleEstado) {
		this.licDetalleEstado = licDetalleEstado;
	}

	public String getLicEstado() {
		return this.licEstado;
	}

	public void setLicEstado(String licEstado) {
		this.licEstado = licEstado;
	}

	public Date getLicFechaEmision() {
		return this.licFechaEmision;
	}

	public void setLicFechaEmision(Date licFechaEmision) {
		this.licFechaEmision = licFechaEmision;
	}

	public Date getLicFechaIngreso() {
		return this.licFechaIngreso;
	}

	public void setLicFechaIngreso(Date licFechaIngreso) {
		this.licFechaIngreso = licFechaIngreso;
	}

	public Boolean getLicIntersecta() {
		return this.licIntersecta;
	}

	public void setLicIntersecta(Boolean licIntersecta) {
		this.licIntersecta = licIntersecta;
	}

	public String getLicIntersectaDescripcion() {
		return this.licIntersectaDescripcion;
	}

	public void setLicIntersectaDescripcion(String licIntersectaDescripcion) {
		this.licIntersectaDescripcion = licIntersectaDescripcion;
	}

	public String getLicNomProponente() {
		return this.licNomProponente;
	}

	public void setLicNomProponente(String licNomProponente) {
		this.licNomProponente = licNomProponente;
	}

	public String getLicNomProyecto() {
		return this.licNomProyecto;
	}

	public void setLicNomProyecto(String licNomProyecto) {
		this.licNomProyecto = licNomProyecto;
	}

	public String getLicNumLicencia() {
		return this.licNumLicencia;
	}

	public void setLicNumLicencia(String licNumLicencia) {
		this.licNumLicencia = licNumLicencia;
	}

	public Long getLicSector() {
		return this.licSector;
	}

	public void setLicSector(Long licSector) {
		this.licSector = licSector;
	}

	public String getLicShape() {
		return this.licShape;
	}

	public void setLicShape(String licShape) {
		this.licShape = licShape;
	}

	public Long getLicSubsector() {
		return this.licSubsector;
	}

	public void setLicSubsector(Long licSubsector) {
		this.licSubsector = licSubsector;
	}

	public Boolean getLicTramiteCentralizado() {
		return this.licTramiteCentralizado;
	}

	public void setLicTramiteCentralizado(Boolean licTramiteCentralizado) {
		this.licTramiteCentralizado = licTramiteCentralizado;
	}

	public Date getLicUpdateDate() {
		return this.licUpdateDate;
	}

	public void setLicUpdateDate(Date licUpdateDate) {
		this.licUpdateDate = licUpdateDate;
	}

	public String getLicUpdateUser() {
		return this.licUpdateUser;
	}

	public void setLicUpdateUser(String licUpdateUser) {
		this.licUpdateUser = licUpdateUser;
	}	
	
	public List<Licensing> getLicensings() {
		return licensings;
	}

	public void setLicensings(List<Licensing> licensings) {
		this.licensings = licensings;
	}

	public Licensing addLicensing(Licensing licensing) {
		getLicensings().add(licensing);
		licensing.setLicenciaAmbientalFisica(this);

		return licensing;
	}

	public Licensing removeLicensing(Licensing licensing) {
		getLicensings().remove(licensing);
		licensing.setLicenciaAmbientalFisica(null);

		return licensing;
	}
	
	public List<LicenciaAmbientalFisicaDetalle> getLicenciaAmbientalFisicaDetalles() {
		return this.licenciaAmbientalFisicaDetalles;
	}

	public void setLicenciaAmbientalFisicaDetalles(List<LicenciaAmbientalFisicaDetalle> licenciaAmbientalFisicaDetalles) {
		this.licenciaAmbientalFisicaDetalles = licenciaAmbientalFisicaDetalles;
	}

	public LicenciaAmbientalFisicaDetalle addLicenciaAmbientalFisicaDetalle(LicenciaAmbientalFisicaDetalle licenciaAmbientalFisicaDetalle) {
		getLicenciaAmbientalFisicaDetalles().add(licenciaAmbientalFisicaDetalle);
		licenciaAmbientalFisicaDetalle.setLicenciaAmbientalFisica(this);

		return licenciaAmbientalFisicaDetalle;
	}

	public LicenciaAmbientalFisicaDetalle removeLicenciaAmbientalFisicaDetalle(LicenciaAmbientalFisicaDetalle licenciaAmbientalFisicaDetalle) {
		getLicenciaAmbientalFisicaDetalles().remove(licenciaAmbientalFisicaDetalle);
		licenciaAmbientalFisicaDetalle.setLicenciaAmbientalFisica(null);

		return licenciaAmbientalFisicaDetalle;
	}

}
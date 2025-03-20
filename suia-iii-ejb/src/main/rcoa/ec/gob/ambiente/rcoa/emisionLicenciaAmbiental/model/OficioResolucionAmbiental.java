package ec.gob.ambiente.rcoa.emisionLicenciaAmbiental.model;

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.rcoa.model.CatalogoGeneralCoa;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;


/**
 * <b> MODEL. </b>
 *
 * @author Luis Lema
 * @version Revision: 1.0
 */
@Entity
@Table(name="trades_environmental_resolution", schema = "coa_emission_environmental_resolution")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "tren_status")),	
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "tren_creator_user")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "tren_creation_date")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "tren_user_update")), 
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "tren_date_update"))
})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "tren_status = 'TRUE'")

@Getter
@Setter
public class OficioResolucionAmbiental extends EntidadAuditable {
	
	private static final long serialVersionUID = 1L;	

	@Id
	@SequenceGenerator(name = "trades_environmental_resolution_tren_id_seq", sequenceName = "trades_environmental_resolution_tren_id_seq", 
	schema = "coa_emission_environmental_resolution", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "trades_environmental_resolution_tren_id_seq")
	@Setter
	@Getter
	@Column(name = "tren_id")
	private Integer id;	

	@Setter
	@Getter
	@ManyToOne
	@JoinColumn(name = "enre_id")
	//@ForeignKey(name = "fk_enre_id")
	private ResolucionAmbiental resolucionAmbiental;

	@Setter
	@Getter
	@Column(name="tren_date_status")
	@Temporal(TemporalType.TIMESTAMP)
	private Date fechaEstado;
	
	@Setter
	@Getter
	@Column(name="tren_affair")
	private String asuntoOficio;
	
	@Setter
	@Getter
	@Column(name="tren_pronouncement")
	private String pronunciamientoOficio;
	
	@Setter
	@Getter
	@Column(name="tren_status_approval")
	private Boolean estadoAprobacion;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="geca_id")
    private CatalogoGeneralCoa tipoUsuarioAlmacena;
	
	@Setter
	@Getter
	@Column(name="tren_considering")
	private String considerando;
	
	@Setter
	@Getter
	@Column(name="tren_solve")
	private String resuelve;
	
	@Setter
	@Getter
	@Column(name="tren_legal_considering")
	private String considerandoLegal;
	
	@Getter
	@Setter
	@Transient
	private byte[] archivoInforme;
	
	// Para la resolucion ambiental
	@Getter
	@Setter
	@Column(name="tren_code_of_report")
	private String codigoReporte;
	
	@Getter
	@Setter
	@Column(name="tren_obligations ")
	private String obligaciones;
	
	@Getter
	@Setter
	@Column(name="tren_date_report")
	private Date fechaReporte;
	
	@Getter
	@Setter
	@Transient
	private String nombreAutoridad;
	@Getter
	@Setter
	@Transient
	private String cargoAutoridad;

	
	@Getter
	@Setter
	@Transient
	private String informePath;
	
	@Getter
	@Setter
	@Transient
	private String nombreFichero;
	
	@Getter
	@Setter
	@Transient
	private Date fechaActual = new Date();
	
	@Getter
	@Setter
	@Transient
	private String fecha;
	
	@Getter
	@Setter
	@Transient
	private String ubicacion;
	
	@Getter
	@Setter
	@Transient
	private Area areaFirma;
	
	@Getter
	@Setter
	@Transient
	private String unidadEnte;
	
	@Getter
	@Setter
	@Transient
	private String enteResponsable;
	
	@Getter
	@Setter
	@Transient
	private String mostrarUnidad;
	
	@Getter
	@Setter
	@Transient
	private String nombreProyecto;
	
}
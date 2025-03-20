package ec.gob.ambiente.suia.domain;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 *
 * <b> Entity que representa la tabla certificado interseccion. </b>
 *
 * @author 
 * @version Revision: 1.0
 * <p>
 * [Autor: pganan, Fecha: 22/12/2014]
 * </p>
 */
@Entity
@Table(name = "certificate_intersection", schema = "suia_iii")
@NamedQueries({
	@NamedQuery(name = CertificadoInterseccion.GET_CERTIFICADO_INTERSECCION, query = "Select ci from CertificadoInterseccion ci where ci.proyectoLicenciamientoAmbiental=:proyecto"),
	@NamedQuery(name = CertificadoInterseccion.GET_CERTIFICADO_INTERSECCION_ID_PROYECTO, query = "Select ci from CertificadoInterseccion ci where ci.proyectoLicenciamientoAmbiental.id=:idProyecto"),
	@NamedQuery(name = CertificadoInterseccion.GET_CERTIFICADO_INTERSECCION_ACTUALIZACION, query = "Select ci from CertificadoInterseccion ci where ci.proyectoLicenciamientoAmbiental.id=:idProyecto and ci.nroActualizacion =:nroActualizacion"),
	@NamedQuery(name = CertificadoInterseccion.GET_CERTIFICADO_INTERSECCION_CODIGO, query = "Select ci from CertificadoInterseccion ci where ci.codigo=:codigo"),
    @NamedQuery(name = CertificadoInterseccion.GET_CI_ACTUALIZACION_SUIA_VERDE, query = "Select ci from CertificadoInterseccion ci where ci.idCuatroCategorias=:idProyecto and ci.nroActualizacion =:nroActualizacion"),
})

@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "cein_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "cein_creation_date")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "cein_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "cein_creator_user")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "cein_user_update"))})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "cein_status = 'TRUE'")
public class CertificadoInterseccion extends EntidadAuditable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8042825496431796923L;



	public static final String GET_CERTIFICADO_INTERSECCION = "ec.com.magmasoft.business.domain.CertificadoInterseccion.getCertificadoInterseccion";
	public static final String GET_CERTIFICADO_INTERSECCION_ID_PROYECTO = "ec.com.magmasoft.business.domain.CertificadoInterseccion.getCertificadoInterseccionIdProyecto";
	public static final String GET_CERTIFICADO_INTERSECCION_ACTUALIZACION = "ec.com.magmasoft.business.domain.CertificadoInterseccion.getCertificadoInterseccionActualizacion";
	public static final String GET_CERTIFICADO_INTERSECCION_CODIGO = "ec.com.magmasoft.business.domain.CertificadoInterseccion.getCertificadoInterseccionCodigo";
	public static final String GET_CI_ACTUALIZACION_SUIA_VERDE = "ec.com.magmasoft.business.domain.CertificadoInterseccion.getCIActualizacionSuiaVerde";
	

    @Getter
    @Setter
    @Column(name = "cein_id")
    @Id
    @SequenceGenerator(name = "CERTIFIACDOINTERSECCION_GENERATOR", initialValue = 1, sequenceName = "seq_cein_id", schema = "suia_iii")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CERTIFIACDOINTERSECCION_GENERATOR")
    private Integer id;

    @Getter
    @Setter
    @Column(name = "cein_codigo")
    private String codigo;
    
    @Getter
    @Setter
    @JoinColumn(name = "pren_id", referencedColumnName = "pren_id")
    @ManyToOne(fetch = FetchType.LAZY)
    @ForeignKey(name = "fk_certificate_intersection_pren_id_projects_environmental_lice_pren_id")
    private ProyectoLicenciamientoAmbiental proyectoLicenciamientoAmbiental;

    @Getter
    @Setter
    @Column(name = "cein_update_number")
    private Integer nroActualizacion;
    
    @Getter
    @Setter
    @Column(name = "cein_user_sign")
    private String usuarioFirma;
    
    @Getter
    @Setter
    @Column(name = "area_id")
    private Integer areaUsuarioFirma;
    
    @Getter
    @Setter
    @Column(name = "cein_url_code_validation")
    private String urlCodigoValidacion;

    @Getter
	@Setter
	@Column(name = "id_4_cat")
	private String idCuatroCategorias;
    
}

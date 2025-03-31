package ec.gob.ambiente.rcoa.certificado.interseccion.model;

import java.io.Serializable;
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
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;

@Entity
@Table(name="certificate_intersection_coa", schema = "coa_mae")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "cein_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "cein_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "cein_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "cein_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "cein_user_update")) })
public class CertificadoInterseccionOficioCoa extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;	
	
	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="cein_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="cein_code")
	private String codigo;
	
	@ManyToOne
	@JoinColumn(name = "prco_id")	
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyectoLicenciaCoa;
	
	@Getter
	@Setter
	@Column(name="cein_location")
	private String ubicacion;
	
	@Getter
	@Setter
	@Column(name="cein_layers")
	private String capas;
	
	@Getter
	@Setter
	@Column(name="cein_viability_intersection")
	private String interseccionViabilidad;
	
	@Getter
	@Setter
	@Column(name="cein_other_intersection")
	private String otraInterseccion;
	
	@Getter
	@Setter
	@Column(name="cein_trade_date")
	private Date fechaOficio;
	
	@Getter
	@Setter
	@Column(name="cein_task_id")
	private Long idTarea;
	
	@Getter
	@Setter
	@Column(name="cein_update_number")
	private Integer nroActualizacion = 0;
	
    @Getter
    @Setter
    @Column(name = "cein_url_code_validation")
    private String urlCodigoValidacion;
    
    @Getter
    @Setter
    @Column(name = "cein_user_sign")
    private String usuarioFirma;
    
    @Getter
    @Setter
    @Column(name = "area_id")
    private Integer areaUsuarioFirma;
	
}
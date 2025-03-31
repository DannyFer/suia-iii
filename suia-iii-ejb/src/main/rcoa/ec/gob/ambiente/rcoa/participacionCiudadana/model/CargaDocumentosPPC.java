package ec.gob.ambiente.rcoa.participacionCiudadana.model;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.rcoa.model.CatalogoGeneralCoa;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.suia.domain.TipoDocumento;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "load_reports_ppc", schema = "coa_citizen_participation")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "lorp_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "lorp_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "lorp_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "lorp_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "lorp_user_update")) 
})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "lorp_status = 'TRUE'")

public class CargaDocumentosPPC extends EntidadAuditable{
	
	private static final long serialVersionUID = 1801511545851715084L;

	@Getter
	@Setter
	@Id
	@Column(name = "lorp_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="prfa_id")
    private ProyectoFacilitadorPPC proyectoFacilitadorPPC;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="geca_id")
    private CatalogoGeneralCoa tipoReporte;
	
	@Getter
	@Setter
	@Column(name="lorp_observation")
	private Boolean tieneObservaciones;
	
	@Getter
	@Setter
	@Column(name="lorp_status_review")
	private Boolean revisadoArchivoAdjunto;
	
	@Getter
	@Setter
	@Column(name="lorp_complementary_actions")
	private Boolean requiereAccionesComplementarias;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="lorp_type_user_uploads")
    private CatalogoGeneralCoa tipoUsuario;

}

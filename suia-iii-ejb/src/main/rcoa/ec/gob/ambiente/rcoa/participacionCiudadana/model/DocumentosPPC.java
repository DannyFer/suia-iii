package ec.gob.ambiente.rcoa.participacionCiudadana.model;

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
import javax.persistence.Transient;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.suia.domain.TipoDocumento;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "documents_ppc", schema = "coa_citizen_participation")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "docu_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "docu_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "docu_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "docu_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "docu_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "docu_status = 'TRUE'")
//@NamedQuery(name=DocumentosPPC.GET_POR_TIPO_Y_ID, query="SELECT d FROM DocumentosCOA d where d.tipoDocumento.id = :idTipo and d.idTabla = :id and d.estado = true order by d.id DESC")})
public class DocumentosPPC extends EntidadAuditable{
	
	private static final long serialVersionUID = 1801511545851715084L;

	@Getter
	@Setter
	@Id
	@Column(name = "docu_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	/*@ManyToOne
	@JoinColumn(name = "prco_id")
	@ForeignKey(name = "fk_prco_id")
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyectoLicenciaCoa;*/
	
	@ManyToOne
	@JoinColumn(name = "doty_id")
	@ForeignKey(name = "doty_id")
	@Getter
	@Setter
	private TipoDocumento tipoDocumento;
	
	@Getter
	@Setter
	@Column(name = "docu_name", length = 255)
	private String nombreDocumento;
	
	@Getter
	@Setter
	@Column(name = "docu_mime", length = 255)
	private String tipo;

	@Getter
	@Setter
	@Column(name = "docu_extesion", length = 255)
	private String extencionDocumento;
	
	@Getter
	@Setter
	@Column(name = "docu_alfresco_id", length = 255)
	private String idAlfresco;
	
	@Getter
	@Setter
	@Column(name = "docu_table_id")
	private Integer idTabla;
	
	@Getter
	@Setter
	@Column(name = "docu_description")
	private String descripcion;
	
	@Getter
	@Setter
	@Column(name = "docu_table_class")
	private String nombreTabla;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "prfa_id")
	private ProyectoFacilitadorPPC proyectoFacilitadorPPC;
	
	@Getter
	@Setter
	@Transient
	private byte[] contenidoDocumento;
	
	@Getter
	@Setter
	@Column(name="docu_process_instance_id")
	private Long idProceso;

}

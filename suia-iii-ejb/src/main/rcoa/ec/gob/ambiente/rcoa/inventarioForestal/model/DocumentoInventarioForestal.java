package ec.gob.ambiente.rcoa.inventarioForestal.model;

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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.TipoDocumento;
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
@Table(name="documents_description_coa_forest_inventory", schema = "coa_forest_inventory")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "dofi_status")),	
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "dofi_creator_user")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "dofi_creation_date")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "dofi_user_update")), 
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "dofi_date_update"))
})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "dofi_status = 'TRUE'")

@Getter
@Setter
public class DocumentoInventarioForestal extends EntidadAuditable {
	
	private static final long serialVersionUID = 1L;	

	@Id
	@Column(name = "dofi_id")
	@SequenceGenerator(name = "DOCUMENT_DESCRIPTION_FOREST_INVENTORY_GENERATOR", sequenceName = "documents_description_coa_forest_inventory_dofi_id_seq", schema = "coa_forest_inventory", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DOCUMENT_DESCRIPTION_FOREST_INVENTORY_GENERATOR")
	private Integer id;	

	@Getter
	@Setter
	@Column(name="dofi_name")
	private String nombreDocumento;
	
	@Getter
	@Setter
	@Column(name="dofi_mime")
	private String mimeDocumento;
	
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="doty_id")
	private TipoDocumento tipoDocumento;
	
	@Getter
	@Setter
	@Column(name="dofi_alfresco_id")
	private String idAlfresco;
	
	@Getter
	@Setter
	@Column(name="dofi_extesion")
	private String extencionDocumento;

	@Getter
	@Setter
	@JoinColumn(name = "refi_id", referencedColumnName = "refi_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private ReporteInventarioForestal reporteInventarioForestal;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="foin_id")
	@ForeignKey(name = "fk_dofi_foin")
	private InventarioForestalAmbiental inventarioForestalAmbiental;
	
	@Getter
	@Setter
	@Column(name="dofi_type")
	private String tipoUsuarioAlmacena;
	
	@Getter
	@Setter
	@Column(name="dofi_table_id")
	private Integer idTabla;
	
	@Getter
	@Setter
	@Column(name="dofi_description")
	private String descripcionTabla;
	
	@Getter
	@Setter
	@Column(name="dofi_table_class")
	private String nombreTabla;
	
	@Getter
	@Setter
	@Column(name="dofi_code_public")
	private String codigoPublico;
	
	@Getter
	@Setter
	@Column(name = "dofi_process_instance_id")
	private Long idProceso;
	
	@Getter
	@Setter
	@Transient
	private byte[] contenidoDocumento;

}
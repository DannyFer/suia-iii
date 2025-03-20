package ec.gob.ambiente.rcoa.model;

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

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "project_licencing_coa_document", schema = "coa_mae")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "prdo_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "prdo_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "prdo_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "prdo_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "prdo_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "prdo_status = 'TRUE'")
public class ProyectoLicenciaCoaDocumento extends EntidadAuditable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4437806709989668889L;

	@Getter
	@Setter
	@Id
	@Column(name = "prdo_id")
	@SequenceGenerator(name = "PROJECTS_ENVIRONMENTAL_LICENSING_PRDO_ID_GENERATOR", sequenceName = "project_licencing_coa_document_prdo_id_seq", schema = "coa_mae", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PROJECTS_ENVIRONMENTAL_LICENSING_PRDO_ID_GENERATOR")
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name = "docu_id")
	@ForeignKey(name = "fk_projects_document_docu_id_documents")
	@Getter
	@Setter
	private DocumentosCOA documentosCOA;
	
	@ManyToOne
	@JoinColumn(name = "prli_id")
	@ForeignKey(name = "prli_id")
	@Getter
	@Setter
	private ProyectoLicenciaCuaCiuu proyectoLicenciaCuaCiuu;
	
	@Getter
	@Setter
	@Column(name = "prdo_observation_bd", length = 255)
	private String observacionDB;
	
	
}

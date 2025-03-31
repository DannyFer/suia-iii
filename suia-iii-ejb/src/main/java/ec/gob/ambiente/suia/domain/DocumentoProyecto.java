package ec.gob.ambiente.suia.domain;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
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
 * <b> Entity que representa la tabla projects_document. </b>
 * 
 * @author Javier Lucero
 * @version Revision: 1.0
 *          <p>
 *          [Autor: Javier Lucero, Fecha: 07/01/2015]
 *          </p>
 */
@NamedQueries({ @NamedQuery(name = DocumentoProyecto.GET_ALL, query = "SELECT m FROM DocumentoProyecto m") })
@Entity
@Table(name = "projects_document", schema = "suia_iii")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "prdo_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "prdo_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "prdo_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "prdo_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "prdo_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "prdo_status = 'TRUE'")
public class DocumentoProyecto extends EntidadAuditable {

	public static final String GET_ALL = "ec.com.magmasoft.business.domain.DocumentoProyecto.getAll";

	private static final long serialVersionUID = -1948131919746177457L;

	@Id
	@SequenceGenerator(name = "PROJECTS_DOCUMENT_PRDOID_GENERATOR", sequenceName = "fapma_article_faar_id_seq", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PROJECTS_DOCUMENT_PRDOID_GENERATOR")
	@Column(name = "prdo_id")
	@Getter
	@Setter
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name = "docu_id")
	@ForeignKey(name = "fk_projects_document_docu_id_documents")
        @Getter
	@Setter
	private Documento documento;
	
	@ManyToOne
	@JoinColumn(name = "pren_id")
	@ForeignKey(name = "fk_projects_document_pren_id_projects_environmental_licensing")
        @Getter
	@Setter
	private ProyectoLicenciamientoAmbiental proyectoLicenciamientoAmbiental;

}
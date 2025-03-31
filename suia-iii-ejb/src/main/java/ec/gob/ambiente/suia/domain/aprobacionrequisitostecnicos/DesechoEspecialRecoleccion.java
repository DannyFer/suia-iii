/*
 * Copyright 2015 MAGMASOFT
 * Todos los derechos reservados
 */
package ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos;

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
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * <b> Clase entidad que mapea la recepcion de los desechos especiales. </b>
 * 
 * @author Javier Lucero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: Javier Lucero $, $Date: 02/07/2015 $]
 *          </p>
 */
@Entity
@Table(name = "waste_special_collection", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "wasc_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "wasc_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "wasc_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "wasc_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "wasc_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "wasc_status = 'TRUE'")
@NamedQueries({
		@NamedQuery(name = DesechoEspecialRecoleccion.LISTAR_POR_APROBACION_REQUISITOS_TECNICOS, query = "SELECT d FROM DesechoEspecialRecoleccion d WHERE d.idAprobacionRequisitosTecnicos = :idAprobacionRequisitosTecnicos AND d.estado = TRUE"),
		@NamedQuery(name = DesechoEspecialRecoleccion.OBTENER_DESECHO_POR_PROYECTO, query = "SELECT d FROM DesechoEspecialRecoleccion d WHERE d.idDesecho = :idDesecho AND d.idAprobacionRequisitosTecnicos = :idAprobacionRequisitosTecnicos AND d.estado = TRUE") })
public class DesechoEspecialRecoleccion extends EntidadAuditable {

	private static final long serialVersionUID = 1L;
	private static final String PAQUETE_CLASE = "ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.DesechoEspecialRecoleccion.";
	public static final String LISTAR_POR_APROBACION_REQUISITOS_TECNICOS = PAQUETE_CLASE
			+ "obtenerPorAprobacionRequisitosTecnicos";
	public static final String OBTENER_DESECHO_POR_PROYECTO = PAQUETE_CLASE + "obtenerDesechoPorProyecto";

	@Id
	@Column(name = "wasc_id")
	@SequenceGenerator(name = "WASTE_SPECIAL_COLLECTION_GENERATOR", sequenceName = "seq_wasc_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "WASTE_SPECIAL_COLLECTION_GENERATOR")
	@Getter
	@Setter
	private Integer id;

	@Getter
	@Setter
	@ManyToOne()
	@JoinColumn(name = "apte_id")
	@ForeignKey(name = "fk_waste_dangerous_transportation_id_approval_requirements_apte_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "apte_status = 'TRUE'")
	private AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos;

	@Getter
	@Setter
	@Column(name = "apte_id", insertable = false, updatable = false)
	private Integer idAprobacionRequisitosTecnicos;

	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "wada_id")
	@ForeignKey(name = "waste_dangerous_waste_waste_special_collection_fk")
	private DesechoPeligroso desechoPeligroso;

	@Getter
	@Setter
	@Column(name = "wada_id", insertable = false, updatable = false)
	private Integer idDesecho;

	@Column(name = "wasc_description_mechanic")
	@Getter
	@Setter
	private String descripcionMecanica;

	@Column(name = "wasc_description_cooling")
	@Getter
	@Setter
	private String descripcionEnfriamiento;

	@Column(name = "wasc_description_catchment")
	@Getter
	@Setter
	private String descripcionCaptacion;

	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "reve_mechanic_id")
	@LazyCollection(LazyCollectionOption.FALSE)
	@ForeignKey(name = "fk_waste_special_collection_mechanic_id_documents_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "docu_status = 'TRUE'")
	private Documento documentoMecanica;

	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "reve_cooling_id")
	@LazyCollection(LazyCollectionOption.FALSE)
	@ForeignKey(name = "fk_waste_special_collection_cooling_id_documents_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "docu_status = 'TRUE'")
	private Documento documentoEnfriamiento;

	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "reve_catchment_id")
	@LazyCollection(LazyCollectionOption.FALSE)
	@ForeignKey(name = "fk_waste_special_collection_catchment_id_documents_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "docu_status = 'TRUE'")
	private Documento documentoCaptacion;
	

}

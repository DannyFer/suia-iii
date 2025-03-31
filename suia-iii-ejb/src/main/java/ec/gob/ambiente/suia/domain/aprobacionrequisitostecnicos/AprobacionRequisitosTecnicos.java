/*
 * Copyright 2015 MAGMASOFT
 * Todos los derechos reservados
 */
package ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.InformeTecnicoAproReqTec;
import ec.gob.ambiente.suia.domain.ModalidadGestionDesechos;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * <b> Clase que representa a la tabla del proceso aprobación de requisitos
 * técnicos. </b>
 * 
 * @author vero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: vero $, $Date: 11/06/2015 $]
 *          </p>
 */
@Entity
@Table(name = "approval_technical_requirements", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "apte_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "apte_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "apte_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "apte_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "apte_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "apte_status = 'TRUE'")
public class AprobacionRequisitosTecnicos extends EntidadAuditable {

	private static final int VALOR_EX_POST_WS = 2;
	public static final String VARIABLE_NUMERO_SOLICITUD = "numeroSolicitud";

	private static final int VALOR_EX_ANTE_WS = 1;

	/**
     *
     */
	private static final long serialVersionUID = 939396203107902628L;

	@Id
	@Column(name = "apte_id")
	@SequenceGenerator(name = "APPROVAL_TECHNICAL_REQUIREMENTS_GENERATOR", sequenceName = "seq_apte_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "APPROVAL_TECHNICAL_REQUIREMENTS_GENERATOR")
	@Getter
	@Setter
	private Integer id;

	@Column(name = "apte_proyect")
	@Getter
	@Setter
	private String proyecto;

	@Column(name = "apte_started_of_necessity")
	@Getter
	@Setter
	private boolean iniciadoPorNecesidad;

	@Column(name = "apte_transport")
	@Getter
	@Setter
	private boolean transporte;

	@Column(name = "apte_management")
	@Getter
	@Setter
	private boolean gestion;

	@Column(name = "apte_studio_type")
	@Getter
	@Setter
	private Integer tipoEstudio;

	@Column(name = "apte_name_proyect")
	@Getter
	@Setter
	private String nombreProyecto;

	@Getter
	@Setter
	@OneToMany(mappedBy = "aprobacionRequisitosTecnicos")
	@LazyCollection(LazyCollectionOption.FALSE)
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "atrm_status = 'TRUE'")
	private List<AprobacionRequisitosTecnicosModalidad> aprobacionModalidades;

	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "area_id", referencedColumnName = "area_id")
	@ForeignKey(name = "fk_approval_technical_requirements_area_id_areas_area_id")
	private Area areaResponsable;

	@Getter
	@Setter
	@OneToMany(mappedBy = "aprobacionRequisitosTecnicos")
	@LazyCollection(LazyCollectionOption.FALSE)
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "trart_status = 'TRUE'")
	private List<InformeTecnicoAproReqTec> informeTecnicoAproReqTecs;

	@OneToMany(mappedBy = "aprobacionRequisitosTecnicos")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "apte_status = 'TRUE'")
	@Getter
	@Setter
	private List<RequisitosConductor> requisitosConductors;

	@ManyToOne
	@JoinColumn(name = "user_id")
	@ForeignKey(name = "fk_approval_technical_requirements_id_usersuser_id")
	@Getter
	@Setter
	private Usuario usuario;

	@Column(name = "apte_another_mode")
	@Getter
	@Setter
	private String otraModalidad;

	@Column(name = "apte_confidential_information")
	@Getter
	@Setter
	private Boolean informacionConfidencial;

	@Column(name = "apte_physical_license")
	@Getter
	@Setter
	private Boolean tieneLicenciaFisica;

	@Column(name = "apte_date_proyect")
	@Getter
	@Setter
	private Date fechaProyecto;

	@ManyToOne
	@JoinColumn(name = "gelo_id")
	@ForeignKey(name = "fk_approval_technical_reqapte_id_geographical_locationsgelo_id")
	@Getter
	@Setter
	private UbicacionesGeografica provincia;

	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "docu_id")
	@LazyCollection(LazyCollectionOption.FALSE)
	@ForeignKey(name = "fk_approval_technical_requirements_docu_id_documentsdocu_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "docu_status = 'TRUE'")
	private Documento documentoLicenciaFisica;

	@Getter
	@Setter
	@Column(name = "apte_request")
	private String solicitud;
	
	@Getter
	@Setter
	@Column(name = "apte_delete_reason")
	private String motivoEliminacion;

	@Column(name = "apte_voluntary")
	@Getter
	@Setter
	private Boolean voluntario;
	
	@Getter
	@Setter
	@Transient
	private Integer numDiasParaDesactivacion;
	
	@Getter
	@Setter
	@Column(name = "apte_is_self_management")
	private Boolean esGestionPropia;
	public List<ModalidadGestionDesechos> getModalidades() {
		List<ModalidadGestionDesechos> modalidades = new ArrayList<ModalidadGestionDesechos>();
		List<AprobacionRequisitosTecnicosModalidad> aproMod = getAprobacionModalidades();
		for (AprobacionRequisitosTecnicosModalidad aprobacionRequisitosTecnicosModalidad : aproMod) {
			modalidades.add(aprobacionRequisitosTecnicosModalidad.getModalidad());
		}
		return modalidades;
	}

	public boolean isGestionConTransporte() {
		if (getModalidades() != null) {
			for (ModalidadGestionDesechos modalidad : getModalidades()) {
				if (modalidad.getId().equals(ModalidadGestionDesechos.ID_MODALIDAD_TRANSPORTE)) {
					return true;
				}
			}
			return false;
		} else {
			return false;
		}
	}

	public boolean isGestionSoloConTransporte() {
		return (isGestionConTransporte() && (getModalidades().size() == 1));
	}

	public boolean isProyectoExAnte() {
		return tipoEstudio.equals(VALOR_EX_ANTE_WS);
	}

	public boolean isProyectoExPost() {
		return tipoEstudio.equals(VALOR_EX_POST_WS);
	}

	public boolean isGestionModalidadOtros() {
		if (getModalidades() != null) {
			for (ModalidadGestionDesechos modalidad : getModalidades()) {
				if (modalidad.getId().equals(ModalidadGestionDesechos.ID_OTROS)) {
					return true;
				}
			}
			return false;
		} else {
			return false;
		}
	}

	public boolean isGestionSoloModalidadOtros() {
		return (isGestionModalidadOtros() && (getModalidades().size() == 1));
	}

	public boolean isGestionSoloConModalidadOtrosYTransporte() {
		return (isGestionModalidadOtros() && (getModalidades().size() == 2) && isGestionConTransporte());
	}

}

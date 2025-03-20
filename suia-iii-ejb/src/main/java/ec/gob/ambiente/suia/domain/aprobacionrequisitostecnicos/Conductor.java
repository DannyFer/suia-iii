package ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos;

/*
 * Copyright 2015 MAGMASOFT
 * Todos los derechos reservados
 */

import java.util.Date;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * <b> Clase entidad de los requisitos del conductor. </b>
 * 
 * @author Javier Lucero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: Javier Lucero $, $Date: 04/06/2015 $]
 *          </p>
 */
@Entity
@NamedQueries(@NamedQuery(name = Conductor.OBTENER_CONDUCTOR_CEDULA, query = "SELECT c FROM Conductor c WHERE c.documento = :documento and c.estadoPermiso='VIGENTE'"))
@Table(name = "qualified_drivers", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "qudr_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "qudr_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "qudr_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "qudr_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "qudr_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "qudr_status = 'TRUE'")

public class Conductor extends EntidadAuditable {

	/**
	* 
	*/
	private static final String ESTADO_CADUCADO = "CADUCADO";

	/**
	 * 
	 */
	private static final long serialVersionUID = 4357256048649563613L;

	public static final String PAQUETE_DOMAIN = "ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.Conductor";
	public static final String OBTENER_CONDUCTOR_CEDULA = PAQUETE_DOMAIN + ".obtenerConductorPorCedula";

	@Id
	@Column(name = "qudr_id")
	@SequenceGenerator(name = "QUALIFIED_DRIVERS_GENERATOR", sequenceName = "seq_qudr_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "QUALIFIED_DRIVERS_GENERATOR")
	@Getter
	@Setter
	private Integer id;

	@Getter
	@Setter
	@Column(name = "qudr_year")
	private Integer anio;

	@Getter
	@Setter
	@Column(name = "qudr_emision_date")
	private Date emision;

	@Getter
	@Setter
	@Column(name = "qudr_validity_date")
	private Date vigencia;

	@Getter
	@Setter
	@Column(name = "gelo_id")
	private Integer idCiudad;

	@Getter
	@Setter
	@Column(name = "qudr_name")
	private String nombre;

	@Getter
	@Setter
	@Column(name = "qudr_pin")
	private String documento;

	@Getter
	@Setter
	@Column(name = "qudr_orga_name")
	private String empresa;

	@Getter
	@Setter
	@Column(name = "qudr_certification_code")
	private String codigoCurso;

	@Getter
	@Setter
	@Column(name = "qudr_permision_status")
	private String estadoPermiso;

	@Getter
	@Setter
	@Column(name = "gelo_name")
	private String ciudad;

	@Getter
	@Setter
	@Column(name = "qudr_observation")
	private String observacion;
	
	@OneToMany(mappedBy = "conductor")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "qudr_status = 'TRUE'")
	@Getter
	@Setter
	private List<RequisitosConductor> requisitosConductors;

	public boolean isPermisoCaducado() {
		if (getEstadoPermiso().equals(ESTADO_CADUCADO)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Conductor) {
			Conductor otroConductor = (Conductor) obj;
			if (this.getDocumento().equals(otroConductor.getDocumento()))
				return true;
		}
		return false;
	}

}

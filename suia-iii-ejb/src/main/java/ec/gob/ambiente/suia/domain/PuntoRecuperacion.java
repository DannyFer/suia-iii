/*
 * Copyright 2014 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.domain;

import java.util.Arrays;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
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

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * 
 * <b> Entity Bean. </b>
 * 
 * @author Carlos Pupo
 * @version Revision: 1.0
 *          <p>
 *          [Autor: Carlos Pupo, Fecha: Jun 08, 2015]
 *          </p>
 */
@Entity
@Table(name = "recovery_points", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "repo_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "repo_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "repo_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "repo_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "repo_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "repo_status = 'TRUE'")
public class PuntoRecuperacion extends EntidadAuditable {

	private static final long serialVersionUID = 9217733078355045389L;

	@Getter
	@Setter
	@Id
	@SequenceGenerator(name = "RECOVERY_POINTS_REPO_ID_GENERATOR", sequenceName = "seq_repo_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RECOVERY_POINTS_REPO_ID_GENERATOR")
	@Column(name = "repo_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "repo_name")
	private String nombre;
	
	@Getter
	@Setter
	@Column(name = "repo_phone")
	private String telefono;
	
	@Getter
	@Setter
	@Column(name = "repo_email")
	private String correo;
	
	@Getter
	@Setter
	@Column(name = "repo_address")
	private String direccion;
	
	@ManyToOne
	@JoinColumn(name = "hwge_id")
	@ForeignKey(name = "fk_repo_id_hwge_id")
	@Getter
	@Setter
	private GeneradorDesechosPeligrosos generadorDesechosPeligrosos;
	
	@ManyToOne
	@JoinColumn(name = "docu_cuf_id")
	@ForeignKey(name = "fk_repo_id_documentsdocu_cuf_id")
	@Getter
	private Documento certificadoUsoSuelos;

	public void setCertificadoUsoSuelos(Documento certificadoUsoSuelos){

		if ((certificadoUsoSuelos==null && this.certificadoUsoSuelos != null) ||
				(this.certificadoUsoSuelos==null && certificadoUsoSuelos != null)){
			this.certifUsoSuelosModif = true;
		}
		else {
			if ((certificadoUsoSuelos!=null && this.certificadoUsoSuelos!=null && certificadoUsoSuelos.getContenidoDocumento()!=null
					&& this.certificadoUsoSuelos.getContenidoDocumento()!=null &&
					!Arrays.equals(certificadoUsoSuelos.getContenidoDocumento(), this.certificadoUsoSuelos.getContenidoDocumento()))
					|| (certificadoUsoSuelos!=null && this.certificadoUsoSuelos!=null && certificadoUsoSuelos.getContenidoDocumento()== null && this.certificadoUsoSuelos.getContenidoDocumento()!= null)
					|| (certificadoUsoSuelos!=null && this.certificadoUsoSuelos!=null && certificadoUsoSuelos.getContenidoDocumento()!= null && this.certificadoUsoSuelos.getContenidoDocumento()== null))
				this.certifUsoSuelosModif = true;

		}

		if(this.certificadoUsoSuelos!=null && this.certificadoUsoSuelos.getId()!=null && certificadoUsoSuelos!= null){

			if (certificadoUsoSuelos.getId()!=null)
			this.certificadoUsoSuelos.setId(certificadoUsoSuelos.getId());
			if (certificadoUsoSuelos.getIdAlfresco()!=null)
				this.certificadoUsoSuelos.setIdAlfresco(certificadoUsoSuelos.getIdAlfresco());
			if (certificadoUsoSuelos.getDireccionAlfresco()!=null)
				this.certificadoUsoSuelos.setDireccionAlfresco(certificadoUsoSuelos.getDireccionAlfresco());

			this.certificadoUsoSuelos.setContenidoDocumento(certificadoUsoSuelos.getContenidoDocumento());
			this.certificadoUsoSuelos.setNombre(certificadoUsoSuelos.getNombre());
			this.certificadoUsoSuelos.setExtesion(certificadoUsoSuelos.getExtesion());
			this.certificadoUsoSuelos.setMime(certificadoUsoSuelos.getMime());
			this.certificadoUsoSuelos.setNombreTabla(certificadoUsoSuelos.getNombreTabla());

		}
		else{
			this.certificadoUsoSuelos = certificadoUsoSuelos;
		}
	}

	@ManyToOne
	@JoinColumn(name = "docu_c_cuf_id")
	@ForeignKey(name = "fk_repo_id_documentsdocu_c_cuf_id")
	@Getter
	private Documento certificadoCompatibilidadUsoSuelos;
	
	public void setCertificadoCompatibilidadUsoSuelos(Documento certificadoCompatibilidadUsoSuelos){

		if ((certificadoCompatibilidadUsoSuelos==null && this.certificadoCompatibilidadUsoSuelos != null) ||
				(this.certificadoCompatibilidadUsoSuelos==null && certificadoCompatibilidadUsoSuelos != null)){
			this.certifCompatibUsoSuelosModif = true;
		}
		else {
			if ((certificadoCompatibilidadUsoSuelos!=null && this.certificadoCompatibilidadUsoSuelos!=null && certificadoCompatibilidadUsoSuelos.getContenidoDocumento()!=null
					&& this.certificadoCompatibilidadUsoSuelos.getContenidoDocumento()!=null &&
					!Arrays.equals(certificadoCompatibilidadUsoSuelos.getContenidoDocumento(), this.certificadoCompatibilidadUsoSuelos.getContenidoDocumento()))
					|| (certificadoCompatibilidadUsoSuelos!=null && this.certificadoCompatibilidadUsoSuelos!=null && certificadoCompatibilidadUsoSuelos.getContenidoDocumento()== null && this.certificadoCompatibilidadUsoSuelos.getContenidoDocumento()!= null)
					|| (certificadoCompatibilidadUsoSuelos!=null && this.certificadoCompatibilidadUsoSuelos!=null && certificadoCompatibilidadUsoSuelos.getContenidoDocumento()!= null && this.certificadoCompatibilidadUsoSuelos.getContenidoDocumento()== null))
				this.certifCompatibUsoSuelosModif = true;

		}

		if(this.certificadoCompatibilidadUsoSuelos!=null && this.certificadoCompatibilidadUsoSuelos.getId()!=null && certificadoCompatibilidadUsoSuelos!= null){

			if (certificadoCompatibilidadUsoSuelos.getId()!=null)
				this.certificadoCompatibilidadUsoSuelos.setId(certificadoCompatibilidadUsoSuelos.getId());
			if (certificadoCompatibilidadUsoSuelos.getIdAlfresco()!=null)
				this.certificadoCompatibilidadUsoSuelos.setIdAlfresco(certificadoUsoSuelos.getIdAlfresco());
			if (certificadoCompatibilidadUsoSuelos.getDireccionAlfresco()!=null)
				this.certificadoCompatibilidadUsoSuelos.setDireccionAlfresco(certificadoUsoSuelos.getDireccionAlfresco());

			this.certificadoCompatibilidadUsoSuelos.setContenidoDocumento(certificadoCompatibilidadUsoSuelos.getContenidoDocumento());
			this.certificadoCompatibilidadUsoSuelos.setNombre(certificadoCompatibilidadUsoSuelos.getNombre());
			this.certificadoCompatibilidadUsoSuelos.setExtesion(certificadoCompatibilidadUsoSuelos.getExtesion());
			this.certificadoCompatibilidadUsoSuelos.setMime(certificadoCompatibilidadUsoSuelos.getMime());
			this.certificadoCompatibilidadUsoSuelos.setNombreTabla(certificadoCompatibilidadUsoSuelos.getNombreTabla());
		}
		else{
			this.certificadoCompatibilidadUsoSuelos = certificadoCompatibilidadUsoSuelos;
		}
	}

	@ManyToOne
	@Getter
	@Setter
	@JoinColumn(name = "gelo_id")
	@ForeignKey(name = "fk_repo_id_geografical_locationsgelo_id")
	private UbicacionesGeografica ubicacionesGeografica;
	
	@Getter
	@Setter
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(mappedBy = "puntoRecuperacion")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "rpsh_status = 'TRUE'")
	private List<FormaPuntoRecuperacion> formasPuntoRecuperacion;
	
	@Getter
	@Setter
	@Transient
	private List<FormaPuntoRecuperacion> formasPuntoRecuperacionActuales;

	@Getter
	@Setter
	@Transient
	private boolean certifUsoSuelosModif;

	@Getter
	@Setter
	@Transient
	private boolean certifCompatibUsoSuelosModif;

	@Override
	public String toString() {
		return "PuntoRecuperacion [id=" + id + ", nombre=" + nombre
				+ ", telefono=" + telefono + ", correo=" + correo
				+ ", direccion=" + direccion + ", generadorDesechosPeligrosos="
				+ generadorDesechosPeligrosos + ", certificadoUsoSuelos="
				+ certificadoUsoSuelos
				+ ", certificadoCompatibilidadUsoSuelos="
				+ certificadoCompatibilidadUsoSuelos
				+ ", ubicacionesGeografica=" + ubicacionesGeografica
				+ ", formasPuntoRecuperacion=" + formasPuntoRecuperacion
				+ ", formasPuntoRecuperacionActuales="
				+ formasPuntoRecuperacionActuales + ", certifUsoSuelosModif="
				+ certifUsoSuelosModif + ", certifCompatibUsoSuelosModif="
				+ certifCompatibUsoSuelosModif + "]";
	}
	
	
}

package ec.gob.ambiente.rcoa.sustancias.quimicas.model;

import java.io.Serializable;
import java.util.ArrayList;
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
import javax.persistence.Table;
import javax.persistence.Transient;

import ec.gob.ambiente.rcoa.model.CatalogoGeneralCoa;
import ec.gob.ambiente.rcoa.model.GestionarProductosQuimicosProyectoAmbiental;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "responsable_chemical_sustances", schema = "coa_chemical_sustances")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "recs_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "recs_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "recs_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "recs_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "recs_user_update")) })
public class ResponsableSustanciaQuimica extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	public ResponsableSustanciaQuimica() {
	}

	public ResponsableSustanciaQuimica(RegistroSustanciaQuimica registroSustanciaQuimica) {
		this.registroSustanciaQuimica = registroSustanciaQuimica;
	}

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "recs_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "recs_identification")
	private String identificacion;

	@Getter
	@Setter
	@Column(name = "recs_name")
	private String nombre;

	@Getter
	@Setter
	@Column(name = "recs_address")
	private String direccion;

	@Getter
	@Setter
	@Column(name = "recs_phone")
	private String telefono;

	@Getter
	@Setter
	@Column(name = "recs_email")
	private String correo;

	@Getter
	@Setter
	@Column(name = "recs_title_number")
	private String numeroTitulo;

	@Getter
	@Setter
	@Column(name = "recs_title_name")
	private String nombreTitulo;

	@Column(name = "recs_place")
	@Getter
	@Setter
	private String lugar;

	@Getter
	@Setter
	@Column(name = "recs_craft_operator")
	private Boolean operadorArtesanal;

	@Getter
	@Setter
	@Column(name = "recs_loading_artisan_title")
	private Boolean recs_loading_artisan_title;

	@Getter
	@Setter
	@Column(name = "recs_technical_responsibility")
	private Boolean recs_technical_responsibility;

	@ManyToOne
	@JoinColumn(name = "chsr_id")
	@Getter
	@Setter
	private RegistroSustanciaQuimica registroSustanciaQuimica;

	@ManyToOne
	@JoinColumn(name = "geca_id")
	@Getter
	@Setter
	private CatalogoGeneralCoa tipoIdentificacion;

	@ManyToOne
	@JoinColumn(name = "geca_id_responsable")
	@Getter
	@Setter
	private CatalogoGeneralCoa tipoResponsable;

	@ManyToOne
	@JoinColumn(name = "mach_id")
	@Getter
	@Setter
	private GestionarProductosQuimicosProyectoAmbiental sustanciaProyecto;
	
	@Getter
	@Setter
	@Column(name = "recs_substance")
	private String sustanciasQuimicas;
	
	@Getter
	@Setter
	@Transient
	private String sustancias;


	@Transient
	@Getter
	private String[] lugaresArray;

	public void setLugaresArray(String[] lugaresArray) {
		this.lugaresArray = lugaresArray;
		if (lugaresArray == null || lugaresArray.length == 0) {
			this.lugar = null;
		} else {
			lugar = "";
			for (String la : lugaresArray) {
				lugar += lugar.isEmpty() ? la : ";" + la;
			}
		}
	}

	public String getLugares() {
		if (this.lugar != null) {
			return this.lugar.replace(";", " ");
		}
		return "";
	}

	public List<String> getNumerosTitulos() {
		List<String> lista = new ArrayList<>();
		if (this.numeroTitulo != null) {
			lista = Arrays.asList(this.numeroTitulo.split(";"));
		}
		return lista;
	}

	public List<String> getNombresTitulos() {
		List<String> lista = new ArrayList<>();
		if (this.nombreTitulo != null) {
			lista = Arrays.asList(this.nombreTitulo.split(";"));
		}
		return lista;
	}

	@Getter
	@Setter
	@Transient
	private DocumentosSustanciasQuimicasRcoa documentoTitProfesional, documentoTitArtesanal,
			documentoResponsabilidadTec;
}
package ec.gob.ambiente.suia.domain;

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
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadBase;
import org.hibernate.annotations.Proxy;

/**
 * The persistent class for the categories_catalog database table.
 * 
 */
@Entity
@Table(name = "waste_dangerous", catalog = "", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "wada_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "wada_status = 'TRUE'")
@NamedQueries({
		@NamedQuery(name = DesechoPeligroso.FIND_ALL, query = "SELECT d FROM DesechoPeligroso d"),
		@NamedQuery(name = DesechoPeligroso.FIND_BY_ID, query = "SELECT d FROM DesechoPeligroso d WHERE d.id= :idDesecho"),
		@NamedQuery(name = DesechoPeligroso.FIND_BY_FUENTE, query = "SELECT d FROM DesechoPeligroso d where d.fuenteDesechoPeligroso.id = :idFuente "),
		@NamedQuery(name = DesechoPeligroso.FIND_LIKE_DESCRIPCION, query = "SELECT d FROM DesechoPeligroso d where (lower(unaccent(d.clave)) like unaccent(:descripcion) or lower(unaccent(d.descripcion)) like unaccent(:descripcion))"),
		@NamedQuery(name = DesechoPeligroso.FIND_BY_FUENTE_LIKE_DESCRIPCION, query = "SELECT d FROM DesechoPeligroso d where d.fuenteDesechoPeligroso.tipoFuenteDesechoPeligroso.id in (1 , 2) and  (lower(unaccent(d.clave)) like unaccent(:descripcion) or lower(unaccent(d.descripcion)) like unaccent(:descripcion))") })
@Proxy( lazy=false )
public class DesechoPeligroso extends EntidadBase {

	private static final long serialVersionUID = 3667078975875351141L;

	public static final String FIND_ALL = "ec.com.magmasoft.business.domain.DesechoPeligroso.findAll";
	public static final String FIND_BY_ID = "ec.com.magmasoft.business.domain.DesechoPeligroso.findById";
	public static final String FIND_BY_FUENTE = "ec.com.magmasoft.business.domain.DesechoPeligroso.findByFuente";
	public static final String FIND_LIKE_DESCRIPCION = "ec.com.magmasoft.business.domain.DesechoPeligroso.findLikeDescripcion";
	public static final String FIND_BY_FUENTE_LIKE_DESCRIPCION = "ec.com.magmasoft.business.domain.DesechoPeligroso.findByFuenteLikeDescripcion";

	public static final String CLAVE_DESECHO_ES_04 = "ES-04";
	public static final String CLAVE_DESECHO_ES_06 = "ES-06";
	public static final String CLAVE_DESECHO_ES_07 = "ES-07";
	public static final String CLAVE_DESECHO_NE_40 = "NE-40";
	
	@Id
	@Getter
	@Setter
	@SequenceGenerator(name = "WASTE_DANGEROUS_SOURCE_WSID_GENERATOR", sequenceName = "seq_wada_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "WASTE_DANGEROUS_SOURCE_WSID_GENERATOR")
	@Column(name = "wada_id", unique = true, nullable = false)
	private Integer id;

	@Getter
	@Setter
	@Column(name = "wada_key", length = 255)
	private String clave;

	@Getter
	@Setter
	@Column(name = "wada_critb", length = 255)
	private String critb;

	@Getter
	@Setter
	@Column(name = "wada_code_basilea", length = 255)
	private String codigoBasilea;

	@Getter
	@Setter
	@Column(name = "wada_description")
	private String descripcion;

	@Getter
	@Setter
	@JoinColumn(name = "wdso_id", referencedColumnName = "wdso_id", nullable = false)
	@ForeignKey(name = "fk_wdso_id_to_wada_id")
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private FuenteDesechoPeligroso fuenteDesechoPeligroso;

	@Getter
	@Setter
	@Transient
	private double capacidadGestion;

	@Getter
	@Setter
	@Transient
	private String origen;

	@Getter
	@Setter
	@Transient
	private boolean habilitarCapacidadGestion;
	
	@Getter
	@Setter
	@Transient
	private boolean desechoSeleccionado;
	
	@Getter
	@Setter
	@Transient
	private boolean esLampara;

	@Getter
	@Setter
	@Transient
	private boolean esMedicamento=true;

	public String getNombresCRITB() {
		String result = "";
		if (critb == null) {
			result = "No aplica";
			return result;
		}

		String separator = ", ";
		if (critb.toLowerCase().contains("c"))
			result += "Corrosivo" + separator;
		if (critb.toLowerCase().contains("r"))
			result += "Reactivo" + separator;
		if (critb.toLowerCase().contains("i"))
			result += "Inflamable" + separator;
		if (critb.toLowerCase().contains("t"))
			result += "Tóxico" + separator;
		if (critb.toLowerCase().contains("b"))
			result += "Biológico-Infeccioso" + separator;

		if (result.endsWith(separator))
			result = result.substring(0, result.indexOf(separator));

		return result;
	}

	public boolean isDesechoES_04() {
		return this.clave != null && this.clave.toUpperCase().equals(CLAVE_DESECHO_ES_04);
	}

	public boolean isDesechoES_06() {
		return this.clave != null && this.clave.toUpperCase().equals(CLAVE_DESECHO_ES_06);
	}
	public boolean isDesechoES_07() {
		return this.clave != null && this.clave.toUpperCase().equals(CLAVE_DESECHO_ES_07);
	}
	public boolean isDesechoNE_40(){
		return this.clave  != null && this.clave.toUpperCase().equals(CLAVE_DESECHO_NE_40);
	}

	@Override
	public String toString() {
		return this.descripcion;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof DesechoPeligroso)) {
			return false;
		}
		DesechoPeligroso other = (DesechoPeligroso) obj;
		if (((this.id == null) && (other.getId() != null)) || ((this.id != null) && !this.id.equals(other.getId()))) {
			return false;
		}
		return true;

	}

	public int hashCode() {
		return id.hashCode();
	}

}
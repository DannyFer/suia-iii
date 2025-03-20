package ec.gob.ambiente.suia.domain;

import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.base.EntidadBase;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;

/**
 * The persistent class for the type_documents database table.
 * 
 */
@Entity
@Table(name = "document_types", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "doty_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "doty_status = 'TRUE'")
public class TipoDocumento extends EntidadBase {

	private static final long serialVersionUID = -143558611369144630L;

	@Id
	@SequenceGenerator(name = "TYPE_DOCUMENTS_TYDOID_GENERATOR")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TYPE_DOCUMENTS_TYDOID_GENERATOR")
	@Column(name = "doty_id")
	@Getter
	@Setter
	private Integer id;

	@Column(name = "doty_name")
	@Getter
	@Setter
	private String nombre;

	@OneToMany(mappedBy = "tipoDocumento")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "docu_status = 'TRUE'")
	@Getter
	@Setter
	private List<Documento> documentos;
	
	/**
	 * Retorna el TipoDocumentoSistema a partir del ID del TipoDocumento
	 * @return TipoDocumentoSistema
	 */
	public TipoDocumentoSistema getTipoDocumentoSistema() {
		if(this.id == null || this.id == 0)
			return null;
		for (TipoDocumentoSistema tds : TipoDocumentoSistema.class.getEnumConstants()) {
			if(tds.getIdTipoDocumento() == this.id)
				return tds;
		} 
		return null;
	}
	
	@Override
	public String toString() {
		return nombre;
	}
}
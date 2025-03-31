package ec.gob.ambiente.prevencion.registrogeneradordesechos.bean;

import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import ec.gob.ambiente.suia.domain.FaseGestionDesecho;

@ManagedBean
@ViewScoped
public class EmpresaPrestadoraServiciosBean1 extends EmpresaPrestadoraServiciosBean {

	private static final long serialVersionUID = 2475917152161192723L;

	@PostConstruct
	public void init() {
		super.init();
		idFasesGestion = new ArrayList<Integer>();
		idFasesGestion.add(FaseGestionDesecho.FASE_DISPOSICION_FINAL);
		idFasesGestion.add(FaseGestionDesecho.FASE_ELIMINACION);
		dialogseleccionarEmpresa = "seleccionarEmpresa1";
		filtroEmpresa = "filtroEmpresa1";
		tableEmpresas = "tableEmpresas1";
		tbl_empresas = "tbl_empresas1";
		btnSeleccionar = "btnSeleccionar1";
	}

}

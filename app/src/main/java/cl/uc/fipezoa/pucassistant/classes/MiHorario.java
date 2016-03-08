package cl.uc.fipezoa.pucassistant.classes;

import java.io.Serializable;

import cl.uc.fipezoa.pucapi.buscacursos.Modulo;
import cl.uc.fipezoa.pucapi.buscacursos.Modulos;
import cl.uc.fipezoa.pucapi.buscacursos.RamoBuscaCursos;
import cl.uc.fipezoa.pucapi.buscacursos.Ramos;

/**
 * Created by fipezoa on 2/17/2016.
 */
public class MiHorario implements Serializable {

    private String name;
    private Ramos<RamoBuscaCursos> ramos;
    private Modulos modulos;
    private String periodo;

    public MiHorario(String name, Ramos<RamoBuscaCursos> ramos, String periodo){
        setName(name);
        setRamos(ramos);
        setPeriodo(periodo);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Ramos<RamoBuscaCursos> getRamos() {
        return ramos;
    }

    public void setRamos(Ramos<RamoBuscaCursos> ramos) {
        this.ramos = ramos;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }
}

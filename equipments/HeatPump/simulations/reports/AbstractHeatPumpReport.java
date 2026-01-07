package equipments.HeatPump.simulations.reports;

import fr.sorbonne_u.components.hem2025e2.GlobalReportI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationReportI;
import fr.sorbonne_u.exceptions.PreconditionException;

/**
 * The class <code>equipments.HeatPump.simulations.reports.mil.AbstractHeatPumpReport</code>.
 *
 * <p><strong>Description</strong></p>
 *
 * <p>
 *
 * </p>
 *
 * <p><strong>Invariants</strong></p>
 *
 * <pre>
 * </pre>
 *
 * <p>Created on : 2025-10-04</p>
 *
 * @author    <a href="mailto:Rodrigo.Vila@etu.sorbonne-universite.fr">Rodrigo Vila</a>
 * @author    <a href="mailto:Damien.Ribeiro@etu.sorbonne-universite.fr">Damien Ribeiro</a>
 */
public abstract class AbstractHeatPumpReport<A> implements SimulationReportI, GlobalReportI {

    protected String modelUri;
    protected String parameter_representation;
    protected A parameterValue;

    public AbstractHeatPumpReport(String uri, String parameter_representation, A parameter) {
        super();

        assert uri != null && !uri.isEmpty() :
                new PreconditionException("uri == null || uri.isEmpty()");
        assert parameter_representation != null :
                new PreconditionException("parameter_representation == null");
        assert parameter != null :
                new PreconditionException("parameter == null");


        this.modelUri = uri;
        this.parameter_representation = parameter_representation;
        this.parameterValue = parameter;
    }

    /**
     * @see fr.sorbonne_u.components.hem2025e2.GlobalReportI#printout
     */
    @Override
    public String printout(String indent) {
        assert indent != null:
                new PreconditionException("indent == null");

        StringBuilder ret = new StringBuilder(indent);
        ret.append("---\n");
        ret.append(indent);
        ret.append('|');
        ret.append(this.modelUri);
        ret.append(" report\n");
        ret.append(indent);
        ret.append('|');
        ret.append(this.parameter_representation);
        ret.append(this.parameterValue);
        ret.append(".\n");
        ret.append(indent);
        ret.append("---\n");
        return ret.toString();
    }

    @Override
    public String toString() {
        return this.printout("");
    }

    /**
     * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationReportI#getModelURI
     */
    @Override
    public String getModelURI() {
        return modelUri;
    }
}

package equipments.dimmerlamp.mil;

import fr.sorbonne_u.components.hem2025e2.GlobalReportI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationReportI;
import fr.sorbonne_u.exceptions.PreconditionException;

/**
 * The class <code>equipments.dimmerlamp.mil.DimmerLampElectricityReport</code>.
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
public class DimmerLampElectricityReport implements SimulationReportI, GlobalReportI {

    protected String modelURI;
    protected double totalConsumption;

    /**
     *
     * Constructor for dimmer lamp electricity report
     *
     * <p><strong>Contract</strong></p>
     *
     * <pre>
     *  pre {@code uri != null && !uri.isEmpty()}
     * </pre>
     * @param uri uri of the simulation model
     * @param totalConsumption consumption computed at the end of the simulation
     */
    public DimmerLampElectricityReport(String uri, double totalConsumption) {
        super();

        assert uri != null && !uri.isEmpty() :
                new PreconditionException("uri == null || uri.isEmpty()");

        this.modelURI = uri;
        this.totalConsumption = totalConsumption;
    }

    /**
     * Description
     *
     * <p><strong>Contract</strong></p>
     *
     * <pre>
     *  pre {@code indent != null}
     * </pre>
     *
     * @param indent indentation as a string of blank characters.
     * @return
     */
    @Override
    public String printout(String indent) {
        assert indent != null:
                new PreconditionException("indent == null");

        StringBuilder ret = new StringBuilder(indent);
        ret.append("---\n");
        ret.append(indent);
        ret.append('|');
        ret.append(this.modelURI);
        ret.append(" report\n");
        ret.append(indent);
        ret.append('|');
        ret.append("total consumption in kwh = ");
        ret.append(this.totalConsumption);
        ret.append(".\n");
        ret.append(indent);
        ret.append("---\n");
        return ret.toString();
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String	toString()
    {
        return this.printout("");
    }

    /**
     * Description
     *
     * <p><strong>Contract</strong></p>
     *
     * <pre>
     * null
     * </pre>
     *
     * @return
     */
    @Override
    public String getModelURI() {
        return this.modelURI;
    }
}

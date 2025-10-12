package equipments.HeatPump.Test;

import equipments.HeatPump.compressor.CompressorOutboundPort;
import equipments.HeatPump.connections.*;
import equipments.HeatPump.interfaces.HeatPumpExternalControlCI;
import equipments.HeatPump.interfaces.HeatPumpInternalControlCI;
import equipments.HeatPump.interfaces.HeatPumpUserCI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;

/**
 * The class <code>equipments.HeatPump.Test.HeatPumpTester</code>.
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
@RequiredInterfaces(required = {
        HeatPumpUserCI.class,
        HeatPumpInternalControlCI.class,
        HeatPumpExternalControlCI.class
})
public class HeatPumpTester
extends AbstractComponent {

    protected final static String REFLECTION_INBOUND_URI = "HEATPUMP-TESTER-URI";
    protected final static int NUMBER_THREADS = 1;
    protected final static int NUMBER_SCHEDULABLE_THREADS = 1;

    protected boolean isUnitTest;

    protected HeatPumpUserOutboundPort userOutboundPort;
    protected String userInboundPortURI;
    protected HeatPumpInternalControlOutboundPort internalOutboundPort;
    protected String internalInboundPortURI;
    protected HeatPumpExternalControlOutboundPort externalOutboundPort;
    protected String externalExternalPortURI;
    protected HeatPumpTester(
            boolean isUnitTest,
            String heatPumpUserInboundURI,
            String heatPumpInternalInboundURI,
            String heatPumpExternalInboundURI) throws Exception {
        super(REFLECTION_INBOUND_URI, NUMBER_THREADS, NUMBER_SCHEDULABLE_THREADS);
        this.isUnitTest = isUnitTest;

        this.userOutboundPort = new HeatPumpUserOutboundPort(this);
        this.userOutboundPort.publishPort();
        this.userInboundPortURI = heatPumpUserInboundURI;

        this.internalOutboundPort = new HeatPumpInternalControlOutboundPort(this);
        this.internalOutboundPort.publishPort();
        this.internalInboundPortURI = heatPumpInternalInboundURI;

        this.externalOutboundPort = new HeatPumpExternalControlOutboundPort(this);
        this.externalOutboundPort.publishPort();
        this.externalExternalPortURI = heatPumpExternalInboundURI;
    }

    @Override
    public synchronized void start() throws ComponentStartException {

        super.start();

        try {
            this.doPortConnection(
                    this.userOutboundPort.getPortURI(),
                    this.userInboundPortURI,
                    HeatPumpUserConnector.class.getCanonicalName()
            );

            this.doPortConnection(
                    this.internalOutboundPort.getPortURI(),
                    this.internalInboundPortURI,
                    HeatPumpInternalControlConnector.class.getCanonicalName()
            );

            this.doPortConnection(
                    this.externalOutboundPort.getPortURI(),
                    this.externalExternalPortURI,
                    HeatPumpExternalControlConnector.class.getCanonicalName()
            );
        } catch (Exception e) {
            throw new ComponentStartException(e);
        }

    }


    @Override
    public synchronized void	finalise() throws Exception
    {
        this.doPortDisconnection(this.userOutboundPort.getPortURI());
        this.doPortDisconnection(this.internalOutboundPort.getPortURI());
        this.doPortDisconnection(this.externalOutboundPort.getPortURI());
        super.finalise();
    }

    @Override
    public synchronized void	shutdown() throws ComponentShutdownException
    {
        try {
            this.userOutboundPort.unpublishPort();
            this.internalOutboundPort.unpublishPort();
            this.externalOutboundPort.unpublishPort();
        } catch (Exception e) {
            throw new ComponentShutdownException(e) ;
        }
        super.shutdown();
    }

    @Override
    public synchronized void execute() throws Exception {}

}

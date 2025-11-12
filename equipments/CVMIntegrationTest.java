package equipments;

// Copyright Jacques Malenfant, Sorbonne Universite.
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to implement a mock-up
// of household energy management system.
//
// This software is governed by the CeCILL-C license under French law and
// abiding by the rules of distribution of free software.  You can use,
// modify and/ or redistribute the software under the terms of the
// CeCILL-C license as circulated by CEA, CNRS and INRIA at the following
// URL "http://www.cecill.info".
//
// As a counterpart to the access to the source code and  rights to copy,
// modify and redistribute granted by the license, users are provided only
// with a limited warranty  and the software's author,  the holder of the
// economic rights,  and the successive licensors  have only  limited
// liability. 
//
// In this respect, the user's attention is drawn to the risks associated
// with loading,  using,  modifying and/or developing or reproducing the
// software by the user in light of its specific status of free software,
// that may mean  that it is complicated to manipulate,  and  that  also
// therefore means  that it is reserved for developers  and  experienced
// professionals having in-depth computer knowledge. Users are therefore
// encouraged to load and test the software's suitability as regards their
// requirements in conditions enabling the security of their systems and/or 
// data to be ensured and,  more generally, to use and operate it in the 
// same conditions as regards security. 
//
// The fact that you are presently reading this means that you have had
// knowledge of the CeCILL-C license and that you accept its terms.

import equipments.HeatPump.HeatPump;
import equipments.HeatPump.Test.HeatPumpTester;
import equipments.HeatPump.compressor.Compressor;
import equipments.HeatPump.compressor.CompressorConnector;
import equipments.HeatPump.temperatureSensor.TemperatureSensor;
import equipments.HeatPump.temperatureSensor.TemperatureSensorConnector;
import equipments.dimmerlamp.DimmerLamp;
import equipments.dimmerlamp.connections.DimmerLampExternalConnector;
import equipments.dimmerlamp.connections.DimmerLampUserConnector;
import equipments.dimmerlamp.test.DimmerLampTester;
import equipments.fan.Fan;
import equipments.hem.RegistrationConnector;
import equipments.oven.Oven;
import equipments.fan.FanTester;
import equipments.hem.HEM;
import equipments.oven.OvenUnitTester;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.exceptions.BCMException;
import fr.sorbonne_u.components.hem2025e1.equipments.batteries.Batteries;
import fr.sorbonne_u.components.hem2025e1.equipments.generator.Generator;
import fr.sorbonne_u.components.hem2025e1.equipments.hairdryer.HairDryer;
import fr.sorbonne_u.components.hem2025e1.equipments.hairdryer.HairDryerTester;
import fr.sorbonne_u.components.hem2025e1.equipments.heater.Heater;
import fr.sorbonne_u.components.hem2025e1.equipments.heater.HeaterUnitTester;
import fr.sorbonne_u.components.hem2025e1.equipments.meter.ElectricMeter;
import fr.sorbonne_u.components.hem2025e1.equipments.solar_panel.SolarPanel;
import fr.sorbonne_u.exceptions.AssertionChecking;
import fr.sorbonne_u.exceptions.InvariantException;
import fr.sorbonne_u.exceptions.PreconditionException;
import fr.sorbonne_u.utils.aclocks.ClocksServer;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

// -----------------------------------------------------------------------------
/**
 * The class <code>CVMIntegrationTest</code> defines the integration test
 * for the household energy management example.
 *
 * <p><strong>Description</strong></p>
 *
 * <p><strong>Implementation Invariants</strong></p>
 *
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 *
 * <p><strong>Invariants</strong></p>
 *
 * <pre>
 * invariant	{@code CLOCK_URI != null && !CLOCK_URI.isEmpty()}
 * invariant	{@code DELAY_TO_START_IN_MILLIS >= 0}
 * invariant	{@code ACCELERATION_FACTOR > 0.0}
 * invariant	{@code START_INSTANT != null}
 * </pre>
 *
 * <p>Created on : 2021-09-10</p>
 *
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			CVMIntegrationTest
		extends		AbstractCVM
{
	/** for integration tests, a {@code Clock} is used to get a
	 *  time-triggered synchronisation of the actions of the components
	 *  in the test scenarios.												*/
	public static final String	CLOCK_URI = "test-clock";
	/** delay before starting the test scenarios, leaving time to build
	 *  and initialise the components and their simulators.					*/
	public static final long	DELAY_TO_START_IN_MILLIS = 3000;
	/** for real time simulations, the acceleration factor applied to the
	 *  the simulated time to get the execution time of the simulations. 	*/
	public static final double	ACCELERATION_FACTOR = 1.0;
	/** start instant in test scenarios, as a string to be parsed.			*/
	public static final Instant	START_INSTANT =
			Instant.parse("2024-09-18T14:00:00.00Z");
	/** number of square meters in the test solar panel.					*/
	public static final int	NB_OF_SQUARE_METERS = 10;

	public static final String COMPRESSOR_INBOUND_URI = "COMPRESSOR-INBOUND-URI";

	public static final String SENSOR_INBOUND_URI = "TEMPERATURE-INBOUND-URI";

	public static final String HEATPUMP_USER_INBOUND_URI = "HEATPUMP-USER-INBOUND-URI";

	public static final String HEATPUMP_INTERNAL_INBOUND_URI = "HEATPUMP-INTERNAL-INBOUND-URI";

	public static final String HEATPUMP_EXTERNAL_INBOUND_URI = "HEATPUMP-EXTERNAL-INBOUND-URI";

	// -------------------------------------------------------------------------
	// Invariants
	// -------------------------------------------------------------------------

	/**
	 * return true if the implementation invariants are observed, false otherwise.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code cvm != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param cvm	instance to be tested.
	 * @return		true if the implementation invariants are observed, false otherwise.
	 */
	protected static boolean	implementationInvariants(CVMIntegrationTest cvm)
	{
		assert	cvm != null : new PreconditionException("cvm != null");

		boolean ret = true;
		return ret;
	}

	/**
	 * return true if the invariants are observed, false otherwise.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code cvm != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param cvm	instance to be tested.
	 * @return	true if the invariants are observed, false otherwise.
	 */
	protected static boolean	invariants(CVMIntegrationTest cvm)
	{
		assert	cvm != null : new PreconditionException("cvm != null");

		boolean ret = true;
		ret &= AssertionChecking.checkImplementationInvariant(
				CLOCK_URI != null && !CLOCK_URI.isEmpty(),
				CVMIntegrationTest.class,
				cvm,
				"CLOCK_URI != null && !CLOCK_URI.isEmpty()");
		ret &= AssertionChecking.checkImplementationInvariant(
				DELAY_TO_START_IN_MILLIS >= 0,
				CVMIntegrationTest.class,
				cvm,
				"DELAY_TO_START_IN_MILLIS >= 0");
		ret &= AssertionChecking.checkImplementationInvariant(
				ACCELERATION_FACTOR > 0.0,
				CVMIntegrationTest.class,
				cvm,
				"ACCELERATION_FACTOR > 0.0");
		ret &= AssertionChecking.checkImplementationInvariant(
				START_INSTANT != null,
				CVMIntegrationTest.class,
				cvm,
				"START_INSTANT != null");
		return ret;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public CVMIntegrationTest() throws Exception
	{
		// Trace and trace window positions
		// -------------------------------------------------------------------------
		// Trace and trace window positions
		// -------------------------------------------------------------------------

		ClocksServer.VERBOSE = true;
		ClocksServer.X_RELATIVE_POSITION = 0;
		ClocksServer.Y_RELATIVE_POSITION = 0;

		HEM.VERBOSE = true;
		HEM.X_RELATIVE_POSITION = 1;
		HEM.Y_RELATIVE_POSITION = 0;

		ElectricMeter.VERBOSE = true;
		ElectricMeter.X_RELATIVE_POSITION = 2;
		ElectricMeter.Y_RELATIVE_POSITION = 0;
		
		// Ligne 2 : chauffage et four
		HeaterUnitTester.VERBOSE = true;
		HeaterUnitTester.X_RELATIVE_POSITION = 0;
		HeaterUnitTester.Y_RELATIVE_POSITION = 1;

		Heater.VERBOSE = true;
		Heater.X_RELATIVE_POSITION = 1;
		Heater.Y_RELATIVE_POSITION = 1;
		
		OvenUnitTester.VERBOSE = true;
		OvenUnitTester.X_RELATIVE_POSITION = 2;
		OvenUnitTester.Y_RELATIVE_POSITION = 1;

		Oven.VERBOSE = true;
		Oven.X_RELATIVE_POSITION = 3;
		Oven.Y_RELATIVE_POSITION = 1;

		// Ligne 3 : sèche-cheveux et ventilateur
		HairDryerTester.VERBOSE = true;
		HairDryerTester.X_RELATIVE_POSITION = 0;
		HairDryerTester.Y_RELATIVE_POSITION = 2;

		HairDryer.VERBOSE = true;
		HairDryer.X_RELATIVE_POSITION = 1;
		HairDryer.Y_RELATIVE_POSITION = 2;

		FanTester.VERBOSE = true;
		FanTester.X_RELATIVE_POSITION = 2;
		FanTester.Y_RELATIVE_POSITION = 2;

		Fan.VERBOSE = true;
		Fan.X_RELATIVE_POSITION = 3;
		Fan.Y_RELATIVE_POSITION = 2;

		// Ligne 4 : lampe et pompe à chaleur
		DimmerLampTester.VERBOSE = true;
		DimmerLampTester.X_RELATIVE_POSITION = 0;
		DimmerLampTester.Y_RELATIVE_POSITION = 3;

		DimmerLamp.VERBOSE = true;
		DimmerLamp.X_RELATIVE_POSITION = 1;
		DimmerLamp.Y_RELATIVE_POSITION = 3;

		HeatPumpTester.VERBOSE = true;
		HeatPumpTester.X_RELATIVE_POSITION = 2;
		HeatPumpTester.Y_RELATIVE_POSITION = 3;

		HeatPump.VERBOSE = true;
		HeatPump.X_RELATIVE_POSITION = 3;
		HeatPump.Y_RELATIVE_POSITION = 3;
		
		// Ligne 5 : producteurs d'énergie
		Batteries.VERBOSE = true;
		Batteries.X_RELATIVE_POSITION = 0;
		Batteries.Y_RELATIVE_POSITION = 4;

		SolarPanel.VERBOSE = true;
		SolarPanel.X_RELATIVE_POSITION = 1;
		SolarPanel.Y_RELATIVE_POSITION = 4;

		Generator.VERBOSE = true;
		Generator.X_RELATIVE_POSITION = 2;
		Generator.Y_RELATIVE_POSITION = 4;

		assert	implementationInvariants(this) :
				new InvariantException(
						"CVMIntegrationTest.glassBoxInvariants(this)");
		assert	invariants(this) :
				new InvariantException(
						"CVMIntegrationTest.blackBoxInvariants(this)");
	}

	/**
	 * @see AbstractCVM#deploy()
	 */
	@Override
	public void			deploy() throws Exception
	{
		// start time in Unix epoch time in nanoseconds.
		long unixEpochStartTimeInMillis = 
				System.currentTimeMillis() + DELAY_TO_START_IN_MILLIS;

		AbstractComponent.createComponent(
				ClocksServer.class.getCanonicalName(),
				new Object[]{
						// URI of the clock to retrieve it
						CLOCK_URI,
						// start time in Unix epoch time
						TimeUnit.MILLISECONDS.toNanos(
										 		unixEpochStartTimeInMillis),
						START_INSTANT,
						ACCELERATION_FACTOR});

		AbstractComponent.createComponent(
				ElectricMeter.class.getCanonicalName(),
				new Object[]{});

		AbstractComponent.createComponent(
				Batteries.class.getCanonicalName(),
				new Object[]{});

		AbstractComponent.createComponent(
				SolarPanel.class.getCanonicalName(),
				new Object[]{NB_OF_SQUARE_METERS});

		AbstractComponent.createComponent(
				Generator.class.getCanonicalName(),
				new Object[]{});

		AbstractComponent.createComponent(
				HairDryer.class.getCanonicalName(),
				new Object[]{});
		// At this stage, the tester for the hair dryer is added only
		// to show the hair dryer functioning; later on, it will be replaced
		// by a simulation of users' actions.
		AbstractComponent.createComponent(
				HairDryerTester.class.getCanonicalName(),
				new Object[]{false});
		
		AbstractComponent.createComponent(
				Fan.class.getCanonicalName(),
				new Object[]{});
		AbstractComponent.createComponent(
				FanTester.class.getCanonicalName(),
				new Object[]{false});

		AbstractComponent.createComponent(
				Heater.class.getCanonicalName(),
				new Object[]{});
		AbstractComponent.createComponent(
				HeaterUnitTester.class.getCanonicalName(),
				new Object[]{false});

		AbstractComponent.createComponent(
				Compressor.class.getCanonicalName(),
				new Object[]{COMPRESSOR_INBOUND_URI});

		AbstractComponent.createComponent(
				TemperatureSensor.class.getCanonicalName(),
				new Object[]{SENSOR_INBOUND_URI});

		AbstractComponent.createComponent(
				HeatPump.class.getCanonicalName(),
				new Object[]{
						COMPRESSOR_INBOUND_URI,
						SENSOR_INBOUND_URI,
						CompressorConnector.class.getCanonicalName(),
						TemperatureSensorConnector.class.getCanonicalName(),
						HEATPUMP_USER_INBOUND_URI,
						HEATPUMP_INTERNAL_INBOUND_URI,
						HEATPUMP_EXTERNAL_INBOUND_URI,
						HEM.RegistrationHEMURI,
						RegistrationConnector.class.getCanonicalName()
				});
		AbstractComponent.createComponent(
				HeatPumpTester.class.getCanonicalName(),
				new Object[]{
						false,
						HEATPUMP_USER_INBOUND_URI,
						HEATPUMP_INTERNAL_INBOUND_URI,
						HEATPUMP_EXTERNAL_INBOUND_URI
				});	// is unit test
		
		AbstractComponent.createComponent(
				DimmerLamp.class.getCanonicalName(),
				new Object[]{
						HEM.RegistrationHEMURI,
						RegistrationConnector.class.getCanonicalName()
				}
		);
		AbstractComponent.createComponent(
				DimmerLampTester.class.getCanonicalName(),
				new Object[]{
						false,
						DimmerLamp.BASE_USER_INBOUND_PORT_URI,
						DimmerLamp.BASE_EXTERNAL_INBOUND_PORT_URI,
						DimmerLampUserConnector.class,
						DimmerLampExternalConnector.class
				});	// is unit test
		
		AbstractComponent.createComponent(
			    Oven.class.getCanonicalName(),
			    new Object[]{
			        Oven.REFLECTION_INBOUND_PORT_URI,
			        Oven.USER_INBOUND_PORT_URI,
			        Oven.INTERNAL_CONTROL_INBOUND_PORT_URI,
			        Oven.EXTERNAL_CONTROL_INBOUND_PORT_URI,
			        HEM.RegistrationHEMURI,
			        RegistrationConnector.class.getCanonicalName()});
		AbstractComponent.createComponent(
		    OvenUnitTester.class.getCanonicalName(),
		    new Object[]{false});
		
		AbstractComponent.createComponent(
				HEM.class.getCanonicalName(),
				new Object[]{});

		super.deploy();
	}

	public static void	main(String[] args)
	{
		BCMException.VERBOSE = true;
		try {
			CVMIntegrationTest cvm = new CVMIntegrationTest();
			cvm.startStandardLifeCycle(20000L);
			Thread.sleep(100000L);
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
// -----------------------------------------------------------------------------

package org.fog.healthsim;

import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.core.CloudSim;
import org.fog.application.*;
import org.fog.application.selectivity.FractionalSelectivity;
import org.fog.entities.*;
import org.fog.placement.Controller;
import org.fog.placement.ModuleMapping;
import org.fog.placement.ModulePlacementEdgewards;
import org.fog.policy.AppModuleAllocationPolicy;
import org.fog.scheduler.StreamOperatorScheduler;
import org.fog.utils.*;
import org.fog.utils.distribution.DeterministicDistribution;
import org.cloudbus.cloudsim.power.PowerHost;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;
import org.cloudbus.cloudsim.sdn.overbooking.BwProvisionerOverbooking;
import org.cloudbus.cloudsim.sdn.overbooking.PeProvisionerOverbooking;
import org.cloudbus.cloudsim.Pe;

import java.util.*;
import java.text.DecimalFormat;

/**
 * Smart Home Temperature Monitoring System with Energy & Latency Analysis
 * 
 * PROBLEM STATEMENT:
 * 1. Create a topology with Cloud, Fog and Edge nodes
 * 2. Add temperature sensors and actuators for Smart Home Temperature
 * Monitoring
 * 3. Generate alert message when temperature is more than 30 degrees
 * 4. Calculate the energy consumption
 * 5. Find the latency for different processing paths
 * 6. Generate comprehensive energy consumption and latency report
 * 
 * SOLUTION ARCHITECTURE:
 * - Cloud: Central data processing and long-term storage
 * - Fog Node: Local gateway for data aggregation and processing
 * - Edge Device: Smart thermostat with temperature sensor
 * - Temperature Sensor: Monitors room temperature (18-35°C range)
 * - Actuator: Controls HVAC system and generates alerts
 * 
 * FEATURES:
 * - Real-time temperature monitoring
 * - Alert generation for temperatures > 30°C
 * - Energy consumption tracking for each device
 * - Latency measurement for different processing paths
 * - Comprehensive reporting system
 */
public class HealthMonitoringSim {

    private static List<FogDevice> fogDevices = new ArrayList<>();
    private static List<Sensor> sensors = new ArrayList<>();
    private static List<Actuator> actuators = new ArrayList<>();
    private static int alertCount = 0;
    private static int totalReadings = 0;

    public static void main(String[] args) throws Exception {
        Log.printLine("=== Starting Smart Home Temperature Monitoring System ===");

        // 1) Initialize CloudSim
        CloudSim.init(1, Calendar.getInstance(), false);

        // 2) Create broker for Smart Home system
        FogBroker broker = new FogBroker("smart-home-broker");
        String appId = "temperature_monitoring_app";

        // 3) Create Cloud Node (Central Data Center)
        FogDevice cloud = createFogDevice("Cloud-DataCenter", 44800, 40000,
                100000, 100000, 0, 0.01, 107.339, 83.4333);
        cloud.setParentId(-1);
        fogDevices.add(cloud);
        Log.printLine("✓ Cloud Node created: " + cloud.getName());

        // 4) Create Fog Node (Home Gateway/Router)
        FogDevice homeGateway = createFogDevice("Home-Gateway", 8000, 16384,
                10000, 10000, 1, 0.01, 200, 20);
        homeGateway.setParentId(cloud.getId());
        homeGateway.setUplinkLatency(50.0); // latency home gateway → cloud
        fogDevices.add(homeGateway);
        Log.printLine("✓ Fog Node created: " + homeGateway.getName());

        // 5) Create Edge Device (Smart Thermostat)
        FogDevice smartThermostat = createFogDevice("Smart-Thermostat", 1500, 2048,
                10000, 10000, 2, 0.0, 87.53, 82.44);
        smartThermostat.setParentId(homeGateway.getId());
        smartThermostat.setUplinkLatency(10.0); // latency thermostat → gateway
        fogDevices.add(smartThermostat);
        Log.printLine("✓ Edge Node created: " + smartThermostat.getName());

        // 6) Create Temperature Sensor with realistic temperature values
        Sensor temperatureSensor = new Sensor("temp-sensor-1", "TEMPERATURE", broker.getId(), appId,
                new DeterministicDistribution(2.0)) { // Reading every 2 seconds

            private Random rand = new Random();

            @Override
            protected void updateSensor() {
                totalReadings++;
                // Generate realistic temperature values (18°C to 35°C)
                double temperature = 18.0 + (rand.nextDouble() * 17.0); // 18-35°C range
                temperature = Math.round(temperature * 10.0) / 10.0; // Round to 1 decimal

                System.out.printf("🌡️  Temperature Reading #%d: %.1f°C%n", totalReadings, temperature);

                // Create tuple with temperature data
                Tuple tuple = new Tuple(getAppId(), getUserId(), getId(), "TEMPERATURE", new HashMap<String, Object>());
                tuple.getTuplePayload().put("TEMPERATURE", temperature);
                tuple.getTuplePayload().put("TIMESTAMP", System.currentTimeMillis());

                // Check for high temperature alert (>30°C)
                if (temperature > 30.0) {
                    alertCount++;
                    System.out.printf("🚨 HIGH TEMPERATURE ALERT! Temperature: %.1f°C (Alert #%d)%n",
                            temperature, alertCount);
                    tuple.getTuplePayload().put("ALERT", true);
                    tuple.getTuplePayload().put("ALERT_LEVEL", "HIGH");
                } else {
                    tuple.getTuplePayload().put("ALERT", false);
                    tuple.getTuplePayload().put("ALERT_LEVEL", "NORMAL");
                }

                // Send tuple to gateway device (Smart Thermostat)
                send(getGatewayDeviceId(), getLatency(), tuple);

                // Schedule next sensor reading
                schedule(getId(), getTransmitDistribution().getNextSample(), FogEvents.SENSOR_UPDATE);
            }
        };

        temperatureSensor.setGatewayDeviceId(smartThermostat.getId());
        temperatureSensor.setLatency(1.0); // sensor → thermostat
        sensors.add(temperatureSensor);
        Log.printLine("✓ Temperature Sensor created and configured");

        // 7) Create HVAC Control Actuator
        Actuator hvacController = new Actuator("hvac-controller-1", broker.getId(), appId, "HVAC_CONTROL");
        hvacController.setGatewayDeviceId(homeGateway.getId());
        hvacController.setLatency(1.5);
        actuators.add(hvacController);
        Log.printLine("✓ HVAC Controller Actuator created");

        // 8) Create Alert Notification Actuator
        Actuator alertNotifier = new Actuator("alert-notifier-1", broker.getId(), appId, "ALERT_NOTIFICATION");
        alertNotifier.setGatewayDeviceId(homeGateway.getId());
        alertNotifier.setLatency(0.5);
        actuators.add(alertNotifier);
        Log.printLine("✓ Alert Notification Actuator created");

        // 9) Create Smart Home Temperature Monitoring Application DAG
        Application app = createTemperatureMonitoringApp(appId, broker.getId());
        Log.printLine("✓ Application DAG created with modules and edges");

        // 10) Map application modules to fog devices
        ModuleMapping mapping = ModuleMapping.createModuleMapping();
        mapping.addModuleToDevice("TemperatureProcessor", "Smart-Thermostat"); // Edge processing
        mapping.addModuleToDevice("LocalLogger", "Smart-Thermostat"); // Local logging
        mapping.addModuleToDevice("AlertManager", "Home-Gateway"); // Fog-level alert management
        mapping.addModuleToDevice("DataAggregator", "Home-Gateway"); // Fog-level data aggregation
        mapping.addModuleToDevice("CloudAnalyzer", "Cloud-DataCenter"); // Cloud analytics
        Log.printLine("✓ Module mapping configured");

        // 11) Create controller and submit application
        Controller controller = new Controller("smart-home-controller", fogDevices, sensors, actuators);
        controller.submitApplication(app,
                new ModulePlacementEdgewards(fogDevices, sensors, actuators, app, mapping));

        Log.printLine("\n🚀 Starting Smart Home Temperature Monitoring Simulation...");
        // Start simulation
        CloudSim.startSimulation();
        CloudSim.stopSimulation();

        // 12) Generate comprehensive report
        printComprehensiveReport(app);

        Log.printLine("=== Smart Home Temperature Monitoring System Completed ===");
    }

    private static Application createHealthApp(String appId, int userId) {
        Application app = Application.createApplication(appId, userId);

        // Modules
        app.addAppModule("edgeProcessor", 10);
        app.addAppModule("logger", 10);
        app.addAppModule("fogAnalyzer", 50);

        // Edges
        app.addAppEdge("HEART_RATE", "edgeProcessor", 500, 500, "HEART_RATE", Tuple.UP, AppEdge.SENSOR);
        app.addAppEdge("edgeProcessor", "logger", 200, 200, "NORMAL_LOG", Tuple.UP, AppEdge.MODULE);
        app.addAppEdge("edgeProcessor", "fogAnalyzer", 800, 1000, "ALERT_REQ", Tuple.UP, AppEdge.MODULE);
        app.addAppEdge("fogAnalyzer", "ALERT", 100, 100, "ALERT", Tuple.DOWN, AppEdge.ACTUATOR);

        // ✅ Tuple mappings with probability (simulating random HR values)
        // 80% of heart rate values are normal
        app.addTupleMapping("edgeProcessor", "HEART_RATE", "NORMAL_LOG", new FractionalSelectivity(0.8));

        // 20% of heart rate values are abnormal (alert)
        app.addTupleMapping("edgeProcessor", "HEART_RATE", "ALERT_REQ", new FractionalSelectivity(0.2));

        // ✅ Mapping inside fogAnalyzer for alert → actuator
        app.addTupleMapping("fogAnalyzer", "ALERT_REQ", "ALERT", new FractionalSelectivity(1.0));

        // Loops for latency measurement
        app.setLoops(Arrays.asList(
                new AppLoop(Arrays.asList("edgeProcessor", "logger")),
                new AppLoop(Arrays.asList("edgeProcessor", "fogAnalyzer"))));

        return app;
    }

    private static FogDevice createFogDevice(String name, long mips, int ram,
            long upBw, long downBw, int level,
            double ratePerMips,
            double busyPower, double idlePower) throws Exception {
        List<Pe> peList = new ArrayList<>();
        peList.add(new Pe(0, new PeProvisionerOverbooking(mips)));

        int hostId = FogUtils.generateEntityId();
        long storage = 1000000;
        int bw = 10000;

        PowerHost host = new PowerHost(
                hostId,
                new RamProvisionerSimple(ram),
                new BwProvisionerOverbooking(bw),
                storage,
                peList,
                new StreamOperatorScheduler(peList),
                new FogLinearPowerModel(busyPower, idlePower));

        List<PowerHost> hostList = new ArrayList<>();
        hostList.add(host);

        FogDeviceCharacteristics characteristics = new FogDeviceCharacteristics(
                "x86", "Linux", "Xen", host, 10.0,
                3.0, 0.05, 0.001, 0.0);

        FogDevice device = new FogDevice(name, characteristics,
                new AppModuleAllocationPolicy(hostList),
                new LinkedList<Storage>(),
                10, upBw, downBw, 0, ratePerMips);
        device.setLevel(level);
        return device;
    }

    private static void printReport(Application app) {
        System.out.println("\n================= REPORT =================");

        TimeKeeper tk = TimeKeeper.getInstance();
        int i = 1;
        for (AppLoop loop : app.getLoops()) {
            Integer loopId = tk.getLoopIdToTupleIds().get(loop);

            Double avg = tk.getLoopIdToCurrentAverage().get(loopId);
            String name = (i == 1) ? "Normal (edge)" : "Abnormal (edge→fog)";
            System.out.printf("Average latency %-20s : %.3f ms%n", name, (avg == null ? Double.NaN : avg));
            i++;
        }

        double phoneEnergy = 0, hospitalEnergy = 0;
        for (FogDevice d : fogDevices) {
            if (d.getName().equals("smartphone"))
                phoneEnergy += d.getEnergyConsumption();
            if (d.getName().equals("hospital-server"))
                hospitalEnergy += d.getEnergyConsumption();
        }
        System.out.printf("Energy Smartphone (edge) : %.3f J%n", phoneEnergy);
        System.out.printf("Energy Hospital (fog)    : %.3f J%n", hospitalEnergy);
        System.out.printf("Total Energy             : %.3f J%n", (phoneEnergy + hospitalEnergy));

        System.out.println("=========================================\n");
    }
}
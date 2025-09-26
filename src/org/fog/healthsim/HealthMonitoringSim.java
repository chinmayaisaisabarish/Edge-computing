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

public class HealthMonitoringSim {

    private static List<FogDevice> fogDevices = new ArrayList<>();
    private static List<Sensor> sensors = new ArrayList<>();
    private static List<Actuator> actuators = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        Log.printLine("Starting HealthMonitoringSim...");

        // 1) Initialize CloudSim
        CloudSim.init(1, Calendar.getInstance(), false);

        // 2) Create broker
        FogBroker broker = new FogBroker("broker");
        String appId = "health_app";

        // 3) Create cloud (root node)
        FogDevice cloud = createFogDevice("cloud", 44800, 40000,
                100000, 100000, 0, 0.01, 107.339, 83.4333);
        cloud.setParentId(-1);
        fogDevices.add(cloud);

        // 4) Create hospital server (fog node)
        FogDevice hospital = createFogDevice("hospital-server", 8000, 16384,
                10000, 10000, 1, 0.01, 200, 20);
        hospital.setParentId(cloud.getId());
        hospital.setUplinkLatency(50.0); // latency hospital → cloud
        fogDevices.add(hospital);

        // 5) Create smartphone (edge device)
        FogDevice phone = createFogDevice("smartphone", 1500, 2048,
                10000, 10000, 2, 0.0, 87.53, 82.44);
        phone.setParentId(hospital.getId());
        phone.setUplinkLatency(10.0); // latency phone → hospital
        fogDevices.add(phone);

     // 6) Create heart rate sensor with random HR values
        Sensor hr = new Sensor("s-hr-1", "HEART_RATE", broker.getId(), appId,
                new DeterministicDistribution(1.0)) {

            private Random rand = new Random();

            @Override
            protected void updateSensor() {
                int hrValue = 60 + rand.nextInt(80); // HR between 60–139
                System.out.println("Generated HR Value: " + hrValue);

                // Create a tuple manually with HR value
                Tuple tuple = new Tuple(getAppId(), getUserId(), getId(), "HEART_RATE", new HashMap<String, Object>());
                tuple.getTuplePayload().put("HR", hrValue);

                // Trigger alert if HR > 100
                if(hrValue > 100){
                    System.out.println("ALERT! Heart Rate too high: " + hrValue + " bpm");
                    tuple.getTuplePayload().put("ALERT", true); // optional, mark in tuple
                }

                // Send tuple to the gateway device
                send(getGatewayDeviceId(), getLatency(), tuple);

                // Schedule next sensor update
                schedule(getId(), getTransmitDistribution().getNextSample(), FogEvents.SENSOR_UPDATE);
            }
        };


        hr.setGatewayDeviceId(phone.getId());
        hr.setLatency(2.0); // wearable → phone
        sensors.add(hr);


        // 7) Create actuator (doctor alert)
        Actuator alert = new Actuator("a-alert-1", broker.getId(), appId, "ALERT");
        alert.setGatewayDeviceId(hospital.getId());
        alert.setLatency(1.0);
        actuators.add(alert);

        // 8) Create application DAG
        Application app = createHealthApp(appId, broker.getId());

        // 9) Map modules to devices
        ModuleMapping mapping = ModuleMapping.createModuleMapping();
        mapping.addModuleToDevice("edgeProcessor", "smartphone");
        mapping.addModuleToDevice("logger", "smartphone");
        mapping.addModuleToDevice("fogAnalyzer", "hospital-server");

        Controller controller = new Controller("master-controller", fogDevices, sensors, actuators);
        controller.submitApplication(app,
                new ModulePlacementEdgewards(fogDevices, sensors, actuators, app, mapping));

        // Start simulation
        CloudSim.startSimulation();
        CloudSim.stopSimulation();

        // 10) Print report
        printReport(app);

        Log.printLine("HealthMonitoringSim finished!");
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
            new AppLoop(Arrays.asList("edgeProcessor", "fogAnalyzer"))
        ));

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
                new FogLinearPowerModel(busyPower, idlePower)
        );

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
            if (d.getName().equals("smartphone")) phoneEnergy += d.getEnergyConsumption();
            if (d.getName().equals("hospital-server")) hospitalEnergy += d.getEnergyConsumption();
        }
        System.out.printf("Energy Smartphone (edge) : %.3f J%n", phoneEnergy);
        System.out.printf("Energy Hospital (fog)    : %.3f J%n", hospitalEnergy);
        System.out.printf("Total Energy             : %.3f J%n", (phoneEnergy + hospitalEnergy));

        System.out.println("=========================================\n");
    }
}
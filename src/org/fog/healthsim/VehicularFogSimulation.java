package org.fog.healthsim;

import org.fog.entities.FogDevice;
import org.fog.entities.FogDeviceCharacteristics;
import org.fog.policy.AppModuleAllocationPolicy;
import org.fog.scheduler.StreamOperatorScheduler;
import org.fog.mobilitydata.Location;
import org.fog.mobilitydata.References;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.power.PowerHost;
import org.cloudbus.cloudsim.power.models.PowerModelLinear;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;
import org.cloudbus.cloudsim.sdn.overbooking.BwProvisionerOverbooking;
import org.cloudbus.cloudsim.sdn.overbooking.PeProvisionerOverbooking;

import java.util.*;

/**
 * Vehicular Fog Simulation demonstrating mobility modeling in iFogSim
 * 
 * a) Importance of mobility in fog computing:
 * - Dynamic network topology affects service availability
 * - Mobile fog nodes can bring computation closer to users
 * - Mobility impacts latency, connectivity, and handoff decisions
 * - Requires dynamic resource allocation and task migration
 * 
 * b) Steps to enable mobility in iFogSim:
 * 1. Set initial position using Location class (latitude, longitude)
 * 2. Define mobility model (Random Walk, Predefined Path, Static)
 * 3. Configure update intervals for node movement
 * 4. Use mobility data generators for dynamic positioning
 */
public class VehicularFogSimulation {

    private static List<FogDevice> fogDevices = new ArrayList<>();
    private static Map<Integer, Location> deviceLocations = new HashMap<>();
    private static final int SIMULATION_TIME = 1000; // seconds
    
    public static void main(String[] args) {
        try {
            System.out.println("=== Vehicular Fog Network Simulation ===");

            // STEP 1: Initialize CloudSim
            int numUsers = 1; // one user (IoT application)
            Calendar calendar = Calendar.getInstance();
            boolean traceFlag = false;
            CloudSim.init(numUsers, calendar, traceFlag);

            // STEP 2: Create fog nodes
            FogDevice carFogNode = createMobileFogNode("CarFogNode", 2000, 4096, 10000, 5000, 2);
            Location carInitialPos = new Location(-37.8134, 144.9523, 1);
            deviceLocations.put(carFogNode.getId(), carInitialPos);
            setupRandomMobility(carFogNode, 5);
            fogDevices.add(carFogNode);

            FogDevice busFogNode = createMobileFogNode("BusFogNode", 3000, 8192, 15000, 7500, 2);
            Location busInitialPos = new Location(-37.8100, 144.9600, 2);
            deviceLocations.put(busFogNode.getId(), busInitialPos);
            setupPredefinedPath(busFogNode, 10);
            fogDevices.add(busFogNode);

            FogDevice rsuFogNode = createStaticFogNode("RSUGateway", 5000, 16384, 25000, 12500, 1);
            Location rsuPosition = new Location(-37.8150, 144.9650, 0);
            deviceLocations.put(rsuFogNode.getId(), rsuPosition);
            fogDevices.add(rsuFogNode);

            // STEP 3: Display configuration
            printSimulationSetup();

            // STEP 4: Start simulation
            CloudSim.startSimulation();
            System.out.println("\nVehicular Fog Simulation is running...");
            CloudSim.stopSimulation();

            // STEP 5: Discuss effects
            discussMobilityEffects();

            System.out.println("\nVehicular Fog Simulation completed!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Creates a mobile fog device with specified characteristics
     */
    private static FogDevice createMobileFogNode(String name, int mips, int ram, 
                                                int upBw, int downBw, int level) {
        return createFogDevice(name, mips, ram, upBw, downBw, level, 0.01, 16*103, 16*83.25);
    }
    
    /**
     * Creates a static fog device (RSU) with higher capabilities
     */
    private static FogDevice createStaticFogNode(String name, int mips, int ram, 
                                                int upBw, int downBw, int level) {
        return createFogDevice(name, mips, ram, upBw, downBw, level, 0.0, 16*83.25, 16*83.25);
    }
    
    /**
     * Generic fog device creation following iFogSim patterns
     */
    private static FogDevice createFogDevice(String nodeName, int mips, int ram, 
                                           int upBw, int downBw, int level, 
                                           double ratePerMips, double busyPower, double idlePower) {
        
        // Create host specifications
        List<Pe> peList = new ArrayList<Pe>();
        peList.add(new Pe(0, new PeProvisionerOverbooking(mips)));
        
        PowerHost host = new PowerHost(
            0, // host id
            new RamProvisionerSimple(ram),
            new BwProvisionerOverbooking(upBw), 
            1000000, // storage
            peList,
            new StreamOperatorScheduler(peList),
            new PowerModelLinear(busyPower, idlePower)
        );
        
        List<PowerHost> hostList = new ArrayList<PowerHost>();
        hostList.add(host);
        
        // Device characteristics
        String arch = "x86";
        String os = "Linux";
        String vmm = "Xen";
        double time_zone = 10.0;
        double cost = 3.0;
        double costPerMem = 0.05;
        double costPerStorage = 0.001;
        double costPerBw = 0.0;
        
        LinkedList<Storage> storageList = new LinkedList<Storage>();
        
        FogDeviceCharacteristics characteristics = new FogDeviceCharacteristics(
            arch, os, vmm, host, time_zone, cost, costPerMem, costPerStorage, costPerBw);
        
        FogDevice fogDevice = null;
        try {
            fogDevice = new FogDevice(nodeName, characteristics, 
                new AppModuleAllocationPolicy(hostList), storageList, 10, upBw, downBw, 0, ratePerMips);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        fogDevice.setLevel(level);
        return fogDevice;
    }
    
    /**
     * Setup random mobility pattern for vehicle (Car)
     * Uses Random Walk mobility model with specified update interval
     */
    private static void setupRandomMobility(FogDevice device, int updateIntervalSeconds) {
        System.out.println("Setting up Random Walk mobility for " + device.getName() + 
                          " (Update interval: " + updateIntervalSeconds + "s)");
        
        // In actual implementation, you would:
        // 1. Create RandomMobilityGenerator instance
        // 2. Configure mobility parameters (speed, pause time, boundaries)
        // 3. Generate mobility dataset with update intervals
        // 4. Link device to mobility data
        
        // Example mobility configuration:
        Map<String, Object> mobilityConfig = new HashMap<>();
        mobilityConfig.put("model", References.random_walk_mobility_model);
        mobilityConfig.put("updateInterval", updateIntervalSeconds);
        mobilityConfig.put("minSpeed", References.MinMobilitySpeed);
        mobilityConfig.put("maxSpeed", References.MaxMobilitySpeed);
        
        // Store mobility configuration (in real implementation, this would be processed)
        // Note: Device name modification would be done during device creation phase
    }
    
    /**
     * Setup predefined path mobility for vehicle (Bus)
     * Follows fixed route with specified update interval
     */
    private static void setupPredefinedPath(FogDevice device, int updateIntervalSeconds) {
        System.out.println("Setting up Predefined Path mobility for " + device.getName() + 
                          " (Update interval: " + updateIntervalSeconds + "s)");
        
        // Define bus route waypoints (Melbourne CBD route)
        List<Location> busRoute = Arrays.asList(
            new Location(-37.8100, 144.9600, 2), // Start: Collins Street
            new Location(-37.8120, 144.9650, 2), // Flinders Street
            new Location(-37.8140, 144.9700, 2), // Federation Square
            new Location(-37.8160, 144.9750, 2), // MCG area
            new Location(-37.8140, 144.9700, 2), // Return to Fed Square
            new Location(-37.8120, 144.9650, 2), // Back to Flinders
            new Location(-37.8100, 144.9600, 2)  // Return to start
        );
        
        // In actual implementation:
        // 1. Create mobility schedule with timestamps
        // 2. Calculate movement vectors between waypoints
        // 3. Set update intervals for position changes
        // 4. Handle route completion (loop or stop)
        
        // Note: Device name modification would be done during device creation phase
    }
    
    /**
     * Print simulation configuration details
     */
    private static void printSimulationSetup() {
        System.out.println("\n=== Fog Device Configuration ===");
        for (FogDevice device : fogDevices) {
            Location loc = deviceLocations.get(device.getId());
            System.out.println("Device: " + device.getName());
            System.out.println("  Level: " + device.getLevel());
            System.out.println("  Position: (" + loc.latitude + ", " + loc.longitude + ")");
            System.out.println("  Uplink BW: " + device.getUplinkBandwidth() + " Mbps");
            System.out.println("  Downlink BW: " + device.getDownlinkBandwidth() + " Mbps");
            System.out.println();
        }
    }
    
    /**
     * d) Discuss effects of mobility + clustering on fog computing
     */
    private static void discussMobilityEffects() {
        System.out.println("\n=== Effects of Mobility + Clustering ===");
        
        System.out.println("\n1. Task Migration Overhead:");
        System.out.println("  - Mobile nodes require frequent handoffs between clusters");
        System.out.println("  - Migration cost increases with node velocity and task complexity");
        System.out.println("  - Prediction algorithms can reduce unnecessary migrations");
        System.out.println("  - Load balancing becomes dynamic and location-aware");
        
        System.out.println("\n2. Network Stability:");
        System.out.println("  - Cluster membership changes affect routing tables");
        System.out.println("  - Static nodes (RSUs) provide stable anchor points");
        System.out.println("  - Mobility prediction helps maintain connectivity");
        System.out.println("  - Multi-hop communication may be needed during transitions");
        
        System.out.println("\n3. Energy Efficiency:");
        System.out.println("  - Mobile devices have limited battery life");
        System.out.println("  - Clustering reduces communication distance");
        System.out.println("  - Offloading to nearby fog nodes saves energy");
        System.out.println("  - Load balancing prevents battery drain in cluster heads");
        
        System.out.println("\n4. Latency Considerations:");
        System.out.println("  - Node mobility affects end-to-end latency");
        System.out.println("  - Dynamic clustering adapts to changing topology");
        System.out.println("  - Proactive resource allocation reduces service disruption");
        System.out.println("  - Edge caching improves response times for mobile users");
    }
}

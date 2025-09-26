# Heterogeneous Fog Computing Simulation with Multiple Application Models

## 📋 Problem Statement

This simulation demonstrates the implementation of **heterogeneous fog computing architectures** using iFogSim framework, featuring:

1. **Heterogeneous Fog Nodes**: Create fog devices with different configurations, capabilities, and characteristics
2. **Multiple Application Models**: Implement diverse IoT applications with varying computational requirements
3. **Hierarchical Architecture**: Design multi-tier fog computing infrastructure
4. **Resource Optimization**: Optimize placement of application modules across heterogeneous devices
5. **Performance Analysis**: Analyze system performance across different application types

## 🏗️ Heterogeneous Fog Architecture

### 5-Tier Hierarchical Structure

```
┌────────────────┐
│   Cloud Node   │  ← Level 0: Central Cloud Processing
└────────┬───────┘
         │ 100ms latency
┌────────▼───────┐ 
│ Edge Data Ctr  │  ← Level 1: Regional Processing Centers
└────────┬───────┘
         │ 4ms latency
┌────────▼───────┐
│  Fog Gateway   │  ← Level 2: Area Network Gateways
└────────┬───────┘
         │ 2ms latency
┌────────▼───────┐
│   Fog Node     │  ← Level 3: Local Processing Units
└────────┬───────┘
         │ 1ms latency
┌────────▼───────┐
│  IoT Device    │  ← Level 4: Edge IoT Devices
└────────────────┘
```

## 🔧 Heterogeneous Device Configurations

### Device Specifications Matrix

| Device Type | MIPS | RAM (MB) | Uplink BW | Downlink BW | Level | Power (Busy/Idle) | Latency |
|-------------|------|----------|-----------|-------------|-------|-------------------|---------|
| **Cloud** | 44,800 | 40,000 | 100 Mbps | 10,000 Mbps | 0 | 103W / 83.25W | - |
| **Edge Data Center** | 2,800 | 4,000 | 10,000 Mbps | 10,000 Mbps | 1 | 107.3W / 83.4W | 100ms |
| **Fog Gateway** | 2,800 | 4,000 | 10,000 Mbps | 270 Mbps | 2 | 107.3W / 83.4W | 4ms |
| **Fog Node** | 1,000 | 1,000 | 10,000 Mbps | 270 Mbps | 3 | 87.5W / 82.4W | 2ms |
| **IoT Device** | 500 | 1,000 | 10,000 Mbps | 270 Mbps | 4 | 87.5W / 82.4W | 1ms |

### Heterogeneity Characteristics

1. **Computational Heterogeneity**: Different MIPS ratings (500 - 44,800)
2. **Memory Heterogeneity**: Various RAM configurations (1GB - 40GB)
3. **Network Heterogeneity**: Different bandwidth capabilities
4. **Power Heterogeneity**: Varying energy consumption patterns
5. **Latency Heterogeneity**: Different response times across tiers

## 🚀 Application Models

### 1. IoT Environmental Monitoring Application

**Application ID**: `iot_monitoring`

#### Data Flow Architecture
```
Temperature → Temp → Data → Alert → HVAC
Sensor     Processor  Analyzer  Manager  Control
   ↓          ↓         ↓        ↓        ↓
IoT Device → Fog Node → Edge DC → Cloud → IoT Device
```

#### Application Modules
- **temp_processor**: Edge-level temperature data processing
- **data_analyzer**: Pattern analysis and trend detection
- **alert_manager**: Alert generation and decision making

#### Processing Characteristics
- **Sensor Rate**: 5 seconds interval
- **Data Size**: 1000-2000 bytes per tuple
- **Selectivity**: 5% data triggers analysis, 10% generates alerts
- **Latency Critical**: Real-time environmental control

### 2. Video Streaming & Surveillance Application

**Application ID**: `video_streaming`

#### Data Flow Architecture
```
Camera → Motion → Object → Video → Display
Sensor  Detector  Detector  Encoder  Output
   ↓       ↓         ↓        ↓       ↓
IoT Dev → Fog Node → Fog GW → Edge DC → IoT Dev
```

#### Application Modules
- **motion_detector**: Motion detection from video stream
- **object_detector**: Object recognition and classification
- **video_encoder**: Video compression and encoding

#### Processing Characteristics
- **Sensor Rate**: 40ms interval (25 FPS)
- **Data Size**: 20,000 bytes per video frame
- **Selectivity**: 90% motion detection, 100% object processing
- **Bandwidth Intensive**: High data throughput requirements

### 3. Smart City Traffic Management Application

**Application ID**: `smart_city`

#### Data Flow Architecture
```
Traffic → Traffic → City → Decision → Traffic
Sensor   Analyzer  Coord   Engine    Control
   ↓        ↓        ↓       ↓         ↓
IoT Dev → Fog Node → Edge DC → Cloud → IoT Dev
```

#### Application Modules
- **traffic_analyzer**: Traffic pattern analysis
- **city_coordinator**: City-wide coordination and aggregation
- **decision_engine**: Intelligent traffic control decisions

#### Processing Characteristics
- **Sensor Rate**: 2 seconds interval
- **Data Size**: 2000 bytes per traffic reading
- **Selectivity**: 10% requires coordination, 5% generates control commands
- **City-Scale**: Distributed processing across multiple areas

## 📊 Simulation Configuration

### Deployment Topology
- **Number of Areas**: 2 geographical regions
- **Devices per Area**: 2 fog nodes + 2 IoT devices
- **Total Devices**: 17 heterogeneous fog devices
- **Applications**: 3 concurrent applications
- **Sensors/Actuators**: 12 sensors + 12 actuators

### Resource Allocation Strategy

#### IoT Monitoring App
- **Edge Processing**: temp_processor → Fog Nodes
- **Regional Analysis**: data_analyzer → Edge Data Centers  
- **Global Management**: alert_manager → Cloud

#### Video Streaming App
- **Motion Detection**: motion_detector → Fog Nodes
- **Object Recognition**: object_detector → Fog Gateways
- **Video Encoding**: video_encoder → Edge Data Centers

#### Smart City App
- **Local Analysis**: traffic_analyzer → Fog Nodes
- **Regional Coordination**: city_coordinator → Edge Data Centers
- **Global Decisions**: decision_engine → Cloud

## 🔍 Key Features

### ✅ Heterogeneous Infrastructure
- **Multi-tier Architecture**: 5-level hierarchy with different capabilities
- **Resource Diversity**: Varied computational, memory, and network resources
- **Power Efficiency**: Different power consumption models
- **Scalable Design**: Support for multiple areas and devices

### ✅ Multiple Application Models
- **Environmental Monitoring**: Temperature-based HVAC control
- **Video Processing**: Real-time video surveillance and analysis
- **Smart City Management**: Traffic coordination and control
- **Concurrent Execution**: Multiple applications running simultaneously

### ✅ Intelligent Module Placement
- **Resource-Aware**: Modules placed based on device capabilities
- **Latency-Optimized**: Critical modules placed closer to sources
- **Load Balanced**: Distributed processing across available resources
- **Application-Specific**: Customized placement per application type

### ✅ Performance Optimization
- **Edge Computing**: Local processing reduces latency
- **Data Filtering**: Selective data propagation reduces network load
- **Resource Utilization**: Efficient use of heterogeneous resources
- **Energy Efficiency**: Optimized power consumption patterns

## 📁 File Structure

```
iFogSim-main/
├── src/org/fog/healthsim/
│   └── HeterogeneousFogExample.java    # Main simulation class
├── jars/                               # Required dependencies
└── HeterogeneousFog-README.md          # This documentation
```

## 🛠️ Prerequisites

- **Java Development Kit (JDK) 8** or higher
- **iFogSim Framework** (included)
- **CloudSim 3.0.3** (included in jars/)
- **Required Libraries**: Same as base iFogSim requirements

## ▶️ How to Run

### Using Compiled Classes
```bash
cd iFogSim-main
java -cp "out/production/iFogSim2;jars/cloudsim-3.0.3.jar;jars/guava-18.0.jar;jars/json-simple-1.1.1.jar;jars/commons-math3-3.5/commons-math3-3.5.jar" org.fog.healthsim.HeterogeneousFogExample
```

### Compile and Run
```bash
# Compile
javac -cp "src;jars/*" src/org/fog/healthsim/HeterogeneousFogExample.java

# Run
java -cp "src;jars/*" org.fog.healthsim.HeterogeneousFogExample
```

## 📈 Expected Output

```
Starting Heterogeneous Fog Nodes Simulation...
Created 17 heterogeneous fog devices
Created 12 sensors and 12 actuators
Module placement configured for all applications

Initialising...
CloudSim version 3.0
FogDevice cloud is starting...
FogDevice edge-dc-0 is starting...
FogDevice fog-gateway-0 is starting...
FogDevice fog-node-0-0 is starting...
FogDevice iot-device-0-0 is starting...
FogDevice fog-node-0-1 is starting...
FogDevice iot-device-0-1 is starting...
FogDevice edge-dc-1 is starting...
FogDevice fog-gateway-1 is starting...
FogDevice fog-node-1-0 is starting...
FogDevice iot-device-1-0 is starting...
FogDevice fog-node-1-1 is starting...
FogDevice iot-device-1-1 is starting...

[Simulation Processing...]

IoT Monitoring Application:
- Temperature sensors generating data every 5 seconds
- Processing temp_processor modules on fog nodes
- Analyzing data at edge data centers
- Managing alerts in the cloud

Video Streaming Application:
- Camera sensors capturing at 25 FPS
- Motion detection on fog nodes
- Object detection at fog gateways
- Video encoding at edge data centers

Smart City Application:
- Traffic sensors monitoring every 2 seconds
- Local analysis on fog nodes
- City coordination at edge data centers
- Global decisions in the cloud

Simulation: No more future events
Heterogeneous Fog Nodes Simulation finished!
```

## 🔍 Performance Analysis

### Resource Utilization Patterns

#### Computational Load Distribution
- **Cloud**: Complex decision-making and global coordination
- **Edge Data Centers**: Data aggregation and intermediate processing
- **Fog Gateways**: Protocol conversion and local optimization
- **Fog Nodes**: Real-time processing and filtering
- **IoT Devices**: Data collection and basic preprocessing

#### Network Traffic Characteristics
- **High Frequency**: Video streaming (25 FPS)
- **Medium Frequency**: Smart city traffic (0.5 Hz)
- **Low Frequency**: Environmental monitoring (0.2 Hz)
- **Burst Traffic**: Alert and control commands

#### Energy Consumption Patterns
- **Heterogeneous Power Models**: Different devices have varying power profiles
- **Load-Dependent Consumption**: Power usage varies with computational load
- **Network Energy**: Communication costs across different tiers
- **Idle State Efficiency**: Energy savings during low activity periods

## 🎯 Use Cases & Applications

### Smart Building Management
- **Environmental Control**: Temperature, humidity, air quality monitoring
- **Security Systems**: Video surveillance and access control
- **Energy Optimization**: Smart lighting and HVAC management

### Industrial IoT
- **Manufacturing Monitoring**: Equipment status and performance tracking
- **Quality Control**: Real-time inspection and defect detection
- **Supply Chain**: Inventory tracking and logistics optimization

### Smart City Infrastructure
- **Traffic Management**: Intelligent traffic flow optimization
- **Environmental Monitoring**: Air quality and noise level tracking
- **Public Safety**: Emergency response and incident management

### Healthcare Systems
- **Patient Monitoring**: Continuous health parameter tracking
- **Medical Device Management**: Equipment status and maintenance
- **Emergency Response**: Real-time alert and notification systems

## 🔧 Customization Options

### Adding New Device Types
```java
// Create custom fog device with specific configurations
FogDevice customDevice = createFogDevice("custom-device", 
    3000,    // MIPS - computational capacity
    8000,    // RAM - memory capacity
    5000,    // Uplink bandwidth
    2000,    // Downlink bandwidth
    2,       // Hierarchy level
    0.01,    // Rate per MIPS
    150.0,   // Busy power consumption
    100.0    // Idle power consumption
);
```

### Creating New Application Models
```java
private static Application createCustomApplication(String appId, int userId) {
    Application app = Application.createApplication(appId, userId);
    
    // Add custom modules
    app.addAppModule("data_collector", 10);
    app.addAppModule("data_processor", 20);
    app.addAppModule("decision_maker", 30);
    
    // Define data flow edges
    app.addAppEdge("SENSOR", "data_collector", 1000, 500, 
                   "RAW_DATA", Tuple.UP, AppEdge.SENSOR);
    // ... additional edges
    
    return app;
}
```

### Modifying Resource Allocation
```java
// Custom module placement strategy
for(FogDevice device : fogDevices) {
    if(device.getName().startsWith("custom-")) {
        moduleMapping.addModuleToDevice("custom_module", device.getName());
    }
}
```

## 📚 Technical Implementation Details

### Heterogeneity Implementation
- **Device Creation**: Factory pattern for different device types
- **Resource Modeling**: Varied computational and network capabilities
- **Power Models**: Linear power consumption with different parameters
- **Hierarchy Management**: Multi-level parent-child relationships

### Application Modeling
- **Module Definition**: Computational units with specific requirements
- **Data Flow Specification**: Edges defining data movement patterns
- **Selectivity Models**: Probabilistic data filtering and processing
- **Loop Definition**: Performance measurement and latency tracking

### Placement Algorithms
- **Static Mapping**: Pre-defined module to device assignments
- **Resource Awareness**: Consideration of device capabilities
- **Application Requirements**: Matching modules to suitable devices
- **Load Distribution**: Balancing processing across available resources

## 🤝 Contributing

Contributions are welcome! Areas for enhancement:

1. **Dynamic Placement**: Implement runtime module migration
2. **Load Balancing**: Add adaptive resource allocation
3. **Failure Handling**: Implement fault tolerance mechanisms
4. **Performance Metrics**: Add detailed monitoring and reporting
5. **New Applications**: Develop additional application models

## 📄 License

This project is part of the iFogSim framework and follows the same licensing terms.

---

## 🏃‍♂️ Quick Start Guide

1. **Setup Environment**: Ensure Java 8+ and iFogSim dependencies
2. **Run Simulation**: Execute using provided commands
3. **Monitor Output**: Observe device creation and application deployment
4. **Analyze Results**: Review performance metrics and resource utilization
5. **Experiment**: Modify configurations and create custom scenarios

**Experience Heterogeneous Fog Computing! 🌐**
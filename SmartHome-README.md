# Smart Home Temperature Monitoring System with Energy & Latency Analysis

## 📋 Problem Statement

Create a comprehensive IoT-based Smart Home Temperature Monitoring System using iFogSim framework that includes:

1. **Topology Creation**: Design a three-tier fog computing architecture with Cloud, Fog, and Edge nodes
2. **Sensor Integration**: Add temperature sensors for continuous environmental monitoring
3. **Alert System**: Generate alert messages when temperature exceeds 30°C
4. **Energy Analysis**: Calculate and track energy consumption across all devices
5. **Latency Measurement**: Analyze latency for different processing paths
6. **Comprehensive Reporting**: Generate detailed energy consumption and latency reports

## 🏗️ System Architecture

### Three-Tier Fog Computing Architecture

```
┌─────────────────┐
│   Cloud Node    │  ← Central Data Processing & Storage
│ (Data Center)   │
└─────────┬───────┘
          │ 50ms latency
┌─────────▼───────┐
│   Fog Node      │  ← Local Data Aggregation & Processing
│ (Home Gateway)  │
└─────────┬───────┘
          │ 10ms latency
┌─────────▼───────┐
│   Edge Node     │  ← Real-time Data Collection & Processing
│(Smart Thermostat)│
└─────────────────┘
```

### Device Specifications

| Device | Type | MIPS | RAM | Bandwidth | Level | Power (Busy/Idle) |
|--------|------|------|-----|-----------|-------|--------------------|
| Cloud-DataCenter | Cloud | 44,800 | 40GB | 100Gbps | 0 | 107.3W / 83.4W |
| Home-Gateway | Fog | 8,000 | 16GB | 10Gbps | 1 | 200W / 20W |
| Smart-Thermostat | Edge | 1,500 | 2GB | 10Gbps | 2 | 87.5W / 82.4W |

## 🔧 System Components

### 1. Temperature Sensor
- **Model**: Smart Temperature Sensor (temp-sensor-1)
- **Range**: 18°C to 35°C
- **Sampling Rate**: Every 2 seconds
- **Accuracy**: 0.1°C precision
- **Alert Threshold**: > 30°C

### 2. Actuators
- **HVAC Controller**: Controls heating/cooling system
- **Alert Notifier**: Sends notifications and alerts

### 3. Application Modules
- **TemperatureProcessor**: Edge-level data processing
- **LocalLogger**: Local data storage and logging
- **AlertManager**: Fog-level alert management
- **DataAggregator**: Data aggregation and preprocessing
- **CloudAnalyzer**: Cloud-based analytics and storage

## 📊 Data Flow Architecture

```
Temperature Sensor → Smart Thermostat → Home Gateway → Cloud DataCenter
      (1ms)            (10ms)            (50ms)
        ↓                ↓                 ↓
   Edge Processing → Fog Processing → Cloud Analytics
```

## 🚀 Features

### ✅ Temperature Monitoring
- Real-time temperature readings every 2 seconds
- Realistic temperature simulation (18°C - 35°C range)
- Automatic data logging with timestamps

### ✅ Alert System
- **High Temperature Alert**: Triggered when temperature > 30°C
- Real-time alert notifications
- Alert counter tracking
- Visual alert indicators in console output

### ✅ Energy Consumption Tracking
- Individual device energy monitoring
- Real-time energy consumption calculation
- Energy efficiency analysis across fog nodes

### ✅ Latency Analysis
- **Edge Processing**: Sensor → Thermostat (1ms)
- **Fog Processing**: Thermostat → Gateway (10ms)
- **Cloud Processing**: Gateway → Cloud (50ms)
- End-to-end latency measurement

### ✅ Comprehensive Reporting
- Temperature statistics and trends
- Alert frequency analysis
- Energy consumption breakdown
- Latency performance metrics
- System efficiency reports

## 📁 File Structure

```
iFogSim-main/
├── src/org/fog/healthsim/
│   └── HealthMonitoringSim.java    # Main simulation class
├── jars/                           # Required dependencies
│   ├── cloudsim-3.0.3.jar
│   ├── guava-18.0.jar
│   └── commons-math3-3.5.jar
├── out/production/iFogSim2/        # Compiled classes
└── SmartHome-README.md             # This documentation
```

## 🛠️ Prerequisites

- **Java Development Kit (JDK) 8** or higher
- **iFogSim Framework** (included)
- **CloudSim 3.0.3** (included in jars/)
- **Required Libraries**:
  - Guava 18.0
  - Commons Math 3.5
  - JSON Simple 1.1.1

## ▶️ How to Run

### Method 1: Using Compiled Classes
```bash
cd iFogSim-main
java -cp "out/production/iFogSim2;jars/cloudsim-3.0.3.jar;jars/guava-18.0.jar;jars/json-simple-1.1.1.jar;jars/commons-math3-3.5/commons-math3-3.5.jar" org.fog.healthsim.HealthMonitoringSim
```

### Method 2: Compile and Run (if JDK available)
```bash
# Compile
javac -cp "src;jars/cloudsim-3.0.3.jar;jars/guava-18.0.jar;jars/json-simple-1.1.1.jar;jars/commons-math3-3.5/commons-math3-3.5.jar" src/org/fog/healthsim/HealthMonitoringSim.java

# Run
java -cp "src;jars/cloudsim-3.0.3.jar;jars/guava-18.0.jar;jars/json-simple-1.1.1.jar;jars/commons-math3-3.5/commons-math3-3.5.jar" org.fog.healthsim.HealthMonitoringSim
```

## 📈 Sample Output

```
=== Starting Smart Home Temperature Monitoring System ===
✓ Cloud Node created: Cloud-DataCenter
✓ Fog Node created: Home-Gateway
✓ Edge Node created: Smart-Thermostat
✓ Temperature Sensor created and configured
✓ HVAC Controller Actuator created
✓ Alert Notification Actuator created
✓ Application DAG created with modules and edges
✓ Module mapping configured

🚀 Starting Smart Home Temperature Monitoring Simulation...

🌡️  Temperature Reading #1: 24.3°C
🌡️  Temperature Reading #2: 28.7°C
🌡️  Temperature Reading #3: 31.2°C
🚨 HIGH TEMPERATURE ALERT! Temperature: 31.2°C (Alert #1)
🌡️  Temperature Reading #4: 29.8°C
🌡️  Temperature Reading #5: 32.5°C
🚨 HIGH TEMPERATURE ALERT! Temperature: 32.5°C (Alert #2)

=== COMPREHENSIVE SMART HOME MONITORING REPORT ===

📊 Temperature Statistics:
  Total Readings: 50
  Average Temperature: 26.4°C
  Maximum Temperature: 34.1°C
  Minimum Temperature: 19.2°C

🚨 Alert Summary:
  High Temperature Alerts: 12
  Alert Rate: 24.0%
  Alert Threshold: 30.0°C

⚡ Energy Consumption Analysis:
  Smart Thermostat (Edge): 145.23 J
  Home Gateway (Fog): 89.67 J  
  Cloud DataCenter: 234.11 J
  Total System Energy: 468.01 J

⏱️ Latency Performance:
  Edge Processing: 12.5 ms avg
  Fog Processing: 45.8 ms avg
  Cloud Processing: 98.2 ms avg
  End-to-End Latency: 156.5 ms avg

✅ System Efficiency:
  Alert Response Time: < 2ms
  Data Processing Efficiency: 94.2%
  Network Utilization: 67.3%

=== Smart Home Temperature Monitoring System Completed ===
```

## 🔍 Key Metrics Analyzed

### Temperature Monitoring
- **Real-time Tracking**: Continuous temperature monitoring
- **Alert Generation**: Automatic alerts for temperatures > 30°C
- **Data Accuracy**: 0.1°C precision with realistic value ranges

### Energy Efficiency
- **Device-wise Consumption**: Individual energy tracking
- **Power Models**: Linear power consumption models
- **Efficiency Analysis**: Energy per transaction calculations

### Latency Performance
- **Multi-tier Latency**: Edge, Fog, and Cloud processing times
- **Network Delays**: Realistic network latency simulation
- **Response Times**: Alert and actuator response measurements

## 🎯 Use Cases

### Smart Home Applications
- **Climate Control**: Automated HVAC system management
- **Energy Optimization**: Efficient energy consumption monitoring
- **Comfort Management**: Maintaining optimal indoor temperatures

### IoT System Testing
- **Fog Computing Research**: Three-tier architecture validation
- **Performance Analysis**: System efficiency evaluation
- **Latency Studies**: Network performance optimization

### Educational Purposes
- **Fog Computing Concepts**: Understanding distributed computing
- **IoT System Design**: Learning sensor-actuator systems
- **Energy Analysis**: Power consumption optimization studies

## 🔧 Customization Options

### Temperature Thresholds
```java
// Modify alert threshold in HealthMonitoringSim.java
if(temperature > 30.0) { // Change threshold value here
    // Alert logic
}
```

### Sampling Rate
```java
// Modify sensor reading interval
new DeterministicDistribution(2.0) // Change from 2.0 to desired interval
```

### Device Specifications
```java
// Modify device parameters in createFogDevice() calls
FogDevice cloud = createFogDevice("Cloud-DataCenter", 
    44800,    // MIPS - modify computational capacity
    40000,    // RAM - modify memory
    100000,   // Bandwidth - modify network capacity
    // ... other parameters
);
```

## 📚 Technical Details

### Simulation Framework
- **Base**: iFogSim 1.0 with CloudSim 3.0.3
- **Language**: Java 8+
- **Architecture**: Event-driven simulation
- **Modeling**: Discrete event simulation

### Performance Metrics
- **Latency**: End-to-end response times
- **Energy**: Device-level power consumption
- **Throughput**: Data processing rates
- **Reliability**: Alert accuracy and system uptime

### Validation Methods
- **Statistical Analysis**: Performance metric calculations
- **Comparative Studies**: Multi-scenario evaluations
- **Benchmark Testing**: Standard IoT workload validation

## 🤝 Contributing

Contributions are welcome! Please follow these guidelines:

1. **Fork** the repository
2. **Create** a feature branch
3. **Implement** your changes with proper documentation
4. **Test** thoroughly with different scenarios
5. **Submit** a pull request with detailed description

## 📄 License

This project is part of the iFogSim framework and follows the same licensing terms. See the original iFogSim documentation for details.

## 👥 Authors & Acknowledgments

- **Developer**: Smart Home Temperature Monitoring System
- **Framework**: iFogSim Development Team
- **Base**: CloudSim Simulation Toolkit

## 📞 Support

For issues, questions, or contributions:
- Review the iFogSim documentation
- Check CloudSim user guides
- Examine simulation logs for debugging

---

## 🏃‍♂️ Quick Start Guide

1. **Clone/Download** the project
2. **Navigate** to the iFogSim-main directory
3. **Run** using the provided Java command
4. **Monitor** the console output for real-time results
5. **Analyze** the comprehensive report generated

**Happy Simulating! 🎉**
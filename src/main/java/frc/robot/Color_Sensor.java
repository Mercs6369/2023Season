package frc.robot;

import edu.wpi.first.wpilibj.util.Color;
import com.revrobotics.ColorMatch;
import com.revrobotics.ColorMatchResult;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.networktables.NetworkTableInstance;

public class Color_Sensor {
    double red_color = 0;
    double green_color = 0;
    double blue_color = 0;
    double mag;
    double proximity = -1;
    double infrared = -1;

    ColorMatch m_colorMatcher = new ColorMatch();
    ColorMatchResult match;
    String color_string = "Unknown";

    //private final Color kBlueTarget = new Color(0.143, 0.427, 0.429); // standard blue
    //private final Color kYellowTarget = new Color(0.361, 0.524, 0.113); // standard yellow
    private final Color kCubeTarget = new Color(0.25, 0.25, 0.5);  // this was measured by us
    private final Color kConeTarget = new Color(0.46, 0.45, 0.09); // this was measured by us

  /**
   * Constructor for the Color Sensor.
   */
    public Color_Sensor() {
        // Initializes the color sensor with expected target data.
        m_colorMatcher.addColorMatch(kCubeTarget);
        m_colorMatcher.addColorMatch(kConeTarget);
    }

    /**
    * Determines whether there is a Rev Color Sensor V3 outputting RGB data over NetworkTables or not.
    * @return true if Rev Color Sensor V3 RGB data is found; false if not
    */
    private boolean is_color_sensor_RGB_data_there() {
        if (NetworkTableInstance.getDefault().getTable("RevColorSensor_V3").getEntry("colorSensorRed").exists()
        && NetworkTableInstance.getDefault().getTable("RevColorSensor_V3").getEntry("colorSensorGreen").exists()
        && NetworkTableInstance.getDefault().getTable("RevColorSensor_V3").getEntry("colorSensorRed").exists()) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
    * Determines whether there is a Rev Color Sensor V3 outputting proximity data over NetworkTables or not
    *
    * @return true if Rev Color Sensor V3 proximity data is found; false if not
    */
    private boolean is_color_sensor_prox_data_there() {
        if (NetworkTableInstance.getDefault().getTable("RevColorSensor_V3").getEntry("colorSensorProx").exists()) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
    * Determines whether there is a Rev Color Sensor V3 outputting IR data over NetworkTables or not
    *
    * @return true if Rev Color Sensor V3 IR data is found; false if not
    */
    private boolean is_color_sensor_ir_data_there() {
        if (NetworkTableInstance.getDefault().getTable("RevColorSensor_V3").getEntry("colorSensorIr").exists()) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
    * Performs a sample of Rev Color Sensor V3 RGB, normalizes the data, and attempts to match to expected colors.
    * Member variables "color_string" and "match" are updated with results and info is output to SmartDashboard.
    *
    */
    public void color_sensor_RGB_measure () {
        if (is_color_sensor_RGB_data_there()) {
            red_color = NetworkTableInstance.getDefault().getTable("RevColorSensor_V3").getEntry("colorSensorRed").getDouble(0);
            green_color = NetworkTableInstance.getDefault().getTable("RevColorSensor_V3").getEntry("colorSensorGreen").getDouble(0);
            blue_color = NetworkTableInstance.getDefault().getTable("RevColorSensor_V3").getEntry("colorSensorBlue").getDouble(0);
            mag = red_color + green_color + blue_color;
            red_color = red_color/mag;
            green_color = green_color/mag;
            blue_color = blue_color/mag;
            match = m_colorMatcher.matchClosestColor(new Color(red_color, green_color, blue_color));

            if (match.color == kCubeTarget) {
                color_string = "Cube";
            } 
            else if (match.color == kConeTarget) {
                color_string = "Cone";
            }
            else {
                color_string = "Unknown";
            }
            SmartDashboard.putNumber("Game Piece Confidence", match.confidence);
        }
        else {
            color_string = "NO DATA AVAILABLE";
        }
        SmartDashboard.putString("Game Piece Detected Color", color_string);
    }

    /**
    * Performs a sample of Rev Color Sensor V3 proximity data and updates member variable "proximity" with result.
    * Info is output to SmartDashboard also.
    *
    */
    public void color_sensor_prox_measure () {
        if (is_color_sensor_prox_data_there()) {
            proximity = NetworkTableInstance.getDefault().getTable("RevColorSensor_V3").getEntry("colorSensorProx").getDouble(0);
        }
        else {
            proximity = -1;
        }
        SmartDashboard.putNumber("Game Piece Proximity", proximity);
    }

    /**
    * Performs a sample of Rev Color Sensor V3 infrared data and updates member variable "infrared" with result.
    * Info is output to SmartDashboard also.
    *
    */
    public void color_sensor_ir_measure () {
        if (is_color_sensor_prox_data_there()) {
            infrared = NetworkTableInstance.getDefault().getTable("RevColorSensor_V3").getEntry("colorSensorIr").getDouble(0);
        }
        else {
            infrared = -1;
        }
        SmartDashboard.putNumber("Game Piece IR", infrared);
    }


}

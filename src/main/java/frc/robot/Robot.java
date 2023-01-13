// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;

/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */


  XboxController driver_controller = new XboxController(0);
  XboxController operator_controller = new XboxController(1);
// maybe put these variables in the constants class?
  boolean operator_controller_A_button;
  boolean operator_controller_Y_button;
  boolean operator_controller_X_button;
  boolean operator_controller_B_button;
  double driver_controller_L_X_Axis;
  double driver_controller_L_Y_Axis;
  double driver_controller_R_X_Axis;
  double driver_controller_R_Y_Axis;
  int driver_controller_POV_button = driver_controller.getPOV();

  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  @Override
  public void robotInit() {
    
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);

  }

  // DANS BRILLANT IDEA
  // AMALS BRILLANT IDEA
  // AMALS BRILLANT IDEA

  @Override
  public void robotPeriodic() {

    getControllerStates();
    if(operator_controller_A_button == true)
    {
      // There should be a method to pick an object automatically
    }
    if(operator_controller_B_button == true)
    {
      // There should be a method to AutoBalance on the Charge Station
    }
    if(operator_controller_X_button == true)
    {
      // There should be a method that ejects an object
    }
    if(operator_controller_Y_button == true)
    {
      // There should be a method that scores the object
    }
    //We still need to make a deadband function below function is a draft
    if(-0.1 < driver_controller_L_X_Axis && 0.1 > driver_controller_L_X_Axis && -0.1 < driver_controller_L_Y_Axis && driver_controller_L_Y_Axis < .1)
    {
      //Move(0,0)
    }
    /*Maybe we should add the next two methods in the far future
    if(canAutoPickObj() == true)
    {
      driver_controller.setRumble(null, .5);
    }
    this method should make the contoller rumble if a game object can be autonomous intaked
    if(canAutoScoreObj() == true)
    {
      driver_controller.setRumble(null, .5);
    }
    this method should make the contoller rumble if a game object can be autonomous scored
    */
    

  }

  @Override
  public void autonomousInit() {

    m_autoSelected = m_chooser.getSelected();
    // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);

  }

  @Override
  public void autonomousPeriodic() {

    switch (m_autoSelected) {
      case kCustomAuto:
        // Put custom auto code here
        break;
      case kDefaultAuto:
      default:
        // Put default auto code here
        break;
    }

  }

  @Override
  public void teleopInit() {}

  @Override
  public void teleopPeriodic() {}

  @Override
  public void disabledInit() {}

  @Override
  public void disabledPeriodic() {}

  @Override
  public void testInit() {}

  @Override
  public void testPeriodic() {}

  @Override
  public void simulationInit() {}

  @Override
  public void simulationPeriodic() {}



  public void getControllerStates() {
    operator_controller_A_button = operator_controller.getAButton();
    operator_controller_B_button = operator_controller.getBButton();
    operator_controller_X_button = operator_controller.getXButton();
    operator_controller_Y_button = operator_controller.getYButton();
    driver_controller_POV_button = driver_controller.getPOV();
    driver_controller_L_X_Axis = driver_controller.getLeftX();
    driver_controller_L_Y_Axis = driver_controller.getLeftY();
    driver_controller_R_X_Axis = driver_controller.getRightX();
    driver_controller_R_Y_Axis = driver_controller.getRightY();

  }
}

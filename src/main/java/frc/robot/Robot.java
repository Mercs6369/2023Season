// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;

public class Robot extends TimedRobot {

  XboxController driver_controller = new XboxController(0);
  XboxController operator_controller = new XboxController(1);
// maybe put these variables in the constants class? Yes, constants being worked by Gargi, still need these though
  boolean operator_controller_A_button;
  boolean operator_controller_Y_button;
  boolean operator_controller_X_button;
  boolean operator_controller_B_button;
  double driver_controller_L_X_Axis;
  double driver_controller_L_Y_Axis;
  double driver_controller_R_X_Axis;
  double driver_controller_R_Y_Axis;
  int driver_controller_POV_button = driver_controller.getPOV();

  Arm m_arm = new Arm();

  // Shuffleboard: Declares variables associated with Alliance Selection
  private final SendableChooser<String> m_alliance = new SendableChooser<>();
  private static final String kRed = "Red";
  private static final String kBlue = "Blue";
  private String m_allianceSelected;
  // Shuffleboard: Declares variables associated with Idle Selection
  private final SendableChooser<String> m_idle = new SendableChooser<>();
  private static final String kNotIdle = "No";
  private static final String kIsIdle = "Yes";
  private String m_idleSelected;
  // Shuffleboard: Declares variables associated with Position Selection
  private final SendableChooser<String> m_position = new SendableChooser<>();
  private static final String kCenter = "Center";
  private static final String kLeft = "Left";
  private static final String kRight = "Right";
  private String m_positionSelected;
  // Shuffleboard: Declares variables associated with Scoring Node
  private final SendableChooser<String>  m_scoringNode= new SendableChooser<>();
  private static final String kHigh = "High";
  private static final String kMedium = "Medium";
  private static final String kHybrid = "Hybrid";
  private String m_scoringNodeSelected;
  // Shuffleboard: Declares variables associated with Leaving Community
  private final SendableChooser<String>  m_leavingCommunity = new SendableChooser<>();
  private static final String kIsLeaving = "Yes";
  private static final String kNotLeaving = "No";
  private String m_leavingCommunitySelected;
  // Shuffleboard: Declares variables associated with Delay
  private final SendableChooser<String>  m_delay = new SendableChooser<>();
  private static final String kNoDelay = "No Delay";
  private static final String kOneSecond = "One Second";
  private static final String kThreeSeconds = "Three Seconds";
  private static final String kFiveSeconds = "Five Seconds";
  private String m_delaySelected;

  @Override
  public void robotInit() {

    robotInitShuffleboard();   // performs robot initialization of Shuffleboard usuage
        
  }

  @Override
  public void robotPeriodic() {

    getControllerStates();    // reads all controller inputs
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
    if(-0.1 < driver_controller_R_X_Axis && 0.1 > driver_controller_R_X_Axis && -0.1 < driver_controller_R_Y_Axis && driver_controller_R_Y_Axis < .1)
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

    autoInitShuffleboard();  // performs autonomous initialization of Shuffleboard usuage

  }

  @Override
  public void autonomousPeriodic() {

  }

  @Override
  public void teleopInit() {

  }

  @Override
  public void teleopPeriodic() {

  }

  @Override
  public void disabledInit() {}

  @Override
  public void disabledPeriodic() {}

  @Override
  public void testInit() {}

  @Override
  public void testPeriodic() {}

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

  public void robotInitShuffleboard() {
      // Shuffleboard: Passes options "Red" and "Blue" for Alliance
      m_alliance.setDefaultOption("Red", kRed);
      m_alliance.setDefaultOption("Blue", kBlue);
      SmartDashboard.putData("Alliance", m_alliance);
      // Shuffleboard: Passes options "Yes" and "No" for Idle
      m_idle.setDefaultOption("No", kNotIdle);
      m_idle.addOption("Yes", kIsIdle);
      SmartDashboard.putData("Idle", m_idle);
      // Shuffleboard: Passes options "Center", "Left", and "Right" for Position
      m_position.setDefaultOption("Center", kCenter);
      m_position.addOption("Left", kLeft);
      m_position.addOption("Right", kRight);
      SmartDashboard.putData("Position", m_position);
      // Shuffleboard: Passes options "High", "Medium", and "Hybrid" for Scoring Node
      m_scoringNode.setDefaultOption("High", kHigh);
      m_scoringNode.addOption("Medium", kMedium);
      m_scoringNode.addOption("Hybrid", kHybrid);
      SmartDashboard.putData("Scoring Node", m_scoringNode);
      // Shuffleboard: Passes options "Yes" and "No" for Leaving Community
      m_leavingCommunity.setDefaultOption("Yes", kIsLeaving);
      m_leavingCommunity.addOption("No", kNotLeaving);
      SmartDashboard.putData("Leaving Community", m_leavingCommunity);
      // Shuffleboard: Passes options "No Delay", "One Second", "Three Seconds", and "Five Seconds" for Delay
      m_delay.setDefaultOption("No Delay", kNoDelay);
      m_delay.addOption("One Second", kOneSecond);
      m_delay.addOption("Three Seconds", kThreeSeconds);
      m_delay.addOption("Five Seconds", kFiveSeconds);
      SmartDashboard.putData("Delay", m_delay);
  }

  public void autoInitShuffleboard() {
    // Shuffleboard: Sets Alliance Selection to variable and prints to console
    m_allianceSelected = m_alliance.getSelected();
    System.out.println("Alliance Selected: " + m_allianceSelected);
    // Shuffleboard: Sets Idle Selection to variable and prints to console
    m_idleSelected = m_idle.getSelected();
    System.out.println("Idle Selected: " + m_idleSelected);
    // Shuffleboard: Sets Position Selection to variable
    m_positionSelected = m_position.getSelected();
    System.out.println("Position Selected: " + m_positionSelected);
    // Shuffleboard: Sets Scoring Node Selection to variable and prints to console
    m_scoringNodeSelected = m_scoringNode.getSelected();
    System.out.println("Scoring Node Selected: " + m_scoringNodeSelected);
    // Shuffleboard: Sets Leaving Community Selection to variable and prints to console
    m_leavingCommunitySelected = m_leavingCommunity.getSelected();
    System.out.println("Leaving Community Selected: " + m_leavingCommunitySelected);
    // Shuffleboard: Sets Delay Selection to variable and prints to console
    m_delaySelected = m_delay.getSelected();
    System.out.println("Delay Selected: " + m_delaySelected);
  }
}
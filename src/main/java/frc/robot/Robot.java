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

  boolean driver_controller_A_button;
  boolean driver_controller_Y_button;
  boolean driver_controller_X_button;
  boolean driver_controller_B_button;
  
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

  // DANS BRILLANT IDEA
  // AMALS BRILLANT IDEA

  @Override
  public void robotPeriodic() {

    getControllerStates();
    if(driver_controller_B_button)
    {
      // something hello this is me jonathan
    }

  }

  @Override
  public void autonomousInit() {
    // Shuffleboard: Prints Alliance Selection to console
    m_allianceSelected = m_alliance.getSelected();
    System.out.println("Alliance Selected: " + m_allianceSelected);
    // Shuffleboard: Prints Idle Selection to console
    m_idleSelected = m_idle.getSelected();
    System.out.println("Idle Selected: " + m_idleSelected);
    // Shuffleboard: Prints Position Selection to console
    m_positionSelected = m_position.getSelected();
    System.out.println("Position Selected: " + m_positionSelected);
    // Shuffleboard: Prints Scoring Node Selection to console
    m_scoringNodeSelected = m_scoringNode.getSelected();
    System.out.println("Scoring Node Selected: " + m_scoringNodeSelected);
    // Shuffleboard: Prints Leaving Community Selection to console
    m_leavingCommunitySelected = m_leavingCommunity.getSelected();
    System.out.println("Leaving Community Selected: " + m_leavingCommunitySelected);
    // Shuffleboard: Prints Delay Selection to console
    m_delaySelected = m_delay.getSelected();
    System.out.println("Delay Selected: " + m_delaySelected);
    
  }

  @Override
  public void autonomousPeriodic() {

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
    driver_controller_A_button = driver_controller.getAButton();
    driver_controller_B_button = driver_controller.getBButton();
    driver_controller_X_button = driver_controller.getXButton();
    driver_controller_Y_button = driver_controller.getYButton();
    
  }
}

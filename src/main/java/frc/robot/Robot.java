// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.interfaces.Gyro;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.LED_Signaling.LED_State;
import frc.robot.Vision.gamePiecePipelineIndex;
import frc.robot.Vision.infoTypeToReturn;
import frc.robot.commands.TeleopSwerve;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;

import com.ctre.phoenix.sensors.Pigeon2;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

public class Robot extends TimedRobot {

  public static CTREConfigs ctreConfigs;
  private Command m_autonomousCommand;
  private RobotContainer m_robotContainer;
  public Pigeon2 gyro;


  XboxController driver_Controller = new XboxController(0);
  XboxController operator_controller = new XboxController(1);
// maybe put these variables in the constants class? Yes, constants being worked by Gargi, still need these though
  boolean operator_controller_A_button;
  boolean operator_controller_Y_button;
  boolean operator_controller_X_button;
  boolean operator_controller_B_button;
  boolean brake_mode_enabled = false; // Controls whether robot is in brake mode or not
  double driver_controller_L_X_Axis;
  double driver_controller_L_Y_Axis;
  double driver_controller_R_X_Axis;
  double driver_controller_R_Y_Axis;
  int driver_controller_POV_button;
  double speedScale = 0.85;

  
  Vision m_vision = new Vision();
  LED_Signaling LEDInstance = new LED_Signaling();
  long lastnano_time = 0;
  Timer m_timeToButtonPress = new Timer();
  Timer charedStationTimer = new Timer();
  Arm m_arm = new Arm();

  double[] autonomousSwerveCommands = {0,0,0};


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



/*
   * 
   *
   * 
   */
  enum pickupStatusEnum {
    in_progress,
    finished,
    idle,
  }
  pickupStatusEnum pickupStatus = pickupStatusEnum.idle;

  /**
   * This needs to be run whenever the button (assigned to picking up a piece) is pressed. You don't have to worry about adding a debounce to this, it's all handled internally :P
   */
  private void _RobotPickUpPiece() {
    pickupStatus = pickupStatusEnum.in_progress;
  }

  private void pickUpPiecePeriodic() {
    autonomousSwerveCommands = m_vision.runAlignmentProcess();
    SmartDashboard.putNumber("Yaw", autonomousSwerveCommands[0]);
    SmartDashboard.putNumber("Area", autonomousSwerveCommands[1]);
    
    double rotation = -1*((0-autonomousSwerveCommands[0])/28);
    rotation = rotation * 1.2;


    if (rotation >= -0.07142857142 && rotation <= 0.07142857142){
      rotation = 0.0;
    } else if (rotation >= 0.07142857142 && rotation <= 0.6) {
      rotation = 0.6;
    } else if (rotation <= -0.07142857142 && rotation >= -0.6) {
      rotation = -0.6;
    } else if (rotation >= 1.2) {
      rotation = 1.2;
    } else if (rotation <= -1.2) {
      rotation = -1.2;
    }

    double swerveYchange; // These 5 variables are being used for calculating how far to move towards the game object.
    double ca = autonomousSwerveCommands[1]; // do not remove these!!! (current area)
    double idealArea = Constants.idealConeArea_Standing;
    double top_speed = Constants.top_speed_mps;
    double increment = top_speed / idealArea;




    SmartDashboard.putNumber("Unofficial status", (ca - idealArea) * increment * -1);


    if ((Constants.idealConeArea_Standing > (ca - 0.5)) && (Constants.idealConeArea_Standing < (ca + 0.5))) {
      swerveYchange = 0.0;
      SmartDashboard.putString("On axis", "true");

    } else {
      swerveYchange = (ca - idealArea) * increment * -1; // this one equation took up like half a whiteboard lmao !!!DO NOT REMOVE THIS!!!
      SmartDashboard.putString("On axis", "false");

    }


    SmartDashboard.putNumber("First", swerveYchange);


    if ((swerveYchange < 0.35) && (swerveYchange >= .11)) { // this is all for the graph line thingy that Coach Dan knows about ask him if things happen
      swerveYchange = 0.35;
    } else if (swerveYchange <= .11) {
      swerveYchange = 0.0;
    }

    swerveYchange = -1 * swerveYchange; // overall invert


    SmartDashboard.putNumber("Rotation For Swerve", rotation);
    SmartDashboard.putNumber("Move (meters per second) For Swerve", swerveYchange);


    if (pickupStatus == pickupStatusEnum.in_progress) {
                   
      SmartDashboard.putString("Pickup Status", "In progress");
      // updateSwerveParameters()
      m_robotContainer.updateSwerveParameters(new Translation2d(0,swerveYchange), rotation, true);
     
    } else {
      SmartDashboard.putString("Pickup Status", "Idle");
    }

  }
  /*
   * 
   * 
   * 
   * 
   * 
   * 
   */

  @Override
  public void robotInit() {


    
    //m_vision.setGamePiecePipeline(gamePiecePipelineIndex.driver);
    ctreConfigs = new CTREConfigs();
    m_robotContainer = new RobotContainer();

    gyro = new Pigeon2(Constants.Swerve.pigeonID);
    gyro.configFactoryDefault();
    zeroGyro();


    final double xRoll = gyro.getRoll();



        
  }
 
  boolean gonebackwards = false;

  @Override
  public void robotPeriodic() {
    
    CommandScheduler.getInstance().run();

    SmartDashboard.putNumber("SwerveDistanceX", getSwerveDistanceX());
    SmartDashboard.putNumber("SwerveDistanceY", getSwerveDistanceY());
    SmartDashboard.putNumber("Roll", gyro.getRoll());
    SmartDashboard.putNumber("Pitch", gyro.getPitch());
    SmartDashboard.putNumber("Yaw", gyro.getYaw());




    if (driver_Controller.getPOV() == 0){
      m_robotContainer.updateSwerveParameters(new Translation2d(0, 2), 0, true);
    }
    else if (driver_Controller.getPOV() == 90){
      m_robotContainer.updateSwerveParameters(new Translation2d(2, 0), 0, true);

    }
    else if (driver_Controller.getPOV() == 270){
      m_robotContainer.updateSwerveParameters(new Translation2d(-2, 0), 0, true);

    }
    else if (driver_Controller.getPOV() == 180){
      m_robotContainer.updateSwerveParameters(new Translation2d(0, -2), 0, true);

    }
    else {
      m_robotContainer.updateSwerveParameters(new Translation2d(0, 0), 0, false);

    }

    if (operator_controller.getRawButton(1) == true){
      m_arm.setIntakeMotor(0.9);
    }
    else if (operator_controller.getRawButton(4) == true) {
      m_arm.setIntakeMotor(-0.9);
    }
    else {
      m_arm.setIntakeMotor(0.0);
    }


    if ((driver_Controller.getRawButton(1)) == true){
      charedStationTimer.start();
      String xy = "";
      if (charedStationTimer.get() < 3){
        m_robotContainer.updateSwerveParameters(new Translation2d(0, 0.6), 0, true);
        xy = "timer";

      }
      else if ((gyro.getRoll()) > -3.735 + 0.5){
        if (gonebackwards == false){
          m_robotContainer.updateSwerveParameters(new Translation2d(0, 0.6), 0, true);
          xy = "forward fast";
        }
        else {
          m_robotContainer.updateSwerveParameters(new Translation2d(0, 0.3), 0, true);
          xy = "forward slow";
        }

      }
      else if ((gyro.getRoll()) < -3.705 - 0.5){
        m_robotContainer.updateSwerveParameters(new Translation2d(0, -0.4), 0, true);
        xy = "backwards";
        gonebackwards = true;
      }
      else {
        m_robotContainer.updateSwerveParameters(new Translation2d(0, 0), 0, true);
        xy = "level";

      }

      SmartDashboard.putString("Status", xy);


    }


      

   }

  @Override
  public void autonomousInit() {
    m_robotContainer = new RobotContainer();
        /*
    m_autonomousCommand = m_robotContainer.getAutonomousCommand();

    // schedule the autonomous command (example)
    if (m_autonomousCommand != null) {
      m_autonomousCommand.schedule();
    }
    */


  }

  @Override
  public void autonomousPeriodic() {
    /*
    double distanceY = m_robotContainer.s_Swerve.swerveOdometry.getPoseMeters().getY();
    if (distanceY < 5.0){
      m_robotContainer.updateSwerveParameters(new Translation2d(0, 0.95), 0, true);

    }
    else {
      m_robotContainer.updateSwerveParameters(new Translation2d(0, 0), 0, true);

    }
    */
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
    // brake_mode_enabled = driver_controller.getStartButton() && driver_controller.getBackButton();
    int m_buttonPressCount = 0;
    double obtainedButtonTime = 0;
    if(operator_controller.getStartButton() && operator_controller.getBackButton()) {
      m_buttonPressCount++;
      if(m_buttonPressCount == 1) {
        m_timeToButtonPress.start();
      }
      obtainedButtonTime = m_timeToButtonPress.get();
    }
      if(obtainedButtonTime > 0.5) {
        brake_mode_enabled = !brake_mode_enabled;
        m_timeToButtonPress.stop();
        m_timeToButtonPress.reset();
    }
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

  public double getSwerveDistanceX(){
    return m_robotContainer.s_Swerve.swerveOdometry.getPoseMeters().getX();
  }
  public double getSwerveDistanceY(){
    return m_robotContainer.s_Swerve.swerveOdometry.getPoseMeters().getY();
  }

  public void zeroGyro(){
    gyro.setYaw(0);
}

  public Rotation2d getYaw() {
      return (Constants.Swerve.invertGyro) ? Rotation2d.fromDegrees(360 - gyro.getYaw()) : Rotation2d.fromDegrees(gyro.getYaw());
  }
}

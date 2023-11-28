// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Arm.ArmStateEnum;
import frc.robot.LED_Signaling.LED_State;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import com.ctre.phoenix.sensors.Pigeon2;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

public class Robot extends TimedRobot {

  public static CTREConfigs ctreConfigs;
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
  double max_speed_limiter = 0.1;  // 10% of the maximum translation velocity and angular velocity
  int autoStage = 0;
  double initialGyroValue = 0.0;
  double initialDistanceY = 0.0;

  boolean[] operator_buttons = {false, false, false, false, false, false, false, false};
  double[] operator_triggers = new double[2];
  
  Vision m_vision = new Vision();
  LED_Signaling LEDInstance = new LED_Signaling(9);
  LED_Signaling LEDInstance2 = new LED_Signaling(8);
  long lastnano_time = 0;
  Timer m_timeToButtonPress = new Timer();
  Timer chargedStationTimer = new Timer();
  Timer moveAutoTimer = new Timer();
  Timer autoIntake = new Timer();
  Timer autoIntakeThing = new Timer();
  Timer autoIntakeThing1 = new Timer();
  Arm m_arm = new Arm();
  NodeSelector nodeSelector = new NodeSelector();

  double[] autonomousSwerveCommands = {0,0,0};

  private final SendableChooser<String>  m_autoRoutine = new SendableChooser<>();
  private static final String kRoutine1 = "MidDefault";
  private static final String kRoutine2 = "CubeLeft";
  private static final String kRoutine3 = "PreloadRight";
  private static final String kRoutine4 = "Do Nothing";
  private static final String kRoutine5 = "MidDefault with mobility";
  private String m_autoRoutineSelected;

  private final SendableChooser<String>  m_speedControl = new SendableChooser<>();
  private static final String kSpeedNormal = "Normal Speed";
  private static final String kSpeedSlow = "Slow Speed";
  private String m_speedSelected;

  private void RobotPickUpPiece() {
    SmartDashboard.putNumber("Yaw", autonomousSwerveCommands[0]);
    SmartDashboard.putNumber("Pitch", autonomousSwerveCommands[1]);

    double rotation = -1*(autonomousSwerveCommands[0])/10;
    SmartDashboard.putNumber("cube rotation", rotation);

    double swerveYchange = -1*autonomousSwerveCommands[1]/5;
    if (Math.abs(autonomousSwerveCommands[1]) >= 0.50){
      swerveYchange = -Math.signum(autonomousSwerveCommands[1])*0.4;
    }
    else if (Math.abs(autonomousSwerveCommands[1]) < 0.5 && Math.abs(autonomousSwerveCommands[1]) > 0.1){
      swerveYchange = -Math.signum(autonomousSwerveCommands[1])*0.25;
    }
    else if (Math.abs(autonomousSwerveCommands[1]) < 0.1){
      swerveYchange = 0.0;
    }

    if (Math.abs(autonomousSwerveCommands[0]) >= 0.50){
      rotation = -Math.signum(autonomousSwerveCommands[0])*0.2;
    }
    else if (Math.abs(autonomousSwerveCommands[0]) < 0.5 && Math.abs(autonomousSwerveCommands[0]) > 0.1){
      rotation = -Math.signum(autonomousSwerveCommands[0])*0.05;
    }
    else if (Math.abs(autonomousSwerveCommands[0]) < 0.1){
      rotation = 0.0;
    }

    
    // else {
    //   swerveYchange = 0.*0;
    // }
    // if (swerveYchange < -3){
    //   swerveYchange = -1*autonomousSwerveCommands[1]/5;
    // }
    // else {
    //   swerveYchange = 0;
    // }
     // These 5 variables are being used for calculating how far to move towards the game object.
    SmartDashboard.putNumber("cube swerveYchange", swerveYchange);
    /* 
    double ca = autonomousSwerveCommands[1]; // do not remove these!!! (current area)
    double idealArea = Constants.idealConeArea_Standing;
    double top_speed = Constants.top_speed_mps;
    double increment = top_speed / idealArea;
*/
    //swerveYchange = (ca - idealArea) * increment * -1; // this one equation took up like half a whiteboard lmao !!!DO NOT REMOVE THIS!!!

    /* 
    if ((swerveYchange < 0.35) && (swerveYchange >= .11)) { // this is all for the graph line thingy that Coach Dan knows about ask him if things happen
      swerveYchange = 0.35;
    } else if (swerveYchange <= .11) {
      swerveYchange = 0.0;
    }
    */
    SmartDashboard.putNumber("rotation", rotation);
    SmartDashboard.putNumber("swerveYchange", swerveYchange);
    m_robotContainer.updateSwerveParameters(new Translation2d(0,swerveYchange), rotation, true);
  

  }

  @Override
  public void robotInit() {
    ctreConfigs = new CTREConfigs();
    m_robotContainer = new RobotContainer();
    robotInitShuffleboard();

    gyro = new Pigeon2(Constants.Swerve.pigeonID);
    gyro.configFactoryDefault();
    zeroGyro();
        
  }
 
  boolean gonebackwards = false;

  @Override
  public void robotPeriodic() {
    LEDInstance.SetLEDS(m_arm.colorValue);
    LEDInstance2.SetLEDS(m_arm.colorValue2);
    
    CommandScheduler.getInstance().run();

    SmartDashboard.putNumber("SwerveDistanceX", getSwerveDistanceX());
    SmartDashboard.putNumber("SwerveDistanceY", getSwerveDistanceY());
    SmartDashboard.putNumber("Roll", gyro.getRoll());
    
    SmartDashboard.putNumber("Main Arm Position", m_arm.get_main_arm_position());
    SmartDashboard.putNumber("Intake Arm Position", m_arm.get_intake_arm_position_selected());
    SmartDashboard.putNumber("Main Arm Position Throughbore", m_arm.get_main_arm_position_throughbore());

    getControllerStates();



    m_vision.gamePiecePeriodic();
    m_vision.aprilTagPeriodic();
    m_vision.reflectiveTapePeriodic();
    nodeSelector.updateSelectedNode(driver_Controller.getPOV());
    SmartDashboard.putString("node", nodeSelector.getCurrentNode());
    

   }

  @Override
  public void autonomousInit() {
    autoInitShuffleboard();
    zeroGyro();
    initialGyroValue = gyro.getRoll();
    initialDistanceY = getSwerveDistanceY(); 
  }

  @Override
  public void autonomousPeriodic() {


    if (m_autoRoutineSelected == "MidDefault") {
      autoTest();
    }
    else if (m_autoRoutineSelected == "CubeLeft") {
      autoDistanceTest();
    }
    else if (m_autoRoutineSelected == "PreloadRight") {
      autoBackup();

    }
    else if (m_autoRoutineSelected == "Do Nothing") {

    }
    else if (m_autoRoutineSelected == "MidDefault with mobility"){
      autoTest1();
    }
    

    //autoBackup();
    //autoDefault();
    //autoDistanceTest(); 

  }

  @Override
  public void teleopInit() {
    teleopInitShuffleboard();
    if (m_speedSelected == "Normal Speed") {
      //
      Constants.Swerve.maxSpeed = 4.5;
    }
    else if (m_speedSelected == "Slow Speed") {
      //
      Constants.Swerve.maxSpeed = 1.5;
    }

  }

  @Override
  public void teleopPeriodic() {
    m_arm.armPeriodic(operator_buttons, operator_triggers, operator_controller.getLeftY(), operator_controller.getRightY());
    
    if (m_arm.GLOBAL_ARM_STATE == ArmStateEnum.Picking_up || m_arm.GLOBAL_ARM_STATE == ArmStateEnum.Scoring) {
      m_robotContainer.updateSwerveParameters(new Translation2d(Constants.Swerve.maxSpeed/1.5 * -driver_Controller.getLeftX(), 
                                                                Constants.Swerve.maxSpeed/1.5 * driver_Controller.getLeftY()),
                                                                Constants.Swerve.maxSpeed/1.5 * -driver_Controller.getRightX(), true);
    } 
    else if (driver_Controller.getPOV() == 0){
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
    else if (driver_Controller.getRawButton(6)) {
      m_robotContainer.updateSwerveParameters(new Translation2d(0, 0), -m_vision.getGamePieceYaw()/10, true);
      // m_robotContainer.updateSwerveParameters(new Translation2d(0, 0), -m_vision.getGamePieceYaw() * (0.1 / Math.sqrt(Math.abs(m_vision.getGamePieceYaw()))), true);

    }
    else {
      m_robotContainer.updateSwerveParameters(new Translation2d(0, 0), 0, false);
    }
    
  }

  @Override
  public void disabledInit() {
    LEDInstance.SetLEDS(LED_State.Off);
    LEDInstance2.SetLEDS(LED_State.Off);
  }

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
    operator_buttons[0] = operator_controller_X_button;
    operator_buttons[1] = operator_controller_Y_button;
    operator_buttons[2] = operator_controller_A_button;
    operator_buttons[3] = operator_controller_B_button;
    operator_buttons[4] = operator_controller.getStartButton();
    operator_buttons[5] = operator_controller.getBackButton();
    operator_buttons[6] = operator_controller.getLeftBumperReleased();
    operator_buttons[7] = operator_controller.getRightBumperReleased();
    operator_triggers[0] = operator_controller.getLeftTriggerAxis();
    operator_triggers[1] = operator_controller.getRightTriggerAxis();
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
      m_autoRoutine.setDefaultOption("MidDefault", kRoutine1);
      m_autoRoutine.addOption("CubeLeft", kRoutine2);
      m_autoRoutine.addOption("PreloadRight", kRoutine3);
      m_autoRoutine.addOption("Do Nothing", kRoutine4);   
      m_autoRoutine.addOption("MidDefault with mobility", kRoutine5);    
      SmartDashboard.putData("Auto Routine Selection", m_autoRoutine);

      m_speedControl.setDefaultOption("Normal Speed", kSpeedNormal);
      m_speedControl.addOption("Slow Speed", kSpeedSlow); 
      SmartDashboard.putData("Speed Selection", m_speedControl);
  }

  public void autoInitShuffleboard() {
    m_autoRoutineSelected = m_autoRoutine.getSelected();
    System.out.println("Routine Selected: " + m_autoRoutineSelected);
  }

  public void teleopInitShuffleboard() {
    m_speedSelected = m_speedControl.getSelected();
    System.out.println("Speed Selected: " + m_speedSelected);
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

  public void autoBalance(double initialGyroValue){
    chargedStationTimer.start();
    String xy = "";
    // if (chargedStationTimer.get() < 5){
    //   m_robotContainer.updateSwerveParameters(new Translation2d(0, -0.7), 0, true);
    //   xy = "timer";

    // }
    if ((gyro.getRoll()) > initialGyroValue + 1.5){
      if (gonebackwards == false){
        m_robotContainer.updateSwerveParameters(new Translation2d(0, 0.6), 0, true);
        xy = "forward fast";

      }
      else {
        m_robotContainer.updateSwerveParameters(new Translation2d(0, 0.3), 0, true);
        xy = "forward slow";
      }

    }
    else if ((gyro.getRoll()) < initialGyroValue - 1.5){
      m_robotContainer.updateSwerveParameters(new Translation2d(0, -0.55), 0, true);
      xy = "backwards";
      gonebackwards = true;
    }
    else {
      m_robotContainer.updateSwerveParameters(new Translation2d(0, 0), 0, true);
      xy = "level";

    }

    SmartDashboard.putString("Status", xy);
  }

  public void autoMove(){
    moveAutoTimer.start();
    if (moveAutoTimer.get() < 7){
      m_robotContainer.updateSwerveParameters(new Translation2d(0, -1), 0, true);
    }
    else {
      m_robotContainer.updateSwerveParameters(new Translation2d(0, -0.25), 0, true);

    }
  }

  public void autoDefault(){
    if (autoStage == 0){
      m_arm.setIntakeMotor(0.1);
      m_arm.move_main_arm_to_position(Constants.Cube_Mid_Score_Position.main_arm_position);
      if (Math.abs((m_arm.get_main_arm_position() - Constants.Cube_Mid_Score_Position.main_arm_position)) < 1000){
        m_arm.move_intake_arm_to_position(Constants.Cube_Mid_Score_Position.intake_arm_position);
        if ((Math.abs(m_arm.get_intake_arm_position_selected() - Constants.Cube_Mid_Score_Position.intake_arm_position) < 1000)){
          autoStage = 1;
        }
      }
    }
    else if (autoStage == 1){
      autoIntake.start();
      if (autoIntake.get() < 1){
        m_arm.setIntakeMotor(-1);
      }
      else {
        m_arm.setIntakeMotor(0);
        autoStage = 2;
      }
    }
    else if (autoStage == 2){
      m_arm.move_intake_arm_to_position(Constants.Start_Arm_Position.intake_arm_position);
      if (Math.abs((m_arm.get_intake_arm_position_selected() - Constants.Start_Arm_Position.intake_arm_position)) < 750){
          if (Math.abs((m_arm.get_main_arm_position() - Constants.Start_Arm_Position.main_arm_position)) < 1500){
            m_arm.setMianArmToZero();
            autoStage = 3;
          }
          else {
            m_arm.move_main_arm_to_position(Constants.Start_Arm_Position.main_arm_position);
          }
      }
    }
    else if (autoStage == 3){
      if ((200.75 - (-1*initialDistanceY)) - (-1*getSwerveDistanceY()) > 0.25){
        m_robotContainer.updateSwerveParameters(new Translation2d(0, -1.5), 0, true);
      }
      else if ((200.75 - (-1*initialDistanceY)) - (-1*getSwerveDistanceY()) <= 0.25){
        m_robotContainer.updateSwerveParameters(new Translation2d(0, 0), 0, false);
        autoStage = 4;
      }
    }
    else if (autoStage == 4){
      if ((150) - (-1*getSwerveDistanceY()) < 0.05){
        m_robotContainer.updateSwerveParameters(new Translation2d(0, 1.75), -gyro.getYaw()/30, true);
      }
      // else if ((229.623151 - (-1*initialDistanceY)) - (-1*getSwerveDistanceY()) <= 0.25){
      //   m_robotContainer.updateSwerveParameters(new Translation2d(0, 0), 0, false);
      //   autoStage = 4;
      // }
      else {
        //autoBalance(initialGyroValue);
        m_robotContainer.updateSwerveParameters(new Translation2d(0, 0), 0, true);
        autoStage = 5;
      }
    }
    else if (autoStage == 5){ // note to self
      autoBalance(initialGyroValue);  
    }

  }

  public void autoBackup(){
    if (autoStage == 0){
      m_arm.setIntakeMotor(0.1);
      m_arm.move_main_arm_to_position(Constants.Cube_Mid_Score_Position.main_arm_position);
      if (Math.abs((m_arm.get_main_arm_position() - Constants.Cube_Mid_Score_Position.main_arm_position)) < 1000){
        m_arm.move_intake_arm_to_position(Constants.Cube_Mid_Score_Position.intake_arm_position);
        if ((Math.abs(m_arm.get_intake_arm_position_selected() - Constants.Cube_Mid_Score_Position.intake_arm_position) < 1000)){
          autoStage = 1;
        }
      }
    }
    else if (autoStage == 1){
      autoIntake.start();
      if (autoIntake.get() < 1.25){
        m_arm.setIntakeMotor(-1);
      }
      else {
        m_arm.setIntakeMotor(0);
        autoStage = 2;
      }
    }
    else if (autoStage == 2){
      m_arm.move_intake_arm_to_position(Constants.Start_Arm_Position.intake_arm_position);
      if (Math.abs((m_arm.get_intake_arm_position_selected() - Constants.Start_Arm_Position.intake_arm_position)) < 750){
          if (Math.abs((m_arm.get_main_arm_position() - Constants.Start_Arm_Position.main_arm_position)) < 1500){
            m_arm.setMianArmToZero();
            autoStage = 3;
          }
          else {
            m_arm.move_main_arm_to_position(Constants.Start_Arm_Position.main_arm_position);
          }
      }
    }
    else if (autoStage == 3){
      autoBalance(initialGyroValue);  
      //autoMove();
    }
  }

  public void autoDistanceTest(){
    if (autoStage == 0){
      m_arm.setIntakeMotor(0.1);
      m_arm.move_main_arm_to_position(Constants.Cube_Mid_Score_Position.main_arm_position);
      if (Math.abs((m_arm.get_main_arm_position() - Constants.Cube_Mid_Score_Position.main_arm_position)) < 1000){
        m_arm.move_intake_arm_to_position(Constants.Cube_Mid_Score_Position.intake_arm_position);
        if ((Math.abs(m_arm.get_intake_arm_position_selected() - Constants.Cube_Mid_Score_Position.intake_arm_position) < 1000)){
          autoStage = 1;
        }
      }
    }
    else if (autoStage == 1){ //224.016
      autoIntake.start();
      if (autoIntake.get() < 0.3){
        m_arm.setIntakeMotor(-1);
      }
      else {
        m_arm.setIntakeMotor(0);
        autoStage = 2;
      }
    }
    else if (autoStage == 2){
      m_arm.move_intake_arm_to_position(Constants.Start_Arm_Position.intake_arm_position);
      if (Math.abs((m_arm.get_intake_arm_position_selected() - Constants.Start_Arm_Position.intake_arm_position)) < 750){
          if (Math.abs((m_arm.get_main_arm_position() - Constants.Start_Arm_Position.main_arm_position)) < 1500){
            m_arm.setMianArmToZero();
            autoIntake.reset();
            autoStage = 3;
          }
          else {
            m_arm.move_main_arm_to_position(Constants.Start_Arm_Position.main_arm_position);
          }
      }
    }
    else if (autoStage == 3){
      if ((4) - (-1*getSwerveDistanceY()) > 0.05){
        m_robotContainer.updateSwerveParameters(new Translation2d(0, -1.75), -gyro.getYaw()/30, true);
      }
      else {
        //autoBalance(initialGyroValue);
        m_robotContainer.updateSwerveParameters(new Translation2d(0, 0), 0, true);
        autoStage = 4;
      }
    }
    else if (autoStage == 4){
      if ((10) - (-1*getSwerveDistanceX()) > 0.05){
        m_robotContainer.updateSwerveParameters(new Translation2d(-1, 0), -gyro.getYaw()/30, true); //rr
      }
      else {
        //autoBalance(initialGyroValue);
        m_robotContainer.updateSwerveParameters(new Translation2d(0, 0), 0, true);
        autoStage = 5;
      }
    }
    else if (autoStage == 5){
      //autoBalance(initialGyroValue);  
      //autoMove();
      //229.623151
      //72.56158302 this is how much the robot needs to travel to balance in in
      //21.603 this is how much the robot needs to travel sideways to get 

      //180 for cube pickup
      //81 for balance
      if ((140) - (-1*getSwerveDistanceY()) > 0.05){
        m_robotContainer.updateSwerveParameters(new Translation2d(0, -1.75), -gyro.getYaw()/30, true);
      }
      // else if ((229.623151 - (-1*initialDistanceY)) - (-1*getSwerveDistanceY()) <= 0.25){
      //   m_robotContainer.updateSwerveParameters(new Translation2d(0, 0), 0, false);
      //   autoStage = 4;
      // }
      else {
        //autoBalance(initialGyroValue);
        m_robotContainer.updateSwerveParameters(new Translation2d(0, 0), 0, true);
        autoStage = 6;
      }
    }
    else if (autoStage == 6){
      m_arm.move_main_arm_to_position(Constants.Cone_Ground_Side_Pickup_Position.main_arm_position);
      //move_main_arm_to_position(Constants.Cone_Ground_Side_Pickup_Position.main_arm_position, getRightY);
      if (Math.abs((m_arm.get_main_arm_position() - Constants.Cone_Ground_Side_Pickup_Position.main_arm_position)) < 1500){
        m_arm.move_intake_arm_to_position(Constants.Cone_Ground_Side_Pickup_Position.intake_arm_position + 4000);
        if (Math.abs((m_arm.get_intake_arm_position_selected() - (Constants.Cone_Ground_Side_Pickup_Position.intake_arm_position + 4000))) < 1000){
          autoStage = 7;
        }
      }
    }
    else if (autoStage == 7){
      autoIntakeThing.start();
      m_arm.setIntakeMotor(-1);
      if (autoIntakeThing.get() < 1.2){
        //m_arm.setIntakeMotor(1);
        m_robotContainer.updateSwerveParameters(new Translation2d(0, -0.9), -gyro.getYaw()/30, true);
      }
      else {
        m_arm.setIntakeMotor(0);
        autoStage = 8;
      }
    }
    else if (autoStage == 8){
      m_arm.move_intake_arm_to_position(Constants.Start_Arm_Position.intake_arm_position);
      if (Math.abs((m_arm.get_intake_arm_position_selected() - Constants.Start_Arm_Position.intake_arm_position)) < 750){
          if (Math.abs((m_arm.get_main_arm_position() - Constants.Start_Arm_Position.main_arm_position)) < 1500){
            m_arm.setMianArmToZero();
            autoIntake.reset();
            autoStage = 9;
          }
          else {
            m_arm.move_main_arm_to_position(Constants.Start_Arm_Position.main_arm_position);
          }
      }
    }
    else if (autoStage == 9){
      if ((17.5) - (-1*getSwerveDistanceY()) < 0.05){
        m_robotContainer.updateSwerveParameters(new Translation2d(0, 1.75), -gyro.getYaw()/30, true);
      }
      // else if ((229.623151 - (-1*initialDistanceY)) - (-1*getSwerveDistanceY()) <= 0.25){
      //   m_robotContainer.updateSwerveParameters(new Translation2d(0, 0), 0, false);
      //   autoStage = 4;
      // }
      else {
        //autoBalance(initialGyroValue);
        m_robotContainer.updateSwerveParameters(new Translation2d(0, 0), 0, true);
        autoStage = 11;
      }
    }
 
    else if (autoStage == 10){

    }

}

public void autoTest(){
  if (autoStage == 0){
    m_arm.setIntakeMotor(0.1);
    m_arm.move_main_arm_to_position(Constants.Cube_Mid_Score_Position.main_arm_position);
    if (Math.abs((m_arm.get_main_arm_position() - Constants.Cube_Mid_Score_Position.main_arm_position)) < 1000){
      m_arm.move_intake_arm_to_position(Constants.Cube_Mid_Score_Position.intake_arm_position);
      if ((Math.abs(m_arm.get_intake_arm_position_selected() - Constants.Cube_Mid_Score_Position.intake_arm_position) < 1000)){
        autoStage = 1;
      }
    }
  }
  else if (autoStage == 1){
    autoIntake.start();
    if (autoIntake.get() < 1.5){
      m_arm.setIntakeMotor(-1);
    }
    else {
      m_arm.setIntakeMotor(0);
      autoStage = 2;
    }
  }
  else if (autoStage == 2){
    m_arm.move_intake_arm_to_position(Constants.Start_Arm_Position.intake_arm_position);
    if (Math.abs((m_arm.get_intake_arm_position_selected() - Constants.Start_Arm_Position.intake_arm_position)) < 750){
        if (Math.abs((m_arm.get_main_arm_position() - Constants.Start_Arm_Position.main_arm_position)) < 1500){
          m_arm.setMianArmToZero();
          autoStage = 3;
        }
        else {
          m_arm.move_main_arm_to_position(Constants.Start_Arm_Position.main_arm_position);
        }
    }
  }
  else if (autoStage == 3){
    //autoBalance(initialGyroValue);  
    //autoMove();
    //229.623151
    //72.56158302 this is how much the robot needs to travel to balance in in
    //21.603 this is how much the robot needs to travel sideways to get 

    //180 for cube pickup
    //81 for balance
    if ((71) - (-1*getSwerveDistanceY()) > 0.05){
      m_robotContainer.updateSwerveParameters(new Translation2d(0, -1.75), -gyro.getYaw()/30, true); //note to gaurav
    }
    // else if ((229.623151 - (-1*initialDistanceY)) - (-1*getSwerveDistanceY()) <= 0.25){
    //   m_robotContainer.updateSwerveParameters(new Translation2d(0, 0), 0, false);
    //   autoStage = 4;
    // }
    else {
      //autoBalance(initialGyroValue);
      m_robotContainer.updateSwerveParameters(new Translation2d(0, 0), 0, true);
      autoStage = 4;
    }
  } else if (autoStage == 4){
      //if ((75) - (-1*getSwerveDistanceY()) < 0.05){
      //  m_robotContainer.updateSwerveParameters(new Translation2d(0, 1.75), -gyro.getYaw()/30, true);
      //}
      // else if ((229.623151 - (-1*initialDistanceY)) - (-1*getSwerveDistanceY()) <= 0.25){
      //   m_robotContainer.updateSwerveParameters(new Translation2d(0, 0), 0, false);
      //   autoStage = 4;
      // }
      //else {
        autoBalance(initialGyroValue);
        //m_robotContainer.updateSwerveParameters(new Translation2d(0, 0), 0, true);

      //}

    }
  }

public void autoTest1(){
  if (autoStage == 0){
    m_arm.setIntakeMotor(0.1);
    m_arm.move_main_arm_to_position(Constants.Cube_Mid_Score_Position.main_arm_position);
    if (Math.abs((m_arm.get_main_arm_position() - Constants.Cube_Mid_Score_Position.main_arm_position)) < 1000){
      m_arm.move_intake_arm_to_position(Constants.Cube_Mid_Score_Position.intake_arm_position);
      if ((Math.abs(m_arm.get_intake_arm_position_selected() - Constants.Cube_Mid_Score_Position.intake_arm_position) < 1000)){
        autoStage = 1;
      }
    }
  }
  else if (autoStage == 1){
    autoIntake.start();
    if (autoIntake.get() < 0.5){
      m_arm.setIntakeMotor(-1);
    }
    else {
      m_arm.setIntakeMotor(0);
      autoStage = 2;
    }
  }
  else if (autoStage == 2){
    m_arm.move_intake_arm_to_position(Constants.Start_Arm_Position.intake_arm_position);
    if (Math.abs((m_arm.get_intake_arm_position_selected() - Constants.Start_Arm_Position.intake_arm_position)) < 750){
        if (Math.abs((m_arm.get_main_arm_position() - Constants.Start_Arm_Position.main_arm_position)) < 1500){
          m_arm.setMianArmToZero();
          autoStage = 3;
        }
        else {
          m_arm.move_main_arm_to_position(Constants.Start_Arm_Position.main_arm_position);
        }
    }
  }
  else if (autoStage == 3){
    //autoBalance(initialGyroValue);  
    //autoMove();
    //229.623151
    //72.56158302 this is how much the robot needs to travel to balance in in
    //21.603 this is how much the robot needs to travel sideways to get 

    //180 for cube pickup
    //81 for balance
    if ((160) - (-1*getSwerveDistanceY()) > 0.05){
      m_robotContainer.updateSwerveParameters(new Translation2d(0, -1.75), -gyro.getYaw()/30, true); //note to gaurav
    }
    // else if ((229.623151 - (-1*initialDistanceY)) - (-1*getSwerveDistanceY()) <= 0.25){
    //   m_robotContainer.updateSwerveParameters(new Translation2d(0, 0), 0, false);
    //   autoStage = 4;
    // }
    else {
      //autoBalance(initialGyroValue);
      m_robotContainer.updateSwerveParameters(new Translation2d(0, 0), 0, true);
      autoStage = 4;
    }
  }
    else if (autoStage == 4){
      if ((92.5) - (-1*getSwerveDistanceY()) < 0.05){
        m_robotContainer.updateSwerveParameters(new Translation2d(0, 1), -gyro.getYaw()/30, true); //note to gaurav
      }
      // else if ((229.623151 - (-1*initialDistanceY)) - (-1*getSwerveDistanceY()) <= 0.25){
      //   m_robotContainer.updateSwerveParameters(new Translation2d(0, 0), 0, false);
      //   autoStage = 4;
      // }
      else {
        //autoBalance(initialGyroValue);
        m_robotContainer.updateSwerveParameters(new Translation2d(0, 0), 0, true);
        autoStage = 5;
      }
    }
   else if (autoStage == 5){
      //if ((75) - (-1*getSwerveDistanceY()) < 0.05){
      //  m_robotContainer.updateSwerveParameters(new Translation2d(0, 1.75), -gyro.getYaw()/30, true);
      //}
      // else if ((229.623151 - (-1*initialDistanceY)) - (-1*getSwerveDistanceY()) <= 0.25){
      //   m_robotContainer.updateSwerveParameters(new Translation2d(0, 0), 0, false);
      //   autoStage = 4;
      // }
      //else {
        autoBalance(initialGyroValue);
        //m_robotContainer.updateSwerveParameters(new Translation2d(0, 0), 0, true);

      //}

    }
  }

  public void autoTestWIthMobility(){
    if (autoStage == 0){
      m_arm.setIntakeMotor(0.1);
      m_arm.move_main_arm_to_position(Constants.Cube_Mid_Score_Position.main_arm_position);
      if (Math.abs((m_arm.get_main_arm_position() - Constants.Cube_Mid_Score_Position.main_arm_position)) < 1000){
        m_arm.move_intake_arm_to_position(Constants.Cube_Mid_Score_Position.intake_arm_position);
        if ((Math.abs(m_arm.get_intake_arm_position_selected() - Constants.Cube_Mid_Score_Position.intake_arm_position) < 1000)){
          autoStage = 1;
        }
      }
    }
    else if (autoStage == 1){
      autoIntake.start();
      if (autoIntake.get() < 1.5){
        m_arm.setIntakeMotor(-1);
      }
      else {
        m_arm.setIntakeMotor(0);
        autoStage = 2;
      }
    }
    else if (autoStage == 2){
      m_arm.move_intake_arm_to_position(Constants.Start_Arm_Position.intake_arm_position);
      if (Math.abs((m_arm.get_intake_arm_position_selected() - Constants.Start_Arm_Position.intake_arm_position)) < 750){
          if (Math.abs((m_arm.get_main_arm_position() - Constants.Start_Arm_Position.main_arm_position)) < 1500){
            m_arm.setMianArmToZero();
            autoStage = 3;
          }
          else {
            m_arm.move_main_arm_to_position(Constants.Start_Arm_Position.main_arm_position);
          }
      }
    }
    else if (autoStage == 3){
      //autoBalance(initialGyroValue);  
      //autoMove();
      //229.623151
      //72.56158302 this is how much the robot needs to travel to balance in in
      //21.603 this is how much the robot needs to travel sideways to get 
  
      //180 for cube pickup
      //81 for balance
      if ((180) - (-1*getSwerveDistanceY()) > 0.05){
        m_robotContainer.updateSwerveParameters(new Translation2d(0, -1.75), -gyro.getYaw()/30, true); //note to gaurav
      }
      // else if ((229.623151 - (-1*initialDistanceY)) - (-1*getSwerveDistanceY()) <= 0.25){
      //   m_robotContainer.updateSwerveParameters(new Translation2d(0, 0), 0, false);
      //   autoStage = 4;
      // }
      else {
        //autoBalance(initialGyroValue);
        m_robotContainer.updateSwerveParameters(new Translation2d(0, 0), 0, true);
        autoStage = 4;
      }
    } else if (autoStage == 4){
        if ((68) - (-1*getSwerveDistanceY()) < 0.05){
         m_robotContainer.updateSwerveParameters(new Translation2d(0, 1.75), -gyro.getYaw()/30, true);
        }
        else {
          m_robotContainer.updateSwerveParameters(new Translation2d(0, 0), 0, true);
          autoStage = 5;
        }
        // else if ((229.623151 - (-1*initialDistanceY)) - (-1*getSwerveDistanceY()) <= 0.25){
        //   m_robotContainer.updateSwerveParameters(new Translation2d(0, 0), 0, false);
        //   autoStage = 4;
        // }
        //else {
          
          //m_robotContainer.updateSwerveParameters(new Translation2d(0, 0), 0, true);
  
        //}
      }
      else if (autoStage == 5){
        autoBalance(initialGyroValue);
      }
    }

  public void autoLeft(){
    if (autoStage == 0){
      m_arm.setIntakeMotor(0.1);
      m_arm.move_main_arm_to_position(Constants.Cube_Mid_Score_Position.main_arm_position);
      if (Math.abs((m_arm.get_main_arm_position() - Constants.Cube_Mid_Score_Position.main_arm_position)) < 1000){
        m_arm.move_intake_arm_to_position(Constants.Cube_Mid_Score_Position.intake_arm_position);
        if ((Math.abs(m_arm.get_intake_arm_position_selected() - Constants.Cube_Mid_Score_Position.intake_arm_position) < 1000)){
          autoStage = 1;
        }
      }
    }
    else if (autoStage == 1){
      autoIntake.start();
      if (autoIntake.get() < 1.5){
        m_arm.setIntakeMotor(-1);
      }
      else {
        m_arm.setIntakeMotor(0);
        autoStage = 2;
      }
    }
    else if (autoStage == 2){
      m_arm.move_intake_arm_to_position(Constants.Start_Arm_Position.intake_arm_position);
      if (Math.abs((m_arm.get_intake_arm_position_selected() - Constants.Start_Arm_Position.intake_arm_position)) < 750){
          if (Math.abs((m_arm.get_main_arm_position() - Constants.Start_Arm_Position.main_arm_position)) < 1500){
            m_arm.setMianArmToZero();
            autoStage = 3;
          }
          else {
            m_arm.move_main_arm_to_position(Constants.Start_Arm_Position.main_arm_position);
          }
      }
    }
    else if (autoStage == 3){
      if ((229.623151 - (-1*initialDistanceY)) - (-1*getSwerveDistanceY()) <= 0.25){
         m_robotContainer.updateSwerveParameters(new Translation2d(0, 0), 0, false);
      }
    }
  }

  /*
   * 
   * 
   * Jon's Auton
   * 
   * 
   */



  double xVelocity = 0;
  double yVelocity = 0;
  double xDestination = 0;
  double yDestination = 0;

  //double[] path = 
  //{0,50,
  //  20,50,
  //  20,40,
  //  0,40,
  //  0,-20,
  //  0,0}; -- this defines the path using X and Y coordinates (inches). This would first make the robot go 50 inches forward, and 0 inches left and right, then ten inches back and 20 inches left/right, and so on. It IS relative to the robot's starting position.
  //runPath(path); -- this would run the path seen above, and will return true if it's finished. It does need to be run constantly.
  //movePeriodic; -- this needs to be run constantly, so that it actually updates swerve paramaters.
  // if you're not pressing a button/the robot shouldn't be moving, I would recommend setting these variables:
  //
  //xVelocity = 0;
  //Velocity = 0;
  //xDestination = 0;
  //yDestination = 0;
  //stage = 0; (this might need to be set to something either really low (like zero) or something really high (like 500))
  //
  //
  
  public void moveTo(double x, double y) { //runPath is basically just a bunch of these moveTo methods looped together.
    xVelocity = 0;
    yVelocity = 0;
    xDestination = x;
    yDestination = y;
    SmartDashboard.putNumber("zAttempted Position Y", y);
    SmartDashboard.putNumber("zAttempted Position X", x);


    if (yDestination > getSwerveDistanceY()) {
      yVelocity = 0.7;
    } else {
      yVelocity = -0.7;
    }

    if (xDestination > getSwerveDistanceX()) {
      xVelocity = 0.6;
    } else {
      xVelocity = -0.6;
    }
  }

  public boolean movePeriodic() {
    SmartDashboard.putNumber("VelocityX - Before", xVelocity);
    SmartDashboard.putNumber("VelocityY - Before", yVelocity);

    if (Math.abs(yDestination - getSwerveDistanceY()) <= 6) { // see below
      yVelocity = 0;
    }
    if (Math.abs(xDestination - getSwerveDistanceX()) <= 6) { // this number (6) can/should be changed, if the robot is within 6 inches of it's destination, it will stop. I think it should work if you were to set it to 1
      xVelocity = 0;
    }

    SmartDashboard.putNumber("DestinationX", xDestination);
    SmartDashboard.putNumber("DestinationY", yDestination);
    SmartDashboard.putNumber("VelocityX - After", xVelocity);
    SmartDashboard.putNumber("VelocityY - After", yVelocity);
    if (xVelocity == 0 && yVelocity == 0) {
      return true;
    } else {
      
      m_robotContainer.updateSwerveParameters(new Translation2d(xVelocity, yVelocity), 0, true);
      return false;
    }

  }

  int stage = 0;
  boolean hasDashboarded = false;
  boolean hasCompletedStage = false;

  public void runPath(double... values) {
    SmartDashboard.putNumber("Stage", stage);
    if (stage < values.length) {
      SmartDashboard.putString("Under length", "yessss");
     
      moveTo(values[stage], values[stage+1]);

      if (movePeriodic() == true) { // completed current moveTo

        if (stage < values.length) {
          

          hasDashboarded = true;
          stage = stage + 2;
          hasCompletedStage = true;

          //xDestination = values[2];
          //yDestination = values[3];
        } else {
          // finisheddddddddddddd
          SmartDashboard.putString("Finished", "yay we finished lets go #poppingoff #letsgooo #blessed");
        }
        
      }
    } else {
      xDestination = getSwerveDistanceX();
      yDestination = getSwerveDistanceY();
      SmartDashboard.putString("Under length", "nooooo");
    }
  }

  public void runBackwards(double... values) {
    SmartDashboard.putNumber("Stage", stage);
    if (stage < values.length) {
      SmartDashboard.putString("Under length", "yessss");

      moveTo(values[values.length - stage - 2], values[values.length - stage - 1]);

      if (movePeriodic() == true) { // completed current moveTo

        if (stage < values.length) {
          

          hasDashboarded = true;
          stage = stage + 2;
          hasCompletedStage = true;

          //xDestination = values[2];
          //yDestination = values[3];
        } else {
          // finisheddddddddddddd
          SmartDashboard.putString("Finished", "yay we finished lets go #poppingoff #letsgooo #blessed");
        }
        
      }
    } else {
      xDestination = getSwerveDistanceX();
      yDestination = getSwerveDistanceY();
      SmartDashboard.putString("Under length", "nooooo");
    }
  }
}

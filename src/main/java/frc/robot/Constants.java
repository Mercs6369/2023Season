package frc.robot;

import com.ctre.phoenix.motorcontrol.NeutralMode;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.math.util.Units;
import frc.lib.util.COTSFalconSwerveConstants;
import frc.lib.util.SwerveModuleConstants;

public class Constants {

    public static final double max_speed_limit = 0.65;

    public static final double stickDeadband = 0.1;

    public static final double ARM_COMMAND_CHECK_LIMIT = 500;

    /* Cone Area Movement */
    public static final double top_speed_mps = 1.5;
    public static final double idealConeArea_Standing = 11.3;
    public static final double idealCubeArea = 15.0;

    /* MOTOR and ENCODER IDS */
    public static final int INTAKE_ROLLER_MOTOR_1_ID = 21;
    public static final int MAIN_ARM_MOTOR_1_ID = 17;
    public static final int MAIN_ARM_MOTOR_2_ID = 18;
    public static final int INTAKE_ARM_MOTOR_ID = 19;
    public static final int INTAKE_ARM_ENCODER_PWM_CHANNEL = 0;
    public static final int MAIN_ARM_ENCODER_PWM_CHANNEL = 9;
    public static final int PIGEON_DEVICE_ID = 16;

    /* Robot Arm Position Combos */
    public static final class Start_Arm_Position {   // starting position
        public static final double intake_arm_position = -1026;
        public static final double main_arm_position = -1400;
    }
    public static final class Cube_Ground_Pickup_Position {   // cube pickup position
        public static final double intake_arm_position = -20350.0;//-22040.0;//-22575.0;//-22538;
        public static final double main_arm_position = -4770.0;//-5698.0;//-7450.0;
    }
    public static final class Cube_Station_Pickup_Position { 
          // cube pickup position
        public static final double intake_arm_position = 0.0;
        public static final double main_arm_position = 0.0;
    }
    public static final class Cone_Ground_Upright_Pickup_Position {   // cone pickup position
        public static final double intake_arm_position = -39515;
        public static final double main_arm_position = -21997;
    }
    public static final class Cone_Ground_Side_Pickup_Position {   // cube pickup position
        public static final double intake_arm_position = -31074;//-26529.0;//-33351.0;//-33524;
        public static final double main_arm_position = -10256;//-7033.0;//-10097.0;//-10258;
    }
    public static final class Cone_Station_Pickup_Position {   // cube pickup position
        public static final double intake_arm_position = -10098;
        public static final double main_arm_position = -5112;
    }
    public static final class Cone_Community_Score_Position {   // cone middle scoring position
        public static final double intake_arm_position = -4530;
        public static final double main_arm_position = -77000;
    }
    public static final class Cone_Mid_Score_Position {   // cone middle scoring position
        public static final double intake_arm_position = -16272;//-10642;
        public static final double main_arm_position = -77358;//-73481;
    }
    public static final class Cone_Top_Score_Position {   // cone top scoring position
        public static final double intake_arm_position = -26000;//-25594.0;
        public static final double main_arm_position = -83527;
    }
    public static final class Cube_Community_Score_Position {   // cube middle scoring position
        public static final double intake_arm_position = -1026;
        public static final double main_arm_position = -1400;
    }
    public static final class Cube_Mid_Score_Position {   // cube middle scoring position
        public static final double intake_arm_position = -5900;
        public static final double main_arm_position = -83358;
    }
    public static final class Cube_Top_Score_Position {   // cube top scoring position
        public static final double intake_arm_position = -27000.0;
        public static final double main_arm_position = -84183.0;
    }

    public static final class Swerve {
        public static final int pigeonID = PIGEON_DEVICE_ID;
        public static final boolean invertGyro = false; // Always ensure Gyro is CCW+ CW-

        public static final COTSFalconSwerveConstants chosenModule =  //TODO: This must be tuned to specific robot
            COTSFalconSwerveConstants.SwerveX();

        /* Drivetrain Constants */
        public static final double trackWidth = Units.inchesToMeters(19);
        public static final double wheelBase = Units.inchesToMeters(23);
        public static final double wheelCircumference = chosenModule.wheelCircumference;

        /* Swerve Kinematics 
         * No need to ever change this unless you are not doing a traditional rectangular/square 4 module swerve */
         public static final SwerveDriveKinematics swerveKinematics = new SwerveDriveKinematics(
            new Translation2d(-wheelBase / 2.0, -trackWidth / 2.0),
            new Translation2d(+wheelBase / 2.0, +trackWidth / 2.0),
            new Translation2d(-wheelBase / 2.0, +trackWidth / 2.0),
            new Translation2d(+wheelBase / 2.0, -trackWidth / 2.0));

        /* Module Gear Ratios */
        public static final double driveGearRatio = chosenModule.driveGearRatio;
        public static final double angleGearRatio = chosenModule.angleGearRatio;

        /* Motor Inverts */
        public static final boolean angleMotorInvert = chosenModule.angleMotorInvert;
        public static final boolean driveMotorInvert = chosenModule.driveMotorInvert;

        /* Angle Encoder Invert */
        public static final boolean canCoderInvert = chosenModule.canCoderInvert;

        /* Swerve Current Limiting */
        public static final int angleContinuousCurrentLimit = 25;
        public static final int anglePeakCurrentLimit = 40;
        public static final double anglePeakCurrentDuration = 0.1;
        public static final boolean angleEnableCurrentLimit = true;

        public static final int driveContinuousCurrentLimit = 35;
        public static final int drivePeakCurrentLimit = 60;
        public static final double drivePeakCurrentDuration = 0.1;
        public static final boolean driveEnableCurrentLimit = true;

        /* These values are used by the drive falcon to ramp in open loop and closed loop driving.
         * We found a small open loop ramp (0.25) helps with tread wear, tipping, etc */
        public static final double openLoopRamp = 0.25;
        public static final double closedLoopRamp = 0.0;

        /* Angle Motor PID Values */
        public static final double angleKP = chosenModule.angleKP;
        public static final double angleKI = chosenModule.angleKI;
        public static final double angleKD = chosenModule.angleKD;
        public static final double angleKF = chosenModule.angleKF;

        /* Drive Motor PID Values */
        public static final double driveKP = 0.05; //TODO: This must be tuned to specific robot
        public static final double driveKI = 0.0;
        public static final double driveKD = 0.0;
        public static final double driveKF = 0.0;

        /* Drive Motor Characterization Values 
         * Divide SYSID values by 12 to convert from volts to percent output for CTRE */
        public static final double driveKS = (0.32 / 12); //TODO: This must be tuned to specific robot
        public static final double driveKV = (1.51 / 12);
        public static final double driveKA = (0.27 / 12);

        /* Swerve Profiling Values */
        /** Meters per Second */
        public static final double maxSpeed = 4.5; //TODO: This must be tuned to specific robot
        /** Radians per Second */
        public static final double maxAngularVelocity = 10.0; //TODO: This must be tuned to specific robot

        /* Neutral Modes */
        public static final NeutralMode angleNeutralMode = NeutralMode.Coast;
        public static final NeutralMode driveNeutralMode = NeutralMode.Brake;

        /* Module Specific Constants */
        /* Front Left Module - Module 0 */
        public static final class Mod0 { //TODO: This must be tuned to specific robot
            public static final int driveMotorID = 36;
            public static final int angleMotorID = 37;
            public static final int canCoderID = 10;
            public static final boolean driveMotorInvert = true;
            public static final Rotation2d angleOffset = Rotation2d.fromDegrees(265.1+90);
            public static final SwerveModuleConstants constants = 
                new SwerveModuleConstants(driveMotorID, angleMotorID, canCoderID, angleOffset);
        }

        /* Front Right Module - Module 1 */
        public static final class Mod1 { //TODO: This must be tuned to specific robot
            public static final int driveMotorID = 31;
            public static final int angleMotorID = 33;
            public static final int canCoderID = 13;
            public static final Rotation2d angleOffset = Rotation2d.fromDegrees(38.5+90);
            public static final SwerveModuleConstants constants = 
                new SwerveModuleConstants(driveMotorID, angleMotorID, canCoderID, angleOffset);
        }
        
        /* Back Left Module - Module 2 */
        public static final class Mod2 { //TODO: This must be tuned to specific robot
            public static final int driveMotorID = 34;
            public static final int angleMotorID = 35;
            public static final int canCoderID = 12;
            public static final Rotation2d angleOffset = Rotation2d.fromDegrees(345.4+90);
            public static final SwerveModuleConstants constants = 
                new SwerveModuleConstants(driveMotorID, angleMotorID, canCoderID, angleOffset);
        }

        /* Back Right Module - Module 3 */
        public static final class Mod3 { //TODO: This must be tuned to specific robot
            public static final int driveMotorID = 39;
            public static final int angleMotorID = 40;
            public static final int canCoderID = 11;
            public static final Rotation2d angleOffset = Rotation2d.fromDegrees(94.4+90);
            public static final SwerveModuleConstants constants = 
                new SwerveModuleConstants(driveMotorID, angleMotorID, canCoderID, angleOffset);
        }
    }

    public static final class AutoConstants { //TODO: The below constants are used in the example auto, and must be tuned to specific robot
        public static final double kMaxSpeedMetersPerSecond = 3;
        public static final double kMaxAccelerationMetersPerSecondSquared = 3;
        public static final double kMaxAngularSpeedRadiansPerSecond = Math.PI;
        public static final double kMaxAngularSpeedRadiansPerSecondSquared = Math.PI;
    
        public static final double kPXController = 1;
        public static final double kPYController = 1;
        public static final double kPThetaController = 1;
    
        /* Constraint for the motion profilied robot angle controller */
        public static final TrapezoidProfile.Constraints kThetaControllerConstraints =
            new TrapezoidProfile.Constraints(
                kMaxAngularSpeedRadiansPerSecond, kMaxAngularSpeedRadiansPerSecondSquared);
    }

    public static final class VisionConstants {
        static final Transform3d robotToCam = new Transform3d(new Translation3d(0, 0, 0), new Rotation3d(0, 0, 0));
    }

    public static final class FieldConstants {
        static final double length = Units.feetToMeters(54);
        static final double width = Units.feetToMeters(27);
    }

}

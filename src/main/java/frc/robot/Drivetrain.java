package frc.robot;


import com.ctre.phoenix.sensors.PigeonIMU;

import frc.robot.SwerveX_Module;

import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveDriveOdometry;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

//import edu.wpi.first.wpilibj2.command.SubsystemBase;



public class Drivetrain {
    public SwerveDriveOdometry swerveOdometry;
    public SwerveX_Module[] mSwerveMods;
    public PigeonIMU gyro;
    final int pigeonID = 10;
    public static final double trackWidth = 29.75; //need to update for us
    public static final double wheelBase = 29.75; ////need to update for us
    
    public final SwerveDriveKinematics swerveKinematics = new SwerveDriveKinematics(
                new Translation2d(wheelBase / 2.0, trackWidth / 2.0), // frontRightModule or frontLeftModule
                new Translation2d(wheelBase / 2.0, -trackWidth / 2.0), // frontRightModule or frontLeftModule
                new Translation2d(-wheelBase / 2.0, trackWidth / 2.0), // backLeftModule or backRightModule
                new Translation2d(-wheelBase / 2.0, -trackWidth / 2.0)); // backLeftModule or backRightModule



    public Drivetrain() {
        /*
        frontLeftModule.setName("Front Left");
        frontRightModule.setName("Front Right");
        backLeftModule.setName("Back Left");
        backRightModule.setName("Back Right");
        */
        gyro = new PigeonIMU(pigeonID);
        gyro.configFactoryDefault();
        gyro.setYaw(0);
        
        //swerveOdometry = new SwerveDriveOdometry(swerveKinematics, new Rotation2d(gyro.getYaw()*(Math.PI/180)), null);

        mSwerveMods = new SwerveX_Module[] {

            /*
            int driveMotorChannel,
            int steerMotorChannel,
            int angleEncoderChannel
            double offset
            */
            new SwerveX_Module(15, 7, 12, -90.87890625), //frontLeftModule
            new SwerveX_Module(9, 8, 13, -213.75), //frontRightModule
            new SwerveX_Module(3, 2, 10, -69.785), //backLeftModule
            new SwerveX_Module(4, 5, 11, -351.457) //backRightModule
        };
    }


    public void drive(Translation2d translation, double rotation, boolean fieldOriented) {
        //rotation *= 2.0 / Math.hypot(wheelBase, trackWidth);
        ChassisSpeeds speeds;
        if (fieldOriented) {
            speeds = ChassisSpeeds.fromFieldRelativeSpeeds(translation.getX(), translation.getY(), rotation,
                    Rotation2d.fromDegrees(gyro.getYaw()));
        } else {
            speeds = new ChassisSpeeds(translation.getX(), translation.getY(), rotation);
        }

        SwerveModuleState[] states = swerveKinematics.toSwerveModuleStates(speeds);
        mSwerveMods[0].setDesiredState(states[0]);
        mSwerveMods[1].setDesiredState(states[1]);
        mSwerveMods[2].setDesiredState(states[2]);
        mSwerveMods[3].setDesiredState(states[3]);

        //swerveOdometry.update(Rotation2d.fromDegrees(gyro.getYaw()), states); 
        
    }

    public void update(){
        mSwerveMods[0].update();
        mSwerveMods[1].update();
        mSwerveMods[2].update();
        mSwerveMods[3].update();

    }

}

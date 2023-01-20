package frc.robot;

import com.ctre.phoenix.sensors.PigeonIMU;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveDriveOdometry;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Drivetrain {
    public SwerveDriveOdometry swerveOdometry;
    public SwerveX_Module[] mSwerveMods;
    public PigeonIMU gyro;
    final int pigeonID = 10;
    public static final double trackWidth = 29.75 * 0.0254; //units converted from inches to meters
    public static final double wheelBase = 29.75 * 0.0254; //units converted from inches to meters
    
    public final SwerveDriveKinematics swerveKinematics = new SwerveDriveKinematics(
                new Translation2d(wheelBase / 2.0, trackWidth / 2.0), //  frontLeftModule
                new Translation2d(wheelBase / 2.0, -trackWidth / 2.0), // frontRightModule
                new Translation2d(-wheelBase / 2.0, trackWidth / 2.0), // backLeftModule
                new Translation2d(-wheelBase / 2.0, -trackWidth / 2.0)); // backRightModule

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
            new SwerveX_Module(15, 7, 12, -90.87890625), //frontLeftModule
            new SwerveX_Module(9, 8, 13, -213.75), //frontRightModule
            new SwerveX_Module(3, 2, 10, -69.785), //backLeftModule
            new SwerveX_Module(4, 5, 11, -351.457) //backRightModule
        };
    }

    public void drive(Translation2d translation, double rotation, boolean fieldOriented) {
        rotation *= 2.0 / Math.hypot(wheelBase, trackWidth);
        ChassisSpeeds speeds;
        if (fieldOriented) {
            speeds = ChassisSpeeds.fromFieldRelativeSpeeds(translation.getX(), translation.getY(), rotation,
                    Rotation2d.fromDegrees(gyro.getYaw()));
        } else {
            speeds = new ChassisSpeeds(translation.getX(), translation.getY(), rotation);
        }
        
        SwerveModuleState[] states = swerveKinematics.toSwerveModuleStates(speeds);
        mSwerveMods[0].setDesiredState(states[0]);
        SmartDashboard.putNumber("Front Left Speed", states[0].speedMetersPerSecond);
        SmartDashboard.putNumber("Front Left Angle", states[0].angle.getRadians());
        mSwerveMods[1].setDesiredState(states[1]);
        SmartDashboard.putNumber("Front Right Speed", states[1].speedMetersPerSecond);
        SmartDashboard.putNumber("Front Right Angle", states[1].angle.getRadians());
        mSwerveMods[2].setDesiredState(states[2]);
        SmartDashboard.putNumber("Back Left Speed", states[2].speedMetersPerSecond);
        SmartDashboard.putNumber("Back Left Angle", states[2].angle.getRadians());
        mSwerveMods[3].setDesiredState(states[3]);
        SmartDashboard.putNumber("Back Right Speed", states[3].speedMetersPerSecond);
        SmartDashboard.putNumber("Back Right Angle", states[3].angle.getRadians());
        //swerveOdometry.update(Rotation2d.fromDegrees(gyro.getYaw()), states); 
    }

    public void update(){
        mSwerveMods[0].update();
        mSwerveMods[1].update();
        mSwerveMods[2].update();
        mSwerveMods[3].update();
    }
}
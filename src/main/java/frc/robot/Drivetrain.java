package frc.robot;

import com.ctre.phoenix.sensors.PigeonIMU;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveDriveOdometry;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveModuleState;

public class Drivetrain {
    public SwerveDriveOdometry swerveOdometry;
    public SwerveX_Module[] mSwerveMods;
    public PigeonIMU gyro;
    final int pigeonID = 16;
    public static final double trackWidth = Units.inchesToMeters(25.5);
    public static final double wheelBase = Units.inchesToMeters(25.5);
    
    public final SwerveDriveKinematics swerveKinematics = new SwerveDriveKinematics(
                new Translation2d(wheelBase / 2.0, trackWidth / 2.0), //  frontLeftModule
                new Translation2d(wheelBase / 2.0, -trackWidth / 2.0), // frontRightModule
                new Translation2d(-wheelBase / 2.0, trackWidth / 2.0), // backLeftModule
                new Translation2d(-wheelBase / 2.0, -trackWidth / 2.0)); // backRightModule

    public Drivetrain() {
        gyro = new PigeonIMU(pigeonID);
        gyro.configFactoryDefault();
        gyro.setYaw(0);
        //swerveOdometry = new SwerveDriveOdometry(swerveKinematics, new Rotation2d(gyro.getYaw()*(Math.PI/180)), null);

        mSwerveMods = new SwerveX_Module[] {
            new SwerveX_Module(15, 7, 12, 274.7), //frontLeftModule
            new SwerveX_Module(9, 8, 13, 39.9), //frontRightModule
            new SwerveX_Module(3, 2, 10, 256.9), //backLeftModule
            new SwerveX_Module(4, 5, 11, 176.7) //backRightModule
        };
    }
    public void brake(){
        mSwerveMods[0].setBrakeMode();
        mSwerveMods[1].setBrakeMode();
        mSwerveMods[2].setBrakeMode();
        mSwerveMods[3].setBrakeMode();
        
        mSwerveMods[0].setDesiredState(new SwerveModuleState(
            0, Rotation2d.fromDegrees(45.0)));
        mSwerveMods[1].setDesiredState(new SwerveModuleState(
            0, Rotation2d.fromDegrees(-45.0)));
        mSwerveMods[2].setDesiredState(new SwerveModuleState(
            0, Rotation2d.fromDegrees(-45.0)));
        mSwerveMods[3].setDesiredState(new SwerveModuleState(
            0, Rotation2d.fromDegrees(45.0)));
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

package frc.robot;

import java.util.ArrayList;
import java.util.List;

import org.photonvision.PhotonCamera;
import org.photonvision.PhotonUtils;
import org.photonvision.targeting.PhotonPipelineResult;
import org.photonvision.targeting.PhotonTrackedTarget;

import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Quaternion;
import edu.wpi.first.math.geometry.Pose2d;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Transform2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Arm.GamePieces;

public class Vision {
    //game piece 
    PhotonCamera gamePieceCamera = new PhotonCamera("gamePieceCamera");;
    PhotonPipelineResult gamePieceResult = new PhotonPipelineResult();
    boolean gamePieceHasTargets = false;
    List<PhotonTrackedTarget> gamePieceTargets;
    PhotonTrackedTarget gamePieceBestTarget = new PhotonTrackedTarget();
    double gamePieceYaw, gamePiecePitch, gamePieceSkew, gamePieceAreaPercent = 999.0;

    // april tag back camera
    PhotonCamera aprilTagCameraBack = new PhotonCamera("aprilTagCameraBack");
    PhotonPipelineResult aprilTagResult = new PhotonPipelineResult();
    boolean aprilTagHasTargets = false;
    List<PhotonTrackedTarget> aprilTagTargets;
    PhotonTrackedTarget aprilTagBestTarget = new PhotonTrackedTarget();
    int fiducialID;
    double aprilTagX, aprilTagY, aprilTagZAngle, aprilTagZ = 999.0;

    public Vision(){
        gamePieceCamera.setPipelineIndex(2);
    }    

    public void gamePiecePeriodic(){

        if (SmartDashboard.getString("Object", "none").equals("cube")){
            gamePieceCamera.setPipelineIndex(1);
        }
        else if (SmartDashboard.getString("Object", "none").equals("cone")){
            gamePieceCamera.setPipelineIndex(2);
        }

        gamePieceResult = gamePieceCamera.getLatestResult();
        
        gamePieceHasTargets = gamePieceResult.hasTargets();

        if (gamePieceHasTargets){
            gamePieceTargets = gamePieceResult.getTargets();
            gamePieceBestTarget = gamePieceResult.getBestTarget();

            gamePieceYaw = gamePieceBestTarget.getYaw();
            gamePiecePitch = gamePieceBestTarget.getPitch();
            gamePieceSkew = gamePieceBestTarget.getSkew();
            gamePieceAreaPercent = gamePieceBestTarget.getArea();
        }
        else{
            gamePieceYaw = 999.0;
            gamePiecePitch = 999.0;
            gamePieceSkew = 999.0;
            gamePieceAreaPercent = 999.0;
        }

        SmartDashboard.putNumber("Game Piece Yaw", gamePieceYaw);
        SmartDashboard.putNumber("Game Piece Pitch", gamePiecePitch);
        SmartDashboard.putNumber("Game Piece Skew", gamePieceSkew);
        SmartDashboard.putNumber("Game Piece Area Percent", gamePieceAreaPercent);
    }

    public void aprilTagPeriodic() {
        aprilTagResult = aprilTagCameraBack.getLatestResult();
        
        aprilTagHasTargets = aprilTagResult.hasTargets();

        if (aprilTagHasTargets) {
            aprilTagTargets = aprilTagResult.getTargets();
            aprilTagBestTarget = aprilTagResult.getBestTarget();

            fiducialID = aprilTagBestTarget.getFiducialId();
            aprilTagX = aprilTagBestTarget.getBestCameraToTarget().getX();
            aprilTagY = aprilTagBestTarget.getBestCameraToTarget().getY();
            aprilTagZ = aprilTagBestTarget.getBestCameraToTarget().getZ();
            aprilTagZAngle = aprilTagBestTarget.getBestCameraToTarget().getRotation().getAngle();
        } else {
            fiducialID = -1;
            aprilTagX = 999.0;
            aprilTagY = 999.0;
            aprilTagZAngle = 999.0;
        }

        // Update SmartDashboard for AprilTag
        SmartDashboard.putNumber("Fiducial ID", fiducialID);
        SmartDashboard.putNumber("AprilTag X (m)", aprilTagX);
        SmartDashboard.putNumber("AprilTag Y (m)", aprilTagY);
        SmartDashboard.putNumber("AprilTag Z (m)", aprilTagZ);
        SmartDashboard.putNumber("AprilTag Z Angle", aprilTagZAngle);
    }
    
}

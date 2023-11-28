package frc.robot;

import java.util.*;

import org.photonvision.PhotonCamera;
import org.photonvision.targeting.PhotonPipelineResult;
import org.photonvision.targeting.PhotonTrackedTarget;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Class for handling vision processing.
 */
public class Vision {
    // game piece 
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

    // reflective tape
    PhotonCamera reflectiveTapeCamera = new PhotonCamera("reflectiveTapeCamera");
    PhotonPipelineResult reflectiveTapeResult = new PhotonPipelineResult();
    boolean reflectiveTapeHasTargets = false;
    List<PhotonTrackedTarget> reflectiveTapeTargets;
    PhotonTrackedTarget reflectiveTapeBestTarget = new PhotonTrackedTarget();
    double reflectiveTapeYaw, reflectiveTapePitch, reflectiveTapeSkew, reflectiveTapeAreaPercent = 999.0;

    /**
     * Constructor for the Vision class.
     */
    public Vision(){
        gamePieceCamera.setPipelineIndex(2);
    }    

    /**
     * Periodic method for processing vision data related to reflective tape.
     */
    public void reflectiveTapePeriodic(){
        reflectiveTapeResult = reflectiveTapeCamera.getLatestResult();
        
        reflectiveTapeHasTargets = reflectiveTapeResult.hasTargets();

        if (reflectiveTapeHasTargets){
            reflectiveTapeTargets = reflectiveTapeResult.getTargets();
            reflectiveTapeBestTarget = reflectiveTapeResult.getBestTarget();

            reflectiveTapeYaw = reflectiveTapeBestTarget.getYaw();
            reflectiveTapePitch = reflectiveTapeBestTarget.getPitch();
            reflectiveTapeSkew = reflectiveTapeBestTarget.getSkew();
            reflectiveTapeAreaPercent = reflectiveTapeBestTarget.getArea();
        }
        else{
            reflectiveTapeYaw = 999.0;
            reflectiveTapePitch = 999.0;
            reflectiveTapeSkew = 999.0;
            reflectiveTapeAreaPercent = 999.0;
        }

        // Update SmartDashboard for reflective tape 
        SmartDashboard.putNumber("Reflective Tape Yaw", reflectiveTapeYaw);
        SmartDashboard.putNumber("Reflective Tape Pitch", reflectiveTapePitch);
        SmartDashboard.putNumber("Reflective Tape Skew", reflectiveTapeSkew);
        SmartDashboard.putNumber("Reflective Tape Area Percent", reflectiveTapeAreaPercent);
    }

    /**
     * Periodic method for processing vision data related to the AprilTag.
     */
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
            aprilTagZ = 999.0;
            aprilTagZAngle = 999.0;
        }

        // Update SmartDashboard for AprilTag
        SmartDashboard.putNumber("Fiducial ID", fiducialID);
        SmartDashboard.putNumber("AprilTag X (m)", aprilTagX);
        SmartDashboard.putNumber("AprilTag Y (m)", aprilTagY);
        SmartDashboard.putNumber("AprilTag Z (m)", aprilTagZ);
        SmartDashboard.putNumber("AprilTag Z Angle", aprilTagZAngle);
    }

    /**
     * Periodic method for processing vision data related to the game piece.
     */
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

        // Update SmartDashboard for game piece
        SmartDashboard.putNumber("Game Piece Yaw", gamePieceYaw);
        SmartDashboard.putNumber("Game Piece Pitch", gamePiecePitch);
        SmartDashboard.putNumber("Game Piece Skew", gamePieceSkew);
        SmartDashboard.putNumber("Game Piece Area Percent", gamePieceAreaPercent);
    }

    /**
     * Gets the Fiducial ID of the AprilTag.
     * @return The Fiducial ID.
     */
    public int getFiducialID(){
        return fiducialID;
    }

    /**
     * Gets the X coordinate of the AprilTag in meters.
     * @return The X coordinate.
     */
    public double getAprilTagX(){
        return aprilTagX;
    }

    /**
     * Gets the Y coordinate of the AprilTag in meters.
     * @return The Y coordinate.
     */
    public double getAprilTagY(){
        return aprilTagY;
    }

    /**
     * Gets the Z coordinate of the AprilTag in meters.
     * @return The Z coordinate.
     */
    public double getAprilTagZ(){
        return aprilTagZ;
    }

    /**
     * Gets the Z angle of the AprilTag in degrees.
     * @return The Z angle.
     */
    public double getAprilTagZAngle(){
        return aprilTagZAngle * (180 / Math.PI);
    }

    /**
     * Gets the Yaw angle of the game piece.
     * @return The Yaw angle.
     */
    public double getGamePieceYaw(){
        return gamePieceYaw;
    }

    /**
     * Gets the Pitch angle of the game piece.
     * @return The Pitch angle.
     */
    public double getGamePiecePitch(){
        return gamePiecePitch;
    }

    /**
     * Gets the Skew angle of the game piece.
     * @return The Skew angle.
     */
    public double getGamePieceSkew(){
        return gamePieceSkew;
    }

    /**
     * Gets the Area Percent of the game piece.
     * @return The Area Percent.
     */
    public double getGamePieceAreaPercent(){
        return gamePieceAreaPercent;
    }

    /**
     * Gets the Yaw angle of the reflective tape.
     * @return The Yaw angle.
     */
    public double getReflectiveTapeYaw(){
        return reflectiveTapeYaw;
    }

    /**
     * Gets the Pitch angle of the reflective tape.
     * @return The Pitch angle.
     */
    public double getReflectiveTapePitch(){
        return reflectiveTapePitch;
    }

    /**
     * Gets the Skew angle of the reflective tape.
     * @return The Skew angle.
     */
    public double getReflectiveTapeSkew(){
        return reflectiveTapeSkew;
    }

    /**
     * Gets the Area Percent of the reflective tape.
     * @return The Area Percent.
     */
    public double getReflectiveTapeAreaPercent(){
        return reflectiveTapeAreaPercent;
    }

}
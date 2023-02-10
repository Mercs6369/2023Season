package frc.robot;

import java.util.List;

import org.photonvision.PhotonCamera;
import org.photonvision.PhotonUtils;
import org.photonvision.targeting.PhotonPipelineResult;
import org.photonvision.targeting.PhotonTrackedTarget;

import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Pose2d;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import edu.wpi.first.apriltag.AprilTag;
import edu.wpi.first.apriltag.AprilTagFieldLayout;

public class Vision {
    public Pose3d getTag(int id){
        final Pose3d tag01 =
                new Pose3d(
                        new Pose2d(
                                610.77,
                                42.19,
                                Rotation2d.fromDegrees(180)));
    
        final Pose3d tag02 =
                new Pose3d(
                        new Pose2d(
                                610.77,
                                108.19,
                                Rotation2d.fromDegrees(180)));
    
        final Pose3d tag03 =
                new Pose3d(
                        new Pose2d(
                                610.77,
                                174.19,
                                Rotation2d.fromDegrees(180)));
    
        final Pose3d tag04 =
                new Pose3d(
                        new Pose2d(
                                636.96,
                                265.74,
                                Rotation2d.fromDegrees(180)));
    
        final Pose3d tag05 =
                new Pose3d(
                        new Pose2d(
                                14.25,
                                265.74,
                                Rotation2d.fromDegrees(0)));
    
    
        final Pose3d tag06 =
                new Pose3d(
                        new Pose2d(
                                40.45,
                                174.19,
                                Rotation2d.fromDegrees(0)));
    
    
        final Pose3d tag07 =
                new Pose3d(
                        new Pose2d(
                                40.45,
                                108.19,
                                Rotation2d.fromDegrees(0)));
    
        final Pose3d tag08 =
                new Pose3d(
                        new Pose2d(
                                40.45,
                                42.19,
                                Rotation2d.fromDegrees(0)));

        if (id == 1){
            return tag01;
        }
        else if (id == 2){
            return tag02;
        }
        else if (id == 3){
            return tag03;
        }
        else if (id == 4){
            return tag04;
        }
        else if (id == 5){
            return tag05;
        }
        else if (id == 6){
            return tag06;
        }
        else if (id == 7){
            return tag07;
        }
        else {
            return tag08;
        }
    }

    PhotonCamera camera = new PhotonCamera("Arducam_OV9281_USB_Camera"); // April Tag camera
    PhotonCamera gamePieceCamera = new PhotonCamera("Microsoft_LifeCam_HD-3000"); // Game Piece Camera
    boolean hasTargets; // april tags
    Color_Sensor m_color_sensor = new Color_Sensor();
    Pose3d robotPose = new Pose3d();
    PhotonTrackedTarget target = new PhotonTrackedTarget();

    private boolean gamePieceHasTargets = false; // Game Piece has Targets
    private PhotonPipelineResult gamePieceCameraResult = new PhotonPipelineResult(); // Game Piece Detection Result
    private PhotonPipelineResult result = new PhotonPipelineResult();
    
    private final double CAMERA_HEIGHT_METERS = 0.0;
    private final double TARGET_HEIGHT_METERS = 0.0;
    private final double CAMERA_PITCH_RADIANS = 0.0;
    private double range = 0.0;

   /**
    * Performs a sample of Rev Color Sensor V3 RGB, normalizes the data, and attempts to match to expected colors.
    * Member variables "color_string" and "match" are updated with results and info is output to SmartDashboard.
    *
    */
    public void CS_RGB_measure() {
        m_color_sensor.color_sensor_RGB_measure();
    }
 
   /**
    * Performs a sample of Rev Color Sensor V3 proximity data and updates member variable "proximity" with result.
    * Info is output to SmartDashboard also.
    *
    */
    public void CS_Prox_measure() {
        m_color_sensor.color_sensor_prox_measure();
    }

    public void targeting() {
        

        result = camera.getLatestResult();
        hasTargets = result.hasTargets();
        if (hasTargets) {
            this.result = result;
        }

        target = result.getBestTarget();

        if (result.hasTargets()) {
            // First calculate range
            range = PhotonUtils.calculateDistanceToTargetMeters(
                            CAMERA_HEIGHT_METERS,
                            TARGET_HEIGHT_METERS,
                            CAMERA_PITCH_RADIANS,
                            ((result.getBestTarget().getPitch())*(Math.PI/180)));
            this.result = result;

            robotPose = PhotonUtils.estimateFieldToRobotAprilTag(target.getBestCameraToTarget(), getTag(target.getFiducialId()), Constants.VisionConstants.robotToCam); 
          
        }

    }

    public int getID(){
        return target.getFiducialId();
    }

    public double getY(){
        return target.getBestCameraToTarget().getY(); 
    }

    public double getX(){
        return target.getBestCameraToTarget().getX(); 
    }


    public PhotonTrackedTarget getTargetWithID(int id) { 
        List<PhotonTrackedTarget> targets = result.getTargets(); 
        for (PhotonTrackedTarget i : targets) {
            if (i.getFiducialId() == id) { 
                return i; 
            }
        }
        return null; 
    }

    public int getBestTarget() {
        if (hasTargets) {
        return result.getBestTarget().getFiducialId(); 
        }
        else {
            return -1; 
        }
    }

    /*
    getDistanceLowerConeNode() takes angleCenterVisionBounds, or the angle in degrees from the limelight to the 
    center of the vision bounds, and limelightHeight, or the height of the limelight from the ground as parameters.

    The limelightHeight does not need to be lower than 22.125 for the method to function, as that signals that the 
    limelightHeight will simply be taller than the level of the lower reflective tape. 
    */ 
    public double getDistanceLowerConeNode(double angleCenterVisionBounds, double limelightHeight) {
       return ((11.875 + 22.125-limelightHeight)/(Math.tan(Math.toRadians(Math.abs(angleCenterVisionBounds)-1.58164))))-8.75;
    }




    /*
     *
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * Everything above this is not Jonathan's code
     * 
     * 
     * 
     * 
     * Everything under this is Jonathan's code.
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     */


    public int getGamePieceCameraPipeline() {
        return gamePieceCamera.getPipelineIndex(); // 0 = cones, 1 = cubes
    }


    enum infoTypeToReturn { // this is used when using GetCubeInfo/GetConeInfo
        Area,
        Pitch,
        Skew,
        Yaw,
        Orientation,
    }


    enum gamePiecePipelineIndex { // this is used for the setGamePiecePipeline method
        driver,
        cube,
        cone,
    }


    double coneAreaAt1Foot;
    double cubeAreaAt1Foot;



    private double getOrientationOfCone() {
        PhotonTrackedTarget bestTarget = gamePieceCameraResult.getBestTarget();

        double objectWidth = Math.abs(bestTarget.getDetectedCorners().get(0).x - bestTarget.getDetectedCorners().get(1).x);
        double objectHeight = Math.abs(bestTarget.getDetectedCorners().get(0).y - bestTarget.getDetectedCorners().get(3).y);
     
        if (objectHeight > (objectWidth + 10)) {
            // Probably standing up
            return 1.0;
        } else {
            // Probably on the side
            return 0.0;
        }
    }  


    public String getBestTargetGlobal() {
        
        double CubeArea;
        double ConeArea;

        setGamePiecePipeline(gamePiecePipelineIndex.cone);     
        gamePieceCameraResult = gamePieceCamera.getLatestResult();

        // cone pipeline
        if (gamePieceCameraResult.hasTargets()) {
            PhotonTrackedTarget bestTarget = gamePieceCameraResult.getBestTarget();
            ConeArea = bestTarget.getArea();
        }


        // cube pipeline
        setGamePiecePipeline(gamePiecePipelineIndex.cube);
        gamePieceCameraResult = gamePieceCamera.getLatestResult();
        
        if (gamePieceCameraResult.hasTargets()) {
            PhotonTrackedTarget bestTarget = gamePieceCameraResult.getBestTarget();
            CubeArea = bestTarget.getArea();
        }


        return "in progress"; // don't mess with any code here


    }
    

    
    public double getCubeInfo(infoTypeToReturn valueToGet) {
        setGamePiecePipeline(gamePiecePipelineIndex.cube);
        gamePieceCameraResult = gamePieceCamera.getLatestResult();
        
        
        if (gamePieceCameraResult.hasTargets()) { // if it does has a target then do this :D
            PhotonTrackedTarget bestTarget = gamePieceCameraResult.getBestTarget();
            switch (valueToGet) {
                case Area:
                    return bestTarget.getArea();
                case Pitch:
                    return bestTarget.getPitch();
                case Skew:
                    return bestTarget.getSkew();
                case Yaw:
                    return bestTarget.getYaw();
                default:
                    break;
            }
        }
        return -1; // if camera doesn't have targets
    }


    public double getConeInfo(infoTypeToReturn valueToGet) {
        setGamePiecePipeline(gamePiecePipelineIndex.cone);
        gamePieceCameraResult = gamePieceCamera.getLatestResult();

        if (gamePieceCameraResult.hasTargets()) { // if it does has a target then do this :D
            PhotonTrackedTarget bestTarget = gamePieceCameraResult.getBestTarget();
            switch (valueToGet) {
                case Area:
                    return bestTarget.getArea();
                case Pitch:
                    return bestTarget.getPitch();
                case Skew:
                    return bestTarget.getSkew();
                case Yaw:
                    return bestTarget.getYaw();
                case Orientation:
                    return getOrientationOfCone();
                default:
                    break;
            }
        }
        return -1; // if camera doesn't have targets
    }




    public void setGamePiecePipeline(gamePiecePipelineIndex newPipelineName) {
       if (newPipelineName == gamePiecePipelineIndex.driver) {
        gamePieceCamera.setPipelineIndex(2); // idx 2 is driver
       } else if (newPipelineName == gamePiecePipelineIndex.cone) {
        gamePieceCamera.setPipelineIndex(0); // idx 0 is cone
       } else if (newPipelineName == gamePiecePipelineIndex.cube) {
        gamePieceCamera.setPipelineIndex(1); // idx 1 is cube
       } else {
        System.out.println("Error in Vision.java - newPipelineName wasn't of the proper enum. Not sure how this is possible :P");
       }


    }
}

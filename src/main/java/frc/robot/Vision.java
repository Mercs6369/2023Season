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


    /**
     * Returns the current game piece targeting pipeline index. 0 = cone, 1 = cube, other is probably driver mode
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


    boolean hasGottenOrientation = false;


    List<Integer> last_four_orientations_of_cone = new ArrayList<>(); // list not array



    /**
     * 
     * Returns the orientation of the cone, this should only be run after a cone game piece has been identified. (1.0 - Standing Up || 0.0 - On It's Side)
     */
    private double getOrientationOfCone() {
        PhotonTrackedTarget bestTarget = gamePieceCameraResult.getBestTarget();


        if (last_four_orientations_of_cone.isEmpty()) {
            last_four_orientations_of_cone.add(0);
            last_four_orientations_of_cone.add(0);
            last_four_orientations_of_cone.add(0);
            last_four_orientations_of_cone.add(0);
        }


        
        
        // gets the farthest left x
        double farthest_left_x = bestTarget.getMinAreaRectCorners().get(0).x;
        if (bestTarget.getMinAreaRectCorners().get(1).x < farthest_left_x) {
            farthest_left_x = bestTarget.getMinAreaRectCorners().get(1).x;

        } else if (bestTarget.getMinAreaRectCorners().get(2).x < farthest_left_x) {
            farthest_left_x = bestTarget.getMinAreaRectCorners().get(2).x;

        } else {
            farthest_left_x = bestTarget.getMinAreaRectCorners().get(3).x;
        }

        // gets the farthest right x
        double farthest_right_x = bestTarget.getMinAreaRectCorners().get(0).x;
        if (bestTarget.getMinAreaRectCorners().get(1).x > farthest_right_x) {
            farthest_right_x = bestTarget.getMinAreaRectCorners().get(1).x;

        } else if (bestTarget.getMinAreaRectCorners().get(2).x > farthest_right_x) {
            farthest_right_x = bestTarget.getMinAreaRectCorners().get(2).x;

        } else {
            farthest_right_x = bestTarget.getMinAreaRectCorners().get(3).x;
        }


        // gets the highest right x
        double highest_right_y = bestTarget.getMinAreaRectCorners().get(0).y;
        if (bestTarget.getMinAreaRectCorners().get(1).y > highest_right_y) {
            highest_right_y = bestTarget.getMinAreaRectCorners().get(1).y;

        } else if (bestTarget.getMinAreaRectCorners().get(2).y > highest_right_y) {
            highest_right_y = bestTarget.getMinAreaRectCorners().get(2).y;

        } else {
            highest_right_y = bestTarget.getMinAreaRectCorners().get(3).y;
        }

         // gets the lowest right x
         double lowest_right_y = bestTarget.getMinAreaRectCorners().get(0).y;
         if (bestTarget.getMinAreaRectCorners().get(1).y < lowest_right_y) {
            lowest_right_y = bestTarget.getMinAreaRectCorners().get(1).y;
 
        } else if (bestTarget.getMinAreaRectCorners().get(2).y > lowest_right_y) {
            lowest_right_y = bestTarget.getMinAreaRectCorners().get(2).y;
 
        } else {
            lowest_right_y = bestTarget.getMinAreaRectCorners().get(3).y;
        }






        double objectHeight = Math.abs(highest_right_y - lowest_right_y);
        double objectWidth = Math.abs(farthest_left_x - farthest_right_x);



        SmartDashboard.putNumber("highest right y",highest_right_y);
        SmartDashboard.putNumber("lowest right y",lowest_right_y);
        SmartDashboard.putNumber("farthest left x",farthest_left_x);
        SmartDashboard.putNumber("farthest right x",farthest_right_x);


        if (objectHeight >= (objectWidth - 10)) {
            last_four_orientations_of_cone.add(1);
            last_four_orientations_of_cone.remove(0);
            int sum = last_four_orientations_of_cone.get(0) + last_four_orientations_of_cone.get(1) + last_four_orientations_of_cone.get(2) + last_four_orientations_of_cone.get(3);

            if (sum > 2){
            SmartDashboard.putString("Orientation", "Standing up");
            return 1.0;
            } else {
                return 0.0;
            }

        } else {
            last_four_orientations_of_cone.add(0);
            last_four_orientations_of_cone.remove(0);
            int sum = last_four_orientations_of_cone.get(0) + last_four_orientations_of_cone.get(1) + last_four_orientations_of_cone.get(2) + last_four_orientations_of_cone.get(3);

            if (sum < 2){
                SmartDashboard.putString("Orientation", "On it's side, probably, ehh, probably not, who knows");
                return 0.0;
            } else {
                return 1.0;
            }
        }


        
    }  
    



    /**
    * 
    *   Returns the best target, can return 3 strings: Cone - The best/closest target is a cone || Cube - The best/closest target is a cone || Error - There is not a target to evaluate.
    *
    */
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

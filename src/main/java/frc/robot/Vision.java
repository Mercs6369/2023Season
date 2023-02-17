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
                                1.03,
                                2.73,
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
    Transform2d robotPose2d = new Transform2d();
    PhotonTrackedTarget target = new PhotonTrackedTarget();

    double gamePieceSwerveCommands[] = {0,0,0};

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

        target = result.getBestTarget();

        if (result.hasTargets()) {

/*             robotPose = PhotonUtils.estimateFieldToRobotAprilTag(
                new Transform3d(
                    new Translation3d(
                        target.getBestCameraToTarget().getX(), target.getBestCameraToTarget().getY(), target.getBestCameraToTarget().getZ()
                    )
                    ,
                    new Rotation3d(0.0, 0.0, Math.toRadians(target.getBestCameraToTarget().getRotation().getAngle()))
                )
                , getTag(target.getFiducialId())
                , Constants.VisionConstants.robotToCam
            ); */

/*
            robotPose2d = PhotonUtils.estimateCameraToTarget(new Translation2d(
                target.getBestCameraToTarget().getX(), target.getBestCameraToTarget().getY()
                )
                , new Pose2d(getTag(target.getFiducialId()).getX(), getTag(target.getFiducialId()).getY(), new Rotation2d(Math.toDegrees(getTag(target.getFiducialId()).getRotation().getAngle())))                              
                , new Rotation2d(Math.toRadians(target.getBestCameraToTarget().getRotation().getAngle())));
*/
            robotPose2d = robotPosition(
                new Pose2d(getTag(target.getFiducialId()).getX(), getTag(target.getFiducialId()).getY(), new Rotation2d(Math.toDegrees(getTag(target.getFiducialId()).getRotation().getAngle()))), 
                new Pose2d(target.getBestCameraToTarget().getX(), target.getBestCameraToTarget().getY(), new Rotation2d(target.getBestCameraToTarget().getRotation().getAngle())));
        }
    }

    public Transform2d robotPosition(Pose2d aprilTag, Pose2d cameraToTarget){

        return new Transform2d(new Translation2d(((aprilTag.getX() * Math.cos(aprilTag.getRotation().getDegrees())) + cameraToTarget.getX()), aprilTag.getY() + cameraToTarget.getY()), new Rotation2d(Math.toRadians(restrictedRageAngle((aprilTag.getRotation().getDegrees() + cameraToTarget.getRotation().getDegrees())))));
    }

    public double restrictedRageAngle(double angle){

        if (angle > 180){
            return angle - 360;
        }
        else {
            return angle - 360;
        }
        
    }



    public int getID(){
        return target.getFiducialId();
    }

    public double getY(){
        return robotPose2d.getY(); 
    }

    public double getX(){
        return robotPose2d.getX(); 
    }

    public double getYaw(){
        return robotPose2d.getRotation().getDegrees();
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
     * Returns the current game piece targeting pipeline index. 0 = cone, 1 = cube, any other value is probably driver mode
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





    List<Integer> last_four_orientations_of_cone = new ArrayList<>(); // list not array



    /**
     * Returns the orientation of the cone, this should only be run after a cone game piece has been identified. (1.0 - Standing Up || 0.0 - On It's Side)
     */
    private double getOrientationOfCone() {
        PhotonTrackedTarget bestTarget = gamePieceCameraResult.getBestTarget();
        //PhotonTrackedTarget bestTarget = gamePieceCameraResult.getTargets().get(0);

        if (last_four_orientations_of_cone.isEmpty()) {
            last_four_orientations_of_cone.add(0);
            last_four_orientations_of_cone.add(0);
            last_four_orientations_of_cone.add(0);
            last_four_orientations_of_cone.add(0);
            last_four_orientations_of_cone.add(0);
            last_four_orientations_of_cone.add(0);

        }

        double[] x_points = {0, 0, 0, 0};
        double[] y_points = {0, 0, 0, 0};

        for (int i = 0; i < 4; i++) {
            x_points[i] = bestTarget.getMinAreaRectCorners().get(i).x;
            y_points[i] = bestTarget.getMinAreaRectCorners().get(i).y;
        }

        double farthest_right_x = x_points[0];
        double highest_right_y = y_points[0];
        double farthest_left_x = x_points[0];
        double lowest_right_y = y_points[0];

        for (int i = 1; i < x_points.length; i++) {
            farthest_right_x = Math.max(farthest_right_x, x_points[i]);
            highest_right_y = Math.max(highest_right_y, y_points[i]);
            farthest_left_x = Math.min(farthest_left_x, x_points[i]);
            lowest_right_y = Math.min(lowest_right_y, y_points[i]);
        }

        double objectHeight = Math.abs(highest_right_y - lowest_right_y);
        double objectWidth = Math.abs(farthest_left_x - farthest_right_x);


        if (objectWidth > .1) {

            double objectRatio = objectHeight/objectWidth;
            SmartDashboard.putNumber("Ratio thing yess eyss", objectRatio);
            if (objectRatio >= .40 && objectRatio <= .85) {
                last_four_orientations_of_cone.add(0);
                last_four_orientations_of_cone.remove(0);
                int sum = last_four_orientations_of_cone.get(0) + last_four_orientations_of_cone.get(1) + last_four_orientations_of_cone.get(2) + last_four_orientations_of_cone.get(3) + last_four_orientations_of_cone.get(4) + last_four_orientations_of_cone.get(5); 

                // on its side

                SmartDashboard.putString("Guess", "On it's side.");
                if (sum < 3){
                    SmartDashboard.putString("Average Orientation", "On it's side.");
                    return 0.0;
                } else {
                    return 1.0;
                }


            } else if (objectRatio >= .85 && objectRatio <= 1.35) {
                last_four_orientations_of_cone.add(0);
                last_four_orientations_of_cone.remove(0);
                int sum = last_four_orientations_of_cone.get(0) + last_four_orientations_of_cone.get(1) + last_four_orientations_of_cone.get(2) + last_four_orientations_of_cone.get(3) + last_four_orientations_of_cone.get(4) + last_four_orientations_of_cone.get(5); 
                // head on

                SmartDashboard.putString("Guess", "Head on.");
                if (sum < 3){
                    SmartDashboard.putString("Average Orientation", "On it's side.");
                    return 0.0;
                } else {
                    return 1.0;
                }



            } else if (objectRatio >= 1.35) { // is the second and neccesary
                last_four_orientations_of_cone.add(1);
                last_four_orientations_of_cone.remove(0);
                int sum = last_four_orientations_of_cone.get(0) + last_four_orientations_of_cone.get(1) + last_four_orientations_of_cone.get(2) + last_four_orientations_of_cone.get(3) + last_four_orientations_of_cone.get(4) + last_four_orientations_of_cone.get(5);                 // standing up

                SmartDashboard.putString("Guess", "Standing Up");
                if (sum > 3){
                    SmartDashboard.putString("Average Orientation", "Standing up");
                    return 1.0;
                } else {
                    return 0.0;
                }

            } else {
                return -9999999.0;
            }




        } else {
            // pAnIK
            return -9999999.0;
        }

/* 
        

        if (objectHeight >= (objectWidth - 10)) {
            last_four_orientations_of_cone.add(1);
            last_four_orientations_of_cone.remove(0);
            int sum = last_four_orientations_of_cone.get(0) + last_four_orientations_of_cone.get(1) + last_four_orientations_of_cone.get(2) + last_four_orientations_of_cone.get(3);
            SmartDashboard.putString("Guess", "Standing Up");
            if (sum > 2){
                SmartDashboard.putString("Average Orientation", "Standing up");
                return 1.0;
            } else {
                return 0.0;
            }

        } else {
            last_four_orientations_of_cone.add(0);
            last_four_orientations_of_cone.remove(0);
            int sum = last_four_orientations_of_cone.get(0) + last_four_orientations_of_cone.get(1) + last_four_orientations_of_cone.get(2) + last_four_orientations_of_cone.get(3);
            SmartDashboard.putString("Guess", "Side");
            if (sum < 2){
                SmartDashboard.putString("Average Orientation", "Side");
                return 0.0;
            } else {
                return 1.0;
            }
        }
 */

        
    }  
    



    /**
    *   Returns the best target, can return 3 strings: Cone - The best/closest target is a cone || Cube - The best/closest target is a cone || Error - There is not a target to evaluate.
    *   Is not finished yet. So I wouldn't recommend using this
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
        } else {
            ConeArea = -1;
        }


        // cube pipeline
        setGamePiecePipeline(gamePiecePipelineIndex.cube);
        gamePieceCameraResult = gamePieceCamera.getLatestResult();
        
        if (gamePieceCameraResult.hasTargets()) {
            PhotonTrackedTarget bestTarget = gamePieceCameraResult.getBestTarget();
            CubeArea = bestTarget.getArea();
        } else {
            CubeArea = -1;
        }


        if (CubeArea == -1 && ConeArea == -1) {
            return "No Target";
        }

        if (CubeArea > ConeArea) {
            return "Cube";
        } else {
            return "Cone";
        }

    }
    

    
    /**
     * Gets the info of the cube based off of valueToGet
     * @param valueToGet Can be: Area, Pitch, Skew, and Yaw
     * @return Returns the requested value based off of valueToGet
     */
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


    /**
     * Gets the info of the cone based off of valueToGet
     * @param valueToGet Can be: Area, Pitch, Skew, Yaw, and Orientation
     * @return Returns the requested value based off of valueToGet
     */
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



    /**
     * Sets the gamePieceCamera pipeline based off of newPipelineName
     * @param newPipelineName Can be: Driver, Cone, or Cube
     */
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


    /**
     * Run this to get the values of the target (only one) that is currently onscreen.
     * @return Returns a two things. The Yaw of the object, and the Area of the object.
     */
    public double[] runAlignmentProcess() {    
        String best_Target_Global = getBestTargetGlobal();
        
        
        if (!(best_Target_Global == "No Target")) {
            gamePieceSwerveCommands[0] = 0.0;
            gamePieceSwerveCommands[1] = 0.0;
            
            if (best_Target_Global == "Cube") {
                setGamePiecePipeline(gamePiecePipelineIndex.cube);
                gamePieceSwerveCommands[0] = getCubeInfo(infoTypeToReturn.Yaw); // x
                gamePieceSwerveCommands[1] = getCubeInfo(infoTypeToReturn.Area);
                if (gamePieceSwerveCommands[0] < 4 && gamePieceSwerveCommands[0] > -4) {
                    gamePieceSwerveCommands[0] = 0;
                }
                if (gamePieceSwerveCommands[0] > 4 && gamePieceSwerveCommands[0] < 7) {
                    gamePieceSwerveCommands[0] = .65;
                }
                if (gamePieceSwerveCommands[0] < -4 && gamePieceSwerveCommands[0] > -7) {
                    gamePieceSwerveCommands[0] = -.65;
                }

             
                

            } else {
                setGamePiecePipeline(gamePiecePipelineIndex.cone);
                gamePieceSwerveCommands[0] = getCubeInfo(infoTypeToReturn.Yaw);
                gamePieceSwerveCommands[1] = getCubeInfo(infoTypeToReturn.Area);
                if (gamePieceSwerveCommands[0] < 4 && gamePieceSwerveCommands[0] > -4) {
                    gamePieceSwerveCommands[0] = 0;
                }
                if (gamePieceSwerveCommands[0] > 4 && gamePieceSwerveCommands[0] < 7) {
                    gamePieceSwerveCommands[0] = 4.39822971504;
                }
                if (gamePieceSwerveCommands[0] < -4 && gamePieceSwerveCommands[0] > -7) {
                    gamePieceSwerveCommands[0] = -4.39822971504;
                }

              

            }
            return gamePieceSwerveCommands;
        } else {
            gamePieceSwerveCommands[0] = -99999.0; // No targets
            gamePieceSwerveCommands[1] = -99999.0;
            return gamePieceSwerveCommands;
        }

    }



}

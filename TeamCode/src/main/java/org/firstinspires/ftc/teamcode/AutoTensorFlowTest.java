/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcontroller.external.samples.HardwarePushbot;
import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;

import java.util.Base64;
import java.util.List;

import static org.firstinspires.ftc.teamcode.Hardware_Cluj.COUNTS_PER_MM;
import static org.firstinspires.ftc.teamcode.Hardware_Cluj.DriveValue;
import static org.firstinspires.ftc.teamcode.Hardware_Cluj.PRINDERE_COMPLETA;
import static org.firstinspires.ftc.teamcode.Hardware_Cluj.PULL_SPEED;
import static org.firstinspires.ftc.teamcode.Hardware_Cluj.PullValue;
import static org.firstinspires.ftc.teamcode.Hardware_Cluj.StrafeValue;
import static org.firstinspires.ftc.teamcode.Hardware_Cluj.TURN_SPEED;
import static org.firstinspires.ftc.teamcode.Hardware_Cluj.TurnValue;

/**
 * This file illustrates the concept of driving a path based on encoder counts.
 * It uses the common Pushbot hardware class to define the drive on the robot.
 * The code is structured as a LinearOpMode
 *
 * The code REQUIRES that you DO have encoders on the wheels,
 *   otherwise you would use: PushbotAutoDriveByTime;
 *
 *  This code ALSO requires that the drive Motors have been configured such that a positive
 *  power command moves them forwards, and causes the encoders to count UP.
 *
 *   The desired path in this example is:
 *   - Drive forward for 48 inches
 *   - Spin right for 12 Inches
 *   - Drive Backwards for 24 inches
 *   - Stop and close the claw.
 *
 *  The code is written using a method called: encoderDrive(speed, leftInches, rightInches, timeoutS)
 *  that performs the actual movement.
 *  This methods assumes that each movement is relative to the last stopping place.
 *  There are other ways to perform encoder based moves, but this method is probably the simplest.
 *  This code uses the RUN_TO_POSITION mode to enable the Motor controllers to generate the run profile
 *
 * Use Android Studios to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
 */

@Autonomous(name="AutoTesnorFlow Albastru", group="Pushbot")
//@Disabled
public class AutoTensorFlowTest extends LinearOpMode {

    /* Declare OpMode members. */
    Hardware_Cluj         robot   = new Hardware_Cluj();   // Use a Pushbot's hardware


    private static final String TFOD_MODEL_ASSET = "Skystone.tflite";
    private static final String LABEL_FIRST_ELEMENT = "Stone";
    private static final String LABEL_SECOND_ELEMENT = "Skystone";

    private ElapsedTime     runtime = new ElapsedTime();


    private static final String VUFORIA_KEY = "AYWmhU7/////AAABmZ2OeZ5XtEDqgR2CdBUQL4EGfodxRjLEIegvxsdPdBhTCeCWbsCjBChlkbPWAv7k8zA3r1FSGtET63cm06w4iKcd7/9lhDlyD9jOOqjjmpG7PALXbyj478BlPVwLc2zp/bfdpAI8Vt3OcEjX1Kzfm3Vx1lRfhzhlfpD4IwF26GrlfxQIp7tv6PpBTy0ZtBkLsdT59GqrdO0BR9uicJF1i4NWij64beyPda6Jzftc+rMPet8jwg9kjntHBTtLHW6McVzQCIGqShxfRHXnWPJ4iOuoj1i6duOdFIuVclnlxtMZ1iwN0o6G+rQ81z4LaYqxQoW4+sVWbrJM9FO87qouaHTN3NThn3nAnNUYvuAmBMEP";
    private VuforiaLocalizer vuforia;

    private TFObjectDetector tfod;


    @Override
    public void runOpMode() {


        telemetry.addData("HI!", " DON'T TOUCH ANYTHING");
        telemetry.update();


        /*
         * Initialize the drive system variables.
         * The init() method of the hardware class does all the work here
         */
        /*    INIT STARTS HERE                           */
        /*                INIT STARTS HERE               */
        /*                            INIT STARTS HERE   */

        /*INIT*/

        robot.init(hardwareMap);


        /***VUFORIA + TENSORFLOW***/
        initVuforia();

        if (ClassFactory.getInstance().canCreateTFObjectDetector()) {
            initTfod();
        } else {
            telemetry.addData("Sorry!", "This device is not compatible with TFOD");
        }

        if (tfod != null) {
            tfod.activate();
        }


        /*ENCODERS*/
        robot.LeftBackMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.LeftFrontMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.RightBackMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.RightFrontMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        robot.LeftBackMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.LeftFrontMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.RightBackMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.RightFrontMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);


        // Send telemetry message to indicate successful Encoder reset

        telemetry.addData(">", "Press Play to start op mode");
        telemetry.update();
        waitForStart();

        // Step through each leg of the path,
        // Note: Reverse movement is obtained by setting a negative distance (not speed)
        /*    AUTONOMUS STARTS HERE                           */
        /*                AUTONOMUS STARTS HERE               */
        /*                            AUTONOMUS STARTS HERE   */

        /**Se ridica partea de sus ca sa cada bratele de prindere a cubului*/

        StrafeRight(160,0.7);
        sleep(1);
        checkTensorFlow(1000);
        sleep(1);
        if(checkTensorFlow(100) == 1){
            DriveBackward(25,0.5);
            StrafeRight(30,0.5);
            RotateRight(45);
            Intake(0.8);
            DriveBackward(70,0.2);
            sleep(1);
            DriveForward(80,0.2);
            sleep(1);
            oprireIntake();
            RotateLeft(55);
            DriveForward(300,0.8);
            sleep(1);
            DriveBackward(70,0.8);
        }
        else
        if(checkTensorFlow(100) == 0){
            DriveBackward(45,0.5);
            sleep(1);
            checkTensorFlow(1000);
            sleep(1);
            if(checkTensorFlow(100) == 1){
                DriveBackward(35,0.5);
                StrafeRight(50,0.5);
                RotateRight(35);
                Intake(0.8);
                DriveBackward(50,0.2);
                sleep(1);
                DriveForward(80,0.2);
                oprireIntake();
                sleep(1);
                RotateLeft(40);
                DriveForward(350,0.8);
                sleep(1);
                DriveBackward(70,0.8);
            }
            else{
                DriveBackward(70,0.5);
                StrafeRight(50,0.5);
                RotateRight(35);
                Intake(0.8);
                DriveBackward(45,0.2);
                sleep(1);
                DriveForward(80,0.2);
                oprireIntake();
                sleep(1);
                RotateLeft(41);
                DriveForward(350,0.8);
                sleep(1);
                DriveBackward(50,0.8);
            }
        }

        /**StrafeRight(160,0.7);
        checkTensorFlow(1000);
        sleep(1);
        if(checkTensorFlow(100) == 1 ){
            DriveBackward(50,0.5);
            RotateRight(45);
            Intake(0.8);
            DriveBackward(75,0.2);
            sleep(1);
            DriveForward(75,0.2);
            sleep(1);
            oprireIntake();
        }
        else
            if(checkTensorFlow(100) == 0){
                DriveBackward(50,0.5);
                checkTensorFlow(1000);
                sleep(1);
                if(checkTensorFlow(100) == 1){
                    DriveBackward(100,0.5);
                    RotateRight(45);
                    Intake(0.8);
                    DriveBackward(55,0.2);
                    sleep(1);
                    DriveForward(55,0.2);
                    sleep(1);
                    oprireIntake();
            }
                else{
                    DriveBackward(100,0.5);
                    RotateRight(45);
                    Intake(0.8);
                    DriveBackward(55,0.2);
                    sleep(1);
                    DriveForward(55,0.2);
                    sleep(1);
                    oprireIntake();
                }
            }**/






        sleep(1000);     // pause for servos to move

        telemetry.addData("Path", "Complete");
        telemetry.update();


    /***  AUTONOMUS ENDS HERE                             ***/
    /***                AUTONOMUS ENDS HERE               ***/
    /***                            AUTONOMUS ENDS HERE   ***/
}


    public int checkTensorFlow(int T) {
            int skystone = 0;
            while (opModeIsActive() && T > 0) {
                if (tfod != null) {
                    // getUpdatedRecognitions() will return null if no new information is available since
                    // the last time that call was made.
                    List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
                    if (updatedRecognitions != null) {
                        telemetry.addData("# Object Detected", updatedRecognitions.size());

                        // step through the list of recognitions and display boundary info.
                        if(updatedRecognitions.size() == 1) {
                            int i = 0;
                            int pozitie = 0;
                            for (Recognition recognition : updatedRecognitions) {
                                telemetry.addData(String.format("label (%d)", i), recognition.getLabel());
                                telemetry.addData(String.format("  left,top (%d)", i), "%.03f , %.03f",
                                        recognition.getLeft(), recognition.getTop());
                                telemetry.addData(String.format("  right,bottom (%d)", i), "%.03f , %.03f",
                                        recognition.getRight(), recognition.getBottom());
                                if(recognition.getLabel().equals(LABEL_SECOND_ELEMENT))
                                    pozitie = (int) recognition.getLeft();
                            }
                            if(pozitie != 0){
                                telemetry.addData("Skystone position", "front");
                                skystone = 1;
                            }

                            telemetry.update();
                        }
                    }
                }
                T--;
                sleep(1);
            }
            return skystone;
    }


    private void initVuforia () {
        /*
         * Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.
         */
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;

        //  Instantiate the Vuforia engine
        vuforia = ClassFactory.getInstance().createVuforia(parameters);

        // Loading trackables is not necessary for the Tensor Flow Object Detection engine.
    }

    private void initTfod () {
        int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
                "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
        tfodParameters.minimumConfidence = 0.8;
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABEL_FIRST_ELEMENT, LABEL_SECOND_ELEMENT);
    }


    /*
     *  Method to perfmorm a relative move, based on encoder counts.
     *  Encoders are not reset as the move is based on the current position.
     *  Move will stop if any of three conditions occur:
     *  1) Move gets to the desired position
     *  2) Move runs out of time
     *  3) Driver stops the opmode running.
     */
    public void EncoderDrive ( double speed, double distance, double timeoutS){
        if (opModeIsActive()) {
            int newBackLeftTarget;
            int newBackRightTarget;
            int newFrontLeftTarget;
            int newFrontRightTarget;
            // Ensure that the opmode is still active
            if (opModeIsActive()) {

                // Determine new target position, and pass to motor controller
                newBackLeftTarget = robot.LeftBackMotor.getCurrentPosition() + (int) (-distance * COUNTS_PER_MM * DriveValue);
                newBackRightTarget = robot.RightBackMotor.getCurrentPosition() + (int) (-distance * COUNTS_PER_MM * DriveValue);
                newFrontLeftTarget = robot.LeftFrontMotor.getCurrentPosition() + (int) (-distance * COUNTS_PER_MM * DriveValue);
                newFrontRightTarget = robot.RightFrontMotor.getCurrentPosition() + (int) (-distance * COUNTS_PER_MM * DriveValue);

                robot.LeftBackMotor.setTargetPosition(newBackLeftTarget);
                robot.RightBackMotor.setTargetPosition(newBackRightTarget);
                robot.LeftFrontMotor.setTargetPosition(newFrontLeftTarget);
                robot.RightFrontMotor.setTargetPosition(newFrontRightTarget);

                // Turn On RUN_TO_POSITION
                robot.LeftBackMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                robot.RightBackMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                robot.LeftFrontMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                robot.RightFrontMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                // reset the timeout time and start motion.
                runtime.reset();
                robot.LeftBackMotor.setPower(speed);
                robot.RightBackMotor.setPower(-speed);
                robot.LeftFrontMotor.setPower(speed);
                robot.RightFrontMotor.setPower(-speed);

                // keep looping while we are still active, and there is time left, and both motors are running.
                // Note: We use (isBusy() && isBusy()) in the loop test, which means that when EITHER motor hits
                // its target position, the motion will stop.  This is "safer" in the event that the robot will
                // always end the motion as soon as possible.
                // However, if you require that BOTH motors have finished their moves before the robot continues
                // onto the next step, use (isBusy() || isBusy()) in the loop test.
                while (opModeIsActive() &&
                        (runtime.seconds() < timeoutS) &&
                        (
                                robot.LeftBackMotor.isBusy() && robot.RightBackMotor.isBusy() &&
                                        robot.LeftFrontMotor.isBusy() && robot.RightFrontMotor.isBusy()
                        )) {

                    // Display it for the driver.
                    telemetry.addData("Path1", "Running to %7d :%7d :%7d :%7d"
                            , newFrontLeftTarget, newFrontRightTarget
                            , newBackLeftTarget, newBackRightTarget
                    );
                    telemetry.addData("Path2", "Running at %7d :%7d :%7d :%7d",
                            robot.LeftFrontMotor.getCurrentPosition(),
                            robot.RightFrontMotor.getCurrentPosition()
                            ,
                            robot.LeftBackMotor.getCurrentPosition(),
                            robot.RightBackMotor.getCurrentPosition()
                    );
                    telemetry.update();
                }

                // Stop all motion;

                robot.LeftBackMotor.setPower(0);
                robot.RightBackMotor.setPower(0);
                robot.LeftFrontMotor.setPower(0);
                robot.RightFrontMotor.setPower(0);

                /* COMMENT THESE FOR SPEED */

                // Turn off RUN_TO_POSITION
                robot.LeftBackMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                robot.RightBackMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                robot.LeftFrontMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                robot.RightFrontMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

                //sleep(250);   // optional pause after each move
            }
        }

    }
    public void EncoderStrafe(double speed, double distance, double timeoutS) {
        int newBackLeftTarget;
        int newBackRightTarget;
        int newFrontLeftTarget;
        int newFrontRightTarget;
        // Ensure that the opmode is still active
        if (opModeIsActive()) {

            // Determine new target position, and pass to motor controller
            newBackLeftTarget = robot.LeftBackMotor.getCurrentPosition() + (int)(distance * COUNTS_PER_MM * StrafeValue);
            newBackRightTarget = robot.RightBackMotor.getCurrentPosition() + (int)(-distance * COUNTS_PER_MM * StrafeValue);
            newFrontLeftTarget = robot.LeftFrontMotor.getCurrentPosition() + (int)(-distance * COUNTS_PER_MM * StrafeValue);
            newFrontRightTarget = robot.RightFrontMotor.getCurrentPosition() + (int)(distance * COUNTS_PER_MM * StrafeValue);

            robot.LeftBackMotor.setTargetPosition(newBackLeftTarget);
            robot.RightBackMotor.setTargetPosition(newBackRightTarget);
            robot.LeftFrontMotor.setTargetPosition(newFrontLeftTarget);
            robot.RightFrontMotor.setTargetPosition(newFrontRightTarget);

            // Turn On RUN_TO_POSITION
            robot.LeftBackMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.RightBackMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.LeftFrontMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.RightFrontMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            // reset the timeout time and start motion.
            runtime.reset();
            robot.LeftBackMotor.setPower(-speed);
            robot.RightBackMotor.setPower(-speed);
            robot.LeftFrontMotor.setPower(speed);
            robot.RightFrontMotor.setPower(speed);

            // keep looping while we are still active, and there is time left, and both motors are running.
            // Note: We use (isBusy() && isBusy()) in the loop test, which means that when EITHER motor hits
            // its target position, the motion will stop.  This is "safer" in the event that the robot will
            // always end the motion as soon as possible.
            // However, if you require that BOTH motors have finished their moves before the robot continues
            // onto the next step, use (isBusy() || isBusy()) in the loop test.
            while (opModeIsActive() &&
                    (runtime.seconds() < timeoutS) &&
                    (
                            robot.LeftBackMotor.isBusy() && robot.RightBackMotor.isBusy() &&
                                    robot.LeftFrontMotor.isBusy() && robot.RightFrontMotor.isBusy()
                    ))
            {

                // Display it for the driver.
                telemetry.addData("Path1",  "Running to %7d :%7d :%7d :%7d"
                        , newFrontLeftTarget, newFrontRightTarget
                        , newBackLeftTarget, newBackRightTarget
                );
                telemetry.addData("Path2",  "Running at %7d :%7d :%7d :%7d",
                        robot.LeftFrontMotor.getCurrentPosition(),
                        robot.RightFrontMotor.getCurrentPosition()
                        ,
                        robot.LeftBackMotor.getCurrentPosition(),
                        robot.RightBackMotor.getCurrentPosition()
                );
                telemetry.update();
            }

            // Stop all motion;

            robot.LeftBackMotor.setPower(0);
            robot.RightBackMotor.setPower(0);
            robot.LeftFrontMotor.setPower(0);
            robot.RightFrontMotor.setPower(0);

            // Turn off RUN_TO_POSITION
            robot.LeftBackMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            robot.RightBackMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            robot.LeftFrontMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            robot.RightFrontMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

            //sleep(250);   // optional pause after each move
        }
    }
    public void DriveForward (double distance, double speed)
    {
        EncoderDrive(speed, -distance,15);
    }

    public void DriveBackward (double distance, double speed) {
        EncoderDrive(-speed, distance,15);
    }

    public void StrafeLeft(double distance, double speed) {
        EncoderStrafe(speed, distance, 15);
    }
    public void StrafeRight (double distance, double speed) {
        EncoderStrafe(-speed, -distance, 15);
    }
    public void EncoderTurn(double speed, double distance, double timeoutS) {
        int newBackLeftTarget;
        int newBackRightTarget;
        int newFrontLeftTarget;
        int newFrontRightTarget;
        // Ensure that the opmode is still active
        if (opModeIsActive()) {

            // Determine new target position, and pass to motor controller
            newBackLeftTarget = robot.LeftBackMotor.getCurrentPosition() + (int) (-distance * COUNTS_PER_MM * TurnValue);
            newBackRightTarget = robot.RightBackMotor.getCurrentPosition() + (int) (distance * COUNTS_PER_MM * TurnValue);
            newFrontLeftTarget = robot.LeftFrontMotor.getCurrentPosition() + (int) (-distance * COUNTS_PER_MM * TurnValue);
            newFrontRightTarget = robot.RightFrontMotor.getCurrentPosition() + (int) (distance * COUNTS_PER_MM * TurnValue);

            robot.LeftBackMotor.setTargetPosition(newBackLeftTarget);
            robot.RightBackMotor.setTargetPosition(newBackRightTarget);
            robot.LeftFrontMotor.setTargetPosition(newFrontLeftTarget);
            robot.RightFrontMotor.setTargetPosition(newFrontRightTarget);

            // Turn On RUN_TO_POSITION
            robot.LeftBackMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.RightBackMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.LeftFrontMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.RightFrontMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            // reset the timeout time and start motion.
            runtime.reset();
            robot.LeftBackMotor.setPower(speed);
            robot.RightBackMotor.setPower(speed);
            robot.LeftFrontMotor.setPower(speed);
            robot.RightFrontMotor.setPower(speed);

            // keep looping while we are still active, and there is time left, and both motors are running.
            // Note: We use (isBusy() && isBusy()) in the loop test, which means that when EITHER motor hits
            // its target position, the motion will stop.  This is "safer" in the event that the robot will
            // always end the motion as soon as possible.
            // However, if you require that BOTH motors have finished their moves before the robot continues
            // onto the next step, use (isBusy() || isBusy()) in the loop test.
            while (opModeIsActive() &&
                    (runtime.seconds() < timeoutS) &&
                    (
                            robot.LeftBackMotor.isBusy() && robot.RightBackMotor.isBusy() &&
                                    robot.LeftFrontMotor.isBusy() && robot.RightFrontMotor.isBusy()
                    )) {

                // Display it for the driver.
                telemetry.addData("Path1", "Running to %7d :%7d :%7d :%7d"
                        , newFrontLeftTarget, newFrontRightTarget
                        , newBackLeftTarget, newBackRightTarget
                );
                telemetry.addData("Path2", "Running at %7d :%7d :%7d :%7d",
                        robot.LeftFrontMotor.getCurrentPosition(),
                        robot.RightFrontMotor.getCurrentPosition()
                        ,
                        robot.LeftBackMotor.getCurrentPosition(),
                        robot.RightBackMotor.getCurrentPosition()
                );
                telemetry.update();
            }
        }
    }

    //robot.servoTavaStanga.setPosition(0.5);
    //robot.servoTavaDreapta.setPosition(0.5);

    public void RotateRight(double angle)
    {
        EncoderTurn(TURN_SPEED, angle, 15);
    }


    public void RotateLeft(double angle)
    {

        EncoderTurn(-TURN_SPEED, -angle, 15);
    }
    public void Intake(double power){
        robot.leftIntakeMotor.setPower(-power);
        robot.rightIntakeMotor.setPower(power);
    }

    public void oprireIntake(){
        robot.leftIntakeMotor.setPower(0);
        robot.rightIntakeMotor.setPower(0);
    }

    public void Scuipa(int power){
        robot.leftIntakeMotor.setPower(-power);
        robot.rightIntakeMotor.setPower(-power);
    }

    public void PrindereTava(){
        robot.servoTavaDreapta.setDirection(Servo.Direction.REVERSE);
        robot.servoTavaStanga.setDirection(Servo.Direction.FORWARD);

        robot.servoTavaStanga.setPosition(0.25);
        robot.servoTavaDreapta.setPosition(-0.25);
    }
    public void DesprindereTava(){
        robot.servoTavaStanga.setPosition(0.0);
        robot.servoTavaDreapta.setPosition(0.0);
    }

    public void StopAllMotion() {
        robot.LeftFrontMotor.setPower(0);
        robot.RightFrontMotor.setPower(0);
        robot.LeftBackMotor.setPower(0);
        robot.RightBackMotor.setPower(0);

    }

}
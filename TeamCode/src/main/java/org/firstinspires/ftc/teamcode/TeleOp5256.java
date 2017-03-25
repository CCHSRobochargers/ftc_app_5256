/*
Copyright (c) 2016 Robert Atkinson

All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted (subject to the limitations in the disclaimer below) provided that
the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list
of conditions and the following disclaimer.

Redistributions in binary form must reproduce the above copyright notice, this
list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

Neither the name of Robert Atkinson nor the names of his contributors may be used to
endorse or promote products derived from this software without specific prior
written permission.

NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESSFOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

/**
 * This file contains an minimal example of a Linear "OpMode". An OpMode is a 'program' that runs in either
 * the autonomous or the teleop period of an FTC match. The names of OpModes appear on the menu
 * of the FTC Driver Station. When an selection is made from the menu, the corresponding OpMode
 * class is instantiated on the Robot Controller and executed.
 * <p>
 * This particular OpMode just executes a basic Tank Drive Teleop for a PushBot
 * It includes all the skeletal structure that all linear OpModes contain.
 * <p>
 * Use Android Studios to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
 */

@TeleOp(name = "Teleop", group = "5256")  // @Autonomous(...) is the other common choice
public class TeleOp5256 extends LinearOpMode {

    Hardware5256 robot = new Hardware5256();

    /* Declare OpMode members. */
    private ElapsedTime runtime = new ElapsedTime();
    // DcMotor leftMotor = null;
    // DcMotor rightMotor = null;

    @Override
    public void runOpMode() throws InterruptedException {
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        robot.init(hardwareMap, telemetry);

        boolean ablock = false;
        boolean bblock = false;
        boolean dpadblockdown = false;
        boolean yblock = false;
        boolean xblock = false;
        boolean dpadblockup = false;
        boolean dpad = false;
        boolean cascadeKillPower = true;
        boolean lefthop = false;
        boolean righthop = false;

        double servovalue = robot.kickerDown;
        double rightDrive;
        double leftDrive;
        double rightShootValue = 0.0;
        double leftShootValue = 0.0;
        double hopServo = robot.hopperUp;


        robot.leftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        robot.rightMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        robot.rightShoot.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        robot.leftShoot.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        robot.sweeper.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        robot.kicker.setPosition(robot.kickerDown);
        robot.hopper.setPosition(robot.hopperUp);

        /* eg: Initialize the hardware variables. Note that the strings used here as parameters
         * to 'get' must correspond to the names assigned during the robot configuration
         * step (using the FTC Robot Controller app on the phone).
         */
        // leftMotor  = hardwareMap.dcMotor.get("left motor");
        // rightMotor = hardwareMap.dcMotor.get("right motor");

        // eg: Set the drive motor directions:
        // "Reverse" the motor that runs backwards when connected directly to the battery
        // leftMotor.setDirection(DcMotor.Direction.FORWARD); // Set to REVERSE if using AndyMark motors
        // rightMotor.setDirection(DcMotor.Direction.REVERSE);// Set to FORWARD if using AndyMark motors

        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        runtime.reset();


        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            telemetry.addData("rightEncoder", robot.rightMotor.getCurrentPosition());
            telemetry.addData("leftEncoder", robot.leftMotor.getCurrentPosition());
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.addData("Left", leftShootValue);
            telemetry.addData("Right", rightShootValue);
            telemetry.addData("kick", servovalue);
            telemetry.addData("hop",hopServo);
            telemetry.update();
            rightDrive = -gamepad1.right_stick_y;
            leftDrive = -gamepad1.left_stick_y;

            if (gamepad1.dpad_up) {
                rightDrive = .5;
                leftDrive = .5;
                robot.rightMotor.setPower(rightDrive);
                robot.leftMotor.setPower(leftDrive);
            }

            if (gamepad1.dpad_right) {
                rightDrive = -.5;
                leftDrive = .5;
                robot.rightMotor.setPower(rightDrive);
                robot.leftMotor.setPower(leftDrive);
            }

            if (gamepad1.dpad_left) {
                rightDrive = .5;
                leftDrive = -.5;
                robot.rightMotor.setPower(rightDrive);
                robot.leftMotor.setPower(leftDrive);
            }

            if (gamepad1.dpad_down) {
                rightDrive = -.5;
                leftDrive = -.5;
                robot.rightMotor.setPower(rightDrive);
                robot.leftMotor.setPower(leftDrive);
            }

            robot.rightMotor.setPower(rightDrive);
            robot.leftMotor.setPower(leftDrive);
            //drive

            if (gamepad2.dpad_up && dpadblockup == false) {
                if (rightShootValue < 0.24) {
                    rightShootValue += 0.02;
                    leftShootValue += 0.02;
                }
                robot.rightShoot.setPower(rightShootValue);
                robot.leftShoot.setPower(leftShootValue);
                dpadblockup = true;
            }

            if (!gamepad2.dpad_up) {
                dpadblockup = false;
            }

            if (gamepad2.dpad_down && dpadblockdown == false) {
                if (rightShootValue > 0.0) {
                    rightShootValue -= 0.02;
                    leftShootValue -= 0.02;
                }
                robot.rightShoot.setPower(rightShootValue);
                robot.leftShoot.setPower(leftShootValue);
                dpadblockdown = true;
            }

            if (!gamepad2.dpad_down) {
                dpadblockdown = false;
            }


            if (gamepad2.a && ablock == false) {
                if (servovalue <= robot.kickerDown) {
                    servovalue = robot.kickerUp;
                }
                robot.kicker.setPosition(servovalue);
                ablock = true;
            }
            if (!gamepad2.a) {
                ablock = false;
            }

            if (gamepad2.b && bblock == false) {
                if (servovalue >= robot.kickerUp) {
                    servovalue = robot.kickerDown;
                }
                robot.kicker.setPosition(servovalue);
                bblock = true;
            }
            if (!gamepad2.b) {
                bblock = false;
            }

            if (gamepad1.a) {
                robot.sweeper.setPower(.45);
            }

            if (gamepad1.b) {
                robot.sweeper.setPower(-0.45);
            }

            if (gamepad1.x){
                robot.sweeper.setPower(0.0);
            }

            if (gamepad2.right_trigger > 0.1 && rightShootValue < 0.1) {
                robot.cascade1.setDirection(DcMotorSimple.Direction.FORWARD);
                robot.cascade1.setPower(gamepad2.right_trigger);
                //robot.cascade2.setDirection(DcMotorSimple.Direction.REVERSE);
               // robot.cascade2.setPower(gamepad2.right_trigger);
                cascadeKillPower = false;
            } else if (gamepad2.right_bumper && rightShootValue < 0.1) {  // add back left trigger > 1.0
                robot.cascade1.setDirection(DcMotorSimple.Direction.REVERSE);
                robot.cascade1.setPower(0.8);// add the power of left trigger back
               // robot.cascade2.setDirection(DcMotorSimple.Direction.FORWARD);
               // robot.cascade2.setPower(gamepad2.left_trigger);
                cascadeKillPower = false;
            } else if (cascadeKillPower == false) {
                robot.cascade1.setDirection(DcMotorSimple.Direction.FORWARD);
                robot.cascade1.setPower(0.05);
               // robot.cascade2.setDirection(DcMotorSimple.Direction.REVERSE);
               // robot.cascade2.setPower(0.05);
            } else if (cascadeKillPower == true) {
                robot.cascade1.setDirection(DcMotorSimple.Direction.FORWARD);
                robot.cascade1.setPower(0.0);
              //  robot.cascade2.setDirection(DcMotorSimple.Direction.REVERSE);
               // robot.cascade2.setPower(0.0);
            }

            if (gamepad2.left_trigger > 0.1 && rightShootValue < 0.1) {
                //robot.cascade1.setDirection(DcMotorSimple.Direction.FORWARD);
                //robot.cascade1.setPower(gamepad2.right_trigger);
                robot.cascade2.setDirection(DcMotorSimple.Direction.REVERSE);
                robot.cascade2.setPower(gamepad2.left_trigger);
                cascadeKillPower = false;
            } else if (gamepad2.left_bumper && rightShootValue < 0.1) {// add back left trigger > 1.0
                //robot.cascade1.setDirection(DcMotorSimple.Direction.REVERSE);
                //robot.cascade1.setPower(gamepad2.left_trigger);
                robot.cascade2.setDirection(DcMotorSimple.Direction.FORWARD);
                robot.cascade2.setPower(0.8);
                cascadeKillPower = false;
            } else if (cascadeKillPower == false) {
                //robot.cascade1.setDirection(DcMotorSimple.Direction.FORWARD);
              //robot.cascade1.setPower(0.05);
                robot.cascade2.setDirection(DcMotorSimple.Direction.REVERSE);
                robot.cascade2.setPower(0.05);
            } else if (cascadeKillPower == true) {
                // robot.cascade1.setDirection(DcMotorSimple.Direction.FORWARD);
                // robot.cascade1.setPower(0.0);
                robot.cascade2.setDirection(DcMotorSimple.Direction.REVERSE);
                robot.cascade2.setPower(0.0);
            }
            if (gamepad2.x){
                cascadeKillPower = true;
            }
            if (gamepad1.right_trigger > 0.5 ) {
                //lefthop = true;
                robot.hopper.setPosition(robot.hopperUp);
                //robot.sweeper.setPower(-0.45);
            }

            if (gamepad1.left_trigger > 0.5 ) {
                //righthop = true;
                robot.hopper.setPosition(robot.hopperDown);
                robot.sweeper.setPower(0.0);
            }

            if (gamepad1.right_bumper) {
                robot.leftServo.setPosition(1.0);
            } else if (gamepad1.left_bumper) {
                robot.leftServo.setPosition(0.0);
            } else {
                robot.leftServo.setPosition(0.5);
                robot.rightServo.setPosition(0.5);
            }

            if (gamepad2.x) {
                robot.leftFork.setPosition(robot.leftForkLatched);
                robot.rightFork.setPosition(robot.rightForkLatched);
            }
            if (gamepad2.y) {
                robot.leftFork.setPosition(robot.leftForkUnlatched);
                robot.rightFork.setPosition(robot.rightForkUnlatched);
            }

            // eg: Run wheels in tank mode (note: The joystick goes negative when pushed forwards)
            // leftMotor.setPower(-gamepad1.left_stick_y);
            // rightMotor.setPower(-gamepad1.right_stick_y);

            idle();
             // Always call idle() at the bottom of your while(opModeIsActive()) loop
        }
    }
}

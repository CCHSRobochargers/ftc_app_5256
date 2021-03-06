/*
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

import android.util.Log;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cGyro;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;


/**
 * This file illustrates the concept of driving a path based on Gyro heading and encoder counts.
 * It uses the common Pushbot hardware class to define the drive on the robot.
 * The code is structured as a LinearOpMode
 *
 * The code REQUIRES that you DO have encoders on the wheels,
 *   otherwise you would use: PushbotAutoDriveByTime;
 *
 *  This code ALSO requires that you have a Modern Robotics I2C gyro with the name "gyro"
 *   otherwise you would use: PushbotAutoDriveByEncoder;
 *
 *  This code requires that the drive Motors have been configured such that a positive
 *  power command moves them forward, and causes the encoders to count UP.
 *
 *  This code uses the RUN_TO_POSITION mode to enable the Motor controllers to generate the run profile
 *
 *  In order to calibrate the Gyro correctly, the robot must remain stationary during calibration.
 *  This is performed when the INIT button is pressed on the Driver Station.
 *  This code assumes that the robot is stationary when the INIT button is pressed.
 *  If this is not the case, then the INIT should be performed again.
 *
 *  Note: in this example, all angles are referenced to the initial coordinate frame set during the
 *  the Gyro Calibration process, or whenever the program issues a resetZAxisIntegrator() call on the Gyro.
 *
 *  The angle of movement/rotation is assumed to be a standardized rotation around the robot Z axis,
 *  which means that a Positive rotation is Counter Clock Wise, looking down on the field.
 *  This is consistent with the FTC field coordinate conventions set out in the document:
 *  ftc_app\doc\tutorial\FTC_FieldCoordinateSystemDefinition.pdf
 *
 * Use Android Studios to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
 */


@Autonomous(name="Auto Gyro Drive", group="5256")
//@Disabled
public class AutoGyroDrive extends LinearOpMode {

    enum Color {NONE, RED, GREEN, BLUE}

    /* Declare OpMode members. */
    Hardware5256 robot = new Hardware5256();   // Use a Pushbot's hardware
    ModernRoboticsI2cGyro gyro = null;                    // Additional Gyro device

    static final double COUNTS_PER_MOTOR_REV = 1120;    // eg: neverest
    static final double DRIVE_GEAR_REDUCTION = 1.0;     // This is < 1.0 if geared UP
    static final double WHEEL_DIAMETER_INCHES = 4.0;     // For figuring circumference
    static final double COUNTS_PER_INCH = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
            (WHEEL_DIAMETER_INCHES * 3.1415);

    // These constants define the desired driving/control characteristics
    // The can/should be tweaked to suite the specific robot drive train.
    static final double DRIVE_SPEED = 0.4;     // Nominal speed for better accuracy.
    static final double TURN_SPEED = 1.0;     // Nominal half speed for better accuracy.

    static final double HEADING_THRESHOLD = 1;      // As tight as we can make it with an integer gyro
    static final double P_TURN_COEFF = 0.005;
    static final double P_HOLD_COEFF = 0.05;  // Larger is more responsive, but also less stable
    static final double P_DRIVE_COEFF = 0.025;     // Larger is more responsive, but also less stable
    static final double GYRO_HOLD_WAIT = 0.5;
    double shootValue = 0.0;
    int currentHeading = 0;

    static final double countsPerDonut = 4955.0;
    static final int moveDoneDelta = (int)(COUNTS_PER_INCH / 4.0);



    @Override
    public void runOpMode() {

        /*
         * Initialize the standard drive system variables.
         * The init() method of the hardware class does most of the work here
         */
        robot.init(hardwareMap, telemetry);
        gyro = (ModernRoboticsI2cGyro) hardwareMap.gyroSensor.get("gyro");
        double heading = 0.0;
        double drivespeed = 0.0;

        // Ensure the robot it stationary, then reset the encoders and calibrate the gyro.
        robot.leftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.rightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        // Send telemetry message to alert driver that we are calibrating;
        telemetry.addData(">", "Calibrating Gyro");    //
        telemetry.update();

        gyro.calibrate();

        if (robot.blueAlliance.getState()) {
            // blue alliance moves
            if (robot.thirdTile.getState()) {
                telemetry.addData(">", "Blue, Third Tile");
            } else {
                telemetry.addData(">", "Blue, Fourth Tile");
                // When "Fourth Tile." from the ramp corner.
            }
        } else {
            // Red alliance moves
            if (robot.thirdTile.getState()) {
                telemetry.addData(">", "Red, Third Tile");
            } else {
                telemetry.addData(">", "Red, Fourth Tile");
            }
        }
        telemetry.update();
        // make sure the gyro is calibrated before continuing
        while (!isStopRequested() && gyro.isCalibrating()) {
            sleep(50);
            idle();
        }

        telemetry.addData(">", "Robot Ready.");    //
        telemetry.update();

        robot.leftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.rightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Wait for the game to start (Display Gyro value), and reset gyro before we move..
        while (!isStarted()) {
            telemetry.addData(">", "Robot Heading = %d", gyro.getIntegratedZValue());
            telemetry.update();
            idle();
        }
        gyro.resetZAxisIntegrator();

        ShooterCtlrThread shooterUp = new ShooterCtlrThread(robot.leftShoot, robot.rightShoot, 0.0, 0.22, 1000);
        ShooterCtlrThread shooterDown = new ShooterCtlrThread(robot.leftShoot, robot.rightShoot, 0.22, 0.0, 2000);
        telemetry.addData(">", "Robot Heading Post-Reset = %d", gyro.getIntegratedZValue());
        if (robot.blueAlliance.getState()) {
            // blue alliance moves
            if (robot.thirdTile.getState()) {
                // When "Third Tile." from the ramp corner.

                gyroDrive(DRIVE_SPEED, 23.0, heading);
                heading = heading - 45.0;
                shooterUp.start();
                gyroTurn(TURN_SPEED, heading);
                gyroHold(1.0, heading, GYRO_HOLD_WAIT);
                gyroDrive(DRIVE_SPEED, 49.0, heading);
                heading = heading - 45.0;
                gyroTurn(TURN_SPEED, heading);
                gyroHold(1.0, heading, GYRO_HOLD_WAIT);
                runIndexer();
                sleep(500);
                shooterDown.start();
                gyroDrive(DRIVE_SPEED, 6.0, heading);
                beaconPushBlue();
                gyroDrive(DRIVE_SPEED, -4.0, heading);
                heading = heading - 25;
                gyroTurn(TURN_SPEED, heading);
                gyroDrive(DRIVE_SPEED, -18, heading);
                heading = heading + 45;
                gyroTurn(TURN_SPEED, heading);
                gyroDrive(DRIVE_SPEED, -48.0, heading);



//                shooterUp.start();
//                gyroDrive(DRIVE_SPEED, -21.0, heading);
//                robot.hopper.setPosition(robot.hopperUp);
////                upShooter();
//
//                runIndexer();
//                sleep(500);
//                shooterDown.start();
//                heading = heading - 5.0;
//                gyroTurn(TURN_SPEED, heading);
//                gyroDrive(DRIVE_SPEED, -37.0, heading);
////                downShooter();

            } else {
                telemetry.addData(">", "Blue, Fourth Tile");
                // When "Fourth Tile." from the ramp corner.

//                gyroDrive(DRIVE_SPEED, 35.0, heading);
//                heading = heading - 90.0;
//                gyroTurn(TURN_SPEED, heading);
//                gyroDrive(DRIVE_SPEED, 77.0, heading);
//                beaconPushBlue();
//                gyroDrive(DRIVE_SPEED, -15.0, heading);
//                heading = heading + 90.0;
//                gyroTurn(TURN_SPEED, heading);
//                gyroDrive(DRIVE_SPEED, 47.5, heading);
//                heading = heading - 90.0;
//                gyroTurn(TURN_SPEED, heading);
//                gyroDrive(DRIVE_SPEED, 18.0, heading);
//                beaconPushBlue();


                sleep(8000);
                gyroDrive(DRIVE_SPEED, -30.0, heading);
                robot.hopper.setPosition(robot.hopperUp);
                heading = heading - 40.0;
                gyroTurn(TURN_SPEED, heading);
                gyroDrive(DRIVE_SPEED, -3.0, heading);
                upShooter();
                runIndexer();
                gyroDrive(DRIVE_SPEED, -30.0, heading);
                downShooter();

            }
        } else {
            // Red alliance moves
            if (robot.thirdTile.getState()) {
                telemetry.addData(">", "Red, Third Tile");
                //  When "Third Tile." from the ramp corner.

                gyroDrive(DRIVE_SPEED, 22.0, heading);
                heading = heading + 45.0;
                shooterUp.start();
                gyroTurn(TURN_SPEED, heading);
                gyroHold(1.0, heading, GYRO_HOLD_WAIT);
                gyroDrive(DRIVE_SPEED, 49.0, heading);
                heading = heading + 45.0;
                gyroTurn(TURN_SPEED, heading);
                gyroHold(1.0, heading, GYRO_HOLD_WAIT);
                runIndexer();
                sleep(500);
                shooterDown.start();
                gyroDrive(DRIVE_SPEED, 9.0, heading);
                beaconPushRed();
                gyroDrive(DRIVE_SPEED, -4.0, heading);
                heading = heading + 25;
                gyroTurn(TURN_SPEED, heading);
                gyroDrive(DRIVE_SPEED, -18, heading);
                heading = heading -45;
                gyroTurn(TURN_SPEED, heading);
                gyroDrive(DRIVE_SPEED, -48.0, heading);

//                shooterUp.start();
//                gyroDrive(DRIVE_SPEED, -21.0, heading);
//                robot.hopper.setPosition(robot.hopperUp);
////                upShooter();
//                runIndexer();
//                sleep(500);
//                shooterDown.start();
//                heading = heading - 5.0;
//                gyroTurn(TURN_SPEED, heading);
//                gyroDrive(DRIVE_SPEED, -37.0, heading);
////                downShooter();


                // gyroDrive(DRIVE_SPEED, -37.0, heading);


            } else {
                telemetry.addData(">", "Red, Fourth Tile");
                // When "Fourth Tile." from the ramp corner.
//                gyroDrive(DRIVE_SPEED, 35.0, heading);
//                heading = heading + 90.0;
//                gyroTurn(TURN_SPEED, heading);
//                gyroDrive(DRIVE_SPEED, 77.0, heading);
//                beaconPushRed();
//                gyroDrive(DRIVE_SPEED, -15.0, heading);
//                heading = heading + 90.0;
//                gyroTurn(TURN_SPEED, heading);
//                gyroDrive(DRIVE_SPEED, 47.5, heading);
//                heading = heading - 90.0;
//                gyroTurn(TURN_SPEED, heading);
//                gyroDrive(DRIVE_SPEED, 18.0, heading);
//                beaconPushRed();

                sleep(8000);
                gyroDrive(DRIVE_SPEED, -30.0, heading);
                robot.hopper.setPosition(robot.hopperUp);
                heading = heading + 40.0;
                gyroTurn(TURN_SPEED, heading);
                gyroDrive(DRIVE_SPEED, -3.0, heading);
                upShooter();
                runIndexer();
                gyroDrive(DRIVE_SPEED, -30.0, heading);
                downShooter();

            }
        }
        telemetry.addData("Path", "Complete");
        telemetry.update();

        while (!isStopRequested()) {
            sleep(50);
            idle();
        }
    }


    /**
     * Method to drive on a fixed compass bearing (angle), based on encoder counts.
     * Move will stop if either of these conditions occur:
     * 1) Move gets to the desired position
     * 2) Driver stops the opmode running.
     *
     * @param speed    Target speed for forward motion.  Should allow for _/- variance for adjusting heading
     * @param distance Distance (in inches) to move from current position.  Negative distance means move backwards.
     * @param angle    Absolute Angle (in Degrees) relative to last gyro reset.
     *                 0 = fwd. +ve is CCW from fwd. -ve is CW from forward.
     *                 If a relative angle is required, add/subtract from current heading.
     */
    public void gyroDrive(double speed,
                          double distance,
                          double angle,
                          Color color) {

        int newLeftTarget;
        int newRightTarget;
        int moveCounts;
        double max;
        double error;
        double steer;
        double leftSpeed;
        double rightSpeed;

        // Ensure that the opmode is still active
        if (opModeIsActive()) {

            // Determine new target position, and pass to motor controller
            moveCounts = (int) (distance * COUNTS_PER_INCH);
            newLeftTarget = robot.leftMotor.getCurrentPosition() + moveCounts;
            newRightTarget = robot.rightMotor.getCurrentPosition() + moveCounts;

            // Set Target and Turn On RUN_TO_POSITION
            robot.leftMotor.setTargetPosition(newLeftTarget);
            robot.rightMotor.setTargetPosition(newRightTarget);

            robot.leftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.rightMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            // start motion.
            speed = Range.clip(Math.abs(speed), 0.0, 1.0);
            robot.leftMotor.setPower(speed);
            robot.rightMotor.setPower(speed);

            // keep looping while we are still active, and BOTH motors are running.
            while (opModeIsActive() &&
                    (Math.abs(robot.leftMotor.getCurrentPosition() - newLeftTarget) > COUNTS_PER_INCH / 2.0) &&
                    (Math.abs(robot.rightMotor.getCurrentPosition() - newRightTarget) > COUNTS_PER_INCH / 2.0)) {

                // adjust relative speed based on heading error.
                error = getError(angle);
                steer = getSteer(error, P_DRIVE_COEFF);

                // if driving in reverse, the motor correction also needs to be reversed
                if (distance < 0)
                    steer *= -1.0;

                leftSpeed = speed - steer;
                rightSpeed = speed + steer;

                // Normalize speeds if any one exceeds +/- 1.0;
                max = Math.max(Math.abs(leftSpeed), Math.abs(rightSpeed));
                if (max > 1.0) {
                    leftSpeed /= max;
                    rightSpeed /= max;
                }

                robot.leftMotor.setPower(leftSpeed);
                robot.rightMotor.setPower(rightSpeed);

                //if ((color == Color.RED) && (robot.beacon.red() > 0)) {
                //    break;
                // }

                //if ((color == Color.BLUE) && (robot.beacon.blue() > 0)) {
                //    break;
                // }

                // Display drive status for the driver.
                telemetry.addData("Err/St", "%5.1f/%5.1f", error, steer);
                telemetry.addData("Target", "%7d:%7d", newLeftTarget, newRightTarget);
                telemetry.addData("Actual", "%7d:%7d", robot.leftMotor.getCurrentPosition(),
                        robot.rightMotor.getCurrentPosition());
                telemetry.addData("Speed", "%5.2f:%5.2f", leftSpeed, rightSpeed);
                telemetry.update();
            }

            // Stop all motion;
            robot.leftMotor.setPower(0);
            robot.rightMotor.setPower(0);

            // Turn off RUN_TO_POSITION
            robot.leftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            robot.rightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
    }

    int degreesToCounts(double degrees) {
        return (int)(degrees * (countsPerDonut / 360.0));
    }

    void gyroTurnNEW(double speed, double newHeading) {
        int angle = (int)newHeading - currentHeading;

        robot.leftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.rightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Figure out how far off we are at the end of the previous move so we can correct
        int gyroError =  angle + (currentHeading - gyro.getHeading());
        while (gyroError > 180) {
            gyroError = 360 - gyroError;
        }
        while (gyroError < -180) {
            gyroError = 360 + gyroError;
        }
        currentHeading = (int)newHeading;

        int leftTargetPosition = robot.leftMotor.getCurrentPosition() + degreesToCounts(gyroError);
        robot.leftMotor.setTargetPosition(leftTargetPosition);
        int rightTargetPosition = robot.rightMotor.getCurrentPosition() - degreesToCounts(gyroError);
        robot.rightMotor.setTargetPosition(rightTargetPosition);
        robot.leftMotor.setPower(speed);
        robot.rightMotor.setPower(speed);

        while (opModeIsActive() &&
                (Math.abs(leftTargetPosition - robot.leftMotor.getCurrentPosition()) > moveDoneDelta) &&
                (Math.abs(rightTargetPosition - robot.rightMotor.getCurrentPosition()) > moveDoneDelta)) {
            sleep(50);
            telemetry.addData("Left", Math.abs(leftTargetPosition - robot.leftMotor.getCurrentPosition()));
            telemetry.addData("Right", Math.abs(rightTargetPosition - robot.rightMotor.getCurrentPosition()));
            telemetry.update();
        }
        robot.leftMotor.setPower(0.0);
        robot.rightMotor.setPower(0.0);

        robot.leftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.rightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public void gyroDrive(double speed,
                          double distance,
                          double angle) {
        gyroDrive(speed, distance, angle, Color.NONE);
    }

    /**
     * Method to spin on central axis to point in a new direction.
     * Move will stop if either of these conditions occur:
     * 1) Move gets to the heading (angle)
     * 2) Driver stops the opmode running.
     *
     * @param speed Desired speed of turn.
     * @param angle Absolute Angle (in Degrees) relative to last gyro reset.
     *              0 = fwd. +ve is CCW from fwd. -ve is CW from forward.
     *              If a relative angle is required, add/subtract from current heading.
     */
    public void gyroTurn(double speed, double angle) {

        // keep looping while we are still active, and not on heading.
        while (opModeIsActive() && !onHeading(speed, angle, P_TURN_COEFF)) {
            // Update telemetry & Allow time for other processes to run.
            telemetry.update();
        }
    }


    /**
     * Method to obtain & hold a heading for a finite amount of time
     * Move will stop once the requested time has elapsed
     *
     * @param speed    Desired speed of turn.
     * @param angle    Absolute Angle (in Degrees) relative to last gyro reset.
     *                 0 = fwd. +ve is CCW from fwd. -ve is CW from forward.
     *                 If a relative angle is required, add/subtract from current heading.
     * @param holdTime Length of time (in seconds) to hold the specified heading.
     */
    public void gyroHold(double speed, double angle, double holdTime) {

        ElapsedTime holdTimer = new ElapsedTime();

        // keep looping while we have time remaining.
        holdTimer.reset();
        while (opModeIsActive() && (holdTimer.time() < holdTime)) {
            // Update telemetry & Allow time for other processes to run.
            onHeading(speed, angle, P_HOLD_COEFF);
            telemetry.update();
        }

        // Stop all motion;
        robot.leftMotor.setPower(0);
        robot.rightMotor.setPower(0);
    }

    public void gyroHoldNEW(double speed, double angle, double holdTime) {
    }

    /**
     * Perform one cycle of closed loop heading control.
     *
     * @param speed  Desired speed of turn.
     * @param angle  Absolute Angle (in Degrees) relative to last gyro reset.
     *               0 = fwd. +ve is CCW from fwd. -ve is CW from forward.
     *               If a relative angle is required, add/subtract from current heading.
     * @param PCoeff Proportional Gain coefficient
     * @return
     */
    boolean onHeading(double speed, double angle, double PCoeff) {
        double error;
        double steer;
        boolean onTarget = false;
        double leftSpeed;
        double rightSpeed;

        // determine turn power based on +/- error
        error = getError(angle);

        if (Math.abs(error) <= HEADING_THRESHOLD) {
            steer = 0.0;
            leftSpeed = 0.0;
            rightSpeed = 0.0;
            onTarget = true;
        } else {
            steer = getSteer(error, PCoeff);
            rightSpeed = speed * steer;
            leftSpeed = -rightSpeed;
        }

        // Send desired speeds to motors.
        robot.leftMotor.setPower(leftSpeed);
        robot.rightMotor.setPower(rightSpeed);

        // Display it for the driver.
        telemetry.addData("Target", "%5.2f", angle);
        telemetry.addData("Err/St", "%5.2f/%5.2f", error, steer);
        telemetry.addData("Speed.", "%5.2f:%5.2f", leftSpeed, rightSpeed);
        telemetry.addData("Color Red", robot.beacon.red());
        telemetry.addData("Color Blue", robot.beacon.blue());

        return onTarget;
    }

    /**
     * getError determines the error between the target angle and the robot's current heading
     *
     * @param targetAngle Desired angle (relative to global reference established at last Gyro Reset).
     * @return error angle: Degrees in the range +/- 180. Centered on the robot's frame of reference
     * +ve error means the robot should turn LEFT (CCW) to reduce error.
     */
    public double getError(double targetAngle) {

        double robotError;

        // calculate error in -179 to +180 range  (
        robotError = targetAngle - gyro.getIntegratedZValue();
        while (robotError > 180) robotError -= 360;
        while (robotError <= -180) robotError += 360;
        return robotError;
    }

    /**
     * returns desired steering force.  +/- 1 range.  +ve = steer left
     *
     * @param error  Error angle in robot relative degrees
     * @param PCoeff Proportional Gain Coefficient
     * @return
     */
    public double getSteer(double error, double PCoeff) {
        return Range.clip(error * PCoeff, -1, 1);
    }

    public void upShooter() {
        while (opModeIsActive() && shootValue < 0.2) {
            shootValue += 0.02;
            robot.rightShoot.setPower(shootValue);
            robot.leftShoot.setPower(shootValue);
            sleep(200);
        }
    }

    public void runIndexer() {
        robot.kicker.setPosition(robot.kickerDown);
        if (opModeIsActive()) {
            sleep(500);
        }
        robot.kicker.setPosition(.50);
        if (opModeIsActive()) {
            sleep(500);
        }
        robot.kicker.setPosition(robot.kickerUp);
        if (opModeIsActive()) {
            sleep(500);
        }
        robot.kicker.setPosition(robot.kickerDown);
        if (opModeIsActive()) {
            sleep(200);
        }
        robot.hopper.setPosition(robot.hopperUp);
        robot.sweeper.setPower(-.45);
        if (opModeIsActive()) {
            sleep(500);
        }
        robot.kicker.setPosition(robot.kickerUp);
        robot.sweeper.setPower(0.0);
    }


    public void downShooter() {
        while (opModeIsActive() && shootValue > 0) {
            shootValue -= 0.02;
            robot.rightShoot.setPower(shootValue);
            robot.leftShoot.setPower(shootValue);
            sleep(200);
        }
    }

    public void beaconPushRed() {
        Log.i("Red1 =", String.valueOf(robot.beacon.blue()));
        Log.i("Blue1 =", String.valueOf(robot.beacon.red()));
        robot.leftServo.setPosition(1.0);
        sleep(2000);
        robot.leftServo.setPosition(0.0);
        sleep(500);
        robot.leftServo.setPosition(0.5);

        Log.i("Red2 =", String.valueOf(robot.beacon.blue()));
        Log.i("Blue2 =", String.valueOf(robot.beacon.red()));
        if (robot.beacon.blue() > robot.beacon.red()) {
            sleep(5000);
            robot.leftServo.setPosition(1.0);
            sleep(1000);
            robot.leftServo.setPosition(0.5);
        }
    }

    public void beaconPushBlue() {
        Log.i("Red1 =", String.valueOf(robot.beacon.red()));
        Log.i("Blue1 =", String.valueOf(robot.beacon.blue()));
        robot.leftServo.setPosition(1.0);
        sleep(2000);
        robot.leftServo.setPosition(0.0);
        sleep(500);
        robot.leftServo.setPosition(0.5);

        Log.i("Red2 =", String.valueOf(robot.beacon.red()));
        Log.i("Blue2 =", String.valueOf(robot.beacon.blue()));
        if (robot.beacon.red() > robot.beacon.blue()) {
            sleep(5000);
            robot.leftServo.setPosition(1.0);
            sleep(1000);
            robot.leftServo.setPosition(0.5);
        }
    }
}

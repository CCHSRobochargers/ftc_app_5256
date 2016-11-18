package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

/**
 * This is NOT an opmode.
 *
 * This class can be used to define all the specific hardware for a single robot.
 * In this case that robot is a Pushbot.
 * See PushbotTeleopTank_Iterative and others classes starting with "Pushbot" for usage examples.
 *
 * This hardware class assumes the following device names have been configured on the robot:
 * Note:  All names are lower case and some have single spaces between words.
 */
public class Hardware5256
{
    /* Public OpMode members. */
    public DcMotor leftMotor             = null;
    public DcMotor rightMotor            = null;
    public DcMotor rightShoot            = null;
    public DcMotor leftShoot             = null;
    public DcMotor sweeper               = null;
    public ColorSensor beacon            = null;
    public Servo arm                     = null;
    public Servo kicker                  = null;
    public DigitalChannel blueAlliance   = null;
    public DigitalChannel secondTile     = null;
    public DcMotor Cascade1              = null;
    public DcMotor Cascade2              = null;

    /* local OpMode members. */
    HardwareMap hwMap           =  null;
    private ElapsedTime period  = new ElapsedTime();

    /* Constructor */
    public Hardware5256(){

    }

    /* Initialize standard Hardware interfaces */
    public void init(HardwareMap ahwMap, Telemetry telemetry) {
        // Save reference to Hardware map
        hwMap = ahwMap;

        // Define and Initialize Motors
        try {
            rightShoot   = hwMap.dcMotor.get("rShoot");
        } catch (IllegalArgumentException e) {
            telemetry.addLine("rShoot not found");
        }

        try {
            leftShoot   = hwMap.dcMotor.get("lShoot");
        } catch (IllegalArgumentException e ){
            telemetry.addLine("lShoot not found");
        }

        try {
            sweeper = hwMap.dcMotor.get("sweep");
        } catch (IllegalArgumentException e) {
            telemetry.addLine("sweep not found");
        }

        try {
            kicker = hwMap.servo.get("kick");
        } catch (IllegalArgumentException e) {
            telemetry.addLine("kick not found");
        }

        try {
            arm = hwMap.servo.get("pusher");
        } catch (IllegalArgumentException e) {
            telemetry.addLine("pusher not found");
        }

        try {
            beacon = hwMap.colorSensor.get("beacon");
        } catch (IllegalArgumentException e) {
            telemetry.addLine("beacon sensor not found");
        }

        try {
            blueAlliance = hwMap.digitalChannel.get("alliance");
        } catch (IllegalArgumentException e) {
            telemetry.addLine("alliance switch not found");
        }

        try {
            secondTile = hwMap.digitalChannel.get("tile");
        } catch (IllegalArgumentException e) {
            telemetry.addLine("tile Switch not found");
        }



        rightMotor  = hwMap.dcMotor.get("motorR");
        leftMotor = hwMap.dcMotor.get("motorL");

        leftMotor.setDirection(DcMotor.Direction.FORWARD); // Set to REVERSE if using AndyMark motors
        rightMotor.setDirection(DcMotor.Direction.REVERSE);// Set to FORWARD if using AndyMark motors
        if (rightShoot != null) {
            rightShoot.setDirection(DcMotor.Direction.REVERSE);
        }

        if (leftShoot != null) {
            leftShoot.setDirection(DcMotor.Direction.FORWARD);
        }

        if (sweeper != null) {
            sweeper.setDirection(DcMotor.Direction.REVERSE);
            sweeper.setPower(0);
        }


        // Set all motors to zero power
        leftMotor.setPower(0);
        rightMotor.setPower(0);

        // Set all motors to run without encoders.
        // May want to use RUN_USING_ENCODERS if encoders are installed.
        leftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        beacon.enableLed(false);
    }

    /***
     *
     * waitForTick implements a periodic delay. However, this acts like a metronome with a regular
     * periodic tick.  This is used to compensate for varying processing times for each cycle.
     * The function looks at the elapsed cycle time, and sleeps for the remaining time interval.
     *
     * @param periodMs  Length of wait cycle in mSec.
     */
    public void waitForTick(long periodMs) {

        long  remaining = periodMs - (long)period.milliseconds();

        // sleep for the remaining portion of the regular cycle period.
        if (remaining > 0) {
            try {
                Thread.sleep(remaining);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Reset the cycle clock for the next pass.
        period.reset();
    }
}


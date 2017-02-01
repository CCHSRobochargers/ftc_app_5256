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
    //public ColorSensor beacon            = null;
    //public Servo arm                     = null;
    public Servo kicker                  = null;
    public DigitalChannel blueAlliance   = null;
    public DigitalChannel thirdTile      = null;
    public DcMotor cascade1              = null;
    public DcMotor cascade2              = null;

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
        rightMotor  = hwMap.dcMotor.get("motorR");
        leftMotor = hwMap.dcMotor.get("motorL");
        rightShoot   = hwMap.dcMotor.get("rShoot");
        leftShoot   = hwMap.dcMotor.get("lShoot");
        cascade1 = hwMap.dcMotor.get("cascade1");
        cascade2 = hwMap.dcMotor.get("cascade2");
        sweeper = hwMap.dcMotor.get("sweeper");
        kicker = hwMap.servo.get("kick");

        //arm = hwMap.servo.get("pusher");

        // = hwMap.colorSensor.get("beacon");
        blueAlliance = hwMap.digitalChannel.get("alliance");
        thirdTile = hwMap.digitalChannel.get("tile");

        leftMotor.setDirection(DcMotor.Direction.FORWARD); // Set to REVERSE if using AndyMark motors
        rightMotor.setDirection(DcMotor.Direction.REVERSE);// Set to FORWARD if using AndyMark motors

        rightShoot.setDirection(DcMotor.Direction.REVERSE);
        leftShoot.setDirection(DcMotor.Direction.FORWARD);

        sweeper.setDirection(DcMotor.Direction.REVERSE);
        sweeper.setPower(0);

        // Set all motors to zero power
        leftMotor.setPower(0);
        rightMotor.setPower(0);

        // Set all motors to run without encoders.
        // May want to use RUN_USING_ENCODERS if encoders are installed.
        leftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

       // beacon.enableLed(false);
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


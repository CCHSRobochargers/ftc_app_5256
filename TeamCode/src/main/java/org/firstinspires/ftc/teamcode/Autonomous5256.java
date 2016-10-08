package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Hardware;

/**
 * Created by CCHSRobotics on 9/10/2016.
 */
@TeleOp(name="Autonomous", group = "5256")
//@Disabled
public class Autonomous5256 extends LinearOpMode {
    private ElapsedTime runtime = new ElapsedTime();
    Hardware Robot = new Hardware();
    DcMotor motorR;
    DcMotor motorL;

    double countsPerMeter = 5361.0; // 10439;    // Found this experimentally: Measured one meter, drove distance, read counts
    double countsPerDonut = 7661.0; // 14161;    // Encoder counts per 360 degrees
    double trackLifterCountsPerDegree = -1170.0 * 2.0 / 90.0;

    int centimetersToCounts(double centimeters) {
        return (int)(centimeters * (countsPerMeter / 100.0));

    @Override
    public void runOpMode() throws InterruptedException {
        motorR = hardwareMap.dcMotor.get("motorR");
        motorL = hardwareMap.dcMotor.get("motorL");
        motorL.setDirection(DcMotorSimple.Direction.REVERSE);
        motorR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motorL.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        waitForStart();
        runtime.reset();
        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.update();

            // eg: Run wheels in tank mode (note: The joystick goes negative when pushed forwards)
             motorR.setPower();
             motorL.setPower();

            idle(); // Always call idle() at the bottom of your while(opModeIsActive()) loop
        }
    }

}




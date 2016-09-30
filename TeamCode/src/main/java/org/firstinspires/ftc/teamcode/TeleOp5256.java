package org.firstinspires.ftc.teamcode;

        import com.qualcomm.robotcore.eventloop.opmode.Disabled;
        import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
        import com.qualcomm.robotcore.eventloop.opmode.OpMode;
        import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
        import com.qualcomm.robotcore.hardware.DcMotor;
        import com.qualcomm.robotcore.hardware.DcMotorSimple;
        import com.qualcomm.robotcore.hardware.Servo;
        import com.qualcomm.robotcore.util.ElapsedTime;
        import com.qualcomm.robotcore.util.Hardware;

/**
 * Created by CCHSRobotics on 9/10/2016.
 */
@TeleOp(name="TeleOp", group = "5256")
//@Disabled
public class TeleOp5256 extends LinearOpMode {
    private ElapsedTime runtime = new ElapsedTime();
    Hardware Robot = new Hardware();
    DcMotor motorR;
    DcMotor motorL;


    @Override
    public void runOpMode() throws InterruptedException {
        motorR = hardwareMap.dcMotor.get("motorR");
        motorL = hardwareMap.dcMotor.get("motorL");
        motorL.setDirection(DcMotorSimple.Direction.REVERSE);
        waitForStart();
        runtime.reset();
        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.update();

            // eg: Run wheels in tank mode (note: The joystick goes negative when pushed forwards)
             motorR.setPower(gamepad1.right_stick_y);
             motorL.setPower(gamepad1.left_stick_y);

            idle(); // Always call idle() at the bottom of your while(opModeIsActive()) loop
        }
    }

}




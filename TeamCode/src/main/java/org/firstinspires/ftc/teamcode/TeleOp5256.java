package org.firstinspires.ftc.teamcode;

        import com.qualcomm.robotcore.eventloop.opmode.Disabled;
        import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
        import com.qualcomm.robotcore.eventloop.opmode.OpMode;
        import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
        import com.qualcomm.robotcore.hardware.DcMotor;
        import com.qualcomm.robotcore.hardware.Servo;
        import com.qualcomm.robotcore.util.ElapsedTime;
        import com.qualcomm.robotcore.util.Hardware;

/**
 * Created by CCHSRobotics on 9/10/2016.
 */
@TeleOp(name="TeleOp", group = "5256")
@Disabled
public class TeleOp5256 extends LinearOpMode {
    Hardware Robot = new Hardware();
    DcMotor leftdrive;
    DcMotor rightdrive;
    DcMotor rightshoot;
    DcMotor leftshoot;
    DcMotor forklift;
    Servo forkgrip;
    Servo sweeper;


    @Override
    public void runOpMode() throws InterruptedException {

    }
}




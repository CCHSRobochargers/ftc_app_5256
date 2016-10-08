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

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name="Autonomous", group="5256")  // @Autonomous(...) is the other common choice
//@Disabled
public class Autonomous extends LinearOpMode {
    private ElapsedTime runtime = new ElapsedTime();
    DcMotor motorR;
    DcMotor motorL;

    double countsPerInch = 95.0;
    double countsPerDegree = 10112.0 / 360.0;

    int inchesToCounts(double inches) {
        return (int)(inches * countsPerInch);
    }

    int degreesToCounts(double degrees) {
        return (int)(degrees * countsPerDegree);
    }

    @Override
    public void runOpMode() throws InterruptedException {

        motorR = hardwareMap.dcMotor.get("motorR");
        motorL = hardwareMap.dcMotor.get("motorL");
        motorL.setDirection(DcMotor.Direction.REVERSE);
        motorL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motorR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        runtime.reset();

        motorL.setTargetPosition(motorL.getCurrentPosition() + inchesToCounts(26.0));
        motorR.setTargetPosition(motorR.getCurrentPosition() + inchesToCounts(26.0));
        motorL.setPower(0.5);
        motorR.setPower(0.5);

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive() && ((motorL.getCurrentPosition() - motorL.getTargetPosition()) != 0)) {
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.update();

            idle(); // Always call idle() at the bottom of your while(opModeIsActive()) loop
        }

        motorL.setPower(0.0);
        motorR.setPower(0.0);
        motorL.setTargetPosition(motorL.getTargetPosition() - degreesToCounts(45.0));
        motorR.setTargetPosition(motorR.getTargetPosition() + degreesToCounts(45.0));
        motorL.setPower(0.5);
        motorR.setPower(0.5);

        while (opModeIsActive() && ((motorL.getCurrentPosition() - motorL.getTargetPosition()) != 0)) {
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.update();

            idle(); // Always call idle() at the bottom of your while(opModeIsActive()) loop
        }

        motorL.setPower(0.0);
        motorR.setPower(0.0);
        motorL.setTargetPosition(motorL.getCurrentPosition() + inchesToCounts(36.0));
        motorR.setTargetPosition(motorR.getCurrentPosition() + inchesToCounts(36.0));
        motorL.setPower(0.5);
        motorR.setPower(0.5);

        while (opModeIsActive() && ((motorL.getCurrentPosition() - motorL.getTargetPosition()) != 0)) {
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.update();

            idle(); // Always call idle() at the bottom of your while(opModeIsActive()) loop
        }

        motorL.setPower(0.0);
        motorR.setPower(0.0);
        motorL.setTargetPosition(motorL.getTargetPosition() - degreesToCounts(45.0));
        motorR.setTargetPosition(motorR.getTargetPosition() + degreesToCounts(45.0));
        motorL.setPower(0.5);
        motorR.setPower(0.5);

        while (opModeIsActive() && ((motorL.getCurrentPosition() - motorL.getTargetPosition()) != 0)) {
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.update();

            idle(); // Always call idle() at the bottom of your while(opModeIsActive()) loop
        }

        motorL.setPower(0.0);
        motorR.setPower(0.0);

        while (opModeIsActive()) {
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.update();

            idle(); // Always call idle() at the bottom of your while(opModeIsActive()) loop
        }
    }
}

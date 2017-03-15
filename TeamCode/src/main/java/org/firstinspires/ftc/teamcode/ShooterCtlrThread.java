package org.firstinspires.ftc.teamcode;

import android.util.Log;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;

/**
 * Created by CCHSRobotics on 3/14/2017.
 */

public class ShooterCtlrThread implements Runnable {
    private Thread t;
    private DcMotor lS;
    private DcMotor rS;
    private double speedAt;
    private double speedTo;
    private double speedMagnitude;
    private long delay;

    public ShooterCtlrThread(DcMotor lShooter, DcMotor rShooter, double startSpeed, double endSpeed, long totalTime) {
        lS = lShooter;
        rS = rShooter;
        speedAt = startSpeed;
        speedTo = endSpeed;
        speedMagnitude = Math.abs(startSpeed - endSpeed);
        delay = (long)(totalTime / (speedMagnitude * 100));
    }

    public void start() {
        t = new Thread(this, "ThreadShooterUpSpeed");
        t.start();
    }

    public void run() {
        if (speedAt < speedTo) {
            for (int c = 0; c < (int)(speedMagnitude * 100.0); c++) {
                lS.setPower(Range.clip(c / 100.0, -1.0, 1.0));
                rS.setPower(Range.clip(c / 100.0, -1.0, 1.0));
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    Log.e("Thread Error", e.toString());
                }
            }
        } else {
            for (int c = (int)(speedMagnitude * 100.0); c > 0; c--) {
                lS.setPower(Range.clip(c / 100.0, -1.0, 1.0));
                rS.setPower(Range.clip(c / 100.0, -1.0, 1.0));
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    Log.e("Thread Error", e.toString());
                }
            }
        }
    }

}